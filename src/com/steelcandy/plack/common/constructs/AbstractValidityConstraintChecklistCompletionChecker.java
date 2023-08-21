/*
 Copyright (C) 2005-2008 by James MacKay.

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

import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.common.Resources;

import java.util.Iterator;

/**
    An abstract base class for classes that check that the main constructs
    in a program, along with all of their subconstructs, have had all of
    their validity constraints properly checked.

    @author  James MacKay
*/
public abstract class AbstractValidityConstraintChecklistCompletionChecker
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        UNCHECKED_CONSTRAINT_MSG =
            "UNCHECKED_CONSTRAINT_MSG";


    // Protected methods

    /**
        Reports and and all unchecked validity constraints that have been
        found by the specified visitor.

        @param v the validity constraint checklist completion construct
        visitor from which to get the unchecked validity constraints that
        we report, if any
        @param handler the error handler to use to report the unchecked
        validity constraints
    */
    protected void
        reportUncheckedConstraints(ChecklistCompletionConstructVisitor v,
                                   ErrorHandler handler)
    {
        Assert.require(v != null);
        Assert.require(handler != null);

        if (v.areUncheckedConstraints())
        {
            Iterator iter = v.uncheckedConstraintsInformation().iterator();
            while (iter.hasNext())
            {
                UncheckedValidityConstraint uncheckedConstraint =
                    (UncheckedValidityConstraint) iter.next();
                Construct c = uncheckedConstraint.construct();

                String msg = _resources.
                    getMessage(UNCHECKED_CONSTRAINT_MSG,
                               uncheckedConstraint.constraintName(),
                               constructDescription(c));

                // Note: we use 'null' as the class and object where the
                // internal error occurred since there's no way in general
                // to determine where a constraint should have been checked.
                handler.handle(new PlackInternalError(ErrorSeverityLevels.
                    NON_FATAL_ERROR_LEVEL, msg, c.sourceCode(),
                    errorConstructLocation(c), (Class) null, (Object) null));
            }
        }
    }

    /**
        @param c a construct that hasn't had one or more of its constraints
        checked
        @return the location to use as the location of 'c' when reporting
        the unchecked constraint(s)
    */
    protected SourceLocation errorConstructLocation(Construct c)
    {
        Assert.require(c != null);

        SourceLocation result = c.location();

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        @param c a construct
        @return a description of the type of construct 'c' is
    */
    protected abstract String constructDescription(Construct c);
        // Assert.require(c != null);
        // Assert.ensure(result != null);
}
