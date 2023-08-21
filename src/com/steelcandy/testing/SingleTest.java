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

/**
    A base class for tests that have no sub-tests.
    <p>
    Subclasses' runTest() methods should call setSucceeded()
    to indicate whether the test succeeded or failed.

    @author James MacKay
*/
public abstract class SingleTest extends AbstractTest
{
    // Private fields

    /**
        Indicates whether this test succeeded or not.

        @see #setSucceeded
    */
    private boolean _succeeded = false;


    // Constructors

    /**
        Constructs a SingleTest that uses a DefaultTestWriter
        for output.

        @see DefaultTestWriter
    */
    public SingleTest()
    {
        // empty
    }

    /**
        Constructs a SingleTest that uses the specified
        TestWriter for output.

        @param writer the writer the test should use for output
    */
    public SingleTest(TestWriter writer)
    {
        super(writer);
    }


    // Protected methods

    /**
        Sets the success/failure status of this test. The
        runTest() method should call this method to indicate
        whether this test succeeded or failed.

        @param didSucceed true if this test succeeded, and false
        if it failed
        @see #testSucceeded
    */
    protected void setSucceeded(boolean didSucceed)
    {
        _succeeded = didSucceed;
    }


    // Overridden methods

    /**
        @see Test#outputAllInformation
    */
    public void outputAllInformation(int level)
    {
        // Just output the information for this test.
        outputInformation(level);
    }

    /**
        @see AbstractTest#testSucceeded
    */
    protected boolean testSucceeded()
    {
        return _succeeded;
    }

    /**
        @see AbstractTest#testCount
    */
    protected int testCount()
    {
        return 1;
    }

    /**
        @see AbstractTest#successfulTestCount
    */
    protected int successfulTestCount()
    {
        return (_succeeded ? 1 : 0);
    }
}
