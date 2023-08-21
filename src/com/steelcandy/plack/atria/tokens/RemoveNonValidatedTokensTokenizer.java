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

import com.steelcandy.plack.common.source.SourcePosition;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A base class for filter tokenizers that remove tokens with a specified
    ID, and handles specially those that don't have their IS_VALIDATED flag
    set. Tokens with the specified ID are removed regardless of whether
    their IS_VALIDATED flag is set, though.
    <p>
    Subclasses just have to implement the handleNonValidatedToken() method to
    handle tokens that this filter is going to remove and that don't have
    their IS_VALIDATED flag set.

    @author James MacKay
    @see AtriaTokenManagerBase#IS_VALIDATED
*/
public abstract class RemoveNonValidatedTokensTokenizer
    extends RemovalFilterTokenizer
{
    // Private fields

    /** The ID of the tokens this tokenizer is to remove. */
    private int _idToRemove;


    // Constructors

    /**
        Constructs a RemoveNonValidatedTokensTokenizer.

        @param idOfTokensToRemove the token ID of the tokens to
        be removed by the tokenizer
        @param handler the error handler the tokenizer is to
        use to handle any errors
    */
    public RemoveNonValidatedTokensTokenizer(int idOfTokensToRemove,
                                             ErrorHandler handler)
    {
        super(handler);
        _idToRemove = idOfTokensToRemove;
    }

    /**
        Constructs a RemoveNonValidatedTokensTokenizer.

        @param idOfTokensToRemove the token ID of the tokens to
        be removed by the tokenizer
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemoveNonValidatedTokensTokenizer(int idOfTokensToRemove,
                                             Resources subtokenizerResources,
                                             String resourceKeyPrefix,
                                             ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
        _idToRemove = idOfTokensToRemove;
    }


    // Protected methods

    /**
        @see RemovalFilterTokenizer#remove
    */
    protected boolean remove(Token tok)
    {
        boolean result = false;
        if (tok.id() == _idToRemove)
        {
            result = true;
            if (tok.isFlagSet(AtriaTokenManager.IS_VALIDATED) == false)
            {
                handleNonValidatedToken(tok);
            }
        }
        return result;
    }


    // Abstract methods

    /**
        The method that is called when the specified token didn't have its
        IS_VALIDATED flag set.

        @param tok the non-validated token
    */
    protected abstract void handleNonValidatedToken(Token tok);
        // Assert.require(tok.isFlagSet(AtriaTokenManager.IS_VALIDATED) == false);
}
