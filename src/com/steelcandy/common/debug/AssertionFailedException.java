/*
 Copyright (C) 1999-2001 by James MacKay.

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

package com.steelcandy.common.debug;

/**
    The class of exception thrown when an assertion fails.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class AssertionFailedException
    extends DebuggingException
{
    // Private fields

    /** The exception's message. */
    private String _message;


    // Constructors

    /**
        Constructs an AssertionFailedException with no message
        associated with it.
    */
    public AssertionFailedException()
    {
        _message = "";
    }

    /**
        Constructs an AssertionFailedException with the specified
        message associated with it.

        @param theMessage the message that is to be associated with
        the exception
    */
    public AssertionFailedException(String theMessage)
    {
        _message = theMessage;
    }


    // Public methods

    /**
        @return the message describing why this exception was thrown,
        or an empty string if there is no such message associated with
        this exception
    */
    public String getMessage()
    {
        return _message;
    }
}
