/*
 Copyright (C) 2001-2004 by James MacKay.

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

import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;

/**
    The interface implemented by all classes that parse tokens into
    constructs.
    <p>
    Many (though not necessarily all) Parsers can be implemented as
    stateless, and thus can be shared. This is also the reason why the
    ErrorHandlers are passed into a Parser's parse() methods, rather than
    into a constructor or other method.

    @author James MacKay
*/
public interface Parser
    extends ErrorSeverityLevels
{
    // Public methods

    /**
        Returns a clone of this parser that can be used to parse a new
        construct.
        <p>
        Note: stateless parsers (and reentrant parsers in general) can just
        return themselves (that is, 'this').

        @return a clone of this parser
    */
    public Parser cloneConstructParser();
        // Assert.ensure(result != null);

    /**
        Indicates whether the specified token can be the first token in a
        construct parseable by this parser.

        @param tok the token to test
        @return true iff the token can be the first token in a construct
        parseable by this parser
    */
    public boolean canStartConstruct(Token tok);
        // Assert.require(tok != null);


    /**
        Returns the construct that results from parsing the first tokens from
        the specified tokenizer.
        <p>
        This version of parseConstruct() is the one that is usually called by
        non-parsers to parse a construct. (The other versions of
        parseConstruct() are usually only called by other parsers.)

        @param t the tokenizer whose first tokens are to be parsed
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the construct that results from parsing the tokens
    */
    public Construct parseConstruct(Tokenizer t, ErrorHandler handler);
        // Assert.require(t != null);
        // Assert.require(handler != null);

    /**
        Returns the construct that results from parsing the first tokens from
        the specified token list and/or tokenizer as a subconstruct of
        another construct.
        <p>
        The tokens in the token list are the as yet unparsed tokens that
        represent (part of) the current line being parsed, and the ones in
        the tokenizer represent the following lines. Thus the tokens in the
        list logically precede the ones in the tokenizer, and so should
        always be processed first.
        <p>
        This version of parseConstruct() is usually only called by other
        parsers.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the construct that resulted from parsing the tokens
    */
    public Construct parseConstruct(TrackedTokenList line, Tokenizer t,
                                    SubconstructParsingData data,
                                    ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(line.isEmpty() == false);
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(handler != null);

    /**
        Sets the construct at the specified index in the specified list of
        constructs to the construct that results from parsing the first
        tokens from the specified token list and/or tokenizer as a
        subconstruct of another construct.
        <p>
        The tokens in the token list are the as yet unparsed tokens that
        represent (part of) the current line being parsed, and the ones in
        the tokenizer represent the following lines. Thus the tokens in the
        list logically precede the ones in the tokenizer, and so should
        always be processed first.
        <p>
        This version of parseConstruct() is usually only called by other
        parsers.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructs the list of constructs into which to put the
        construct resulting from parsing the tokens
        @param index the index of the element of 'constructList' to set to
        the construct resulting from parsing the tokens
        @param handler the error handler to use to handle any errors that
        occur during parsing
    */
    public void parseConstruct(TrackedTokenList line, Tokenizer t,
                               SubconstructParsingData data,
                               ConstructList constructs, int index,
                               ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(line.isEmpty() == false);
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(constructs != null);
        // Assert.require(index >= 0);
        // Assert.require(index < constructs.size());
        // Assert.require(handler != null);
}
