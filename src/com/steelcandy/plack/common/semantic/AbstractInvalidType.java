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
    An abstract base class for Type classes that represent invalid or missing
    types: by default they are equal to and conform to all other types, and
    all other types are equal to and conform to them.

    @author  James MacKay
*/
public abstract class AbstractInvalidType
    extends AbstractFlexibleType
{
    // Public methods

    /**
        This implementation returns the same thing as name() since invalid
        types' global names aren't usually of the same form as valid type
        names.

        @see Type#unqualifiedName
    */
    public String unqualifiedName()
    {
        String result = name();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Type#isInvalidType
    */
    public boolean isInvalidType()
    {
        return true;
    }

    /**
        At least by default invalid types are fully resolved.

        @see Type#isFullyResolved
    */
    public boolean isFullyResolved()
    {
        return true;
    }

    /**
        @see Type#resolve(TypeResolver)
    */
    public Type resolve(TypeResolver resolver)
    {
        Assert.require(resolver != null);

        // Assert.ensure(result != null);
        return this;
    }

    /**
        @see Type#accessibleSymbols(Type)
    */
    public SymbolTable accessibleSymbols(Type contextType)
    {
        Assert.require(contextType != null);

        SymbolTable result = allSymbols();

        Assert.ensure(result != null);
        return result;
    }
}
