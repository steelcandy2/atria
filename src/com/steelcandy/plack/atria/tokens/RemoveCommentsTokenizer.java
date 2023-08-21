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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.AbstractTokenizer;  // javadocs
import com.steelcandy.plack.common.tokens.RemovalFilterTokenizer;
import com.steelcandy.plack.common.tokens.Token;

import com.steelcandy.common.Resources;

/**
    A filter tokenizer that removes comment tokens.

    @author James MacKay
*/
public class RemoveCommentsTokenizer
    extends RemovalFilterTokenizer
{
    // Constructors

    /**
        Constructs a RemoveCommentsTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public RemoveCommentsTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemoveCommentsTokenizer(Resources subtokenizerResources,
                                   String resourceKeyPrefix,
                                   ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see RemovalFilterTokenizer#remove
    */
    protected boolean remove(Token tok)
    {
        return (tok.id() == AtriaTokenManager.COMMENT);
    }
}
