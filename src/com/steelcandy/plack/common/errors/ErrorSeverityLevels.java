/*
 Copyright (C) 2002-2004 by James MacKay.

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
    The interface that defines all of the error severity levels.
    <p>
    Note: the severity levels are zero-based and consecutive, so they can be
    used conveniently as array indices.

    @author James MacKay
*/
public interface ErrorSeverityLevels
{
    // Error severity level and related constants

    /** The minimum valid severity level. */
    public static final int
        MINIMUM_ERROR_SEVERITY_LEVEL = 0;


    /** The severity level of debug messages. */
    public static final int
        DEBUG_LEVEL = MINIMUM_ERROR_SEVERITY_LEVEL;

    /** The severity level of informational messages. */
    public static final int
        INFO_LEVEL = DEBUG_LEVEL + 1;

    /** The severity level of notices. */
    public static final int
        NOTICE_LEVEL = INFO_LEVEL + 1;

    /** The severity level of warnings. */
    public static final int
        WARNING_LEVEL = NOTICE_LEVEL + 1;

    /** The severity level of non-fatal errors. */
    public static final int
        NON_FATAL_ERROR_LEVEL = WARNING_LEVEL + 1;

    /** The severity level of fatal errors. */
    public static final int
        FATAL_ERROR_LEVEL = NON_FATAL_ERROR_LEVEL + 1;


    /** The maximum valid severity level. */
    public static final int
        MAXIMUM_ERROR_SEVERITY_LEVEL = FATAL_ERROR_LEVEL;

    /** The total number of severity levels. */
    public static final int NUMBER_OF_ERROR_SEVERITY_LEVELS =
        MAXIMUM_ERROR_SEVERITY_LEVEL - MINIMUM_ERROR_SEVERITY_LEVEL + 1;
}
