/*
 Copyright (C) 2005 by James MacKay.

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

import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.plack.common.constructs.*;

/**
    An abstract base class for Atria constructs.
    <p>
    Subclasses should override the accept() method that takes an
    AtriaConstructVisitor, not the one that just takes a ConstructVisitor.
    (The latter is already implemented to cast the ConstructVisitor to an
    AtriaConstructVisitor and pass it to the other version of accept().)

    @author James MacKay
*/
public abstract class AtriaAbstractConstruct
    extends AbstractConstruct
    implements AtriaConstruct
{
    // Constructors

    /**
        @see AbstractConstruct#AbstractConstruct(ValidityConstraintChecklist)
    */
    public AtriaAbstractConstruct(ValidityConstraintChecklist checklist)
    {
        // Assert.require(checklist != null);
        super(checklist);
    }

    /**
        @see AbstractConstruct#AbstractConstruct(SourceLocation, ValidityConstraintChecklist)
    */
    public AtriaAbstractConstruct(SourceLocation loc,
                                  ValidityConstraintChecklist checklist)
    {
        // 'loc' may be null
        // Assert.require(checklist != null);
        super(loc, checklist);
    }

    /**
        @see AbstractConstruct#AbstractConstruct(AbstractConstruct, ValidityConstraintChecklist)
    */
    public AtriaAbstractConstruct(AtriaAbstractConstruct c,
                                  ValidityConstraintChecklist checklist)
    {
        // Assert.require(c != null);
        // Assert.require(checklist != null);
        super(c, checklist);
    }


    // Public methods

    /**
        @exception IllegalArgumentException thrown if the visitor
        is not an AtriaConstructVisitor
        @see Construct#accept(ConstructVisitor, ErrorHandler)
    */
    public void accept(ConstructVisitor visitor, ErrorHandler handler)
    {
        Assert.require(visitor != null);
        Assert.require(handler != null);

        accept(visitor, this, handler);
    }


    // Public static methods

    /**
        Causes the specified Atria construct to accept the specified visitor.
        <p>
        This method exists so that its implementation can be shared across
        the various implementations of the AtriaConstruct interface.

        @param visitor the visitor that the construct is to accept
        @param construct the construct that is to accept the visitor
        @param handler the error handler to be used to handle any errors that
        occur while visiting
        @exception IllegalArgumentException thrown if the visitor is not an
        AtriaConstructVisitor
    */
    public static void accept(ConstructVisitor visitor,
                              AtriaConstruct construct,
                              ErrorHandler handler)
    {
        Assert.require(visitor != null);
        Assert.require(construct != null);
        Assert.require(handler != null);

        if (visitor instanceof AtriaConstructVisitor)
        {
            construct.accept((AtriaConstructVisitor) visitor, handler);
        }
        else
        {
            handleInvalidVisitorType(visitor, construct,
                                     AtriaConstructVisitor.class);
        }
    }
}
