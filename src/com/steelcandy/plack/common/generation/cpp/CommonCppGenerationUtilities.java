/*
 Copyright (C) 2009-2012 by James MacKay.

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

package com.steelcandy.plack.common.generation.cpp;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.generation.CommonCodeGenerationUtilities;

import com.steelcandy.common.io.*;

/**
    A class that contains various utility methods useful in generating
    C++ source code as target code.
    <p>
    Note: in a language with multiple inheritance this class would be used
    as a base class. However, in Java a language-specific singleton subclass
    is usually defined and then accessed from the various classes that
    generate Java source code, since such classes usually already have a
    superclass.

    @author  James MacKay
    @version $Revision: 1.15 $
*/
public class CommonCppGenerationUtilities
    extends CommonCodeGenerationUtilities
    implements CommonCppGeneratorBase
{
    // Constants

    /**
        The valid prefixes for C++ string literals: the default one is for
        literals made of of chars, and the wide one for ones made up of
        wchars.
    */
    public static final String
        CPP_DEFAULT_STRING_LITERAL_PREFIX = "",
        CPP_WIDE_STRING_LITERAL_PREFIX = "L",
        CPP_QUOTE_CHARACTER_STRING = "\"";

    /**
        The text at the start of sections of a C++ class definition.
    */
    private static final String
        CLASS_SECTION_SUFFIX = ":",
        PRIVATE_CLASS_SECTION_START =
            CPP_PRIVATE_KEYWORD + CLASS_SECTION_SUFFIX,
        PROTECTED_CLASS_SECTION_START =
            CPP_PROTECTED_KEYWORD + CLASS_SECTION_SUFFIX,
        PUBLIC_CLASS_SECTION_START =
            CPP_PUBLIC_KEYWORD + CLASS_SECTION_SUFFIX;


    // Public static methods

    /**
        Writes out the start of a block of C++ code that contains code that
        can't be reached by a thread of execution using normal means: it can
        usually only be reached by switching or jumping to a label inside the
        block.

        @param w the writer to use
        @see #writeNoNormalEntryBlockEnd(IndentWriter)
    */
    public static void writeNoNormalEntryBlockStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeIfClauseStart(w);
        write(w, CPP_FALSE_CONSTANT_NAME);
        writeIfClauseMiddle(w);
    }

    /**
        Writes out the end of a block of C++ code that contains code that
        can't be reached by a thread of execution using normal means: it can
        usually only be reached by switching or jumping to a label inside the
        block.

        @param w the writer to use
        @see #writeNoNormalEntryBlockStart(IndentWriter w)
    */
    public static void writeNoNormalEntryBlockEnd(IndentWriter w)
    {
        Assert.require(w != null);

        writeIfClauseEnd(w);
    }


    /**
        Writes out the start of a C++ "if" clause: everything from the
        "if" keyword to the opening parenthesis just before the conditional
        expression.

        @param w the writer to use
    */
    public static void writeIfClauseStart(IndentWriter w)
    {
        Assert.require(w != null);

        write(w, CPP_IF_START);
        write(w, "(");
    }

    /**
        Writes out the middle of a C++ "if" clause: everything from the
        closing parenthesis just after the conditional expression to the
        opening curly brace (and newline) just before the clause's
        statements.

        @param w the writer to use
    */
    public static void writeIfClauseMiddle(IndentWriter w)
    {
        Assert.require(w != null);

        writeLine(w, ")");
        writeBlockStart(w);
    }

    /**
        Writes out the middle of a C++ "if" clause: everything after the
        clause's statements.

        @param w the writer to use
    */
    public static void writeIfClauseEnd(IndentWriter w)
    {
        Assert.require(w != null);

        writeBlockEnd(w);
    }

    /**
        Writes out the start of a C++ "else if" clause: everything from the
        "else" keyword to the opening parenthesis just before the conditional
        expression.

        @param w the writer to use
    */
    public static void writeElseIfClauseStart(IndentWriter w)
    {
        Assert.require(w != null);

        write(w, CPP_ELSE_IF_START);
        write(w, "(");
    }

    /**
        Writes out the middle of a C++ "else if" clause: everything from the
        closing parenthesis just after the conditional expression to the
        opening curly brace (and newline) just before the clause's
        statements.

        @param w the writer to use
    */
    public static void writeElseIfClauseMiddle(IndentWriter w)
    {
        Assert.require(w != null);

        writeIfClauseMiddle(w);
    }

    /**
        Writes out the end of a C++ "else if" clause: everything after the
        clause's statements.

        @param w the writer to use
    */
    public static void writeElseIfClauseEnd(IndentWriter w)
    {
        Assert.require(w != null);

        writeIfClauseEnd(w);
    }

    /**
        Writes out the start of a C++ "else" clause: everything from the
        "else" keyword to the opening curly brace (and newline) just before
        the clause's statements.

        @param w the writer to use
    */
    public static void writeElseClauseStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeLine(w, CPP_ELSE_KEYWORD);
        writeBlockStart(w);
    }

    /**
        Writes out the end of a C++ "else" clause: everything after the
        clause's statements.

        @param w the writer to use
    */
    public static void writeElseClauseEnd(IndentWriter w)
    {
        Assert.require(w != null);

        writeIfClauseEnd(w);
    }


    /**
        Writes out the C++ boolean constant corresponding to 'val'.

        @param w the writer to use
        @param val a boolean value
    */
    public static void writeBooleanConstant(IndentWriter w, boolean val)
    {
        Assert.require(w != null);

        write(w, val ? CPP_TRUE_CONSTANT_NAME : CPP_FALSE_CONSTANT_NAME);
    }


    /**
        Writes the start of a C++ comment block, including increasing the
        indent level by one.

        @param w the writer to use
        @see #writeCommentBlockEnd(IndentWriter)
    */
    public static void writeCommentBlockStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeLine(w, CPP_BLOCK_COMMENT_START);
        indentMore(w);
    }

    /**
        Writes the start of a C++ comment block, including increasing the
        indent level by one.
        <p>
        Note: this method exists to facilitate the writing out of
        javadoc-like comment blocks.

        @param w the writer to use
        @param suffix the suffix to write immediately after the characters
        that start the comment block ("/*"): there will be no spaces between
        it and the suffix except any at the start of 'suffix'
        @see #writeCommentBlockEnd(IndentWriter)
    */
    public static void writeCommentBlockStart(IndentWriter w, String suffix)
    {
        Assert.require(w != null);

        write(w, CPP_BLOCK_COMMENT_START);
        writeLine(w, suffix);
        indentMore(w);
    }

    /**
        Writes the end of a C++ comment block, including decreasing the
        indent level by one.

        @param w the writer to use
        @see #writeCommentBlockStart(IndentWriter)
        @see #writeCommentBlockStart(IndentWriter, String)
    */
    public static void writeCommentBlockEnd(IndentWriter w)
    {
        Assert.require(w != null);

        indentLess(w);
        writeLine(w, CPP_BLOCK_COMMENT_END);
    }


    /**
        Writes the start of a C++ block, including increasing the indent
        level by one.

        @param w the writer to use
        @see #writeBlockEnd(IndentWriter)
        @see #writeBlockEnd(IndentWriter, String)
    */
    public static void writeBlockStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeLine(w, "{");
        indentMore(w);
    }

    /**
        Writes the end of a C++ block, including decreasing the indent level
        by one.

        @param w the writer to use
        @see #writeBlockStart(IndentWriter)
        @see #writeBlockEnd(IndentWriter, String)
    */
    public static void writeBlockEnd(IndentWriter w)
    {
        Assert.require(w != null);

        indentLess(w);
        writeLine(w, "}");
    }

    /**
        Writes the end of a C++ block, including decreasing the indent level
        by one. The block-ending brace is followed by 'suffix' and a new line
        isn't automatically added after it.

        @param w the writer to use
        @param suffix the text that's to follow the block's closing brace
        @see #writeBlockStart(IndentWriter)
        @see #writeBlockEnd(IndentWriter)
    */
    public static void writeBlockEnd(IndentWriter w, String suffix)
    {
        Assert.require(w != null);
        Assert.require(suffix != null);

        indentLess(w);
        write(w, "}");
        writeLine(w, suffix);
    }


    /**
        Writes the start of a C++ namespace named 'name', including
        increasing the indent level by one, or of an unnamed namespace if
        'name' is null.

        @param w the writer to use
        @param name the namespace's name, or null if the start of an unnamed
        namespace is to be written
        @see #writeNamespaceEnd(IndentWriter, String)
    */
    public static void writeNamespaceStart(IndentWriter w, String name)
    {
        Assert.require(w != null);
        // 'name' can be null

        if (name != null)
        {
            write(w, CPP_NAMESPACE_KEYWORD);
            write(w, " ");
            writeLine(w, name);
        }
        else // name == null
        {
            writeLine(w, CPP_NAMESPACE_KEYWORD);
        }
        writeBlockStart(w);
    }

    /**
        Writes the end of a C++ namespace named 'name', including
        decreasing the indent level by one, or of an unnamed namespace if
        'name' is null.

        @param w the writer to use
        @param name the namespace's name, or null if the start of an unnamed
        namespace is to be written
        @see #writeNamespaceStart(IndentWriter, String)
    */
    public static void writeNamespaceEnd(IndentWriter w, String name)
    {
        Assert.require(w != null);
        // 'name' can be null

        String suffix = "  // " + CPP_NAMESPACE_KEYWORD;
        if (name != null)
        {
            suffix += (" " + name);
        }

        writeBlockEnd(w, suffix);
    }


    /**
        Writes out the start of the body of the C++ class named 'className',
        including increasing the indent level by one.

        @param w the writer to use
        @param className the C++ class' name
    */
    public static void writeClassBodyStart(IndentWriter w, String className)
    {
        Assert.require(w != null);
        Assert.require(className != null);

        writeBlockStart(w);
    }

    /**
        Writes out the end of the body of the C++ class named 'className',
        including decreasing the indent level by one.

        @param w the writer to use
        @param className the C++ class' name
    */
    public static void writeClassBodyEnd(IndentWriter w, String className)
    {
        Assert.require(w != null);
        Assert.require(className != null);

        writeBlockEnd(w, ";  // " + CPP_CLASS_KEYWORD + " " + className);
    }

    /**
        Writes out the start of a private section in a C++ class
        definition, including increasing the indent level by one.

        @param w the writer to use
        @see #writeClassSectionEnd(IndentWriter)
    */
    public static void writePrivateClassSectionStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeClassSectionStart(w, PRIVATE_CLASS_SECTION_START);
    }

    /**
        Writes out the start of a protected section in a C++ class
        definition, including increasing the indent level by one.

        @param w the writer to use
        @see #writeClassSectionEnd(IndentWriter)
    */
    public static void writeProtectedClassSectionStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeClassSectionStart(w, PROTECTED_CLASS_SECTION_START);
    }

    /**
        Writes out the start of a public section in a C++ class definition,
        including increasing the indent level by one.

        @param w the writer to use
        @see #writeClassSectionEnd(IndentWriter)
    */
    public static void writePublicClassSectionStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeClassSectionStart(w, PUBLIC_CLASS_SECTION_START);
    }

    /**
        Writes out the end of a section in a C++ class definition,
        including decreasing the indent level by one.

        @param w the writer to use
        @see #writePrivateClassSectionStart(IndentWriter)
        @see #writeProtectedClassSectionStart(IndentWriter)
        @see #writePublicClassSectionStart(IndentWriter)
    */
    public static void writeClassSectionEnd(IndentWriter w)
    {
        Assert.require(w != null);

        // empty
    }


    /**
        Writes out the start of an anonymous (that is, unnamed) C++
        enumeration, including increasing the indent level by one.

        @param w the writer to use
    */
    public static void writeAnonymousEnumerationStart(IndentWriter w)
    {
        Assert.require(w != null);

        writeLine(w, CPP_ENUM_KEYWORD);
        writeBlockStart(w);
    }

    /**
        Writes out the end of a C++ enumeration, including decreasing the
        indent level by one.

        @param w the writer to use
    */
    public static void writeEnumerationEnd(IndentWriter w)
    {
        Assert.require(w != null);

        writeBlockEnd(w, ";");
    }


    /**
        Writes out the part of a C++ method or function declaration that
        indicates that it doesn't throw any exceptions.

        @param w the writer to use
        @see #writeThrowNothingClause(IndentWriter)
    */
    public static void writeDeclarationThrowNothingClause(IndentWriter w)
    {
        Assert.require(w != null);

        writeThrowClause(w, "", ";");
    }

    /**
        Writes out the part of a C++ method or function signature that
        indicates that it doesn't throw any exceptions.

        @param w the writer to use
        @see #writeDeclarationThrowNothingClause(IndentWriter)
    */
    public static void writeThrowNothingClause(IndentWriter w)
    {
        Assert.require(w != null);

        writeThrowClause(w, "", "");
    }

    /**
        Writes out the part of a C++ method or function declaration that
        indicates that it can only throw an exception of the specified type.

        @param w the writer to use
        @param typeName the name of the exception's type
        @see #writeThrowClause(IndentWriter, String)
    */
    public static void
        writeDeclarationThrowClause(IndentWriter w, String typeName)
    {
        Assert.require(w != null);
        Assert.require(typeName != null);

        writeThrowClause(w, typeName, ";");
    }

    /**
        Writes out the part of a C++ method or function signature that
        indicates that it can only throw an exception of the specified type.

        @param w the writer to use
        @param typeName the name of the exception's type
        @see #writeDeclarationThrowClause(IndentWriter, String)
    */
    public static void writeThrowClause(IndentWriter w, String typeName)
    {
        Assert.require(w != null);
        Assert.require(typeName != null);

        writeThrowClause(w, typeName, "");
    }

    /**
        Writes out the part of a C++ method or function declaration that
        indicates that it can only throw a std::bad_alloc exception.

        @param w the writer to use
        @see #writeThrowBadAllocClause(IndentWriter)
    */
    public static void writeDeclarationThrowBadAllocClause(IndentWriter w)
    {
        Assert.require(w != null);

        writeThrowClause(w, CPP_BAD_ALLOC_EXCEPTION_TYPE_NAME, ";");
    }

    /**
        Writes out the part of a C++ method or function signature that
        indicates that it can only throw a std::bad_alloc exception.

        @param w the writer to use
        @see #writeDeclarationThrowBadAllocClause(IndentWriter)
    */
    public static void writeThrowBadAllocClause(IndentWriter w)
    {
        Assert.require(w != null);

        writeThrowClause(w, CPP_BAD_ALLOC_EXCEPTION_TYPE_NAME, "");
    }


    /**
        Writes out a C++ case label whose value is the int 'num'.

        @param w the writer to use
        @param num the value in the case label
    */
    public static void writeCaseLabel(IndentWriter w, int num)
    {
        Assert.require(w != null);

        writeRawCaseLabel(w, String.valueOf(num));
    }

    /**
        Writes out a C++ case label whose value is the character 'ch'.

        @param w the writer to use
        @param ch the character value in the case label
    */
    public static void writeCaseLabel(IndentWriter w, char ch)
    {
        Assert.require(w != null);

        writeRawCaseLabel(w,
            characterLiteral(ch, CPP_WIDE_STRING_LITERAL_PREFIX));
    }


    /**
        Writes out the C++ string literal that represents the string 'str',
        where the characters in the C++ string literal will all be wchars.

        @param w the writer to use
        @param str the C++ string's contents: it may contain characters that
        need to be escaped
        @see #wideStringLiteral(String)
        @see #writeStringLiteral(IndentWriter, String, String)
        @see #CPP_WIDE_STRING_LITERAL_PREFIX
    */
    public static void writeWideStringLiteral(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        writeStringLiteral(w, str, CPP_WIDE_STRING_LITERAL_PREFIX);
    }

    /**
        @param str the C++ string's contents: it may contain characters that
        need to be escaped
        @return the C++ wide string literal that represents a string
        consisting of the characters in 'str'
        @see #writeWideStringLiteral(IndentWriter, String)
    */
    public static String wideStringLiteral(String str)
    {
        Assert.require(str != null);

        final String quote = CPP_QUOTE_CHARACTER_STRING;
        final String prefix = CPP_WIDE_STRING_LITERAL_PREFIX;
        int sz = str.length();
        StringBuffer buf =
            new StringBuffer(prefix.length() + sz + quote.length() * 2);
        buf.append(prefix).append(quote);
        for (int i = 0; i < sz; i++)
        {
            buf.append(oneCharacterInLiteral(str.charAt(i)));
        }
        buf.append(quote);

        // Assert.ensure(result != null);
        return buf.toString();
    }

    /**
        Writes out the C++ string literal that represents the string 'str'.

        @param w the writer to use
        @param str the C++ string's contents: it may contain characters that
        need to be escaped
        @param literalPrefix the prefix to prepend to the string literal to
        indicate the type of characters in it: it should be the value of one
        of our CPP_*_STRING_LITERAL_PREFIX constants
        @see #writeWideStringLiteral(IndentWriter, String)
    */
    public static void writeStringLiteral(IndentWriter w, String str,
                                          String literalPrefix)
    {
        Assert.require(w != null);
        Assert.require(str != null);
        Assert.require(literalPrefix != null);

        int sz = str.length();
        write(w, literalPrefix);
        write(w, CPP_QUOTE_CHARACTER_STRING);
        for (int i = 0; i < sz; i++)
        {
            write(w, oneCharacterInLiteral(str.charAt(i)));
        }
        write(w, CPP_QUOTE_CHARACTER_STRING);
    }

    /**
        Writes out the C++ character literal that represents the character
        'ch', where the character in the C++ literal will all be a wchar.

        @param w the writer to use
        @param ch the C++ character's value: it may need to be escaped
        @see #writeCharacterLiteral(IndentWriter, char, String)
        @see #CPP_WIDE_STRING_LITERAL_PREFIX
    */
    public static void writeWideCharacterLiteral(IndentWriter w, char ch)
    {
        Assert.require(w != null);

        writeCharacterLiteral(w, ch, CPP_WIDE_STRING_LITERAL_PREFIX);
    }

    /**
        Writes out the C++ character literal that represents the character
        'ch'.

        @param w the writer to use
        @param ch the C++ character's value: it may need to be escaped
        @param literalPrefix the prefix to prepend to the character literal
        to indicate the type of character in it: it should be the value of
        one of our CPP_*_STRING_LITERAL_PREFIX constants
        @see #writeWideCharacterLiteral(IndentWriter, char)
    */
    public static void writeCharacterLiteral(IndentWriter w, char ch,
                                             String literalPrefix)
    {
        Assert.require(w != null);
        Assert.require(literalPrefix != null);

        write(w, characterLiteral(ch, literalPrefix));
    }


    // Protected static methods

    /**
        @param ch a character
        @param literalPrefix the prefix to prepend to the character literal
        to indicate the type of character in it: it should be the value of
        one of our CPP_*_STRING_LITERAL_PREFIX constants
        @return a C++ character literal with prefix 'literalPrefix' that
        represents 'ch'
    */
    protected static String characterLiteral(char ch, String literalPrefix)
    {
        Assert.require(literalPrefix != null);

        // Assert.ensure(result != null);
        return literalPrefix + "\'" + oneCharacterInLiteral(ch) + "\'";
    }

    /**
        Writes out a C++ case label whose value is represented in C++ by
        the contents of 'caseValue'.

        @param w the writer to use
        @param caseValue the raw case value: it should consist of a valid
        C++ expression (that can be used as a C++ case value)
    */
    protected static void
        writeRawCaseLabel(IndentWriter w, String caseValue)
    {
        Assert.require(w != null);
        Assert.require(caseValue != null);

        writeLineIndentedLess(w, CPP_CASE_KEYWORD + " " + caseValue + ":");
    }

    /**
        @param ch a character that's supposed to be a character in a C++
        character or string literal
        @return the C++ version of 'ch' (which may or may not need to be
        escaped)
    */
    protected static String oneCharacterInLiteral(char ch)
    {
        String result;

        switch (ch)
        {
        case '\\':
            result = "\\\\";
            break;
        case '"':
            result = "\\\"";
            break;
        case '\'':
            result = "\\\'";
            break;
        default:
            result = Character.toString(ch);
        }

        Assert.ensure(result != null);
        return result;
    }


    // Private static methods

    /**
        Writes out an indented C++ throw clause.
        <p>
        Note: this only works for C++ throw clauses that throw zero or one
        types of exceptions.

        @param w the writer to use
        @param exTypeName the name of the type of exception that the throw
        clause is to declare it throws, or an empty string if the clause is
        to declare that it throws nothing
        @param suffix the code that is to follow the throw clause (on the
        same line)
        @see #writeThrowNothingClause(IndentWriter)
        @see #writeDeclarationThrowNothingClause(IndentWriter)
    */
    private static void
        writeThrowClause(IndentWriter w, String exTypeName, String suffix)
    {
        Assert.require(w != null);
        Assert.require(exTypeName != null);  // though it may be empty
        Assert.require(suffix != null);

        indentMore(w);
        try
        {
            write(w, CPP_THROW_KEYWORD);
            write(w, " (");
            write(w, exTypeName);
            write(w, ")");
            writeLine(w, suffix);
        }
        finally
        {
            indentLess(w);
        }
    }

    /**
        Writes out the start of a section in a C++ class definition that
        starts with 'start', including increasing the indent level by one.

        @param w the writer to use
        @param start the text that the section is to start with: it should
        be one of the *_CLASS_SECTION_START constants
        @see #PRIVATE_CLASS_SECTION_START
        @see #PROTECTED_CLASS_SECTION_START
        @see #PUBLIC_CLASS_SECTION_START
    */
    private static void writeClassSectionStart(IndentWriter w, String start)
    {
        Assert.require(w != null);
        Assert.require(start != null);

        writeLineIndentedLess(w, start);
    }
}
