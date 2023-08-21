/*
 Copyright (C) 2014 by James MacKay.

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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by sorted sets of ints, much like a
    java.util.SortedSet with int as the type of its values.
    <p>
    Note: like a SortedSet, an iterator on an instance that is created using
    our iterator() method is guaranteed to return the instance's items in
    ascending order.

    @author  James MacKay
*/
public interface IntSortedSet
    extends IntSet
{
    // Public methods

    /**
        @return the first - that is, the smallest - int in this set
    */
    public int first();
        // Assert.require(isEmpty() == false);

    /**
        @return the last - that is, the largest - int in this set
    */
    public int last();
        // Assert.require(isEmpty() == false);
}
