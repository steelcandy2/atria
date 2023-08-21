/*
 Copyright (C) 2001 by James MacKay.

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

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

/**
    Class containing various utility methods and constants that
    don't fit anywhere else.

    @author James MacKay
*/
public class Utilities
{
    // Constants

    /** The number of bits in a Java int. */
    public static final int BITS_IN_INT = 32;

    /** The number of bits in a Java long. */
    public static final int BITS_IN_LONG = 64;


    // Constructors

    /**
        This constructor is private to prevent this class from
        being instantiated.
    */
    private Utilities()
    {
        // empty
    }


    // Main method

    /**
        This main method is used to test this class' methods.

        @param args the command line arguments. They are currently ignored
    */
    public static void main(String[] args)
    {
        out("Number of bits in an int: " + BITS_IN_INT);
        out("Number of bits in a long: " + BITS_IN_LONG);

        System.exit(0);
    }

    /**
        Outputs the specified string, followed by a newline.
        <p>
        This method is only used by the <code>main()</code> method to
        output information as part of testing this class.

        @param msg the string to output
    */
    private static void out(String msg)
    {
        Io.writeLine(msg);
    }
}
