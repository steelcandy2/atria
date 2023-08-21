/*
 Copyright (C) 2011 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.compare.AbstractComparator;

import com.steelcandy.common.compare.DefaultIntegerComparator;
import com.steelcandy.common.compare.DefaultStringComparator;

/**
    The class of Comparator that compares Types lexicographically by their
    global names. Types with the name global name will compare as equal.

    @author  James MacKay
    @see Type
*/
public class GlobalNameTypeComparator
    extends AbstractComparator
{
    // Constants

    /**
        The comparator that instances of this class use to compare the
        global names of Types.

        @see Type#globalName
    */
    public static final DefaultStringComparator
        NAME_COMPARATOR = DefaultStringComparator.instance();

    /**
        The single instance of this class.
        <p>
        Since this class has no state the same instance can be shared across
        threads.
    */
    private static final GlobalNameTypeComparator
        _instance = new GlobalNameTypeComparator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static GlobalNameTypeComparator instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs a GlobalNameTypeComparator.
    */
    private GlobalNameTypeComparator()
    {
        // empty
    }


    // Protected methods

    /**
        Note: all Types compare greater than any non-Type, and all non-Types
        compare equal to each other.

        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        boolean isObj1Type = (obj1 instanceof Type);
        boolean isObj2Type = (obj2 instanceof Type);

        int result;

        if (isObj1Type && isObj2Type)
        {
            String name1 = ((Type) obj1).globalName();
            String name2 = ((Type) obj2).globalName();
            result = NAME_COMPARATOR.compare(name1, name2);
        }
        else
        {
            result = compareWhenNotBothRightTypes(isObj1Type, isObj2Type);
        }

        return result;
    }
}
