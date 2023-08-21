// Copyright (C) James MacKay

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;
import com.steelcandy.common.debug.AssertionFailedException;

import com.steelcandy.common.Resources;

import java.text.MessageFormat;

/**
    Constructs an object that contains information about an operator. It is
    usually the class of the object returned by an operator token's
    objectValue() method.
    <p>
    As much as is possible of an operator's data in an OperatorData object
    is initialized to invalid values after the OperatorData object is
    constructed. Each piece of the operator's data must be set individually
    using the appropriate <code>set...()</code> method.
    <p>
    An operator's associativity is with respect to all other operators with
    the same precedence. All operators with the same precedence also usually
    have the same associativity, but this is not required.

    @author James MacKay
    @see Token#objectValue
*/
public class OperatorData
    implements OperatorConstants
{
    // Constants

    /**
        The largest arity for which special information (e.g. a name) is
        provided by this class.
    */
    protected static final int MAX_SPECIAL_ARITY = TERNARY;


    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NULLARY_ARITY_NAME = "NULLARY_ARITY_NAME";
    private static final String
        UNARY_ARITY_NAME = "UNARY_ARITY_NAME";
    private static final String
        BINARY_ARITY_NAME = "BINARY_ARITY_NAME";
    private static final String
        TERNARY_ARITY_NAME = "TERNARY_ARITY_NAME";
    private static final String
        DEFAULT_ARITY_NAME = "DEFAULT_ARITY_NAME";

    private static final String
        LEFT_ASSOCIATIVITY_NAME = "LEFT_ASSOCIATIVITY_NAME";
    private static final String
        RIGHT_ASSOCIATIVITY_NAME = "RIGHT_ASSOCIATIVITY_NAME";

    private static final String
        PREFIX_FIXITY_NAME = "PREFIX_FIXITY_NAME";
    private static final String
        POSTFIX_FIXITY_NAME = "POSTFIX_FIXITY_NAME";
    private static final String
        INFIX_FIXITY_NAME = "INFIX_FIXITY_NAME";
    private static final String
        OUTFIX_FIXITY_NAME = "OUTFIX_FIXITY_NAME";

    private static final String
        OPERATOR_DATA_FORMAT = "OPERATOR_DATA_FORMAT";


    // Name array constants

    /**
        An array whose i'th element is the name of the arity of an operator
        that takes i operands.

        @see #arityName(int)
    */
    private static final String[] _arityNames =
        new String[MAX_SPECIAL_ARITY + 1];
    static
    {
        _arityNames[NULLARY] = _resources.getString(NULLARY_ARITY_NAME);
        _arityNames[UNARY]   = _resources.getString(UNARY_ARITY_NAME);
        _arityNames[BINARY]  = _resources.getString(BINARY_ARITY_NAME);
        _arityNames[TERNARY] = _resources.getString(TERNARY_ARITY_NAME);
    };

    /**
        An array containing operator associativity names, indexed by
        associativity constant.

        @see #associativityName(int)
    */
    private static final String[] _associativityNames =
        new String[NUMBER_OF_ASSOCIATIVITIES];
    static
    {
        _associativityNames[LEFT_ASSOCIATIVE] =
            _resources.getString(LEFT_ASSOCIATIVITY_NAME);
        _associativityNames[RIGHT_ASSOCIATIVE] =
            _resources.getString(RIGHT_ASSOCIATIVITY_NAME);
    };

    /**
        An array containing operator fixity names, indexed by fixity
        constant.

        @see #fixityName(int)
    */
    private static final String[] _fixityNames =
        new String[NUMBER_OF_FIXITIES];
    static
    {
        _fixityNames[PREFIX]  = _resources.getString(PREFIX_FIXITY_NAME);
        _fixityNames[POSTFIX] = _resources.getString(POSTFIX_FIXITY_NAME);
        _fixityNames[INFIX]   = _resources.getString(INFIX_FIXITY_NAME);
        _fixityNames[OUTFIX]  = _resources.getString(OUTFIX_FIXITY_NAME);
    };


    // Constants (part II)

    /**
        The formatter to use to construct the string representation of an
        instance of this class.
    */
    private static final MessageFormat
        _formatter = new MessageFormat(_resources.
                                getMessage(OPERATOR_DATA_FORMAT));


    // Private fields

    /** The operator's arity. */
    private int _arity = -1;

    /** The operator's associativity. */
    private int _associativity = -1;

    /** The operator's fixity. */
    private int _fixity = -1;

    /** The operator's precedence level. */
    private int _precedence = -1;


    // Constructors

    /**
        Constructs an OperatorData object. All of the various set...()
        methods must be called for the OperatorData object to be valid.

        @see #setArity
        @see #setAssociativity
        @see #setFixity
        @see #setPrecedence
    */
    public OperatorData()
    {
        // empty
    }


    // Accessor methods

    /**
        @return the operator's arity
    */
    public int arity()
    {
        Assert.ensure(_arity >= 0);
        return _arity;
    }

    /**
        @return the operator's associativity
    */
    public int associativity()
    {
        Assert.ensure(_associativity >= 0);
        Assert.ensure(_associativity < NUMBER_OF_ASSOCIATIVITIES);
        return _associativity;
    }

    /**
        @return the operator's fixity
    */
    public int fixity()
    {
        Assert.ensure(_fixity >= 0);
        Assert.ensure(_fixity < NUMBER_OF_FIXITIES);
        return _fixity;
    }

    /**
        @return the operator's precedence
    */
    public int precedence()
    {
        return _precedence;
    }


    // Setter methods

    /**
        Sets the operator's arity to the one specified.

        @param newArity the operator's new arity
    */
    public void setArity(int newArity)
    {
        Assert.require(newArity >= 0);

        _arity = newArity;
    }

    /**
        Sets the operator's associativity to the one specified.

        @param newAssociativity the operator's new associativity
    */
    public void setAssociativity(int newAssociativity)
    {
        Assert.require(newAssociativity >= 0);
        Assert.require(newAssociativity < NUMBER_OF_ASSOCIATIVITIES);

        _associativity = newAssociativity;
    }

    /**
        Sets the operator's fixity to the one specified.

        @param newFixity the operator's new fixity
    */
    public void setFixity(int newFixity)
    {
        Assert.require(newFixity >= 0);
        Assert.require(newFixity < NUMBER_OF_FIXITIES);

        _fixity = newFixity;
    }

    /**
        Sets the operator's precedence to the specified level.

        @param newPrecedence the operator's new precedence level
    */
    public void setPrecedence(int newPrecedence)
    {
        _precedence = newPrecedence;
    }


    // Validation methods

    /**
        Indicates whether this OperatorData object is valid: that is, whether
        all of the operator's data has been set, and set to valid values.

        @return true iff this OperatorData object is valid
        @see #isValid
    */
    public boolean isValid()
    {
        boolean result = true;
        try
        {
            checkValid();
        }
        catch (AssertionFailedException ex)
        {
            result = false;
        }
        return result;
    }

    /**
        Checks that this OperatorData object is valid: that is, that all of
        its data has been set, and set to valid values.
        <p>
        This method is provided in addition to isValid() since this method
        will provide information about why an OperatorData object is invalid
        (i.e. from the exception's message and stack trace).

        @exception AssertionFailedException thrown if this OperatorData object
        isn't valid (and assertions are enabled)
        @see #isValid
    */
    public void checkValid()
        throws AssertionFailedException
    {
        Assert.check(_arity >= 0);
        Assert.check(_associativity >= 0);
        Assert.check(_associativity < NUMBER_OF_ASSOCIATIVITIES);
        Assert.check(_fixity >= 0);
        Assert.check(_fixity < NUMBER_OF_FIXITIES);
        Assert.check(_precedence >= 0);
    }


    // Public methods

    /**
        Indicates whether, upon encountering the new operator described by
        'newData', the old operator described by this OperatorData (which
        operator is assumed to precede the new operator in the same
        expression) can be applied immediately.
        <p>
        This method returns true if and only if
        <ul>
            <li>the old operator has a higher precedence than the new
                operator, or
            <li>the old operator has the same precedence as the new
                operator, and the old operator is left-associative.
        </ul>
        (The latter case ensures that left-associative operators are
        applied in left-to-right order, and that right-associative operators
        are applied in right-to-left order.)

        @param newData the OperatorData that describes the new operator that
        has just been encountered
        @return true iff the old operator can be applied immediately upon the
        new operator's being encountered
    */
    public boolean applyBefore(OperatorData newData)
    {
        Assert.require(newData != null);

        int oldPrec = this.precedence();
        int newPrec = newData.precedence();
        return (oldPrec > newPrec) ||
               (oldPrec == newPrec &&
                this.associativity() == LEFT_ASSOCIATIVE);
    }

    /**
        @return a clone of this OperatorData object
    */
    public OperatorData cloneData()
    {
        OperatorData result = new OperatorData();

        result.setArity(arity());
        result.setAssociativity(associativity());
        result.setFixity(fixity());
        result.setPrecedence(precedence());

        return result;
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        Object[] args =
        {
            arityName(arity()),
            associativityName(associativity()),
            fixityName(fixity()),
            String.valueOf(precedence())
        };

        return _formatter.format(args);
    }


    // Public static methods

    /**
        Returns the name of the specified arity.

        @param arity the arity whose name is to be returned
        @return the arity's name
    */
    public static String arityName(int arity)
    {
        Assert.require(arity >= 0);

        String result = null;
        if (arity <= MAX_SPECIAL_ARITY)
        {
            result = _arityNames[arity];
        }
        else
        {
            result = _resources.getMessage(DEFAULT_ARITY_NAME,
                                           Integer.toString(arity));
        }
        return result;
    }

    /**
        Returns the name of the specified associativity.

        @param associativity the associativity whose name is to be returned
        @return the associativity's name
    */
    public static String associativityName(int associativity)
    {
        Assert.require(associativity >= 0);
        Assert.require(associativity < NUMBER_OF_ASSOCIATIVITIES);

        return _associativityNames[associativity];
    }

    /**
        Returns the name of the specified fixity.

        @param fixity the fixity whose name is to be returned
        @return the fixity's name
    */
    public static String fixityName(int fixity)
    {
        Assert.require(fixity >= 0);
        Assert.require(fixity < NUMBER_OF_FIXITIES);

        return _fixityNames[fixity];
    }
}
