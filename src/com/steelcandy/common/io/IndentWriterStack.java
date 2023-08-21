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

package com.steelcandy.common.io;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of IndentWriter items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/common/io/IndentWriterStack.java
    </ul>

    @author James MacKay
    @see IndentWriter
*/
public class IndentWriterStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of IndentWriter items.
    */
    public IndentWriterStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified IndentWriter onto the top of this
        stack.

        @param item the IndentWriter to push onto this stack
    */
    public void push(IndentWriter item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the IndentWriter from the top of this stack and
        returns it.

        @return the IndentWriter popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public IndentWriter pop()
        throws NoSuchItemException
    {
        return (IndentWriter) popObject();
    }

    /**
        Returns the IndentWriter at the top of this stack, without
        removing it.

        @return the IndentWriter at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public IndentWriter top()
        throws NoSuchItemException
    {
        return (IndentWriter) topObject();
    }

    /**
        Returns an iterator over all of the IndentWriter items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the IndentWriter items in
        this stack, in order
        @see IndentWriterStack.IndentWriterStackIterator
    */
    public IndentWriterIterator iterator()
    {
        return new IndentWriterStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) IndentWriterStack.

        @see IndentWriter
        @see IndentWriterStack
    */
    protected static class IndentWriterStackIterator
        extends TypedStackIterator
        implements IndentWriterIterator
    {
        // Constructors

        /**
            Constructs a(n) IndentWriterStackIterator from the
            IndentWriterStack whose items it is to iterate over.

            @param stack the IndentWriterStack that the iterator
            is to iterate over the elements of
        */
        public IndentWriterStackIterator(IndentWriterStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see IndentWriterIterator#next
        */
        public IndentWriter next()
        {
            return (IndentWriter) nextObject();
        }

        /**
            @see IndentWriterIterator#peek
        */
        public IndentWriter peek()
        {
            return (IndentWriter) peekObject();
        }
    }
}
