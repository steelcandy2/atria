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
    Represents a stack of Construct items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/plack/common/constructs/ConstructStack.java
    </ul>

    @author James MacKay
    @see Construct
*/
public class ConstructStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of Construct items.
    */
    public ConstructStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified Construct onto the top of this
        stack.

        @param item the Construct to push onto this stack
    */
    public void push(Construct item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the Construct from the top of this stack and
        returns it.

        @return the Construct popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public Construct pop()
        throws NoSuchItemException
    {
        return (Construct) popObject();
    }

    /**
        Returns the Construct at the top of this stack, without
        removing it.

        @return the Construct at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public Construct top()
        throws NoSuchItemException
    {
        return (Construct) topObject();
    }

    /**
        Returns an iterator over all of the Construct items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the Construct items in
        this stack, in order
        @see ConstructStack.ConstructStackIterator
    */
    public ConstructIterator iterator()
    {
        return new ConstructStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) ConstructStack.

        @see Construct
        @see ConstructStack
    */
    protected static class ConstructStackIterator
        extends TypedStackIterator
        implements ConstructIterator
    {
        // Constructors

        /**
            Constructs a(n) ConstructStackIterator from the
            ConstructStack whose items it is to iterate over.

            @param stack the ConstructStack that the iterator
            is to iterate over the elements of
        */
        public ConstructStackIterator(ConstructStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see ConstructIterator#next
        */
        public Construct next()
        {
            return (Construct) nextObject();
        }

        /**
            @see ConstructIterator#peek
        */
        public Construct peek()
        {
            return (Construct) peekObject();
        }
    }
}
