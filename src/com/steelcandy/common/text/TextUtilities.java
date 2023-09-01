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

package com.steelcandy.common.text;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;
import com.steelcandy.common.compare.*;
import com.steelcandy.common.io.Io;

import java.util.*;

/**
    Class containing various text-related utility methods and
    constants.

    @author James MacKay
*/
public class TextUtilities
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TextResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NON_FINAL_LIST_SEPARATOR =
            "NON_FINAL_LIST_SEPARATOR",
        FINAL_LIST_SEPARATOR =
            "FINAL_LIST_SEPARATOR",
        DEFAULT_POSITION_NAME_SUFFIX =
            "DEFAULT_POSITION_NAME_SUFFIX",
        FIRST_POSITION_NAME_SUFFIX =
            "FIRST_POSITION_NAME_SUFFIX",
        SECOND_POSITION_NAME_SUFFIX =
            "SECOND_POSITION_NAME_SUFFIX",
        THIRD_POSITION_NAME_SUFFIX =
            "THIRD_POSITION_NAME_SUFFIX";

    /** Resource identifier components. */
    private static final String
        SPECIFIC_POSITION_NAME_KEY_PREFIX =
            "POSITION_NAME_FOR_";


    /**  The line separator used by this platform. */
    public static final String LINE_SEPARATOR =
        System.getProperty("line.separator");

    /** A shorter name for this platform's line separator. */
    public static final String NL = LINE_SEPARATOR;


    /** The characters that can start a newline. */
    public static final String NEWLINE_START_CHARS = "\n\r";

    /** The characters that can be anywhere in a newline. */
    public static final String NEWLINE_CHARS = NEWLINE_START_CHARS;


    /** An intern()ed empty String. */
    public static final String
        INTERNED_EMPTY_STRING = (new String()).intern();


    // Constructors

    /**
        This constructor is private to prevent this class from
        being instantiated.
    */
    private TextUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        @param c a list of strings
        @return a string representation of 'c'
    */
    public static String toString(StringList c)
    {
        // 'c' can be null

        String result;

        if (c == null)
        {
            result = "[null StringList]";
        }
        else
        {
            StringBuffer buf = new StringBuffer("[");
            boolean isFirst = true;

            StringIterator iter = c.iterator();
            while (iter.hasNext())
            {
                if (isFirst)
                {
                    isFirst = false;
                }
                else
                {
                    buf.append(", ");
                }
                buf.append(iter.next());
            }

            buf.append("]");
            result = buf.toString();
            Assert.check(result.length() > 0);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param a an array of Strings
        @return a StringList whose 'i''th item is the 'i''th item in 'a'
    */
    public static StringList toStringList(String[] a)
    {
        Assert.require(a != null);

        int sz = a.length;

        StringList result = StringList.createArrayList(sz);

        for (int i = 0; i < sz; i++)
        {
            result.add(a[i]);
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == a.length);
        return result;
    }

    /**
        @param str a string
        @param len the minimum length of the result
        @return 'str' if it's at least 'len' characters long, and a string
        consisting of 'str' followed by enough spaces to make it 'len'
        characters long otherwise
    */
    public static String pad(String str, int len)
    {
        Assert.require(str != null);
        Assert.require(len >= 0);

        String result = pad(str, len, ' ');

        Assert.ensure(result != null);
        Assert.ensure(result.length() == Math.max(len, str.length()));
        return result;
    }

    /**
        @param str a string
        @param len the minimum length of the result
        @param padChar the pad character
        @return 'str' if it's at least 'len' characters long, and a string
        consisting of 'str' followed by enough copies of 'ch' to make it
        'len' characters long otherwise
    */
    public static String pad(String str, int len, char padChar)
    {
        Assert.require(str != null);
        Assert.require(len >= 0);

        String result = str;

        int padLen = str.length() - len;
        if (padLen > 0)
        {
            StringBuffer buf = new StringBuffer(len);
            buf.append(str);
            for (int i = 0; i < padLen; i++)
            {
                buf.append(padChar);
            }
            result = buf.toString();
            Assert.check(result.length() == len);
        }

        Assert.ensure(result != null);
        Assert.ensure(result.length() == Math.max(len, str.length()));
        return result;
    }


    /**
        Returns a list of the keys for the map 'm' that is sorted by the
        default String comparator. All of 'm''s keys are assumed to be
        Strings.

        @param m the map
        @return the sorted list of 'm''s keys
        @see DefaultStringComparator
        @see #sortedStringKeys(Map, Comparator)
    */
    public static StringList sortedStringKeys(Map m)
    {
        Assert.require(m != null);

        StringList result =
            sortedStringKeys(m, DefaultStringComparator.instance());

        Assert.ensure(result != null);
        Assert.ensure(result.size() == m.size());
        return result;
    }

    /**
        Returns a list of the keys for the map 'm' that is sorted by the
        default String comparator. All of 'm''s keys are assumed to be
        Strings.

        @param m the map
        @return the sorted list of 'm''s keys
        @see DefaultStringComparator
        @see #sortedStringKeys(Map, Comparator)
    */
    public static StringList sortedStringKeys(Map m, Comparator keyComp)
    {
        Assert.require(m != null);
        Assert.require(keyComp != null);

        StringList result;

        if (m.isEmpty())
        {
            result = StringList.createEmptyList();
        }
        else
        {
            result = StringList.createArrayList(m.size());
            Iterator iter = m.keySet().iterator();
            while (iter.hasNext())
            {
                result.add((String) iter.next());
            }
            result.sort(keyComp);
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == m.size());
        //Assert.ensure("'result' is sorted according to 'keyComp'");
        return result;
    }


    /**
        @param str a string
        @param chars a set of characters
        @return true iff 'str' at least one of the characters in 'chars' is
        in 'str'
    */
    public static boolean contains(String str, String chars)
    {
        Assert.require(str != null);
        Assert.require(chars != null);

        boolean result = false;

        int len = chars.length();
        for (int i = 0; i < len; i++)
        {
            char ch = chars.charAt(i);
            if (str.indexOf(ch) >= 0)
            {
                result = true;
                break;  // for
            }
        }

        return result;
    }

    /**
        @param str a string
        @param chars a set of characters
        @return true iff every character in 'str' is in 'chars'
    */
    public static boolean onlyContainsCharactersIn(String str, String chars)
    {
        Assert.require(str != null);
        Assert.require(chars != null);

        boolean result = true;

        int len = str.length();
        for (int i = 0; i < len; i++)
        {
            char ch = str.charAt(i);
            if (chars.indexOf(ch) < 0)
            {
                // 'ch' is not in 'chars'
                result = false;
                break;  // for
            }
        }

        return result;
    }

    /**
        @param str a string
        @return true iff 'str' contains one or more whitespace characters
    */
    public static boolean containsWhitespace(String str)
    {
        Assert.require(str != null);

        boolean result = false;

        int len = str.length();
        for (int i = 0; i < len; i++)
        {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch))
            {
                result = true;
                break;
            }
        }

        return result;
    }

    /**
        @param str a string
        @return true iff every character of 'str' is a whitespace character:
        thus all zero characters of an empty string are whitespace ones
        @see #countWhitespaceFrom(int, String)
    */
    public static boolean isAllWhitespace(String str)
    {
        Assert.require(str != null);

        boolean result = true;

        int sz = str.length();
        for (int i = 0; result && i < sz; i++)
        {
            result = Character.isWhitespace(str.charAt(i));
        }

        Assert.ensure(sz > 0 || result);  // sz == 0 implies result
        return result;
    }

            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
        @param str a string
        @return true iff every character of 'str' is a decimal digit
    */
    public static boolean isAllDigits(String str)
    {
        Assert.require(str != null);

        boolean result = true;

        int len = str.length();
        for (int i = 0; i < len; i++)
        {
            char ch = str.charAt(i);
            if (Character.isDigit(ch) == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
TODO: add 'isAllLowercaseAlphanumeric()', 'isAllAlphanumeric()', etc. !!!

        @param str a string
        @return true iff every character of 'str' is a decimal digit or
        an uppercase letter
    */
    public static boolean isAllUppercaseAlphanumeric(String str)
    {
        Assert.require(str != null);

        boolean result = true;

        int len = str.length();
        for (int i = 0; i < len; i++)
        {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch) == false &&
                Character.isDigit(ch) == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }


    /**
        Returns a string consisting of the specified number of copies of
        the specified character, or the empty string if the number of
        copies is non-positive.

        @param ch the character from which to build the string
        @param numCopies the number of characters to be in the result
        @return a string consisting of numCopies copies of 'ch'
    */
    public static String copies(char ch, int numCopies)
    {
        if (numCopies <= 0)
        {
            return INTERNED_EMPTY_STRING;
        }

        StringBuffer buf = new StringBuffer(numCopies);
        for (int i = 0; i < numCopies; i++)
        {
            buf.append(ch);
        }
        return buf.toString();
    }

    /**
        Returns a string consisting of the specified number of copies of
        the specified string, or the empty string if the number of
        copies is non-positive.

        @param str the string from which to build the string
        @param numCopies the number of copies of 'str' to be in the result
        @return a string consisting of numCopies copies of 'str'
    */
    public static String copies(String str, int numCopies)
    {
        Assert.require(str != null);

        if (numCopies <= 0)
        {
            return INTERNED_EMPTY_STRING;
        }

        StringBuffer buf = new StringBuffer(numCopies * str.length());
        for (int i = 0; i < numCopies; i++)
        {
            buf.append(str);
        }
        return buf.toString();
    }

    /**
        @param str a string
        @param ch a character
        @return the result of removing every occurence of 'ch' from 'str' (so
        that it no longer contains any occurrences of 'ch')
    */
    public static String removeAllOccurrences(String str, char ch)
    {
        Assert.require(str != null);

        int sz = str.length();
        StringBuffer res = new StringBuffer(sz);

        for (int i = 0; i < sz; i++)
        {
            char strChar = str.charAt(i);
            if (strChar != ch)
            {
                res.append(strChar);
            }
        }

        String result = res.toString();

        Assert.ensure(result.length() <= str.length());
        return result;
    }


    /**
        Splits the specified string into the parts that are separated by
        parts that match the specified regular expression. The
        separators will <em>not</em> appear in the resulting list, and
        separators at the beginning or end of the string will be ignored.

        @param str the string to split
        @param separatorRegex the regular expression that matches the
        separators
        @return a list of substrings of 'str' that were separated by parts
        that match 'separatorRegex'
    */
    public static StringList splitOnRegex(String str, String separatorRegex)
    {
        Assert.require(str != null);
        Assert.require(separatorRegex != null);

        String[] parts = str.split(separatorRegex);

        StringList result = StringList.createArrayList(parts.length);

        for (int i = 0; i < parts.length; i++)
        {
            result.add(parts[i]);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Splits the specified string into the parts that are separated
        by the specified separator. The separators will <em>not</em>
        appear in the resulting list, and separators at the beginning
        or end of the string will be ignored.

        @param str the string to split
        @param separator the separator to split 'str' on
        @return a list of the substrings of 'str' that were separated
        by 'separator'
    */
    public static StringList split(String str, String separator)
    {
        Assert.require(str != null);
        Assert.require(separator != null);

        StringList result = StringList.createArrayList();

        while (str.startsWith(separator))
        {
            str = str.substring(separator.length(), str.length());
        }
        while (str.endsWith(separator))
        {
            str = str.substring(0, str.length() - separator.length());
        }

        int index;
        while (true)
        {
            index = str.indexOf(separator);
            if (index < 0)
            {
                result.add(str);
                break;
            }

            result.add(str.substring(0, index));
            str = str.substring(index + separator.length(), str.length());
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the results of joining together the string representations
        of the objects returned by the specified iterator, each separated
        by the specified separator string.

        @param iter the iterator that returns the objects whose string
        representations are to be joined together
        @param separator the separator string to put between the
        objects' string representations
        @return the result of joining the objects' string representations,
        separated by 'separator'
    */
    public static String separate(Iterator iter, String separator)
    {
        Assert.require(iter != null);
        Assert.require(separator != null);

        StringBuffer result = new StringBuffer();

        if (iter.hasNext())
        {
            result.append(iter.next());
        }
        while (iter.hasNext())
        {
            result.append(separator).append(iter.next());
        }

        // Assert.ensure(result != null);
        return result.toString();
    }

    /**
        @see #separate(StringIterator, String, int)
    */
    public static String separate(StringIterator iter, String separator)
    {
        Assert.require(iter != null);
        Assert.require(separator != null);

        //Assert.ensure(result != null);
        return separate(iter, separator, 32);
    }

    /**
        Returns the results of joining together the strings returned by the
        specified iterator, each separated by the specified separator string.

        @param iter the iterator that returns the strings that are to be
        joined together
        @param separator the separator string to put between the strings
        @param estimatedSize an estimate of the length of the result
        @return the result of joining the strings, separated by 'separator'
    */
    public static String separate(StringIterator iter, String separator,
                                  int estimatedSize)
    {
        Assert.require(iter != null);
        Assert.require(separator != null);
        Assert.require(estimatedSize >= 0);

        StringBuffer result = new StringBuffer(estimatedSize);

        if (iter.hasNext())
        {
            result.append(iter.next());
        }
        while (iter.hasNext())
        {
            result.append(separator).append(iter.next());
        }

        // Assert.ensure(result != null);
        return result.toString();
    }

    /**
        @see #separate(Iterator, String)
    */
    public static String separate(String[] items, String separator)
    {
        Assert.require(items != null);
        Assert.require(separator != null);

        return separate(StringList.iterator(items), separator,
            totalLength(items) + (separator.length() * (items.length - 1)));
    }

    /**
        @see #separate(Iterator, String)
    */
    public static String separate(Object[] items, String separator)
    {
        Assert.require(items != null);
        Assert.require(separator != null);

        return separate(Arrays.asList(items).iterator(), separator);
    }

    /**
        @see #separate(Iterator, String)
    */
    public static String separate(int[] items, String separator)
    {
        Assert.require(items != null);
        Assert.require(separator != null);

        StringBuffer result = new StringBuffer();

        int numItems = items.length;
        if (numItems > 0)
        {
            result.append(items[0]);
        }
        for (int i = 1; i < numItems; i++)
        {
            result.append(separator).append(items[i]);
        }

        return result.toString();
    }


    /**
        @param items an array of Strings
        @return the total length of all of the Strings in 'items'
    */
    public static int totalLength(String[] items)
    {
        Assert.require(items != null);

        int result = 0;

        int sz = items.length;
        for (int i = 0; i < sz; i++)
        {
            result += items[i].length();
        }

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Indicates whether the specified character is the
        first character of a newline, where a newline is a CR
        (carriage return), LF (linefeed) or CRLF.

        @param ch the character to test for being the first
        character of a newline
        @return true iff the character is the first character
        of a newline
        @see #isNewlineCharacter(char)
    */
    public static boolean isNewlineStart(char ch)
    {
        return (NEWLINE_START_CHARS.indexOf(ch) >= 0);
    }

    /**
        Indicates whether the specified character is a character
        in a newline, where a newline is a CR (carriage return),
        LF (linefeed) or CRLF.

        @param ch the character to test for being a character
        in a newline
        @return true iff the character is a character in a newline
        @see #isNewlineStart(char)
    */
    public static boolean isNewlineCharacter(char ch)
    {
        return (NEWLINE_CHARS.indexOf(ch) >= 0);
    }

    /**
        Returns the specified string with any and all newlines
        removed from the beginning and end of it.

        @param str the string
        @return the result of removing all newlines from the beginning
        and end of 'str'
    */
    public static String trimNewlines(String str)
    {
        int length = str.length();

        // Remove newlines from the start of the string.
        int startIndex;
        for (startIndex = 0; startIndex < length; startIndex++)
        {
            char ch = str.charAt(startIndex);
            if (isNewlineCharacter(ch) == false)
            {
                break;
            }
        }

        int pastEndIndex;
        for (pastEndIndex = length; pastEndIndex > startIndex; pastEndIndex--)
        {
            char ch = str.charAt(pastEndIndex - 1);
            if (isNewlineCharacter(ch) == false)
            {
                break;
            }
        }

        return str.substring(startIndex, pastEndIndex);
    }

    /**
        Returns the specified string with all instances of the
        specified character trimmed off of the front of it.

        @param str the string to trim
        @param ch the character to trim off of the front of 'str'
        @return the result of trimming all of the 'ch' characters
        off of the front of 'str'
    */
    public static String trimLeading(String str, char ch)
    {
        int length = str.length();
        int numLeadingChars = 0;
        for (int i = 0; i < length; i++)
        {
            if (str.charAt(i) != ch)
            {
                break;
            }
            numLeadingChars += 1;
        }

        return str.substring(numLeadingChars);
    }


    /**
        @param str a string
        @param toReplace the substring to replace
        @param replacement the string to replace each occurrence of
        'toReplace' in 'str' with
        @return a string that is the same as 'str' except that each
        non-overlapping occurrence of 'toReplace' has been replaced with
        'replacement'
    */
    public static String
        replaceAll(String str, String toReplace, String replacement)
    {
        Assert.require(str != null);
        Assert.require(toReplace != null);
        Assert.require(replacement != null);

        String result;

        int replaceIndex = str.indexOf(toReplace);
        if (replaceIndex < 0)
        {
            result = str;
        }
        else
        {
            int toReplaceLength = toReplace.length();
            int startIndex = 0;
            StringBuffer buf = new StringBuffer(str.length());
            do
            {
                buf.append(str.substring(startIndex, replaceIndex)).
                    append(replacement);
                startIndex = replaceIndex + toReplaceLength;
                Assert.check(startIndex <= str.length());
                replaceIndex = str.indexOf(toReplace, startIndex);
            } while (replaceIndex >= 0);

            // Add the (possibly empty) part of 'str' that is after the
            // last occurrence of 'toReplace'.
            buf.append(str.substring(startIndex));

            result = buf.toString();
        }

        Assert.ensure(result != null);
        return result;
    }


    // Locale-specific methods

    /*
        Note: since the implementations of these methods are specific to a
        locale, they may need to be moved to locale-specific classes in the
        future. (Or add a java.util.Locale argument to each?)
    */

// TODO: add a version of this method that allows the locale to be specified !!!
    /**
        Returns a localized message part that consists of all of the items in
        'items' as a list in the default locale's usual format.

        @param items the items in the list
        @return a localized message part that consists of all of the items in
        'items' as a list in the default locale's usual format
    */
    public static String localizedList(StringList items)
    {
        Assert.require(items != null);

        String result;

        int sz = items.size();
        if (sz == 0)
        {
            result = INTERNED_EMPTY_STRING;
        }
        else if (sz == 1)
        {
            result = items.get(0);
        }
        else
        {
            StringBuffer buf = new StringBuffer(100);
            String prefix = _resources.getMessage(NON_FINAL_LIST_SEPARATOR);
            boolean isFirst = true;
            StringIterator iter = items.iterator();
            while (iter.hasNext())
            {
                String text = iter.next();
                if (isFirst)
                {
                    isFirst = false;  // and no prefix
                }
                else if (iter.hasNext())
                {
                    buf.append(prefix);
                }
                else
                {
// TODO: is there a better way to do this???!!!!????
// - message catalogs seem to discard leading whitespace, and I don't want to
//   have to count on trailing whitespace being left undisturbed either
                    buf.append(" ");
                    buf.append(_resources.getMessage(FINAL_LIST_SEPARATOR));
                    buf.append(" ");
                }
                buf.append(text);
            }
            result = buf.toString();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param pos the position of an item: e.g. 1 for first, 2 for second,
        etc.
        @return the name of the position that 'pos' represents: e.g. "first"
    */
    public static String positionName(int pos)
    {
        Assert.require(pos > 0);

        String result = specificPositionName(pos);

        if (result == null)
        {
            result = String.valueOf(pos) + positionNameSuffix(pos);
        }

        Assert.ensure(result != null);
        Assert.ensure(result.length() > 0);
        return result;
    }

    /**
        @param positions a list of item positions
        @return a list of the names of the positions in 'positions', in order
        @see #positionName(int)
        @see #localizedList(StringList)
    */
    public static String positionNamesList(int[] positions)
    {
        Assert.require(positions != null);
        //Assert.require(positions[i] > 0 for all 0 <= i < positions.length);

        int sz = positions.length;
        StringList names = StringList.createArrayList(sz);
        for (int i = 0; i < sz; i++)
        {
            names.add(positionName(positions[i]));
        }

        String result = localizedList(names);

        Assert.ensure(result != null);
        Assert.ensure((positions.length == 0) == result.isEmpty());
        return result;
    }


    /**
        @param pos the position of an item: e.g. 1 for first, 2 for second,
        etc.
        @return the specific name of the position that 'pos' represents, or
        null if we don't provide a specific name for 'pos'
        @see #positionName(int)
    */
    protected static String specificPositionName(int pos)
    {
        Assert.require(pos > 0);

        String result;

        String key = SPECIFIC_POSITION_NAME_KEY_PREFIX + String.valueOf(pos);
        try
        {
            result = _resources.getRequiredString(key);
        }
        catch (MissingResourceException ex)
        {
            result = null;
        }

        // 'result' may be null
        Assert.ensure(result == null || result.length() > 0);
        return result;
    }

    /**
        @param pos the position of an item: e.g. 1 for first, 2 for second,
        etc.
        @return the suffix that should appear at the end of the position name
        corresponding to 'pos'
        @see #positionName(int)
    */
    protected static String positionNameSuffix(int pos)
    {
        Assert.require(pos > 0);

        int t = pos % 10;
        int h = pos % 100;
        String key = DEFAULT_POSITION_NAME_SUFFIX;
        if (t == 1)
        {
            if (h != 11)
            {
                key = FIRST_POSITION_NAME_SUFFIX;
            }
        }
        else if (t == 2)
        {
            if (h != 12)
            {
                key = SECOND_POSITION_NAME_SUFFIX;
            }
        }
        else if (t == 3)
        {
            if (h != 13)
            {
                key = THIRD_POSITION_NAME_SUFFIX;
            }
        }

        String result = _resources.getRequiredString(key);

        Assert.ensure(result != null);
        return result;
    }


    // XML-related methods

    /**
        Returns the specified string with all of the special XML
        characters (such as less-than signs) escaped.

        @param str the original string
        @return 'str' with all of the special XML characters in it
        escaped
    */
    public static String escapeXml(String str)
    {
        int numChars = str.length();
        StringBuffer result = new StringBuffer(numChars);

        for (int i = 0; i < numChars; i++)
        {
            // TODO: replace this with a cleaner solution !!!
            char ch = str.charAt(i);
            switch (ch)
            {
            case '<':
                result.append("&lt;");
                break;
            case '>':
                result.append("&gt;");
                break;
            case '&':
                result.append("&amp;");
                break;
            case '"':
                result.append("&quot;");
                break;
            default:
                result.append(ch);
            }
        }

        return result.toString();
    }


    // Main method

    /**
        This main method is used to test this class' methods.

        @param args the command line arguments. They are currently ignored
    */
    public static void main(String[] args)
    {
        char ch1 = 'x';
        out("the character: " + ch1);
        for (int i = 0; i <= 10; i++)
        {
            out("    " + i + " copies: " + copies(ch1, i));
        }

        testSplit();
        testLocalizedList();

        System.exit(0);
    }

    /**
        Tests the localizedList() method.
    */
    private static void testLocalizedList()
    {
        int sz = 5;
        StringList strs = StringList.createArrayList(sz);

        strs.add("one");
        strs.add("two");
        strs.add("three");
        strs.add("four");
        strs.add("five");

        for (int i = 0; i <= sz; i++)
        {
            out("Localized list of " + i + " items: [" +
                localizedList(strs.subList(0, i)) + "]");
        }
    }

    /**
        Tests the split() method.
    */
    private static void testSplit()
    {
        String[] strs = new String[]
        {
            "first.second.third.fourth",
            "first.second.third.fourth.",
            ".first.second.third.fourth",
            ".first.second.third.fourth.",
            "first..second.third.fourth",
            "first.second.third.fourth..",
            "..first.second.third.fourth",
            "..first.second.third.fourth..",
            "",
            ".",
            "..",
            "first"
        };
        String sep = ".";

        for (int i = 0; i < strs.length; i++)
        {
            out("Splitting '" + strs[i] + "' on the separator '" + sep + "':");
            StringList result = split(strs[i], sep);
            out("    " + result.size() + " parts ...");
            StringIterator iter = result.iterator();
            while (iter.hasNext())
            {
                out("        " + iter.next());
            }
        }
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
