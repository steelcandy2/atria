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

package com.steelcandy.plack.atria.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.plack.common.source.SourceLocation;

/**
    The base class for token classes that represent fragments of
    source code that are not considered programming language
    fragments, such as comments and whitespace.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public abstract class AtriaNonCodeToken
    extends AtriaAbstractToken
{
    // Constants

    /** The flags that all non-code tokens have set. */
    protected static final int
        _nonCodeFlags = AtriaTokenManager.IS_NON_CODE;


    // Constructors

    /**
        Constructs an AtriaNonCodeToken.

        @param loc the location in the source code of the source
        fragment that the token represents
    */
    public AtriaNonCodeToken(SourceLocation loc)
    {
        super(loc);
    }


    // Overridden/implemented methods

    /**
        @see AbstractToken#isFlagSet
    */
    public boolean isFlagSet(int flag)
    {
        return (_nonCodeFlags & flag) != 0;
    }
}
