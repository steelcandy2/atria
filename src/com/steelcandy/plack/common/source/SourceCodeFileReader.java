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
    The class of SourceCodeReader used to read the contents of
    SourceCodeFiles.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class SourceCodeFileReader
    extends SourceCodeReader
{
    // Private fields

    /** Indicates whether this reader has been closed. */
    private boolean _hasBeenClosed;

    /**
        The factory that created our SourceCodeFile, or null if that
        factory is unknown or the SourceCodeFile wasn't created by a
        factory.
    */
    private SourceCodeFactory _creator;


    // Constructors

    /**
        Constructs a SourceCodeFileReader.

        @param source the pathname of the source file that this reader is to
        read from
        @param f the SourceCodeFactory that created the SourceCodeFile whose
        contents we read, or null if that factory is unknown or the
        SourceCodeFile wasn't created by a factory
        @exception FileNotFoundException if the file with pathname 'source'
        doesn't exist
    */
    public SourceCodeFileReader(File source, SourceCodeFactory f)
        throws FileNotFoundException
    {
        // Assert.require(source != null);
        super(new BufferedReader(new FileReader(source)));
        // 'f' may be null

        _hasBeenClosed = false;
        _creator = f;
        if (_creator != null)
        {
            _creator.notifyFileReaderOpened();
        }
    }


    // Public methods

    /**
        @see Reader#close
    */
    public void close()
        throws IOException
    {
        try
        {
            super.close();
        }
        finally
        {
            _hasBeenClosed = true;
            if (_creator != null)
            {
                _creator.notifyFileReaderClosed();
            }
        }
    }

    /**
        @see Object#finalize
    */
    public void finalize()
        throws Throwable
    {
        if (_hasBeenClosed == false)
        {
            close();
        }
        super.finalize();
    }
}
