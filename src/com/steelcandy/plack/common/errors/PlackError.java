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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

/**
    The interface implemented by classes that represent errors that occur
    while processing source code.
    <p>
    The piece of source containing an error and/or the location of the error
    in the source code can be null, which indicates that that information is
    not available about or applicable to the error.

    @author James MacKay
    @version $Revision: 1.12 $
*/
public interface PlackError
    extends ErrorSeverityLevels
{
    // Public methods

    /**
        @return this error's key, or null if it doesn't have one
    */
    public ErrorKey key();
        // 'result' may be null

    /**
        Indicates whether this error and the specified error have equal keys,
        and hence represent the same error.
        <p>
        Note: this method will return false if either of the errors doesn't
        have a key (i.e. if either of their key() methods returns null).

        @param err another error
        @return true iff our key and 'err's key are equal
    */
    public boolean equalKeys(PlackError err);
        // Assert.require(err != null);
        // Assert.ensure(key() != null || result == false);
        // Assert.ensure(err.key() != null || result == false);


    /**
        @return this error's severity level
    */
    public int level();

    /**
        @return the name of this error's severity level
    */
    public String levelName();
        // Assert.ensure(result != null);

    /**
        @return true iff this is a fatal error
    */
    public boolean isFatal();

    /**
        @return this error's description
    */
    public String description();
        // Assert.ensure(result != null);

    /**
        @return the piece of source code containing this error, or null if
        the piece of source code containing this error isn't known
    */
    public SourceCode sourceCode();
        // 'result' may be null

    /**
        @return the location in the source code where this error occurred, or
        null if the error's location isn't known
        @see #sourceCode
    */
    public SourceLocation location();
        // 'result' may be null
}
