/*
 Copyright (C) 2004-2009 by James MacKay.

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

import com.steelcandy.common.compare.AbstractComparator;

import java.util.Collection;

/**
    A singleton comparator that orders Collection objects by their sizes:
    larger Collections are considered to be greater than smaller Collections.
    <p>
    All non-Collection objects are considered to be greater than all
    Collection objects, and all non-Collection objects are considered to be
    equal to each other.

    @author  James MacKay
    @version $Revision: 1.3 $
*/
public class CollectionSizeComparator
    extends AbstractComparator
{
    // Constants

    /** The single instance of this class. */
    private static final CollectionSizeComparator
        _instance = new CollectionSizeComparator();


    // Constructors

    /** @return the single instance of this class. */
    public static CollectionSizeComparator instance()
    {
        return _instance;
    }

    /**
        Constructs a CollectionSizeComparator.
    */
    protected CollectionSizeComparator()
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

        boolean obj1IsCollection = (obj1 instanceof Collection);
        boolean obj2IsCollection = (obj2 instanceof Collection);

        int result = 0;

        if (obj1IsCollection && obj2IsCollection)
        {
            Collection c1 = (Collection) obj1;
            Collection c2 = (Collection) obj2;

            // c1 < c2 iff c1.size() < c2.size()
            result = c2.size() - c1.size();
        }
        else
        {
            result = compareWhenNotBothRightTypes(obj1IsCollection,
                                                  obj2IsCollection);
        }

        return result;
    }
}
