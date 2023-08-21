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
    An abstract base class for TypeResolvers.

    @author  James MacKay
*/
public abstract class AbstractTypeResolver
    implements TypeResolver
{
    // Public methods

    /**
        @see TypeResolver#resolveAll(TypeList)
    */
    public TypeList resolveAll(TypeList types)
    {
        Assert.require(types != null);

        TypeList result;

        int sz = types.size();
        if (sz > 1)
        {
            result = TypeList.createArrayList(types.size());

            TypeIterator iter = types.iterator();
            while (iter.hasNext())
            {
                result.add(iter.next().resolve(this));
            }
        }
        else if (sz == 1)
        {
            result = TypeList.
                createSingleItemList(types.get(0).resolve(this));
        }
        else
        {
            Assert.check(sz == 0);
            result = TypeList.createEmptyList();
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == types.size());
        return result;
    }
}
