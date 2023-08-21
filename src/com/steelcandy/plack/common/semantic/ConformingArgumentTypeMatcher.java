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
    type to match a formal argument's type iff the actual's type conforms
    to the formal's type.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class ConformingArgumentTypeMatcher
    extends AbstractArgumentTypeMatcher
{
    // Constants

    /** The single instance of this class. */
    private static final ConformingArgumentTypeMatcher
        _instance = new ConformingArgumentTypeMatcher();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static ConformingArgumentTypeMatcher instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private ConformingArgumentTypeMatcher()
    {
        // empty
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

        return actualType.conformsTo(formalType);
    }
}
