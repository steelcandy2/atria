/*
 Copyright (C) 2002-2015 by James MacKay.

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

package com.steelcandy.build;

// import com.steelcandy.common.debug.Assert;

import java.io.*;
import java.util.*;

/**
    Splits a specially-formatted text file containing the contents of
    multiple files into individual files, which are placed in a specified
    directory.
    <p>
    Note: instances of this class will <em>not</em> create the directory that
    the split out files are to be placed in if it doesn't exist.
    <p>
    A file that is to be split by an instance of this class must contain
    zero or more blank lines - where a blank line is any line consisting of
    nothing but a single newline - followed by one or blocks that have
    the following format:
    <ul>
        <li>a line that
            <ul>
                <li>starts with the NEW_FILE_PREFIX (see below), followed
                    by</li>
                <li>zero or more whitespace characters, followed by</li>
                <li>the name the file to output the rest of the block to,
                    followed by</li>
                <li>zero or more whitespace characters</li>
            </ul></li>
        <li>zero or more lines that do <em>not</em> start with the
            NEW_FILE_PREFIX</li>
    </ul>
    The filename part of the first line of a block must not contain any
    directory components. Note that NEW_FILE_PREFIX ends with a space.

    @author James MacKay
*/
public class FileSplitter
{
    // Constants

    /**
        The prefix that appears at the start of those lines (and only
        those lines) that indicate the start of a file to be split out.
    */
    private static final String NEW_FILE_PREFIX = "%%%% file ";

    /**  The line separator used by the current platform. */
    public static final String LINE_SEPARATOR =
        System.getProperty("line.separator");


    // Private fields

    /** The directory into which to put the split files. */
    private File _targetDir;

    /**
        Indicates whether the file being split should be deleted after all of
        the files have been split out of it: it is true if the file should be
        deleted and false if it shouldn't be.
    */
    private boolean _doDeleteFileBeingSplit;

    /**
        The pathname of the file to which to write the pathnames of the files
        (at least potentially) split out of the file to be split, or null if
        the pathnames are not to be written anywhere.
    */
    private File _splitOutFilesFile;

    /**
        Indicates whether files are forced to be split out, even if the file
        already exists and is newer than the file to be split: it is true if
        splitting is to be forced, and false if it isn't.
    */
    private boolean _isSplittingForced;

    /**
        The 'files to discard' file, or null if no such file is to be used.

        @see #setFilesToDiscardFile(File)
    */
    private File _filesToDiscardFile;


    // Constructors

    /**
        Constructs a new FileSplitter.

        @param targetDir the directory into which to put the files split out
        of the file being split
    */
    public FileSplitter(File targetDir)
    {
        _targetDir = targetDir;
        _doDeleteFileBeingSplit = false;
        _splitOutFilesFile = null;
        _isSplittingForced = false;
        _filesToDiscardFile = null;
    }


    // Public configuration methods

    /**
        Sets whether the file to be split should be deleted after all of the
        files in it have been successfully split out.
        <p>
        The default is not to delete the file.

        @param doDelete true if the file to be split should be deleted after
        the files are split out of it, and false if it shouldn't
    */
    public void setDeleteFileBeingSplit(boolean doDelete)
    {
        _doDeleteFileBeingSplit = doDelete;
    }

    /**
        Sets whether files are forced to be split out, even if the files
        already exist and are newer than the file they're split out of.
        <p>
        The default is to not force files to be split out.

        @param doForce true if files are to always be split out, and false
        if they are only split out when out of date or missing
    */
    public void setForceSplitting(boolean doForce)
    {
        _isSplittingForced = doForce;
    }

    /**
        If the specified pathname is non-null then the pathname of the file
        to which to write the filenames of the files (at least potentially)
        split out from the file to be split is set to that pathname. If the
        specified pathname is null then the filenames will not be written out
        to any file.
        <p>
        The filename of each file in the file to be split is added to the
        list, regardless of whether it was actually split out (e.g. because
        the file already exists and is newer than the file being split),
        except if it was not split out because it was explicitly discarded.
        <p>
        The filenames will be written to the file one per line. The filenames
        do not include any directory information: the file's format is
        intended to be consistent with that of .cvsignore files.
        <p>
        The default is for the filenames not to be written to any file.

        @param f the pathname of the file to which to write the filenames of
        the (potentially) split out files
    */
    public void setSplitOutFilesFile(File f)
    {
        _splitOutFilesFile = f;
    }

