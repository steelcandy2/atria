/*
 Copyright (C) 2003 by James MacKay.

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
    Represents a simple debug 'error'.

    @author James MacKay
    @version $Revision$
*/
public class DebugError
    extends AbstractSimplePlackError
{
    // Constructors

    /**
        Constructs a DebugError.

        @param description the description of the debug 'error'
    */
    public DebugError(String description)
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
        return DEBUG_LEVEL;
    }
}
