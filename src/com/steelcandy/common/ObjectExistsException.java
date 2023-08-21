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
    The class of exception thrown when trying to replace an object
    that already exists when that is illegal.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class ObjectExistsException
    extends SteelCandyException
{
    /**
        Default constructor.
    */
    public ObjectExistsException()
    {
        // empty
    }

    /**
        Constructs an ObjectExistsException from a message describing
        why the exception was thrown.

        @param msg the message describing why the exception was thrown
    */
    public ObjectExistsException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an ObjectExistsException from another exception.

        @param ex the exception from which to construct an ObjectExistsException
    */
    public ObjectExistsException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an ObjectExistsException from a message describing why
        the exception was thrown, and from another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct an
        ObjectExistsException
    */
    public ObjectExistsException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
