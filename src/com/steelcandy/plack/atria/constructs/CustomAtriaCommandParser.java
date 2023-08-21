/*
 Copyright (C) 2005-2016 by James MacKay.

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

/**
    The custom Command parser implementation.
    <p>
    A custom parser is necessary because the default one could not parse
    the zero or more Argument subconstructs.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class CustomAtriaCommandParser
    extends DefaultAtriaCommandParser
{
    // Constants

    /**
        The instance of this class returned by createCustom().

        @see #create
    */
    private static final CustomAtriaCommandParser
        _instance = new CustomAtriaCommandParser();


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
    public static AtriaCommandParser createCustom()
    {
        return _instance;
    }

    /**
        Constructs a CustomAtriaCommandParser.
        <p>
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected CustomAtriaCommandParser()
    {
        // empty
    }


    // Construct part parsing methods

    /**
        @see DefaultAtriaCommandParser#parseArgumentSubconstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, AtriaConstructManagerBase.Command, ErrorHandler)
    */
    protected void
        parseArgumentSubconstruct(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            AtriaConstructManager.Command parent,
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
            if (line.get(0).id() == TOKEN_MANAGER.RIGHT_BRACKET)
            {
                break;  // since we've reached the end of the command
            }

            boolean oldSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(true);
            AtriaConstructManager.Argument arg = PARSER_FACTORY.
                createArgumentParser().parse(line, t, data, handler);
            parent.addArgument(arg);

            boolean newSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(oldSuccess && newSuccess);
            if (newSuccess == false)
            {
                // The Argument subconstruct is invalid.
                String description = MANAGER.
                    idToDescription(MANAGER.ARGUMENT);
                handleInvalidArgumentSubconstruct(line, data, arg,
                                                  description, t, handler);
            }

        }
    }
}
