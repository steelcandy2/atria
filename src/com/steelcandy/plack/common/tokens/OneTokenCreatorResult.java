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

import com.steelcandy.plack.common.source.SourcePosition;

/**
    The class of the result returned by TokenCreators that always create at
    most one token.

    @author James MacKay
    @version $Revision: 1.2 $
    @see TokenCreator
*/
public class OneTokenCreatorResult
    extends AbstractTokenCreatorResult
{
    // Private fields

    /** The token that was created, or null if no token was created. */
    private Token _token;


    // Constructors

    /**
        Constructs a OneTokenCreatorResult.
        <p>
        This constructor assumes that the specified token is always non-null:
        if it can be null then one of the other constructors should be used.

        @param tok the single token that this result contains
    */
    public OneTokenCreatorResult(Token tok)
    {
        super(tok);
        _token = tok;
    }

    /**
        Constructs a OneTokenCreatorResult.

        @param tok the single token that this result contains
        @param nextTokenStartPos the starting position of the token after
        this result's token
    */
    public OneTokenCreatorResult(Token tok,
                                 SourcePosition nextTokenStartPos)
    {
        super(nextTokenStartPos);
        _token = tok;
    }

    /**
        Constructs a OneTokenCreatorResult.

        @param tok the single token that this result contains
        @param lineNumber the line number part of the starting position of
        the next token after this result's token
        @param offset the offset part of the starting position of the next
        token after this result's token
    */
    public OneTokenCreatorResult(Token tok, int lineNumber, int offset)
    {
        super(lineNumber, offset);

        _token = tok;
    }


    // Public methods

    /**
        @see TokenCreatorResult#firstToken
    */
    public Token firstToken()
    {
        return _token;
    }

    /**
        @see TokenCreatorResult#extraTokens
    */
    public TokenIterator extraTokens()
    {
        return null;
    }
}
