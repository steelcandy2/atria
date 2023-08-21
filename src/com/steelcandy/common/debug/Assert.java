/*
 Copyright (C) 1999-2001 by James MacKay.

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

package com.steelcandy.common.debug;

/**
    Controls which types of assertions are checked, as well as methods
    that can be used to check them. It can also be used to check for
    unreachable code.

    @author James MacKay
*/
public final class Assert
{
    // Constants

    /**
        Constants that control which types of assertions are actually
        checked.
    */
    public static final boolean preconds  = true;
    public static final boolean checks    = true;
    public static final boolean postconds = true;

    /**
        Alternate names for the above constants - don't modify the
        values of these:
    */
    public static final boolean preconditions = preconds;
    public static final boolean postconditions = postconds;


    // Public static methods

    /**
        Throws a PreconditionFailedException if and only if
        theCondition is false.

        @param theCondition the (value of the) condition to test
        @exception PreconditionFailedException thrown iff
        'theCondition' is false
    */
    public static final void require(boolean theCondition)
    {
        if (preconds && !theCondition)
        {
            throw new PreconditionFailedException();
        }
    }

    /**
        Throws a PreconditionFailedException constructed from
        theMessage if and only if theCondition is false.

        @param theCondition the (value of the) condition to test
        @param theMessage the message to report if 'theCondition'
        is false
        @exception PreconditionFailedException thrown iff
        'theCondition' is false
    */
    public static final void require(boolean theCondition, String theMessage)
    {
        if (preconds && !theCondition)
        {
            throw new PreconditionFailedException(theMessage);
        }
    }

    /**
        Throws an AssertionFailedException if and only if theCondition
        is false.

        @param theCondition the (value of the) condition to test
        @exception AssertionFailedException thrown iff
        'theCondition' is false
    */
    public static final void check(boolean theCondition)
    {
        if (checks && !theCondition)
        {
            throw new AssertionFailedException();
        }
    }

    /**
        Throws an AssertionFailedException constructed from theMessage
        if and only if theCondition is false.

        @param theCondition the (value of the) condition to test
        @param theMessage the message to report if 'theCondition'
        is false
        @exception AssertionFailedException thrown iff
        'theCondition' is false
    */
    public static final void check(boolean theCondition, String theMessage)
    {
        if (checks && !theCondition)
        {
            throw new AssertionFailedException(theMessage);
        }
    }

     /**
        Throws a PostconditionFailedException if and only if theCondition
        is false.

        @param theCondition the (value of the) condition to test
        @exception PostconditionFailedException thrown iff
        'theCondition' is false
    */
    public static final void ensure(boolean theCondition)
    {
        if (postconds && !theCondition)
        {
            throw new PostconditionFailedException();
        }
    }

     /**
        Throws a PostconditionFailedException constructed from theMessage
        if and only if theCondition is false.

        @param theCondition the (value of the) condition to test
        @param theMessage the message to report if 'theCondition'
        is false
        @exception PostconditionFailedException thrown iff
        'theCondition' is false
    */
    public static final void ensure(boolean theCondition, String theMessage)
    {
        if (postconds && !theCondition)
        {
            throw new PostconditionFailedException(theMessage);
        }
    }


    /**
        Place a call to this method just before any code that should
        never get executed: it will throw an
        UnreachableCodeReachedException if it is reached.
    */
    public static final void unreachable()
    {
        throw new UnreachableCodeReachedException();
    }
}