    /**
        If the specified pathname is non-null then the pathname of the file
        that specifies the filenames of the files not to be split out of the
        file being split is set to that pathname. If the specified
        pathname is null, or no file with the specified pathname exists, then
        no files are discarded.
        <p>
        The file is assumed to contain only blank lines and lines consisting
        of the filename of exactly one file that is not to be split out:
        every line that is not blank is considered to specify a filename. The
        files listed in the specified file are not split out even if they are
        missing or out of date.
        <p>
        The default is for no 'files to discard' file to be read or used.

        @param f the file to use as the 'files to discard' file, or null if
        no such file is to be used
    */
    public void setFilesToDiscardFile(File f)
    {
        _filesToDiscardFile = f;
    }


    // Public methods

    /**
        Splits the specified file.

        @param f the pathname of the file to split
        @exception IOException thrown if an error occurs reading from the
        file to split or writing out one of the files split out of the file
        being split
    */
    public void split(File f)
        throws IOException
    {
        // Assert.require(f != null);
        Set discards = createFilesToDiscardSet(_filesToDiscardFile);
        List splitOutFiles = new ArrayList();

        BufferedReader r = null;
        try
        {
            r = new BufferedReader(new FileReader(f));
            String infoLine = firstNonBlankLine(r);
            if (infoLine == null)
            {
                String msg = "the file " + f.getPath() +
                    " could not be split since it contains nothing " +
                    "except possibly some blank lines";
                throw new IOException(msg);
            }
            else if (infoLine.startsWith(NEW_FILE_PREFIX) == false)
            {
                String msg = "the file " + f.getPath() +
                    " could not be split since its first non-blank " +
                    "line does not start with the 'new file' prefix " +
                    "\"" + NEW_FILE_PREFIX + "\". The line is:" +
                    LINE_SEPARATOR + "[" + infoLine + "]";
                throw new IOException(msg);
            }

            while (infoLine != null)
            {
                // Assert.check(infoLine.startsWith(NEW_FILE_PREFIX));
                infoLine = outputFile(infoLine, r, discards,
                                      splitOutFiles, f);
            }
        }
        finally
        {
            tryToClose(r);
        }

        createSplitOutFilesFile(splitOutFiles);
        tryToDeleteFileBeingSplit(f);
    }


    // Protected methods

    /**
        Creates and returns a set containing the filenames of all of the
        files in the specified 'files to discard' file.

        @param filesToDiscardFile the pathname of the 'files to discard'
        file from which to get the filenames to put in the returned set
        @return a set containing all of the filenames specified in
        'filesToDiscardFile'
        @exception IOException thrown if an error occurs in reading from the
        'filesToDiscardFile'
    */
    protected Set createFilesToDiscardSet(File filesToDiscardFile)
        throws IOException
    {
        Set result = new HashSet();

        if (filesToDiscardFile != null &&
            filesToDiscardFile.exists())
        {
            BufferedReader r = null;
            try
            {
                r = new BufferedReader(new FileReader(filesToDiscardFile));
                while (true)
                {
                    String line = firstNonBlankLine(r);
                    if (line == null)
                    {
                        break;
                    }
                    result.add(line.trim());
                }
            }
            finally
            {
                tryToClose(r);
            }
        }

        return result;
    }

    /**
        Returns the first non-blank line read from the specified reader, or
        null if there are no more non-blank lines to be read from the reader.

        @param r the reader from which to read lines
        @return the first non-blank line read from the reader
        @exception IOException thrown if an error occurs reading from the
        reader
    */
    protected String firstNonBlankLine(BufferedReader r)
        throws IOException
    {
        String result;

        while (true)
        {
            result = r.readLine();
            if (result == null || result.length() > 0)
            {
                break;
            }
        }

        return result;
    }

