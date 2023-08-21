/*
 Copyright (C) 2001-2005 by James MacKay.

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

import com.steelcandy.common.text.TextUtilities;

/**
    An iterator over the elements in an int array.

    @author James MacKay
    @version $Revision: 1.7 $
*/
public class IntArrayIterator
    implements IntIterator
{
    // Private fields

    /** The array being iterated over. */
    private int[] _array;

    /** An iterator over the array's indices. */
    private ConsecutiveIntIterator _iter;


    // Constructors

    /**
        Constructs an IntArrayIterator from the array whose elements the
        iterator is to iterate over.

        @param array the int array whose elements the iterator is to iterate
        over
    */
    public IntArrayIterator(int[] array)
    {
        _array = array;
        _iter = new ConsecutiveIntIterator(0, array.length - 1);
    }


    // Public methods

    /**
        @see IntIterator#hasNext
    */
    public boolean hasNext()
    {
        return _iter.hasNext();
    }

    /**
        @see IntIterator#next
    */
    public int next()
    {
        return _array[_iter.next()];
    }

    /**
        @see IntIterator#peek
    */
    public int peek()
    {
        return _array[_iter.peek()];
    }


    /**
        @see Object#toString
    */
    public String toString()
    {
        String result = TextUtilities.separate(_array, ", ");

        Assert.ensure(result != null);
        return result;
    }
}
