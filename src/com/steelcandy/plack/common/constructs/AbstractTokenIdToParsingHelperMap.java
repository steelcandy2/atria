/*
 Copyright (C) 2002-2005 by James MacKay.

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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;
import com.steelcandy.plack.common.tokens.Tokenizer;

import com.steelcandy.common.Resources;

/**
    An abstract base class for maps from token IDs to subconstruct parsing
    helpers.
    <p>
    <strong>Note</strong>: an instance's initialize() method must be called
    after it is constructed but before it is used.
    <p>
    Note: parsing helper classes do not have a common base class or
    interface.
    <p>
    Subclasses only have to implement the initializeMap() and
    createDefaultObject() methods, though they may also override the
    mapLoadFactor() method if they wish. They should probably define a
    get(int tokenId) method with an appropriate return type too.

    @author James MacKay
    @see AbstractTokenIdToObjectMap#initialize
*/
public abstract class AbstractTokenIdToParsingHelperMap
    extends AbstractTokenIdToObjectMap
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        MISPLACED_SUBCONSTRUCT_MSG =
            "MISPLACED_SUBCONSTRUCT_MSG";


    // Constructors

    /**
        Constructs an AbstractTokenIdToParsingHelperMap from the number of
        entries in the map.

        @param numEntries the number of entries that the map should be able
        to hold (though it will grow as necessary)
    */
    public AbstractTokenIdToParsingHelperMap(int numEntries)
    {
        super(numEntries);
    }


    // Public methods

    /**
        Indicates whether this map explicitly maps the specified token ID to
        a parsing helper.

        @param tokenId the token ID to test
        @return true iff this map explicitly maps 'tokenId' to a parsing
        helper
    */
    public boolean hasParsingHelperFor(int tokenId)
    {
        return hasMappingFor(tokenId);
    }


    // Error handling methods

    /**
        Reports a subconstruct appearing in the incorrect place in a sequence
        of subconstructs.

        @param subconstructDescription a description of the misplaced
        subconstruct
        @param followingSubconstructsDescription a description of all of the
        subconstructs that can validly follow the misplaced one (and hence
        cannot precede it)
        @param loc the location in the source code of the misplaced
        subconstruct
        @param p the parser on whose behalf the misplaced siubconstruct was
        being parsed
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected static void reportMisplacedSubconstruct(String
        subconstructDescription, String followingSubconstructsDescription,
        SourceLocation loc, Parser p, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(subconstructDescription != null);
        Assert.require(followingSubconstructsDescription != null);
        Assert.require(loc != null);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISPLACED_SUBCONSTRUCT_MSG,
                       subconstructDescription,
                       followingSubconstructsDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, p, t, handler);
    }

    /**
        @see MinimalAbstractParser#handleError(int, String, SourceLocation, Parser, Tokenizer, ErrorHandler)
    */
    public static void handleError(int level, String description,
                                   SourceLocation loc, Parser p,
                                   Tokenizer t, ErrorHandler handler)
    {
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        MinimalAbstractParser.
            handleError(level, description, loc, p, t, handler);
    }
}
