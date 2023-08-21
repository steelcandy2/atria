/*
 Copyright (C) 2014-2015 by James MacKay.

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

import com.steelcandy.plack.atria.constructs.AtriaConstructManager;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.semantic.AbstractNameChecker;

import com.steelcandy.common.Resources;
import com.steelcandy.common.text.*;

/**
    A singleton class that determines the validity of names in Atria message
    catalogs.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class AtriaMessageCatalogNameChecker
    extends AbstractNameChecker
{
    // Constants

    /** The single instance of this class. */
    private static final AtriaMessageCatalogNameChecker
        _instance = new AtriaMessageCatalogNameChecker();

    /**
        The separator between components in a module or package name.

        Note: this is the separator in module or package name that appears
        in an Atria message catalog: it may or may not correspond to the
        separator used in any source code generated from such a catalog.
    */
    private static final String
        MODULE_NAME_COMPONENT_SEPARATOR = ".";


    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaMessageCatalogBuilderResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        MESSAGE_ELEMENT_NAME_START_NOT_LOWERCASE_MSG =
            "MESSAGE_ELEMENT_NAME_START_NOT_LOWERCASE_MSG",
        MESSAGE_ELEMENT_NAME_NON_ALPHANUMERIC_MSG =
            "MESSAGE_ELEMENT_NAME_NON_ALPHANUMERIC_MSG",
        MODULE_NAME_STARTS_WITH_SEPARATOR_MSG =
            "MODULE_NAME_STARTS_WITH_SEPARATOR_MSG",
        MODULE_NAME_ENDS_WITH_SEPARATOR_MSG =
            "MODULE_NAME_ENDS_WITH_SEPARATOR_MSG",
        EMPTY_MODULE_NAME_COMPONENT_MSG =
            "EMPTY_MODULE_NAME_COMPONENT_MSG",
        NON_LOWERCASE_IN_MODULE_NAME_MSG =
            "NON_LOWERCASE_IN_MODULE_NAME_MSG",
        CLASS_NAME_TOO_SHORT_MSG =
            "CLASS_NAME_TOO_SHORT_MSG",
        CLASS_NAME_START_NOT_UPPERCASE_MSG =
            "CLASS_NAME_START_NOT_UPPERCASE_MSG",
        CLASS_NAME_SECOND_NOT_LOWERCASE_MSG =
            "CLASS_NAME_SECOND_NOT_LOWERCASE_MSG",
        CLASS_NAME_NON_ALPHANUMERIC_MSG =
            "CLASS_NAME_NON_ALPHANUMERIC_MSG";


    // Constructors

    /**
        @return the single instance of this class
    */
    public static AtriaMessageCatalogNameChecker instance()
    {
        return _instance;
    }

    /**
        This constructor should only be used to construct the single instance
        of this class.

        @see #instance
    */
    private AtriaMessageCatalogNameChecker()
    {
        // empty
    }


    // Public methods

    /**
        @param name a name
        @return null if 'name' is a valid name for a message element in an
        Atria message catalog, and an error message fragment describing why
        it's invalid otherwise
    */
    public String checkValidMessageElementName(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        String result = null;

        if (isFirstLowercase(name) == false)
        {
            result = MESSAGE_ELEMENT_NAME_START_NOT_LOWERCASE_MSG;
        }
        else if (isRestAlphanumeric(name, 1) == false)
        {
            result = MESSAGE_ELEMENT_NAME_NON_ALPHANUMERIC_MSG;
        }
        result = messageWithKey(result);

        // 'result' may be null
        return result;
    }


    /**
        @param name a name
        @return null iff 'name' is a valid name for a module name specified
        in an Atria message catalog, and an error message fragment describing
        why it's invalid otherwise
    */
    public String checkValidModuleName(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        String result;

        final String sep = MODULE_NAME_COMPONENT_SEPARATOR;
        if (name.startsWith(sep))
        {
            result = _resources.
                getMessage(MODULE_NAME_STARTS_WITH_SEPARATOR_MSG, sep);
            Assert.check(result != null);
        }
        else if (name.endsWith(sep))
        {
            result = _resources.
                getMessage(MODULE_NAME_ENDS_WITH_SEPARATOR_MSG, sep);
            Assert.check(result != null);
        }
        else
        {
            result = null;
            StringIterator iter = TextUtilities.split(name, sep).iterator();
            Assert.check(iter.hasNext());  // since name.length() > 0
            while (result == null && iter.hasNext())
            {
                String comp = iter.next();
                if (comp.length() == 0)
                {
                    result = _resources.
                        getMessage(EMPTY_MODULE_NAME_COMPONENT_MSG);
                    Assert.check(result != null);
                }
                else if (isAllLowercase(comp) == false)
                {
                    result = _resources.
                        getMessage(NON_LOWERCASE_IN_MODULE_NAME_MSG);
                    Assert.check(result != null);
                }
            }
        }

        // 'result' may be null
        return result;
    }

    /**
        @param name a name
        @return null iff 'name' is a valid name for an (unqualified) class
        name specified in an Atria message catalog, and an error message
        fragment describing why it's invalid otherwise
    */
    public String checkValidClassName(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        String result = null;

        if (hasMinimumLength(name, 2) == false)
        {
            result = CLASS_NAME_TOO_SHORT_MSG;
        }
        else if (isFirstUppercase(name) == false)
        {
            result = CLASS_NAME_START_NOT_UPPERCASE_MSG;
        }
        else if (isSecondLowercase(name) == false)
        {
            result = CLASS_NAME_SECOND_NOT_LOWERCASE_MSG;
        }
        else if (isRestAlphanumeric(name, 2) == false)
        {
            result = CLASS_NAME_NON_ALPHANUMERIC_MSG;
        }
        result = messageWithKey(result);

        // 'result' may be null
        return result;
    }


    // Protected methods

    /**
        @param key a message key, or null
        @return null if 'key' is null, and the zero-argument message whose
        resource key is 'key' otherwise
    */
    protected String messageWithKey(String key)
    {
        // 'key' can be null

        String result;

        if (key != null)
        {
            result = _resources.getMessage(key);
        }
        else
        {
            result = null;
        }

        Assert.ensure((result == null) == (key == null));
        return result;
    }


    /**
        @see AbstractNameChecker#description(Construct)
    */
    protected String description(Construct c)
    {
        Assert.require(c != null);

        return AtriaConstructManager.instance().idToDescription(c.id());
    }

    /**
        @see AbstractNameChecker#createErrorKey(int)
    */
    protected ErrorKey createErrorKey(int errorKeyId)
    {
        // Assert.require(errorKeyId != CommonErrorKeyIds.NON_ERROR_KEY_ID);

        Assert.unreachable();
            // since we don't create any errors

        // 'result' may be null
        return null;
    }
}
