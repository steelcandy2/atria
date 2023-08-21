/*
 Copyright (C) 2002-2008 by James MacKay.

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

package com.steelcandy.plack.common.constructs.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.PlackInternalError;
import com.steelcandy.plack.common.errors.WriterErrorHandler;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceCodeString;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructWriter;
import com.steelcandy.plack.common.constructs.TextConstructWriter;
import com.steelcandy.plack.common.constructs.XmlConstructWriter;

import com.steelcandy.common.Resources;
import com.steelcandy.common.SteelCandyException;

import com.steelcandy.common.program.Program;
import com.steelcandy.common.program.ProgramException;

import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.Io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Random;

/**
    The abstract base class for classes that generate test data used to test
    the constructs of a language.
    <p>
    Note: the methods in this class that write out and/or append data start
    with 'doWrite' and 'doAppend' rather than 'write' and 'append' so that
    the latter prefixes can be used by subclass methods that write specified
    constructs and construct parts without danger of inadvertently overriding
    one of this class' methods.

    @author James MacKay
*/
public abstract class AbstractConstructTestDataCreator
    implements Program
{
    // Space type constants

    /** The type of space that is never to be output. */
    protected static final int NO_SPACE = 0;

    /** The type of space that is not allowed to be output. */
    protected static final int DISALLOWED_SPACE = 1;

    /** The type of space that should not be output. */
    protected static final int DISCOURAGED_SPACE = 2;

    /** The type of space that should be output. */
    protected static final int ENCOURAGED_SPACE = 3;

    /** The type of space that must be output. */
    protected static final int REQUIRED_SPACE = 4;


    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonConstructTestingResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TEST_DATA_CREATOR_ARGUMENTS_SUMMARY =
            "TEST_DATA_CREATOR_ARGUMENTS_SUMMARY";
    private static final String
        TEST_DATA_CREATOR_USAGE_DESCRIPTION =
            "TEST_DATA_CREATOR_USAGE_DESCRIPTION";
    private static final String
        TEST_DATA_CREATION_IO_ERROR_MSG =
            "TEST_DATA_CREATION_IO_ERROR_MSG";
    private static final String
        COULD_NOT_GET_TOKEN_FROM_SOURCE_MSG =
            "COULD_NOT_GET_TOKEN_FROM_SOURCE_MSG";
    private static final String
        MULTIPLE_TOKENS_FROM_SOURCE_MSG =
            "MULTIPLE_TOKENS_FROM_SOURCE_MSG";


    /** The default normal maximum count. */
    private static final int DEFAULT_NORMAL_MAXIMUM_COUNT = 6;

    /** The default absolute maximum count. */
    private static final int DEFAULT_ABSOLUTE_MAXIMUM_COUNT = 10;

    /**
        The default probability (expressed as a float value between
        0.0 and 1.0) that an unbounded count will be increased once.
    */
    private static final float
        DEFAULT_INCREASE_COUNT_PROBABILITY = 0.00f;

    /** The default maximum part level. */
    private static final int DEFAULT_MAXIMUM_PART_LEVEL = 15;


    /** The source position used by default in creating tokens. */
    protected static final SourcePosition
        DEFAULT_SOURCE_POSITION = new SourcePosition(1, 0);


    /** The indent size used in writing out construct information. */
    private static final int DEFAULT_INDENT_SIZE = 2;


    // Private fields

    /** The random number generator used in generating the test data. */
    private Random _random;

    /**
        The normal maximum count: the maximum value that can normally be
        returned by the 'unbounded count' generateXXXCount() methods. A
        larger value can be returned if the count is increased one or more
        times.
    */
    private int _normalMaximumCount = DEFAULT_NORMAL_MAXIMUM_COUNT;

    /**
        The absolute maximum count: the maximum value that can ever be
        returned by the 'unbounded count' generateXXXCount() methods, even if
        the count is increased one or more times.
    */
    private int _absoluteMaximumCount = DEFAULT_ABSOLUTE_MAXIMUM_COUNT;

    /**
        The probability (expressed as a float between 0.0 and 1.0) that an
        unbounded count will be increased once.
    */
    private float _increaseCountProbability =
        DEFAULT_INCREASE_COUNT_PROBABILITY;


    /**
        The number of levels of construct parts we're at: top-level
        constructs are at level 0, its parts are at level 1, those parts'
        parts are at level 2, etc.
        <p>
        This level is used to curb excessively deeply nested constructs, as
        well as the non-termination of recursive constructs.
        <p>
        Note: we don't adjust the part count used when there is to be exactly
        one instance of a part, so the maximum part level could get exceeded
        in this case. (This will not lead to infinitely deep parts, though,
        since otherwise the construct will always be infinitely deep/long.)
    */
    private int _partLevel;

    /** The maximum allowed part level. */
    private int _maximumPartLevel = DEFAULT_MAXIMUM_PART_LEVEL;


    // Constructors

    /**
        Constructs an AbstractConstructTestDataCreator.
    */
    public AbstractConstructTestDataCreator()
    {
        _random = new Random();
        _partLevel = 0;
    }


    // Public methods

    /**
        Sets the normal maximum count to the specified maximum.

        @param newMaximum the new normal maximum count: the maximum count
        that can be returned unless it is increased
    */
    public void setNormalMaximumCount(int newMaximum)
    {
        Assert.require(newMaximum >= 0);

        _normalMaximumCount = newMaximum;
    }

    /**
        Sets the absolute maximum count to the specified maximum.

        @param newMaximum the new absolute maximum count: the maximum count
        that can be returned, even if it is increased
    */
    public void setAbsoluteMaximumCount(int newMaximum)
    {
        Assert.require(newMaximum >= 0);

        _absoluteMaximumCount = newMaximum;
    }

    /**
        Sets the probability (expressed as a float between 0.0 and 1.0) that
        an unbounded count will be increased once to the specified new
        probability.
        <p>
        A count can be increased more than once.

        @param newProbability the new probability that an unbounded count
        will be increased once
    */
    public void setIncreaseCountProbability(float newProbability)
    {
        Assert.require(newProbability >= 0.0);
        Assert.require(newProbability <= 1.0);

        _increaseCountProbability = newProbability;
    }


    // Part level methods

    /**
        Sets the maximum allowed part level to the specified maximum.

        @param newMaximum the new maximum part level
    */
    public void setMaximumPartLevel(int newMaximum)
    {
        Assert.require(newMaximum >= 0);

        _maximumPartLevel = newMaximum;
    }

    /**
        Increases the current part level by one.
    */
    protected void incrementPartLevel()
    {
        _partLevel += 1;
    }

    /**
        Decreases the current part level by one.
    */
    protected void decrementPartLevel()
    {
        Assert.check(_partLevel > 0);
        _partLevel -= 1;
    }


    // Program methods

    /**
        Generates the test data indicated by the specified command line
        arguments.
        <p>
        TODO: provide a way for the indent size to be specified via the
        command line arguments (e.g. -i 3)!!!

        @see Program#execute(String[])
    */
    public void execute(String[] args)
        throws ProgramException
    {
        Assert.require(args != null);

        if (args.length != 2)
        {
            throw EXECUTOR.createBadUsageException(this);
        }

        try
        {
            generateDefaultTestData(new File(args[0]),
                                    new File(args[1]));
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(TEST_DATA_CREATION_IO_ERROR_MSG,
                           ex.getClass().getName(),
                           SteelCandyException.message(ex));
            throw EXECUTOR.createFailureException(this, msg, ex);
        }
        catch (Throwable ex)
        {
            String msg = EXECUTOR.unexpectedExceptionMessage(ex);
            throw EXECUTOR.createFailureException(this, msg, ex);
        }
    }

    /**
        @see Program#argumentsSummary
    */
    public String argumentsSummary()
    {
        String result = _resources.
            getMessage(TEST_DATA_CREATOR_ARGUMENTS_SUMMARY);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Program#usageDescription
    */
    public String usageDescription()
    {
        String result = _resources.
            getMessage(TEST_DATA_CREATOR_USAGE_DESCRIPTION);

        Assert.ensure(result != null);
        return result;
    }


    // Data generation utility methods

    /**
        Creates and returns a token representing the specified piece of
        source code.
        <p>
        Note: an error will be reported if the piece of source code cannot be
        represented by exactly one token.
        <p>
        Note: it is usually more efficient to create tokens directly by
        calling the appropriate create...Token() method on the language's
        token manager. Thus the token manager methods should be used instead
        of this method wherever possible (e.g. when the type of token is
        known and the source code that it represents is fixed).

        @param source the piece of source code that the returned token is to
        represent
        @return a token representing the specified piece of source code, or
        null if it could not be tokenized
        @see #createTokenSourceCodeTokenizer(ErrorHandler)
    */
    protected Token createToken(String source)
    {
        Token result = null;

        SourceCodeString src = new SourceCodeString(source);
        WriterErrorHandler handler =
            new WriterErrorHandler(Io.err);

        SourceCodeTokenizer t = null;
        try
        {
            t = createTokenSourceCodeTokenizer(handler);
            t.initialize(src);

            // The tokenizer should tokenize the source into exactly one
            // token.
            if (t.hasNext() == false)
            {
                String msg = _resources.
                    getMessage(COULD_NOT_GET_TOKEN_FROM_SOURCE_MSG, source);
                handler.handle(new PlackInternalError(
                    PlackInternalError.FATAL_ERROR_LEVEL, msg, src,
                    null, getClass(), this));
            }
            else
            {
                result = t.next();
                if (t.hasNext())
                {
                    String msg = _resources.
                        getMessage(MULTIPLE_TOKENS_FROM_SOURCE_MSG, source);
                    handler.handle(new PlackInternalError(
                        PlackInternalError.FATAL_ERROR_LEVEL,
                        msg, src, null, getClass(), this));
                }
            }
        }
        finally
        {
            if (t != null)
            {
                t.close();
            }
        }

        return result;
    }

    /**
        Generates test data for the default type of construct: an instance of
        the default type of construct, and the corresponding source code.

        @param outputSourceFile the file to which to write the source code
        that corresponds to the generated construct
        @param constructInfoFile the file to which to write the generated
        construct's information
        @exception IOException thrown if an error occurs in writing to either
        of the files
        @see #generateDefaultConstruct(IndentWriter)
    */
    public void generateDefaultTestData(File outputSourceFile,
                                        File constructInfoFile)
        throws IOException
    {
        Assert.require(outputSourceFile != null);
        Assert.require(constructInfoFile != null);

        IndentWriter sourceWriter = null;
        ConstructWriter infoWriter = null;
        try
        {
            sourceWriter = sourceCodeWriter(outputSourceFile);
            infoWriter = new XmlConstructWriter();
            infoWriter.setWriter(new FileWriter(constructInfoFile));
            infoWriter.setIndentSize(DEFAULT_INDENT_SIZE);

            generateDefaultTestData(sourceWriter, infoWriter);
        }
        finally
        {
            Io.tryToClose(sourceWriter);
            TextConstructWriter.tryToClose(infoWriter);
        }
    }

    /**
        Generates test data for the default type of construct: an instance of
        the default type of construct, and the corresponding source code.

        @param sourceWriter the writer to use to write the source code that
        corresponds to the generated construct
        @param infoWriter the writer to use to write the generated
        construct's information
        @exception IOException thrown if an error occurs in writing using one
        of the specified writers
        @see #generateDefaultConstruct(IndentWriter)
    */
    public void generateDefaultTestData(IndentWriter sourceWriter,
                                        ConstructWriter infoWriter)
        throws IOException
    {
        Assert.require(sourceWriter != null);
        Assert.require(infoWriter != null);

        doWriteConstructInformation(infoWriter,
                                generateDefaultConstruct(sourceWriter));
    }

    /**
        Configures a construct writer to write constructs in the same form as
        the information about constructs generated as part of test data
        creation is written.
        <p>
        This method is automatically called on any construct writers used to
        write the information about constructs generated as part of test data
        creation, so this method only needs to be called explicitly on other
        writers (such as a construct writer used to write the information
        about a construct that results from a parser parsing test data source
        code in order to check that the parser's construct matches the one
        corresponding to the test data source code).

        @param w the construct writer to configure
    */
    public void configureConstructWriter(ConstructWriter w)
    {
        Assert.require(w != null);

        // Since we don't have the construct's source code, and since (at
        // least currently) the generated construct doesn't have its source
        // location set, we have the construct writer hide these pieces of
        // information.
        w.configuration().setShowLocation(ConstructWriter.HIDE);
        w.configuration().setShowSourceCode(ConstructWriter.HIDE);
        w.setIndentSize(DEFAULT_INDENT_SIZE);
    }

    /**
        Writes the information about the specified construct using the
        specified writer.
        <p>
        The specified construct writer may be modified by this method so that
        it doesn't output information that constructs created as part of test
        data generation do not contain.

        @param w the writer to use to output information about 'c'
        @param c the construct whose information is to be written using 'w'
    */
    protected void doWriteConstructInformation(ConstructWriter w,
                                               Construct c)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(c != null);

        configureConstructWriter(w);
        c.write(w, null);
    }

    /**
        Returns a writer that writes the generated source code to the
        specified file.

        @param outputSourceFile the file that the writer is to write to
        @exception IOException thrown if the writer could not be created
        (for example, if the file cannot be written to)
    */
    protected IndentWriter sourceCodeWriter(File outputSourceFile)
        throws IOException
    {
        return sourceCodeWriter(new FileWriter(outputSourceFile));
    }

    /**
        Returns a writer that writes the generated source code using the
        specified writer. The specified writer will be closed when the
        returned writer is.

        @param w the writer to use to write the generated source code
    */
    protected IndentWriter sourceCodeWriter(Writer w)
    {
        return IndentWriter.createClosing(w);
    }


    // Source code writing and appending methods

    /**
        Writes one of the characters in the specified string using the
        specified writer.

        @param w the writer to use to write the character
        @param str the string from which the character to write is chosen
        @exception IOException thrown if an error occurs in writing the
        character
    */
    protected void doWriteOneOf(Writer w, String str)
        throws IOException
    {
        int index = generateUniformRandomNumber(0, str.length() - 1);
        w.write(str.charAt(index));
    }

    /**
        Appends one of the characters in the specified string to the
        specified buffer.

        @param buf the buffer to append the character to
        @param str the string from which the character to append is chosen
    */
    protected void doAppendOneOf(StringBuffer buf, String str)
    {
        int index = generateUniformRandomNumber(0, str.length() - 1);
        buf.append(str.charAt(index));
    }


    /**
        Writes a space of the specified type using the specified writer,
        depending on whether it precedes the first part of a construct.

        @param w the writer to use to write the space (if one is written)
        @param spaceType the type of space to write (if one is written)
        @param isFirstPart true if the space will precede the first part of a
        construct, and false if it follows the first part
        @exception IOException thrown if an error occurs in writing the space
    */
    protected void doWriteSpace(IndentWriter w,
                                int spaceType, boolean isFirstPart)
        throws IOException
    {
        if (isFirstPart == false)
        {
            switch (spaceType)
            {
            case NO_SPACE:
                // don't write anything
                break;
            case DISALLOWED_SPACE:
                doWriteDisallowedSpace(w);
                break;
            case DISCOURAGED_SPACE:
                doWriteDiscouragedSpace(w);
                break;
            case ENCOURAGED_SPACE:
                doWriteEncouragedSpace(w);
                break;
            case REQUIRED_SPACE:
                doWriteRequiredSpace(w);
                break;
            default:
                Assert.unreachable();
            }
        }
    }

    /**
        Appends a space of the specified type to the specified buffer,
        depending on whether it precedes the first part of a construct.

        @param buf the buffer to which to append the space (if one is to be
        appended)
        @param spaceType the type of space to append to the buffer (if one is
        to be appended)
        @param isFirstPart true if the space will precede the first part of a
        construct, and false if it follows the first part
    */
    protected void doAppendSpace(StringBuffer buf,
                                 int spaceType, boolean isFirstPart)
    {
        if (isFirstPart == false)
        {
            switch (spaceType)
            {
            case NO_SPACE:
                // don't append anything
                break;
            case DISALLOWED_SPACE:
                doAppendDisallowedSpace(buf);
                break;
            case DISCOURAGED_SPACE:
                doAppendDiscouragedSpace(buf);
                break;
            case ENCOURAGED_SPACE:
                doAppendEncouragedSpace(buf);
                break;
            case REQUIRED_SPACE:
                doAppendRequiredSpace(buf);
                break;
            default:
                Assert.unreachable();
            }
        }
    }


    /**
        Writes, using the specified writer, a space where one is required.
        <p>
        This implementation always writes a space, which is usually what is
        desired. However, subclasses that want to generate intentionally
        erroneous source code might want to override this method.

        @param w the writer to use to write the space
        @exception IOException thrown if an error occurs in writing the space
        @see #doWriteSpace(Writer)
    */
    protected void doWriteRequiredSpace(Writer w)
        throws IOException
    {
        doWriteSpace(w);
    }

    /**
        Appends, to the specified buffer, a space where one is required.
        <p>
        This implementation always appends a space, which is usually what is
        desired. However, subclasses that want to generate intentionally
        erroneous source code might want to override this method.

        @param buf the buffer to which to append the space
        @see #doAppendSpace(StringBuffer)
    */
    protected void doAppendRequiredSpace(StringBuffer buf)
    {
        doAppendSpace(buf);
    }

    /**
        Writes, using the specified writer, a space where one should be
        present stylistically, though is neither required nor disallowed.
        <p>
        This implementation always writes a space, which results in properly
        formatted code, but may not be the best choice for fully testing a
        language's processing tools. Subclasses often override this method to
        only generate a space some of the time.

        @param w the writer to use to write the space
        @exception IOException thrown if an error occurs in writing the space
        @see #doWriteSpace(Writer)
    */
    protected void doWriteEncouragedSpace(Writer w)
        throws IOException
    {
        doWriteSpace(w);
    }

    /**
        Appends, to the specified buffer, a space where one should be present
        stylistically, though is neither required nor disallowed.
        <p>
        This implementation always appends a space, which results in properly
        formatted code, but may not be the best choice for fully testing a
        language's processing tools. Subclasses often override this method to
        only generate a space some of the time.

        @param buf the buffer to which to append the space
        @see #doAppendSpace(StringBuffer)
    */
    protected void doAppendEncouragedSpace(StringBuffer buf)
    {
        doAppendSpace(buf);
    }

    /**
        Writes, using the specified writer, a space where one should not be
        present stylistically, though is neither required nor disallowed.
        <p>
        This implementation never writes a space, which results in properly
        formatted code, but may not be the best choice for fully testing a
        language's processing tools. Subclasses often override this method to
        generate a space some of the time.

        @param w the writer to use to write the space
        @exception IOException thrown if an error occurs in writing the space
        @see #doWriteSpace(Writer)
    */
    protected void doWriteDiscouragedSpace(Writer w)
        throws IOException
    {
        // By default we don't write a space.
    }

    /**
        Appends, to the specified buffer, a space where one should not be
        present stylistically, though is neither required nor disallowed.
        <p>
        This implementation never appends a space, which results in properly
        formatted code, but may not be the best choice for fully testing a
        language's processing tools. Subclasses often override this method to
        generate a space some of the time.

        @param buf the buffer to which to append the space
        @see #doAppendSpace(StringBuffer)
    */
    protected void doAppendDiscouragedSpace(StringBuffer buf)
    {
        // By default we don't append a space.
    }

    /**
        Writes, using the specified writer, a space where one is not allowed.
        <p>
        This implementation doesn't write anything, which is usually what is
        desired. However, subclasses that want to generate intentionally
        erroneous source code might want to override this method.

        @param w the writer to use to write the space
        @exception IOException thrown if an error occurs in writing the space
        @see #doWriteSpace(Writer)
    */
    protected void doWriteDisallowedSpace(Writer w)
        throws IOException
    {
        // The space isn't allowed, so don't write one.
    }

    /**
        Appends, to the specified buffer, a space where one is not allowed.
        <p>
        This implementation doesn't append anything, which is usually what is
        desired. And unlike the case with the doWriteDisallowedSpace()
        method, having this method append a space will probably put it in the
        middle of a token, causing errors without testing the language
        processing tool itself. So this method really shouldn't be
        overridden.

        @param buf the buffer to which to append the space
        @see #doAppendSpace(StringBuffer)
    */
    protected void doAppendDisallowedSpace(StringBuffer buf)
    {
        // The space isn't allowed, so don't write one.
    }

    /**
        Writes a space using the specified writer.

        @param w the writer to use to write the space
        @exception IOException thrown if an error occurs in writing the space
    */
    protected void doWriteSpace(Writer w)
        throws IOException
    {
        w.write(" ");
    }

    /**
        Appends a space to the specified buffer.

        @param buf the buffer to which to append the space
    */
    protected void doAppendSpace(StringBuffer buf)
    {
        buf.append(" ");
    }


    // Count generation methods

    /**
        Adjusts the specified part count depending on the current state of
        data generation. The returned count will always be at least as large
        as the specified minimum count, though.

        @param count the part count to adjust
        @param minimumCount the minimum count that will be returned
        @return the adjusted part count
    */
    protected int adjustPartCount(int count, int minimumCount)
    {
        int result = count;

        // If the maximum part level has been exceeded then all counts should
        // be forced to the minimum (to help prevent another level of parts).
        if (_partLevel > _maximumPartLevel)
        {
            result = minimumCount;
        }

        return result;
    }

    /**
        Note: we don't check that the returned result is valid (other than
        that it is non-negative) to allow subclasses to generate invalid data
        (for example, for use in testing error detection and reporting).

        @return the number of instances of a part a test construct
        should have when the construct can have zero or one instances of the
        part
    */
    protected int generateZeroOrOnePartCount()
    {
        // The result will be zero with a probability of (m/n).
        int m = 3;
        int n = 4;
        int result = generateUniformRandomNumber(1, n);
        result = (result <= m) ? 0 : 1;
        result = adjustPartCount(result, 0);

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Note: we don't check that the returned result is valid (other than
        that it is non-negative) to allow subclasses to generate invalid data
        (for example, for use in testing error detection and reporting).

        @return the number of instances of a part a test construct
        should have when the construct can have exactly one instance of the
        part
    */
    protected int generateOnePartCount()
    {
        int result = 1;
        result = adjustPartCount(result, 1);

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Note: we don't check that the returned result is valid (other than
        that it is non-negative) to allow subclasses to generate invalid data
        (for example, for use in testing error detection and reporting).

        @return the number of instances of a part a test construct
        should have when the construct can have zero or more instances of the
        part
    */
    protected int generateZeroOrMorePartCount()
    {
        int result = generateNormalRandomNumber(0, _normalMaximumCount);
        result = increaseCount(result);
        result = adjustPartCount(result, 0);

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Note: we don't check that the returned result is valid (other than
        that it is non-negative) to allow subclasses to generate invalid data
        (for example, for use in testing error detection and reporting).

        @return the number of instances of a part a test construct
        should have when the construct can have one or more instances of the
        part
    */
    protected int generateOneOrMorePartCount()
    {
        int result = generateNormalRandomNumber(1, _normalMaximumCount);
        result = increaseCount(result);
        result = adjustPartCount(result, 1);

        Assert.ensure(result >= 0);
        return result;
    }


    /**
        Generates a random probability: that is, a number between zero
        (inclusive) and one (exclusive).

        @return a (usually random) value between zero (inclusive) and one
        (exclusive)
    */
    protected float generateProbability()
    {
        return _random.nextFloat();
    }

    /**
        Generates and returns a random number between the specified lower
        bound (inclusive) and the specified upper bound (also inclusive).
        The values returned by this method are uniformly distributed.

        @param lowerBound the smallest number that can be returned
        @param upperBound the largest number that can be returned
        @return a random number between 'lowerBound' (inclusive) and
        'upperBound' (also inclusive)
    */
    protected int generateUniformRandomNumber(int lowerBound, int upperBound)
    {
        Assert.require(lowerBound >= 0);
        Assert.require(upperBound >= lowerBound);

        int result = _random.
                nextInt(upperBound - lowerBound + 1) + lowerBound;

        Assert.ensure(result >= lowerBound);
        Assert.ensure(result <= upperBound);
        return result;
    }

    /**
        Generates and returns a random number at or above the specified lower
        bound (inclusive). The number may be greater than the specified upper
        bound, but won't be most of the time. The values returned by this
        method bear some resemblance to a normal distribution with a standard
        deviation of (upperBound - lowerBound).

        @param lowerBound the smallest number that can be returned
        @param upperBound the largest number that will usually be returned
        @return a random number greater than or equal to 'lowerBound'
        (inclusive) and usually less than or equal to 'upperBound'
    */
    protected int generateNormalRandomNumber(int lowerBound, int upperBound)
    {
        Assert.require(lowerBound >= 0);
        Assert.require(upperBound >= lowerBound);

        // The absolute value of a normally distributed value with mean 0.0
        // and standard deviation 1.0.
        double rand = Math.abs(_random.nextGaussian());

        int result = (int) (rand * (upperBound - lowerBound)) + lowerBound;

        Assert.ensure(result >= lowerBound);
        // The result may be somewhat larger than upperBound
// System.err.println("normal: " + lowerBound + " <= " + result + " <=? " + upperBound);
        return result;
    }

    /**
        Possibly increases the specified unbounded normal count and returns
        the increased value.
        <p>
        The probability that an unbounded count should be increased once is
        used to determine whether to increase the count. The count can be
        increased more than once by this method.

        @param normalCount the count generated using the normal maximum count
        that is to (possibly) be increased (zero or more times)
        @return the increased count
    */
    protected int increaseCount(int normalCount)
    {
        int result = normalCount;

        int m = 1;
        while (result <= _absoluteMaximumCount && doIncreaseCount())
        {
            result += m * generateNormalRandomNumber(1, _normalMaximumCount);
            m *= 2;
        }

        if (result > _absoluteMaximumCount)
        {
            result = _absoluteMaximumCount;
        }

        Assert.ensure(result <= _absoluteMaximumCount);
        return result;
    }

    /**
        Indicates whether an unbounded count should be increased again.

        @return true if the count should be increased again, and false if it
        shouldn't
    */
    protected boolean doIncreaseCount()
    {
        return (generateProbability() < _increaseCountProbability);
    }


    // Abstract methods

    /**
        Generates and returns an instance of the language's default construct
        after writing the corresponding source code using the specified
        writer.
        <p>
        A language's default construct is usually the one that is at the
        'top' of the language's grammar: the type of construct of which all
        other types of constructs are (direct or indirect) subconstructs, and
        which is not (usually) a subconstruct of any other type of construct.

        @param w the writer to use to write the source corresponding to the
        generated construct
        @return the generated instance of the language's default type of
        construct
        @exception IOException thrown if an error occurs in writing the
        source code (using 'w')
    */
    protected abstract Construct generateDefaultConstruct(IndentWriter w)
        throws IOException;
        // Assert.require(w != null);

    /**
        Creates and returns a tokenizer that will be used to tokenize a piece
        of source code into a single token.
        <p>
        Note: initialize() will be called on the returned tokenizer at a
        later point.

        @param handler the error handler that the tokenizer is to use to
        report any errors
        @return a tokenizer that tokenizes a piece of source code into a
        single token
        @see SourceCodeTokenizer#initialize
    */
    protected abstract SourceCodeTokenizer
        createTokenSourceCodeTokenizer(ErrorHandler handler);
        // Assert.require(handler != null);
        // Assert.ensure(result != null);
}
