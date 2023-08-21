/*
 Copyright (C) 2006 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    A singleton DelayedTextWriter that doesn't write anything.

    @author  James MacKay
    @see NullDelayedTextWriter
*/
public class NullDelayedTextWriter
    extends DelayedTextWriter
{
    // Constants

    /** The single instance of this class. */
    private static final NullDelayedTextWriter
        _instance = new NullDelayedTextWriter();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static NullDelayedTextWriter instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs the single instance of this class
    */
    private NullDelayedTextWriter()
    {
        // empty
    }


    // Protected methods

    /**
        @see DelayedTextWriter#writeTextNow(IndentWriter)
    */
    protected void writeTextNow(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        // empty
    }
}
