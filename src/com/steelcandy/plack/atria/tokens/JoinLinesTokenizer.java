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
    A shell filter tokenizer that wraps the the filter subtokenizers needed
    to convert physical lines into logical lines.
    <p>
    This tokenizer's subtokenizers should usually include at least the
    following tokenizers in the following order:
    <ul>
        <li>a PhysicalToLogicalLinesTokenizer, and
        <li>a RemoveExplicitLineContinuersTokenizer.
    </ul>

    @author James MacKay
    @see PhysicalToLogicalLinesTokenizer
    @see RemoveExplicitLineContinuersTokenizer
*/
public class JoinLinesTokenizer
    extends ReflectiveShellFilterTokenizer
{
    // Constants

    /**
        The suffix that this tokenizer appends to the resource key prefix it
        is constructed to create the actual resource key prefix used to look
        up the fully-qualified class names of this tokenizer's subtokenizers.
    */
    private static final String PREFIX_SUFFIX = "joinlines";


    // Constructors

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public JoinLinesTokenizer(Resources subtokenizerResources,
                              String resourceKeyPrefix,
                              ErrorHandler handler)
    {
        super(subtokenizerResources,
              Resources.keyConcat(resourceKeyPrefix, PREFIX_SUFFIX),
              handler);
    }
}
