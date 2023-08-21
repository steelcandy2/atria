/*
 Copyright (C) 2001-2002 by James MacKay.

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

import com.steelcandy.common.*;

import com.steelcandy.common.creation.ObjectCreator;
import com.steelcandy.common.creation.ObjectCreatorException;
import com.steelcandy.common.creation.ReflectionUtilities;

import com.steelcandy.common.io.Io;

/**
    A base class for tests that perform all of the Tests in a package
    whose names follow a specific pattern.
    <p>
    An instance of this class of Test is constructed with the name
    its sub-tests' classes are to start with: here we'll assume that
    this name is 'TestName'. Starting with i = 1, when the test is
    run it will try to instantiate an instance of a Test class named
    'TestNamei' (where the 'i' in the name is the value of 'i', initially
    1) in the same package as the test using that class' default
    constructor. If it is successful then it runs that test as its next
    sub-test and repeat this process for i + 1; otherwise the test
    assumes that it has reached the end of its sub-tests and thus that
    it is done.
    <p>
    Subclasses don't have to implement anything (except constructors),
    though they may want to override the name() method in some cases.
    Even if no methods are overridden it is important that a subclass
    of this class be used to run the tests in a package since the class'
    package's name is used to construct the names of its sub-tests.

    @author James MacKay
*/
public class PackageTest extends AbstractTest
{
    // Constants

    /** The default name for sub-tests. */
    public static final String DEFAULT_SUBTEST_NAME = "Test";

    /** The resources used by this class. */
    private static final Resources _resources =
        TestingResourcesLocator.resources;

    /** Resource keys. */
    private static final String SUBTEST_CREATE_FAILED_MSG =
        "SUBTEST_CREATE_FAILED_MSG";
    private static final String NOT_A_TEST_MSG = "NOT_A_TEST_MSG";
    private static final String CANNOT_GET_INFO_MSG =
        "CANNOT_GET_INFO_MSG";
    private static final String TREATED_AS_FAILED_MSG =
        "TREATED_AS_FAILED_MSG";


    // Private fields

    /** The (start of the) names of this test's sub-tests' classes' names. */
    private String _subtestName;

    /** This test's number of sub-tests (so far). */
    private int _numSubtests = 0;

    /** The number of successful sub-tests that have been run so far. */
    private int _numSuccessfulSubtests = 0;


    // Constructors

    /**
        Constructs a PackageTest that uses a DefaultTestWriter for
        output and that uses the default sub-test name.

        @see #DEFAULT_SUBTEST_NAME
        @see DefaultTestWriter
    */
    public PackageTest()
    {
        this(DEFAULT_SUBTEST_NAME);
    }

    /**
        Constructs a PackageTest that uses a DefaultTestWriter for
        output and that uses the specified name as its sub-tests' name.

        @param subtestName the name the test's sub-tests are
        expected to start with
        @see DefaultTestWriter
    */
    public PackageTest(String subtestName)
    {
        this(subtestName, new DefaultTestWriter());
    }

    /**
        Constructs a PackageTest that uses the specified TestWriter
        for output and that uses the default sub-test name.

        @param writer the writer the test should use for output
        @see #DEFAULT_SUBTEST_NAME
    */
    public PackageTest(TestWriter writer)
    {
        this(DEFAULT_SUBTEST_NAME, writer);
    }

    /**
        Constructs a PackageTest that uses the specified TestWriter
        for output and that uses the specified name as its sub-tests'
        name.

        @param subtestName the name the test's sub-tests are
        expected to start with
        @param writer the writer the test should use for output
    */
    public PackageTest(String subtestName, TestWriter writer)
    {
        super(writer);
        Assert.require(subtestName != null);

        _subtestName = subtestName;

        // Uncomment the following line to enable debug output
        writer.setWriter(TestWriter.DEBUG, Io.err);
    }


    // Protected methods

    /**
        @return this class' package's name
    */
    protected String packageName()
    {
        return ReflectionUtilities.getPackageName(getClass());
    }


    // Overridden methods

    /**
        The name of a PackageTest is its fully-qualified class name.

        @see AbstractTest#name
    */
    public String name()
    {
        return getClass().getName();
    }

    /**
        @see Test#description
    */
    public String description()
    {
        return "Package " + packageName() + " " + _subtestName + " tests";
    }

    /**
        @see Test#outputAllInformation
    */
    public void outputAllInformation(int level)
    {
        // Output information about this test ...
        outputInformation(level);

        // ... then the output information for all of its sub-tests.
        int subtestNumber = 1;
        while (true)
        {
            try
            {
                writer().writeLine(TestWriter.DEBUG,
                    "trying to create sub-test #" + subtestNumber);
                Test subtest = createSubtest(subtestNumber);
                if (subtest == null)
                {
                    writer().writeLine(TestWriter.DEBUG,
                        "   sub-test doesn't exist");
                    break;  // there are no more sub-tests
                }
                else
                {
                    writer().writeLine(TestWriter.DEBUG,
                        "   sub-test exists. Outputting its information");
                    subtest.setWriter(writer());
                    subtest.outputInformation(level + 1);
                }
            }
            catch (ObjectCreatorException ex)
            {
                Object[] args = new Object[]
                {
                    new Integer(subtestNumber),
                    ex.getLocalizedMessage()
                };
                String msg =
                    _resources.getMessage(SUBTEST_CREATE_FAILED_MSG, args);
                writer().writeLine(TestWriter.ERROR, msg);

                msg = "  " + _resources.getMessage(CANNOT_GET_INFO_MSG);
                writer().writeLine(TestWriter.ERROR, msg);
            }
            catch (ClassCastException ex)
            {
                Object[] args = new Object[]
                {
                    new Integer(subtestNumber),
                    Test.class.getName()
                };
                String msg = _resources.getMessage(NOT_A_TEST_MSG, args);
                writer().writeLine(TestWriter.ERROR, msg);

                msg = "  " + _resources.getMessage(CANNOT_GET_INFO_MSG);
                writer().writeLine(TestWriter.ERROR, msg);
            }
            subtestNumber += 1;
        }   // while (true)
    }


