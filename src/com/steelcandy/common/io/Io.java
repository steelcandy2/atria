/*
 Copyright (C) 2001-2009 by James MacKay.

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

import com.steelcandy.common.Resources;
import com.steelcandy.common.containers.Containers;
import com.steelcandy.common.text.TextUtilities;

import java.io.*;
import java.util.*;

import java.util.Map;
import java.util.HashMap;

/**
    Class containing various utility methods and constants
    related to input and output.

    @author James MacKay
    @version $Revision: 1.19 $
*/
public class Io
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        IoResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        INVALID_OUTPUT_DESTINATION_MSG =
            "INVALID_OUTPUT_DESTINATION_MSG",
        CANNOT_UPDATE_NON_REGULAR_FILE_MSG =
            "CANNOT_UPDATE_NON_REGULAR_FILE_MSG",
        FILE_TO_UPDATE_FROM_NOT_REGULAR_FILE_MSG =
            "FILE_TO_UPDATE_FROM_NOT_REGULAR_FILE_MSG",
        RENAME_TO_UPDATE_FILE_FAILED_MSG =
            "RENAME_TO_UPDATE_FILE_FAILED_MSG";


    // Miscellaneous I/O constants

    /**  The line separator used by this platform. */
    public static final String
        LINE_SEPARATOR = TextUtilities.LINE_SEPARATOR;

    /** A shorter name for the line separator used by this platform. */
    public static final String NL = LINE_SEPARATOR;

    /**
        The separator that separates a filename extension from the
        rest of the filename.
    */
    public static final String EXTENSION_SEPARATOR = ".";


    // Standard I/O Writer constants

    /** The standard input Reader. */
    public static final Reader in = new InputStreamReader(System.in);

    /** The standard output PrintWriter. */
    public static final PrintWriter out =
        new PrintWriter(System.out, true);  // println auto-flushes

    /** The standard error PrintWriter. */
    public static final PrintWriter err =
        new PrintWriter(System.err, true);  // println auto-flushes

    /** The Writer that discards all of its output. */
    public static final PrintWriter none =
        new PrintWriter(DiscardWriter.instance());


    // Output destination constants

    /** The output destination that specifies standard output. */
    public static final String STDOUT_DESTINATION = "output";

    /** The output destination that specifies standard error. */
    public static final String STDERR_DESTINATION = "error";

    /**
        The output destination that specifies that the output
        is to be discarded.
    */
    public static final String DISCARD_DESTINATION = "discard";

    /**
        Maps each of the 'standard' output destinations (as given by
        the *_DESTINATION constants defined in this class) to the
        Writer to use to output to that destination.
    */
    public static final Map _standardDestinationToWriter =
                            createStandardDestinationToWriterMap();

    /**
        The prefix for all output destinations that specify a file
        as the destination. This prefix must be followed by the
        file's pathname.
    */
    public static final String FILE_DESTINTATION_PREFIX = "file:";


    // Constructors

    /**
        This constructor is private to prevent this class from being
        instantiated.
    */
    private Io()
    {
        // empty
    }


    // Directory utility methods

    /**
        Deletes everything in and under the directory with the specified
        pathname - though not the directory itself - iff it is an existing
        directory. Otherwise it does nothing.

        @param dir the pathname of the directory to clean (if it is the
        pathname of an existing directory)
        @return true if no file named 'dir' exists, or it exists and was
        successfully cleaned
    */
    public static boolean cleanDirectory(File dir)
    {
        Assert.require(dir != null);
        Assert.require(mayBeDirectory(dir));

        boolean result = true;

        if (dir.isDirectory())
        {
            File[] contents = dir.listFiles();
            for (int i = 0; i < contents.length; i++)
            {
                if (delete(contents[i]) == false)
                {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
        Deletes the file with the specified pathname. If it is the pathname
        of a directory then the directory's contents will be deleted as well
        (and thus the directory doesn't have to be empty).

        @see File#delete
    */
    public static boolean delete(File f)
    {
        Assert.require(f != null);

        boolean result = true;

        if (f.isDirectory())
        {
            if (cleanDirectory(f))
            {
                if (f.delete() == false)
                {
                    result = false;
                }
            }
            else
            {
                // The directory isn't empty, so 'f.delete()' would fail.
                result = false;
            }
        }
        else
        {
            if (f.delete() == false)
            {
                result = false;
            }
        }

        return result;
    }


    // File utility methods

    /**
        Updates the file with pathname 'f1' based on 'f2':
        <ul>
            <li>if 'f1' doesn't exist then 'f2' is renamed to 'f1';
                otherwise</li>
            <li>if the contents of 'f2' are the same as the contents of
                'f1' then 'f2' is deleted; otherwise</li>
            <li>'f2' is renamed to 'f1', replacing the old file 'f1'.</li>
        </ul>

        @param f1 the pathname of the file to update
        @param f2 the pathname of the file to use to update 'f1'
        @exception IOException thrown if an error occurs in trying to update
        'f1'
    */
    public static void updateFirst(File f1, File f2)
        throws IOException
    {
        Assert.require(f1 != null);
        Assert.require(f2 != null);

        boolean doRename = false;
        if (f1.exists() == false)
        {
            doRename = true;
        }
        else if (f1.isFile() == false)
        {
            // 'f1' exists but isn't a regular file.
            String msg = _resources.
                getMessage(CANNOT_UPDATE_NON_REGULAR_FILE_MSG, f1.getPath());
            throw new IOException(msg);
        }
        else if (f2.isFile() == false)
        {
            // 'f2' either doesn't exist or exists but isn't a regular file.
            String msg = _resources.
                getMessage(FILE_TO_UPDATE_FROM_NOT_REGULAR_FILE_MSG,
                           f1.getPath(), f2.getPath());
            throw new IOException(msg);
        }
        else if (haveTheSameContents(f1, f2))
        {
            tryToDelete(f2);
        }
        else
        {
            doRename = true;
        }

        if (doRename)
        {
            if (f2.renameTo(f1) == false)
            {
                String msg = _resources.
                    getMessage(RENAME_TO_UPDATE_FILE_FAILED_MSG,
                               f1.getPath(), f2.getPath());
                throw new IOException(msg);
            }
        }
    }

    /**
        @param f1 the pathname of a file
        @param f2 the pathname of another file
        @return true iff 'f1' and 'f2' have exactly the same contents
        @exception IOException is thrown iff an I/O error occurs in reading
        one of the files
    */
    public static boolean haveTheSameContents(File f1, File f2)
        throws IOException
    {
        Assert.require(f1 != null);
        Assert.require(f2 != null);

        Boolean result = true;
        FileReader r1 = null;
        FileReader r2 = null;
        int ch1;
        int ch2;
        try
        {
            r1 = new FileReader(f1);
            r2 = new FileReader(f2);
            while (result)
            {
                ch1 = r1.read();
                ch2 = r2.read();
                if (ch1 == -1 || ch2 == -1)
                {
                    result = (ch1 == ch2);
                    break;  // while
                }
                else if (ch1 != ch2)
                {
                    result = false;
                }
            }  // while
        }
        finally
        {
            tryToClose(r2);
            tryToClose(r1);
        }

        return result;
    }


    /**
        Returns the canonicalized form of the specified pathname, or if that
        fails then returns the specified pathname made absolute.

        @param f the pathname
        @return the canonicalized form of 'f', or if that fails then 'f'
        made absolute
    */
    public static File tryToCanonicalizeFile(File f)
    {
        Assert.require(f != null);

        File result;

        try
        {
            result = f.getCanonicalFile();
        }
        catch (IOException ex)
        {
// TODO: log this !!!!
            result = f.getAbsoluteFile();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the canonicalized form of the specified pathname, or if that
        fails then returns the specified pathname made absolute.

        @param f the pathname
        @return the canonicalized form of 'f', or if that fails then 'f'
        made absolute
    */
    public static String tryToCanonicalizePath(File f)
    {
        Assert.require(f != null);

        String result;

        try
        {
            result = f.getCanonicalPath();
        }
        catch (IOException ex)
        {
// TODO: log this !!!!
            result = f.getAbsolutePath();
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Indicates whether it is possible for the file with the specified
        pathname to be that of a regular file: that is, whether it either
        doesn't exist yet or exists and is a regular file.

        @param f the pathname of the file to test
        @return true iff the file doesn't exist, or does exist and is a
        regular file
    */
    public static boolean mayBeFile(File f)
    {
        Assert.require(f != null);

        return (f.exists() == false || f.isFile());
    }

    /**
        Indicates whether it is possible for the file with the specified
        pathname to be that of a directory: that is, whether it either
        doesn't exist yet or exists and is a directory.

        @param f the pathname of the file to test
        @return true iff the file doesn't exist, or does exist and is a
        directory
    */
    public static boolean mayBeDirectory(File f)
    {
        Assert.require(f != null);

        return (f.exists() == false || f.isDirectory());
    }

    /**
        @see #makeAbsolute(File, File)
    */
    public static File makeAbsolute(String filename, File baseDir)
    {
        Assert.require(filename != null);
        Assert.require(baseDir != null);
        Assert.require(baseDir.isAbsolute());

        // Assert.ensure(result != null);
        // Assert.ensure(result.isAbsolute());
        return makeAbsolute(new File(filename), baseDir);
    }

    /**
        Makes the specified filename absolute iff it isn't already. If
        it isn't absolute then it will be made absolute by making it
        relative to the specified base directory, which is assumed to
        be absolute.

        @param f the filename to make absolute (iff it isn't already)
        @param baseDir the directory to combine 'f' with to make 'f'
        absolute: it is assumed to be absolute, but this is not checked
        @return 'f' as an absolute filename
    */
    public static File makeAbsolute(File f, File baseDir)
    {
        Assert.require(f != null);
        Assert.require(baseDir != null);
        Assert.require(baseDir.isAbsolute());

        File result = f;

        if (result.isAbsolute() == false)
        {
            result = new File(baseDir, f.getPath());
        }

        Assert.ensure(result != null);
        Assert.ensure(result.isAbsolute());
        return result;
    }


    /**
        Note: a pathname whose base name starts with an extension separator
        (such as a UNIX hidden/dot file, for example) but doesn't contain
        any other extension separators is NOT considered to have an
        extension (at least by this method).

        @param f a pathname
        @return true iff 'f' has an extension
    */
    public static boolean hasExtension(File f)
    {
        Assert.require(f != null);

        int index = f.getName().lastIndexOf(EXTENSION_SEPARATOR);

        boolean result = (index > 0);

        return result;
    }

    /**
        Returns the pathname that results from replacing the extension of
        the specified pathname with the specified extension. The specified
        pathname object is never modified.

        @param f a pathname
        @param newExt the result pathname's extension
        @return the pathname that is the same as 'f' except that its
        extension is 'newExt'
    */
    public static File replaceExtension(File f, String newExt)
    {
        Assert.require(f != null);
        Assert.require(hasExtension(f));
        Assert.require(newExt != null);

        File dir = f.getParentFile();
        String name = f.getName();
        int index = name.lastIndexOf(EXTENSION_SEPARATOR);
        Assert.check(index > 0);
            // since hasExtension(f) is true

        String base = name.
            substring(0, index + EXTENSION_SEPARATOR.length());

        File result = new File(dir, base + newExt);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the result of removing the extension part - if there
        is one - from the specified pathname. The extension separator
        is also removed.

        @param pathname a pathname
        @return the result of removing the extension from 'pathname'
        @see #EXTENSION_SEPARATOR
    */
    public static String removeExtension(String pathname)
    {
        String result = pathname;

        int index = pathname.lastIndexOf(EXTENSION_SEPARATOR);
        if (index >= 0)
        {
            result = pathname.substring(0, index);
        }

        Assert.ensure(result != null);
        return result;
    }


    // Public static methods

    /**
        Writes the specified string followed by the current platform's
        line separator using the specified writer.

        @param msg the string to output as a line
        @param w the writer to use to output the line
        @exception IOException thrown if an I/O error occurs while
        writing the line
        @see #LINE_SEPARATOR
    */
    public static void writeLine(Writer w, String msg)
        throws IOException
    {
        w.write(msg);
        w.write(LINE_SEPARATOR);
    }

    /**
        Writes the specified string followed by the current platform's
        line separator using the specified PrintWriter.
        <p>
        Note that, unlike writeLine(Writer, String), this method cannot
        throw an IOException.

        @param msg the string to output as a line
        @param w the PrintWriter to use to output the line
        @see #LINE_SEPARATOR
    */
    public static void writeLine(PrintWriter w, String msg)
    {
        w.println(msg);
    }

    /**
        Writes the specified string followed by the current platform's
        line separator using the standard output PrintWriter.

        @param msg the string to output as a line
        @see #LINE_SEPARATOR
    */
    public static void writeLine(String msg)
    {
        writeLine(out, msg);
    }


    /**
        Creates and returns a Writer that will write to the
        specified output destination.

        @param dest a string description of the destination to
        which the Writer to be created is to write
        @return a Writer that writes to the specified destination
        @exception IOException if the Writer cannot be created
        @exception IllegalArgumentException thrown if the specified
        output destination is not a valid output destination
    */
    public static Writer createWriter(String dest)
        throws IOException, IllegalArgumentException
    {
        Assert.require(dest != null);

        return createWriter(dest, null);
    }

    /**
        Creates and returns a Writer that will write to the
        specified output destination.

        @param dest a string description of the destination to
        which the Writer to be created is to write
        @param baseDir if it isn't null, the directory to which
        all file output destinations that specify relative pathnames
        are relative to
        @return a Writer that writes to the specified destination
        @exception IOException if the Writer cannot be created
        @exception IllegalArgumentException thrown if the specified
        output destination is not a valid output destination
    */
    public static Writer createWriter(String dest, File baseDir)
        throws IOException, IllegalArgumentException
    {
        Assert.require(dest != null);

        // First see if the destination is one of the standard ones.
        Writer result = (Writer) _standardDestinationToWriter.get(dest);
        if (result == null)
        {
            // The destination isn't one of the standard ones. If
            // it specifies a file ...
            if (dest.startsWith(FILE_DESTINTATION_PREFIX))
            {
                String filename =
                    dest.substring(FILE_DESTINTATION_PREFIX.length());
                File f = new File(filename);
                if (baseDir != null && f.isAbsolute() == false)
                {
                    f = new File(baseDir, filename);
                }
                result = new FileWriter(f);
            }
        }

        if (result == null)
        {
            // The destination is invalid.
            String msg =
                _resources.getMessage(INVALID_OUTPUT_DESTINATION_MSG, dest);
            throw new IllegalArgumentException(msg);
        }
        return result;
    }


    /**
        Returns a string containing the contents of the specified
        file.

        @param f the file whose contents are to be returned
        @return a string containing the file's contents
        @exception IOException thrown if an I/O error occurs
        while reading the file
    */
    public static String toString(File f)
        throws IOException
    {
        return toString(new FileReader(f));
    }

    /**
        Returns a string containing everything that can be read
        from the specified reader.

        @param r the reader that is to be read from
        @return a string containing everything read from 'r'
        @exception IOException thrown if an I/O error occurs
        while reading the file
    */
    public static String toString(Reader r)
        throws IOException
    {
        StringWriter out = new StringWriter();
        for (int ch = r.read(); ch != -1; ch = r.read())
        {
            out.write(ch);
        }
        return out.toString();
    }

    /**
        Tries to close() the specified Reader, iff it is non-null,
        but ignores any IOExceptions that occur.

        @param r the Reader to try to close (iff it isn't null)
    */
    public static void tryToClose(Reader r)
    {
        if (r != null)
        {
            try
            {
                r.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }

    /**
        Tries to flush() the specified Writer, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param w the Writer to try to flush (iff it isn't null)
    */
    public static void tryToFlush(Writer w)
    {
        if (w != null)
        {
            try
            {
                w.flush();
            }
            catch (IOException ex)
            {
                // ignore failure to flush
            }
        }
    }

    /**
        Tries to close() the specified Writer, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param w the Writer to try to close (iff it isn't null)
    */
    public static void tryToClose(Writer w)
    {
        if (w != null)
        {
            try
            {
                w.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }

    /**
        Tries to close() the specified InputStream, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param in the input stream to try to close (iff it isn't null)
    */
    public static void tryToClose(InputStream in)
    {
        if (in != null)
        {
            try
            {
                in.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }

    /**
        Tries to close() the specified OutputStream, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param out the output stream to try to close (iff it isn't null)
    */
    public static void tryToClose(OutputStream out)
    {
        if (out != null)
        {
            try
            {
                out.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }

    /**
        Tries to delete the file with pathname 'f', iff 'f' isn't null.

        @param f the pathname of the file to try to delete (if 'f' isn't
        null)
    */
    public static void tryToDelete(File f)
    {
        if (f != null)
        {
            f.delete();
        }
    }

    /**
        Indicates whether the specified file, was last modified after the
        specified last modified 'time'. (If the file doesn't exist then
        it cannot be newer.)
        <p>
        This method is often used to determine whether the specified
        file that depends on one or more other files needs to be
        regenerated. In this case the last modified time would be the
        latest/largest last modified time of all of the files that
        the specified file depends on, and the file would have to be
        regenerated iff this method returns <code>false</code>.

        @param f the file to test
        @param lastModifiedTime the time to compare the file's last
        modified time against, if the file exists: this 'time' is assumed
        to be comparable to the value returned by File.lastModified()
        @return true if the exists and was last modified after the specified
        last modified time
        @see File#lastModified
    */
    public static boolean isFileNewerThan(File f, long lastModifiedTime)
    {
        Assert.require(f != null);

        boolean result = false;

        if (f.exists())
        {
            long fileLastModifiedTime = f.lastModified();
            result = (fileLastModifiedTime > lastModifiedTime);
        }

        return result;
    }


    // Private static methods

    /**
        Creates and returns a table that maps the String constants
        representing the standard output destinations to a Writer
        that outputs to that destination.

        @return a standard output destination to Writer map
    */
    private static Map createStandardDestinationToWriterMap()
    {
        Map result = Containers.createHashMap(3);

        result.put(STDOUT_DESTINATION, out);
        result.put(STDERR_DESTINATION, err);
        result.put(DISCARD_DESTINATION, none);

        return result;
    }
}
