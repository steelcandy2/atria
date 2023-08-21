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

/**
    A class of object into which tokens can flow: given a Tokenizer an
    instance of this class will pull Tokens from it until it has no more
    tokens to return.
    <p>
    This class is often useful in testing tokenizers: it can be used to take
    the place of the parser (or other class) that might normally consume a
    tokenizer's tokens.

    @author James MacKay
    @version $Revision: 1.5 $
    @see Tokenizer
    @see Token
*/
public class TokenSink
{
    // Private fields

    /** The tokenizer from which this sink is to pull tokens. */
    private Tokenizer _tokenizer;


    // Constructors

    /**
        Constructs a TokenSink from the tokenizer that it is to pull tokens
        from.

        @param tokenizer the tokenizer that this sink is to pull tokens from
    */
    public TokenSink(Tokenizer tokenizer)
    {
        Assert.require(tokenizer != null);

        _tokenizer = tokenizer;
    }


    // Public methods

    /**
        Causes this sink to start pulling tokens from its tokenizer. It will
        stop once it has pulled all of the tokens from the tokenizer.
        <p>
        Note: this method will close() our tokenizer iff it is a
        SourceCodeTokenizer.

        @see SourceCodeTokenizer#close
    */
    public void start()
    {
        try
        {
            while (_tokenizer.hasNext())
            {
                _tokenizer.next();
            }
        }
        finally
        {
            if (_tokenizer instanceof SourceCodeTokenizer)
            {
                ((SourceCodeTokenizer) _tokenizer).close();
            }
        }
    }
}
