/*
 Copyright (C) 2004 by James MacKay.

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
    A TokenIterator that iterates through the Tokens in a TokenList.
    <p>
    Note: this is a custom implementation of the TokenIterator interface,
    not the one that is automatically generated.

    @author James MacKay
    @see Token
*/
public class CustomTokenIterator
    implements TokenIterator
{
    // Private fields

    /** The list whose Tokens this iterator iterates over. */
    private TokenList _list;

    /**
        The index into the list of the next Token to be returned by this
        iterator.
    */
    private int _nextIndex;


    // Constructors

    /**
        Constructs a CustomTokenIterator from the TokenList whose Tokens the
        iterator will iterate over.
    */
    public CustomTokenIterator(TokenList list)
    {
        Assert.require(list != null);

        _list = list;
        _nextIndex = 0;
    }


    // Public methods

    /**
        @see TokenIterator#hasNext
    */
    public boolean hasNext()
    {
        return (_nextIndex < _list.size());
    }

    /**
        @see TokenIterator#next
    */
    public Token next()
        throws NoSuchItemException
    {
        return _list.get(_nextIndex++);
    }

    /**
        @see TokenIterator#peek
    */
    public Token peek()
        throws NoSuchItemException
    {
        return _list.get(_nextIndex);
    }

    /**
        @see TokenIterator#remaining
    */
    public TokenList remaining()
    {
        TokenList result = _list.subList(_nextIndex);
        _nextIndex = _list.size();  // consumes the remaining tokens

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TokenIterator#nextTo
    */
    public TokenList nextTo(UnaryTokenPredicate endPredicate)
    {
        TokenList result;

        int pastEndIndex = _list.indexOf(endPredicate, _nextIndex);

        // If there is no matching token then return the remaining tokens.
        if (pastEndIndex < 0)
        {
            result = remaining();
        }
        else
        {
            result = _list.subList(_nextIndex, pastEndIndex);
            _nextIndex = pastEndIndex;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TokenIterator#nextThrough
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate)
    {
        TokenList result;

        int endIndex = _list.indexOf(endPredicate, _nextIndex);

        // If there is no matching token or if it is the last one in the list
        // then return the remaining tokens.
        if (endIndex < 0 || endIndex == (_list.size() - 1))
        {
            result = remaining();
        }
        else
        {
            result = _list.subList(_nextIndex, endIndex + 1);
            _nextIndex = endIndex + 1;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TokenIterator#discardNext
    */
    public void discardNext()
        throws NoSuchItemException
    {
        next();
    }

    /**
        @see TokenIterator#discardRemaining
    */
    public void discardRemaining()
    {
        _nextIndex = _list.size();
    }
}
