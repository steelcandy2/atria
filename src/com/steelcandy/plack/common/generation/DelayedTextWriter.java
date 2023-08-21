/*
 Copyright (C) 2004-2009 by James MacKay.

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

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    An abstract base class for classes that delay the writing of a fragment
    of textual target code.
    <p>
    This class is usually extended by anonymous inner classes in order to
    allow pieces of target code to be pased into methods.

    @author  James MacKay
    @see NullDelayedTextWriter
*/
public abstract class DelayedTextWriter
{
    // Public methods

    /**
        Writes the text now using the specified writer.
        <p>
        Note: this method is final only to prevent subclasses from
        accidentally overriding the wrong method.

        @param w the writer to use
        @exception CodeGenerationIoException thrown if an I/O error occurs in
        writing the text
    */
    public final void writeNow(IndentWriter w)
        throws CodeGenerationIoException
    {
        Assert.require(w != null);

        try
        {
            writeTextNow(w);
        }
        catch (IOException ex)
        {
            throw new CodeGenerationIoException(ex);
        }
    }


    // Abstract methods

    /**
        Writes the text now using the specified writer.

        @param w the writer to use
        @exception IOException thrown if an I/O error occurs in writing the
        text
    */
    protected abstract void writeTextNow(IndentWriter w)
        throws IOException;
        // Assert.require(w != null);
}
