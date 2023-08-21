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
    An abstract base class for classes that can guard access to
    some resources.
    <p>
    Usually a guard is acquired or activated in its constructor. Thus
    an instance is usually used in the following way:
    <pre>
        Guard g = null;
        try
        {
            g = new ThreadGuard(someObject);

            // Various manipulations of 'someObject' here
        }
        finally
        {
            Guard.release(g);
        }
    </pre>

    @author James MacKay
    @version $Revision$
*/
public abstract class Guard
{
    // Private fields


    // Constructors

    /**
        Constructs a Guard.
    */
    public Guard()
    {
        // empty
    }


    // Public static methods

    /**
        Releases the specified guard iff it is non-null.

        @param g the guard to release iff it is non-null
        @see #release
    */
    public static void release(Guard g)
    {
        if (g != null)
        {
            g.release();
        }
    }


    // Abstract methods

    /**
        Releases this guard, permitting access to the resource that
        it guards.
    */
    public abstract void release();
}
