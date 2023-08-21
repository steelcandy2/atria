/*
 Copyright (C) 2001-2005 by James MacKay.

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
    The interface implemented by all classes that create zero or more tokens
    from source code.
    <p>
    Since classes implementing this interface can usually only create tokens
    of certain types, some checking is usually done on the source fragment
    before passing it to a TokenCreator to ensure that the TokenCreator can
    create a token from it. (Often this checking only looks at the first
    character of the source fragment.) If a TokenCreator cannot create a
    token from the source fragment given to it then it must report an error
    (using the error handler passed to it) and not create a token.
    <p>
    Every call to a TokenCreator's create() method is assumed to consume at
    least one character from the source code, and to create zero or more
    tokens from the source code it consumes. Note that a TokenCreator can
    consume source code characters and yet not create a token: for example, a
    type of TokenCreator could be written and used to discard whitespace
    characters. TokenCreators only rarely create more than one token per call
    to their create() method.
    <p>
    Instances of this class are often placed in arrays indexed by character
    values so that a TokenCreator can be selected based on the first
    character of the source fragment. They are also assumed to be stateless,
    and hence usable by multiple threads concurrently.

    @author James MacKay
    @see Token
*/
public interface TokenCreator
{
    // Public methods

    /**
        Attempts to create zero (rarely), one (usually) or (occasionally)
        more tokens representing the source fragment that the specified
        iterator is on the first character of.

        @param iter the iterator over the source code being tokenized.
        The next character it returns is the first character of the source
        fragment for which this method is creating a token
        @param startPos the position in the source code of the first
        character returned by 'iter'
        @param tokenizer the tokenizer for whom the token is being created
        @param handler the error handler to use to handle any errors
        @return a TokenCreatorResult containing the tokens that were
        created, as well as the starting position in the source code of the
        next token
        @see TokenCreatorResult
    */
    public TokenCreatorResult
        create(CharacterIterator iter, SourcePosition startPos,
               Tokenizer tokenizer, ErrorHandler handler);
}
