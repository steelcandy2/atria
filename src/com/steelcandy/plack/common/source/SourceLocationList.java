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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.containers.TypedList;
import com.steelcandy.common.NoSuchItemException;

import java.util.*;

/**
    Represents a list of SourceLocation items.
    <p>
    This is basically a type-safe wrapper around a regular 'typeless' Java
    list.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/list.java.xsl
        <li>Output: src/com/steelcandy/plack/common/source/SourceLocationList.java
    </ul>

    @author James MacKay
    @see SourceLocation
*/
public abstract class SourceLocationList
    extends TypedList
{
    // Constants

    /** The default SourceLocationList capacity. */
    protected static final int DEFAULT_CAPACITY = 5;

    /** The (unmodifiable) empty list. */
    private static final SourceLocationList
        EMPTY_LIST = new LinkedTypedList(untypedEmptyList());


    // Constructors

    /**
        @return an empty list of SourceLocation items that is backed
        by an array
    */
    public static SourceLocationList createArrayList()
    {
        return createArrayList(DEFAULT_CAPACITY);
    }

    /**
        Creates and returns an empty list of SourceLocation items
        that is backed by an array and has the specified initial capacity.
        <p>
        Note that lists backed by a linked list cannot have their initial
        capacity set, so there is no corresponding named constructor to
        create a linked list-backed typed list.

        @param initialCapacity the list's initial capacity
        @return an empty list of SourceLocation items that is backed
        by an array
    */
    public static SourceLocationList createArrayList(int initialCapacity)
    {
        return new ArrayTypedList(initialCapacity);
    }

    /**
        @param c the SourceLocationList to copy
        @return a list containing the same items as 'c' in the same order,
        and that is backed by an array
    */
    public static SourceLocationList createArrayList(SourceLocationList c)
    {
        Assert.require(c != null);

        SourceLocationList result = createArrayList(c.size());

        appendItems(c, result);

        Assert.ensure(result != null);
        Assert.ensure(result.size() == c.size());
        return result;
    }

    /**
        @return an empty list of SourceLocation items that is backed
        by a linked list
    */
    public static SourceLocationList createLinkedList()
    {
        // Linked lists cannot have their initial capacity set.
        return new LinkedTypedList();
    }

    /**
        @param c the SourceLocationList to copy
        @return a list containing the same items as 'c' in the same order,
        and that is backed by a linked list
    */
    public static SourceLocationList createLinkedList(SourceLocationList c)
    {
        Assert.require(c != null);

        SourceLocationList result = createLinkedList();

        appendItems(c, result);

        Assert.ensure(result != null);
        Assert.ensure(result.size() == c.size());
        return result;
    }

    /**
        @return an unmodifiable list of SourceLocation items that
        contains the specified SourceLocation as its sole element
    */
    public static SourceLocationList
        createSingleItemList(SourceLocation item)
    {
        return new SingleItemTypedList(item);
    }

    /**
        @return an unmodifiable empty list of SourceLocation items
    */
    public static SourceLocationList createEmptyList()
    {
        return EMPTY_LIST;
    }


    /**
        Constructs a(n) SourceLocationList with the specified
        initial capacity.

        @param initialCapacity the list's initial capacity
    */
    protected SourceLocationList(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
        Constructs a(n) SourceLocationList with the specified
        backing list.

        @param backingList the list's backing list
    */
    protected SourceLocationList(List backingList)
    {
        super(backingList);
    }


    // Public static methods

    /**
        @param arr an array of SourceLocation items
        @return an iterator over all of the items in 'arr', in order
    */
    public static SourceLocationIterator iterator(SourceLocation[] arr)
    {
        //Assert.ensure(result != null);
        return new SourceLocationArrayIterator(arr);
    }


    // Public methods

    /**
        @return an iterator over all of the SourceLocation items in
        this list, in order
    */
    public SourceLocationIterator iterator()
    {
        return new BuiltInSourceLocationListIterator(this);
    }

    /**
        @param item the SourceLocation to look for
        @return true iff at least one SourceLocation in this list
        is equal to 'item', where 'item''s equals() method is used to compare
        items
        @see Object#equals(Object)
    */
    public boolean contains(SourceLocation item)
    {
        // 'item' can be null

        return containsObject(item);
    }

    /**
        @param index the index in the list of the SourceLocation
        to return
        @return the (index + 1)th SourceLocation in this list
        @exception NoSuchItemException thrown if this list doesn't have an
        item at the specified index
    */
    public SourceLocation get(int index)
        throws NoSuchItemException
    {
        return (SourceLocation) getObject(index);
    }

    /**
        @return the last element in the list
        @exception NoSuchItemException thrown if this list doesn't have a
        last item - that is, if it is empty
    */
    public SourceLocation getLast()
        throws NoSuchItemException
    {
        return (SourceLocation) getLastObject();
    }

    /**
        Performs a binary search on this list to find an item equal to the
        specified item. This list must be sorted in ascending order according
        to the specified comparator or the results of calling this method are
        undefined.

        @param item the item to search this list for
        @param c the comparator to use in the search
        @return the index of one of the items in this list that is equal to
        'item' according to 'c', or -1 if there is no such item in this list
    */
    public int binarySearch(SourceLocation item, Comparator c)
    {
        // 'item' can be null
        Assert.require(c != null);

        return binarySearchForObject(item, c);
    }


    /**
        Returns a list of those items in this list that satisfy the specified
        predicate.

        @param predicate the predicate that an item in this list must satisfy
        in order for it to be included in the returned list
    */
    public SourceLocationList
        filter(UnarySourceLocationPredicate predicate)
    {
        Assert.require(predicate != null);

        SourceLocationList result =
            SourceLocationList.createArrayList(size());

        SourceLocationIterator iter = iterator();
        while (iter.hasNext())
        {
            SourceLocation item = iter.next();
            if (predicate.isSatisfied(item))
            {
                result.add(item);
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the index of the first SourceLocation in this list
        that satisfies the specified predicate, or -1 if there is no such
        SourceLocation in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @return the index of the first SourceLocation in this list
        that satisfies 'predicate'
    */
    public int indexOf(UnarySourceLocationPredicate predicate)
    {
        Assert.require(predicate != null);

        return indexOf(predicate, 0);
    }

    /**
        Returns the index of the first SourceLocation in this list
        at or after the specified index that satisfies the specified
        predicate, or -1 if there is no such SourceLocation in
        this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @param startIndex the index at which an item must be at or after in
        this list in order for its index to be returned
        @return the index of the first item in this list that is at or after
        the startIndex and that satisfies the predicate
    */
    public int indexOf(UnarySourceLocationPredicate predicate,
                       int startIndex)
    {
        Assert.require(predicate != null);
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < size());

        int result = -1;
        for (int i = startIndex; i < size(); i++)
        {
            if (predicate.isSatisfied(get(i)))
            {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
        Returns the index of the last SourceLocation in the list
        that satisfies the specified predicate, or -1 if there is no such
        SourceLocation in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @return the index of the last item in this list that satisfies the
        predicate
    */
    public int lastIndexOf(UnarySourceLocationPredicate predicate)
    {
        return lastIndexOf(predicate, size() - 1);
    }

    /**
        Returns the index of the first SourceLocation in this list
        at or before the specified index that satisfies the specified
        predicate, or -1 if there is no such SourceLocation
        in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @param startIndex the index at which an item must be at or before in
        this list in order for its index to be returned
        @return the index of the last item in this list that is at or before
        the startIndex and that satisfies the predicate
    */
    public int lastIndexOf(UnarySourceLocationPredicate predicate,
                           int startIndex)
    {
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < size());

        int result = -1;
        for (int i = startIndex; i >= 0; i--)
        {
            if (predicate.isSatisfied(get(i)))
            {
                result = i;
                break;
            }
        }
        return result;
    }


    /**
        Adds the specified SourceLocation to the end of this list.

        @param newItem the SourceLocation to add to the end of
        this list
    */
    public void add(SourceLocation newItem)
    {
        addObject(newItem);
    }

    /**
        Adds all of the SourceLocation items in the specified list
        in order to the end of this list.

        @param list the list of SourceLocation items to add to the
        end of this list
    */
    public void addAll(SourceLocationList list)
    {
        addAllObjects(list);
    }

    /**
        Swaps the item at index 'index1' with the one at 'index2'.
        <p>
        Note: this works properly if 'index1' and 'index2' are equal too.

        @param index1 the index of the item to swap with the one at index
        'index2'
        @param index2 the index of the item to swap with the one at index
        'index1'
    */
    public void swap(int index1, int index2)
    {
        Assert.require(index1 >= 0);
        Assert.require(index1 < size());
        Assert.require(index2 >= 0);
        Assert.require(index2 < size());

        SourceLocation tmp = get(index1);
        set(index1, get(index2));
        set(index2, tmp);
    }

    /**
        Inserts the specified SourceLocation into this list at the
        specified index.

        @param newItem the SourceLocation to insert into this list
        @param index the index at which to insert the SourceLocation
    */
    public void insert(SourceLocation newItem, int index)
    {
        insertObject(newItem, index);
    }

    /**
        Inserts all of the SourceLocation items in the specified
        list into this list in order, at the specified index.

        @param list the list of SourceLocation items to insert into
        this list
        @param index the index at which to insert the SourceLocation
        items
    */
    public void insertAll(SourceLocationList list, int index)
    {
        insertAllObjects(list, index);
    }

    /**
        Sets the (index + 1)th element of the list to be the specified
        SourceLocation.

        @param index the index of the list element to set
        @param newItem the value to set the list element to
    */
    public void set(int index, SourceLocation newItem)
    {
        Assert.require(index >= 0);
        Assert.require(index <= size());  // can have index == size()

        setObject(index, newItem);
    }

    /**
        Removes from this list the first SourceLocation in it
        that is equal to 'item', where items are compared using 'item''s
        equals() method.

        @param item the SourceLocation to remove
        @return true iff an item was removed from this list
        @see Object#equals(Object)
    */
    public boolean remove(SourceLocation item)
    {
        // 'item' can be null

        return removeObject(item);
    }


    /**
        @return an array containing all of the elements of this list in order
    */
    public SourceLocation[] toArray()
    {
        SourceLocation[] array =
            new SourceLocation[size()];
        return (SourceLocation[]) toObjectArray(array);
    }


    // Private static methods

    /**
        Appends all of the items in 'src' to the end of 'dest'.

        @param src the list of items to append to 'dest'
        @param dest the list to append 'src''s items to
    */
    private static void appendItems(SourceLocationList src, SourceLocationList dest)
    {
        Assert.require(src != null);
        Assert.require(dest != null);

        SourceLocationIterator iter = src.iterator();
        while (iter.hasNext())
        {
            dest.add(iter.next());
        }
    }


    // Abstract methods

    /**
        @return a clone of this list
    */
    public abstract SourceLocationList cloneList();
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == size());

    /**
        Returns a list containing the items in this list from the one at the
        specified index through the last one in this list, inclusive.
        <p>
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the index of the first item in this list to be in
        the returned sublist
        @return the sublist of this list
    */
    public abstract SourceLocationList subList(int startIndex);
        // Assert.ensure(result != null);

    /**
        Returns a list containing the items in this list from the one at the
        specified start index through the one at the index, if any, before
        the specified past-end index.
        <p>
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the index of the first item in this list to be in
        the returned sublist
        @param pastEndIndex the index one past the index of the last item in
        this list to be in the returned sublist
        @return the sublist of this list
    */
    public abstract SourceLocationList
        subList(int startIndex, int pastEndIndex);
        // Assert.ensure(result != null);


    // Inner classes

    /**
        An array-backed SourceLocationList.
    */
    private static class ArrayTypedList
        extends SourceLocationList
    {
        // Constructors

        /**
            Constructs an ArrayTypedList.

            @param initialCapacity the list's initial capacity
        */
        public ArrayTypedList(int initialCapacity)
        {
            super(initialCapacity);
        }

        /**
            Constructs an ArrayTypedList.

            @param backingList the list's backing list
        */
        protected ArrayTypedList(List backingList)
        {
            super(backingList);
        }


        // Public methods

        /**
            @see SourceLocationList#cloneList
        */
        public SourceLocationList cloneList()
        {
            SourceLocationList result = new ArrayTypedList(size());

            result.addAll(this);

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see SourceLocationList#subList(int)
        */
        public SourceLocationList subList(int startIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex));
        }

        /**
            @see SourceLocationList#subList(int, int)
        */
        public SourceLocationList
            subList(int startIndex, int pastEndIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex,
                                                     pastEndIndex));
        }


        // Protected methods

        /**
            @see TypedList#createBackingList(int)
        */
        protected List createBackingList(int initialCapacity)
        {
            return new ArrayList(initialCapacity);
        }
    }

    /**
        An unmodifiable single-item list-backed SourceLocationList.
    */
    private static class SingleItemTypedList
        extends SourceLocationList
    {
        // Constructors

        /**
            Constructs a SingleItemTypedList.

            @param item the list's single item
        */
        public SingleItemTypedList(SourceLocation item)
        {
            super(Collections.singletonList(item));
        }


        // Public methods

        /**
            @see SourceLocationList#cloneList
        */
        public SourceLocationList cloneList()
        {
            SourceLocationList result = new SingleItemTypedList(get(0));

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see SourceLocationList#subList(int)
        */
        public SourceLocationList subList(int startIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex));
        }

        /**
            @see SourceLocationList#subList(int, int)
        */
        public SourceLocationList
            subList(int startIndex, int pastEndIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex,
                                                     pastEndIndex));
        }


        // Protected methods

        /**
            @see TypedList#createBackingList(int)
        */
        protected List createBackingList(int initialCapacity)
        {
            // This method should never get called.
            throw new UnsupportedOperationException();
        }
    }

    /**
        A linked list-backed SourceLocationList.
    */
    private static class LinkedTypedList
        extends SourceLocationList
    {
        // Constructors

        /**
            Constructs a LinkedTypedList.
        */
        public LinkedTypedList()
        {
            // The initial capacity will be ignored.
            super(1);
        }

        /**
            Constructs a LinkedTypedList.

            @param backingList the list's backing list
        */
        protected LinkedTypedList(List backingList)
        {
            super(backingList);
        }


        // Public methods

        /**
            @see SourceLocationList#cloneList
        */
        public SourceLocationList cloneList()
        {
            SourceLocationList result = new LinkedTypedList();

            result.addAll(this);

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see SourceLocationList#subList(int)
        */
        public SourceLocationList subList(int startIndex)
        {
            return new LinkedTypedList(subBackingList(startIndex));
        }

        /**
            @see SourceLocationList#subList(int, int)
        */
        public SourceLocationList
            subList(int startIndex, int pastEndIndex)
        {
            return new LinkedTypedList(subBackingList(startIndex,
                                                      pastEndIndex));
        }


        // Protected methods

        /**
            @see TypedList#createBackingList(int)
        */
        protected List createBackingList(int initialCapacity)
        {
            // The initial capacity of a LinkedList cannot be set.
            return new LinkedList();
        }
    }


    // Built-in iterator inner class

    /**
        Iterates through the SourceLocation items in a(n)
        SourceLocationList.

        @see SourceLocation
        @see SourceLocationList
    */
    protected static class BuiltInSourceLocationListIterator
        extends TypedListIterator
        implements SourceLocationIterator
    {
        // Constructors

        /**
            Constructs a BuiltInSourceLocationListIterator from the
            SourceLocationList whose SourceLocation items it is
            to iterate over.

            @param list the SourceLocationList that the
            iterator is to iterate over the elements of
        */
        public BuiltInSourceLocationListIterator(SourceLocationList list)
        {
            super(list);
        }


        // Public methods

        /**
            @see SourceLocationIterator#next
        */
        public SourceLocation next()
        {
            return (SourceLocation) nextObject();
        }

        /**
            @see SourceLocationIterator#peek
        */
        public SourceLocation peek()
        {
            return (SourceLocation) peekObject();
        }
    }
}

/**
    The typed array iterator class.
*/
class SourceLocationArrayIterator
    implements SourceLocationIterator
{
    // Private fields

    /** The array that we iterate over. */
    private SourceLocation[] _array;

    /**
        The index in '_array' of the next item to be returned by our
        next() method.
    */
    private int _nextIndex;


    // Constructors

    /**
        Constructs an iterator from the array that it iterates over.

        @param arr the array
    */
    public SourceLocationArrayIterator(SourceLocation[] arr)
    {
        Assert.require(arr != null);

        _array = arr;
        _nextIndex = 0;
    }


    // Public methods

    /**
        @see SourceLocationIterator#hasNext
    */
    public boolean hasNext()
    {
        return (_nextIndex < _array.length);
    }

    /**
        @see SourceLocationIterator#next
    */
    public SourceLocation next()
        throws NoSuchItemException
    {
        try
        {
            return _array[_nextIndex++];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new NoSuchItemException(ex);
        }
    }

    /**
        @see SourceLocationIterator#peek
    */
    public SourceLocation peek()
        throws NoSuchItemException
    {
        try
        {
            return _array[_nextIndex];
        }
        catch (ArrayIndexOutOfBoundsException ex)
        {
            throw new NoSuchItemException(ex);
        }
    }
}
