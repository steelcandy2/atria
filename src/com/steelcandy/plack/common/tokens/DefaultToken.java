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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocation;

/**
    A simple implementation of the Token interface. Each instance stores a
    copy of its information (ID, flags and source location) so it is not the
    most space-efficient implementation, but it can help avoid a proliferation
    of specialized Token subclasses.
    <p>
    By default tokens that are direct instances of this class have no values
    associated with them, but the appropriate methods can be overridden by
    subclasses to add them.
    <p>
    This class isn't subclassed very often: in most cases the AbstractToken
    class should be subclassed directly instead.

    @author James MacKay
    @see AbstractToken
*/
public class DefaultToken extends AbstractToken
{
    // Private fields

    /** This token's ID. */
    private int _id;

    /** This token's flags. */
    private int _flags;


    // Constructors

    /**
        Constructs a DefaultToken with no flags set.

        @param id the token's ID
        @param location the location in the source code of the fragment that
        the token represents
    */
    public DefaultToken(int id, SourceLocation location)
    {
        this(id, 0, location);
    }

    /**
        Constructs a DefaultToken.

        @param id the token's ID
        @param flags the flags that the token has set, bitwise ORed together
        @param location the location in the source code of the fragment that
        the token represents
    */
    public DefaultToken(int id, int flags, SourceLocation location)
    {
        super(location);
        _id = id;
        _flags = flags;
    }


    // Implemented/overridden AbstractToken methods

    /**
        @see Token#cloneToken
    */
    public Token cloneToken(SourceLocation loc)
    {
        return new DefaultToken(id(), _flags, loc);
    }

    /**
        @see Token#id
    */
    public int id()
    {
        return _id;
    }

    /**
        @see AbstractToken#isFlagSet
    */
    public boolean isFlagSet(int flag)
    {
        return (_flags & flag) != 0;
    }


    // Protected methods

    /**
        @return this token's flags
    */
    protected int flags()
    {
        return _flags;
    }
}
