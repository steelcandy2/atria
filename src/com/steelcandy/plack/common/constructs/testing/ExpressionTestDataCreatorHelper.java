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

package com.steelcandy.plack.common.constructs.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ExpressionParserHelper;
import com.steelcandy.plack.common.constructs.SingleTokenConstruct;  // javadocs only

import com.steelcandy.plack.common.tokens.Token;  // javadocs only
import com.steelcandy.plack.common.tokens.OperatorData;

import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.RecordingIndentWriter;

import java.io.IOException;

/**
    An abstract base class for classes that help generate expressions test
    data for use in testing the constructs of a language. It makes several
    assumptions:
    <ul>
        <li>all operator construct classes are subclasses of
            SingleTokenConstruct,</li>
        <li>all tokens that represent operators have an object value
            associated with them that is an OperatorData object describing
            the operator, and</li>
        <li>all operators are either unary prefix, unary postfix or binary
            infix</li>
    </ul>
    <p>
    An expression is assumed to consist of primitive expressions and
    operators. Thus, for example, a parenthesized expression is considered to
    be a primitive expression.
    <p>
    This base class is reentrant, so unless a subclass' implementation makes
    it non-reentrant a single instance of it can be shared.

    @author James MacKay
    @see Token#hasObjectValue
    @see Token#objectValue
    @see OperatorData
    @see SingleTokenConstruct
*/
public abstract class ExpressionTestDataCreatorHelper
{
    // Constructors

    /**
        Constructs an ExpressionTestDataCreatorHelper.
    */
    public ExpressionTestDataCreatorHelper()
    {
        // empty
    }


    // Public methods

    /**
        Generates test data for an expression construct: a construct is
        generated and returned after the corresponding source code is written
        using the specified writer.
        <p>
        Note: this method doesn't adjust the part level at all.

        @param w the writer to use to write out the source code corresponding
        to the returned expression construct
        @return a newly-generated expression construct instance
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    public Construct generateExpression(IndentWriter w)
        throws IOException
    {
        Construct result = null;

        while (result == null)
        {
            int num = generateRandomNumber(0, 3);
            switch (num)
            {
            case 0:
                result = generatePrimitiveExpression(w);
                break;
            case 1:
                result = generateUnaryPrefixOperatorExpression(w);
                break;
            case 2:
                result = generateUnaryPostfixOperatorExpression(w);
                break;
            case 3:
                result = generateBinaryInfixOperatorExpression(w);
                break;
            default:
                Assert.unreachable();
                result = null;  // not reached
            }
        }

        return result;
    }


    // Protected methods

    /**
        Generates test data for a unary prefix operator expression construct:
        a construct is generated and returned after the corresponding source
        code is written using the specified writer, or null is returned and
        nothing is written if the language doesn't have any unary prefix
        operators.
        <p>
        Note: this method doesn't adjust the part level at all.

        @param w the writer to use to write out the source code corresponding
        to the returned expression construct
        @return a newly-generated expression construct instance, or null if
        the language doesn't have any unary prefix operators
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    protected Construct
        generateUnaryPrefixOperatorExpression(IndentWriter w)
        throws IOException
    {
        Construct result = null;

        RecordingIndentWriter r = new RecordingIndentWriter();
        writeSpaceBeforeUnaryPrefixOperator(r);
        Construct op = generateUnaryPrefixOperator(r);

        if (op != null)
        {
            r.playBack(w);
            writeSpaceAfterUnaryPrefixOperator(w, op);
            Construct operand = generateUnaryPrefixOperand(w, op);
            result = createUnaryPrefixOperatorExpression(op, operand);
        }

        return result;
    }

    /**
        Generates test data for the operand of the specified unary prefix
        operator: a construct representing the operand is generated and
        returned after the corresponding source code is written using the
        specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the unary prefix operator
        @return the generated operand construct that corresponds to the
        source code that was written using 'w'
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateUnaryPrefixOperand(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperandAfterOperator(w, op);
    }


    /**
        Generates test data for a unary postfix operator expression
        construct: a construct is generated and returned after the
        corresponding source code is written using the specified writer, or
        null is returned and nothing is written if the language doesn't have
        any unary postfix operators.
        <p>
        Note: this method doesn't adjust the part level at all.

        @param w the writer to use to write out the source code corresponding
        to the returned expression construct
        @return a newly-generated expression construct instance, or null if
        the language doesn't have any unary postfix operators
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    protected Construct
        generateUnaryPostfixOperatorExpression(IndentWriter w)
        throws IOException
    {
        Construct result = null;

        RecordingIndentWriter r = new RecordingIndentWriter();
        writeSpaceBeforeUnaryPostfixOperator(r);
        Construct op = generateUnaryPostfixOperator(r);

        if (op != null)
        {
            Construct operand = generateUnaryPostfixOperand(w, op);
            r.playBack(w);
            writeSpaceAfterUnaryPostfixOperator(w, op);
            result = createUnaryPostfixOperatorExpression(operand, op);
        }

        return result;
    }

    /**
        Generates test data for the operand of the specified unary postfix
        operator: a construct representing the operand is generated and
        returned after the corresponding source code is written using the
        specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the unary postfix operator
        @return the generated operand construct that corresponds to the
        source code that was written using 'w'
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateUnaryPostfixOperand(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperandBeforeOperator(w, op);
    }


    /**
        Generates test data for a binary infix operator expression construct:
        a construct is generated and returned after the corresponding source
        code is written using the specified writer, or null is returned and
        nothing is written if the language doesn't have any binary infix
        operators.
        <p>
        Note: this method doesn't adjust the part level at all.

        @param w the writer to use to write out the source code corresponding
        to the returned expression construct
        @return a newly-generated expression construct instance, or null if
        the language doesn't have any binary infix operators
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    protected Construct
        generateBinaryInfixOperatorExpression(IndentWriter w)
        throws IOException
    {
        Construct result = null;

        RecordingIndentWriter r = new RecordingIndentWriter();
        writeSpaceBeforeBinaryInfixOperator(r);
        Construct op = generateBinaryInfixOperator(r);

        if (op != null)
        {
            writeSpaceAfterBinaryInfixOperator(r, op);
            Construct first = generateFirstBinaryInfixOperand(w, op);
            r.playBack(w);
            Construct second = generateSecondBinaryInfixOperand(w, op);
            result = createBinaryInfixOperatorExpression(first, op, second);
        }

        return result;
    }

    /**
        Generates test data for the first operand of the specified binary
        infix operator: a construct representing the operand is generated and
        returned after the corresponding source code is written using the
        specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the binary infix operator
        @return the generated operand construct that corresponds to the
        source code that was written using 'w'
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateFirstBinaryInfixOperand(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperandBeforeOperator(w, op);
    }

    /**
        Generates test data for the second operand of the specified binary
        infix operator: a construct representing the operand is generated and
        returned after the corresponding source code is written using the
        specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the binary infix operator
        @return the generated operand construct that corresponds to the
        source code that was written using 'w'
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateSecondBinaryInfixOperand(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperandAfterOperator(w, op);
    }

    /**
        Generates test data for the operand of the specified operator that
        precedes the operator: a construct representing the operand is
        generated an returned after the corresponding source code is written
        using the specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the operator whose operand we're
        generating
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateOperandBeforeOperator(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperand(w, op, true);
    }

    /**
        Generates test data for the operand of the specified operator that
        follows the operator: a construct representing the operand is
        generated an returned after the corresponding source code is written
        using the specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the operator whose operand we're
        generating
        @exception IOException thrown if the source code could not be written
    */
    protected Construct
        generateOperandAfterOperator(IndentWriter w, Construct op)
        throws IOException
    {
        return generateOperand(w, op, false);
    }

