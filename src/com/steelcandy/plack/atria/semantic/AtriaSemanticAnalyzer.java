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

package com.steelcandy.plack.atria.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;
import com.steelcandy.plack.atria.constructs.*;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

import java.util.*;

/**
    The class of semantic analyzer that analyzes Atria constructs.

    @author  James MacKay
    @version $Revision: 1.14 $
*/
public class AtriaSemanticAnalyzer
    extends AtriaAbstractSemanticAnalyzer
{
    // Private fields

    /**
        A set of the namespace prefixes defined by all of the 'namespace'
        Commands we're analyzed so far. Each item is a String.
    */
    private Set _namespacePrefixes;

    /**
        Indicates whether the construct we're currently analyzing is a
        Command that is a direct subconstruct of a PrologueItem.
    */
    private boolean _isPrologueItem;

    /**
        A list of all of the Command constructs that we have visited so
        far that represent 'top' commands that are direct subconstructs of
        PrologueItems. Each item in the list is a Command construct.

        @see AtriaConstructManagerBase.Command
    */
    private ConstructList _topCommandPrologueItems;

    /**
        The depth of the current (or next) element: the root element of a
        document has depth 0, its direct Element subconstructs have depth 1,
        etc. The top element specified by a 'top' command - if there is one -
        is taken into account.
    */
    private int _elementDepth;


    // Constructors

    /**
        Constructs an AtriaSemanticAnalyzer.
    */
    public AtriaSemanticAnalyzer()
    {
        _namespacePrefixes = new HashSet();
        _isPrologueItem = false;
        _topCommandPrologueItems = ConstructList.createArrayList();
        _elementDepth = 0;
    }


    // Public methods

    /**
        Performs semantic analysis on the specified document.

        @param doc the document to analyze
        @param handler the error handler to use to handle any errors that
        occur in analyzing 'doc'
    */
    public void analyze(AtriaConstructManager.Document doc,
                        ErrorHandler handler)
    {
        Assert.require(doc != null);
        Assert.require(handler != null);

        doc.accept(this, handler);
    }

    /**
        Performs semantic analysis on the specified element.
        <p>
        This method allows elements to be analyzed one at a time rather than
        all at once as part of a Document, which may be useful for programs
        that do not add all of the Element subconstructs to a Document (in
        order to avoid putting representations of large documents in memory
        all at once). All of a Document's Element subconstructs should be
        analyzed by the same instance of this class, however.

        @param e the element to analyze
        @param depth the depth of 'e' in its document: 1 if it is the
        document's root element, 2 if it is a child element of a document's
        root element, etc.
        @param handler the error handler to use to handle any errors that
        occur in analyzing 'e'
    */
    public void analyze(AtriaConstructManager.Element e, int depth,
                        ErrorHandler handler)
    {
        Assert.require(e != null);
        Assert.require(depth >= 1);
        Assert.require(handler != null);

        e.accept(this, handler);
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

        visitDocumentSubconstructs(c, handler);

// TODO: do we have to change this to handle the case where Elements are
// analyzed separately ???!!!???
// - see analyze(Element, ...)'s 'depth' argument
        int numElements = c.elementCount();
        if (_topCommandPrologueItems.isEmpty() && numElements != 1)
        {
            if (numElements > 1)
            {
                CHECKER.reportMoreThanOneTopElement(c, handler);
            }
            else
            {
                Assert.check(numElements == 0);
                CHECKER.reportNoTopElement(c, handler);
            }
        }
        CHECKER.checkedDocumentIsTopElement(c);

        // Note: for plain Atria documents there's nothing to do to check
        // a Document's Application constraint, so we just mark it checked
        // here. Application-specific subclasses may have to do some checks,
        // though, in which case they should override the appropriate visitor
        // methods to perform the checks.
        CHECKER.checkedDocumentApplication(c);
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

        int numItems = _topCommandPrologueItems.size();
        if (numItems > 0)
        {
            _elementDepth += 1;
            if (numItems > 1)
            {
                CHECKER.reportTooManyTopCommandPrologueItems(c,
                                        _topCommandPrologueItems, handler);
            }
        }
        CHECKER.checkedPrologueAtMostOneTopItem(c);
    }

    /**
        @see AtriaConstructVisitor#visitLanguageSpecifier(AtriaConstructManagerBase.LanguageSpecifier, ErrorHandler)
    */
    public void visitLanguageSpecifier(AtriaConstructManager.
        LanguageSpecifier c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitLanguageSpecifierSubconstructs(c, handler);

        CHECKER.checkFirstIsLanguage(c, handler);
        CHECKER.checkLanguageName(c, handler);

        CHECKER.checkedLanguageSpecifierFirstIsLanguage(c);
        CHECKER.checkedLanguageSpecifierName(c);
    }

    /**
        @see AtriaConstructVisitor#visitPrologueItem(AtriaConstructManagerBase.PrologueItem, ErrorHandler)
    */
    public void visitPrologueItem(AtriaConstructManager.
        PrologueItem c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        _isPrologueItem = true;
        try
        {
            visitPrologueItemSubconstructs(c, handler);
        }
        finally
        {
            _isPrologueItem = false;
        }
    }

    /**
        @see AtriaConstructVisitor#visitElement(AtriaConstructManagerBase.Element, ErrorHandler)
    */
    public void visitElement(AtriaConstructManager.
        Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        IntAttribute attr = c.depthAttribute();
        Assert.check(attr.isSet() == false);
        attr.setValue(_elementDepth);

        _elementDepth += 1;
        try
        {
            visitElementSubconstructs(c, handler);
        }
        finally
        {
            _elementDepth -= 1;
        }

        CHECKER.checkAttributeNamesUnique(c, handler);
        CHECKER.checkedElementUniqueAttributeNames(c);

        Assert.ensure(c.depthAttribute().isSet());
        Assert.ensure(c.depthAttribute().value() >= 0);
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

        visitAttributeSubconstructs(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitCommand(AtriaConstructManager.Command c,
                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        analyzeCommand(c, handler);
        super.visitCommand(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitText(AtriaConstructManagerBase.Text, ErrorHandler)
    */
    public void visitText(AtriaConstructManager.
        Text c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitTextSubconstructs(c, handler);
    }

    /**
        @see AtriaConstructVisitor#visitName(AtriaConstructManagerBase.Name, ErrorHandler)
    */
    public void visitName(AtriaConstructManager.Name c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        visitNameSubconstructs(c, handler);

        Assert.check(c.namespacePrefixAttribute().isSet() == false);
        Assert.check(c.unqualifiedNameAttribute().isSet() == false);

        String prefix = "";
        String unqualifiedName = "";

        String name = MANAGER.toText(c);
        if (name != null)
        {
            int len = name.length();
            int sepIndex = name.indexOf(AtriaInfo.NAMESPACE_SEPARATOR);
            if (sepIndex < 0)
            {
                // empty - there's no namespace separator in 'name'
            }
            else if (sepIndex == 0)
            {
                CHECKER.reportNameStartsWithNamespaceSeparator(c, handler);
            }
            else if (sepIndex == len - 1)
            {
                CHECKER.reportNameEndsWithNamespaceSeparator(c, handler);
            }
            else
            {
                int nextSepIndex = name.
                    indexOf(AtriaInfo.NAMESPACE_SEPARATOR, sepIndex + 1);
                if (nextSepIndex >= 0)
                {
                    CHECKER.reportNameHasMultipleNamespaceSeparators(c,
                                                    nextSepIndex, handler);
                }
            }

            if (sepIndex > 0 && sepIndex < len - 1)
            {
                // The name has a namespace prefix.
                prefix = name.substring(0, sepIndex);
                unqualifiedName = name.substring(sepIndex + 1);
            }
            else  // 'c' has no namespace prefix
            {
                unqualifiedName = name;
            }
        }
        CHECKER.checkedNameOneNamespaceSeparator(c);
        CHECKER.checkedNameNamespaceSeparatorNotAtEnds(c);

        c.namespacePrefixAttribute().setValue(prefix);
        c.unqualifiedNameAttribute().setValue(unqualifiedName);

        if (prefix.length() > 0 &&
            _namespacePrefixes.contains(prefix) == false)
        {
            CHECKER.reportNameNamespaceNotDefined(c, prefix, handler);
        }
        CHECKER.checkedNameDefinedNamespacePrefix(c);

        Assert.ensure(c.namespacePrefixAttribute().isSet());
        Assert.ensure(c.unqualifiedNameAttribute().isSet());
    }


    // Command visitor methods

    /**
        @see AtriaCommandConstructVisitor#visitGetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitGetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkOneNameArgument(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkJoinCommandArguments(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNamespaceCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkNamespaceCommandArguments(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNewlineCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNewlineCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkZeroArguments(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkZeroArguments(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkOneExpressionArgument(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitSetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkSetCommandArguments(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitTopCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.checkTopCommandArguments(c, handler);
    }


    /**
        @see AtriaCommandConstructVisitor#visitNonPredefinedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNonPredefinedCommand(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // At least for now we don't allow commands that contain namespace
        // prefixes.
        CHECKER.reportNotPredefinedCommandName(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitUnknownNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitUnknownNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        CHECKER.reportNotPredefinedCommandName(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitMissingNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitMissingNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // empty
    }


    // Protected methods

    /**
        Analyzes each command, regardless of which specific command it
        represents.

        @param c the command to analyze
        @param handler the error handler to use to handle any errors that
        occur while analyzing 'c'
    */
    protected void analyzeCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = MANAGER.commandName(c);
        boolean isPrologueItem = _isPrologueItem;
        try
        {
            _isPrologueItem = false;  // for our subconstructs
            visitCommandSubconstructs(c, handler);
        }
        finally
        {
            _isPrologueItem = isPrologueItem;
        }

        if (isPrologueItem)
        {
            CHECKER.checkCommandNonPrologueItem(c, handler);
            if (name != null)
            {
                if (name.equals(AtriaInfo.TOP_COMMAND_NAME))
                {
                    _topCommandPrologueItems.add(c);
                }
                else if (name.equals(AtriaInfo.NAMESPACE_COMMAND_NAME))
                {
                    addNamespacePrefix(_namespacePrefixes, c, handler);
                }
            }
        }
        else  // isPrologueItem == false
        {
            CHECKER.checkCommandPrologueItemOnly(c, handler);
        }
        CHECKER.checkedCommandNonPrologueItem(c);
        CHECKER.checkedCommandPrologueItemOnly(c);

        // The following will be checked by the specific command visitor
        // methods (and addNamespacePrefix()).
        CHECKER.checkedCommandPredefinedName(c);

        CHECKER.checkedCommandNoArguments(c);
        CHECKER.checkedCommandOneArgument(c);
        CHECKER.checkedCommandTwoArguments(c);

        CHECKER.checkedCommandOneNameArgument(c);
        CHECKER.checkedCommandOneExpressionArgument(c);

        CHECKER.checkedCommandJoinArguments(c);
        CHECKER.checkedCommandNamespaceArguments(c);
        CHECKER.checkedCommandSetArguments(c);
        CHECKER.checkedCommandTopArguments(c);
    }


    /**
        Adds to the specified set the namespace prefix defined by the
        specified 'namespace' command.
        <p>
        Note: a prefix may not get added if the 'namespace' command is
        invalid, or defines a prefix that has already been defined.

        @param s the set to add a namespace prefix to
        @param c the 'namespace' command that defines the prefix
        @param handler the error handler to use to handle any errors that
        occur in adding the namespace prefix
    */
    protected void addNamespacePrefix(Set s, AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(s != null);
        Assert.require(c != null);
        Assert.require(handler != null);

        String prefix = null;
        if (c.argumentCount() > 0)
        {
            Construct firstArg = c.argumentList().get(0);
            if (firstArg.id() == MANAGER.NAME)
            {
                prefix = MANAGER.
                    toText((AtriaConstructManager.Name) firstArg);
            }
        }

        if (prefix != null && s.add(prefix) == false)
        {
            // There's already a 'namespace' command that defines 'prefix'.
            CHECKER.reportNamespacePrefixAlreadyDefined(c, prefix, handler);
        }
    }

    /**
        Note: the top element specified by a 'top' command - if there is
        one - is taken into account.

        @return the depth of the current (or next) element: the root element
        of a document has depth 0, its direct Element subconstructs have
        depth 1, etc.
    */
    protected int elementDepth()
    {
        int result = _elementDepth;

        Assert.ensure(result >= 0);
        return result;
    }
}
