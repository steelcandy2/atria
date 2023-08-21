/*
 Copyright (C) 2001-2004 by James MacKay.

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
    The package test class that runs as sub-tests all of the test
    classes in its package named 'SpecialTestn' for n = 1, 2, ....

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class SpecialTests
    extends PackageTest
{
    // Constants

    /** The start of the name of all sub-test classes. */
    public static final String SUBTEST_NAME = "SpecialTest";


    // Constructors

    /**
        Constructs a PackageTest that uses a DefaultTestWriter for
        output and that uses the default sub-test name.
    */
    public SpecialTests()
    {
        super(SUBTEST_NAME);
    }


    // Public static methods

    /**
        This class' main method.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        SpecialTests t = new SpecialTests();
        EXECUTOR.executeAndExit(t, args);
    }
}
