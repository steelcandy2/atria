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

import com.steelcandy.plack.common.base.*;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceCode;

/**
    A tokenizer that is a wrapper around another tokenizer, and which allows
    tokens be pushed back onto it after they've been removed. More generally,
    any tokens can be pushed back onto an instance of this class, regardless
    of whether they were obtained from the tokenizer, or even whether any
    tokens at all have been obtained from the tokenizer.
    <p>
    The tokenizer that an instance of this class wraps is assumed to have
    already been initialize()d before it is wrapped: the instance will not
    attempt to initialize it.
    <p>
    The push() methods can be used to push tokens back onto an instance of
    this class. The last token push()ed onto an instance of this class will
    be the next token obtained from the tokenizer.

    @author James MacKay
    @version $Revision: 1.7 $
*/
public class PushbackTokenizer
    extends BufferedTokenizer
{
    // Private fields

    /**
        The tokenizer that this tokenizer wraps, and to which it adds the
        ability to have tokens pushed back onto it.
    */
    private Tokenizer _tokenizer;


    // Constructors

    /**
        Constructs a PushbackTokenizer from the tokenizer that it wraps.

        @param t the tokenizer that the PushbackTokenizer wraps
        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public PushbackTokenizer(Tokenizer t, ErrorHandler handler)
    {
        super(handler);
        Assert.require(t != null);

        _tokenizer = t;
    }

    /**
        Constructs a PushbackTokenizer from the tokenizer that it wraps. The
        PushbackTokenizer uses the wrapped tokenizer's error handler to
        handle any errors.

        @param t the tokenizer that the PushbackTokenizer wraps
    */
    public PushbackTokenizer(Tokenizer t)
    {
        this(t, t.errorHandler());
    }


    // Public methods

    /**
        Pushes the specified token back onto this tokenizer (so that it will
        be the next token returned by this tokenizer).

        @param tok the token to push back onto this tokenizer
        @see BufferedTokenizer#pushBack(Token)
    */
    public void push(Token tok)
    {
        Assert.require(tok != null);

        pushBack(tok);
    }

    /**
        Pushes the specified tokens back onto this tokenizer in reverse order
        from the order that they appear in the list (so the first token in
        the list will be the next token returned by this tokenizer, followed
        by the second token in the list, then the third, etc.).

        @param list the list of tokens to push back onto this tokenizer
        @see BufferedTokenizer#pushBack(TokenList)
    */
    public void push(TokenList list)
    {
        Assert.require(list != null);

        pushBack(list);
    }


    // Overridden/implemented methods

    /**
        @see BufferedTokenizer#generateTokens
    */
    protected void generateTokens()
    {
        if (_tokenizer.hasNext())
        {
            output(_tokenizer.next());
        }
    }

    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        SourceCode result = _tokenizer.sourceCode();

        Assert.ensure(result != null);
        return result;
    }
}
