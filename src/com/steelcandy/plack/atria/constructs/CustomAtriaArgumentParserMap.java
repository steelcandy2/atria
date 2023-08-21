/*
 Copyright (C) 2005-2006 by James MacKay.

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

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;  // javadocs only
import com.steelcandy.plack.common.constructs.*;

/**
    The token ID to parser map used in implementing the custom Argument
    parser.

    @author James MacKay
    @version $Revision: 1.2 $
    @see CustomAtriaArgumentParser
*/
public class CustomAtriaArgumentParserMap
    extends AbstractAtriaArgumentParserMap
{
    // Constructors

    /**
        Constructs a map large enough to hold the number of mappings
        added to it by the inherited version of initializeMap().

        @see AbstractTokenIdToObjectMap#initializeMap
    */
    public CustomAtriaArgumentParserMap()
    {
        super();
    }

    /**
        Constructs a map large enough to hold the specified number
        of mappings.
        <p>
        This constructor exists so that it can be called by constructors
        of subclasses that override initializeMap() to add a different
        number of mappings.

        @param numEntries the number of entries/mappings that the map
        is to be able to hold
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected CustomAtriaArgumentParserMap(int numEntries)
    {
        super(numEntries);
    }


    // Protected methods

    /**
        @see AbstractAtriaArgumentParserMap#createNameParser
    */
    protected Parser createNameParser()
    {
        // We always return a Name parser for Arguments that start with a
        // Name token. If it is to be parsed as an Attribute instead then
        // DefaultAtriaArgumentParser will get and use a parser from
        // somewhere other than this map.
        Parser result = PARSER_FACTORY.createNameParser();

        Assert.ensure(result != null);
        return result;
    }
}
