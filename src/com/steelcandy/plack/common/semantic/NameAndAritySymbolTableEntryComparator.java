/*
 Copyright (C) 2009-2012 by James MacKay.

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
    The class of Comparator that compares SymbolTableEntries
    lexicographically by their names, and if their names compare equal then
    compares them so that the one with the lower arity compares less than
    the one with the higher arity. SymbolTableEntries with the same name
    and arity will compare as equal.

    @author  James MacKay
    @see SymbolTableEntry
*/
public class NameAndAritySymbolTableEntryComparator
    extends AbstractComparator
{
    // Constants

    /**
        The comparator that instances of this class use to compare the
        names of SymbolTableEntries.

        @see SymbolTableEntry#name
    */
    public static final DefaultStringComparator
        NAME_COMPARATOR = DefaultStringComparator.instance();

    /**
        The comparator that instances of this class use to compare the
        arities of SymbolTableEntries.

        @see SymbolTableEntry#arity
    */
    public static final DefaultIntegerComparator
        ARITY_COMPARATOR = DefaultIntegerComparator.instance();


    /**
        The single instance of this class.
        <p>
        Since this class has no state the same instance can be shared across
        threads.
    */
    private static final NameAndAritySymbolTableEntryComparator
        _instance = new NameAndAritySymbolTableEntryComparator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static NameAndAritySymbolTableEntryComparator instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs a NameAndAritySymbolTableEntryComparator.
    */
    private NameAndAritySymbolTableEntryComparator()
    {
        // empty
    }


    // Protected methods

    /**
        Note: all SymbolTableEntries compare greater than any
        non-SymbolTableEntry, and all non-SymbolTableEntries compare equal
        to each other.

        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        boolean isObj1Entry = (obj1 instanceof SymbolTableEntry);
        boolean isObj2Entry = (obj2 instanceof SymbolTableEntry);

        int result;

        if (isObj1Entry && isObj2Entry)
        {
            result = compareNonNullEntries((SymbolTableEntry) obj1,
                                           (SymbolTableEntry) obj2);
        }
        else
        {
            result = compareWhenNotBothRightTypes(isObj1Entry, isObj2Entry);
        }

        return result;
    }

    /**
        Compares the specified non-null SymbolTableEntries, returning -1,
        0 or 1 if the first is less than, equal to or greater than the
        second, respectively.

        @param e1 the first entry
        @param e2 the second entry
    */
    protected int compareNonNullEntries(SymbolTableEntry e1,
                                        SymbolTableEntry e2)
    {
        Assert.require(e1 != null);
        Assert.require(e2 != null);

        int result = NAME_COMPARATOR.compare(e1.name(), e2.name());

        if (result == 0)
        {
            result = ARITY_COMPARATOR.compare(e1.arity(), e2.arity());
        }

        return result;
    }
}
