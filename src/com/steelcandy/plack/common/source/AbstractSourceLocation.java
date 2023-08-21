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

import com.steelcandy.common.creation.ReflectionUtilities;

/**
    An abstract base clas for SourceLocations.

    @author James MacKay
*/
public abstract class AbstractSourceLocation
    implements SourceLocation
{
    // Public methods

    /**
        Note: subclasses that are made up of sublocations should override
        this implementation.

        @see SourceLocation#basicComponents
    */
    public SourceLocationList basicComponents()
    {
        SourceLocationList result =
            SourceLocationList.createSingleItemList(this);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @return our class' name in the format to be used in subclass'
        implementations of toString()
        @see Object#toString
    */
    protected String classNameToString()
    {
        String result =
            ReflectionUtilities.getUnqualifiedClassName(getClass());

        Assert.ensure(result != null);
        return result;
    }
}
