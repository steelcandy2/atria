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

package com.steelcandy.plack.common.compiler;

/**
    The class of exception thrown when a set of compiler arguments is
    invalid. (It could be that a single argument is invalid, or that two or
    more arguments used together are invalid.)

    @author James MacKay
*/
public class InvalidCompilerArgumentsException
    extends CompilerException
{
    // Constructors

    /**
        Constructs an InvalidCompilerArgumentsException.
    */
    public InvalidCompilerArgumentsException()
    {
        // empty
    }

    /**
        Constructs an InvalidCompilerArgumentsException.

        @param msg a message describing why the exception was thrown
    */
    public InvalidCompilerArgumentsException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an InvalidCompilerArgumentsException.

        @param ex the exception from which to construct the exception
    */
    public InvalidCompilerArgumentsException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an InvalidCompilerArgumentsException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the exception
    */
    public InvalidCompilerArgumentsException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
