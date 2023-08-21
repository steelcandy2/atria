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

import org.jdom.*;

import java.util.*;

/**
    The class of StoredItem that is implemented using the JDOM representation
    of XML documents.

    @author  James MacKay
*/
public class JdomStoredItem
    extends AbstractStoredItem
{
    // Private fields

    /**
        The JDOM Element that contains all of this item's information.
    */
    private Element _element;


    // Constructors

    /**
        Constructs a JdomStoredItem from the JDOM element that contains all
        of the item's information.

        @param e a JDOM element
    */
    public JdomStoredItem(Element e)
    {
        Assert.require(e != null);

        _element = e;
    }


    // Public methods

    /**
        @see StoredItem#name
    */
    public String name()
    {
        String result = _element.
            getAttributeValue(AbstractStorage.NAME_ATTRIBUTE_NAME);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see StoredItem#type
    */
    public String type()
    {
        String result = _element.
            getAttributeValue(AbstractStorage.TYPE_ATTRIBUTE_NAME);

        Assert.ensure(result != null);
        Assert.ensure(result.length() > 0);
        return result;
    }

    /**
        @see StoredItem#findAttribute(String)
    */
    public String findAttribute(String attrName)
    {
        Assert.require(attrName != null);

        String result = _element.getAttributeValue(attrName);

        // 'result' may be null
        Assert.ensure(attrName.length() > 0 || result == null);
        return result;
    }

    /**
        @see StoredItem#textContents
    */
    public String textContents()
    {
        String result = _element.getText();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see StoredItem#children
    */
    public StoredItemList children()
    {
        List childElems = _element.getChildren();

        StoredItemList result =
            StoredItemList.createArrayList(childElems.size());

        Iterator iter = childElems.iterator();
        while (iter.hasNext())
        {
            StoredItem child = new JdomStoredItem((Element) iter.next());
            result.add(child);
        }
        Assert.check(result.size() == childElems.size());

        Assert.ensure(result != null);
        return result;
    }
}
