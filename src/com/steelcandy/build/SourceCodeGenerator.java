/*
 Copyright (C) 2002-2015 by James MacKay.

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

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
    An abstract base class for classes that generate all of the
    automatically generated source files specified in a properties file.
    <p>
    Subclasses just have to implement the abstract methods declared in
    this class, all of which specify information specific to the language
    that the generated source code will be in.
    <p>
    <strong>Note</strong>: since this class is usually run before any of
    our source code has been compiled this class should not use any of
    our other classes.

    @author James MacKay
*/
public abstract class SourceCodeGenerator
{
    // Constants

    /**
        The extension used on files containing one or more other
        files that are to be split out.
    */
    protected static final String ALL_EXT = ".all";

    /**
        The extension of files that list all of the files generated from
        another file, usually by them being split out of another file.
    */
    private static final String GENERATED_EXT = ".generated";

    /**
        The extension of files that list the files that are not to be
        split out of another file.
    */
    private static final String DISCARD_EXT = ".discard";

    /** The (final) extension on XSL files (by default). */
    private static final String XSL_EXT = ".xsl";


    /** The date format to use for generation date/times. */
    private static SimpleDateFormat GENERATION_DATE_TIME_FORMAT =
        new SimpleDateFormat("MMMM dd, yyyy hh:mm.ssa");

    /** The XSL namespace. */
    private static final Namespace XSL_NAMESPACE = Namespace.
        getNamespace("xsl", "http://www.w3.org/1999/XSL/Transform");


    /** The size of the buffer used in reading in input files. */
    private static int INPUT_BUFFER_SIZE = 1024;


    // Private fields

    /**
        Indicates whether we should always generate all of the files,
        regardless of whether they're up to date.
    */
    private boolean _forceGeneration;

    /**
        Indicates whether we should be allowed to generate source code
        files in parallel.
    */
    private boolean _doParallelizeGeneration;

    /**
        A string representation of the  date/time at which the source code
        was generated.
    */
    private String _generationDateTime;

    /**
        The directory to which all relative pathnames are assumed to
        be relative.
    */
    private File _baseDir = null;

    /**
        The base of the source code directory that the generated source
        files are to be placed under.
    */
    private File _sourceDir;


    /** The language description document file. */
    private File _languageDescriptionFile;

    /** The language description document. */
    private Document _languageDescription;

    /**
        The directory containing the XSL transforms/stylesheets that
        transform the language description document into the various
        source files.
    */
    private File _languageTransformersDir;

    /** The directory to put the language's generated documents in. */
    private File _languageDocsDir;

    /**
        A list of the SingleInputGenerators that are used to generate
        source code and documentation files from the language description
        document. Each item in the list is a SingleInputGenerator.
    */
    private List _languageGenerators;

    /**
        A list of the FileCopiers that are used to copy files unchanged
        to other directories. Each item in the list is a FileCopier.
    */
    private List _fileCopiers;


    /** The file that describes all of the generic classes. */
    private File _genericsDescriptionFile;

    /** The document that describes all of the generic classes. */
    private Document _genericsDescription;

    /**
        The directory containing the XSL transforms/stylesheets that
        transform the generic classes document into the various
        source files.
    */
    private File _genericsTransformersDir;

    /**
        A list of the SingleInputGenerators that are used to generate
        source code and documentation files from the generics description
        document. Each item in the list is a SingleInputGenerator.
    */
    private List _genericsGenerators;


    /** The file that describes all of the generated visitor classes. */
    private File _visitorsDescriptionFile;

    /** The document that describes the generated visitor classes. */
    private Document _visitorsDescription;

    /**
        The directory containing the XSL transforms/stylesheets that
        transform the visitors document into the various source files.
    */
    private File _visitorsTransformersDir;

    /**
        A list of the SingleInputGenerators that are used to generate
        source code and documentation files from the visitors description
        document. Each item in the list is a SingleInputGenerator.
    */
    private List _visitorsGenerators;


    // Constructors

    /**
        Constructs a SourceCodeGenerator from the specified properties.

        @param p the properties that describe what source code to generate
        and where to generate it
        @exception SourceCodeGenerationException thrown if there is a
        problem with the properties
    */
    public SourceCodeGenerator(Properties p)
        throws SourceCodeGenerationException
    {
        this();
        setProperties(p);
    }

    /**
        Constructs an uninitialized SourceCodeGenerator. Its
        setProperties() method must be called before it can be used.

        @see #setProperties(Properties)
    */
    protected SourceCodeGenerator()
    {
        _forceGeneration = false;
        _doParallelizeGeneration = false;
        _generationDateTime =
            GENERATION_DATE_TIME_FORMAT.format(new Date());
    }


    // Source code generation methods

    /**
        Sets whether all files will be generated regardless of whether
        they're up to date.

        @param forceGeneration true if all files are to be generated
        regardless of whether they're up to date, and false if only
        those that are not up to date will be generated
    */
    public void setForceGeneration(boolean forceGeneration)
    {
        _forceGeneration = forceGeneration;
    }

    /**
        Sets whether the generation of some source files will be done in
        parallel.

        @param doParallelize true if some source files are to be generated in
        parallel, and false if only one source file is to be generated at a
        time
    */
    public void setDoParallelizeGeneration(boolean doParallelize)
    {
        _doParallelizeGeneration = doParallelize;
    }

    /**
        Sets our base directory to the specified one.
    */
    public void setBaseDirectory(String newBaseDir)
    {
        _baseDir = new File(newBaseDir);
    }

    /**
        Generates the source code.

        @exception SourceCodeGenerationException thrown if one or more
        of the source files could not be generated
    */
    public void generate()
        throws SourceCodeGenerationException
    {
        generateGenericClassesSourceCode();
        generateVisitorClassesSourceCode();
        generateLanguageSourceCode();
        copyFiles();
    }

    /**
        Generates the source code for the generic classes.

        @exception SourceCodeGenerationException thrown if one or more
        of the source files could not be generated
    */
    public void generateGenericClassesSourceCode()
        throws SourceCodeGenerationException
    {
        File in = _genericsDescriptionFile;
        generateAll(in, generatorsToUse(_genericsGenerators, in));
    }

    /**
        Generates the source code for the visitor classes.

        @exception SourceCodeGenerationException thrown if one or more
        of the source files could not be generated
    */
    public void generateVisitorClassesSourceCode()
        throws SourceCodeGenerationException
    {
        File in = _visitorsDescriptionFile;
        generateAll(in, generatorsToUse(_visitorsGenerators, in));
    }

    /**
        Generates the language's source code (and documents).

        @exception SourceCodeGenerationException thrown if one or more
        of the source files could not be generated
    */
    public void generateLanguageSourceCode()
        throws SourceCodeGenerationException
    {
        File in = makeAbsolute(_languageDescriptionFile);

        List gensToUse = generatorsToUse(_languageGenerators, in);
        if (gensToUse.isEmpty() == false)
        {
            generateAll(in, gensToUse);
        }
    }

