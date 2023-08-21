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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

/**
    The class of object that checks that the main constructs in an Atria
    program, along with all of their subconstructs, have had all of their
    validity constraints properly checked.

    @author  James MacKay
*/
public class AtriaValidityConstraintChecklistCompletionChecker
    extends AbstractValidityConstraintChecklistCompletionChecker
{
    // Constants

    /** The single AtriaValidityConstraintChecklistFactory instance. */
    private static final AtriaValidityConstraintChecklistFactory
        FACTORY = AtriaValidityConstraintChecklistFactory.instance();


    // Constructors

    /**
        Constructs an AtriaValidityConstraintChecklistCompletionChecker.
    */
    public AtriaValidityConstraintChecklistCompletionChecker()
    {
        // empty
    }


    // Public methods

    /**
        Checks that the specified document and all of its subconstructs have
        had all of their validity constraints correctly checked.

        @param c the document construct to check
        @param handler the error handler to use to report any constraints
        that have been improperly checked or not checked at all
    */
    public void check(AtriaConstructManager.Document c,
                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        if (areChecking())
        {
            AtriaChecklistCompletionConstructVisitor v = createVisitor();
            c.accept(v, handler);
            reportUncheckedConstraints(v, handler);
        }
    }

    /**
        Checks that the specified element and all of its subconstructs have
        had all of their validity constraints correctly checked.

        @param c the element construct to check
        @param handler the error handler to use to report any constraints
        that have been improperly checked or not checked at all
    */
    public void check(AtriaConstructManager.Element c,
                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        if (areChecking())
        {
            AtriaChecklistCompletionConstructVisitor v = createVisitor();
            c.accept(v, handler);
            reportUncheckedConstraints(v, handler);
        }
    }


    // Protected methods

    /**
        @see AtriaValidityConstraintChecklistFactoryBase#areCheckingValidityConstraintChecklists
    */
    protected boolean areChecking()
    {
        return FACTORY.areCheckingValidityConstraintChecklists();
    }

    /**
        @return the visitor to use to check that all validity constraints
        have been properly checked on a construct and all of its
        subconstructs
    */
    protected AtriaChecklistCompletionConstructVisitor createVisitor()
    {
        AtriaChecklistCompletionConstructVisitor result =
            FACTORY.createPreorderChecklistCompletionVisitor();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see AbstractValidityConstraintChecklistCompletionChecker#constructDescription(Construct)
    */
    protected String constructDescription(Construct c)
    {
        Assert.require(c != null);

        String result =
            AtriaConstructManager.instance().idToDescription(c.id());

        Assert.ensure(result != null);
        return result;
    }
}
