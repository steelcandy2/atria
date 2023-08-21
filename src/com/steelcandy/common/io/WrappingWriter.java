/*
 Copyright (C) 2001-2005 by James MacKay.

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
    An abstract base class for writers that wrap another writer.
    <p>
    Often the wrapping writer adds to or transforms the data to be written
    and then passes the results to the wrapped writer to do the actual
    writing.
    <p>
    This class' implementations of the write() methods just call the
    'write(int)' method on the wrapped writer.
    <p>
    Despite being abstract, this class declares no abstract methods and
    implements all of the abstract methods declared in its superclass. Thus
    subclasses do not have to implement any of its methods, though they
    almost always override write(int), and can also override some or all of
    the other write() methods (for better performance, for example).

    @author James MacKay
    @version $Revision: 1.8 $
    @see Writer
    @see #write(int)
    @see #write(char[], int, int)
    @see #write(String, int, int)
*/
public abstract class WrappingWriter
    extends Writer
{
    // Private fields

    /** The writer that this writer wraps. */
    private Writer _writer;

    /**
        Indicates whether we should close the wrapped writer when we're
        closed.

        @see #setCloseWrappedWriterOnClose
    */
    private boolean _closeWrappedWriter;


    // Constructors

    /**
        Constructs a WrappingWriter.

        @param w the writer to be wrapped
        @param closeWrappedWriter true if the writer's close() method should
        attempt to close its wrapped writer, and false if it shouldn't
    */
    public WrappingWriter(Writer w, boolean closeWrappedWriter)
    {
        Assert.require(w != null);

        _writer = w;
        setCloseWrappedWriterOnClose(closeWrappedWriter);
    }

    /**
        Constructs a WrappingWriter that does <em>not</em> attempt to close
        its wrapped writer when it is closed. (This is the default in order
        to be consistent with the behaviour of the standard Java Writer
        classes.)

        @param w the writer to be wrapped
    */
    public WrappingWriter(Writer w)
    {
        this(w, true);
    }


    // Public methods

    /**
        Sets whether this writer will attempt to close its wrapped writer
        when it is closed.

        @param closeWrappedWriter true if the WrappingWriter's close() method
        should attempt to close the wrapped writer, and false if it shouldn't
        @see #close
    */
    public void setCloseWrappedWriterOnClose(boolean closeWrappedWriter)
    {
        _closeWrappedWriter = closeWrappedWriter;
    }

    /**
        Replaces the writer we wrap with the specified writer. The replaced
        writer will <em>not</em> have been closed, though an attempt will
        have been made to flush it.

        @param w our new wrapped writer
        @return the wrapped writer that was replaced
    */
    public Writer replaceWriter(Writer w)
    {
        Assert.require(w != null);

        Writer result = _writer;

        Io.tryToFlush(_writer);  // the writer being replaced
        _writer = w;

        Assert.ensure(result != null);
        return result;
    }


    // Writer methods

    /**
        @see Writer#write(int)
    */
    public void write(int ch)
        throws IOException
    {
        _writer.write(ch);
    }

    /**
        @see Writer#write(char[])
    */
    public void write(char[] buf)
        throws IOException
    {
        write(buf, 0, buf.length);
    }

    /**
        @see Writer#write(char[], int, int)
    */
    public void write(char[] buf, int offset, int length)
        throws IOException
    {
        if (buf != null)
        {
            for (int i = 0; i < length; i++)
            {
                write(buf[offset + i]);
            }
        }
    }

    /**
        @see Writer#write(String)
    */
    public void write(String str)
        throws IOException
    {
        write(str, 0, str.length());
    }

    /**
        @see Writer#write(String, int, int)
    */
    public void write(String str, int offset, int length)
        throws IOException
    {
        if (str != null)
        {
            for (int i = 0; i < length; i++)
            {
                write(str.charAt(offset + i));
            }
        }
    }


    /**
        @see Writer#flush
    */
    public void flush()
        throws IOException
    {
        _writer.flush();
    }

    /**
        @see Writer#close
    */
    public void close()
        throws IOException
    {
        Writer w = writer();
        if (w != null)
        {
            if (_closeWrappedWriter)
            {
                w.close();
            }
            else
            {
                // If we don't close our wrapped writer then we at least
                // flush it (or try to, anyway).
                w.flush();
            }
        }
    }


    // Protected methods

    /**
        @return the wrapped writer
    */
    protected Writer writer()
    {
        Assert.ensure(_writer != null);
        return _writer;
    }
}
