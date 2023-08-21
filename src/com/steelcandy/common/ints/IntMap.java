/*
 Copyright (C) 2001-2009 by James MacKay.

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

import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by maps that map ints to ints, much like a
    java.util.Map with int as the type of its keys and values.

    @author James MacKay
    @see java.util.Map
*/
public interface IntMap
    extends IntCollection
{
    // Public methods

    /**
        Returns the value that this map maps the specified key to.

        @param key the key to be mapped by this map
        @return the value that this map maps the key to
        @exception NoSuchItemException thrown if this map doesn't map the
        specified key (i.e. if <code>hasKey(key) == false</code>)
    */
    public int get(int key)
        throws NoSuchItemException;

    /**
        Sets the value that this map maps the specified key to to be the
        specified value, replacing any existing mapping from that key.

        @param key the key whose mapping is to be set/changed
        @param value the value to which the key now maps
    */
    public void set(int key, int value);
        // Assert.ensure(hasKey(key));
        // Assert.ensure(get(key) == value);

    /**
        Indicates whether this map will map the specified key to a value.

        @param key the key to test
        @return true if this map will map the key to a value, and false if it
        won't
    */
    public boolean hasKey(int key);

    /**
        Indicates whether this map will map at least one key to the specified
        value.

        @param value the value to test
        @return true if this map will map at least one key to the value, and
        false if it won't
    */
    public boolean hasValue(int value);

    /**
        @return an iterator over the keys that this map maps into values
    */
    public IntIterator keys();
        // Assert.ensure(result != null);

    /**
        @return an iterator over the values to which this map maps keys. A
        given value will be returned once by the iterator for every key that
        this map maps to that value
    */
    public IntIterator values();
        // Assert.ensure(result != null);


    /**
        @param minKey the smallest key the map can have
        @param maxKey the largest key the map can have
        @param defaultValue the value that the map will map keys to until an
        altermate mapping is specified
        @return an IntArrayMap that contains the mappings in this map
        @exception IndexOutOfBoundsException is thrown if one or more of
        this map's keys are less than 'minKey' or greater than 'maxKey'
    */
    public IntArrayMap toArrayMap(int minKey, int maxKey, int defaultValue)
        throws IndexOutOfBoundsException;
        //Assert.require(maxKey >= minKey);
        //Assert.ensure(result != null);
        //Assert.ensure(result.size() >= size());
            // since 'result' may have additional default mappings
}
