/*
 Copyright (C) 2002-2005 by James MacKay.

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

package com.steelcandy.common.program;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;

import java.io.*;
import java.text.MessageFormat;

/**
    A singleton class that can be used to execute programs, usually (though
    not necessarily) from a main() method. It also provides some program-
    related utility methods.
    <p>
    This class can execute both Program objects and external programs.
    <p>
    Note: if the system property named "program.name" is set then its value
    will be used in place of "java [class-name]" in the usage message. (This
    is useful when a script is used to run a program.)
    <p>
    TODO: add method that will execute (external) programs asynchronuously(?)

    @author James MacKay
    @version $Revision: 1.14 $
    @see Program#EXECUTOR
*/
public class ProgramExecutor
{
    // Exit code constants

    /**
        An external program's exit code if the thread that is waiting
        for the program to complete is interrupted while it is waiting.
        <p>
        Note that it is possible (if pretty unlikely) that an external
        program could exit with this same code. Thus an external program
        exiting with this code doesn't necessarily indicate that the
        thread executing it was interrupted.
    */
    public static final int INTERRUPTED_EXIT_CODE = 8932872;

    /**
        An external program's exit code if an I/O error occurred while
        trying to execute the program.
        <p>
        Note that it is possible (if pretty unlikely) that an external
        program could exit with this same code. Thus an external program
        exiting with this code doesn't necessarily indicate that an I/O
        error occurred while executing the program.
    */
    public static final int IO_ERROR_EXIT_CODE = 5794266;


    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        ProgramResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        USAGE_MESSAGE_FORMAT =
            "USAGE_MESSAGE_FORMAT";
    private static final String
        UNEXPECTED_EXCEPTION_MSG =
            "UNEXPECTED_EXCEPTION_MSG";
    private static final String
        FAILURE_FORMAT_MSG =
            "FAILURE_FORMAT_MSG";
    private static final String
        INTERPRETER_NAME_MSG =
            "INTERPRETER_NAME_MSG";

    /** The formatter to use to build usage messages. */
    private static final MessageFormat _usageMessageFormatter =
        new MessageFormat(_resources.getMessage(USAGE_MESSAGE_FORMAT));

    /** The name of the interpreter used to run a program by default. */
    private static final String
        INTERPRETER_NAME = _resources.getMessage(INTERPRETER_NAME_MSG);


    /**
        The name of the system property that, if present, specifies the name
        of a program.
    */
    private static final String
        PROGRAM_NAME_PROPERTY = "program.name";

    /** The sole instance of this class. */
    private static final ProgramExecutor
        _instance = new ProgramExecutor();


    // Constructors

    /**
        @return the sole instance of this class
    */
    public static ProgramExecutor instance()
    {
        return _instance;
    }

    /**
        Constructs the sole instance of this class.
    */
    private ProgramExecutor()
    {
        // empty
    }


    // Program execution methods

    /**
        Executes the specified program and returns an exit code that
        indicates whether it successfully completed or not.

        @param p the program to execute
        @param args the program's command line arguments
        @return zero if the program completed successfully, and non-zero if
        it didn't
    */
    public int execute(Program p, String[] args)
    {
        Assert.require(p != null);

        int result = 0;

        try
        {
// TODO: replace the following println()s with logging code !!!
// System.err.println("executing program of class " + p.getClass().getName() + " ...");
            p.execute(args);
// System.err.println("program executed successfully");
        }
        catch (BadProgramUsageException ex)
        {
// System.err.println("PROGRAM FAILED due to a BadProgramUsageException of class " + ex.getClass().getName() + ": [" + ex.getLocalizedMessage() + "]");
            reportAsErrorInformation(ex.getLocalizedMessage());
            result = 2;
        }
        catch (ProgramException ex)
        {
// System.err.println("PROGRAM FAILED due to a ProgramException of class " + ex.getClass().getName() + ": [" + ex.getLocalizedMessage() + "]");
            reportAsErrorInformation(ex.getLocalizedMessage());
//            reportExceptionAsErrorInformation(ex);
            result = 1;
        }
        catch (Throwable ex)
        {
            reportAsErrorInformation("PROGRAM FAILED due to an UNEXPECTED " +
                                     "exception:");
            ex.printStackTrace();  // this includes the exception class and message
            result = 3;
        }

// System.err.println("program exiting with exit code " + result);
        return result;
    }

