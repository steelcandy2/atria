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
import com.steelcandy.common.containers.Containers;

import java.util.Map;
import java.util.HashMap;

/**
    Maps ints to ints using a hash table.

    @author James MacKay
*/
public class IntHashMap
    extends AbstractIntMap
    implements IntMap
{
    // Constants

    /** The default capacity of an IntHashMap. */
    protected static final int DEFAULT_INITIAL_CAPACITY = 17;

    /** The default load factor of an IntHashMap. */
    protected static final float
        DEFAULT_LOAD_FACTOR = Containers.DEFAULT_HASH_LOAD_FACTOR;


    // Private fields

    /** The map from ints to ints. */
    private Map _map;


    // Constructors

    /**
        Constructs an empty IntHashMap with the default initial capacity and
        load factor.

        @see #DEFAULT_INITIAL_CAPACITY
        @see #DEFAULT_LOAD_FACTOR
    */
    public IntHashMap()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty IntHashMap with the specified initial capacity
        and the default load factor.

        @param initialCapacity the map's initial capacity
    */
    public IntHashMap(int initialCapacity)
    {
        //Assert.require(initialCapacity >= 0);
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty IntHashMap with the specified initial capacity
        and load factor.

        @param initialCapacity the map's initial capacity
        @param loadFactor the map's load factor
        @see Containers#createHashMap(int, float)
    */
    public IntHashMap(int initialCapacity, float loadFactor)
    {
        Assert.require(initialCapacity >= 0);
        Assert.require(loadFactor > 0.0f);

        _map = Containers.createHashMap(initialCapacity, loadFactor);
    }


    // Public methods

    /**
        @see IntCollection#size
    */
    public int size()
    {
        int result = _map.size();

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @see IntCollection#isEmpty
    */
    public boolean isEmpty()
    {
        return _map.isEmpty();
    }


    /**
        @see IntMap#get(int)
    */
    public int get(int key)
    {
        if (hasKey(key))
        {
            return toInt(_map.get(toInteger(key)));
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        @see IntMap#set(int, int)
    */
    public void set(int key, int value)
    {
        _map.put(toInteger(key), toInteger(value));

        Assert.ensure(hasKey(key));
        Assert.ensure(get(key) == value);
    }

    /**
        @see IntMap#hasKey(int)
    */
    public boolean hasKey(int key)
    {
        return _map.containsKey(toInteger(key));
    }

    /**
        @see IntMap#hasValue(int)
    */
    public boolean hasValue(int value)
    {
        return _map.containsValue(toInteger(value));
    }

    /**
        @see IntMap#keys
    */
    public IntIterator keys()
    {
        return new CollectionIterator(_map.keySet());
    }

    /**
        @see IntMap#values
    */
    public IntIterator values()
    {
        return new CollectionIterator(_map.values());
    }

    /**
        @see IntMap#toArrayMap(int, int, int)
    */
    public IntArrayMap toArrayMap(int minKey, int maxKey, int defaultValue)
        throws IndexOutOfBoundsException
    {
        Assert.require(maxKey >= minKey);

        IntArrayMap result = new IntArrayMap(minKey, maxKey, defaultValue);
        IntIterator iter = keys();
        while (iter.hasNext())
        {
            int k = iter.next();
            checkKeyNotSmallerThan(minKey, k);
            checkKeyNotLargerThan(maxKey, k);
            result.set(k, get(k));
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() >= size());
            // since 'result' may have additional default mappings
        return result;
    }
}
