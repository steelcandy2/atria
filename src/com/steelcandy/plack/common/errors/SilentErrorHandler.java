/*
 Copyright (C) 2003-2015 by James MacKay.

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
    A singleton ErrorHandler that handles all errors by ignoring them.
    <p>
    Note: instances of this class don't keep track of how many errors they've
    handled, and so can't be used to determine whether any errors have
    occurred: all of its error counts will always be zero. Use a
    SilentCountingErrorHandler instead if you want to be able to determine
    whether any errors have occurred.

    @author James MacKay
    @see SilentCountingErrorHandler
*/
public class SilentErrorHandler
    extends AbstractUniformErrorHandler
{
    // Constants

    /** The single instance of this class. */
    private static final SilentErrorHandler
        _instance = new SilentErrorHandler();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static SilentErrorHandler instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private SilentErrorHandler()
    {
        // empty
    }


    // Protected methods

    /**
        @see AbstractUniformErrorHandler#handleUniformly(PlackError)
    */
    protected void handleUniformly(PlackError error)
    {
        Assert.require(error != null);

        // empty - since we ignore it
    }
}
