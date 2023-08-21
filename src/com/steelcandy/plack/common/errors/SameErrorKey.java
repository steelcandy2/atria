/*
 Copyright (C) 2004-2005 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

/**
    Represents ErrorKeys for errors that are the same iff they occur in the
    same place in source code with the same full name, have the same ID, and
    either have the same discriminator or both don't have a discriminator.
    <p>
    Note: discriminators are compared by passing one as an argument to the
    other's equals() method.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class SameErrorKey
    extends AbstractErrorKey
{
    // Private fields

    /** The error's ID. */
    private int _id;

    /** The error we're the key for. */
    private PlackError _error;

    /** This key's discriminator, or null if it doesn't have one. */
    private Object _discriminator;


    // Constructors

    /**
        Constructs a SameErrorKey.

        @param id the error's ID, which distinguishes it from other types of
        errors
        @param error the error that the key will be the key for
        @param d the key's discriminator
    */
    public SameErrorKey(int id, PlackError error, Object d)
    {
        Assert.require(error != null);
        Assert.require(d != null);

        _id = id;
        _error = error;
        _discriminator = d;
    }

    /**
        Constructs a SameErrorKey: it will have no discriminator.

        @param id the error's ID, which distinguishes it from other types of
        errors
        @param error the error that the key will be the key for
    */
    public SameErrorKey(int id, PlackError error)
    {
        Assert.require(error != null);

        _id = id;
        _error = error;
        _discriminator = null;
    }

    /**
        Constructs a SameErrorKey. setError() must be called on the instance
        before it can be used.

        @param id the error's ID, which distinguishes it from other types of
        errors
        @param d the key's discriminator
        @see ErrorKey#setError(PlackError)
    */
    public SameErrorKey(int id, Object d)
    {
        Assert.require(d != null);

        _id = id;
        _error = null;
        _discriminator = d;
    }

    /**
        Constructs a SameErrorKey. setError() must be called on the instance
        before it can be used. It will have no discriminator.

        @param id the error's ID, which distinguishes it from other types of
        errors
        @see ErrorKey#setError(PlackError)
    */
    public SameErrorKey(int id)
    {
        _id = id;
        _error = null;
        _discriminator = null;
    }


    // Public methods

    /**
        @see ErrorKey#setError(PlackError)
    */
    public void setError(PlackError error)
    {
        Assert.require(error != null);

        _error = error;
    }

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        Assert.check(_error != null);

        boolean result = false;

        if (obj != null && obj instanceof SameErrorKey)
        {
            SameErrorKey key = (SameErrorKey) obj;

            result = (_id == key._id) &&
                samePlace(key._error) && sameDiscriminator(key);
        }

        return result;
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        Assert.check(_error != null);

        int result = _id;

        SourceCode src = _error.sourceCode();
        if (src != null)
        {
            result ^= src.fullName().hashCode();
        }
        if (_error.location() != null)
        {
            result ^= _error.location().hashCode();
        }

        return result;
    }


    // Protected methods

    /**
        @return true iff we have a discriminator
    */
    protected boolean hasDiscriminator()
    {
        return (_discriminator != null);
    }

    /**
        @return our discriminator, if it has one
    */
    protected Object discriminator()
    {
        Assert.require(hasDiscriminator());

        Assert.ensure(_discriminator != null);
        return _discriminator;
    }

    /**
        @param k another SameErrorKey
        @return true iff we have the same discriminator as 'k'
    */
    protected boolean sameDiscriminator(SameErrorKey k)
    {
        Assert.require(k != null);

        boolean result = false;

        boolean hasDisc1 = hasDiscriminator();
        boolean hasDisc2 = k.hasDiscriminator();
        if (hasDisc1 && hasDisc2)
        {
            result = discriminator().equals(k.discriminator());
        }
        else if (hasDisc1 == false && hasDisc2 == false)
        {
            result = true;
        }

        return result;
    }


    /**
        @param err another error
        @return true iff our error and 'err' occurred at the same place in
        the same source code
    */
    protected boolean samePlace(PlackError err)
    {
        Assert.require(err != null);

        boolean result = false;

        SourceCode code1 = _error.sourceCode();
        SourceCode code2 = err.sourceCode();
        SourceLocation loc1 = _error.location();
        SourceLocation loc2 = err.location();

        // If any of the parts are null then we assume that the errors are
        // NOT in the same place.
        if (code1 != null && code2 != null && loc1 != null && loc2 != null)
        {
            result = code1.fullName().equals(code2.fullName()) &&
                        loc1.equals(loc2);
        }

        return result;
    }
}
