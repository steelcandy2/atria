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
    An abstract base class for type-safe construct attributes.
    <p>
    An attribute contains the attribute's value (e.g. the Type object
    representing a construct's type). It also allows constructs to
    optionally have an attribute: if the attribute is set (as indicated by
    <code>isSet()</code>) then the construct has the attribute, and if it
    isn't set then the construct doesn't have the attribute. (A construct's
    attribute should never be set to null to indicate that the construct
    doesn't have the attribute.) Note that the <code>isSet()</code> indicates
    whether the <em>attribute</em> is set, not whether its value is set:
    however, setting an attribute's value is how an attribute is set.
    <p>
    This class provides implementations of those methods that do not depend
    on the element type, and also manages the backing object. Subclasses that
    represent an attribute with a value of type <code>XXX</code> are usually
    named <code>XXXAttribute</code>, and should implement the following
    methods:
    <ul>
        <li><code>public XXX value()</code>, implemented using the
            <code>valueObject()</code> method defined in this class, and</li>
        <li><code>public void setValue(XXX newValue)</code>, implemented
            using the <code>setValueObject()</code> method defined in this
            class.</li>
    </ul>

    @author James MacKay
*/
public abstract class Attribute
{
    // Private fields

    /** The attribute's value. */
    private Object _value;


    // Constructors

    /**
        Constructs an Attribute that has not been set yet.
    */
    protected Attribute()
    {
        _value = null;

        Assert.ensure(isSet() == false);
    }

    /**
        Constructs an Attribute that has been set, and has the
        specified value.

        @param value the attribute's value
    */
    protected Attribute(Object value)
    {
        Assert.require(value != null);

        _value = value;

        Assert.ensure(isSet());
    }

    /**
        Copy constructor.

        @param a the attribute that the attribute being constructed is to be
        a copy of
    */
    protected Attribute(Attribute a)
    {
        // Assert.require(a != null);
        this(a.valueObject());
    }


    // Public methods

    /**
        Indicates whether this attribute's value has been set (which allows
        constructs to have optional attributes).

        @return true if this attribute's value is set, and false if it isn't
    */
    public boolean isSet()
    {
        return (_value != null);
    }


    // Protected methods

    /**
        @return this attribute's value
    */
    protected Object valueObject()
    {
        Assert.require(isSet());

        Assert.ensure(_value != null);
        return _value;
    }

    /**
        Sets this attribute's value to be the specified value.

        @param newValue this attribute's new value
    */
    protected void setValueObject(Object newValue)
    {
        Assert.require(newValue != null);

        _value = newValue;

        Assert.ensure(isSet());
    }
}
