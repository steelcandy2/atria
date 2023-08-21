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

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.common.containers.Containers;

import java.util.*;

/**
    An abstract base class for AtriaExtendedConstructVisitors.
    <p>
    Note: if Java permitted multiple inheritance then most of the class'
    code would be in an AtriaAbstractCommandConstructVisitor class and
    this class would extend that class and AtriaAbstractConstructVisitor.

    @author  James MacKay
*/
public abstract class AtriaAbstractExtendedConstructVisitor
    extends AtriaAbstractConstructVisitor
    implements AtriaExtendedConstructVisitor
{
    // Constants

    /** The single Atria construct manager instance. */
    protected static final AtriaConstructManager
        MANAGER = AtriaConstructManager.instance();


    /**
        A map from each predefined Atria command name to the CommandSelector
        that selects the visitor method to use to visit a Command with that
        name. The keys are Strings and the values are CommandSelectors.
    */
    private static final Map _selectors = createCommandSelectorMap();


    // Visitor methods

    /**
        @see AtriaConstructVisitor#visitCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitCommand(AtriaConstructManager.Command c,
                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        acceptCommandVisitor(this, c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitGetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitGetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitJoinCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNamespaceCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitNewlineCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNewlineCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuoteCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitQuotedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitSetCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitTopCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }


    /**
        @see AtriaCommandConstructVisitor#visitNonPredefinedCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNonPredefinedCommand(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitUnknownNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitUnknownNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }

    /**
        @see AtriaCommandConstructVisitor#visitMissingNameCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitMissingNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        defaultCommandVisit(c, handler);
    }


    // Protected methods

    /**
        The method called by default to visit Command constructs.
        <p>
        This implementation just calls defaultVisit().

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
        @see AtriaAbstractConstructVisitor#defaultVisit(AtriaConstruct, ErrorHandler)
    */
    protected void defaultCommandVisit(AtriaConstructManager.Command c,
                                       ErrorHandler handler)
    {
        // Assert.require(c != null);
        // Assert.require(handler != null);

        defaultVisit(c, handler);
    }


    // Protected static methods

    /**
        Causes the specified visitor to accept the specified command by
        causing the proper visitor method to be called on the visitor and
        passed the command.

        @param v the visitor that 'c' is to accept
        @param c the construct that is to accept 'v'
        @param handler the error handler passed to the visitor method that
        visits 'c'
    */
    protected static void
        acceptCommandVisitor(AtriaCommandConstructVisitor v,
                             AtriaConstructManager.Command c,
                             ErrorHandler handler)
    {
        Assert.require(v != null);
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = null;
        if (c.hasName())
        {
            AtriaConstructManager.Name cmdName = c.name();
            if (cmdName.namespacePrefixAttribute().value().length() > 0)
            {
                v.visitNonPredefinedCommand(c, handler);
            }
            else
            {
                name = MANAGER.toText(cmdName);
                if (name != null)
                {
                    CommandSelector selector =
                        (CommandSelector) _selectors.get(name);
                    if (selector != null)
                    {
                        selector.select(v, c, handler);
                    }
                    else
                    {
                        v.visitUnknownNameCommand(c, handler);
                    }
                }
            }
        }

        if (name == null)
        {
            v.visitMissingNameCommand(c, handler);
        }
    }


    // Private static methods

    /**
        @return a map from each predefined Atria command name to the
        CommandSelector that selects the visitor method to use to visit a
        Command with that name: the keys are Strings and the values are
        CommandSelectors
    */
    private static Map createCommandSelectorMap()
    {
        int numEntries = AtriaInfo.COMMAND_NAMES_LIST.size();

        Map result = Containers.createHashMap(numEntries);

        result.put(AtriaInfo.GET_COMMAND_NAME,
                    new GetCommandSelector());
        result.put(AtriaInfo.JOIN_COMMAND_NAME,
                    new JoinCommandSelector());
        result.put(AtriaInfo.NAMESPACE_COMMAND_NAME,
                    new NamespaceCommandSelector());
        result.put(AtriaInfo.NEWLINE_COMMAND_NAME,
                    new NewlineCommandSelector());
        result.put(AtriaInfo.QUOTE_COMMAND_NAME,
                    new QuoteCommandSelector());
        result.put(AtriaInfo.QUOTED_COMMAND_NAME,
                    new QuotedCommandSelector());
        result.put(AtriaInfo.SET_COMMAND_NAME,
                    new SetCommandSelector());
        result.put(AtriaInfo.TOP_COMMAND_NAME,
                    new TopCommandSelector());

        Assert.ensure(result != null);
        Assert.ensure(result.size() == numEntries);
        return result;
    }


    // Inner classes

    /**
        The interface implemented by classes that select the visitor method
        that an AtriaCommandConstructVisitor is to use to visit a command.
    */
    private static interface CommandSelector
    {
        /**
            Selects the method that the specified visitor should use to
            visit the specified command and calls that method.

            @param v the visitor that is to visit 'c'
            @param c the command to be visited by 'c'
            @param handler the error handler to pass as an argument to the
            visitor method that is called
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler);
            // Assert.require(v != null);
            // Assert.require(c != null);
            // Assert.require(handler != null);
    }

    /**
        Selects the visitor method to use to visit 'get' commands.
    */
    private static class GetCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitGetCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'join' commands.
    */
    private static class JoinCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitJoinCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'namespace' commands.
    */
    private static class NamespaceCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitNamespaceCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'newline' commands.
    */
    private static class NewlineCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitNewlineCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'quote' commands.
    */
    private static class QuoteCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitQuoteCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'quoted' commands.
    */
    private static class QuotedCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitQuotedCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'set' commands.
    */
    private static class SetCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitSetCommand(c, handler);
        }
    }

    /**
        Selects the visitor method to use to visit 'top' commands.
    */
    private static class TopCommandSelector
        implements CommandSelector
    {
        /**
            @see AtriaAbstractExtendedConstructVisitor.CommandSelector#select(AtriaCommandConstructVisitor, AtriaConstructManagerBase.Command, ErrorHandler)
        */
        public void select(AtriaCommandConstructVisitor v,
            AtriaConstructManager.Command c, ErrorHandler handler)
        {
            Assert.require(v != null);
            Assert.require(c != null);
            Assert.require(handler != null);

            v.visitTopCommand(c, handler);
        }
    }
}
