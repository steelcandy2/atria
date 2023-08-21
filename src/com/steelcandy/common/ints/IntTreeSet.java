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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

import java.util.Set;
import java.util.TreeSet;

/**
    An IntSet implemented using a (binary) tree.

    @author  James MacKay
    @version $Revision: 1.7 $
*/
public class IntTreeSet
    extends AbstractIntSet
    implements IntSortedSet
{
    // Private fields

    /** The set of ints. */
    private TreeSet _set;


    // Constructors

    /**
        Constructs an empty IntTreeSet.
    */
    public IntTreeSet()
    {
        _set = new TreeSet();

        Assert.ensure(isEmpty());
    }


    // Public methods

    /**
        @see IntSortedSet#first
    */
    public int first()
    {
        Assert.require(isEmpty() == false);

        return toInt(_set.first());
    }

    /**
        @see IntSortedSet#last
    */
    public int last()
    {
        Assert.require(isEmpty() == false);

        return toInt(_set.last());
    }


    // Protected methods

    /**
        @see AbstractIntSet#backingSet
    */
    protected Set backingSet()
    {
        Assert.ensure(_set != null);
        return _set;
    }
}
