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
    An abstract base class for Atria tokens.

    @author James MacKay
    @version $Revision: 1.10 $
*/
public abstract class AtriaAbstractToken
    extends AbstractToken
{
    // Constructors

    /**
        Constructs an AtriaAbstractToken.

        @param loc the location in the source code of the source fragment
        that the token represents
    */
    public AtriaAbstractToken(SourceLocation loc)
    {
        super(loc);
    }


    // Public methods

    /**
        @see AbstractToken#toString
    */
    public String toString()
    {
        StringBuffer result = new StringBuffer(super.toString());
        String flags = flagsToConstantNames();
        if (flags.length() > 0)
        {
            result.append("; ").append(flags);
        }
        return result.toString();
    }

    /**
        @return a comma-separated list of the token flag constants
        corresponding to the token flags that this token has set
    */
    protected String flagsToConstantNames()
    {
        return AtriaTokenManager.instance().flagsToConstantNames(this);
    }
}