    /**
        @param gens a list of SingleInputGenerators
        @param input the file that the generators in 'gens' will use as input
        @return a list of those generators in 'gens' that will actually be
        used to generate code
        @exception SourceCodeGenerationException thrown if an error occurred
        in selecting generators
    */
    protected List generatorsToUse(List gens, File input)
        throws SourceCodeGenerationException
    {
        List result;

        if (_forceGeneration)
        {
            result = gens;
        }
        else
        {
            result = new ArrayList(gens.size());
            Iterator iter = gens.iterator();
            while (iter.hasNext())
            {
                SingleInputGenerator g = (SingleInputGenerator) iter.next();
                if (g.isUpToDate(input) == false)
                {
                    result.add(g);
                }
            }
        }

        return result;
    }

    /**
        Generates all of the source code that all of the generators in the
        specified list can generate from the specified input file.

        @param input the input file from which the source code is to be
        generated
        @param gens the generators to use to generate the source code
        @exception SourceCodeGenerationException thrown if one or more
        of the source files could not be generated
        @see #generateAllSequentially(File, List)
        @see #generateAllInParallel(File, List)
    */
    protected void generateAll(File input, List gens)
        throws SourceCodeGenerationException
    {
        if (_doParallelizeGeneration)
        {
            generateAllInParallel(input, gens);
        }
        else
        {
            generateAllSequentially(input, gens);
        }
    }

    /**
        Generates the source code one file at a time.

        @see #generateAll(File, List)
    */
    protected void generateAllSequentially(File input, List gens)
        throws SourceCodeGenerationException
    {
        org.w3c.dom.Document contents = fileContentsAsDocument(input);
        Iterator iter = gens.iterator();
        while (iter.hasNext())
        {
            SingleInputGenerator g = (SingleInputGenerator) iter.next();
            g.transform(input, contents);
        }
    }

    /**
        Generates the source code files at least somewhat in parallel.

        @see #generateAll(File, List)
    */
    protected void generateAllInParallel(File input, List gens)
        throws SourceCodeGenerationException
    {
        org.w3c.dom.Document contents = fileContentsAsDocument(input);
        Iterator iter;

        // Run each generator in its own thread.
        List threads = new ArrayList(gens.size());
        iter = gens.iterator();
        while (iter.hasNext())
        {
            SingleInputGenerator g = (SingleInputGenerator) iter.next();
            Thread thr = new GenerationThread(input, contents, g);
            thr.start();
            threads.add(thr);
        }

        // Wait for all of the threads to complete.
        SourceCodeGenerationException genEx = null;
        iter = threads.iterator();
        while (iter.hasNext())
        {
            GenerationThread thr = (GenerationThread) iter.next();
            while (true)
            {
                try
                {
                    thr.join();
                    if (genEx == null)
                    {
                        genEx = thr.exception();
                    }
                    break;
                }
                catch (InterruptedException ex)
                {
                    // try join()ing again
                }
            }
        }

        // Throw the first of any SourceCodeGenerationExceptions that
        // occurred in generating the source code.
        if (genEx != null)
        {
            throw genEx;
        }
    }


    /**
        Copies all of the files that are to be copied unchanged.
    */
    public void copyFiles()
        throws SourceCodeGenerationException
    {
        Iterator iter = _fileCopiers.iterator();
        while (iter.hasNext())
        {
            FileCopier copier = (FileCopier) iter.next();
            if (_forceGeneration || copier.isUpToDate() == false)
            {
                copier.copy();
            }
        }
    }

    /**
        Transforms the specified input XML into the specified output file
        using the specified XSL transform/stylesheet.

        @param in contains the XML to transform
        @param transform the stylesheet to use to transform 'in'
        @param out the filename of the file to which to output the
        results of transforming 'in'
        @param parameters a map from the names of the global transform
        parameters to their values: all of the keys and values are Strings
        @exception SourceCodeGenerationException thrown if the
        transformation fails
    */
    protected void doTransform(String in, File transform, File out,
                               Map parameters)
        throws SourceCodeGenerationException
    {
        doTransform(new StreamSource(new StringReader(in)), transform,
                    out, parameters);
    }

    /**
        Transforms the specified input XML file into the specified
        output file using the specified XSL transform/stylesheet.

        @param in the XML file to transform
        @param transform the stylesheet to use to transform 'in'
        @param out the filename of the file to which to output the
        results of transforming 'in'
        @param parameters a map from the names of the global transform
        parameters to their values: all of the keys and values are Strings
        @exception SourceCodeGenerationException thrown if the
        transformation fails
    */
    protected void doTransform(File in, File transform, File out,
                               Map parameters)
        throws SourceCodeGenerationException
    {
        doTransform(new StreamSource(makeAbsolute(in)), transform,
                    out, parameters);
    }

    /**
        Transforms the specified input XML document into the specified
        output file using the specified XSL transform/stylesheet.

        @param in the XML document to transform
        @param transform the stylesheet to use to transform 'in'
        @param out the filename of the file to which to output the
        results of transforming 'in'
        @param parameters a map from the names of the global transform
        parameters to their values: all of the keys and values are Strings
        @exception SourceCodeGenerationException thrown if the
        transformation fails
    */
    protected void doTransform(org.w3c.dom.Document in, File transform,
                               File out, Map parameters)
        throws SourceCodeGenerationException
    {

        doTransform(new DOMSource(in), transform, out, parameters);
    }

    /**
        Transforms the specified stream source into the specified
        output file using the specified XSL transform/stylesheet.

        @param in the source from which to read the XML to transform
        @param transform the stylesheet to use to transform 'in'
        @param out the filename of the file to which to output the
        results of transforming 'in'
        @param parameters a map from the names of the global transform
        parameters to their values: all of the keys and values are Strings
        @exception SourceCodeGenerationException thrown if the
        transformation fails
    */
    protected void doTransform(Source in, File transform, File out,
                               Map parameters)
        throws SourceCodeGenerationException
    {
        FileOutputStream outStream = null;
        try
        {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.
                newTransformer(new StreamSource(makeAbsolute(transform)));

            Iterator iter = parameters.keySet().iterator();
            while (iter.hasNext())
            {
                String name = (String) iter.next();
                String value = (String) parameters.get(name);
                t.setParameter(name, value);
            }

            File absoluteOut = makeAbsolute(out);
            info("generating " + absoluteOut.getPath());
            outStream = new FileOutputStream(absoluteOut);
            t.transform(in, new StreamResult(outStream));
        }
        catch (TransformerConfigurationException ex)
        {
            String msg = "XSL transformer configuration failed: " +
                            ex.getLocalizedMessage();
            throw new SourceCodeGenerationException(msg, ex);
        }
        catch (TransformerException ex)
        {
            String msg = "XSL transformer error: " +
                                    ex.getLocalizedMessage();
            throw new SourceCodeGenerationException(msg, ex);
        }
        catch (FileNotFoundException ex)
        {
            String msg = "could not write to " + out.getPath() + ": " +
                            ex.getLocalizedMessage();
            throw new SourceCodeGenerationException(msg, ex);
        }
        finally
        {
            if (outStream != null)
            {
                try
                {
                    outStream.close();
                }
                catch (IOException ex)
                {
                    String msg = "could not close the output stream " +
                        "opened on\n" + out.getPath();
                    throw new SourceCodeGenerationException(msg, ex);
                }
            }
        }
    }


