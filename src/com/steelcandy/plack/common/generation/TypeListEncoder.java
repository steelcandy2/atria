/*
 Copyright (C) 2004-2009 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.semantic.Type;  // javadocs
import com.steelcandy.plack.common.semantic.TypeList;

import com.steelcandy.common.text.StringList;

/**
    The interface implemented by classes that encode lists of Types into
    a String that can be used in an identifier in the target language.
    <p>
    Note: concrete subclasses are usually target language-specific so that
    their encodings are valid identifier parts in that language.

    @author  James MacKay
*/
public interface TypeListEncoder
{
    // Public methods

    /**
        @param types a list of Types
        @param handler the error handler to use to handle any errors that
        occur in the encoding
        @return a String that uniquely identifies 'types', including the
        order and values of its elements, and can be used as part of an
        identifier in the target language
    */
    public String encode(TypeList types, ErrorHandler handler);
        // Assert.require(types != null);
        // Assert.require(handler != null);
        // Assert.ensure(result != null);

    /**
        @param globalTypeNames the global names of a list of Types
        @param handler the error handler to use to handle any errors that
        occur in the encoding
        @return a String that uniquely identifies the list of types whose
        global names are in 'globalTypeNames', including the order and
        values of the types, and can be used as part of an identifier in the
        target language
        @see Type#globalName
    */
    public String encode(StringList globalTypeNames, ErrorHandler handler);
        // Assert.require(globalTypeNames != null);
        // Assert.require(handler != null);
        // Assert.ensure(result != null);
}
