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

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.tokens.AtriaTokenManager;

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructVisitor;  // javadocs only

/**
    The interface implemented by all Constructs specific to the Atria
    language.

    @author James MacKay
    @version $Revision: 1.8 $
*/
public interface AtriaConstruct
    extends Construct
{
    // Constants

    /** The Atria token manager instance. */
    public static final AtriaTokenManager
        TOKEN_MANAGER = AtriaTokenManager.instance();


    // Public methods

    /**
        Causes this construct to accept the specified visitor
        (i.e. by calling the appropriate <code>visit...()</code>
        method on it, passing itself as the parameter).

        @param visitor the visitor to accept
        @param handler the error handler to be used to handle
        any errors that occur while visiting
        @see Construct#accept(ConstructVisitor, ErrorHandler)
    */
    public void accept(AtriaConstructVisitor visitor,
                       ErrorHandler handler);
        // Assert.require(visitor != null);
        // Assert.require(handler != null);
}
