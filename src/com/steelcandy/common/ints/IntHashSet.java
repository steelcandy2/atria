/*
 Copyright (C) 2005-2014 by James MacKay.

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
    An IntSet implemented using a hash table.

    @author  James MacKay
    @version $Revision: 1.7 $
*/
public class IntHashSet
    extends AbstractIntSet
{
    // Constants

    /** The default capacity of a IntHashSet. */
    protected static final int DEFAULT_INITIAL_CAPACITY = 17;

    /** The default load factor of a IntHashSet. */
    protected static final float DEFAULT_LOAD_FACTOR = 0.75f;


    // Private fields

    /** The set of ints. */
    private HashSet _set;


    // Constructors

    /**
        Constructs an empty IntHashSet with the default initial capacity and
        load factor.

        @see #DEFAULT_INITIAL_CAPACITY
        @see #DEFAULT_LOAD_FACTOR
    */
    public IntHashSet()
    {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty IntHashSet with the specified initial capacity
        and the default load factor.

        @param initialCapacity the set's initial capacity
    */
    public IntHashSet(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
        Constructs an empty IntHashSet with the specified initial capacity
        and load factor.

        @param initialCapacity the set's initial capacity
        @param loadFactor the set's load factor
    */
    public IntHashSet(int initialCapacity, float loadFactor)
    {
        _set = new HashSet(initialCapacity, loadFactor);
    }


    // Protected methods

    /**
        @see AbstractIntSet#backingSet
    */
    protected Set backingSet()
    {
        Assert.ensure(_set != null);
        return _set;
    }
}
