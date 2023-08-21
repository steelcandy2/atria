/*
 Copyright (C) 2005-2008 by James MacKay.

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
    An abstract base class for error handlers that handle all errors the same
    way.

    @author James MacKay
*/
public abstract class AbstractUniformErrorHandler
    extends AbstractCountingErrorHandler
{
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


    // Abstract methods

    /**
        Handles the specified error.

        @param error the error to handle
    */
    protected abstract void handleUniformly(PlackError error);
        // Assert.require(error != null);
}
