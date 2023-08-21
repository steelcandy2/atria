/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.Resources;
import com.steelcandy.common.ResourcesLocator;

/**
    The class used to locate the resources used by the common error classes.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class CommonErrorResourcesLocator
    extends ResourcesLocator
{
    /** The common errors Resources. */
    public static final Resources resources =
        createResources(CommonErrorResourcesLocator.class);
}
