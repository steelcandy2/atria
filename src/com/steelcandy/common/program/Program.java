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
    The interface implemented by classes that can be run as standalone
    programs.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public interface Program
{
    // Constants

    /** The sole ProgramExecutor instance. */
    public static final ProgramExecutor
        EXECUTOR = ProgramExecutor.instance();


    // Public methods

    /**
        Executes this program with the specified command line arguments.
        <p>
        This method should throw a BadProgramUsageException if the arguments
        are invalid, and a ProgramFailedException if the arguments are valid
        but the program's execution failed for some other reason.

        @param args the command line arguments
        @exception ProgramException thrown if the program did not complete
        successfully
    */
    public void execute(String[] args)
        throws ProgramException;
        // Assert.require(args != null);

    /**
        @return a summary of this program's arguments, as they should appear
        on the first line of the program's usage message
    */
    public String argumentsSummary();
        // Assert.ensure(result != null);

    /**
        @return a description of how to use this program (which consists of
        all of the text of the program's usage message except its first line)
    */
    public String usageDescription();
        // Assert.ensure(result != null);
}
