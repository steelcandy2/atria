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
    The interface implemented by all tests.
    <p>
    A test's name should be a reasonably short and human-friendly
    name for the test, and a test's description should be a human-
    friendly one- or two-line description of what the test tests.
    <p>
    In order to run a Test, the following methods must be called
    in order:
    <ul>
        <li>setup()
        <li>run()
        <li>cleanup()
    </ul>
    The perform() method should be implemented to do these three
    things in order.
    <p>
    The hasBeenRun() method should return false until the test's
    run() method has been called and has completed, and should
    return true thereafter.
    <p>
    A given instance of a Test class cannot be used more than once:
    to rerun a test, use a new instance of the class. (This is intended
    to simplify the implementation of test classes, and to help ensure
    that a test can be consistently repeated.)
    <p>
    A test's results cannot be retrieved until after the test's run()
    method has been executed.
    <p>
    A test can have zero or more sub-tests that are (presumably)
    run as part of running the test itself. It is usually a good
    idea for a test to either just perform sub-tests or just perform
    a single test, but not both, though there is (currently) no
    restrictions pertaining to this.

    @author James MacKay
    @version $Revision: 1.5 $
*/
public interface Test
{
    // Constants

    /**
        The number of spaces in each level of indentation in test
        output.
    */
    public static final int SPACES_PER_INDENT_LEVEL = 2;


    // Test information methods

    /**
        @return this test's name
    */
    public String name();
        // Assert.require(result != null);

    /**
        @return a (one or two line) description of this test
    */
    public String description();
        // Assert.require(result != null);

    /**
        Outputs the information (name, description, etc.) about
        the current test and all of its sub-tests (and sub-sub-tests,
        etc.). The test's writer for output of type INFORMATION
        should be used to output the information.

        @param level the number of levels this test is below the
        topmost test for which information is being output. For
        example, the topmost test is at level 0, its sub-tests
        are at level 1, its sub-sub-tests are at level 2, etc.
        (This value is often used for indenting the test's information)
        @see #outputInformation
    */
    public void outputAllInformation(int level);

    /**
        Outputs the information (name, description, etc.) about
        the current test <em>only</em>. The test's writer for output
        of type INFORMATION should be used to output the information.

        @param level the number of levels this test is below the
        topmost test for which information is being output. For
        example, the topmost test is at level 0, its sub-tests
        are at level 1, its sub-sub-tests are at level 2, etc.
        (This value is often used for indenting the test's information)
        @see #outputAllInformation
    */
    public void outputInformation(int level);


    // Test setup methods

    /**
        Sets up everything that this test needs before it is run().

        @see #run
    */
    public void setup();
        // Assert.require(hasBeenRun() == false);

    /**
        Sets the TestWriter that this test is to use to output its
        various types of output.

        @param newWriter the writer to use to output information
    */
    public void setWriter(TestWriter newWriter);


    // Test execution methods

    /**
        Performs this test: it sets it up, runs it, then cleans
        it up.

        @see #setup
        @see #run
        @see #cleanup
    */
    public void perform();
        // Assert.require(hasBeenRun() == false);
        // Assert.ensure(hasBeenRun());

    /**
        Runs this test.
    */
    public void run();
        // Assert.require(hasBeenRun() == false);

    /**
        Indicates whether this test's run() method has been called
        and has completed.

        @return true iff this test's run() method has been called
        and has completed
    */
    public boolean hasBeenRun();


    // Test cleanup methods

    /**
        Cleans up after this test has been run().

        @see #run
    */
    public void cleanup();
        // Assert.require(hasBeenRun());


    // Test results methods

    /**
        Indicates whether the test was successful.
        <p>
        A test with sub-tests should be considered successful only
        if all of its subtests were successful (but note that a test
        can fail even if all of its sub-tests succeed).

        @return true iff this test has been run() and was successful
    */
    public boolean succeeded();
        // Assert.require(hasBeenRun());

    /**
        Once this test has been run(), returns the number of tests
        that were run as part of this test, including all of its
        sub-tests, and this test as well iff it does more than just
        run its sub-tests.
        <p>
        This method cannot be called until after this test has been
        run() to allow for tests that (for example) don't know how
        many subtests it has until it's finished executing them.

        @return the number of tests that were run as part of this test
    */
    public int numberOfTests();
        // Assert.require(hasBeenRun());

    /**
        @return the number of tests that were run as part of this test
        and that were successful
    */
    public int numberOfSuccessfulTests();
        // Assert.require(hasBeenRun());
        // Assert.ensure(result <= numberOfTests());
        // Assert.ensure(result < numberOfTests() || succeeded());
}
