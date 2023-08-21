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
    A class used to test the Assert class.
    <p>
    TODO: add a method to test Assert.unreachable().

    @author James MacKay
*/
public class Tester
{
    // Constructors

    /**
        Constructs a Tester.
    */
    public Tester()
    {
        // empty
    }


    // Public methods

    /**
        Runs all of this object's tests.
    */
    public void testAll()
    {
        testPreconditions();
        testChecks();
        testPostconditions();
    }

    /**
        Tests the handling of preconditions.
    */
    public void testPreconditions()
    {
        // These preconditions should never fail.
        Assert.require(0 == 0, "0 == 0");
        Assert.require(true);

        // These preconditions should fail iff preconditions are
        // being checked.
        try
        {
            Assert.require(1 < 1);
            System.out.println("Preconditions are NOT being checked - " +
                                "the precondition '1 < 1' did not fail");
        }
        catch (PreconditionFailedException ex)
        {
            System.out.println("Preconditions are being checked - " +
                                "the precondition '1 < 1' failed: " +
                                ex.getMessage());
        }
        try
        {
            Assert.require(true == !true, "true == !true");
            System.out.println("Preconditions are NOT being checked - " +
                                "the precondition 'true == !true' " +
                                "did not fail");
        }
        catch (PreconditionFailedException ex)
        {
            System.out.println("Preconditions are being checked - " +
                                "the precondition 'true == !true' failed: " +
                                ex.getMessage());
        }
    }

    /**
        Tests the handling of assertion checks.
    */
    public void testChecks()
    {
        // These assertion checks should never fail.
        Assert.check(0 == 0, "0 == 0");
        Assert.check(true);

        // These assertion checks should fail iff assertions are
        // being checked:
        try
        {
            Assert.check(1 < 1);
            System.out.println("Assertions are NOT being checked - " +
                                "the assertion '1 < 1' did not fail");
        }
        catch (AssertionFailedException ex)
        {
            System.out.println("Assertions are being checked - " +
                                "the assertion '1 < 1' failed: " +
                                ex.getMessage());
        }
        try
        {
            Assert.check(true == !true, "true == !true");
            System.out.println("Assertions are NOT being checked - " +
                                "the assertion 'true == !true' " +
                                "did not fail");
        }
        catch (AssertionFailedException ex)
        {
            System.out.println("Assertions are being checked - " +
                                "the assertion 'true == !true' failed: " +
                                ex.getMessage());
        }
    }

    /**
        Tests the handling of postconditions.
    */
    public void testPostconditions()
    {
        // These postconditions should never fail.
        Assert.ensure(0 == 0, "0 == 0");
        Assert.ensure(true);

        // These postconditions should fail iff postconditions are
        // being checked.
        try
        {
            Assert.ensure(1 < 1);
            System.out.println("Postconditions are NOT being checked - " +
                                "the postcondition '1 < 1' did not fail");
        }
        catch (PostconditionFailedException ex)
        {
            System.out.println("Postconditions are being checked - " +
                                "the postcondition '1 < 1' failed: " +
                                ex.getMessage());
        }
        try
        {
            Assert.ensure(true == !true, "true == !true");
            System.out.println("Postconditions are NOT being checked - " +
                                "the postcondition 'true == !true' " +
                                "did not fail");
        }
        catch (PostconditionFailedException ex)
        {
            System.out.println("Postconditions are being checked - " +
                                "the postcondition 'true == !true' failed: " +
                                ex.getMessage());
        }
    }


    // Main method

    /**
        Creates a Tester object and runs all of its tests.

        @param args the command line arguments
        @see #testAll
    */
    public static void main(String[] args)
    {
        Tester t = new Tester();
        t.testAll();
    }
}
