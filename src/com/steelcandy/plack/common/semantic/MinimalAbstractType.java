/*
 Copyright (C) 2005-2015 by James MacKay.

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

import com.steelcandy.common.UnsupportedMethodException;

/**
    A minimal abstract base class for Type classes.

    @author  James MacKay
*/
public abstract class MinimalAbstractType
    implements Type
{
    // Public static methods

    /**
        @param t a type
        @return a list of all of 't''s direct and indirect supertypes
        @see Type#directSupertypes
    */
    public static TypeList allSupertypes(Type t)
    {
        Assert.require(t != null);

        TypeList result = TypeList.createArrayList();

        appendAllSupertypes(t, result);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param types a list of types
        @return true iff none of the types in 'types' is invalid
        @see #containsInvalidType(TypeList)
        @see Type#isInvalidType
    */
    public static boolean areAllValidTypes(TypeList types)
    {
        Assert.require(types != null);

        return (containsInvalidType(types) == false);
    }

    /**
        @param types a list of types
        @return true if one or more of the types in 'types' is invalid
        @see #areAllValidTypes(TypeList)
        @see Type#isInvalidType
    */
    public static boolean containsInvalidType(TypeList types)
    {
        Assert.require(types != null);

        boolean result = false;

        TypeIterator iter = types.iterator();
        while (iter.hasNext())
        {
            if (iter.next().isInvalidType())
            {
                result = true;
                break;  // while
            }
        }

        return result;
    }


    /**
        @param types a list of types
        @return true iff all of the types in 'types' are fully resolved
        @see Type#isFullyResolved
    */
    public static boolean areAllFullyResolved(TypeList types)
    {
        Assert.require(types != null);

        boolean result = true;

        TypeIterator iter = types.iterator();
        while (iter.hasNext())
        {
            if (iter.next().isFullyResolved() == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }


    /**
        @param list1 a list of types
        @param list2 another list of types
        @return true iff 'list1' and 'list2' are the same size, and the
        corresponding items in each list are equal
    */
    public static boolean areAllEqual(TypeList list1, TypeList list2)
    {
        Assert.require(list1 != null);
        Assert.require(list2 != null);

        boolean result = (list1.size() == list2.size());

        if (result)
        {
            result = areSameSizeAllEqual(list1, list2);
        }

        return result;
    }


    /**
        @param list1 a list of types
        @param list2 another list of types: it is assumed to be the same size
        as 'list1'
        @return true iff the corresponding items in 'list1' and 'list2' are
        equal
    */
    public static boolean areSameSizeAllEqual(TypeList list1, TypeList list2)
    {
        Assert.require(list1 != null);
        Assert.require(list2 != null);
        Assert.require(list1.size() == list2.size());

        boolean result = true;

        TypeIterator iter1 = list1.iterator();
        TypeIterator iter2 = list2.iterator();
        while (iter1.hasNext())
        {
            Type t1 = iter1.next();
            Type t2 = iter2.next();
            if (t1.equals(t2) == false)
            {
//System.err.println("<><> type " + t1.globalName() + " <> type " + t2.globalName());
                result = false;
                break;
            }
        }

        return result;
    }


    // Public methods

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof Type)
        {
            result = equals((Type) obj);
        }

        return result;
    }

    /**
        By default types do not represent invalid types.

        @see Type#isInvalidType
    */
    public boolean isInvalidType()
    {
        return false;
    }

    /**
        @see Type#display
    */
    public String display()
    {
        String result = globalName();

        Assert.ensure(result != null);
        return result;
    }


    /**
        Note: it is especially important that this method be implemented since
        the default implementation usually throws the exception thrown by our
        implementation of hashCode().

        @see Object#toString
        @see #hashCode
    */
    public String toString()
    {
        return display();
    }

    /**
        Since this method must return the same value for two Objects that our
        equals(Object) method considers equal, and since there are Types that
        (thanks to their isAlwaysEqualTo() method returning true) are equal
        to every other Type object, the only way this method could return a
        valid value would be if it returned the same value for all Type
        instances. But that would result is exceptionally poor hashing, so
        this method throws an exception in order to indicate that it should
        not be hashed.

        @see Object#hashCode
    */
    public int hashCode()
    {
        throw new UnsupportedMethodException(getClass(), "hashCode()");
    }


    // Protected static methods

    /**
        Appends all of the direct and indirect supertypes of 't' to
        'supertypes'.

        @param t the type whose supertypes are to be appended to 'supertypes'
        @param supertypes the list to append 't''s supertypes to
    */
    protected static void appendAllSupertypes(Type t, TypeList supertypes)
    {
        Assert.require(t != null);
        Assert.require(supertypes != null);

        TypeIterator iter = t.directSupertypes().iterator();
        while (iter.hasNext())
        {
            Type st = iter.next();
            supertypes.add(st);
            appendAllSupertypes(st, supertypes);
        }
    }


    // Protected methods

    /**
        @return a single-item TypeList whose sole item is this type
    */
    protected TypeList toTypeList()
    {
        TypeList result = TypeList.createSingleItemList(this);

        Assert.ensure(result != null);
        return result;
    }
}
