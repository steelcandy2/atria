/*
 Copyright (C) 2015 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Parser;

/**
    An ErrorHandler that handles errors by ignoring them, other than
    maintaining counts of the number of errors of each severity level that it
    has handled.

    @author James MacKay
    @see SilentErrorHandler
*/
public class SilentCountingErrorHandler
    extends AbstractCountingErrorHandler
{
    // Constructors

    /**
        Constructs a SilentCountingErrorHandler that hasn't handled any
        errors yet.
    */
    public SilentCountingErrorHandler()
    {
        // empty
    }


    // Error handling methods

    /**
        @see ErrorHandler#handle(PlackInternalError)
    */
    protected void handleError(PlackInternalError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(ReadError)
    */
    protected void handleError(ReadError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(ExternalDataError)
    */
    protected void handleError(ExternalDataError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(TokenizingError, Tokenizer)
    */
    protected void handleError(TokenizingError error, Tokenizer source)
    {
        Assert.require(error != null);
        Assert.require(source != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(ParsingError, Parser)
    */
    protected void handleError(ParsingError error, Parser parser)
    {
        Assert.require(error != null);
        Assert.require(parser != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(DocumentationError)
    */
    protected void handleError(DocumentationError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(ValidationError)
    */
    protected void handleError(ValidationError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(SemanticError)
    */
    protected void handleError(SemanticError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(CodeGenerationError)
    */
    protected void handleError(CodeGenerationError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(CompilerError)
    */
    protected void handleError(CompilerError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }

    /**
        @see ErrorHandler#handle(RuntimeError)
    */
    protected void handleError(RuntimeError error)
    {
        Assert.require(error != null);

        handleUniformly(error);
    }


    // Protected methods

    /**
        Handles the specified error.

        @param error the error to handle
    */
    protected void handleUniformly(PlackError error)
    {
        Assert.require(error != null);

        // empty - since we ignore it (other than counting it)
    }
}
