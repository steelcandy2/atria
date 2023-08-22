<?xml version="1.0"?>
<!--
    Transforms the information about a typed list into the source
    code for the Java class that represents the list.

    Copyright (C) 2002-2015 by James MacKay.

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
-->

<!DOCTYPE xsl:transform [
<!ENTITY copy "&#169;">
<!ENTITY nbsp "&#160;">
]>

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="common-generic.java.xsl"/>

    <!-- Configuration -->


    <!-- Global variables -->

    <xsl:variable name="top" select="$root/lists/list[@item-name=$item-class-name]"/>

    <xsl:variable name="default-capacity">
        <xsl:choose>
            <xsl:when test="$top/@default-capacity">
                <xsl:value-of select="$top/@default-capacity"/>
            </xsl:when>
            <xsl:otherwise>15</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="built-in-iterator-class-name"
        select="concat('BuiltIn', $item-name, 'ListIterator')"/>

    <xsl:variable name="iterator-class-name">
        <xsl:choose>
            <xsl:when test="$top/@iterator-class-name">
                <xsl:value-of select="$top/@iterator-class-name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$built-in-iterator-class-name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="generic-classes">
        <!-- Note: the copyright years in the generated file are the same as
ours. -->
        <xsl:text>/*
 Copyright (C) 2002-2015 by James MacKay.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
*/

package </xsl:text>
        <xsl:value-of select="$module-name"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;
</xsl:text>
        <xsl:value-of select="$item-import"/>
        <xsl:text>
import com.steelcandy.common.containers.TypedList;
import com.steelcandy.common.NoSuchItemException;

import java.util.*;

/**
    Represents a list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items.
    &lt;p&gt;
    This is basically a type-safe wrapper around a regular 'typeless' Java
    list.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends TypedList
{
    // Constants

    /** The default </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> capacity. */
    protected static final int DEFAULT_CAPACITY = </xsl:text>
        <xsl:value-of select="$default-capacity"/>
        <xsl:text>;

    /** The (unmodifiable) empty list. */
    private static final </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        EMPTY_LIST = new LinkedTypedList(untypedEmptyList());


    // Constructors

    /**
        @return an empty list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items that is backed
        by an array
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createArrayList()
    {
        return createArrayList(DEFAULT_CAPACITY);
    }

    /**
        Creates and returns an empty list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items
        that is backed by an array and has the specified initial capacity.
        &lt;p&gt;
        Note that lists backed by a linked list cannot have their initial
        capacity set, so there is no corresponding named constructor to
        create a linked list-backed typed list.

        @param initialCapacity the list's initial capacity
        @return an empty list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items that is backed
        by an array
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createArrayList(int initialCapacity)
    {
        return new ArrayTypedList(initialCapacity);
    }

    /**
        @param c the </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> to copy
        @return a list containing the same items as 'c' in the same order,
        and that is backed by an array
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createArrayList(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> c)
    {
        Assert.require(c != null);

        </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result = createArrayList(c.size());

        appendItems(c, result);

        Assert.ensure(result != null);
        Assert.ensure(result.size() == c.size());
        return result;
    }

    /**
        @return an empty list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items that is backed
        by a linked list
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createLinkedList()
    {
        // Linked lists cannot have their initial capacity set.
        return new LinkedTypedList();
    }

    /**
        @param c the </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> to copy
        @return a list containing the same items as 'c' in the same order,
        and that is backed by a linked list
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createLinkedList(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> c)
    {
        Assert.require(c != null);

        </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result = createLinkedList();

        appendItems(c, result);

        Assert.ensure(result != null);
        Assert.ensure(result.size() == c.size());
        return result;
    }

    /**
        @return an unmodifiable list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items that
        contains the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> as its sole element
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        createSingleItemList(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
    {
        return new SingleItemTypedList(item);
    }

    /**
        @return an unmodifiable empty list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> createEmptyList()
    {
        return EMPTY_LIST;
    }


    /**
        Constructs a(n) </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> with the specified
        initial capacity.

        @param initialCapacity the list's initial capacity
    */
    protected </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
        Constructs a(n) </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> with the specified
        backing list.

        @param backingList the list's backing list
    */
    protected </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>(List backingList)
    {
        super(backingList);
    }
</xsl:text>
        <xsl:if test="not($top/@iterator-class-name)">
            <xsl:text>

    // Public static methods

    /**
        @param arr an array of </xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text> items
        @return an iterator over all of the items in 'arr', in order
    */
    public static </xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text>Iterator iterator(</xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text>[] arr)
    {
        //Assert.ensure(result != null);
        return new </xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text>ArrayIterator(arr);
    }
