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

package com.steelcandy.plack.atria.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A filter tokenizer that ensures that all lines start with an INDENT
    token: if a line doesn't start with one then a token with ID INDENT and
    that represents zero levels of indentation will be prepended to it.
    <p>
    This tokenizer can be applied to token streams either before or after it
    has been processed to convert physical lines into logical lines.

    @author James MacKay
    @version $Revision: 1.10 $
*/
public class IndentAllLinesTokenizer
    extends AtriaFilterTokenizer
{
    // Constants

    /**
        The length of the source fragment represented by a zero-level
        INDENT token.
    */
    private static final int ZERO_INDENT_LENGTH = 0;

    /** The string value of a zero-level INDENT token. */
    private static final String ZERO_INDENT_STRING = "";


    // Constructors

    /**
        Constructs an IndentAllLinesTokenizer.

        @param handler the error handler the tokenizer is to
        use to handle any errors
    */
    public IndentAllLinesTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public IndentAllLinesTokenizer(Resources subtokenizerResources,
                                   String resourceKeyPrefix,
                                   ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Overridden/implemented methods

    /**
        @see BufferedTokenizer#generateTokens
    */
    protected void generateTokens()
    {
        TokenList line = nextSourceLine();
        if (line.isEmpty() == false)
        {
            Token firstToken = line.get(0);
            if (firstToken.id() != AtriaTokenManager.INDENT)
            {
                // The line doesn't start with an INDENT token, so output one
                // that represents zero levels of indentation.
                Token indent = AtriaTokenManager.instance().
                    createIndentToken(firstToken.location().startPosition(),
                                      ZERO_INDENT_LENGTH, ZERO_INDENT_STRING);
                output(indent);
            }
            output(line);  // the original line
        }
    }
}
