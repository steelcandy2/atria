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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.compare.AbstractComparator;

/**
    A singleton Comparator that compares SourceLocations using their
    startPosition()s' compare() method.

    @author  James MacKay
    @version $Revision$
*/
public class SourceLocationStartPositionComparator
    extends AbstractComparator
{
    // Constants

    /** The sole instance of this class. */
    private static final SourceLocationStartPositionComparator
        _instance = new SourceLocationStartPositionComparator();


    // Constructors

    /**
        @return the sole instance of this class
    */
    public static SourceLocationStartPositionComparator instance()
    {
        return _instance;
    }

    /**
        Constructs the sole instance of this class.
    */
    private SourceLocationStartPositionComparator()
    {
        // empty
    }


    // Protected methods

    /**
        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        SourceLocation loc1 = (SourceLocation) obj1;
        SourceLocation loc2 = (SourceLocation) obj2;

        int result = loc1.startPosition().compare(loc2.startPosition());

        return result;
    }
}
