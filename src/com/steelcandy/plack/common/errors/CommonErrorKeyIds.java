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

/**
    The interface that defines common ErrorKey IDs and related constants.

    @author  James MacKay
    @version $Revision: 1.13 $
*/
public interface CommonErrorKeyIds
{
    // Constants

    /** The first error key ID available for use by subclasses. */
    public static final int
        COMMON_FIRST_AVAILABLE_ID = 0;

    /**
        A value that is guaranteed not to be a valid error key ID (for use
        as a sentinel value, for example).
    */
    public static final int
        NON_ERROR_KEY_ID = COMMON_FIRST_AVAILABLE_ID - 1;
}
