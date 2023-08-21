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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.plack.common.base.InternalLanguageProcessingException;

/**
    The class of exception thrown when an internal parser error occurs: that
    is, an error that should not occur if the parser is implemented
    correctly. It does <em>not</em> indicate an error in the source code
    being parsed.

    @author James MacKay
*/
public class ParsingException
    extends InternalLanguageProcessingException
{
    // Constructors

    /**
        Constructs a ParsingException.
    */
    public ParsingException()
    {
        // empty
    }

    /**
        Constructs a ParsingException.

        @param msg a message describing why the exception was thrown
    */
    public ParsingException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a ParsingException.

        @param ex the exception from which to construct the exception
    */
    public ParsingException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs a ParsingException from a message describing why it
        occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the ParsingException
    */
    public ParsingException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
