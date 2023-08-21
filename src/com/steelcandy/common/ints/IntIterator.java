/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by all iterators over int items.
    <p>
    Unlike java.util.Iterators this class does not provide the ability to
    remove elements.
    <p>
    The <code>peek()</code> and <code>next()</code> methods throw a
    NoSuchItemException when and only when <code>hasNext()</code> returns
    false.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public interface IntIterator
{
    // Public methods

    /**
        @return true iff this iterator will return at least one more int
    */
    public boolean hasNext();

    /**
        @return the next int
        @exception NoSuchItemException thrown iff there isn't a next int
        @see #peek
    */
    public int next()
        throws NoSuchItemException;

    /**
        @return the next int without consuming it (so the next call to peek()
        or next() will return the same int)
        @exception NoSuchItemException thrown iff there isn't a next int
        @see #next
    */
    public int peek()
        throws NoSuchItemException;
}
