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

package com.steelcandy.build.plack.atria;

// import com.steelcandy.common.debug.Assert;

import com.steelcandy.build.*;

import org.jdom.Document;
import org.jdom.Element;

import java.io.File;

import java.util.List;
import java.util.Properties;

/**
    Generates all of the automatically generated Java source files for the
    Atria programming language, as specified in a properties file.
    <p>
    <strong>Note</strong>: since this class is usually run before any of
    our source code has been compiled this class should not use any of
    our other classes.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaJavaSourceCodeGenerator
    extends JavaSourceCodeGenerator
{
    // Constructors

    /**
        Constructs an AtriaJavaSourceCodeGenerator.

        @param p the properties that describe what source code to generate
        and where to generate it
        @exception SourceCodeGenerationException thrown if there is a
        problem with the properties
    */
    public AtriaJavaSourceCodeGenerator(Properties p)
        throws SourceCodeGenerationException
    {
        super(p);
    }

    /**
        Constructs an uninitialized AtriaJavaSourceCodeGenerator. Its
        setProperties() method must be called before it can be used.

        @see SourceCodeGenerator#setProperties(Properties)
    */
    protected AtriaJavaSourceCodeGenerator()
    {
        super();
    }


    // Protected methods

    /**
        @see SourceCodeGenerator#createLanguageGenerators(File, File, File, Document, Element)
    */
    protected List createLanguageGenerators(File sourceDir, File docsDir,
        File transformersDir, Document languageDescription, Element impl)
        throws SourceCodeGenerationException
    {
        List result = super.createLanguageGenerators(sourceDir, docsDir,
                            transformersDir, languageDescription, impl);

        File atriaTransformersDir = new File(transformersDir, "atria");

        // Add the language definition document(s).
        File absoluteDocsDir = makeAbsolute(docsDir);
        addLanguageDocumentationGenerator(result,
            new File(atriaTransformersDir, "language-definition.xsl"),
            new File(docsDir, "LanguageDefinition" + ALL_EXT),
            "language-definition", absoluteDocsDir);

        return result;
    }


    // Main method

    /**
        Creates and runs a source code generator configured using the
        properties file specified by the arguments.

        @param args the command-line arguments
    */
    public static void main(String[] args)
    {
        run(args, new AtriaJavaSourceCodeGenerator());
    }
}
