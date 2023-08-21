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
    A singleton unary token predicate that cannot be satisfied by any token.

    @author James MacKay
    @see Token
*/
public class UnsatisfiableUnaryTokenPredicate
    implements UnaryTokenPredicate
{
    // Constants

    /** The single instance of this class. */
    private static final UnsatisfiableUnaryTokenPredicate
        _instance = new UnsatisfiableUnaryTokenPredicate();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static UnsatisfiableUnaryTokenPredicate instance()
    {
        return _instance;
    }

    /**
        Constructs an UnsatisfiableUnaryTokenPredicate.
    */
    private UnsatisfiableUnaryTokenPredicate()
    {
        // empty
    }


    // Public methods

    /**
        @see UnaryTokenPredicate#isSatisfied
    */
    public boolean isSatisfied(Token tok)
    {
        return false;
    }
}
