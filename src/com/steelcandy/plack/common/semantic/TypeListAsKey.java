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
    Represents a TypeList that can be used as a key in a Map or other
    container (since it implements equals() and hashCode() appropriately).
    <p>
    Note: some/most Type classes do not support the hashCode() operation
    since some types - most notably ones that represent invalid types - can
    be equal to all other types. This class assumes that Types whose
    globalName()s are the same are equal.

    @author  James MacKay
*/
public class TypeListAsKey
{
    // Constants

    /**
        The maximum number of types in a TypeList to use in calculating an
        instance's TypeList's hash code.
    */
    private static final int MAXIMUM_ITEMS_USED_IN_HASH_CODE = 3;


    // Private fields

    /** The TypeList. */
    private TypeList _types;


    // Constructors

    /**
        Constructs a TypeListAsKey
    */
    public TypeListAsKey(TypeList t)
    {
        Assert.require(t != null);

        _types = t;
    }


    // Public methods

    /**
        @return our TypeList
    */
    public TypeList types()
    {
        TypeList result = _types;

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof TypeListAsKey)
        {
            TypeList otherTypes = ((TypeListAsKey) obj).types();
            int num = _types.size();
            if (otherTypes.size() == num)
            {
                result = true;
                for (int i = 0; i < num; i++)
                {
                    Type t1 = _types.get(i);
                    Type t2 = otherTypes.get(i);
                    if (areEqual(t1, t2) == false)
                    {
                        result = false;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
        Note: if equals(Object) considers its argument to be equal to us then
        we must have the same hash code as that object.

        @see Object#hashCode
    */
    public int hashCode()
    {
        int num = _types.size();
        int maxNum = Math.min(num, MAXIMUM_ITEMS_USED_IN_HASH_CODE);

        int result = 0;

        for (int i = 0; i < maxNum; i++)
        {
            // Note: most Type classes do not support the hashCode()
            // operation, so we use globalName()'s hash code instead.
            result ^= _types.get(i).globalName().hashCode();
        }

        result += num;

        return result;
    }


    // Protected methods

    /**
        Note: this implementation assumes that two Types are equal iff they
        have the same global name. This is not true in general, but is an
        assumption made by this class.

        @param t1 a type
        @param t2 another type
        @return true iff we consider 't1' and 't2' to be equal
        @see TypeListAsKey
    */
    protected boolean areEqual(Type t1, Type t2)
    {
        // 't1' can be null
        // 't2' can be null

        boolean result = false;

        if (t1 != null && t2 != null)
        {
            result = t1.globalName().equals(t2.globalName());
        }
        else if (t1 == null && t2 == null)
        {
            result = true;
        }

        return result;
    }
}
