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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.source.SourceLocationStartPositionComparator;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

/**
    An abstract base class for classes that help parse expression constructs.
    It makes several assumptions:
    <ul>
        <li>all operator construct classes are subclasses of
            SingleTokenConstruct</li>
        <li>all tokens that represent operators have an object value
            associated with them that is an OperatorData object describing
            the operator</li>
        <li>all operators are either unary prefix, unary postfix or binary
            infix</li>
    </ul>
    <p>
    An expression is assumed to consist of primitive expressions and
    operators. Thus, for example, a parenthesized expression is considered to
    be a primitive expression. It is also assumed not to consist of an entire
    line, insofar as it doesn't check for lines indented underneath it, nor
    for extra tokens following it on the same line. (An 'expression
    statement' construct of some sort is usually defined to represent
    expressions that consist of an entire line: such classes should do the
    aforementioned checks.)
    <p>
    This class' parsing algorithm is loosely based on the one illustrated on
    page 170 and described on pages 165-172 of <em>Program Translation
    Fundamentals: Methods and Issues</em> by Peter Calingaert [Computer
    Science Press, 1988].
    <p>
    This is a parser helper class rather than a Parser subclass since all
    parsers for constructs of a given language usually have to subclass a
    language-specific Parser subclass, and since Java doesn't support
    multiple inheritance such classes could not also subclass this class if
    it were a Parser subclass.

    @author James MacKay
    @see Token#hasObjectValue
    @see Token#objectValue
    @see OperatorData
    @see SingleTokenConstruct
*/
public abstract class ExpressionParserHelper
    extends AbstractParserHelper
    implements OperatorConstants
{
    // Constants

    /**
        The resources used by this class.
        <p>
        This constant does not have the customary name '_resources' since
        subclasses that are inner classes of an outer class that has a
        _resources constant will not be able to access the outer class'
        _resources: the compiler will think that this private constant is
        being referred to instead.
    */
    private static final Resources _helperResources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_ENTIRE_EXPRESSION_START_MSG =
            "INVALID_ENTIRE_EXPRESSION_START_MSG";
    private static final String
        TWO_EXPRESSIONS_IN_A_ROW_MSG =
            "TWO_EXPRESSIONS_IN_A_ROW_MSG";
    private static final String
        CANNOT_HANDLE_OPERATOR_MSG =
            "CANNOT_HANDLE_OPERATOR_MSG";
    private static final String
        OPERATOR_CANNOT_BE_PRECEDED_BY_EXPR_MSG =
            "OPERATOR_CANNOT_BE_PRECEDED_BY_EXPR_MSG";
    private static final String
        OPERATOR_CANNOT_BE_FOLLOWED_BY_EXPR_MSG =
            "OPERATOR_CANNOT_BE_FOLLOWED_BY_EXPR_MSG";
    private static final String
        OPERATOR_MUST_BE_PRECEDED_BY_EXPR_MSG =
            "OPERATOR_MUST_BE_PRECEDED_BY_EXPR_MSG";
    private static final String
        OPERATOR_MUST_BE_FOLLOWED_BY_EXPR_MSG =
            "OPERATOR_MUST_BE_FOLLOWED_BY_EXPR_MSG";
    private static final String
        TOO_LOW_UNARY_POSTFIX_PRECEDENCE_MSG =
            "TOO_LOW_UNARY_POSTFIX_PRECEDENCE_MSG";
    private static final String
        TOO_LOW_UNARY_PREFIX_PRECEDENCE_MSG =
            "TOO_LOW_UNARY_PREFIX_PRECEDENCE_MSG";


    // Constructors

    /**
        Constructs an ExpressionParserHelper.
    */
    public ExpressionParserHelper()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);

        // Note: we return 'true' for any type of operator since this parser
        // should be used to handle ones that can't start an expression too.
        return isOperator(tok) || isStartOfPrimitiveExpression(tok);
    }

    /**
        Returns the expression construct that results from parsing the first
        tokens from the specified token list as a subconstruct of another
        construct. If the expression is completely absent then null will be
        returned: an error will also be reported iff it is specified that the
        expression is required to be present.
        <p>
        The tokens in the token list are the as yet unparsed tokens that
        represent (part of) the current line being parsed, and the ones in
        the tokenizer represent the following lines. Thus the tokens in the
        list logically precede the ones in the tokenizer, and so should
        always be processed first.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens will be consumed
        from it by this method
        @param data contains information about the parsing of the expression
        as a subconstruct, including whether it was parsed successfully
        @param p the parser using this helper
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the expression construct that results from parsing the tokens
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public Construct parse(TrackedTokenList line, Tokenizer t,
                           SubconstructParsingData data,
                           Parser p, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty() == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        Construct result;
        try
        {
            Stacks s = new Stacks();

            parseExpression(s, line, t, data, p, handler);

            // Apply any operators that haven't already been applied.
            ConstructStack stack = s.operatorStack();
            while (stack.isEmpty() == false)
            {
                applyTopOperator(s, p, t, data, handler);
            }

            // The operand left on the stack, if any, is the expression that
            // we are to return.
            stack = s.operandStack();
            if (stack.isEmpty())
            {
                // The expression was missing (and its being missing has
                // already been handled).
                result = null;
            }
            else
            {
                result = stack.pop();
                Assert.check(stack.isEmpty());
            }
        }
        catch (InvalidConstructPartException ex)
        {
            // TODO: try to build a partial expression that we can return
            // from the contents of the operator and operand stacks? (In
            // which case their states may be inconsistent: e.g. not
            // necessarily enough operands for each operator.)
            data.setWasParsedSuccessfully(false);
            result = null;
        }

        return result;
    }


    /**
        Returns the OperatorData object corresponding to the specified
        operator construct.

        @param op the operator construct representing the operator whose
        OperatorData object is to be returned
        @return the OperatorData object for the operator represented by 'op'
    */
    public static OperatorData data(Construct op)
    {
        Assert.require(op != null);
        Assert.require(op instanceof SingleTokenConstruct);

        Token opToken = ((SingleTokenConstruct) op).token();
        return data(opToken);
    }

    /**
        Returns the OperatorData object corresponding to the operator that
        the specified operator token represents.

        @param opToken the operator token representing the operator whose
        OperatorData object is to be returned
        @return the OperatorData object for the operator represented by 'op'
    */
    public static OperatorData data(Token opToken)
    {
        Assert.require(opToken != null);
        // Assert.require(isOperator(opToken));
        Assert.require(opToken.hasObjectValue());
        Assert.require(opToken.objectValue() instanceof OperatorData);

        return (OperatorData) opToken.objectValue();
    }


    // Protected methods

    /**
        Parses the expression represented by the first tokens in the
        specified token list, putting the expression's operators and
        operands on the specified stacks.

        @param s the operator and operand stacks onto which to push the
        operators and operands in the expression being parsed
        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens will be consumed
        from it by this method
        @param data contains information about the parsing of the
        expression as a subconstruct, including whether it was parsed
        successfully
        @param p the parser using this helper
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @exception InvalidConstructPartException thrown if a part of the
        expression is invalid and could not be parsed, or if the expression
        is absent but is required to be present
    */
    protected void parseExpression(Stacks s, TrackedTokenList line,
        Tokenizer t, SubconstructParsingData data, Parser p,
        ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(s != null);
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

// TODO: replace the commented out System.err.println()s in this method
// with code to log the information !!!

        int initialLineSize = line.size();
        boolean previousWasExpression = false;
            // true if the previous part of the expression was a (primitive)
            // expression, and false if it was an operator or there has been
            // no previous part
        while (line.isEmpty() == false)
        {
// System.err.println("%%% # operands = " + s.operandStack().size() + ", # operators = " + s.operatorStack().size());
            Token tok = line.get(0);
            if (isOperator(tok))
            {
// System.err.println("   [found an operator]");
                checkOperatorAndPrevious(tok, s, previousWasExpression,
                                         p, t, handler);
// System.err.println("   --> adding a " + arityName(data(tok)) + " " + fixityName(data(tok)) + " operator with precedence " + data(tok).precedence() + " [ID = " + tok.id() + "]");
                createAndPushOperator(line, s, p, t, data, handler);
                previousWasExpression = false;
            }
            else if (isStartOfPrimitiveExpression(tok))
            {
// System.err.println("   [found an operand]");
                if (previousWasExpression)
                {
                    handleConsecutiveExpressions(line, s, p, t, handler);
                }

                boolean oldSuccess = data.wasParsedSuccessfully();
                data.setWasParsedSuccessfully(true);
                Construct expr =
                    parsePrimitiveExpression(line, t, data, handler);
                boolean newSuccess = data.wasParsedSuccessfully();
                if (newSuccess)
                {
                    checkOperandAndPrevious(expr, s, p, t, handler);
// System.err.println("   ==> adding an operand of class " + expr.getClass().getName());
                    s.operandStack().push(expr);
                }
                else
                {
                    // The reason why the primitive expression couldn't be
                    // parsed has already been reported.
                    throw InvalidConstructPartException.instance();
                }
                data.setWasParsedSuccessfully(oldSuccess && newSuccess);
                previousWasExpression = true;
            }
            else
            {
// System.err.println("================ END OF EXPRESSION REACHED ===========");
                // We've (apparently) reached the end of the expression.
                if (line.size() == initialLineSize)
                {
                    // No expression at all was processed.
                    handleInvalidEntireExpressionStart(line, t, data,
                                                       p, handler);
                }
                break;  // while
            }
        }

        // If the expression ends with an operator then check that it doesn't
        // need to be followed by an operand.
        if (previousWasExpression == false &&
            s.operatorStack().isEmpty() == false)
        {
            checkTrailingOperator(s, p, t, handler);
        }
    }


    // Protected methods

    /**
        Handles the case where the entire expression cannot be parsed because
        it does not start with a primitive expression or operator.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens should be
        consumed from it by this method
        @param data contains information about the parsing of the
        expression as a subconstruct, including whether it was parsed
        successfully
        @param p the parser using this helper
        @param handler the error handler to use to handle any errors that
        occur during parsing
    */
    protected void handleInvalidEntireExpressionStart(TrackedTokenList line,
                        Tokenizer t, SubconstructParsingData data, Parser p,
                        ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty() == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(p != null);
        Assert.require(handler != null);

        data.setWasParsedSuccessfully(false);

        // Report the invalid expression start.
        Token tok = line.get(0);
        String msg = _helperResources.
            getMessage(INVALID_ENTIRE_EXPRESSION_START_MSG,
                       tokenDescription(tok));
        handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                           tok.location(), p, t, handler);

        // Consume the rest of the tokens in 'line', since we can't
        // necessarily pick up parsing the rest of the expression at a later
        // point in 'line'.
        line.advanceToEnd();

        // Assert.ensure("no tokens were consumed from 't'");
    }

    /**
        Checks that the operator represented by the specified token, as well
        as the expression part(s) that precede it (if any), are valid.
        <p>
        This implementation only checks one preceding part (iff there is
        one).

        @param opTok the token that represents the operator to check
        @param s the operand and operator stacks that contain the expressions
        and operators that have preceded 'opTok'
        @param previousWasExpression true if an expression preceded 'opTok',
        and false if either an operator preceded 'opTok' or nothing did
        @param p the parser on whose behalf the expression is being parsed
        @param t the tokenizer from which the expression's tokens originate
        @param handler the error handler to use to handle any errors
        @exception InvalidConstructPartException thrown if the operator is
        invalid, or is invalid in combination with the expression or operator
        that preceded it (if any)
    */
    protected void checkOperatorAndPrevious(Token opTok, Stacks s,
        boolean previousWasExpression, Parser p, Tokenizer t,
        ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(opTok != null);
        Assert.require(s != null);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        OperatorData opData = data(opTok);
        if (isBinaryInfix(opData))
        {
            // A binary infix operator must be preceded by an expression.
            if (previousWasExpression == false)
            {
                handleOperatorMissingPrecedingExpression(opTok, p,
                                                         t, handler);
            }

            // A binary infix operator cannot be preceded by a unary postfix
            // operator with a lower precedence (since otherwise the postfix
            // operator becomes its first operand, which is an error since
            // operands must be full expressions).
            if (s.operatorStack().isEmpty() == false)
            {
                Construct prevOp = s.operatorStack().top();
                OperatorData prevData = data(prevOp);

                if (isUnaryPostfix(prevData) &&
                    prevData.applyBefore(opData) == false)
                {
                    String msg = _helperResources.
                        getMessage(TOO_LOW_UNARY_POSTFIX_PRECEDENCE_MSG,
                                   arityName(opData), fixityName(opData));
                    handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                                       opTok.location(), p, t, handler);
                    throw InvalidConstructPartException.instance();
                }
            }
        }
        else if (isUnaryPostfix(opData))
        {
            // A unary postfix operator must be preceded by an expression.
            if (previousWasExpression == false)
            {
                handleOperatorMissingPrecedingExpression(opTok, p,
                                                         t, handler);
            }
        }
        else if (isUnaryPrefix(opData))
        {
            // Unary prefix operators must NOT be preceded by an expression
            // (including a unary postfix operator, which is the end of an
            // expression).
            boolean isValid = true;
            if (previousWasExpression)
            {
                isValid = false;
            }
            else if (s.operatorStack().isEmpty() == false)
            {
                Construct prevOperator = s.operatorStack().top();
                OperatorData prevData = data(prevOperator);
                if (prevData.arity() == 1 && prevData.fixity() == POSTFIX)
                {
                    isValid = false;
                }
            }

            if (isValid == false)
            {
                String msg = _helperResources.
                    getMessage(OPERATOR_CANNOT_BE_PRECEDED_BY_EXPR_MSG,
                               arityName(opData), fixityName(opData));
                handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                                   opTok.location(), p, t, handler);
                throw InvalidConstructPartException.instance();
            }

            // A binary infix operator cannot be followed by a unary prefix
            // operator with a lower precedence (since otherwise the prefix
            // operator becomes its second operand, which is an error since
            // operands must be full expressions).
            if (s.operatorStack().isEmpty() == false)
            {
                Construct prevOp = s.operatorStack().top();
                OperatorData prevData = data(prevOp);

                if (isBinaryInfix(prevData) &&
                    prevData.applyBefore(opData))
                {
                    // Note: the preceding binary operator is used as the
                    // location of the error.
                    String msg = _helperResources.
                        getMessage(TOO_LOW_UNARY_PREFIX_PRECEDENCE_MSG,
                                   arityName(prevData),
                                   fixityName(prevData));
                    handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                                       prevOp.location(), p, t, handler);
                    throw InvalidConstructPartException.instance();
                }
            }
        }
        else
        {
            // The arity and/or fixity of the operator is not that of an
            // operator that this parser helper can handle. (This is a parser
            // implementation error, not a problem with the source code being
            // parsed.)
            String msg = _helperResources.
                getMessage(CANNOT_HANDLE_OPERATOR_MSG,
                           arityName(opData), fixityName(opData));
            throw new ParsingException(msg);
        }
    }

    /**
        Handles the expression that must immediately precede the operator
        represented by the specified token being missing.

        @param opToken the token that represents the operator that requires
        that an expression immediately precedes it
        @param p the parser on whose behalf we're parsing the expression
        @param t the tokenizer from which the expression's tokens originate
        @param handler the error handler to use to handle the error
    */
    protected void handleOperatorMissingPrecedingExpression(Token opToken,
        Parser p, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        OperatorData data = data(opToken);
        String msg = _helperResources.
            getMessage(OPERATOR_MUST_BE_PRECEDED_BY_EXPR_MSG,
                       arityName(data), fixityName(data));
        handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                           opToken.location(), p, t, handler);
        throw InvalidConstructPartException.instance();
    }

    /**
        Checks that the specified operand/expression, as well as the
        expression part(s) that precede it (if any) are valid. The expression
        part that immediately precedes the operand, if there is one, has
        already been determined to be an operator, not another expression.
        <p>
        This implementation only checks one preceding part (iff there is
        one).

        @param operand the operand to check: it is an expression construct
        @param s the operand and operator stacks that contain the
        expressions and operators that have preceded 'operand'
        @param p the parser on whose behalf the expression is being parsed
        @param t the tokenizer from which the expression's tokens originate
        @param handler the error handler to use to handle any errors
        @exception InvalidConstructPartException thrown if the operand is
        invalid, or is invalid in combination with the operator that precedes
        it (if any)
    */
    protected void checkOperandAndPrevious(Construct operand, Stacks s,
        Parser p, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(operand != null);
        Assert.require(s != null);
        // Assert.require("the preceding expression part, if there is " +
        //                "one, is an operator, not an expression");
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        if (s.operatorStack().isEmpty() == false)
        {
            Construct prevOp = s.operatorStack().top();
            OperatorData data = data(prevOp);

            // A unary postfix operator cannot be followed by an expression.
            if (isUnaryPostfix(data))
            {
                // A unary postfix operator cannot be followed by an
                // expression.
                String msg = _helperResources.
                    getMessage(OPERATOR_CANNOT_BE_FOLLOWED_BY_EXPR_MSG,
                               arityName(data), fixityName(data));
                handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                                   prevOp.location(), p, t, handler);
                throw InvalidConstructPartException.instance();
            }
        }
    }

    /**
        Checks that the operator that was at the end of the expression
        represented by the contents of the operator and operand stacks is
        valid. (If this method is called then the expression ended with an
        operator: this does not have to be checked here.)
        <p>
        The trailing operator is invalid if it must be followed by an operand
        and is not. (The operator itself has already been checked.)

        @param s the operator and operand stacks whose contents represent the
        expression that ends with an operator
        @param p the parser on whose behalf we are parsing the expression
        @param t the tokenizer from which the expression's tokens originate
        @param handler the error handler to use to handle any errors
        @exception InvalidConstructPartException thrown if the operator is
        invalid and parsing may not be able to be continued successfully
    */
    protected void checkTrailingOperator(Stacks s, Parser p, Tokenizer t,
                                         ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(s != null);
        Assert.require(s.operatorStack().isEmpty() == false);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Unary prefix and binary infix operators must be followed by
        // another operand/expression.
        Construct op = s.operatorStack().top();
        OperatorData data = data(op);
        if (isBinaryInfix(data) || isUnaryPrefix(data))
        {
            String msg = _helperResources.
                getMessage(OPERATOR_MUST_BE_FOLLOWED_BY_EXPR_MSG,
                           arityName(data), fixityName(data));
            handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                               op.location(), p, t, handler);
            throw InvalidConstructPartException.instance();
        }
    }

    /**
        Handles the occurrence of two consecutive expressions with no
        operator or separator between them. The first token of the specified
        line is the start of the second of the consecutive expressions, and
        the expression on top of the operand stack is the first.
        <p>
        This implementation always throws an InvalidConstructPartException.

        @param line the list of tokens whose first token is the start of the
        second consecutive expression
        @param s the operator and operand stacks
        @param p the parser on whose behalf we are parsing the expression
        that has the consecutive expressions as subexpressions
        @param t the tokenizer from which the expression's tokens originate
        @param handler the error handler to use to handle any errors
        @exception InvalidConstructPartException thrown if the expressions
        being consecutive is invalid (which is always the case for most
        languages) and it is unlikely that parsing can be successfully
        continued
    */
    protected void handleConsecutiveExpressions(TrackedTokenList line,
        Stacks s, Parser p, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty() == false);
        Assert.require(s != null);
        Assert.require(s.operandStack().isEmpty() == false);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        Construct firstExpr = s.operandStack().top();
        String msg = _helperResources.
            getMessage(TWO_EXPRESSIONS_IN_A_ROW_MSG);
        handleParsingError(NON_FATAL_ERROR_LEVEL, msg,
                           firstExpr.location(), p, t, handler);
        throw InvalidConstructPartException.instance();
    }


    /**
        Creates an operator from the first token in the specified token list
        and pushes it onto the specified operand stack.

        @see #pushOperator(Construct, ExpressionParserHelper.Stacks, Parser, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    protected void createAndPushOperator(TrackedTokenList line, Stacks s,
        Parser p, Tokenizer t, SubconstructParsingData data,
        ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty() == false);
        Assert.require(isOperator(line.get(0)));
        Assert.require(s != null);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        Construct op = createOperatorConstruct(line.get(0));
        finishConstruct(op, t, data, line.get(0).location(), handler);
        line.advance();  // over the operator
        pushOperator(op, s, p, t, data, handler);
    }

    /**
        Pushes the specified operator construct onto the specified operator
        stack after applying those operators already on the operator stack
        that are to be applied before that operator. The specified operand
        stack contains the operators' operands: its topmost constructs should
        be the operands of the operator being pushed onto the operator stack
        that precede the operator in the expression.

        @param op the operator that is to be pushed onto 'operatorStack'
        @param s the stacks containing the operators and operands that
        preceded 'op'
        @param p the parser on whose behalf we're parsing an expression
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens will be consumed
        from it by this method
        @param data contains information about the parsing of the expression
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
    */
    protected void pushOperator(Construct op, Stacks s, Parser p,
        Tokenizer t, SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(op != null);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        ConstructStack operatorStack = s.operatorStack();
        while (operatorStack.isEmpty() == false &&
               data(operatorStack.top()).applyBefore(data(op)))
        {
            applyTopOperator(s, p, t, data, handler);
        }
        operatorStack.push(op);
    }

    /**
        Applies the operator on the top of the specified operator stack to
        the operand(s) on the top of the operand stack, and pushes the result
        onto the operand stack.

        @param s contains the operator stack from which to get the operator
        to apply, and the operand stack from which to get the operator's
        operands and onto which to push the result of applying the operator
        @param p the parser that we're helping
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens will be consumed
        from it by this method
        @param data contains information about the parsing of the expression
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
    */
    protected void applyTopOperator(Stacks s, Parser p, Tokenizer t,
                        SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(s != null);
        Assert.require(s.operatorStack().isEmpty() == false);
        Assert.require(p != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        Construct op = s.operatorStack().pop();
        int arity = data(op).arity();

        // The operands are popped off of the operand stack in reverse order:
        // e.g. the last operand is popped off first. (There should always be
        // enough operands on the stack.)
        ConstructStack operandStack = s.operandStack();

// TODO: replace the following commented out block with code to log
// the information !!!
/*
if (operandStack.size() < arity)
{
System.err.println("===> operator = " + op.toString());
System.err.println("     arity = " + arity + ", num operands = " +
                   operandStack.size());
System.err.println(s.toString());
}
*/
        Assert.check(operandStack.size() >= arity);
        Construct[] operands = new Construct[arity];
        for (int i = arity - 1; i >= 0; i--)
        {
            operands[i] = operandStack.pop();
        }

        // Push the result of applying the operator to the operands onto the
        // operandStack.
        Construct result = apply(op, operands, handler);
        finishConstruct(result, t, data, expressionLocation(op, operands),
                        handler);
        Assert.check(result != null);
        operandStack.push(result);
    }

    /**
        Returns the location in the source code of the expression consisting
        of the specified operator and operands.

        @param op the construct that represents the operator
        @param operands the constructs that represent the operands
        @return the location of the expression consisting of the operator
        and its operands
    */
    protected SourceLocation expressionLocation(Construct op,
                                                Construct[] operands)
    {
        Assert.require(op != null);
        Assert.require(operands != null);

        int numOperands = operands.length;
        SourceLocationList locs = SourceLocationList.
            createArrayList(numOperands + 1);
        locs.add(op.location());
        for (int i = 0; i < numOperands; i++)
        {
            locs.add(operands[i].location());
        }

        locs.sort(SourceLocationStartPositionComparator.instance());

        SourceLocation result = sourceLocationFactory().create(locs);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Indicates whether the specified operator data describes a unary
        prefix operator.

        @param data the operator data to check
        @return true iff 'data' describes a unary prefix operator
    */
    protected boolean isUnaryPrefix(OperatorData data)
    {
        return (data.arity() == 1) && (data.fixity() == PREFIX);
    }

    /**
        Indicates whether the specified operator data describes a unary
        postfix operator.

        @param data the operator data to check
        @return true iff 'data' describes a unary postfix operator
    */
    protected boolean isUnaryPostfix(OperatorData data)
    {
        return (data.arity() == 1) && (data.fixity() == POSTFIX);
    }

    /**
        Indicates whether the specified operator data describes a binary
        infix operator.

        @param data the operator data to check
        @return true iff 'data' describes a binary infix operator
    */
    protected boolean isBinaryInfix(OperatorData data)
    {
        return (data.arity() == 2) && (data.fixity() == INFIX);
    }


    /**
        Returns the name of the arity specified in the specified operator
        data.

        @param data the operator data that contains the arity
        @return the name of the arity in 'data'
    */
    protected String arityName(OperatorData data)
    {
        return OperatorData.arityName(data.arity());
    }

    /**
        Returns the name of the fixity specified in the specified operator
        data.

        @param data the operator data that contains the fixity
        @return the name of the fixity in 'data'
    */
    protected String fixityName(OperatorData data)
    {
        return OperatorData.fixityName(data.fixity());
    }


    // Abstract methods

    /**
        @return the source location factory to use to create the locations of
        expressions and their subconstructs
    */
    protected abstract SourceLocationFactory sourceLocationFactory();
        // Assert.ensure(result != null);

    /**
        Indicates whether the specified token represents an operator.

        @param tok the token to test
        @return true iff the token represents an operator
    */
    protected abstract boolean isOperator(Token tok);
        // Assert.require(tok != null);

    /**
        Creates and returns an operator construct from the token representing
        the operator.
    */
    protected abstract Construct createOperatorConstruct(Token tok);
        // Assert.require(tok != null);
        // Assert.require(isOperator(tok));
        // Assert.ensure(result != null);

    /**
        Indicates whether the specified token is the first token of a
        primitive expression.

        @param tok the token to test
        @return true iff the token is the first token of a primitive
        expression
    */
    protected abstract boolean isStartOfPrimitiveExpression(Token tok);
        // Assert.require(tok != null);

    /**
        Parses the first tokens returned by the specified tokenizer into a
        primitive expression construct and returns it.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed, and whose first tokens represent the
        primitive expression that this method is to parse
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed: no tokens should be
        consumed from it by this method
        @param data contains information about the parsing of the primitive
        expression as a subconstruct, including whether it was parsed
        successfully
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the construct representing the primitive expression
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    protected abstract Construct
        parsePrimitiveExpression(TrackedTokenList line, Tokenizer t,
                                 SubconstructParsingData data,
                                 ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(line.isEmpty() == false);
        // Assert.require(isStartOfPrimitiveExpression(line.get(0)));
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(handler != null);

    /**
        Applies the specified operator to the specified operands and returns
        the resulting construct.
        <p>
        Note that some or all of the operands may be null (iff they were
        missing).

        @param op the operator to apply to the operands
        @param operands the operator's operands (some or all of which may be
        null)
        @param handler the error handler to use to handle any errors
        @return the construct representing the result of applying the
        operator to the operands
    */
    protected abstract Construct
        apply(Construct op, Construct[] operands, ErrorHandler handler);
        // Assert.require(op != null);
        // Assert.require(operands != null);
        // Assert.require(data(op).arity() == operands.length);
        // Assert.require(handler != null);
        // Assert.ensure(result != null);

    /**
        @param tok a token
        @return a description of 'tok', suitable for use in an error message
    */
    protected abstract String tokenDescription(Token tok);
        // Assert.require(tok != null);
        // Assert.ensure(result != null);


    // Inner classes

    /**
        A struct-like class that contains an operator stack and an operand
        stack.
        <p>
        The main reason for the existence of this class is to help avoid
        passing one of the stacks in place of the other in a method call
        (which is very easy to do since they are both of the same type).
    */
    protected static class Stacks
    {
        // Private fields

        /** The operator stack. */
        private ConstructStack _operatorStack;

        /** The operand stack. */
        private ConstructStack _operandStack;


        // Constructors

        /**
            Constructs a Stacks object containing empty operator and operand
            stacks.
        */
        public Stacks()
        {
            _operandStack = new ConstructStack();
            _operatorStack = new ConstructStack();
        }


        // Public methods

        /**
            @return the operator stack
        */
        public ConstructStack operatorStack()
        {
            return _operatorStack;
        }

        /**
            @return the operand stack
        */
        public ConstructStack operandStack()
        {
            return _operandStack;
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            StringBuffer result = new StringBuffer();

            result.append("Operator Stack:").append(Io.NL).
                   append(_operatorStack.toString()).append(Io.NL).
                   append("Operand Stack:").append(Io.NL).
                   append(_operandStack.toString());

            return result.toString();
        }
    }
}
