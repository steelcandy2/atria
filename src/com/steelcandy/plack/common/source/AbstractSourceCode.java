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

import com.steelcandy.common.io.Io;

import java.io.*;

/**
    An abstract base class for SourceCode classes.

    @author James MacKay
*/
public abstract class AbstractSourceCode
    extends SourceCode
{
    // Public methods

    /**
        @see SourceCode#reader
    */
    public Reader reader()
        throws IOException
    {
        return createSourceReader();
    }


    /**
        @see SourceCode#lines(int, int)
    */
    public String lines(int startLineNumber, int endLineNumber)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(startLineNumber >= 0);
        Assert.require(endLineNumber >= startLineNumber);

        String result;

        SourceCodeReader r = null;
        try
        {
            r = createSourceReader();
            result = lines(r, startLineNumber, endLineNumber);
        }
        finally
        {
            Io.tryToClose(r);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SourceCode#lines(int, int, int, int)
    */
    public String lines(int startLineNumber, int startOffset,
                        int endLineNumber, int pastEndOffset)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(startLineNumber >= 0);
        Assert.require(endLineNumber >= startLineNumber);
        Assert.require(startOffset >= 0);
        Assert.require(pastEndOffset >= startOffset);

        String result;

        SourceCodeReader r = null;
        try
        {
            r = createSourceReader();
            result = lines(r, startLineNumber,
                           startOffset, endLineNumber, pastEndOffset);
        }
        finally
        {
            Io.tryToClose(r);
        }

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        Returns a SourceCodeReader from which this entire piece of source
        code can be read.

        @return a SourceCodeReader from which this source code fragment can
        be read
        @exception IOException if an I/O error occurs while getting the
        reader
    */
    protected abstract SourceCodeReader createSourceReader()
        throws IOException;
        // Assert.ensure(result != null);
}
