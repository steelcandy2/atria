/*
 Copyright (C) 2004-2005 by James MacKay.

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

package com.steelcandy.plack.common.compiler.documenter;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.DocumentationError;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.constructs.Construct;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;
import com.steelcandy.common.xml.XmlUtilities;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
    An abstract base class for documentation generators.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public abstract class AbstractDocumentationGenerator
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonDocumenterResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DOCUMENTATION_OUTPUT_FAILED_MSG =
            "DOCUMENTATION_OUTPUT_FAILED_MSG";
    private static final String
        UNKNOWN_NAME_MSG =
            "UNKNOWN_NAME_MSG";


    /** The name used when the actual name is missing. */
    private static final String
        UNKNOWN_NAME = _resources.getMessage(UNKNOWN_NAME_MSG);


    // Protected static methods

    /**
        @return the name to use when the actual name is missing
    */
    protected static String unknownName()
    {
        // Assert.ensure(result != null);
        return UNKNOWN_NAME;
    }

    /**
        Outputs the documentation in the specified XML document to the file
        with the specified pathname.

        @param doc the XML document containing the generated documentation
        that is to be output
        @param outputFile the pathname of the file that the generated
        documentation is to be output to
        @param handler the error handler to use to handle any errors that
        occur in outputting the documentation
    */
    protected static void
        outputDocumentation(Document doc, File outputFile,
                            ErrorHandler handler)
    {
        Assert.require(doc != null);
        Assert.require(outputFile != null);
        Assert.require(Io.mayBeFile(outputFile));
        Assert.require(handler != null);

        Format fmt = Format.getPrettyFormat();
        fmt.setIndent("    ");
        XMLOutputter outputter = new XMLOutputter(fmt);

        FileWriter w = null;
        try
        {
            w = new FileWriter(outputFile);
            outputter.output(doc, w);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(DOCUMENTATION_OUTPUT_FAILED_MSG,
                           outputFile.getPath(), ex.getLocalizedMessage());
            handler.handle(new DocumentationError(NON_FATAL_ERROR_LEVEL,
                msg, null, null));
        }
        finally
        {
            Io.tryToClose(w);
        }
    }

    /**
        @param c a construct
        @return the source code that 'c' represents
        @exception DocumentationGenerationIoException if an I/O error
        occurs in trying to obtain the source code
    */
    protected static String sourceCode(Construct c)
    {
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());

        String result;

        try
        {
            result = c.sourceCode().fragmentAt(c.location());
        }
        catch (IOException ex)
        {
            throw new DocumentationGenerationIoException(ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    // JDOM utility methods

    /**
        @see XmlUtilities#createDocument(String)
    */
    protected static Document createDocument(String rootName)
    {
        Assert.require(rootName != null);

        Document result = XmlUtilities.createDocument(rootName);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see XmlUtilities#createChildElement(Element, String)
    */
    protected static Element createChildElement(Element parent, String name)
    {
        Assert.require(parent != null);
        Assert.require(name != null);

        Element result = XmlUtilities.createChildElement(parent, name);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see XmlUtilities#addElement(Element, Element)
    */
    protected static void addElement(Element parent, Element child)
    {
        Assert.require(parent != null);
        Assert.require(child != null);

        XmlUtilities.addElement(parent, child);
    }

    /**
        @see XmlUtilities#addAttribute(Element, String, String)
    */
    protected static void addAttribute(Element e, String name, String value)
    {
        Assert.require(e != null);
        Assert.require(name != null);
        Assert.require(value != null);

        XmlUtilities.addAttribute(e, name, value);
    }

    /**
        @see XmlUtilities#addAttribute(Element, String, boolean)
    */
    protected static void addAttribute(Element e, String name, boolean value)
    {
        Assert.require(e != null);
        Assert.require(name != null);

        XmlUtilities.addAttribute(e, name, value);
    }
}
