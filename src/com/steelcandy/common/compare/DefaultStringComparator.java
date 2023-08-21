/*
 Copyright (C) 2004-2015 by James MacKay.

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
    The singleton comparator class that compares Strings in the default
    way: nulls compare less than all non-nulls, and two non-null Strings
    are compared lexicographically.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class DefaultStringComparator
    extends AbstractStringComparator
{
    // Constants

    /** The single instance of this class. */
    private static final DefaultStringComparator
        _instance = new DefaultStringComparator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static DefaultStringComparator instance()
    {
        return _instance;
    }

    /**
        Constructs a DefaultStringComparator.
    */
    private DefaultStringComparator()
    {
        // empty
    }


    // Protected methods

    /**
        @see AbstractStringComparator#compareNonNullStrings(String, String)
    */
    protected int compareNonNullStrings(String str1, String str2)
    {
        Assert.require(str1 != null);
        Assert.require(str2 != null);

        return str1.compareTo(str2);
    }
}
