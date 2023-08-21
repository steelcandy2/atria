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

package com.steelcandy.plack.common.generation.java;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.generation.CommonCodeGenerationUtilities;

import java.util.HashSet;
import java.util.Set;

/**
    A class that contains various utility methods useful in generating
    Java source code as target code.
    <p>
    Note: in a language with multiple inheritance this class would be used
    as a base class. However, in Java a language-specific singleton subclass
    is usually defined and then accessed from the various classes that
    generate Java source code, since such classes usually already have a
    superclass.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class CommonJavaGenerationUtilities
    extends CommonCodeGenerationUtilities
{
    // Constants

    /**
        A set of all of the Java reserved words and other special names
        that generated target code would be well advised to avoid.
    */
    private static final Set
        _specialJavaNames = createSpecialJavaNamesSet();


    // Constructors

    /**
        Constructs a CommonJavaGenerationUtilities object.
    */
    public CommonJavaGenerationUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        @param name a name/identifier
        @return true iff 'name' is the same as a Java reserved word or
        other special name that generated target code would be well
        advised to avoid
    */
    public static boolean isSpecialJavaName(String name)
    {
        Assert.require(name != null);

        boolean result = _specialJavaNames.contains(name);

        return result;
    }


    // Private static methods

    /**
        @return a set containing all of the Java reserved words and other
        special names that generated target code would be well advised to
        avoid: all of the set's elements will be Strings
    */
    private static Set createSpecialJavaNamesSet()
    {
        Set result = new HashSet(200);

        // Reserved words (from Java 1.0)
        result.add("abstract");
        result.add("boolean");
        result.add("break");
        result.add("byte");
        result.add("byvalue");
        result.add("case");
        result.add("cast");
        result.add("catch");
        result.add("char");
        result.add("class");
        result.add("const");
        result.add("continue");
        result.add("default");
        result.add("do");
        result.add("double");
        result.add("else");
        result.add("extends");
        result.add("false");
        result.add("final");
        result.add("finally");
        result.add("float");
        result.add("for");
        result.add("future");
        result.add("generic");
        result.add("goto");
        result.add("if");
        result.add("implements");
        result.add("import");
        result.add("inner");
        result.add("instanceof");
        result.add("int");
        result.add("interface");
        result.add("long");
        result.add("native");
        result.add("new");
        result.add("null");
        result.add("operator");
        result.add("outer");
        result.add("package");
        result.add("private");
        result.add("protected");
        result.add("public");
        result.add("rest");
        result.add("return");
        result.add("short");
        result.add("static");
        result.add("super");
        result.add("switch");
        result.add("synchronized");
        result.add("this");
        result.add("throw");
        result.add("throws");
        result.add("transient");
        result.add("true");
        result.add("try");
        result.add("var");
        result.add("void");
        result.add("volatile");
        result.add("while");

        // Classes in java.lang (as of 1.3)
        result.add("Cloneable");
        result.add("Comparable");
        result.add("Runnable");
        result.add("Boolean");
        result.add("Byte");
        result.add("Character");
        result.add("Class");
        result.add("ClassLoader");
        result.add("Compiler");
        result.add("Double");
        result.add("Float");
        result.add("InheritableThreadLocal");
        result.add("Integer");
        result.add("Long");
        result.add("Math");
        result.add("Number");
        result.add("Object");
        result.add("Package");
        result.add("Process");
        result.add("Runtime");
        result.add("RuntimePermission");
        result.add("SecurityManager");
        result.add("Short");
        result.add("StrictMath");
        result.add("String");
        result.add("StringBuffer");
        result.add("System");
        result.add("Thread");
        result.add("ThreadGroup");
        result.add("ThreadLocal");
        result.add("Throwable");
        result.add("Void");

        result.add("ArithmeticException");
        result.add("ArrayIndexOutOfBoundsException");
        result.add("ArrayStoreException");
        result.add("ClassCastException");
        result.add("ClassNotFoundException");
        result.add("CloneNotSupportedException");
        result.add("Exception");
        result.add("IllegalAccessException");
        result.add("IllegalArgumentException");
        result.add("IlegalMonitorStateException");
        result.add("IllegalStateException");
        result.add("IllegalThreadStateException");
        result.add("IndexOutOfBoundsException");
        result.add("InstantiationException");
        result.add("InterruptedException");
        result.add("NegativeArraySizeException");
        result.add("NoSuchFieldException");
        result.add("NoSuchMethodException");
        result.add("NullPointerException");
        result.add("NumberFormatException");
        result.add("RuntimeException");
        result.add("SecurityException");
        result.add("StringIndexOutOfBoundsException");
        result.add("UnsupportedOperationException");

        result.add("AbstractMethodError");
        result.add("ClassCircularityError");
        result.add("ClassFormatError");
        result.add("Error");
        result.add("ExceptionInInitializerError");
        result.add("IllegalAccessError");
        result.add("IncompatibleClassChangeError");
        result.add("InstantiationError");
        result.add("InternalError");
        result.add("LinkageError");
        result.add("NoClassDefFoundError");
        result.add("NoSuchFieldError");
        result.add("NoSuchMethodError");
        result.add("OutOfMemoryError");
        result.add("StackOverflowError");
        result.add("ThreadDeath");
        result.add("UnknownError");
        result.add("UnsatisfiedLinkError");
        result.add("UnsupportedClassVersionError");
        result.add("VerifyError");
        result.add("VirtualMachineError");

        // Methods and constants in java.lang.Object (as of 1.3)
        result.add("clone");
        result.add("equals");
        result.add("finalize");
        result.add("getClass");
        result.add("hashCode");
        result.add("notify");
        result.add("notifyAll");
        result.add("toString");
        result.add("wait");

        // Others
        result.add("java");  // to avoid conflicts with standard package
                             // names like "java.lang"
        result.add("run");   // from java.lang.Runnable

        Assert.ensure(result != null);
        return result;
    }
}
