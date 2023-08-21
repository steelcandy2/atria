/*
 Copyright (C) 2004-2011 by James MacKay.

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
    An IndentWriter that writes to a String.
    <p>
    Note: any attempts to write anything using an instance after its
    contents have been retrieved will fail. Our contents() and
    contentsBuffer() methods can be called repeatedly, though.

    @author James MacKay
*/
public class StringIndentWriter
    extends IndentWriter
{
    // Private fields

    /** Indicates whether our Writer has been closed or not. */
    private boolean _isWriterClosed;


    // Constructors

    /**
        Constructs a StringIndentWriter.
    */
    public StringIndentWriter()
    {
        this(new StringWriter());
    }

    /**
        Constructs a StringIndentWriter.

        @param initialCapacity the initial capacity of the buffer that this
        writer writes to
    */
    public StringIndentWriter(int initialCapacity)
    {
        this(new StringWriter(initialCapacity));
        Assert.require(initialCapacity >= 0);
    }

    /**
        Constructs a StringIndentWriter.

        @param w the StringWriter that the StringIndentWriter is to use
        to do its writing
    */
    public StringIndentWriter(StringWriter w)
    {
        // The 'true' argument indicates that when we're closed our Writer
        // will be too, which our contents() and contentsBuffer() methods
        // rely on.
        super(w, true);
        Assert.require(w != null);
        _isWriterClosed = false;
    }


    // Public methods

    /**
        @see Writer#close
    */
    public void close()
        throws IOException
    {
        super.close();
        _isWriterClosed = true;
    }

    /**
        @return a buffer containing everything that this writer has written
        so far
    */
    public StringBuffer contentsBuffer()
    {
        if (_isWriterClosed == false)
        {
            // This ensures that EVERYTHING we've written - including all of
            // our last line - has been written to our Writer: our superclass
            // LineFilterWriter doesn't write out a partial last line
            try
            {
                close();
            }
            catch (IOException ex)
            {
// TODO: is there a better way to handle this ???!!!!????
                throw new RuntimeException(ex);
            }
            Assert.check(_isWriterClosed);
        }
        StringBuffer result = buffer();

        Assert.ensure(result != null);
        return result;
    }


    /**
        @return everything this writer has written so far
    */
    public String contents()
    {
        String result = contentsBuffer().toString();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return true iff one or more characters have been written using this
        writer
    */
    public boolean hasBeenUsed()
    {
        return (buffer().length() > 0);
    }


    // Private methods

    /**
        @return the buffer that we write to
    */
    private StringBuffer buffer()
    {
        StringBuffer result = ((StringWriter) writer()).getBuffer();

        Assert.ensure(result != null);
        return result;
    }
}
