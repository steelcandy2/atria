/*
 Copyright (C) 2015 by James MacKay.

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

import com.steelcandy.plack.common.source.HasSourceLocation;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.errors.ErrorHandler;

import java.io.IOException;

/**
    The class of Construct that delegates to another Construct.
    <p>
    Note: one use for this class is to prevent a construct from being cast
    to a more specific type (other than this one, which isn't particularly
    useful).

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class DefaultDelegatingConstruct
    implements Construct
{
    // Private fields

    /** The Construct that we delegate to. */
    private Construct _delegate;


    // Constructors

    /**
        Constructs a DefaultDelegatingConstruct from the construct that it
        is to delegate to.

        @param c the construct to be delegated to
    */
    public DefaultDelegatingConstruct(Construct c)
    {
        Assert.require(c != null);

        _delegate = c;
    }


    // Public methods

    /**
        @see HasSourceLocation#location
    */
    public SourceLocation location()
    {
        SourceLocation result = delegate().location();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#id
    */
    public int id()
    {
        return delegate().id();
    }

    /**
        @see Construct#accept(ConstructVisitor, ErrorHandler)
    */
    public void accept(ConstructVisitor visitor,
                       ErrorHandler handler)
    {
        Assert.require(visitor != null);
        Assert.require(handler != null);

        delegate().accept(visitor, handler);
    }

    /**
        @see Construct#canSetLocation
    */
    public boolean canSetLocation()
    {
        return delegate().canSetLocation();
    }

    /**
        @see Construct#setLocation(SourceLocation)
    */
    public void setLocation(SourceLocation loc)
    {
        Assert.require(canSetLocation());
        Assert.require(loc != null);

        delegate().setLocation(loc);
    }

    /**
        @see Construct#hasValue
    */
    public boolean hasValue()
    {
        return delegate().hasValue();
    }

    /**
        @see Construct#value
    */
    public String value()
    {
        Assert.require(hasValue());

        return delegate().value();
    }

    /**
        @see Construct#write(ConstructWriter, SourceCode)
    */
    public void write(ConstructWriter w, SourceCode source)
        throws IOException
    {
        Assert.require(w != null);
        // 'source' can be null

        delegate().write(w, source);
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

        delegate().write(w, name, source);
    }

    /**
        @see Construct#safe
    */
    public SafeConstruct safe()
    {
        SafeConstruct result = new SafeConstruct(this);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#toUnsafeConstruct
    */
    public Construct toUnsafeConstruct()
    {
        // Assert.ensure(result != null);
        return this;
    }

    /**
        @see Construct#deepClone
    */
    public Construct deepClone()
    {
        Construct result =
            new DefaultDelegatingConstruct(delegate().deepClone());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#shallowClone
    */
    public Construct shallowClone()
    {
        Construct result =
            new DefaultDelegatingConstruct(delegate().shallowClone());

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see Construct#hasCorrectnessData
    */
    public boolean hasCorrectnessData()
    {
        return delegate().hasCorrectnessData();
    }

    /**
        @see Construct#removeCorrectnessData
    */
    public void removeCorrectnessData()
    {
        delegate().removeCorrectnessData();
    }

    /**
        @see Construct#validityChecklist
    */
    public ValidityConstraintChecklist validityChecklist()
        throws MissingConstructCorrectnessDataException
    {
        ValidityConstraintChecklist result = delegate().validityChecklist();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#setSourceCode(SourceCode)
    */
    public void setSourceCode(SourceCode code)
        throws MissingConstructCorrectnessDataException
    {
        Assert.require(code != null);

        delegate().setSourceCode(code);
    }

    /**
        @see Construct#sourceCode
    */
    public SourceCode sourceCode()
        throws MissingConstructCorrectnessDataException
    {
        return delegate().sourceCode();
    }


    // Protected methods

    /**
        @return the construct that we delegate to
    */
    protected Construct delegate()
    {
        Assert.ensure(_delegate != null);
        return _delegate;
    }
}
