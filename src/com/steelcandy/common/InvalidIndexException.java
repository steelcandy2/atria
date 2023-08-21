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

/**
    The class of exception thrown when an invalid index is encountered.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class InvalidIndexException
    extends SteelCandyException
{
    // Constructors

    /**
        Constructs an InvalidIndexException.
    */
    public InvalidIndexException()
    {
        // empty
    }

    /**
        Constructs an InvalidIndexException.

        @param msg an message describing why the exception was thrown
    */
    public InvalidIndexException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an InvalidIndexException.

        @param ex the exception from which to construct an exception
    */
    public InvalidIndexException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an exception from a message describing why it
        occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct an
        InvalidIndexException
    */
    public InvalidIndexException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
