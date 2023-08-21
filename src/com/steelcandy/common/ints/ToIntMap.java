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

import java.util.*;

/**
    The interface implemented by maps that map Objects to ints, much like a
    java.util.Map with int as the type of its values.

    @author James MacKay
    @see java.util.Map
*/
public interface ToIntMap
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
    public int get(Object key)
        throws NoSuchItemException;

    /**
        @param key the key to be mapped by this map
        @param defaultValue the value to return if this map doesn't map 'key'
        @return the value that this map maps 'key' to if it does map it, and
        'defaultValue' if it doesn't
    */
    public int find(Object key, int defaultValue);

    /**
        Sets the value that this map maps the specified key to to be the
        specified value, replacing any existing mapping from that key.

        @param key the key whose mapping is to be set/changed
        @param value the value to which the key now maps
    */
    public void set(Object key, int value);
        // Assert.ensure(hasKey(key));
        // Assert.ensure(get(key) == value);

    /**
        Indicates whether this map will map the specified key to a value.

        @param key the key to test
        @return true if this map will map the key to a value, and false if it
        won't
    */
    public boolean hasKey(Object key);

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
        @see #keySet
    */
    public Iterator keys();
        // Assert.ensure(result != null);

    /**
        @return a Set that is a view of the keys that this map maps into
        values
        @see #keys
        @see Map#keySet
    */
    public Set keySet();
        // Assert.ensure(result != null);

    /**
        @return an iterator over the values to which this map maps keys. A
        given value will be returned once by the iterator for every key that
        this map maps to that value
    */
    public IntIterator values();
        // Assert.ensure(result != null);

    /**
        Note: if more than one key corresponds to a given value then the
        ordering of those keys in our result relative to each other is
        undefined.

        @return a list of all of our keys ordered so that a key whose
        corresponding value is 'v' will precede all other keys whose
        corresponding value is greater than 'v', and will follow all other
        keys whose corresponding value is less than 'v'
    */
    public List keysSortedByAscendingValues();
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == size());

    /**
        Note: if more than one key corresponds to a given value then the
        ordering of those keys in our result relative to each other is
        undefined.

        @return a list of all of our keys ordered so that a key whose
        corresponding value is 'v' will precede all other keys whose
        corresponding value is less than 'v', and will follow all other keys
        whose corresponding value is greater than 'v'
    */
    public List keysSortedByDescendingValues();
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == size());
}
