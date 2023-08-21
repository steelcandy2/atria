/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;

import java.io.IOException;
import java.io.Writer;  // javadocs only

/**
    The interface implemented by writers that can output constructs and their
    various parts.
    <p>
    Note that this class does <em>not</em> extend Writer.

    @author James MacKay
    @version $Revision: 1.8 $
    @see Writer
*/
public interface ConstructWriter
    extends ConstructWriterOptions
{
    // Public methods

    /**
        Sets the writer used to output the construct and its parts to the
        specified writer.

        @param w the writer to use to output the construct and its parts
        @see #doCloseWriter(boolean)
    */
    public void setWriter(Writer w);

    /**
        Sets whether closing this construct writer should also close the
        writer that it uses to output the construct and its parts.

        @param doClose true if we are to close our writer, and false if we
        are not to close it
        @see #setWriter(Writer)
    */
    public void doCloseWriter(boolean doClose);

    /**
        @return this writer's configuration
    */
    public ConstructWriterConfiguration configuration();

    /**
        Sets the number of spaces output per level of indentation to the
        specified number.

        @param size the number of spaces in the string output for each level
        of indentation
    */
    public void setIndentSize(int size);

    /**
        @return the current indent level
    */
    public int indentLevel();

    /**
        @return the current indent size
    */
    public int indentSize();

    /**
        Closes this writer.

        @exception IOException thrown if this writer could be closed
        @see Writer#close
    */
    public void close()
        throws IOException;


    // Output methods

    /**
        Outputs the start of the specified construct, including the
        information (at least potentially) common to all constructs.

        @param c the construct to start writing
        @param name the construct's name, or null if it doesn't have one (a
        construct usually only has a name when it is a subconstruct)
        @param source the source code that 'c' represents a fragment of, or
        null if it isn't available
        @exception IOException if an error occurs writing the start of the
        construct
    */
    public void writeConstructStart(Construct c, String name,
                                    SourceCode source)
        throws IOException;
        // Assert.require(c != null);
        // 'name' can be null
        // 'source' can be null

    /**
        Outputs the end of the specified construct, and also signals to this
        writer that we've reached the end of a construct.

        @param c the construct to finish writing
        @param name the construct's name, or null if it doesn't have one (a
        construct usually only has a name when it is a subconstruct)
        @param source the source code that 'c' represents a fragment of, or
        null if it isn't available
        @exception IOException if an error occurs writing the end of the
        construct
    */
    public void
        writeConstructEnd(Construct c, String name, SourceCode source)
        throws IOException;
        // Assert.require(c != null);
        // 'name' can be null
        // 'source' can be null

    /**
        Writes the specified construct flags.

        @param flags the construct flags to output
        @exception IOException if an error occurs writing the flags'
        information
    */
    public void writeFlags(int flags)
        throws IOException;
        // Assert.require(indentLevel() >= 0);

    /**
        Writes the specified required subconstruct.

        @param name the subconstruct's name
        @param sc the subconstruct to output
        @param source the source code that the subconstruct represents a
        fragment of, or null if it isn't available
        @exception IOException if an error occurs writing the subconstruct's
        information
    */
    public void writeSubconstruct(String name, Construct sc,
                                  SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // 'sc' and/or 'source' can be null

    /**
        Writes the specified optional subconstruct.

        @param name the subconstruct's name
        @param sc the subconstruct to output
        @param source the source code that the subconstruct represents a
        fragment of, or null if it isn't available
        @exception IOException if an error occurs writing the construct's
        information
    */
    public void writeOptionalSubconstruct(String name, Construct sc,
                                          SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // 'sc' and/or 'source' can be null

    /**
        Writes the specified required subconstruct list.

        @param name the subconstructs' name
        @param list the list of subconstructs to output, or null if the
        (required) subconstruct list is absent or empty
        @param source the source code that each of the subconstructs in
        'list' represents a fragment of, or null if it isn't available
        @exception IOException if an error occurs writing the subconstructs'
        information
    */
    public void writeSubconstructList(String name, ConstructList list,
                                      SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // 'list' and/or 'source' can be null

    /**
        Writes the specified optional subconstruct list.

        @param name the subconstructs' name
        @param list the list of subconstructs to output, or null if the
        optional subconstruct list is absent or empty
        @param source the source code that each of the subconstructs in
        'list' represents a fragment of, or null if it isn't available
        @exception IOException if an error occurs writing the subconstructs'
        information
    */
    public void
        writeOptionalSubconstructList(String name, ConstructList list,
                                      SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // 'list' and/or 'source' can be null
}
