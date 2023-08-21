/*
 Copyright (C) 2002-2004 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.plack.common.base.PlackException;

/**
    The singleton exception thrown when parsing a construct fails in such a
    way that parsing part or all of the rest of the construct cannot be done
    with any confidence.
    <p>
    This class of exception is thrown merely to signal the parsing failure:
    it does not contain any other information. Thus it can be - and is - a
    singleton. It is usually only thrown internally in a parser's
    implementation.

    @author James MacKay
*/
public class InvalidConstructPartException
    extends PlackException
{
    // Constants

    /** The single instance of this class. */
    private static final InvalidConstructPartException
        _instance = new InvalidConstructPartException();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static InvalidConstructPartException instance()
    {
        return _instance;
    }
}
