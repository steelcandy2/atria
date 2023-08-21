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

package com.steelcandy.plack.common.base;

/**
    The base class for exceptions that indicate an internal error
    in a component of a language processing tool. It does <em>not</em>
    indicate an error in the source code being processed.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class InternalLanguageProcessingException
    extends PlackRuntimeException
{
    // Constructors

    /**
        Constructs an InternalLanguageProcessingException.
    */
    public InternalLanguageProcessingException()
    {
        // empty
    }

    /**
        Constructs an InternalLanguageProcessingException.

        @param msg a message describing why the exception was thrown
    */
    public InternalLanguageProcessingException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an InternalLanguageProcessingException.

        @param ex the exception from which to construct the exception
    */
    public InternalLanguageProcessingException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an InternalLanguageProcessingException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was
        thrown
        @param ex the exception from which to construct the
        InternalLanguageProcessingException
    */
    public InternalLanguageProcessingException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
