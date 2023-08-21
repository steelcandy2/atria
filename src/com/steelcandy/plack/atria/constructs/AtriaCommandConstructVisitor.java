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

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The interface implemented by classes that visit specific types of
    Atria commands.

    @author  James MacKay
*/
public interface AtriaCommandConstructVisitor
{
    // Visitor methods

    /**
        Visits the specified 'get' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitGetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'join' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitJoinCommand(AtriaConstructManager.Command c,
                                 ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'namespace' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'newline' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitNewlineCommand(AtriaConstructManager.Command c,
                                    ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'quote' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitQuoteCommand(AtriaConstructManager.Command c,
                                  ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'quoted' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitQuotedCommand(AtriaConstructManager.Command c,
                                   ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'set' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitSetCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified 'top' command.

        @param c the command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);


    /**
        Visits the specified command, whose name contains a namespace
        prefix and hence is not a predefined command.

        @param c the non-predefined command to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitNonPredefinedCommand(AtriaConstructManager.Command c,
                                          ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified command, whose name does not contain a namespace
        prefix but is also not the name of a known predefined command.

        @param c the command with the unknown name to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitUnknownNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);

    /**
        Visits the specified command, whose name is missing.

        @param c the command with the missing name to visit
        @param handler the error handler to use to handle any errors that
        occur while visiting 'c'
    */
    public void visitMissingNameCommand(AtriaConstructManager.Command c,
                                        ErrorHandler handler);
        // Assert.require(c != null);
        // Assert.require(handler != null);
}
