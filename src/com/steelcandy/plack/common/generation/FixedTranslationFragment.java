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

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    The class of TranslationFragment that represents a fixed piece of target
    code.

    @author  James MacKay
*/
public class FixedTranslationFragment
    implements TranslationFragment
{
    // Private fields

    /** A fixed piece of target code. */
    private String _code;


    // Constructors

    /**
        Constructs a FixedTranslationFragment from the fixed piece of target
        code that it represents.

        @param code a fixed piece of target code
    */
    public FixedTranslationFragment(String code)
    {
        Assert.require(code != null);

        _code = code;
    }


    // Public methods

    /**
        @see TranslationFragment#isEmpty
    */
    public boolean isEmpty()
    {
        return (_code.length() == 0);
    }

    /**
        @see TranslationFragment#write(IndentWriter)
    */
    public void write(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        w.write(_code);
    }
}
