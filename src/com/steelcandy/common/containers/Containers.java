/*
 Copyright (C) 2002-2010 by James MacKay.

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

package com.steelcandy.common.containers;

import com.steelcandy.common.debug.Assert;

import java.util.*;

/**
    A collection of container-related utility methods.

    @author James MacKay
    @version $Revision: 1.9 $
*/
public class Containers
{
    // Public constants

    /**
        The load factor used by HashMaps and HashSets by default when one
        isn't explicitly specified.
        <p>
        Note: this value doesn't seem to be publicly accessible in the code:
        if and when it does use that to set this constant's value instead.
        (The current value is currently obtained from the HashMap and HashSet
        javadocs.)
    */
    public static final float DEFAULT_HASH_LOAD_FACTOR = 0.75f;


    // Constructors

    /**
        This class should never be instantiated: it is just a place to put
        container-related utility methods that don't fit anywhere else.
    */
    private Containers()
    {
        // empty
    }


    // Public static methods

    /**
        @return an unmodifiable empty List
    */
    public static List emptyList()
    {
        return Collections.EMPTY_LIST;
    }

    /**
        @param obj an object
        @return an unmodifiable list whose sole element is 'obj'
        @see Collections#singletonList(Object)
    */
    public static List singletonList(Object obj)
    {
        return Collections.singletonList(obj);
    }


    /**
        @return an unmodifiable empty Map
    */
    public static Map emptyMap()
    {
        return Collections.EMPTY_MAP;
    }

    /**
        @param key a key
        @param value a value
        @return an unmodifiable map whose sole mapping is from 'key' to
        'value'
        @see Collections#singletonMap(Object, Object)
    */
    public static Map singletonMap(Object key, Object value)
    {
        return Collections.singletonMap(key, value);
    }

    /**
        Creates and returns a new HashMap with the default load factor that
        won't be rehashed until at least 'initialCapacity' entries have been
        added to it.
        <p>
        Note that this is different from the HashMap constructed using the
        HashMap(int) constructor, since it will rehash when the number of
        entries exceeds 'initialCapacity' times the load factor.

        @param initialCapacity the map's initial capacity
        @return a new HashMap that won't be rehashed until at least
        'initialCapacity' entries have been added to it
        @see #DEFAULT_HASH_LOAD_FACTOR
    */
    public static HashMap createHashMap(int initialCapacity)
    {
        Assert.require(initialCapacity >= 0);

        //Assert.ensure(result != null);
        return createHashMap(initialCapacity, DEFAULT_HASH_LOAD_FACTOR);
    }

    /**
        Creates and returns a new HashMap with load factor 'loadFactor' that
        won't be rehashed until at least 'initialCapacity' entries have been
        added to it.
        <p>
        Note that this is different from the HashMap constructed using the
        HashMap(int) constructor, since it will rehash when the number of
        entries exceeds 'initialCapacity' times the load factor.

        @param initialCapacity the map's initial capacity
        @param loadFactor the map's load factor
        @return a new HashMap that won't be rehashed until at least
        'initialCapacity' entries have been added to it
    */
    public static HashMap
        createHashMap(int initialCapacity, float loadFactor)
    {
        Assert.require(initialCapacity >= 0);
        Assert.require(loadFactor > 0.0);

        double cap = Math.ceil((((double) initialCapacity) * loadFactor));

        //Assert.ensure(result != null);
        return new HashMap((int) cap);
    }


    /**
        @return an unmodifiable empty Set
    */
    public static Set emptySet()
    {
        return Collections.EMPTY_SET;
    }

    /**
        @param obj an object
        @return an unmodifiable set whose sole element is 'obj'
        @see Collections#singleton(Object)
    */
    public static Set singletonSet(Object obj)
    {
        return Collections.singleton(obj);
    }

    /**
        Creates and returns a new HashSet with the default load factor that
        won't be rehashed until at least 'initialCapacity' entries have been
        added to it.
        <p>
        Note that this is different from the HashSet constructed using the
        HashSet(int) constructor, since it will rehash when the number of
        entries exceeds 'initialCapacity' times the load factor.

        @param initialCapacity the map's initial capacity
        @return a new HashSet that won't be rehashed until at least
        'initialCapacity' entries have been added to it
        @see #DEFAULT_HASH_LOAD_FACTOR
    */
    public static HashSet createHashSet(int initialCapacity)
    {
        Assert.require(initialCapacity >= 0);

        //Assert.ensure(result != null);
        return createHashSet(initialCapacity, DEFAULT_HASH_LOAD_FACTOR);
    }

    /**
        Creates and returns a new HashSet with load factor 'loadFactor' that
        won't be rehashed until at least 'initialCapacity' entries have been
        added to it.
        <p>
        Note that this is different from the HashSet constructed using the
        HashSet(int) constructor, since it will rehash when the number of
        entries exceeds 'initialCapacity' times the load factor.

        @param initialCapacity the map's initial capacity
        @param loadFactor the map's load factor
        @return a new HashSet that won't be rehashed until at least
        'initialCapacity' entries have been added to it
    */
    public static HashSet
        createHashSet(int initialCapacity, float loadFactor)
    {
        Assert.require(initialCapacity >= 0);
        Assert.require(loadFactor > 0.0);

        double cap = Math.ceil((((double) initialCapacity) * loadFactor));

        //Assert.ensure(result != null);
        return new HashSet((int) cap);
    }

    /**
        @param a an array
        @return a set of all of the unique elements in 'a'
    */
    public static Set createSet(Object[] a)
    {
        Assert.require(a != null);

        Set result;

        int len = a.length;
        if (len == 0)
        {
            result = emptySet();
        }
        else
        {
            result = new HashSet(len);
            for (int i = 0; i < len; i++)
            {
                result.add(a[i]);
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        @param s1 a set
        @param s2 another set
        @return the intersection of 's1' and 's2'
    */
    public static Set intersect(Set s1, Set s2)
    {
        Assert.require(s1 != null);
        Assert.require(s2 != null);

        // Iterate over the smallest set.
        Set first = s1;
        Set second = s2;
        if (second.size() < first.size())
        {
            first = s2;
            second = s1;
        }
        int initialSize = first.size();

        Set result = new HashSet(initialSize);

        Iterator iter = first.iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            if (second.contains(obj))
            {
                result.add(obj);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= s1.size());
        Assert.ensure(result.size() <= s2.size());
        return result;
    }

    /**
        @param s1 a set
        @param s2 another set
        @return the union of 's1' and 's2'
    */
    public static Set union(Set s1, Set s2)
    {
        Assert.require(s1 != null);
        Assert.require(s2 != null);

        Set result = new HashSet(s1);

        result.addAll(s2);

        Assert.ensure(result != null);
        Assert.ensure(result.size() >= s1.size());
        Assert.ensure(result.size() >= s2.size());
        return result;
    }
}
