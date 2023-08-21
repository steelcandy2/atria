/*
 Copyright (C) 2014 by James MacKay.

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

import com.steelcandy.common.io.*;

import java.io.IOException;

/**
    The class of TranslationFragment that represents a fixed piece of target
    code, where that piece of target code can be built up from smaller
    pieces of target code.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class ExpandableFixedTranslationFragment
    implements TranslationFragment
{
    // Private fields

    /**
        A buffer containing the concatenation of smaller pieces of target
        code that our fixed piece of target code will be made up of.
    */
    private StringBuffer _buffer;


    // Constructors

    /**
        Constructs an ExpandableFixedTranslationFragment that initially
        represents an empty piece of target code.
    */
    public ExpandableFixedTranslationFragment()
    {
        _buffer = new StringBuffer();

        Assert.ensure(isEmpty());
    }


    // Public methods

    /**
        Adds the piece of target code 'code' to the end of the fixed piece of
        target code that we represent.

        @param code the piece of target code to append
        @return this
    */
    public ExpandableFixedTranslationFragment add(String code)
    {
        Assert.require(code != null);

        _buffer.append(code);

        return this;
    }

    /**
        Adds the string representation of 'code' to the end of the fixed
        piece of target code that we represent.

        @param code an int whose text representation is the piece of target
        code to append
        @return this
    */
    public ExpandableFixedTranslationFragment add(int code)
    {
        return add(String.valueOf(code));
    }

    /**
        Adds a newline to the end of the fixed piece of target code that we
        represent.

        @return this
    */
    public ExpandableFixedTranslationFragment addNewline()
    {
        return add(Io.NL);
    }


    /**
        @see TranslationFragment#isEmpty
    */
    public boolean isEmpty()
    {
        return (_buffer.length() == 0);
    }

    /**
        @see TranslationFragment#write(IndentWriter)
    */
    public void write(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        w.write(_buffer.toString());
    }
}