    /**
        @see AbstractTest#runTest
    */
    protected void runTest()
        throws Throwable
    {
        TestWriter subtestWriter = obtainSubtestWriter(writer());
        while (true)
        {
            try {
                writer().write(TestWriter.DEBUG,
                    "trying to create and run sub-test #" + (_numSubtests + 1));
                Test subtest = createSubtest(_numSubtests + 1);
                if (subtest == null)
                {
                    writer().writeLine(TestWriter.DEBUG,
                        "   sub-test doesn't exist");
                    break;  // there are no more sub-tests
                }
                else
                {
                    writer().writeLine(TestWriter.DEBUG,
                        "   sub-test exists. Performing it");
                    subtest.setWriter(subtestWriter);
                    subtest.perform();
                    if (subtest.succeeded())
                    {
                        _numSuccessfulSubtests += 1;
                    }
                }
            }
            catch (ObjectCreatorException ex)
            {
                // The sub-test could not be created for a reason
                // other than that its class doesn't exist. We still
                // count it as a sub-test (since its class exists),
                // but not as a successful one.
                Object[] args = new Object[]
                {
                    new Integer(_numSubtests + 1),
                    ex.getLocalizedMessage()
                };
                String msg =
                    _resources.getMessage(SUBTEST_CREATE_FAILED_MSG, args);
                writer().writeLine(TestWriter.ERROR, msg);

                msg = "  " + _resources.getMessage(TREATED_AS_FAILED_MSG);
                writer().writeLine(TestWriter.ERROR, msg);
            }
            catch (ClassCastException ex)
            {
                // The sub-test could be created, but it isn't a Test.
                // We still count it as a sub-test (since its class
                // exists), but not as a successful one.
                Object[] args = new Object[]
                {
                    new Integer(_numSubtests + 1),
                    Test.class.getName()
                };
                String msg = _resources.getMessage(NOT_A_TEST_MSG, args);
                writer().writeLine(TestWriter.ERROR, msg);

                msg = "  " + _resources.getMessage(TREATED_AS_FAILED_MSG);
                writer().writeLine(TestWriter.ERROR, msg);
            }
            _numSubtests += 1;
        }
    }

    /**
        @see AbstractTest#testSucceeded
    */
    protected boolean testSucceeded()
    {
        return (_numSuccessfulSubtests == _numSubtests);
    }

    /**
        @see AbstractTest#testCount
    */
    protected int testCount()
    {
        return _numSubtests;
    }

    /**
        @see AbstractTest#successfulTestCount
    */
    protected int successfulTestCount()
    {
        return _numSuccessfulSubtests;
    }


    // Protected methods

    /**
        Creates and returns an instance of the sub-test class
        for the specified sub-test number, or returns null if
        there is no such class.
        <p>
        The sub-tests' classes' default constructors are used
        to (try to) construct the sub-test class instances.

        @param subtestNumber the number of the sub-test to
        create a Test object for
        @return the subtest that was created, or null if the
        sub-test's class doesn't exist
        @exception ClassCastException if the sub-test's class
        is not a subclass of Test
        @exception ObjectCreatorException thrown if the sub-test
        could not be created for some reason other than that the
        sub-test's class doesn't exist
    */
    protected Test createSubtest(int subtestNumber)
        throws ObjectCreatorException, ClassCastException
    {
        Test result = null;

        // Construct the fully-qualified name of the sub-test class.
        String className = _subtestName + Integer.toString(subtestNumber);
        className = ReflectionUtilities.
                        constructClassName(packageName(), className);

        try
        {
            result = (Test) ObjectCreator.instance().create(className);
        }
        catch (ObjectCreatorException ex)
        {
            // If the class doesn't exist then it just means that
            // we've reached the end of the sub-tests. Otherwise
            // throw the exception to indicate that there's (probably)
            // something wrong with the sub-test class.
            if (ex.reason() != ObjectCreatorException.CLASS_NOT_FOUND)
            {
                throw ex;
            }
        }
        return result;
    }

    /**
        Returns the TestWriter that all sub-tests are to use for
        output.
        <p>
        If a modified version of this test's writer is to be
        used as the sub-tests' writer then it should be clone()d
        before it is modified. (Otherwise this test's writer will
        be modified as well.)

        @param writer the TestWriter used by this test for output
        @return the TestWriter to be used for output by all of
        this test's sub-tests
        @see TestWriter#cloneWriter
    */
    protected TestWriter obtainSubtestWriter(TestWriter writer)
    {
        // The sub-test writer is the same as our writer except
        // that the sub-tests' SUMMARY information is output as
        // RESULTS.
        TestWriter result = writer().cloneWriter();
        result.setWriter(TestWriter.SUMMARY, writer(), TestWriter.RESULTS);
        return result;
    }
}
