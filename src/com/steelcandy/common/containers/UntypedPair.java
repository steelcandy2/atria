/*
 Copyright (C) 2009 by James MacKay.

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
import com.steelcandy.common.UnsupportedMethodException;

import java.util.Iterator;

/**
    Represents an (untyped) immutable pair.

    @author James MacKay
    @see TypedStack
*/
public class UntypedPair
{
    // Private fields

    /** The first item in this pair. */
    private Object _first;

    /** The second item in this pair. */
    private Object _second;


    // Constructors

    /**
        Constructs an UntypedPair.

        @param first the pair's first item
        @param second the pair's second item
    */
    public UntypedPair(Object first, Object second)
    {
        // 'first' may be null
        // 'second' may be null
        _first = first;
        _second = second;
    }


    // Public methods

    /**
        @return our first item
    */
    public Object first()
    {
        // 'result' may be null
        return _first;
    }

    /**
        @return our second item
    */
    public Object second()
    {
        // 'result' may be null
        return _second;
    }

    /**
        Note: the returned iterator does not support the
        <code>remove()</code> operation.

        @return an iterator over the items in this pair, returning our
        first item first, followed by our second item
        @see Iterator#remove
    */
    public Iterator iterator()
    {
        return new UntypedPairIterator(this);
    }


    // Overridden/implemented methods

    /**
        Note: a stack with a single item will have that item labelled as the
        stack's top item in the string.

        @see Object#toString
    */
    public String toString()
    {
        String str1 = first().toString();
        String str2 = second().toString();
        int len = str1.length() + str2.length() + 5;  // approximate

        StringBuffer result = new StringBuffer(len);
        result.append("(").append(str1).append(", ").
               append(str2).append(")");

        return result.toString();
    }

    /**
        @see Object#equals(Object)
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof UntypedPair)
        {
            UntypedPair pair = (UntypedPair) obj;
            result = (first().equals(pair.first())) &&
                        (second().equals(pair.second()));
        }

        return result;
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        return first().hashCode() ^ second().hashCode();
    }


    // Inner classes

    /**
        An abstract base class for iterators that iterate through the items
        in an UntypedPair.
    */
    protected static class UntypedPairIterator
        implements Iterator
    {
        // Private fields

        /** The UntypedPair to iterate through. */
        private UntypedPair _pair;

        /**
            The 0-based index of the next item to return: there are no more
            items to return iff this field is greater than 1.
        */
        private int _nextIndex;


        // Constructors

        /**
            Constructs an UntypedPairIterator from the pair whose items it
            is to iterate over.

            @param c the pair the iterator is to iterator over the items of
        */
        public UntypedPairIterator(UntypedPair c)
        {
            Assert.require(c != null);
            _pair = c;
            _nextIndex = 0;
        }


        // Public methods

        /**
            @see Iterator#hasNext
        */
        public boolean hasNext()
        {
            return (_nextIndex < 2);
        }

        /**
            @see Iterator#next()
        */
        public Object next()
        {
            Object result;
            if (_nextIndex == 0)
            {
                result = _pair.first();
            }
            else if (_nextIndex == 1)
            {
                result = _pair.second();
            }
            else
            {
                throw new NoSuchItemException();
            }

            // 'result' may be null
            return result;
        }

        /**
            This operation is unsupported.

            @see Iterator#remove
        */
        public void remove()
        {
            throw new UnsupportedMethodException(getClass(), "remove()");
        }
    }
}
