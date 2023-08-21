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

/**
    Represents an error that occurs in reading data: i.e. an I/O error.

    @author James MacKay
*/
public class ReadError
    extends AbstractPlackError
{
    // Constructors

    /**
        Constructs a ReadError.

        @param level the error's severity level
        @param description the error's description
        @param code the source code whose reading caused the error, or null
        if the source code isn't known
    */
    public ReadError(int level, String description, SourceCode code)
    {
        // Assert.require(description != null);
        // 'code' can be null
        super(level, description, code, null);
    }
}
