/*
 Copyright (C) 2001-2005 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;

import java.io.IOException;

/**
    An abstract base class for classes that represent a language construct.
    <p>
    This class manages the construct's source location. Subclasses just have
    to implement the accept() method.

    @author James MacKay
    @version $Revision: 1.11 $
    @see SingleTokenConstruct
*/
public abstract class AbstractConstruct
    extends MinimalAbstractConstruct
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        CONSTRUCT_HAS_NO_VALUE_MSG =
            "CONSTRUCT_HAS_NO_VALUE_MSG",
        INVALID_VISITOR_TYPE_MSG =
            "INVALID_VISITOR_TYPE_MSG";


    // Private fields

    /**
        The location in the source code of the source fragment that this
        construct represents.
    */
    private SourceLocation _location;


    // Constructors

    /**
        Constructs an AbstractConstruct. Its location must be set using
        <code>setLocation</code>

        @param checklist the construct's validity constraint checklist
        @see Construct#setLocation
    */
    public AbstractConstruct(ValidityConstraintChecklist checklist)
    {
        this((SourceLocation) null, checklist);
    }

    /**
        Constructs an AbstractConstruct.

        @param loc the location in the source code of the source fragment
        that the construct represents
        @param checklist the construct's validity constraint checklist
    */
    public AbstractConstruct(SourceLocation loc,
                             ValidityConstraintChecklist checklist)
    {
        super(checklist);
        Assert.require(checklist != null);
        // 'loc' may be null

        _location = loc;
    }

    /**
        Copy constructor (more or less).

        @param c the construct that the construct is to be a copy of
        @param checklist the construct's validity constraint checklist
    */
    public AbstractConstruct(AbstractConstruct c,
                             ValidityConstraintChecklist checklist)
    {
        super(c, checklist);
        Assert.require(c != null);
        Assert.require(checklist != null);

        _location = c.location();
    }


    // Public methods

    /**
        @see Construct#location
    */
    public SourceLocation location()
    {
        return _location;
    }

    /**
        @see Construct#canSetLocation
    */
    public boolean canSetLocation()
    {
        return true;
    }

    /**
        @see Construct#setLocation
    */
    public void setLocation(SourceLocation loc)
    {
        Assert.require(canSetLocation());
        Assert.require(loc != null);

        _location = loc;
    }

    /**
        @see Construct#hasValue
    */
    public boolean hasValue()
    {
        return false;
    }

    /**
        @see Construct#value
    */
    public String value()
    {
        Assert.require(hasValue());

        String msg = _resources.
            getMessage(CONSTRUCT_HAS_NO_VALUE_MSG, getClass().getName());
        throw new NoSuchItemException(msg);
    }


    // Protected static methods

    /**
        Handles the case where the specified construct was asked to accept()
        the specified visitor, but can't because such constructs only accept
        visitors of the specified required class.

        @param v the visitor that 'c' was asked to accept
        @param c the construct that was asked to accept() 'v'
        @param requiredVisitorClass the class of visitor that 'c' accepts
        @exception IllegalArgumentException is always thrown as the way to
        handle the invalid type of visitor
    */
    protected static void handleInvalidVisitorType(ConstructVisitor v,
                                    Construct c, Class requiredVisitorClass)
        throws IllegalArgumentException
    {
        Assert.require(v != null);
        Assert.require(c != null);
        Assert.require(requiredVisitorClass != null);

        String msg = _resources.getMessage(INVALID_VISITOR_TYPE_MSG,
            v.getClass().getName(), c.getClass().getName(),
            requiredVisitorClass.getName());
        throw new IllegalArgumentException(msg);
    }
}
