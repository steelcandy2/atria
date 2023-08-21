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

package com.steelcandy.common.threads;

import com.steelcandy.common.debug.Assert;

/**
    The singleton class of Guard that does nothing to protect its
    resource.

    @author James MacKay
*/
public class NoGuard
    extends Guard
{
    // Constants

    /** The single instance of this class. */
    private static final NoGuard _instance = new NoGuard();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static NoGuard instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private NoGuard()
    {
        // empty
    }


    // Public methods

    /**
        @see Guard#release
    */
    public void release()
    {
        // empty
    }
}
