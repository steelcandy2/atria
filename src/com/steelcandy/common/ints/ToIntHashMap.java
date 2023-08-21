/*
 Copyright (C) 2005-2015 by James MacKay.

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

import java.util.*;

/**
    Maps Objects to ints using a hash table.

    @author James MacKay
*/
public class ToIntHashMap
    extends AbstractIntCollection
    implements ToIntMap
{
    // Constants

    /** The default capacity of a ToIntHashMap. */
    protected static final int
        DEFAULT_INITIAL_CAPACITY = IntHashMap.DEFAULT_INITIAL_CAPACITY;

    /** The default load factor of a ToIntHashMap. */
    protected static final float
        DEFAULT_LOAD_FACTOR = IntHashMap.DEFAULT_LOAD_FACTOR;


    // Private fields

    /** The map from Objects to ints. */
    private Map _map;


    // Constructors

    /**
        Constructs an empty ToIntHashMap with the default initial capacity
        and load factor.

        @see #DEFAULT_INITIAL_CAPACITY
        @see #DEFAULT_LOAD_FACTOR
    */
    public ToIntHashMap()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty ToIntHashMap with the specified initial capacity
        and the default load factor.

        @param initialCapacity the map's initial capacity
    */
    public ToIntHashMap(int initialCapacity)
    {
        //Assert.require(initialCapacity >= 0);
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty ToIntHashMap with the specified initial capacity
        and load factor.

        @param initialCapacity the map's initial capacity
        @param loadFactor the map's load factor
        @see Containers#createHashMap(int, float)
    */
    public ToIntHashMap(int initialCapacity, float loadFactor)
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
        @see ToIntMap#get(Object)
    */
    public int get(Object key)
    {
        if (hasKey(key))
        {
            return toInt(_map.get(key));
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        @see ToIntMap#find(Object, int)
    */
    public int find(Object key, int defaultValue)
    {
        int result = defaultValue;

        if (hasKey(key))
        {
            result = toInt(_map.get(key));
        }

        return result;
    }

    /**
        @see ToIntMap#set(Object, int)
    */
    public void set(Object key, int value)
    {
        _map.put(key, toInteger(value));

        Assert.ensure(hasKey(key));
        Assert.ensure(get(key) == value);
    }

    /**
        @see ToIntMap#hasKey(Object)
    */
    public boolean hasKey(Object key)
    {
        return _map.containsKey(key);
    }

    /**
        @see ToIntMap#hasValue(int)
    */
    public boolean hasValue(int value)
    {
        return _map.containsValue(toInteger(value));
    }

    /**
        @see ToIntMap#keys
    */
    public Iterator keys()
    {
        return keySet().iterator();
    }

    /**
        @see ToIntMap#keySet
    */
    public Set keySet()
    {
        return _map.keySet();
    }

    /**
        @see ToIntMap#values
    */
    public IntIterator values()
    {
        return new CollectionIterator(_map.values());
    }

    /**
        @see ToIntMap#keysSortedByAscendingValues
    */
    public List keysSortedByAscendingValues()
    {
        ArrayList result = new ArrayList(_map.keySet());

        Collections.sort(result, new AscendingValuesComparator(this));

        Assert.ensure(result != null);
        Assert.ensure(result.size() == size());
        return result;
    }

    /**
        @see ToIntMap#keysSortedByDescendingValues
    */
    public List keysSortedByDescendingValues()
    {
        ArrayList result = new ArrayList(size());

        Collections.sort(result, new DescendingValuesComparator(this));

        Assert.ensure(result != null);
        Assert.ensure(result.size() == size());
        return result;
    }


    /**
        @see Object#toString
    */
    public String toString()
    {
        return toString(this);
    }
}


// Private classes

/**
    An abstract base class for comparators that compare a ToIntHashMap's keys
    based on their corresponding values.
*/
abstract class AbstractValuesComparator
    implements Comparator
{
    // Private fields

    /**
        The ToIntHashMap that maps the keys we compare to their corresponding
        values.
    */
    private ToIntHashMap _map;


    // Constructors

    /**
        Constructs an AbstractValuesComparator from the map that maps keys
        to their corresponding values.

        @param m the map
    */
    public AbstractValuesComparator(ToIntHashMap m)
    {
        Assert.require(m != null);

        _map = m;
    }


    // Protected methods

    /**
        @param k1 the first map key
        @param k2 the second map key
        @return a negative, zero or positive value iff the value that 'k1'
        maps to is less than, equal to or greater than the value that 'k2'
        maps to
    */
    protected int compareByValues(Object obj1, Object obj2)
    {
        // 'obj1' can be null
        // 'obj2' can be null

        return _map.get(obj1) - _map.get(obj2);
    }
}

/**
    The class of comparator that compares a ToIntHashMap's keys based on
    their corresponding values: a key 'k' is considered less than, equal to
    or greater than another key iff the value that 'k' is mapped to is less
    than, equal to or greater than the value that the other key maps to.

    @author James MacKay
*/
class AscendingValuesComparator
    extends AbstractValuesComparator
{
    // Constructors

    /**
        Constructs an AscendingValuesComparator from the map that maps keys
        to their corresponding values.

        @param m the map
    */
    public AscendingValuesComparator(ToIntHashMap m)
    {
        super(m);
        Assert.require(m != null);
    }


    // Protected methods

    /**
        @see Comparator#compare(Object, Object)
    */
    public int compare(Object obj1, Object obj2)
    {
        return compareByValues(obj1, obj2);
    }
}

/**
    The class of comparator that compares a ToIntHashMap's keys based on
    their corresponding values: a key 'k' is considered less than, equal to
    or greater than another key iff the value that 'k' is mapped to is
    greater than, equal to or less than the value that the other key maps to.

    @author James MacKay
*/
class DescendingValuesComparator
    extends AbstractValuesComparator
{
    // Constructors

    /**
        Constructs a DescendingValuesComparator from the map that maps keys
        to their corresponding values.

        @param m the map
    */
    public DescendingValuesComparator(ToIntHashMap m)
    {
        super(m);
        Assert.require(m != null);
    }


    // Public methods

    /**
        @see Comparator#compare(Object, Object)
    */
    public int compare(Object obj1, Object obj2)
    {
        return -compareByValues(obj1, obj2);  // negated
    }
}
