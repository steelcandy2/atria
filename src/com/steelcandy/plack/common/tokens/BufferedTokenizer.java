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
    An abstract base class for tokenizers that can generate more than one
    token every time they're asked for a token. This class provides a buffer
    to hold the tokens until they're retrieved.
    <p>
    Subclasses just have to implement the generateTokens() abstract method,
    as well as the sourceCode() Tokenizer method. Each call to the
    generateTokens() method must add at least one token to the buffer unless
    it has output <em>all</em> of its tokens to the buffer. Tokens are output
    to the buffer using one of the output() methods.

    @author James MacKay
    @see Token
*/
public abstract class BufferedTokenizer
    extends AbstractTokenizer
{
    // Private fields

    /**
        The buffer in which tokens that have been generated but not yet
        retrieved are stored.
    */
    private TokenList
        _outputBuffer = TokenList.createArrayList();

    /**
        The index in the _outputBuffer of the next token to be returned by
        getNextToken(), or -1 if there are no more tokens in the
        _outputBuffer.

        @see #getNextToken
        @see _outputBuffer
    */
    private int _nextOutputTokenIndex = -1;


    // Constructors

    /**
        Constructs a BufferedTokenizer with a buffer that is initially
        empty.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public BufferedTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public BufferedTokenizer(Resources subtokenizerResources,
                             String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Abstract methods

    /**
        Generates one or more tokens and outputs them to the output buffer
        (using one of the output() methods). If at least one token isn't
        added to the buffer by a call to this method then it will be assumed
        that this tokenizer has no more tokens to return.

        @see #output
    */
    protected abstract void generateTokens();


    // Implemented/overridden AbstractTokenizer methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        boolean result = areTokensInOutputBuffer();
        if (result == false)
        {
            // Try to generate some tokens. Iff none are generated then we
            // have reached the end of our tokens.
            generateTokens();
            result = areTokensInOutputBuffer();
        }
        return result;
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        // If there are no more tokens in the _outputBuffer ...
        if (areTokensInOutputBuffer() == false)
        {
            // This should always put at least one token in the buffer iff
            // our precondition that canGetNextToken() is true holds.
            generateTokens();
        }
        Assert.check(areTokensInOutputBuffer());
        return nextOutputBufferToken();
    }


    // Protected methods

    /**
        @return true iff there is at least one more token in the
        _outputBuffer that has not yet been returned
    */
    protected boolean areTokensInOutputBuffer()
    {
        return (_nextOutputTokenIndex >= 0);
    }

    /**
        @return the next token in the _outputBuffer
    */
    protected Token nextOutputBufferToken()
    {
        Assert.require(areTokensInOutputBuffer());

        Token result = _outputBuffer.get(_nextOutputTokenIndex++);
        if (_nextOutputTokenIndex >= _outputBuffer.size())
        {
            // We've output all of the tokens in the buffer, so clear it out.
            clearOutputBuffer();
        }
        return result;
    }

    /**
        Clears the output buffer so that there are no more tokens in it.
    */
    protected void clearOutputBuffer()
    {
        _outputBuffer.removeAll();
        _nextOutputTokenIndex = -1;
    }


    /**
        Outputs the specified token to the output buffer.

        @param tok the token to output to the token buffer
    */
    protected void output(Token tok)
    {
        _outputBuffer.add(tok);
        if (_nextOutputTokenIndex < 0)
        {
            _nextOutputTokenIndex = 0;
        }
    }

    /**
        Outputs the specified tokens to the output buffer in order.
        <p>
        This method is just a convenience: it has exactly the same effect as
        calling output(tok1) followed by output(tok2).

        @param tok1 the first token to output to the output buffer
        @param tok2 the second token to output to the output buffer
    */
    protected void output(Token tok1, Token tok2)
    {
        output(tok1);
        output(tok2);
    }

    /**
        Outputs all of the tokens in the specified list to the output buffer,
        in order.

        @param tokList the list of tokens whose elements are to be output to
        the output buffer
    */
    protected void output(TokenList tokList)
    {
        output(tokList.iterator());
    }

    /**
        Outputs all of the tokens that the specified iterator iterates over
        to the output buffer, in order.

        @param tokIter the token iterator whose elements are to be output to
        the output buffer
    */
    protected void output(TokenIterator tokIter)
    {
        while (tokIter.hasNext())
        {
            output(tokIter.next());
        }
    }


    /**
        Pushes the specified token back onto the end of this tokenizer's
        buffer (so that it will be the next token returned by this
        tokenizer).

        @param tok the token to push back onto this tokenizer's buffer
    */
    protected void pushBack(Token tok)
    {
        Assert.require(tok != null);

        _outputBuffer.insert(tok, 0);
    }

    /**
        Pushes the specified tokens back onto the end of this tokenizer's
        buffer in reverse order from the order that they appear in the list
        (so the first token in the list will be the next token returned by
        this tokenizer, followed by the second token in the list, then the
        third, etc.).
        <p>
        The order in which the tokens are pushed back onto the buffer is the
        one that correctly pushes back tokens that were obtained from this
        tokenizer and appended to the list (though there is no restriction
        that only tokens obtained from this tokenizer can be pushed back onto
        this tokenizer's buffer).

        @param list the list of tokens to push back onto this tokenizer's
        buffer
    */
    protected void pushBack(TokenList list)
    {
        Assert.require(list != null);

        for (int i = list.size() - 1; i >= 0; i--)
        {
            pushBack(list.get(i));
        }
    }
}
