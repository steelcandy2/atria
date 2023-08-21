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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    An abstract base class for filter tokenizers that remove all tokens that
    represent invalid characters and reports the presence of the invalid
    characters that they represent.
    <p>
    Tokenizers that extend this class usually appear at or near the end of
    any sequence of tokenizers.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public abstract class AbstractRemoveInvalidCharactersTokenizer
    extends AbstractFilterTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        SINGLE_INVALID_CHARACTER_MSG =
            "SINGLE_INVALID_CHARACTER_MSG";
    private static final String
        MULTIPLE_INVALID_CHARACTERS_MSG =
            "MULTIPLE_INVALID_CHARACTERS_MSG";


    // Constructors

    /**
        Constructs an AbstractRemoveInvalidCharactersTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public AbstractRemoveInvalidCharactersTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AbstractRemoveInvalidCharactersTokenizer(
        Resources subtokenizerResources, String resourceKeyPrefix,
        ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        discardInvalidCharacterTokens();
        return source().hasNext();
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        discardInvalidCharacterTokens();
        return source().next();
    }


    /**
        Reports and discards any and all invalid character tokens that are
        the next token(s) from our source tokenizer.
    */
    protected void discardInvalidCharacterTokens()
    {
        Tokenizer source = source();

        if (source.hasNext() && isInvalidCharacterToken(source.peek()))
        {
            // There's at least one InvalidCharacterToken.
            TokenList tokList = TokenList.createArrayList();
            StringBuffer invalidChars = new StringBuffer();

            Token tok = source.next();
            tokList.add(tok);
            invalidChars.append(tok.stringValue());  // the invalid char

            // Add any immediately following invalid characters/tokens to
            // 'invalidChars'/'tokList'.
            while (source.hasNext())
            {
                tok = source().peek();
                if (isInvalidCharacterToken(tok) == false)
                {
                    break;  // while
                }
                source.discardNext();  // discard 'tok'
                tokList.add(tok);
                invalidChars.append(tok.stringValue());  // the invalid char
            }

            // Report the invalid tokens.
            int numInvalidChars = tokList.size();
            Assert.check(numInvalidChars > 0);
            String msg;
            SourceLocation loc;
            if (numInvalidChars > 1)
            {
                msg = _resources.
                    getMessage(MULTIPLE_INVALID_CHARACTERS_MSG,
                               invalidChars.toString(),
                               String.valueOf(numInvalidChars));
                loc = location(tokList);
            }
            else
            {
                msg = _resources.
                    getMessage(SINGLE_INVALID_CHARACTER_MSG,
                               invalidChars.toString());
                loc = tokList.get(0).location();
            }
            handleError(NON_FATAL_ERROR_LEVEL, msg, loc);
        }

        Assert.ensure(source().hasNext() == false ||
            isInvalidCharacterToken(source().peek()) == false);
    }


    // Abstract methods

    /**
        @param tokens a list of tokens
        @return the location of the source code fragment that all of the
        tokens in 'tokens' represent
    */
    protected abstract SourceLocation location(TokenList tokens);
        // Assert.require(tokens != null);
        // Assert.ensure(result != null);

    /**
        @param tok a token
        @return true iff tok is an invalid character token
    */
    protected abstract boolean isInvalidCharacterToken(Token tok);
        // Assert.require(tok != null);
}
