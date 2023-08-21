/*
 Copyright (C) 2003-2006 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import java.util.Map;  // javadocs only

/**
    Represents the type of key that is usually associated with
    SymbolTableEntry objects in a Map.

    @author James MacKay
    @version $Revision: 1.2 $
    @see SymbolTableEntry
    @see Map
*/
public class SymbolTableEntryKey
{
    // Private fields

    /** The entry's name. */
    private String _name;

    /** The entry's arity. */
    private int _arity;


    // Constructors

    /**
        Constructs a SymbolTableEntryKey.

        @param name the entry's name
        @param arity the entry's arity
    */
    public SymbolTableEntryKey(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        _name = name;
        _arity = arity;
    }

    /**
        Constructs a SymbolTableEntryKey from the SymbolTableEntry
        that it is to be the key for.

        @param entry the SymbolTableEntry that the key is to be a
        key for
    */
    public SymbolTableEntryKey(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        _name = entry.name();
        _arity = entry.arity();

        Assert.ensure(_name != null);
    }


    // Public methods

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof SymbolTableEntryKey)
        {
            SymbolTableEntryKey key = (SymbolTableEntryKey) obj;
            result = (_arity == key._arity) &&
                        (_name.equals(key._name));
        }

        return result;
    }

    /**
        @see Object#hashCode
        @see #equals(Object)
    */
    public int hashCode()
    {
        // Instances are equal iff their names and arities are
        // equal, so instances with equal names and arities must
        // have equal hash codes.
        return _arity + _name.hashCode();
    }
}
