/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.plack.common.generation.java;

import com.steelcandy.common.Resources;
import com.steelcandy.common.ResourcesLocator;

/**
    The class used to locate the resources used by the common Java code
    generation classes.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class CommonJavaGenerationResourcesLocator
    extends ResourcesLocator
{
    /** The common Java code generation Resources. */
    public static final Resources resources =
        createResources(CommonJavaGenerationResourcesLocator.class);
}
