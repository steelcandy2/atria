/*
 Copyright (C) 2005-2023 by James MacKay.

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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.Io;
import com.steelcandy.common.text.StringIterator;
import com.steelcandy.common.text.TextUtilities;
import com.steelcandy.common.xml.XmlException;
import com.steelcandy.common.xml.XmlUtilities;

import org.jdom.*;

import java.io.IOException;
import java.io.Writer;

import java.util.Iterator;
import java.util.List;

/**
    A converter that converts XML documents to Atria documents.

TODO: make the output Atria code prettier !!!
TODO: merge adjacent CDATA and Text elements ???!!!???
TODO: have XML comments that are on the same line as text, etc. in XML
      also be on the same line in Atria !!!
TODO: if the last thing in an XML document is an EntityRef then there is
      no newline at the end of the document: fix this !!!!
        - is this the case for any other parts of XML documents???

    @author  James MacKay
*/
public class XmlToAtriaConverter
{
    // Constants

    /** The Atria comment start character. */
    private static final String
        ATRIA_COMMENT_START = AtriaInfo.COMMENT_START;

    /** The Atria text start character. */
    private static final String
        ATRIA_TEXT_START = AtriaInfo.QUOTE;

    /** The Atria text end character. */
    private static final String
        ATRIA_TEXT_END = AtriaInfo.QUOTE;

    /**
        The character that separates an Atria attribute's name from its
        value.
    */
    private static final String
        ATRIA_ATTRIBUTE_NAME_VALUE_SEPARATOR = "=";


    /**
        The character that separates a namespace prefix from a name in
        XML that has been converted to Atria.
    */
    private static final String ATRIA_NAMESPACE_SEPARATOR = ":";

// TODO: fix/remove this !!!
    /**
        The text that starts the result of converting an XML processing
        instruction to Atria.
    */
    private static final String
        PROCESSING_INSTRUCTION_START = ATRIA_COMMENT_START + "?";


    /** The current platform's line separator. */
    private static final String
        LINE_SEPARATOR = Io.LINE_SEPARATOR;


    // Private fields


    // Constructors

    /**
        Constructs an XmlToAtriaConverter.
    */
    public XmlToAtriaConverter()
    {
        // empty
    }


    // Public methods

    /**
        Converts the specified XML document to an Atria document, outputting
        the Atria document using the specified IndentWriter.

        @param doc the XML document to convert
        @param w the IndentWriter to use to output the Atria document that
        'doc' was converted to
        @exception IOException thrown if an I/O error occurs in outputting
        the Atria document
    */
    public void convert(Document doc, IndentWriter w)
        throws IOException
    {
        Assert.require(doc != null);
        Assert.require(w != null);

        // Output the 'language atria' line.
        w.write(AtriaInfo.FIRST_LANGUAGE_SPECIFIER_NAME);
        w.write(" ");
        writeLine(w, AtriaInfo.LANGUAGE_NAME);
        w.write(LINE_SEPARATOR);

        convertAllContents(doc.getContent(), w);
    }


    // Protected methods

