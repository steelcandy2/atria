/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.ints.OutOfUniqueValuesException;

/**
    The interface implemented by factory classes that create names that are
    unique with respect to all of the other names created by an instance
    between the times it is reset.
    <p>
    Note: often subclasses will create the names in such a way that the
    names it creates are guaranteed to be different from any other names
    that are in scope (for example, by including a prefix or suffix on
    each name that cannot be part of other names).

    @author  James MacKay
    @version $Revision: 1.1 $
    @see #reset
*/
public interface UniqueNameFactory
{
    // Public methods

    /**
        Resets this factory: it can now create names that are the same as
        ones it has created before the call to this method
    */
    public void reset();

    /**
        @return a name that is different from any others that this factory
        has returned since it was created or last had reset() called on it,
        whichever was more recent
        @exception OutOfUniqueValuesException if there are no more new names
        left to return
        @see #reset
    */
    public String newName()
        throws OutOfUniqueValuesException;
        // Assert.ensure(result != null);
        // Assert.ensure(result.length() > 0);
}
