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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;

/**
    The class of LongIntFactory that returns long ints that are guaranteed to
    be different from any of the other long ints returned by the same factory
    since the later of
    <ul>
        <li>when the factory was constructed, and
        <li>the last time reset() was called on the instance.
    </ul>

    @author  James MacKay
*/
public class UniqueLongIntFactory
    implements LongIntFactory
{
    // Constants

    /** The resources used by this class. */
    private static final Resources
        _resources = IntResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_UNIQUE_LONG_INT_LEFT_MSG =
            "NO_UNIQUE_LONG_INT_LEFT_MSG";


    // Private fields

    /** The next long int that this factory will return. */
    private long _next;

    /**
        Indicates whether there is another long int after the one that was
        last returned.
    */
    private boolean _isNext;


    // Constructors

    /**
        Constructs a UniqueLongIntFactory.
    */
    public UniqueLongIntFactory()
    {
        reset();
    }


    // Public methods

    /**
        @see LongIntFactory#reset
    */
    public void reset()
    {
        _next = 0;
        _isNext = true;
    }

    /**
        This method throws an OutOfUniqueValuesException if there are no
        more unique long ints to return.

        @see LongIntFactory#next
    */
    public long next()
    {
        if (_isNext == false)
        {
            String msg = _resources.
                getMessage(NO_UNIQUE_LONG_INT_LEFT_MSG);
            throw new OutOfUniqueValuesException(msg);
        }

        long result = _next;

        _next += 1;
        if (_next == 0)
        {
            _isNext = false;
        }

        return result;
    }
}
