/*
 Copyright (C) 2005-2009 by James MacKay.

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

import com.steelcandy.common.Resources;
import com.steelcandy.common.containers.Containers;
import com.steelcandy.common.xml.XmlUtilities;

import java.io.File;
import java.util.*;

/**
    An abstract base class for Atria interpreters.
    <p>
    Note: most subclasses' visitor methods will be written to assume that
    there are no lexical, syntax or semantic errors in the Atria code being
    processed. Of course, it is up to the subclass to ensure that the visitor
    methods are only called under such circumstances (for example, by calling
    them only if haveBeenErrors() returns false after having called
    toValidatedDocument()).

    @author  James MacKay
    @version $Revision: 1.15 $
*/
public abstract class AtriaAbstractInterpreter
    extends AtriaAbstractExtendedConstructVisitor
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaProgramResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        VARIABLE_ALREADY_SET_MSG =
            "VARIABLE_ALREADY_SET_MSG",
        UNSET_VARIABLE_USED_MSG =
            "UNSET_VARIABLE_USED_MSG";


    /** The single parser factory instance. */
    protected static final AtriaParserFactory
        PARSER_FACTORY = AtriaParserFactory.instance();

    /** The initial size of an instance's variable pool. */
    private static final int INITIAL_VARIABLE_POOL_SIZE = 64;


    // Private fields

    /**
        A map from the names of the currently defined Atria variables to
        their values. The keys and the values are all Strings.
    */
    private Map _variablePool;


    // Constructors

    /**
        Constructs an AtriaAbstractInterpreter
    */
    public AtriaAbstractInterpreter()
    {
        _variablePool = Containers.createHashMap(INITIAL_VARIABLE_POOL_SIZE);
        addInitialVariables();
    }


    // Visitor methods

    /**
        @see AtriaConstructVisitor#visitLanguageSpecifier(AtriaConstructManagerBase.LanguageSpecifier, ErrorHandler)
    */
    public void visitLanguageSpecifier(AtriaConstructManager.
        LanguageSpecifier c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // empty - interpreters generally don't care about these
    }


    /**
        @see AtriaCommandConstructVisitor#visitUnknownNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitUnknownNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since interpreters shouldn't be interpreting documents with
            // errors in them
    }

    /**
        @see AtriaCommandConstructVisitor#visitMissingNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitMissingNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since interpreters shouldn't be interpreting documents with
            // errors in them
    }


    // Variable pool methods

    /**
        Note: the value of 'c''s IdentifierArgument attribute will be used as
        the name of the variable to set.

        @param c the construct that represents the 'set' command that is
        setting the variable
        @param value the value to set the variable named 'name' to
        @param handler the error handler used to handle any errors that occur
        in trying to set the variable's value (for example, if the variable's
        value has already been set)
    */
    protected void setVariable(AtriaConstructManager.Command c,
                               String value, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.nameArgumentAttribute().isSet());
        Assert.require(value != null);
        Assert.require(handler != null);

        String name = c.nameArgumentAttribute().value();
//System.err.println("===> setting the value of '" + name + "' to '" + value + "'");
        String oldValue = (String) _variablePool.put(name, value);
        if (oldValue != null)
        {
            Construct errorConstruct = c;
            if (c.argumentCount() > 0)
            {
                errorConstruct = c.argumentList().get(0);
                            // = the name of the variable being set
            }
            String msg = _resources.
                getMessage(VARIABLE_ALREADY_SET_MSG, name, value,
                           oldValue);
            handleRuntimeError(FATAL_ERROR_LEVEL, msg, errorConstruct,
                               handler);
        }
    }

    /**
        Note: the value of 'c''s IdentifierArgument attribute will be used as
        the name of the variable to get.

        @param c the construct that represents the 'get' command that is
        getting the variable's value
        @param handler the error handler to use to handle any errors that
        occur in trying to get the variable's value (for example, if the
        variable's value hasn't been set)
        @return the value of the Atria variable named 'name'
    */
    protected String getVariable(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.nameArgumentAttribute().isSet());
        Assert.require(handler != null);

        String name = c.nameArgumentAttribute().value();

        String result = (String) _variablePool.get(name);
