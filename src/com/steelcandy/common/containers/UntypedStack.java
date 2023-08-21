/*
 Copyright (C) 2003-2005 by James MacKay.

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
import com.steelcandy.common.UnsupportedMethodException;

import com.steelcandy.common.text.TextUtilities;

import java.util.LinkedList;
import java.util.Iterator;

/**
    Represents an (untyped) stack.

    @author James MacKay
    @version $Revision: 1.2 $
    @see TypedStack
*/
public class UntypedStack
{
    // Constants

    /** Prefixes used in converting the stack items in toString(). */
    private static final String TOP_PREFIX    = "  [top]    ";
    private static final String BOTTOM_PREFIX = "  [bottom] ";
    private static final String NORM_PREFIX   = "           ";


    // Private fields

    /**
        The items in this stack. The first item in the list is at the bottom
        of the stack and the last item in the list is at the top of the
        stack.
        <p>
        This variable is declared as a LinkedList rather than a List since
        this class uses several of the methods that are declared in
        LinkedList but are not declared in List (e.g.
        <code>removeLast()</code>).
    */
    private LinkedList _items;


    // Constructors

    /**
        Constructs an empty stack.
    */
    public UntypedStack()
    {
        _items = new LinkedList();
    }


    // Public methods

    /**
        Removes all of the items from this stack, leaving it empty.
    */
    public void removeAll()
    {
        _items.clear();

        Assert.ensure(isEmpty());
    }

    /**
        @return the number of items in this stack
    */
    public int size()
    {
        return _items.size();
    }

    /**
        @return true iff this stack contains no items
    */
    public boolean isEmpty()
    {
        return _items.isEmpty();
    }

    /**
        Pushes the specified object onto the top of this stack.

        @param obj the object to push onto this stack
    */
    public void push(Object obj)
    {
        _items.add(obj);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the object at the top of this stack off of the stack and returns
        it.

        @return the object popped off of the top of this stack
        @exception NoSuchItemException thrown if this stack is empty
    */
    public Object pop()
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
        Returns the object at the top of this stack without removing it.

        @return the object at the top of this stack
        @exception NoSuchItemException thrown if this stack is empty
    */
    public Object top()
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

    /**
        Note: the returned iterator does not support the
        <code>remove()</code> operation.

        @return an iterator over the items in this stack. It returns the
        items in LIFO order, removing items as it iterates (as though its
        next() method called this stack's pop() method)
        @see Iterator#remove
    */
    public Iterator iterator()
    {
        return new UntypedStackIterator(this);
    }


    // Overridden/implemented methods

    /**
        Note: a stack with a single item will have that item labelled as the
        stack's top item in the string.

        @see Object#toString
    */
    public String toString()
    {
        StringBuffer result = new StringBuffer(getClass().getName());
        result.append(":").append(TextUtilities.NL);

        Iterator iter = _items.iterator();
        String prefix = BOTTOM_PREFIX;
        while (iter.hasNext())
        {
            Object item = iter.next();
            if (iter.hasNext() == false)
            {
                prefix = TOP_PREFIX;
            }
            result.append(prefix).append(item.toString()).
                   append(TextUtilities.NL);
            prefix = NORM_PREFIX;
        }

        return result.toString();
    }


    // Inner classes

    /**
        An abstract base class for iterators that iterate through the items
        in an UntypedStack. The items are popped from the stack as they are
        iterated over.
    */
    protected static class UntypedStackIterator
        implements Iterator
    {
        // Private fields

        /** The UntypedStack to iterate through. */
        private UntypedStack _stack;


        // Constructors

        /**
            Constructs an UntypedStackIterator from the stack whose items it
            is to iterate over.

            @param stack the stack the iterator is to iterator over the items
            of
        */
        public UntypedStackIterator(UntypedStack stack)
        {
            _stack = stack;
        }


        // Public methods

        /**
            @see Iterator#hasNext
        */
        public boolean hasNext()
        {
            return (_stack.isEmpty() == false);
        }

        /**
            @see Iterator#next()
        */
        public Object next()
        {
            return _stack.pop();
        }

        /**
            This operation is unsupported.

            @see Iterator#remove
        */
        public void remove()
        {
            throw new UnsupportedMethodException(getClass(), "remove()");
        }
    }
}
