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

import com.steelcandy.plack.common.source.SourcePosition;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import java.io.Writer;

/**
    The abstract base class for PrintTokenizers used to print information
    about Atria language tokens.
    <p>
    Subclasses just have to implement the outputTokenInformation() method.

    @author James MacKay
    @version $Revision: 1.10 $
*/
public abstract class AtriaPrintTokenizer
    extends PrintTokenizer
{
    // Constants

    /** The single AtriaTokenManager instance. */
    protected static final AtriaTokenManager
        MANAGER = AtriaTokenManager.instance();


    // Constructors

    /**
        Constructs an AtriaPrintTokenizer that outputs information about
        tokens to standard output.

        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public AtriaPrintTokenizer(ErrorHandler handler)
    {
        this(Io.out, handler);
    }

    /**
        Constructs an AtriaPrintTokenizer from the Writer it is to use to
        output information about the tokens it obtains from its source.

        @param writer the writer to use to output token information
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public AtriaPrintTokenizer(Writer writer, ErrorHandler handler)
    {
        super(writer, handler);
    }

    /**
        Constructs an AtriaPrintTokenizer that outputs information about
        tokens to stardard output.

        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AtriaPrintTokenizer(Resources subtokenizerResources,
                              String resourceKeyPrefix, ErrorHandler handler)
    {
        this(Io.out, subtokenizerResources, resourceKeyPrefix, handler);
    }

    /**
        Constructs an AtriaPrintTokenizer from the Writer it is to use to
        output information about the tokens it obtains from its source.

        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AtriaPrintTokenizer(Writer writer,
                Resources subtokenizerResources, String resourceKeyPrefix,
                ErrorHandler handler)
    {
        super(writer, subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Utility methods

    /**
        Returns a string representation of the specified token's ID. The ID's
        uppercase name will be returned.

        @param tok the token whose ID is to be represented in the returned
        string
        @return the string representation of the token's ID
    */
    protected String idToString(Token tok)
    {
        return MANAGER.idToConstantName(tok.id());
    }

    /**
        Returns a string representation of the specified token's location in
        the source code.

        @param tok the token whose location is to be represented in the
        returned string
        @return the string representation of the token's location
    */
    protected String locationToString(Token tok)
    {
        return tok.location().toString();
    }

    /**
        Returns a string representation of the flags that the specified
        token has set, which will be an empty string iff the token has
        no flags set. The string will contain the flags' uppercase names.

        @param tok the token whose flags are to be represented in the
        returned string
        @return a string representation of the flags that the token has set
    */
    protected String flagsToString(Token tok)
    {
        return MANAGER.flagsToConstantNames(tok);
    }

    /**
        Returns a string representation of the specified token's values,
        which will be an empty string iff the token has no values.

        @param tok the token whose ID is to be represented in the
        returned string
        @return the string representation of the token's ID
    */
    protected String valuesToString(Token tok)
    {
        return tok.valuesToString();
    }
}
