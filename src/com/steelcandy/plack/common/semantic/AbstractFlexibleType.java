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

import com.steelcandy.common.UnsupportedMethodException;

/**
    An abstract base class for flexible Type classes: by default they are
    equal to and conform to all other types, and all other types are equal
    to and conform to them.
    <p>
    This class can be subclassed to build classes that represent invalid
    (e.g. missing or nonexistent) types, but it could conceivably also be
    subclassed to build types for use in languages with minimal and/or
    optional typing. However, classes that represent invalid types should
    subclass AbstractInvalidType instead of subclassing this class directly.
    <p>
    Subclasses just have to implement globalName().

    @author  James MacKay
    @version $Revision: 1.3 $
    @see AbstractInvalidType
*/
public abstract class AbstractFlexibleType
    extends MinimalAbstractType
{
    // Public methods

    /**
        @see Type#conformsTo(Type)
    */
    public boolean conformsTo(Type t)
    {
        Assert.require(t != null);

        return true;
    }

    /**
        @see Type#isAlwaysConformedTo
    */
    public boolean isAlwaysConformedTo()
    {
        // Assert.ensure(isNeverConformedTo() == false || result == false);
        // "isNeverConformedTo() implies result == false"
        return true;
    }

    /**
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

        return true;
    }

    /**
        @see Type#isAlwaysEqualTo
    */
    public boolean isAlwaysEqualTo()
    {
        // Assert.ensure(isNeverEqualTo() == false || result == false);
        // "isNeverEqualTo() implies result == false"
        return true;
    }

    /**
        @see Type#isNeverEqualTo
    */
    public boolean isNeverEqualTo()
    {
        // Assert.ensure(isAlwaysEqualTo() == false || result == false);
        // "isAlwaysEqualTo() implies result == false"
        return false;
    }
}
