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

package com.steelcandy.common.xml;

import com.steelcandy.common.SteelCandyException;

/**
    The base class for all XML exceptions.

    @author James MacKay
*/
public class XmlException
    extends SteelCandyException
{
    // Constructors

    /**
        Default constructor.
    */
    public XmlException()
    {
        super();
    }

    /**
        Constructs an exception from a message describing why it
        occurred.

        @param msg the message describing why the exception occurred
    */
    public XmlException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an exception from another exception or error.

        @param ex the exception or error from which to construct
        an exception
    */
    public XmlException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an exception from a message describing why it
        occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct an exception
    */
    public XmlException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
