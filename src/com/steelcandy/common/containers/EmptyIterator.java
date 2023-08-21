/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common.containers;

import com.steelcandy.common.debug.Assert;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
    Represents an empty iterator.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class EmptyIterator
    implements Iterator
{
    // Private fields

    /**
        The single instance of this class. (There's only one since an empty
        iterator can't be modified, and so there's no point in having more
        than one.)
    */
    private static EmptyIterator _instance = new EmptyIterator();


    // Constructors

    /**
        Returns the single instance of this class.

        @return the single instance of this class
    */
    public static EmptyIterator instance()
    {
        return _instance;
    }

    /**
        Default constructor.
    */
    private EmptyIterator()
    {
        // empty
    }


    // Overridden methods

    /**
        @see Iterator#hasNext
    */
    public boolean hasNext()
    {
        return false;
    }

    /**
        @exception NoSuchElementException always thrown since there are no
        elements
        @see Iterator#next
    */
    public Object next()
    {
        throw new NoSuchElementException();
    }

    /**
        @exception IllegalStateException always thrown since next() must
        be called first, but next() can never succeed
        @see Iterator#remove
    */
    public void remove()
    {
        throw new IllegalStateException();
    }
}
