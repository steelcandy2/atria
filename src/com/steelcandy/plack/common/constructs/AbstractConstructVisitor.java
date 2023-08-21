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
    An abstract base class for classes that implement the ConstructVisitor
    interface.

    @author James MacKay
*/
public abstract class AbstractConstructVisitor
    extends MinimalAbstractConstructVisitor
{
    // Abstract methods

    /**
        The default action performed on visiting items.

        @param item the item being visited
        @param handler the error handler to use to handle any errors that
        occur while visiting
        @see MinimalAbstractConstructVisitor#handleUnexpectedVisit(Construct, ErrorHandler)
    */
    protected abstract void defaultVisit(Construct item,
                                         ErrorHandler handler);
        // Assert.require(item != null);
        // Assert.require(handler != null);
}
