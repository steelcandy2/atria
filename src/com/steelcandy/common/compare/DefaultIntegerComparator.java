/*
 Copyright (C) 2009 by James MacKay.

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
    The singleton comparator class that compares ints or Integers in the
    default way: nulls compare less than all non-nulls, and two non-null
    Integers or ints are compared such that larger values come after smaller
    values.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class DefaultIntegerComparator
    extends AbstractComparator
{
    // Constants

    /** The single instance of this class. */
    private static final DefaultIntegerComparator
        _instance = new DefaultIntegerComparator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static DefaultIntegerComparator instance()
    {
        return _instance;
    }

    /**
        Constructs a DefaultIntegerComparator.
    */
    private DefaultIntegerComparator()
    {
        // empty
    }


    // Public methods

    /**
        Compares the specified ints.

        @param i1 the first int
        @param i2 the second int
        @return a negative, zero or positive value if 'i1' is less than,
        equal to or greater than 'i2'
        @see #compare(Integer, Integer)
    */
    public int compare(int i1, int i2)
    {
        return (i1 - i2);
    }

    /**
        Compares the specified integers (either or both of which may
        be null).
        <p>
        Note: this method exists as an optimization in the case
        where we know we're comparing Integers. It compares them
        in the same way that compareNonNull() does.

        @param i1 the first integer
        @param i2 the second integer
        @return a negative, zero or positive value if 'i1' is less than,
        equal to or greater than 'i2'
        @see #compare(int, int)
        @see #compareNonNull(Object, Object)
    */
    public int compare(Integer i1, Integer i2)
    {
        int result;

        if (i1 != null && i2 != null)
        {
            result = compare(i1.intValue(), i2.intValue());
        }
        else if (i1 == null && i2 == null)
        {
            result = 0;
        }
        else if (i1 == null)
        {
            result = -1;
        }
        else  // i2 == null
        {
            result = 1;
        }

        return result;
    }


    // Protected methods

    /**
        @exception ClassCastException thrown if either of the objects is not
        an Integer.
        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        return compare(((Integer) obj1).intValue(),
                       ((Integer) obj2).intValue());
    }
}
