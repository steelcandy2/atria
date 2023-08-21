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

/**
    The interface implemented by classes that can be saved to and restored
    from an external file or other data source.

    Subclasses should also have a (named) constructor that takes a single
    ExternalDataElement argument. The constructor should throw an
    ExternalizingException iff the element's type class and/or type ID is
    missing or the wrong one for this class of object.

    @author  James MacKay
    @version $Revision: 1.1 $
    @see ExternalDataElement
    @see ExternalizingException
*/
public interface Externalizable
{
    // Public methods

    /**
        Externalizes us by adding child elements and attributes to 'e',
        in addition to setting 'e''s type class and type ID.

        @param e the element to add our information to
        @exception ExternalizingException is thrown if we cannot add all
        of our information to 'e'

        @see ExternalDataElement#setTypeClass(String)
        @see ExternalDataElement#setTypeId(String)
        @see ExternalDataElement#addAttribute(String, String)
        @see ExternalDataElement#addChildElement(String)
    */
    public void save(ExternalDataElement e)
        throws ExternalizingException;
        // Assert.require(e != null);
}
