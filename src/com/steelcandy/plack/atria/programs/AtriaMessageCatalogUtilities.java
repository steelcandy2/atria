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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaUtilities;
import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    A class that contains message catalog-related methods that don't belong
    anywhere else, or are used by more than one class that can't extend
    another class (usually because there's already one that it has to
    extend.)

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class AtriaMessageCatalogUtilities
    extends AtriaUtilities
    implements AtriaMessageCatalogConstants
{
    // Constructors

    /**
        Constructs an AtriaMessageCatalogUtilities object.
        <p>
        This class should never be instantiated: all of its methods should
        be static.
    */
    private AtriaMessageCatalogUtilities()
    {
        // empty
    }


    // Public static methods

    /**
        Parses and returns the value of the specified Atria attribute if
        it is a literal boolean value, and returns null otherwise.

        @param c the attribute whose value is to be parsed
        @return the value of 'c' if it is a literal boolean, and null
        otherwise
        @see Boolean#booleanValue
    */
    public static Boolean
        parseBooleanAttributeValue(AtriaConstructManager.Attribute c)
    {
        Assert.require(c != null);

        Boolean result = null;

        String value = parseTextAttributeValue(c);
        if (value != null)
        {
            if (TRUE_VALUE_TEXT.equals(value))
            {
                result = Boolean.TRUE;
            }
            else if (FALSE_VALUE_TEXT.equals(value))
            {
                result = Boolean.FALSE;
            }
        }

        // 'result' may be null
        return result;
    }

    /**
        Parses and returns the value of the specified Atria attribute if
        it is a literal non-negative integer, and returns -1 otherwise.

        @param c the attribute whose value is to be parsed
        @return the value of 'c' if it is a literal non-negative integer, and
        -1 otherwise
    */
    public static int
        parseNonnegativeIntegerAttributeValue(AtriaConstructManager.
                                                            Attribute c)
    {
        Assert.require(c != null);

        int result = -1;

        String value = parseTextAttributeValue(c);
        if (value != null)
        {
            try
            {
                result = Integer.parseInt(value);
                if (result < 0)
                {
                    result = -1;
                }
            }
            catch (NumberFormatException ex)
            {
                // empty - just return -1
            }
        }

        Assert.ensure(result >= -1);
        return result;
    }

    /**
        Parses and returns the text that is the value of the specified
        attribute if it is a Text construct, and returns null otherwise.

        @param c the attribute whose value is to be parsed
        @return the text that is the value of 'c', or null if that value
        is not a Text construct
    */
    public static String parseTextAttributeValue(AtriaConstructManager.
                                                            Attribute c)
    {
        Assert.require(c != null);

        String result = null;

        if (c.hasExpression())
        {
            AtriaConstructManager.Expression expr = c.expression();
            if (expr instanceof AtriaConstructManager.Text)
            {
                AtriaConstructManager.Text t =
                    (AtriaConstructManager.Text) expr;
                Assert.check(t.token().hasStringValue());
                result = t.token().stringValue();
                Assert.check(result != null);
            }
        }

        // 'result' may be null
        return result;
    }
}
