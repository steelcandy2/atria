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

package com.steelcandy.plack.atria.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.shared.source.DefaultSourceLocationFactoryBase;

/**
    The singleton factory class used to create SourceLocations for Atria
    tokens, constructs, etc.

    @author  James MacKay
*/
public class AtriaSourceLocationFactory
    extends DefaultSourceLocationFactoryBase
{
    // Constants

    /** The single instance of this class. */
    private static final AtriaSourceLocationFactory
        _instance = new AtriaSourceLocationFactory();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static AtriaSourceLocationFactory instance()
    {
        return _instance;
    }

    /**
        Constructs a AtriaSourceLocationFactory.
        <p>
        This constructor should only be used to construct the single instance
        of this class.
    */
    private AtriaSourceLocationFactory()
    {
        // empty
    }
}
