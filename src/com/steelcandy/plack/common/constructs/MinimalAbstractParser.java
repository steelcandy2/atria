/*
 Copyright (C) 2001-2016 by James MacKay.

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

import com.steelcandy.plack.common.source.HasSourceLocation;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;
import com.steelcandy.plack.common.source.SourceLocationList;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ParsingError;

import com.steelcandy.plack.common.tokens.AbstractTokenizer;
import com.steelcandy.plack.common.tokens.HasIdTokenPredicate;
import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.TokenIterator;
import com.steelcandy.plack.common.tokens.TokenList;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;
import com.steelcandy.plack.common.tokens.UnaryTokenPredicate;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;

/**
    A minimal abstract base class for classes that implement the Parser
    interface. It just provides implementations of all but one of the
    parseConstruct() methods in terms of the others (in addition to some
    utility methods).
    <p>
    Subclasses usually just have to implement the abstract methods,
    canStartConstruct(), and parseConstruct(TrackedTokenList, Tokenizer,
    SubconstructParsingData, ErrorHandler), though non-reentrant parsers
    should also override this class' implementation of cloneConstructParser().

    @author James MacKay
    @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
*/
public abstract class MinimalAbstractParser
    implements Parser
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_TOP_LEVEL_CONSTRUCT_START_MSG =
            "INVALID_TOP_LEVEL_CONSTRUCT_START_MSG",
        SOURCE_CODE_AFTER_TOP_LEVEL_CONSTRUCT_MSG =
            "SOURCE_CODE_AFTER_TOP_LEVEL_CONSTRUCT_MSG",
        MISSING_CONSTRUCT_MSG =
            "MISSING_CONSTRUCT_MSG",
        MISSING_COMPOUND_SUBCONSTRUCT_MSG =
            "MISSING_COMPOUND_SUBCONSTRUCT_MSG",
        INVALID_TOKEN_MSG =
            "INVALID_TOKEN_MSG",
        SUBCONSTRUCTS_UNDER_SINGLE_LINE_CONSTRUCT_MSG =
            "SUBCONSTRUCTS_UNDER_SINGLE_LINE_CONSTRUCT_MSG",
        SUBCONSTRUCTS_INDENTED_TOO_FAR_MSG =
            "SUBCONSTRUCTS_INDENTED_TOO_FAR_MSG",
        EXPECTED_END_OF_LINE_MSG =
            "EXPECTED_END_OF_LINE_MSG",
        INVALID_START_TERMINAL_MSG =
            "INVALID_START_TERMINAL_MSG",
        INVALID_END_TERMINAL_MSG =
            "INVALID_END_TERMINAL_MSG",
        MISSING_END_TERMINAL_MSG =
            "MISSING_END_TERMINAL_MSG",
        MISSING_FIRST_REPEATED_SUBCONSTRUCT_MSG =
            "MISSING_FIRST_REPEATED_SUBCONSTRUCT_MSG",
        MISSING_SUBSEQUENT_REPEATED_SUBCONSTRUCT_MSG =
            "MISSING_SUBSEQUENT_REPEATED_SUBCONSTRUCT_MSG",
        INVALID_TERMINAL_MSG =
            "INVALID_TERMINAL_MSG",
        MISSING_TERMINAL_MSG =
            "MISSING_TERMINAL_MSG",
        INVALID_CONSTRUCT_FLAG_MSG =
            "INVALID_CONSTRUCT_FLAG_MSG",
        MISSING_CONSTRUCT_FLAG_MSG =
            "MISSING_CONSTRUCT_FLAG_MSG",
        MISSING_SUBCONSTRUCT_MSG =
            "MISSING_SUBCONSTRUCT_MSG",
        INVALID_FIRST_INDENTED_SUBCONSTRUCT_TOKEN_MSG =
            "INVALID_FIRST_INDENTED_SUBCONSTRUCT_TOKEN_MSG",
        INVALID_FIRST_CHOICE_TOKEN_MSG =
            "INVALID_FIRST_CHOICE_TOKEN_MSG";


    // Public methods

    /**
        @see Parser#cloneConstructParser
    */
    public Parser cloneConstructParser()
    {
        return this;
    }

    /**
        @see Parser#parseConstruct(Tokenizer, ErrorHandler)
    */
    public Construct parseConstruct(Tokenizer t, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        Construct result = null;

        TrackedTokenList line = nextLine(t);
        if (line.isEmpty() == false)
        {
            // We ignore whether parsing was successful (as indicated by
            // 'data') and just return the resulting construct (which may
            // be null).
            SubconstructParsingData data = createParsingData();
            result = parseConstruct(line, t, data, handler);
        }
        else
        {
            handleConstructMissing(line, t, handler);
        }

        return result;  // may be null
    }

    /**
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ConstructList, int, ErrorHandler)
    */
    public void parseConstruct(TrackedTokenList line, Tokenizer t,
                               SubconstructParsingData data,
                               ConstructList constructs, int index,
                               ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty() == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(constructs != null);
        Assert.require(index >= 0);
        Assert.require(index < constructs.size());
        Assert.require(handler != null);

        constructs.set(index, parseConstruct(line, t, data, handler));
    }


    // Parsing utility methods

    /**
        @return a new SubconstructParsingData object
    */
    protected SubconstructParsingData createParsingData()
    {
        // Assert.ensure(result != null);
        return new SubconstructParsingData(locationFactory());
    }


    // Source location-related utility methods

    /**
        Returns a list of the locations of the specified constructs.
        <p>
        List elements can be null: they are just ignored.

        @param list the list of constructs whose locations are to be returned
        @return a list of the source locations of each of the constructs in
        'list'
    */
    public static SourceLocationList locations(ConstructList list)
    {
        Assert.require(list != null);

        SourceLocationList result =
            SourceLocationList.createArrayList(list.size());
        ConstructIterator iter = list.iterator();
        while (iter.hasNext())
        {
            Construct c = iter.next();
            if (c != null)
            {
                result.add(c.location());
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the location of the fragment of source code all of whose
        parts' locations are specified, in order, in the specified list of
        locations. The fragment of source code is assumed to consist of
        consecutive parts (subconstructs and terminals/tokens) of a single
        language construct.
        <p>
        The locations are assumed to be in increasing order, and none of
        their locations can overlap.
        <p>
        List elements can be null: they are just ignored.
        <p>
        This method is usually used when creating the location of a construct
        consisting of a mix of terminals (tokens) and non-terminals
        (subconstructs).

        @param list the list of locations of the consecutive parts of a
        language construct
        @return the source location of the part of the language construct
    */
    protected SourceLocation location(SourceLocationList list)
    {
        Assert.require(list != null);

        // 'result' may be null
        return locationFactory().create(list);
    }

    /**
        Returns the location of the fragment of source code all of whose
        parts' locations are specified. The fragment of source code is
        assumed to consist of consecutive parts (subconstructs and
        terminals/tokens) of a single language construct.
        <p>
        This is just a more convenient version of
        <code>location(SourceLocationList)</code> in the case where there are
        two locations. Thus any requirements that that method makes also
        apply to this method.

        @param loc1 the location of the first part of the language construct
        @param loc2 the location of the second part of the language construct
        @return the source location of the part of the language construct
    */
    protected SourceLocation location(SourceLocation loc1,
                                      SourceLocation loc2)
    {
        // 'loc1' can be null
        // 'loc2' can be null

        SourceLocationList list = SourceLocationList.createArrayList(2);
        list.add(loc1);
        list.add(loc2);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the fragment of source code all of whose
        parts' locations are specified. The fragment of source code is
        assumed to consist of consecutive parts (subconstructs and
        terminals/tokens) of a single language construct.
        <p>
        This is just a more convenient version of
        <code>location(SourceLocationList)</code> in the case where there
        are three locations. Thus any requirements that that method makes
        also apply to this method.

        @param loc1 the location of the first part of the language construct
        @param loc2 the location of the second part of the language construct
        @param loc3 the location of the third part of the language construct
        @return the source location of the part of the language construct
    */
    protected SourceLocation
        location(SourceLocation loc1,
                 SourceLocation loc2, SourceLocation loc3)
    {
        // 'loc1' can be null
        // 'loc2' can be null
        // 'loc3' can be null

        SourceLocationList list = SourceLocationList.createArrayList(3);
        list.add(loc1);
        list.add(loc2);
        list.add(loc3);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the fragment of source code represented by
        the specified tokens. The tokens are assumed to represent a single
        language construct.
        <p>
        The tokens are assumed to be in increasing order by source location,
        and none of their locations can overlap.
        <p>
        List elements can be null: they are just ignored.

        @param list the list of tokens representing a single language
        construct
        @return the source location of the fragment of source code that the
        tokens (and hence the construct) represent
        @see #location(SourceLocationList)
    */
    protected SourceLocation location(TokenList list)
    {
        Assert.require(list != null);

        // 'result' may be null
        return location(AbstractTokenizer.locations(list));
    }

    /**
        Returns the location of the fragment of source code represented by
        the specified constructs. The constructs are assumed to represent
        consecutive subconstructs of another construct.
        <p>
        The constructs are assumed to be in increasing order by source
        location, and none of their locations can overlap.
        <p>
        List elements can be null: they are just ignored.
        <p>
        This method is used when creating the location of a construct
        consisting solely of nonterminals (subconstructs).

        @param list the list of constructs representing consecutive
        subconstructs of another 'parent' language construct
        @return the source location of the fragment of source code that the
        subconstructs represent
        @see #location(SourceLocationList)
    */
    protected SourceLocation location(ConstructList list)
    {
        Assert.require(list != null);

        // 'result' may be null
        return location(locations(list));
    }

    /**
        Returns the location of the construct that consists of the specified
        parts. (It is assumed that all of the construct's parts have been
        specified.)
        <p>
        Parts can be null: they are just ignored.
        <p>
        A part of construct is either a terminal (represented by a Token) or
        a subconstruct (represented by a Construct).

        @param part1 the first part of the construct
        @param part2 the second part of the construct
        @return the location of the construct that consists of the specified
        parts
        @see Token
        @see Construct
    */
    protected SourceLocation
        location(HasSourceLocation part1, HasSourceLocation part2)
    {
        // 'part1' can be null
        // 'part2' can be null

        SourceLocationList list = SourceLocationList.createArrayList(2);
        addPart(part1, list);
        addPart(part2, list);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the construct that consists of the specified
        parts. (It is assumed that all of the construct's parts have been
        specified.)
        <p>
        A part of construct is either a terminal (represented by a Token) or
        a subconstruct (represented by a Construct).

        @param part1 the first part of the construct
        @param part2 the second part of the construct
        @param part3 the third part of the construct
        @return the location of the construct that consists of the specified
        parts
        @see Token
        @see Construct
    */
    protected SourceLocation
        location(HasSourceLocation part1,
                 HasSourceLocation part2, HasSourceLocation part3)
    {
        // 'part1' can be null
        // 'part2' can be null
        // 'part3' can be null

        SourceLocationList list = SourceLocationList.createArrayList(3);
        addPart(part1, list);
        addPart(part2, list);
        addPart(part3, list);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the construct that consists of the specified
        parts. (It is assumed that all of the construct's parts have been
        specified.)
        <p>
        A part of construct is either a terminal (represented by a Token) or
        a subconstruct (represented by a Construct).

        @param part1 the first part of the construct
        @param part2 the second part of the construct
        @param part3 the third part of the construct
        @param part4 the fourth part of the construct
        @return the location of the construct that consists of the specified
        parts
        @see Token
        @see Construct
    */
    protected SourceLocation
        location(HasSourceLocation part1, HasSourceLocation part2,
                 HasSourceLocation part3, HasSourceLocation part4)
    {
        // 'part1' can be null
        // 'part2' can be null
        // 'part3' can be null
        // 'part4' can be null

        SourceLocationList list = SourceLocationList.createArrayList(4);
        addPart(part1, list);
        addPart(part2, list);
        addPart(part3, list);
        addPart(part4, list);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the construct that consists of the specified
        parts. (It is assumed that all of the construct's parts have been
        specified.)
        <p>
        A part of construct is either a terminal (represented by a Token) or
        a subconstruct (represented by a Construct).

        @param part1 the first part of the construct
        @param part2 the second part of the construct
        @param part3 the third part of the construct
        @param part4 the fourth part of the construct
        @param part5 the fifth part of the construct
        @return the location of the construct that consists of the specified
        parts
        @see Token
        @see Construct
    */
    protected SourceLocation
        location(HasSourceLocation part1, HasSourceLocation part2,
                 HasSourceLocation part3, HasSourceLocation part4,
                 HasSourceLocation part5)
    {
        // 'part1' can be null
        // 'part2' can be null
        // 'part3' can be null
        // 'part4' can be null
        // 'part5' can be null

        SourceLocationList list = SourceLocationList.createArrayList(5);
        addPart(part1, list);
        addPart(part2, list);
        addPart(part3, list);
        addPart(part4, list);
        addPart(part5, list);

        // 'result' may be null
        return location(list);
    }

    /**
        Returns the location of the construct that consists of the specified
        parts. (It is assumed that all of the construct's parts have been
        specified.)
        <p>
        A part of construct is either a terminal (represented by a Token) or
        a subconstruct (represented by a Construct).

        @param part1 the first part of the construct
        @param part2 the second part of the construct
        @param part3 the third part of the construct
        @param part4 the fourth part of the construct
        @param part5 the fifth part of the construct
        @param part6 the sixth part of the construct
        @return the location of the construct that consists of the specified
        parts
        @see Token
        @see Construct
    */
    protected SourceLocation
        location(HasSourceLocation part1, HasSourceLocation part2,
                 HasSourceLocation part3, HasSourceLocation part4,
                 HasSourceLocation part5, HasSourceLocation part6)
    {
        // 'part1' can be null
        // 'part2' can be null
        // 'part3' can be null
        // 'part4' can be null
        // 'part5' can be null
        // 'part6' can be null

        SourceLocationList list = SourceLocationList.createArrayList(6);
        addPart(part1, list);
        addPart(part2, list);
        addPart(part3, list);
        addPart(part4, list);
        addPart(part5, list);
        addPart(part6, list);

        // 'result' may be null
        return location(list);
    }

    /**
        Adds the specified part's location to the specified list of locations
        iff the part isn't null: otherwise nothing is done.
        <p>
        Note: not adding null parts isn't strictly necessary, but it may be
        slightly more efficient to do it here that to have
        location(SourceLocationList) remove them later. (And besides, we
        already had this method ...)

        @param part the part whose location is to be added to the list iff
        the part isn't null
        @param list the list of locations to add the part's location to
    */
    protected void addPart(HasSourceLocation part, SourceLocationList list)
    {
        // 'part' can be null
        Assert.require(list != null);

        if (part != null)
        {
            list.add(part.location());
        }
    }


    // Error handling methods

    /**
        Checks that there are no tokens left in the specified tokenizer after
        a top-level construct has been parsed.

        @param t the tokenizer to check
        @param handler the error handler to use to handle there being any
        tokens after a top-level construct, if there are any
    */
    protected void checkForTokensAfterTopLevelConstruct(Tokenizer t,
                                                        ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        if (t.hasNext())
        {
            handleTokensAfterTopLevelConstruct(t, t.peek(), handler);
        }
    }

    /**
        Handles there being one or more tokens after what was thought to be
        the end of a top-level construct parsed by this parser.

        @param t the tokenizer containing any tokens that represent any
        source code after a top-level construct
        @param firstTok the first token after the top-level construct
        @param handler the error handler to use to handle the error
    */
    protected void handleTokensAfterTopLevelConstruct(Tokenizer t,
                                    Token firstTok, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(firstTok != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(SOURCE_CODE_AFTER_TOP_LEVEL_CONSTRUCT_MSG,
                       parseableConstructDescription());
        SourceLocation loc = firstTok.location();

        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Handles a top-level instance of the type of construct parsed by this
        parser not starting with the proper type of token.

        @param t the tokenizer that is the source of the tokens being parsed
        @param tok the token that was invalid as the start of the top-level
        construct
        @param handler the error handler to use to handle the top-level
        construct not starting with the proper type of token
    */
    protected void handleTopLevelConstructStartInvalid(Token tok,
                                        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String description = parseableConstructDescription();
        reportTopLevelConstructStartInvalid(description, tok, t, handler);
    }

    /**
        Handles an instance of the type of construct parsed by this parser
        being missing.

        @param line the list containing the tokens in the current line being
        parsed: isEmpty() will return true, but the 'absolute version' of the
        list may contain tokens (which can be used as context for an error
        message, for example)
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the missing
        construct
    */
    protected void handleConstructMissing(TrackedTokenList line,
                                          Tokenizer t, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(t != null);
        Assert.require(handler != null);

        String description = parseableConstructDescription();
        reportConstructMissing(description, line, t, handler);
    }

    /**
        Reports that the start of a top-level construct is invalid.
        <p>
        Note: this method is meant to be used in the implementation of
        handleTopLevelConstructStartInvalid().

        @param description a description of the type of the top-level
        construct
        @param tok the token that was invalid as the start of the top-level
        construct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the invalid
        top-level construct
        @see #handleTopLevelConstructStartInvalid(Token, Tokenizer, ErrorHandler)
    */
    protected void reportTopLevelConstructStartInvalid(String description,
                            Token tok, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(tok != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_TOP_LEVEL_CONSTRUCT_START_MSG, description);

        SourceLocation loc = tok.location();

        // We report this as a fatal error so that we don't get a cascade
        // of spurious errors when the file (or whatever) being parsed does
        // not contain the expected type of source code: for example, if a
        // binary file or other type of source file or text file gets
        // parsed accidentally.
        handleError(FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports that a construct is missing.
        <p>
        Note: this method is meant to be used in the implementation of
        handleConstructMissing().

        @param description a description of the type of construct that is
        missing
        @param line the list containing the tokens in the current line being
        parsed: isEmpty() will return true, but the 'absolute version' of the
        list may contain tokens (which can be used as context for an error
        message, for example)
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the missing
        construct
        @see #handleConstructMissing(TrackedTokenList, Tokenizer, ErrorHandler)
    */
    protected void reportConstructMissing(String description,
        TrackedTokenList line, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(line != null);
        Assert.require(line.isEmpty());
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_CONSTRUCT_MSG, description);

        SourceLocation loc = null;
        Token previousToken = line.lastAdvancedOver();
        if (previousToken != null)
        {
            loc = previousToken.location();
        }

        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports compound construct subconstruct(s) being missing.

        @param data the subconstruct parsing data used in parsing the
        compound construct
        @param subconstructDescription the description of the missing
        subconstruct(s)
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingCompoundSubconstruct(String
        subconstructDescription, SubconstructParsingData data,
        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(subconstructDescription != null);
        Assert.require(data != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_COMPOUND_SUBCONSTRUCT_MSG,
                       subconstructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg,
                    data.previousLocation(), t, handler);
    }

    /**
        Reports as being invalid the token that represents the same source
        code as a single token construct.

        @param constructDescription a description of the single token
        construct whose token is invalid or missing
        @param loc the location in the source code of the invalid token, if
        it's available
        @param invalidTokDesc a description of the invalid token
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidSingleTokenConstructToken(String
        constructDescription, String invalidTokDesc, SourceLocation loc,
        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(invalidTokDesc != null);
        // 'loc' can be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_TOKEN_MSG, constructDescription,
                       invalidTokDesc);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports there being subconstructs indented under what is supposed to
        be a single line construct with the specified description.

        @param constructDescription a description of the single line token
        with the subconstructs indented under it
        @param loc the location in the source code of all of the
        subconstructs indented under the single line construct, if it's
        available
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportSubconstructsUnderSingleLineConstruct(String
        constructDescription, SourceLocation loc, Tokenizer t,
        ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        // 'loc' can be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(SUBCONSTRUCTS_UNDER_SINGLE_LINE_CONSTRUCT_MSG,
                       constructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports indented subconstruct(s) of a multiline construct that are
        indented too many levels under the construct's first line.

        @param constructDescription a description of the multiline construct
        that has an indented subconstruct that is indented too far
        @param indentedLevels the number of levels under the first line that
        the indented subconstruct(s) are indented
        @param loc the location in the source code of the subconstruct(s)
        that are indented too many levels, if it's available
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportSubconstructIndentedTooFar(String
        constructDescription, int indentedLevels, SourceLocation loc,
        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(indentedLevels > 0);
        // 'loc' can be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(SUBCONSTRUCTS_INDENTED_TOO_FAR_MSG,
                       String.valueOf(indentedLevels),
                       constructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports extra, unexpected tokens at the end of a line.

        @param loc the location in the source code of the extra tokens at the
        end of the line, if it's available
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportExtraAtEndOfLine(SourceLocation loc,
                                Tokenizer t, ErrorHandler handler)
    {
        // 'loc' can be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(EXPECTED_END_OF_LINE_MSG);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a repeated construct's start terminal being missing or
        invalid.

        @param constructDescription a description of the repeated construct
        @param terminalDescription a description of the start terminal
        @param loc the location in the source code where the start terminal
        was expected to be
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidRepeatedConstructStartTerminal(String
        constructDescription, String terminalDescription,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_START_TERMINAL_MSG,
                       constructDescription, terminalDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a repeated construct's ending terminal being missing or
        invalid.

        @param constructDescription a description of the repeated construct
        @param terminalDescription a description of the ending terminal
        @param loc the location in the source code where the ending
        terminal was expected to be
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidRepeatedConstructEndingTerminal(String
        constructDescription, String terminalDescription,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_END_TERMINAL_MSG,
                       constructDescription, terminalDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a repeated construct's ending terminal being missing.

        @param constructDescription a description of the repeated construct
        @param terminalDescription a description of the ending terminal
        @param loc the location in the source code just before where the
        ending terminal was expected to be, or null if there is nothing
        before where the ending terminal was expected
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingRepeatedConstructEndingTerminal(String
        constructDescription, String terminalDescription,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        // 'loc' may be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_END_TERMINAL_MSG,
                       constructDescription, terminalDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a repeated construct's first subconstruct being missing.

        @param constructDescription a description of the repeated construct
        @param subconstructDescription a description of the missing first
        subconstruct
        @param loc the location in the source code just before where the
        first subconstruct was expected to be, or null if there is nothing
        before where the first subconstruct was expected
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingRepeatedConstructFirstSubconstruct(String
        constructDescription, String subconstructDescription,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(subconstructDescription != null);
        // 'loc' may be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_FIRST_REPEATED_SUBCONSTRUCT_MSG,
                       constructDescription, subconstructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a repeated construct's second or subsequent subconstruct
        being missing.

        @param constructDescription a description of the repeated construct
        @param subconstructDescription a description of the missing
        subsequent subconstruct
        @param loc the location in the source code just before where the
        subconstruct was expected to be: it will always be non-null since the
        missing subconstruct was preceded by a separator
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingRepeatedConstructSubsequentSubconstruct(
        String constructDescription, String subconstructDescription,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(constructDescription != null);
        Assert.require(subconstructDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_SUBSEQUENT_REPEATED_SUBCONSTRUCT_MSG,
                       constructDescription, subconstructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports an invalid terminal. It may be invalid because it is missing.

        @param description a description of the invalid terminal
        @param loc the location of the invalid token in the source code
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidTerminal(String description,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_TERMINAL_MSG, description);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a missing terminal. (It is definitely missing since there are
        no more tokens.)

        @param description a description of the missing terminal
        @param loc the location of the construct part just before where the
        missing terminal was expected to be, or null if there is no such
        construct part
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingTerminal(String description,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        // 'loc' may be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_TERMINAL_MSG, description);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports an invalid construct flag. It may be invalid because it is
        missing.

        @param description a description of the invalid flag
        @param loc the location of the invalid flag in the source code
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidConstructFlag(String description,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_CONSTRUCT_FLAG_MSG, description);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a missing construct flag. (It is definitely missing since
        there are no more tokens.)

        @param description a description of the missing flag
        @param loc the location of the construct part just before where the
        missing flag was expected to be, or null if there is no such
        construct part
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingConstructFlag(String description,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        // 'loc' may be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_CONSTRUCT_FLAG_MSG, description);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports a missing subconstruct. (It is definitely missing since there
        are no more tokens.)

        @param description a description of the missing subconstruct
        @param loc the location of the construct part just before where the
        missing subconstruct was expected to be, or null if there is no such
        construct part
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportMissingSubconstruct(String description,
        SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        // 'loc' may be null
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MISSING_SUBCONSTRUCT_MSG, description);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports an invalid subconstruct that is indented under the first line
        of a multiline construct that is invalid because it starts with the
        wrong token.

        @param tok the invalid token
        @param constructDescription a description of the multiline construct
        with the invalid indented subconstruct
        @param loc the location in the source code of the invalid indented
        subconstruct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidFirstIndentedSubconstructToken(Token tok,
        String constructDescription, SourceLocation loc, Tokenizer t,
        ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(constructDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: 'tok' represents the indentation at the start of the
        // subconstruct.
        String msg = _resources.
            getMessage(INVALID_FIRST_INDENTED_SUBCONSTRUCT_TOKEN_MSG,
                       constructDescription);
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Reports an invalid choice construct that is invalid because it starts
        with the wrong token.

        @param tok the invalid token
        @param constructDescription a description of the choice construct
        @param loc the location in the source code of the choice construct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
    */
    protected void reportInvalidFirstChoiceToken(Token tok,
        String constructDescription, SourceLocation loc, Tokenizer t,
        ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(constructDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(INVALID_FIRST_CHOICE_TOKEN_MSG,
                       constructDescription, tokenDescription(tok));
        handleError(NON_FATAL_ERROR_LEVEL, msg, loc, t, handler);
    }

    /**
        Handles the parsing error described by the specified information on
        behalf of the specified parser using the specified error handler.

        @param level the error's severity level
        @param description the error's description
        @param loc the location in the source code where the error occurred,
        if it's available
        @param p the parser on whose behalf we are handling the error
        @param t the tokenizer in whose tokens the error was found
        @param handler the error handler to use to handle the parsing error
        @see ParsingError#ParsingError(int, String, SourceCode, SourceLocation)
    */
    public static void handleError(int level, String description,
                                   SourceLocation loc, Parser p,
                                   Tokenizer t, ErrorHandler handler)
    {
        Assert.require(description != null);
        // 'loc' can be null
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        ParsingError error = new ParsingError(level, description,
                                              t.sourceCode(), loc);
        handler.handle(error, p);
    }

    /**
        Handles the parsing error described by the specified information
        using the specified error handler.

        @param level the error's severity level
        @param description the error's description
        @param loc the location in the source code where the error occurred
        @param t the tokenizer in whose tokens the error was found
        @param handler the error handler to use to handle the parsing error
    */
    protected void handleError(int level, String description,
                               SourceLocation loc, Tokenizer t,
                               ErrorHandler handler)
    {
        Assert.require(description != null);
        // 'loc' can be null
        Assert.require(t != null);
        Assert.require(handler != null);

        handleError(level, description, loc, this, t, handler);
    }


    // Other utility methods

    /**
        Finishes processing the specified construct after it has been
        completely parsed.
        <p>
        This method
        <ul>
            <li>sets the piece of source code that the construct
                represents a part of,</li>
            <li>sets the location of the part of the source code that the
                construct represents (iff the construct's location can be
                set), and</li>
            <li>sets the construct as the previous construct part (in
                'data')</li>
        </ul>
        <p>
        Note: this method is public static so that it can be used by
        AbstractParserHelper to implement its finishConstruct() method.

        @see #finishConstruct(Construct, Tokenizer, SubconstructParsingData, SourceLocation, ErrorHandler)
    */
    public static void finishParsedConstruct(Construct c, Tokenizer t,
        SubconstructParsingData data, SourceLocation loc,
        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(t != null);
        Assert.require(data != null);
        // 'loc' may be null
        Assert.require(handler != null);

        c.setSourceCode(t.sourceCode());
        if (loc != null && c.canSetLocation())
        {
            c.setLocation(loc);
        }

        data.setPreviousPart(c);
    }

    /**
        @see #finishParsedConstruct(Construct, Tokenizer, SubconstructParsingData, SourceLocation, ErrorHandler)
    */
    protected void finishConstruct(Construct c, Tokenizer t,
        SubconstructParsingData data, SourceLocation loc,
        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(t != null);
        Assert.require(data != null);
        // 'loc' may be null
        Assert.require(handler != null);

        finishParsedConstruct(c, t, data, loc, handler);
    }

    /**
        Returns the tokens from the specified tokenizer that represent the
        next line of source code. The isEndOfLine() method is used to
        determine where the line ends.
        <p>
        All of the tokens in the returned list will have been removed from
        the tokenizer. The list will include any "end of line" token at the
        end of the line, if such a token is present.

        @param t the tokenizer from which to get the line's tokens
        @return a list of the tokens that represent the next line of source
        code
    */
    protected TrackedTokenList nextLine(Tokenizer t)
    {
        Assert.require(t != null);

        TrackedTokenList result = new TrackedTokenList();

/*
if (t.hasNext() && t.peek().location() != null)
{
    System.err.println("===> starting line " +
                       t.peek().location().startLineNumber());
}
else
{
    System.err.println("===> starting new empty line");
}
*/
        while (t.hasNext())
        {
            Token tok = t.next();
            result.add(tok);
            if (isEndOfLine(tok))
            {
                break;
            }
        }

        // Assert.ensure(result != null);
        return result;
    }

    /**
        Indicates whether there is anything left in the line represented by
        the tokens in the specified list except possibly an end-of-line
        token.

        @param line the list of tokens that represent the line
        @return true iff the line is empty or contains a single token that
        represents the end of a line
        @see #isEndOfLine(Token)
    */
    protected boolean isEmptyLine(TrackedTokenList line)
    {
        Assert.require(line != null);

        int size = line.size();
        return (size == 0) || (size == 1 && isEndOfLine(line.get(0)));
    }


    // Abstract methods

    /**
        @return a description of the type of construct(s) that are parseable
        by this parser
    */
    protected abstract String parseableConstructDescription();
        // Assert.ensure(result != null);

    /**
        @return the SourceLocationFactory that this parser is to use to
        create SourceLocations
    */
    protected abstract SourceLocationFactory locationFactory();

    /**
        Indicates whether the specified token is an "end of line" token:
        that is, a token that is the last token in a line of source code.

        @param tok the token to test
        @return true iff 'tok' is the last token in a line of source code
    */
    protected abstract boolean isEndOfLine(Token tok);

    /**
        @param tok a token
        @return a description of 'tok', suitable for use in an error message
    */
    protected abstract String tokenDescription(Token tok);
        // Assert.require(tok != null);
        // Assert.ensure(result != null);
}
