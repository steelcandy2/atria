/*
 Copyright (C) 2001-2006 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;

/**
    An abstract base class for all construct manager classes.
    <p>
    This class does not itself contain any construct inner classes (which is
    why it is called 'empty'), but defines some common useful methods.
    <p>
    Subclasses do not have to implement any methods: this class is abstract
    only to prevent its being instantiated.

    @author James MacKay
*/
public abstract class EmptyConstructManagerBase
{
    // Construct flag constants

    /** The number of construct flags used by this class. */
    public static final int EMPTY_NUMBER_OF_USED_FLAGS = 0;

    /** The first construct flag available for use by subclasses. */
    public static final int EMPTY_FIRST_AVAILABLE_FLAG =
        1 << EMPTY_NUMBER_OF_USED_FLAGS;


    // Construct ID constants

    /** The first construct ID available for use by subclasses. */
    public static final int
        EMPTY_FIRST_AVAILABLE_ID = 0;

    /**
        A value that is guaranteed not to be a valid construct ID (for use
        as a sentinel value, for example).
    */
    public static final int
        NON_CONSTRUCT_ID = EMPTY_FIRST_AVAILABLE_ID - 1;


    // Other constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        UNEXPECTED_FLAG_MSG = "UNEXPECTED_FLAG_MSG";
    private static final String
        UNEXPECTED_CONSTRUCT_ID_MSG = "UNEXPECTED_CONSTRUCT_ID_MSG";


    // Construct cloning methods

    /**
        @param list a list of constructs
        @return a list of constructs whose i'th element is a deep clone
        of the i'th element of 'list'
        @see Construct#deepClone
    */
    public static ConstructList deepCloneList(ConstructList list)
    {
        Assert.require(list != null);

        ConstructList result = ConstructList.createArrayList(list.size());

        ConstructIterator iter = list.iterator();
        while (iter.hasNext())
        {
            result.add(iter.next().deepClone());
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == list.size());
        return result;
    }

    /**
        @param list a list of constructs
        @return a list of constructs whose i'th element is a shallow clone
        of the i'th element of 'list'
        @see Construct#shallowClone
    */
    public static ConstructList shallowCloneList(ConstructList list)
    {
        Assert.require(list != null);

        ConstructList result = ConstructList.createArrayList(list.size());

        ConstructIterator iter = list.iterator();
        while (iter.hasNext())
        {
            result.add(iter.next().shallowClone());
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == list.size());
        return result;
    }


    // Construct ID/flag <-> description conversion methods

    /**
        Returns a description of the specified construct flag.

        @param flag the construct flag whose description is to be returned
        @return the construct flag's description
    */
    public String flagToDescription(int flag)
    {
        handleUnexpectedFlag(flag);

        return null;  // this should never be reached
    }

    /**
        @param constructId the construct ID of the construct whose
        description is to be returned
        @return a description of the construct with the specified construct
        ID
    */
    public String idToDescription(int constructId)
    {
        handleUnexpectedConstructId(constructId);

        return null;  // this should never be reached
    }


    // Private methods

    /**
        Handles the specified unexpected construct flag.
        <p>
        A flag is usually reported as unexpected because it was passed to
        one of this class' method implementations whe a subclass'
        implementation should have handled it.

        @param flag the unexpected construct flag
        @exception IllegalArgumentException thrown as part of handling the
        unexpected flag
    */
    private void handleUnexpectedFlag(int flag)
    {
        String msg = _resources.getMessage(UNEXPECTED_FLAG_MSG,
                                           Integer.toString(flag));
        throw new IllegalArgumentException(msg);
    }

    /**
        Handles the specified unexpected construct ID.
        <p>
        A construct ID is usually reported as unexpected because it was
        passed to one of this class' method implementations when a subclass'
        implementation should have handled it.

        @param constructId the unexpected construct ID
        @exception IllegalArgumentException thrown as part of handling the
        unexpected construct ID
    */
    private void handleUnexpectedConstructId(int constructId)
    {
        String msg = _resources.getMessage(UNEXPECTED_CONSTRUCT_ID_MSG,
                                           Integer.toString(constructId));
        throw new IllegalArgumentException(msg);
    }
}
