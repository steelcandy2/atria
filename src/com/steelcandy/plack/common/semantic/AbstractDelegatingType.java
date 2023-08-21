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
    An abstract base class for Types that delegate most or all of their
    methods to another Type instance.

    @author James MacKay
*/
public abstract class AbstractDelegatingType
    implements Type
{
    // Public methods

    /**
        @see Type#conformsTo(Type)
    */
    public boolean conformsTo(Type t)
    {
        Assert.require(t != null);

        return delegatee().conformsTo(t);
    }

    /**
        @see Type#isAlwaysConformedTo
    */
    public boolean isAlwaysConformedTo()
    {
        boolean result = delegatee().isAlwaysConformedTo();

        // Assert.ensure(isNeverConformedTo() == false || result == false);
        // "isNeverConformedTo() implies result == false"
        return result;
    }

    /**
        @see Type#isNeverConformedTo
    */
    public boolean isNeverConformedTo()
    {
        boolean result = delegatee().isNeverConformedTo();

        // Assert.ensure(isAlwaysConformedTo() == false || result == false);
        // "isAlwaysConformedTo() implies result == false"
        return result;
    }


    /**
        @see Type#equals(Type)
    */
    public boolean equals(Type t)
    {
        Assert.require(t != null);

        return delegatee().equals(t);
    }

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        return delegatee().equals(obj);
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        return delegatee().hashCode();
    }

    /**
        @see Type#isAlwaysEqualTo
    */
    public boolean isAlwaysEqualTo()
    {
        boolean result = delegatee().isAlwaysEqualTo();

        // Assert.ensure(isNeverEqualTo() == false || result == false);
        // "isNeverEqualTo() implies result == false"
        return result;
    }

    /**
        @see Type#isNeverEqualTo
    */
    public boolean isNeverEqualTo()
    {
        boolean result = delegatee().isNeverEqualTo();

        // Assert.ensure(isAlwaysEqualTo() == false || result == false);
        // "isAlwaysEqualTo() implies result == false"
        return result;
    }


    /**
        @see Type#allSymbols
    */
    public SymbolTable allSymbols()
    {
        SymbolTable result = delegatee().allSymbols();

        // Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#accessibleSymbols(Type)
    */
    public SymbolTable accessibleSymbols(Type contextType)
    {
        Assert.require(contextType != null);

        SymbolTable result = delegatee().accessibleSymbols(contextType);

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see Type#isFullyResolved
    */
    public boolean isFullyResolved()
    {
        return delegatee().isFullyResolved();
    }

    /**
        @see Type#resolve(TypeResolver)
    */
    public Type resolve(TypeResolver resolver)
    {
        Assert.require(resolver != null);

        Type result = delegatee().resolve(resolver);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#display
    */
    public String display()
    {
        String result = delegatee().display();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#globalName
    */
    public String globalName()
    {
        String result = delegatee().globalName();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#name
    */
    public String name()
    {
        String result = delegatee().name();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#unqualifiedName
    */
    public String unqualifiedName()
    {
        String result = delegatee().unqualifiedName();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#typeParameters
    */
    public TypeList typeParameters()
    {
        TypeList result = delegatee().typeParameters();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#isInvalidType
    */
    public boolean isInvalidType()
    {
        return delegatee().isInvalidType();
    }


    // Abstract methods

    /**
        @return the Type instance to which this instance delegates its
        methods
    */
    protected abstract Type delegatee();
        // Assert.ensure(result != null);
}
