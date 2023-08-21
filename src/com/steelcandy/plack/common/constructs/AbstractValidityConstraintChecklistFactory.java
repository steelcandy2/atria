/*
 Copyright (C) 2002-2005 by James MacKay.

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

import com.steelcandy.plack.common.semantic.ValidityConstraintAlreadyCheckedException;

import com.steelcandy.common.Resources;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
    An abstract base class for factories that create the validity constraint
    checklists for various types of constructs.
    <p>
    Subclasses' factory methods should be implemented using code like this:
    <pre>
        ValidityConstraintChecklist result = createCompletedChecklist();

        if (result == null)
        {
            result = new XxxValidityConstraintCheckList();
        }

        Assert.ensure(result != null);
        return result;
    </pre>

    @author James MacKay
    @version $Revision: 1.7 $
*/
public abstract class AbstractValidityConstraintChecklistFactory
{
    // Constants

    /** The pre-completed checklist instance. */
    private static final
        CompleteValidityConstraintChecklist COMPLETED_CHECKLIST =
            CompleteValidityConstraintChecklist.instance();


    // Private fields

    /**
        Indicates whether this factory should always just create
        pre-completed checklists.
    */
    private boolean _createCompletedChecklists;


    // Constructors

    /**
        Constructs an AbstractValidityConstraintChecklistFactory.

        @param doCreateCompletedChecklists is true iff the factory is to
        always create checklists that are already/always completed
    */
    protected AbstractValidityConstraintChecklistFactory(
                                        boolean doCreateCompletedChecklists)
    {
        _createCompletedChecklists = doCreateCompletedChecklists;
    }


    // Protected methods

    /**
        @return a pre-completed validity constraint checklist if that is
        what this factory is to create: otherwise returns null
    */
    protected ValidityConstraintChecklist createCompletedChecklist()
    {
        ValidityConstraintChecklist result = null;

        if (_createCompletedChecklists)
        {
            result = COMPLETED_CHECKLIST;
        }

        // 'result' may be null
        return result;
    }


    // Protected static methods

    /**
        Appends an UncheckedValidityConstraint to the end of the specified
        list for each of the specified construct's unchecked validity
        constraints.

        @param c the construct to check for unchecked constraints
        @param uncheckedInfo the list to which to add information about each
        of c's unchecked constraints: each item added will be an
        UncheckedValidityConstraint object
        @exception MissingConstructCorrectnessDataException thrown iff 'c'
        does not contain its correctness data
    */
    protected static void
        addUncheckedConstraints(Construct c, List uncheckedInfo)
        throws MissingConstructCorrectnessDataException
    {
        ValidityConstraintChecklist checklist = c.validityChecklist();
        if (checklist.isComplete() == false)
        {
            Iterator iter =
                checklist.uncheckedConstraintNames().iterator();
            while (iter.hasNext())
            {
                String name = (String) iter.next();
                uncheckedInfo.
                    add(new UncheckedValidityConstraint(c, name));
            }
        }
    }
}
