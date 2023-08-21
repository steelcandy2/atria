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
    The interface implemented by classes that compare actual argument types
    to corresponding formal argument types to see if they match.

    @author  James MacKay
*/
public interface ArgumentTypeMatcher
{
    // Public methods

    /**
        Indicates whether the specified actual argument type matches the
        specified formal argument type.

        @param formalType a formal argument type
        @param actualType an actual argument type
        @return true iff 'actualType' matches 'formalType'
    */
    public boolean isMatch(Type formalType, Type actualType);
        // Assert.require(formalType != null);
        // Assert.require(actualType != null);
}
