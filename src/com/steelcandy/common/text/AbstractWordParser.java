/*
 Copyright (C) 2004-2016 by James MacKay.

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

package com.steelcandy.common.text;

import com.steelcandy.common.debug.Assert;

/**
    An abstract base class for classes that parse Strings into words.
    <p>
    A word character is a character that is part of a word, and a non-word
    character is one that is not part of a word. By default whitespace
    characters are non-word characters and all other characters are word
    characters.
    <p>
    Note: this class is a base class rather than a utilities class with all
    static methods so that some of its methods can be overridden. For
    example, overriding isWordCharacter() to return Character.isDigit() can
    be used to parse a String into integers.

    @author  James MacKay
    @version $Revision: 1.6 $
    @see #isWordCharacter(char)
*/
public class AbstractWordParser
{
    // Constructors

    /**
        Constructs an AbstractWordParser
    */
    public AbstractWordParser()
    {
        // empty
    }


    // Protected methods

    /**
        @param str a string
        @return the number of words in 'str'
    */
    protected int numberOfWords(String str)
    {
        Assert.require(str != null);

        int result = 0;

        if (str.length() > 0)
        {
            int index = 0;
            while (index >= 0)
            {
                index = nextWordCharacterIndex(str, index);
                if (index < 0)
                {
                    break;  // while
                }
                result += 1;
                index = nextNonwordCharacterIndex(str, index);
            }
        }

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @param str a string
        @param wordIndex a 0-based index
        @return the ('wordIndex'+1)th word in 'str', or the empty string
        if 'str' doesn't contain at least ('wordIndex'+1) words
    */
    protected String word(String str, int wordIndex)
    {
        Assert.require(wordIndex >= 0);

        String result = "";

        int startIndex = wordStartIndex(str, wordIndex);
        if (startIndex >= 0)
        {
            int pastEndIndex = nextNonwordCharacterIndex(str, startIndex);
            if (pastEndIndex >= 0)
            {
                result = str.substring(startIndex, pastEndIndex);
            }
            else
            {
                result = str.substring(startIndex);
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param str a string
        @param wordIndex a 0-based index
        @return the part of 'str' after the non-word characters immediately
        following the ('wordIndex'+1)th word in 'str', or the empty string
        if 'str' doesn't contain at least ('wordIndex'+2) words
    */
    protected String afterWord(String str, int wordIndex)
    {
        Assert.require(wordIndex >= 0);

        String result = "";

        int startIndex = wordStartIndex(str, wordIndex + 1);
        if (startIndex >= 0)
        {
            result = str.substring(startIndex);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        @param str a string
        @param wordIndex a 0-based index
        @return the index of the first character of the ('wordIndex'+1)th
        word in 'str', or -1 if 'str' doesn't contain at least
        ('wordIndex'+1) words
    */
    protected int wordStartIndex(String str, int wordIndex)
    {
        Assert.require(str != null);
        Assert.require(wordIndex >= 0);

        int result = nextWordCharacterIndex(str, 0);

        for (int i = 0; i < wordIndex; i++)
        {
            // Skip over the (i+1)th word, leaving 'result' as the index
            // of the first character of the (i+2)th word (if there is an
            // (i+2)th word).
            result = nextNonwordCharacterIndex(str, result);
            if (result < 0)
            {
                break;  // there's nothing after the (i+1)th word
            }
            result = nextWordCharacterIndex(str, result);
            if (result < 0)
            {
                break;  // there's no (i+2)th word
            }
        }

        Assert.ensure(result >= -1);
        return result;
    }

    /**
        @param str a string
        @param startIndex an index into 'str'
        @return the index of the next non-word character in 'str' at or
        after index 'startIndex', or -1 if there is no such character
    */
    protected int nextNonwordCharacterIndex(String str, int startIndex)
    {
        Assert.require(str != null);
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < str.length());

        int result = -1;

        int len = str.length();
        for (int i = startIndex; i < len; i++)
        {
            if (isWordCharacter(str.charAt(i)) == false)
            {
                result = i;
                break;
            }
        }

        Assert.ensure(result == -1 || result >= startIndex);
        Assert.ensure(result < str.length());
        return result;
    }

    /**
        @param str a string
        @param startIndex an index into 'str'
        @return the index of the word character in 'str' at or after index
        'startIndex', or -1 if there is no such character
    */
    protected int nextWordCharacterIndex(String str, int startIndex)
    {
        Assert.require(str != null);
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < str.length());

        int result = -1;

        int len = str.length();
        for (int i = startIndex; i < len; i++)
        {
            if (isWordCharacter(str.charAt(i)))
            {
                result = i;
                break;
            }
        }

        Assert.ensure(result == -1 || result >= startIndex);
        Assert.ensure(result < str.length());
        return result;
    }

    /**
        @param ch a character
        @return true iff 'ch' is a word character
    */
    protected boolean isWordCharacter(char ch)
    {
        return (Character.isWhitespace(ch) == false);
    }
}
