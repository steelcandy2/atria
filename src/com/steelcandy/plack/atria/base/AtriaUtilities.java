/*
 Copyright (C) 2014 by James MacKay.

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

package com.steelcandy.plack.atria.base;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    A class that contains Atria-related utility methods that don't belong
    anywhere else.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaUtilities
{
    // Constructors

    /**
        Constructs an AtriaUtilities object.
        <p>
        This class should never be instantiated: all of its methods should
        be static.
    */
    protected AtriaUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        Writes out to 'w' the Atria ExpressionItems that correspond to 'txt'.
        (Exactly one ExpressionItem will be written out if 'txt' doesn't
        contain any newlines or double quotation marks.)

        @param txt the text to write out as Atria ExpressionItems
        @param w the writer to use to write out the ExpressionItems
        @exception IOException thrown if an I/O error occurs while writing to
        'w'
    */
    public static void
        writeStringAsExpressionItems(String txt, IndentWriter w)
        throws IOException
    {
        Assert.require(txt != null);
        Assert.require(w != null);

        final char quote = AtriaInfo.QUOTE_CHAR;
        final char nl = '\n';

        int sz = txt.length();
        boolean isOpenText = false;
        if (sz > 0)
        {
            for (int i = 0; i < sz; i++)
            {
                char ch = txt.charAt(i);
                if (ch == quote || ch == nl)
                {
                    if (isOpenText)
                    {
                        w.write(quote);
                        w.write(nl);
                        isOpenText = false;
                    }
                    w.write("[");
                    w.write((ch == nl) ? AtriaInfo.NEWLINE_COMMAND_NAME :
                                         AtriaInfo.QUOTE_COMMAND_NAME);
                    w.write("]");
                    w.write(nl);
                }
                else
                {
                    if (isOpenText == false)
                    {
                        w.write(quote);
                        isOpenText = true;
                    }
                    w.write(ch);
                }
            }  // for (int i = 0; ...)
            if (isOpenText)
            {
                w.write(quote);
                w.write(nl);
            }
        }  // if (sz > 0)
    }
}
