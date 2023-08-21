/*
 Copyright (C) 2004-2005 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

/**
    The singleton immutable empty symbol table class.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class EmptySymbolTable
    extends AbstractEmptySymbolTable
{
    // Constants

    /** The single instance of this class. */
    private static final EmptySymbolTable _instance = new EmptySymbolTable();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static EmptySymbolTable instance()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private EmptySymbolTable()
    {
        // empty
    }
}
