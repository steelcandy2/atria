/*
 Copyright (C) 2003-2005 by James MacKay.

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

package com.steelcandy.common.containers;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
    An unmodifiable list that is the concatenation of zero or more other
    lists.

    @author James MacKay
*/
public class MultipleList
    extends AbstractList
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        ContainerResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        BAD_MULTIPLE_LIST_INDEX_MSG =
            "BAD_MULTIPLE_LIST_INDEX_MSG";


    // Private fields

    /**
        The lists that this list represents the concatenation of, in order.
        Each item in the list is a List.
    */
    private List _lists;

    /** The number of items in this list. */
    private int _size;


    // Constructors

    /**
        Constructs an empty MultipleList.
    */
    public MultipleList()
    {
        _lists = new ArrayList();
        _size = 0;
    }

    /**
        Constructs a MultipleList from its first component list.

        @param first the MultipleList's first component list
    */
    public MultipleList(List first)
    {
        this();
        Assert.require(first != null);

        addList(first);
    }

    /**
        Constructs a MultipleList from its first two component lists.

        @param first the MultipleList's first component list
        @param second the MultipleList's second component list
    */
    public MultipleList(List first, List second)
    {
        this();
        Assert.require(first != null);
        Assert.require(second != null);

        addList(first);
        addList(second);
    }

    /**
        Constructs a MultipleList from its first three component lists.

        @param first the MultipleList's first component list
        @param second the MultipleList's second component list
        @param third the MultipleList's third component list
    */
    public MultipleList(List first, List second, List third)
    {
        this();
        Assert.require(first != null);
        Assert.require(second != null);
        Assert.require(third != null);

        addList(first);
        addList(second);
        addList(third);
    }


    // Public methods

    /**
        Adds the specified list to the end of the list os lists that this
        list represents the concatenation of.

        @param list the list to add to the end of this list's list of lists
    */
    public void addList(List list)
    {
        Assert.require(list != null);

        _lists.add(list);
        _size += list.size();
    }

    /**
        @see List#get(int)
    */
    public Object get(int index)
    {
        Object result = null;

        if (index < 0 || index >= _size)
        {
            String msg = _resources.
                getMessage(BAD_MULTIPLE_LIST_INDEX_MSG,
                           String.valueOf(index), String.valueOf(_size));
            throw new IndexOutOfBoundsException(msg);
        }

        int componentIndex = index;
        Iterator iter = _lists.iterator();
        while (iter.hasNext())
        {
            List list = (List) iter.next();
            int nextIndex = componentIndex - list.size();
            if (nextIndex >= 0)
            {
                componentIndex = nextIndex;
            }
            else
            {
                result = list.get(componentIndex);
                break;
            }
        }

        // 'result' may be null
        return result;
    }

    /**
        @see List#size
    */
    public int size()
    {
        Assert.ensure(_size >= 0);
        return _size;
    }
}
