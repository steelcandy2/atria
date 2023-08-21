/*
 Copyright (C) 2014-2015 by James MacKay.

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

package com.steelcandy.plack.common.base;

import com.steelcandy.common.debug.Assert;

import java.io.IOException;

/**
    The interface implemented by classes that can load/recreate a Storable
    from its saved representation.
    <p>
    A given concrete subclass can usually only load Storables of a specific
    type or small set of types.

    @author  James MacKay
    @version $Revision: 1.3 $
    @see Storable
    @see Storage
*/
public interface StorableLoader
{
    // Public methods

    /**
        @param st the storage that 'item' was obtained from
        @param item a stored item that contains all of the information
        that was saved for a Storable
        @return (a copy of) the Storable object that 'item' contains the
        information for
        @exception IOException if an I/O error occurs while loading the
        object
        @exception StorageException if a StorableLoader hasn't (yet) been
        registered with 'st' for one of the object's subobjects
    */
    public Storable load(Storage st, StoredItem item)
        throws IOException, StorageException;
        // Assert.require(st != null);
        // Assert.require(item != null);
        // Assert.ensure(result != null);
}
