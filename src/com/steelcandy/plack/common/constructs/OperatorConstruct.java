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

import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.tokens.OperatorConstants;
import com.steelcandy.plack.common.tokens.OperatorData;
import com.steelcandy.plack.common.tokens.OperatorToken;
import com.steelcandy.plack.common.tokens.Token;

/**
    An abstract base class for a language construct that represents an
    operator.
    <p>
    The operator is assumed to be represented by a single token (the token
    from which an OperatorConstruct is constructed), and the token is assumed
    to either be an OperatorToken or a token that has an object value
    associated with it that is an instance of OperatorData.
    <p>
    Subclasses just have to implement the accept() method.

    @author James MacKay
    @version $Revision: 1.11 $
    @see OperatorToken
    @see OperatorData
*/
public abstract class OperatorConstruct
    extends SingleTokenConstruct
    implements OperatorConstants
{
    // Private fields

    /**
        The OperatorData object containing information about the operator
        that this construct represents.
    */
    private OperatorData _data;


    // Constructors

    /**
        Constructs an OperatorConstruct.

        @param checklist the construct's validity constraint checklist
    */
    public OperatorConstruct(ValidityConstraintChecklist checklist)
    {
        super(checklist);
    }


    // Public methods

    /**
        @see SingleTokenConstruct#setToken(Token)
    */
    public void setToken(Token tok)
    {
        Assert.require(tok.hasObjectValue());
        Assert.require(tok.objectValue() != null);
        Assert.require(tok.objectValue() instanceof OperatorData);

        super.setToken(tok);
        _data = (OperatorData) tok.objectValue();
    }


    /**
        Indicates whether, upon encountering the specified new operator, this
        operator (which is assumed to precede the new operator in the same
        expression) can be applied immediately.
        <p>
        This method returns true if and only if
        <ul>
            <li>this operator has a higher precedence than the new operator,
                or</li>
            <li>this operator has the same precedence as the new operator,
                and this operator is left-associative.</li>
        </ul>
        (The latter case ensures that left-associative operators are applied
        in left-to-right order, and that right-associative operators are
        applied in right-to-left order.)

        @param newOperator the new operator that has just been encountered
        @return true iff this operator can be applied immediately upon the
        new operator's being encountered
    */
    public boolean applyBefore(OperatorConstruct newOperator)
    {
        Assert.require(newOperator != null);

        int prec = precedence();
        int newPrec = newOperator.precedence();
        return (prec > newPrec) ||
               (prec == newPrec && associativity() == LEFT_ASSOCIATIVE);
    }

    /**
        @return the arity of the operator that this construct represents
    */
    public int arity()
    {
        return data().arity();
    }

    /**
        @return the name of the arity of this operator
    */
    public String arityName()
    {
        return OperatorData.arityName(arity());
    }

    /**
        @return the associativity of the operator that this construct
        represents: it should be the value of one of the *_ASSOCIATIVE
        constants
        @see OperatorConstants
    */
    public int associativity()
    {
        return data().associativity();
    }

    /**
        @return the name of the associativity of this operator
    */
    public String associativityName()
    {
        return OperatorData.associativityName(associativity());
    }

    /**
        @return the fixity of the operator that this construct represents:
        it should be the value of one of the *FIX constants
        @see OperatorConstants
    */
    public int fixity()
    {
        return data().fixity();
    }

    /**
        @return the name of the fixity of this operator
    */
    public String fixityName()
    {
        return OperatorData.fixityName(fixity());
    }

    /**
        @return the precedence level of the operator that this construct
        represents
    */
    public int precedence()
    {
        return data().precedence();
    }


    // Protected methods

    /**
        @return the OperatorData object containing information about the
        operator that this construct represents
    */
    protected OperatorData data()
    {
        return _data;
    }
}
