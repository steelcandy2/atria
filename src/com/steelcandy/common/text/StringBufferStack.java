/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.common.text;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of StringBuffer items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/common/text/StringBufferStack.java
    </ul>

    @author James MacKay
    @see StringBuffer
*/
public class StringBufferStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of StringBuffer items.
    */
    public StringBufferStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified StringBuffer onto the top of this
        stack.

        @param item the StringBuffer to push onto this stack
    */
    public void push(StringBuffer item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the StringBuffer from the top of this stack and
        returns it.

        @return the StringBuffer popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public StringBuffer pop()
        throws NoSuchItemException
    {
        return (StringBuffer) popObject();
    }

    /**
        Returns the StringBuffer at the top of this stack, without
        removing it.

        @return the StringBuffer at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public StringBuffer top()
        throws NoSuchItemException
    {
        return (StringBuffer) topObject();
    }

    /**
        Returns an iterator over all of the StringBuffer items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the StringBuffer items in
        this stack, in order
        @see StringBufferStack.StringBufferStackIterator
    */
    public StringBufferIterator iterator()
    {
        return new StringBufferStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) StringBufferStack.

        @see StringBuffer
        @see StringBufferStack
    */
    protected static class StringBufferStackIterator
        extends TypedStackIterator
        implements StringBufferIterator
    {
        // Constructors

        /**
            Constructs a(n) StringBufferStackIterator from the
            StringBufferStack whose items it is to iterate over.

            @param stack the StringBufferStack that the iterator
            is to iterate over the elements of
        */
        public StringBufferStackIterator(StringBufferStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see StringBufferIterator#next
        */
        public StringBuffer next()
        {
            return (StringBuffer) nextObject();
        }

        /**
            @see StringBufferIterator#peek
        */
        public StringBuffer peek()
        {
            return (StringBuffer) peekObject();
        }
    }
}
