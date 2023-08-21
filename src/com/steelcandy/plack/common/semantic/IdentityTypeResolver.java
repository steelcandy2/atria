/*
 Copyright (C) 2015 by James MacKay.

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
    The singleton class of TypeResolver that doesn't resolve any types: that
    is, according to it every type resolves to itself.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class IdentityTypeResolver
    implements TypeResolver
{
    // Private constants

    /** The single instance of this class. */
    private static final IdentityTypeResolver
        _instance = new IdentityTypeResolver();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static final IdentityTypeResolver instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private IdentityTypeResolver()
    {
        // empty
    }


    // Public methods

    /**
        @see TypeResolver#resolve(String)
    */
    public Type resolve(String typeName)
    {
        Assert.require(typeName != null);

        // 'result' may be null
        return null;
    }

    /**
        @see TypeResolver#resolveAll(TypeList)
    */
    public TypeList resolveAll(TypeList types)
    {
        Assert.require(types != null);

        TypeList result = types;

        Assert.ensure(result != null);
        Assert.ensure(result.size() == types.size());
        return result;
    }
}
