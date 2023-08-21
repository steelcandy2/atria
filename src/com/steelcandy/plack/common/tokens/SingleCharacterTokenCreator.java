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

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.plack.common.source.CharacterIterator;
import com.steelcandy.plack.common.source.SourcePosition;

/**
    An abstract base class for TokenCreators that create tokens representing
    a single character.
    <p>
    Subclasses just have to implement the createToken() method to create the
    token representing the single character.

    @author James MacKay
*/
public abstract class SingleCharacterTokenCreator
    extends AbstractTokenCreator
{
    // Public methods

    /**
        @see TokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
    */
    public TokenCreatorResult
        create(CharacterIterator iter, SourcePosition startPos,
               Tokenizer tokenizer, ErrorHandler handler)
    {
        char ch = iter.next();

        return createOneTokenResult(createToken(ch, startPos));
    }


    // Abstract methods

    /**
        Creates and returns a token to represent the single character at the
        specified location.

        @param ch the single character that the token is to represent
        @param startPos the position in the source code of 'ch'
    */
    protected abstract Token
        createToken(char ch, SourcePosition startPos);
        // Assert.require(startPos != null);
}
