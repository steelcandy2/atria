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

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.plack.common.constructs.Construct;  // javadocs only
import com.steelcandy.plack.common.constructs.ConstructVisitor;
import com.steelcandy.plack.common.constructs.SingleTokenConstruct;
import com.steelcandy.plack.common.constructs.ValidityConstraintChecklist;

/**
    A base class for an Atria language construct that is represented by a
    single token.
    <p>
    Subclasses should override the accept() method that takes an
    AtriaConstructVisitor, not the one that just takes a ConstructVisitor.
    (The latter is already implemented to cast the ConstructVisitor to an
    AtriaConstructVisitor and pass it to the other version of accept().)

    @author James MacKay
    @version $Revision: 1.14 $
*/
public abstract class AtriaSingleTokenConstruct
    extends SingleTokenConstruct
    implements AtriaConstruct
{
    // Constructors

    /**
        @see SingleTokenConstruct#SingleTokenConstruct(ValidityConstraintChecklist)
    */
    public AtriaSingleTokenConstruct(ValidityConstraintChecklist checklist)
    {
        // Assert.require(checklist != null);
        super(checklist);
    }

    /**
        @see SingleTokenConstruct#SingleTokenConstruct(SingleTokenConstruct, ValidityConstraintChecklist)
    */
    public AtriaSingleTokenConstruct(AtriaSingleTokenConstruct c,
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

        AtriaAbstractConstruct.accept(visitor, this, handler);
    }
}
