/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.HasSourceLocation;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourcePosition;

/**
    The interface implemented by all types of tokens.
    <p>
    All tokens have an ID that distinguishes it from other types of tokens.
    They also have a set of flags associated with them. What the flags are
    and what they signify are language-specific: this interface just provides
    the isFlagSet() method as the way to check whether a given flag is set,
    and the canSetFlag(), setFlag(), etc. methods to set them.
    <p>
    Tokens can have various types of values associated with them. Trying to
    access the value of a given type from a token that doesn't have a value
    of that type will cause an exception to be thrown. In some cases the
    values can also be set after the token has been created, though this
    tends to be rare.
    <p>
    Classes implementing this interface should implement the toString()
    method to return a string containing (at least) the token's class'
    fully-qualified name. (AbstractToken already does this.)

    @author James MacKay
    @see AbstractToken
*/
public interface Token
    extends HasSourceLocation
{
    // Public methods

    /**
        @return the position of the (theoretical) next character after the
        last character in the piece of source code represented by this token
    */
    public SourcePosition positionAfter();

    /**
        @param loc the copy's location
        @return a new Token that is a copy of this token but with the
        specified location
    */
    public Token cloneToken(SourceLocation loc);


    /**
        @return the ID identifying what this token represents
    */
    public int id();

    /**
        Indicates whether this token has the specified flag set. (The valid
        flags are language-specific.)

        @param flag the flag to check
        @return true if the flag is set, and false if it isn't
    */
    public boolean isFlagSet(int flag);

    /**
        Indicates whether the specified flag can be set on this token using
        its setFlag() method. Note that the flag may or may not be set or
        settable by other means.
    */
    public boolean canSetFlag(int flag);

    /**
        Sets the specified flag on this token.
    */
    public void setFlag(int flag);
        // Assert.require(canSetFlag(flag));

    /**
        Indicates whether the specified flag can be cleared/unset on this
        token using its clearFlag() method. Note that the flag may or may not
        be clearable by other means.
    */
    public boolean canClearFlag(int flag);

    /**
        Clears/unsets the specified flag on this token.
    */
    public void clearFlag(int flag);
        // Assert.require(canClearFlag(flag));


    // Token value methods

    /**
        Returns a string representation of all of this token's values.

        @return a string representation of all of this token's values
        @see AbstractToken#valuesToString(Token)
    */
    public String valuesToString();

    /**
        Indicates whether this token has a string value associated with it.

        @return true iff this token has a string value associated with it
    */
    public boolean hasStringValue();

    /**
        Returns the string value associated with this token.

        @return the string value associated with this token
    */
    public String stringValue();
        // Assert.require(hasStringValue());

    /**
        Indicates whether the string value associated with this token can be
        set.

        @return true if the string value associated with this token can be
        set, and false if it can't
        @see #setStringValue
    */
    public boolean canSetStringValue();

    /**
        Sets the string value associated with this token to the specified
        value.

        @param newValue the value that is to be this token's new string value
        @see #canSetStringValue
    */
    public void setStringValue(String newValue);
        // Assert.require(canSetStringValue());


    /**
        Indicates whether this token has an integer value associated with it.

        @return true iff this token has an integer value associated with it
    */
    public boolean hasIntValue();

    /**
        @return the integer value associated with this token
    */
    public int intValue();
        // Assert.require(hasIntValue());

    /**
        Indicates whether the integer value associated with this token can be
        set.

        @return true if the integer value associated with this token can be
        set, and false if it can't
        @see #setIntValue
    */
    public boolean canSetIntValue();

    /**
        Sets the integer value associated with this token to the specified
        value.

        @param newValue the value that is to be this token's new integer
        value
        @see #canSetIntValue
    */
    public void setIntValue(int newValue);
        // Assert.require(canSetIntValue());


    /**
        Indicates whether this token has a second integer value associated
        with it.

        @return true iff this token has a second integer value associated
        with it
    */
    public boolean hasSecondIntValue();

    /**
        @return the second integer value associated with this token
    */
    public int secondIntValue();
        // Assert.require(hasSecondIntValue());

    /**
        Indicates whether the second integer value associated with this token
        can be set.

        @return true if the second integer value associated with this token
        can be set, and false if it can't
        @see #setSecondIntValue
    */
    public boolean canSetSecondIntValue();

    /**
        Sets the second integer value associated with this token to the
        specified value.

        @param newValue the value that is to be this token's new second
        integer value
        @see #canSetSecondIntValue
    */
    public void setSecondIntValue(int newValue);
        // Assert.require(canSetSecondIntValue());


    /**
        Indicates whether this token has a double value associated with it.

        @return true iff this token has a double value associated with it
    */
    public boolean hasDoubleValue();

    /**
        @return the double value associated with this token
    */
    public double doubleValue();
        // Assert.require(hasDoubleValue());

    /**
        Indicates whether the double value associated with this token can be
        set.

        @return true if the double value associated with this token can be
        set, and false if it can't
        @see #setDoubleValue
    */
    public boolean canSetDoubleValue();

    /**
        Sets the double value associated with this token to the specified
        value.

        @param newValue the value that is to be this token's new double value
        @see #canSetDoubleValue
    */
    public void setDoubleValue(double newValue);
        // Assert.require(canSetDoubleValue());


    /**
        Indicates whether this token has an Object value associated with it.

        @return true iff this token has an Object value associated with it
    */
    public boolean hasObjectValue();

    /**
        @return the Object value associated with this token
    */
    public Object objectValue();
        // Assert.require(hasObjectValue());

    /**
        Indicates whether the Object value associated with this token can be
        set.

        @return true if the Object value associated with this token can be
        set, and false if it can't
        @see #setObjectValue
    */
    public boolean canSetObjectValue();

    /**
        Sets the Object value associated with this token to the specified
        value.

        @param newValue the value that is to be this token's new Object value
        @see #canSetObjectValue
    */
    public void setObjectValue(Object newValue);
        // Assert.require(canSetObjectValue());
}
