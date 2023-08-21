/*
 Copyright (C) 2014 by James MacKay.

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

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    The interface implemented by classes whose instances can be saved to
    (possibly persistent) Storage, and later (a copy can be) loaded from it.

    @author  James MacKay
    @version $Revision: 1.1 $
    @see StorableLoader
    @see Storage
*/
public interface Storable
{
    // Public methods

    /**
        Saves this object to 'st' by writing its information to 'w'.

        @param st the storage to which to save this object
        @param w the writer to which to write this object's information
        (which will store it in 'st')
        @param name the name to save this object under: it is usually used to
        distinguish it from among a parent object's other child objects
        @exception IOException is thrown iff an I/O error occurs as part of
        saving this object's representation
    */
    public void save(Storage st, IndentWriter w, String name)
        throws IOException;
        // Assert.require(st != null);
        // Assert.require(w != null);
        // Assert.require(name != null);
}
