/*
 Copyright (C) 2001-2004 by James MacKay.

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

import com.steelcandy.plack.common.tokens.Token;

import com.steelcandy.common.io.Io;

import java.io.IOException;

/**
    A base class for a language construct that is represented by a single
    token.
    <p>
    An instance's <code>setToken()</code> must be called before that
    instance can be used.
    <p>
    Subclasses just have to implement the accept() method and
    writeTokenInformation() methods.

    @author James MacKay
    @see #setToken(Token)
    @see AbstractConstruct
*/
public abstract class SingleTokenConstruct
    extends MinimalAbstractConstruct
{
    // Private fields

    /**
        The token that represents the same source fragment that this
        construct does.
    */
    private Token _token;


    // Constructors

    /**
        Constructs a SingleTokenConstruct.

        @param checklist the construct's validity constraint checklist
    */
    public SingleTokenConstruct(ValidityConstraintChecklist checklist)
    {
        // Assert.require(checklist != null);
        super(checklist);
    }

    /**
        Copy constructor (more or less).

        @param c the construct that the construct is to be a copy of
        @param checklist the construct's validity constraint checklist
    */
    public SingleTokenConstruct(SingleTokenConstruct c,
                                ValidityConstraintChecklist checklist)
    {
        super(c, checklist);
        Assert.require(c != null);
        Assert.require(checklist != null);

        // We don't clone the token since it should never be modified.
        _token = c.token();
    }


    // Public methods

    /**
        @return the token that represents the same source fragment as this
        construct
    */
    public Token token()
    {
        return _token;
    }

    /**
        Sets the specified token to be the token that represents the same
        source fragment as this construct.

        @param tok the token that represents the same source fragment as this
        construct
    */
    public void setToken(Token tok)
    {
        Assert.require(tok != null);

        _token = tok;
    }

    /**
        @see Construct#location
    */
    public SourceLocation location()
    {
        return _token.location();
    }

    /**
        @see Construct#canSetLocation
    */
    public boolean canSetLocation()
    {
        return false;
    }

    /**
        This method does nothing (except check its preconditions) rather than
        throw an exception so that subclasses can override this method and
        call this version, even if the subclass allows the construct's
        location to be set.

        @see Construct#setLocation
    */
    public void setLocation(SourceLocation loc)
    {
        Assert.require(canSetLocation());
        Assert.require(loc != null);
    }

    /**
        @see Construct#write(ConstructWriter, String, SourceCode)
    */
    public void write(ConstructWriter w, String name, SourceCode source)
        throws IOException
    {
        Assert.require(w != null);
        // 'name' can be null
        // 'source' can be null

        w.writeConstructStart(this, name, source);
        w.writeConstructEnd(this, name, source);
    }

    /**
        @see Construct#hasValue
    */
    public boolean hasValue()
    {
        return (_token != null) && _token.hasStringValue();
    }

    /**
        @see Construct#value
    */
    public String value()
    {
        Assert.require(hasValue());

        return _token.stringValue();
    }
}
