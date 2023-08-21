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

import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.common.*;

/**
    A tokenizer that removes (the tokens representing) blank lines from (a
    stream of tokens representing) a piece of Atria source code.
    <p>
    This tokenizer doesn't make any assumptions about the tokens that it
    processes.

    @author James MacKay
*/
public class RemoveBlankLinesTokenizer
    extends AtriaFilterTokenizer
{
    // Constructors

    /**
        Constructs a RemoveBlankLinesTokenizer.

        @param handler the error handler the tokenizer is to
        use to handle any errors
    */
    public RemoveBlankLinesTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemoveBlankLinesTokenizer(Resources subtokenizerResources,
                                     String resourceKeyPrefix,
                                     ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see BufferedTokenizer#generateTokens
    */
    protected void generateTokens()
    {
        // Process lines until there are no more or we've output
        // a non-blank one, whichever comes first. (This method
        // must output at least one token when it's called, unless
        // the end of the token stream has been reached.)
        while (source().hasNext())
        {
            TokenList line = nextSourceLine();
            if (isBlankLine(line) == false)
            {
                Assert.check(line.isEmpty() == false);
                output(line);
                break;
            }
            // Otherwise do nothing, effectively removing the
            // current (blank) line.
        }
    }
}
