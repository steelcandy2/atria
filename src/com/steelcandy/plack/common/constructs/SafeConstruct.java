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

/**
    Represents a Construct that is safe, in that it can't be (directly) cast
    to a (useful) supertype.

    @author James MacKay
    @version $Revision: 1.1 $
    @see Construct#safe
    @see Construct#toUnsafeConstruct
*/
public class SafeConstruct
    extends DefaultDelegatingConstruct
{
    // Constructors

    /**
        Constructs a SafeConstruct from the (likely) unsafe construct that it
        is to prevent from being cast.

        @param c the construct to be made safe
    */
    public SafeConstruct(Construct c)
    {
        super(c);
        Assert.require(c != null);
    }


    // Public methods

    /**
        @see Construct#safe
    */
    public SafeConstruct safe()
    {
        // Assert.ensure(result != null);
        return this;
    }

    /**
        @see Construct#toUnsafeConstruct
    */
    public Construct toUnsafeConstruct()
    {
        Construct result = delegate();

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see Construct#deepClone
    */
    public Construct deepClone()
    {
        Construct result = new SafeConstruct(delegate().deepClone());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#shallowClone
    */
    public Construct shallowClone()
    {
        Construct result = new SafeConstruct(delegate().shallowClone());

        Assert.ensure(result != null);
        return result;
    }
}
