/*
 Copyright (C) 2009 by James MacKay.

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
    An AbstractTypeResolvingSymbolTableEntry that caches the types that are
    the results of its type resolver resolving other types.

    @author James MacKay
    @version $Revision: 1.5 $
    @see TypeResolver
*/
public abstract class AbstractCachingTypeResolvingSymbolTableEntry
    extends AbstractTypeResolvingSymbolTableEntry
{
    // Private fields

    /**
        The types that our arguments' types resolved to, or null if they
        haven't been resolved yet.
    */
    private TypeList _argumentTypes;

    /**
        The types that our results' types resolved to, or null if they
        haven't been resolved yet.
    */
    private TypeList _resultTypes;

    /**
        Our originating type, or null if it hasn't been resolved yet.
    */
    private Type _originatingType;


    // Constructors

    /**
        Constructs an AbstractCachingTypeResolvingSymbolTableEntry.

        @param entry the original symbol table entry
        @param resolver the type resolver to use to resolve any and all types
        associated with 'entry'
    */
    public AbstractCachingTypeResolvingSymbolTableEntry(
                            SymbolTableEntry entry, TypeResolver resolver)
    {
        super(entry, resolver);
        Assert.require(entry != null);
        Assert.require(resolver != null);

        _argumentTypes = null;
        _resultTypes = null;
        _originatingType = null;
    }


    // Public methods

    /**
        @see SymbolTableEntry#originatingType
    */
    public Type originatingType()
    {
        Assert.require(hasOriginatingType());

        if (_originatingType == null)
        {
            _originatingType = super.originatingType();
            Assert.check(_originatingType != null);
        }

        Assert.ensure(_originatingType != null);
        return _originatingType;
    }

    /**
        @see SymbolTableEntry#argumentType(int)
    */
    public Type argumentType(int index)
        throws MissingTypeException
    {
        Assert.require(index >= 0);
        Assert.require(index < arity());

        Type result = argumentTypes().get(index);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#argumentTypes
    */
    public TypeList argumentTypes()
        throws MissingTypeException
    {
        if (_argumentTypes == null)
        {
            _argumentTypes = super.argumentTypes();
            Assert.check(_argumentTypes != null);
        }

        TypeList result = _argumentTypes;

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

        Type result = resultTypes().get(index);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTableEntry#resultTypes
    */
    public TypeList resultTypes()
        throws MissingTypeException
    {
        if (_resultTypes == null)
        {
            _resultTypes = super.resultTypes();
            Assert.check(_resultTypes != null);
        }

        TypeList result = _resultTypes;

        Assert.ensure(result != null);
        Assert.ensure(result.size() == numberOfResults());
        return result;
    }
}
