/*
 Copyright (C) 2003-2006 by James MacKay.

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

package com.steelcandy.build.plack;

// import com.steelcandy.common.debug.Assert;

import org.jdom.*;
import org.jdom.input.SAXBuilder;

import java.io.*;
import java.util.*;

/**
    An abstract base class for XML checkers that check entire XML
    documents.
    <p>
    Subclasses have to implement createRootElementChecker(), and often
    override doComplexChecks().

    @author James MacKay
*/
public abstract class AbstractXmlDocumentChecker
    extends AbstractXmlChecker
{
    // Constants

    /** The single NullElementChecker instance. */
    private static final NullElementChecker
        _nullElementChecker = new NullElementChecker();


    // Private fields

    /**
        The language description document being checked by this
        checker.
    */
    private Document _doc;


    // Constructors

    /**
        Constructs an AbstractXmlDocumentChecker.

        @param doc the XML document that the checker is to check
        @param out the output stream on which to report any errors
        found by the checker in the XML document
    */
    public AbstractXmlDocumentChecker(Document doc, PrintStream out)
    {
        super(out);
        // Assert.require(doc != null);
        // Assert.require(out != null);

        setDocument(doc);
    }

    /**
        Constructs an AbstractXmlDocumentChecker.

        @param docFile the file whose contents are the XML document that
        the checker is to check
        @param out the output stream on which to report any errors
        found by the checker in the XML document
        @exception IOException if the contents of 'docFile' could not
        be read
        @exception JDOMException thrown if the contents of 'docFile'
        are not a valid XML document
    */
    public AbstractXmlDocumentChecker(File docFile, PrintStream out)
        throws IOException, JDOMException
    {
        super(out);
        // Assert.require(docFile != null);
        // Assert.require(out != null);

        setDocument(docFile);
    }

    /**
        Constructs an AbstractXmlDocumentChecker that isn't ready for
        use yet.
        <p>
        An instance constructed using this constructor must have its
        setOutputStream() and setDocument() methods called before it
        can be used.

        @see AbstractXmlChecker#setOutputStream(PrintStream)
        @see #setDocument(Document)
    */
    protected AbstractXmlDocumentChecker()
    {
        super();
        _doc = null;
    }


    // Public methods

    /**
        Checks our XML document for errors.

        @see #numberOfErrors
    */
    public void checkDocument()
    {
        // Assert.check(_doc != null);
        ElementChecker checker = createRootElementChecker();
        try
        {
            checker.check(_doc.getRootElement());
            if (numberOfErrors() == 0)
            {
                doComplexChecks(_doc, checker);
            }
            else
            {
                reportMessage("the more complex checks were not performed " +
                    "on the XML document due to the presence of earlier " +
                    "errors");
            }
        }
        catch (FatalXmlCheckingErrorException ex)
        {
            // empty - the fatal error has already been reported
        }
    }


    // Public static methods

    /**
        Executes the specified instance of this class as part of
        the implementation of a main() method.
        <p>
        This method executes by using the specified checker to check
        the XML document in the file whose pathname is given by the
        first command line argument.

        @param args the command line arguments
        @param checker the XML document checker to use to check the
        XML document
        @return an exit code, suitable for use by System.exit(): it
        will be zero if no errors were found in the XML document, and
        greater than zero otherwise
    */
    protected static int execute(String[] args,
                                 AbstractXmlDocumentChecker checker)
    {
        // Assert.require(args != null);
        // Assert.require(checker != null);

        int result = 0;

        PrintStream out = System.err;
        if (args.length == 1)
        {
            checker.setOutputStream(out);

            // NOTE: we can't use common.xml.XmlUtilities in this
            // class since source code generation code can't depend
            // on other code.
            File docFile = new File(args[0]);
            Document doc = null;
            try
            {
                checker.setDocument(docFile);
                checker.checkDocument();
                if (checker.numberOfErrors() > 0)
                {
                    result = 1;
                }
            }
            catch (IOException ex)
            {
                // TODO: localize this !!!???!!!
                out.println("Could not open the file with pathname " +
                    docFile.getPath() + ": " + ex.getLocalizedMessage());
                result = 2;
            }
            catch (JDOMException ex)
            {
                // TODO: localize this !!!???!!!
                out.println("Could not parse the XML document with " +
                            "pathname " + docFile.getPath() + ": " +
                            ex.getLocalizedMessage());
                result = 3;
            }
        }
        else
        {
            out.println("usage: java " + checker.getClass().getName() +
                " pathname\n\nwhere 'pathname' is the pathname of the " +
                "XML document\nthat is to be " +
                "checked\n\n");
            result = 4;
        }

        return result;
    }


    // Protected methods

    /**
        Performs any more complex checks on the specified document that
        were not already performed by the specified root element checker.
        <p>
        Note: the rootElementChecker is rarely used by this method, but
        is provided for use in those cases where it is needed.
        <p>
        This implementation does nothing.
    */
    protected void
        doComplexChecks(Document doc, ElementChecker rootElementChecker)
    {
        // Assert.require(doc != null);
        // Assert.require(rootElementChecker != null);

        // empty
    }

    /**
        Sets the XML document that this checker is to check to be
        the specified document.

        @param doc the XML document that this checker is to check
        @see AbstractXmlDocumentChecker#AbstractXmlDocumentChecker
    */
    protected void setDocument(Document doc)
    {
        // Assert.require(doc != null);

        _doc = doc;
    }

    /**
        Sets the XML document that this checker is to check to be
        the XML document contained in the specified file.

        @param docFile the file containing the XML document that this
        checker is to check
        @exception IOException if the contents of 'docFile' could not
        be read
        @exception JDOMException thrown if the contents of 'docFile'
        are not a valid XML document
        @see AbstractXmlDocumentChecker#AbstractXmlDocumentChecker
    */
    protected void setDocument(File docFile)
        throws IOException, JDOMException
    {
        // Assert.require(docFile != null);

        SAXBuilder builder = new SAXBuilder(false);  // not validated
        _doc = builder.build(new FileInputStream(docFile));
    }

    /**
        @return an ElementChecker that does no checking
    */
    protected ElementChecker createNullElementChecker()
    {
        // Assert.ensure(_nullElementChecker != null);
        return _nullElementChecker;
    }


    // Abstract methods

    /**
        @return the element checker to use to check our XML
        document's root element (as well as its attributes and all
        of its descendent elements).
    */
    protected abstract ElementChecker createRootElementChecker();


    // Inner classes

    /**
        The class of ElementChecker that does no checks.
    */
    private static class NullElementChecker
        implements ElementChecker
    {
        // Public methods

        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // empty - do no checking
        }
    }
}