//System.err.println("---> got value of variable '" + name + "': '" + result + "'");
        if (result == null)
        {
            Construct errorConstruct = c;
            if (c.argumentCount() > 0)
            {
                errorConstruct = c.argumentList().get(0);
                            // = the name of the variable
            }
            String msg = _resources.
                getMessage(UNSET_VARIABLE_USED_MSG, name);
            handleRuntimeError(FATAL_ERROR_LEVEL, msg, errorConstruct,
                               handler);
        }

        Assert.require(result != null);
        return result;
    }


    /**
        Adds to the variable pool all of the variables whose values are
        set before an Atria document it processed. Overridden versions of
        this method should first call the superclass version.
        <p>
        This method is used to set the values of read-only variables defined
        by Atria and/or an application. Variables are to be added here using
        setInitialVariable().
        <p>
        The names of the variables added here should always contain a
        namespace prefix, as ones without such a prefix are to be left to the
        Atria document writer to set.

        @see #setInitialVariable(String, String)
    */
    protected void addInitialVariables()
    {
        setInitialVariable("ns:xsl", XmlUtilities.XSL_NAMESPACE_URI);
    }

    /**
        Sets the value of the Atria variable with the specified name to the
        specified value as part of loading initial variables into the
        variable pool. This method should only be called from
        addInitialVariables().
        <p>
        Note: unlike when setting a variable from Atria code it is legal to
        reset the value of a variable here.

        @param name the name of the variable to set
        @param value the value to set the variable named 'name' to
        @see #addInitialVariables
        @see #setVariable(AtriaConstructManagerBase.Command, String, ErrorHandler)
    */
    protected void setInitialVariable(String name, String value)
    {
        Assert.require(name != null);
        Assert.require(value != null);

        _variablePool.put(name, value);
    }


    // Protected methods

    /**
        Creates and returns an error handler.
        <p>
        This implementation returns an error handler that outputs error
        information to standard error.

        @return an error handler to use to handle any errors that occur in
        interpreting a new Atria document
    */
    protected ErrorHandler createErrorHandler()
    {
        ErrorHandler result = WriterErrorHandler.createForStandardError();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Converts the contents of the specified file into an Atria document
        by tokenizing it, parsing it and then semantically analyzing it.

        @param f the pathname of the Atria source file to be processed
        @param handler the error handler to use to handle any errors that
        occur in processing 'f'
        @return the Atria document that the contents of 'f' represent, or
        null if the contents of 'f' do not represent an Atria document
    */
    protected AtriaConstructManager.Document
        toValidatedDocument(File f, ErrorHandler handler)
    {
        Assert.require(f != null);
        Assert.require(handler != null);

        AtriaConstructManager.Document result;
        SourceCodeTokenizer t = null;
        try
        {
            t = createTokenizer(f, handler);
            AtriaDocumentParser p = createDocumentParser();

            result = p.parseTopLevel(t, handler);
            if (result != null)
            {
                AtriaSemanticAnalyzer a = createSemanticAnalyzer();
                a.analyze(result, handler);

                AtriaValidityConstraintChecklistCompletionChecker checker =
                    new AtriaValidityConstraintChecklistCompletionChecker();
                checker.check(result, handler);
            }
        }
        catch (FatalErrorException ex)
        {
            // The fatal error has already been reported to 'handler'.
            result = null;
        }
        finally
        {
            if (t != null)
            {
                t.close();
            }
        }

        // 'result' may be null
        return result;
    }

    /**
        @return the semantic analyzer to use to analyze a new Atria
        document
    */
    protected AtriaSemanticAnalyzer createSemanticAnalyzer()
    {
        AtriaSemanticAnalyzer result = new AtriaSemanticAnalyzer();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return the parser that this interpreter will use to parse a new
        Atria document
    */
    protected AtriaDocumentParser createDocumentParser()
    {
        AtriaDocumentParser result = PARSER_FACTORY.createDocumentParser();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param f the pathname of an Atria source code file
        @param handler the error handler that the tokenizer is to use to
        handle any errors that occur in tokenizing 'f'
        @return the tokenizer that this interpreter will use to tokenize 'f'
    */
    protected SourceCodeTokenizer
        createTokenizer(File f, ErrorHandler handler)
    {
        Assert.require(f != null);
        Assert.require(handler != null);

        AtriaDefaultTokenizer result = new AtriaDefaultTokenizer(handler);

        result.initialize(new SourceCodeFile(f));

        Assert.ensure(result != null);
        return result;
    }


    /**
        Note: this implementation doesn't consider warnings or lesser
        "errors" to be errors.

        @param handler an error handler
        @return true iff 'handler' hasn't handled any errors yet
    */
    protected boolean haveBeenErrors(ErrorHandler handler)
    {
        Assert.require(handler != null);

        return (handler.nonFatalAndAboveErrorCount() > 0);
    }

    /**
        Handles the runtime error described by the specified information
        using the specified error handler.

        @param level the severity level of the error
        @param description a description of the error
        @param c the construct that represents the fragment of source code
        that resulted in the error
        @param handler the error handler to use to handle the runtime error
    */
    protected void handleRuntimeError(int level, String description,
                                      Construct c, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(handler != null);

        handler.handle(new RuntimeError(level, description, c.sourceCode(),
                                        c.location()));
    }
}
