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

package com.steelcandy.common.io;

import com.steelcandy.common.debug.Assert;

import java.io.*;

/**
    A singleton Writer that discards all of the information it
    it is to output.

    @author James MacKay
*/
public class DiscardWriter
    extends Writer
{
    // Constants

    /** The single instance of this class. */
    private static final DiscardWriter _instance = new DiscardWriter();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static DiscardWriter instance()
    {
        return _instance;
    }

    /**
        Default constructor. It should only be called to construct
        the single instance of this class and by subclasses'
        constructors.
    */
    protected DiscardWriter()
    {
        // empty
    }


    // Implemented Writer methods

    /**
        @see Writer#write(char[], int, int)
    */
    public void write(char[] cbuf, int offset, int length)
    {
        // Do nothing, effectively discarding the data to be written.
    }

    /**
        @see Writer#flush
    */
    public void flush()
    {
        // empty
    }

    /**
        @see Writer#close
    */
    public void close()
    {
        // empty
    }


    // Overridden Writer methods

    // These methods are just overridden for efficiency: they're
    // not abstract in Writer.

    /**
        @see Writer#write(int)
    */
    public void write(int ch)
    {
        // Do nothing, effectively discarding the data to be written.
    }

    /**
        @see Writer#write(char[])
    */
    public void write(char[] cbuf)
    {
        // Do nothing, effectively discarding the data to be written.
    }

    /**
        @see Writer#write(String)
    */
    public void write(String str)
    {
        // Do nothing, effectively discarding the data to be written.
    }

    /**
        @see Writer#write(String, int, int)
    */
    public void write(String str, int offset, int length)
    {
        // Do nothing, effectively discarding the data to be written.
    }
}
