/*
 Copyright (C) 2001-2005 by James MacKay.

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

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
    Represents an empty enumeration.

    @author James MacKay
    @version $Revision: 1.5 $
*/
public class EmptyEnumeration implements Enumeration
{
    // Private fields

    /**
        The single instance of this class. (There's only one since an empty
        enumeration can't be modified, so there's no point in having more
        than one.)
    */
    private static EmptyEnumeration _instance = new EmptyEnumeration();


    // Constructors

    /**
        Returns the single instance of this class.

        @return the single instance of this class
    */
    public static EmptyEnumeration instance()
    {
        return _instance;
    }

    /**
        Default constructor.
    */
    private EmptyEnumeration()
    {
        // empty
    }


    // Overridden methods

    /**
        Always returns false since this enumeration is empty.

        @return false
    */
    public boolean hasMoreElements()
    {
        return false;
    }

    /**
        Always throws a NoSuchElementException since this enumeration is
        empty.

        @return nothing - it always throws an exception
        @exception NoSuchElementException always thrown since there are no
        elements
    */
    public Object nextElement()
    {
        throw new NoSuchElementException();
    }
}
