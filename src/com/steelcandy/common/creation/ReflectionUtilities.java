/*
 Copyright (C) 2001-2015 by James MacKay.

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

import com.steelcandy.common.io.Io;

import java.text.MessageFormat;

/**
    Class containing various reflection-related utility methods
    and constants.

    @author James MacKay
*/
public class ReflectionUtilities
{
    // Constants

    /**
        The separator between components of a package (or
        fully-qualified class) name.
    */
    public static final String PACKAGE_SEPARATOR = ".";

    /**
        The separator between an outer class' name and an inner one.
    */
    public static final char OUTER_INNER_CLASS_NAME_SEPARATOR_CHAR = '$';


    // Constructors

    /**
        This constructor is private to prevent this class from
        being instantiated.
    */
    private ReflectionUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        @return the stack trace element that describes the method that
        called the method that called us, or null if the method that called
        us doesn't have a caller
        @see #findCaller(int)
    */
    public static StackTraceElement findCaller()
    {
        // 'result' can be null
        return findCaller(2);  // +1 to skip this method
    }

    /**
        Note: if we let 'f' be a method that called us, then level 1
        corresponds to the method that called 'f', level 2 corresponds to the
        method that called the method that called 'f', and so on.

        @param level a number of call levels
        @return the stack trace element that describes the method 'level'
        levels below the method that called us in the call stack, or null if
        we don't have a level 'level' caller
        @see #findCaller
        @see StackTraceElement#getMethodName
        @see StackTraceElement#getLineNumber
    */
    public static StackTraceElement findCaller(int level)
    {
        Assert.require(level > 0);

        StackTraceElement result = null;

        Exception debugEx = new Exception();
        int traceLevel = level + 1;  // +1 to skip this method
        StackTraceElement[] trace = debugEx.getStackTrace();
        if (trace.length > traceLevel)
        {
            result = trace[traceLevel];
            Assert.check(result != null);
System.err.println("caller (level=" + level + ") = " + describeCaller(result));
//System.err.println("  - all callers:");
//System.err.println(describeAllCallers(trace, "    | "));
        }

        // 'result' can be null
        return result;
    }

    /**
        Note: our result isn't localized: it's probably suitable only for use
        in debugging.

        @return a description of the method that called the method that
        called us
        @see #describeCaller(int)
        @see #findCaller
    */
    public static String describeCaller()
    {
        // Assert.ensure(result != null);
        return describeCaller(2);  // +1 for this method
    }

