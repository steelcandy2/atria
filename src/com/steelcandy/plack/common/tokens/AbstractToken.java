/*
 Copyright (C) 2001-2008 by James MacKay.

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

import com.steelcandy.plack.common.source.*;

import com.steelcandy.common.*;

/**
    An abstract base class for most Token classes. It only maintains the
    location in the source code of the source fragment that a token
    represents: the fields must be maintained by subclasses. By default
    instances of this class have no flags set. This class also overrides
    several Object methods.
    <p>
    Subclasses only have to implement the id() and cloneToken() methods,
    though they may also want to override the flag- and value-related
    methods.
    <p>
    By default tokens that are direct instances of this class have no flags
    set and no values associated with them (nor can the values be set), but
    the appropriate methods can be overridden by subclasses to change some or
    all of these.
    <p>
    The implementation of positionAfter() provided by this class assumes that
    the next position is one character after and on the same line as the last
    character in the piece of source code represented by this token. This is
    usually the correct implementation, but certain subclasses - particularly
    token classes can represent pieces of source code containing newlines -
    may want to override it.
    <p>
    Token classes not descended from this one may find it useful to use this
    class' valuesToString(Token) static method to implement their
    parameterless valuesToString() methods. Similarly, the flagToIndex()
    static method can be used by other classes to convert flags to a
    zero-based index.

    @author James MacKay
*/
public abstract class AbstractToken
    implements Token
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        CANNOT_SET_STRING_VALUE_MSG =
            "CANNOT_SET_STRING_VALUE_MSG";
    private static final String
        CANNOT_SET_INT_VALUE_MSG =
            "CANNOT_SET_INT_VALUE_MSG";
    private static final String
        CANNOT_SET_SECOND_INT_VALUE_MSG =
            "CANNOT_SET_SECOND_INT_VALUE_MSG";
    private static final String
        CANNOT_SET_DOUBLE_VALUE_MSG =
            "CANNOT_SET_DOUBLE_VALUE_MSG";
    private static final String
        CANNOT_SET_OBJECT_VALUE_MSG =
            "CANNOT_SET_OBJECT_VALUE_MSG";


    // Private fields

    /**
        The location in the source code of the fragment that this token
        represents.
    */
    private SourceLocation _location;


    // Constructors

    /**
        Constructs an AbstractToken.

        @param location the location in the source code of the source
        fragment that the token represents
    */
    public AbstractToken(SourceLocation location)
    {
        _location = location;
    }


    // Token position/location methods

    /**
        @see Token#location
    */
    public SourceLocation location()
    {
        return _location;
    }

    /**
        @see Token#positionAfter
    */
    public SourcePosition positionAfter()
    {
        return location().pastEndPosition();
    }


    // Token flag methods

    /**
        This implementation always returns false.

        @see Token#isFlagSet
    */
    public boolean isFlagSet(int flag)
    {
        return false;
    }

    /**
        This implementation always returns false.
        <p>
        If this method is overridden to return <code>true</code> for some
        flags then setFlag() should also be overridden to be able to set
        those flags.

        @see Token#canSetFlag
        @see #setFlag
    */
    public boolean canSetFlag(int flag)
    {
        return false;
    }

    /**
        This implementation does nothing.
        <p>
        If canSetFlag() is overridden to return <code>true</code> for some
        flags then this method should also be overridden to be able to set
        those flags.

        @see Token#setFlag
        @see #canSetFlag
    */
    public void setFlag(int flag)
    {
        Assert.require(canSetFlag(flag));
    }

    /**
        This implementation always returns false.
        <p>
        If this method is overridden to return <code>true</code> for some
        flags then clearFlag() should also be overridden to be able to
        clear/unset those flags.

        @see Token#canClearFlag
        @see #clearFlag
    */
    public boolean canClearFlag(int flag)
    {
        return false;
    }

    /**
        This implementation does nothing.
        <p>
        If canClearFlag() is overridden to return <code>true</code> for some
        flags then this method should also be overridden to be able to
        clear/unset those flags.

        @see Token#clearFlag
        @see #canClearFlag
    */
    public void clearFlag(int flag)
    {
        Assert.require(canClearFlag(flag));
    }


    // Token value methods

    /**
        @see Token#valuesToString
    */
    public String valuesToString()
    {
        return valuesToString(this);
    }

    /**
        @see Token#hasStringValue
    */
    public boolean hasStringValue()
    {
        return false;
    }

    /**
        @exception IllegalStateException thrown if this version of this
        method is called
        @see Token#stringValue
    */
    public String stringValue()
    {
        Assert.require(hasStringValue());
        throw new IllegalStateException();
    }

    /**
        @see Token#canSetStringValue
    */
    public boolean canSetStringValue()
    {
        return false;
    }

    /**
        This method must be overridden if canSetStringValue() is overridden
        to return true.

        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#setStringValue
    */
    public void setStringValue(String newValue)
    {
        Assert.require(canSetStringValue());

        String msg = _resources.getMessage(CANNOT_SET_STRING_VALUE_MSG);
        throw new IllegalStateException(msg);
    }


    /**
        @see Token#hasIntValue
    */
    public boolean hasIntValue()
    {
        return false;
    }

    /**
        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#intValue
    */
    public int intValue()
    {
        Assert.require(hasIntValue());
        throw new IllegalStateException();
    }

    /**
        @see Token#canSetIntValue
    */
    public boolean canSetIntValue()
    {
        return false;
    }

    /**
        This method must be overridden if canSetIntValue() is overridden to
        return true.

        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#setIntValue
    */
    public void setIntValue(int newValue)
    {
        Assert.require(canSetIntValue());

        String msg = _resources.getMessage(CANNOT_SET_INT_VALUE_MSG);
        throw new IllegalStateException(msg);
    }


    /**
        @see Token#hasSecondIntValue
    */
    public boolean hasSecondIntValue()
    {
        return false;
    }

    /**
        @exception IllegalStateException thrown if this version of this
        method is called
        @see Token#secondIntValue
    */
    public int secondIntValue()
    {
        Assert.require(hasSecondIntValue());
        throw new IllegalStateException();
    }

    /**
        @see Token#canSetSecondIntValue
    */
    public boolean canSetSecondIntValue()
    {
        return false;
    }

    /**
        This method must be overridden if canSetSecondIntValue() is
        overridden to return true.

        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#setSecondIntValue
    */
    public void setSecondIntValue(int newValue)
    {
        Assert.require(canSetSecondIntValue());

        String msg = _resources.getMessage(CANNOT_SET_SECOND_INT_VALUE_MSG);
        throw new IllegalStateException(msg);
    }


    /**
        @see Token#hasDoubleValue
    */
    public boolean hasDoubleValue()
    {
        return false;
    }

    /**
        @exception IllegalStateException thrown if this version of this
        method is called
        @see Token#doubleValue
    */
    public double doubleValue()
    {
        Assert.require(hasDoubleValue());
        throw new IllegalStateException();
    }

    /**
        @see Token#canSetDoubleValue
    */
    public boolean canSetDoubleValue()
    {
        return false;
    }

    /**
        This method must be overridden if canSetDoubleValue() is overridden
        to return true.

        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#setDoubleValue
    */
    public void setDoubleValue(double newValue)
    {
        Assert.require(canSetDoubleValue());

        String msg = _resources.getMessage(CANNOT_SET_DOUBLE_VALUE_MSG);
        throw new IllegalStateException(msg);
    }


    /**
        @see Token#hasObjectValue
    */
    public boolean hasObjectValue()
    {
        return false;
    }

    /**
        @exception IllegalStateException thrown if this version of this
        method is called
        @see Token#objectValue
    */
    public Object objectValue()
    {
        Assert.require(hasObjectValue());
        throw new IllegalStateException();
    }

    /**
        @see Token#canSetObjectValue
    */
    public boolean canSetObjectValue()
    {
        return false;
    }

    /**
        This method must be overridden if canSetObjectValue() is overridden
        to return true.

        @exception IllegalStateException thrown if this version of this
        method is ever called
        @see Token#setObjectValue
    */
    public void setObjectValue(Object newValue)
    {
        Assert.require(canSetObjectValue());

        String msg = _resources.getMessage(CANNOT_SET_OBJECT_VALUE_MSG);
        throw new IllegalStateException(msg);
    }


    // Overridden Object methods

    /**
        By default tokens are considered equal if they have the same ID and
        location.

        @see Object#equals
    */
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj != null && obj instanceof Token)
        {
            Token tok = (Token) obj;
            result = id() == tok.id() &&
                        location().equals(tok.location());
        }
        return result;
    }

    /**
        We have to override this method so that objects that are equal
        according to equals() have the same hash code.

        @see Object#hashCode
        @see Object#equals
    */
    public int hashCode()
    {
        // We don't just use id() since we don't want all tokens of the same
        // type to hash to the same value.
        return id() ^ location().hashCode();
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer(getClass().getName());

        buf.append(": id = ").append(String.valueOf(id()));

        String values = valuesToString(this);
        if (values.length() > 0)
        {
            buf.append(", ").append(values);
        }

        return buf.toString();
    }


    // Static utility methods

    /**
        Converts the specified token flag to a zero-based index: flag 1 is
        mapped to index 0, flag 2 to index 1, flag 4 to index 2, and flag
        (2^n) to index n.
        <p>
        This method assumes that the specified flag has exactly one bit set.

        @param flag the flag value that is to be converted to a zero-based
        index
        @return the zero-based index created from the flag
    */
    public static int flagToIndex(int flag)
    {
        Assert.require(flag > 0);

        int result = -1;
        for (int shiftBy = 0; shiftBy < Utilities.BITS_IN_INT; shiftBy++)
        {
            if ((flag >>> shiftBy) == 1)
            {
                result = shiftBy;
            }
        }

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Returns a string representation of the specified token's values,
        which will be an empty string iff the token has no values associated
        with it.

        @param tok the token whose values are to be returned (as a string)
        @return a string representation of the token's values
    */
    public static String valuesToString(Token tok)
    {
        StringBuffer buf = new StringBuffer();
        if (tok.hasIntValue())
        {
            buf.append(", int = ").
                append(String.valueOf(tok.intValue()));
        }
        if (tok.hasSecondIntValue())
        {
            buf.append(", second int = ").
                append(String.valueOf(tok.secondIntValue()));
        }
        if (tok.hasDoubleValue())
        {
            buf.append(", double = ").
                append(String.valueOf(tok.doubleValue()));
        }
        if (tok.hasStringValue())
        {
            buf.append(", string = [").
                append(tok.stringValue()).append("]");
        }
        if (tok.hasObjectValue())
        {
            buf.append(", object value = [").
                append(tok.objectValue().toString()).append("]");
        }

        String result = buf.toString();
        if (result.length() > 0)
        {
            // Remove the leading ", ".
            Assert.check(result.length() > 2);
            result = result.substring(2);
        }
        return result;
    }

    /**
        Returns the result of setting all of the flagsToSet in the
        currentFlags.

        @param flagsToSet the flags to be set
        @param currentFlags the flags that are currently set
        @return the result of setting all of the flagsToSet in the
        currentFlags
    */
    protected static int setFlags(int flagsToSet, int currentFlags)
    {
        return currentFlags | flagsToSet;
    }

    /**
        Returns the result of clearing all of the flagsToSet in the
        currentFlags.

        @param flagsToClear the flags to be cleared/unset
        @param currentFlags the flags that are currently set
        @return the result of clearing all of the flagsToClear in the
        currentFlags
    */
    protected static int clearFlags(int flagsToClear, int currentFlags)
    {
        return currentFlags & (~flagsToClear);
    }
}
