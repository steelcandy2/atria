/*
 Copyright (C) 2002-2005 by James MacKay.

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

package com.steelcandy.build;

/**
    The class of exception thrown when generating source code fails.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class SourceCodeGenerationException
    extends Exception
{
    // Constructors

    /**
        Constructs an exception from a message describing why it
        occurred.

        @param msg the message describing why the exception occurred
    */
    public SourceCodeGenerationException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an exception from another exception.

        @param ex the exception that this exception wraps
    */
    public SourceCodeGenerationException(Exception ex)
    {
        super(ex);
    }
}
