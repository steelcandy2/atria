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

package com.steelcandy.common.text;

/**
    The interface implemented by all unary predicates on
    StringBuffer items.
    <p>
    <strong>Note</strong>: this file was automatically generated, and so
    should not be edited directly.
    <ul>
        <li>Input: src/com/steelcandy/build/steelcandy/generic-classes.xml
        <li>Transform: src/com/steelcandy/build/steelcandy/unary-predicate.java.xsl
        <li>Output: src/com/steelcandy/common/text/UnaryStringBufferPredicate.java
    </ul>

    @author James MacKay
    @see StringBuffer
*/
public interface UnaryStringBufferPredicate
{
    /**
        Indicates whether the specified StringBuffer satisfies this
        predicate.

        @param item the StringBuffer to test
        @return true iff the StringBuffer satisfies this predicate
    */
    public boolean isSatisfied(StringBuffer item);
}
