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
    An abstract base class for writers that add a prefix to each line that
    they output.
    <p>
    By default blank lines are not prefixed: subclasses can override
    writeLine() to change this, though.
    <p>
    Note: the prefix for a line is built just before the line is actually
    written using the wrapped writer, so if a subclass' prefix is based on
    information that can change while this writer is writing parts of the
    line then the subclass may want to build the prefix earlier and store
    it for getPrefix() to return. (As an example, see the IndentWriter
    class.)
    <p>
    Note: this class' methods will not themselves throw IOExceptions: they
    will only throw ones originating from the wrapped writer. In particular,
    if the wrapped writer doesn't throw IOExceptions when writing then this
    class won't either (though close() will if the instance's close() method
    is to call the wrapped writer's close() method and the latter throws an
    IOException).

    @author James MacKay
    @version $Revision: 1.6 $
    @see #getPrefix(String, String)
*/
public abstract class PrefixWriter
    extends LineFilterWriter
{
    // Constructors

    /**
        Constructs a PrefixWriter.

        @param w the writer to be wrapped
        @param closeWrappedWriter true if the writer's close() method should
        attempt to close its wrapped writer, and false if it shouldn't
    */
    public PrefixWriter(Writer w, boolean closeWrappedWriter)
    {
        super(w, closeWrappedWriter);
    }

    /**
        Constructs a PrefixWriter that does <em>not</em> attempt to close its
        wrapped writer when it is closed.

        @param w the writer to be wrapped
    */
    public PrefixWriter(Writer w)
    {
        super(w);
    }


    // Protected methods

    /**
        @see LineFilterWriter#writeLine(String, String)
    */
    protected void writeLine(String line, String terminator)
        throws IOException
    {
        Assert.require(line != null);
        Assert.require(terminator != null);
        Assert.require(line.length() > 0 || terminator.length() > 0);

        Writer w = writer();

        // Only output the prefix if the line is non-empty.
        if (line.length() > 0)
        {
            w.write(getPrefix(line, terminator));
            w.write(line);
        }
        w.write(terminator);
    }


    // Abstract methods

    /**
        Returns the prefix to prepend to the specified line before outputting
        it and the specified line terminator.

        @param line the unterminated line to which the prefix will be
        prepended
        @param terminator the line terminator for 'line'
        @return the prefix to prepend to 'line' before outputting it and
        'terminator'
    */
    protected abstract String getPrefix(String line, String terminator);
        // Assert.require(line != null);
        // Assert.require(terminator != null);
        // Assert.require(line.length() > 0 || terminator.length() > 0);
}
