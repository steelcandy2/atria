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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Parser;

/**
    The interface implemented by all classes whose instances handle the
    various types of errors that occur while processing source code.

    @author James MacKay
*/
public interface ErrorHandler
{
    // Error handling methods

    /**
        Handles the specified internal error.

        @param error the error to handle
    */
    public void handle(PlackInternalError error);
        // Assert.require(error != null);

    /**
        Handles the specified read error.

        @param error the error to handle
    */
    public void handle(ReadError error);
        // Assert.require(error != null);

    /**
        Handles the specified external data error.

        @param error the error to handle
    */
    public void handle(ExternalDataError error);
        // Assert.require(error != null);

    /**
        Handles the specified tokenizing error.

        @param error the error to handle
        @param source the tokenizer that found the error
    */
    public void handle(TokenizingError error, Tokenizer source);
        // Assert.require(error != null);
        // Assert.require(source != null);

    /**
        Handles the specified parsing error.

        @param error the error to handle
        @param parser the parser that found the error
    */
    public void handle(ParsingError error, Parser parser);
        // Assert.require(error != null);
        // Assert.require(parser != null);

    /**
        Handles the specified documentation error.

        @param error the error to handle
    */
    public void handle(DocumentationError error);
        // Assert.require(error != null);

    /**
        Handles the specified validation error.

        @param error the error to handle
    */
    public void handle(ValidationError error);
        // Assert.require(error != null);

    /**
        Handles the specified semantic error.

        @param error the error to handle
    */
    public void handle(SemanticError error);
        // Assert.require(error != null);

    /**
        Handles the specified code generation error.

        @param error the error to handle
    */
    public void handle(CodeGenerationError error);
        // Assert.require(error != null);

    /**
        Handles the specified compiler error.

        @param error the error to handle
    */
    public void handle(CompilerError error);
        // Assert.require(error != null);

    /**
        Handles the specified runtime error.

        @param error the error to handle
    */
    public void handle(RuntimeError error);
        // Assert.require(error != null);


    // Public methods

    /**
        Transfers all of the errors from the specified recording error
        handler to this error handler.
        <p>
        Note: since the handling of (especially fatal) errors can throw
        exceptions, as can other processing, it is usually a good idea to put
        calls to this method in a finally block to ensure that the errors are
        always transferred.

        @param recorder the recording error handler to transfer errors from
        @see RecordingErrorHandler#transferErrorsTo(ErrorHandler)
    */
    public void transferErrors(RecordingErrorHandler recorder);
        // Assert.require(recorder != null);

    /**
        Transfers all of the errors from the specified recording error
        handler to this error handler, with the guarantee that they will all
        be added together (i.e. consecutively).
        <p>
        Note: since the handling of (especially fatal) errors can throw
        exceptions, as can other processing, it is usually a good idea to put
        calls to this method in a finally block to ensure that the errors are
        always transferred.

        @param recorder the recording error handler to transfer errors from
        @see RecordingErrorHandler#transferErrorsTo(ErrorHandler)
    */
    public void transferErrorsAtomically(RecordingErrorHandler recorder);
        // Assert.require(recorder != null);


    // Error count methods

    /**
        @return the number of errors handled by this error handler so far,
        regardless of the errors' severity levels
    */
    public int errorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of fatal errors handled by this error handler so
        far
    */
    public int fatalErrorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of fatal and worse errors handled by this error
        handler so far (though at least currently there's nothing worse than
        a fatal error)
    */
    public int fatalErrorAndAboveCount();
        // Assert.ensure(result >= fatalErrorCount());

    /**
        @return the number of non-fatal errors handled by this error handler
        so far
    */
    public int nonFatalErrorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of non-fatal and worse errors handled by this
        error handler so far
    */
    public int nonFatalAndAboveErrorCount();
        // Assert.ensure(result >= nonFatalErrorCount());

    /**
        @return the number of warning 'errors' handled by this error handler
        so far
    */
    public int warningErrorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of warning and worse errors handled by this error
        handler so far
    */
    public int warningAndAboveErrorCount();
        // Assert.ensure(result >= warningErrorCount());

    /**
        @return the number of information-level 'errors' handled by this
        error handler so far
    */
    public int informationErrorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of information-level and worse errors handled by
        this error handler so far
    */
    public int informationAndAboveErrorCount();
        // Assert.ensure(result >= informationErrorCount());

    /**
        @return the number of debug-level 'errors' handled by this error
        handler so far
    */
    public int debugErrorCount();
        // Assert.ensure(result >= 0);

    /**
        @return the number of debug-level and worse errors handled by this
        error handler so far
    */
    public int debugErrorAndAboveCount();
        // Assert.ensure(result >= debugErrorCount());
}
