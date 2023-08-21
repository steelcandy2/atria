/*
 Copyright (C) 2008 by James MacKay.

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

package com.steelcandy.plack.common.base;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by classes that represent all of the data
    about the top-most/main Externalizable in a file or other data source.

    @author  James MacKay
*/
public interface ExternalData
{
    // Public methods

    /**
        @return the element that contains all of the information about the
        top-most Externalizable whose information we contain
        @exception ExternalizingException is thrown iff the element can't
        be obtained (possibly because the information is missing or
        malformed)
    */
    public ExternalDataElement topElement();
}
