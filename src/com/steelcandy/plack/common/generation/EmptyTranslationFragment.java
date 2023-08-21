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
    The class of TranslationFragment that represents an empty piece of target
    code.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class EmptyTranslationFragment
    implements TranslationFragment
{
    // Constants

    /** The single instance of this class. */
    private static final EmptyTranslationFragment
        _instance = new EmptyTranslationFragment();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static EmptyTranslationFragment instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs our single instance.
    */
    private EmptyTranslationFragment()
    {
        // empty
    }


    // Public methods

    /**
        @see TranslationFragment#isEmpty
    */
    public boolean isEmpty()
    {
        return true;
    }

    /**
        @see TranslationFragment#write(IndentWriter)
    */
    public void write(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        // empty - nothing to write
    }
}
