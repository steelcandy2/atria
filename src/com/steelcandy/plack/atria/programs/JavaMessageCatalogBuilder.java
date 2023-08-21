/*
 Copyright (C) 2014-2015 by James MacKay.

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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;
import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.semantic.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.creation.ReflectionUtilities;
import com.steelcandy.common.text.*;

import java.io.*;
import java.util.*;

/**
    Builds Java message catalog classes from specially formatted Atria
    documents.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class JavaMessageCatalogBuilder
    extends AtriaAbstractMessageCatalogBuilder
{
    // Constants

    /** The file extension for the Java source files we generate. */
    private static final String
        JAVA_SOURCE_EXTENSION = "java";

    /**
        The name of the class that all generated message catalog classes
        extend.
    */
    private static final String
        BASE_CLASS_NAME = ReflectionUtilities.
                            getUnqualifiedClassName(MessageCatalog.class);

    /**
        The name of the package in which is defined the class named
        BASE_CLASS_NAME.
    */
    private static final String
        BASE_CLASS_PACKAGE_NAME = ReflectionUtilities.
                                    getPackageName(MessageCatalog.class);

    /**
        The names of all of the packages and/or classes that a generated
        message catalog class needs to import.
    */
    private static final String[] CATALOG_IMPORTS = {
        BASE_CLASS_PACKAGE_NAME + "." + BASE_CLASS_NAME,
        "java.util.Locale",
    };

    /**
        The name of the Java constant that references the single instance of
        the message catalog class being generated.
    */
    private static final String
        INSTANCE_CONSTANT_NAME = "messages";


    /**
        The name of the Writer argument to message-building methods that
        write out their message.
    */
    private static final String
        WRITER_ARGUMENT_NAME = "w";

    /** The name of the Locale argument. */
    private static final String
        LOCALE_ARGUMENT_NAME = "loc";

    /** The separator between routine arguments. */
    private static final String
        ARG_SEP = ", ";

    /**
        The prefix - before the argument number - of a message argument in
        generated Java source code.
    */
    private static final String
        MESSAGE_ARGUMENT_NAME_PREFIX = "arg";

