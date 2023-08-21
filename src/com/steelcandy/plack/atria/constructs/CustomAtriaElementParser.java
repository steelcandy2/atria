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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.plack.common.source.SourceLocationList;

/**
    The custom Element parser implementation.
    <p>
    A custom parser is necessary because the default one could not parse
    the zero or more Attribute subconstructs on an Element's first line.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class CustomAtriaElementParser
    extends DefaultAtriaElementParser
{
    // Constants

    /**
        The instance of this class returned by createCustom().

        @see #create
    */
    private static final CustomAtriaElementParser
        _instance = new CustomAtriaElementParser();


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
    public static AtriaElementParser createCustom()
    {
        return _instance;
    }

    /**
        Constructs a CustomAtriaElementParser.
        <p>
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected CustomAtriaElementParser()
    {
        // empty
    }


    // First line construct part parsing methods

    /**
        @see DefaultAtriaElementParser#parseAttributeSubconstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, AtriaConstructManagerBase.Element, ErrorHandler)
    */
    protected void
        parseAttributeSubconstruct(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            AtriaConstructManager.Element parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        while (isEmptyLine(line) == false)
        {
            boolean oldSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(true);
            AtriaConstructManager.Attribute attr = PARSER_FACTORY.
                createAttributeParser().parse(line, t, data, handler);
            parent.addAttribute(attr);

            boolean newSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(oldSuccess && newSuccess);
            if (newSuccess == false)
            {
                // The Attribute subconstruct is invalid.
                String description = MANAGER.
                    idToDescription(MANAGER.ATTRIBUTE);
                handleInvalidAttributeSubconstruct(line, data, attr,
                                                   description, t, handler);
            }
        }
    }
}
