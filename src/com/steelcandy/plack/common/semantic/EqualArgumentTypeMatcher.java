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
    The singleton ArgumentTypeMatcher that considers an actual argument's
    type to match a formal argument's type iff the two types are equal.
    <p>
    If a formal and actual type are both invalid and both have the same
    global name then they're considered to be equal by this matcher.

    @author  James MacKay
*/
public class EqualArgumentTypeMatcher
    extends AbstractArgumentTypeMatcher
{
    // Constants

    /** The single instance of this class. */
    private static final EqualArgumentTypeMatcher
        _instance = new EqualArgumentTypeMatcher();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static EqualArgumentTypeMatcher instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private EqualArgumentTypeMatcher()
    {
        // empty
    }


    // Public methods

    /**
        @see ArgumentTypeMatcher#isMatch(Type, Type)
    */
    public boolean isMatch(Type formalType, Type actualType)
    {
        Assert.require(formalType != null);
        Assert.require(actualType != null);

        boolean result;

        boolean isFormalInvalid = formalType.isInvalidType();
        boolean isActualInvalid = actualType.isInvalidType();
        if (isFormalInvalid && isActualInvalid)
        {
// TODO: do we want this???!!!???
// - e.g. in the case where types are given generic names such as "unknown type"
// - this is probably alright for now, but may not be the best we can do
            result = formalType.globalName().equals(actualType.globalName());
        }
        else if (isFormalInvalid || isActualInvalid)
        {
            result = false;
        }
        else
        {
            result = isValidTypesMatch(formalType, actualType);
        }

        return result;
    }


    // Protected methods

    /**
        @see AbstractArgumentTypeMatcher#isValidTypesMatch(Type, Type)
    */
    protected boolean isValidTypesMatch(Type formalType, Type actualType)
    {
        Assert.require(formalType != null);
        Assert.require(formalType.isInvalidType() == false);
        Assert.require(actualType != null);
        Assert.require(actualType.isInvalidType() == false);

        return actualType.equals(formalType);
    }
}
