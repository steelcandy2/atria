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
import java.util.ArrayList;
import java.util.List;

/**
    A converter that converts XML documents to Atria documents.

TODO: make the output Atria code prettier !!!
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

    /**
        The text that starts the result of converting an XML processing
        instruction to Atria.
    */
    private static final String
        PROCESSING_INSTRUCTION_START = ATRIA_COMMENT_START + "?";


    /** The current platform's line separator. */
    private static final String
        LINE_SEPARATOR = Io.LINE_SEPARATOR;

    /** The double quote character ("). */
    private static final char
        DOUBLE_QUOTE_CHAR = '"';

    /* The newline character. */
    private static final char
        NEWLINE_CHAR = '\n';

    // Private fields


    // Constructors

    /**
        Constructs an XmlToAtriaConverter.
    */
    public XmlToAtriaConverter()
    {
        // empty
    }

    // Public static methods

    /**
        Returns 'txt' with all leading whitespace characters removed.

        @param txt a string
        @return txt except that it doesn't include any consecutive leading
        whitespace characters that may be at the start of it (i.e. before its
        first non-whitespace character)
    */
    public static String trimLeading(String txt)
    {
        Assert.require(txt != null);

        int sz = txt.length();
        int i = 0;
        for (; i < sz; i++)
        {
            if (Character.isWhitespace(txt.charAt(i)) == false)
            {
                break;
            }
        }

        String result = txt;
        if (i > 0)
        {
            result = txt.substring(i);
        }

        //System.err.println("===> txt=[" + txt + "], result=[" + result + "]");
        Assert.ensure(result != null);
        Assert.ensure(result.length() <= txt.length());
        Assert.ensure(result.isEmpty() ||
            Character.isWhitespace(result.charAt(0)) == false);
            // doesn't start with a whitespace character
        return result;
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
        writeLine(w);

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

        Iterator iter = mergeText(contents).iterator();
        while(iter.hasNext())
        {
            convertContent(iter.next(), w);
        }
    }

    /**
        Returns a copy of the specified list, but with consecutive sequences
        of elements that are of type Text (including those of type CDATA,
        which is a subtype of Text) replaced with a single element of type
        String (where that string is the result of concatenating the result
        of calling the 'getText()' method on each Text).

        @param contents the contents of an XML document: each item in the
        list is assumed to be a Content object
        @return a copy of 'contents' with each sequence of consecutive Text
        objects replaced with a single element of type String
    */
    protected List mergeText(List contents)
    {
        Assert.require(contents != null);

        List result = new ArrayList();
        StringBuffer buf = null;
        Iterator iter = contents.iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            if (obj instanceof Text)  // assumes a CDATA is a Text too
            {
                String part = ((Text) obj).getText();
                if (buf == null)
                {
                    buf = new StringBuffer();
                }
                buf.append(part);
            }
            else
            {
                if (buf != null)
                {
                    result.add(buf.toString());
                    buf = null;
                }
                result.add(obj);
            }
        }

        if (buf != null)
        {
            result.add(buf.toString());
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= contents.size());
        return result;
    }

    /**
        Converts the specified XML content to the corresponding part of an
        Atria document.

        @param c the XML content to convert: it should be a String or a
        (subtype of) Content
        @param w the writer to use to write the content, after it has been
        converted to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertContent(Object c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        // Note: there doesn't seem to be anything defined in JDOM - such as
        // a visitor class - to determine the exact type of content that a
        // Content object represents, so we have to resort to this sequence
        // of instanceof tests.
        if (c instanceof String)
        {
            // 'c' was built by mergeText().
            convertText(trimLeading((String) c), w);
        }
        else if (c instanceof Element)
        {
            convertElement((Element) c, w);
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
        writeLine(w);

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
        Converts the specified sequence of consecutive text in an XML
        document to the corresponding part of an Atria document.

        @param txt the sequence of consecutive text in an XML text to convert
        @param w the writer to use to write 'c', after it has been converted
        to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertText(String txt, IndentWriter w)
        throws IOException
    {
        Assert.require(txt != null);
        Assert.require(w != null);

        Iterator iter = buildTextParts(txt).iterator();
        while (iter.hasNext())
        {
            Object obj = iter.next();
            Assert.check(obj != null);
            if (obj instanceof String)
            {
                writeAtriaText((String) obj, w);
            }
            else
            {
                Assert.check(obj instanceof Character);
                char ch = ((Character) obj).charValue();
                String cmd;
                if (ch == DOUBLE_QUOTE_CHAR)
                {
                    cmd = AtriaInfo.QUOTE_COMMAND_NAME;
                }
                else
                {
                    Assert.check(ch == NEWLINE_CHAR);
                    cmd = AtriaInfo.NEWLINE_COMMAND_NAME;
                }
                writeAtriaZeroArgumentAtriaCommand(cmd, w);
                writeZeroArgumentAtriaCommand(cmd, w);
                writeLine(w);
            }
        }
    }

    /**
        Builds a list of parts from the specified sequence of consecutive
        text in an XML: a Character for each character that can't be part of
        an Atria text, and a String for each sequence of consecutive
        characters that can be.

        @param c the sequence of consecutive text in an XML text to convert
        @return a list each of whose elements is either a Character
        representing a character that can't appear in an Atria text, or a
        String representing a non-empty sequence of characters that can
    */
    protected List buildTextParts(String text)
    {
        Assert.require(text != null);

        List result = new ArrayList();
        int sz = text.length();
        if (sz > 0)
        {
            Character quote = Character.valueOf(DOUBLE_QUOTE_CHAR);
            Assert.check(quote != null);
            Character newline = Character.valueOf(NEWLINE_CHAR);
            Assert.check(newline != null);

            StringBuffer buf = null;
            for (int i = 0; i < sz; i++)
            {
                char ch = text.charAt(i);
                Character toAdd = null;
                if (AtriaInfo.isTextCharacter(ch))
                {
                    Assert.check(ch != DOUBLE_QUOTE_CHAR);
                    Assert.check(ch != NEWLINE_CHAR);
                    if (buf == null)
                    {
                        buf = new StringBuffer();
                    }
                    buf.append(ch);
                }
                else if (ch == DOUBLE_QUOTE_CHAR)
                {
                    toAdd = quote;
                }
                else
                {
                    Assert.check(ch == NEWLINE_CHAR);  // newline
                        // otherwise there's a character that we can't
                        // represent in an Atria text
                    toAdd = newline;
                }

                if (toAdd != null)
                {
                    if (buf != null)
                    {
                        Assert.check(buf.length() > 0);
                        result.add(buf.toString());
                        buf = null;
                    }
                    result.add(toAdd);
                }
            }

            if (buf != null)
            {
                Assert.check(buf.length() > 0);
                result.add(buf.toString());
            }
        }

        Assert.ensure(result != null);  // though it may be empty
        return result;
    }

    /**
        Writes to 'w' an Atria text that contains all of the characters in
        'txt' in order.

        @param txt the characters in the Atria text
        @param w the writer to write the Atria text to
        @exception IOException thrown if an error occurs in writing out the
        Atria text
    */
    protected void writeAtriaText(String txt, IndentWriter w)
        throws IOException
    {
        Assert.require(txt != null);
        Assert.require(txt.isEmpty() == false);
        Assert.require(w != null);

        w.write(ATRIA_TEXT_START);
        w.write(txt);
        writeLine(w, ATRIA_TEXT_END);
    }

    /**
        Writes to 'w' a use of the zero-argument Atria command named 'cmd'.

        @param cmd the name of the Atria command
        @param w the writer to write the Atria command use to
        @exception IOException thrown if an error occurs in writing out the
        Atria command use
    */
    protected void writeAtriaZeroArgumentAtriaCommand(String cmd,
                                                      IndentWriter w)
        throws IOException
    {
        Assert.require(cmd != null);
        Assert.require(cmd.isEmpty() == false);
        Assert.require(w != null);

        writeAtriaCommandStart(cmd, w);
        writeAtriaCommandEnd(w);
    }

    /**
        Writes to 'w' the start of a use of the Atria command named 'cmd':
        namely, the open bracket and the command name (but no space after the
        command name).

        @param cmd the name of the Atria command
        @param w the writer to write the Atria command use start to
        @exception IOException thrown if an error occurs in writing out the
        start of the Atria command use
        @see #writeAtriaCommandEnd(IndentWriter)
    */
    protected void writeAtriaCommandStart(String name, IndentWriter w)
        throws IOException
    {
        Assert.require(name != null);
        Assert.require(w != null);

        w.write("[");
        w.write(name);
    }

    /**
        Writes to 'w' the end of a use of an Atria command: namely, the close
        bracket.

        @param w the writer to write the Atria command use end to
        @exception IOException thrown if an error occurs in writing out the
        end of the Atria command use
        @see #writeAtriaCommandStart(String, IndentWriter)
    */
    protected void writeAtriaCommandEnd(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        w.write("]");
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
        writeLine(w);
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
        writeLine(w);
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
        outputAttributeValueParts(buildTextParts(value), w);
    }

    /**
        Outputs the attribute value whose parts are in 'parts' using 'w'.

        @param parts the Atria attribute's value's parts: each element is
        either a Character that represents a character in the value that
        can't appear in an Atria text, or  String that represents a non-empty
        sequence of characters that can
        @param w the writer to use to output the Atria attribute value
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute value
        @see #buildTextParts(String)
    */
    protected void outputAttributeValueParts(List parts, IndentWriter w)
        throws IOException
    {
        Assert.require(parts != null);
        Assert.require(w != null);

        int sz = parts.size();
        if (sz == 0)
        {
            outputSimpleAttributeValue("", w);
        }
        else if (sz == 1)
        {
            Object p = parts.get(0);
            if (p instanceof String)
            {
                outputSimpleAttributeValue((String) p, w);
            }
            else
            {
                outputQuoteCommandFor(p, w);
            }
        }
        else
        {
            // 'val' contains at least one character that can't appear in an
            // Atria text. We assume here it's not a newline since attribute
            // values can't contain those, so it must contain one or more
            // double quotes.
            //
            // We handle the case where the first and last part are both
            // quotes by using the Atria 'quoted' command.
            Assert.check(sz >= 2);
            if (isDoubleQuoteCharacter(parts.get(0)) &&
                isDoubleQuoteCharacter(parts.get(sz - 1)))
            {
                writeAtriaCommandStart(AtriaInfo.QUOTED_COMMAND_NAME, w);
                w.write(" ");
                outputAttributeValueParts(parts.subList(1, sz - 1), w);
                writeAtriaCommandEnd(w);
            }
            else
            {
                // Otherwise we just use the 'join' command.
                writeAtriaCommandStart(AtriaInfo.JOIN_COMMAND_NAME, w);
                w.write(" ");
                Iterator iter = parts.iterator();
                while (iter.hasNext())
                {
                    Object p = iter.next();
                    if (p instanceof String)
                    {
                        outputSimpleAttributeValue((String) p, w);
                    }
                    else
                    {
                        outputQuoteCommandFor(p, w);
                    }

                    if (iter.hasNext())
                    {
                        w.write(" ");
                    }
                }
                writeAtriaCommandEnd(w);
            }
        }
    }

    /**
        Outputs a use of the Atria 'quote' command using 'w' for 'quoteChar',
        which must be a Character representing a double quote character (").

        @param val the Atria attribute's value
        @param w the writer to use to output the Atria attribute value
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute value
    */
    protected void outputQuoteCommandFor(Object quoteChar, IndentWriter w)
        throws IOException
    {
        Assert.require(isDoubleQuoteCharacter(quoteChar));
        Assert.require(w != null);

        writeAtriaZeroArgumentAtriaCommand(AtriaInfo.QUOTE_COMMAND_NAME, w);
    }

    /**
        Outputs the attribute value 'val' using 'w', where we can assume that
        'val' doesn't contain any characters that can't appear in an Atria
        text.

        @param val the Atria attribute's value
        @param w the writer to use to output the Atria attribute value
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute value
    */
    protected void outputSimpleAttributeValue(String val, IndentWriter w)
        throws IOException
    {
        w.write(ATRIA_TEXT_START);
        w.write(val);
        w.write(ATRIA_TEXT_END);
    }

    /**
        Returns true iff 'q' is a Character object that represents the double
        quote character (").
     */
    protected boolean isDoubleQuoteCharacter(Object q)
    {
        //System.err.println("===> isDoubleQuoteCharacter(" + q + ") ...");
        return (q != null) && (q instanceof Character) &&
            (((Character) q).charValue() == DOUBLE_QUOTE_CHAR);
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
    /**
        Writes out, using the specified writer, just the newline character(s)
        that indicate the end of a line.

        @param w the writer
        @exception IOException thrown iff an I/O error occurs in writing
        the newline
    */
    protected void writeLine(Writer w)
        throws IOException
    {
        writeLine(w, "");
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
}
