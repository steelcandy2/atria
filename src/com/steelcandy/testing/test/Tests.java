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
    classes in its package named 'Testn' for n = 1, 2, ....

    @author James MacKay
*/
public class Tests
    extends PackageTest
{
    // Constructors

    /**
        Constructs a PackageTest that uses a DefaultTestWriter for
        output and that uses the default sub-test name.
    */
    public Tests()
    {
        // empty
    }


    // Public static methods

    /**
        This class' main method.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        Tests t = new Tests();
        EXECUTOR.executeAndExit(t, args);
    }
}
