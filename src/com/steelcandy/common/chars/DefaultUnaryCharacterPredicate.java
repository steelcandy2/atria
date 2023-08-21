/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common.chars;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

/**
    The default implementation of the UnaryCharacterPredicate
    interface.

    @author James MacKay
*/
public class DefaultUnaryCharacterPredicate
    implements UnaryCharacterPredicate
{
    // Private fields

    /**
        The smallest (in integral value) of the characters that
        do not have the default value according to this predicate.
    */
    private char _minValue;

    /**
        The largest (in integral value) of the characters that
        do not have the default value according to this predicate.
    */
    private char _maxValue;

    /**
        The n'th element of this array indicates whether this
        predicate returns its non-default value for the char
        with integral value n + _minValue, where
        0 <= n <= (_maxValue - _minValue). It will be null iff
        there are no characters for which a non-default value
        is to be returned.
    */
    private boolean[] _hasNonDefaultValue = null;

    /**
        The default value of this predicate: that is, the value of
        this predicate for all of those characters not represented
        in the _values array.
    */
    private boolean _defaultValue;


    // Constructors

    /**
        Constructs a UnaryCharacterPredicate.

        @param charsWithNonDefaultValue the characters for which the
        predicate is not to return its default value. (It will return
        its default value for all other characters)
        @param defaultValue the predicate's default value
    */
    public DefaultUnaryCharacterPredicate(String charsWithNonDefaultValue,
                                          boolean defaultValue)
    {
        Assert.require(charsWithNonDefaultValue != null);

        // If there are no characters for which this predicate is
        // to return its non-default value, then _hasNonDefaultValue
        // will be left set to null.
        if (charsWithNonDefaultValue.length() > 0)
        {
            computeExtremeCharacterValues(charsWithNonDefaultValue);

            // Create the array indicating which character in the
            // range _minValue through _maxValue inclusive has the
            // non-default value. (This code depends on the fact that
            // all of a boolean array's elements are initially false.)
            int arraySize = _maxValue - _minValue + 1;
            Assert.check(arraySize > 0);

            _hasNonDefaultValue = new boolean[arraySize];
            int numChars = charsWithNonDefaultValue.length();
            for (int i = 0; i < numChars; i++)
            {
                char ch = charsWithNonDefaultValue.charAt(i);
                Assert.check(ch >= _minValue);
                Assert.check(ch <= _maxValue);
                _hasNonDefaultValue[(int) ch - _minValue] = true;
            }
        }
    }

    /**
        @see UnaryCharacterPredicate#isSatisfied
    */
    public boolean isSatisfied(char ch)
    {
        boolean result = _defaultValue;

        // If there is at least one character for which this
        // predicate can return its non-default value, and if
        // 'ch' could be one of those characters ...
        if (_hasNonDefaultValue != null &&
            ch >= _minValue && ch <= _maxValue)
        {
            if (_hasNonDefaultValue[(int) ch - _minValue])
            {
                result = !_defaultValue;
            }
        }
        return result;
    }


    // Private methods

    /**
        Computes the minimum and maximum characters in the
        specified string, and sets _minValue and _maxValue
        to those respective values.

        @param str the string that consists of the characters
        whose minumum and maximum elements are to be determined.
        @see #_minValue
        @see #_maxValue
    */
    private void computeExtremeCharacterValues(String str)
    {
        // Calculate the minimum and maximum characters in
        // the charsWithNonDefaultValue.
        _minValue = Character.MAX_VALUE;
        _maxValue = Character.MIN_VALUE;
        int numChars = str.length();
        for (int i = 0; i < numChars; i++)
        {
            char ch = str.charAt(i);
            if (ch < _minValue)
            {
                _minValue = ch;
            }
            if (ch > _maxValue)
            {
                _maxValue = ch;
            }
        }
    }


    // Testing methods

    /**
        The main method, which is used to test this class.
    */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            Io.err.println("usage: java " +
                DefaultUnaryCharacterPredicate.class.getName() +
                " matchingChars charsToCheck");
            Io.err.println("");
            Io.err.println("where matchingChars is the string of " +
                             "characters for which the predicate ");
            Io.err.println("is to be satisfied, and charsToCheck is the " +
                             "string of characters to pass to the " +
                             "predicate.");
            Io.err.println("");
            System.exit(1);
        }

        String matchingChars = args[0];
        String charsToCheck = args[1];
        UnaryCharacterPredicate predicate =
            new DefaultUnaryCharacterPredicate(matchingChars, false);
        for (int i = 0; i < charsToCheck.length(); i++)
        {
            char ch = charsToCheck.charAt(i);
            String msg = "the character '" + ch + "' ";
            boolean satisfied = predicate.isSatisfied(charsToCheck.charAt(i));
            msg += satisfied ? "satisfies the predicate"
                             : "does NOT satisfy the predicate";
            Io.out.println(msg);
        }
        System.exit(0);
    }
}
