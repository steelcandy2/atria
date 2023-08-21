/*
 Copyright (C) 2002-2009 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.Containers;

import java.util.Map;

/**
    An abstract base class for classes that map token IDs to other objects.
    <p>
    <strong>Note</strong>: an instance's initialize() method must be called
    after it is constructed but before it is used. (The map is not
    initialized in its constructor since that would prevent the
    implementations of initialization methods like initializeMap() and
    createDefaultObject() from using constants and fields defined in a
    subclass.)
    <p>
    Subclasses only have to implement the addMappings() method, though they
    may also override the createDefaultObject() and mapLoadFactor() methods
    if they wish.

    @author James MacKay
    @version $Revision: 1.5 $
    @see #initialize
*/
public abstract class AbstractTokenIdToObjectMap
{
    // Constants

    /**
        The default load factor used in instances' internal Maps.

        @see #mapLoadFactor
    */
    protected static final float
        DEFAULT_MAP_LOAD_FACTOR = Containers.DEFAULT_HASH_LOAD_FACTOR;


    // Private fields

    /**
        The map from token IDs to the objects that they map to. The keys are
        Integers (token IDs) and the values are Objects.
    */
    private Map _map;

    /**
        The object returned for token IDs for which there is no explicit
        mapping.
    */
    private Object _defaultObject;


    // Constructors

    /**
        Constructs an AbstractTokenIdToObjectMap from the number of entries
        in the map.
        <p>
        Note: the map's load factor is obtained from mapLoadFactor().

        @param numEntries the number of entries that the map should be able
        to hold (though it will grow as necessary)
        @see #mapLoadFactor
    */
    public AbstractTokenIdToObjectMap(int numEntries)
    {
        Assert.require(numEntries >= 0);

        // Make the map large enough to hold numEntries entries without it
        // being rehashed (in order to avoid having wasted spece in the map).
        _map = Containers.createHashMap(numEntries, mapLoadFactor());
    }


    // Public methods

    /**
        Initializes the contents of this map. It must be called before this
        map can usefully be used.
    */
    public void initialize()
    {
        initializeMap();
        _defaultObject = createDefaultObject();
    }


    // Protected methods

    /**
        Indicates whether this map explicitly maps the specified token ID to
        an object.

        @param tokenId the token ID to test
        @return true iff this map explicitly maps 'tokenId' to an object
    */
    protected boolean hasMappingFor(int tokenId)
    {
        return _map.containsKey(new Integer(tokenId));
    }

    /**
        Returns the object to which the specified token ID maps. It will
        always be mapped to a (non-null) object.

        @param tokenId the token ID to map to an object
        @return the object to which the token ID maps
    */
    protected Object getObject(int tokenId)
    {
        Object result = _map.get(new Integer(tokenId));

        if (result == null)
        {
            result = _defaultObject;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        This implementation returns DEFAULT_MAP_LOAD_FACTOR.

        @return the load factor used in this instance's Map
        @see #DEFAULT_MAP_LOAD_FACTOR
    */
    protected float mapLoadFactor()
    {
        Assert.ensure(DEFAULT_MAP_LOAD_FACTOR > 0.0);
        return DEFAULT_MAP_LOAD_FACTOR;
    }

    /**
        Adds a mapping from the specified token ID to the specified object to
        this map.
        <p>
        This method is intended to be called from initializeMap().

        @param tokenId the token ID to add a mapping for
        @param obj the object to which 'tokenId' is to be mapped
    */
    protected void addMapping(int tokenId, Object obj)
    {
        _map.put(new Integer(tokenId), obj);
    }


    // Abstract methods

    /**
        Initializes the contents of this map by adding mappings from token
        IDs to objects (using addMapping()).

        @see #addMapping(int, Object)
    */
    protected abstract void initializeMap();

    /**
        @return the object returned by this map for any token ID for which
        there is no explicit mapping
    */
    protected abstract Object createDefaultObject();
}
