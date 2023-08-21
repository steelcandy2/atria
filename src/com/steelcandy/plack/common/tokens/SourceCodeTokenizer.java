/*
 Copyright (C) 2001-2008 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.base.*;
import com.steelcandy.plack.common.source.SourceCode;

/**
    The interface implemented by all Tokenizers that tokenize a piece of
    source code represented by a SourceCode object.
    <p>
    An instance's <code>initialize(SourceCode)</code> method must be called
    before any of its other methods are called.

    @author James MacKay
*/
public interface SourceCodeTokenizer
    extends Tokenizer
{
    // Public methods

    /**
        Initializes this tokenizer and sets the specified source code to be
        the source code that it tokenizes.

        @param sourceCode the source code that this tokenizer will tokenize
    */
    public void initialize(SourceCode sourceCode);
        // Assert.require(sourceCode != null);

    /**
        Closes this tokenizer, allowing it to clean up after it will no
        longer be used.
    */
    public void close();
}
