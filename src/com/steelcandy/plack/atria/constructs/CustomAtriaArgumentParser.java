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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;

/**
    The custom Argument parser implementation.

    @author James MacKay
*/
public class CustomAtriaArgumentParser
    extends AbstractAtriaArgumentParser
{
    // Constants

    /**
        The token ID to parser map used by default by instances of
        this class.
    */
    private static final TokenIdToParserMap DEFAULT_MAP =
        new CustomAtriaArgumentParserMap();
    static
    {
        DEFAULT_MAP.initialize();
    }

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final CustomAtriaArgumentParser
        _instance = new CustomAtriaArgumentParser();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        <p>
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static AtriaArgumentParser create()
    {
        return _instance;
    }

    /**
        Constructs a CustomAtriaArgumentParser.
        <p>
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected CustomAtriaArgumentParser()
    {
        // empty
    }


    // Public methods

    /**
        @see AtriaArgumentParser#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public AtriaConstructManager.Argument
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        AtriaConstructManager.Argument result;

        // If the first token is a Name then parse it as an Attribute if the
        // next token is '=', and as a Name otherwise.
        int firstId = line.get(0).id();
        if (firstId == TOKEN_MANAGER.NAME && line.size() > 1 &&
            line.get(1).id() == TOKEN_MANAGER.ASSIGNMENT_SIGN)
        {
            result = PARSER_FACTORY.createAttributeParser().
                                        parse(line, t, data, handler);
        }
        else
        {
            result = super.parse(line, t, data, handler);
        }

        return result;
    }


    // Protected methods

    /**
        @see AbstractAtriaArgumentParser#getTokenIdToParserMap
    */
    protected TokenIdToParserMap getTokenIdToParserMap()
    {
        return DEFAULT_MAP;
    }
}
