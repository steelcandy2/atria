/*
 Copyright (C) 2001-2008 by James MacKay.

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

/**
    An abstract base class for classes used to create SourceLocations.

    @author James MacKay
    @see SourceLocation
*/
public abstract class AbstractSourceLocationFactory
    implements SourceLocationFactory
{
    // Factory methods

    /**
        @see SourceLocationFactory#create(int, int, int)
    */
    public SourceLocation create(int lineNumber, int offset, int length)
    {
        Assert.require(lineNumber >= 1);
        Assert.require(offset >= 0);
        Assert.require(length >= 0);

        SourcePosition startPos = new SourcePosition(lineNumber, offset);
        SourceLocation result = create(startPos, length);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SourceLocationFactory#create(SourceLocation, SourceLocation)
    */
    public SourceLocation create(SourceLocation startLoc,
                                 SourceLocation endLoc)
    {
        Assert.require(startLoc != null);
        Assert.require(endLoc != null);

        SourceLocation result = create(startLoc.startPosition(),
                                       endLoc.pastEndPosition());

        Assert.ensure(result != null);
        return result;
    }


    // Utility methods

    /**
        Indicates whether the source locations in the specified list are in
        order and are disjoint from one another, ignoring any and all null
        locations in the list.
        <p>
        Two source locations are considered to be disjoint if the last
        character in the first location precedes the first character in the
        second location. Thus two locations are <em>not</em> considered
        disjoint if a piece of one location is contained in a 'hole' in the
        other location.

        @return true iff the locations in this list are in order and are
        disjoint
    */
    public static boolean areOrderedAndDisjoint(SourceLocationList list)
    {
        // Ignore any null elements in the list.
        SourceLocationList nonNullList = nonNullLocations(list);

        boolean result = true;
        int size = nonNullList.size();
        if (size > 1)
        {
            for (int i = 0; i < size - 1; i++)
            {
                SourceLocation loc1 = nonNullList.get(i);
                SourceLocation loc2 = nonNullList.get(i + 1);

                SourcePosition loc1PastEndPos = loc1.pastEndPosition();
                SourcePosition loc2StartPos = loc2.startPosition();

                Assert.check(loc2StartPos != null);
                if (loc1PastEndPos.compare(loc2StartPos) > 0)
                {
                    // Then the first character of the second location is the
                    // same as or precedes the last character of the first
                    // location. Thus they either overlap (i.e. aren't
                    // disjoint) or are unordered.
                    result = false;
                    break;  // for
                }
            }
        }
        return result;
    }

    /**
        Sorts the specified list of SourceLocations in place in ascending
        order: locations with earlier start positions will be before those
        with later ones, and locations that have the same start positions
        are sorted such that the ones with earlier past-end positions will
        be before the ones with later past-end positions.

        @param list the list to sort (in place)
    */
    protected void sort(SourceLocationList list)
    {
        Assert.require(list != null);

        list.sort(DefaultSourceLocationComparator.create());
    }

    /**
        Sorts the specified list of SourceLocations (as per sort()) in place
        and removes any locations that are duplicates.

        @param list the list to sort and remove duplicates from (in place)
    */
    protected void sortUnique(SourceLocationList list)
    {
        Assert.require(list != null);

        list.sortUnique(DefaultSourceLocationComparator.create());
    }


    /**
        Returns a list consisting of the non-null elements  - and only the
        non-null elements - of the specified list, in order.
        <p>
        The returned list may or may not be the same list as the one
        specified.

        @param list the list from which to get the non-null elements
        @return a list of all of the non-null elements of 'list', in order
    */
    protected static SourceLocationList
        nonNullLocations(SourceLocationList list)
    {
        SourceLocationList result;
        if (areAllNonNull(list))
        {
            result = list;
        }
        else
        {
            result = SourceLocationList.createArrayList();
            SourceLocationIterator iter = list.iterator();
            while (iter.hasNext())
            {
                SourceLocation loc = iter.next();
                if (loc != null)
                {
                    result.add(loc);
                }
            }
        }

        return result;
    }

    /**
        Indicates whether all of the items in the specified list are
        non-null.

        @param list the list to check
        @return true iff all of the items in 'list' are non-null
    */
    protected static boolean areAllNonNull(SourceLocationList list)
    {
        Assert.require(list != null);
        boolean result = true;

        SourceLocationIterator iter = list.iterator();
        while (iter.hasNext())
        {
            if (iter.next() == null)
            {
                result = false;
                break;
            }
        }

        return result;
    }
}
