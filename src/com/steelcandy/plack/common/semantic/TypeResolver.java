/*
 Copyright (C) 2005 by James MacKay.

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
    The interface implemented by classes that resolve types into other
    types.
    <p>
    Note: non-Type classes should rarely call instances' resolve() method
    directly: instead they should just pass the instance as an argument to
    a Type object's resolve() method.

    @author  James MacKay
    @version $Revision: 1.2 $
    @see Type#resolve(TypeResolver)
*/
public interface TypeResolver
{
    // Public methods

    /**
        @param typeName the name of the type to resolve
        @return the type that 'typeName' resolves to, or null if this
        resolver doesn't resolve it to anything
        @see Type#resolve(TypeResolver)
    */
    public Type resolve(String typeName);
        // Assert.require(typeName != null);
        // 'result' may be null

    /**
        @param types a list of types
        @return a list whose i'th item is the result of resolving the i'th
        item in 'types' using this resolver
        @see Type#resolve(TypeResolver)
    */
    public TypeList resolveAll(TypeList types);
        // Assert.require(types != null);
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == types.size());
}
