/*
 Copyright (C) 2001-2005 by James MacKay.

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

package com.steelcandy.common.containers;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

import com.steelcandy.common.text.TextUtilities;

import java.util.LinkedList;
import java.util.Iterator;

/**
    An abstract base class for type-safe queues.
    <p>
    This class provides implementations of those methods that do not depend
    on the item type, and also manages the backing list.

    @author James MacKay
    @version $Revision: 1.9 $
*/
public abstract class TypedQueue
{
    // Constants

    /** Prefixes used in converting the queue items in toString(). */
    private static final String TAIL_PREFIX = "  [tail] ";
    private static final String HEAD_PREFIX = "  [head] ";
    private static final String NORM_PREFIX = "         ";


    // Private fields

    /**
        The items in this queue. The first item in the list is the tail of
        the queue and the last item in the list is the head of the queue.
        <p>
        This variable is declared as a LinkedList rather than a List since
        this class uses several of the methods that are declared in
        LinkedList but are not declared in List.
    */
    private LinkedList _items;


    // Constructors

    /**
        Constructs an empty queue.
    */
    public TypedQueue()
    {
        _items = new LinkedList();
    }


    // Public methods

    /**
        Removes all of the items from this queue, leaving it empty.
    */
    public void removeAll()
    {
        _items.clear();

        Assert.ensure(isEmpty());
    }

    /**
        @return the number of items in this queue
    */
    public int size()
    {
        return _items.size();
    }

    /**
        @return true iff this queue contains no items
    */
    public boolean isEmpty()
    {
        return _items.isEmpty();
    }


    // Overridden/implemented methods

    /**
        Note: a queue with a single item will have that item labelled as the
        queue's head item in the string.

        @see Object#toString
    */
    public String toString()
    {
        StringBuffer result = new StringBuffer(getClass().getName());
        result.append(":").append(TextUtilities.NL);

        Iterator iter = _items.iterator();
        String prefix = TAIL_PREFIX;
        while (iter.hasNext())
        {
            Object item = iter.next();
            if (iter.hasNext() == false)
            {
                prefix = HEAD_PREFIX;
            }
            result.append(prefix).append(item.toString()).
                   append(TextUtilities.NL);
            prefix = NORM_PREFIX;
        }

        return result.toString();
    }


    // Protected methods

    /**
        Adds the specified object to this queue at its tail.

        @param obj the object to enqueue
    */
    protected void enqueueObject(Object obj)
    {
        _items.addFirst(obj);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Removes the object from the head of this queue and returns it.

        @return the object removed from the head of this queue
        @exception NoSuchItemException thrown if this queue is empty
    */
    protected Object dequeueObject()
        throws NoSuchItemException
    {
        if (isEmpty() == false)
        {
            return _items.removeLast();
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        Returns the object at the head of this queue without removing it.

        @return the object at the head of this queue
        @exception NoSuchItemException thrown if this queue is empty
    */
    protected Object peekObject()
        throws NoSuchItemException
    {
        if (isEmpty() == false)
        {
            return _items.getLast();
        }
        else
        {
            throw new NoSuchItemException();
        }
    }


    // Inner classes

    /**
        An abstract base class for iterators that iterate through all of the
        items in a TypedQueue. The items are dequeued from the queue as they
        are iterated over.

        @see TypedList
    */
    protected abstract static class TypedQueueIterator
    {
        // Private fields

        /** The TypedQueue to iterate through. */
        private TypedQueue _queue;


        // Constructors

        /**
            Constructs a TypedQueueIterator from the TypedQueue whose items
            it is to iterate over.

            @param queue the TypedQueue the iterator is to iterate over the
            items of
        */
        public TypedQueueIterator(TypedQueue queue)
        {
            _queue = queue;
        }


        // Public methods

        /**
            Indicates whether this iterator has any more items left to
            iterate over.

            @return true iff this iterator has any more items left to
            iterate over
        */
        public boolean hasNext()
        {
            return (_queue.isEmpty() == false);
        }


        // Protected methods

        /**
            @return the next item
            @exception NoSuchItemException thrown if there is no next object
            @see #peekObject
        */
        protected Object nextObject()
        {
            return _queue.dequeueObject();
        }

        /**
            @return the next item without consuming it (so the next call to
            peekObject() or nextObject() will return the same item)
            @exception NoSuchItemException thrown if there is no next object
            @see #nextObject
        */
        protected Object peekObject()
        {
            return _queue.peekObject();
        }
    }
}
