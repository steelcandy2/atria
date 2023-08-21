/*
 Copyright (C) 2001-2008 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.errors.TokenizingError;

import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.common.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
    The abstract base class for most tokenizers. It provides an
    implementation of most of the methods declared in the Tokenizer
    interface: subclasses only have to implement the abstract
    canGetNextToken() and getNextToken() methods, as well as the
    sourceCode() method.
    <p>
    Wherever possible a tokenizer's methods should call the
    <code>handleError()</code> method defined in this class rather than
    accessing the error handler directly through the
    <code>errorHandler()</code> method.

    @author James MacKay
    @see Token
    @see #canGetNextToken
    @see #getNextToken
    @see #handleError
    @see Tokenizer#errorHandler
*/
public abstract class AbstractTokenizer
    extends ErrorHandlerTokenizer
{
    /*
        Invariant: hasTokenAfterNext() implies hasNext()
    */

    // Private fields

    /**
        The next token to be returned by this tokenizer, or null if we
        haven't yet retrieved the next token.
    */
    private Token _nextToken = null;

    /**
        The token after the next token to be returned by this tokenizer, or
        null if we haven't retrieved the token after the next token.
    */
    private Token _tokenAfterNext = null;

    /**
        Indicates whether or not this source should discard its remaining
        tokens.
    */
    private boolean _discardRemainingTokens = false;

    /**
        The TokenListeners that are listening for our tokens, or null if
        there are no TokenListeners listening for our tokens. Each item in
        the list is a TokenListener.
    */
    private List _listeners;


    // Constructors

    /**
        Constructs an AbstractTokenizer.

        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public AbstractTokenizer(ErrorHandler handler)
    {
        super(handler);

        _listeners = null;
    }

    /**
        Constructs an AbstractTokenizer. The Resources and String parameters
        are ignored: this constructor allows Tokenizers to be constructed via
        reflection.

        @param subtokenizerResources the Resources from which this tokenizer
        gets information about its subtokenizers. It is ignored by this
        constructor
        @param resourceKeyPrefix the first part of the resource keys used to
        look up information about this tokenizer's subtokenizers in the
        Resources object. It is ignored by this constructor
        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public AbstractTokenizer(Resources subtokenizerResources,
                             String resourceKeyPrefix,
                             ErrorHandler handler)
    {
        this(handler);
    }


    // Public static methods

    /**
        Returns a list of the locations of the specified tokens.
        <p>
        List elements can be null: they are just ignored.

        @param list the list of tokens whose locations are to be returned
        @return a list of the source locations of each of the tokens in
        'list'
    */
    public static SourceLocationList locations(TokenList list)
    {
        Assert.require(list != null);

        SourceLocationList result =
            SourceLocationList.createArrayList(list.size());
        TokenIterator iter = list.iterator();
        while (iter.hasNext())
        {
            Token tok = iter.next();
            if (tok != null)
            {
                result.add(tok.location());
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        Adds the specified listener to the list of TokenListeners that are
        listening for our tokens.

        @param listener the listener to add to the list of TokenListeners
        that are listening for our tokens
    */
    public void addListener(TokenListener listener)
    {
        Assert.require(listener != null);

        if (_listeners == null)
        {
            _listeners = new ArrayList();
        }
        _listeners.add(listener);
    }

    /**
        @see TokenIterator#hasNext
    */
    public boolean hasNext()
    {
        boolean result;

        if (_discardRemainingTokens)
        {
            // We've 'discarded' all of our tokens.
            result = false;
        }
        else if (_nextToken != null)
        {
            // We've got one cached that we can return.
            result = true;
        }
        else
        {
            // See if we can get one by calling getAndReportNextToken().
            Assert.check(_tokenAfterNext == null);
            result = canGetNextToken();
        }

        return result;
    }

    /**
        @see Tokenizer#hasTokenAfterNext
    */
    public boolean hasTokenAfterNext()
    {
        boolean result;

        if (_discardRemainingTokens)
        {
            // We've 'discarded' all of our tokens.
            result = false;
        }
        else if (hasNext() == false)
        {
            // There's no next token, so there won't be one after it either.
            result = false;
        }
        else if (_tokenAfterNext != null)
        {
            // We've got one cached that we can return.
            result = true;
        }
        else
        {
            // Cache the next token if it isn't already, then see if there is
            // one after that.
            if (_nextToken == null)
            {
                Assert.check(canGetNextToken());
                _nextToken = getAndReportNextToken();
            }

            // See if we can get the token after the _nextToken.
            result = canGetNextToken();
        }

        return result;
    }

    /**
        @see TokenIterator#peek
    */
    public Token peek()
    {
        Token result;

        if (hasNext())
        {
            if (_nextToken == null)
            {
                // We haven't retrieved the next token yet.
                Assert.check(canGetNextToken());
                _nextToken = getAndReportNextToken();
            }
            result = _nextToken;
        }
        else
        {
            throw new NoSuchItemException();
        }

        return result;
    }

    /**
        @see Tokenizer#peekTokenAfterNext
    */
    public Token peekTokenAfterNext()
    {
        Token result;

        if (hasTokenAfterNext())
        {
            Assert.check(_nextToken != null);
                // as a result of hasTokenAfterNext()'s implementation
            if (_tokenAfterNext == null)
            {
                Assert.check(canGetNextToken());
                _tokenAfterNext = getAndReportNextToken();
            }
            result = _tokenAfterNext;
        }
        else
        {
            throw new NoSuchItemException();
        }

        return result;
    }

    /**
        @see TokenIterator#next
    */
    public Token next()
    {
        if (hasNext() == false)
        {
            throw new NoSuchItemException();
        }

        Token result = null;
        if (_nextToken != null)
        {
            // We've already retrieved the next token, so return that token
            // and remove it as the next token.
            result = _nextToken;
            _nextToken = _tokenAfterNext;
            _tokenAfterNext = null;
        }
        else
        {
            // We haven't retrieved the next token yet, so we retrieve it
            // now.
            Assert.check(canGetNextToken());
            result = getAndReportNextToken();
        }

        return result;
    }

    /**
        @see TokenIterator#remaining
    */
    public TokenList remaining()
    {
        return nextThrough(UnsatisfiableUnaryTokenPredicate.instance());
    }

    /**
        @see TokenIterator#nextTo
    */
    public TokenList nextTo(UnaryTokenPredicate endPredicate)
    {
        TokenList result = TokenList.createArrayList();
        while (hasNext())
        {
            if (endPredicate.isSatisfied(peek()))
            {
                // Don't consume the token that satisfies the predicate, and
                // don't include it in the result.
                break;    // while
            }
            else
            {
                // Consume the token and add it to the result.
                result.add(next());
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TokenIterator#nextThrough
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate)
    {
        TokenList result = TokenList.createArrayList();
        while (hasNext())
        {
            Token tok = next();
            result.add(tok);
            if (endPredicate.isSatisfied(tok))
            {
                break;    // while;
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see TokenIterator#discardNext
    */
    public void discardNext()
    {
        next();
    }

    /**
        @see TokenIterator#discardRemaining
    */
    public void discardRemaining()
    {
        _discardRemainingTokens = true;
        _nextToken = null;
        _tokenAfterNext = null;
    }


    // Protected methods

    /**
        Returns the next token from this tokenizer (using getNextToken()) and
        reports it to all of our TokenListeners.

        @return the next token from this tokenizer
        @see #getNextToken
        @see #reportToken(Token)
    */
    protected Token getAndReportNextToken()
    {
        Assert.require(canGetNextToken());

        Token result = getNextToken();

        if (result != null)
        {
            reportToken(result);
        }

        return result;
    }

    /**
        Reports the specified token to all of our TokenListeners.

        @param tok the token to report to our TokenListeners
    */
    protected void reportToken(Token tok)
    {
        if (_listeners != null)
        {
            Iterator iter = _listeners.iterator();
            while (iter.hasNext())
            {
                TokenListener listener = (TokenListener) iter.next();
                listener.accept(tok);
            }
        }
    }

    /**
        Handles the tokenizing error with the specified severity level,
        description and location in the source code.

        @param level the error's severity level: it should be one of the
        *_LEVEL constants defined in the class ErrorSeverityLevels
        @param description the error's description
        @param loc the location in the source code where the error occurred

        @see ErrorSeverityLevels
        @see TokenizingError
    */
    protected void handleError(int level, String description,
                               SourceLocation loc)
    {
        TokenizingError error =
            new TokenizingError(level, description, sourceCode(), loc);
        errorHandler().handle(error, this);
    }


    // Abstract methods

    /**
        Indicates whether another token can be obtained by calling
        getNextToken().

        @return true if another token can be obtained by calling
        getNextToken(), and false if not
    */
    protected abstract boolean canGetNextToken();

    /**
        Returns the next token from this tokenizer.
        <p>
        This method is only called when the next token is needed. Thus it
        doesn't have to check whether we're discarding all of our remaining
        tokens, whether we have the next token cached, etc.

        @return the next token from this tokenizer
    */
    protected abstract Token getNextToken();
        // Assert.require(canGetNextToken());
}