    // Property-related methods

    /**
        Sets this generator's properties to the ones specified.

        @param p this generator's new properties
        @exception SourceCodeGenerationException thrown if a required
        property is missing, one of the property's values is invalid,
        or the specified properties are null
    */
    protected void setProperties(Properties p)
        throws SourceCodeGenerationException
    {
        if (p == null)
        {
            String msg = "Null Properties object specified";
            throw new SourceCodeGenerationException(msg);
        }

        parseProperties(p);
    }

    /**
        Extracts all of the information from the specified
        properties.

        @param p the properties to parse
        @exception SourceCodeGenerationException thrown if a required
        property is missing or one of the property values is invalid
    */
    protected void parseProperties(Properties p)
        throws SourceCodeGenerationException
    {
        // Note: we don't make any Files absolute here: that is done
        //       just before they're used.

        // Use the base directory from the properties iff it hasn't
        // been set explicitly already.
        if (_baseDir == null)
        {
            _baseDir = new File(requiredProperty(p, "baseDirectory"));
        }
        _sourceDir = new File(requiredProperty(p, "targetSourceDirectory"));

        _languageDescriptionFile =
            new File(requiredProperty(p, "languageDescriptionDocument"));
        _languageDescription = makeDocument(_languageDescriptionFile);
        _languageTransformersDir =
            new File(requiredProperty(p, "languageTransformersDirectory"));
        _languageDocsDir =
            new File(requiredProperty(p, "languageDocumentsDirectory"));
        _languageGenerators = createLanguageGenerators(_sourceDir,
            _languageDocsDir, _languageTransformersDir,
            _languageDescription,
            implementationElement(_languageDescription));

        _genericsDescriptionFile =
            new File(requiredProperty(p, "genericClassesDocument"));
        _genericsDescription = makeDocument(_genericsDescriptionFile);
        _genericsTransformersDir = new File(
            requiredProperty(p, "genericClassesTransformersDirectory"));

        _genericsGenerators =
            createGenericsGenerators(_sourceDir,
                                     _genericsTransformersDir,
                                     _genericsDescription);

        _visitorsDescriptionFile =
            new File(requiredProperty(p, "visitorClassesDocument"));
        _visitorsDescription = makeDocument(_visitorsDescriptionFile);
        _visitorsTransformersDir = new File(
            requiredProperty(p, "visitorClassesTransformersDirectory"));

        _visitorsGenerators =
            createVisitorsGenerators(_sourceDir,
                                     _visitorsTransformersDir,
                                     _visitorsDescription);

        _fileCopiers = createFileCopiers();
    }

    /**
        Creates the FileCopies that copy files unchanged to another
        directory.
        @return the FileCopiers that copy the files unchanged
        @exception SourceCodeGenerationException thrown if one or more
        of the FileCopiers could not be created
    */
    protected List createFileCopiers()
        throws SourceCodeGenerationException
    {
        List result = new ArrayList(2);

        addFileCopier(result,
            new File(_languageTransformersDir, "stylesheet.css"),
            _languageDocsDir);
        addFileCopier(result,
            new File(_languageTransformersDir, "stylesheet-screen.css"),
            _languageDocsDir);
        addFileCopier(result,
            new File(_languageTransformersDir, "stylesheet-print.css"),
            _languageDocsDir);

        // Assert.ensure(result != null);
        return result;
    }

    /**
        Adds to the specified list a FileCopier that copies the source file
        with the specified pathname to the destination directory with the
        specified pathname.

        @param copiers the list to append the FileCopier to
        @param src the file that the FileCopier is to copy
        @param destDir the directory into which the FileCopier is to copy
        'src'
        @exception SourceCodeGenerationException thrown if the FileCopier
        could not be created
    */
    protected void addFileCopier(List copiers, File src, File destDir)
        throws SourceCodeGenerationException
    {
        // Assert.require(copiers != null);
        // Assert.require(src != null);
        // Assert.require(destDir != null);

        copiers.add(new FileCopier(src, destDir));
    }

    /**
        Creates the SingleInputGenerators that generate source code and
        documentation files from the specified generics description
        document.

        @param sourceDir the directory that all of the generated source
        files go under
        @param transformersDir the directory containing all of the XSL
        transforms/stylesheets used to tranform 'genericsDescription'
        @param genericsDescription the generics description document
        @return the SingleInputGenerators that generate the source code
        and documentation files from 'genericsDescription'
        @exception SourceCodeGenerationException thrown if one or more
        of the SingleInputGenerators could not be created
    */
    protected List createGenericsGenerators(File sourceDir,
            File transformersDir, Document genericsDescription)
        throws SourceCodeGenerationException
    {
        List result = new ArrayList();

        String suffix = xslFileSuffix();
        String ext = sourceFileExtension();
        String paramName = "item-class-name";

        // Iterate over each of the elements that contains all of the
        // elements describing the instances of a given class.
        Iterator iter = genericsDescription.
            getRootElement().getChildren().iterator();
        while (iter.hasNext())
        {
            // Each child of 'parent' is assumed to have the same name.
            Element parent = (Element) iter.next();
            List children = parent.getChildren();
            if (children.size() > 0)
            {
                String childName = ((Element) children.get(0)).getName();
                File transform =
                    new File(transformersDir, childName + suffix);
                Set depends = buildDependencies(transform);

                // Add a SingleInputGenerator for each child
                Iterator childIter = children.iterator();
                while (childIter.hasNext())
                {
                    Element child = (Element) childIter.next();
                    File moduleDir = moduleNameToDirectory(
                        child.getAttributeValue("module-name"));
                    File outDir = new File(sourceDir, moduleDir.getPath());
                    File out = new File(outDir,
                        child.getAttributeValue("class-name") + ext);
                    String paramValue =
                        child.getAttributeValue("item-name");

                    SingleInputGenerator g =
                        new SingleInputGenerator(transform, out);
                    g.addParameter(paramName, paramValue);

                    Iterator dependIter = depends.iterator();
                    while (dependIter.hasNext())
                    {
                        File f = (File) dependIter.next();
                        g.addDependency(f);
                    }
                    result.add(g);
                }
            }
        }  // while

        return result;
    }