    /**
        Executes the specified program with the specified arguments and then
        exits (using System.exit()).
        <p>
        System.exit() will be passed a zero exit code if the program
        completed successfully, and a non-zero one if it didn't.

        @param p the program to execute
        @param args the program's command line arguments
        @see System#exit(int)
    */
    public void executeAndExit(Program p, String[] args)
    {
        Assert.require(p != null);

        System.exit(execute(p, args));
    }


    // External program execution methods

    /**
        @see #execute(String, String[], File)
    */
    public int execute(String programName, String[] args)
    {
        return execute(programName, args, null);
    }

    /**
        Executes the external program with the specified pathname with the
        specified arguments and returns the exit code that the external
        program exited with.
        <p>
        Note: the exit code will be INTERRUPTED_EXIT_CODE if this thread was
        interrupted while waiting for the program to complete.

        @param programName the pathname of the external program to execute
        @param args the external program's command line arguments
        @param dir the external program's working directory: if it is null
        then the external program's working directory will be the current one
        @return the exit code that the external program exited with
    */
    public int execute(String programName, String[] args, File dir)
    {
        String[] command;
        if (args != null && args.length > 0)
        {
            command = new String[args.length + 1];
            command[0] = programName;
            for (int i = 0; i < args.length; i++)
            {
                command[i + 1] = args[i];
            }
        }
        else
        {
            command = new String[] { programName };
        }

        int result = 0;
        try
        {
            Process p = Runtime.getRuntime().exec(command, null, dir);
            result = p.waitFor();
        }
        catch (IOException ex)
        {
            // TODO: log that an I/O error occurred while trying to
            // execute the program.
            result = IO_ERROR_EXIT_CODE;
        }
        catch (InterruptedException ex)
        {
            // TODO: log that this thread was interrupted while waiting for
            // the program to complete. We report an error since we don't
            // know whether the program succeeded or not.
            result = INTERRUPTED_EXIT_CODE;
        }
        return result;
    }

    /**
        @see #executeAndExit(String, String[], File)
    */
    public void executeAndExit(String programName, String[] args)
    {
        executeAndExit(programName, args, null);
    }

    /**
        Executes the external program with the specified pathname with the
        specified arguments and then exits (using System.exit()).
        <p>
        System.exit() will be passed the exit code that the external program
        exited with.

        @param programName the pathname of the external program to execute
        @param args the external program's command line arguments
        @param dir the external program's working directory: if it is null
        then the external program's working directory will be the current one
        @see System#exit(int)
    */
    public void executeAndExit(String programName, String[] args, File dir)
    {
        Assert.require(programName != null);

        System.exit(execute(programName, args, dir));
    }


    // Program implementation utility methods

    /**
        Reports the specified message in the same way (e.g. using the same
        output stream) as a program error or failure would be reported. It
        will not in and of itself cause the program to fail, however.

        @param msg the message to report
    */
    public void reportAsErrorInformation(String msg)
    {
        Assert.require(msg != null);

        System.err.println(msg);
    }

    /**
        Reports the specified exception in the same way (e.g. using the same
        output stream) as a program error of failure would be reported. It
        will not in and of itself cause the program to fail, however.

        @param ex the exception to report
    */
    protected void reportExceptionAsErrorInformation(ProgramException ex)
    {
        Assert.require(ex != null);

        Throwable nestedEx = ex.getCause();
        if (nestedEx != null)
        {
            nestedEx.printStackTrace();
        }
    }

    /**
        Throws a BadProgramUsageException for the specified program.

        @param p a program that was incorrectly used
        @param ex the exception that originally signalled the bad program
        usage
        @return a BadProgramUsageException to throw to indicate that 'p'
        was incorrectly used
    */
    public BadProgramUsageException
        createBadUsageException(Program p, Throwable ex)
    {
        Assert.require(p != null);
        Assert.require(ex != null);

        String msg =
            formatFailureMessage(ex.getLocalizedMessage()) + usageMessage(p);

        // Assert.ensure(result != null);
        return new BadProgramUsageException(msg, ex);
    }

