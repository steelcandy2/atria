/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

/**
    A unary token predicate that is satisfied by a token iff that token has a
    specified flag set.
    <p>
    Since an instance's state is unchanging once it has been constructed, its
    isSatisfied() method is reentrant, and thus the instance can be shared.

    @author James MacKay
    @see Token
*/
public class HasFlagSetTokenPredicate
    implements UnaryTokenPredicate
{
    // Private fields

    /**
        The flag that a token must have set in order to satisfy this
        predicate.
    */
    private int _flagToHaveSet;


    // Constructors

    /**
        Constructs a HasFlagSetTokenPredicate from the flag that a token must
        have set in order to satisfy the predicate.

        @param flagToHaveSet the flag a token must have set in order to
        satisfy the predicate
    */
    public HasFlagSetTokenPredicate(int flagToHaveSet)
    {
        _flagToHaveSet = flagToHaveSet;
    }


    // Implemented UnaryTokenPredicate methods

    /**
        @see UnaryTokenPredicate#isSatisfied
    */
    public boolean isSatisfied(Token tok)
    {
        return (tok != null) && tok.isFlagSet(_flagToHaveSet);
    }
}
