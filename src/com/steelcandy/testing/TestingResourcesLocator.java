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

package com.steelcandy.testing;

import com.steelcandy.common.Resources;
import com.steelcandy.common.ResourcesLocator;

/**
    The class used to locate the resources used by all of
    the testing-related classes in this package.

    @author James MacKay
*/
public class TestingResourcesLocator
    extends ResourcesLocator
{
    /** The testing Resources. */
    public static final Resources resources =
        createResources(TestingResourcesLocator.class);
}
