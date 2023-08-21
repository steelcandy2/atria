/*
 Copyright (C) 2005-2006 by James MacKay.

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

package com.steelcandy.plack.atria.base;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.text.TextUtilities;

import java.util.*;

/**
    A class that contains miscellaneous information about the Atria
    programming language that doesn't seem to belong anywhere else.

    @author James MacKay
*/
public class AtriaInfo
{
    // Name constants

    /**
        The name of the Atria language, as it would appear in a
        'language' statement.
    */
    public static final String
        LANGUAGE_NAME = AtriaConstants.LANGUAGE_NAME;

    /** The first name in a valid LanguageSpecifier. */
    public static final String
        FIRST_LANGUAGE_SPECIFIER_NAME =
            AtriaConstants.FIRST_LANGUAGE_SPECIFIER_NAME;


    /** The file extension of Atria source code files. */
    public static final String
        SOURCE_EXTENSION = AtriaConstants.SOURCE_EXTENSION;

    /** The file extension of Atria source code files, with leading '.'. */
    public static final String
        FULL_SOURCE_EXTENSION = "." + SOURCE_EXTENSION;

    /**
        The lowest codepoint of all Unicode characters that are not also
        7-bit ASCII characters.
    */
    public static final int FIRST_NON_ASCII_UNICODE_CODEPOINT =
        Integer.parseInt(AtriaConstants.FIRST_NON_ASCII_UNICODE_CODEPOINT);


    /** Command names. */
    public static final String
        GET_COMMAND_NAME = AtriaConstants.GET_COMMAND_NAME,
        JOIN_COMMAND_NAME = AtriaConstants.JOIN_COMMAND_NAME,
        NAMESPACE_COMMAND_NAME = AtriaConstants.NAMESPACE_COMMAND_NAME,
        NEWLINE_COMMAND_NAME = AtriaConstants.NEWLINE_COMMAND_NAME,
        QUOTE_COMMAND_NAME = AtriaConstants.QUOTE_COMMAND_NAME,
        QUOTED_COMMAND_NAME = AtriaConstants.QUOTED_COMMAND_NAME,
        SET_COMMAND_NAME = AtriaConstants.SET_COMMAND_NAME,
        TOP_COMMAND_NAME = AtriaConstants.TOP_COMMAND_NAME;

    /**
        The set of the names of all of the (predefined) commands. The values
        are all Strings.
    */
    public static final Set
        COMMAND_NAMES_SET = AtriaConstants.COMMAND_NAMES_SET;

    /**
        The list of the names of all of the (predefined) commands. The values
        are all Strings.
    */
    public static final List
        COMMAND_NAMES_LIST = AtriaConstants.COMMAND_NAMES_LIST;

    /**
        The set of the names of all of the commands that can only be
        direct subconstructs of PrologueItems. The values are all Strings.
    */
    public static final Set
        PROLOGUE_ITEM_ONLY_COMMAND_NAMES_SET =
            AtriaConstants.PROLOGUE_ITEM_ONLY_COMMAND_NAMES_SET;

    /**
        The list of the names of all of the commands that can only be
        direct subconstructs of PrologueItems. The values are all Strings.
    */
    public static final List
        PROLOGUE_ITEM_ONLY_COMMAND_NAMES_LIST =
            AtriaConstants.PROLOGUE_ITEM_ONLY_COMMAND_NAMES_LIST;

    /**
        The set of the names of all of the commands that cannot be direct
        subconstructs of PrologueItems. The values are all Strings.
    */
    public static final Set
        NON_PROLOGUE_ITEM_COMMAND_NAMES_SET =
            AtriaConstants.NON_PROLOGUE_ITEM_COMMAND_NAMES_SET;

    /**
        The list of the names of all of the commands that cannot be direct
        subconstructs of PrologueItems. The values are all Strings.
    */
    public static final List
        NON_PROLOGUE_ITEM_COMMAND_NAMES_LIST =
            AtriaConstants.NON_PROLOGUE_ITEM_COMMAND_NAMES_LIST;


    // Character constants (and their String versions)

    /** The character that starts an Atria comment. */
    public static final char
        COMMENT_START_CHAR = '\'';

    /** The character that starts an Atria comment. */
    public static final String
        COMMENT_START = String.valueOf(COMMENT_START_CHAR);


    /** The tab character. */
    public static final char
        TAB_CHAR = '\t';

    /** The tab character. */
    public static final String
        TAB = String.valueOf(TAB_CHAR);


    /** The quote character (used to start and end text literals). */
    public static final String
        QUOTE = AtriaCharacterClasses.QUOTATION_MARK;

    /** The quote character (used to start and end text literals). */
    public static final char
        QUOTE_CHAR = QUOTE.charAt(0);


    /** The namespace separator character. */
    public static final String
        NAMESPACE_SEPARATOR = AtriaCharacterClasses.NAMESPACE_SEPARATOR;

    /** The namespace separator character. */
    public static final char
        NAMESPACE_SEPARATOR_CHAR = NAMESPACE_SEPARATOR.charAt(0);


    // Character classes (as Strings)

    /** All of the Atria Name characters. */
    public static final String
        NAME_CHARS = AtriaCharacterClasses.NAME_CHARACTER;

    /** All of the Atria literal text characters. */
    public static final String
        TEXT_CHARS = AtriaCharacterClasses.TEXT_CHARACTER;


    // Miscellaneous constants

    /** The number of spaces per level of indentation. */
    public static final int
        SPACES_PER_INDENT_LEVEL = 4;


    // Public static methods

    /**
        Returns the specified pathname with the full Atria source file
        extension removed from it.

        @param pathname the pathname
        @return 'pathname' with the FULL_SOURCE_EXTENSION removed from the
        end of it
        @see #FULL_SOURCE_EXTENSION
    */
    public static String removeSourceExtension(String pathname)
    {
        Assert.require(pathname.endsWith(FULL_SOURCE_EXTENSION));

        return pathname.substring(0,
                pathname.length() - FULL_SOURCE_EXTENSION.length());
    }

    /**
        Indicates whether the specified character is the first character of a
        newline in Atria source code.

        @param ch the character to test
        @return true iff the character is the first character of a newline in
        Atria
    */
    public static boolean isNewlineStart(char ch)
    {
        return TextUtilities.isNewlineStart(ch);
    }

    /**
        @param ch the character to test
        @return true iff 'ch' is a valid character in an Atria identifier
    */
    public static boolean isNameCharacter(char ch)
    {
        return (NAME_CHARS.indexOf(ch) >= 0);
    }

    /**
        @param ch the character to test
        @return true iff 'ch' can validly be in a literal Atria text
    */
    public static boolean isTextCharacter(char ch)
    {
        return (((int) ch) >= FIRST_NON_ASCII_UNICODE_CODEPOINT) ||
                (TEXT_CHARS.indexOf(ch) >= 0);
    }
}
