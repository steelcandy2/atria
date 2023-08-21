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

package com.steelcandy.common;

/**
    The class of exception thrown when a method is called on an
    instance of a class that does not support/implement the operation
    represented by the method.
    <p>
    This class exists mainly to allow UnsupportedOperationExceptions
    to be created without having to specify a message.

    @author James MacKay
*/
public class UnsupportedMethodException
    extends UnsupportedOperationException
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonExceptionResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        UNSUPPORTED_METHOD_MSG =
            "UNSUPPORTED_METHOD_MSG";


    // Constructors

    /**
        Constructs an UnsupportedMethodException from the class
        containing the unsupported method and the signature of the
        unsupported method.

        @param c the class that does not support the method
        @param methodName the signature of the unsupported method
    */
    public UnsupportedMethodException(Class c, String methodName)
    {
        super(_resources.getMessage(UNSUPPORTED_METHOD_MSG,
                                    c.getName(), methodName));
    }
}