    /**
        Generates test data for the operand of the specified operator that
        precedes the operator: a construct representing the operand is
        generated an returned after the corresponding source code is written
        using the specified writer.

        @param w the writer to use to write the operand's source code
        @param op the construct representing the operator whose operand we're
        generating
        @exception IOException thrown if the source code could not be written
    */
    private Construct generateOperand(IndentWriter w, Construct op,
                                      boolean isBeforeOperator)
        throws IOException
    {
        Construct result = null;

        RecordingIndentWriter r = new RecordingIndentWriter();
        Construct operandOp;
        while (result == null)
        {
            int num = generateRandomNumber(0, 3);
            switch (num)
            {
            case 0:
                result = generatePrimitiveExpression(w);
                break;
            case 1:
                r.clear();
                writeSpaceBeforeUnaryPrefixOperator(r);
                operandOp = generateUnaryPrefixOperator(r);
                if (operandOp != null &&
                    isAllowedOperandOperatorFor(op, operandOp,
                                                isBeforeOperator))
                {
                    r.playBack(w);
                    writeSpaceAfterUnaryPrefixOperator(w, operandOp);
                    Construct subOperand =
                        generateOperandAfterOperator(w, operandOp);
                    result = createUnaryPrefixOperatorExpression(operandOp,
                                                                 subOperand);
                }
                break;
            case 2:
                r.clear();
                writeSpaceBeforeUnaryPostfixOperator(r);
                operandOp = generateUnaryPostfixOperator(r);
                if (operandOp != null &&
                    isAllowedOperandOperatorFor(op, operandOp,
                                                isBeforeOperator))
                {
                    Construct subOperand =
                        generateOperandBeforeOperator(w, operandOp);
                    r.playBack(w);
                    writeSpaceAfterUnaryPostfixOperator(w, operandOp);
                    result = createUnaryPostfixOperatorExpression(subOperand,
                                                                  operandOp);
                }
                break;
            case 3:
                r.clear();
                writeSpaceBeforeBinaryInfixOperator(r);
                operandOp = generateBinaryInfixOperator(r);
                if (operandOp != null &&
                    isAllowedOperandOperatorFor(op, operandOp,
                                                isBeforeOperator))
                {
                    Construct first =
                        generateOperandBeforeOperator(w, operandOp);
                    r.playBack(w);
                    writeSpaceAfterBinaryInfixOperator(w, operandOp);
                    Construct second =
                        generateOperandAfterOperator(w, operandOp);
                    result = createBinaryInfixOperatorExpression(first,
                                                    operandOp, second);
                }
                break;
            default:
                Assert.unreachable();
                break;
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Indicates whether an operand that is an operator expression with
        the specified operand operator ('operandOp') is allowed as an
        operand of the specified operator 'op'. The operand is to precede
        or follow the operator according to the specified flag.

        @param op the operator that the operand operator's operand is to be
        an operand of
        @param operandOp the operator part of the operand
        @param isOperandBeforeOp true if the operand is to precede 'op' and
        false if the operand is to follow 'op'
    */
    protected boolean
        isAllowedOperandOperatorFor(Construct op, Construct operandOp,
                                    boolean isOperandBeforeOp)
    {
        // The operand operator 'operandOp' is allowed if it will be applied
        // before 'op' will.
        return (isOperandBeforeOp && applyBefore(operandOp, op)) ||
               (isOperandBeforeOp == false &&
                applyBefore(op, operandOp) == false);
    }

    /**
        Indicates whether the first specified operator should be applied
        before the second specified operator is if the first operator
        precedes the second one.

        @param op1 the construct representing the first operator
        @param op2 the construct representing the second operator
        @return true iff the first operator should be applied before the
        second operator is, if the first operator precedes the second one
    */
    protected boolean applyBefore(Construct op1, Construct op2)
    {
        Assert.require(op1 != null);
        Assert.require(op2 != null);

        return data(op1).applyBefore(data(op2));
    }

    /**
        @see ExpressionParserHelper#data(Construct)
    */
    protected OperatorData data(Construct op)
    {
        return ExpressionParserHelper.data(op);
    }


    /**
        Writes any space(s) to be output before a unary prefix operator.
        <p>
        This implementation doesn't write a space since a unary prefix
        operator can start an expression, which can start a line, which would
        result in the line not being indented a whole number of levels. If it
        doesn't start an expression then it must follow a binary operator or
        another unary prefix, so the missing space won't cause problems
        provided a space is output after every binary or unary prefix
        operator.

        @param w the writer to use to write any spaces before the operator
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
        @see #writeSpaceAfterBinaryInfixOperator(IndentWriter, Construct)
        @see #writeSpaceAfterUnaryPrefixOperator(IndentWriter, Construct)
    */
    protected void writeSpaceBeforeUnaryPrefixOperator(IndentWriter w)
        throws IOException
    {
        // empty - no space is to be written
    }

    /**
        Writes any space(s) to be output after the specified unary prefix
        operator.

        @param w the writer to use to write any spaces after 'op'
        @param op the unary prefix operator that any spaces written by this
        method will follow
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
        @see #writeSpaceBeforeUnaryPrefixOperator(IndentWriter)
    */
    protected void
        writeSpaceAfterUnaryPrefixOperator(IndentWriter w, Construct op)
        throws IOException
    {
        writeSpaceAfterOperator(w, op);
    }

    /**
        Writes any space(s) to be output before a unary postfix operator.

        @param w the writer to use to write any spaces before the operator
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
    */
    protected void writeSpaceBeforeUnaryPostfixOperator(IndentWriter w)
        throws IOException
    {
        writeSpaceBeforeOperator(w);
    }

    /**
        Writes any space(s) to be output after the specified unary postfix
        operator.

        @param w the writer to use to write any spaces after 'op'
        @param op the unary postfix operator that any spaces written by this
        method will follow
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
    */
    protected void
        writeSpaceAfterUnaryPostfixOperator(IndentWriter w, Construct op)
        throws IOException
    {
        writeSpaceAfterOperator(w, op);
    }

    /**
        Writes any space(s) to be output before a binary infix operator.

        @param w the writer to use to write any spaces before the operator
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
    */
    protected void writeSpaceBeforeBinaryInfixOperator(IndentWriter w)
        throws IOException
    {
        writeSpaceBeforeOperator(w);
    }

    /**
        Writes any space(s) to be output after the specified binary infix
        operator.

        @param w the writer to use to write any spaces after 'op'
        @param op the binary infix operator that any spaces written by this
        method will follow
        @exception IOException thrown if trying to write the space resulted
        in an I/O error
        @see #writeSpaceBeforeUnaryPrefixOperator(IndentWriter)
    */
    protected void
        writeSpaceAfterBinaryInfixOperator(IndentWriter w, Construct op)
        throws IOException
    {
        writeSpaceAfterOperator(w, op);
    }


    // Abstract methods

    /**
        Generates test data for a primitive expression construct: a construct
        is generated and returned after the corresponding source code is
        written using the specified writer.
        <p>
        Note: this method doesn't adjust the part level at all.

        @param w the writer to use to write out the source code corresponding
        to the returned primitive expression construct
        @return a newly-generated primitive expression construct instance
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    protected abstract Construct generatePrimitiveExpression(IndentWriter w)
        throws IOException;

    /**
        @see AbstractConstructTestDataCreator#generateUniformRandomNumber(int, int)
    */
    protected abstract int
        generateRandomNumber(int lowerBound, int upperBound);
        // Assert.require(lowerBound >= 0);
        // Assert.require(upperBound >= lowerBound);
        // Assert.ensure(result >= lowerBound);
        // Assert.ensure(result <= upperBound);


    /**
        Generates test data for a unary prefix operator construct: a
        construct is generated and returned after the corresponding source
        code is written using the specified writer, or null is returned and
        nothing is written if the language has no unary prefix operators.

        @param w the writer to use to write the source code
        @return the generated unary prefix operator construct that
        corresponds to the source code that was written using 'w', or null if
        the language has no unary prefix operators
        @exception IOException thrown if the source code could not be written
    */
    protected abstract Construct
        generateUnaryPrefixOperator(IndentWriter w)
        throws IOException;

    /**
        Creates and returns a construct that represents an expression
        consisting of the specified unary prefix operator and the specified
        operand.

        @param op the unary prefix operator part of the expression
        @param operand the operand part of the expression
        @return a unary prefix operator expression construct with 'op' and
        'operand' as its parts
    */
    protected abstract Construct
        createUnaryPrefixOperatorExpression(Construct op, Construct operand);
        // Assert.require(op != null);
        // Assert.require(operand != null);

    /**
        Generates test data for a unary postfix operator construct: a
        construct is generated and returned after the corresponding source
        code is written using the specified writer, or null is returned and
        nothing is written if the language has no unary postfix operators.

        @param w the writer to use to write the source code
        @return the generated unary postfix operator construct that
        corresponds to the source code that was written using 'w', or null if
        the language has no unary postfix operators
        @exception IOException thrown if the source code could not be written
    */
    protected abstract Construct
        generateUnaryPostfixOperator(IndentWriter w)
        throws IOException;

    /**
        Creates and returns a construct that represents an expression
        consisting of the specified unary postfix operator and the specified
        operand.

        @param operand the operand part of the expression
        @param op the unary postfix operator part of the expression
        @return a unary postfix operator expression construct with 'op' and
        'operand' as its parts
    */
    protected abstract Construct
        createUnaryPostfixOperatorExpression(Construct operand, Construct op);
        // Assert.require(operand != null);
        // Assert.require(op != null);


    /**
        Generates test data for a binary infix operator construct: a
        construct is generated and returned after the corresponding source
        code is written using the specified writer, or null is returned and
        nothing is written if the language has no binary infix operators.

        @param w the writer to use to write the source code
        @return the generated binary infix operator construct that
        corresponds to the source code that was written using 'w', or null if
        the language has no binary infix operators
        @exception IOException thrown if the source code could not be written
    */
    protected abstract Construct
        generateBinaryInfixOperator(IndentWriter w)
        throws IOException;

    /**
        Creates and returns a construct that represents an expression
        consisting of the specified binary infix operator and the specified
        operands.

        @param firstOperand the first operand part of the expression
        @param op the unary postfix operator part of the expression
        @param secondOperand the second operand part of the expression
        @return a unary postfix operator expression construct with 'op', and
        'firstOperand' and 'secondOperand' as its parts
    */
    protected abstract Construct
        createBinaryInfixOperatorExpression(Construct firstOperand,
                                Construct op, Construct secondOperand);
        // Assert.require(firstOperand != null);
        // Assert.require(op != null);
        // Assert.require(secondOperand != null);


    /**
        Writes any space(s) to be output before an operator.

        @param w the writer to use to write any spaces before the operator
    */
    protected abstract void writeSpaceBeforeOperator(IndentWriter w)
        throws IOException;

    /**
        Writes any space(s) to be output after the specified operator.

        @param w the writer to use to write any spaces after 'op'
        @param op the operator that any spaces written by this method will
        follow
    */
    protected abstract void
        writeSpaceAfterOperator(IndentWriter w, Construct op)
        throws IOException;
}
