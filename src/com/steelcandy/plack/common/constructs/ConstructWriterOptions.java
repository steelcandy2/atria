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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

/**
    A class that represents the options used to configure a ConstructWriter.

    @author James MacKay
    @version $Revision: 1.1 $
    @see ConstructWriter
*/
public interface ConstructWriterOptions
{
    // Show option constants

    /** Indicates that a part of a construct should never be shown. */
    public static final ShowOption
        HIDE = ConstructWriterConfiguration.HIDE;

    /**
        Indicates that a part of a construct should be shown if it is
        present.
    */
    public static final ShowOption
        SHOW = ConstructWriterConfiguration.SHOW;

    /**
        Indicates that a part of a construct should be shown, even if it
        isn't present.
    */
    public static final ShowOption
        ALWAYS_SHOW = ConstructWriterConfiguration.ALWAYS_SHOW;


    // Inner interfaces

    /**
        The interface implemented by each of the options indicating how to
        show a part of a construct.
    */
    public static interface ShowOption
    {
        // empty
    }
}
