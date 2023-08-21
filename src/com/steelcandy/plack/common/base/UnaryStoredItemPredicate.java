/*
 Copyright (C) 2014 by James MacKay.

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

/**
    The interface implemented by all unary predicates on
    StoredItem items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/unary-predicate.java.xsl
        <li>Output: src/com/steelcandy/plack/common/base/UnaryStoredItemPredicate.java
    </ul>

    @author James MacKay
    @see StoredItem
*/
public interface UnaryStoredItemPredicate
{
    /**
        Indicates whether the specified StoredItem satisfies this
        predicate.

        @param item the StoredItem to test
        @return true iff the StoredItem satisfies this predicate
    */
    public boolean isSatisfied(StoredItem item);
}
