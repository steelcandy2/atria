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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;  // javadocs

import com.steelcandy.common.io.Io;

import java.io.*;

/**
    Represents a source code file.
    <p>
    Note: in general instances should be constructed using a
    SourceCodeFactory instead of calling this class' constructors directly.

    @author James MacKay
    @version $Revision: 1.12 $
*/
public class SourceCodeFile
    extends AbstractSourceCode
{
    // Private fields

    /**
        The SourceCodeFactory that created this object, or null if it
        wasn't created by a SourceCodeFactory (so far as we know).
    */
    private SourceCodeFactory _creator;

    /** The pathname of the file containing the source code. */
    private File _sourceFile;


    // Constructors

    /**
        Constructs a SourceCodeFile from the pathname of the source code
        file.

        @param sourceFile the pathname of the source code file
    */
    public SourceCodeFile(File sourceFile)
    {
        Assert.require(sourceFile != null);

        _sourceFile = sourceFile;
        _creator = null;
    }


    // Public methods

    /**
        Sets the factory that caused this object to be constructed to be
        the specified factory.

        @param creator the factory that caused this instance to be
        constructed
        @see SourceCodeFactory#create(File, ErrorHandler)
    */
    public void setCreator(SourceCodeFactory creator)
    {
        Assert.require(creator != null);

        _creator = creator;
    }

    /**
        Returns the source code file's pathname.

        @see SourceCode#fullName
    */
    public String fullName()
    {
        return _sourceFile.getPath();
    }

    /**
        Returns the source code file's filename, stripped of any directory
        information.

        @see SourceCode#name
    */
    public String name()
    {
        return _sourceFile.getName();
    }


    /**
        @see SourceCode#toSourceCodeString
    */
    public SourceCodeString toSourceCodeString()
        throws IOException
    {
        return new SourceCodeString(Io.toString(_sourceFile),
                                    fullName(), name());
    }

    /**
        @see SourceCode#toSourceCodeString(long)
    */
    public SourceCode toSourceCodeString(long maxSize)
        throws IOException
    {
        Assert.require(maxSize >= 0);

        SourceCode result = this;
        if (_sourceFile.length() <= maxSize)
        {
            result = toSourceCodeString();
        }

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @see AbstractSourceCode#createSourceReader
    */
    protected SourceCodeReader createSourceReader()
        throws IOException
    {
        SourceCodeFileReader result =
            new SourceCodeFileReader(_sourceFile, _creator);

        Assert.ensure(result != null);
        return result;
    }
}
