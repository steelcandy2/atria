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

package com.steelcandy.common.compare;

import com.steelcandy.common.debug.Assert;

import java.util.Comparator;

/**
    An abstract base class for Comparators. It considers null to be
    strictly less than every non-null value.
    <p>
    Subclasses just have to implement compareNonNull().

    @author James MacKay
    @version $Revision: 1.4 $
*/
public abstract class AbstractComparator
    implements Comparator
{
    // Public methods

    /**
        @see Comparator#compare(Object, Object)
    */
    public int compare(Object obj1, Object obj2)
    {
        int result;

        // Assumes null is less than all non-null values.
        if (obj1 != null && obj2 != null)
        {
            result = compareNonNull(obj1, obj2);
        }
        else if (obj1 == null && obj2 == null)
        {
            result = 0;
        }
        else if (obj1 == null)
        {
            result = -1;
        }
        else  // obj2 == null
        {
            result = 1;
        }

        return result;
    }


    // Protected methods

    /**
        Compares the specified ints.

        @param i1 the first int
        @param i2 the second int
        @return a negative, zero or positive value if 'i1' is less
        than, equal to or greater than 'i2'
    */
    protected int compareInts(int i1, int i2)
    {
        return i1 - i2;
    }

    /**
        Compares the specified Strings in the usual way: nulls compare
        less than all non-null values, and two non-null values are
        compared lexicographically.

        @param str1 the first String
        @param str2 the second String
        @return a negative, zero or positive value if 'str1' is less
        than, equal to or greater than 'str2'
        @see DefaultStringComparator
    */
    protected int compareStrings(String str1, String str2)
    {
        return DefaultStringComparator.instance().compare(str1, str2);
    }

    /**
        Returns the result of comparing two objects when one or both of them
        aren't the type of object a subclass is intended to compare: an
        instance of the right type compares greater than one not of the
        right type, and two instances that are both not of the right type
        compare equal to each other.

        @param isObj1RightType true iff the first object being compared is
        of the right type
        @param isObj2RightType true iff the second object being compared is
        of the right type
    */
    protected int compareWhenNotBothRightTypes(boolean isObj1RightType,
                                               boolean isObj2RightType)
    {
        Assert.require(isObj1RightType == false ||
                       isObj2RightType == false);

        int result;

        if (isObj1RightType == false && isObj2RightType == false)
        {
            // All objects not of the right type compare equal to each other.
            result = 0;
        }
        else if (isObj1RightType)
        {
            // obj1 > obj2 (right type > wrong type)
            result = 1;
        }
        else  // isObj2RightType
        {
            // obj1 < obj2 (wrong type < right type)
            result = -1;
        }

        return result;
    }


    // Abstract methods

    /**
        Compares the specified objects, neither of which is null.

        @param obj1 the first object
        @param obj2 the second object
        @return a negative, zero or positive integer if 'obj1' is
        less than, equal to or greater than 'obj2', respectively
    */
    protected abstract int compareNonNull(Object obj1, Object obj2);
        // Assert.require(obj1 != null);
        // Assert.require(obj2 != null);
}
