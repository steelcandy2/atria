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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by all iterators over SymbolTable
    items.
    <p>
    Unlike java.util.Iterators this class does not provide the ability to
    remove elements.
    <p>
    The <code>peek()</code> and <code>next()</code> methods throw a
    NoSuchItemException when and only when <code>hasNext()</code> returns false.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/iterator.java.xsl
        <li>Output: src/com/steelcandy/plack/common/semantic/SymbolTableIterator.java
    </ul>

    @author James MacKay
    @see SymbolTable
*/
public interface SymbolTableIterator
{
    // Public methods

    /**
        @return true iff this iterator will return at least one more
        SymbolTable
    */
    public boolean hasNext();

    /**
        @return the next SymbolTable
        @exception NoSuchItemException thrown iff there isn't a next
        SymbolTable
        @see #peek
    */
    public SymbolTable next()
        throws NoSuchItemException;

    /**
        Note: this method can be called before <code>next()</code> has
        ever been called.

        @return the next SymbolTable without consuming it
        (so the next call to peek() or next() will return the same
        SymbolTable)
        @exception NoSuchItemException thrown iff there isn't a next
        SymbolTable
        @see #next
    */
    public SymbolTable peek()
        throws NoSuchItemException;
}
