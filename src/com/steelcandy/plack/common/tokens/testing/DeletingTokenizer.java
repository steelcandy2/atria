/*
 Copyright (C) 2002-2008 by James MacKay.

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
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.Resources;

import java.io.Writer;

/**
    An abstract base class for error-causing tokenizers that attempt to cause
    errors by deleting tokens from its source tokenizer's token stream.
    <p>
    Subclasses only have to implement the abstract methods declared in this
    class' superclasses, though some may also want to override
    canDeleteToken() to prevent certain types of tokens from being deleted.
    <p>
    <strong>Note</strong>: the tokens that are deleted are not replaced with
    anything, but in most cases this will not be a problem, as the 'hole'
    will be assumed to have been whitespace that was removed.

    @author James MacKay
    @version $Revision: 1.4 $
    @see #canDeleteToken(Token)
*/
public abstract class DeletingTokenizer
    extends ErrorCausingTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenTestingResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DELETED_TOKEN_MSG = "DELETED_TOKEN_MSG";

    /**
        The default percent probability that a given token from an instance's
        source tokenizer's token stream should be deleted.
    */
    private static final double DEFAULT_DELETE_PERCENT_PROBABILITY = 0.05;


    // Private fields

    /**
        The percent probability that we should delete a given token in our
        source tokenizer's token stream.
    */
    private double _deletePercentProbability;


    // Constructors

    /**
        Constructs a DeletingTokenizer that deletes tokens from its source
        tokenizer's token stream with the specified percent probability.

        @param w the writer used to write out the details of any attempts by
        the tokenizer to introduce errors into its source tokenizer's token
        stream
        @param deletePercentProbability the percent probability that the
        tokenizer will delete a token from its source tokenizer's token
        stream
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public DeletingTokenizer(Writer w, double deletePercentProbability,
                             ErrorHandler handler)
    {
        super(w, handler);
        // Assert.require(w != null);
        Assert.require(deletePercentProbability >= 0.0);
        Assert.require(deletePercentProbability <= 100.0);
        // Assert.require(handler != null);

        _deletePercentProbability = deletePercentProbability;
    }

    /**
        Constructs a DeletingTokenizer that deletes tokens from its source
        tokenizer's token stream with the default probability.

        @param w the writer used to write out the details of any attempts by
        the tokenizer to introduce errors into its source tokenizer's token
        stream
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public DeletingTokenizer(Writer w, ErrorHandler handler)
    {
        this(w, DEFAULT_DELETE_PERCENT_PROBABILITY, handler);
        // Assert.require(w != null);
        // Assert.require(handler != null);
    }


    // Public methods

    /**
        @see FilterTokenizer#initialize(Tokenizer)
    */
    public void initialize(Tokenizer sourceTokenizer)
    {
        Assert.require(sourceTokenizer != null);

        super.initialize(sourceTokenizer);

        // Possibly delete the first token.
        possiblyDeleteNextToken();
    }


    // Protected methods

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        return source().hasNext();
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        Token result = source().next();

        // Possibly delete the token after the one we're returning.
        possiblyDeleteNextToken();

        return result;
    }

    /**
        Possibly deletes the next token from our source tokenizer's token
        stream.
    */
    protected void possiblyDeleteNextToken()
    {
        if (source().hasNext())
        {
            Token tok = source().peek();
            if (shouldPerformDeletion() && canDeleteToken(tok))
            {
                source().next();  // discarding the next token
                reportManipulation(tok, _resources.
                    getMessage(DELETED_TOKEN_MSG, description(tok)));
            }
        }
    }

    /**
        @return true iff we should attempt to delete the next token
    */
    protected boolean shouldPerformDeletion()
    {
        return shouldPerformAction(deletePercentProbability());
    }

    /**
        Indicates whether the specified token is allowed to be deleted. (For
        example, it may not be legal to delete the token that indicates the
        end of the token stream.)
        <p>
        This implementation allows any and all tokens to be deleted.

        @param tok the token to check
        @return true iff 'tok' is allowed to be deleted
    */
    protected boolean canDeleteToken(Token tok)
    {
        Assert.require(tok != null);

        return true;
    }

    /**
        @return the percent probability that a given token will be deleted
        by this tokenizer from its source tokenizer's token stream
    */
    protected double deletePercentProbability()
    {
        return _deletePercentProbability;
    }
}
