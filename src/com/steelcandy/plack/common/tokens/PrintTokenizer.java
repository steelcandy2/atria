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

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import java.io.*;

/**
    The abstract base class for filter tokenizers that pass through unchanged
    the tokens they obtain from their sources, but also output information
    about the tokens using a specified Writer.
    <p>
    Subclasses only have to implement the abstract outputTokenInformation()
    method.
    <p>
    Instances of this class can be used for, among other things, debugging a
    sequence of tokenizers: inserting instances of this class into the
    sequence of tokenizers can help one to discover which tokenizer is
    causing a given problem.

    @author James MacKay
    @see Token
*/
public abstract class PrintTokenizer
    extends AbstractFilterTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TOKEN_INFO_OUTPUT_FAILED_MSG =
            "TOKEN_INFO_OUTPUT_FAILED_MSG";


    // Private fields

    /** The Writer to use to output the tokens' information. */
    private Writer _writer;


    // Constructors

    /**
        Constructs a PrintTokenizer that outputs information about tokens to
        standard output.

        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public PrintTokenizer(ErrorHandler handler)
    {
        this(Io.out, handler);
    }

    /**
        Constructs a PrintTokenizer from the Writer it is to use to output
        information about the tokens it obtains from its source.

        @param writer the writer to use to output token information
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public PrintTokenizer(Writer writer, ErrorHandler handler)
    {
        super(handler);
        Assert.require(writer != null);

        _writer = writer;
    }

    /**
        Constructs a PrintTokenizer that outputs information about tokens to
        standard output.

        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public PrintTokenizer(Resources subtokenizerResources,
                          String resourceKeyPrefix, ErrorHandler handler)
    {
        this(Io.out, subtokenizerResources, resourceKeyPrefix, handler);
    }

    /**
        Constructs a PrintTokenizer from the Writer it is to use to output
        information about the tokens it obtains from its source.

        @param writer the writer to use to output token information
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public PrintTokenizer(Writer writer, Resources subtokenizerResources,
                          String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
        Assert.require(writer != null);

        setWriter(writer);
    }


    // Public methods

    /**
        Sets the specified Writer to be the new Writer that this tokenizer
        uses to output token information.

        @param newWriter this tokenizer's new Writer to use to output token
        information
    */
    public void setWriter(Writer newWriter)
    {
        Assert.require(newWriter != null);

        _writer = newWriter;
    }


    // Implemented/overridden methods

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
        Assert.check(canGetNextToken());

        Token result = source().next();
        try
        {
            outputTokenInformation(result, _writer);
        }
        catch (IOException ex)
        {
            String msg = _resources.getMessage(TOKEN_INFO_OUTPUT_FAILED_MSG);
            Io.err.println(msg);
            try
            {
                // Try outputting the token's information to stderr (but give
                // up outputting it if we fail).
                outputTokenInformation(result, Io.err);
            }
            catch (IOException innerEx)
            {
                // Ignore this.
            }
        }

        // If our source has no more tokens then (try to) close our Writer.
        if (source().hasNext() == false)
        {
            Io.tryToClose(_writer);
        }

        return result;
    }


    // Abstract methods

    /**
        Outputs information about the specified token using the specified
        Writer.

        @param tok the token whose information is to be output
        @param writer the Writer that is to be used to output the token's
        information
        @exception IOException if an I/O error occurs outputting the token's
        output
    */
    protected abstract void outputTokenInformation(Token tok, Writer writer)
        throws IOException;
}
