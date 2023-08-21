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
    The interface implemented by classes that return long ints, usually in
    some sort of order or pattern.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public interface LongIntFactory
{
    // Public methods

    /**
        Resets this factory, so that it behaves as though it was just
        constructed.
    */
    public void reset();

    /**
        @return the next long int from this factory
        @exception OutOfUniqueValuesException if this factory has no more
        long ints left to return
    */
    public long next();
}
