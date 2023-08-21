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

/**
    Represents an unchecked validity constraint.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class UncheckedValidityConstraint
{
    // Private fields

    /** The construct whose unchecked validity constraint this is. */
    private Construct _construct;

    /** The name of the unchecked validity constraint. */
    private String _constraintName;


    // Constructors

    /**
        Constructs an UncheckedValidityConstraint that represent the validity
        constraint with the specified name being unchecked on the specified
        construct.

        @param c the construct on which the constraint was not checked
        @param constraintName the name of the validity constraint that was
        not checked on 'c'
    */
    public UncheckedValidityConstraint(Construct c, String constraintName)
    {
        Assert.require(c != null);
        Assert.require(constraintName != null);

        _construct = c;
        _constraintName = constraintName;
    }


    // Public methods

    /**
        @return the construct with the unchecked validity constraint
    */
    public Construct construct()
    {
        return _construct;
    }

    /**
        @return the name of the unchecked constraint
    */
    public String constraintName()
    {
        return _constraintName;
    }
}
