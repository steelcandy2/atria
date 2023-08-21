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

import com.steelcandy.common.Resources;
import com.steelcandy.common.NoSuchItemException;

/**
    Maps ints to ints using an array.
    <p>
    An IntArrayMap must map all of the keys in a consecutive sequence of
    ints, and only those keys in the range of keys specified when an
    IntArrayMap can ever be mapped by the IntArrayMap.

    @author James MacKay
*/
public class IntArrayMap
    extends AbstractIntMap
    implements IntMap
{
    // Constants

    /** The resources used by this class. */
    private static final Resources
        _resources = IntResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        KEY_TOO_SMALL_MSG =
            "KEY_TOO_SMALL_MSG";
    private static final String
        KEY_TOO_LARGE_MSG =
            "KEY_TOO_LARGE_MSG";


    // Private fields

    /** The array that maps keys to values. */
    private int[] _array;

    /** The smallest key value that this map maps. */
    private int _minKey;

    /** The largest key value that this map maps. */
    private int _maxKey;


    // Constructors

    /**
        Constructs an IntArrayMap from the minimum and maximum keys that the
        IntArrayMap can map, and the value to which the IntArrayMap maps keys
        until an alternate mapping is specified for a key.

        @param minKey the smallest key the map can have
        @param maxKey the largest key the map can have
        @param defaultValue the value that the map will map keys to until an
        altermate mapping is specified
        @see IntMap#set(int, int)
    */
    public IntArrayMap(int minKey, int maxKey, int defaultValue)
    {
        Assert.require(maxKey >= minKey);

        initialize(minKey, maxKey);

        // Initialize the array so that all keys map to the defaultValue.
        int len = _array.length;
        for (int i = 0; i < len; i++)
        {
            _array[i] = defaultValue;
        }
    }

    /**
        Constructs an IntArrayMap from the minimum and maximum keys that the
        IntArrayMap can map. It will map keys to zero until an alternate
        mapping is specified for a key.

        @param minKey the smallest key the map can have
        @param maxKey the largest key the map can have
        @see IntMap#set(int, int)
    */
    public IntArrayMap(int minKey, int maxKey)
    {
        Assert.require(maxKey >= minKey);

        initialize(minKey, maxKey);
        // Note: the elements of a newly-created int array are all zero.
    }


    // Initialization methods

    /**
        Initializes this (newly-created) IntArrayMap.

        @param minKey the smallest key that this map maps
        @param maxKey the largest key that this map maps
    */
    private void initialize(int minKey, int maxKey)
    {
        _minKey = minKey;
        _maxKey = maxKey;

        _array = new int[_maxKey - _minKey + 1];
    }


    // Public methods

    /**
        @see IntCollection#size
    */
    public int size()
    {
        int result = _array.length;

        Assert.ensure(result >= 0);
        return result;
    }


    /**
        @see IntMap#get(int)
    */
    public int get(int key)
    {
        checkKey(key);
        return _array[key - _minKey];
    }

    /**
        @see IntMap#set(int, int)
    */
    public void set(int key, int value)
    {
        checkKey(key);
        _array[key - _minKey] = value;

        Assert.ensure(hasKey(key));
        Assert.ensure(get(key) == value);
    }

    /**
        @see IntMap#hasKey(int)
    */
    public boolean hasKey(int key)
    {
        return (_minKey <= key) && (key <= _maxKey);
    }

    /**
        @see IntMap#hasValue(int)
    */
    public boolean hasValue(int value)
    {
        boolean result = false;
        for (int i = 0; i < _array.length; i++)
        {
            if (_array[i] == value)
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
        @see IntMap#keys
    */
    public IntIterator keys()
    {
        return new ConsecutiveIntIterator(_minKey, _maxKey);
    }

    /**
        @see IntMap#values
    */
    public IntIterator values()
    {
        return new IntArrayIterator(_array);
    }

    /**
        @see IntMap#toArrayMap(int, int, int)
    */
    public IntArrayMap toArrayMap(int minKey, int maxKey, int defaultValue)
        throws IndexOutOfBoundsException
    {
        Assert.require(maxKey >= minKey);

        checkKeyNotSmallerThan(_minKey, minKey);
        checkKeyNotLargerThan(_maxKey, maxKey);

        IntArrayMap result = new IntArrayMap(minKey, maxKey, defaultValue);
        copyAllMappingsTo(result);

        Assert.ensure(result != null);
        Assert.ensure(result.size() >= size());
            // since 'result' may have additional default mappings
        return result;
    }


    // Protected methods

    /**
        Checks that the specified key is mapped by this map, and throws a
        NoSuchItemException if it isn't.

        @param key the key to check
        @exception NoSuchItemException thrown if the key is not mapped by
        this map
    */
    protected void checkKey(int key)
        throws NoSuchItemException
    {
        if (key < _minKey)
        {
            String msg = _resources.getMessage(KEY_TOO_SMALL_MSG,
                            toInteger(key), toInteger(_minKey));
            throw new NoSuchItemException(msg);
        }
        else if (key > _maxKey)
        {
            String msg = _resources.getMessage(KEY_TOO_LARGE_MSG,
                            toInteger(key), toInteger(_maxKey));
            throw new NoSuchItemException(msg);
        }
    }
}
