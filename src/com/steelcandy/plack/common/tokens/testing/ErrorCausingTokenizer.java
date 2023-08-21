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

package com.steelcandy.plack.common.tokens.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.io.Io;
import com.steelcandy.common.Resources;

import java.io.IOException;
import java.io.Writer;

import java.util.Random;

/**
    An abstract base class for tokenizers that deliberately try to introduce
    errors into a stream of tokens obtained from the tokenizer that an
    instance wraps.
    <p>
    Instances of this class of tokenizer are generally only used to test a
    language processor's error handling capabilities.
    <p>
    Note: the tokenizer that an instance is to wrap should be passed to the
    instance's initialize(Tokenizer) method before any attempts are made to
    obtain tokens from the instance.
    <p>
    Subclasses only have to implement the canGetNextToken() and
    getNextToken() methods, though they may want to override the initialize()
    method (in which case they should call the superclass' version from the
    new version).

    @author James MacKay
    @see FilterTokenizer#initialize(Tokenizer)
*/
public abstract class ErrorCausingTokenizer
    extends AbstractFilterTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenTestingResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        REPORT_TOKEN_STREAM_MANIPULATION_FAILED_MSG =
            "REPORT_TOKEN_STREAM_MANIPULATION_FAILED_MSG";
    private static final String
        TOKEN_DESCRIPTION_MSG =
            "TOKEN_DESCRIPTION_MSG";
    private static final String
        EMACS_LOCATION_MSG =
            "EMACS_LOCATION_MSG";
    private static final String
        EMACS_UNKNOWN_LOCATION_MSG =
            "EMACS_UNKNOWN_LOCATION_MSG";
    private static final String
        TOKEN_LOCATION_DESCRIPTION_MSG =
            "TOKEN_LOCATION_DESCRIPTION_MSG";
    private static final String
        UNKNOWN_TOKEN_LOCATION_DESCRIPTION_MSG =
            "UNKNOWN_TOKEN_LOCATION_DESCRIPTION_MSG";


    // Private fields

    /**
        The writer to use to output information about how this tokenizer has
        tried to introduce errors into its source tokenizer's token stream.
    */
    private Writer _errorInfoWriter;

    /**
        The random number generator used in determining when to manipulate
        the token stream.
    */
    private Random _random = new Random();


    // Constructors

    /**
        Constructs a ErrorCausingTokenizer.
        <p>
        Note: the initialize(Tokenizer) method must be called on a newly-
        constructed instance before it can be used.

        @param w the writer used to write out the details of any attempts by
        the tokenizer to introduce errors into its source tokenizer's token
        stream
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public ErrorCausingTokenizer(Writer w, ErrorHandler handler)
    {
        super(handler);
        Assert.require(w != null);
        // Assert.require(handler != null);

        _errorInfoWriter = w;
    }


    // Protected methods

    /**
        Indicates whether to perform an action that should occur with the
        specified percent probability.

        @param percentProbability the percent probability that the action
        should be performed: a value of 100.0 indicates that the action
        should always be performed, and a value of 1.0 indicates that the
        action should be performed 1% of the time, and a value of 0.0
        indicates that the action should never be performed
        @return true iff the action should be performed this time
    */
    protected boolean shouldPerformAction(double percentProbability)
    {
        Assert.require(percentProbability >= 0.0);
        Assert.require(percentProbability <= 100.0);

        return (_random.nextDouble() * 100.0) < percentProbability;
    }

    /**
        Reports the specified message as a description of a change made by
        this tokenizer to its source tokenizer's token stream.

        @param tok the token that was manipulated
        @param msg a message describing the change that was made
    */
    protected void reportManipulation(Token tok, String msg)
    {
        Assert.require(tok != null);

        try
        {
            Io.writeLine(_errorInfoWriter,
                emacsLocation(tok) + " " + msg);
        }
        catch (IOException ex)
        {
            String errorMsg = _resources.
                getMessage(REPORT_TOKEN_STREAM_MANIPULATION_FAILED_MSG,
                           getClass().getName(), msg);

            // We pass 'null' in as the error's source location since the
            // error doesn't correspond to anything in the source code. We
            // report it as a fatal error since it shouldn't happen, and
            // because this tokenizer is assumed to be being used in testing
            // and we don't want the unreported manipulation to get missed
            // inadvertently.
            handleError(FATAL_ERROR_LEVEL, errorMsg, null);
        }
    }

    /**
        Returns a description of the specified token.

        @param tok the token
        @return a description of 'tok'
        @see #tokenIdToDescription(Token)
        @see #locationDescription(Token)
    */
    protected String description(Token tok)
    {
        Assert.require(tok != null);

        // Assert.ensure(result != null);
        return _resources.
            getMessage(TOKEN_DESCRIPTION_MSG,
                tokenIdToDescription(tok),
                locationDescription(tok));
    }

    /**
        Returns a description of the specified token.

        @param tok the token
        @return a description of 'tok'
        @see #description(Token)
        @see CommonTokenManager#idToDescription(int)
    */
    protected String tokenIdToDescription(Token tok)
    {
        Assert.require(tok != null);

        return tokenManager().idToDescription(tok.id());
    }

    /**
        Returns an emacs-friendly representation of the location of the
        specified token.
        <p>
        Note that this is automatically prepended to each token manipulation
        report already, so there is rarely a need to call this method from a
        subclass.

        @param tok the token
        @return an emacs-friendly representation of the location of 'tok'
        (including the name of its source file)
        @see #locationDescription(Token)
    */
    protected String emacsLocation(Token tok)
    {
        Assert.require(tok != null);
        String result;

        SourceLocation loc = tok.location();
        SourceCode code = sourceCode();
        if (loc != null)
        {
            result = _resources.
                getMessage(EMACS_LOCATION_MSG, code.fullName(),
                    String.valueOf(loc.startLineNumber()),
                    String.valueOf(loc.startOffset() + 1));
        }
        else
        {
            result = _resources.
                getMessage(EMACS_UNKNOWN_LOCATION_MSG, code.fullName());
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns a human-readable description of the location of the specified
        token in the source code.

        @param tok the token
        @return a description of the location of 'tok' in the source code
        @see #description(Token)
        @see #emacsLocation(Token)
    */
    protected String locationDescription(Token tok)
    {
        Assert.require(tok != null);
        String result;

        SourceLocation loc = tok.location();
        if (loc != null)
        {
            result = _resources.
                getMessage(TOKEN_LOCATION_DESCRIPTION_MSG,
                    String.valueOf(loc.startLineNumber()),
                    String.valueOf(loc.startOffset()));
        }
        else
        {
            result = _resources.
                getMessage(UNKNOWN_TOKEN_LOCATION_DESCRIPTION_MSG);
        }

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        @return the token manager that manages the tokens that this tokenizer
        processes
    */
    protected abstract CommonTokenManager tokenManager();
}
