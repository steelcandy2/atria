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

import com.steelcandy.plack.common.base.*;
import com.steelcandy.plack.common.source.SourceLocation;

/**
    A class of token that can be used to represent an operator.
    <p>
    An OperatorToken's object value is the OperatorData object that it was
    constructed from, and that holds all of the information about the
    operator.
    <p>
    This class isn't subclassed very often: in most cases the AbstractToken
    class should be subclassed directly instead.

    @author James MacKay
    @version $Revision: 1.6 $
    @see AbstractToken
*/
public class OperatorToken
    extends DefaultToken
{
    // Private fields

    /** The information about the operator. */
    private OperatorData _data;


    // Constructors

    /**
        Constructs an OperatorToken.

        @param id the token's ID
        @param flags the flags that the token has set, bitwise ORed together
        @param data the information about the operator
        @param location the location in the source code of the fragment that
        the token represents
    */
    public OperatorToken(int id, int flags, OperatorData data,
                         SourceLocation location)
    {
        super(id, flags, location);

        data.checkValid();
        _data = data;
    }


    // Implemented/overridden AbstractToken methods

    /**
        @see Token#cloneToken
    */
    public Token cloneToken(SourceLocation loc)
    {
        return new OperatorToken(id(), flags(), _data.cloneData(), loc);
    }


    /**
        @see Token#hasObjectValue
    */
    public boolean hasObjectValue()
    {
        return true;
    }

    /**
        @see Token#objectValue
    */
    public Object objectValue()
    {
        return _data;
    }


    // Static utility methods

    /**
        Returns the arity of the operator represented by the specified
        (operator) token.
        <p>
        Note: this method (intentionally) doesn't require that the token be
        an instance of OperatorToken, but just that its object value is an
        instance of OperatorData.

        @param tok the token that represents the operator whose arity is to
        be returned
        @return the arity of the operator that 'tok' represents
    */
    public static int arity(Token tok)
    {
        Assert.require(tok != null);
        Assert.require(tok.hasObjectValue());
        Assert.require(tok.objectValue() != null);
        Assert.require(tok.objectValue() instanceof OperatorData);

        OperatorData data = (OperatorData) tok.objectValue();
        return data.arity();
    }

    /**
        Returns the associativity of the operator represented by the
        specified (operator) token. The returned value will be the value of
        one of the associativity constants defined in OperatorConstants.
        <p>
        Note: this method (intentionally) doesn't require that the token be
        an instance of OperatorToken, but just that its object value is an
        instance of OperatorData.

        @param tok the token that represents the operator whose associativity
        is to be returned
        @return the associativity of the operator that 'tok' represents
        @see OperatorConstants#LEFT_ASSOCIATIVE
        @see OperatorConstants#RIGHT_ASSOCIATIVE
    */
    public static int associativity(Token tok)
    {
        Assert.require(tok != null);
        Assert.require(tok.hasObjectValue());
        Assert.require(tok.objectValue() != null);
        Assert.require(tok.objectValue() instanceof OperatorData);

        OperatorData data = (OperatorData) tok.objectValue();
        return data.associativity();
    }

    /**
        Returns the fixity of the operator represented by the specified
        (operator) token. The returned value will be the value of one of the
        fixity constants defined in OperatorConstants.
        <p>
        Note: this method (intentionally) doesn't require that the token be
        an instance of OperatorToken, but just that its object value is an
        instance of OperatorData.

        @param tok the token that represents the operator whose fixity is to
        be returned
        @return the fixity of the operator that 'tok' represents
        @see OperatorConstants#PREFIX
        @see OperatorConstants#POSTFIX
        @see OperatorConstants#INFIX
        @see OperatorConstants#OUTFIX
    */
    public static int fixity(Token tok)
    {
        Assert.require(tok != null);
        Assert.require(tok.hasObjectValue());
        Assert.require(tok.objectValue() != null);
        Assert.require(tok.objectValue() instanceof OperatorData);

        OperatorData data = (OperatorData) tok.objectValue();
        return data.fixity();
    }

    /**
        Returns the precedence level of the operator represented by the
        specified (operator) token.
        <p>
        Note: this method (intentionally) doesn't require that the token be
        an instance of OperatorToken, but just that its object value is an
        instance of OperatorData.

        @param tok the token that represents the operator whose precedence
        level is to be returned
        @return the precedence level of the operator that 'tok' represents
    */
    public static int precedence(Token tok)
    {
        Assert.require(tok != null);
        Assert.require(tok.hasObjectValue());
        Assert.require(tok.objectValue() != null);
        Assert.require(tok.objectValue() instanceof OperatorData);

        OperatorData data = (OperatorData) tok.objectValue();
        return data.precedence();
    }
}
