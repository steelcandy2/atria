/*
 Copyright (C) 2004-2009 by James MacKay.

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

package com.steelcandy.plack.common.generation.java;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by classes that generate target code that is
    Java source code, including CodeGenerators and Subgenerators.

    @author  James MacKay
    @version $Revision: 1.4 $
*/
public interface CommonJavaGeneratorBase
{
    // Constants

    /** The arguments for a call of a zero-argument Java method. */
    public static final String EMPTY_ARGUMENTS = "()";

    /**
        The separator used to separate the components in Java package names.
    */
    public static final String PACKAGE_NAME_COMPONENT_SEPARATOR = ".";


    /** The extension to use on all generated Java source code files. */
    public static final String EXTENSION = ".java";

    /**
        The subdirectory under the object code base directory to put
        generated Java source target code under. (This leaves a place under
        the object code base directory for subdirectories that can hold the
        .class files resulting from compiling the Java source code, for
        example.)

        @see #CLASSES_SUBDIR
    */
    public static final String SOURCE_SUBDIR = "src";

    /**
        The subdirectory under the object code base directory to put the
        .class files resulting from compiling the Java source code target
        code.

        @see #SOURCE_SUBDIR
    */
    public static final String CLASSES_SUBDIR = "classes";
}
