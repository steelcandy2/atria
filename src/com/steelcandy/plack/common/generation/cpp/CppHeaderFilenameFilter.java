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
    A singleton FilenameFilter that only accepts C++ header files.

    @author  James MacKay
    @version $Revision: 1.1 $
    @see CommonCppGeneratorBase#CPP_HEADER_FILE_EXT
*/
public class CppHeaderFilenameFilter
    extends AbstractExtensionFilenameFilter
{
    // Constants

    /** The single instance of this class. */
    private static final CppHeaderFilenameFilter
        _instance = new CppHeaderFilenameFilter();


    // Constructors.

    /**
        @return the single instance of this class
    */
    public static CppHeaderFilenameFilter instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs our sole instance.
    */
    private CppHeaderFilenameFilter()
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
        return CommonCppGeneratorBase.CPP_HEADER_FILE_EXT;
    }
}
