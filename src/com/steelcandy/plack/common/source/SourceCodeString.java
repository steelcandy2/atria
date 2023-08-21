/*
 Copyright (C) 2001-2005 by James MacKay.

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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;
import com.steelcandy.common.Utilities;

import java.io.*;

/**
    Represents a string of source code.
    <p>
    Note: in general instances should be constructed using a
    SourceCodeFactory instead of calling this class' constructors directly.

    @author James MacKay
    @version $Revision: 1.11 $
*/
public class SourceCodeString
    extends AbstractSourceCode
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        SourceCodeResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String DEFAULT_SOURCE_CODE_STRING_NAME =
        "DEFAULT_SOURCE_CODE_STRING_NAME";


    /** The default name for a string of source code. */
    protected static final String DEFAULT_SOURCE_NAME =
        _resources.getMessage(DEFAULT_SOURCE_CODE_STRING_NAME);


    // Private fields

    /** The string of source code. */
    private String _sourceString;

    /** The full name associated with this string of source code. */
    private String _fullName;

    /**
        The "short" name associated with this string of source code,
        or null if its full name should also be used as its short name.

        @see SourceCode#name
    */
    private String _name;


    // Constructors

    /**
        Constructs a SourceCodeString from the string of source code.

        @param sourceString the string of source code
    */
    public SourceCodeString(String sourceString)
    {
        this(sourceString, DEFAULT_SOURCE_NAME);
    }

    /**
        Constructs a SourceCodeString from the string of source code and the
        full name to associate with that source code.

        @param sourceString the string of source code
        @param fullName the full name to associate with the source code
    */
    public SourceCodeString(String sourceString, String fullName)
    {
        Assert.require(sourceString != null);
        Assert.require(fullName != null);

        _fullName = fullName;
        _sourceString = sourceString;
    }

    /**
        Constructs a SourceCodeString from the string of source code and the
        names to associate with that source code.

        @param sourceString the string of source code
        @param fullName the full name to associate with the source code
        @param shortName the "short" name to associate with the source code
    */
    public SourceCodeString(String sourceString, String fullName,
                            String shortName)
    {
        Assert.require(sourceString != null);
        Assert.require(fullName != null);
        Assert.require(shortName != null);

        _fullName = fullName;
        _name = shortName;
        _sourceString = sourceString;
    }


    // Overridden methods

    /**
        @see SourceCode#fullName
    */
    public String fullName()
    {
        Assert.ensure(_fullName != null);
        return _fullName;
    }

    /**
        @see SourceCode#name
    */
    public String name()
    {
        String result = _name;

        if (result == null)
        {
            result = fullName();
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see SourceCode#toSourceCodeString
    */
    public SourceCodeString toSourceCodeString()
    {
        return this;
    }

    /**
        @see SourceCode#toSourceCodeString(long)
    */
    public SourceCode toSourceCodeString(long maxSize)
    {
        Assert.require(maxSize >= 0);

        // Assert.ensure(this != null);
        return this;
    }


    // Protected methods

    /**
        @see AbstractSourceCode#createSourceReader
    */
    protected SourceCodeReader createSourceReader()
        throws IOException
    {
        SourceCodeReader result = new SourceCodeReader(_sourceString);

        Assert.ensure(result != null);
        return result;
    }
}
