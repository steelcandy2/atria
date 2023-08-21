/*
 Copyright (C) 2005-2009 by James MacKay.

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
    The default SourceLocation comparator: locations with earlier start
    positions compare as less than those with later ones, and locations
    with the same start positions compare such that those with earlier
    past-end positions are less than those with later past-end positions.
    Locations with the same start and past-end positions will compare as
    equal.

    @author  James MacKay
*/
public class DefaultSourceLocationComparator
    extends AbstractComparator
{
    // Constants

    /**
        The single instance of this class.
        <p>
        Since this class has no state the same instance can be shared across
        threads.
    */
    private static final DefaultSourceLocationComparator
        _instance = new DefaultSourceLocationComparator();


    // Constructors

    /**
        @return an instance of this class
    */
    public static DefaultSourceLocationComparator create()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs a DefaultSourceLocationComparator.
    */
    private DefaultSourceLocationComparator()
    {
        // empty
    }


    // Protected methods

    /**
        Note: all SourceLocations compare greater than any
        non-SourceLocation, and all non-SourceLocations compare equal to
        each other.

        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        boolean isObj1Location = (obj1 instanceof SourceLocation);
        boolean isObj2Location = (obj2 instanceof SourceLocation);

        int result;

        if (isObj1Location && isObj2Location)
        {
            result = compareNonNullLocations((SourceLocation) obj1,
                                             (SourceLocation) obj2);
        }
        else
        {
            result = compareWhenNotBothRightTypes(isObj1Location,
                                                  isObj2Location);
        }

        return result;
    }

    /**
        Compares the specified non-null SourceLocations, returning -1, 0 or 1
        if the first is less than, equal to or greater than the second,
        respectively.

        @param loc1 the first location
        @param loc2 the second location
    */
    protected int compareNonNullLocations(SourceLocation loc1,
                                          SourceLocation loc2)
    {
        Assert.require(loc1 != null);
        Assert.require(loc2 != null);

        int result = loc1.startPosition().compare(loc2.startPosition());

        if (result == 0)
        {
            result = loc1.pastEndPosition().compare(loc2.pastEndPosition());
        }

        return result;
    }
}
