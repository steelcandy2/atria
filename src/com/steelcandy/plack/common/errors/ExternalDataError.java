/*
 Copyright (C) 2008 by James MacKay.

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
    Represents an error that occurs when we fail to read/write data from/to
    an external source/sink (such as a file) as part of a compilation.
    <p>
    At least currently this type of error is always fatal.

    @author James MacKay
*/
public class ExternalDataError
    extends AbstractSimplePlackError
{
    // Constructors

    /**
        Constructs an ExternalDataError.

        @param description the description of the error
    */
    public ExternalDataError(String description)
    {
        // Assert.require(description != null);
        super(description);
    }


    // Public methods

    /**
        @see PlackError#level
    */
    public int level()
    {
        return FATAL_ERROR_LEVEL;
    }
}
