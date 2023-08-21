/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common;

import com.steelcandy.common.debug.Assert;

/**
    The base class for classes that are used to locate a
    Resources object.
    <p>
    Subclasses usually define a Resources public constant
    named <code>resources</code>. This constant's value is
    the Resources resulting from calling createResources()
    with the subclass' Class object: the name used to create
    the Resources is the class' name with the SUFFIX
    removed from it. (Thus all subclasses' names must end
    in the SUFFIX.)

    @author James MacKay
    @version $Revision: 1.1 $
    @see #SUFFIX
    @see Resources
*/
public class ResourcesLocator
{
    // Constants

    /** The suffix that all subclasses' names must end with. */
    protected static final String SUFFIX = "Locator";


    // Static methods

    /**
        Creates the Resources for the ResourcesLocator
        subclass with the specified Class object.

        @param c the ResourceLocator's Class object
        @return the ResourceLocator's Resources
    */
    protected static Resources createResources(Class c)
    {
        Assert.require(c.getName().endsWith(SUFFIX));

        // The name passed to the Resources is the fully-
        // qualified class name with the SUFFIX removed from it.
        String name = c.getName();
        name = name.substring(0, name.length() - SUFFIX.length());
        return Resources.getResources(name);
    }
}
