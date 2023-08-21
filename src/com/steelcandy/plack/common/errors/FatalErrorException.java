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

package com.steelcandy.plack.common.errors;

import com.steelcandy.plack.common.base.PlackRuntimeException;

/**
    The singleton exception thrown when a fatal error occurs while processing
    source code. Note that the fatal error is due to a user (e.g. programmer)
    error: it is <em>not</em> due to an error in the compiler or language
    processor.
    <p>
    This class of exception is thrown as a simple way to terminate the
    processing of a piece of source code: it is not intended to be used to
    transmit information about the fatal error. Instead, the fatal error
    should be handled using an ErrorHandler and then the ErrorHandler can
    throw the instance of this exception after it has reported it.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class FatalErrorException
    extends PlackRuntimeException
{
    // Constants

    /** The sole instance of this class. */
    private static final FatalErrorException
        _instance = new FatalErrorException();


    // Constructors

    /**
        @return the sole instance of this class
    */
    public static FatalErrorException instance()
    {
        return _instance;
    }

    /**
        Constructs a FatalErrorException.
    */
    private FatalErrorException()
    {
        // empty
    }
}
