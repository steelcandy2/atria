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

/**
    The interface implemented by classes that represent a Storable or part of
    a Storable in a form closer to that in which it's stored in a Storage
    instance.

    @author  James MacKay
    @version $Revision: 1.5 $
    @see Storable
    @see StorableLoader
*/
public interface StoredItem
{
    // Public methods

    /**
        @return the name of this item
    */
    public String name();
        // Assert.ensure(result != null);

    /**
        @return the name of the type of this item
    */
    public String type();
        // Assert.ensure(result != null);
        // Assert.ensure(result.length() > 0);


    /**
        @param attrName an attribute name
        @return true iff this item has an attribute named 'attrName'
    */
    public boolean hasAttribute(String attrName);
        // Assert.require(attrName != null);
        // Assert.ensure(attrName.length() > 0 || result == false);

    /**
        @param attrName an attribute name
        @return the value of the attribute named 'attrName' on this item
        @see #hasAttribute(String)
        @see #findAttribute(String)
    */
    public String attribute(String attrName);
        // Assert.require(attrName != null);
        // Assert.require(hasAttribute(attrName));
        // Assert.ensure(result != null);

    /**
        @param attrName an attribute name
        @return the value of the attribute named 'attrName' on this item, or
        null if it doesn't have an attribute named 'attrName'
    */
    public String findAttribute(String attrName);
        // Assert.require(attrName != null);
        // 'result' may be null
        // Assert.ensure(attrName.length() > 0 || result == null);


    /**
        @param attrName an attribute name
        @return the value of the boolean-valued attribute named 'attrName'
        on this item
        @exception StorageException thrown if the value of the attribute
        isn't a valid representation of a boolean-valued attribute's value
        @see #findBooleanAttribute(String)
    */
    public boolean booleanAttribute(String attrName)
        throws StorageException;
        // Assert.require(attrName != null);
        // Assert.require(hasAttribute(attrName));

    /**
        @param attrName an attribute name
        @return the value of the boolean-valued attribute named 'attrName' on
        this item, or null if it doesn't have an attribute named 'attrName'
        @exception StorageException thrown if the value of the attribute
        isn't a valid representation of a boolean-valued attribute's value
        @see Boolean#booleanValue
    */
    public Boolean findBooleanAttribute(String attrName)
        throws StorageException;
        // Assert.require(attrName != null);
        // 'result' may be null
        // Assert.ensure(attrName.length() > 0 || result == null);


    /**
        @return the text contents of this item: it does <em>not</em> include
        any text contained in its child items
    */
    public String textContents();
        // Assert.ensure(result != null);

    /**
        @return a list of all of this item's child items, in order: each item
        in the list is a StoredItem
    */
    public StoredItemList children();
        // Assert.ensure(result != null);
}
