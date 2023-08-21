/*
 Copyright (C) 2002-2008 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.plack.common.tokens.SourceCodeTokenizer;

/**
    An abstract base class for source code parsers.
    <p>
    Subclasses just have to implement the abstract methods declared in this
    class.

    @author James MacKay
*/
public abstract class AbstractSourceCodeParser
    implements SourceCodeParser
{
    // Public methods

    /**
        @see SourceCodeParser#parse(SourceCode, ErrorHandler)
    */
    public Construct parse(SourceCode source, ErrorHandler handler)
    {
        Assert.require(source != null);
        Assert.require(handler != null);

        Construct result;

        SourceCodeTokenizer t = null;
        try
        {
            t = createTokenizer(source, handler);
            Assert.check(t != null);

            Parser p = createTokenizerParser();
            Assert.check(p != null);
            result = p.parseConstruct(t, handler);
        }
        finally
        {
            if (t != null)
            {
                t.close();
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        Creates and returns a tokenizer that will tokenize the specified
        source code.

        @param source the source code that the tokenizer is to tokenize
        @param handler the error handler that the tokenizer is to use to
        handle any errors
        @return a tokenizer that will tokenize 'source'
    */
    protected SourceCodeTokenizer
        createTokenizer(SourceCode source, ErrorHandler handler)
    {
        Assert.require(source != null);
        Assert.require(handler != null);

        SourceCodeTokenizer result = createSourceCodeTokenizer(handler);
        result.initialize(source);

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        @return a parser that can parse source code parsed by this parser
        once it has been processed by a tokenizer
    */
    protected abstract Parser createTokenizerParser();
        // Assert.ensure(result != null);

    /**
        Creates and returns a source code tokenizer that this parser can use
        to tokenize source code that it parses.

        @param handler the error handler that the tokenizer is to use to
        handle any errors
        @return a tokenizer that can be used to tokenize source code that
        this parser parses
    */
    protected abstract SourceCodeTokenizer
        createSourceCodeTokenizer(ErrorHandler handler);
        // Assert.ensure(result != null);
}
