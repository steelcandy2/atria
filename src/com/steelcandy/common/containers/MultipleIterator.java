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

import com.steelcandy.common.UnsupportedMethodException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
    The class of iterator that iterates over the items returned
    by zero or more other iterators.
    <p>
    This class of iterator does not support the <code>remove()</code>
    operation.

    @author James MacKay
*/
public class MultipleIterator
    implements Iterator
{
    // Private fields

    /**
        A list of the iterators whose items this iterator iterates over, in
        the order that their items are iterated over. Each item in the list
        is an Iterator.
    */
    private List _iterators;


    // Constructors

    /**
        Constructs an empty MultipleIterator.
    */
    public MultipleIterator()
    {
        _iterators = new LinkedList();
    }


    // Public methods

    /**
        Adds the specified iterator to the end of this iterator's list
        of iterators.

        @param iter the iterator whose items this iterator is to return
        after it has returned all of the items returned by all iterators
        added earlier to this iterator
    */
    public void add(Iterator iter)
    {
        Assert.require(iter != null);

        _iterators.add(iter);
    }

    /**
        @see Iterator#hasNext
    */
    public boolean hasNext()
    {
        boolean result = false;

        Iterator iter = _iterators.iterator();
        while (result == false && iter.hasNext())
        {
            Iterator i = (Iterator) iter.next();
            if (i.hasNext())
            {
                result = true;
            }
            else
            {
                // 'i' is empty, so remove it from our list.
                iter.remove();
            }
        }

        return result;
    }

    /**
        @see Iterator#next
    */
    public Object next()
    {
        Object result = null;
        boolean foundNext = false;

        Iterator iter = _iterators.iterator();
        while (foundNext == false && iter.hasNext())
        {
            Iterator i = (Iterator) iter.next();
            if (i.hasNext())
            {
                result = i.next();
                foundNext = true;
            }
            else
            {
                // 'i' is empty, so remove it from our list.
                iter.remove();
            }
        }

        if (foundNext == false)
        {
            throw new NoSuchElementException();
        }

        return result;
    }

    /**
        @see Iterator#remove
    */
    public void remove()
    {
        throw new UnsupportedMethodException(getClass(), "remove()");
    }


    // Public static methods

    /**
        Main method that does some basic tests of this class.
    */
    public static void main(String[] args)
    {
        List list1 = new LinkedList();
        list1.add("a");

        List list2 = new LinkedList();

        List list3 = new LinkedList();
        list3.add("b");
        list3.add("c");
        list3.add("d");

        List list4 = new LinkedList();
        list4.add("e");
        list4.add("f");

        MultipleIterator iter = new MultipleIterator();
        iter.add(list1.iterator());
        iter.add(list2.iterator());
        iter.add(list3.iterator());
        iter.add(list4.iterator());

        StringBuffer buf = new StringBuffer();
        System.out.println("result should be:");
        System.out.println("    abcdef");
        while (iter.hasNext())
        {
            buf.append(iter.next());
        }
        System.out.println("result is:");
        System.out.println("    " + buf.toString());
    }
}
