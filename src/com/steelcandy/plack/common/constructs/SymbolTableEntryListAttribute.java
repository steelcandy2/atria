/*
 Copyright (C) 2004-2012 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.semantic.SymbolTableEntryList;

/**
    Represents a construct attribute whose value is of type
    SymbolTableEntryList.

    @author James MacKay
    @see SymbolTableEntryList
*/
public class SymbolTableEntryListAttribute
    extends Attribute
{
    // Constructors

    /**
        Constructs a SymbolTableEntryListAttribute that has not been set yet.
    */
    public SymbolTableEntryListAttribute()
    {
        super();

        Assert.ensure(isSet() == false);
    }

    /**
        Constructs a SymbolTableEntryListAttribute that has been set, and
        that has the specified value.

        @param value the attribute's value
    */
    public SymbolTableEntryListAttribute(SymbolTableEntryList value)
    {
        super(value);
        //Assert.require(value != null);

        Assert.ensure(isSet());
    }

    /**
        Copy constructor.

        @param a the attribute that the attribute being constructed is to be
        a copy of
    */
    protected SymbolTableEntryListAttribute(SymbolTableEntryListAttribute a)
    {
        // Assert.require(a != null);
        super(a);
    }


    // Public methods

    /**
        @return this attribute's value
        @see Attribute#valueObject
    */
    public SymbolTableEntryList value()
    {
        Assert.require(isSet());

        //Assert.ensure(result != null);
        return (SymbolTableEntryList) valueObject();
    }

    /**
        Sets this attribute's value to be the specified value.

        @param newValue this attribute's new value
        @see Attribute#setValueObject
    */
    public void setValue(SymbolTableEntryList newValue)
    {
        Assert.require(newValue != null);

        setValueObject(newValue);

        Assert.ensure(isSet());
    }

    /**
        @return a clone of this attribute
    */
    public SymbolTableEntryListAttribute cloneAttribute()
    {
        // Assert.ensure(result != null);
        return new SymbolTableEntryListAttribute(this);
    }
}
