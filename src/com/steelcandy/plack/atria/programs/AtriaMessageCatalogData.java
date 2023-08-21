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

/**
    Represents data about a message catalog.

    @author  James MacKay
*/
public class AtriaMessageCatalogData
    implements AtriaMessageCatalogConstants
{
    // Private fields

    /**
        The name of the module or package in which will be defined the class
        (families) that will represent our message catalog, or null if it
        hasn't been set yet.
    */
    private String _moduleName;

    /**
        The name of the class (families) that will represent our message
        catalog, or null if it hasn't been set yet.
    */
    private String _className;

    /**
        The name of the locale that our message catalog is for, or null if it
        hasn't been set (yet).
        <p>
        Note: we allow both our locale name and style name to be set, even
        though specifying both in a message catalog is illegal.
    */
    private String _localeName;

    /**
        The name of the style that our message catalog is for, or null if it
        hasn't been set (yet).
        <p>
        Note: we allow both our locale name and style name to be set, even
        though specifying both in a message catalog is illegal.
    */
    private String _styleName;

    /**
        Indicates whether our message catalog's messages can be localized.
    */
    private boolean _isLocalizable;

    /**
        Indicates whether compact element and attribute names are used in
        the catalog we're processing. By default a catalog is NOT compact.
        <p>
        Note: the root element and its attributes are the same regardless of
        whether a catalog is compact or not.
    */
    private boolean _isCompact;


    // Constructors

    /**
        Constructs an AtriaMessageCatalogData object.
    */
    public AtriaMessageCatalogData()
    {
        _moduleName = null;
        _className = null;
        _localeName = null;
        _styleName = null;
        _isLocalizable = false;
        _isCompact = false;  // by default
    }


    // Public methods

    /**
        @return true iff our module name has been set
        @see #moduleName
    */
    public boolean hasModuleName()
    {
        return (_moduleName != null);
    }

    /**
        @return the name of the module or package in which will be defined
        the class (families) that will represent our message catalog
    */
    public String moduleName()
    {
        Assert.require(hasModuleName());

        String result = _moduleName;

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets our module/package name to the specified name.

        @param name our new module name
        @see #moduleName
    */
    public void setModuleName(String name)
    {
        Assert.require(name != null);

        _moduleName = name;
    }

    /**
        @return true iff our class (family) name has been set
        @see #className
    */
    public boolean hasClassName()
    {
        return (_className != null);
    }

    /**
        @return the name of the class (families) that will represent our
        message catalog
    */
    public String className()
    {
        Assert.require(hasClassName());

        String result = _className;

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets our class (family) name to the specified name.

        @param name our new class name
        @see #className
    */
    public void setClassName(String name)
    {
        Assert.require(name != null);

        _className = name;
    }


    /**
        @return true iff our locale name has been set
        @see #localeName
    */
    public boolean hasLocaleName()
    {
        return (_localeName != null);
    }

    /**
        @return the name of the locale that our message catalog is for
    */
    public String localeName()
    {
        Assert.require(hasLocaleName());

        String result = _localeName;

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets our locale name to the specified name.

        @param name our new locale name
        @see #localeName
    */
    public void setLocaleName(String name)
    {
        Assert.require(name != null);

        _localeName = name;
    }

    /**
        @return true iff our style name has been set
        @see #styleName
    */
    public boolean hasStyleName()
    {
        return (_styleName != null);
    }

    /**
        @return the name of the style that our message catalog is for
    */
    public String styleName()
    {
        Assert.require(hasStyleName());

        String result = _styleName;

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets our style name to the specified name.

        @param name our new style name
        @see #styleName
    */
    public void setStyleName(String name)
    {
        Assert.require(name != null);

        _styleName = name;
    }


    /**
        @return true if our catalog's messages are localizable, and false if
        they aren't
    */
    public boolean isLocalizable()
    {
        return _isLocalizable;
    }

    /**
        Sets whether our messages are localizable or not.

        @param isLocalizable is true if our messages are localizable, and
        false if they're not
    */
    public void setIsLocalizable(boolean isLocalizable)
    {
        _isLocalizable = isLocalizable;
    }


    /**
        @return true iff compact element and attribute names are used in our
        catalog
    */
    public boolean isCompact()
    {
        return _isCompact;
    }

    /**
        Sets whether our compact element and attribute names are used in our
        catalog.

        @param isCompact is true if compact element and attribute names are
        used in our catalog, and false if they're not
    */
    public void setIsCompact(boolean isCompact)
    {
        _isCompact = isCompact;
    }


    // Utility methods

    /**
        @param name an element's name
        @return true iff 'name' is the name of (quoted or unquoted) argument
        element
    */
    public boolean isArgumentElementName(String name)
    {
        Assert.require(name != null);

        boolean result;

        if (isCompact())
        {
            result = name.equals(COMPACT_ARG_ELEMENT_NAME) ||
                        name.equals(COMPACT_QUOTED_ARG_ELEMENT_NAME);
        }
        else
        {
            result = name.equals(ARG_ELEMENT_NAME) ||
                        name.equals(QUOTED_ARG_ELEMENT_NAME);
        }

        return result;
    }

    /**
        @param name an element name
        @return true iff 'name' is the name of a quoted argument element
    */
    public boolean isQuotedArgumentElementName(String name)
    {
        Assert.require(name != null);

        boolean result;

        if (isCompact())
        {
            result = name.equals(COMPACT_QUOTED_ARG_ELEMENT_NAME);
        }
        else
        {
            result = name.equals(QUOTED_ARG_ELEMENT_NAME);
        }

        return result;
    }

    /**
        @param name an attribute's name
        @return true iff 'name' is the name of the attribute on a message
        element that specifies how many arguments the message takes
    */
    public boolean isNumberOfArgumentsAttributeName(String name)
    {
        Assert.require(name != null);

        return (name.equals(numberOfArgumentsAttributeName()));
    }

    /**
        @return the name of the attribute on a message element that indicates
        how many arguments the message takes
    */
    public String numberOfArgumentsAttributeName()
    {
        String result;

        if (isCompact())
        {
            result = COMPACT_NUMBER_OF_ARGS_ATTRIBUTE_NAME;
        }
        else
        {
            result = NUMBER_OF_ARGS_ATTRIBUTE_NAME;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param name an attribute name
        @return true iff 'name' is the name of the attribute on a message
        argument element that specifies the argument's 0-based index
    */
    public boolean isArgumentIndexAttributeName(String name)
    {
        Assert.require(name != null);

        return name.equals(argumentIndexAttributeName());
    }

    /**
        @return the name of the attribute on a message argument element that
        specifies the argument's 0-based index
    */
    public String argumentIndexAttributeName()
    {
        String result;

        if (isCompact())
        {
            result = COMPACT_ARG_INDEX_ATTRIBUTE_NAME;
        }
        else
        {
            result = ARG_INDEX_ATTRIBUTE_NAME;
        }

        Assert.ensure(result != null);
        return result;
    }
}
