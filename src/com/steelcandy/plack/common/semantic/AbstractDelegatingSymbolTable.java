/*
 Copyright (C) 2004-2005 by James MacKay.

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
    An abstract base class for symbol tables that delegate some or all of
    their implementation to another symbol table instance.
    <p>
    Subclasses just have to implement delegate(), though they may want to
    override other methods if they don't want them handled by the delegate.

    @author James MacKay
*/
public abstract class AbstractDelegatingSymbolTable
    extends MinimalAbstractSymbolTable
    implements SymbolTable
{
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

        delegate().add(entry);
    }

    /**
        @see SymbolTable#isEmpty
    */
    public boolean isEmpty()
    {
        return delegate().isEmpty();
    }

    /**
        @see SymbolTable#iterator
    */
    public SymbolTableEntryIterator iterator()
    {
        SymbolTableEntryIterator result = delegate().iterator();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        SymbolTableEntry result = delegate().remove();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#find(String, int)
    */
    public SymbolTableEntryList find(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result = delegate().find(name, arity);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#findRecursively(String, int)
    */
    public SymbolTableEntryList findRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result =
            delegate().findRecursively(name, arity);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#parentFindRecursively(String, int)
    */
    public SymbolTableEntryList
        parentFindRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result =
            delegate().parentFindRecursively(name, arity);

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

        SymbolTableEntryList result =
            delegate().remove(name, arity);

        Assert.ensure(result != null);
        return result;
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

        SymbolTableEntryList result =
            delegate().find(name, argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#findRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList findRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        SymbolTableEntryList result =
            delegate().findRecursively(name, argumentTypes, matcher);

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

        SymbolTableEntryList result =
            delegate().parentFindRecursively(name, argumentTypes, matcher);

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

        SymbolTableEntryList result =
            delegate().remove(name, argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        String result = delegate().toString();

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @see MinimalAbstractSymbolTable#isValidEntryKind(int)
    */
    protected boolean isValidEntryKind(int kind)
    {
        return true;
            // since we don't care: our delegate will/should check for itself
    }

    /**
        @see MinimalAbstractSymbolTable#checkForDuplicate(SymbolTableEntry)
    */
    protected void checkForDuplicate(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        // empty - by default we leave this to our delegate
    }


    // Abstract methods

    /**
        @return the SymbolTable that this compilable is to delegate its
        implementation to
    */
    protected abstract SymbolTable delegate();
        // Assert.ensure(result != null);
}
