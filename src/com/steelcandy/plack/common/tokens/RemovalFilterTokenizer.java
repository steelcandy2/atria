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

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.common.*;

/**
    An abstract base class for filter tokenizers that generate the same
    tokens in the same order that they're obtained from their source
    tokenizers, except that some of them are removed.
    <p>
    Subclasses just have to implement the remove() method to indicate which
    tokens are to be removed.

    @author James MacKay
    @version $Revision: 1.8 $
    @see Token
    @see FilterTokenizer
*/
public abstract class RemovalFilterTokenizer
    extends AbstractFilterTokenizer
{
    // Constructors

    /**
        Constructs a RemovalFilterTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public RemovalFilterTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemovalFilterTokenizer(Resources subtokenizerResources,
                                  String resourceKeyPrefix,
                                  ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Abstract methods

    /**
        Indicates whether the specified token should be removed from the
        tokens obtained from our source tokenizer.

        @param tok the token whose removal will be decided by what is
        returned by this method
        @return true if the token is to be removed, and false if it isn't
    */
    protected abstract boolean remove(Token tok);


    // Implemented/overridden methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        boolean result = false;
        while (source().hasNext())
        {
            if (remove(source().peek()))
            {
                // Discard the token (since it is to be removed.)
                source().discardNext();
            }
            else
            {
                // The token is the next token that will be returned by
                // getNextToken(). (Note that we don't remove it here.)
                result = true;
                break;
            }
        }
        return result;
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        Token result = null;
        Tokenizer src = source();
        Assert.check(src.hasNext());  // since canGetNextToken()
        while (src.hasNext())
        {
            Token tok = src.next();
            if (remove(tok) == false)
            {
                result = tok;
                break;
            }
        }

        Assert.ensure(result != null);
        return result;
    }
}
