/*
 Copyright (C) 2005-2012 by James MacKay.

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
    The interface implemented by sets of ints, much like a java.util.Set
    with int as the type of its values.

    @author  James MacKay
    @version $Revision: 1.5 $
*/
public interface IntSet
    extends IntCollection
{
    // Public methods

    /**
        Adds the specified int to this set if it isn't already in this set.

        @param value the value to add to this set
        @return true iff 'value' was added to this set
    */
    public boolean add(int value);
        // Assert.ensure(has(value));

    /**
        Adds all of the ints in the specified set that aren't already in this
        set to this set.

        @param values the set of values to (potentially) add to this set
    */
    public void addAll(IntSet values);
        // Assert.require(values != null);
        // Assert.ensure(size() >= values.size());

    /**
        Removes the specified int from this set if it is in this set.

        @param value the value to remove from this set
        @return true iff 'value' was removed from this set
    */
    public boolean remove(int value);
        // Assert.ensure(has(value) == false);

    /**
        Indicates whether this set contains the specified value.

        @param value the value to test
        @return true if this set contains 'value', and false if it doesn't
    */
    public boolean has(int value);

    /**
        @return an iterator over the values in this set
    */
    public IntIterator iterator();
        // Assert.ensure(result != null);

    /**
        @return an array containing all of the ints in this collection, in
        the order that they're returned by an iterator returned by
        'iterator()'
        @see #iterator
    */
    public int[] toArray();
        // Assert.ensure(result.length == size());
}
