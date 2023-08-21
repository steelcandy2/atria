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

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.Resources;

/**
    A filter tokenizer that removes all InvalidCharacterTokens and reports
    the presence of the invalid characters that they represent.
    <p>
    This tokenizer usually appears at or near the end of any sequence of
    tokenizers.

    @author James MacKay
*/
public class RemoveInvalidCharactersTokenizer
    extends AbstractRemoveInvalidCharactersTokenizer
{
    // Constants

    /** The single AtriaTokenManager instance. */
    private static final AtriaTokenManager
        TOKEN_MANAGER = AtriaTokenManager.instance();


    // Constructors

    /**
        Constructs a RemoveInvalidCharactersTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public RemoveInvalidCharactersTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemoveInvalidCharactersTokenizer(Resources subtokenizerResources,
                                  String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see AbstractRemoveInvalidCharactersTokenizer#location(TokenList)
    */
    protected SourceLocation location(TokenList tokens)
    {
        Assert.require(tokens != null);

        SourceLocation result = TOKEN_MANAGER.location(tokens);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see AbstractRemoveInvalidCharactersTokenizer#isInvalidCharacterToken(Token)
    */
    protected boolean isInvalidCharacterToken(Token tok)
    {
        Assert.require(tok != null);

        return (tok.id() == TOKEN_MANAGER.INVALID_CHARACTER);
    }
}