</xsl:text>
        </xsl:if>
            <xsl:text>

    // Public methods

    /**
        @return an iterator over all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in
        this list, in order
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator iterator()
    {
        return new </xsl:text>
        <xsl:value-of select="$iterator-class-name"/>
        <xsl:text>(this);
    }

    /**
        @param item the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to look for
        @return true iff at least one </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        is equal to 'item', where 'item''s equals() method is used to compare
        items
        @see Object#equals(Object)
    */
    public boolean contains(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
    {
        // 'item' can be null

        return containsObject(item);
    }

    /**
        @param index the index in the list of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        to return
        @return the (index + 1)th </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        @exception NoSuchItemException thrown if this list doesn't have an
        item at the specified index
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> get(int index)
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) getObject(index);
    }

    /**
        @return the last element in the list
        @exception NoSuchItemException thrown if this list doesn't have a
        last item - that is, if it is empty
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> getLast()
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) getLastObject();
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
    public int binarySearch(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item, Comparator c)
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
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        filter(Unary</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Predicate predicate)
    {
        Assert.require(predicate != null);

        </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result =
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.createArrayList(size());

        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator iter = iterator();
        while (iter.hasNext())
        {
            </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item = iter.next();
            if (predicate.isSatisfied(item))
            {
                result.add(item);
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the index of the first </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        that satisfies the specified predicate, or -1 if there is no such
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @return the index of the first </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        that satisfies 'predicate'
    */
    public int indexOf(Unary</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Predicate predicate)
    {
        Assert.require(predicate != null);

        return indexOf(predicate, 0);
    }

    /**
        Returns the index of the first </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        at or after the specified index that satisfies the specified
        predicate, or -1 if there is no such </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in
        this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @param startIndex the index at which an item must be at or after in
        this list in order for its index to be returned
        @return the index of the first item in this list that is at or after
        the startIndex and that satisfies the predicate
    */
    public int indexOf(Unary</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Predicate predicate,
                       int startIndex)
    {
        Assert.require(predicate != null);
        Assert.require(startIndex &gt;= 0);
        Assert.require(startIndex &lt; size());

        int result = -1;
        for (int i = startIndex; i &lt; size(); i++)
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
        Returns the index of the last </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in the list
        that satisfies the specified predicate, or -1 if there is no such
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @return the index of the last item in this list that satisfies the
        predicate
    */
    public int lastIndexOf(Unary</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Predicate predicate)
    {
        return lastIndexOf(predicate, size() - 1);
    }

    /**
        Returns the index of the first </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> in this list
        at or before the specified index that satisfies the specified
        predicate, or -1 if there is no such </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        in this list.

        @param predicate the predicate that an item in this list must satisfy
        in order for its index to be returned
        @param startIndex the index at which an item must be at or before in
        this list in order for its index to be returned
        @return the index of the last item in this list that is at or before
        the startIndex and that satisfies the predicate
    */
    public int lastIndexOf(Unary</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Predicate predicate,
                           int startIndex)
    {
        Assert.require(startIndex &gt;= 0);
        Assert.require(startIndex &lt; size());

        int result = -1;
        for (int i = startIndex; i &gt;= 0; i--)
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
        Adds the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to the end of this list.

        @param newItem the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to add to the end of
        this list
    */
    public void add(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> newItem)
    {
        addObject(newItem);
    }

    /**
        Adds all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in the specified list
        in order to the end of this list.

        @param list the list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items to add to the
        end of this list
    */
    public void addAll(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> list)
    {
        addAllObjects(list);
    }

    /**
        Swaps the item at index 'index1' with the one at 'index2'.
        &lt;p&gt;
        Note: this works properly if 'index1' and 'index2' are equal too.

        @param index1 the index of the item to swap with the one at index
        'index2'
        @param index2 the index of the item to swap with the one at index
        'index1'
    */
    public void swap(int index1, int index2)
    {
        Assert.require(index1 &gt;= 0);
        Assert.require(index1 &lt; size());
        Assert.require(index2 &gt;= 0);
        Assert.require(index2 &lt; size());

        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> tmp = get(index1);
        set(index1, get(index2));
        set(index2, tmp);
    }

    /**
        Inserts the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> into this list at the
        specified index.

        @param newItem the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to insert into this list
        @param index the index at which to insert the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
    */
    public void insert(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> newItem, int index)
    {
        insertObject(newItem, index);
    }

    /**
        Inserts all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in the specified
        list into this list in order, at the specified index.

        @param list the list of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items to insert into
        this list
        @param index the index at which to insert the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        items
    */
    public void insertAll(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> list, int index)
    {
        insertAllObjects(list, index);
    }

    /**
        Sets the (index + 1)th element of the list to be the specified
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>.

        @param index the index of the list element to set
        @param newItem the value to set the list element to
    */
    public void set(int index, </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> newItem)
    {
        Assert.require(index &gt;= 0);
        Assert.require(index &lt;= size());  // can have index == size()

        setObject(index, newItem);
    }

    /**
        Removes from this list the first </xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text> in it
        that is equal to 'item', where items are compared using 'item''s
        equals() method.

        @param item the </xsl:text>
            <xsl:value-of select="$item-name"/>
            <xsl:text> to remove
        @return true iff an item was removed from this list
        @see Object#equals(Object)
    */
    public boolean remove(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
    {
        // 'item' can be null

        return removeObject(item);
    }


    /**
        @return an array containing all of the elements of this list in order
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[] toArray()
    {
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[] array =
            new </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[size()];
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[]) toObjectArray(array);
    }


    // Private static methods

    /**
        Appends all of the items in 'src' to the end of 'dest'.

        @param src the list of items to append to 'dest'
        @param dest the list to append 'src''s items to
    */
    private static void appendItems(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> src, </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> dest)
    {
        Assert.require(src != null);
        Assert.require(dest != null);

        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator iter = src.iterator();
        while (iter.hasNext())
        {
            dest.add(iter.next());
        }
    }


    // Abstract methods

    /**
        @return a clone of this list
    */
    public abstract </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> cloneList();
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == size());

    /**
        Returns a list containing the items in this list from the one at the
        specified index through the last one in this list, inclusive.
        &lt;p&gt;
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the index of the first item in this list to be in
        the returned sublist
        @return the sublist of this list
    */
    public abstract </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> subList(int startIndex);
        // Assert.ensure(result != null);

    /**
        Returns a list containing the items in this list from the one at the
        specified start index through the one at the index, if any, before
        the specified past-end index.
        &lt;p&gt;
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the index of the first item in this list to be in
        the returned sublist
        @param pastEndIndex the index one past the index of the last item in
        this list to be in the returned sublist
        @return the sublist of this list
    */
    public abstract </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        subList(int startIndex, int pastEndIndex);
        // Assert.ensure(result != null);


    // Inner classes

    /**
        An array-backed </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.
    */
    private static class ArrayTypedList
        extends </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
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
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#cloneList
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> cloneList()
        {
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result = new ArrayTypedList(size());

            result.addAll(this);

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> subList(int startIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex));
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int, int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
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
        An unmodifiable single-item list-backed </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.
    */
    private static class SingleItemTypedList
        extends </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    {
        // Constructors

        /**
            Constructs a SingleItemTypedList.

            @param item the list's single item
        */
        public SingleItemTypedList(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
        {
            super(Collections.singletonList(item));
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#cloneList
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> cloneList()
        {
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result = new SingleItemTypedList(get(0));

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> subList(int startIndex)
        {
            return new ArrayTypedList(subBackingList(startIndex));
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int, int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
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
        A linked list-backed </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.
    */
    private static class LinkedTypedList
        extends </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
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
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#cloneList
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> cloneList()
        {
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> result = new LinkedTypedList();

            result.addAll(this);

            Assert.ensure(result != null);
            Assert.ensure(result.size() == size());
            return result;
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> subList(int startIndex)
        {
            return new LinkedTypedList(subBackingList(startIndex));
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#subList(int, int)
        */
        public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
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
</xsl:text>
        <xsl:if test="not($top/@iterator-class-name)">
            <xsl:text>
</xsl:text>
            <xsl:call-template name="built-in-iterator"/>
        </xsl:if>
        <xsl:text>}
</xsl:text>

        <xsl:if test="not($top/@iterator-class-name)">
            <xsl:call-template name="array-iterator"/>
        </xsl:if>
    </xsl:template>


    <!-- Other templates -->

    <xsl:template name="built-in-iterator">
        <xsl:text>
    // Built-in iterator inner class

    /**
        Iterates through the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in a(n)
        </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.

        @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    */
    protected static class BuiltIn</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>ListIterator
        extends TypedListIterator
        implements </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator
    {
        // Constructors

        /**
            Constructs a BuiltIn</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>ListIterator from the
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> whose </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items it is
            to iterate over.

            @param list the </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> that the
            iterator is to iterate over the elements of
        */
        public BuiltIn</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>ListIterator(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> list)
        {
            super(list);
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#next
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> next()
        {
            return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) nextObject();
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#peek
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> peek()
        {
            return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) peekObject();
        }
    }
</xsl:text>
    </xsl:template>

    <xsl:template name="array-iterator">
        <xsl:text>
/**
    The typed array iterator class.
*/
class </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>ArrayIterator
    implements </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator
{
    // Private fields

    /** The array that we iterate over. */
    private </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[] _array;

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
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>ArrayIterator(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>[] arr)
    {
        Assert.require(arr != null);

        _array = arr;
        _nextIndex = 0;
    }


    // Public methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#hasNext
    */
    public boolean hasNext()
    {
        return (_nextIndex &lt; _array.length);
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#next
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> next()
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
        @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#peek
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> peek()
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
        </xsl:text>
    </xsl:template>
</xsl:transform>
