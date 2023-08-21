/*
 Copyright (C) 2006 by James MacKay.

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

import com.steelcandy.common.xml.XmlUtilities;

import com.steelcandy.common.Resources;

import org.jdom.*;

import java.io.*;

/**
    A converter that converts Atria documents to XML.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaToJdomConverter
    extends AtriaAbstractInterpreter
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaProgramResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        UNKNOWN_NON_PREDEFINED_COMMAND_MSG =
            "UNKNOWN_NON_PREDEFINED_COMMAND_MSG",
        CONVERSION_TO_JDOM_FAILED_MSG =
            "CONVERSION_TO_JDOM_FAILED_MSG";


    /** The text output by the 'newline' command. */
    private static final String NEWLINE = "\n";

    /** The text output by the 'quote' command. */
    private static final String QUOTE = "\"";


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


    /** The current JDOM element, or null if there isn't one. */
    private Element _currentElement;

    /**
        The buffer we're currently building a value in, or null if we arent'
        currently building a value.
    */
    private StringBuffer _buffer;


    /**
        The Atria Document we're currently converting, or null if we aren't
        currently converting anything.
    */
    private AtriaConstructManager.Document _document;

    /**
        The error handler that we use to handle errors that occur during the
        conversion, or null if we're not currently doing a conversion.
    */
    private ErrorHandler _handler;

    /**
        The JDOM document that is the result of the current conversion, or
        null if we're not currently doing a conversion.
    */
    private Document _result;


    // Constructors

    /**
        Constructs an AtriaToJdomConverter.
    */
    public AtriaToJdomConverter()
    {
        _topCommand = null;
        _namespaceCommands = ConstructList.createArrayList();

        _currentElement = null;
        _buffer = null;

        _document = null;
        _handler = null;
        _result = null;
    }


    // Public methods

    /**
        Converts the Atria document in the specified file to a JDOM Document.
        Any error information will be output to standard error.

        @param f the pathname of the Atria document file to convert
        @return the JDOM document that the contents of 'f' were converted to
        @exception AtriaConversionException if the the conversion failed
    */
    public Document convert(File f)
        throws AtriaConversionException
    {
        Document result;

        try
        {
            _handler = createErrorHandler();
            _document = toValidatedDocument(f, _handler);
            if (_document != null && haveBeenErrors(_handler) == false)
            {
                _result = new Document();
                _document.accept(this, _handler);
            }

            if (haveBeenErrors(_handler))
            {
// TODO: include ALL error messages in the exception ???!!!!???
// - for now they're always written to standard error
// - add '_handler' to the AtriaConversionException?
                String msg = _resources.
                    getMessage(CONVERSION_TO_JDOM_FAILED_MSG);
                throw new AtriaConversionException(msg);
            }
            else
            {
                result = _result;
            }
        }
        finally
        {
            _document = null;
            _handler = null;
            _result = null;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Converts the specified Atria document to a JDOM document.

        @param doc the Atria document to convert.
        @param handler the error handler to use to handle any errors that
        occur in doing the conversion
        @return the JDOM document that 'doc' was converted into, or null if
        the conversion failed
    */
    public Document convert(AtriaConstructManager.Document doc,
                            ErrorHandler handler)
    {
        Document result;

        try
        {
            _document = doc;
            _handler = handler;

            _result = new Document();
            _document.accept(this, _handler);

            // We return null rather than the results of failed conversions.
            if (haveBeenErrors(_handler))
            {
                result = null;
            }
            else
            {
                result = _result;
            }
        }
        finally
        {
            _document = null;
            _handler = null;
            _result = null;
        }

        // 'result' may be null
        Assert.ensure(result != null || haveBeenErrors(handler));
            // "(result == null) implies haveBeenErrors(handler)"
        return result;
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

        // Process the part of the document after the prologue.
        Assert.check(_currentElement == null);
        if (_topCommand == null)
        {
            // The direct Element subconstruct of 'c' is the root element.
            Assert.check(c.elementCount() == 1);
            visitAll(c.elementList(), handler);
        }
        else
        {
            // The root element is specified by the '_topCommand'.
            buildRootElementFromTopCommand(_topCommand, handler);
            Assert.check(_currentElement != null);

            // Add 'c''s Element subconstructs under the root element.
            visitAll(c.elementList(), handler);
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

        Element parent = _currentElement;
        try
        {
            String name = MANAGER.toText(c.name());
            _currentElement = new Element(name);

            if (parent == null)
            {
                // We're the root element.
                _result.setRootElement(_currentElement);
                addNamespaceDeclarations(_currentElement, handler);
            }
            else
            {
                parent.addContent(_currentElement);
            }

            visitAll(c.attributeList(), handler);
            visitAll(c.contentItemList(), handler);
        }
        finally
        {
            _currentElement = parent;
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

        String name = MANAGER.toText(c.name());

        String value;
        Assert.check(_buffer == null);
        try
        {
            _buffer = new StringBuffer();
            c.expression().accept(this, handler);
            value = _buffer.toString();
        }
        finally
        {
            _buffer = null;
        }

        _currentElement.setAttribute(name, value);
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
        outputText(contents);
    }

    /**
        @see AtriaConstructVisitor#visitName(AtriaConstructManagerBase.Name, ErrorHandler)
    */
    public void visitName(AtriaConstructManager.Name c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since names should never get visited by this visitor
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
        outputText(value);
    }

    /**
        @see AtriaCommandConstructVisitor#visitJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // Output all of the arguments one after another.
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

        outputText(NEWLINE);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        outputText(QUOTE);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        outputText(QUOTE);
        visitAll(c.argumentList(), handler);  // should just be one
        outputText(QUOTE);
    }

    /**
        @see AtriaCommandConstructVisitor#visitSetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String value;
        Assert.check(_buffer == null);
        try
        {
            _buffer = new StringBuffer();
            c.argumentList().get(1).accept(this, handler);
            value = _buffer.toString();
        }
        finally
        {
            _buffer = null;
        }

        // Note: variables' values have not had special XML characters
        // escaped.
        setVariable(c, value, handler);
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
        Outputs the specified text: by appending it to the buffer if there
        currently is one, or adding it to the current element as text content
        otherwise.

        @param text the text to output
    */
    private void outputText(String text)
    {
        Assert.require(text != null);

// TODO: should we be outputting XmlUtilities.escape(text) here instead???!!!???
        if (_buffer != null)
        {
            _buffer.append(text);
        }
        else
        {
// TODO: can an element's content have consecutive Texts, or do we have
// to combine them into one Text???!!!???
            _currentElement.addContent(new Text(text));
        }
    }


    // Private methods

    /**
        Builds the root element of the JDOM document that is the result of
        the current conversion from the specified 'top' command.

        @param c the 'top' command
        @param handler the error handler to use to handle any errors that
        occur in building the root element
    */
    private void buildRootElementFromTopCommand(AtriaConstructManager.
                                           Command c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = c.nameArgumentAttribute().value();
        _currentElement = new Element(name);

        if (c.argumentCount() > 1)
        {
            // Arguments after the first should be Attributes.
            visitAll(c.argumentList().subList(1), _handler);
                // 'subList()' skips the element name argument
        }

        _result.setRootElement(_currentElement);
        addNamespaceDeclarations(_currentElement, handler);

        Assert.ensure(_currentElement != null);
        Assert.ensure(_result.hasRootElement());
    }

    /**
        Adds the namespace declarations specified by the '_namespaceCommands'
        to the specified element.

        @param e the element to add the namespace declarations to
        @param handler the error handler to use to handle any errors that
        occur in adding the namespace declaration
    */
    private void addNamespaceDeclarations(Element e, ErrorHandler handler)
    {
        Assert.require(e != null);
        Assert.require(handler != null);

        ConstructIterator iter = _namespaceCommands.iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Command c =
                (AtriaConstructManager.Command) iter.next();
            String prefix = c.nameArgumentAttribute().value();

            // The namespace URI is given by the namespace command's second
            // argument, which is an Expression.
            String uri;
            Assert.check(_buffer == null);
            try
            {
                _buffer = new StringBuffer();
                c.argumentList().get(1).accept(this, handler);
                uri = _buffer.toString();
            }
            finally
            {
                _buffer = null;
            }

            e.addNamespaceDeclaration(Namespace.getNamespace(prefix, uri));
        }
    }
}
