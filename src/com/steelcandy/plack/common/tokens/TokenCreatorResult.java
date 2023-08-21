/*
 Copyright (C) 2001-2008 by James MacKay.

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
    The interface implemented by all classes that can be returned as the
    result of a TokenCreator creating tokens.
    <p>
    <strong>Note</strong>: each token returned by an instance of this class
    is assumed to have its SourcePosition set. (The SourcePosition returned
    by nextTokenStartPosition() is the position of the start of the next
    token after the <em>last</em> one returned by this result.)

    @author James MacKay
    @see TokenCreator
*/
public interface TokenCreatorResult
{
    // Public methods

    /**
        @return the first token contained in this result, or null if this
        result doesn't contain any tokens
    */
    public Token firstToken();

    /**
        @return an iterator over the second a subsequent tokens in this
        result, in order, or null if this result contains fewer than two
        tokens
    */
    public TokenIterator extraTokens();

    /**
        @return the position in the source code of the character after the
        last character represented by the last token returned by this result
    */
    public SourcePosition nextTokenStartPosition();
}
