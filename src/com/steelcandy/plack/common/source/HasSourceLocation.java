/*
 Copyright (C) 2001-2004 by James MacKay.

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

/**
    The interface implemented by all classes that represent a fragment of
    source code, and that know the location of that fragment in the piece of
    source code in which it is contained.

    @author James MacKay
    @version $Revision: 1.3 $
    @see SourceLocation
*/
public interface HasSourceLocation
{
    // Public methods

    /**
        @return the location in the source code of the source code fragment
        that this object represents
    */
    public SourceLocation location();
        // Assert.ensure(result != null);
}
