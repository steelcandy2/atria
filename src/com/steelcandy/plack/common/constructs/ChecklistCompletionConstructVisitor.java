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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import java.util.List;

/**
    The interface implemented by construct visitors that check which
    constructs' validity constraints have not been marked as having
    been checked in their validity constraint checklists.
    <p>
    Note: direct subinterfaces usually extend both this interface and their
    language's base construct visitor interface.

    @author James MacKay
*/
public interface ChecklistCompletionConstructVisitor
{
    // Public methods

    /**
        @return true if there is at least one validity constraint that has
        not been checked, and false if all of the constructs' validity
        constraints have been marked as checked on their checklists
    */
    public boolean areUncheckedConstraints();

    /**
        @return a list of UncheckedValidityConstraint objects, each of which
        describes one of the validity constraints that had not been checked,
        in the order that they were discovered
        @see UncheckedValidityConstraint
    */
    public List uncheckedConstraintsInformation();
}
