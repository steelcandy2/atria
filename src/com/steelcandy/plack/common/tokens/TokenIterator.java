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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by all iterators over tokens.
    <p>
    Unlike java.util.Iterators this class does not provide the ability to
    remove elements.
    <p>
    The <code>peek()</code> and <code>next()</code> methods throw a
    NoSuchItemException when and only when <code>hasNext()</code> returns
    false.

    @author James MacKay
    @see Token
*/
public interface TokenIterator
{
    // Public methods

    /**
        Indicates whether this iterator has any more tokens left to iterate
        over.

        @return true iff this iterator has any more tokens left to iterate
        over
    */
    public boolean hasNext();

    /**
        @return the next token, consuming that token
        @exception NoSuchItemException thrown iff there isn't a next token
        @see #peek
    */
    public Token next()
        throws NoSuchItemException;

    /**
        @return the next token without consuming it (so the next call to
        peek() or next() will return the same token)
        @exception NoSuchItemException thrown iff there isn't a next token
        @see #next
    */
    public Token peek()
        throws NoSuchItemException;

    /**
        @return a (possibly empty) list of this iterator's remaining tokens,
        consuming them all
        @see #discardRemaining
    */
    public TokenList remaining();
        // Assert.ensure(result != null);

    /**
        Returns a list of the tokens from the next token from this iterator
        (i.e. the one that would be returned if next() were called instead of
        this method) through to either the token before the first token that
        satisfies the specified predicate, or (if no tokens match the
        specified predicate) the last token that this iterator will return.
        <p>
        If this iterator has no tokens left to return then this method will
        return an empty TokenList. (Thus this method can be called on an
        iterator for which hasNext() returns false.)

        @param endPredicate the predicate that is used to detect the token
        after the last token to put in the returned list of tokens
        @return the next token through either the token before the first
        token that satisfies the endPredicate, or (if no tokens match the
        endPredicate) the last token that will be returned by this iterator
    */
    public TokenList nextTo(UnaryTokenPredicate endPredicate);
        // Assert.ensure(result != null);

    /**
        Returns a list of the tokens from the next token to be returned by
        this iterator (i.e. the one that would be returned if next() were
        called instead of this method) through to the first token that either
        satisfies the specified predicate or is the last token that this
        iterator will return.
        <p>
        If this iterator has no tokens left to return then this method will
        return an empty TokenList. (Thus this method can be called on a
        TokenIterator for which hasNext() returns false.)

        @param endPredicate the predicate that is used to detect the last
        token to put in the returned list of tokens
        @return the next token through the first token that either satisfies
        the endPredicate or is the last one that will be returned by this
        iterator
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate);
        // Assert.ensure(result != null);

    /**
        Discards the next token, which consumes it as well.
        <p>
        Calling next() and ignoring the returned Token would have the same
        effect, but the name of this method makes the caller's intention to
        discard the token clear.

        @exception NoSuchItemException thrown if there is no next token (i.e.
        if <code>hasNext() == false</code>)
        @see #next
    */
    public void discardNext()
        throws NoSuchItemException;

    /**
        Causes all of our remaining tokens to be discarded.
        <p>
        Calling remaining() and ignoring the returned TokenList would have
        the same effect, but the name of this method makes the caller's
        intention to discard the tokens clear.

        @see #remaining
    */
    public void discardRemaining();
}
