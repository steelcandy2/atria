/*
 Copyright (C) 2003-2004 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.plack.common.base.PlackRuntimeException;

/**
    The class of exception thrown when an attempt is made to access
    a Type that is missing.
    <p>
    A type may be missing because it hasn't been set yet, or because
    it will never be set: for example, because it is part of a
    typeless language.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class MissingTypeException
    extends PlackRuntimeException
{
    // Constructors

    /**
        Constructs a MissingTypeException.
    */
    public MissingTypeException()
    {
        // empty
    }

    /**
        Constructs a MissingTypeException.

        @param msg a message describing why the exception was thrown
    */
    public MissingTypeException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a MissingTypeException.

        @param ex the exception from which to construct the exception
    */
    public MissingTypeException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs a MissingTypeException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was
        thrown
        @param ex the exception from which to construct the
        exception
    */
    public MissingTypeException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
