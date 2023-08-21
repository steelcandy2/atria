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
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.constructs.Construct;

import com.steelcandy.common.Resources;

/**
    A minimal abstract base class for classes that implement the
    ConstructVisitor interface. It does not help implement any construct
    visitor methods, but provides a common implementation of other construct
    visitor methods (such as visitAll(), for example).

    @author James MacKay
*/
public abstract class MinimalAbstractConstructVisitor
    implements ConstructVisitor
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DID_NOT_EXPECT_TO_VISIT_CONSTRUCT_MSG =
            "DID_NOT_EXPECT_TO_VISIT_CONSTRUCT_MSG";


    // Public methods

    /**
        @see ConstructVisitor#visitAll(ConstructList, ErrorHandler)
    */
    public void visitAll(ConstructList constructs, ErrorHandler handler)
    {
        Assert.require(constructs != null);
        Assert.require(handler != null);

        visitAll(constructs.iterator(), handler);
    }

    /**
        @see ConstructVisitor#visitAll(ConstructIterator, ErrorHandler)
    */
    public void visitAll(ConstructIterator iter, ErrorHandler handler)
    {
        Assert.require(iter != null);
        Assert.require(handler != null);

        while (iter.hasNext())
        {
            Construct c = iter.next();
            if (c != null)
            {
                c.accept(this, handler);
            }
            else
            {
                visitNull(handler);
            }
        }
    }


    // Protected methods

    /**
        The action performed on visiting null items.
        <p>
        This implementation does nothing.

        @param handler the error handler to use to handle any errors that
        occur while visiting
    */
    protected void visitNull(ErrorHandler handler)
    {
        // empty
    }


    // Utility methods

    /**
        Handles the situation where this visitor has visited the specified
        construct, but did not expect to visit such a construct (usually
        because the construct is of a type that the visitor is not prepared
        to handle).
        <p>
        This method is intended to be used in the implementation of default
        visitor methods.

        @param c the construct that this visitor did not expect to visit
        @param handler the error handler to use to handle the unexpected
        visiting of 'c'
        @see AbstractConstructVisitor#defaultVisit(Construct, ErrorHandler)
    */
    protected void handleUnexpectedVisit(Construct c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(DID_NOT_EXPECT_TO_VISIT_CONSTRUCT_MSG,
                       getClass().getName(), c.getClass().getName(),
                       String.valueOf(c.id()));
        handleInternalError(msg, c, handler);
    }

    /**
        Handles the internal error described by the specified message that
        occurred while visiting the specified construct.

        @param msg describes the internal error
        @param c the construct that was being visited when the internal
        error occurred
        @param handler the error handler to use to handle the internal error
    */
    protected void handleInternalError(String msg, Construct c,
                                       ErrorHandler handler)
    {
        Assert.require(msg != null);
        Assert.require(c != null);
        Assert.require(handler != null);

        MinimalAbstractConstruct.handleInternalError(ErrorSeverityLevels.
            NON_FATAL_ERROR_LEVEL, msg, c, this, handler);
    }
}
