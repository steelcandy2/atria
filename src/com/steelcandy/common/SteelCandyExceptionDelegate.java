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

package com.steelcandy.common;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

import java.io.*;

/**
    A class containing the functionality common to all Steel Candy
    exceptions, and to which the base Steel Candy exception classes'
    methods delegate.

    @author James MacKay
    @see SteelCandyException
    @see SteelCandyRuntimeException
*/
public class SteelCandyExceptionDelegate
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonExceptionResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DEFAULT_EXCEPTION_MESSAGE_MSG =
            "DEFAULT_EXCEPTION_MESSAGE_MSG";


    /** The default exception message. */
    private static final String
        DEFAULT_EXCEPTION_MESSAGE =
            _resources.getMessage(DEFAULT_EXCEPTION_MESSAGE_MSG);


    // Private fields

    /** The Steel Candy exception that delegates to this delegate. */
    private Exception _delegator;


    // Constructors

    /**
        Constructs a SteelCandyExceptionDelegate from the Steel
        Candy exception that is going to be delegating to it.

        @param delegator the Steel Candy exception that is going
        to be delegating to the SteelCandyExceptionDelegate
    */
    public SteelCandyExceptionDelegate(Exception delegator)
    {
        Assert.require(delegator != null);

        _delegator = delegator;
    }


    // Public static methods

    /**
        Returns the specified exception's message: if it doesn't
        have one (that is, if it is null) then the default exception
        message is returned.

        @param ex the exception whose message is to be returned
        @return the exception's message
        @see #defaultMessage
    */
    public static String message(Throwable ex)
    {
        Assert.require(ex != null);

        String result = ex.getMessage();
        if (result == null)
        {
            result = defaultMessage();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the default exception message. It is usually used
        when the exception has a null message.

        @return the default exception message
    */
    public static String defaultMessage()
    {
        return DEFAULT_EXCEPTION_MESSAGE;
    }

    /**
        Returns a string containing the specified exception's stack
        trace.

        @param ex the exception whose stack trace is to be returned
        @return the exception's stack trace (as a string)
    */
    public static String stackTrace(Throwable ex)
    {
        Assert.require(ex != null);

        ByteArrayOutputStream out = new ByteArrayOutputStream(450);
        PrintStream printOut = new PrintStream(out);
        ex.printStackTrace(new PrintStream(out));
        Io.tryToClose(printOut);

        return out.toString();
    }
}
