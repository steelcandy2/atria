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

import com.steelcandy.plack.atria.tokens.AtriaTokenManager;
import com.steelcandy.plack.atria.constructs.*;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.semantic.*;

/**
    An abstract base class for Atria semantic analyzers.
    <p>
    Semantic analyzers
    <ul>
        <li>set various construct attributes, and
        <li>check validity constraints that aren't checked as parts of other
            operations.
    </ul>
    <p>
    Note: semantic analyzers decide what gets done when, while other
    classes - e.g. the CHECKER - perform the actual work (checking
    constraints building/setting construct attributes' values).

    @author  James MacKay
    @see #CHECKER
*/
public class AtriaAbstractSemanticAnalyzer
    extends AtriaAbstractExtendedConstructVisitor
{
    // Constants

    /** The single AtriaTokenManager instance. */
    protected static final AtriaTokenManager
        TOKEN_MANAGER = AtriaTokenManager.instance();

    /** The single Atria constraint checker instance. */
    protected static final AtriaConstraintChecks
        CHECKER = AtriaConstraintChecks.instance();


    // Constructors

    /**
        Constructs an AtriaAbstractSemanticAnalyzer.
    */
    public AtriaAbstractSemanticAnalyzer()
    {
        // empty
    }


    // Protected static methods

    /**
        @see AtriaConstraintChecks#reportApplicationConstraintViolated(String, Construct, ErrorHandler)
    */
    protected static void
        reportApplicationConstraintViolated(String description,
                            Construct errorConstruct, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(errorConstruct != null);
        Assert.require(handler != null);

        CHECKER.reportApplicationConstraintViolated(description,
                                                    errorConstruct, handler);
    }
}
