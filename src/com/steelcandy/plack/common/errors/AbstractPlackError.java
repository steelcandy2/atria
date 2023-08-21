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
    An abstract base class for PlackErrors. By default they have no keys.

    @author James MacKay
    @version $Revision: 1.5 $
    @see AbstractKeyedPlackError
*/
public abstract class AbstractPlackError
    extends MinimalAbstractPlackError
{
    // Private fields

    /**
        The severity level of this error. It should be one of the *_LEVEL
        constants.
    */
    private int _level;

    /** A description of this error. */
    private String _description;

    /**
        The piece of source code containing the error, or null if the piece
        of source code containing the error is not known.
    */
    private SourceCode _code;

    /**
        The location in the source code where the error occurred, or null if
        the error doesn't have a location associated with it.
    */
    private SourceLocation _location;


    // Constructors

    /**
        Constructs an AbstractPlackError.

        @param level the error's severity level
        @param description a description of the error
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
    */
    public AbstractPlackError(int level, String description,
                              SourceCode code, SourceLocation loc)
    {
        Assert.require(description != null);
        // 'code' and/or 'loc' can be null

        setLevel(level);
        setDescription(description);
        setSourceCode(code);
        setLocation(loc);
    }


    // Public methods

    /**
        @see PlackError#level
    */
    public int level()
    {
        return _level;
    }

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
        // 'result' may be null
        return _code;
    }

    /**
        @see PlackError#location
    */
    public SourceLocation location()
    {
        // 'result' may be null
        return _location;
    }


    /**
        Sets this error's severity level to the one specified.

        @param newLevel this error's new severity level
    */
    public void setLevel(int newLevel)
    {
        Assert.require(newLevel >= MINIMUM_ERROR_SEVERITY_LEVEL);
        Assert.require(newLevel <= MAXIMUM_ERROR_SEVERITY_LEVEL);

        _level = newLevel;
    }

    /**
        Sets this error's description to the one specified.

        @param newDescription this error's new description
    */
    public void setDescription(String newDescription)
    {
        Assert.require(newDescription != null);

        _description = newDescription;
    }

    /**
        Sets the piece of source code that contains this error to the
        specified piece of source code.

        @param sourceCode the piece of source code containing this error
    */
    public void setSourceCode(SourceCode sourceCode)
    {
        // As described above, the source code can be null.

        _code = sourceCode;
    }

    /**
        Sets the location in the source code where this error occurred to the
        specified location.

        @param newLocation the new location where this error occurred
    */
    public void setLocation(SourceLocation newLocation)
    {
        // As described above, the location can be null.

        _location = newLocation;
    }
}
