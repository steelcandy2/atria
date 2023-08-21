/*
 Copyright (C) 2001-2002 by James MacKay.

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

/**
    A WorkProcessor that creates a new thread to process
    each piece of work.

    @author James MacKay
*/
public class ThreadWorkProcessor
    implements WorkProcessor
{
    // Private fields

    /** The number of pieces of work currently being processed. */
    private int _workCount = 0;

    /**
        The condition variable used to wait on the condition
        that all of this processor's work is done.
    */
    private Object _doneCondition = new Object();


    // Constructors

    /**
        Constructs a ThreadWorkProcessor.
    */
    public ThreadWorkProcessor()
    {
        // empty
    }


    // Public methods

    /**
        @see WorkProcessor#add
    */
    public synchronized void add(Runnable work)
    {
        Assert.require(work != null);

        _workCount++;
        new Thread(new Worker(work));
    }

    /**
        @see WorkProcessor#isDone
    */
    public synchronized boolean isDone()
    {
        return (_workCount == 0);
    }

    /**
        @see WorkProcessor#waitUntilDone
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


    // Inner classes

    /**
        A Runnable class that performs a single piece of work.
    */
    protected class Worker implements Runnable
    {
        // Private fields

        /** The work that this worker processes. */
        private Runnable _work;


        // Constructors

        /**
            Constructs a Worker from the work it is to do.
        */
        public Worker(Runnable work)
        {
            Assert.require(work != null);

            _work = work;
        }


        // Public methods

        /**
            Performs this worker's work, decrements the number
            of pieces of work currently being processed, and if
            there is then no work being processed notifies anyone
            waiting for that condition to become true.

            @see Runnable#run
        */
        public void run()
        {
            try
            {
                _work.run();
            }
            finally
            {
                // We synchronize on _doneCondition first to prevent
                // us from deadlocking when waitUntilDone() is run
                // at the same time (since both lock _doneCondition
                // first, then monitor()).
                synchronized (_doneCondition)
                {
                    synchronized (monitor())
                    {
                        if (--_workCount == 0)
                        {
                            _doneCondition.notifyAll();
                        }
                    }
                }
            }
        }   // public void run()
    }
}
