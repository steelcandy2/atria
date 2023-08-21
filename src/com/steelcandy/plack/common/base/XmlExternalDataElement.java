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

import com.steelcandy.common.Resources;

import org.jdom.*;

/**
    The class of ExternalDataElement that stores its information in an XML
    element.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class XmlExternalDataElement
    implements ExternalDataElement
{
    // Constants

    /** The name of the attribute specifying an element's type class. */
    private static final String
        TYPE_CLASS_ATTRIBUTE_NAME = "extern-type-class";

    /** The name of the attribute specifying an element's type ID. */
    private static final String
        TYPE_ID_ATTRIBUTE_NAME = "extern-type-id";


    /** The resources used by this class. */
    private static final Resources
        _resources = CommonExternalizingResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TYPE_CLASS_ALREADY_SET_MSG =
            "TYPE_CLASS_ALREADY_SET_MSG",
        TYPE_ID_ALREADY_SET_MSG =
            "TYPE_ID_ALREADY_SET_MSG",
        ATTRIBUTE_ALREADY_EXISTS_MSG =
            "ATTRIBUTE_ALREADY_EXISTS_MSG",
        INVALID_ATTRIBUTE_NAME_MSG =
            "INVALID_ATTRIBUTE_NAME_MSG",
        INVALID_ATTRIBUTE_VALUE_MSG =
            "INVALID_ATTRIBUTE_VALUE_MSG",
        INVALID_ELEMENT_NAME_MSG =
            "INVALID_ELEMENT_NAME_MSG";


    // Private fields

    /** The XML element that contains our information. */
    private Element _element;


    // Constructors

    /**
        Constructs an XmlExternalDataElement from the XMl element that
        contains its information.

        @param e the XML element
    */
    public XmlExternalDataElement(Element e)
    {
        Assert.require(e != null);

        _element = e;

        Assert.ensure(_element != null);
    }


    // Public methods

    /**
        @see ExternalDataElement#setTypeClass(String)
    */
    public void setTypeClass(String typeClass)
        throws ExternalizingException
    {
        Assert.require(typeClass != null);

        String name = TYPE_CLASS_ATTRIBUTE_NAME;
        String old = _element.getAttributeValue(name);
        if (old == null)
        {
            _element.setAttribute(name, typeClass);
        }
        else
        {
            // The type class has already been set.
            String msg = _resources.getMessage(TYPE_CLASS_ALREADY_SET_MSG,
                                               _element.getName(), old);
            throw new ExternalizingException(msg);
        }
    }

    /**
        @see ExternalDataElement#typeClass
    */
    public String typeClass()
    {
        // 'result' may be null
        return _element.getAttributeValue(TYPE_CLASS_ATTRIBUTE_NAME);
    }


    /**
        @see ExternalDataElement#setTypeId(String)
    */
    public void setTypeId(String typeId)
        throws ExternalizingException
    {
        Assert.require(typeId != null);

        String name = TYPE_ID_ATTRIBUTE_NAME;
        String old = _element.getAttributeValue(name);
        if (old == null)
        {
            _element.setAttribute(name, typeId);
        }
        else
        {
            // The type ID has already been set.
            String msg = _resources.getMessage(TYPE_ID_ALREADY_SET_MSG,
                                               _element.getName(), old);
            throw new ExternalizingException(msg);
        }
    }

    /**
        @see ExternalDataElement#typeId
    */
    public String typeId()
    {
        // 'result' may be null
        return _element.getAttributeValue(TYPE_ID_ATTRIBUTE_NAME);
    }


    /**
        @see ExternalDataElement#addAttribute(String, String)
    */
    public void addAttribute(String name, String value)
        throws ExternalizingException
    {
        Assert.require(name != null);
        Assert.require(value != null);

        String old = _element.getAttributeValue(name);
        if (old == null)
        {
            try
            {
                _element.setAttribute(name, value);
            }
            catch (IllegalNameException ex)
            {
                String msg = _resources.
                    getMessage(INVALID_ATTRIBUTE_NAME_MSG,
                               _element.getName(), name, value,
                               ex.getLocalizedMessage());
                throw new ExternalizingException(msg, ex);
            }
            catch (IllegalDataException ex)
            {
                String msg = _resources.
                    getMessage(INVALID_ATTRIBUTE_VALUE_MSG,
                               _element.getName(), name, value,
                               ex.getLocalizedMessage());
                throw new ExternalizingException(msg, ex);
            }
        }
        else
        {
            // We already have an attribute named 'name'.
            String msg = _resources.getMessage(ATTRIBUTE_ALREADY_EXISTS_MSG,
                                    _element.getName(), name, value, old);
            throw new ExternalizingException(msg);
        }
    }

    /**
        @see ExternalDataElement#addChildElement(String)
    */
    public ExternalDataElement addChildElement(String name)
        throws ExternalizingException
    {
        Assert.require(name != null);

        ExternalDataElement result = null;

        try
        {
            Element child = new Element(name);
            _element.addContent(child);
            result = new XmlExternalDataElement(child);
        }
        catch (IllegalNameException ex)
        {
            String msg = _resources.getMessage(INVALID_ELEMENT_NAME_MSG,
                    _element.getName(), name, ex.getLocalizedMessage());
            throw new ExternalizingException(msg, ex);
        }
        catch (IllegalAddException ex)
        {
            // This should never happen since we just created the child
            // element, and so it shouldn't have a parent element yet.
            throw new ExternalizingException(ex);
        }

        Assert.ensure(result != null);
        return result;
    }
}
