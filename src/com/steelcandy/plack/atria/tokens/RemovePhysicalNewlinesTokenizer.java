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
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A filter tokenizer that removes all 'physical newlines': newlines that
    are at the end of physical lines but are not also at the end of logical
    lines. It also removes any INDENT tokens that immediately follow physical
    newlines.
    <p>
    This filter assumes that the tokens it processes have already been
    processed by a PhysicalToLogicalLinesTokenizer, since it identifies
    physical newlines from logical ones by assuming that the former do not
    have their IS_END_OF_LINE flag set anymore. (The
    PhysicalToLogicalLinesTokenizer clears this flag on all newlines iff they
    are physical newlines.)
    <p>
    Note that this filter does <em>not</em> remove any INDENT tokens that
    immediately follow physical newlines: they are assumed to be validated
    and removed by an IndentationTokenizer that (eventually) processes the
    output of this filter.

    @author James MacKay
    @see PhysicalToLogicalLinesTokenizer
    @see AtriaTokenManagerBase#NEWLINE
    @see AtriaTokenManagerBase#IS_END_OF_LINE
    @see AtriaTokenManagerBase#INDENT
*/
public class RemovePhysicalNewlinesTokenizer
    extends RemovalFilterTokenizer
{
    // Constructors

    /**
        Constructs a RemovePhysicalNewlinesTokenizer.

        @param handler the error handler the tokenizer is to
        use to handle any errors
    */
    public RemovePhysicalNewlinesTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemovePhysicalNewlinesTokenizer(Resources subtokenizerResources,
                                           String resourceKeyPrefix,
                                           ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see RemovalFilterTokenizer#remove
    */
    protected boolean remove(Token tok)
    {
        // Remove physical newlines: NEWLINE tokens that no longer
        // have their IS_END_OF_LINE flag set.
        return (tok.id() == AtriaTokenManager.NEWLINE &&
                tok.isFlagSet(AtriaTokenManager.IS_END_OF_LINE) == false);
    }
}
