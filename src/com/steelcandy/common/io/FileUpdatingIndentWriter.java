/*
 Copyright (C) 2009 by James MacKay.

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;  // javadocs

/**
    An IndentWriter that updates the file that it writes to: it initially
    writes everything to a temporary file in the same directory as its file,
    then when the writer it closed renames it to its file's pathname iff
    either its file doesn't exist, or its file exists the the temporary
    file's contents are different from its file's contents. Otherwise its
    file is left unchanged and the temporary file is deleted.

    @author James MacKay
    @version $Revision: 1.12 $
*/
public class FileUpdatingIndentWriter
    extends IndentWriter
{
    // Constants

    /**
        The minimum length of the prefix used in creating a temporary file's
        pathname.

        @see #createTemporaryFile(File)
        @see File#createTempFile(String, String, File)
    */
    private static final int
        MIN_TEMP_FILE_PREFIX_LENGTH = 3;

    /**
        The string to end the prefix part of a temporary file's pathname
        with. It may be appended to the prefix more than once iff the prefix
        is too short.

        @see #createTemporaryFile(File)
        @see #MIN_TEMP_FILE_PREFIX_LENGTH
    */
    private static final String
        TEMP_FILE_PREFIX_TERMINATOR = "-";


    // Private fields

    /** The pathname of the file we're to update. */
    private File _file;

    /** The pathname of our temporary file. */
    private File _tempFile;


    // Constructors

    /**
        @param f the pathname of a (possibly nonexistent) file
        @return a FileUpdatingIndentWriter that will update 'f' with what
        is written using it
        @exception IOException thrown if creating the writer fails
    */
    public static FileUpdatingIndentWriter create(File f)
        throws IOException
    {
        Assert.require(f != null);

        FileUpdatingIndentWriter result;
        File tempFile = null;
        try
        {
            tempFile = createTemporaryFile(f);
            tempFile.deleteOnExit();
                // though hopefully it'll be gone before that
            result = new FileUpdatingIndentWriter(f, tempFile);
        }
        catch (IOException ex)
        {
            Io.tryToDelete(tempFile);
            throw ex;
        }
        catch (RuntimeException ex)
        {
            Io.tryToDelete(tempFile);
            throw ex;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Constructs a FileUpdatingIndentWriter.

        @param f the pathname of the file to be updated
        @param tf the pathname of the temporary file to use
        @exception IOException if the writer couldn't be constructed
    */
    protected FileUpdatingIndentWriter(File f, File tf)
        throws IOException
    {
        super(new FileWriter(tf), true);
        Assert.require(f != null);
        Assert.require(tf != null);

        _file = f;
        _tempFile = tf;
    }


    // Public methods

    /**
        @see Writer#close
    */
    public void close()
        throws IOException
    {
        try
        {
            super.close();
            Io.updateFirst(_file, _tempFile);
        }
        finally
        {
            Io.tryToDelete(_tempFile);
        }
    }


    // Private static methods

    /**
        Creates an empty temporary file in the same directory as the file
        with pathname 'f' that starts with its basename.

        @param f the pathname of a file
        @return the pathname of the temporary file that was created
        @exception IOException thrown if creating the temporary file fails
    */
    private static File createTemporaryFile(File f)
        throws IOException
    {
        Assert.require(f != null);

        String sep = TEMP_FILE_PREFIX_TERMINATOR;
        File dir = f.getParentFile();
        String prefix = f.getName() + sep;

        // File.createTempFile() requires that the prefix be at least 3
        // characters long.
        while (prefix.length() < MIN_TEMP_FILE_PREFIX_LENGTH)
        {
            prefix += sep;
        }

        File result = File.createTempFile(prefix, "", dir);

        Assert.ensure(result != null);
        return result;
    }
}
