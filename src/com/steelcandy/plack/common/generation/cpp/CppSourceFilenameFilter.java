/*
 Copyright (C) 2009 by James MacKay.

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

package com.steelcandy.plack.common.generation.cpp;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.AbstractExtensionFilenameFilter;

/**
    A singleton FilenameFilter that only accepts C++ source files.

    @author  James MacKay
    @see CommonCppGeneratorBase#CPP_SOURCE_FILE_EXT
*/
public class CppSourceFilenameFilter
    extends AbstractExtensionFilenameFilter
{
    // Constants

    /** The single instance of this class. */
    private static final CppSourceFilenameFilter
        _instance = new CppSourceFilenameFilter();


    // Constructors.

    /**
        @return the single instance of this class
    */
    public static CppSourceFilenameFilter instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs our sole instance.
    */
    private CppSourceFilenameFilter()
    {
        // empty
    }


    // Protected methods

    /**
        @see AbstractExtensionFilenameFilter#extension
    */
    protected String extension()
    {
        // Assert.ensure(result != null);
        return CommonCppGeneratorBase.CPP_SOURCE_FILE_EXT;
    }
}