    /**
        Note: our result isn't localized: it's probably suitable only for use
        in debugging.

        @param level a number of call levels
        @return a description of the method 'level' levels below the method
        that called us in the call stack
        @see #describeCaller
        @see #findCaller(int)
    */
    public static String describeCaller(int level)
    {
        Assert.require(level > 0);

        String result;

        StackTraceElement e = findCaller(level + 1);  // +1 for this method
        if (e == null)
        {
            result = "[caller unknown]";
        }
        else
        {
            result = describeCaller(e);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Note: our result isn't localized: it's probably suitable only for use
        in debugging.

        @param prefix the prefix to start every line of the descrption with
        @return a description of the method that called this one, the method
        that called that one, and so on, one call per line
    */
    public static String describeAllCallers(String prefix)
    {
        Assert.require(prefix != null);

        Exception debugEx = new Exception();

        String result = describeAllCallers(debugEx.getStackTrace(), prefix);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the name of the package that the specified class is in. The
        empty string will be returned if the class is in the default package.

        @param c the class whose package's name is to be returned
        @return the class' package's name
        @see #getPackageName(String)
    */
    public static String getPackageName(Class c)
    {
        Assert.require(c != null);

        //Assert.ensure(result != null);
        return getPackageName(c.getName());
    }

    /**
        Returns the name of the package that the class with the specified
        fully-qualified class name is in. The empty string will be returned
        if the class is in the default package. No check are made to determine
        whether a class with the specified name actually exists.

        @param className the fully-qualified class name of the class whose
        package name is to be returned
        @return the class' package's name
        @see #getPackageName(Class)
    */
    public static String getPackageName(String className)
    {
        Assert.require(className != null);

        String result;

        int lastSeparatorIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        if (lastSeparatorIndex < 0)
        {
            result = "";
        }
        else
        {
            // the package name is everything up to, but not including,
            // the past package separator:
            result = className.substring(0, lastSeparatorIndex);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see #getUnqualifiedClassName(String)
    */
    public static String getUnqualifiedClassName(Class c)
    {
        Assert.require(c != null);

        //Assert.ensure(result != null);
        return getUnqualifiedClassName(c.getName());
    }

    /**
        @see #getVeryUnqualifiedClassName(String)
    */
    public static String getVeryUnqualifiedClassName(Class c)
    {
        Assert.require(c != null);

        //Assert.ensure(result != null);
        return getVeryUnqualifiedClassName(c.getName());
    }

    /**
        Given a fully-qualified class name, returns the class' unqualified
        name: that is, the class' name with the package part removed.

        @param fullClassName the fully-qualified class name for which the
        unqualified class name is to be returned
        @return the unqualified class name
    */
    public static String getUnqualifiedClassName(String fullClassName)
    {
        Assert.require(fullClassName != null);

        String result = fullClassName;
        int index = fullClassName.lastIndexOf(PACKAGE_SEPARATOR);
        if (index >= 0)
        {
            if ((index + 1) < fullClassName.length())
            {
                result = fullClassName.substring(index + 1);
            }
            else
            {
                // There is nothing after the last PACKAGE_SEPARATOR.
                result = "";
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Given a fully-qualified class name, returns the class' <em>very</em>
        unqualified name: that is, the class' unqualified name with any and
        all outer class' names removed too.

        @param fullClassName the fully-qualified class name for which the
        unqualified class name is to be returned
        @return the very unqualified class name
        @see #getUnqualifiedClassName(String)
    */
    public static String getVeryUnqualifiedClassName(String fullClassName)
    {
        Assert.require(fullClassName != null);

        String result = getUnqualifiedClassName(fullClassName);

        int index =
                result.lastIndexOf(OUTER_INNER_CLASS_NAME_SEPARATOR_CHAR);
        if (index >= 0)
        {
            // 'result' is qualified with one or more outer class names,
            // which we remove here.
            Assert.check(index + 1 < result.length());
                // otherwise the class name ends in a '$', which shouldn't
                // happen
            result = result.substring(index + 1);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Constructs and returns a fully-qualified class name from
        the name of the class' package and the class' unqualified
        name. (The package name should <em>not</em> end in a period (.).)
        <p>
        Note that the class does not have to exist, and that no validation
        is done on either the unqualified class name, the package name, or
        the resulting fully-qualified class name.

        @param packageName the name of the class' package
        @param unqualifiedClassName the class' unqualified name
        @return the class' fully-qualified class name.
    */
    public static String constructClassName(String packageName,
                                            String unqualifiedClassName)
    {
        Assert.require(packageName != null);
        Assert.require(unqualifiedClassName != null);

        //Assert.ensure(result != null);
        return packageName + PACKAGE_SEPARATOR + unqualifiedClassName;
    }


    /**
        Creates and returns a string representation of a constructor
        for the specified class that takes the specified types of
        arguments.

        @param c the class that the constructor is a member of
        @param ctorArgTypes the Classes for the types of the arguments
        that the constructor takes
        @return a string representation of the constructor's signature
    */
    public static String
        createConstructorSignature(Class c, Class[] ctorArgTypes)
    {
        Assert.require(c != null);
        Assert.require(ctorArgTypes != null);  // though it may be empty

        String ctorName = getUnqualifiedClassName(c);
        return createSignature(null, ctorName, ctorArgTypes);
    }

    /**
        Constructs and returns a string representation of a constructor
        or method signature from the specified information.

        @param resultType the Class representing the type of the method,
        or null if the 'method' is a constructor
        @param methodName the name of the method or constructor. (In the
        case of a constructor the name should be the unqualified name of
        the class)
        @param argumentTypes the Classes representing the types of the
        method's arguments
    */
    public static String createSignature(Class resultType, String methodName,
                                         Class[] argumentTypes)
    {
        // 'resultType' can be null
        Assert.require(methodName != null);
        Assert.require(argumentTypes != null);

        StringBuffer res = new StringBuffer();

        // Append the result's type (if it has one - i.e. if it is a
        // method and not a constructor), and the method's name.
        if (resultType != null)
        {
            res.append(getUnqualifiedClassName(resultType.getName()));
            res.append(" ");
        }
        res.append(methodName);

        // Append the method's/constructor's argument types.
        res.append("(");
        for (int i = 0; i < argumentTypes.length; i++)
        {
            if (i > 0)
            {
                res.append(", ");
            }
            res.append(getUnqualifiedClassName(argumentTypes[i]));
        }
        res.append(")");

        return res.toString();
    }


    /**
        Returns the index of the first element of the specified
        array that is an instance of the specified class, or -1
        if there is no such element in the array.
        <p>
        Note that <code>null</code> is <em>not</em> considered
        to be an instance of any Class, and thus the index of
        a null element will never be returned.

        @param c the Class of the array element we're to return the index of
        @param array the array to search for the instance
        @return the index of the first element in the array that is an
        instance of the class, or null if there's no such element
    */
    public static int indexOfInstanceOf(Class c, Object[] array)
    {
        Assert.require(c != null);
        Assert.require(array != null);  // though it may be empty

        int result = -1;

        for (int i = 0; i < array.length; i++)
        {
            if (c.isInstance(array[i]))
            {
                result = i;
                break;
            }
        }

        Assert.ensure(result >= -1);
        return result;
    }


    // Private static methods

    /**
        Note: our result isn't localized: it's probably suitable only for use
        in debugging.

        @param e a stack trace element
        @return a description of the method call represented by 'e'
        that called us in the call stack
    */
    private static String describeCaller(StackTraceElement e)
    {
        Assert.require(e != null);

        String result = e.getClassName() + "." + e.getMethodName() +
                            "() [line " + e.getLineNumber() + "]";

        Assert.ensure(result != null);
        return result;
    }

    /**
        Note: our result isn't localized: it's probably suitable only for use
        in debugging.

        @param elems a call stack
        @param prefix the prefix to start every line of the descrption with
        @return a description of the method calls that are represented by the
        items in 'elems', one call per line
    */
    private static String describeAllCallers(StackTraceElement[] elems,
                                             String prefix)
    {
        Assert.require(elems != null);
        Assert.require(prefix != null);

        StringBuffer res = new StringBuffer();

        int sz = elems.length; // - 1;
            // -1 to skip the element that represents our caller
        for (int i = 0; i < sz; i++)
        {
            res.append(prefix).append(describeCaller(elems[i])).
                append("\n");
        }

        String result = res.toString();

        Assert.ensure(result != null);
        return result;
    }


    // Main method

    /**
        This main method is used to test this class' methods.

        @param args the command line arguments. They are currently ignored
    */
    public static void main(String[] args)
    {
        out("The package separator: " + PACKAGE_SEPARATOR);
        out("This class' package: " +
                getPackageName(ReflectionUtilities.class));

        System.exit(0);
    }

    /**
        Outputs the specified string, followed by a newline.
        <p>
        This method is only used by the <code>main()</code> method to
        output information as part of testing this class.

        @param msg the string to output
    */
    private static void out(String msg)
    {
        Io.writeLine(msg);
    }
}
