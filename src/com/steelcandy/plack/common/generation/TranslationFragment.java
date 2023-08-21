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
    The interface implemented by classes that represent fragments of the
    translation of a language's source code into the target language.

    @author  James MacKay
*/
public interface TranslationFragment
{
    // Public methods

    /**
        @return true iff there's no target code in this fragment
    */
    public boolean isEmpty();

    /**
        Writes this translation fragment using 'w'.

        @param w the writer to use
        @throws IOException if an error occurs in writing using 'w'
    */
    public void write(IndentWriter w)
        throws IOException;
        // Assert.require(w != null);
}
