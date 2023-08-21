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

import com.steelcandy.common.NoSuchItemException;

/**
    An iterator over a sequence of consecutive ints.

    @author James MacKay
    @version $Revision: 1.8 $
*/
public class ConsecutiveIntIterator
    implements IntIterator
{
    // Private fields

    /** The first value in the sequence. */
    private int _first;

    /** The last value in the sequence. */
    private int _last;

    /** The next value to be returned by this iterator. */
    private int _next;


    // Constructors

    /**
        Constructs a ConsecutiveIntIterator from the first and the last
        values in the sequence of consecutive ints the iterator is to
        iterate over.

        @param first the first value in the sequence of ints the iterator is
        to iterate over
        @param last the last value in the sequence of ints the iterator is to
        iterate over
    */
    public ConsecutiveIntIterator(int first, int last)
    {
        _first = first;
        _last  = last;
        _next  = first;
    }


    // Public methods

    /**
        @see IntIterator#hasNext
    */
    public boolean hasNext()
    {
        return _next <= _last;
    }

    /**
        @see IntIterator#next
    */
    public int next()
    {
        if (hasNext())
        {
            return _next++;
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        @see IntIterator#peek
    */
    public int peek()
    {
        if (hasNext())
        {
            return _next;
        }
        else
        {
            throw new NoSuchItemException();
        }
    }


    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();

        for (int i = _first; i <= _last; i++)
        {
            buf.append(i);
            if (i < _last)
            {
                buf.append(", ");
            }
        }

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }
}
