/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of int items.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class IntStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of int items.
    */
    public IntStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified int onto the top of this stack.

        @param item the int to push onto this stack
    */
    public void push(int item)
    {
        pushObject(new Integer(item));

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the int from the top of this stack and returns it.

        @return the int popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public int pop()
        throws NoSuchItemException
    {
        return intValue(popObject());
    }

    /**
        Returns the int at the top of this stack without removing it.

        @return the int at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public int top()
        throws NoSuchItemException
    {
        return intValue(topObject());
    }

    /**
        Returns an iterator over all of the int items in this stack, in
        order from the top of the stack to the bottom. The items are popped
        as they are removed from the iterator.

        @return an iterator over all of the int items in this stack, in order
        @see IntStack.IntStackIterator
    */
    public IntIterator iterator()
    {
        return new IntStackIterator(this);
    }


    // Private static methods

    /**
        @param obj an Object that is assumed to be an Integer
        @return the int value that is the same as 'obj'
    */
    private static int intValue(Object obj)
    {
        return ((Integer) obj).intValue();
    }


    // Inner classes

    /**
        Iterates through the items in an IntStack.

        @see IntStack
    */
    protected static class IntStackIterator
        extends TypedStackIterator
        implements IntIterator
    {
        // Constructors

        /**
            Constructs an IntStackIterator from the stack whose items it is
            to iterate over.

            @param stack the stack that the iterator is to iterate over the
            items of
        */
        public IntStackIterator(IntStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see IntIterator#next
        */
        public int next()
        {
            return intValue(nextObject());
        }

        /**
            @see IntIterator#peek
        */
        public int peek()
        {
            return intValue(peekObject());
        }
    }
}
