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

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.UnsupportedMethodException;

/**
    The default Atria parser factory class.
    <p>
    Implementation Note: we use the create() static
    methods on the individual parser classes to create parser instances
    rather than cloning prototype instances created by this factory because:
    <ul>
        <li>if we assign the parser instances to constants in this
            class then a circularity is introduced when constructing this
            class: when a parser is constructed as part of constructing this
            class, that parser's PARSER_FACTORY constant field must be
            created, which causes this class to be constructed .... (Many
            parsers need the PARSER_FACTORY to construct parsers to parse
            their subconstructs.)
        <li>if we assign the parser instances to constants in this
            class lazily, then - because Java's memory model doesn't properly
            support the double-checked locking pattern - every attempt
            to create a parser will have to be synchronized (or we could
            just live with the possibility of creating a few instances
            of a given type of parser, though that seems a little ugly
            and error-prone)
    </ul>

    @author James MacKay
*/
public class DefaultAtriaParserFactory
    extends AtriaParserFactoryBase
{
    // Factory methods

    /**
        @return a parser of Element constructs
    */
    public AtriaElementParser createElementParser()
    {
        return CustomAtriaElementParser.createCustom();
    }

    /**
        @return a parser of Argument constructs
    */
    public AtriaArgumentParser createArgumentParser()
    {
        return CustomAtriaArgumentParser.create();
    }

    /**
        @return a parser of Command constructs
    */
    public AtriaCommandParser createCommandParser()
    {
        return CustomAtriaCommandParser.createCustom();
    }
}
