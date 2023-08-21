/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import java.io.*;

/**
    The class of Reader used to read source code.

    @author  James MacKay
*/
public class SourceCodeReader
    extends Reader
{
    // Private fields

    /**
        The BufferedReader that this reader delegates most of its
        operations to.
    */
    private BufferedReader _reader;


    // Constructors

    /**
        Constructs a SourceCodeReader.

        @param source the String of source code that the reader is to read
        from
    */
    public SourceCodeReader(String source)
    {
        Assert.require(source != null);

        _reader = new BufferedReader(new StringReader(source));
    }

    /**
        Constructs a SourceCodeReader.

        @param r the BufferedReader that this reader is to delegate most of
        its operations to
    */
    protected SourceCodeReader(BufferedReader r)
    {
        Assert.require(r != null);

        _reader = r;
    }


    // Public methods

    /**
        @see BufferedReader#readLine
    */
    public String readLine()
        throws IOException
    {
        return _reader.readLine();
    }


    /**
        @see Reader#read
    */
    public int read()
        throws IOException
    {
        return _reader.read();
    }

    /**
        @see Reader#read(char[])
    */
    public int read(char[] cbuf)
        throws IOException
    {
        return _reader.read(cbuf);
    }

    /**
        @see Reader#read(char[], int, int)
    */
    public int read(char[] cbuf, int off, int len)
        throws IOException
    {
        return _reader.read(cbuf, off, len);
    }

    /**
        @see Reader#skip(long)
    */
    public long skip(long n)
        throws IOException
    {
        return _reader.skip(n);
    }

    /**
        @see Reader#ready
    */
    public boolean ready()
        throws IOException
    {
        return _reader.ready();
    }

    /**
        @see Reader#markSupported
    */
    public boolean markSupported()
    {
        return _reader.markSupported();
    }

    /**
        @see Reader#mark(int)
    */
    public void mark(int limit)
        throws IOException
    {
        _reader.mark(limit);
    }

    /**
        @see Reader#reset
    */
    public void reset()
        throws IOException
    {
        _reader.reset();
    }

    /**
        @see Reader#close
    */
    public void close()
        throws IOException
    {
        _reader.close();
    }
}
