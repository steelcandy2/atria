/*
 Copyright (C) 2002 by James MacKay.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package com.steelcandy.common.work;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.creation.ReflectionUtilities;

import java.util.LinkedList;

/**
    A work processor that uses a pool of threads to process
    its work.

    @author James MacKay
*/
public class ThreadPoolWorkProcessor
    implements WorkProcessor
{
    /*
        Implementation Note: both the work queue and the worker
        counts are protected by an instance's monitor.
    */

    // Constants

    /**
        The first part of the name of every thread in an instance's
        thread pool.
    */
    private static final String
        THREAD_NAME_PREFIX = ReflectionUtilities.
            getUnqualifiedClassName(ThreadPoolWorkProcessor.class) +
                "Worker-";


    // Private fields

    /**
        The queue of work that this processor has to process.
        Each element is a Runnable.
        <p>
        Work should be added to the start of the list and removed
        from the end.
    */
    private LinkedList _workQueue;

    /** The number of workers that we should have. */
    private int _desiredWorkerCount;

    /** The number of workers that we do have. */
    private int _actualWorkerCount;

    /** The number of workers that are actively processing work. */
    private int _workingWorkerCount;

    /**
        The condition variable used to wait on the condition
        that all of this processor's work is done.
    */
    private Object _doneCondition = new Object();


    // Constructors

    /**
        Constructs a ThreadPoolWorkProcessor with the specified
        number of threads in its thread pool.

        @param poolSize the number of threads in the work
        processor's thread pool
    */
    public ThreadPoolWorkProcessor(int poolSize)
    {
        Assert.require(poolSize >= 0);

        _workQueue = new LinkedList();
        _actualWorkerCount = 0;
        _workingWorkerCount = 0;
        setPoolSize(poolSize);
    }


    // Public methods

    /**
        Sets the number of threads in this work processor's thread
        pool to the specified number of threads.

        @param poolSize this work processor's new thread pool size
    */
    public synchronized void setPoolSize(int poolSize)
    {
        Assert.require(poolSize >= 0);

        int numWorkersToAdd = poolSize - _desiredWorkerCount;
        _desiredWorkerCount = poolSize;

        if (numWorkersToAdd > 0)
        {
            // Add 'numWorkersToAdd' workers.
            for (int i = 0; i < numWorkersToAdd; i++)
            {
                String name =
                    getWorkerName(_actualWorkerCount + i);
                Worker w = new Worker(name);
                w.start();
            }
        }
        else if (numWorkersToAdd < 0)
        {
            // We need to kill -numWorkersToAdd workers. Have all
            // of the inactive workers wake up to see if they
            // should die.
            notifyAll();
        }
        // Otherwise the pool size hasn't changed, so we don't need
        // to do anything.
    }


    /**
        Adds the specified work to the work that this processor
        is to process.

        @param work the work that this processor is to process
    */
    public synchronized void add(Runnable work)
    {
        Assert.require(work != null);

        _workQueue.addFirst(work);
    }

    /**
        Indicates whether this processor has finished processing
        all of its work.

        @return true if this processor has finished processing all
        of its work, and false if it hasn't
    */
    public synchronized boolean isDone()
    {
        return isDoneUnsynchronized();
    }

    /**
        Waits until this processor has finished all of its work,
        then returns.
        <p>
        Note: this method is not synchronized, and callers do not
        have to hold this instance's monitor.
    */
    public void waitUntilDone()
    {
        synchronized (_doneCondition)
        {
            // The following call to isDone() synchronizes on 'this'.
            while (isDone() == false);
            {
                try
                {
                    _doneCondition.wait();
                }
                catch (InterruptedException ex)
                {
                    // Since we're up, we might as well check
                    // our condition.
                }
            }
        }
    }

    /**
        Note: the result of this method is usually only useful
        in identifying which work processor a worker/thread is
        part of (which can be determined by looking at the
        worker/thread's name).

        @return an ID uniquely identifying this work processor
        @see Thread#getName
    */
    public int id()
    {
        return System.identityHashCode(this);
    }


    // Protected methods

    /**
        Note: this method doesn't need to be synchronized, and the
        caller doesn't need to hold this instance's monitor.

        @return this work processor's monitor
    */
    protected Object monitor()
    {
        return this;
    }

    /**
        Note: this method is not synchronized: it is assumed that
        the caller holds this instance's monitor.

        @return true iff the actual number of workers is greater
        than the desired number of workers
    */
    protected boolean areTooManyWorkers()
    {
        return _actualWorkerCount > _desiredWorkerCount;
    }

    /**
        Returns the name to give the 'workerNumber''th Worker
        in this work processor.
        <p>
        Note: this method doesn't need to be synchronized, and the
        caller doesn't need to hold this instance's monitor.

        @param workerNumber the number of the worker whose name is
        to be returned: the first worker in a work processor would
        have number 1, the second 2, etc.
        @return the worker's name
    */
    protected String getWorkerName(int workerNumber)
    {
        return THREAD_NAME_PREFIX + id() + workerNumber;
    }

    /**
        Causes the calling (presumably Worker) thread to wait on
        this work processor's work queue.
        <p>
        Note: this method is not synchronized: it is assumed that
        the caller holds this instance's monitor.
    */
    protected void waitOnQueue()
    {
        try
        {
            wait();
        }
        catch (InterruptedException ex)
        {
            // empty - since we're up, we might as well check the queue
        }
    }

    /**
        Tries to get a unit of work from this work processor's work
        queue.
        <p>
        Note: this method is not synchronized: it is assumed that the
        caller holds this instance's monitor.

        @return the next unit of work that is to be processed if one
        is available, or null if there isn't one available
    */
    protected Runnable tryToGetWork()
    {
        Runnable result = null;

        if (_workQueue.isEmpty() == false)
        {
            result = (Runnable) _workQueue.removeLast();
            Assert.check(result != null);
        }

        return result;
    }

    /**
        Indicates whether this work processor has more workers than
        it should: that is, whether the size of its thread pool is
        larger than it was last set to be.
        <p>
        Note: this method is not synchronized: it is assumed that the
        caller holds this instance's monitor.

        @return true iff this work processor has more workers than it
        should
    */
    protected boolean tooManyWorkers()
    {
        return (_actualWorkerCount > _desiredWorkerCount);
    }


    // Private methods

    /**
        Indicates whether this work processor is done performing
        all of its work.
        <p>
        This method exists in order to allow synchronized methods
        to check whether all of the work is done without the
        overhead of another synchronization.
        <p>
        Note: this method is not synchronized: it is assumed that
        the calling thread holds this instance's monitor.

        @return true iff this work processor is done performing
        all of its work
    */
    private boolean isDoneUnsynchronized()
    {
        // We're done if there's no more work to do and there are
        // no workers actively working on anything.
        return _workQueue.isEmpty() && _workingWorkerCount == 0;
    }


    // Inner classes

    /**
        Represents a worker thread that processes work on its work
        processor's work queue.
    */
    private class Worker
        extends Thread
    {
        // Constructors

        /**
            Constructs a Worker with the specified name. It is assumed
            that the thread that is creating the Worker holds the worker's
            work processor's monitor.
        */
        public Worker(String name)
        {
            super(name);

            _actualWorkerCount += 1;
        }


        // Public methods

        /**
            @see Thread#run
        */
        public void run()
        {
            /*
                For each iteration:
                    s - wait on the work queue
                    s - check whether we should die, and if so, die
                    s - if there is work
                    s     - remove it from the queue
                    u     - perform the work
                    sd    - check whether all of the work is done
                    s     - check whether we should die, and if so, die
            */
            Object mon = monitor();
            Runnable work;
            while (true)
            {
                synchronized (mon)
                {
                    waitOnQueue();
                    if (tooManyWorkers())
                    {
                        _actualWorkerCount -= 1;
                        break;  // i.e. die
                    }
                    work = tryToGetWork();
                    if (work != null)
                    {
                        _workingWorkerCount += 1;
                    }
                }

                if (work != null)
                {
                    try
                    {
                        work.run();
                    }
                    finally
                    {
                        // We synchronize on _doneCondition first to prevent
                        // us from deadlocking when waitUntilDone() is run
                        // at the same time (since both lock _doneCondition
                        // first, then monitor()).
                        synchronized (_doneCondition)
                        {
                            synchronized (mon)
                            {
                                _workingWorkerCount -= 1;
                                if (isDoneUnsynchronized())
                                {
                                    _doneCondition.notifyAll();
                                }

                                // Note: we don't need to hold _doneCondition
                                // here, but I don't think it will make much
                                // difference in performance if we do. (It
                                // does avoid having to reacquire 'mon'.)
                                if (tooManyWorkers())
                                {
                                    _actualWorkerCount -= 1;
                                    break;  // i.e. die
                                }
                            }
                        }  // synchronized (_doneCondition)
                    }  // finally
                }
            }  // while (true)
        }
    }  // class Worker
}
