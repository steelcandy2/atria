/*
 Copyright (C) 2004 by James MacKay.

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

/**
    The interface implemented by classes that represent a keyed error's
    key.
    <p>
    Subclasses must implement their equals() and hashCode() methods such
    that the keys for errors that are to be considered duplicates compare
    as equal.

    @author  James MacKay
    @see Object#equals(Object)
    @see Object#hashCode
*/
public interface ErrorKey
{
    // Public methods

    /**
        Sets the error that this key is the key for.
        <p>
        Note: subclasses are allowed to ignore and/or discard the error: it
        is provided for use by those types of keys that need it.

        @param error the error that this key is to be the key for
    */
    public void setError(PlackError error);
        // Assert.require(error != null);
}
