/*
 Copyright (C) 2004 by James MacKay.

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
    An abstract base class for ArgumentTypeMatchers.
    <p>
    By default subclasses will <em>not</em> consider a formal and an actual
    type to match if either is an invalid type. Most subclasses will want to
    inherit this behaviour, in which case they should just implement
    isValidTypesMatch(); other subclasses will probably also want to override
    this class' implementation of isMatch().

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public abstract class AbstractArgumentTypeMatcher
    implements ArgumentTypeMatcher
{
    // Public methods

    /**
        @see ArgumentTypeMatcher#isMatch(Type, Type)
    */
    public boolean isMatch(Type formalType, Type actualType)
    {
        Assert.require(formalType != null);
        Assert.require(actualType != null);

        boolean result = false;

        if (actualType.isInvalidType() == false &&
            formalType.isInvalidType() == false)
        {
            result = isValidTypesMatch(formalType, actualType);
        }

        return result;
    }


    // Abstract methods

    /**
        Indicates whether the specified actual argument type matches the
        specified formal argument type. Both types are assumed to not be
        invalid.

        @param formalType a formal argument type
        @param actualType an actual argument type
        @return true iff 'actualType' matches 'formalType'
    */
    protected abstract boolean
        isValidTypesMatch(Type formalType, Type actualType);
        // Assert.require(formalType != null);
        // Assert.require(formalType.isInvalidType() == false);
        // Assert.require(actualType != null);
        // Assert.require(actualType.isInvalidType() == false);
}
