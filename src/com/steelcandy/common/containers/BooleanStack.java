/*
 Copyright (C) 2004-2005 by James MacKay.

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

/**
    Represents a stack of boolean items.

    @author James MacKay
*/
public class BooleanStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of Boolean items.
    */
    public BooleanStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified boolean onto the top of this stack.

        @param item the boolean to push onto this stack
    */
    public void push(boolean item)
    {
        pushObject(new Boolean(item));

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the boolean from the top of this stack and returns it.

        @return the boolean popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public boolean pop()
        throws NoSuchItemException
    {
        return booleanValue(popObject());
    }

    /**
        Returns the boolean at the top of this stack without removing it.

        @return the boolean at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public boolean top()
        throws NoSuchItemException
    {
        return booleanValue(topObject());
    }

    /**
        Returns an iterator over all of the boolean items in this stack, in
        order from the top of the stack to the bottom. The items are popped
        as they are removed from the iterator.

        @return an iterator over all of the boolean items in this stack, in
        order
        @see BooleanStack.BooleanStackIterator
    */
    public BooleanIterator iterator()
    {
        return new BooleanStackIterator(this);
    }


    // Private static methods

    /**
        @param obj an Object that is assumed to be a Boolean
        @return the boolean value that is the same as 'obj'
    */
    private static boolean booleanValue(Object obj)
    {
        return ((Boolean) obj).booleanValue();
    }


    // Inner classes

    /**
        Iterates through the items in a BooleanStack.

        @see BooleanStack
    */
    protected static class BooleanStackIterator
        extends TypedStackIterator
        implements BooleanIterator
    {
        // Constructors

        /**
            Constructs a BooleanStackIterator from the stack whose items it
            is to iterate over.

            @param stack the stack that the iterator is to iterate over the
            items of
        */
        public BooleanStackIterator(BooleanStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see BooleanIterator#next
        */
        public boolean next()
        {
            return booleanValue(nextObject());
        }

        /**
            @see BooleanIterator#peek
        */
        public boolean peek()
        {
            return booleanValue(peekObject());
        }
    }
}
