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
import java.io.StringWriter;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of StringWriter items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/common/io/StringWriterStack.java
    </ul>

    @author James MacKay
    @see StringWriter
*/
public class StringWriterStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of StringWriter items.
    */
    public StringWriterStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified StringWriter onto the top of this
        stack.

        @param item the StringWriter to push onto this stack
    */
    public void push(StringWriter item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the StringWriter from the top of this stack and
        returns it.

        @return the StringWriter popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public StringWriter pop()
        throws NoSuchItemException
    {
        return (StringWriter) popObject();
    }

    /**
        Returns the StringWriter at the top of this stack, without
        removing it.

        @return the StringWriter at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public StringWriter top()
        throws NoSuchItemException
    {
        return (StringWriter) topObject();
    }

    /**
        Returns an iterator over all of the StringWriter items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the StringWriter items in
        this stack, in order
        @see StringWriterStack.StringWriterStackIterator
    */
    public StringWriterIterator iterator()
    {
        return new StringWriterStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) StringWriterStack.

        @see StringWriter
        @see StringWriterStack
    */
    protected static class StringWriterStackIterator
        extends TypedStackIterator
        implements StringWriterIterator
    {
        // Constructors

        /**
            Constructs a(n) StringWriterStackIterator from the
            StringWriterStack whose items it is to iterate over.

            @param stack the StringWriterStack that the iterator
            is to iterate over the elements of
        */
        public StringWriterStackIterator(StringWriterStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see StringWriterIterator#next
        */
        public StringWriter next()
        {
            return (StringWriter) nextObject();
        }

        /**
            @see StringWriterIterator#peek
        */
        public StringWriter peek()
        {
            return (StringWriter) peekObject();
        }
    }
}