    /**
        Converts the specified list of XML content items to the corresponding
        parts of an Atria document.

        @param contents the XML contents to convert: each item in the list is
        assumed to be a Content object
        @param w the writer to use to write the contents, after it has been
        converted to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertAllContents(List contents, IndentWriter w)
        throws IOException
    {
        Assert.require(contents != null);
        Assert.require(w != null);

        Iterator iter = contents.iterator();
        while(iter.hasNext())
        {
            Content c = (Content) iter.next();
// TODO: group adjacent CDATA and Text content items here ???!!!???
            convertContent(c, w);
        }
    }

    /**
        Converts the specified XML content to the corresponding part of an
        Atria document.

        @param c the XML content to convert
        @param w the writer to use to write the content, after it has been
        converted to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertContent(Content c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        // Note: there doesn't seem to be anything defined in JDOM - such as
        // a visitor class - to determine the exact type of content that a
        // Content object represents, so we have to resort to this sequence
        // of instanceof tests.
        if (c instanceof Element)
        {
            convertElement((Element) c, w);
        }
        else if (c instanceof CDATA)
        {
            // Note: since CDATA is a subclass of Text this 'else if'
            // clause has to appear before the one for Text.
            convertCdata((CDATA) c, w);
        }
        else if (c instanceof Text)
        {
            convertText((Text) c, w);
        }
        else if (c instanceof Comment)
        {
            convertComment((Comment) c, w);
        }
        else if (c instanceof EntityRef)
        {
            convertEntityReference((EntityRef) c, w);
        }
        else if (c instanceof ProcessingInstruction)
        {
            convertProcessingInstruction((ProcessingInstruction) c, w);
        }
        else if (c instanceof DocType)
        {
            convertDocumentType((DocType) c, w);
        }
        else
        {
            Assert.unreachable();
                // since there shouldn't be any other types of Content.
        }
    }

    /**
        Converts the specified XML element to the corresponding part of an
        Atria document.

        @param c the XML element to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertElement(Element c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        // Output the element's name ...
        outputNamespacePrefix(c.getNamespacePrefix(), w);
        w.write(c.getName());

        // ... attributes ...
        Iterator iter = c.getAttributes().iterator();
        while (iter.hasNext())
        {
            w.write(" ");
            convertAttribute((Attribute) iter.next(), w);
        }

        // ... namespace declarations (including its own, iff it declares
        // it) ...
        //
        // Note: getAdditionalNamespaces() won't return an element's
        // namespace, even if the namespace's declaration is on that element.
        outputElementsNamespaceDeclaration(c, w);
        iter = c.getAdditionalNamespaces().iterator();
        while (iter.hasNext())
        {
            outputNamespaceDeclaration((Namespace) iter.next(), w);
        }

        w.write(LINE_SEPARATOR);

        // ... and child elements (indented one level).
        w.incrementIndentLevel();
        try
        {
            convertAllContents(c.getContent(), w);
        }
        finally
        {
            w.decrementIndentLevel();
        }
    }

    /**
        Converts the specified XML attribute to the corresponding part of an
        Atria document.

        @param c the XML attribute to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertAttribute(Attribute c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        outputNamespacePrefix(c.getNamespacePrefix(), w);
        outputAttribute(c.getName(), c.getValue(), w);
    }

    /**
        Converts the specified XML text to the corresponding part of an
        Atria document.

        @param c the XML text to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertText(Text c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        String text = escape(getText(c));
        if (text.length() > 0)
        {
            w.write(ATRIA_TEXT_START);
            w.write(text);
            writeLine(w, ATRIA_TEXT_END);
        }
    }

    /**
        Converts the specified XML CDATA text to the corresponding part of an
        Atria document.

        @param c the XML CDATA text to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertCdata(CDATA c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        convertText(c, w);
    }

    /**
        Converts the specified XML comment to the corresponding part of an
        Atria document.

        @param c the XML comment to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertComment(Comment c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

// TODO: is there a more robust way to split the text into lines ???!!!???
        StringIterator lineIter = TextUtilities.
            split(c.getText(), LINE_SEPARATOR).iterator();
        while (lineIter.hasNext())
        {
            String line = lineIter.next();
            w.write(ATRIA_COMMENT_START);
            if (line.length() > 0 && line.startsWith(" ") == false)
            {
                w.write(" ");
            }
            writeLine(w, line);
        }
    }

    /**
        Converts the specified XML entity reference to the corresponding part
        of an Atria document.

        @param c the XML entity reference to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertEntityReference(EntityRef c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        w.write("&");
        w.write(c.getName());
        w.write(";");
    }

    /**
        Converts the specified XML document type to the corresponding part
        of an Atria document.

        @param c the XML document type to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertDocumentType(DocType c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

// TODO: fix this !!!
// - use something other than a comment to represent these
// - internalSubset can have quotation marks and newlines

        w.write(ATRIA_COMMENT_START);
        w.write("!DOCTYPE");
        outputAttribute(" element", c.getElementName(), w);

        outputNonEmptyAttribute(" publicId", c.getPublicID(), w);
        outputNonEmptyAttribute(" systemId", c.getSystemID(), w);
        outputNonEmptyAttribute(" internalSubset", c.getInternalSubset(), w);
        w.write(LINE_SEPARATOR);
    }

    /**
        Converts the specified XML processing instruction to the
        corresponding part of an Atria document.

        @param c the XML processing instruction to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertProcessingInstruction(ProcessingInstruction c,
                                                IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        w.write(PROCESSING_INSTRUCTION_START);
        w.write(c.getTarget());

        Iterator iter = c.getPseudoAttributeNames().iterator();
        while (iter.hasNext())
        {
            String name = (String) iter.next();
            String value = c.getPseudoAttributeValue(name);
            w.write(" ");
            outputAttribute(name, value, w);
        }
        w.write(LINE_SEPARATOR);
    }


    /**
        Outputs a declaration of the specified element's own namespace using
        the specified writer iff its namespace is declared on the element
        itself.

        @param c the element
    */
    protected void
        outputElementsNamespaceDeclaration(Element c, IndentWriter w)
        throws IOException
    {
        String prefix = c.getNamespacePrefix();
        if (prefix != null && prefix.length() > 0)
        {
            if (c.isRootElement() ||
                ((Element) c.getParent()).getNamespace(prefix) == null)
            {
                // Either 'c' doesn't have a parent or 'prefix' isn't in
                // scope on its parent, so 'c' must declare its own
                // namespace.
                outputNamespaceDeclaration(c.getNamespace(), w);
            }
        }
    }