    /**
        Creates the file that is to contain the filenames of all of the
        (potentially) split out files, iff this splitter is configured to
        create such a file.

        @param splitOutFiles the filenames of the files that were (at
        least potentially) split out of the file we're currently splitting:
        each item in the list is assumed to be a String
        @see #setSplitOutFilesFile(File)
    */
    protected void createSplitOutFilesFile(List splitOutFiles)
    {
        // Assert.require(splitOutFiles != null);

        if (_splitOutFilesFile != null)
        {
            FileWriter w = null;
            try
            {
                tryToDeleteGeneratedFile(_splitOutFilesFile);
                w = new FileWriter(_splitOutFilesFile);
                Iterator iter = splitOutFiles.iterator();
                while (iter.hasNext())
                {
                    String name = (String) iter.next();
                    w.write(name + LINE_SEPARATOR);
                }
            }
            catch (IOException ex)
            {
                log("could not write all split out files to " +
                    _splitOutFilesFile.getPath() + ": " +
                    ex.getLocalizedMessage());
            }
            finally
            {
                tryToClose(w);
            }
        }
    }

    /**
        Tries to delete the file being split iff we have been configured to
        delete it.

        @param f the pathname of the file we're currently - or have just
        finished - splitting
        @see #setDeleteFileBeingSplit(boolean)
    */
    protected void tryToDeleteFileBeingSplit(File f)
    {
        if (_doDeleteFileBeingSplit)
        {
            if (f.delete() == false)
            {
                log("could not delete the file named " +
                    f.getPath() + "after the files were split out of it");
            }
        }
    }

    /**
        Outputs the file whose contents are read from the specified reader,
        using the information in the specified information line.
        <p>
        The contents of the file to output are all of the lines in the reader
        up to the last line in the reader, or the line before the next
        information line.

        @param infoLine the information line describing how the file is to be
        output
        @param r the reader from which to read the contents of the file to
        output
        @param discards a set of the filenames of the files to discard: that
        is, that should unconditionally never be split out
        @param splitOutFiles the list of split out files, to which we will
        add the file we output (unless it is discarded): each item in this
        list must be a String
        @param f the pathname of the file being split
        @return the information line read from the reader that indicated the
        end of the output file, or null if there are no more lines left to
        read from 'r'
    */
    protected String outputFile(String infoLine, BufferedReader r,
                                Set discards, List splitOutFiles, File f)
        throws IOException
    {
        // Assert.require(infoLine != null);
        // Assert.require(r != null);
        // Assert.require(splitOutFiles != null);
        // Assert.require(f != null);

        String result = null;

        File outputFile = getOutputFile(infoLine);
        boolean isDiscarded = false;
        if (outputFile != null)
        {
            String name = outputFile.getName();
            isDiscarded = discards.contains(name);

            // Add 'name' to the list of split out files regardless of
            // whether it is actually split out, unless it was explicitly
            // discarded.
            if (isDiscarded == false)
            {
                splitOutFiles.add(name);
            }
        }

        Writer w = null;
        try
        {
            // Unless we're forcing all files to be split out, don't split
            // out 'outputFile' if it
            // - is invalid,
            // - is to be discarded or
            // - already exists and is (strictly) newer than the file being
            //   split.
            if (outputFile == null || isDiscarded ||
                (_isSplittingForced == false && outputFile.exists() &&
                    outputFile.lastModified() > f.lastModified()))
            {
                // Do not split out the file (and don't delete it).
                w = null;
            }
            else
            {
                // Split out the file, deleting it first (in case it is
                // write-protected) and creating any and all missing
                // ancestor directories.
                tryToDeleteGeneratedFile(outputFile);
                w = new FileWriter(outputFile);
            }

            while (true)
            {
                result = r.readLine();
                if (result == null || result.startsWith(NEW_FILE_PREFIX))
                {
                    break;
                }

                if (w != null)
                {
                    w.write(result + LINE_SEPARATOR);
                }
            }
        }
        finally
        {
            tryToClose(w);
        }

        return result;
    }

    /**
        Returns the output file whose pathname, relative to the target
        directory, is contained in the specified information line.

        @param infoLine the information line containing the pathname of the
        output file (relative to the target directory)
        @return the pathname of the output file, or null if it is invalid:
        it is assumed not to be missing from 'infoLine'
    */
    protected File getOutputFile(String infoLine)
    {
        // Assert.require(infoLine.startsWith(NEW_FILE_PREFIX));

        File result;

        String pathname =
            infoLine.substring(NEW_FILE_PREFIX.length()).trim();
        String parent = (new File(pathname)).getParent();
        if (parent != null)
        {
            log("could not split out the file named " + pathname +
                " because its name  contains one or more directory " +
                "name components.");
            result = null;
        }
        else
        {
            result = new File(_targetDir, pathname);
        }

        // 'result' may be null
        return result;
    }

