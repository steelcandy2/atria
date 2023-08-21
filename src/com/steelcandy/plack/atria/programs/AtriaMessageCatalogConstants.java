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

/**
    The interface that contains the definitions of constants useful to
    several message catalog-related classes.

    @author  James MacKay
*/
public interface AtriaMessageCatalogConstants
{
    // Constants

    /** The root element name. */
    public static final String
        ROOT_ELEMENT_NAME = "messages";

    /** The valid root element attribute names. */
    public static final String
        LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME = "locale",
        STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME = "style",
        MODULE_ROOT_ELEMENT_ATTRIBUTE_NAME = "module",
        NAME_ROOT_ELEMENT_ATTRIBUTE_NAME = "name",
        LOCALIZABLE_ROOT_ELEMENT_ATTRIBUTE_NAME = "localizable",
        COMPACT_ROOT_ELEMENT_ATTRIBUTE_NAME = "compact";

    /** Valid boolean attribute values. */
    public static final String
        TRUE_VALUE_TEXT = "true",
        FALSE_VALUE_TEXT = "false";

    /**
        The valid full and compact names for the attribute of a message
        element that specifies the number of arguments that the message
        accepts.
    */
    public static final String
        NUMBER_OF_ARGS_ATTRIBUTE_NAME = "args",
        COMPACT_NUMBER_OF_ARGS_ATTRIBUTE_NAME = "n";

    /**
        Valid full and compact names for a child element of a message
        element.
    */
    public static final String
        ARG_ELEMENT_NAME = "arg",
        COMPACT_ARG_ELEMENT_NAME = "a",
        QUOTED_ARG_ELEMENT_NAME = "quoted-arg",
        COMPACT_QUOTED_ARG_ELEMENT_NAME = "q";

    /**
        The valid full and compact names for an 'arg' or 'quoted-arg'
        element's index attribute.
    */
    public static final String
        ARG_INDEX_ATTRIBUTE_NAME = "index",
        COMPACT_ARG_INDEX_ATTRIBUTE_NAME = "i";
}
