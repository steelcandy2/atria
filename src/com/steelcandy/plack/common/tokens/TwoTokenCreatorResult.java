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

import com.steelcandy.plack.common.source.SourcePosition;

/**
    The class of the result returned by a TokenCreator that has created
    exactly two tokens.

    @author James MacKay
    @version $Revision: 1.2 $
    @see TokenCreator
*/
public class TwoTokenCreatorResult
    extends AbstractTokenCreatorResult
{
    // Private fields

    /** The first token that was created. */
    private Token _firstToken;

    /** A list whose sole element is the second token that was created. */
    private TokenList _secondTokenList;


    // Constructors

    /**
        Constructs a TwoTokenCreatorResult.
        <p>
        This constructor assumes that both specified tokens are non-null: if
        either can be null then a different TokenCreatorResult subclass
        should be used.

        @param firstToken the first token that was created
        @param secondToken the second token that was created
    */
    public TwoTokenCreatorResult(Token firstToken, Token secondToken)
    {
        super(secondToken);
        Assert.require(firstToken != null);
        Assert.require(secondToken != null);

        _firstToken = firstToken;
        _secondTokenList = TokenList.createSingleItemList(secondToken);
    }


    // Public methods

    /**
        @see TokenCreatorResult#firstToken
    */
    public Token firstToken()
    {
        return _firstToken;
    }

    /**
        @see TokenCreatorResult#extraTokens
    */
    public TokenIterator extraTokens()
    {
        return _secondTokenList.iterator();
    }
}
