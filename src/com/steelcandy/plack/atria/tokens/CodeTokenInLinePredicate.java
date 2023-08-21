/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.atria.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.UnaryTokenPredicate;

/**
    Represents a singleton predicate that is satisfied by any token that
    represents a piece of Atria code (as opposed to a comment, for example)
    and not the end of a line. More precisely, it is satisfied by any token
    that does not have any of its IS_NON_CODE or IS_END_OF_LINE flags set.
    <p>
    This predicate is usually used to test the tokens that represent a
    (physical or logical) line of Atria source code, starting from the last
    token and moving towards the first, to find the last token in the line
    that represents a 'real' piece of Atria source code (as opposed to a
    newline or comment, for example).

    @author James MacKay
    @version $Revision: 1.7 $
    @see Token
*/
public class CodeTokenInLinePredicate
    implements UnaryTokenPredicate
{
    // Constants

    /** The single instance of this class. */
    private static final CodeTokenInLinePredicate
        _instance = new CodeTokenInLinePredicate();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static UnaryTokenPredicate instance()
    {
        return _instance;
    }

    /**
        Default constructor. It should only be called by subclasses'
        constructors and to construct the single instance of this class.
    */
    protected CodeTokenInLinePredicate()
    {
        // empty
    }


    // Public methods

    /**
        @see UnaryTokenPredicate#isSatisfied
    */
    public boolean isSatisfied(Token tok)
    {
        return (tok != null) &&
               (tok.isFlagSet(AtriaTokenManager.IS_END_OF_LINE) == false) &&
               (tok.isFlagSet(AtriaTokenManager.IS_NON_CODE) == false);
    }
}
