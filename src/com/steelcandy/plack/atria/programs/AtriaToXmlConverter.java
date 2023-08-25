/*
 Copyright (C) 2005-2006 by James MacKay.

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

import com.steelcandy.plack.atria.tokens.*;
import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.semantic.*;

import com.steelcandy.common.io.*;
import com.steelcandy.common.xml.XmlUtilities;

import com.steelcandy.common.Resources;

import java.io.*;

/**
    A converter that converts Atria documents to XML.

    @author  James MacKay
*/
public class AtriaToXmlConverter
    extends AtriaAbstractWritingInterpreter
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaProgramResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TO_XML_HEADER_COMMENT_MSG =
            "TO_XML_HEADER_COMMENT_MSG",
        UNKNOWN_NON_PREDEFINED_COMMAND_MSG =
            "UNKNOWN_NON_PREDEFINED_COMMAND_MSG",
        CONVERSION_TO_XML_FAILED_MSG =
            "CONVERSION_TO_XML_FAILED_MSG";


    /**
        The XML declaration that will appear at the top of each XML document
        that results from converting an Atria document.
    */
    private static final String
        XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"" +
                            " standalone=\"yes\" ?>";


    // Private fields

    /**
        The 'top' command in the prologue of the document we're converting,
        or null if there isn't one.
    */
    private AtriaConstructManager.Command _topCommand;

    /**
        All of the 'namespace' commands in the prologue of the document we're
        converting. Each item is an AtriaConstructManager.Command.
    */
    private ConstructList _namespaceCommands;

    /**
        Indicates whether the Element currently being - or about to be -
        written is followed by an Element or the end tag of its parent
        Element.
    */
    private boolean _isNextAnElement;


    /**
        The Atria Document we're currently converting to XML, or null if we
        aren't currently converting anything.
    */
    private AtriaConstructManager.Document _document;

    /**
        The pathname of the Atria file that we're currently converting, or
        null if we're not currently converting one.
    */
    private File _pathname;

    /**
        The error handler that we use to handle errors that occur during the
        conversion, or null if we're not currently doing a conversion.
    */
    private ErrorHandler _handler;


    // Constructors

    /**
        Constructs an AtriaToXmlConverter that writes the result of the
        conversion using the specified writer.

        @param w the writer to use to write out the results of the conversion
    */
    public AtriaToXmlConverter(Writer w)
    {
        Assert.require(w != null);

        setBaseWriter(IndentWriter.createNonclosing(w));

        _topCommand = null;
        _namespaceCommands = ConstructList.createArrayList();
        _isNextAnElement = true;

        _document = null;
        _pathname = null;
        _handler = null;
    }

    /**
        Constructs an AtriaToXmlConverter that writes the result of the
        conversion to standard output.
    */
    public AtriaToXmlConverter()
    {
        this(IndentWriter.createForStandardOutput());
    }


    // Public methods

    /**
        Converts the Atria document in the specified file to XML and outputs
        it (to the destination specified when we were constructed). Any error
        information will be output to standard error.

        @param f the pathname of the Atria document file to convert
        @exception AtriaConversionException if the the conversion failed
    */
    public void convert(File f)
        throws AtriaConversionException
    {
        try
        {
            _pathname = f;
            _handler = createErrorHandler();
            _document = toValidatedDocument(_pathname, _handler);
            if (_document != null && haveBeenErrors(_handler) == false)
            {
                _document.accept(this, _handler);
            }

            if (haveBeenErrors(_handler))
            {
// TODO: include ALL error messages in the exception ???!!!!???
// - for now they're always written to standard error
// - add '_handler' to the AtriaConversionException?
                String msg = _resources.
                    getMessage(CONVERSION_TO_XML_FAILED_MSG);
                throw new AtriaConversionException(msg);
            }
        }
        finally
        {
            _document = null;
            _handler = null;
            _pathname = null;

            tryToCloseBaseWriter();
        }
    }


    // Visitor methods

    /**
        @see AtriaConstructVisitor#visitDocument(AtriaConstructManagerBase.Document, ErrorHandler)
    */
    public void visitDocument(AtriaConstructManager.
        Document c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // Process the prologue.
        c.prologue().accept(this, handler);

        // Write the document header.
        writeLine(XML_DECLARATION);
        writeLine(XmlUtilities.COMMENT_START);
        indent();
        try
        {
            String msg = _resources.
                getMessage(TO_XML_HEADER_COMMENT_MSG, _pathname.getPath());
            writeLine(msg);
        }
        finally
        {
            unindent();
        }
        writeLine(XmlUtilities.COMMENT_END);

        // Output the part of the document after the prologue, possibly
        // including a root element specified by a 'top' command.
        if (_topCommand == null)
        {
            // The direct Element subconstruct of 'c' is the root element.
            Assert.check(c.elementCount() == 1);
            visitAll(c.elementList(), handler);
        }
        else  // _topCommand != null
        {
            // Write the root element starting tag.
            writeRootElementFromTopCommand(_topCommand);

            // Write 'c''s Element subconstructs under the root element.
            indent();
            try
            {
                ConstructIterator iter = c.elementList().iterator();
                while (iter.hasNext())
                {
                    _isNextAnElement = true;
                    iter.next().accept(this, handler);
                }
            }
            finally
            {
                unindent();
            }

            // Write the root element ending tag.
            write(XmlUtilities.END_TAG_START);
            write(_topCommand.nameArgumentAttribute().value());
            writeLine(XmlUtilities.TAG_END);
        }
    }

    /**
        @see AtriaConstructVisitor#visitPrologue(AtriaConstructManagerBase.Prologue, ErrorHandler)
    */
    public void visitPrologue(AtriaConstructManager.
        Prologue c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitPrologueSubconstructs(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitPrologueItem(AtriaConstructManagerBase.PrologueItem, ErrorHandler)
    */
    public void visitPrologueItem(AtriaConstructManager.
        PrologueItem c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitPrologueItemSubconstructs(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitElement(AtriaConstructManagerBase.Element, ErrorHandler)
    */
    public void visitElement(AtriaConstructManager.
        Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.depthAttribute().isSet());
        Assert.require(handler != null);

        boolean isNextAnElement = _isNextAnElement;

        write(XmlUtilities.START_TAG_START);
        visitName(c.name(), handler);
        visitAll(c.attributeList(), handler);
        if (MANAGER.isRootElement(c))
        {
            writeNamespaceDeclarationAttributes();
        }

        String tagEnd = XmlUtilities.EMPTY_TAG_END;
        ConstructIterator iter = c.contentItemList().iterator();
        if (iter.hasNext())
        {
            tagEnd = XmlUtilities.TAG_END;
            if (MANAGER.isElement(iter.peek()))
            {
                writeLine(XmlUtilities.TAG_END);
            }
            else
            {
                write(XmlUtilities.TAG_END);
            }
            indent();
            try
            {
                while (iter.hasNext())
                {
                    Construct item = iter.next();
                    _isNextAnElement = iter.hasNext() == false ||
                                        MANAGER.isElement(iter.peek());
                    item.accept(this, handler);
                }
            }
            finally
            {
                unindent();
            }

            write(XmlUtilities.END_TAG_START);
            visitName(c.name(), handler);
        }

        if (isNextAnElement)
        {
            writeLine(tagEnd);
        }
        else
        {
            write(tagEnd);
        }
    }

    /**
        @see AtriaConstructVisitor#visitExpressionItem(AtriaConstructManagerBase.ExpressionItem, ErrorHandler)
    */
    public void visitExpressionItem(AtriaConstructManager.
        ExpressionItem c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitExpressionItemSubconstructs(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitAttribute(AtriaConstructManagerBase.Attribute, ErrorHandler)
    */
    public void visitAttribute(AtriaConstructManager.
        Attribute c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        write(" ");
        visitName(c.name(), handler);
        write(XmlUtilities.ATTRIBUTE_NAME_VALUE_SEPARATOR);
        write(XmlUtilities.ATTRIBUTE_VALUE_START);
        c.expression().accept(this, handler);
        write(XmlUtilities.ATTRIBUTE_VALUE_END);
    }

    /**
        @see AtriaConstructVisitor#visitText(AtriaConstructManagerBase.Text, ErrorHandler)
    */
    public void visitText(AtriaConstructManager.
        Text c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String contents = MANAGER.contents(c);
        writeText(contents);
    }

    /**
        @see AtriaConstructVisitor#visitName(AtriaConstructManagerBase.Name, ErrorHandler)
    */
    public void visitName(AtriaConstructManager.Name c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = MANAGER.toText(c);
        write(name);
    }


    /**
        @see AtriaCommandConstructVisitor#visitGetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitGetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String value = getVariable(c, handler);
        writeText(value);
    }

    /**
        @see AtriaCommandConstructVisitor#visitJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // Write all of the arguments one after another.
        visitAll(c.argumentList(), handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNamespaceCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        _namespaceCommands.add(c);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNewlineCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNewlineCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeLine("");
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeText("\"");
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeText("\"");
        visitAll(c.argumentList(), handler);  // should just be one
        writeText("\"");
    }

    /**
        @see AtriaCommandConstructVisitor#visitSetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        StringBuffer buf;
        startBuffer();
        try
        {
            c.argumentList().get(1).accept(this, handler);
        }
        finally
        {
            buf = finishBuffer();
        }

        // Note: variables' values have not had special XML characters
        // escaped.
        setVariable(c, buf.toString(), handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitTopCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.check(_topCommand == null);
            // since there should be at most one 'top' command
        _topCommand = c;
    }

    /**
        @see AtriaCommandConstructVisitor#visitNonPredefinedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNonPredefinedCommand(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // We don't define any non-predefined commands.
        String msg = _resources.
            getMessage(UNKNOWN_NON_PREDEFINED_COMMAND_MSG,
                       MANAGER.commandName(c));
        handleRuntimeError(FATAL_ERROR_LEVEL, msg, c.name(), handler);
    }


    // Protected methods

    /**
        Writes out the XML document's root element using the information
        specified in the specified 'top' command (as well as any 'namespace'
        commands).

        @param c the 'top' command
    */
    protected void
        writeRootElementFromTopCommand(AtriaConstructManager.Command c)
    {
        Assert.require(c != null);

        write(XmlUtilities.START_TAG_START);
        write(c.nameArgumentAttribute().value());
        if (c.argumentCount() > 1)
        {
            // Arguments after the first should be Attributes.
            visitAll(c.argumentList().subList(1), _handler);
                // 'subList()' skips the element name argument
        }
        if (_namespaceCommands.isEmpty() == false)
        {
            writeNamespaceDeclarationAttributes();
        }

        // Note: the contents of a root element created by a 'top' command
        // can only be Elements (since Documents can only have direct Element
        // subconstructs after their prologues), so we always want a newline
        // after the root element's start tag.
        writeLine(XmlUtilities.TAG_END);
    }

    /**
        Writes the attributes that declare the namespaces declared by all
        of the Atria 'namespace' commands in the document we're currently
        converting.
    */
    protected void writeNamespaceDeclarationAttributes()
    {
        ConstructIterator iter = _namespaceCommands.iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Command c =
                (AtriaConstructManager.Command) iter.next();
            String prefix = c.nameArgumentAttribute().value();

            write(" ");
            write(XmlUtilities.
                    applyPrefix(XmlUtilities.NAMESPACE_PREFIX, prefix));
            write(XmlUtilities.ATTRIBUTE_NAME_VALUE_SEPARATOR);
            write(XmlUtilities.ATTRIBUTE_VALUE_START);
            c.argumentList().get(1).accept(this, _handler);
                // = the URL corresponding to the prefix
            write(XmlUtilities.ATTRIBUTE_VALUE_END);
        }
    }


    /**
        Writes out the specified text followed by a newline, all using our
        writer(). Any characters in the text that are special XML characters
        are escaped first.

        @param msg the text to write
    */
    protected void writeTextLine(String msg)
    {
        Assert.require(msg != null);

        writeLine(XmlUtilities.escape(msg));
    }

    /**
        Writes out the specified text using our writer(). Any characters in
        the text that are special XML characters are escaped first.

        @param msg the text to write
    */
    protected void writeText(String msg)
    {
        Assert.require(msg != null);

        write(XmlUtilities.escape(msg));
    }


    /**
        @see AtriaAbstractWritingInterpreter#document
    */
    protected AtriaConstructManager.Document document()
    {
        // 'result' may be null
        return _document;
    }

    /**
        @see AtriaAbstractWritingInterpreter#errorHandler
    */
    protected ErrorHandler errorHandler()
    {
        // 'result' may be null
        return _handler;
    }
}
