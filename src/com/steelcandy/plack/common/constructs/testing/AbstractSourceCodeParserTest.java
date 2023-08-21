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
import com.steelcandy.plack.common.errors.WriterErrorHandler;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceCodeFile;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructWriter;
import com.steelcandy.plack.common.constructs.SourceCodeParser;
import com.steelcandy.plack.common.constructs.TextConstructWriter;
import com.steelcandy.plack.common.constructs.XmlConstructWriter;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import com.steelcandy.common.program.Program;
import com.steelcandy.common.program.ProgramException;
import com.steelcandy.common.program.ProgramFailedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
    An abstract base class for classes that test a source code parser.
    <p>
    The parser is tested by
    <ul>
        <li>creating a source code file <em>testSource</em> and the
            corresponding Construct <em>testConstruct</em>,</li>
        <li>parsing <em>testSource</em> using a parser, creating a Construct
            <em>parsedConstruct</em>, and</li>
        <li>checking that <em>testConstruct</em> and <em>parsedConstruct</em>
            are the same (modulo differences in what the test data creator
            and the parser generate: for example, currently
            <em>testConstruct</em> and its subconstructs will not have
            their SourceLocations set).</li>
    </ul>

    @author James MacKay
    @version $Revision: 1.11 $
*/
public abstract class AbstractSourceCodeParserTest
    implements Program
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonConstructTestingResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        PARSER_TEST_ARGUMENTS_SUMMARY =
            "PARSER_TEST_ARGUMENTS_SUMMARY";
    private static final String
        PARSER_TEST_USAGE_DESCRIPTION =
            "PARSER_TEST_USAGE_DESCRIPTION";
    private static final String
        COULD_NOT_CREATE_TEST_DATA_MSG =
            "COULD_NOT_CREATE_TEST_DATA_MSG";
    private static final String
        COULD_NOT_WRITE_PARSED_CONSTRUCT_INFO_MSG =
            "COULD_NOT_WRITE_PARSED_CONSTRUCT_INFO_MSG";
    private static final String
        COULD_NOT_PARSE_SOURCE_MSG =
            "COULD_NOT_PARSE_SOURCE_MSG";
    private static final String
        CONSTRUCT_INFO_FILES_DIFFER_MSG =
            "CONSTRUCT_INFO_FILES_DIFFER_MSG";


    /** The default test source file name (without an extension). */
    protected static final String
        DEFAULT_TEST_SOURCE_NAME_START =
            "source-code.";

    /** The default test construct information file name. */
    protected static final String
        DEFAULT_TEST_CONSTRUCT_INFO_FILE_NAME =
            "test-construct-info.txt";

    /** The default parsed construct information file name. */
    protected static final String
        DEFAULT_PARSED_CONSTRUCT_INFO_FILE_NAME =
            "parsed-construct-info.txt";


    // Private fields

    /** The directory to which all file names are relative. */
    private File _baseDir;

    /** The test source file name. */
    private String _testSourceFileName;

    /** The test construct information file name. */
    private String _testConstructInfoFileName;

    /** The parsed construct information file name. */
    private String _parsedConstructInfoFileName;


    // Constructors

    /**
        Constructs an AbstractSourceCodeParserTest that uses the default
        names for the various files involved in the test. All of the files
        will be relative to the specified base directory.

        @param baseDir the base directory of this test
    */
    public AbstractSourceCodeParserTest(File baseDir)
    {
        this();
        setBaseDirectory(baseDir);
    }

    /**
        @see #AbstractSourceCodeParserTest(File)
    */
    public AbstractSourceCodeParserTest(String baseDir)
    {
        this(new File(baseDir));
    }

    /**
        Constructs an AbstractSourceCodeParserTest that uses the current
        directory as its base directory.
    */
    public AbstractSourceCodeParserTest()
    {
        initialize();
    }

    /**
        Initializes an AbstractSourceCodeParserTest.
        <p>
        This method is intended to only be called from this class'
        constructors.
    */
    private void initialize()
    {
        setTestSourceFileName(DEFAULT_TEST_SOURCE_NAME_START +
                              sourceFileExtension());
        setTestConstructInformationFileName(
            DEFAULT_TEST_CONSTRUCT_INFO_FILE_NAME);
        setParsedConstructInformationFileName(
            DEFAULT_PARSED_CONSTRUCT_INFO_FILE_NAME);
    }


    // Program methods

    /**
        @see Program#execute(String[])
    */
    public void execute(String[] args)
        throws ProgramException
    {
        Assert.require(args != null);

        if (args.length != 1)
        {
            throw EXECUTOR.createBadUsageException(this);
        }

        setBaseDirectory(args[0]);
        File testSourceFile = makeAbsolute(_testSourceFileName);
        File testConstructInfoFile =
                        makeAbsolute(_testConstructInfoFileName);
        File parsedConstructInfoFile =
                        makeAbsolute(_parsedConstructInfoFileName);

        // Generate the test source file and the corresponding construct's
        // information file.
        AbstractConstructTestDataCreator dataCreator = null;
        try
        {
            dataCreator = createTestDataCreator();
            dataCreator.generateDefaultTestData(testSourceFile,
                                                testConstructInfoFile);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(COULD_NOT_CREATE_TEST_DATA_MSG,
                           ProgramException.message(ex));
            throw EXECUTOR.createFailureException(this, msg, ex);
        }

        // Parse the testSourceFile.
        SourceCode source = new SourceCodeFile(testSourceFile);
        ErrorHandler handler = new WriterErrorHandler(Io.err);
        SourceCodeParser parser = createSourceCodeParser();
        Construct parsedConstruct = parser.parse(source, handler);

        // Write out the parsed construct's information.
        if (parsedConstruct != null)
        {
            ConstructWriter parsedConstructWriter = null;
            try
            {
                parsedConstructWriter = new XmlConstructWriter();
                parsedConstructWriter.
                    setWriter(new FileWriter(parsedConstructInfoFile));
                dataCreator.
                    configureConstructWriter(parsedConstructWriter);
                    // So that the same information about both the generated
                    // and the parsed constructs will be output.

                parsedConstruct.write(parsedConstructWriter, source);
            }
            catch (IOException ex)
            {
                String msg = _resources.
                    getMessage(COULD_NOT_WRITE_PARSED_CONSTRUCT_INFO_MSG,
                               parsedConstructInfoFile.getPath(),
                               ProgramException.message(ex));
                throw EXECUTOR.createFailureException(this, msg, ex);
            }
            finally
            {
                TextConstructWriter.tryToClose(parsedConstructWriter);
            }
        }
        else
        {
            String msg = _resources.
                getMessage(COULD_NOT_PARSE_SOURCE_MSG,
                           testSourceFile.getPath());
            throw EXECUTOR.createFailureException(this, msg);
        }

        // TODO: change the following so that it doesn't require the
        // existence of an external 'diff' program!!!

        // Compare the test construct's information against the parsed
        // construct's information. This test is successful if they're the
        // same.
        String[] diffArgs = new String[]
        {
            testConstructInfoFile.getPath(),
            parsedConstructInfoFile.getPath()
        };
        int diffResult = EXECUTOR.execute("diff", diffArgs);
        if (diffResult != 0)
        {
            String msg = _resources.
                getMessage(CONSTRUCT_INFO_FILES_DIFFER_MSG,
                           testConstructInfoFile.getPath(),
                           parsedConstructInfoFile.getPath(),
                           String.valueOf(diffResult));
            throw EXECUTOR.createFailureException(this, msg);
        }
    }

    /**
        Makes the specified filename absolute iff it isn't already.

        @param f the filename to make abolsute (iff it isn't already)
        @return 'f' as an absolute filename
    */
    protected File makeAbsolute(String f)
    {
        return Io.makeAbsolute(f, _baseDir);
    }

    /**
        @see Program#argumentsSummary
    */
    public String argumentsSummary()
    {
        String result = _resources.
            getMessage(PARSER_TEST_ARGUMENTS_SUMMARY);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Program#usageDescription
    */
    public String usageDescription()
    {
        String result = _resources.
            getMessage(PARSER_TEST_USAGE_DESCRIPTION);

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        The directory to which all file names are relative.

        @param dir this test's base directory
        @see #setBaseDirectory(String)
    */
    public void setBaseDirectory(File dir)
    {
        Assert.require(dir != null);

        _baseDir = dir;
    }

    /**
        The directory to which all file names are relative.

        @param dir this test's base directory
        @see #setBaseDirectory(File)
    */
    public void setBaseDirectory(String dir)
    {
        Assert.require(dir != null);

        _baseDir = new File(dir);
    }

    /**
        Sets the name of this test's test source file to the specified file
        name.

        @param sourceFileName the new name of this test's test source file
    */
    public void setTestSourceFileName(String sourceFileName)
    {
        Assert.require(sourceFileName != null);

        _testSourceFileName = sourceFileName;
    }

    /**
        Sets the name of the file containing the information about the
        construct that corresponds to the test source file to the specified
        file name.

        @param infoFileName the new name of the test construct information
        file
    */
    public void setTestConstructInformationFileName(String infoFileName)
    {
        Assert.require(infoFileName != null);

        _testConstructInfoFileName = infoFileName;
    }

    /**
        Sets the name of the file containing the information about the
        construct that results from parsing the test source file.

        @param infoFileName the new name of the parsed construct information
        file
    */
    public void setParsedConstructInformationFileName(String infoFileName)
    {
        Assert.require(infoFileName != null);

        _parsedConstructInfoFileName = infoFileName;
    }


    // Abstract methods

    /**
        @return the extension of the source files used in this test
    */
    protected abstract String sourceFileExtension();

    /**
        @return the construct test data creator to use to create the test
        data used in this test
        @exception ProgramFailedException thrown if the test data creator
        could not be obtained
    */
    protected abstract
        AbstractConstructTestDataCreator createTestDataCreator()
        throws ProgramFailedException;

    /**
        @return the source code parser to use in this test
        @exception ProgramFailedException thrown if the parser could not be
        obtained
    */
    protected abstract SourceCodeParser createSourceCodeParser()
        throws ProgramFailedException;
}
