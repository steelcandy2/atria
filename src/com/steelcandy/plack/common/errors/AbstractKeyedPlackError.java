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
    The base class for PlackErrors that can have a key associated with them,
    allowing instances to be compared in order to detect duplicate errors.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public abstract class AbstractKeyedPlackError
    extends AbstractPlackError
{
    // Private fields

    /**
        The key that, if non-null, is used to compare this error with other
        AbstractKeyedPlackErrors to determine whether they describe the same
        error.
    */
    private ErrorKey _key;


    // Constructors

    /**
        Constructs an AbstractKeyedPlackError with no key associated with it.

        @param level the error's severity level
        @param description a description of the error
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
    */
    public AbstractKeyedPlackError(int level, String description,
                                   SourceCode code, SourceLocation loc)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        this(level, description, code, loc, null);
    }

    /**
        Constructs an AbstractKeyedPlackError with the specified key.

        @param level the error's severity level
        @param description a description of the error
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param key the error's key (iff it is non-null)
    */
    public AbstractKeyedPlackError(int level, String description,
                                   SourceCode code, SourceLocation loc,
                                   ErrorKey key)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'key' can be null
        super(level, description, code, loc);

        _key = key;
    }


    // Public methods

    /**
        @see PlackError#key
    */
    public ErrorKey key()
    {
        // 'result' may be null
        return _key;
    }

    /**
        AbstractKeyedPlackErrors are equal iff their keys are non-null and
        equal, or if their superclass' equals() method says they're equal.

        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (_key != null)
        {
            // We're equal to 'obj' only if its key is equal to ours.
            if (obj != null && obj instanceof AbstractKeyedPlackError)
            {
                result = equalKeys((AbstractKeyedPlackError) obj);
            }
        }
        else
        {
            // We have no key, so we're equal to 'obj' iff our superclass'
            // equals() says we are.
            result = super.equals(obj);
        }

        return result;
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        int result;

        if (_key != null)
        {
            // We're equal to another object iff we have the same key.
            result = _key.hashCode();
        }
        else
        {
            // We have no key, so we're equal to another object iff our
            // superclass' equals() method says we are.
            result = super.hashCode();
        }

        return result;
    }
}
