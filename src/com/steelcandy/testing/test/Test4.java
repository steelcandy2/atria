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

package com.steelcandy.testing.test;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.testing.*;

/**
    The fourth test for testing tests.

    @author James MacKay
    @see Tests
*/
public class Test4 extends SingleTest
{
    // Overridden methods

    /**
        @see Test#description
    */
    public String description()
    {
        return "a test used to test PackageTests using the default " +
               "sub-test name. This test always succeeds";
    }

    /**
        @see AbstractTest#runTest
    */
    protected void runTest()
    {
        writer().writeLine(TestWriter.DEBUG,
            "At start of Test4.runTest()");

        // This test always succeeds.
        setSucceeded(true);

        writer().writeLine(TestWriter.DEBUG,
            "At end of Test4.runTest()");
    }
}
