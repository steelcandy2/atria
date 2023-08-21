/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.atria.constructs.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;

import com.steelcandy.plack.common.constructs.SourceCodeParser;
import com.steelcandy.plack.common.constructs.testing.AbstractConstructTestDataCreator;
import com.steelcandy.plack.common.constructs.testing.AbstractSourceCodeParserTest;

import com.steelcandy.common.program.ProgramFailedException;

import java.io.File;

/**
    A class that tests an Atria language parser.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaParserTest
    extends AbstractSourceCodeParserTest
{
    // Constants

    /** The extension on all source files involved in this test. */
    private static final String
        SOURCE_EXTENSION = AtriaInfo.SOURCE_EXTENSION;


    // Constructors

    /**
        Constructs an AtriaParserTest that uses the default names for the
        various files involved in the test. All of the files will be relative
        to the specified base directory.

        @param baseDir the base directory of this test
    */
    public AtriaParserTest(File baseDir)
    {
        super(baseDir);
    }

    /**
        @see #AtriaParserTest(File)
    */
    public AtriaParserTest(String baseDir)
    {
        super(baseDir);
    }

    /**
        Constructs an AtriaParserTest that uses the current directory as its
        base directory.
    */
    public AtriaParserTest()
    {
        super();
    }


    // Protected methods

    /**
        @see AbstractSourceCodeParserTest#sourceFileExtension
    */
    protected String sourceFileExtension()
    {
        return SOURCE_EXTENSION;
    }

    /**
        @see AbstractSourceCodeParserTest#createTestDataCreator
    */
    protected AbstractConstructTestDataCreator createTestDataCreator()
        throws ProgramFailedException
    {
        return new AtriaConstructTestDataCreator();
    }

    /**
        @see AbstractSourceCodeParserTest#createSourceCodeParser
    */
    protected SourceCodeParser createSourceCodeParser()
        throws ProgramFailedException
    {
        return new AtriaTestInterpreterParser();
    }


    // The main method and associated methods

    /**
        Runs a single parser test.
        <p>
        The current directory will be used as the test's base directory.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        AtriaParserTest test = new AtriaParserTest();
        EXECUTOR.executeAndExit(test, args);
    }
}
