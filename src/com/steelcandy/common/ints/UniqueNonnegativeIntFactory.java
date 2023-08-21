/*
 Copyright (C) 2004-2012 by James MacKay.

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
    The class of IntFactory that returns ints that are guaranteed to be
    non-negative and different from any of the other ints returned by the
    same factory since the later of
    <ul>
        <li>when the factory was constructed, and
        <li>the last time reset() was called on the instance.
    </ul>

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class UniqueNonnegativeIntFactory
    implements IntFactory
{
    // Constants

    /** The resources used by this class. */
    private static final Resources
        _resources = IntResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_UNIQUE_NONNEGATIVE_INT_LEFT_MSG =
            "NO_UNIQUE_NONNEGATIVE_INT_LEFT_MSG";


    // Private fields

    /** The next int that this factory will return. */
    private int _next;


    // Constructors

    /**
        Constructs a UniqueNonnegativeIntFactory.
    */
    public UniqueNonnegativeIntFactory()
    {
        reset();
    }


    // Public methods

    /**
        @see IntFactory#reset
    */
    public void reset()
    {
        _next = 0;
    }

    /**
        @see IntFactory#peek
    */
    public int peek()
    {
        return _next;
    }

    /**
        This method throws an OutOfUniqueValuesException if there are no
        more unique non-negative ints to return.

        @see IntFactory#next
    */
    public int next()
    {
        checkIsNext();

        int result = _next;

        _next += 1;

        return result;
    }


    // Protected methods

    /**
        @exception throws an OutOfUniqueValuesException iff there are no
        more unique ints to return
    */
    protected void checkIsNext()
        throws OutOfUniqueValuesException
    {
        if (_next < 0)
        {
            String msg = _resources.
                getMessage(NO_UNIQUE_NONNEGATIVE_INT_LEFT_MSG);
            throw new OutOfUniqueValuesException(msg);
        }
    }
}
