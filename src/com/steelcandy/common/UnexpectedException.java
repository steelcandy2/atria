/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common;

import com.steelcandy.common.text.TextUtilities;

/**
    The class of exception thrown when an exception is not expected to
    be able to be thrown, but is.
    <p>
    This exception is usually used when a method being called declares
    that it throws a given exception, but because of circumstances known
    to the programmer the exception should not have been able to occur.
    <p>
    This class' <code>printStackTrace()</code> methods print the stack
    trace of this exception rather than the unexpected exception since
    presumably interest lies in discovering which 'impossible' situation
    occurred that allowed the exception to be thrown, and because the
    unexpected exception can be obtained from this one and its stack
    trace can be obtained directly from it.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class UnexpectedException
    extends SteelCandyRuntimeException
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonExceptionResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        UNEXPECTED_EXCEPTION_MSG =
            "UNEXPECTED_EXCEPTION_MSG";


    // Private fields

    /**  The unexpected exception. */
    private Throwable _exception;


    // Constructors

    /**
        Constructs an UnexpectedException from the unexpected exception
        and a message describing why the exception was thrown.

        @param msg a message describing why the exception was thrown
        @param ex the exception that unexpectedly thrown
    */
    public UnexpectedException(String msg, Throwable ex)
    {
        super(constructMessage(ex, msg));
        _exception = ex;
    }


    // Public methods

    /**
        @return the unexpected exception
    */
    public Throwable getUnexpectedException()
    {
        return _exception;
    }


    // Private methods

    /**
        Constructs and returns an exception message from the unexpected
        exception and a description of why it was thrown.

        @param ex the unexpected exception
        @param msg the message describing why the unexpected exception
        was thrown
        @return the exception message constructed from the given information
    */
    private static String constructMessage(Throwable ex, String msg)
    {
        Object[] args = new Object[]
        {
            ex.getClass().getName(),
            msg,
            TextUtilities.LINE_SEPARATOR
        };
        return _resources.getMessage(UNEXPECTED_EXCEPTION_MSG, args);
    }
}
