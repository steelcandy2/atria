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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of ConstructList items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/plack/common/constructs/ConstructListStack.java
    </ul>

    @author James MacKay
    @see ConstructList
*/
public class ConstructListStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of ConstructList items.
    */
    public ConstructListStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified ConstructList onto the top of this
        stack.

        @param item the ConstructList to push onto this stack
    */
    public void push(ConstructList item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the ConstructList from the top of this stack and
        returns it.

        @return the ConstructList popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public ConstructList pop()
        throws NoSuchItemException
    {
        return (ConstructList) popObject();
    }

    /**
        Returns the ConstructList at the top of this stack, without
        removing it.

        @return the ConstructList at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public ConstructList top()
        throws NoSuchItemException
    {
        return (ConstructList) topObject();
    }

    /**
        Returns an iterator over all of the ConstructList items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the ConstructList items in
        this stack, in order
        @see ConstructListStack.ConstructListStackIterator
    */
    public ConstructListIterator iterator()
    {
        return new ConstructListStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) ConstructListStack.

        @see ConstructList
        @see ConstructListStack
    */
    protected static class ConstructListStackIterator
        extends TypedStackIterator
        implements ConstructListIterator
    {
        // Constructors

        /**
            Constructs a(n) ConstructListStackIterator from the
            ConstructListStack whose items it is to iterate over.

            @param stack the ConstructListStack that the iterator
            is to iterate over the elements of
        */
        public ConstructListStackIterator(ConstructListStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see ConstructListIterator#next
        */
        public ConstructList next()
        {
            return (ConstructList) nextObject();
        }

        /**
            @see ConstructListIterator#peek
        */
        public ConstructList peek()
        {
            return (ConstructList) peekObject();
        }
    }
}
