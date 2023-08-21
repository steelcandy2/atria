/*
 Copyright (C) 2015 by James MacKay.

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
    Represents Maps that keep only the most recently used items, discarding
    the oldest ones.

    @author James MacKay
*/
public class MostRecentlyUsedCacheMap
    extends LinkedHashMap
{
    // Constants

    /** An instance's default load factor. */
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;


    // Private fields

    /** The maximum number of items that can we can contain. */
    private int _maxSize;


    // Constructors

    /**
        Constructs a MostRecentlyUsedCacheMap that can contain at most
        'maxSize' items: if more items are added then the least-recently
        used ones are removed from the map.

        @param maxSize the maximum number of items the map can contain
        @see MostRecentlyUsedCacheMap#MostRecentlyUsedCacheMap(int, float)
    */
    public MostRecentlyUsedCacheMap(int maxSize)
    {
        this(maxSize, DEFAULT_LOAD_FACTOR);
        Assert.require(maxSize >= 0);
    }

    /**
        Constructs a MostRecentlyUsedCacheMap that can contain at most
        'maxSize' items, and has a load factor of 'factor'.

        @param maxSize the maximum number of items the map can contain
        @param factor the map's load factor
        @see MostRecentlyUsedCacheMap#MostRecentlyUsedCacheMap(int)
        @see LinkedHashMap#LinkedHashMap(int, float, boolean)
    */
    public MostRecentlyUsedCacheMap(int maxSize, float factor)
    {
        super(maxSize, factor, true);
            // 'true' means that the map keeps items in the order that they
            // were last accessed, as opposed to the order in which they
            // were inserted
        Assert.require(maxSize >= 0);
        Assert.require(factor > 0.0f);

        _maxSize = maxSize;
    }


    // Public methods

    /**
        @see Map#get(Object)
    */
/*
    public Object get(Object key)
    {
        Object result = super.get(key);
System.err.println("||| cache.get(" + key + ")" + ((result == null) ? " [not found]" : ""));

        return result;  // may be null
    }
*/

    /**
        @return the maximum number of items that we can contain
    */
    public int maximumSize()
    {
        Assert.ensure(_maxSize >= 0);
        return _maxSize;
    }

    /**
        Sets the maximum number of items that we can contain to 'newMaxSize'.

        @param newMaxSize the new maximum number of items that we can contain
    */
    public void setMaximumSize(int newMaxSize)
    {
        Assert.require(newMaxSize >= 0);

        int numToRemove = (size() - newMaxSize);
        if (numToRemove > 0)
        {
            removeOldestEntries(numToRemove);
        }
        _maxSize = newMaxSize;

        Assert.ensure(maximumSize() == newMaxSize);
    }


    // Protected methods

    /**
        @see LinkedHashMap#removeEldestEntry(Map.Entry)
    */
    protected boolean removeEldestEntry(Map.Entry eldest)
    {
//System.err.println("=== size = " + size() + ", max # items = " + _maxItems);
        return (size() > maximumSize());
    }

    /**
        Removes the 'n' oldest entries from us.
        <p>
        Note: this method is differrent from removeEldestEntry() in that it
        actually removes entries instead of just indicating whether we
        should.

        @see #removeEldestEntry(Map.Entry)
    */
    protected void removeOldestEntries(int n)
    {
//System.err.println("---> removeOldestEntries(" + n + ") ...");
        Assert.require(n >= 0);
        Assert.require(n <= maximumSize());

//System.err.println(" - max. size = " + maximumSize());
        if (n > 0)
        {
            Iterator iter = keySet().iterator();
            for (int i = 0; i < n; i++)
            {
                Object k = iter.next();
                    // 'iter' will have a next item here since (by one of our
                    // preconditions) n <= maximumSize()
//System.err.println(" - i = " + i + ", k = " + k.toString());
                iter.remove();  // the entry with key 'k'
            }
        }

        //Assert.ensure("maximumSize() = old maximumSize() - n");
    }
}
