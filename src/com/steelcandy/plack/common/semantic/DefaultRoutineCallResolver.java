/*
 Copyright (C) 2004-2008 by James MacKay.

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

import com.steelcandy.common.Resources;

import java.util.BitSet;

/**
    The default implementation of the RoutineCallResolver interface.

    @author  James MacKay
    @version $Revision: 1.7 $
*/
public class DefaultRoutineCallResolver
    extends AbstractRoutineCallResolver
{
    // Constants

    /** The single instance of this class. */
    private static final DefaultRoutineCallResolver
        _instance = new DefaultRoutineCallResolver();


    // Constructors

    /**
        @return a (logically) new instance of this class
    */
    public static DefaultRoutineCallResolver create()
    {
        return _instance;
    }

    /**
        Constructs a DefaultRoutineCallResolver.
    */
    protected DefaultRoutineCallResolver()
    {
        // empty
    }


    // Protected methods

    /**
        @see AbstractRoutineCallResolver#findBestMatches(SymbolTableEntryList, String, TypeList)
    */
    protected SymbolTableEntryList
        findBestMatches(SymbolTableEntryList candidates,
                        String routineName, TypeList argumentTypes)
    {
        Assert.require(candidates != null);
        Assert.require(routineName != null);
        Assert.require(argumentTypes != null);

        SymbolTableEntryList result =
            findMatches(candidates, routineName, argumentTypes);
        if (result.size() > 1 && argumentTypes.isEmpty() == false)
        {
            result = findBestMatchesFromMatches(result,
                                                routineName, argumentTypes);
        }
        // Note: if argumentTypes.isEmpty() then there is no way for one
        // match to be better than another.

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns a list of all of the entries in the specified list of
        candidates that match the call with arguments of the specfied types
        of a routine with the specified name.

        @param candidates the symbol table entries that represent the
        routines that are candidates to match the routine call
        @param routineName the name of the routine that was called
        @param argumentTypes the types of the arguments that the routine was
        called with, in order
        @return the entries that represent the routines that match
        'argumentTypes'
    */
    protected SymbolTableEntryList
        findMatches(SymbolTableEntryList candidates,
                    String routineName, TypeList argumentTypes)
    {
        Assert.require(candidates != null);
        Assert.require(routineName != null);
        Assert.require(argumentTypes != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(candidates.size());

        SymbolTableEntryIterator iter = candidates.iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry candidate = iter.next();
            if (isMatch(candidate, routineName, argumentTypes))
            {
                result.add(candidate);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= candidates.size());
        return result;
    }

    /**
        Indicates whether the specified candidate symbol table entry matches
        a call with arguments of the specified types of a routine with the
        specified name.

        @param candidate the symbol table entry to check
        @param routineName the name of the routine that was called
        @param argumentTypes the types of the arguments that the routine was
        called with, in order
        @return true iff 'candidate' represents a routine named 'routineName'
        that matches a call with arguments whose types are 'argumentTypes'
    */
    protected boolean isMatch(SymbolTableEntry candidate,
                              String routineName, TypeList argumentTypes)
    {
        Assert.require(candidate != null);
        Assert.require(routineName != null);
        Assert.require(argumentTypes != null);

        boolean result = false;

        int numArgs = argumentTypes.size();
        if (routineName.equals(candidate.name()) &&
            candidate.arity() == numArgs)
        {
            result = true;
            for (int i = 0; i < numArgs; i++)
            {
                Type callArgType = argumentTypes.get(i);
                Type candidateArgType = candidate.argumentType(i);
                if (callArgType.conformsTo(candidateArgType) == false)
                {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }


    /**
        Given a list of the symbol table entries that represent the routines
        that match a given routine call, returns the entries that represent
        the routines that best match the routine call.

        @param matches the symbol table entries that represent the routines
        that match a given routine call
        @param routineName the name of the routine that was called
        @param argumentTypes the types of the arguments passed to the routine
        in the call, in order: they are usually only used in error messages
        @return the symbol table entries representing the routines that best
        match the routine call
    */
    protected SymbolTableEntryList
        findBestMatchesFromMatches(SymbolTableEntryList matches,
                                String routineName, TypeList argumentTypes)
    {
        Assert.require(matches != null);
        Assert.require(matches.size() > 1);
        Assert.require(routineName != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.isEmpty() == false);

        int numArgs = argumentTypes.size();
        BitSet bestMatchIndices = bestMatchIndices(matches, 0);
        for (int i = 1; i < numArgs && bestMatchIndices.length() > 0; i++)
        {
            bestMatchIndices.and(bestMatchIndices(matches, i));
        }

        SymbolTableEntryList result;

        int len = bestMatchIndices.length();
        if (len == 0)
        {
            result = SymbolTableEntryList.createEmptyList();
        }
        else
        {
            result = SymbolTableEntryList.createArrayList(len);
            for (int i = 0; i < len; i++)
            {
                if (bestMatchIndices.get(i))
                {
                    result.add(matches.get(i));
                }
            }
            Assert.check(result.isEmpty() == false);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns a bit set whose bit at index i is set iff the symbol table
        entry at index i in 'matches' represents a routine whose
        ('argumentIndex'+1)th argument's type is not a strictly worse match
        than the ('argumentIndex'+1)th argument of any other routine with an
        entry in 'matches'.

        @param matches a list of symbol table entries that represent routines
        that matched the same routine call
        @param argumentIndex the index of an argument of each of the routines
        represented by an entry in 'matches'
        @return a bit set whose bit at index i is set iff the symbol table
        entry at index i in 'matches' represents a routine whose
        ('argumentIndex'+1)th argument's type is not a proper subtype of the
        type of the ('argumentIndex'+1)th argument of any routine represented
        by any other entry in 'matches'
    */
    protected BitSet
        bestMatchIndices(SymbolTableEntryList matches, int argumentIndex)
    {
        Assert.require(matches != null);
        Assert.require(matches.size() > 1);
        Assert.require(argumentIndex >= 0);

        int numMatches = matches.size();

        // Initialize 'result' to have all of its bits set.
        BitSet result = new BitSet(numMatches);
        for (int i = 0; i < numMatches; i++)
        {
            result.set(i);
        }

        for (int i = 0; i < numMatches; i++)
        {
            if (result.get(i))
            {
                Type t1 = matches.get(i).argumentType(argumentIndex);
                for (int j = 0; j < i; j++)
                {
                    if (result.get(j))
                    {
                        Type t2 = matches.get(j).argumentType(argumentIndex);
                        if (t1.equals(t2) == false)
                        {
                            if (t1.conformsTo(t2))
                            {
                                // t1 is a proper subtype of t2, and so the
                                // match at index 'i' is a strictly better
                                // match than the one at index 'j'.
                                result.clear(j);
                            }
                            else if (t2.conformsTo(t1))
                            {
                                // The match at index 'j' is strictly better
                                // than the one at index 'i'.
                                result.clear(i);
                            }
                        }
                    }  // if (result.get(j))
                }  // for (int j)
            }  // if (result,get(i))
        }  // for (int i)

        Assert.ensure(result != null);
        Assert.ensure(result.size() >= matches.size());
        Assert.ensure(result.length() > 0);
            // since not all of the matches can be eliminated
        return result;
    }

    /**
        @see AbstractRoutineCallResolver#removeErrorMatches(SymbolTableEntryList)
    */
    protected SymbolTableEntryList
        removeErrorMatches(SymbolTableEntryList matches)
    {
        Assert.require(matches != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(matches.size());

        SymbolTableEntryIterator iter = matches.iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry entry = iter.next();
            if (AbstractType.areAllValidTypes(entry.argumentTypes()))
            {
                result.add(entry);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= matches.size());
        return result;
    }
}
