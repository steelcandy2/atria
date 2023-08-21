/*
 Copyright (C) 2003-2005 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

/**
    An abstract base class for simple PlackErrors: errors that only have a
    level and a description.
    <p>
    Subclasses just have to implement <code>level()</code>.

    @author James MacKay
    @see PlackError#level
*/
public abstract class AbstractSimplePlackError
    extends MinimalAbstractPlackError
{
    // Private fields

    /** This error's description. */
    private String _description;


    // Constructors

    /**
        Constructs a AbstractSimplePlackError.

        @param description the error's description
    */
    public AbstractSimplePlackError(String description)
    {
        Assert.require(description != null);

        _description = description;
    }


    // Public methods

    /**
        @see PlackError#description
    */
    public String description()
    {
        Assert.ensure(_description != null);
        return _description;
    }

    /**
        @see PlackError#sourceCode
    */
    public SourceCode sourceCode()
    {
        return null;
    }

    /**
        @see PlackError#location
    */
    public SourceLocation location()
    {
        return null;
    }
}
