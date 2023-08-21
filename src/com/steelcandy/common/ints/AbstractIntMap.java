/*
 Copyright (C) 2009 by James MacKay.

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
    An abstract base class for IntMaps.

    @author James MacKay
*/
public abstract class AbstractIntMap
    extends AbstractIntCollection
    implements IntMap
{
    // Public methods

    /**
        @see Object#toString
    */
    public String toString()
    {
        return toString(this);
    }


    // Protected methods

    /**
        Copies all of the mappings from this map into 'dest'.

        @param dest the map to copy all of our mappings into
    */
    protected void copyAllMappingsTo(IntMap dest)
    {
        Assert.require(dest != null);

        IntIterator iter = keys();
        while (iter.hasNext())
        {
            int k = iter.next();
            dest.set(k, get(k));
        }
    }

    /**
        @param minKey the minimum value a map key can have
        @param key a potential map key
        @exception IndexOutOfBoundsException is thrown iff 'key' is less
        than 'minKey'
    */
    protected void checkKeyNotSmallerThan(int minKey, int key)
        throws IndexOutOfBoundsException
    {
        if (key < minKey)
        {
            throw new IndexOutOfBoundsException("minimum key is too " +
                "small: " + key + " < " + minKey);
        }
    }

    /**
        @param maxKey the maximum value a map key can have
        @param key a potential map key
        @exception IndexOutOfBoundsException is thrown iff 'key' is greater
        than 'maxKey'
    */
    protected void checkKeyNotLargerThan(int maxKey, int key)
    {
        if (key > maxKey)
        {
            throw new IndexOutOfBoundsException("maximum key is too " +
                "big: " + key + " > " + maxKey);
        }
    }
}
