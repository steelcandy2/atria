/*
 Copyright (C) 2009-2014 by James MacKay.

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

/**
    The interface implemented by classes that generate target code that is
    C++ source code, including CodeGenerators and Subgenerators.

    @author  James MacKay
    @version $Revision: 1.12 $
*/
public interface CommonCppGeneratorBase
{
    // Constants

    /** The arguments for a call of a zero-argument C++ function. */
    public static final String EMPTY_ARGUMENTS = "()";

    /**
        The C++ quotation mark character that - among other things - starts
        and ends string literals.
    */
    /** A single double quotation mark. */
    public static final char CPP_QUOTE_CHAR = '"';

    /** The string representation of a CPP_QUOTE_CHAR. */
    public static final String CPP_QUOTE = String.valueOf(CPP_QUOTE_CHAR);

    /**
        The character that separates pathname components in C++ (for example,
        in #include directives).
    */
    public static final char CPP_FILE_SEPARATOR_CHAR = '/';

    /**
        The string that separates pathname components in C++ (for example,
        in #include directives).
    */
    public static final String CPP_FILE_SEPARATOR =
        String.valueOf(CPP_FILE_SEPARATOR_CHAR);

// TODO: make this configurable ???!!!!???
// - this could be platform-specific (or at least could be subject to
//   platform-specific restrictions)
    /** The filename extension used for C++ header files. */
    public static final String
        CPP_HEADER_FILE_EXT = ".h";

// TODO: make this configurable ???!!!!???
// - this could be platform-specific (or at least could be subject to
//   platform-specific restrictions)
    /** The filename extension used for C++ source files. */
    public static final String
        CPP_SOURCE_FILE_EXT = ".cpp";

// TODO: make this configurable ???!!!!???
// - this could be platform-specific (or at least could be subject to
//   platform-specific restrictions)
    /** The filename extension used for C++ object files. */
    public static final String
        CPP_OBJECT_FILE_EXT = ".o";

// TODO: make this configurable ???!!!!???
// - this could be platform-specific (or at least could be subject to
//   platform-specific restrictions)
    /** The filename extension used for C++ shared libraries. */
    public static final String
        CPP_SHARED_LIB_EXT = ".so";


    /** The separator between C++ template arguments or parameters. */
    public static final String
        TEMPLATE_ARGUMENTS_SEPARATOR = ", ";

    /** Operators. */
    public static final String
        CPP_UNARY_MINUS_OPERATOR = "-",
        CPP_UNARY_NOT_OPERATOR = "!",
        CPP_BINARY_AND_OPERATOR = "&&",
        CPP_BINARY_OR_OPERATOR = "||",
        CPP_BINARY_PLUS_OPERATOR = "+",
        CPP_BINARY_MINUS_OPERATOR = "-",
        CPP_BINARY_TIMES_OPERATOR = "*",
        CPP_BINARY_DIVIDE_OPERATOR = "/";

    /** The C++ scope resolution (pseudo-)operator. */
    public static final String
        CPP_SCOPE_OPERATOR = "::";

    /** Preprocessor directives. */
    public static final String
        CPP_INCLUDE_DIRECTIVE = "#include",
        CPP_DEFINE_DIRECTIVE = "#define",
        CPP_UNDEFINE_DIRECTIVE = "#undef",
        CPP_IF_DIRECTIVE = "#if",
        CPP_ELSE_DIRECTIVE = "#else",
        CPP_IF_NOT_DEFINED_DIRECTIVE = "#ifndef",
        CPP_ENDIF_DIRECTIVE = "#endif";

    /** Keywords/reserved words. */
    public static final String
        CPP_BREAK_KEYWORD = "break",
        CPP_CASE_KEYWORD = "case",
        CPP_CATCH_KEYWORD = "catch",
        CPP_CLASS_KEYWORD = "class",
        CPP_CONSTANT_KEYWORD = "const",
        CPP_ELSE_KEYWORD = "else",
        CPP_ENUM_KEYWORD = "enum",
        CPP_IF_KEYWORD = "if",
        CPP_INLINE_KEYWORD = "inline",
        CPP_NAMESPACE_KEYWORD = "namespace",
        CPP_PRIVATE_KEYWORD = "private",
        CPP_PROTECTED_KEYWORD = "protected",
        CPP_PUBLIC_KEYWORD = "public",
        CPP_RETURN_KEYWORD = "return",
        CPP_STATIC_KEYWORD = "static",
        CPP_SWITCH_KEYWORD = "switch",
        CPP_TEMPLATE_KEYWORD = "template",
        CPP_THROW_KEYWORD = "throw",
        CPP_TYPEDEF_KEYWORD = "typedef",
        CPP_WHILE_KEYWORD = "while",
        CPP_ELSE_IF_KEYWORDS = CPP_ELSE_KEYWORD + " " + CPP_IF_KEYWORD;

    /** The starts of common C++ phrases. */
    public static final String
        CPP_CATCH_START = CPP_CATCH_KEYWORD + " ",
        CPP_CONSTANT_START = CPP_CONSTANT_KEYWORD + " ",
        CPP_ELSE_IF_START = CPP_ELSE_IF_KEYWORDS + " ",
        CPP_IF_START = CPP_IF_KEYWORD + " ",
        CPP_INLINE_START = CPP_INLINE_KEYWORD + " ",
        CPP_RETURN_START = CPP_RETURN_KEYWORD + " ",
        CPP_STATIC_START = CPP_STATIC_KEYWORD + " ",
        CPP_THROW_START = CPP_THROW_KEYWORD + " ",
        CPP_TYPEDEF_START = CPP_TYPEDEF_KEYWORD + " ",
        CPP_STATIC_CONSTANT_START = CPP_STATIC_START + CPP_CONSTANT_START;

    /** The names of the C++ boolean constants. */
    public static final String
        CPP_TRUE_CONSTANT_NAME = "true",
        CPP_FALSE_CONSTANT_NAME = "false";

    /** Miscellaneous symbols. */
    public static final String
        CPP_POINTER_SUFFIX = " *",
        CPP_BLOCK_COMMENT_START = "/*",
        CPP_BLOCK_COMMENT_END = "*/",
        CPP_LINE_COMMENT_START = "//";


    /**
        Standard C++ namespace, class, etc. names.

        See p. 384-385 of TCPL Special Edition for a list of some standard
        exception class names.
    */
    public static final String
        CPP_VOID_TYPE_NAME = "void",
        CPP_STANDARD_NAMESPACE_NAME = "std",
        CPP_STANDARD_NAMESPACE_PREFIX =
            CPP_STANDARD_NAMESPACE_NAME + CPP_SCOPE_OPERATOR,
        CPP_BAD_ALLOC_EXCEPTION_TYPE_NAME =
            CPP_STANDARD_NAMESPACE_PREFIX + "bad_alloc",
        CPP_OUT_OF_RANGE_EXCEPTION_TYPE_NAME =
            CPP_STANDARD_NAMESPACE_PREFIX + "out_of_range",
        CPP_INVALID_ARGUMENT_EXCEPTION_TYPE_NAME =
            CPP_STANDARD_NAMESPACE_PREFIX + "invalid_argument",
        CPP_ASSERT_MACRO_NAME = "assert";
}
