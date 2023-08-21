/*
 Copyright (C) 2006 by James MacKay.

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
import java.util.Set;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of Set items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/common/containers/SetStack.java
    </ul>

    @author James MacKay
    @see Set
*/
public class SetStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of Set items.
    */
    public SetStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified Set onto the top of this
        stack.

        @param item the Set to push onto this stack
    */
    public void push(Set item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the Set from the top of this stack and
        returns it.

        @return the Set popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public Set pop()
        throws NoSuchItemException
    {
        return (Set) popObject();
    }

    /**
        Returns the Set at the top of this stack, without
        removing it.

        @return the Set at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public Set top()
        throws NoSuchItemException
    {
        return (Set) topObject();
    }

    /**
        Returns an iterator over all of the Set items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the Set items in
        this stack, in order
        @see SetStack.SetStackIterator
    */
    public SetIterator iterator()
    {
        return new SetStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) SetStack.

        @see Set
        @see SetStack
    */
    protected static class SetStackIterator
        extends TypedStackIterator
        implements SetIterator
    {
        // Constructors

        /**
            Constructs a(n) SetStackIterator from the
            SetStack whose items it is to iterate over.

            @param stack the SetStack that the iterator
            is to iterate over the elements of
        */
        public SetStackIterator(SetStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see SetIterator#next
        */
        public Set next()
        {
            return (Set) nextObject();
        }

        /**
            @see SetIterator#peek
        */
        public Set peek()
        {
            return (Set) peekObject();
        }
    }
}
