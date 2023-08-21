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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;

/**
    A minimal abstract base class for SymbolTableEntry classes.
    <p>
    By default entries don't have an originating type.

    @author James MacKay
    @version $Revision: 1.20 $
*/
public abstract class MinimalAbstractSymbolTableEntry
    implements SymbolTableEntry
{
    // Constructors

    /**
        Constructs a MinimalAbstractSymbolTableEntry.
    */
    public MinimalAbstractSymbolTableEntry()
    {
        // empty
    }


    // Public static methods

    /**
        @param entry1 a symbol table entry
        @param entry2 another symbol table entry
        @param m an argument type matcher
        @return true iff 'entry1' and 'entry2' have the same arity and each
        of the corresponding arguments of 'entry2' match those of 'entry1',
        according to 'm'
    */
    public static boolean doAllArgumentTypesMatch(SymbolTableEntry entry1,
                            SymbolTableEntry entry2, ArgumentTypeMatcher m)
    {
        Assert.require(entry1 != null);
        Assert.require(entry1 != null);
        Assert.require(m != null);

        boolean result = false;

        int arity = entry1.arity();
        if (arity == entry2.arity())
        {
            for (int i = 0; i < arity; i++)
            {
                Type t1 = entry1.argumentType(i);
                Type t2 = entry2.argumentType(i);
                if (m.isMatch(t1, t2) == false)
                {
                    result = false;
                    break;  // for
                }
            }
        }

        return result;
    }

    /**
        @param c a list of symbol table entries
        @return a displayable representation of of all of the entries in 'c',
        including the arguments and results of each, separated by 'sep'
        @see SymbolTableEntry#displayFull
    */
    public static String displayAllFull(SymbolTableEntryList c, String sep)
    {
        Assert.require(c != null);
        Assert.require(sep != null);

        String result;

        if (c.isEmpty())
        {
            result = "";
        }
        else
        {
            StringBuffer res = new StringBuffer();
            SymbolTableEntryIterator iter = c.iterator();
            while (iter.hasNext())
            {
                res.append(iter.next().displayFull());
                if (iter.hasNext())
                {
                    res.append(sep);
                }
            }
            result = res.toString();
        }

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        @see SymbolTableEntry#hasOriginatingType
    */
    public boolean hasOriginatingType()
    {
        return false;
    }

    /**
        @see SymbolTableEntry#originatingType
    */
    public Type originatingType()
    {
        Assert.require(hasOriginatingType());

        Assert.unreachable();
            // since hasOriginatingType() always returns false

        return null;  // not reached
    }


    /**
        @see SymbolTableEntry#canSetFirstResultType
    */
    public boolean canSetFirstResultType()
    {
        return false;
    }

    /**
        @see SymbolTableEntry#setFirstResultType(Type)
    */
    public void setFirstResultType(Type t)
    {
        Assert.require(canSetFirstResultType());
        Assert.require(t != null);

        Assert.unreachable();
            // since our canSetFirstResultType() method always returns false
    }


    /**
        Note: this representation is intended for debugging purposes, not
        display purposes.

        @see Object#toString
        @see SymbolTableEntry#displaySignature
        @see SymbolTableEntry#displayFull
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer(name());

        buf.append("(");
        int arity = arity();
        for (int i = 0; i < arity; i++)
        {
            if (i > 0)
            {
                buf.append(", ");
            }
            buf.append(argumentType(i).display());
        }
        buf.append(")->(");

        int numResults = numberOfResults();
        for (int i = 0; i < numResults; i++)
        {
            if (i > 0)
            {
                buf.append(", ");
            }
            buf.append(resultType(i).display());
        }
        buf.append(") [");

        buf.append(accessibilityDescription()).append(" ").
            append(kindDescription());
        if (isDeclaration())
        {
            buf.append(" declaration");
        }
        if (hasOriginatingType())
        {
            buf.append(" defined in type ").
                append(originatingType().globalName());
        }
        buf.append("]");

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }


    /**
        Note: subclasses for entries for symbols in languages in which each
        symbol always has the same argument and result types may want to
        override this implementation to just check whether the entries have
        the same names and orginating types.

        @see SymbolTableEntry#equals(SymbolTableEntry)
    */
    public boolean equals(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        return name().equals(entry.name()) &&
                areOriginatingTypesEqual(entry) &&
                areAllArgumentAndResultTypesEqual(this, entry);
    }

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof SymbolTableEntry)
        {
            result = equals((SymbolTableEntry) obj);
        }

        return result;
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        return name().hashCode() + arity();
    }


    // Protected methods

    /**
        @param entry another symbol table entry
        @return true iff this entry and 'entry' are considered to have
        equal originating types
    */
    protected boolean areOriginatingTypesEqual(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        // By default if one entry is missing its orginating type and the
        // other isn't then their originating types are NOT considered to be
        // equal.
        boolean hasOriginType = hasOriginatingType();

        boolean result = (hasOriginType == entry.hasOriginatingType());

        if (result && hasOriginType)
        {
            result = originatingType().equals(entry.originatingType());
        }

        return result;
    }

    /**
        Note: this method is intended to be used in implementing
        definition().

        @param def the construct that defined our symbol, iff it's not null
        @return 'def', if it's not null
        @exception EntryConstructRemovedException thrown iff 'def' is null
        (which we assume means that the defining construct has been removed)

        @see SymbolTableEntry#definition
        @see SymbolTableEntry#removeConstructs
    */
    protected Construct definition(Construct def)
        throws EntryConstructRemovedException
    {
        Construct result = def;

        if (result == null)
        {
            handleRemovedConstruct();
            Assert.unreachable();
        }

        // Assert.ensure(result != null);
        return result;
    }

    /**
        Handles the situation where a construct that is to be returned by
        one of an instance's methods has been removed.

        Note: methods overriding this one are required to throw an
        EntryConstructRemovedException: client code depends on this.

        @exception EntryConstructRemovedException is always thrown as part
        of handling the removed construct
        @see SymbolTableEntry#removeConstructs
    */
    protected void handleRemovedConstruct()
    {
        throw new EntryConstructRemovedException();
    }


    /**
        @param entry1 a symbol table entry
        @param entry2 another symbol table entry
        @return true iff 'entry1' and 'entry2' have the same arity and number
        of results, and each of their argument and result types are pairwise
        equal
    */
    protected boolean
        areAllArgumentAndResultTypesEqual(SymbolTableEntry entry1,
                                          SymbolTableEntry entry2)
    {
        Assert.require(entry1 != null);
        Assert.require(entry2 != null);

        return areAllArgumentTypesEqual(entry1, entry2) &&
                areAllResultTypesEqual(entry1, entry2);
    }

    /**
        @param entry1 a symbol table entry
        @param entry2 another symbol table entry
        @return true iff 'entry1' and 'entry2' have the same arity and each
        of their corresponding arguments' types are equal
    */
    protected boolean areAllArgumentTypesEqual(SymbolTableEntry entry1,
                                               SymbolTableEntry entry2)
    {
        Assert.require(entry1 != null);
        Assert.require(entry2 != null);

        boolean result = (entry1.arity() == entry2.arity());
//if (result == false) { System.err.println("-- different arities (" + entry1.arity() + ", " + entry2.arity() + ")"); }
        if (result)
        {
            result = MinimalAbstractType.
                areSameSizeAllEqual(entry1.argumentTypes(),
                                    entry2.argumentTypes());
//if (result == false) { System.err.println("-- argument types not all equal"); }
        }

        return result;
    }

    /**
        @param entry1 a symbol table entry
        @param entry2 another symbol table entry
        @return true iff 'entry1' and 'entry2' have the same number of
        results and each of their corresponding results' types are equal
    */
    protected boolean areAllResultTypesEqual(SymbolTableEntry entry1,
                                             SymbolTableEntry entry2)
    {
        Assert.require(entry1 != null);
        Assert.require(entry2 != null);

        boolean result =
            (entry1.numberOfResults() == entry2.numberOfResults());
//if (result == false) { System.err.println("-- different # results (" + entry1.numberOfResults() + ", " + entry2.numberOfResults() + ")"); }

        if (result)
        {
            result = MinimalAbstractType.
                areSameSizeAllEqual(entry1.resultTypes(),
                                    entry2.resultTypes());
//if (result == false) { System.err.println("-- result types not all equal"); }
        }

        return result;
    }


    /**
        @param entry a symbol table entry
        @return true iff all of 'entry''s argument and result types are
        fully resolved
    */
    protected boolean isEverythingFullyResolved(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

// TODO: do we require that entry.originatingType() be fully resolved too ???!!!???
        boolean argsResolved = MinimalAbstractType.
            areAllFullyResolved(entry.argumentTypes());
        boolean resultsResolved = MinimalAbstractType.
            areAllFullyResolved(entry.resultTypes());

        return argsResolved && resultsResolved;
    }

    /**
        The default implementation of addDirectlyShadowedEntries(), which
        just calls addDirectlyShadowedEntry() for each entry in the list.

        @see SymbolTableEntry#addDirectlyShadowedEntries(SymbolTableEntryList)
    */
    protected void
        defaultAddDirectlyShadowedEntries(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        SymbolTableEntryIterator iter = entries.iterator();
        while (iter.hasNext())
        {
            addDirectlyShadowedEntry(iter.next());
        }
    }


    // Protected static methods

    /**
        Adds the specified entry to the end of the list that is the value
        of the specified attribute, creating and setting a new list as the
        attribute's value iff it doesn't already have one.

        @param attr the attribute to whose value 'entry' is to be added
        @param entry the entry to add to the end of the value of 'attr'
    */
    protected static void addEntry(SymbolTableEntryListAttribute attr,
                                   SymbolTableEntry entry)
    {
        Assert.require(attr != null);
        // attr.isSet() may or may not be true
        Assert.require(entry != null);

        SymbolTableEntryList entryList;
        if (attr.isSet())
        {
            entryList = attr.value();
        }
        else
        {
            entryList = SymbolTableEntryList.createArrayList();
            attr.setValue(entryList);
        }

        entryList.add(entry);
    }
}
