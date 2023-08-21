/*
 Copyright (C) 2004 by James MacKay.

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
    Represents a construct attribute whose value is of type int.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class IntAttribute
    extends Attribute
{
    // Constructors

    /**
        Constructs an IntAttribute that has not been set yet.
    */
    public IntAttribute()
    {
        super();

        Assert.ensure(isSet() == false);
    }

    /**
        Constructs an IntAttribute that has been set, and that has the
        specified value.

        @param value the attribute's value
    */
    public IntAttribute(int value)
    {
        super(new Integer(value));

        Assert.ensure(isSet());
    }

    /**
        Copy constructor.

        @param a the attribute that the attribute being constructed is to be
        a copy of
    */
    protected IntAttribute(IntAttribute a)
    {
        // Assert.require(a != null);
        super(a);
    }


    // Public methods

    /**
        @return this attribute's value
        @see Attribute#valueObject
    */
    public int value()
    {
        Assert.require(isSet());

        Integer result = (Integer) valueObject();

        return result.intValue();
    }

    /**
        Sets this attribute's value to be the specified value.

        @param newValue this attribute's new value
        @see Attribute#setValueObject
    */
    public void setValue(int newValue)
    {
        setValueObject(new Integer(newValue));

        Assert.ensure(isSet());
    }

    /**
        @return a clone of this attribute
    */
    public IntAttribute cloneAttribute()
    {
        // Assert.ensure(result != null);
        return new IntAttribute(this);
    }
}
