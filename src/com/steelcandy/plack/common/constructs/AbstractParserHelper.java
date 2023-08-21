/*
 Copyright (C) 2002-2005 by James MacKay.

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
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;

import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.tokens.Tokenizer;

/**
    An abstract base class for classes that help parse constructs.
    <p>
    Parser helpers are usually used in place of a Parser subclass when a
    parser already subclasses a language-specific Parser subclass (as most
    do) and thus (since Java doesn't support multiple inheritance) cannot
    subclass another Parser subclass. Instead they have to delegate to a
    parser helper instance.

    @author James MacKay
*/
public abstract class AbstractParserHelper
    implements ErrorSeverityLevels
{
    // Utility methods

    /**
        Handles the specified error using the specified error handler.
        <p>
        Note: this method is not named handleError() since naming it that
        seems to prevent instances of this class that are inner classes
        of a Parser from seeing and calling the parser's handleError()
        method.

        @param level the error's severity level
        @param description the error's description
        @param loc the location in the source code where the error occurred
        @param p the parser that we're helping
        @param t the tokenizer in whose tokens the error was found
        @param handler the error handler to use to handle the error
    */
    protected void
        handleParsingError(int level, String description,
                           SourceLocation loc, Parser p,
                           Tokenizer t, ErrorHandler handler)
    {
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        MinimalAbstractParser.
            handleError(level, description, loc, p, t, handler);
    }

    /**
        @see MinimalAbstractParser#finishConstruct(Construct, Tokenizer, SubconstructParsingData, SourceLocation, ErrorHandler)
    */
    protected void finishConstruct(Construct c, Tokenizer t,
        SubconstructParsingData data, SourceLocation loc,
        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(t != null);
        Assert.require(data != null);
        // 'loc' may be null
        Assert.require(handler != null);

        MinimalAbstractParser.finishParsedConstruct(c, t, data, loc, handler);
    }
}
