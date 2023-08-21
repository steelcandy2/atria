/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by classes that listen passively for tokens.

    @author  James MacKay
    @version $Revision$
*/
public interface TokenListener
{
    // Public methods

    /**
        Accepts the specified token as one that this listener was
        listening for.

        @param tok a token that this listener was listening for
    */
    public void accept(Token tok);
        // Assert.require(tok != null);
}
