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

import com.steelcandy.common.*;

/**
    A filter tokenizer that outputs unchanged the tokens that it obtains from
    from its source tokenizer.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class NullFilterTokenizer
    extends AbstractFilterTokenizer
{
    // Constructors

    /**
        Constructs a NullFilterTokenizer.

        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public NullFilterTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public NullFilterTokenizer(Resources subtokenizerResources,
                               String resourceKeyPrefix,
                               ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Overridden/implemented methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        return source().hasNext();
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        return source().next();
    }
}
