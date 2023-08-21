/*
 Copyright (C) 2005-2015 by James MacKay.

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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

import java.util.Collection;
import java.util.Iterator;

/**
    An abstract base class for collections classes whose items are ints.

    @author  James MacKay
*/
public abstract class AbstractIntCollection
    implements IntCollection
{
    // Constructors

    /**
        Constructs an AbstractIntCollection.
    */
    public AbstractIntCollection()
    {
        // empty
    }


    // Public static methods

    /**
        @param a an array of ints
        @return a string representation of 'a'
    */
    public static String toString(int[] a)
    {
        StringBuffer res = new StringBuffer();
        res.append("[");

        int n = a.length;
        for (int i = 0; i < n; i++)
        {
            if (i > 0)
            {
                res.append(", ");
            }
            res.append(Integer.toString(a[i]));
        }

        res.append("]");

        String result = res.toString();

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        @see IntCollection#isEmpty
    */
    public boolean isEmpty()
    {
        return (size() == 0);
    }


    // Protected methods

    /**
        Converts the specified object (which had better be a non-null
        Integer) to the int value that it represents.

        @param obj the object to convert to the int value that it represents
        @return the int value that the object represents
    */
    protected int toInt(Object obj)
    {
        Assert.require(obj != null);
        Assert.require(obj instanceof Integer);

        return ((Integer) obj).intValue();
    }

    /**
        Converts the specified Integer to the int value that it represents.

        @param val the Integer to convert to the int value that
        it represents
        @return the int value that the Integer represents
    */
    protected int toInt(Integer val)
    {
        Assert.require(val != null);

        return val.intValue();
    }

    /**
        Converts the specified int value to an Integer object that represents
        it.

        @param val the int to convert to an Integer
        @return an Integer object representing the int value
    */
    protected Integer toInteger(int val)
    {
        return new Integer(val);
    }


    /**
        Used to implement IntSets' toString() method.

        @param s an IntSet
        @return the String representation of 's'
        @see Object#toString
    */
    protected String toString(IntSet s)
    {
        Assert.require(s != null);

        StringBuffer buf = new StringBuffer(s.size() * 5);
        buf.append("{");

        IntIterator iter = s.iterator();
        while (iter.hasNext())
        {
            buf.append(iter.next());
            if (iter.hasNext())
            {
                buf.append(", ");
            }
        }
        buf.append("}");

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Used to implement IntMaps' toString() method.

        @param m an IntMap
        @return the String representation of 'm'
        @see Object#toString
    */
    protected String toString(IntMap m)
    {
        Assert.require(m != null);

        StringBuffer buf = new StringBuffer(m.size() * 10);
        buf.append("{");

        IntIterator iter = m.keys();
        while (iter.hasNext())
        {
            int key = iter.next();
            int value = m.get(key);
            buf.append(key).append(": ").append(value);
            if (iter.hasNext())
            {
                buf.append(", ");
            }
        }
        buf.append("}");

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Used to implement ToIntMaps' toString() method.

        @param m a ToIntMap
        @return the String representation of 'm'
        @see Object#toString
    */
    protected String toString(ToIntMap m)
    {
        Assert.require(m != null);

        StringBuffer buf = new StringBuffer(m.size() * 10);
        buf.append("{");

        Iterator iter = m.keys();
        while (iter.hasNext())
        {
            Object key = iter.next();
            int value = m.get(key);
            buf.append(key.toString()).append(": ").append(value);
            if (iter.hasNext())
            {
                buf.append(", ");
            }
        }
        buf.append("}");

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }


    // Protected static methods

    /**
        @param iter an iterator over ints
        @param sz the size of the array to be returned
        @return an array of ints consisting of the next 'sz' ints
        returned by 'iter', in order
    */
    protected static int[] toArray(IntIterator iter, int sz)
    {
        Assert.require(iter != null);
        Assert.require(sz >= 0);

        int[] result = new int[sz];

        for (int i = 0; i < sz; i++)
        {
            result[i] = iter.next();
        }

        Assert.ensure(result != null);
        Assert.ensure(result.length == sz);
        return result;
    }


    // Inner classes

    /**
        An iterator over the ints in a Collection.
        <p>
        Each Object in the Collection is assumed to be a non-null Integer:
        an instance of this class will iterate over the ints that each such
        Object/Integer represents.
    */
    protected class CollectionIterator
        implements IntIterator
    {
        // Private fields

        /** An iterator the collection of ints. */
        private Iterator _iter;

        /**
            The next Object that this iterator's next() or peek() methods
            will return (converted to an int), or null iff this iterator will
            not return any more ints.
        */
        private Object _nextObject;


        // Constructors

        /**
            Constructs a CollectionIterator from the collection of ints to
            iterate over.
        */
        public CollectionIterator(Collection coll)
        {
            Assert.require(coll != null);

            _iter = coll.iterator();
            updateNextObject();
        }


        // Public methods

        /**
            @see IntIterator#hasNext
        */
        public boolean hasNext()
        {
            return (_nextObject != null);
        }

        /**
            @see IntIterator#next
        */
        public int next()
        {
            if (hasNext())
            {
                Object result = _nextObject;
                updateNextObject();
                return toInt(result);
            }
            else
            {
                throw new NoSuchItemException();
            }
        }

        /**
            @see IntIterator#peek
        */
        public int peek()
        {
            if (hasNext())
            {
                return toInt(_nextObject);
            }
            else
            {
                throw new NoSuchItemException();
            }
        }


        // Private methods

        /**
            Updates '_nextObject' to reference the next object returned by
            the collection iterator, or null if the enumeration has no more
            objects to return.
        */
        private void updateNextObject()
        {
            _nextObject = (_iter.hasNext()) ? _iter.next() : null;

            Assert.ensure(_iter.hasNext() == false || _nextObject != null);
        }
    }
}
