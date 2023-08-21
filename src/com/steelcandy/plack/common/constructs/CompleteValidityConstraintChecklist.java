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

import com.steelcandy.common.containers.Containers;

import java.util.Set;

/**
    The singleton ValidityConstraintChecklist that is always complete.
    <p>
    This class of ValidityConstraintChecklist can be used in production
    language processors to avoid the overhead of checking that validity
    constraints are properly checked.

    @author James MacKay
*/
public class CompleteValidityConstraintChecklist
    implements ValidityConstraintChecklist
{
    // Constants

    /** The single instance of this class. */
    private static final CompleteValidityConstraintChecklist
        _instance = new CompleteValidityConstraintChecklist();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static CompleteValidityConstraintChecklist instance()
    {
        return _instance;
    }

    /**
        Constructs the instance of this class.
    */
    private CompleteValidityConstraintChecklist()
    {
        // empty
    }


    // Public methods

    /**
        @see ValidityConstraintChecklist#markChecked(String)
    */
    public void markChecked(String constraintName)
    {
        Assert.require(constraintName != null);

        // empty
    }

    /**
        @see ValidityConstraintChecklist#isComplete
    */
    public boolean isComplete()
    {
        return true;
    }

    /**
        @see ValidityConstraintChecklist#uncheckedConstraintNames
    */
    public Set uncheckedConstraintNames()
    {
        Set result = Containers.emptySet();

        Assert.ensure(result != null);
        return result;
    }
}
