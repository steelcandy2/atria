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

import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourcePosition;

/**
    An abstract base class for TokenCreatorResults.
    <p>
    Subclasses have to implement all of the methods declared in the
    TokenCreatorResult interface except the nextTokenStartPosition() method.

    @author James MacKay
    @see TokenCreator
*/
public abstract class AbstractTokenCreatorResult
    implements TokenCreatorResult
{
    // Private fields

    /** The starting position of the token after this result's token. */
    private SourcePosition _nextTokenStartPosition;


    // Constructors

    /**
        Constructs an AbstractTokenCreatorResult.
        <p>
        This constructor assumes that the specified token is non-null: if
        this can be false then one of the other constructors should be used
        instead of this one.

        @param tok the last token in the result: it is used to (and only to)
        calculate the starting position of the next token
    */
    public AbstractTokenCreatorResult(Token tok)
    {
        this(tok.positionAfter());
    }

    /**
        Constructs an AbstractTokenCreatorResult.

        @param nextTokenStartPos the starting position of the token after the
        last of this result's tokens
    */
    public AbstractTokenCreatorResult(SourcePosition nextTokenStartPos)
    {
        _nextTokenStartPosition = nextTokenStartPos;
    }

    /**
        Constructs an AbstractTokenCreatorResult.

        @param lineNumber the line number part of the starting position of
        the next token after this result's token
        @param offset the offset part of the starting position of the next
        token after this result's token
    */
    public AbstractTokenCreatorResult(int lineNumber, int offset)
    {
        this(new SourcePosition(lineNumber, offset));
    }


    // Public methods

    /**
        @see TokenCreatorResult#nextTokenStartPosition
    */
    public SourcePosition nextTokenStartPosition()
    {
        return _nextTokenStartPosition;
    }
}
