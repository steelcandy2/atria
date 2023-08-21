/*
 Copyright (C) 2003-2004 by James MacKay.

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

import com.steelcandy.common.containers.TypedQueue;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a queue of Runnable items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/queue.java.xsl
        <li>Output: src/com/steelcandy/common/work/RunnableQueue.java
    </ul>

    @author James MacKay
    @see Runnable
*/
public class RunnableQueue
    extends TypedQueue
{
    // Constructors

    /**
        Constructs an empty queue of Runnable items.
    */
    public RunnableQueue()
    {
        // empty
    }


    // Public methods

    /**
        Adds the specified Runnable to this queue at its tail.

        @param item the Runnable to enqueue
    */
    public void enqueue(Runnable item)
    {
        enqueueObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Removes the Runnable from the head of this queue and
        returns it.

        @return the Runnable removed from the head of this queue
        @exception NoSuchItemException thrown if the queue is empty
    */
    protected Runnable dequeue()
        throws NoSuchItemException
    {
        return (Runnable) dequeueObject();
    }

    /**
        Returns the Runnable at the head of this queue without
        removing it.

        @return the Runnable at the head of this queue
        @exception NoSuchItemException thrown if this queue is empty
    */
    protected Runnable peek()
        throws NoSuchItemException
    {
        return (Runnable) peekObject();
    }

    /**
        Returns an iterator over all of the Runnable items in
        this queue, in order. The items are dequeued as they are removed
        from the iterator.

        @return an iterator over all of the Runnable items in this
        queue, in order
        @see RunnableQueue.RunnableQueueIterator
    */
    public RunnableIterator iterator()
    {
        return new RunnableQueueIterator(this);
    }


    // Inner classes

    /**
        Iterates through the Runnable items in a RunnableQueue.

        @see Runnable
        @see RunnableQueue
    */
    protected static class RunnableQueueIterator
        extends TypedQueueIterator
        implements RunnableIterator
    {
        // Constructors

        /**
            Constructs a(n) RunnableQueueIterator from the queue
            whose Runnable items it is to iterate over.

            @param queue the RunnableQueue that the iterator is to
            iterate over the elements of
        */
        public RunnableQueueIterator(RunnableQueue queue)
        {
            super(queue);
        }


        // Public methods

        /**
            @see RunnableIterator#next
        */
        public Runnable next()
        {
            return (Runnable) nextObject();
        }

        /**
            @see RunnableIterator#peek
        */
        public Runnable peek()
        {
            return (Runnable) peekObject();
        }
    }
}
