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

package com.steelcandy.plack.common.compiler;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.base.PlackException;

/**
    The base class for all types of exceptions thrown by a compiler.
    <p>
    Note: instances of this class should only be thrown and caught internally
    in a compiler, since compilers are generally at the 'top' of a system and
    hence shouldn't throw exceptions.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class CompilerException
    extends PlackException
{
    // Constructors

    /**
        Constructs a CompilerException.
    */
    public CompilerException()
    {
        // empty
    }

    /**
        Constructs a CompilerException.

        @param msg a message describing why the exception was thrown
    */
    public CompilerException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a CompilerException.

        @param ex the exception from which to construct the exception
    */
    public CompilerException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs a CompilerException from a message describing why it
        occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the exception
    */
    public CompilerException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
