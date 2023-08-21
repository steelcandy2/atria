/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common.chars;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by all unary predicates on chars.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public interface UnaryCharacterPredicate
{
    /**
        Indicates whether the specified character satisfies this
        predicate.

        @param item the character to test
        @return true iff the character satisfies this predicate
    */
    public boolean isSatisfied(char item);
}
