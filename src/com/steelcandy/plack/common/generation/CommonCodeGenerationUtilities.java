/*
 Copyright (C) 2004-2015 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
    A class that contains various utility methods useful in generating target
    code of various types.
    <p>
    Note: a language-specific singleton subclass of this class is usually
    used rather than using this class directly.

    @author  James MacKay
*/
public class CommonCodeGenerationUtilities
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonGenerationResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        CLOSING_TARGET_CODE_FILE_FAILED_MSG =
            "CLOSING_TARGET_CODE_FILE_FAILED_MSG",
        CREATE_OBJECT_CODE_BASE_DIR_FAILED_MSG =
            "CREATE_OBJECT_CODE_BASE_DIR_FAILED_MSG",
        CREATE_DIR_FAILED_MSG =
            "CREATE_DIR_FAILED_MSG",
        OPENING_TARGET_CODE_FILE_FAILED_MSG =
            "OPENING_TARGET_CODE_FILE_FAILED_MSG",
        WRITING_TARGET_CODE_FAILED_MSG =
            "WRITING_TARGET_CODE_FAILED_MSG",
        OBJECT_FILE_DELETE_FAILED_MSG =
            "OBJECT_FILE_DELETE_FAILED_MSG";

    /**
        The initial capacity of StringWriters created using
        createStringWriter().

        @see #createStringWriter
        @see #createStringIndentWriter
        @see StringWriter#StringWriter(int)
    */
    private static final int INITIAL_WRITER_CAPACITY = 512;

    /** The formatter to use to format the generation date in target code. */
    private static final SimpleDateFormat _generationDateFormat =
        new SimpleDateFormat("MMMM d, yyyy 'at' h:mm.ss a");


    // Constructors

    /**
        Constructs a CommonCodeGenerationUtilities object.
    */
    public CommonCodeGenerationUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        @return a new, empty ExpandableFixedTranslationFragment
    */
    public ExpandableFixedTranslationFragment expandableFragment()
    {
        ExpandableFixedTranslationFragment result =
            new ExpandableFixedTranslationFragment();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param s a set
        @return a shallow clone of 's'
    */
    public static Set cloneSet(Set s)
    {
        Assert.require(s != null);

        Set result = new HashSet(s);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a String representation of the current date/time
    */
    public static String now()
    {
        String result =
            _generationDateFormat.format(Calendar.getInstance().getTime());

        Assert.ensure(result != null);
        return result;
    }

    /**
        Ensures that the object code base directory with the specified
        pathname exists, creating it (and any missing parent directories) if
        necessary.

        @param baseDir the pathname of the object code base directory
        @exception IOException thrown if 'baseDir' does not exist and could
        not be created
    */
    public static void ensureObjectCodeBaseDirectoryExists(File baseDir)
        throws IOException
    {
        Assert.require(baseDir != null);

        ensureDirectoryExists(baseDir,
            CREATE_OBJECT_CODE_BASE_DIR_FAILED_MSG);

        Assert.ensure(baseDir.isDirectory());
    }

    /**
        Ensures that the directory with the pathname 'dir' exists, creating
        it (and any missing parent directories) if necessary.

        @param dir the pathname of the directory
        @exception IOException thrown if 'dir' does not exist and could
        not be created
    */
    public static void ensureDirectoryExists(File dir)
        throws IOException
    {
        Assert.require(dir != null);

        ensureDirectoryExists(dir, CREATE_DIR_FAILED_MSG);

        Assert.ensure(dir.isDirectory());
    }

    /**
        Deletes all of the non-directories under the specified object code
        base directory.

        @param baseDir the pathname of the object code base directory
        @exception IOException thrown iff we could not delete all of the
        non-directories under 'baseDir'
    */
    public static void deleteObjectCodeBaseDirectoryContents(File baseDir)
        throws IOException
    {
        Assert.require(baseDir != null);

        if (baseDir.isDirectory())
        {
            deleteObjectCodeUnder(baseDir);
        }
    }

    /**
        Creates and returns the writer to use to output target code to
        a file with the specified base name.

        @param dir the pathname of the target/object code directory that is
        to contain the file that the writer will write to
        @param baseFilename the base filename of the file that the writer
        is to write to: it does not include any directories or an extension
        @param extension the extension of the file that the writer is to
        write to: it should include the leading extension separator (usually
        '.')
        @return the writer to use to output the target code
        @exception FatalErrorException thrown iff the wrtier could not be
        created
    */
    public static IndentWriter createWriter(File dir, String baseFilename,
        String extension, ErrorHandler handler)
        throws FatalErrorException
    {
        Assert.require(dir != null);
        Assert.require(baseFilename != null);
        Assert.require(extension != null);
        Assert.require(handler != null);

        IndentWriter result = null;

        File f = new File(dir, baseFilename + extension);
        try
        {
            result = IndentWriter.createClosing(new FileWriter(f));
        }
        catch (IOException ex)
        {
            handleOpeningTargetCodeFileFailed(f, ex, handler);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns the writer to use to output target code to
        a file with the specified base name. The file will be left unchanged
        if, when the writer is closed, what was written is the same as the
        file's contents.

        @param dir the pathname of the target/object code directory that is
        to contain the file that the writer will write to
        @param baseFilename the base filename of the file that the writer
        is to write to: it does not include any directories or an extension
        @param extension the extension of the file that the writer is to
        write to: it should include the leading extension separator (usually
        '.')
        @return the writer to use to output the target code
        @exception FatalErrorException thrown iff the writer could not be
        created
        @see FileUpdatingIndentWriter
    */
    public static IndentWriter createUpdatingWriter(File dir,
        String baseFilename, String extension, ErrorHandler handler)
        throws FatalErrorException
    {
        Assert.require(dir != null);
        Assert.require(baseFilename != null);
        Assert.require(extension != null);
        Assert.require(handler != null);

        IndentWriter result = null;

        File f = new File(dir, baseFilename + extension);
        try
        {
            result = FileUpdatingIndentWriter.create(f);
        }
        catch (IOException ex)
        {
            handleOpeningTargetCodeFileFailed(f, ex, handler);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        @return a new StringIndentWriter
    */
    public static StringIndentWriter createStringIndentWriter()
    {
        StringIndentWriter result =
            new StringIndentWriter(createStringWriter());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a new StringWriter
    */
    public static StringWriter createStringWriter()
    {
        StringWriter result = new StringWriter(INITIAL_WRITER_CAPACITY);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Handles the situation where an attempt to output target code
        failed.

        @param ex the exception that signalled the failed attempt to output
        target code
        @param handler the error handler to use in handling the failure to
        output the target code
        @exception FatalErrorException thrown if failing to output the
        target code is a fatal error
    */
    public static void
        handleWritingTargetCodeFailed(CodeGenerationIoException ex,
                                      ErrorHandler handler)
    {
        Assert.require(ex != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(WRITING_TARGET_CODE_FAILED_MSG,
                       ex.getLocalizedMessage());
// TODO: does this need to be fatal ???!!!???
        handleError(FATAL_ERROR_LEVEL, msg, handler);
    }

    /**
        Closes the specified writer, which was presumably being used to
        output target code.

        @param w the target code writer to close
        @param handler the error handler to use to handle the case where
        'w' could not be closed
    */
    public static void close(Writer w, ErrorHandler handler)
    {
        // 'w' may be null
        Assert.require(handler != null);

        if (w != null)
        {
            try
            {
                w.close();
            }
            catch (IOException ex)
            {
                // Try to at least flush everything out to 'w'.
                Io.tryToFlush(w);

                String msg = _resources.
                    getMessage(CLOSING_TARGET_CODE_FILE_FAILED_MSG,
                               ex.getLocalizedMessage());
                handleError(NON_FATAL_ERROR_LEVEL, msg, handler);
            }
        }
    }


    /**
        Writes out the specified string of target code followed by a
        newline.

        @param w the writer to use to write 'str'
        @param str the string of target code to write
    */
    public static void writeLine(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        write(w, str);
        write(w, Io.NL);
    }

    /**
        Writes out a newline.

        @param w the writer to use to write the newline
    */
    public static void writeLine(IndentWriter w)
    {
        Assert.require(w != null);

        write(w, Io.NL);
    }

    /**
        Reduces 'w''s indentation level by one, writes out 'str' followed by
        a newline, then increases 'w''s indentation level by one.

        @param w the writer to use
        @param str the string to write out
        @see #writeLine(IndentWriter, String)
        @see #indentLess(IndentWriter)
        @see #indentMore(IndentWriter)
    */
    public static void writeLineIndentedLess(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(w.indentLevel() > 0);
        Assert.require(str != null);

        indentLess(w);
        try
        {
            writeLine(w, str);
        }
        finally
        {
            indentMore(w);
        }
    }

    /**
        Reduces 'w''s indentation level to zero, writes out 'str' followed by
        a newline, then increases 'w''s indentation level to where it was.

        @param w the writer to use
        @param str the string to write out
        @see #writeUnindented(IndentWriter, String)
        @see #writeLine(IndentWriter, String)
        @see #indentLess(IndentWriter)
        @see #indentMore(IndentWriter)
    */
    public static void writeLineUnindented(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        int oldLevel = w.indentLevel();
        try
        {
            w.setIndentLevel(0);
            writeLine(w, str);
        }
        finally
        {
            w.setIndentLevel(oldLevel);
        }
    }

    /**
        Reduces 'w''s indentation level to zero, writes out 'str', then
        increases 'w''s indentation level to where it was.
        <p>
        Note: this method won't have any effect unless 'str' is being written
        at the start of a line.

        @param w the writer to use
        @param str the string to write out
        @see #writeLineUnindented(IndentWriter, String)
        @see #indentLess(IndentWriter)
        @see #indentMore(IndentWriter)
    */
    public static void writeUnindented(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        int oldLevel = w.indentLevel();
        try
        {
            w.setIndentLevel(0);
            write(w, str);
        }
        finally
        {
            w.setIndentLevel(oldLevel);
        }
    }

    /**
        Causes both 'w1' and 'w2' to write everything written to them to the
        writers they were constructed from.

        @param w1 a PostponedIndentWriter
        @param w2 another PostponedIndentWriter
    */
    public static void writePostponed(PostponedIndentWriter w1,
                                      PostponedIndentWriter w2)
    {
        Assert.require(w1 != null);
        Assert.require(w2 != null);

        writePostponed(w1);
        writePostponed(w2);
    }

    /**
        Causes 'w' to write everything written to it to the writer it was
        constructed from.

        @param w a PostponedIndentWriter
    */
    public static void writePostponed(PostponedIndentWriter w)
    {
        Assert.require(w != null);

        try
        {
            w.writePostponed();
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Causes both 'w1' and 'w2' to write everything written to them to the
        writers they were constructed from as though it is only a part of a
        line: in particular, it won't be indented.

        @param w1 a PostponedIndentWriter
        @param w2 another PostponedIndentWriter
    */
    public static void writePostponedAsPartialLine(PostponedIndentWriter w1,
                                                   PostponedIndentWriter w2)
    {
        Assert.require(w1 != null);
        Assert.require(w2 != null);

        writePostponedAsPartialLine(w1);
        writePostponedAsPartialLine(w2);
    }

    /**
        Causes 'w' to write everything written to it to the writer it was
        constructed from as though it is only a part of a line: in
        particular, it won't be indented.

        @param w a PostponedIndentWriter
    */
    public static void writePostponedAsPartialLine(PostponedIndentWriter w)
    {
        Assert.require(w != null);

        try
        {
            w.writePostponedAsPartialLine();
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Causes 'pw' to write everything written to it to 'w' as though it is
        only a part of a line: in particular, it won't be indented.

        @param pw a PostponedIndentWriter
        @param w the writer it is to write to (instead of the writer it was
        created from)
    */
    public static void
        writePostponedAsPartialLineTo(PostponedIndentWriter pw,
                                      IndentWriter w)
    {
        Assert.require(pw != null);
        Assert.require(w != null);

        try
        {
            pw.writePostponedAsPartialLineTo(w);
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Writes out the specified string of target code.

        @param w the writer to use to write 'str'
        @param str the string of target code to write
    */
    public static void write(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        try
        {
            w.write(str);
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Writes out the specified character of target code.

        @param w the writer to use to write 'ch'
        @param ch the character of target code to write
    */
    public static void write(IndentWriter w, char ch)
    {
        Assert.require(w != null);

        try
        {
            w.write((int) ch);
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Writes out (the string representation of) the specified integer as
        target code.

        @param w the writer to use to write 'i'
        @param i the integer whose string representation is to be written
        out as target code
    */
    public static void write(IndentWriter w, int i)
    {
        Assert.require(w != null);

        try
        {
            w.write(String.valueOf(i));
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }

    /**
        Indents all of the target code written using the specified writer one
        more level.

        @param w the writer
    */
    public static void indentMore(IndentWriter w)
    {
        Assert.require(w != null);

        w.incrementIndentLevel();
    }

    /**
        Indents all of the target code written using the specified writer one
        less level.

        @param w the writer
    */
    public static void indentLess(IndentWriter w)
    {
        Assert.require(w != null);

        w.decrementIndentLevel();
    }


    /**
        Handles the code generation error described by the specified
        information.

        @param level the severity of the error
        @param description a description of why the error occurred
        @param handler the error handler to use to handle the error
    */
    public static void handleError(int level, String description,
                                   ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(handler != null);

        handler.handle(new CodeGenerationError(level, description));
    }


    // Protected methods

    /**
        Handles the case where a file that it to have target code written
        into it could not be opened due to 'ex' having been thrown.

        @param f the pathname of the file that was being opened
        @param ex the exception that caused opening 'f' to fail
        @param handler the error handler to use to handle the failure to
        open 'f'
        @exception FatalErrorException iff failure to open 'f' is a fatal
        error
    */
    protected static void handleOpeningTargetCodeFileFailed(File f,
                                IOException ex, ErrorHandler handler)
        throws FatalErrorException
    {
        Assert.require(f != null);
        Assert.require(ex != null);
        Assert.require(handler != null);


        String msg = _resources.
            getMessage(OPENING_TARGET_CODE_FILE_FAILED_MSG,
                       f.getPath(), ex.getLocalizedMessage());
        handleError(FATAL_ERROR_LEVEL, msg, handler);
        Assert.unreachable();
                // since this is a fatal error
    }

    /**
        Deletes all of the object code files under the specified directory,
        including any object code files in subdirectories. Subdirectories
        themselves will not be deleted, however.
        <p>
        Note: this method will only delete regular files.

        @param dir the directory under which to delete all of the object code
        files
        @exception IOException if all of the object code files could not be
        deleted
    */
    protected static void deleteObjectCodeUnder(File dir)
        throws IOException
    {
        Assert.require(dir != null);
        Assert.require(dir.isDirectory());

        File[] contents = dir.listFiles();
        for (int i = 0; i < contents.length; i++)
        {
            File f = contents[i];
            if (f.isDirectory())
            {
                deleteObjectCodeUnder(f);
            }
            else if (f.isFile())
            {
                if (f.delete() == false)
                {
                    String msg = _resources.
                        getMessage(OBJECT_FILE_DELETE_FAILED_MSG,
                                   f.getPath());
                    throw new IOException(msg);
                }
            }
        }
    }


    // Private static methods

    /**
        Ensures that the directory with the specified pathname exists,
        creating it if necessary.

        @param dir the pathname of the directory
        @param msgKey the message key for the message to use in reporting
        that the directory couldn't be created: its only argument is a String
        representation of 'dir'
        @exception IOException thrown if 'dir' does not exist and could not
        be created
    */
    private static void ensureDirectoryExists(File dir, String msgKey)
        throws IOException
    {
        Assert.require(dir != null);
        Assert.require(msgKey != null);

        if (dir.isDirectory() == false)
        {
            if (dir.mkdirs() == false)
            {
                // Failed to create 'dir' or one of its ancestor dirs.
                String msg = _resources.
                    getMessage(msgKey, dir.getPath());
                throw new IOException(msg);
            }
        }

        Assert.ensure(dir.isDirectory());
    }
}
