/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common;

/**
    The class used to locate the resource bundle containing
    the resources used by the common/base exception classes.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class CommonExceptionResourcesLocator
    extends ResourcesLocator
{
    /** The resources used by this class. */
    public static final Resources resources =
        createResources(CommonExceptionResourcesLocator.class);
}
