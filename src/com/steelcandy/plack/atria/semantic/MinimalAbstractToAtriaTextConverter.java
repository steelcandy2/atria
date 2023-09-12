/*
 Copyright (C) 2023 by James MacKay.

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

package com.steelcandy.plack.atria.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;

import java.io.IOException;

/**
    A minimal abstract base class for classes that convert arbitrary text
    into the Atria representation of that text.
    <p>
    The main function of this class is to centralize — as much as possible —
    the handling of characters that can't appear in Atria Text constructs.
    <p>
    Note: for our purposes a character is a special character iff it can't
    appear between the double quote characters at the start and end of a
    valid Atria Text construct. A character is non-special if it isn't
    special.

    @author James MacKay
    @see AbstractToAtriaTextConverter
*/
public abstract class MinimalAbstractToAtriaTextConverter
{
    // Constants

    /** The Atria text start character. */
    public static final String
        ATRIA_TEXT_START = AtriaInfo.QUOTE;

    /** The Atria text end character. */
    public static final String
        ATRIA_TEXT_END = AtriaInfo.QUOTE;

    /** The double quote character ("). */
    public static final char
        DOUBLE_QUOTE_CHAR = '"';

    /* The newline character. */
    public static final char
        NEWLINE_CHAR = '\n';


    // Public static methods

    /**
        @param ch a character
        @return that name of the Atria command that represents 'ch' if 'ch'
        is a special character, and null otherwise
    */
    public static String findCommandNameForSpecialCharacter(char ch)
    {
        String result = null;

        if (ch == DOUBLE_QUOTE_CHAR)
        {
            result = AtriaInfo.QUOTE_COMMAND_NAME;
            Assert.check(result != null);
            Assert.check(result.isEmpty() == false);
        }
        else if (ch == NEWLINE_CHAR)
        {
            result = AtriaInfo.NEWLINE_COMMAND_NAME;
            Assert.check(result != null);
            Assert.check(result.isEmpty() == false);
        }

        // 'result' can be null
        Assert.ensure(result == null || result.isEmpty() == false);
        return result;
    }

    /**
        Returns true iff 'q' is a Character object that represents the
        double quote character.

        @see AbstractToAtriaTextConverter#DOUBLE_QUOTE_CHAR
    */
    public static boolean isDoubleQuoteCharacter(Object q)
    {
        //System.err.println("===> isDoubleQuoteCharacter(" + q + ") ...");
        return (q != null) && (q instanceof Character) &&
            (((Character) q).charValue() == DOUBLE_QUOTE_CHAR);
    }


    // Protected methods

    /**
        Converts the specified text into its Atria representation.

        @exception IOException thrown if an I/O error occurs as part of the
        conversion
    */
    protected void convertText(String txt)
        throws IOException
    {
        Assert.require(txt != null);  // though it can be empty

        int sz = txt.length();
        if (sz > 0)
        {
            StringBuffer buf = null;
            for (int i = 0; i < sz; i++)
            {
                char ch = txt.charAt(i);
                if (AtriaInfo.isTextCharacter(ch))
                {
                    Assert.check(ch != DOUBLE_QUOTE_CHAR);
                    Assert.check(ch != NEWLINE_CHAR);
                    if (buf == null)
                    {
                        buf = new StringBuffer();
                    }
                    buf.append(ch);
                }
                else if (ch == DOUBLE_QUOTE_CHAR)
                {
                    convertBufferIfNonempty(buf);
                    buf = null;
                    convertDoubleQuote(ch);
                }
                else
                {
                    Assert.check(ch == NEWLINE_CHAR);  // newline
                        // otherwise there's a character that we can't
                        // represent in an Atria text
                    convertBufferIfNonempty(buf);
                    buf = null;
                    convertNewline(ch);
                }
            }
            convertBufferIfNonempty(buf);
        }
    }

    /**
        Converts the contents of the specified buffer unless it's null or
        empty.

        @param buf the buffer whose contents - which are assumed to all be
        non-special characters - are to be converted
        @exception IOException thrown if an I/O error occurs as part of the
        conversion
        @see #convertNonspecialText(String)
    */
    protected void convertBufferIfNonempty(StringBuffer buf)
        throws IOException
    {
        // 'buf' can be null

        if (buf != null && buf.length() > 0)
        {
            convertNonspecialText(buf.toString());
        }
    }


    // Protected abstract methods

    /**
        Converts the sequence of characters in 'txt', where each character in
        it is assumed to be non-special.

        @param txt the sequence of non-special characters to convert
        @exception IOException thrown if an I/O error occurs as part of the
        conversion
        @see #convertDoubleQuote(char)
        @see #convertNewline(char)
    */
    protected abstract void convertNonspecialText(String txt)
        throws IOException;
        // Assert.require(txt != null);
        // Assert.require(txt.isEmpty() == false);

    /**
        Converts the double quote character 'ch'.

        @param ch a double quote character
        @exception IOException thrown if an I/O error occurs as part of the
        conversion
        @see #DOUBLE_QUOTE_CHAR
        @see #convertNewline(char)
    */
    protected abstract void convertDoubleQuote(char ch)
        throws IOException;
        // Assert.require(ch == DOUBLE_QUOTE_CHAR);

    /**
        Converts the newline character 'ch'.

        @param ch a newline character
        @exception IOException thrown if an I/O error occurs as part of the
        conversion
        @see #NEWLINE_CHAR
        @see #convertDoubleQuote(char)
    */
    protected abstract void convertNewline(char ch)
        throws IOException;
        // Assert.require(ch == NEWLINE_CHAR);
}
