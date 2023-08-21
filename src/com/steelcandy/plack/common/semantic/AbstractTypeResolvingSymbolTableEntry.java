/*
 Copyright (C) 2005-2015 by James MacKay.

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

import com.steelcandy.common.UnsupportedMethodException;

/**
    An abstract base class for classes of symbol table entry that delegate to
    another symbol table entry, except that any and all types associated with
    the other entry are resolved using a type resolver.

    @author James MacKay
    @see TypeResolver
*/
public abstract class AbstractTypeResolvingSymbolTableEntry
    extends AbstractDelegatingSymbolTableEntry
{
    // Private fields

    /** The entry that we delegate to. */
    private SymbolTableEntry _entry;

    /**
        The resolver that we use to resolve any and all types associated with
        '_entry'.
    */
    private TypeResolver _resolver;


    // Constructors

    /**
        Constructs an AbstractDelegatingSymbolTableEntry.

        @param entry the original symbol table entry
        @param resolver the type resolver to use to resolve any and all types
        associated with 'entry'
    */
    public AbstractTypeResolvingSymbolTableEntry(SymbolTableEntry entry,
                                                 TypeResolver resolver)
    {
        Assert.require(entry != null);
        Assert.require(resolver != null);

        _entry = entry;
        _resolver = resolver;
    }


    // Public methods

    /**
        @see SymbolTableEntry#originatingType
    */
    public Type originatingType()
    {
        Assert.require(hasOriginatingType());

        Type result = resolve(super.originatingType());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#containsMatching(SymbolTable)
        @see AbstractSymbolTableEntry#containsMatching(SymbolTable)
    */
    public boolean containsMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        boolean result = (findMatching(table).isEmpty() == false);

        return result;
    }


    /**
        @see SymbolTableEntry#directlyShadows
    */
    public SymbolTableEntryList directlyShadows()
    {
        SymbolTableEntryList result =
            createResolvingEntries(super.directlyShadows());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntry(SymbolTableEntry)
    */
    public void addDirectlyShadowedEntry(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        throw new UnsupportedMethodException(getClass(),
                            "addDirectlyShadowedEntry(SymbolTableEntry)");
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntries(SymbolTableEntryList)
    */
    public void addDirectlyShadowedEntries(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        throw new UnsupportedMethodException(getClass(),
                        "addDirectlyShadowedEntries(SymbolTableEntryList)");
    }


    /**
        @see SymbolTableEntry#argumentType(int)
    */
    public Type argumentType(int index)
        throws MissingTypeException
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        Type result = resolve(super.argumentType(index));

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#argumentTypes
    */
    public TypeList argumentTypes()
        throws MissingTypeException
    {
        TypeList result = resolveAll(super.argumentTypes());

        Assert.ensure(result != null);
        Assert.ensure(result.size() == arity());
        return result;
    }

    /**
        @see SymbolTableEntry#resultType(int)
    */
    public Type resultType(int index)
        throws MissingTypeException
    {
        Assert.require(index >= 0);
        Assert.require(index < numberOfResults());

        Type result = resolve(super.resultType(index));

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#resultTypes
    */
    public TypeList resultTypes()
        throws MissingTypeException
    {
        TypeList result = resolveAll(super.resultTypes());

        Assert.ensure(result != null);
        Assert.ensure(result.size() == numberOfResults());
        return result;
    }

    /**
        @see SymbolTableEntry#firstResultType
    */
    public Type firstResultType()
        throws MissingTypeException
    {
        Assert.require(numberOfResults() > 0);

        Type result = resultType(0);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @return the entry that we delegate to, which may (or may not) have
        types associated with it that are not resolved
    */
    protected SymbolTableEntry unresolvedEntry()
    {
        Assert.ensure(_entry != null);
        return _entry;
    }

    /**
        @see AbstractDelegatingSymbolTableEntry#delegate
    */
    protected SymbolTableEntry delegate()
    {
        SymbolTableEntry result = unresolvedEntry();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param types a list of types
        @return a list of the result of resolving each type in 'types' using
        our type resolver
    */
    protected TypeList resolveAll(TypeList types)
    {
        Assert.require(types != null);

        TypeList result = typeResolver().resolveAll(types);

        Assert.ensure(result != null);
        Assert.ensure(result.size() == types.size());
        return result;
    }

    /**
        @param t a type
        @return the result of resolving 't' using our type resolver
    */
    protected Type resolve(Type t)
    {
        Assert.require(t != null);

        Type result = t.resolve(typeResolver());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return our type resolver
    */
    protected TypeResolver typeResolver()
    {
        TypeResolver result = _resolver;

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param entries a list of symbol table entries
        @return a list of symbol table entries whose i'th item is the same as
        the i'th item in 'entries' except that all of its associated types
        have been resolved using our type resolver
    */
    protected SymbolTableEntryList
        createResolvingEntries(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        SymbolTableEntryList result;

        if (entries.isEmpty())
        {
            result = entries;
        }
        else
        {
            result = SymbolTableEntryList.createArrayList(entries.size());
            SymbolTableEntryIterator iter = entries.iterator();
            while (iter.hasNext())
            {
                result.add(createResolvingEntry(iter.next()));
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        @param entry a symbol table entry
        @return a symbol table entry that is the same as 'entry' except that
        all of its associated types have been resolved using our type
        resolver
    */
    protected abstract SymbolTableEntry
        createResolvingEntry(SymbolTableEntry entry);
        // Assert.require(entry != null);
        // Assert.ensure(result != null);
}
