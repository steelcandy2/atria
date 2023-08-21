/*
 Copyright (C) 2001-2009 by James MacKay.

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

package com.steelcandy.common.xml;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;
import com.steelcandy.common.containers.Containers;
import com.steelcandy.common.io.Io;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.*;

import org.xml.sax.SAXParseException;

import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
    A class containing various XML-related utility and convenience methods.

    @author James MacKay
*/
public class XmlUtilities
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        XmlResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        CREATE_JDOM_DOCUMENT_FAILED_MSG =
            "CREATE_JDOM_DOCUMENT_FAILED_MSG",

        IO_ERROR_CREATING_JDOM_DOCUMENT_MSG =
            "IO_ERROR_CREATING_JDOM_DOCUMENT_MSG",
        CREATE_JDOM_DOCUMENT_FROM_FILE_FAILED_MSG =
            "CREATE_JDOM_DOCUMENT_FROM_FILE_FAILED_MSG",
        IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_FILE_MSG =
            "IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_FILE_MSG",

        CREATE_JDOM_DOCUMENT_FROM_ZIP_FILE_FAILED_MSG =
            "CREATE_JDOM_DOCUMENT_FROM_ZIP_FILE_FAILED_MSG",
        CREATE_JDOM_DOCUMENT_FROM_CLOSED_ZIP_FILE_MSG =
            "CREATE_JDOM_DOCUMENT_FROM_CLOSED_ZIP_FILE_MSG",
        CREATE_JDOM_DOCUMENT_FROM_MISSING_ENTRY_MSG =
            "CREATE_JDOM_DOCUMENT_FROM_MISSING_ENTRY_MSG",
        IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_ZIP_FILE_MSG =
            "IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_ZIP_FILE_MSG",
        CREATE_JDOM_DOCUMENT_FROM_BAD_FORMAT_ZIP_FILE_MSG =
            "CREATE_JDOM_DOCUMENT_FROM_BAD_FORMAT_ZIP_FILE_MSG",

        MISSING_REQUIRED_ATTRIBUTE_MSG =
            "MISSING_REQUIRED_ATTRIBUTE_MSG",
        MISSING_REQUIRED_ELEMENT_MSG =
            "MISSING_REQUIRED_ELEMENT_MSG",
        NON_BOOLEAN_ATTRIBUTE_VALUE_MSG =
            "NON_BOOLEAN_ATTRIBUTE_VALUE_MSG",
        NOT_EXACTLY_ONE_CHILD_ELEMENT_MSG =
            "NOT_EXACTLY_ONE_CHILD_ELEMENT_MSG";


    /**
        A map from characters that have to be escaped in XML to their
        escaped form. The keys and values are both Strings.
    */
    private static final Map _escapesMap = createEscapesMap();


    /** XML code fragments. */
    public static final String
        COMMENT_START = "<!--",
        COMMENT_END = "-->",
        ATTRIBUTE_NAME_VALUE_SEPARATOR = "=",
        ATTRIBUTE_VALUE_START = "\"",
        ATTRIBUTE_VALUE_END = ATTRIBUTE_VALUE_START,
        START_TAG_START = "<",
        END_TAG_START = "</",
        TAG_END = ">",
        EMPTY_TAG_END = "/>",
        NAMESPACE_PREFIX = "xmlns",
        NAMESPACE_SEPARATOR = ":",
        ENTITY_START = "&",
        ENTITY_END = ";";

    /** XML-related namespace URIs. */
    public static final String
        XSL_NAMESPACE_URI = "http://www.w3.org/1999/XSL/Transform";


    // Constructors

    /**
        This constructor is private to prevent this class from being
        instantiated.
    */
    private XmlUtilities()
    {
        // empty
    }


    // Public methods

    /**
        @param prefix an XML namespace prefix
        @param name an XML element, attribute, etc. name (without a namespace
        prefix)
        @return 'name' with the namespace prefix 'prefix' applied to it
    */
    public static String applyPrefix(String prefix, String name)
    {
        Assert.require(prefix != null);
        Assert.require(name != null);

        StringBuffer buf = new StringBuffer(prefix.length() + 1 +
                                            name.length());
        buf.append(prefix).append(NAMESPACE_SEPARATOR).append(name);

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param str a string
        @return 'str' with all characters that must be escaped in XML
        documents escaped
    */
    public static String escape(String str)
    {
        Assert.require(str != null);

        int numChars = str.length();
        StringBuffer buf = new StringBuffer(numChars);
        for (int i = 0; i < numChars; i++)
        {
            String ch = String.valueOf(str.charAt(i));
            String escaped = (String) _escapesMap.get(ch);
            if (escaped != null)
            {
                buf.append(escaped);
            }
            else
            {
                buf.append(ch);
            }
        }

        String result = buf.toString();

        Assert.ensure(result != null);
        return result;
    }


    /**
        Parses the specified string as the contents of an XML element and
        returns a fake element with it as its content.

        @param str the string to parse as the content of an XML element
        @return a fake element with the result of parsing 'str' as its
        content: everything about the element except its contents should be
        ignored
        @throws XmlException if 'str' could not be parsed as the content of
        an XML element
    */
    public static Element parseAsElementContent(String str)
        throws XmlException
    {
        Assert.require(str != null);

        StringReader r = new StringReader("<fake>" + str + "</fake>");
        Document doc = createDocument(r);

        Element result = doc.getRootElement();

        Assert.ensure(result != null);
        return result;
    }


    // DOM document creation methods

    /**
        @param doc a JDOM document
        @return 'doc' as a DOM document
        @throws XmlException if the conversion failed
    */
    public static org.w3c.dom.Document toDomDocument(Document doc)
        throws XmlException
    {
        Assert.require(doc != null);

        org.w3c.dom.Document result;

        DOMOutputter outputter = new DOMOutputter();
        try
        {
            result = outputter.output(doc);
        }
        catch (JDOMException ex)
        {
            throw new XmlException(ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    // JDOM document creation methods

    /**
        @param rootName the name of the document's root element
        @return a new JDOM XML document with an empty root element named
        'rootName'
    */
    public static Document createDocument(String rootName)
    {
        Assert.require(rootName != null);

        Document result = new Document(new Element(rootName));

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a validated JDOM Document representing the XML
        document read from the specified file.

        @param in the pathname of the file to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, File)
        @see #createDocument(File)
    */
    public static Document createValidatedDocument(File in)
        throws XmlException
    {
        Assert.require(in != null);

        Document result = createDocument(new SAXBuilder(true), in);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document representing the XML document
        read from the specified file. The document is not validated.

        @param in the pathname of the file to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, File)
        @see #createValidatedDocument(File)
    */
    public static Document createDocument(File in)
        throws XmlException
    {
        Assert.require(in != null);

        Document result = createDocument(new SAXBuilder(false), in);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document using the specified document
        builder that represents the XML document read from the specified
        file.
        <p>
        TODO: a SAXBuilder is also capable of constructing JDOM Documents
        from other types of sources. As they are needed, additional
        createDocument() methods that create Documents from these different
        types of sources can be added to this class.

        @param builder the document builder to use to build the JDOM Document
        representing the XML document
        @param in the pathname of the file to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
    */
    public static Document
        createDocument(SAXBuilder builder, File in)
        throws XmlException
    {
        Assert.require(builder != null);
        Assert.require(in != null);

        Document result;

        try
        {
            result = builder.build(in);
        }
        catch (JDOMException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_FILE_FAILED_MSG,
                           in.getPath(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_FILE_MSG,
                           in.getPath(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a validated JDOM Document representing the XML
        document read from the specified entry of the specified zip file.

        @param f the zip file that contains the entry
        @param entryName the name of the zip file entry to read the XML
        document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, ZipFile, String)
        @see #createDocument(ZipFile, String)
    */
    public static Document
        createValidatedDocument(ZipFile f, String entryName)
        throws XmlException
    {
        Assert.require(f != null);
        Assert.require(entryName != null);

        Document result = createDocument(new SAXBuilder(true), f, entryName);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document representing the XML document
        read from the specified entry of the specified zip file. The
        document is not validated.

        @param f the zip file that contains the entry
        @param entryName the name of the zip file entry to read the XML
        document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, ZipFile, String)
        @see #createValidatedDocument(ZipFile, String)
    */
    public static Document createDocument(ZipFile f, String entryName)
        throws XmlException
    {
        Assert.require(f != null);
        Assert.require(entryName != null);

        Document result =
            createDocument(new SAXBuilder(false), f, entryName);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document using the specified document
        builder that represents the XML document read from the specified
        entry of the specified zip file.
        <p>
        TODO: a SAXBuilder is also capable of constructing JDOM Documents
        from other types of sources. As they are needed, additional
        createDocument() methods that create Documents from these different
        types of sources can be added to this class.

        @param builder the document builder to use to build the JDOM Document
        representing the XML document
        @param f the zip file that contains the entry
        @param entryName the name of the zip file entry to read the XML
        document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
    */
    public static Document
        createDocument(SAXBuilder builder, ZipFile f, String entryName)
        throws XmlException
    {
        Assert.require(builder != null);
        Assert.require(f != null);
        Assert.require(entryName != null);

        ZipEntry entry;
        try
        {
            entry = f.getEntry(entryName);
        }
        catch (IllegalStateException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_CLOSED_ZIP_FILE_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }

        if (entry == null)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_MISSING_ENTRY_MSG,
                           f.getName(), entryName);
            throw new XmlException(msg);
        }

        InputStream in;
        try
        {
            in = f.getInputStream(entry);
        }
        catch (ZipException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_BAD_FORMAT_ZIP_FILE_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_ZIP_FILE_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg);
        }
        catch (IllegalStateException ex)
        {
            // 'f' could have been closed since we checked above.
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_CLOSED_ZIP_FILE_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }
        Assert.check(in != null);

        Document result;

        try
        {
            result = builder.build(in);
        }
        catch (JDOMException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FROM_ZIP_FILE_FAILED_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_CREATING_JDOM_DOCUMENT_FROM_ZIP_FILE_MSG,
                           f.getName(), entryName,
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a validated JDOM Document representing the XML
        document read from the specified reader.

        @param r the reader to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, Reader)
        @see #createDocument(Reader)
    */
    public static Document createValidatedDocument(Reader r)
        throws XmlException
    {
        Assert.require(r != null);

        Document result = createDocument(new SAXBuilder(true), r);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document representing the XML document
        read from the specified reader. The document is not validated.

        @param r the reader to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, Reader)
        @see #createValidatedDocument(Reader)
    */
    public static Document createDocument(Reader r)
        throws XmlException
    {
        Assert.require(r != null);

        Document result = createDocument(new SAXBuilder(false), r);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document using the specified document
        builder that represents the XML document read from the specified
        reader.
        <p>
        TODO: a SAXBuilder is also capable of constructing JDOM Documents
        from other types of sources. As they are needed, additional
        createDocument() methods that create Documents from these different
        types of sources can be added to this class.

        @param builder the document builder to use to build the JDOM Document
        representing the XML document
        @param r the reader to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
    */
    public static Document createDocument(SAXBuilder builder, Reader r)
        throws XmlException
    {
        Assert.require(builder != null);
        Assert.require(r != null);

        Document result;

        try
        {
            result = builder.build(r);
        }
        catch (JDOMException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FAILED_MSG,
                           r.getClass().getName(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_CREATING_JDOM_DOCUMENT_MSG,
                           r.getClass().getName(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a validated JDOM Document representing the XML
        document read from the specified input stream.

        @param in the input stream to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, InputStream)
        @see #createDocument(InputStream)
    */
    public static Document createValidatedDocument(InputStream in)
        throws XmlException
    {
        Assert.require(in != null);

        Document result = createDocument(new SAXBuilder(true), in);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document representing the XML document
        read from the specified input stream. The document is not validated.

        @param in the input stream to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
        @see #createDocument(SAXBuilder, InputStream)
        @see #createValidatedDocument(InputStream)
    */
    public static Document createDocument(InputStream in)
        throws XmlException
    {
        Assert.require(in != null);

        Document result = createDocument(new SAXBuilder(false), in);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document using the specified document
        builder that represents the XML document read from the specified
        input stream.
        <p>
        TODO: a SAXBuilder is also capable of constructing JDOM Documents
        from other types of sources. As they are needed, additional
        createDocument() methods that create Documents from these different
        types of sources can be added to this class.

        @param builder the document builder to use to build the JDOM Document
        representing the XML document
        @param in the input stream to read the XML document from
        @return a JDOM Document representing the XML document
        @exception XmlException thrown if the document could not be created
    */
    public static Document
        createDocument(SAXBuilder builder, InputStream in)
        throws XmlException
    {
        Assert.require(builder != null);
        Assert.require(in != null);

        Document result;

        try
        {
            result = builder.build(in);
        }
        catch (JDOMException ex)
        {
            String msg = _resources.
                getMessage(CREATE_JDOM_DOCUMENT_FAILED_MSG,
                           in.getClass().getName(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_CREATING_JDOM_DOCUMENT_MSG,
                           in.getClass().getName(),
                           ex.getLocalizedMessage());
            throw new XmlException(msg, ex);
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a validated JDOM Document representing the XML
        document that is the resource associated with the specified class (if
        the resource name is not absolute) with the specified name. The
        document is not validated.

        @param c the class with which the XML document resource is associated
        @param resourceName the name of the XML document resource
        @return a JDOM document representing the XML document resource
        @exception XmlException thrown if the document could not be created
    */
    public static Document
        createValidatedDocument(Class c, String resourceName)
        throws XmlException
    {
        Assert.require(c != null);
        Assert.require(resourceName != null);

        Document result =
            createValidatedDocument(c.getResourceAsStream(resourceName));

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a JDOM Document representing the XML document
        that is the resource associated with the specified class (if the
        resource name is not absolute) with the specified name. The document
        is not validated.

        @param c the class with which the XML document resource is associated
        @param resourceName the name of the XML document resource
        @return a JDOM document representing the XML document resource
        @exception XmlException thrown if the document could not be created
    */
    public static Document createDocument(Class c, String resourceName)
        throws XmlException
    {
        Assert.require(c != null);
        Assert.require(resourceName != null);

        Document result =
            createDocument(c.getResourceAsStream(resourceName));

        Assert.ensure(result != null);
        return result;
    }


    // JDOM document output methods

    /**
        Outputs the specified document to the specified file, replacing any
        existing file.

        @param doc the document to output
        @param f the pathname of the file to output 'doc' to
        @throws IOException if an I/O error occurs in trying to output 'doc'
        to 'f'
    */
    public static void output(Document doc, File f)
        throws IOException
    {
        Assert.require(doc != null);
        Assert.require(f != null);

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(f);
            output(doc, out);
        }
        finally
        {
            Io.tryToClose(out);
        }
    }

    /**
        Outputs the specified document to the specified output stream.

        @param doc the document to output
        @param out the stream to output the document to
        @throws IOException if an I/O error occurs in trying to output 'doc'
        to 'out'
    */
    public static void output(Document doc, OutputStream out)
        throws IOException
    {
        Assert.require(doc != null);
        Assert.require(out != null);

        Format fmt = Format.getPrettyFormat();
        fmt.setIndent("    ");
        XMLOutputter outputter = new XMLOutputter(fmt);

        outputter.output(doc, out);
    }


    // JDOM element- and attrribute-related methods

    /**
        Creates and returns a new element with the specified name that is a
        child of the specified parent element.

        @param parent the parent of the new child element
        @param name the child element's name
        @return the new child element of 'parent'
    */
    public static Element createChildElement(Element parent, String name)
    {
        Assert.require(parent != null);
        Assert.require(name != null);

        Element result = new Element(name);

        addElement(parent, result);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Adds the specified child element to the specified parent element
        as its last child element.

        @param parent the parent element: the one to add 'child' to as
        its last child element
        @param child the child element to add to 'parent'
    */
    public static void addElement(Element parent, Element child)
    {
        Assert.require(parent != null);
        Assert.require(child != null);

        parent.addContent(child);
    }

    /**
        Adds an attribute with the specified name and value to the specified
        element.

        @param e the element to add the attribute to
        @param name the attribute's name
        @param value the attribute's value
    */
    public static void addAttribute(Element e, String name, String value)
    {
        Assert.require(e != null);
        Assert.require(name != null);
        Assert.require(value != null);

        e.setAttribute(name, value);
    }

    /**
        Adds an attribute with the specified name and value to the specified
        element.

        @param e the element to add the attribute to
        @param name the attribute's name
        @param value the attribute's (boolean) value
    */
    public static void addAttribute(Element e, String name, boolean value)
    {
        Assert.require(e != null);
        Assert.require(name != null);

        addAttribute(e, name, String.valueOf(value));
    }


    /**
        Returns the element with the specified name that is a child of the
        specified parent element, or throws an exception if such an element
        cannot be found.
        <p>
        This method never returns null.

        @param parent the parent element
        @param elementName the name of the child element of 'parent' that
        we're to return
        @return the child element of 'parent' named 'elementName'
        @exception XmlException thrown if there is no child element of
        'parent' named 'elementName'
    */
    public static Element
        requiredChild(Element parent, String elementName)
        throws XmlException
    {
        Assert.require(parent != null);

        Element result = parent.getChild(elementName);
        if (result == null)
        {
            String msg = _resources.
                getMessage(MISSING_REQUIRED_ELEMENT_MSG,
                           elementName, parent.toString());
            throw new XmlException(msg);
        }
        return result;
    }

    /**
        Returns the element with the specified name that is a child of the
        specified parent element, or throws an exception if such an element
        cannot be found or if there is more than one child with the specified
        name.
        <p>
        This method never returns null.

        @param parent the parent element
        @param elementName the name of the child element of 'parent' that
        we're to return
        @return the child element of 'parent' named 'elementName'
        @exception XmlException thrown if there is no child element of
        'parent' named 'elementName', or more than one child element of
        'parent' named 'elementName'
    */
    public static Element
        requiredOnlyChild(Element parent, String elementName)
        throws XmlException
    {
        Assert.require(parent != null);

        Element result;

        List children = parent.getChildren(elementName);
        int numChildren = children.size();
        if (numChildren == 1)
        {
            result = (Element) children.get(0);
        }
        else
        {
            String msg = _resources.
                getMessage(NOT_EXACTLY_ONE_CHILD_ELEMENT_MSG,
                           String.valueOf(numChildren),
                           elementName, parent.toString());
            throw new XmlException(msg);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the attribute of the specified element that has
        the specified name, or throws an exception if such an
        attribute cannot be found.
        <p>
        This method never returns null.

        @param elem the element whose attribute is to be returned
        @param attributeName the name of the attribute of 'elem'
        to return
        @return the attribute of 'elem' named 'attributeName'
        @exception XmlException thrown if 'elem' doesn't have an
        attribute named 'attributeName'
    */
    public static Attribute
        requiredAttribute(Element elem, String attributeName)
        throws XmlException
    {
        Assert.require(elem != null);

        Attribute result = elem.getAttribute(attributeName);
        if (result == null)
        {
            String msg = _resources.
                getMessage(MISSING_REQUIRED_ATTRIBUTE_MSG,
                           attributeName, elem.toString());
            throw new XmlException(msg);
        }
        return result;
    }

    /**
        @param e an element
        @param attributeName the name of an attribute
        @return the value of the attribute named 'attributeName' on the
        element 'e'
        @throws XmlException if 'e' doesn't have an attribute named
        'attributeName'
    */
    public static String
        requiredAttributeValue(Element e, String attributeName)
        throws XmlException
    {
        Assert.require(e != null);
        Assert.require(attributeName != null);

        String result = requiredAttribute(e, attributeName).getValue();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the value of the specified attribute, or the specified
        default value if the attribute is null.

        @param attr the attribute whose boolean value is to be
        returned
        @param defaultValue the boolean value to be returned if 'attr'
        is null
        @return the boolean value of the attribute
        @exception XmlException thrown if the attribute's value does
        not represent a boolean value
    */
    public static boolean
        booleanValue(Attribute attr, boolean defaultValue)
        throws XmlException
    {
        boolean result = defaultValue;

        if (attr != null)
        {
            try
            {
                result = attr.getBooleanValue();
            }
            catch (DataConversionException ex)
            {
                String parentName = "[has no parent]";
                if (attr.getParent() != null)
                {
                    parentName = attr.getParent().getName();
                }

                String msg = _resources.
                    getMessage(NON_BOOLEAN_ATTRIBUTE_VALUE_MSG,
                               attr.getName(), attr.getValue(),
                               parentName);
                throw new XmlException(msg, ex);
            }
        }

        return result;
    }


    // SAX-specific methods

    /**
        Starting with the direct cause of the specified exception (if any),
        then that cause's direct cause (if any), and so on, returns the first
        cause that is a SAXParseException.
        <p>
        Note: the line number and column number of the location of an error
        in an XML document can be obtained from a SAXParseException by calling
        its getLineNumber() and getColumnNumber() methods, respectively.
        <p>
        Note: this method does <em>not</em> check whether the specified
        exception itself is a SAXParseException, only its direct and
        indirect causes.

        @param ex the exception from which to extract a SAXParseException
        cause
        @return the first SAXParseException that is a direct or indirect
        cause of 'ex', or null if none of the direct or indirect causes of
        'ex' is a SAXParseException
        @see Throwable#getCause
        @see SAXParseException
        @see SAXParseException#getLineNumber
        @see SAXParseException#getColumnNumber
    */
    public static SAXParseException
        extractSaxParseExceptionCause(Throwable ex)
    {
        SAXParseException result = null;

        Throwable cause = ex.getCause();
        if (cause != null)
        {
            if (cause instanceof SAXParseException)
            {
                result = (SAXParseException) cause;
            }
            else
            {
                result = extractSaxParseExceptionCause(cause);
            }
        }

        // 'result' may be null
        return result;
    }


    // Private static methods

    /**
        @return a map from characters that must be escaped in XML documents
        to their escaped forms: the keys and values are all Strings
    */
    private static Map createEscapesMap()
    {
        Map result = Containers.createHashMap(4);

        result.put("&", "&amp;");
        result.put("<", "&lt;");
        result.put(">", "&gt;");

        // Apparently (according to http://www.sitemaps.org/faq.html) both
        // single and double quotes always have to be escaped in XML.
        result.put("\"", "&quot;");
        result.put("'", "&apos;");

        Assert.ensure(result != null);
        return result;
    }
}
