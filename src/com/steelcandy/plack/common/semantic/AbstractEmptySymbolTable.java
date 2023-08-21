/*
 Copyright (C) 2005-2009 by James MacKay.

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

/**
    An abstract base class for immutable empty symbol tables.

    @author James MacKay
*/
public abstract class AbstractEmptySymbolTable
    implements SymbolTable
{
    // Public methods

    /**
        Note: this implementation always throws an
        ImmutableSymbolTableException.

        @see SymbolTable#add(SymbolTableEntry)
    */
    public void add(SymbolTableEntry entry)
        throws ImmutableSymbolTableException
    {
        Assert.require(entry != null);

        throw new ImmutableSymbolTableException();
    }

    /**
        @see SymbolTable#isEmpty
    */
    public boolean isEmpty()
    {
        return true;
    }

    /**
        @see SymbolTable#hasParent
    */
    public boolean hasParent()
    {
        return false;
    }

    /**
        @see SymbolTable#iterator
    */
    public SymbolTableEntryIterator iterator()
    {
        SymbolTableEntryIterator result =
            SymbolTableEntryList.createEmptyList().iterator();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        Assert.unreachable();
            // since isEmpty() is always true

        // Assert.ensure(result != null);
        return null;  // not reached
    }

    /**
        @see SymbolTable#find(String, int)
    */
    public SymbolTableEntryList find(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        // Assert.ensure(result != null);
        return SymbolTableEntryList.createEmptyList();
    }

    /**
        @see SymbolTable#findRecursively(String, int)
    */
    public SymbolTableEntryList findRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        // Assert.ensure(result != null);
        return SymbolTableEntryList.createEmptyList();
    }

    /**
        @see SymbolTable#parentFindRecursively(String, int)
    */
    public SymbolTableEntryList
        parentFindRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result = SymbolTableEntryList.createEmptyList();

        Assert.ensure(result != null);
        return result;
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

        // Assert.ensure(result != null);
        return SymbolTableEntryList.createEmptyList();
    }

    /**
        @see SymbolTable#findRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList
        findRecursively(String name, TypeList argumentTypes,
                        ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Assert.ensure(result != null);
        return SymbolTableEntryList.createEmptyList();
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

        SymbolTableEntryList result = SymbolTableEntryList.createEmptyList();

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

    /**
        @see SymbolTable#removeConstructs
    */
    public void removeConstructs()
    {
        // empty
    }
}
