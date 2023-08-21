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

import com.steelcandy.common.Resources;

/**
    An abstract base class for StoredItem classes.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public abstract class AbstractStoredItem
    implements StoredItem
{
    // Constants

    /** The resources used by this class. */
    private static final Resources
        _resources = CommonStorageResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_BOOLEAN_ATTRIBUTE_VALUE_MSG =
            "INVALID_BOOLEAN_ATTRIBUTE_VALUE_MSG";


    // Public methods

    /**
        @see StoredItem#hasAttribute(String)
    */
    public boolean hasAttribute(String attrName)
    {
        Assert.require(attrName != null);

        String val = findAttribute(attrName);

        boolean result = (val != null);

        Assert.ensure(attrName.length() > 0 || result == false);
        return result;
    }

    /**
        @see StoredItem#attribute(String)
    */
    public String attribute(String attrName)
    {
        Assert.require(attrName != null);
        Assert.require(hasAttribute(attrName));

        String result = findAttribute(attrName);

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see StoredItem#booleanAttribute(String)
    */
    public boolean booleanAttribute(String attrName)
        throws StorageException
    {
        Assert.require(attrName != null);
        Assert.require(hasAttribute(attrName));

        return toBooleanValue(attrName, attribute(attrName));
    }

    /**
        @see StoredItem#findBooleanAttribute(String)
    */
    public Boolean findBooleanAttribute(String attrName)
        throws StorageException
    {
        Assert.require(attrName != null);

        Boolean result = null;

        String val = findAttribute(attrName);
        if (val != null)
        {
            result = new Boolean(toBooleanValue(attrName, val));
        }

        // 'result' may be null
        Assert.ensure(attrName.length() > 0 || result == null);
        return result;
    }


    // Protected methods

    /**
        @param attrValue the value of an attribute
        @return the boolean value that 'attrValue' represents
        @exception StorageException thrown if 'attrValue' isn't a valid
        representation of a boolean-valued attribute's value
    */
    protected boolean toBooleanValue(String attrName, String attrValue)
        throws StorageException
    {
        Assert.require(attrName != null);
        Assert.require(attrValue != null);

        boolean result;

        if (attrValue.equals(AbstractStorage.TRUE_ATTRIBUTE_VALUE))
        {
            result = true;
        }
        else if (attrValue.equals(AbstractStorage.FALSE_ATTRIBUTE_VALUE))
        {
            result = false;
        }
        else
        {
            String msg = _resources.
                getMessage(INVALID_BOOLEAN_ATTRIBUTE_VALUE_MSG,
                           attrName, attrValue);
            throw new StorageException(msg);
        }

        return result;
    }
}
