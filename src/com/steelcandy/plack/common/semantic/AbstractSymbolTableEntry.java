/*
 Copyright (C) 2003-2015 by James MacKay.

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

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructList;

import com.steelcandy.common.UnsupportedMethodException;
import com.steelcandy.common.compare.*;

import java.util.*;

/**
    An abstract base class for SymbolTableEntry classes.

    @author James MacKay
    @version $Revision: 1.19 $
*/
public abstract class AbstractSymbolTableEntry
    extends MinimalAbstractSymbolTableEntry
{
    // Private fields

    /** The entries that we directly shadow/override. */
    private SymbolTableEntryList _shadowedEntries;


    // Constructors

    /**
        Constructs an AbstractSymbolTableEntry.
    */
    public AbstractSymbolTableEntry()
    {
        _shadowedEntries = SymbolTableEntryList.createEmptyList();
    }


    // Public static methods

    /**
        @param m an argument type matcher
        @return a Comparator that compares SymbolTableEntries and considers
        them to be equal iff they have the same names and number of
        arguments, and if each of their corresponding arguments match
        according to 'm'
    */
    public static Comparator createComparator(ArgumentTypeMatcher m)
    {
        Assert.require(m != null);

        //Assert.ensure(result != null);
        return new EntryComparator(m);
    }


    // Public methods

    /**
        @see SymbolTableEntry#containsMatching(SymbolTable)
    */
    public boolean containsMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        return (findMatching(table).isEmpty() == false);
    }

    /**
        This implementation throws an UnsupportedOperationException.

        @see SymbolTableEntry#findMatching(SymbolTable)
    */
    public SymbolTableEntryList findMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        // Assert.ensure(result != null);
        throw new UnsupportedMethodException(getClass(),
                                    "findMatching(SymbolTable)");
    }

    /**
        This implementation throws an UnsupportedOperationException.

        @see SymbolTableEntry#removeMatching(SymbolTable)
    */
    public SymbolTableEntryList removeMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        // Assert.ensure(result != null);
        throw new UnsupportedMethodException(getClass(),
                                    "removeMatching(SymbolTable)");
    }


    /**
        @see SymbolTableEntry#argumentTypes
    */
    public TypeList argumentTypes()
    {
        TypeList result;

        int arity = arity();
        if (arity > 1)
        {
            result = TypeList.createArrayList(arity);
            for (int i = 0; i < arity; i++)
            {
                result.add(argumentType(i));
            }
        }
        else if (arity == 1)
        {
            result = TypeList.createSingleItemList(argumentType(0));
        }
        else
        {
            Assert.check(arity == 0);
            result = TypeList.createEmptyList();
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == arity());
        return result;
    }

    /**
        @see SymbolTableEntry#resultTypes
    */
    public TypeList resultTypes()
    {
        TypeList result;

        int numResults = numberOfResults();
        if (numResults > 1)
        {
            result = TypeList.createArrayList(numResults);
            for (int i = 0; i < numResults; i++)
            {
                result.add(resultType(i));
            }
        }
        else if (numResults == 1)
        {
            result = TypeList.createSingleItemList(resultType(0));
        }
        else
        {
            Assert.check(numResults == 0);
            result = TypeList.createEmptyList();
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == numberOfResults());
        return result;
    }


    /**
        @see SymbolTableEntry#directlyShadows
    */
    public SymbolTableEntryList directlyShadows()
    {
        Assert.ensure(_shadowedEntries != null);
        return _shadowedEntries;
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntry(SymbolTableEntry)
    */
    public void addDirectlyShadowedEntry(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        if (_shadowedEntries.isEmpty())
        {
            // The empty list that _shadowedEntries is initialized to is
            // probably unmodifiable.
            _shadowedEntries = SymbolTableEntryList.createArrayList();
        }

        _shadowedEntries.add(entry);
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntries(SymbolTableEntryList)
    */
    public void addDirectlyShadowedEntries(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        defaultAddDirectlyShadowedEntries(entries);
    }

    /**
        @see SymbolTableEntry#findGeneralization
    */
    public SymbolTableEntry findGeneralization()
    {
        // 'result' may be null
        return null;
    }
}


// Private classes

/**
    @see AbstractSymbolTableEntry#createComparator(ArgumentTypeMatcher)
*/
class EntryComparator
    extends AbstractComparator
{
    // Private fields

    /**
        The matcher we use to match entries' argument types.
    */
    private ArgumentTypeMatcher _matcher;


    // Constructors

    /**
        Constructs a new EntryComparator.

        @param m the matcher that the Comparator we construct is to use to
        compare entry argument types
    */
    public EntryComparator(ArgumentTypeMatcher m)
    {
        Assert.require(m != null);

        _matcher = m;
    }


    // Protected methods

    /**
        @see AbstractComparator#compareNonNull(Object, Object)
    */
    protected int compareNonNull(Object obj1, Object obj2)
    {
        Assert.require(obj1 != null);
        Assert.require(obj2 != null);

        int result = 0;

        if (obj1 instanceof SymbolTableEntry &&
            obj2 instanceof SymbolTableEntry)
        {
            SymbolTableEntry entry1 = (SymbolTableEntry) obj1;
            SymbolTableEntry entry2 = (SymbolTableEntry) obj2;

            result = compareStrings(entry1.name(), entry2.name());
            if (result == 0)
            {
                int numArgs = entry1.arity();
                result = compareInts(numArgs, entry2.arity());
                if (result == 0)
                {
                    int i;
                    Type at1 = null;
                    Type at2 = null;
                    for (i = 0; i < numArgs; i++)
                    {
                        at1 = entry1.argumentType(i);
                        at2 = entry2.argumentType(i);
                        if (_matcher.isMatch(at1, at2) == false)
                        {
                            break;  // for
                        }
                    }

                    if (i < numArgs)
                    {
                        // Determine which is less than the other in a
                        // consistent - yet fairly arbitrary - way.
                        result = compareStrings(at1.globalName(),
                                                at2.globalName());
                    }
                }
            }
        }
        else
        {
            // One or both of our arguments isn't a SymbolTableEntry.
            throw new ClassCastException();
        }

        return result;
    }
}
