/*
 Copyright (C) 2014-2016 by James MacKay.

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

import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.semantic.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;
import com.steelcandy.common.text.*;

import java.io.*;
import java.util.*;

/**
    An abstract base class that builds/generates a file - usually a source
    code file in an executable language - from specially formatted Atria
    documents that represent a message catalog.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public abstract class AtriaAbstractMessageCatalogBuilder
    extends AtriaAbstractWritingInterpreter
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaMessageCatalogBuilderResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        MESSAGE_CATALOG_BUILDER_USAGE_MSG =
            "MESSAGE_CATALOG_BUILDER_USAGE_MSG";


    /** The message catalog namespace URI. */
    public static final String
        MESSAGES_NAMESPACE_URI =
            "http://www.steelcandy.com/ns/atria/messages/0.1";


    // Private fields

    /**
        The data about the message catalog we're building.
    */
    private AtriaMessageCatalogData _data;

    /**
        The Atria Document we're currently building a message catalog from,
        or null if we currently building one.
    */
    private AtriaConstructManager.Document _document;

    /**
        The names of all of the variables whose values are set by 'set'
        commands in the document we're processing, in the order that the
        'set' commands occur in the document.
    */
    private StringList _variableNames;

    /**
        A map from the names of variables defined by 'set' commands to the
        expressions that evaluate to their values. The keys are Strings and
        the values are Expression constructs.
    */
    private Map _variableNamesToExpressions;

    /**
        The pathname of the Atria message catalog file that we're currently
        generating source code from, or null if we're not currently
        generating code from one.
    */
    private File _inputPathname;

    /**
        The pathname of the source code file that we're currently generating,
        or null if we're not currently generating one.
    */
    private File _outputPathname;

    /**
        The error handler that we use to handle errors that occur in building
        our message catalog, or null if we're not currently building one.
    */
    private ErrorHandler _handler;


    // Constructors

    /**
        Constructs an AtriaAbstractMessageCatalogBuilder.
    */
    public AtriaAbstractMessageCatalogBuilder()
    {
        _data = null;

        _document = null;

        clearCatalogVariables();

        _inputPathname = null;
        _outputPathname = null;
        _handler = null;
    }


    // Public methods

    /**
        Performs all of the actions for a subclass' static main() method
        (except calling System.exit()).
        <p>
        Subclasses' main method's body is usually
        <code>
            System.exit((new ???MessageCatalogBuilder()).doMain(args));
        </code>
        where '???MessageCatalogBuilder' is the subclass' name.

        @param args the main() method's arguments
        @return the main method's exit code
    */
    public int doMain(String[] args)
    {
        int result = 0;

        int numCatalogs = args.length;
        if (args.length > 0)
        {
            for (int i = 0; i < numCatalogs; i++)
            {
                if (build(new File(args[i])) == false)
                {
                    // Building the message catalog failed.
                    result += 1;
                }
            }
        }
        else
        {
            String msg = _resources.
                getMessage(MESSAGE_CATALOG_BUILDER_USAGE_MSG,
                           getClass().getName());
            System.err.println(msg);
        }

        return result;
    }

    /**
        Generates the output file from the message catalog defined by the
        Atria source code in the specified file.

        @param f the file containing the Atria source code that describes the
        message catalog
        @return true iff the output file was successfully built/generated
    */
    public boolean build(File f)
    {
        Assert.require(f != null);
        Assert.require(Io.hasExtension(f));

        boolean result = false;

        try
        {
            clearCatalogVariables();
            _data = new AtriaMessageCatalogData();
            _inputPathname = f;
            _outputPathname = createOutputPathname(f);
            _handler = createErrorHandler();
            _document = toValidatedDocument(_inputPathname, _handler);
            if (_document != null && haveBeenErrors(_handler) == false)
            {
                setBaseWriter(createBaseWriter(_outputPathname));
                _document.accept(this, _handler);
                if (haveBeenErrors(_handler) == false)
                {
                    result = true;
                }
            }
        }
        catch (FatalErrorException ex)
        {
            // empty - the error has already been handled
        }
        finally
        {
            _document = null;
            _handler = null;
            _outputPathname = null;
            _inputPathname = null;
            _data = null;

            tryToCloseBaseWriter();
        }

        return result;
    }


    // Visitor methods

    /**
        @see AtriaConstructVisitor#visitDocument(AtriaConstructManagerBase.Document, ErrorHandler)
    */
    public void visitDocument(AtriaConstructManager.Document c,
                              ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        c.prologue().accept(this, handler);
        writeOutputFile(c, _data, handler);
    }

    /**
        @see AtriaConstructVisitor#visitPrologue(AtriaConstructManagerBase.Prologue, ErrorHandler)
    */
    public void visitPrologue(AtriaConstructManager.Prologue c,
                              ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitAll(c.prologueItemList(), handler);
    }

    /**
        @see AtriaConstructVisitor#visitPrologueItem(AtriaConstructManagerBase.PrologueItem, ErrorHandler)
    */
    public void visitPrologueItem(AtriaConstructManager.PrologueItem c,
                                  ErrorHandler handler)
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
        Assert.require(handler != null);

        int depth = c.depthAttribute().value();
        Assert.check(depth >= 0);

        if (depth == 0)  // root element
        {
            visitAll(c.contentItemList(), handler);
        }
        else if (depth == 1)  // message element
        {
            writeMessage(c, handler);
        }
        else  // 'arg' or 'quoted-arg' element
        {
            Assert.check(depth == 2);
            writeMessageArgument(c, handler);
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
        @see AtriaConstructVisitor#visitText(AtriaConstructManagerBase.Text, ErrorHandler)
    */
    public void visitText(AtriaConstructManager.
        Text c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeLiteralText(MANAGER.contents(c), handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNamespaceCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since they're not allowed in message catalogs, as checked
            // by our semantic analyzer
    }

    /**
        @see AtriaCommandConstructVisitor#visitSetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = c.nameArgumentAttribute().value();
        Construct expr = c.argumentList().get(1);

        _variableNames.add(name);
        Object old = _variableNamesToExpressions.put(name, expr);
        Assert.check(old == null);
    }

    /**
        @see AtriaCommandConstructVisitor#visitTopCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // we ignore these commands
    }


    /**
        @see AtriaCommandConstructVisitor#visitGetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitGetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeGetCommand(c.nameArgumentAttribute().value(), handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeJoinCommand(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNewlineCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNewlineCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeNewlineCommand(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeQuoteCommand(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        writeQuotedCommand(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNonPredefinedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNonPredefinedCommand(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since non-predefined commands are not allowed in message
            // catalogs, and so this should have been detected by our
            // semantic analyzer.
    }


    // Protected methods

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

    /**
        @see AtriaAbstractInterpreter#addInitialVariables
    */
    protected void addInitialVariables()
    {
        super.addInitialVariables();

// TODO: FIX THIS !!!
// - namespace 'ns' is not defined, so this doesn't work
// - should we even be including the 'ns' namespace directly in the
//   variable name?
//        setInitialVariable("ns:messages", MESSAGES_NAMESPACE_URI);
    }

    /**
        @see AtriaAbstractInterpreter#createSemanticAnalyzer
    */
    protected AtriaSemanticAnalyzer createSemanticAnalyzer()
    {
        AtriaSemanticAnalyzer result =
            new AtriaMessageCatalogSemanticAnalyzer(_data);

        Assert.ensure(result != null);
        return result;
    }


    /**
        Clears our set of variables defined in the Atria message catalog.
    */
    protected void clearCatalogVariables()
    {
        _variableNames = StringList.createArrayList();
        _variableNamesToExpressions = new HashMap();

        Assert.ensure(_variableNames != null);
        Assert.ensure(_variableNames.isEmpty());
        Assert.ensure(_variableNamesToExpressions != null);
        Assert.ensure(_variableNamesToExpressions.isEmpty());
    }

    /**
        @return true iff at least one variable is defined by "set" commands
        in the Atria message catalog document we're processing
    */
    protected boolean areVariables()
    {
        return (_variableNames.isEmpty() == false);
    }

    /**
        @return an iterator over the names of all of the variables defined by
        "set" commands in the Atria message catalog document we're
        processing: the iterator returns them in the order that the "set"
        commands occur in the document
    */
    protected StringIterator variableNamesIterator()
    {
        StringIterator result = _variableNames.iterator();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param varName a variable name
        @return the expression that evaluates to the value of the variable
        named 'varName', or null if there's no "set" command in the Atria
        document we're processing that sets the value of a variable named
        'varName'
    */
    protected AtriaConstructManager.Expression
        findVariableValue(String varName)
    {
        Assert.require(varName != null);

        // 'result' may be null
        return (AtriaConstructManager.Expression)
            _variableNamesToExpressions.get(varName);
    }

    /**
        @return true iff the catalog we're generating an output file from is
        localizable
        @see AtriaMessageCatalogData#isLocalizable
    */
    protected boolean isCatalogLocalizable()
    {
        return _data.isLocalizable();
    }

    /**
        @see AtriaMessageCatalogData#isQuotedArgumentElementName(String)
    */
    protected boolean isQuotedArgumentElementName(String name)
    {
        Assert.require(name != null);

        return _data.isQuotedArgumentElementName(name);
    }

    /**
        @param f the pathname of an Atria source file that describes a
        message catalog
        @return the pathname of the source file that we generate that will
        represent/implement the message catalog
    */
    protected File createOutputPathname(File f)
    {
        Assert.require(f != null);
        Assert.require(Io.hasExtension(f));

        File result = Io.replaceExtension(f, outputFileExtension());

        Assert.ensure(result != null);
        return result;
    }


    /**
        @param c a message element
        @return the number of arguments the message accepts, as specified by
        'c''s "args" attribute (which is assumed to be present)
    */
    protected int numberOfMessageArguments(AtriaConstructManager.Element c)
    {
        Assert.require(c != null);

        int result = -1;

        ConstructIterator iter = c.attributeList().iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) iter.next();
            String attrName = MANAGER.attributeName(attr);
            if (_data.isNumberOfArgumentsAttributeName(attrName))
            {
                result = AtriaMessageCatalogUtilities.
                            parseNonnegativeIntegerAttributeValue(attr);
                break;
            }
        }

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @param c an 'arg' or 'quoted-arg' element
        @return the index of the argument, as specified by 'c''s "index"
        attribute (which is assumed to be present)
    */
    protected int messageArgumentIndex(AtriaConstructManager.Element c)
    {
        Assert.require(c != null);

        int result = -1;

        ConstructIterator iter = c.attributeList().iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) iter.next();
            String attrName = MANAGER.attributeName(attr);
            if (_data.isArgumentIndexAttributeName(attrName))
            {
                result = AtriaMessageCatalogUtilities.
                            parseNonnegativeIntegerAttributeValue(attr);
                break;
            }
        }

        Assert.ensure(result >= 0);
        return result;
    }


    // Abstract methods

    /**
        Writes out the contents of the output file that we're currently
        building/generating.

        @param c the Atria document that represents the message catalog
        @param data the data about the message catalog we're building
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeOutputFile(AtriaConstructManager.Document c,
                        AtriaMessageCatalogData data, ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(data != null);
        // Assert.require(handler != null);

    /**
        Writes out the part of the output file we're generating that
        corresponds to the message catalog message that 'c' represents.

        @param c an element in the Atria message catalog that represents a
        single message in the catalog
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeMessage(AtriaConstructManager.Element c,
                                         ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Writes out the part of the output file we're generating that
        corresponds to the (quoted or unquoted) message catalog message
        argument that 'c' represents.

        @param c an element in the Atria message catalog that represents a
        single argument to a message in the catalog
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeMessageArgument(AtriaConstructManager.
                                            Element c, ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Writes to the output file we're building/generating the text 'txt'
        that appeared in the Atria source file as the contents of a quoted
        string.

        @param txt the contents between the double quotes of an Atria text
        literal
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void
        writeLiteralText(String txt, ErrorHandler handler);
        // Assert.require(txt != null);
        // Assert.require(handler != null);

    /**
        Writes to the output file we're generating the translation of the
        Atria "get" command that retrieves the value of the variable named
        'varName'.

        @param varName the name of the (Atria) variable that the get command
        retrieves the value of
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void
        writeGetCommand(String varName, ErrorHandler handler);
        // Assert.require(varName != null);
        // Assert.require(varName.length() > 0);
        // Assert.require(handler != null);

    /**
        Writes to our output file the translation of the "join" command 'c'.

        @param c the join command
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeJoinCommand(AtriaConstructManager.Command c,
                                             ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Writes to our output file the translation of the "newline" command
        'c'.

        @param c the newline command
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeNewlineCommand(AtriaConstructManager.
                                            Command c, ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Writes to our output file the translation of the "quote" command 'c'.

        @param c the quote command
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeQuoteCommand(AtriaConstructManager.
                                            Command c, ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Writes to our output file the translation of the "quoted" command
        'c'.

        @param c the quoted command
        @param handler the error handler to use to handle any and all errors
    */
    protected abstract void writeQuotedCommand(AtriaConstructManager.
                                            Command c, ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        @return the file extension on all output files we generate
        @see #createOutputPathname(File)
    */
    protected abstract String outputFileExtension();
        // Assert,ensure(result != null);
}
