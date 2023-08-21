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

package com.steelcandy.common.program;

import com.steelcandy.common.debug.Assert;

/**
    The class of exception thrown when a program is improperly used.
    <p>
    As with all ProgramExceptions, this exception's message should
    be displayable to an end user: it should be properly formatted
    for output, and should only contain user-domain terms and information.

    @author James MacKay
    @see ProgramExecutor#usageMessage(Program)
*/
public class BadProgramUsageException
    extends ProgramException
{
    /**
        Constructs a BadProgramUsageException from the program's
        usage message.

        @param usageMessage the program's usage message
        @see ProgramExecutor#usageMessage(Program)
    */
    public BadProgramUsageException(String usageMessage)
    {
        super(usageMessage);
    }

    /**
        Constructs a BadProgramUsageException from the program's
        usage message and a message providing more specific information
        about why the specific arguments were invalid.

        @param usageMessage the program's usage message
        @param msg a message describing specifically why the arguments
        were invalid
        @see ProgramExecutor#usageMessage(Program)
    */
    public BadProgramUsageException(String usageMessage, String msg)
    {
        super(msg + "\n" + usageMessage);
    }

    /**
        Constructs a BadProgramUsageException from the program's
        usage message and the exception that originally signalled the
        bad usage.

        @param usageMessage the program's usage message
        @param ex the exception that originally signalled the bad usage
        @see ProgramExecutor#usageMessage(Program)
    */
    public BadProgramUsageException(String usageMessage, Throwable ex)
    {
        super(usageMessage, ex);
    }
}
