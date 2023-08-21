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

import org.jdom.*;
import org.jdom.input.*;

import java.io.File;
import java.io.IOException;

/**
    The class of ExternalData that stores information in XML format.

    @author  James MacKay
*/
public class XmlExternalData
    implements ExternalData
{
    // Private fields

    /** The XML document that contains our information. */
    private Document _document;

    /** The ExternalDataElement that represents our topmost element. */
    private XmlExternalDataElement _topElement;


    // Constructors

    /**
        Constructs an XmlExternalData object that contains the information
        in the XML document in the file with pathname 'f'.

        @exception ExternalizingException is thrown iff the file with
        pathname 'f' is missing, can't be read, or doesn't contain a
        satisfactory XML document
    */
    public XmlExternalData(File f)
        throws ExternalizingException
    {
        Assert.require(f != null);

        try
        {
            SAXBuilder b = new SAXBuilder();
            _document = b.build(f);
        }
        catch (IOException ex)
        {
            throw new ExternalizingException(ex);
        }
        catch (JDOMException ex)
        {
            throw new ExternalizingException(ex);
        }

        try
        {
            _topElement = new XmlExternalDataElement(_document.
                                                        getRootElement());
        }
        catch (IllegalStateException ex)
        {
            // This should never happen since a Document constructed as
            // above should always have a root element.
            throw new ExternalizingException(ex);
        }

        Assert.ensure(_document != null);
        Assert.ensure(_topElement != null);
    }

    /**
        Constructs an XmlExternalData object from the name of its topmost
        element.

        Initially the data object will just contain an empty topmost element.

        @param topElementName the name that the data object's topmost element
        will have
        @exception ExternalizingException is thrown iff 'topElementName'
        isn't a valid element name
    */
    public XmlExternalData(String topElementName)
        throws ExternalizingException
    {
        Assert.require(topElementName != null);

        try
        {
            Element root = new Element(topElementName);
            _document = new Document(root);
            _topElement = new XmlExternalDataElement(root);
        }
        catch (IllegalNameException ex)
        {
            throw new ExternalizingException(ex);
        }
        catch (IllegalAddException ex)
        {
            // This should never happen since we've just created the root
            // element 'root' above.
            throw new ExternalizingException(ex);
        }

        Assert.ensure(_document != null);
        Assert.ensure(_topElement != null);
    }


    // Public methods

    /**
        @see ExternalData#topElement
    */
    public ExternalDataElement topElement()
    {
        Assert.ensure(_topElement != null);
        return _topElement;
    }
}
