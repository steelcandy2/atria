/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.atria.constructs.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.tokens.AtriaDefaultTokenizer;
import com.steelcandy.plack.atria.constructs.*;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.SourceCodeTokenizer;

import com.steelcandy.plack.common.constructs.AbstractSourceCodeParser;
import com.steelcandy.plack.common.constructs.Parser;

/**
    A source code parser for use in testing that should be similar to the
    type of parser used by Atria interpreters and similar language processing
    tools.
    <p>
    Note: this parser only returns a Construct, so it should only be used in
    tests where the specific type of construct that is parsed does not need
    to be known.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaTestInterpreterParser
    extends AbstractSourceCodeParser
{
    // Protected methods

    /**
        @see AbstractSourceCodeParser#createTokenizerParser
    */
    protected Parser createTokenizerParser()
    {
        Parser result =
            AtriaParserFactory.instance().createDocumentParser();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see AbstractSourceCodeParser#createSourceCodeTokenizer(ErrorHandler)
    */
    protected SourceCodeTokenizer
        createSourceCodeTokenizer(ErrorHandler handler)
    {
        // Assert.ensure(result != null);
        return new AtriaDefaultTokenizer(handler);
    }
}
