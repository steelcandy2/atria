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

/**
    Represents a construct attribute whose value is of type String.

    @author James MacKay
*/
public class StringAttribute
    extends Attribute
{
    // Constructors

    /**
        Constructs a StringAttribute that has not been set yet.
    */
    public StringAttribute()
    {
        super();

        Assert.ensure(isSet() == false);
    }

    /**
        Constructs a StringAttribute that has been set, and that has the
        specified value.

        @param value the attribute's value
    */
    public StringAttribute(String value)
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
    protected StringAttribute(StringAttribute a)
    {
        // Assert.require(a != null);
        super(a);
    }


    // Public methods

    /**
        @return this attribute's value
        @see Attribute#valueObject
    */
    public String value()
    {
        Assert.require(isSet());

        //Assert.ensure(result != null);
        return (String) valueObject();
    }

    /**
        Sets this attribute's value to be the specified value.

        @param newValue this attribute's new value
        @see Attribute#setValueObject
    */
    public void setValue(String newValue)
    {
        Assert.require(newValue != null);

        setValueObject(newValue);

        Assert.ensure(isSet());
    }

    /**
        @return a clone of this attribute
    */
    public StringAttribute cloneAttribute()
    {
        // Assert.ensure(result != null);
        return new StringAttribute(this);
    }
}
