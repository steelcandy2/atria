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

    Note: while namespace declarations on a document's root element other
    than one for a default namespace will be converted into Atria top-level
    namespace commands, all other namespace declarations will be converted
    into Atria attributes (with the name 'xmlns' for default namespace
    declarations and with the namespace prefix 'xmlns' for all others).

TODO: have XML comments that are on the same line as text, etc. in XML
      also be on the same line in Atria !!!

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

    /**
        The level of the content we're currently processing: -1 if we're not
        currently processing any content, 0 if it's part of the document's
        content (and not that of any of the document's elements), 1 if it's
        part of the root element's content (and not that of any of the root
        element's descendent elements), etc.
    */
    private int _contentLevel;

    /**
        Indicates whether the part of an XML document we're currently
        converted is immediately preceded by one or more whitespace
        characters (where whitespace includes newlines).
    */
    private boolean _isPrecededByWhitespace;


    // Constructors

    /**
        Constructs an XmlToAtriaConverter.
    */
    public XmlToAtriaConverter()
    {
        _contentLevel = -1;
        _isPrecededByWhitespace = false;
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

        // Output 'namespace' commands for the namespace declarations on
        // 'doc''s root element (only).
        outputNamespaceCommands(doc.getRootElement(), w);

        _contentLevel = -1;
        _isPrecededByWhitespace = false;
        convertAllContents(doc.getContent(), w);

        // Try to ensure that the document ends with a newline: this won't
        // work if it ends with a space, but it does handle the case where
        // the document ends with an entity reference: see
        // convertEntityReference().
        if (_isPrecededByWhitespace == false)
        {
            writeLine(w);
        }

        Assert.ensure(_contentLevel == -1);
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

        _contentLevel += 1;
        Iterator iter = mergeText(contents).iterator();
        String prefixText = "";
        String rawPrefixText = "";
        boolean wasPrevAnElement = false;
        boolean isFirst = true;
        while (iter.hasNext())
        {
            Object obj = iter.next();
            Assert.check(obj != null);
            if (obj instanceof String)
            {
                Assert.check(prefixText.isEmpty());
                    // since there shouldn't be two Strings in a row (because
                    // we merged consecutive text together)
                rawPrefixText = prefixText = (String) obj;
                if (wasPrevAnElement &&
                    TextUtilities.isAllWhitespace(prefixText))
                {
                    prefixText = "";
                }
            }
            else
            {
                Content c = (Content) obj;
                if (c instanceof Element)
                {
                    boolean isNotTopLevel = (_contentLevel > 0);
                    if (isNotTopLevel && (isFirst ||
                        (wasPrevAnElement && rawPrefixText.isEmpty())))
                    {
                        // There's no whitespace between the opening tag of
                        // 'c' and either the opening tag of its parent
                        // element or of the closing tag of sibling element
                        // immediately preceding it. Write out a
                        // zero-argument 'join' command to indicate the
                        // absence of whitespace here.
                        writeZeroArgumentJoinCommandLine(w);
                        _isPrecededByWhitespace = false;
                    }
                    convertContent(prefixText, c, w);
                    if (isNotTopLevel && iter.hasNext() == false)
                    {
                        // There's no Text content after this Element, so
                        // there mustn't be any whitespace between us an our
                        // parent element's closing tag. Write out a
                        // zero-argument 'join' command to indicate the
                        // absence of whitespace here (but not at the end of
                        // a document).
                        writeZeroArgumentJoinCommandLine(w);
                        _isPrecededByWhitespace = false;
                    }
                    wasPrevAnElement = true;
                }
                else
                {
                    convertContent(prefixText, c, w);
                    wasPrevAnElement = false;
                }
                rawPrefixText = prefixText = "";
            }
            isFirst = false;
        }

        String txt =
            normalizeWhitespace(prefixText, _isPrecededByWhitespace);
        convertText(txt, w);
        _isPrecededByWhitespace = doesEndWithWhitespace(txt);
        _contentLevel -= 1;

        //Assert.ensure("_contentLevel == old _contentLevel");
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
        Converts the specified XML content - as well as the specified text
        that precedes it - to the corresponding part of an Atria document.

        @param txt the text that immediately precedes 'c'
        @param c the XML content to convert: it should be a String or a
        (subtype of) Content
        @param w the writer to use to write the content, after it has been
        converted to Atria
        @exception IOException thrown if an I/O error occurs in outputting
        the converted XML
    */
    protected void convertContent(String txt, Content c, IndentWriter w)
        throws IOException
    {
        Assert.require(txt != null);  // though it may be empty
        Assert.require(c != null);
        Assert.require(w != null);

        // Note: there doesn't seem to be anything defined in JDOM - such as
        // a visitor class - to determine the exact type of content that a
        // Content object represents, so we have to resort to this sequence
        // of instanceof tests.
        if (c instanceof Element)
        {
            txt = normalizeWhitespace(txt, _isPrecededByWhitespace);
            convertText(txt, w);
            _isPrecededByWhitespace = doesEndWithWhitespace(txt);
            convertElement((Element) c, w);
        }
        else if (c instanceof Comment)
        {
            txt = normalizeWhitespace(txt, _isPrecededByWhitespace);
            convertText(txt, w);
            _isPrecededByWhitespace = doesEndWithWhitespace(txt);
            convertComment((Comment) c, w);
        }
        else
        {
            convertText(txt, w);
            if (c instanceof EntityRef)
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
                    // since there shouldn't be any other types of Content
            }
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
        if (c.isRootElement() == false)
        {
            // We output non-default namespace declarations on elements other
            // than the root element as attributes. (Ones on the root element
            // are converted into Atria namespace commands instead.)
            iter = c.getAdditionalNamespaces().iterator();
            while (iter.hasNext())
            {
                outputNamespaceDeclaration((Namespace) iter.next(), w);
            }
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

        @param txt the sequence of consecutive text in an XML text to convert
        @return a list each of whose elements is either a Character
        representing a character that can't appear in an Atria text, or a
        String representing a non-empty sequence of characters that can
    */
    protected List buildTextParts(String txt)
    {
        Assert.require(txt != null);

        List result = new ArrayList();
        int sz = txt.length();
        if (sz > 0)
        {
            Character quote = Character.valueOf(DOUBLE_QUOTE_CHAR);
            Assert.check(quote != null);
            Character newline = Character.valueOf(NEWLINE_CHAR);
            Assert.check(newline != null);

            StringBuffer buf = null;
            for (int i = 0; i < sz; i++)
            {
                char ch = txt.charAt(i);
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
        Writes out using the specified writer an Atria 'join' command with
        no arguments, as well as a newline at the end of it.

        @param w the writer to use to write out the command
        @exception IOException thrown if an I/O error occurs in writing out
        the command
    */
    protected void writeZeroArgumentJoinCommandLine(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        writeZeroArgumentAtriaCommand(AtriaInfo.JOIN_COMMAND_NAME, w);
        writeLine(w);
    }

    /**
        Writes to 'w' a use of the zero-argument Atria command named 'cmd'.

        @param cmd the name of the Atria command
        @param w the writer to write the Atria command use to
        @exception IOException thrown if an error occurs in writing out the
        Atria command use
    */
    protected void writeZeroArgumentAtriaCommand(String cmd,
                                                      IndentWriter w)
        throws IOException
    {
        Assert.require(cmd != null);
        Assert.require(cmd.isEmpty() == false);
        Assert.require(w != null);

        writeAtriaCommandStart(cmd, w);
        writeAtriaCommandEnd(w);
        _isPrecededByWhitespace = false;
    }

    /**
        Writes to 'w' the start of a use of the Atria command named 'name':
        namely, the open bracket and the command name (but no space after the
        command name).

        @param name the name of the Atria command
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
        _isPrecededByWhitespace = false;
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
            if ((line.isEmpty() || line.startsWith(" ")) == false)
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
        _isPrecededByWhitespace = false;
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
// - internalSubset can have quotation marks, newlines and angle brackets

        w.write(ATRIA_COMMENT_START);
        w.write("!DOCTYPE");
        outputAttribute(" element", c.getElementName(), w);

        outputNonEmptyAttribute(" publicId", c.getPublicID(), w);
        outputNonEmptyAttribute(" systemId", c.getSystemID(), w);

        // We don't use outputNonEmptyAttribute() here since 'val' can
        // contain characters that aren't valid in Atria attribute values,
        // including newlines (which can cause its value to 'escape' the end
        // of the Atria comment line that we output).
        //String val = c.getInternalSubset();
        //outputNonEmptyAttribute(" internalSubset", val, w);

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
        Indicates whether 'prefix' is an empty/default namespace prefix.

        Note: I'm not sure whether namespace prefixes as returned by methods
        like Namespace.getPrefix() and Element.getNamespacePrefix() can be
        null or not, so we err on the side of caution here and allow for the
        prefix to be null (in which case it's treated as being empty).

        @param prefix a namespace prefix
        @return true iff 'prefix' is an empty namespace prefix
    */
    protected boolean isEmptyNamespacePrefix(String prefix)
    {
        return (prefix == null) || prefix.isEmpty();
    }

    /**
        Outputs a declaration of the specified element's own namespace using
        the specified writer iff its namespace must be declared on the
        element itself.

        @param c the element
        @param w the writer to use to write out the declaration
        @exception IOException thrown if an I/O error occurs in outputting
        the namespace declaration
    */
    protected void
        outputElementsNamespaceDeclaration(Element c, IndentWriter w)
        throws IOException
    {
        //System.err.println("===> outputElementsNamespaceDeclaration(...) ...");
        Namespace ns = c.getNamespace();
        String prefix;
        if (ns != null && c.isRootElement())
        {
            // We've already output a namespace command for the root
            // element's namespace unless it's the default namespace.
            prefix = ns.getPrefix();
            if (isEmptyNamespacePrefix(prefix) == false)
            {
                ns = null;
            }
        }

        if (ns != null)
        {
            prefix = ns.getPrefix();
            Namespace inheritedNs =
                findInheritedNamespaceForPrefix(prefix, c);
            if (inheritedNs == null || (ns.equals(inheritedNs) == false))
            {
                // Either we didn't inherit a namespace for 'prefix' from one
                // of our ancestors, or we did but it's different from the
                // one that corresponds to 'prefix' for us (so we need to
                // (re)define the prefix here).
                outputNamespaceDeclaration(ns, w);
            }
        }
        // Otherwise 'c' doesn't have a namespace.
    }

    /**
        Returns the namespace corresponding to the specified prefix that is
        defined on a (proper) ancestor of 'c', or returns null if there's no
        such namespace.

        @param prefix a namespace prefix
        @param c an element
        @return the namespace defined on a proper ancestor of 'c'
        corresponding to the namespace prefix 'prefix', or null if there's no
        such namespace
    */
    protected Namespace
        findInheritedNamespaceForPrefix(String prefix, Element c)
    {
        Assert.require(prefix != null);  // but can be empty
        Assert.require(c != null);

        Namespace result = null;
        if (c.isRootElement() == false)
        {
            result = ((Element) c.getParent()).getNamespace(prefix);
                // may be null
        }

        // 'result' may be null
        return result;
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
        //System.err.println("===> outputNamespaceDeclaration(" + ns + ", w) ...");
        // 'ns' may be null
        Assert.require(w != null);

        if (ns != null)
        {
            String uri = ns.getURI();
            if (uri.isEmpty() == false)
            {
                String nsPrefix = XmlUtilities.NAMESPACE_PREFIX;
                String prefix = ns.getPrefix();
                w.write(" ");
                if (prefix.isEmpty())
                {
                    // xmlns="https://..."
                    outputAttribute(nsPrefix, ns.getURI(), w);
                }
                else
                {
                    // xmlns:prefix="https://..."
                    w.write(nsPrefix);
                    w.write(XmlUtilities.NAMESPACE_SEPARATOR);
                    outputAttribute(prefix, uri, w);
                }
            }
        }
    }

    /**
        Outputs Atria namespace commands for all of the non-default namespace
        declarations on the element 'c'.

        @param c an XML element
        @param w the writer to use to output the namespace commands
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria namespace commands
    */
    protected void outputNamespaceCommands(Element c, IndentWriter w)
        throws IOException
    {
        Assert.require(c != null);
        Assert.require(w != null);

        List namespaces;
        Namespace ns = c.getNamespace();
        if (ns != null)
        {
            namespaces = new ArrayList();
            namespaces.add(ns);
            namespaces.addAll(c.getAdditionalNamespaces());
        }
        else
        {
            namespaces = c.getAdditionalNamespaces();
        }

        boolean wasOutput = false;
        Iterator iter = namespaces.iterator();
        while (iter.hasNext())
        {
            ns = (Namespace) iter.next();
            String prefix = ns.getPrefix();
            if (isEmptyNamespacePrefix(prefix) == false)
            {
                // It's a non-default namespace declaration: that is, it has
                // a (non-empty) prefix.
                writeAtriaNamespaceCommand(prefix, ns.getURI(), w);
                wasOutput = true;
            }
            // Otherwise it's a default namespace declaration (which we
            // currently just pass through as an element attribute, since
            // Atria doesn't support/have the concept of default namespaces).
        }

        if (wasOutput)
        {
            writeLine(w);
        }
    }

    /**
        Writes out an Atria namespace command, including the newline at the
        end of it, where the

        @param w the writer to use to output the Atria namespace command
        @exception IOException thrown if an I/O error occurs in trying
        to output the command
    */
    protected void writeAtriaNamespaceCommand(String prefix, String uri,
                                              IndentWriter w)
        throws IOException
    {
        Assert.require(isEmptyNamespacePrefix(prefix) == false);
        Assert.require(uri != null);
        Assert.require(uri.isEmpty() == false);
        Assert.require(w != null);

        writeAtriaCommandStart(AtriaInfo.NAMESPACE_COMMAND_NAME, w);
        w.write(" ");
        w.write(prefix);
        w.write(" ");
        outputAttributeValueParts(buildTextParts(uri), w);
        writeAtriaCommandEnd(w);
        writeLine(w);
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
            outputOneAttributeValuePart(parts.get(0), w);
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
                    outputOneAttributeValuePart(iter.next(), w);
                    if (iter.hasNext())
                    {
                        w.write(" ");
                    }
                }
                writeAtriaCommandEnd(w);
            }
        }
        _isPrecededByWhitespace = false;
    }

    /**
        Outputs using 'w' the one part 'part' of an attribute's value.

        @param part a part of an attribute's value
        @param w the writer to use to output the part of an attribute value
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute value part
    */
    protected void outputOneAttributeValuePart(Object part, IndentWriter w)
        throws IOException
    {
        Assert.require(part != null);
        Assert.require(w != null);

        if (part instanceof String)
        {
            outputSimpleAttributeValue((String) part, w);
        }
        else
        {
            // The only character that can be in a valid XML attribute value
            // that can't appear unescaped in an Atria attribute value should
            // be a double quote character ("). So if we violate
            // outputQuoteCommandFor()'s isDoubleQuoteCharacter()
            // precondition here then we've found a counterexample for that
            // assertion, and it needs to be modified.
            outputQuoteCommandFor(part, w);
        }
    }

    /**
        Outputs a use of the Atria 'quote' command using 'w' for 'quoteChar',
        which must be a Character representing a double quote character (").

        @param quoteChar the Atria attribute's value
        @param w the writer to use to output the Atria attribute value
        @exception IOException thrown if an I/O error occurs in trying
        to output the Atria attribute value
    */
    protected void outputQuoteCommandFor(Object quoteChar, IndentWriter w)
        throws IOException
    {
        //System.err.println("===> quoteChar = [" + quoteChar + "], class = " + quoteChar.getClass().getName());
        Assert.require(isDoubleQuoteCharacter(quoteChar));
        Assert.require(w != null);

        writeZeroArgumentAtriaCommand(AtriaInfo.QUOTE_COMMAND_NAME, w);
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
        _isPrecededByWhitespace = false;
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
        //System.err.println("===> outputNonEmptyAttribute(" + name + ", " + value + ", w) ...");
        Assert.require(name != null);
        // 'value' may be null
        Assert.require(w != null);

        if (value != null && value.isEmpty() == false)
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
        _isPrecededByWhitespace = false;
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
        _isPrecededByWhitespace = true;  // the newline
    }

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

        if (msg != null && msg.isEmpty() == false)
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

        if (msg != null && msg.isEmpty() == false)
        {
            w.write(msg);
            w.write(addedMsg);
        }
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
        @see TextUtilities.doesEndWithWhitespace(String)
    */
    protected boolean doesEndWithWhitespace(String txt)
    {
        Assert.require(txt != null);

        return TextUtilities.doesEndWithWhitespace(txt);
    }


    // Private methods

    /**
        Returns 'txt' with all of the whitespace in it normalized: that is,
        with all sequences of one or more consecutive whitespace characters
        replaced with a single space. (But note that any and all leading
        whitespace will be omitted from our result iff 'willFollowWhitespace'
        is true.)

        @param txt a string
        @param willFollowWhitespace is true iff our result will, when it's
        written out to an XML document, be immediately preceded by one or
        more whitespace characters
        @return the result of normalizing all of the whitespace in 'txt'
    */
    private String normalizeWhitespace(String txt,
                                       boolean willFollowWhitespace)
    {
        Assert.require(txt != null);

        StringBuffer res = new StringBuffer();
        int startIndex = 0;
        int sz = txt.length();
        if (sz > 0 && Character.isWhitespace(txt.charAt(0)))
        {
            if (willFollowWhitespace == false)
            {
                res.append(" ");
            }
            startIndex = TextUtilities.countWhitespaceFrom(0, txt);
        }

        while (startIndex < sz)
        {
            char ch = txt.charAt(startIndex);
            if (Character.isWhitespace(ch))
            {
                // We're at the start of non-leading whitespace.
                res.append(" ");
                startIndex += TextUtilities.
                                  countWhitespaceFrom(startIndex, txt);
            }
            else
            {
                res.append(ch);
                startIndex += 1;
            }
        }
        String result = res.toString();

        Assert.ensure(result != null);
        Assert.ensure(result.length() <= txt.length());
        return result;
    }
}
