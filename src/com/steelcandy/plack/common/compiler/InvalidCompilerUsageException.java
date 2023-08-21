/*
 Copyright (C) 2005 by James MacKay.

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
    The class of exception thrown when a compiler is used improperly: that
    is, not in accordance with its usage description.
    <p>
    Note: this class differs from InvalidCompilerArgumentsException in that
    that class of exception may be thrown for invalid arguments that are not
    on the command line, and hence shouldn't result in a compiler usage
    message.

    @author James MacKay
*/
public class InvalidCompilerUsageException
    extends CompilerException
{
    // Constructors

    /**
        Constructs an InvalidCompilerUsageException.
    */
    public InvalidCompilerUsageException()
    {
        // empty
    }

    /**
        Constructs an InvalidCompilerUsageException.

        @param msg a message describing why the exception was thrown
    */
    public InvalidCompilerUsageException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an InvalidCompilerUsageException.

        @param ex the exception from which to construct the exception
    */
    public InvalidCompilerUsageException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an InvalidCompilerUsageException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the exception
    */
    public InvalidCompilerUsageException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
