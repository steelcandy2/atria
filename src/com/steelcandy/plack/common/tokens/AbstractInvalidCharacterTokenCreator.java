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
import com.steelcandy.plack.common.errors.TokenizingError;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourcePosition;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.CharacterIterator;

import com.steelcandy.common.*;

/**
    An abstract base class for TokenCreators that handle characters that are
    not valid anywhere in the source code when used directly (but may be
    valid in string literals or comments, for example). Subclasses can
    override the createError() method to handle characters that are only
    invalid in some contexts, or to create a more appropriate or specific
    error.

    @author James MacKay
    @version $Revision: 1.11 $
    @see TokenCreatorSourceCodeTokenizer.InvalidCharacterTokenCreator
*/
public abstract class AbstractInvalidCharacterTokenCreator
    extends AbstractTokenCreator
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_CHAR_MSG =
            "INVALID_CHAR_MSG";


    // Public methods

    /**
        @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
    */
    public TokenCreatorResult
        create(CharacterIterator iter, SourcePosition startPos,
               Tokenizer tokenizer, ErrorHandler handler)
    {
        handler.handle(createError(iter.peek(), startPos), tokenizer);

        iter.discard();  // the invalid character
        return createZeroTokenResult(startPos.lineNumber(),
                                     startPos.offset() + 1);
    }


    // Protected methods

    /**
        Creates and returns an error indicating that the specified characters
        is not valid anywhere in the source code.

        @param invalidChar the character that is not valid anywhere in the
        source code
        @param charPosition the invalid character's position in the source
        code
        @return an error describing the invalid character
    */
    protected TokenizingError createError(char invalidChar,
                                          SourcePosition charPosition)
    {
        String msg = _resources.getMessage(INVALID_CHAR_MSG,
                                           String.valueOf(invalidChar));
        SourceLocation loc = createSingleCharacterLocation(charPosition);
        return new TokenizingError(invalidCharacterErrorSeverityLevel(),
                                   msg, tokenizingSourceCode(), loc);
    }

    /**
        This implementation returns NON_FATAL_ERROR_LEVEL.

        @return the error severity level of errors used to report an invalid
        character
    */
    protected int invalidCharacterErrorSeverityLevel()
    {
        return TokenizingError.NON_FATAL_ERROR_LEVEL;
    }


    // Abstract methods

    /**
        Creates and returns the location of the single character at the
        specified position.

        @param pos the position of the character
        @return the character's location
    */
    protected abstract SourceLocation
        createSingleCharacterLocation(SourcePosition pos);
        // Assert.require(pos != null);
        // Assert.ensure(result != null);

    /**
        @return the piece of source code that this token creator is creating
        tokens to represent parts of, or null if that information is not
        available
    */
    protected abstract SourceCode tokenizingSourceCode();
}
