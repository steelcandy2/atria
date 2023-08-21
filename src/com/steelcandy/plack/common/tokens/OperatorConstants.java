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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

/**
    An interface that defines constants used to specify information about an
    operator, such as its precedence, arity and associativity.
    <p>
    Operators are not limited to having one of the arities represented by
    constants in this interface: the ones defined here just provide a
    symbolic name for the most common arities.
    <p>
    The OperatorData class provides methods to get names for the various
    values that this interface provides constants for.
    <p>
    An operator's precedence level is assumed to be non-negative.

    @author James MacKay
    @version $Revision: 1.5 $
    @see OperatorData
*/
public interface OperatorConstants
{
    // Arity constants

    /** The arity of a nullary (0-argument) operator. */
    public static final int NULLARY = 0;

    /** The arity of a unary (1-argument) operator. */
    public static final int UNARY = 1;

    /** The arity of a binary (2-argument) operator. */
    public static final int BINARY = 2;

    /** The arity of a ternary (3-argument) operator. */
    public static final int TERNARY = 3;


    // Associativity constants

    /** The associativity of a left-associative operator. */
    public static final int LEFT_ASSOCIATIVE = 0;

    /** The associativity of a right-associative operator. */
    public static final int RIGHT_ASSOCIATIVE = LEFT_ASSOCIATIVE + 1;


    /** The number of different associativities. */
    public static final int
        NUMBER_OF_ASSOCIATIVITIES = RIGHT_ASSOCIATIVE + 1;


    // Fixity constants

    /** The fixity of a prefix operator. */
    public static final int PREFIX = 0;

    /** The fixity of a postfix operator. */
    public static final int POSTFIX = PREFIX + 1;

    /** The fixity of an infix operator. */
    public static final int INFIX = POSTFIX + 1;

    /** The fixity of an outfix/matchfix operator. */
    public static final int OUTFIX = INFIX + 1;


    /** The number of different fixities. */
    public static final int NUMBER_OF_FIXITIES = OUTFIX + 1;
}
