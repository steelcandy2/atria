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
    The class of the result returned by TokenCreators that never create any
    tokens.

    @author James MacKay
    @see TokenCreator
*/
public class ZeroTokenCreatorResult
    extends AbstractTokenCreatorResult
{
    // Constructors

    /**
        Constructs a ZeroTokenCreatorResult.

        @param nextTokenStartPos the starting position of the next token
    */
    public ZeroTokenCreatorResult(SourcePosition nextTokenStartPos)
    {
        super(nextTokenStartPos);
    }

    /**
        Constructs a ZeroTokenCreatorResult.

        @param lineNumber the line number part of the starting position of
        the next token
        @param offset the offset part of the starting position of the next
        token
    */
    public ZeroTokenCreatorResult(int lineNumber, int offset)
    {
        super(lineNumber, offset);
    }


    // Public methods

    /**
        @see TokenCreatorResult#firstToken
    */
    public Token firstToken()
    {
        return null;
    }

    /**
        @see TokenCreatorResult#extraTokens
    */
    public TokenIterator extraTokens()
    {
        return null;
    }
}
