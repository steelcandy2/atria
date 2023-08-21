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

package com.steelcandy.common.creation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;
import com.steelcandy.common.SteelCandyException;

import com.steelcandy.common.creation.ReflectionUtilities;

import java.lang.reflect.*;

/**
    The class of exception thrown when an ObjectCreator fails to
    create an object.

    @author James MacKay
*/
public class ObjectCreatorException
    extends SteelCandyException
{
    // Reason constants

    /** The first reason constant's value. */
    protected static final int FIRST_REASON = 0;


    /** Indicates that the object's class could not be found. */
    public static final int CLASS_NOT_FOUND = FIRST_REASON;

    /** Indicates that access to the constructor isn't allowed. */
    public static final int SECURITY_RESTRICTION = CLASS_NOT_FOUND + 1;

    /** Indicates that a matching constructor could not be found. */
    public static final int NO_SUCH_CTOR = SECURITY_RESTRICTION + 1;

    /**
        Indicates that the class is abstract, and thus a direct
        instance could not be created.
    */
    public static final int ABSTRACT_CLASS = NO_SUCH_CTOR + 1;

    /** The constructor's modifiers are such that it is not accessible. */
    public static final int NO_ACCESS = ABSTRACT_CLASS + 1;

    /**
        Indicates that one or more of the constructor arguments
        doesn't have the correct type.
    */
    public static final int BAD_CTOR_ARGS = NO_ACCESS + 1;

    /** Indicates that the constructor threw an exception when called. */
    public static final int CTOR_EXCEPTION = BAD_CTOR_ARGS + 1;


    /** The last reason constant's value. */
    protected static final int LAST_REASON = CTOR_EXCEPTION;

    /** The number of reason constants. */
    protected static final int
        NUMBER_OF_REASONS = LAST_REASON - FIRST_REASON + 1;


    // Resources-related constants

    /** The Resources used by this class. */
    private static final Resources _resources =
        ObjectCreatorResourcesLocator.resources;

    /** Resource keys. */
    private static final String CLASS_NOT_FOUND_MSG = "CLASS_NOT_FOUND_MSG";
    private static final String SECURITY_RESTRICTION_MSG =
        "SECURITY_RESTRICTION_MSG";
    private static final String NO_SUCH_CTOR_MSG = "NO_SUCH_CTOR_MSG";
    private static final String ABSTRACT_CLASS_MSG = "ABSTRACT_CLASS_MSG";
    private static final String NO_ACCESS_MSG = "NO_ACCESS_MSG";
    private static final String BAD_CTOR_ARGS_MSG = "BAD_CTOR_ARGS_MSG";
    private static final String CTOR_EXCEPTION_MSG = "CTOR_EXCEPTION_MSG";


    // Private fields

    /**
        The constant that indicates the reason why this exception occurred.
        (It should be one of the reason constants defined in this class.)
    */
    private int _reason;


    // Constructors

    /**
        Creates and returns an ObjectCreatorException that indicates
        that the object's class could not be found.

        @param className the fully-qualified name of the class that
        could not be found
    */
    public static ObjectCreatorException
        createForClassNotFound(String className)
    {
        String msg = _resources.getMessage(CLASS_NOT_FOUND_MSG, className);
        return new ObjectCreatorException(CLASS_NOT_FOUND, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that access to the constructor is not allowed by a security
        policy.

        @param c the Class containing the constructor to which access
        is restricted
        @param ctorArgTypes the Classes for the types of the arguments
        that the constructor to be used to create the object takes
    */
    public static ObjectCreatorException
        createForSecurityRestriction(Class c, Class[] ctorArgTypes)
    {
        String signature = ReflectionUtilities.
                    createConstructorSignature(c, ctorArgTypes);
        String msg = _resources.getMessage(SECURITY_RESTRICTION_MSG,
                                           signature);
        return new ObjectCreatorException(SECURITY_RESTRICTION, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that a constructor that takes the specified type and/or number
        of arguments could not be found in the class.

        @param c the Class in which a matching constructor could not
        be found
        @param ctorArgTypes the Classes for the types of the arguments
        that the constructor to be used to create the object takes
    */
    public static ObjectCreatorException
        createForNoSuchConstructor(Class c, Class[] ctorArgTypes)
    {
        String signature = ReflectionUtilities.
                    createConstructorSignature(c, ctorArgTypes);
        String msg = _resources.getMessage(NO_SUCH_CTOR_MSG, signature);
        return new ObjectCreatorException(NO_SUCH_CTOR, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that a direct instance of the class could not be created
        because that class is abstract.

        @param c the class that is abstract
    */
    public static ObjectCreatorException
        createForAbstractClass(Class c)
    {
        String msg = _resources.getMessage(ABSTRACT_CLASS_MSG, c.getName());
        return new ObjectCreatorException(ABSTRACT_CLASS, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that the constructor to be used to create an object is
        inaccessible due to its access level (e.g. private).

        @param ctor the constructor that is not accessible due to
        its access level
    */
    public static ObjectCreatorException
        createForNoAccess(Constructor ctor)
    {
        Object[] args =
            new Object[] { ctor, Modifier.toString(ctor.getModifiers()) };
        String msg = _resources.getMessage(NO_ACCESS_MSG, args);
        return new ObjectCreatorException(NO_ACCESS, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that an object could not be created because the wrong number
        and/or the wrong types of arguments were passed to the
        constructor.

        @param ctor the constructor that was passed the bad arguments
        @param args the arguments that were passed to the constructor
    */
    public static ObjectCreatorException
        createForBadConstructorArguments(Constructor ctor, Object[] args)
    {
        // Construct a displayable version of the types of the arguments
        // that were passed to the constructor.
        StringBuffer buf = new StringBuffer("(");
        for (int i = 0; i < args.length; i++)
        {
            if (i > 0)
            {
                buf.append(", ");
            }
            buf.append(args[i].getClass().getName());
        }
        buf.append(")");

        Integer argsLength = new Integer(args.length);
        String msg = _resources.getMessage(BAD_CTOR_ARGS_MSG,
                         new Object[] { ctor, argsLength, buf.toString() });
        return new ObjectCreatorException(BAD_CTOR_ARGS, msg);
    }

    /**
        Creates and returns an ObjectCreatorException that indicates
        that the constructor used to create the object threw an
        exception or error.

        @param ctor the constructor that threw the exception
        @param ex the exception or error that the constructor threw
    */
    public static ObjectCreatorException
        createForConstructorException(Constructor ctor, Throwable ex)
    {
        Object[] args =
        {
            ctor,
            ex.getClass().getName(),
            message(ex),
            stackTrace(ex)
        };
        String msg = _resources.getMessage(CTOR_EXCEPTION_MSG, args);
        return new ObjectCreatorException(CTOR_EXCEPTION, msg, ex);
    }


    /**
        Constructs an ObjectCreatorException from the reason constant
        indicating the reason why the exception occurred and a message
        describing why it occurred.
        <p>
        This constructor should only be called by other constructors
        in this class or in a subclass.

        @param reason the reason constant indicating why the exception
        being constructed occurred
        @param msg a description of why the exception occurred
    */
    protected ObjectCreatorException(int reason, String msg)
    {
        super(msg);
        Assert.require(reason >= FIRST_REASON);
        Assert.require(reason <= LAST_REASON);

        _reason = reason;
    }

    /**
        Constructs an ObjectCreatorException from the reason constant
        indicating the reason why the exception occurred, a message
        describing why it occurred, and another exception.
        <p>
        This constructor should only be called by other constructors
        in this class or in a subclass.

        @param reason the reason constant indicating why the exception
        being constructed occurred
        @param msg a description of why the exception occurred
        @param ex the exception that caused this exception
    */
    protected ObjectCreatorException(int reason, String msg, Throwable ex)
    {
        super(msg, ex);
        Assert.require(reason >= FIRST_REASON);
        Assert.require(reason <= LAST_REASON);

        _reason = reason;
    }


    // Public method

    /**
        @return the reason constant corresponding to the reason
        why this exception was thrown
    */
    public int reason()
    {
        return _reason;
    }
}
