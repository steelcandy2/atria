/*
 Copyright (C) 2002-2004 by James MacKay.

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

import java.util.Set;

/**
    The interface implemented by all classes that represent a checklist of a
    construct's validity constraints.
    <p>
    When one of a construct's validity constraints has been checked
    (regardless of whether the construct satisfied the constraint or not)
    then the markChecked() method should be called (with the proper
    arguments) on the construct's validity constraint checklist. Then when
    the construct's validity constraints have ostensibly all been checked
    the construct's validity constraint checklist's isComplete() method can
    be called to verify that all of the construct's validity constraints were
    indeed checked.

    @author James MacKay
    @version $Revision: 1.4 $
*/
public interface ValidityConstraintChecklist
{
    // Public methods

    /**
        Called to mark the constraint with the specified name as having been
        checked for this checklist's construct.

        @param constraintName the name of the validity constraint to mark as
        having been checked
        @exception ValidityConstraintAlreadyCheckedException thrown if the
        constraint with the specified name has already been checked on this
        checklist's construct
        @exception IllegalArgumentException thrown if there is no validity
        constraint in this checklist with the specified name
    */
    public void markChecked(String constraintName)
        throws ValidityConstraintAlreadyCheckedException,
               IllegalArgumentException;
        // Assert.require(constraintName != null);

    /**
        Indicates whether all of the constraints in this checklist were
        checked.

        @return true iff all of the constraints in this checklist were
        checked
    */
    public boolean isComplete();

    /**
        @return the names of the validity constraints in this checklist that
        have not been checked yet: each item in the returned set is a String
    */
    public Set uncheckedConstraintNames();
        // Assert.ensure(result != null);
}
