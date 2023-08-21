/*
 Copyright (C) 2005-2007 by James MacKay.

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

import com.steelcandy.common.NoSuchItemException;

/**
    An abstract base class for classes of immutable symbol table that
    delegate to another symbol table, except that any and all types
    associated with the other symbol table and its entries are resolved using
    a type resolver.

    @author James MacKay
    @version $Revision: 1.6 $
    @see TypeResolver
    @see MinimalAbstractSymbolTable#findMatches(SymbolTableEntryList, TypeList, ArgumentTypeMatcher)
*/
public abstract class AbstractTypeResolvingSymbolTable
    extends AbstractTransformingSymbolTable
{
    // Private fields

    /** The symbol table that we delegate to. */
    private SymbolTable _table;

    /**
        The resolver that we use to resolve any and all types associated with
        '_table'.
    */
    private TypeResolver _resolver;


    // Constructors

    /**
        Constructs an AbstractTypeResolvingSymbolTable.

        @param table the original symbol table
        @param resolver the type resolver to use to resolve any and all
        types associated with 'table'
    */
    public AbstractTypeResolvingSymbolTable(SymbolTable table,
                                            TypeResolver resolver)
    {
        Assert.require(table != null);
        Assert.require(resolver != null);

        _table = table;
        _resolver = resolver;
    }


    // Public methods

    /**
        @see SymbolTable#add(SymbolTableEntry)
    */
    public void add(SymbolTableEntry entry)
        throws ImmutableSymbolTableException,
            InvalidSymbolTableEntryException,
            DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        throw new ImmutableSymbolTableException();
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
        throws ImmutableSymbolTableException
    {
        Assert.require(isEmpty() == false);

        // Assert.ensure(result != null);
        throw new ImmutableSymbolTableException();
    }

    /**
        @see SymbolTable#remove(String, int)
    */
    public SymbolTableEntryList remove(String name, int arity)
        throws ImmutableSymbolTableException
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        // Assert.ensure(result != null);
        throw new ImmutableSymbolTableException();
    }

    /**
        @see SymbolTable#find(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList find(String name, TypeList argumentTypes,
                                     ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Note: we use findMatches() so that the matching is done against
        // transform()ed entries: find(String, int) does the
        // transform()ation.
        SymbolTableEntryList result =
            findMatches(find(name, argumentTypes.size()),
                        argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see #findRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList findRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Note: we use findMatches() so that the matching is done against
        // transform()ed entries: findRecursively(String, int) does the
        // transform()ation.
        SymbolTableEntryList result =
            findMatches(findRecursively(name, argumentTypes.size()),
                        argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#parentFindRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList parentFindRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Note: we use findMatches() so that the matching is done against
        // transform()ed entries: parentFindRecursively(String, int) does the
        // transform()ation.
        SymbolTableEntryList result =
            findMatches(parentFindRecursively(name, argumentTypes.size()),
                        argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList remove(String name, TypeList argumentTypes,
                                       ArgumentTypeMatcher matcher)
        throws ImmutableSymbolTableException
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Assert.ensure(result != null);
        throw new ImmutableSymbolTableException();
    }


    // Public methods

    /**
        @see SymbolTable#removeConstructs
    */
    public void removeConstructs()
    {
        // empty (since we're immutable, and we don't want to remove
        // constructs from the entries of '_table')
    }


    // Protected methods

    /**
        @see AbstractDelegatingSymbolTable#delegate
    */
    protected SymbolTable delegate()
    {
        SymbolTable result = _table;

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see AbstractTransformingSymbolTable#transform(SymbolTableEntry)
    */
    protected SymbolTableEntry transform(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        SymbolTableEntry result = createResolvingEntry(entry);

        Assert.ensure(result != null);
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


    // Abstract methods

    /**
        @param entry a symbol table entry from the table that we delegate to
        @return a symbol table entry that is the same as 'entry' except that
        all of its associated types have been resolved using our type
        resolver
    */
    protected abstract SymbolTableEntry
        createResolvingEntry(SymbolTableEntry entry);
        // Assert.require(entry != null);
        // Assert.ensure(result != null);
}
