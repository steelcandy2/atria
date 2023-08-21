/*
 Copyright (C) 2001-2015 by James MacKay.

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

import com.steelcandy.common.NoSuchItemException;

import com.steelcandy.common.text.TextUtilities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Iterator;

/**
    An abstract base class for type-safe lists: that is, list classes that
    are basically just type-safe wrappers around a regular 'typeless' Java
    list.
    <p>
    This class provides implementations of those methods that do not depend
    on the item type, and also manages the backing list.

    @author James MacKay
    @version $Revision: 1.23 $
*/
public abstract class TypedList
{
    // Private fields

    /** The items in this list, in order. */
    private List _items;


    // Constructors

    /**
        Constructs an empty list with the specified initial capacity.

        @param initialCapacity the list's initial capacity
    */
    public TypedList(int initialCapacity)
    {
        _items = createBackingList(initialCapacity);
    }

    /**
        Constructs a TypedList from its backing list.

        @param backingList the typed list's backing list
    */
    public TypedList(List backingList)
    {
        Assert.require(backingList != null);

        _items = backingList;
    }


    // Abstract methods

    /**
        Creates and returns a backing list for an instance of this class
        that has the specified initial capacity.

        @param initialCapacity the backing list's initial capacity
        @return the backing list for an instance of this class
    */
    protected abstract List createBackingList(int initialCapacity);


    // Public methods

    /**
        @return an untyped list containing the items in this list: changes
        made to the returned list will be reflected in this list ans vice
        versa
    */
    public List asUntypedList()
    {
        Assert.ensure(_items != null);
        return _items;
    }

