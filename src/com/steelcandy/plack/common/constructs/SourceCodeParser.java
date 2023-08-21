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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.plack.common.tokens.SourceCodeTokenizer;
import com.steelcandy.plack.common.tokens.Tokenizer;

/**
    The interface implemented by all parsers that can parse source code
    directly, instead of requiring that it be given a tokenizer.
    <p>
    (An instance of this class almost always still uses a tokenizer: it just
    provides it itself rather than requiring that it be passed into it.)

    @author James MacKay
    @version $Revision: 1.1 $
    @see Tokenizer
    @see SourceCodeTokenizer
*/
public interface SourceCodeParser
{
    // Public methods

    /**
        Parses the specified source code into a construct and returns that
        construct.

        @param source the source code to parse
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the construct resulting from parsing the source code, or null
        if the construct is missing or too malformed to be even partially
        constructed
    */
    public Construct parse(SourceCode source, ErrorHandler handler);
        // Assert.require(source != null);
        // Assert.require(handler != null);
}
