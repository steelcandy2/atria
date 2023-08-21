/*
 Copyright (C) 2003-2011 by James MacKay.

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
    The interface implemented by all classes that represent the type of an
    expression or other language construct or fragment.
    <p>
    <strong>Note</strong>: no relationship is assumed between the global and
    local names of a given type. For example, depending on the language that
    a type is a part of, its global and local names might always - or just
    in some cases - be the same, its local name might always - or just
    sometimes - appear at the end of its global name, or its global and local
    names could be completely unrelated.

    @author James MacKay
*/
public interface Type
{
    // Public methods

    /**
        Indicates whether this type conforms to the specified type: that is,
        whether an object of this type can be used wherever an object of the
        specified type is expected.

        @param t the type to which this type does or does not conform
        @return true if this type conforms to the specified type, and false
        if it doesn't
    */
    public boolean conformsTo(Type t);
        // Assert.require(t != null);

    /**
        @return true iff other types always conform to this type, regardless
        of what those other types are
    */
    public boolean isAlwaysConformedTo();
        // Assert.ensure(isNeverConformedTo() == false || result == false);
        // "isNeverConformedTo() implies result == false"

    /**
        @return true iff other types never conform to this type, regardless
        of what those other types are
    */
    public boolean isNeverConformedTo();
        // Assert.ensure(isAlwaysConformedTo() == false || result == false);
        // "isAlwaysConformedTo() implies result == false"


    /**
        Indicates whether this object represents the exact same type as
        the specified object does.
        <p>
        Subclasses should also implement the version of <code>equals()</code>
        inherited from <code>Object</code> to be consistent with this method.
        (This version merely bypasses checking its parameter's type and
        casting it, as well as requiring that the other type not be null.)

        @param t the type to compare this type with
        @return true if this type represents exactly the same type as the
        specified type, and false if it doesn't
        @see Object#equals
    */
    public boolean equals(Type t);
        // Assert.require(t != null);

    /**
        @return true iff this type is always equal to all other types,
        regardless of what those other types are
    */
    public boolean isAlwaysEqualTo();
        // Assert.ensure(isNeverEqualTo() == false || result == false);
        // "isNeverEqualTo() implies result == false"

    /**
        @return true iff this type is never equal to any other types,
        regardless of what those other types are
    */
    public boolean isNeverEqualTo();
        // Assert.ensure(isAlwaysEqualTo() == false || result == false);
        // "isAlwaysEqualTo() implies result == false"


    /**
        @return all of this type's symbols, including the ones that are not
        accessible from any other type, or from other instances of the same
        type
    */
    public SymbolTable allSymbols();
        // Assert.ensure(result != null);

    /**
        @param contextType the type from within which the symbols are to be
        accessible
        @return all of this type's symbols that are accessible from within
        'contextType'
    */
    public SymbolTable accessibleSymbols(Type contextType);
        // Assert.require(contextType != null);
        // Assert.ensure(result != null);


    /**
        Note: most types are fully resolved. However, some types (such as
        types that represent type parameters, or types with unresolved type
        parameters) may not be fully resolved.

        @return true iff this type is fully resolved
    */
    public boolean isFullyResolved();

    /**
        Resolves this type using the specified resolver.
        <p>
        Note: most types resolve to themselves. However, some types (such
        as types that represent generic formals, for example) may resolve
        to other types.
        <p>
        Note: since a given type resolver may not be able to resolve all
        unresolved types, the results of this method are not necessarily
        fully resolved (that is, calling isFullyResolved() on them will not
        necessarily return true).

        @param resolver the resolver to use to resolve this type
        @return the result of resolving this type using 'resolver'
    */
    public Type resolve(TypeResolver resolver);
        // Assert.require(resolver != null);
        // Assert.ensure(result != null);


    /**
        @return a displayable representation of this type
        @see #globalName
    */
    public String display();
        // Assert.ensure(result != null);

    /**
        @return the name of this type that is unique across all types in the
        system: thus it includes our type parameters
        @see #display
    */
    public String globalName();
        // Assert.ensure(result != null);

    /**
        @return the global name of this type, but without any type parameter
        information
    */
    public String name();
        // Assert.ensure(result != null);

    /**
        @return the global name of this type, but without any type parameter
        or module/package/etc. information
    */
    public String unqualifiedName();
        // Assert.ensure(result != null);


    /**
        @return this type's type parameters, or an empty list if it is not a
        parameterized type
    */
    public TypeList typeParameters();
        // Assert.ensure(result != null);

    /**
        @return this type's direct supertypes, or an empty list if it has no
        direct supertypes
    */
    public TypeList directSupertypes();
        // Assert.ensure(result != null);

    /**
        Note: often when a type is invalid or missing it is convenient to
        replace it with an instance of a subclass that represents invalid
        types. In order to avoid causing further errors, such a type is
        usually equal to and conforms to all other types, and all other
        types are equal to and conform to it. However, there are some
        circumstances where a type that <em>does</em> conform to (or is
        equal to) another type will result in an error: this method is
        intended for use in such situations.

        @return true iff this type represents an invalid type
        @see AbstractInvalidType
    */
    public boolean isInvalidType();
}
