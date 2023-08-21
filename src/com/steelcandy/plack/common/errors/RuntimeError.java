/*
 Copyright (C) 2005 by James MacKay.

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
    Represents a runtime error: an error that occurs while a program is
    actually running.
    <p>
    Note: this type of error is mostly used when a language is being
    interpreted. Runtime errors are usually handled differently when they
    occur in compiled code.

    @author James MacKay
*/
public class RuntimeError
    extends AbstractKeyedPlackError
{
    // Constructors

    /**
        Constructs a RuntimeError.

        @param level the error's severity level
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
    */
    public RuntimeError(int level, String description,
                        SourceCode code, SourceLocation loc)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        super(level, description, code, loc);
    }

    /**
        Constructs a RuntimeError.

        @param level the error's severity level
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param key the error's key
    */
    public RuntimeError(int level, String description,
                        SourceCode code, SourceLocation loc, ErrorKey key)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'key' may be null
        super(level, description, code, loc, key);
    }
}
