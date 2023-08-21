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

package com.steelcandy.plack.common.base;

import com.steelcandy.common.SteelCandyRuntimeException;

/**
    The base class for all Plack runtime exceptions.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class PlackRuntimeException
    extends SteelCandyRuntimeException
{
    // Constructors

    /**
        Default constructor.
    */
    public PlackRuntimeException()
    {
        super();
    }

    /**
        Constructs an exception from a message describing why it
        occurred.

        @param msg the message describing why the exception occurred
    */
    public PlackRuntimeException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an exception from another exception or error.

        @param ex the exception or error from which to construct
        an exception
    */
    public PlackRuntimeException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an exception from a message describing why it
        occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct a
        PlackException
    */
    public PlackRuntimeException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