    /**
        Tries to delete the specified file iff it exists, in preparation for
        overwriting it with a generated file.

        @param f the file to try to delete iff it exists
    */
    protected void tryToDeleteGeneratedFile(File f)
    {
        // Assert.require(f != null);

        if (f.exists())
        {
            if (f.delete() == false)
            {
                log("could not delete the existing file " + f.getPath() +
                    " in preparation for replacing it: it might not be " +
                    "possible to replace the file.");
            }
        }
    }

    /**
        Logs the specified message.

        @param msg the message to log
    */
    protected void log(String msg)
    {
        // Assert.require(msg != null);

        System.err.println(msg);
    }


    // Protected static methods

    /**
        Tries to close() the specified Reader, iff it is non-null, but
        ignores any IOExceptions that occur.

        @param r the Reader to try to close (iff it isn't null)
    */
    protected static void tryToClose(Reader r)
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
        Tries to close() the specified Writer, iff it is non-null, but
        ignores any IOExceptions that occur.

        @param w the Writer to try to close (iff it isn't null)
    */
    protected static void tryToClose(Writer w)
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


    // Main method

    /**
        Splits the file with the pathname specified by the first argument
        into files that are placed under the directory with the pathname
        specified by the second argument.

        @param args the command-line arguments
    */
    public static void main(String[] args)
    {
        int rc = 0;
        String generatedFile = null;
        String discardFile = null;
        int argIndex = 0;

        while (argIndex < args.length)
        {
            String arg = args[argIndex];
            if (arg.equals("-g"))
            {
                argIndex += 1;
                if (argIndex < args.length)
                {
                    generatedFile = args[argIndex];
                    argIndex += 1;
                }
                else
                {
                    System.err.println(getUsageMessage());
                    break;  // while
                }
            }
            else if (arg.equals("-d"))
            {
                argIndex += 1;
                if (argIndex < args.length)
                {
                    discardFile = args[argIndex];
                    argIndex += 1;
                }
                else
                {
                    System.err.println(getUsageMessage());
                    break;  // while
                }
            }
            else
            {
                break;  // while
            }
        }

        if ((args.length - argIndex) != 2)
        {
            System.err.println(getUsageMessage());
        }
        else
        {
            File fileToSplit = new File(args[argIndex]);
            File targetDir = new File(args[argIndex + 1]);
            FileSplitter splitter = new FileSplitter(targetDir);
            if (generatedFile != null)
            {
//System.err.println("gen. file = [" + generatedFile + "]");
                splitter.setSplitOutFilesFile(new File(generatedFile));
            }
            if (discardFile != null)
            {
//System.err.println("discard file = [" + discardFile + "]");
                splitter.setFilesToDiscardFile(new File(discardFile));
            }
            try
            {
                splitter.split(fileToSplit);
            }
            catch (IOException ex)
            {
                System.err.println("error splitting files: " +
                                    ex.getLocalizedMessage());
                rc = 1;
            }
        }

        // Uncomment the next line iff the JVM is to be exited on failure.
        // System.exit(rc);
    }

    /**
        @return the usage message describing how to use this class' main()
        method
    */
    private static String getUsageMessage()
    {
        StringBuffer buf = new StringBuffer();

        buf.append(LINE_SEPARATOR).
            append("usage: java ").
            append(FileSplitter.class.getName()).
            append(" [-g generated-file] [-d discard-file]").
            append(" file dir").
            append(LINE_SEPARATOR).
            append(LINE_SEPARATOR).
            append("where 'file' is the pathname of the file to " +
                   "split, and").
            append(LINE_SEPARATOR).
            append("'dir' is the pathname of the directory " +
                   "under which to").
            append(LINE_SEPARATOR).
            append("put the files that are split out of 'file'").
            append(LINE_SEPARATOR).
            append(LINE_SEPARATOR);

        return buf.toString();
    }
}
