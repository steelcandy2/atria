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

/**
    Represents an error that occurs during target code generation.

    @author James MacKay
*/
public class CodeGenerationError
    extends AbstractPlackError
{
    // Constructors

    /**
        Constructs a CodeGenerationError.

        @param level the error's severity level
        @param description the error's description
    */
    public CodeGenerationError(int level, String description)
    {
        // Assert.require(description != null);
        super(level, description, null, null);
    }
}
