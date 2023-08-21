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
import com.steelcandy.plack.common.source.SourceCode;

/**
    A tokenizer that returns the tokens from the list of tokens from which
    it was constructed.

    @author James MacKay
    @version $Revision: 1.8 $
*/
public class ListTokenizer
    extends IteratorTokenizer
{
    // Constructors

    /**
        Constructs a ListTokenizer from the list of tokens it is to return
        and the tokenizer that they were (presumably) obtained from.
        <p>
        The specified tokenizer's error handler will be used by the
        constructed tokenizer to handle any errors.

        @param list the list of tokens the tokenizer is to return
        @param origin the tokenizer that the tokens were obtained from
    */
    public ListTokenizer(TokenList list, Tokenizer origin)
    {
        super(list.iterator(), origin);
    }

    /**
        Constructs a ListTokenizer from the list of tokens it is to return
        and the tokenizer that they were (presumably) obtained from.

        @param list the list of tokens the tokenizer is to return
        @param origin the tokenizer that the tokens were obtained from
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public ListTokenizer(TokenList list, Tokenizer origin,
                         ErrorHandler handler)
    {
        super(list.iterator(), origin.sourceCode(), handler);
    }

    /**
        Constructs a ListTokenizer from the list of tokens it is to return
        and the source code that the tokens (presumably) represent at least
        part of.

        @param list the list of tokens the tokenizer is to return
        @param source the source code that the tokens represent at least part
        of
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public ListTokenizer(TokenList list, SourceCode source,
                         ErrorHandler handler)
    {
        super(list.iterator(), source, handler);
    }
}
