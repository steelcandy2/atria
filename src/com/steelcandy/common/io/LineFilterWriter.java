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

import java.io.IOException;
import java.io.Writer;

/**
    An abstract base class for writers that add to or transform each line
    that they output.
    <p>
    Note: if close() isn't called on an instance of this class then all or
    part of the last line to be written may not get written.

    @author James MacKay
    @version $Revision: 1.6 $
    @see Writer
*/
public abstract class LineFilterWriter
    extends WrappingWriter
{
    // Private fields

    /**
        The buffer containing the unterminated part of the current line that
        has been passed to this writer.
    */
    private StringBuffer _line = new StringBuffer();

    /**
        The buffer containing the part of the line terminator of the current
        line that has been passed to this writer.
    */
    private StringBuffer _terminator = new StringBuffer();


    // Constructors

    /**
        Constructs a LineFilterWriter.

        @param w the writer to be wrapped
        @param closeWrappedWriter true if the writer's close() method should
        attempt to close its wrapped writer, and false if it shouldn't
    */
    public LineFilterWriter(Writer w, boolean closeWrappedWriter)
    {
        super(w, closeWrappedWriter);
    }

    /**
        Constructs a LineFilterWriter that does <em>not</em> attempt to close
        its wrapped writer when it is closed.

        @param w the writer to be wrapped
    */
    public LineFilterWriter(Writer w)
    {
        super(w);
    }


    // Public methods

    /**
        @see Writer#write(int)
    */
    public void write(int charCode)
        throws IOException
    {
        char ch = (char) charCode;
        if (ch == '\n')  // line feed (LF)
        {
            // A LF always marks the end of a line.
            _terminator.append(ch);
            writeLineAndClear(_line, _terminator);
        }
        else if (ch == '\r')  // carriage return (CR)
        {
            // Add it to _terminator but don't output the line yet since this
            // CR could be followed by a LF.
            _terminator.append(ch);
        }
        else
        {
            if (_terminator.length() > 0)
            {
                // The previous line ended in a CR and we were waiting for a
                // possible LF after it, but we got this character instead,
                // so write the previous line.
                writeLineAndClear(_line, _terminator);
            }
            _line.append(ch);
        }
    }

    /**
        @see Writer#close
    */
    public void close()
        throws IOException
    {
        try
        {
            // Output the last line, if there is one.
            if (_line.length() > 0 || _terminator.length() > 0)
            {
                writeLineAndClear(_line, _terminator);
            }
        }
        finally
        {
            super.close();
        }
    }


    // Protected methods

    /**
        @return true iff the current line is empty
    */
    protected boolean isCurrentLineEmpty()
    {
        return (_line.length() == 0);
    }


    // Private methods

    /**
        Writes the specified line that ends with the specified line
        terminator, then clears both buffers.
        <p>
        This method is private since it shouldn't be overridden, and to
        prevent it being mistakenly overridden in place of the writeLine()
        method.

        @param line the buffer containing the (unterminated) line to write
        @param terminator the buffer containing 'line''s line terminator
        @exception IOException thrown if an error occurs trying to write the
        line
    */
    private void
        writeLineAndClear(StringBuffer line, StringBuffer terminator)
        throws IOException
    {
        writeLine(line.toString(), terminator.toString());
        line.setLength(0);
        terminator.setLength(0);
    }


    // Abstract methods

    /**
        Writes the specified unterminated line that ends with the specified
        line terminator (i.e. a newline or equivalent), presumably using the
        wrapped writer and usually after adding to or otherwise transforming
        the line.

        @param line the (unterminated) line to write
        @param terminator the line terminator with which the line ends: it
        may be the empty string (iff 'line' doesn't end with a newline of
        some sort). It should usually be written too
        @exception IOException thrown if an error occurs trying to write the
        line
        @see WrappingWriter#writer
    */
    protected abstract void writeLine(String line, String terminator)
        throws IOException;
        // Assert.require(line != null);
        // Assert.require(terminator != null);
        // Assert.require(line.length() > 0 || terminator.length() > 0);
}
