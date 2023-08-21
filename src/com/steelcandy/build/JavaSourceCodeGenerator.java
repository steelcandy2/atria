/*
 Copyright (C) 2002-2003 by James MacKay.

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

package com.steelcandy.build;

// import com.steelcandy.common.debug.Assert;

import java.io.File;

import java.util.Properties;

/**
    Generates all of the automatically generated Java source files
    specified in a properties file.
    <p>
    <strong>Note</strong>: since this class is usually run before any of
    our source code has been compiled this class should not use any of
    our other classes.

    @author James MacKay
*/
public class JavaSourceCodeGenerator
    extends SourceCodeGenerator
{
    // Constructors

    /**
        Constructs a JavaSourceCodeGenerator.

        @param p the properties that describe what source code to generate
        and where to generate it
        @exception SourceCodeGenerationException thrown if there is a
        problem with the properties
    */
    public JavaSourceCodeGenerator(Properties p)
        throws SourceCodeGenerationException
    {
        super(p);
    }

    /**
        Constructs an uninitialized JavaSourceCodeGenerator. Its
        setProperties() method must be called before it can be used.

        @see SourceCodeGenerator#setProperties(Properties)
    */
    protected JavaSourceCodeGenerator()
    {
        super();
    }


    // Protected methods

    /**
        @see SourceCodeGenerator#sourceFileExtension
    */
    protected String sourceFileExtension()
    {
        return ".java";
    }

    /**
        @see SourceCodeGenerator#implementationLanguageName
    */
    protected String implementationLanguageName()
    {
        return "Java";
    }


    // Main method

    /**
        Creates and runs a source code generator configured using the
        properties file specified by the arguments.

        @param args the command-line arguments
    */
    public static void main(String[] args)
    {
        run(args, new JavaSourceCodeGenerator());
    }
}
