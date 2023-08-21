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

package com.steelcandy.plack.shared.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.AbstractSourceLocationFactory;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;  // javadocs only
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.source.SourcePosition;

/**
    The singleton factory class used, by default, to create SourceLocations.
    <p>
    Different SourceLocationFactories may need to be used for languages with
    weirder requirements on source locations.

    @author James MacKay
    @see SourceLocation
*/
public class DefaultSourceLocationFactory
    extends DefaultSourceLocationFactoryBase
{
    // Constants

    /** The single instance of this class. */
    private static final DefaultSourceLocationFactory
        _instance = new DefaultSourceLocationFactory();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static DefaultSourceLocationFactory instance()
    {
        return _instance;
    }

    /**
        Constructs a DefaultSourceLocationFactory.
        <p>
        This constructor should only be used to construct the single instance
        of this class.
    */
    private DefaultSourceLocationFactory()
    {
        // empty
    }
}
