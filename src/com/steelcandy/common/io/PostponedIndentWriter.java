/*
 Copyright (C) 2009-2011 by James MacKay.

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

/**
    A class of IndentWriter that can later be used to write everything
    written to it to the IndentWriter that it was constructed from, or
    another IndentWriter.
    <p>
    While a StringIndentWriter can be written to and then have its contents
    written to another IndentWriter later, using an instance of this class
    allows lines to be unindented as many levels as would be allowed by the
    IndentWriter from which it was constructed.
    <p>
    Note: instances of this class are almost always created by calling
    createPostponed() on another IndentWriter: they're rarely created by
    calling one of our constructors directly.

    @author James MacKay
    @version $Revision: 1.4 $
    @see IndentWriter#createPostponed
*/
public class PostponedIndentWriter
    extends StringIndentWriter
{
    // Private fields

    /**
        The writer to which will (probably) eventually be written
        whatever's written to us.
    */
    private IndentWriter _writer;


    // Constructors

    /**
        Constructs a PostponedIndentWriter.

        @param w the IndentWriter to which will (probably) be written
        whatever's written to the PostponedIndentWriter being constructed
        @see IndentWriter#createPostponed
    */
    public PostponedIndentWriter(IndentWriter w)
    {
        Assert.require(w != null);

        _writer = w;
        setIndentLevel(w.indentLevel());
        setIndentSize(w.indentSize());
    }


    // Public methods

    /**
        Writes whatever's been written to us to the IndentWriter that we
        were constructed from.
        <p>
        Note: any attempts to write to this instance after this method has
        been called will fail.

        @exception IOException is thrown if it occurs in writing to the
        IndentWriter
        @see #PostponedIndentWriter(IndentWriter)
        @see #writePostponedTo(IndentWriter)
    */
    public void writePostponed()
        throws IOException
    {
        writePostponedTo(_writer);
    }

    /**
        Writes whatever's been written to us to 'w'.
        <p>
        Note: any attempts to write to this instance after this method has
        been called will fail.

        @param w the writer to write to
        @exception IOException is thrown if it occurs in writing to 'w'
        @see #writePostponed
    */
    public void writePostponedTo(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        Assert.require(w != null);

        int oldLevel = w.indentLevel();
        try
        {
            w.setIndentLevel(0);
            w.write(contents());
        }
        finally
        {
            w.setIndentLevel(oldLevel);
        }
    }


    /**
        Writes whatever's been written to us to the IndentWriter that we
        were constructed from as though it is only a part of a line: in
        particular, it won't be indented.
        <p>
        No checks are made on what has been written using this writer to
        ensure that it can represent part of a line.
        <p>
        Note: any attempts to write to this instance after this method has
        been called will fail.

        @exception IOException is thrown if it occurs in writing to the
        IndentWriter
        @see #PostponedIndentWriter(IndentWriter)
        @see #writePostponedAsPartialLineTo(IndentWriter)
    */
    public void writePostponedAsPartialLine()
        throws IOException
    {
        writePostponedAsPartialLineTo(_writer);
    }

    /**
        Writes whatever's been written to us to 'w' as though it is only part
        of a line: in particular, it won't be indented.
        <p>
        No checks are made on what has been written using this writer to
        ensure that it can represent part of a line.
        <p>
        Note: any attempts to write to this instance after this method has
        been called will fail.

        @exception IOException is thrown if it occurs in writing to 'w'
    */
    public void writePostponedAsPartialLineTo(IndentWriter w)
        throws IOException
    {
// TODO: is there a better way to do this ???!!!!????
// - right now this will be incorrect when the partial line starts with a line prefix
        String linePart = contents();
        int prefixSize = indentSize() * indentLevel();
        if (linePart.startsWith(IndentWriter.prefix(indentLevel(),
                                                    indentSize())))
        {
            linePart = linePart.substring(prefixSize);
        }
        w.write(linePart);
    }
}