    /**
        Creates the SingleInputGenerators that generate source code and
        documentation files from the specified visitors description
        document.

        @param sourceDir the directory that all of the generated source
        files go under
        @param transformersDir the directory containing all of the XSL
        transforms/stylesheets used to tranform 'visitorsDescription'
        @param visitorsDescription the visitors description document
        @return the SingleInputGenerators that generate the source code
        and documentation files from 'visitorsDescription'
        @exception SourceCodeGenerationException thrown if one or more
        of the SingleInputGenerators could not be created
    */
    protected List createVisitorsGenerators(File sourceDir,
            File transformersDir, Document visitorsDescription)
        throws SourceCodeGenerationException
    {
        List result = new ArrayList();

        String suffix = xslFileSuffix();
        File transform =
            new File(transformersDir, "visitor" + suffix);
        Set depends = buildDependencies(transform);
        String ext = sourceFileExtension() + ALL_EXT;
        String paramName = "base-kind";

        // Iterate over each of the elements that describes a type of
        // visitor.
        Iterator iter = visitorsDescription.
            getRootElement().getChildren().iterator();
        while (iter.hasNext())
        {
            Element visitor = (Element) iter.next();
            File moduleDir = moduleNameToDirectory(
                visitor.getAttributeValue("module-name"));
            File outDir = new File(sourceDir, moduleDir.getPath());
            String interfaceClassName =
                visitor.getAttributeValue("interface-name");
            File out = new File(outDir, interfaceClassName + ext);
            String paramValue =
                visitor.getAttributeValue("base-kind");

            File absoluteOutputDir = makeAbsolute(outDir);
            FileSplitter splitter =
                new FileSplitter(absoluteOutputDir);
            setAncillaryFiles(splitter, absoluteOutputDir,
                              interfaceClassName);

            SingleInputGenerator g =
                new SingleInputGenerator(transform, out, splitter);
            g.addParameter(paramName, paramValue);

            Iterator dependIter = depends.iterator();
            while (dependIter.hasNext())
            {
                File f = (File) dependIter.next();
                g.addDependency(f);
            }
            result.add(g);
        }  // while (iter.hasNext())

        return result;
    }

