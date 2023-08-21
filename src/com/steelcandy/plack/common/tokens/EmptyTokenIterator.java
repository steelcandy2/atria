/*
 Copyright (C) 2001-2005 by James MacKay.

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
import com.steelcandy.common.Resources;

/**
    A singleton TokenIterator that iterates over no tokens.

    @author James MacKay
    @version $Revision: 1.10 $
    @see Token
*/
public class EmptyTokenIterator
    implements TokenIterator
{
    // Constants

    /** The single instance of this class. */
    private static final EmptyTokenIterator _instance =
        new EmptyTokenIterator();

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_NEXT_TOKEN_MSG =
            "NO_NEXT_TOKEN_MSG";
    private static final String
        NO_TOKEN_TO_PEEK_MSG =
            "NO_TOKEN_TO_PEEK_MSG";


    // Constructors

    /**
        @return the single instance of this class
    */
    public static TokenIterator instance()
    {
        return _instance;
    }

    /**
        Default constructor. This should only be called to construct the
        single instance of this class.
    */
    private EmptyTokenIterator()
    {
        // empty
    }


    // Implemented TokenIterator methods

    /**
        Always returns false.

        @see TokenIterator#hasNext
    */
    public boolean hasNext()
    {
        return false;
    }

    /**
        @see TokenIterator#next
    */
    public Token next()
    {
        String msg = _resources.getMessage(NO_NEXT_TOKEN_MSG);
        throw new NoSuchItemException(msg);
    }

    /**
        @see TokenIterator#peek
    */
    public Token peek()
    {
        String msg = _resources.getMessage(NO_TOKEN_TO_PEEK_MSG);
        throw new NoSuchItemException(msg);
    }

    /**
        @see TokenIterator#remaining
    */
    public TokenList remaining()
    {
        return createEmptyTokenList();
    }

    /**
        @see TokenIterator#nextTo
    */
    public TokenList nextTo(UnaryTokenPredicate endPredicate)
    {
        return createEmptyTokenList();
    }

    /**
        @see TokenIterator#nextThrough
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate)
    {
        return createEmptyTokenList();
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
        // We never have any tokens left, so there's nothing to do.
    }


    // Protected methods

    /**
        Creates and returns an empty TokenList.

        @return an empty token list
    */
    protected TokenList createEmptyTokenList()
    {
        return TokenList.createEmptyList();
    }
}
