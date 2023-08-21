/*
 Copyright (C) 2002-2004 by James MacKay.

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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ReadError;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.Utilities;

import java.io.*;

/**
    A CharacterIterator that iterates over the characters in a piece of
    source code.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class SourceCodeCharacterIterator
    extends CharacterIterator
{
    // Private fields

    /**
        The piece of source code whose characters this iterator iterates
        over.
    */
    private SourceCode _sourceCode;


    // Constructors

    /**
        Constructs a SourceCodeCharacterIterator.

        @param code the piece of source code whose characters this iterator
        iterates over
        @param errorChar the character to be returned from this iterator when
        an error occurs in reading the character from the source
        @param handler the error handler for the iterator to use in handling
        errors
        @exception IOException thrown if the source code could not be opened
        for reading
    */
    public SourceCodeCharacterIterator(SourceCode code, char errorChar,
                                       ErrorHandler handler)
        throws IOException
    {
        super(code.reader(), errorChar, handler);
        Assert.require(code != null);

        _sourceCode = code;
    }


    // Protected methods

    /**
        @see CharacterIterator#sourceCode
    */
    protected SourceCode sourceCode()
    {
        return _sourceCode;
    }
}
