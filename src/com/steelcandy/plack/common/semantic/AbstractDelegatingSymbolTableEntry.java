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

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructList;

/**
    An abstract base class for SymbolTableEntries that (at least by
    default) delegate all of their methods to another SymbolTableEntry.

    @author James MacKay
    @see SymbolTable
*/
public abstract class AbstractDelegatingSymbolTableEntry
    extends MinimalAbstractSymbolTableEntry
{
    // Public methods

    /**
        @see SymbolTableEntry#name
    */
    public String name()
    {
        // Assert.ensure(result != null);

        return delegate().name();
    }

    /**
        @see SymbolTableEntry#modifiers
    */
    public int modifiers()
    {
        return delegate().modifiers();
    }

    /**
        @see SymbolTableEntry#isDeclaration
    */
    public boolean isDeclaration()
    {
        return delegate().isDeclaration();
    }

    /**
        @see SymbolTableEntry#definition
    */
    public Construct definition()
        throws EntryConstructRemovedException
    {
        // Assert.ensure(result != null);
        return delegate().definition();
    }

    /**
        @see SymbolTableEntry#sourceCode
    */
    public SourceCode sourceCode()
    {
        // Assert.ensure(result != null);
        return delegate().sourceCode();
    }

    /**
        @see SymbolTableEntry#location
    */
    public SourceLocation location()
    {
        // Assert.ensure(result != null);
        return delegate().location();
    }

    /**
        @see SymbolTableEntry#hasOriginatingType
    */
    public boolean hasOriginatingType()
    {
        return delegate().hasOriginatingType();
    }

    /**
        @see SymbolTableEntry#originatingType
    */
    public Type originatingType()
    {
        Assert.require(hasOriginatingType());

        Type result = delegate().originatingType();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#kind
    */
    public int kind()
    {
        return delegate().kind();
    }

    /**
        @see SymbolTableEntry#kindDescription
    */
    public String kindDescription()
    {
        return delegate().kindDescription();
    }

    /**
        @see SymbolTableEntry#containsMatching(SymbolTable)
    */
    public boolean containsMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        return delegate().containsMatching(table);
    }

    /**
        @see SymbolTableEntry#findMatching(SymbolTable)
    */
    public SymbolTableEntryList findMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        // Assert.ensure(result != null);
        return delegate().findMatching(table);
    }

    /**
        @see SymbolTableEntry#removeMatching(SymbolTable)
    */
    public SymbolTableEntryList removeMatching(SymbolTable table)
        throws UnsupportedOperationException
    {
        Assert.require(table != null);

        // Assert.ensure(result != null);
        return delegate().removeMatching(table);
    }

    /**
        @see SymbolTableEntry#directlyShadows
    */
    public SymbolTableEntryList directlyShadows()
    {
        SymbolTableEntryList result = delegate().directlyShadows();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntry(SymbolTableEntry)
    */
    public void addDirectlyShadowedEntry(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        delegate().addDirectlyShadowedEntry(entry);
    }

    /**
        @see SymbolTableEntry#addDirectlyShadowedEntries(SymbolTableEntryList)
    */
    public void addDirectlyShadowedEntries(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        delegate().addDirectlyShadowedEntries(entries);
    }

    /**
        @see SymbolTableEntry#accessibility
    */
    public int accessibility()
    {
        return delegate().accessibility();
    }

    /**
        @see SymbolTableEntry#accessibilityDescription
    */
    public String accessibilityDescription()
    {
        // Assert.ensure(result != null);
        return delegate().accessibilityDescription();
    }

    /**
        @see SymbolTableEntry#isAtLeastAsAccessibleAs(SymbolTableEntry)
    */
    public boolean isAtLeastAsAccessibleAs(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        return delegate().isAtLeastAsAccessibleAs(entry);
    }

    /**
        @see SymbolTableEntry#arity
    */
    public int arity()
    {
        // Assert.ensure(result >= 0);
        return delegate().arity();
    }

    /**
        @see SymbolTableEntry#argumentConstruct(int)
    */
    public Construct argumentConstruct(int index)
        throws EntryConstructRemovedException
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        // Assert.ensure(result != null);
        return delegate().argumentConstruct(index);
    }

    /**
        @see SymbolTableEntry#argumentModifiers(int)
    */
    public int argumentModifiers(int index)
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        return delegate().argumentModifiers(index);
    }

    /**
        @see SymbolTableEntry#argumentType(int)
    */
    public Type argumentType(int index)
        throws MissingTypeException
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        // Assert.ensure(result != null);
        return delegate().argumentType(index);
    }

    /**
        @see SymbolTableEntry#argumentTypes
    */
    public TypeList argumentTypes()
        throws MissingTypeException
    {
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == arity());
        return delegate().argumentTypes();
    }

    /**
        @see SymbolTableEntry#numberOfResults
    */
    public int numberOfResults()
    {
        // Assert.ensure(result >= 0);
        return delegate().numberOfResults();
    }

    /**
        @see SymbolTableEntry#resultConstruct(int)
    */
    public Construct resultConstruct(int index)
        throws EntryConstructRemovedException
    {
        Assert.require(index >= 0);
        Assert.require(index < numberOfResults());

        // Assert.ensure(result != null);
        return delegate().resultConstruct(index);
    }

    /**
        @see SymbolTableEntry#resultModifiers(int)
    */
    public int resultModifiers(int index)
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        return delegate().resultModifiers(index);
    }

    /**
        @see SymbolTableEntry#resultType(int)
    */
    public Type resultType(int index)
        throws MissingTypeException
    {
        Assert.require(index >= 0);
        Assert.require(index < numberOfResults());

        // Assert.ensure(result != null);
        return delegate().resultType(index);
    }

    /**
        @see SymbolTableEntry#resultTypes
    */
    public TypeList resultTypes()
        throws MissingTypeException
    {
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == numberOfResults());
        return delegate().resultTypes();
    }

    /**
        @see SymbolTableEntry#firstResultType
    */
    public Type firstResultType()
        throws MissingTypeException
    {
        // Assert.ensure(result != null);
        return delegate().firstResultType();
    }


    /**
        @see SymbolTableEntry#displaySignature
    */
    public String displaySignature()
    {
        // Assert.ensure(result != null);
        return delegate().displaySignature();
    }

    /**
        @see SymbolTableEntry#displayFull
    */
    public String displayFull()
    {
        // Assert.ensure(result != null);
        return delegate().displayFull();
    }

    /**
        @see SymbolTableEntry#findGeneralization
    */
    public SymbolTableEntry findGeneralization()
    {
        // 'result' may be null
        return delegate().findGeneralization();
    }


    // Abstract methods

    /**
        @return the SymbolTableEntry that this entry delegates to
    */
    protected abstract SymbolTableEntry delegate();
        // Assert.ensure(result != null);
}