    /**
        Outputs a declaration of the specified namespace using the specified
        writer, iff the namespace is non-null.
        <p>
        Note: the declaration will be preceded by a space if it is output.

        @param ns the namespace
        @param w the writer
        @exception IOException thrown if an I/O error occurs in outputting
        the namespace declaration
    */
    protected void outputNamespaceDeclaration(Namespace ns, IndentWriter w)
        throws IOException
    {
        // 'ns' may be null
        Assert.require(w != null);

        if (ns != null)
        {
            w.write(" xmlns:");
            outputAttribute(ns.getPrefix(), ns.getURI(), w);
        }
    }

    /**
        Outputs an Atria attribute with the specified name and value.

        @param name the Atria attribute's name
        @param value the Atria attribute's value
        @param w the writer to use to output the Atria attribute
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute
    */
    protected void
        outputAttribute(String name, String value, IndentWriter w)
        throws IOException
    {
        Assert.require(name != null);
        Assert.require(value != null);
        Assert.require(w != null);

        w.write(name);
        w.write(ATRIA_ATTRIBUTE_NAME_VALUE_SEPARATOR);
        w.write(ATRIA_TEXT_START);
        w.write(value);
        w.write(ATRIA_TEXT_END);
    }

    /**
        Outputs an Atria attribute with the specified name and value iff the
        value is non-null and not an empty string.

        @param name the Atria attribute's name
        @param value the Atria attribute's value
        @param w the writer to use to output the Atria attribute
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute
    */
    protected void outputNonEmptyAttribute(String name, String value,
                                           IndentWriter w)
        throws IOException
    {
        Assert.require(name != null);
        // 'value' may be null
        Assert.require(w != null);

        if (value != null && value.length() > 0)
        {
            outputAttribute(name, value, w);
        }
    }

    /**
        Outputs the specified namespace prefix (with trailing separator)
        using the specified writer iff the prefix is non-null and not an
        empty string.

        @param prefix the namespace prefix
        @param w the writer
        @exception IOException thrown iff an I/O error occurs in outputting
        the message
    */
    protected void outputNamespacePrefix(String prefix, IndentWriter w)
        throws IOException
    {
        // 'prefix' may be null
        Assert.require(w != null);

        writeNonEmpty(w, prefix, ATRIA_NAMESPACE_SEPARATOR);
    }



    /**
        See Io#writeLine(Writer, String)
    */
    protected void writeLine(Writer w, String msg)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(msg != null);

        Io.writeLine(w, msg);
    }

    /**
        Writes the specified message using the specified writer iff the
        message is non-null and not an empty string.

        @param w the writer
        @param msg the message
        @exception IOException thrown iff an I/O error occurs in writing
        the message
    */
    protected void writeNonEmpty(Writer w, String msg)
        throws IOException
    {
        Assert.require(w != null);
        // 'msg' may be null

        if (msg != null && msg.length() > 0)
        {
            w.write(msg);
        }
    }

    /**
        Writes the specified message followed by the specified additional
        message iff the first message is non-null and not an empty string.

        @param w the writer
        @param msg the message
        @param addedMsg an additional message
        @exception IOException thrown iff an I/O error occurs in writing
        the messages
    */
    protected void writeNonEmpty(Writer w, String msg, String addedMsg)
        throws IOException
    {
        Assert.require(w != null);
        // 'msg' may be null
        Assert.require(addedMsg != null);

        if (msg != null && msg.length() > 0)
        {
            w.write(msg);
            w.write(addedMsg);
        }
    }

    /**
        @param str a string
        @return 'str' with all characters that must be escaped in Atrie
        documents escaped
    */
    protected String escape(String str)
    {
        String result = str;

        int numChars = str.length();
        int i;
        for (i = 0; i < numChars; i++)
        {
            char ch = str.charAt(i);
            if (ch == '&' || ch == '"')
            {
                break;
            }
        }

        if (i < numChars)
        {
            // There is at least one character in 'str' that needs to be
            // escaped, and the first such character is at index 'i'.
            StringBuffer buf = new StringBuffer(numChars);
            buf.append(str.substring(0, i));
            for (int j = i; j < numChars; j++)
            {
                char ch = str.charAt(j);
                if (ch == '&')
                {
                    buf.append("&amp;");
                }
                else if (ch == '"')
                {
                    buf.append("&quot;");
                }
                else
                {
                    buf.append(ch);
                }
            }

            result = buf.toString();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param c an XML text part
        @return the text that 'c' represents
    */
    protected String getText(Text c)
    {
        Assert.require(c != null);

        String result = c.getTextTrim();

        Assert.ensure(result != null);
        return result;
    }
}
