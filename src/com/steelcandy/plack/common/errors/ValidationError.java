/*
 Copyright (C) 2001-2005 by James MacKay.

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
    Represents an error that occurs during validation as a result of a
    validity constraint not being satisfied.

    @author James MacKay
*/
public class ValidationError
    extends AbstractKeyedPlackError
{
    // Private fields

    /** The name of the validity constraint that was not satisfied. */
    private String _constraintName;


    // Constructors

    /**
        Constructs a ValidationError.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param code the piece of source code that the error was found in
        @param loc the location in the source code where the error occurred
    */
    public ValidationError(int level, String constraintName,
                           String description,
                           SourceCode code, SourceLocation loc)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        super(level, description, code, loc);
        Assert.require(constraintName != null);

        _constraintName = constraintName;
    }

    /**
        Constructs a ValidationError.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param code the piece of source code that the error was found in
        @param loc the location in the source code where the error occurred
        @param key the error's key
    */
    public ValidationError(int level, String constraintName,
                           String description, SourceCode code,
                           SourceLocation loc, ErrorKey key)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'key' can be null
        super(level, description, code, loc, key);
        Assert.require(constraintName != null);

        _constraintName = constraintName;
    }


    // Public methods

    /**
        @return the name of the validity constraint that wasn't satisfied
    */
    public String constraintName()
    {
        String result = _constraintName;

        Assert.ensure(result != null);
        return result;
    }
}
