/*
 Copyright (C) 2008 by James MacKay.

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

/**
    The interface implemented by classes that represent an element in an
    ExternalData object.

    A type class should be unique across all other type classes, and should
    indicate the general class of Externalizable that an instance represents.
    Examples include "symbol-table", "symbol-table-entry", "construct",
    "type" and "compilable-data".

    A type ID should be unique across all other classes of Externalizable
    with the same type class. They should start with "common-" if the
    concrete Externalizable subclass is defined in a common package, and a
    language-specific prefix if the Externalizable subclass is defined in a
    language-specific package.

    @author  James MacKay
*/
public interface ExternalDataElement
{
    // Public methods

    /**
        Sets our type class to 'typeClass'.

        @param typeClass our new type class
        @exception ExternalizingException is thrown iff our type class has
        already been set, or 'typeClass' is an invalid type class
    */
    public void setTypeClass(String typeClass)
        throws ExternalizingException;
        // Assert.require(typeClass != null);

    /**
        @return our type class, or null if it hasn't been set yet
    */
    public String typeClass();
        // 'result' may be null


    /**
        Sets our type ID to 'typeId'.

        @param typeId our new type ID
        @exception ExternalizingException is thrown iff our type ID has
        already been set, or 'typeId' is an invalid type ID
    */
    public void setTypeId(String typeId)
        throws ExternalizingException;
        // Assert.require(typeId != null);

    /**
        @return our type ID, or null if it hasn't been set yet
    */
    public String typeId();
        // 'result' may be null


    /**
        Adds an attribute named 'name' with value 'value' to this element.

        @param name the name of the attribute to add
        @param value the value of the attribute to add
        @exception ExternalizingException is thrown iff we already have an
        attribute named 'name', 'name' is an invalid attribute name, or
        'value' is an invalid attribute value
    */
    public void addAttribute(String name, String value)
        throws ExternalizingException;
        // Assert.require(name != null);
        // Assert.require(value != null);

    /**
        Adds a new child element named 'name' as our next child element and
        returns the new element.

        Note that an element can have more than one child element with the
        same name.

        @param name the new child element's name
        @exception ExternalizingException is thrown iff 'name' is an invalid
        element name
    */
    public ExternalDataElement addChildElement(String name)
        throws ExternalizingException;
        // Assert.require(name != null);
}
