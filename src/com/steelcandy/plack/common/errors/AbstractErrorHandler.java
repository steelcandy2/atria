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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Parser;

/**
    An abstract base class for ErrorHandlers.
    <p>
    Note: the handle() methods are final in this class in order to prevent
    them from being overridden accidentally (since usually the handleError()
    methods should be overridden instead).

    @author James MacKay
    @version $Revision: 1.8 $
*/
public abstract class AbstractErrorHandler
    implements ErrorHandler, ErrorSeverityLevels
{
    // Error handling methods

    /**
        @see ErrorHandler#handle(PlackInternalError)
    */
    public final void handle(PlackInternalError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(ReadError)
    */
    public final void handle(ReadError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(ExternalDataError)
    */
    public final void handle(ExternalDataError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(TokenizingError, Tokenizer)
    */
    public final void handle(TokenizingError error, Tokenizer source)
    {
        Assert.require(error != null);
        Assert.require(source != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error, source);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(ParsingError, Parser)
    */
    public final void handle(ParsingError error, Parser parser)
    {
        Assert.require(error != null);
        Assert.require(parser != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error, parser);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(DocumentationError)
    */
    public final void handle(DocumentationError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(ValidationError)
    */
    public final void handle(ValidationError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(SemanticError)
    */
    public final void handle(SemanticError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(CodeGenerationError)
    */
    public final void handle(CodeGenerationError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(CompilerError)
    */
    public final void handle(CompilerError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }

    /**
        @see ErrorHandler#handle(RuntimeError)
    */
    public final void handle(RuntimeError error)
    {
        Assert.require(error != null);

        if (isDuplicate(error) == false)
        {
            beforeHandlingError(error);
            handleError(error);
            afterHandlingError(error);
        }
    }


    // Public methods

    /**
        @see ErrorHandler#transferErrors(RecordingErrorHandler)
    */
    public void transferErrors(RecordingErrorHandler recorder)
    {
        Assert.require(recorder != null);

        recorder.transferErrorsTo(this);
    }

    /**
        @see ErrorHandler#transferErrorsAtomically(RecordingErrorHandler)
    */
    public synchronized void
        transferErrorsAtomically(RecordingErrorHandler recorder)
    {
        Assert.require(recorder != null);

        recorder.transferErrorsTo(this);
    }


    /**
        @see ErrorHandler#errorCount
    */
    public int errorCount()
    {
        // Note: currently debug is the lowest error severity level.
        // Assert.ensure(result >= 0);
        return debugErrorAndAboveCount();
    }

    /**
        @see ErrorHandler#fatalErrorAndAboveCount
    */
    public int fatalErrorAndAboveCount()
    {
        // Note: currently there's nothing worse than a fatal error.
        // Assert.ensure(result >= fatalErrorCount());
        return fatalErrorCount();
    }

    /**
        @see ErrorHandler#nonFatalAndAboveErrorCount
    */
    public int nonFatalAndAboveErrorCount()
    {
        // Assert.ensure(result >= nonFatalErrorCount());
        return nonFatalErrorCount() + fatalErrorAndAboveCount();
    }

    /**
        @see ErrorHandler#warningAndAboveErrorCount
    */
    public int warningAndAboveErrorCount()
    {
        // Assert.ensure(result >= warningErrorCount());
        return warningErrorCount() + nonFatalAndAboveErrorCount();
    }

    /**
        @see ErrorHandler#informationAndAboveErrorCount
    */
    public int informationAndAboveErrorCount()
    {
        // Assert.ensure(result >= informationErrorCount());
        return informationErrorCount() + warningAndAboveErrorCount();
    }

    /**
        @see ErrorHandler#debugErrorAndAboveCount
    */
    public int debugErrorAndAboveCount()
    {
        // Assert.ensure(result >= debugErrorCount());
        return debugErrorCount() + informationAndAboveErrorCount();
    }


    // Protected methods

    /**
        The method called before the specified error is to be handled
        by the appropriate version of handleError().
        <p>
        This implementation does nothing.

        @param error the error that is about to be handled
    */
    protected void beforeHandlingError(PlackError error)
    {
        Assert.require(error != null);

        // empty
    }

    /**
        The method called after the specified error has been handled
        by the appropriate version of handleError().
        <p>
        This implementation does nothing.

        @param error the error that was just handled
    */
    protected void afterHandlingError(PlackError error)
    {
        Assert.require(error != null);

        // empty
    }

    /**
        This implementation doesn't consider any errors to be duplicates.

        @param error an error that this handler is about to handle
        @return true iff this handler considers 'error' to be a duplicate of
        an error that this handler has already handled
    */
    protected boolean isDuplicate(PlackError error)
    {
        return false;
    }

    /**
        Note: the error level is usually not shown in error messages that
        report errors with the default (severity) level.

        @return the default error level
    */
    protected int defaultErrorLevel()
    {
        return NON_FATAL_ERROR_LEVEL;
    }

    /**
        Handles the specified error iff it is a fatal error.

        @param error the error to handle iff it is fatal
        @exception FatalErrorException thrown iff 'error' is a fatal error
    */
    protected void handleFatalError(PlackError error)
        throws FatalErrorException
    {
        if (error.isFatal())
        {
            throw FatalErrorException.instance();
        }
    }


    // Abstract error handling methods

    /**
        @see ErrorHandler#handle(PlackInternalError)
    */
    protected abstract void handleError(PlackInternalError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(ReadError)
    */
    protected abstract void handleError(ReadError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(ExternalDataError)
    */
    protected abstract void handleError(ExternalDataError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(TokenizingError, Tokenizer)
    */
    protected abstract void
        handleError(TokenizingError error, Tokenizer source);
        // Assert.require(error != null);
        // Assert.require(source != null);

    /**
        @see ErrorHandler#handle(ParsingError, Parser)
    */
    protected abstract void handleError(ParsingError error, Parser parser);
        // Assert.require(error != null);
        // Assert.require(parser != null);

    /**
        @see ErrorHandler#handle(DocumentationError)
    */
    protected abstract void handleError(DocumentationError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(ValidationError)
    */
    protected abstract void handleError(ValidationError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(SemanticError)
    */
    protected abstract void handleError(SemanticError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(CodeGenerationError)
    */
    protected abstract void handleError(CodeGenerationError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(CompilerError)
    */
    protected abstract void handleError(CompilerError error);
        // Assert.require(error != null);

    /**
        @see ErrorHandler#handle(RuntimeError)
    */
    protected abstract void handleError(RuntimeError error);
        // Assert.require(error != null);
}
