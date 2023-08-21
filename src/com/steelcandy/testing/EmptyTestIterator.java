/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.Utilities;

/**
    A singleton TestIterator that iterates over no tests.

    @author James MacKay
    @version $Revision: 1.6 $
    @see Test
*/
public class EmptyTestIterator implements TestIterator
{
    // Constants

    /** The single instance of this class. */
    private static final EmptyTestIterator _instance =
        new EmptyTestIterator();

    /** The resources used by this class. */
    private static final Resources _resources =
        TestingResourcesLocator.resources;

    /** Resource keys. */
    private static final String NO_NEXT_TEST_MSG = "NO_NEXT_TEST_MSG";
    private static final String NO_TEST_TO_PEEK_MSG = "NO_TEST_TO_PEEK_MSG";


    // Constructors

    /**
        @return the single instance of this class
    */
    public static TestIterator instance()
    {
        return _instance;
    }

    /**
        Default constructor. This should only be called to
        construct the single instance of this class.
    */
    private EmptyTestIterator()
    {
        // empty
    }


    // Implemented TestIterator methods

    /**
        Always returns false.

        @see TestIterator#hasNext
    */
    public boolean hasNext()
    {
        return false;
    }

    /**
        @see TestIterator#next
    */
    public Test next()
    {
        String msg = _resources.getMessage(NO_NEXT_TEST_MSG);
        throw new NoSuchItemException(msg);
    }

    /**
        @see TestIterator#peek
    */
    public Test peek()
    {
        String msg = _resources.getMessage(NO_TEST_TO_PEEK_MSG);
        throw new NoSuchItemException(msg);
    }
}
