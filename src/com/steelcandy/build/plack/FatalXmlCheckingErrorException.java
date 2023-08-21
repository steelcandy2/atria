/*
 Copyright (C) 2003 by James MacKay.

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

package com.steelcandy.build.plack;

/**
    The class of exception thrown after a fatal error in an XML document
    is discovered and reported. It is a singleton since it is intended
    only to signal that a fatal error occurred, and not to transmit
    information about the fatal error itself.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class FatalXmlCheckingErrorException
    extends RuntimeException
{
    // Constants

    /** The sole instance of this class. */
    private static final FatalXmlCheckingErrorException
        _instance = new FatalXmlCheckingErrorException();


    // Constructors

    /**
        @return the sole instance of this class
    */
    public static FatalXmlCheckingErrorException instance()
    {
        return _instance;
    }

    /**
        Constructs a FatalXmlCheckingErrorException.
    */
    private FatalXmlCheckingErrorException()
    {
        // empty
    }
}
