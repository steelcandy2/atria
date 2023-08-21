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

import java.util.HashSet;
import java.util.Set;

/**
    An abstract base class for IntSets (including IntSortedSets).

    @author  James MacKay
*/
public abstract class AbstractIntSet
    extends AbstractIntCollection
    implements IntSet
{
    // Public methods

    /**
        @see IntCollection#size
    */
    public int size()
    {
        int result = backingSet().size();

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @see IntCollection#isEmpty
    */
    public boolean isEmpty()
    {
        return backingSet().isEmpty();
    }


    /**
        @see IntSet#add(int)
    */
    public boolean add(int value)
    {
        boolean result = backingSet().add(toInteger(value));

        Assert.ensure(has(value));
        return result;
    }

    /**
        @see IntSet#addAll(IntSet)
    */
    public void addAll(IntSet values)
    {
        Assert.require(values != null);

// TODO: is there a better implementation for this ???!!!???
        IntIterator iter = values.iterator();
        while (iter.hasNext())
        {
            add(iter.next());
        }

        Assert.ensure(size() >= values.size());
    }

    /**
        @see IntSet#remove(int)
    */
    public boolean remove(int value)
    {
        boolean result = backingSet().remove(toInteger(value));

        Assert.ensure(has(value) == false);
        return result;
    }

    /**
        @see IntSet#has(int)
    */
    public boolean has(int value)
    {
        return backingSet().contains(toInteger(value));
    }

    /**
        @see IntSet#iterator
    */
    public IntIterator iterator()
    {
        IntIterator result = new CollectionIterator(backingSet());

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see IntSet#toArray
    */
    public int[] toArray()
    {
        return toArray(iterator(), size());
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        return toString(this);
    }


    // Abstract methods

    /**
        @return our backing Set: that is, the Set that contains all of our
        items
    */
    protected abstract Set backingSet();
        // Assert.ensure(result != null);
}
