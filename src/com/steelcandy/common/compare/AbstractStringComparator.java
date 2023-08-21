/*
 Copyright (C) 2015 by James MacKay.

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

/**
    An abstract base class for Comparator classes that compare Strings in
    the default way: nulls compare less than all non-nulls, and all nulls are
    equal.

    @author James MacKay
*/
public abstract class AbstractStringComparator
    extends AbstractComparator
{
    // Constructors

    /**
        Constructs an AbstractStringComparator.
    */
    public AbstractStringComparator()
    {
        // empty
    }


    // Public methods

    /**
        Compares the specified strings (either or both of which may
        be null).
        <p>
        Note: this method exists as an optimization in the case
        where we know we're comparing Strings. It compares them
        in the same way that compareNonNull() does.

        @param str1 the first string
        @param str2 the second string
        @return a negative, zero or positive value if 'str1' is
        less than, equal to or greater than 'str2'
        @see #compareNonNull(Object, Object)
    */
    public int compare(String str1, String str2)
    {
        int result;

        if (str1 != null && str2 != null)
        {
            result = compareNonNullStrings(str1, str2);
        }
        else if (str1 == null && str2 == null)
        {
            result = 0;
        }
        else if (str1 == null)
        {
            result = -1;
        }
        else  // str2 == null
        {
            result = 1;
        }

        return result;
    }


    // Protected methods

    /**
        @exception ClassCastException thrown if either of the objects is not
        a String
        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        return ((String) obj1).compareTo((String) obj2);
    }


    // Abstract methods

    /**
        @param str1 a non-null String
        @param str2 another non-null String
        @return a negative, zero or positive value if 'str1' is less than,
        equal to or greater than 'str2'
    */
    protected abstract int compareNonNullStrings(String str1, String str2);
        // Assert.require(str1 != null);
        // Assert.require(str2 != null);
}
