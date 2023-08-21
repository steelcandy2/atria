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
    An abstract base class for Type classes.

    @author  James MacKay
    @version $Revision: 1.4 $
*/
public abstract class AbstractType
    extends MinimalAbstractType
{
    // Public methods

    /**
        @see Type#conformsTo(Type)
    */
    public boolean conformsTo(Type t)
    {
        Assert.require(t != null);

        boolean result;

        if (t.isAlwaysConformedTo())
        {
            Assert.check(t.isNeverConformedTo() == false);
            result = true;
        }
        else if (t.isNeverConformedTo())
        {
            result = false;
        }
        else
        {
            result = reallyConformsTo(t);
        }

        return result;
    }

    /**
        By default all types do not conform to this type.

        @see Type#isAlwaysConformedTo
    */
    public boolean isAlwaysConformedTo()
    {
        // Assert.ensure(isNeverConformedTo() == false || result == false);
        // "isNeverConformedTo() implies result == false"
        return false;
    }

    /**
        By default some types conform to this type: this type, for example.

        @see Type#isNeverConformedTo
    */
    public boolean isNeverConformedTo()
    {
        // Assert.ensure(isAlwaysConformedTo() == false || result == false);
        // "isAlwaysConformedTo() implies result == false"
        return false;
    }


    /**
        @see Type#equals(Type)
    */
    public boolean equals(Type t)
    {
        Assert.require(t != null);

        boolean result;

        if (t.isAlwaysEqualTo())
        {
            Assert.check(t.isNeverEqualTo() == false);
            result = true;
        }
        else if (t.isNeverEqualTo())
        {
            result = false;
        }
        else
        {
            result = reallyEquals(t);
        }

        return result;
    }

    /**
        By default types are not equal to all other types.

        @see Type#isAlwaysEqualTo
    */
    public boolean isAlwaysEqualTo()
    {
        // Assert.ensure(isNeverEqualTo() == false || result == false);
        // "isNeverEqualTo() implies result == false"
        return false;
    }

    /**
        By default types are equal to at least some types: themselves, for
        one.

        @see Type#isNeverEqualTo
    */
    public boolean isNeverEqualTo()
    {
        // Assert.ensure(isAlwaysEqualTo() == false || result == false);
        // "isAlwaysEqualTo() implies result == false"
        return false;
    }


    // Abstract methods

    /**
        @param t another type
        @return true iff this type conforms to 't'
        @see #conformsTo(Type)
    */
    protected abstract boolean reallyConformsTo(Type t);
        // Assert.require(t != null);

    /**
        @param t another type
        @return true iff this type represents exactly the same type that
        't' does
        @see #equals(Type)
    */
    protected abstract boolean reallyEquals(Type t);
        // Assert.require(t != null);
}
