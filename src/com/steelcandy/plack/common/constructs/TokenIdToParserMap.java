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

import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;  // javadocs only
import com.steelcandy.plack.common.tokens.Token;  // javadocs only

/**
    The interface implemented by read-only maps from token IDs to parsers.
    <p>
    <strong>Note</strong>: an instance's initialize() method must be called
    after it is constructed but before it is used.
    <p>
    Instances must map every token ID to a (non-null) parser. This
    requirement is usually fulfilled by mapping token IDs that are not
    explicitly mapped to a parser to a default parser. (This default parser
    often reports an error whenever it is used.)

    @author James MacKay
    @version $Revision: 1.4 $
    @see Token#id
    @see Parser
    @see #initialize
*/
public interface TokenIdToParserMap
{
    // Public methods

    /**
        Initializes the contents of this map. It must be called before this
        map can usefully be used.

        @see AbstractTokenIdToObjectMap#initialize
    */
    public void initialize();

    /**
        Indicates whether this map explicitly maps the specified token ID to
        a parser.

        @param tokenId the token ID to test
        @return true iff this map explicitly maps 'tokenId' to a parser
    */
    public boolean hasParserFor(int tokenId);

    /**
        Returns the parser to which the specified token ID maps. It will
        always be mapped to a (non-null) parser.

        @param tokenId the token ID to map to a parser
        @return the Parser to which the token ID maps
    */
    public Parser get(int tokenId);
        // Assert.ensure(result != null);
}
