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

package com.steelcandy.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.*;
import com.steelcandy.common.creation.ReflectionUtilities;
import com.steelcandy.common.text.TextUtilities;

import com.steelcandy.common.program.Program;
import com.steelcandy.common.program.ProgramException;

import java.util.Date;

/**
    An abstract base class for Tests.
    <p>
    This class provides commonly-used implementations for several
    of the methods declared in the Test interface, but of course
    subclasses can override those implementations. Usually all Tests
    subclass this class.
    <p>
    In several cases instead of subclasses implementing Test methods
    directly, they should implement corresponding (usually abstract)
    methods in this class. (The Test methods are implemented in this
    class to delegate to the corresponding methods in this class.)
    Thus subclasses should implement runTest() instead of run(),
    testSucceeded() in place of succeeded(), successfulTestCount()
    instead of numberOfSuccessfulTests(), testCount() instead of
    numberOfTests(), etc.

    @author James MacKay
    @version $Revision: 1.10 $
*/
public abstract class AbstractTest
    implements Test, Program
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TestingResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        TEST_INFO_1 =
            "TEST_INFO_1";
    private static final String
        TEST_INFO_2 =
            "TEST_INFO_2";

    private static final String
        TEST_START_TIMING_MSG =
            "TEST_START_TIMING_MSG";
    private static final String
        TEST_FINISH_TIMING_MSG =
            "TEST_FINISH_TIMING_MSG";

    private static final String
        TEST_SUCCEEDED_MSG =
            "TEST_SUCCEEDED_MSG";
    private static final String
        TEST_FAILED_MSG =
            "TEST_FAILED_MSG";
    private static final String
        MULTIPLE_TEST_SUMMARY_MSG =
            "MULTIPLE_TEST_SUMMARY_MSG";

    private static final String
        TEST_ERROR_MSG =
            "TEST_ERROR_MSG";
    private static final String
        MISSING_EXCEPTION_MSG =
            "MISSING_EXCEPTION_MSG";

    private static final String
        ARGUMENTS_SUMMARY_MSG =
            "ARGUMENTS_SUMMARY_MSG";
    private static final String
        USAGE_DESCRIPTION_MSG =
            "USAGE_DESCRIPTION_MSG";


    // Private fields

    /**
        The TestWriter that this test uses to output information.
    */
    private TestWriter _writer;

    /**
        Indicates whether this test's run() method has been called
        and has completed.
    */
    private boolean _hasBeenRun = false;


    // Constructors

    /**
        Constructs an AbstractTest that uses a DefaultTestWriter
        for output.
    */
    public AbstractTest()
    {
        this(new DefaultTestWriter());
    }

    /**
        Constructs an AbstractTest that uses the specified
        TestWriter for output.

        @param writer the TestWriter the test should use for
        output
    */
    public AbstractTest(TestWriter writer)
    {
        setWriter(writer);
    }


    // Test information methods

    /**
        By default a test's name is its unqualified class name.

        @see Test#name
    */
    public String name()
    {
        return ReflectionUtilities.getUnqualifiedClassName(getClass());
    }

    /**
        @see Test#outputInformation
    */
    public void outputInformation(int level)
    {
        String indent = indentation(level, SPACES_PER_INDENT_LEVEL);
        Object[] args =
        {
            indent,
            name(),
            getClass().getName()
        };
        String msg = _resources.getMessage(TEST_INFO_1, args);
        writer().writeLine(TestWriter.INFORMATION, msg);
        msg = _resources.getMessage(TEST_INFO_2, indent, description());
        writer().writeLine(TestWriter.INFORMATION, msg);
    }


    // Test setup methods

    /**
        By default an AbstractTest's setup() method does nothing
        (except check this method's preconditions, so methods that
        override this one should still call its superclass' version
        of this method).

        @see Test#setup
    */
    public void setup()
    {
        Assert.require(hasBeenRun() == false);

        writer().writeLine(TestWriter.DEBUG, "AbstractTest.setup() called");
    }

    /**
        @see Test#setWriter
    */
    public void setWriter(TestWriter writer)
    {
        Assert.require(writer != null);

        // We don't write a DEBUG message here since we may not
        // have our desired DEBUG Writer set yet.
/*
        // Since this method is called to initially set this test's
        // writer, we have to check that writer() isn't null.
        if (writer() != null)
        {
            writer().writeLine(TestWriter.DEBUG,
                "AbstractTest.setWriter() called");
        }
*/

        _writer = writer;
    }


    // Test execution methods

    /**
        @see Test#perform
    */
    public void perform()
    {
        Assert.require(hasBeenRun() == false);

        setup();
        run();
        cleanup();

        Assert.ensure(hasBeenRun());
    }

    /**
        Subclasses should not override this method: instead
        they should implement/override the runTest() method.

        @see Test#run
        @see #runTest
    */
    public final void run()
    {
        Assert.require(hasBeenRun() == false);
        try
        {
            String msg = _resources.getMessage(TEST_START_TIMING_MSG,
                                               name(), new Date());
            writer().writeLine(TestWriter.TIMING, msg);
            runTest();
        }
        catch (Throwable ex)
        {
            reportTestError(ex);
        }
        finally
        {
            Date now = new Date();
            String msg = _resources.getMessage(TEST_FINISH_TIMING_MSG,
                                               name(), now);
            writer().writeLine(TestWriter.TIMING, msg);
            _hasBeenRun = true;
        }

        // Output a summary of the results of the test.
        int numTests = numberOfTests();
        if (numTests == 1)
        {
            String msgId = succeeded() ? TEST_SUCCEEDED_MSG : TEST_FAILED_MSG;
            writer().writeLine(TestWriter.SUMMARY,
                               _resources.getMessage(msgId));
        }
        else if (numTests >= 0)
        {
            Object[] args = new Object[]
            {
                new Integer(numTests),
                new Integer(numberOfSuccessfulTests())
            };
            String msg = _resources.
                getMessage(MULTIPLE_TEST_SUMMARY_MSG, args);
            writer().writeLine(TestWriter.SUMMARY, msg);
        }

        Assert.ensure(hasBeenRun());
    }

    /**
        Actually runs this test. This run() method delegates to
        this method after checking its preconditions, and also
        keeps track of when this method's run() method is done,
        and reports (as error output) any exceptions that this
        method throws as

        @exception Throwable an exception/error that is to be
        reported as part of this test's error output
        @see #run
    */
    protected abstract void runTest()
        throws Throwable;

    /**
        @see Test#hasBeenRun
    */
    public boolean hasBeenRun()
    {
        return _hasBeenRun;
    }


    // Test cleanup methods

    /**
        By default an AbstractTest's cleanup() method does nothing
        (except check this method's preconditions, so methods that
        override this one should still call its superclass' version
        of this method).

        @see Test#cleanup
    */
    public void cleanup()
    {
        Assert.require(hasBeenRun());

        writer().writeLine(TestWriter.DEBUG, "AbstractTest.cleanup() called");
    }


    // Test results methods

    /**
        Reports the specified error or exception that was thrown
        from this test's runTest() method.

        @param ex the error/exception thrown by this test's runTest()
        method
    */
    protected void reportTestError(Throwable ex)
    {
        String msg = _resources.getMessage(TEST_ERROR_MSG,
                                           ex.getClass().getName());
        writer().writeLine(TestWriter.ERROR, msg);
        msg = ex.getLocalizedMessage();
        if (msg == null)
        {
            msg = _resources.getMessage(MISSING_EXCEPTION_MSG);
        }
        writer().writeLine(TestWriter.ERROR, "  " + msg);
        ex.printStackTrace();
    }

    /**
        This method should not be overridden: instead the
        testSucceeded() method should be implemented.

        @see Test#succeeded
        @see #testSucceeded
    */
    public final boolean succeeded()
    {
        Assert.require(hasBeenRun());

        boolean result = testSucceeded();

        Assert.ensure(result == false || successfulTestCount() == testCount());
        return result;
    }

    /**
        Indicates whether this test succeeded.

        @return true iff this test succeeded
        @see #succeeded
    */
    protected abstract boolean testSucceeded();

    /**
        This method should not be overridden: instead the
        testCount() method should be implemented.

        @see Test#numberOfTests
        @see #testCount
    */
    public int numberOfTests()
    {
        Assert.require(hasBeenRun());

        int result = testCount();

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @return the number of tests that were run as part of this test
        @see #numberOfTests
    */
    protected abstract int testCount();

    /**
        This method should not be overridden: instead the
        successfulTestCount() method should be implemented.

        @see Test#numberOfSuccessfulTests
        @see #successfulTestCount
    */
    public final int numberOfSuccessfulTests()
    {
        Assert.require(hasBeenRun());

        int result = successfulTestCount();

        Assert.ensure(result <= testCount());
        Assert.ensure(result == testCount() || testSucceeded() == false);
        return result;
    }

    /**
        @return the number of tests that were run as part of this test
        and that were successful
        @see #numberOfSuccessfulTests
    */
    protected abstract int successfulTestCount();


    // Test results output methods

    /**
        @return the TestWriter this test is to use to output
        any and all information
    */
    protected TestWriter writer()
    {
        return _writer;
    }


    // Utility methods

    /**
        @see Program#execute(String[])
    */
    public void execute(String[] args)
        throws ProgramException
    {
        Assert.require(args != null);

        try
        {
            if (args.length == 0)
            {
                writer().writeLine(TestWriter.DEBUG,
                                   "Running tests ...");
                perform();
            }
            else if (args.length == 1 && args[0].equals("info"))
            {
                writer().writeLine(TestWriter.DEBUG,
                                   "Outputting test info ...");
                outputAllInformation(0);
            }
            else
            {
                throw EXECUTOR.createBadUsageException(this);
            }
        }
        catch (Throwable ex)
        {
            throw EXECUTOR.createFailureException(this,
                    EXECUTOR.unexpectedExceptionMessage(ex), ex);
        }
    }

    /**
        @see Program#argumentsSummary
    */
    public String argumentsSummary()
    {
        String result = _resources.
            getMessage(ARGUMENTS_SUMMARY_MSG);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Program#usageDescription
    */
    public String usageDescription()
    {
        String result = _resources.
            getMessage(USAGE_DESCRIPTION_MSG);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the indentation string with which to prefix lines
        that are to be indented the specified number of levels.

        @param numLevels the number of levels of indentation that
        the indentation string is to represent
        @param spacesPerLevel the number of spaces in each level of
        indentation
    */
    protected static String indentation(int numLevels, int spacesPerLevel)
    {
        Assert.require(numLevels >= 0);
        Assert.require(spacesPerLevel >= 0);

        return TextUtilities.copies(' ', numLevels * spacesPerLevel);
    }
}
