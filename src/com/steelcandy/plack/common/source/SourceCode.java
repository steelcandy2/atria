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

import com.steelcandy.common.Resources;

import com.steelcandy.common.text.TextUtilities;

import java.io.*;

/**
    Base class for all classes that represent a piece of source code.

    @author James MacKay
*/
public abstract class SourceCode
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        SourceCodeResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_SUCH_LINE_MSG = "NO_SUCH_LINE_MSG";
    private static final String
        NOT_ENOUGH_LINES_MSG = "NOT_ENOUGH_LINES_MSG";
    private static final String
        LINES_BETWEEN_FORMAT = "LINES_BETWEEN_FORMAT";
    private static final String
        UNKNOWN_SOURCE_CODE_FULL_NAME_MSG =
            "UNKNOWN_SOURCE_CODE_FULL_NAME_MSG";
    private static final String
        UNKNOWN_SOURCE_CODE_NAME_MSG =
            "UNKNOWN_SOURCE_CODE_NAME_MSG";

    private static final String
        UNKNOWN_SOURCE_CODE_FULL_NAME = _resources.
            getMessage(UNKNOWN_SOURCE_CODE_FULL_NAME_MSG);
    private static final String
        UNKNOWN_SOURCE_CODE_NAME = _resources.
            getMessage(UNKNOWN_SOURCE_CODE_NAME_MSG);


    // Public static methods

    /**
        Returns the full name associated with the specified source code.
        <p>
        If the source code is null then the generic full name for an unknown
        piece of source code is returned.

        @param code a piece of source code
        @return the full name of 'code'
    */
    public static String fullName(SourceCode code)
    {
        String result;

        if (code != null)
        {
            result = code.fullName();
        }
        else
        {
            result = UNKNOWN_SOURCE_CODE_FULL_NAME;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the name associated with the specified piece of source code.
        <p>
        If the source code is null then the generic name for an unknown piece
        of source code is returned.

        @param code a piece of source code
        @return the name of 'code'
    */
    public static String name(SourceCode code)
    {
        String result;

        if (code != null)
        {
            result = code.name();
        }
        else
        {
            result = UNKNOWN_SOURCE_CODE_NAME;
        }

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        Returns the full name associated with this source code.
        <p>
        The name does not have to be unique, though it could be useful since
        it will often be used to identify this source code object.

        @return this source code's full name
        @see #name
    */
    public abstract String fullName();
        // Assert.ensure(result != null);

    /**
        Returns the name associated with this source code.
        <p>
        Unlike the fullName(), this name doesn't have to be unique: if it
        differs from the full name then it is usually a shorter, more
        convenient (e.g. for display) form of the name. For example, a
        SourceCode object that represents a source code file could have the
        file's pathname as its full name, and its filename stripped of any
        directory information as its name.

        @return this source code's name
        @see #fullName
    */
    public abstract String name();
        // Assert.ensure(result != null);

    /**
        Returns a Reader from which this entire piece of source code can be
        read.

        @return a Reader from which this source code fragment can be read
        @exception IOException if an I/O error occurs while getting the
        reader
    */
    public abstract Reader reader()
        throws IOException;

    /**
        Returns a SourceCodeString object that represents the same source
        code as does this SourceCode object.
        <p>
        The (full) name of the SourceCodeString should also be set to be the
        same as this object's (full) name.

        @return a SourceCodeString object that represents the same source
        code as does this SourceCode object
        @exception IOException if an I/O error occurs during the conversion
    */
    public abstract SourceCodeString toSourceCodeString()
        throws IOException;
        // Assert.ensure(result != null);

    /**
        Returns a SourceCodeString object that represents the same source
        code as does this SourceCode object, provided that the size of the
        source code (in characters) is less than or equal to the specified
        size: otherwise this SourceCode object is returned.
        <p>
        The (full) name of the SourceCodeString should also be set to be the
        same as this object's (full) name.

        @param maxSize the maximum size that the source code represented by
        this object can have and still have a SourceCodeString representing
        the source code be returned
        @return a SourceCode object representing the same source code as this
        SourceCode object
    */
    public abstract SourceCode toSourceCodeString(long maxSize)
        throws IOException;
        // Assert.require(maxSize >= 0);
        // Assert.ensure(result != null);


    // Source fragment retrieval methods

    /**
        Returns the fragment of the specified line of source code that starts
        with the character at the specified start offset and ends with the
        character at the specified end offset.

        @param lineNumber the line number of the line of source code
        containing the line fragment
        @param startOffset the offset in the line of the first character of
        the line fragment to return
        @param pastEndOffset the offset in the line one past the offset of
        the last character of the line fragment to return
        @exception IOException thrown if an I/O exception occurs while trying
        to get the line fragment
        @exception IndexOutOfBoundsException thrown if this piece of source
        code is not long enough to contain the specified line fragment
    */
    public String lineFragment(int lineNumber,
                               int startOffset, int pastEndOffset)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(lineNumber >= 0);
        Assert.require(startOffset >= 0);
        Assert.require(pastEndOffset >= startOffset);

        String result = lines(lineNumber, startOffset,
                              lineNumber, pastEndOffset);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the specified line of source code.

        @param lineNumber the line number of the line of source code to
        return
        @exception IOException thrown if an I/O exception occurs while
        trying to get the line
        @exception IndexOutOfBoundsException thrown if this piece of
        source code is not long enough to contain the specified line
    */
    public String line(int lineNumber)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(lineNumber >= 0);

        String result = lines(lineNumber, lineNumber);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the fragment of source code starting at the beginning of the
        specified starting line and continuing to the end of the specified
        ending line.

        @param startLineNumber the line number of the first line in the
        fragment
        @param endLineNumber the line number of the last line in the fragment
        @exception IOException thrown if an I/O exception occurs while trying
        to get the lines
        @exception IndexOutOfBoundsException thrown if this piece of source
        code is not long enough to contain the specified lines
    */
    public abstract String lines(int startLineNumber, int endLineNumber)
        throws IOException, IndexOutOfBoundsException;
        // Assert.require(startLineNumber >= 0);
        // Assert.require(endLineNumber >= startLineNumber);
        // Assert.ensure(result != null);

    /**
        Returns the fragment of source code starting at the specified offset
        in the specified starting line and continuing to the specified
        offset (inclusive) in the specified ending line.

        @param startLineNumber the line number of the line containing the
        first part of the fragment
        @param startOffset the offset into the startLineNumber'th line of the
        first character of the fragment
        @param endLineNumber the line number of the line containing the last
        part of the fragment
        @param pastEndOffset the offset into the endLineNumber'th line that
        is one position past the offset of the last character of the fragment
        @return the fragment of source code
        @exception IOException thrown if an I/O exception occurs while trying
        to get the lines
        @exception IndexOutOfBoundsException thrown if this piece of source
        code is not long enough to contain the specified lines
    */
    public abstract String lines(int startLineNumber, int startOffset,
                                 int endLineNumber, int pastEndOffset)
        throws IOException, IndexOutOfBoundsException;
        // Assert.require(startLineNumber >= 0);
        // Assert.require(endLineNumber >= startLineNumber);
        // Assert.require(startOffset >= 0);
        // Assert.require(pastEndOffset >= startOffset);
        // Assert.ensure(result != null);

    /**
        Returns the fragment of source code at the specified location in this
        piece of source code.

        @param loc the location in this piece of source code of the fragment
        to return
        @return the fragment of source code at the specified location
        @exception IOException thrown if an I/O exception occurs while trying
        to get the fragment
        @exception IndexOutOfBoundsException thrown if this piece of source
        code is not long enough to contain the specified location
    */
    public String fragmentAt(SourceLocation loc)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(loc != null);

        String result = loc.fragmentOf(this);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        Returns the lines of source code with line numbers in the specified
        (inclusive) range, as read from the specified SourceCodeReader.

        @param r the SourceCodeReader from which to read the lines of source
        code
        @param startLineNumber the line number of the first line
        @param endLineNumber the line number of the last line
        @exception IOException thrown if an I/O exception occurs while trying
        to get the lines
        @exception IndexOutOfBoundsException thrown if this piece of source
        code is not long enough to contain the specified lines
    */
    protected String lines(SourceCodeReader r,
                           int startLineNumber, int endLineNumber)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(startLineNumber >= 0);
        Assert.require(endLineNumber >= startLineNumber);

        skipLinesBefore(r, startLineNumber);

        // Return the startLineNumber'th through endLineNumber'th lines.
        StringBuffer buf = new StringBuffer();
        for (int i = startLineNumber; i <= endLineNumber; i++)
        {
            buf.append(readLine(r, i));
            if (i < endLineNumber)
            {
                buf.append(TextUtilities.NL);
            }
        }

        // Assert.ensure(result != null);
        return buf.toString();
    }

    /**
        Returns the fragment of source code - as read from the specified
        reader - starting at the specified offset in the specified starting
        line and continuing to the specified offset (inclusive) in the
        specified ending line.
        <p>
        Any line termination characters at the end of the last line will
        <em>not</em> be removed.

        @param r the reader from which to read the lines of source code
        @param startLineNumber the line number of the line containing the
        first part of the fragment
        @param startOffset the offset into the startLineNumber'th line of the
        first character of the fragment
        @param endLineNumber the line number of the line containing the last
        part of the fragment
        @param pastEndOffset the offset into the endLineNumber'th line that
        is one position past the last character of the fragment
        @return the fragment of source code
        @exception IOException thrown if an I/O exception occurs while trying
        to get the lines
        @exception IndexOutOfBoundsException thrown if the source code is not
        long enough to contain the specified lines
    */
    protected String lines(SourceCodeReader r,
                           int startLineNumber, int startOffset,
                           int endLineNumber, int pastEndOffset)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(startLineNumber >= 0);
        Assert.require(endLineNumber >= startLineNumber);
        Assert.require(startOffset >= 0);
        Assert.require(pastEndOffset >= startOffset);

        skipLinesBefore(r, startLineNumber);

        StringBuffer buf = new StringBuffer();
        for (int i = startLineNumber; i <= endLineNumber; i++)
        {
            String line = readLine(r, i);

            if (i == startLineNumber)
            {
                if (i == endLineNumber)
                {
// System.err.println("---> line length = " + line.length() + "; start offset = " + startOffset +", past end offset = " + pastEndOffset + "\n    line = [" + line + "]");
                    line = line.substring(startOffset, pastEndOffset);
                }
                else
                {
                    line = line.substring(startOffset);
                }
            }
            else if (i == endLineNumber)
            {
                line = line.substring(0, pastEndOffset);
            }

            buf.append(line);
            if (i < endLineNumber)
            {
                buf.append(TextUtilities.NL);
            }
        }   // for

        // Assert.ensure(result != null);
        return buf.toString();
    }

    /**
        Skips the lines before the one with the specified line number in the
        source code read from the specified reader.

        @param r the reader to skip the lines in (by reading them and
        discarding them)
        @param lineNumber the line number of the line to skip all of the
        lines before
        @exception IOException thrown if an I/O error occurs while reading
        the lines to be skipped
        @exception IndexOutOfBoundsException thrown if there are not enough
        lines in the source code to skip the required number of lines: there
        must be at least (lineNumber - 1) lines
    */
    protected void skipLinesBefore(SourceCodeReader r, int lineNumber)
        throws IOException, IndexOutOfBoundsException
    {
        for (int i = 1; i < lineNumber; i++)
        {
            if (r.readLine() == null)
            {
                String msg = _resources.
                    getMessage(NOT_ENOUGH_LINES_MSG,
                               Integer.toString(lineNumber),
                               Integer.toString(i));
                throw new IndexOutOfBoundsException(msg);
            }
        }
    }

    /**
        Reads and returns the next line - assumed to be the
        <code>lineNumber</code>'th one - from the specified reader.

        @param r the reader to read the next line from
        @param lineNumber the line number of the next line to be read from
        the reader
        @return the next line read from the reader
        @exception IOException thrown if an I/O error occurs while reading
        the line
        @exception IndexOutOfBoundsException thrown if the reader doesn't
        have a next line
    */
    protected String readLine(SourceCodeReader r, int lineNumber)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(r != null);

        StringBuffer result = new StringBuffer();
        boolean lineMissing = true;
        int ch;
        while ((ch = r.read()) != -1)
        {
            lineMissing = false;
            result.append((char) ch);

            // Include the newline, which can be a linefeed ('\n'), a
            // carriage return ('\r') or a carriage return followed by a
            // linefeed ('\r\n').
            if (ch == '\n')
            {
                break;
            }
            else if (ch == '\r')
            {
                Assert.check(r.markSupported());
                r.mark(0);
                ch = r.read();
                if (ch == '\n')
                {
                    result.append((char) ch);
                }
                else if (ch != -1)
                {
                    // 'Put back' the last character read.
                    r.reset();
                }
                break;
            }
        }

        if (lineMissing)
        {
            String msg = _resources.getMessage(NO_SUCH_LINE_MSG,
                                        Integer.toString(lineNumber));
            throw new IndexOutOfBoundsException(msg);
        }

        Assert.ensure(result != null);
        return result.toString();
    }
}