    /**
        Creates the SingleInputGenerators that are used to generate
        source code and documentation files from the specified language
        description document.

        @param sourceDir the directory that all of the generated source
        files go under
        @param docsDir the directory that all of the generated document
        files go under
        @param transformersDir the directory containing all of the XSL
        transforms/stylesheets used to tranform 'languageDescription'
        @param languageDescription the language description document
        @param impl the element of 'languageDescription' that contains
        information specific to our implementation language
        @return a list of the SingleInputGenerators to use to generate
        source code and documentation files from 'languageDescription'
        @exception SourceCodeGenerationException thrown if one or more
        of the SingleInputGenerators could not be created
    */
    protected List createLanguageGenerators(File sourceDir, File docsDir,
        File transformersDir, Document languageDescription, Element impl)
        throws SourceCodeGenerationException
    {
        // Get the directories into which to put the various generated
        // source files.
        //
        // NOTE: we assume here that the implementation element has
        //       the expected contents.
        String moduleName = impl.getChild("base-module").
                                        getAttributeValue("name");
        File baseLanguageDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteBaseLanguageDir = makeAbsolute(baseLanguageDir);


        moduleName = impl.getChild("tokens-module").
                                        getAttributeValue("name");
        File tokensDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteTokensDir = makeAbsolute(tokensDir);

        moduleName = impl.getChild("tokenizers-module").
                                        getAttributeValue("name");
        File tokenizersDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteTokenizersDir = makeAbsolute(tokenizersDir);


        moduleName = impl.getChild("constructs-module").
                                        getAttributeValue("name");
        File constructsDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteConstructsDir = makeAbsolute(constructsDir);

        moduleName = impl.getChild("parsers-module").
                                        getAttributeValue("name");
        File parsersDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteParsersDir = makeAbsolute(parsersDir);

        moduleName = impl.getChild("constructs-testing-module").
                                        getAttributeValue("name");
        File constructsTestingDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteConstructsTestingDir =
                makeAbsolute(constructsTestingDir);

        moduleName = impl.getChild("validation-module").
                                        getAttributeValue("name");
        File validationDir = new File(sourceDir,
                            moduleNameToDirectory(moduleName).getPath());
        File absoluteValidationDir = makeAbsolute(validationDir);

        List result = new ArrayList();
        String suffix = xslFileSuffix();

        // Build the SingleInputGenerators for the documents.
        File absoluteDocsDir = makeAbsolute(docsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "semantics.xsl"),
            new File(docsDir, "Semantics" + ALL_EXT),
            "semantics", absoluteDocsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "validity-constraints.xsl"),
            new File(docsDir, "ValidityConstraints" + ALL_EXT),
            "validity-constraints", absoluteDocsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "grammar.xsl"),
            new File(docsDir, "Grammar" + ALL_EXT),
            "grammar", absoluteDocsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "main-document.xsl"),
            new File(docsDir, "MainDocument" + ALL_EXT),
            "main-document", absoluteDocsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "operator-method-map.xsl"),
            new File(docsDir, "OperatorMethodMap" + ALL_EXT),
            "operator-method-map", absoluteDocsDir);
        addLanguageDocumentationGenerator(result,
            new File(transformersDir, "frame-documents.html.xsl"),
            new File(docsDir, "FrameDocuments" + ALL_EXT),
            "frame-documents", absoluteDocsDir);

        // Build the SingleInputGenerators for the source files.
        String ext = sourceFileExtension();
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "Constants" + suffix),
            new File(baseLanguageDir, "Constants" + ext + ALL_EXT),
            "constants", absoluteBaseLanguageDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "CharacterClasses" + suffix),
            new File(baseLanguageDir, "CharacterClasses" + ext + ALL_EXT),
            "character-classes", absoluteBaseLanguageDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "TokenManagers" + suffix),
            new File(tokensDir, "TokenManagers" + ext + ALL_EXT),
            "token-managers", absoluteTokensDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "SourceTokenizers" + suffix),
            new File(tokenizersDir, "SourceTokenizers" + ext + ALL_EXT),
            "source-tokenizers", absoluteTokenizersDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "ConstructManagers" + suffix),
            new File(constructsDir, "ConstructManagers" + ext + ALL_EXT),
            "construct-managers", absoluteConstructsDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "ConstructVisitors" + suffix),
            new File(constructsDir, "ConstructVisitors" + ext + ALL_EXT),
            "construct-visitors", absoluteConstructsDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "Parsers" + suffix),
            new File(parsersDir, "Parsers" + ext + ALL_EXT),
            "parsers", absoluteParsersDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir,
                     "ValidityConstraintChecklists" + suffix),
            new File(constructsDir,
                     "ValidityConstraintChecklists" + ext + ALL_EXT),
            "validity-constraint-checklists", absoluteConstructsDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir,
                     "ConstructTestDataCreators" + suffix),
            new File(constructsTestingDir,
                     "ConstructTestDataCreators" + ext + ALL_EXT),
            "construct-test-data-creators",
            absoluteConstructsTestingDir);
        addLanguageSourceCodeGenerator(result,
            new File(transformersDir, "Validators" + suffix),
            new File(validationDir, "Validators" + ext + ALL_EXT),
            "validators", absoluteValidationDir);

        return result;
    }

    /**
        @param languageDescription the language description document
        @return the element of the language description document that
        contains information specific to our implementation language
        @exception SourceCodeGenerationException thrown iff no element
        corresponding to our implementation language could be found
    */
    protected Element implementationElement(Document languageDescription)
        throws SourceCodeGenerationException
    {
        Element result = null;

        String langName = implementationLanguageName();
        Iterator iter = languageDescription.
            getRootElement().getChildren("implementation").iterator();
        while (result == null && iter.hasNext())
        {
            Element elem = (Element) iter.next();
            if (langName.equals(elem.getAttributeValue("language")))
            {
                result = elem;
            }
        }
        if (result == null)
        {
            String msg =
                "could not find an implementation element in the " +
                    "language description\n" +
                "document whose 'language' attribute's value is " +
                    langName;
            throw new SourceCodeGenerationException(msg);
        }

        return result;
    }


    // Protected methods

    /**
        Sets the pathnames of various ancillary files on the specified
        file splitter to be files in the specified directory that have
        the specified base name as their filename (though with different
        extensions).
        <p>
        Currently the 'generated files' and 'files to discard' files
        are the ones that are set.

        @param splitter the file splitter whose ancillary file's
        pathnames are to be set
        @param dir the directory that the ancillary files will be in
        @param basename the ancillary files' base name
    */
    protected void setAncillaryFiles(FileSplitter splitter, File dir,
                                     String basename)
    {
        // Assert.require(splitter != null);
        // Assert.require(dir != null);
        // Assert.require(basename != null);

        splitter.setSplitOutFilesFile(new File(dir,
            basename + GENERATED_EXT));
        splitter.setFilesToDiscardFile(new File(dir,
            basename + DISCARD_EXT));
    }

    /**
        Returns the value of the required property with the specified
        name in the specified set of properties, or throws an exception
        if the property is missing.

        @param p the set of properties in which to look for the
        property
        @param propertyName the name of the property to look for
        @return the value of the property in 'p' named 'propertyName'
        @exception SourceCodeGenerationException thrown if 'p' doesn't
        contain a property named 'propertyName'
    */
    protected String requiredProperty(Properties p, String propertyName)
        throws SourceCodeGenerationException
    {
        String result = p.getProperty(propertyName);

        if (result == null)
        {
            String msg = "The required property named " + propertyName +
                         " is missing";
            throw new SourceCodeGenerationException(msg);
        }

        return result;
    }

    /**
        Makes the specified filename absolute.

        @param f the filename to make absolute
        @return 'f' if it is already absolute; otherwise the filename
        constructed from the base directory and 'f'
        @exception SourceCodeGenerationException thrown if the base
        directory hasn't been set yet
    */
    protected File makeAbsolute(File f)
        throws SourceCodeGenerationException
    {
        File result = f;

        if (result.isAbsolute() == false)
        {
            if (_baseDir == null)
            {
                String msg = "base directory not set";
                throw new SourceCodeGenerationException(msg);
            }
            result = new File(_baseDir, f.getPath());
        }

        return result;
    }

    /**
        Returns the Document object representing the specified XML
        file.

        @param documentFile the XML file to make the Document for
        @return a Document representing the XML file
        @exception SourceCodeGenerationException thrown if an error
        occurs in reading the XML file
    */
    protected Document makeDocument(File documentFile)
        throws SourceCodeGenerationException
    {
        Document result;

        SAXBuilder builder = new SAXBuilder();
        try
        {
            result = builder.build(makeAbsolute(documentFile));
        }
        catch (JDOMException ex)
        {
            String msg = "could not create an XML Document from the " +
                         "file named " + documentFile + ":\n" +
                         ex.getLocalizedMessage();
            throw new SourceCodeGenerationException(msg, ex);
        }
        catch (IOException ex)
        {
            String msg = "an I/O error occurred in trying to create " +
                         "an XML Document from the file named " +
                         documentFile + ":\n" + ex.getLocalizedMessage();
            throw new SourceCodeGenerationException(msg, ex);
        }

        return result;
    }


    /**
        Adds to the specfied list a SingleInputGenerator that is used to
        generate documentation files from a language description document,
        and that is built from the specified transform and output files.

        @param generators the list of SingleInputGenerators to append
        a SingleInputGenerator to
        @param transform the XSL transform file to construct the
        SingleInputGenerator from
        @param output the output file to construct the SingleInputGenerator
        from
        @exception SourceCodeGenerationException thrown if the generator
        could not be created
    */
    protected void addLanguageDocumentationGenerator(List generators,
                                                File transform, File output)
        throws SourceCodeGenerationException
    {
        addGenerator(generators, transform, output, null, null);
    }

    /**
        Adds to the specfied list a SingleInputGenerator that is used to
        generate documentation files from a language description document,
        and that is built from the specified transform and output files,
        as well as a FileSplitter built from the specified (absolute)
        output directory and base name.

        @param generators the list of SingleInputGenerators to append
        a SingleInputGenerator to
        @param transform the XSL transform file to construct the
        SingleInputGenerator from
        @param output the output file to construct the SingleInputGenerator
        from
        @param basename the base name to construct the SingleInputGenerator's
        FileSplitter from
        @param absoluteOutputDir the absolute output directory pathname to
        construct the SingleInputGenerator's FileSplitter from
        @exception SourceCodeGenerationException thrown if the generator
        could not be created
    */
    protected void addLanguageDocumentationGenerator(List generators,
        File transform, File output, String basename,
        File absoluteOutputDir)
        throws SourceCodeGenerationException
    {
        addGenerator(generators, transform, output, basename,
                     absoluteOutputDir);
    }

    /**
        Adds to the specfied list a SingleInputGenerator that is used to
        generate source code files from a language description document,
        and that is built from the specified transform and output files,
        as well as a FileSplitter built from the specified (absolute)
        output directory and base name.

        @param generators the list of SingleInputGenerators to append
        a SingleInputGenerator to
        @param transform the XSL transform file to construct the
        SingleInputGenerator from
        @param output the output file to construct the SingleInputGenerator
        from
        @param basename the base name to construct the SingleInputGenerator's
        FileSplitter from
        @param absoluteOutputDir the absolute output directory pathname to
        construct the SingleInputGenerator's FileSplitter from
        @exception SourceCodeGenerationException thrown if the generator
        could not be created
    */
    protected void addLanguageSourceCodeGenerator(List generators,
        File transform, File output, String basename,
        File absoluteOutputDir)
        throws SourceCodeGenerationException
    {
        addGenerator(generators, transform, output, basename,
                     absoluteOutputDir);
    }

    /**
        Adds to the specfied list a SingleInputGenerator that is used to
        generate a source code file from a language description document,
        and that is built from the specified transform and output files.

        @param generators the list of SingleInputGenerators to append
        a SingleInputGenerator to
        @param transform the XSL transform file to construct the
        SingleInputGenerator from
        @param output the output file to construct the SingleInputGenerator
        from
        @exception SourceCodeGenerationException thrown if the generator
        could not be created
    */
    protected void addLanguageSourceCodeGenerator(List generators,
                                                File transform, File output)
        throws SourceCodeGenerationException
    {
        addGenerator(generators, transform, output, null, null);
    }

    /**
        Given the specified XSL transformation file, returns a set of
        all of the XSL files that it depends on, either directly or
        indirectly.

        @param transform the XSL transformation file
        @return the pathnames of the XSL files that 'transform' directly
        or indirectly depends on: each item in it is a File
        @exception SourceCodeGenerationException thrown if an error occurs
        in building the dependencies
    */
    protected Set buildDependencies(File transform)
        throws SourceCodeGenerationException
    {
        Set result = new HashSet();

        Document doc = makeDocument(transform);
        Iterator iter = importsAndIncludes(doc).iterator();
        while (iter.hasNext())
        {
            Element e = (Element) iter.next();
            String target = e.getAttributeValue("href");
            if (target != null)
            {
                File dependency = new File(transform.getParent(), target);
                if (result.contains(dependency) == false)
                {
                    result.add(dependency);

                    // Add all of 'dependency''s direct and indirect
                    // dependencies too.
                    result.addAll(buildDependencies(dependency));
                }
            }
        }

        return result;
    }

    /**
        Returns a list of all of the XSL 'include' and 'import' elements
        in the specified XSL document.

        @param doc the XSL document
        @return a list of all of the 'import' and 'include' elements in
        'doc'
    */
    protected List importsAndIncludes(Document doc)
    {
        Element root = doc.getRootElement();
        List imports = root.getChildren("import", XSL_NAMESPACE);
        List includes = root.getChildren("include", XSL_NAMESPACE);

        List result = new ArrayList(imports.size() + includes.size());

        result.addAll(imports);
        result.addAll(includes);

        return result;
    }


    /**
        Converts the specified module name into the (relative) pathname
        of the corresponding directory.

        @param moduleName the name of a module
        @return the relative pathname of the directory in which to put
        generated source files containing classes in that module
    */
    protected File moduleNameToDirectory(String moduleName)
    {
        return new File(moduleName.replace('.', File.separatorChar));
    }

    /**
        @return the suffix of all language-specific XSL files
    */
    protected String xslFileSuffix()
    {
        return sourceFileExtension() + XSL_EXT;
    }


    /**
        @param f a file
        @return an XML document that represents the contents of 'f'
        @exception SourceCodeGenerationException thrown if the contents
        of 'f' is not a well-formed XML document, or an I/O error occurred
        in reading the contents of 'f'
    */
    protected org.w3c.dom.Document fileContentsAsDocument(File f)
        throws SourceCodeGenerationException
    {
        DOMOutputter outputter = new DOMOutputter();
        try
        {
            return outputter.output(makeDocument(f));
        }
        catch (JDOMException ex)
        {
            throw new SourceCodeGenerationException(ex);
        }
    }

    /**
        @param f a file
        @return a String containing the contents of 'f'
        @exception SourceCodeGenerationException thrown if an I/O error
        occurs reading the contents of 'f'
    */
    protected String fileContents(File f)
        throws SourceCodeGenerationException
    {
        char[] cbuf = new char[INPUT_BUFFER_SIZE];
        StringBuffer buf = new StringBuffer(INPUT_BUFFER_SIZE);

        FileReader r = null;
        try
        {
            r = new FileReader(makeAbsolute(f));
            while (true)
            {
                int n = r.read(cbuf);
                if (n == -1)
                {
                    break;  // end of file
                }
                buf.append(cbuf, 0, n);
            }
        }
        catch (IOException ex)
        {
            throw new SourceCodeGenerationException(ex);
        }
        finally
        {
            tryToClose(r);
        }

        return buf.toString();
    }


    // Private methods

    /**
        Adds to the specfied list a SingleInputGenerator that is used to
        generate source code files from a language description document,
        and that is built from the specified transform and output files,
        as well as a FileSplitter built from the specified (absolute)
        output directory and base name.

        @param generators the list of SingleInputGenerators to append
        a SingleInputGenerator to
        @param transform the XSL transform file to construct the
        SingleInputGenerator from
        @param output the output file to construct the SingleInputGenerator
        from
        @param basename the base name to construct the SingleInputGenerator's
        FileSplitter from, or null if the file is not to be split
        @param absoluteOutputDir the absolute output directory pathname to
        construct the SingleInputGenerator's FileSplitter from, or null if
        the file is not to be split
        @exception SourceCodeGenerationException thrown if the generator
        could not be created
    */
    private void addGenerator(List generators,
        File transform, File output, String basename,
        File absoluteOutputDir)
        throws SourceCodeGenerationException
    {
        SingleInputGenerator g;

        if (absoluteOutputDir != null && basename != null)
        {
            FileSplitter splitter = new FileSplitter(absoluteOutputDir);
            setAncillaryFiles(splitter, absoluteOutputDir, basename);
            g = new SingleInputGenerator(transform, output, splitter);
        }
        else
        {
            g = new SingleInputGenerator(transform, output);
        }

        Iterator iter = buildDependencies(transform).iterator();
        while (iter.hasNext())
        {
            File f = (File) iter.next();
            g.addDependency(f);
        }

        generators.add(g);
    }

    /**
        Returns the name of the output file specified by the language
        description document element with the specified name that is a
        child of the specified implementation element from that document,
        and that has the specified extension.

        @param implementation the language description document
        implementation element that contains the child element
        @param childElementName the name of the child element of
        'implementation' that specifies the output file's name
        @param the output file's extension
        @return the full filename (not including any directory parts) of
        the output file
    */
    private String outputFilename(Element implementation,
                                  String childElementName,
                                  String extension)
    {
        return implementation.getChild(childElementName).
                    getAttributeValue("name") + extension;
    }


    // Protected static methods

    /**
        Tries to close() the specified Reader, iff it is non-null,
        but ignores any IOExceptions that occur.

        @param r the Reader to try to close (iff it isn't null)
    */
    protected static void tryToClose(Reader r)
    {
        if (r != null)
        {
            try
            {
                r.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }

    /**
        Tries to flush() the specified Writer, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param w the Writer to try to flush (iff it isn't null)
    */
    protected static void tryToFlush(Writer w)
    {
        if (w != null)
        {
            try
            {
                w.flush();
            }
            catch (IOException ex)
            {
                // ignore failure to flush
            }
        }
    }

    /**
        Tries to close() the specified Writer, iff it isn't null,
        but ignores any IOExceptions that occur.

        @param w the Writer to try to close (iff it isn't null)
    */
    protected static void tryToClose(Writer w)
    {
        if (w != null)
        {
            try
            {
                w.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }


    // Abstract methods

    /**
        @return the extension of all generated source files (not
        including documents)
    */
    protected abstract String sourceFileExtension();

    /**
        @return the name of the implementation language
    */
    protected abstract String implementationLanguageName();


    // main() support fields and methods

    /** The pathname of the source generation properties file. */
    private static String _propertiesPathname;

    /** Indicates whether we should force source file generation. */
    private static boolean _force;

    /** Indicates whether we should allow parallel file generation. */
    private static boolean _doGenerateInParallel;

    /** Indicates whether we should be verbose. */
    private static boolean _verbose;

    /**
        Iff non-null, the base directory that is to override the one
        in the properties file.
    */
    private static String _argBaseDir;

    /**
        Parses the specified arguments, setting the (static) fields
        of this class accordingly.

        @param args the arguments to parse
        @return true if the arguments are valid, and false if they
        aren't
    */
    protected static boolean parseArguments(String[] args)
    {
        boolean result = true;

        _propertiesPathname = null;
        _force = false;
        _doGenerateInParallel = false;
        _argBaseDir = null;
        _verbose = false;

        for (int i = 0; result && i < args.length; i++)
        {
            String arg = args[i];

            // Parse the options here.
            if (arg.equals("-f"))
            {
                _force = true;
                continue;
            }
            if (arg.equals("-v"))
            {
                _verbose = true;
                continue;
            }
            if (arg.equals("-p"))
            {
                _doGenerateInParallel = true;
                continue;
            }
            if (arg.equals("-b"))
            {
                if (i + 1 < args.length)
                {
                    i += 1;
                    _argBaseDir = args[i];
                }
                else
                {
                    // There is no base directory after the option.
                    result = false;
                }
                continue;
            }

            // If we get here then 'arg' isn't an option so we assume
            // it is the properties pathname argument.
            _propertiesPathname = arg;
            if (i + 1 < args.length)
            {
                // The arguments are invalid if there are more arguments
                // after the properties pathname.
                result = false;
            }
            break;
        }

        return result;
    }

    /**
        Runs the specified source code generator after configuring it
        according to the specified arguments.
        <p>
        This method is usually called from a subclass' main() method,
        and is usually the only thing such a main() method calls.
        Subclasses' main() methods should never call System.exit()
        so that such a call can be centralized here.

        @param args the command-line arguments
        @param generator the source code generator to configure and run
    */
    public static void run(String[] args, SourceCodeGenerator generator)
    {
        int result = 0;

        if (parseArguments(args) == false)
        {
            error(null, "\nusage: java com.steelcandy.build." +
                    "SourceCodeGenerator [properties-file]\n\n" +
                  "where 'properties-file' is the pathname of the " +
                    "properties file that\n" +
                  "describes what source code to generate and where " +
                    "to put the generated\n" +
                  "source files\n\n");
            result = 1;
        }
        else
        {
            File f = new File(_propertiesPathname);
            info("using the properties file " + f.getPath());

            generator.setForceGeneration(_force);
            if (_force)
            {
                info("generation of all source files has been forced");
            }

            generator.setDoParallelizeGeneration(_doGenerateInParallel);
            if (_doGenerateInParallel)
            {
                info("some source files will be generated in parallel");
            }

            if (_argBaseDir != null)
            {
                info("overriding the base directory to be " + _argBaseDir);
                generator.setBaseDirectory(_argBaseDir);
            }

            FileInputStream in = null;
            try
            {
                in = new FileInputStream(f);
                Properties p = new Properties();
                p.load(in);
                generator.setProperties(p);

                generator.generate();
            }
            catch (IOException ex)
            {
                error(ex, "\ncould not read the specified properties file " +
                        args[0] + "\n" +
                      "due to an exception of class " +
                        ex.getClass().getName() + ": " +
                        ex.getLocalizedMessage() + "\n\n");
                result = 2;
            }
            catch (SourceCodeGenerationException ex)
            {
                error(ex, "\nan error occurred in trying to generate " +
                        "the source code:\n" +
                      ex.getLocalizedMessage());
                result = 4;
            }
            finally
            {
                if (in != null)
                {
                    try
                    {
                        in.close();
                    }
                    catch (IOException ex)
                    {
                        error(ex, "\ncould not close the properties file " +
                                args[0] + "\n\n");
                        result = 3;
                    }
                }
            }
        }

        // Uncomment this iff we want this program to cause the JVM
        // to exit. (Currently we do since I don't know how else to have
        // Ant figure out that this failed.)
        System.exit(result);
    }

    /**
        Logs the specified error message.

        @param msg the error message to log
    */
    private static void error(Throwable ex, String msg)
    {
        if (_verbose && (ex != null)) {
            ex.printStackTrace();
        }
        System.err.println(msg);
    }

    /**
        Logs the specified informational message.

        @param msg the informational message to log
    */
    private static void info(String msg)
    {
        System.out.println(msg);
    }


    // Inner classes

    /**
        Copies a specified file to a specified directory, if it isn't
        up to date.
    */
    public class FileCopier
    {
        // Private fields

        /** The file to copy. */
        private File _source;

        /** The pathname of the copy of the file. */
        private File _dest;


        // Constructors

        /**
            Constructs a FileCopier from the pathname of the file it is
            to copy and the directory to which it is to copy it.

            @param source the pathname of the file to be copied
            @param destDir the pathname of the directory to copy 'source' to
            @exception SourceCodeGenerationException thrown if the FileCopier
            could not be constructed
        */
        public FileCopier(File source, File destDir)
            throws SourceCodeGenerationException
        {
            // Assert.require(source != null);
            // Assert.require(destDir != null);
            // Assert.require(destDir.isDirectory());

            _source = makeAbsolute(source);
            _dest = makeAbsolute(new File(destDir, source.getName()));
        }


        // Public methods

        /**
            Copies our source file to our destination directory.

            @exception SourceCodeGenerationException thrown if the file
            could not be successfully copied
        */
        public void copy()
            throws SourceCodeGenerationException
        {
            FileReader r = null;
            FileWriter w = null;

            info("copying " + _source.getPath() + " to " + _dest.getPath());
            try
            {
                r = new FileReader(_source);
                w = new FileWriter(_dest);
                while (true)
                {
                    int ch = r.read();
                    if (ch == -1)
                    {
                        break;  // end of file
                    }
                    w.write(ch);
                }
            }
            catch (IOException ex)
            {
                String msg = "could not copy the file " + _source.getPath() +
                    " to " + _dest.getPath() + ": " +
                    ex.getLocalizedMessage();
                throw new SourceCodeGenerationException(msg, ex);
            }
            finally
            {
                tryToClose(r);
                tryToClose(w);
            }
        }

        /**
            Indicates whether our destination file is newer than our source
            file.
            <p>
            If our destination file is newer than our source file then we
            don't usually have to copy the file.

            @return true iff our destination file is newer than our source
            file
            @exception SourceCodeGenerationException thrown if the
            last modified times of the files cannot be obtained
        */
        public boolean isUpToDate()
            throws SourceCodeGenerationException
        {
            boolean result = false;

            if (_dest.exists())
            {
                long srcTime = _source.lastModified();
                long destTime = _dest.lastModified();

                // NOTE: I think usually one checks whether the output file
                // is newer than OR as new as the other files. We require
                // that it be strictly newer just to be paranoid.
                result = (destTime > srcTime);
            }

            return result;
        }
    }

    /**
        A source code generator that generates zero or more source code or
        documentation files from a single input file.
    */
    public class SingleInputGenerator
    {
        // Constants

        /**
            The name of the global transform parameter whose value is
            the (possibly relative) pathname of the input file that a
            generator transforms into an output file.
        */
        private static final String
            INPUT_PATHNAME_PARAM_NAME = "input-pathname";

        /**
            The name of the global transform parameter whose value is
            the (possibly relative) pathname of the XSL file used to
            transform input files.
        */
        private static final String
            TRANSFORM_PATHNAME_PARAM_NAME = "transform-pathname";

        /**
            The name of the global transform parameter whose value is
            the (possibly relative) pathname of the output file that
            a generator generates.
        */
        private static final String
            OUTPUT_PATHNAME_PARAM_NAME = "output-pathname";

        /**
            The name of the global transform parameter whose value is
            the date/time at which the transformation took place.
        */
        private static final String
            GENERATION_DATE_TIME_PARAM_NAME = "generation-date-time";


        // Private fields

        /**
            The XSL document used to transform an input document into
            the output document.
        */
        private File _transform;

        /**
            The file that the stylesheet is to transform an input
            document into.
        */
        private File _output;

        /**
            The splitter to use to split the output file, or null if
            the output file is not to be split.

            @see #_output
        */
        private FileSplitter _splitter;

        /**
            A list of the other files that the generation of the output
            file depends on. Each element is a File.
            <p>
            These files are used in determining whether the output file
            is up to date.
        */
        private List _dependencies;

        /**
            A map from the names of XSL transformer global parameters to
            their values. All of the keys and values are Strings.
        */
        private Map _transformParameters;


        // Constructors

        /**
            Constructs a SingleInputGenerator.

            @param transform the XSL file to use to transform an input
            file into 'output'
            @param output the file that 'transform' is to transform an
            input file into (though 'output' may be split into other
            files by 'splitter')
            @param splitter the splitter to use to split 'output' into
            multiple files, or null if 'output' is not to be split
        */
        public SingleInputGenerator(File transform, File output,
                                    FileSplitter splitter)
        {
            _transform = transform;
            _output = output;
            _splitter = splitter;

            _dependencies = new ArrayList();
            _transformParameters = new HashMap();
            addCommonParameters();
        }

        /**
            Constructs a SingleInputGenerator. The output file will not be
            split.

            @param transform the XSL file to use to transform an input
            file into 'output'
            @param output the file that 'transform' is to transform an
            input file into
        */
        public SingleInputGenerator(File transform, File output)
        {
            this(transform, output, null);
        }


        // Public methods

        /**
            Adds the global transform parameter with the specified name and
            value to this generator's transform.
            <p>
            If a parameter with the specified name already exists then the
            new value will replace the old value.

            @param name the name of the parameter to add
            @param value the value of the parameter to add
        */
        public void addParameter(String name, String value)
        {
            // Assert.require(name != null);
            // Assert.require(value != null);

            _transformParameters.put(name, value);
        }

        /**
            Adds the specified file as one of the files (other than the
            input file and the stylesheet) that is involved in generating
            our output file.

            @param f the file to add as one of the files involved in
            generating our output file
        */
        public void addDependency(File f)
        {
            _dependencies.add(f);
        }

        /**
            Indicates whether our output file is newer than the specified
            input file, our stylesheet and any other files involved in
            generating the output file.
            <p>
            If the output file is newer than both the input file and
            the stylesheet then we usually don't have to transform the
            input file into the output file.

            @param input the input file to use in determining whether our
            output file is up to date
            @exception SourceCodeGenerationException thrown if the
            last modified times of the files cannot be obtained
        */
        public boolean isUpToDate(File input)
            throws SourceCodeGenerationException
        {
            long latestTime = makeAbsolute(input).lastModified();
            latestTime = Math.max(latestTime,
                                  makeAbsolute(_transform).lastModified());

            Iterator iter = _dependencies.iterator();
            while (iter.hasNext())
            {
                File f = (File) iter.next();
                latestTime = Math.max(latestTime,
                                      makeAbsolute(f).lastModified());
            }

            // NOTE: I think usually one checks whether the output file
            // is newer than OR as new as the other files. We require
            // that it be strictly newer just to be paranoid.
            File absoluteOut = makeAbsolute(_output);
            boolean result = (absoluteOut.lastModified() > latestTime);
            if (result)
            {
                // info(absoluteOut.getPath() + " is up to date");
            }

            return result;
        }

        /**
            Generates our output file by transforming the specified input
            using our stylesheet, then splitting it iff we have a splitter to
            do it.

            @param input the pathname of the input file to transform into
            our output file
            @param contents the contents of the file with pathname 'input',
            as an XML document
            @exception SourceCodeGenerationException thrown if the output
            file could not be generated
        */
        public void transform(File input, org.w3c.dom.Document contents)
            throws SourceCodeGenerationException
        {
            addParameter(INPUT_PATHNAME_PARAM_NAME, input.getPath());
            doTransform(contents, _transform, _output, _transformParameters);
            if (_splitter != null)
            {
                try
                {
                    _splitter.split(makeAbsolute(_output));
                }
                catch (IOException ex)
                {
                    // TODO: log the stack trace !!!
                    ex.printStackTrace();
                    String msg = ex.getLocalizedMessage();
                    throw new SourceCodeGenerationException(msg, ex);
                }
            }
        }


        // Protected methods

        /**
            Adds the global transform parameters common to the generation
            of most/all source code generations to our transform.
            <p>
            Parameters should be added using <code>addParameter()</code>.

            @see #addParameter(String, String)
        */
        protected void addCommonParameters()
        {
            addParameter(TRANSFORM_PATHNAME_PARAM_NAME,
                         _transform.getPath());
            addParameter(OUTPUT_PATHNAME_PARAM_NAME,
                         _output.getPath());
            addParameter(GENERATION_DATE_TIME_PARAM_NAME,
                         _generationDateTime);
        }
    }

    /**
        The class of Thread that runs a SingleInputGenerator on an input
        file.
    */
    private static class GenerationThread
        extends Thread
    {
        // Private fields

        /** The pathname of the input file. */
        File _input;

        /** The contents of the input file as an XML document. */
        org.w3c.dom.Document _contents;

        /** The generator. */
        private SingleInputGenerator _generator;

        /**
            The SourceCodeGenerationException thrown while generating the
            source code, or null if no such exception has been thrown yet.
        */
        private SourceCodeGenerationException _exception;


        // Constructors

        /**
            Constructs a new GenerationThread.

            @param input the pathname of the file from which to generate the
            source code
            @param contents the contents of the file named 'input' as an XML
            document
            @param g the generator to apply to 'contents' to generate the
            source code
        */
        public GenerationThread(File input, org.w3c.dom.Document contents,
                                SingleInputGenerator g)
        {
            _input = input;
            _contents = contents;
            _generator = g;
            _exception = null;
        }


        // Public methods

        /**
            @see Thread#run
        */
        public void run()
        {
            try
            {
                _generator.transform(_input, _contents);
            }
            catch (SourceCodeGenerationException ex)
            {
                _exception = ex;
            }
        }

        /**
            @return the SourceCodeGenerationException that was thrown while
            generating the source code, or null if no such exception was
            thrown
        */
        public SourceCodeGenerationException exception()
        {
            return _exception;
        }
    }
}
