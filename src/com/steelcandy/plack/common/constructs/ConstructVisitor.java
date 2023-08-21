/*
 Copyright (C) 2001-2004 by James MacKay.

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

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The interface implemented by all Construct visitors.
    <p>
    This interface provides few <code>visit...()</code> methods since the
    different types of Constructs it is to visit (and hence most of the
    different <code>visit...()</code> methods it is to provide) are
    language-specific.

    @author James MacKay
    @see Construct
    @see Construct#accept
*/
public interface ConstructVisitor
{
    // Public methods

    /**
        Causes this visitor to visit all of the constructs in the specified
        list, in order.

        @param constructs the list of constructs that this visitor is to
        visit
        @param handler the error handler to use to handle any errors that
        occur while visiting
        @see #visitAll(ConstructIterator, ErrorHandler)
    */
    public void visitAll(ConstructList constructs, ErrorHandler handler);
        // Assert.require(constructs != null);
        // Assert.require(handler != null);

    /**
        Causes this visitor to visit all of the constructs returned by the
        specified iterator, in the order that the iterator returns them.

        @param iter the iterator that returns the constructs that this
        visitor is to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting
        @see #visitAll(ConstructList, ErrorHandler)
    */
    public void visitAll(ConstructIterator iter, ErrorHandler handler);
        // Assert.require(iter != null);
        // Assert.require(handler != null);
}
