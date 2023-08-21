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
    The class of exception thrown when a precondition fails.

    @author James MacKay
*/
public class PreconditionFailedException
    extends AssertionFailedException
{
    // Constructors

    /**
        Constructs a PostconditionFailedException.
    */
    public PreconditionFailedException()
    {
        super();
    }

    /**
        Constructs a PostconditionFailedException.

        @param theMessage the message that is to be associated with
        the exception
    */
    public PreconditionFailedException(String theMessage)
    {
        super(theMessage);
    }
}
