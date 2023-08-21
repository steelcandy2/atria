/*
 Copyright (C) 2002-2004 by James MacKay.

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
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;

/**
    A class that contains the information about a subconstruct that is being
    or was just parsed.

    @author James MacKay
*/
public class SubconstructParsingData
{
    // Private fields

    /** Indicates whether the subconstruct was parsed successfully. */
    private boolean _wasParsedSuccessfully;

    /**
        The location in the source code of the previous construct part. It
        should never be null.
    */
    private SourceLocation _previousLocation;

    /** The factory to use to create source locations. */
    private SourceLocationFactory _locationFactory;


    // Constructors

    /**
        Constructs a SubconstructParsingData object.
        <p>
        Initially the subconstruct is assumed to have been parsed
        successfully.

        @param locationFactory the factory that the parsing data can use to
        create source locations
        @see #setWasParsedSuccessfully(boolean)
    */
    public SubconstructParsingData(SourceLocationFactory locationFactory)
    {
        Assert.require(locationFactory != null);

        _locationFactory = locationFactory;
        reset();
    }


    // Public methods

    /**
        Sets whether the subconstruct was successfully parsed.

        @param wasSuccessful true to set that the subconstruct was parsed
        successfully, or false to set that the subconstruct was not parsed
        successfully
    */
    public void setWasParsedSuccessfully(boolean wasSuccessful)
    {
        _wasParsedSuccessfully = wasSuccessful;
    }

    /**
        Indicates whether the subconstruct was parsed successfully.
        <p>
        A subconstruct is parsed successfully if no errors occurred.

        @return true if the subconstruct was parsed successfully, and false
        if it wasn't
    */
    public boolean wasParsedSuccessfully()
    {
        return _wasParsedSuccessfully;
    }


    /**
        Sets the previous construct part to be the specified part.

        @param part the new previous construct part
    */
    public void setPreviousPart(HasSourceLocation part)
    {
        Assert.require(part != null);

        SourceLocation loc = part.location();
        if (loc != null)
        {
            setPreviousLocation(loc);
        }
    }

    /**
        Sets the location of the previous construct part to the specified
        location.

        @param loc the new location of the previous construct part
    */
    public void setPreviousLocation(SourceLocation loc)
    {
        Assert.require(loc != null);

        _previousLocation = loc;
    }

    /**
        @return the location in the source code of the previous construct
        part, or null if there was no previous construct part
    */
    public SourceLocation previousLocation()
    {
        Assert.ensure(_previousLocation != null);
        return _previousLocation;
    }

    /**
        Resets this object so that it is ready to be reused.
    */
    public void reset()
    {
        _wasParsedSuccessfully = true;
        _previousLocation = _locationFactory.create(1, 0, 0);
    }
}