// TODO: is there a different prefix to use for 32-bit Unicode chars ???!!!???
    /**
        The Unicode character escape prefix for use in Java String
        constants. (The prefix must be followed by the hexadecimal
        representation of the character's (16-bit?) codepoint.
    */
    private static final String
        UNICODE_ESCAPE_PREFIX = "\\u";

    /**
        The name of the method in the generated Java code that maps an
        Atria variable name to its value.
        <p>
        The method is assumed to be defined in the base message catalog
        class.
    */
    private static final String
        ADD_VARIABLE_METHOD_NAME = "addVariable";

    /**
        The name of the method in the generated Java code that returns the
        values of Atria variables given their names.
        <p>
        The method is assumed to be defined in the base message catalog
        class.
    */
    private static final String
        VARIABLE_VALUE_METHOD_NAME = "variableValue";


    /** Java standard library class names. */
    private static final String
        LOCALE_CLASS_NAME = "Locale",
        STRING_CLASS_NAME = "String";

    /** Java character/String constants. */
    private static final String
        JAVA_QUOTE_VAR = "QUOTE",
        JAVA_NEWLINE_VAR = "NEWLINE";

    /** The Java doouble quotation mark character (as a String). */
    private static final String
        JAVA_LITERAL_QUOTE = "\"";


    // Constructors

    /**
        Constructs a JavaMessageCatalogBuilder.
    */
    public JavaMessageCatalogBuilder()
    {
        // empty
    }


    // Main method

    /**
        Main method.

        @param args the command-line arguments
    */
    public static void main(String[] args)
    {
        System.exit((new JavaMessageCatalogBuilder()).doMain(args));
    }


    // Protected methods

    /**
        Writes the start of the Java class file that contains the class
        that represents the message catalog.

        @param packageName the name of the Java package that the class
        families are to be a part of
        @param className the name of the message catalog class that is
        defined in the file
        @see #writeClassFileEnd(String, String)
    */
    protected void
        writeClassFileStart(String packageName, String className)
    {
        Assert.require(packageName != null);
        Assert.require(className != null);

        writeLine("// autogenerated");
        writeLine("");
        write("package ");
        write(packageName);
        writeLine(";");
        writeLine("");

        int sz = CATALOG_IMPORTS.length;
        if (sz > 0)
        {
            for (int i = 0; i < sz; i++)
            {
                write("import ");
                write(CATALOG_IMPORTS[i]);
                writeLine(";");
                writeLine("");
            }
        }

        writeLine("/**");
        indent();
        write("The \"");
        write(className);
        writeLine("\" message catalog.");
        writeLine("");
        writeLine("Note: this file was automatically generated, " +
                  "and so should not be");
        writeLine("edited directly.");
        writeLine("");
        writeLine("@author  autogenerated");
        writeLine("@version autogenerated");
        unindent();
        writeLine("*/");
        write("public class ");
        writeLine(className);
        indent();
        write("extends ");
        writeLine(BASE_CLASS_NAME);
        unindent();
        writeLine("{");
        indent();   // unindented again in writeClassFileEnd()

        writeLine("// Public constants");
        writeLine("");
        writeLine("/** The single instance of this message catalog. */");
        write("public static final ");
        writeLine(className);
        indent();
        write(INSTANCE_CONSTANT_NAME);
        write(" = new ");
        write(className);
        writeLine("();");
        unindent();
        writeLine("");
        writeLine("");

        writeLine("// Constructors");
        writeLine("");
        writeLine("/**");
        indent();
        writeLine("Constructs our single instance,");
        unindent();
        writeLine("*/");
        write("private ");
        write(className);
        writeLine("()");
        writeLine("{");
        indent();
        writeLine("addAllVariables();");
        unindent();
        writeLine("}");
        writeLine("");
        writeLine("");

        writeLine("// Message-building methods");
        writeLine("");
    }

    /**
        Writes the end of the Java class file that contains the class
        that represents the message catalog.

        @param packageName the name of the Java package that the class
        families are to be a part of
        @param className the name of the message catalog class that is
        defined in the file
        @see #writeClassFileStart(String, String)
    */
    protected void
        writeClassFileEnd(String packageName, String className)
    {
        Assert.require(packageName != null);
        Assert.require(className != null);

        unindent();
        writeLine("}");
    }

    /**
        Writes out the Java source code that stores the values of all of the
        variables set in the source Atria message catalog.

        @param doc the source Atria message catalog document
        @param handler the error handler to use to handle any and all errors
    */
    protected void writeAllVariableSettingMethods(AtriaConstructManager.
                                        Document doc, ErrorHandler handler)
    {
        Assert.require(doc != null);
        Assert.require(handler != null);

        writeLine("");
        writeLine("// Private methods");
        writeLine("");
        writeLine("/**");
        indent();
        writeLine("Adds all of the variables defined in the source Atria " +
                  "message catalog.");
        unindent();
        writeLine("*/");
        writeLine("private void addAllVariables()");
        writeLine("{");
        indent();
        StringIterator iter = variableNamesIterator();
        if (iter.hasNext() == false)
        {
            writeLine("// empty");
        }
        else
        {
            write("StringWriter ");
            write(WRITER_ARGUMENT_NAME);
            writeLine(";");
            writeLine("");
            while (iter.hasNext())
            {
                String varName = iter.next();
                AtriaConstructManager.Expression expr =
                                                findVariableValue(varName);
                write(WRITER_ARGUMENT_NAME);
                writeLine(" = new StringWriter();");
                expr.accept(this, handler);
                write(ADD_VARIABLE_METHOD_NAME);
                write("(");
                write(JAVA_LITERAL_QUOTE);
                write(varName);
                write(JAVA_LITERAL_QUOTE);
                write(", ");
                write(WRITER_ARGUMENT_NAME);
                writeLine(".toString());");
                if (iter.hasNext())
                {
                    writeLine("");
                }
            }
        }  // else
        unindent();
        writeLine("}");
    }

    /**
        Writes out the definitions of all of the message building methods
        for all of the messages defined in 'doc'.

        @param doc the source Atria message catalog document
        @param handler the error handler to use to handle any and all errors
    */
    protected void writeAllMessageBuildingMethods(AtriaConstructManager.
                                    Document doc, ErrorHandler handler)
    {
        Assert.require(doc != null);
        Assert.require(handler != null);

        if (doc.elementCount() > 0)
        {
            visitAll(doc.elementList(), handler);
        }
    }


    /**
        Writes the definition of the Java method that builds a String
        message corresponding to the specified Atria message element.

        @param c the Atria message element
        @param handler the error handler to use to handle any and all errors
    */
    protected void
        writeMessageStringBuildingMethodDefinition(AtriaConstructManager.
                                             Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int numArgs = numberOfMessageArguments(c);
        writeMessageStringBuildingMethodDocumentation(numArgs);

        String methodName = MANAGER.elementName(c);
        Assert.check(methodName != null);
        writeMessageStringBuildingMethodSignature(methodName, numArgs);
        writeLine("{");
        indent();
        try
        {
            writeMessageStringBuildingMethodBody(c, methodName, handler);
        }
        finally
        {
            unindent();
        }
        writeLine("}");
        writeLine("");
    }

    /**
        Writes the documentation comments that appear right above the
        definition of a Java method that builds String messages that take the
        specified number of arguments.

        @param numArgs the number of arguments taken by the method that we
        write out the documentation comments for
    */
    protected void writeMessageStringBuildingMethodDocumentation(int numArgs)
    {
        Assert.require(numArgs >= 0);

        writeLine("/**");
        indent();
        if (isCatalogLocalizable())
        {
            write("@param ");
            write(LOCALE_ARGUMENT_NAME);
            writeLine(" the locale to use in building the message");
        }

        for (int i = 0; i < numArgs; i++)
        {
            write("@param ");
            writeArgumentName(i);
            write(" argument #");
            writeLine(String.valueOf(i + 1));
        }

        writeLine("@return the message built from the specified " +
                  "arguments");
        unindent();
        writeLine("*/");
    }

    /**
        Writes the signature part of the definition of the Java String
        message-building method with the specified name and number of
        arguments.

        @param methodName the name of the method
        @param numArgs the number of arguments the method takes (not
        counting the 'Style' argument)
    */
    protected void
        writeMessageStringBuildingMethodSignature(String methodName,
                                                  int numArgs)
    {
        Assert.require(methodName != null);
        Assert.require(numArgs >= 0);

        boolean isLocalizable = isCatalogLocalizable();

        write("public ");
        write(STRING_CLASS_NAME);
        write(" ");
        write(methodName);
        write("(");
        if (isLocalizable)
        {
            write(LOCALE_CLASS_NAME);
            write(" ");
            write(LOCALE_ARGUMENT_NAME);
        }
        if (numArgs > 0)
        {
            if (isLocalizable)
            {
                writeLine(",");
                indent();
            }
            try
            {
                for (int i = 0; i < numArgs; i++)
                {
                    write(STRING_CLASS_NAME);
                    write(" ");
                    writeArgumentName(i);
                    if (i < numArgs - 1)
                    {
                        write(ARG_SEP);
                    }
                }
            }
            finally
            {
                if (isLocalizable)
                {
                    unindent();
                }
            }
        }
        writeLine(")");
    }

    /**
        Writes the body of the definition of the Java String message-building
        method corresponding to the specified element construct.
        <p>
        Note: this method assumes that the caller has indented our writer to
        the level to which the first line of the method body is to be
        indented.

        @param c the Atria message element
        @param methodName the name of the method whose body we're building
        @param handler the error handler to use to handle any and all errors
    */
    protected void
        writeMessageStringBuildingMethodBody(AtriaConstructManager.
                        Element c, String methodName, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(methodName != null);
        Assert.require(handler != null);

        writeLine("StringWriter w = new StringWriter();");
        write(methodName);
        write("(w, ");
        writeNakedMessageMethodCallArguments(c, handler);
        writeLine(");");
        writeLine("");
        writeLine("// Assert.ensure(result != null);");
        writeLine("return toStringMessage(w);");
    }


    /**
        Writes the definition of the Java method that writes to a Java Writer
        the message corresponding to the specified Atria message element.

        @param c the Atria message element
        @param handler the error handler to use to handle any and all errors
    */
    protected void writeWriteMessageMethodDefinition(AtriaConstructManager.
                                             Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int numArgs = numberOfMessageArguments(c);
        writeWriteMessageMethodDocumentation(numArgs);

        String methodName = MANAGER.elementName(c);
        Assert.check(methodName != null);
        writeWriteMessageMethodSignature(methodName, numArgs);
        writeLine("{");
        indent();
        try
        {
            writeWriteMessageMethodBody(c, handler);
        }
        finally
        {
            unindent();
        }
        writeLine("}");
        writeLine("");
    }

    /**
        Writes the documentation comments that appear right above the
        definition of a Java method that writes out a message.

        @param numArgs the number of arguments taken by the method that we
        write out the documentation comments for
    */
    protected void writeWriteMessageMethodDocumentation(int numArgs)
    {
        Assert.require(numArgs >= 0);

        writeLine("/**");
        indent();
        write("@param ");
        write(WRITER_ARGUMENT_NAME);
        writeLine(" the Writer to use to write out the message");

        if (isCatalogLocalizable())
        {
            write("@param ");
            write(LOCALE_ARGUMENT_NAME);
            writeLine(" the locate to use in building the message");
        }

        for (int i = 0; i < numArgs; i++)
        {
            write("@param ");
            writeArgumentName(i);
            write(" argument #");
            writeLine(String.valueOf(i + 1));
        }
        unindent();
        writeLine("*/");
    }

    /**
        Writes the signature part of the definition of the Java message
        writing method with the specified name and number of arguments.

        @param methodName the name of the method
        @param numArgs the number of arguments the method takes (not
        counting the Writer and Locale arguments)
    */
    protected void writeWriteMessageMethodSignature(String methodName,
                                                    int numArgs)
    {
        Assert.require(methodName != null);
        Assert.require(numArgs >= 0);

        boolean isLocalizable = isCatalogLocalizable();

        write("public void ");
        write(methodName);
        write("(Writer ");
        write(WRITER_ARGUMENT_NAME);
        write(ARG_SEP);

        if (isLocalizable)
        {
            write(LOCALE_CLASS_NAME);
            write(" ");
            write(LOCALE_ARGUMENT_NAME);
        }
        if (numArgs > 0)
        {
            if (isLocalizable)
            {
                writeLine(",");
                indent();
            }
            try
            {
                for (int i = 0; i < numArgs; i++)
                {
                    write(STRING_CLASS_NAME);
                    write(" ");
                    writeArgumentName(i);
                    if (i < numArgs - 1)
                    {
                        write(ARG_SEP);
                    }
                }
            }
            finally
            {
                if (isLocalizable)
                {
                    unindent();
                }
            }
        }
        writeLine(")");
    }

    /**
        Writes the body of the definition of the Java message writing method
        corresponding to the specified element construct.
        <p>
        Note: this method assumes that the caller has indented our writer to
        the level to which the first line of the method body is to be
        indented.

        @param c the Atria message element
        @param handler the error handler to use to handle any and all errors
    */
    protected void writeWriteMessageMethodBody(AtriaConstructManager.
                                            Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);
        ConstructList items = c.contentItemList();
        if (items.isEmpty())
        {
            writeLine("// empty");
        }
        else
        {
            visitAll(items, handler);
        }
    }


    /**
        Writes out the arguments for a call to a message String builder (or -
        except for the Writer argument a message writing) method
        <em>without</em> the parentheses that usually surround method
        arguments.

        @param c the element that represents the message that the method
        builds (or writes)
        @param handler the error handler to use to handle any errors
    */
    protected void
        writeNakedMessageMethodCallArguments(AtriaConstructManager.Element c,
                                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int numArgs = numberOfMessageArguments(c);
        if (isCatalogLocalizable())
        {
            write(LOCALE_ARGUMENT_NAME);
            if (numArgs > 0)
            {
                write(ARG_SEP);
            }
        }
        for (int i = 0; i < numArgs; i++)
        {
            if (i > 0)
            {
                write(ARG_SEP);
            }
            writeArgumentName(i);
        }
    }

    /**
        Writes out the Java source code that writes out a double quotation
        mark using the Writer argument.
    */
    protected void writeWriteQuote()
    {
        writeWriteCallStart();
        write(JAVA_QUOTE_VAR);
        writeWriteCallEnd();
    }

    /**
        Writes out the Java source code that writes out a newline using the
        Writer argument.
    */
    protected void writeWriteNewline()
    {
        writeWriteCallStart();
        write(JAVA_NEWLINE_VAR);
        writeWriteCallEnd();
    }

    /**
        Writes out the start of a call of a write() method on the Java
        Writer argument.
        <p>
        Note: the next this written after this method is called should be the
        value that the call is to write out, which should then be followed by
        a call to writeWriteCallEnd().

        @see #writeWriteCallEnd
    */
    protected void writeWriteCallStart()
    {
        write(WRITER_ARGUMENT_NAME);
        write(".write(");  // closing parenthesis in writeWriteCallEnd()
    }

    /**
        Writes out the end of a call of a write() method on the Java
        Writer argument.

        @see #writeWriteCallStart
    */
    protected void writeWriteCallEnd()
    {
        writeLine(");");  // opening parenthesis in writeWriteCallStart()
    }

    /**
        Writes out the name of the message builder (or message parts builder)
        method argument with index 'index'.

        @param index the 0-based index of the method argument
        @see #argumentName(int)
    */
    protected void writeArgumentName(int index)
    {
        Assert.require(index >= 0);

        write(argumentName(index));
    }

    /**
        @param index the 0-based index of a message argument
        @return the name of the message builder (or message parts builder)
        method argument with index 'index'
        @see #writeArgumentName(int)
    */
    protected String argumentName(int index)
    {
        Assert.require(index >= 0);

        String result = MESSAGE_ARGUMENT_NAME_PREFIX + String.valueOf(index);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeOutputFile(AtriaConstructManagerBase.Document, AtriaMessageCatalogData, ErrorHandler)
    */
    protected void writeOutputFile(AtriaConstructManager.Document c,
                          AtriaMessageCatalogData data, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        String moduleName = data.moduleName();
        String className = data.className();

        // Note: writeMessageBuildingMethods() causes 'c''s direct Element
        // subconstructs to be visited.
        writeClassFileStart(moduleName, className);
        writeAllMessageBuildingMethods(c, handler);
        writeAllVariableSettingMethods(c, handler);
        writeClassFileEnd(moduleName, className);
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeMessage(AtriaConstructManagerBase.Element, ErrorHandler)
    */
    protected void writeMessage(AtriaConstructManager.Element c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeMessageStringBuildingMethodDefinition(c, handler);
        writeWriteMessageMethodDefinition(c, handler);
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeMessageArgument(AtriaConstructManagerBase.Element, ErrorHandler)
    */
    protected void writeMessageArgument(AtriaConstructManager.
                                        Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = MANAGER.elementName(c);
        int index = messageArgumentIndex(c);
        boolean isQuoted = isQuotedArgumentElementName(name);

        if (isQuoted)
        {
            writeWriteQuote();
        }
        writeWriteCallStart();
        write(argumentName(index));
        writeWriteCallEnd();
        if (isQuoted)
        {
            writeWriteQuote();
        }
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeLiteralText(String, ErrorHandler)
    */
    protected void writeLiteralText(String txt, ErrorHandler handler)
    {
        Assert.require(txt != null);
        Assert.require(handler != null);

        int sz = txt.length();
        StringBuffer buf = new StringBuffer(sz * 2);
            // allow room for Unicode escapes

        final int firstNonAsciiCode =
            AtriaInfo.FIRST_NON_ASCII_UNICODE_CODEPOINT;
        for (int i = 0; i < sz; i++)
        {
            char ch = txt.charAt(i);
            int charCode = (int) ch;
            if (charCode < firstNonAsciiCode)
            {
                buf.append(ch);
            }
            else
            {
                buf.append(UNICODE_ESCAPE_PREFIX);
                buf.append(Integer.toHexString(charCode));
            }
        }

        writeWriteCallStart();
        write(JAVA_LITERAL_QUOTE);
        write(buf.toString());
        write(JAVA_LITERAL_QUOTE);
        writeWriteCallEnd();
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeGetCommand(String, ErrorHandler)
    */
    protected void writeGetCommand(String varName, ErrorHandler handler)
    {
        Assert.require(varName != null);
        Assert.require(varName.length() > 0);
        Assert.require(handler != null);

        writeWriteCallStart();
        write(VARIABLE_VALUE_METHOD_NAME);
        write("(");
        write(JAVA_LITERAL_QUOTE);
        write(varName);
        write(JAVA_LITERAL_QUOTE);
        write(")");
        writeWriteCallEnd();
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    protected void writeJoinCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // Write out each argument.
        visitAll(c.argumentList(), handler);
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeNewlineCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    protected void writeNewlineCommand(AtriaConstructManager.Command c,
                                       ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeWriteNewline();
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    protected void writeQuoteCommand(AtriaConstructManager.Command c,
                                     ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeWriteQuote();
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#writeQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    protected void writeQuotedCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeWriteQuote();
        visitAll(c.argumentList(), handler);  // the one Expression argument
        writeWriteQuote();
    }

    /**
        @see AtriaAbstractMessageCatalogBuilder#outputFileExtension
    */
    protected String outputFileExtension()
    {
        String result = JAVA_SOURCE_EXTENSION;

        // Assert,ensure(result != null);
        return result;
    }
}
