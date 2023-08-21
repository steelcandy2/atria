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

package com.steelcandy.common.ints;

import com.steelcandy.common.debug.Assert;

/**
    The default implementation of the UnaryIntPredicate interface.

    @author James MacKay
    @version $Revision: 1.5 $
*/
public class DefaultUnaryIntPredicate
    implements UnaryIntPredicate
{
    // Private fields

    /**
        The map that maps ints explicitly to 1 (true) or 0 (false). (Other
        ints are mapped to the default value.)
    */
    private IntMap _map = new IntHashMap();

    /**
        The default value of this predicate: that is, the value of this
        predicate for all of those ints not mapped by the _map.
    */
    private boolean _defaultValue;


    // Constructors

    /**
        Constructs a UnaryIntPredicate.

        @param defaultValue the predicate's default value
        @see #setNonDefaultValue(int)
        @see #setValue(int, boolean)
    */
    public DefaultUnaryIntPredicate(boolean defaultValue)
    {
        _defaultValue = defaultValue;
    }


    // Public methods

    /**
        Sets the result returned by this predicate when applied to the
        specified int value to be the <em>current</em> non-default value.

        @param value the int value for which the result returned by this
        predicate is to be set to the current non-default value
    */
    public void setNonDefaultValue(int value)
    {
        setValue(value, !_defaultValue);
    }

    /**
        Sets the result returned by this predicate when applied to the
        specified int value to be the specified result.

        @param value the int value for which the result returned by this
        predicate is to be set
        @param result the result returned when this predicate is applied to
        the specified int value
    */
    public void setValue(int value, boolean result)
    {
        // Convert boolean value to int for map.
        _map.set(value, result ? 1 : 0);
    }


    /**
        @see UnaryIntPredicate#isSatisfied
    */
    public boolean isSatisfied(int item)
    {
        boolean result = _defaultValue;
        if (_map.hasKey(item))
        {
            // Convert int map value to boolean.
            result = (_map.get(item) > 0);
        }
        return result;
    }
}
