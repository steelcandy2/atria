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

import com.steelcandy.common.ints.UnaryIntPredicate;

/**
    A unary token predicate that is satisfied by a token iff that token's ID
    satisfies a UnaryIntPredicate.

    @author James MacKay
    @version $Revision: 1.4 $
    @see Token
    @see UnaryIntPredicate
*/
public class IdPredicateTokenPredicate
    implements UnaryTokenPredicate
{
    // Private fields

    /**
        The UnaryIntPredicate that a token's ID must satisfy iff the token is
        to satisfy this predicate.
    */
    private UnaryIntPredicate _idPredicate;


    // Constructors

    /**
        Constructs an IdPredicateTokenPredicate.

        @param idPredicate the predicate that a token's ID must satisfy iff
        the token is to satisfy the predicate being constructed
    */
    public IdPredicateTokenPredicate(UnaryIntPredicate idPredicate)
    {
        Assert.require(idPredicate != null);

        _idPredicate = idPredicate;
    }


    // Public methods

    /**
        @see UnaryTokenPredicate#isSatisfied
    */
    public boolean isSatisfied(Token tok)
    {
        return (tok != null) && _idPredicate.isSatisfied(tok.id());
    }
}
