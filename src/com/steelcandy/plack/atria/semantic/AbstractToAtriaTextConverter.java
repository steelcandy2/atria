/*
 Copyright (C) 2023 by James MacKay.

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

package com.steelcandy.plack.atria.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;

import java.io.IOException;

/**
    An abstract base class for classes that convert arbitrary text into the
    Atria representation of that text.

    @author James MacKay
*/
public abstract class AbstractToAtriaTextConverter
    extends MinimalAbstractToAtriaTextConverter
{
    // Protected methods

    /**
        @see MinimalAbstractToAtriaTextConverter#convertDoubleQuote(char)
    */
    protected void convertDoubleQuote(char ch)
        throws IOException
    {
        Assert.require(ch == DOUBLE_QUOTE_CHAR);

        convertSpecialCharacter(ch);
    }

    /**
        @see MinimalAbstractToAtriaTextConverter#convertNewline(char)
    */
    protected void convertNewline(char ch)
        throws IOException
    {
        Assert.require(ch == NEWLINE_CHAR);

        convertSpecialCharacter(ch);
    }


    // Protected abstract methods

    /**
        Converts the specified special character.

        @param ch the special character to convert
        @exception IOException thrown if an I/O error occurs as part of
        the conversion
    */
    protected abstract void convertSpecialCharacter(char ch)
        throws IOException;
}
