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
    An abstract base class for TokenCreators.
    <p>
    Subclasses have to implement all of the TokenCreator methods: this class
    just provides utility methods.

    @author James MacKay
    @see TokenCreator
*/
public abstract class AbstractTokenCreator
    implements TokenCreator
{
    // Protected methods

    /**
        Returns a TokenCreatorResult containing no tokens and the specified
        position as its next token start position.

        @param nextTokenStartPos the result's next token start position
        @return a TokenCreatorResult containing no tokens
    */
    protected TokenCreatorResult
        createZeroTokenResult(SourcePosition nextTokenStartPos)
    {
        Assert.require(nextTokenStartPos != null);

        return new ZeroTokenCreatorResult(nextTokenStartPos);
    }

    /**
        Returns a TokenCreatorResult containing no tokens and the specified
        line number and offset as the line number and offset of its next
        token start position.

        @param lineNumber the line number part of the result's next token
        start position
        @param offset the offset part of the result's next token start
        position
        @return a TokenCreatorResult containing no tokens
    */
    protected TokenCreatorResult
        createZeroTokenResult(int lineNumber, int offset)
    {
        Assert.require(lineNumber >= 1);
        Assert.require(offset >= 0);

        return new ZeroTokenCreatorResult(lineNumber, offset);
    }

    /**
        Returns a TokenCreatorResult containing the specified token as its
        only token. Its next token start position is the position after the
        token.

        @param tok the token that the result is to contain
        @return a TokenCreatorResult containing 'tok'
    */
    protected TokenCreatorResult createOneTokenResult(Token tok)
    {
        Assert.require(tok != null);

        return new OneTokenCreatorResult(tok);
    }

    /**
        Returns a TokenCreatorResult containing the specified tokens as its
        tokens. Its next token start position is the position after the
        second token.

        @param first the first token that the result is to contain
        @param second the second token that the result is to contain
        @return a TokenCreatorResult containing 'first' and 'second'
    */
    protected TokenCreatorResult
        createTwoTokenResult(Token first, Token second)
    {
        Assert.require(first != null);
        Assert.require(second != null);

        return new TwoTokenCreatorResult(first, second);
    }
}
