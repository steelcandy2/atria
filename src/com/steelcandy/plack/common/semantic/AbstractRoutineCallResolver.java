/*
 Copyright (C) 2004-2006 by James MacKay.

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
    An abstract base class for RoutineCallResolvers.
    <p>
    Note: the error messages used by this class' implementations of
    handle...() methods to report errors include the use of the term
    'routine', so languages that do not refer to their methods, functions,
    etc. by that term should override this class' handle...() methods.

    @author  James MacKay
    @version $Revision: 1.8 $
*/
public abstract class AbstractRoutineCallResolver
    implements RoutineCallResolver
{
    // Constructors

    /**
        Constructs an AbstractRoutineCallResolver.
    */
    public AbstractRoutineCallResolver()
    {
        // empty
    }


    // Public methods

    /**
        @see RoutineCallResolver#resolve(SymbolTableEntryList, String, TypeList)
    */
    public SymbolTableEntryList
        resolve(SymbolTableEntryList candidates,
                String routineName, TypeList argumentTypes)
    {
        Assert.require(candidates != null);
        Assert.require(routineName != null);
        Assert.require(argumentTypes != null);

        SymbolTableEntryList result =
            findBestMatches(candidates, routineName, argumentTypes);
        if (result.size() > 1)
        {
            SymbolTableEntryList nonError = removeErrorMatches(result);
            if (nonError.isEmpty())
            {
                // All of the best matches had errors, so we pick one match
                // arbitrarily to be the best match. (Using more than one of
                // the matches with errors would just lead to a spurious
                // error.)
                result = SymbolTableEntryList.
                                createSingleItemList(result.get(0));
            }
            else
            {
                result = nonError;
                    // = the best non-error matches
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        Finds and returns the entries in the specified list of candidate
        entries that represent the routines that best match the specified
        argument types.

        @param candidates the symbol table entries that represent the
        routines that the routine call can possibly resolve to a call to
        @param routineName the name of the routine that was called: this
        is usually only used in error descriptions since it is usually the
        same for all candidates (but this method still has to check that)
        @param argumentTypes the types of the arguments passed to the
        routine in the call, in order
        @return the symbol table entries representing the routines that best
        match the argument types
    */
    protected abstract SymbolTableEntryList
        findBestMatches(SymbolTableEntryList candidates,
                        String routineName, TypeList argumentTypes);
        // Assert.require(candidates != null);
        // Assert.require(routineName != null);
        // Assert.require(argumentTypes != null);
        // Assert.ensure(result != null);

    /**
        Returns those entries in the specified list of routine call matches
        that do not contain errors in those parts of the entries used as
        matching criteria. (This allows non-erroneous entries to be used in
        preference to ones with errors.)
        <p>
        As an example, for languages that resolve routine calls using
        routines' argument types would usually have this method remove
        entries that have one or more arguments whose types are invalid.

        @param matches the entries from which to remove the ones with errors
        @return those entries in 'matches' without any errors in those parts
        used as matching criteria
        @see Type#isInvalidType
    */
    protected abstract SymbolTableEntryList
        removeErrorMatches(SymbolTableEntryList matches);
        // Assert.require(matches != null);
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() <= matches.size());
}
