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
    The interface implemented by classes that return ints, usually in some
    sort of order or pattern.

    @author  James MacKay
    @version $Revision: 1.3 $
*/
public interface IntFactory
{
    // Public methods

    /**
        Resets this factory, so that it behaves as though it was just
        constructed.
    */
    public void reset();

    /**
        @return the next int from this factory without actually consuming it
        (so the next call to next() or peek() will return the same value)
        @exception OutOfUniqueValuesException if this factory has no more
        ints left to return
    */
    public int peek()
        throws OutOfUniqueValuesException;

    /**
        @return the next int from this factory
        @exception OutOfUniqueValuesException if this factory has no more
        ints left to return
    */
    public int next()
        throws OutOfUniqueValuesException;
}
