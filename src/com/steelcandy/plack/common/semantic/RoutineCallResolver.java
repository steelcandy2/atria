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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The interface implemented by classes that determines which one of a set
    of routines a given routine call resolves to, or reports an error if
    the call does not resolve to a single routine. In this class a routine
    can be a function, procedure, method, or anything else that can be
    called or invoked with arguments.
    <p>
    Note that instances do not check routine accessibility: if that is needed
    then either the inaccessible routines should be removed from the list of
    candidates passed to resolve(), or accessiblity should be checked for
    the routine returned by resolve().
    <p>
    Note: while it is <em>very</em> common that the result of resolving a
    routine call is one of the candidate routines, it is not strictly
    necessary.

    @author  James MacKay
*/
public interface RoutineCallResolver
{
    // Public methods

    /**
        Returns the symbol table entries that represent the routines that
        best match the specified argument types.
        <p>
        If the returned list has
        <ul>
            <li>exactly one item in it then it represents the routine that is
                the single best match to the argument types
            <li>no items in it then there were no routines that matched the
                argument types
            <li>two or more items in it then they represent the routines that
                were the best matches to the argument types, but there was no
                single best match
        </ul>

        @param candidates the symbol table entries that represent the
        routines that the routine call can possibly resolve to a call to
        @param routineName the name of the routine that was called: this
        is usually only used in error descriptions since it is usually the
        same for all candidates
        @param argumentTypes the types of the arguments passed to the
        routine in the call, in order
        @return the symbol table entries that represent the routines that
        best match 'argumentTypes'
    */
    public SymbolTableEntryList
        resolve(SymbolTableEntryList candidates,
                String routineName, TypeList argumentTypes);
        // Assert.require(candidates != null);
        // Assert.require(routineName != null);
        // Assert.require(argumentTypes != null);
        // Assert.ensure(result != null);
}
