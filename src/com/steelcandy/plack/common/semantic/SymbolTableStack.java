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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of SymbolTable items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/plack/common/semantic/SymbolTableStack.java
    </ul>

    @author James MacKay
    @see SymbolTable
*/
public class SymbolTableStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of SymbolTable items.
    */
    public SymbolTableStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified SymbolTable onto the top of this
        stack.

        @param item the SymbolTable to push onto this stack
    */
    public void push(SymbolTable item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the SymbolTable from the top of this stack and
        returns it.

        @return the SymbolTable popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public SymbolTable pop()
        throws NoSuchItemException
    {
        return (SymbolTable) popObject();
    }

    /**
        Returns the SymbolTable at the top of this stack, without
        removing it.

        @return the SymbolTable at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public SymbolTable top()
        throws NoSuchItemException
    {
        return (SymbolTable) topObject();
    }

    /**
        Returns an iterator over all of the SymbolTable items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the SymbolTable items in
        this stack, in order
        @see SymbolTableStack.SymbolTableStackIterator
    */
    public SymbolTableIterator iterator()
    {
        return new SymbolTableStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) SymbolTableStack.

        @see SymbolTable
        @see SymbolTableStack
    */
    protected static class SymbolTableStackIterator
        extends TypedStackIterator
        implements SymbolTableIterator
    {
        // Constructors

        /**
            Constructs a(n) SymbolTableStackIterator from the
            SymbolTableStack whose items it is to iterate over.

            @param stack the SymbolTableStack that the iterator
            is to iterate over the elements of
        */
        public SymbolTableStackIterator(SymbolTableStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see SymbolTableIterator#next
        */
        public SymbolTable next()
        {
            return (SymbolTable) nextObject();
        }

        /**
            @see SymbolTableIterator#peek
        */
        public SymbolTable peek()
        {
            return (SymbolTable) peekObject();
        }
    }
}
