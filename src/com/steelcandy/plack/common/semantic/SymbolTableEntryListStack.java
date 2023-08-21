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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of SymbolTableEntryList items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/stack.java.xsl
        <li>Output: src/com/steelcandy/plack/common/semantic/SymbolTableEntryListStack.java
    </ul>

    @author James MacKay
    @see SymbolTableEntryList
*/
public class SymbolTableEntryListStack
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of SymbolTableEntryList items.
    */
    public SymbolTableEntryListStack()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified SymbolTableEntryList onto the top of this
        stack.

        @param item the SymbolTableEntryList to push onto this stack
    */
    public void push(SymbolTableEntryList item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the SymbolTableEntryList from the top of this stack and
        returns it.

        @return the SymbolTableEntryList popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public SymbolTableEntryList pop()
        throws NoSuchItemException
    {
        return (SymbolTableEntryList) popObject();
    }

    /**
        Returns the SymbolTableEntryList at the top of this stack, without
        removing it.

        @return the SymbolTableEntryList at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public SymbolTableEntryList top()
        throws NoSuchItemException
    {
        return (SymbolTableEntryList) topObject();
    }

    /**
        Returns an iterator over all of the SymbolTableEntryList items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the SymbolTableEntryList items in
        this stack, in order
        @see SymbolTableEntryListStack.SymbolTableEntryListStackIterator
    */
    public SymbolTableEntryListIterator iterator()
    {
        return new SymbolTableEntryListStackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) SymbolTableEntryListStack.

        @see SymbolTableEntryList
        @see SymbolTableEntryListStack
    */
    protected static class SymbolTableEntryListStackIterator
        extends TypedStackIterator
        implements SymbolTableEntryListIterator
    {
        // Constructors

        /**
            Constructs a(n) SymbolTableEntryListStackIterator from the
            SymbolTableEntryListStack whose items it is to iterate over.

            @param stack the SymbolTableEntryListStack that the iterator
            is to iterate over the elements of
        */
        public SymbolTableEntryListStackIterator(SymbolTableEntryListStack stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see SymbolTableEntryListIterator#next
        */
        public SymbolTableEntryList next()
        {
            return (SymbolTableEntryList) nextObject();
        }

        /**
            @see SymbolTableEntryListIterator#peek
        */
        public SymbolTableEntryList peek()
        {
            return (SymbolTableEntryList) peekObject();
        }
    }
}
