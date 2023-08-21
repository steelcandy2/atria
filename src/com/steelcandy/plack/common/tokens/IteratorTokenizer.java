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
    A tokenizer that returns the tokens that are returned by the token
    iterator that it was constructed from.

    @author James MacKay
    @version $Revision: 1.4 $
*/
public class IteratorTokenizer
    extends AbstractTokenizer
{
    // Private fields

    /** The source code that the tokens represent (part of). */
    private SourceCode _sourceCode;

    /** The iterator whose tokens this tokenizer is to return. */
    private TokenIterator _iter;


    // Constructors

    /**
        Constructs an IteratorTokenizer from the iterator whose tokens it is
        to return and the tokenizer that the iterator (presumably) obtained
        them from.
        <p>
        The specified tokenizer's error handler will be used by the
        constructed tokenizer to handle any errors.

        @param iter the iterator whose tokens the tokenizer is to return
        @param origin the tokenizer that the tokens were obtained from
    */
    public IteratorTokenizer(TokenIterator iter, Tokenizer origin)
    {
        this(iter, origin, origin.errorHandler());
    }

    /**
        Constructs an IteratorTokenizer from the iterator whose tokens it is
        to return and the tokenizer that the iterator (presumably) obtained
        them from.

        @param iter the iterator whose tokens the tokenizer is to return
        @param origin the tokenizer that the tokens were obtained from
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public IteratorTokenizer(TokenIterator iter, Tokenizer origin,
                             ErrorHandler handler)
    {
        this(iter, origin.sourceCode(), handler);
    }

    /**
        Constructs an IteratorTokenizer from the iterator whose tokens it is
        to return and the source code that the tokens (presumably) represent
        at least part of.

        @param iter the iterator whose tokens the tokenizer is to return
        @param source the source code that the tokens represent at least part
        of
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public IteratorTokenizer(TokenIterator iter, SourceCode source,
                             ErrorHandler handler)
    {
        super(handler);
        Assert.require(iter != null);
        Assert.require(source != null);
        Assert.require(handler != null);

        _iter = iter;
        _sourceCode = source;
    }


    // Public methods

    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        Assert.ensure(_sourceCode != null); // i.e. result != null
        return _sourceCode;
    }


    // Protected methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        return _iter.hasNext();
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        return _iter.next();
    }
}
