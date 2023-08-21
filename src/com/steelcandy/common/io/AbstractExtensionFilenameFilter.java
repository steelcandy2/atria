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

package com.steelcandy.common.io;

import com.steelcandy.common.debug.Assert;

import java.io.File;
import java.io.FilenameFilter;

/**
    An abstract base class for FilenameFilters that select pathnames whose
    base names end with a specific extension.

    @author  James MacKay
*/
public abstract class AbstractExtensionFilenameFilter
    implements FilenameFilter
{
    // Public methods

    /**
        @see FilenameFilter#accept(File, String)
    */
    public boolean accept(File dir, String name)
    {
        return (name != null) && name.endsWith(extension());
    }


    // Abstract methods

    /**
        @return the extension - including the leading '.' - that a file's
        basename must end with in order for it to be accepted by this filter
    */
    protected abstract String extension();
        // Assert.ensure(result != null);
}
