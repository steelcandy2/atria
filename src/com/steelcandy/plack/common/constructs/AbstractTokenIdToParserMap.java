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

import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;

/**
    An abstract base class for TokenIdToParserMaps.
    <p>
    <strong>Note</strong>: an instance's initialize() method must be called
    after it is constructed but before it is used.
    <p>
    Subclasses only have to implement the initializeMap() and
    createDefaultObject() methods, though they may also override the
    mapLoadFactor() method if they wish.

    @author James MacKay
    @version $Revision: 1.4 $
    @see AbstractTokenIdToObjectMap#initialize
*/
public abstract class AbstractTokenIdToParserMap
    extends AbstractTokenIdToObjectMap
    implements TokenIdToParserMap
{
    // Constructors

    /**
        Constructs an AbstractTokenIdToParserMap from the number of entries
        in the map.

        @param numEntries the number of entries that the map should be able
        to hold (though it will grow as necessary)
    */
    public AbstractTokenIdToParserMap(int numEntries)
    {
        super(numEntries);
    }


    // Public methods

    /**
        @see TokenIdToParserMap#hasParserFor(int)
    */
    public boolean hasParserFor(int tokenId)
    {
        return hasMappingFor(tokenId);
    }

    /**
        @see TokenIdToParserMap#get(int)
    */
    public Parser get(int tokenId)
    {
        // Assert.ensure(result != null);
        return ((Parser) getObject(tokenId)).cloneConstructParser();
    }
}
