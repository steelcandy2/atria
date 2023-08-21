/*
 Copyright (C) 2002-2004 by James MacKay.

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

package com.steelcandy.plack.common.compiler;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.base.InternalLanguageProcessingException;
import com.steelcandy.plack.common.base.PlackException;
import com.steelcandy.plack.common.errors.*;

import com.steelcandy.common.Resources;

/**
    An abstract base class for all classes that represent a unit of work to
    be done as part of compiling source code.
    <p>
    Note: the differences between and intentions behind the run(),
    performWork() and perform() methods are as follows:
    <ul>
        <li>run() contains functionality that is common to the performing of
            most if not all compilers' units of work
        <li>performWork() is intended to be overridden - if necessary - by
            (usually language-specific) abstract base classes. It provides a
            place for extra processing to be specified (such as the catching
            of other types of exceptions) without losing any of the
            functionality of the implementation of run()
        <li>perform() is intended to be implemented only by concrete
            subclasses to actually perform their work
    </ul>

    @author James MacKay
    @version $Revision: 1.5 $
*/
public abstract class AbstractCompilerWork
    implements Runnable, ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonCompilerResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INTERNAL_ERROR_MSG = "INTERNAL_ERROR_MSG";
    private static final String
        UNEXPECTED_EXCEPTION_MSG = "UNEXPECTED_EXCEPTION_MSG";


    // Private fields

    /**
        The error handler to which all errors are to ultimately be reported.
    */
    private ErrorHandler _parentErrorHandler;


    // Constructors

    /**
        Constructs an AbstractCompilerWork object.

        @param handler the error handler to which all errors that occur while
        performing this work are to ultimately be reported
    */
    public AbstractCompilerWork(ErrorHandler handler)
    {
        Assert.require(handler != null);

        _parentErrorHandler = handler;
    }


    // Public methods

    /**
        @see Runnable#run
    */
    public void run()
    {
        RecordingErrorHandler handler = new RecordingErrorHandler();
        try
        {
            performWork(handler);
            performSucceeded();
        }
        catch (FatalErrorException ex)
        {
            performFailed();
            handleFatalError(ex, handler);
        }
        catch (InternalLanguageProcessingException ex)
        {
            performFailed();
            handleInternalError(ex, handler);
        }
        catch (Throwable ex)
        {
            performFailed();
            handleUnexpectedException(ex, handler);
        }
        finally
        {
            // Transfer all errors to the parent error handler.
            _parentErrorHandler.
                transferErrorsAtomically(handler);
        }
    }


    // Protected methods

    /**
        Performs this unit of work.
        <p>
        Note: the error handler passed to this method has not been used yet,
        and is only used to handle this unit of work's errors.
        <p>
        This implementation just calls perform(), but subclasses can override
        it to do any extra processing (such as catching other types of
        exceptions) while still retaining this class' implementation of
        run().

        @param handler the error handler to use to handle any errors
    */
    protected void performWork(ErrorHandler handler)
    {
        Assert.require(handler != null);
        Assert.require(handler.errorCount() == 0);

        perform(handler);
    }

    /**
        Does anything required after this work was performed successfully.
        <p>
        This implementation does nothing.

        @see #performFailed
    */
    protected void performSucceeded()
    {
        // empty
    }

    /**
        Does anything required after performing this work failed to complete
        successfully. Note that whatever caused performing this work to fail
        will be reported elsewhere, and so should not be reported by this
        method.
        <p>
        This implementation does nothing.

        @see #performSucceeded
    */
    protected void performFailed()
    {
        // empty
    }


    /**
        Handles a fatal error occurring while performing this unit of work.
        <p>
        Note: the fatal error has already been reported, so it should not be
        reported again here.

        @param ex the exception that signalled the fatal error
        @param handler the handler to use to handle the error (if an error
        handler is used at all)
    */
    protected void handleFatalError(FatalErrorException ex,
                                    ErrorHandler handler)
    {
        Assert.require(ex != null);
        Assert.require(handler != null);

        // empty
    }

    /**
        Handles an internal error occurring in a language processor while
        performing this unit of work.

        @param ex the exception that signalled the internal error
        @param handler the handler to use to handle the error (if an error
        handler is used at all)
    */
    protected void
        handleInternalError(InternalLanguageProcessingException ex,
                            ErrorHandler handler)
    {
        Assert.require(ex != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INTERNAL_ERROR_MSG,
                ex.getLocalizedMessage(),
                ex.getClass().getName(),
                PlackException.stackTrace(ex));
        handler.handle(new PlackInternalError(FATAL_ERROR_LEVEL,
            msg, null, null, ex.getStackTrace()));
    }

    /**
        Handles the specified unexpected exception being thrown while
        performing this unit of work.

        @param ex the unexpected exception
        @param handler the handler to use to handle the exception (if an
        error handler is used at all)
    */
    protected void handleUnexpectedException(Throwable ex,
                                             ErrorHandler handler)
    {
        Assert.require(ex != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(UNEXPECTED_EXCEPTION_MSG,
                ex.getLocalizedMessage(),
                ex.getClass().getName(),
                PlackException.stackTrace(ex));
        handler.handle(new PlackInternalError(FATAL_ERROR_LEVEL,
            msg, null, null, ex.getStackTrace()));
    }


    // Abstract methods

    /**
        Performs this unit of work.
        <p>
        Note: the error handler passed to this method has not been used yet,
        and is only used to handle this unit of work's errors.

        @param handler the error handler to use to handle any errors
    */
    protected abstract void perform(ErrorHandler handler);
        // Assert.require(handler != null);
        // Assert.require(handler.errorCount() == 0);
}