    /**
        @return an untyped iterator over the items in this list
    */
    public Iterator untypedIterator()
    {
        Iterator result = _items.iterator();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sorts this list in place using the specified comparator to compare
        its items. The sort is stable.

        @param c the comparator that determines the order in which our items
        are sorted
        @see Collections#sort(List, Comparator)
    */
    public void sort(Comparator c)
    {
        Assert.require(c != null);

        Collections.sort(_items, c);
    }

    /**
        Sorts this list in place using the specified comparator to compare
        its items, then removes any duplicate items (using the specified
        comparator to determine whether items are equal). The sort is stable.

        @param c the comparator that determines the order in which our items
        are sorted, as well as which items are considered equal for the
        purposes of detecting duplicates
        @see #sort(Comparator)
    */
    public void sortUnique(Comparator c)
    {
        Assert.require(c != null);

        sort(c);

        // We don't use an Iterator here since there'll be modifications
        // to this list in the loop. We also call size() each time through
        // the loop because our size can change.
        for (int i = 0; i < (size() - 1); i++)
        {
            Object curr = getObject(i);
            int j = i + 1;
            while (j < size())
            {
                Object next = getObject(j);
                if (c.compare(curr, next) == 0)
                {
                    // 'curr' and 'next' are equal, so remove 'next'. Then
                    // check the items after 'next' (if any)
                    remove(j);
                }
                else
                {
                    break;  // while
                }
            }
        }
    }

    /**
        Removes the (index + 1)th item from this list.

        @param index the index of the item to remove from this list
        @exception NoSuchItemException thrown if this list doesn't have an
        item at the specified index
    */
    public void remove(int index)
        throws NoSuchItemException
    {
        if (index >= 0 && index < _items.size())
        {
            _items.remove(index);
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        Removes the last item from this list.

        @exception NoSuchItemException thrown if this list is empty
    */
    public void removeLast()
        throws NoSuchItemException
    {
        remove(_items.size() - 1);
    }

    /**
        Removes all of the items from this list, leaving it empty.
    */
    public void removeAll()
    {
        _items.clear();

        Assert.ensure(isEmpty());
    }


    /**
        @return the number of items in this list
    */
    public int size()
    {
        return _items.size();
    }

    /**
        @return true iff this list contains no items
    */
    public boolean isEmpty()
    {
        return _items.isEmpty();
    }


    // Overridden/implemented methods

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer result = new StringBuffer(getClass().getName());
        result.append(":").append(TextUtilities.NL);

        Iterator iter = _items.iterator();
        while (iter.hasNext())
        {
            result.append("    ").append(iter.next().toString()).
                   append(TextUtilities.NL);
        }

        return result.toString();
    }


    // Protected methods

    /**
        @param obj an object
        @return true iff we contain at least one item that is equal to 'obj',
        where they're compared using 'obj''s equals() method
        @see Object#equals(Object)
    */
    protected boolean containsObject(Object obj)
    {
        // 'obj' can be null

        return _items.contains(obj);
    }

    /**
        Returns the item at the specified index in this list.

        @param index the index of the item in this list that is to be
        returned
        @return the item in this list at the specified index
        @exception NoSuchItemException thrown if this list doesn't have an
        item at the specified index
    */
    protected Object getObject(int index)
        throws NoSuchItemException
    {
        if (index >= 0 && index < _items.size())
        {
            return _items.get(index);
        }
        else
        {
            throw new NoSuchItemException();
        }
    }

    /**
        @return the last item in this list
        @exception NoSuchItemException thrown if this list is empty
    */
    protected Object getLastObject()
        throws NoSuchItemException
    {
        return getObject(_items.size() - 1);
    }

    /**
        @see Collections#binarySearch(List, Object, Comparator)
    */
    protected int binarySearchForObject(Object obj, Comparator c)
    {
        // 'obj' can be null
        Assert.require(c != null);

        return Collections.binarySearch(_items, obj, c);
    }

    /**
        Adds the specified item to the end of this list.

        @param newItem the item to add to the end of this list
    */
    protected void addObject(Object newItem)
    {
        _items.add(newItem);
    }

    /**
        Adds all of the items in the specified list in order to the end of
        this list.

        @param list the list of items to add to the end of this list
    */
    protected void addAllObjects(TypedList list)
    {
        _items.addAll(list._items);
    }

    /**
        Inserts the specified item at the specified index in this list.

        @param newItem the item to insert into this list
        @param index the index at which to insert the item
    */
    protected void insertObject(Object newItem, int index)
    {
        _items.add(index, newItem);
    }

    /**
        Inserts all of the items in the specified list into this list in
        order, at the specified index.
        <p>
        Is there a more efficient way to implement this ????!!!!??

        @param list the list of items to insert into this list
        @param index the index at which to insert the items
    */
    protected void insertAllObjects(TypedList list, int index)
    {
        _items.addAll(index, list._items);
    }

    /**
        Sets the (index + 1)th item in the list to be the specified item.

        @param index the index of the list item to set
        @param newItem the item to set the (index + 1)th item in the list to
    */
    protected void setObject(int index, Object newItem)
    {
        Assert.require(index >= 0);
        Assert.require(index <= _items.size());
            // can have index == _items.size()

        if (index < _items.size())
        {
            _items.set(index, newItem);
        }
        else
        {
            //Assert.check(index == _items.size());
            _items.add(newItem);
        }
    }

    /**
        Removes from this list the first item in it that is equal to 'obj',
        where items are compared using 'obj''s equals() method.

        @param obj an object
        @return true iff an item was removed from this list
        @see Object#equals(Object)
    */
    protected boolean removeObject(Object obj)
    {
        // 'obj' can be null

        return _items.remove(obj);
    }

    /**
        Returns an array containing all of the items in this list, and that
        has the same runtime type as the specified array.
        <p>
        Note: the specified array will be the one that is returned iff it is
        big enough to hold all of this list's items.

        @param array the array whose runtime type is the same as that of the
        array that is to be returned: if it is big enough, it will
        <em>be</em> the array that is returned
        @return an array (<em>array</em> iff it is big enough) containing all
        of the items of this list in order
    */
    protected Object[] toObjectArray(Object[] array)
    {
        return _items.toArray(array);
    }

    /**
        Returns a list containing the items in this list's backing List from
        the one at the specified index through the last one in this list,
        inclusive.
        <p>
        The returned list is backed by this list's backing list, so changes
        in one will be reflected in the other.

        @param startIndex the index of the first item in this list's backing
        list to be in the returned sublist
        @return the sublist of this list's backing list
    */
    protected List subBackingList(int startIndex)
    {
        return subBackingList(startIndex, _items.size());
    }

    /**
        Returns a list containing the items in this list's backing List from
        the one at the specified start index through the one at the at the
        index, if any, before the specified past-the-end index.
        <p>
        The returned list is backed by this list's backing list, so changes
        in one will be reflected in the other.

        @param startIndex the index of the first item in this list's backing
        list to be in the returned sublist
        @param pastEndIndex the index one past the index of the last item in
        this list's backing list to be in the returned sublist
        @return the sublist of this list's backing list
    */
    protected List subBackingList(int startIndex, int pastEndIndex)
    {
        return _items.subList(startIndex, pastEndIndex);
    }


    // Protected static methods

    /**
        @return the untyped unmodifiable empty List
    */
    protected static List untypedEmptyList()
    {
        return Collections.EMPTY_LIST;
    }


    // Inner classes

    /**
        An abstract base class for iterators that iterate through the items
        in a TypedList.

        @see TypedList
    */
    protected abstract static class TypedListIterator
    {
        // Private fields

        /** The TypedList to iterate through. */
        private TypedList _list;

        /**
            The index in the list of the next item to be returned by this
            iterator's next() method.
        */
        private int _nextIndex = 0;


        // Constructors

        /**
            Constructs a TypedListIterator from the TypedList whose items it
            is to iterate over.

            @param list the TypedList the iterator is to iterate over the
            items of
        */
        public TypedListIterator(TypedList list)
        {
            _list = list;
        }


        // Public methods

        /**
            Indicates whether this iterator has any more items left to
            iterate over.

            @return true iff this iterator has any more items left to
            iterate over
        */
        public boolean hasNext()
        {
            return (_nextIndex < _list.size());
        }


        // Protected methods

        /**
            @return the next item
            @exception NoSuchItemException thrown if there isn't a next
            object
            @see #peekObject
        */
        protected Object nextObject()
        {
            return _list.getObject(_nextIndex++);
        }

        /**
            @return the next item without consuming it (so the next call to
            peekObject() or nextObject() will return the same item)
            @exception NoSuchItemException thrown if there isn't a next
            object
            @see #nextObject
        */
        protected Object peekObject()
        {
            return _list.getObject(_nextIndex);
        }
    }
}