    /**
        Throws a BadProgramUsageException for the specified program.

        @param p a program that was incorrectly used
        @param errorMessage if non-null, a more specific description of why
        the program arguments were considered invalid
        @return a BadProgramUsageException to throw to indicate that 'p'
        was incorrectly used
    */
    public BadProgramUsageException
        createBadUsageException(Program p, String errorMessage)
    {
        Assert.require(p != null);
        // 'errorMessage' can be null

        String msg = usageMessage(p);
        if (errorMessage != null)
        {
            msg = formatFailureMessage(errorMessage) + msg;
        }

        // Assert.ensure(result != null);
        return new BadProgramUsageException(msg);
    }

    /**
        @see #createBadUsageException(Program, String)
    */
    public BadProgramUsageException createBadUsageException(Program p)
        throws BadProgramUsageException
    {
        Assert.require(p != null);

        BadProgramUsageException result =
            createBadUsageException(p, (String) null);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Throws a ProgramFailedException with the specified message (after
        formatting it) to indicate that the specified program failed.

        @param p a program that failed
        @param msg the (unformatted) message that describes why 'p' failed
        @param ex if non-null, the exception that caused 'p' to fail
        @return a ProgramFailedException to throw to indicate that 'p'
        failed
    */
    public ProgramFailedException
        createFailureException(Program p, String msg, Throwable ex)
    {
        Assert.require(p != null);
        Assert.require(msg != null);
        // 'ex' can be null

        // Note: we don't currently use 'p' or 'ex'.

        // Assert.ensure(result != null);
        return new ProgramFailedException(formatFailureMessage(msg));
    }

    /**
        @see #createFailureException(Program, String, Throwable)
    */
    public ProgramFailedException
        createFailureException(Program p, String msg)
    {
        Assert.require(p != null);
        Assert.require(msg != null);

        ProgramFailedException result = createFailureException(p, msg, null);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see #createFailureException(Program, String, Throwable)
    */
    public ProgramFailedException
        createFailureException(Program p, Throwable ex)
    {
        Assert.require(p != null);
        Assert.require(ex != null);

        String msg =
            formatFailureMessage(ex.getLocalizedMessage());

        // Assert.ensure(result != null);
        return new ProgramFailedException(msg, ex);
    }

    /**
        Returns the specified program's usage message.

        @param p the program whose usage message is to be returned
        @return the program's usage message
    */
    public String usageMessage(Program p)
    {
        Assert.require(p != null);

        String programName = System.getProperty(PROGRAM_NAME_PROPERTY,
                    INTERPRETER_NAME + " " + p.getClass().getName());
        Object[] args = new Object[]
        {
            programName,
            p.argumentsSummary(),
            p.usageDescription()
        };

        return _usageMessageFormatter.format(args);
    }

    /**
        Formats the specified message so that it is an appropriate message to
        construct a ProgramFailedException from.

        @param msg the message to format
        @return the result of formatting msg
    */
    public String formatFailureMessage(String msg)
    {
        return _resources.getMessage(FAILURE_FORMAT_MSG, msg);
    }

    /**
        Returns a message that explains that a program failed because the
        specified unexpected exception was thrown. The message is
        <em>not</em> formatted: if it is to be used to construct a
        ProgramFailedException then it should first be passed to
        formatFailureMessage() to format it.

        @param ex the unexpected exception
        @return a message (suitable for use in constructing a
        ProgramFailedException) that explains that the program failed because
        the exception was thrown
        @see #formatFailureMessage(String)
    */
    public String unexpectedExceptionMessage(Throwable ex)
    {
        return _resources.
            getMessage(UNEXPECTED_EXCEPTION_MSG,
                       ex.getClass().getName(),
                       ProgramException.message(ex),
                       ProgramException.stackTrace(ex));
    }
}
