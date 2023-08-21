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
    A singleton ErrorHandler that should never be used to handle any errors.
    <p>
    Instances of this class are useful when an ErrorHandler is required (to
    be passed into a ConstructVisitor, for example) but should never actually
    get used (because the visitor should never cause any errors, for
    example).

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class UnusableErrorHandler
    extends AbstractUniformErrorHandler
{
    // Constants

    /** The single instance of this class. */
    private static final UnusableErrorHandler
        _instance = new UnusableErrorHandler();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static UnusableErrorHandler instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private UnusableErrorHandler()
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

        Assert.unreachable();
            // since this handler should never have to handle any errors
    }
}
