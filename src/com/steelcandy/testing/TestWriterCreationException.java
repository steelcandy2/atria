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

package com.steelcandy.testing;

import com.steelcandy.common.SteelCandyException;

/**
    The type of exception thrown when a TestWriter cannot be
    created.

    @author James MacKay
*/
public class TestWriterCreationException
    extends SteelCandyException
{
    // Constructors

    /**
        Default constructor.
    */
    public TestWriterCreationException()
    {
        super();
    }

    /**
        Constructs an exception from a message describing why it occurred.

        @param msg the message describing why the exception occurred
    */
    public TestWriterCreationException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an exception from another exception or error.

        @param ex the exception or error from which to construct an exception
    */
    public TestWriterCreationException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an exception from a message describing why it occurred
        and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the exception
    */
    public TestWriterCreationException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
