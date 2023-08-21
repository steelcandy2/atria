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

import com.steelcandy.common.compare.DefaultStringComparator;

/**
    The class of Comparator that compares SymbolTableEntries
    lexicographically by their names, and if their names compare equal then
    compares them so that the one with the lower arity compares less than
    the one with the higher arity. And if both their names and arities
    compare equal then the names of the types of their respective arguments
    are compared pairwise lexicographically, in order from the entries'
    first arguments' types to their last arguments' types.
    <p>
    There isn't anything especially useful about our method of comparing
    entries' arguments' type names: it just provides a consistent ordering
    of entries that can have the same name and arity but different argument
    types.

    @author  James MacKay
    @version $Revision: 1.2 $
    @see SymbolTableEntry
*/
public class NameArityAndArgumentTypeNamesSymbolTableEntryComparator
    extends AbstractComparator
{
    // Constants

    /**
        The comparator that instances of this class use to compare
        SymbolTableEntries by their names then arities.
    */
    public static final NameAndAritySymbolTableEntryComparator
        NAME_AND_ARITY_COMPARATOR =
            NameAndAritySymbolTableEntryComparator.instance();

    /**
        The comparator that we use to compare the global names of entries'
        argument types.
    */
    public static final GlobalNameTypeComparator
        ARGUMENT_TYPE_COMPARATOR = GlobalNameTypeComparator.instance();

    /**
        The single instance of this class.
        <p>
        Since this class has no state the same instance can be shared across
        threads.
    */
    private static final NameArityAndArgumentTypeNamesSymbolTableEntryComparator
        _instance = new NameArityAndArgumentTypeNamesSymbolTableEntryComparator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static
        NameArityAndArgumentTypeNamesSymbolTableEntryComparator instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs a NameArityAndArgumentTypeNamesSymbolTableEntryComparator.
    */
    private NameArityAndArgumentTypeNamesSymbolTableEntryComparator()
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

        int result = NAME_AND_ARITY_COMPARATOR.compare(e1, e2);

        if (result == 0)
        {
            // Compare the global names (lexicographically) of
            // corresponding arguments, in order.
            GlobalNameTypeComparator cmp = ARGUMENT_TYPE_COMPARATOR;
            int arity = e1.arity();
            Assert.check(e2.arity() == arity);
            for (int i = 0; result == 0 && i < arity; i++)
            {
                result = cmp.compare(e1.argumentType(i),
                                     e2.argumentType(i));
            }
        }

        return result;
    }
}
