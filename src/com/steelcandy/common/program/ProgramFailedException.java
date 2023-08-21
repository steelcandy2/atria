/*
 Copyright (C) 2002 by James MacKay.

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
    The class of exception thrown when a program fails to complete
    successfully.
    <p>
    As with all ProgramExceptions, this exception's message should
    be displayable to an end user: it should be properly formatted
    for output, and should only contain user-domain terms and information.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class ProgramFailedException
    extends ProgramException
{
    /**
        Constructs ProgramFailedException.

        @param msg a (properly formatted) message describing (in user's
        terms) why the program failed
    */
    public ProgramFailedException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a ProgramFailedException.

        @param msg a (properly formatted) message describing (in user's
        terms) why the program failed
        @param ex the exception that caused the program to fail
    */
    public ProgramFailedException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
