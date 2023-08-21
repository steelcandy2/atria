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
    The second test for testing tests.

    @author James MacKay
    @see Tests
*/
public class SpecialTest2 extends SingleTest
{
    // Overridden methods

    /**
        @see Test#description
    */
    public String description()
    {
        return "a test used to test PackageTests using a specified " +
               "sub-test name (SpecialTest, here). This test always FAILS";
    }

    /**
        @see AbstractTest#runTest
    */
    protected void runTest()
    {
        writer().writeLine(TestWriter.DEBUG,
            "At start of SpecialTest2.runTest()");

        // This test always fails.
        setSucceeded(false);

        writer().writeLine(TestWriter.DEBUG,
            "At end of SpecialTest2.runTest()");
    }
}
