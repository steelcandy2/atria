/*
 Copyright (C) 2002-2004 by James MacKay.

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
    Represents a stack of OperatorConstruct items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/plack/common/constructs/OperatorConstructStack.java
    </ul>

    @author James MacKay
    @see OperatorConstruct
*/
public class OperatorConstructStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of OperatorConstruct items.
    */
    public OperatorConstructStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified OperatorConstruct onto the top of this
        stack.

        @param item the OperatorConstruct to push onto this stack
    */
    public void push(OperatorConstruct item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the OperatorConstruct from the top of this stack and
        returns it.

        @return the OperatorConstruct popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public OperatorConstruct pop()
        throws NoSuchItemException
    {
        return (OperatorConstruct) popObject();
    }

    /**
        Returns the OperatorConstruct at the top of this stack, without
        removing it.

        @return the OperatorConstruct at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public OperatorConstruct top()
        throws NoSuchItemException
    {
        return (OperatorConstruct) topObject();
    }

    /**
        Returns an iterator over all of the OperatorConstruct items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the OperatorConstruct items in
        this stack, in order
        @see OperatorConstructStack.OperatorConstructStackIterator
    */
    public OperatorConstructIterator iterator()
    {
        return new OperatorConstructStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) OperatorConstructStack.

        @see OperatorConstruct
        @see OperatorConstructStack
    */
    protected static class OperatorConstructStackIterator
        extends TypedStackIterator
        implements OperatorConstructIterator
    {
        // Constructors

        /**
            Constructs a(n) OperatorConstructStackIterator from the
            OperatorConstructStack whose items it is to iterate over.

            @param stack the OperatorConstructStack that the iterator
            is to iterate over the elements of
        */
        public OperatorConstructStackIterator(OperatorConstructStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see OperatorConstructIterator#next
        */
        public OperatorConstruct next()
        {
            return (OperatorConstruct) nextObject();
        }

        /**
            @see OperatorConstructIterator#peek
        */
        public OperatorConstruct peek()
        {
            return (OperatorConstruct) peekObject();
        }
    }
}
