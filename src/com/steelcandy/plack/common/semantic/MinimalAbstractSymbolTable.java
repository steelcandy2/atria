/*
 Copyright (C) 2003-2015 by James MacKay.

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
import com.steelcandy.common.UnexpectedException;
import com.steelcandy.common.containers.Containers;
import com.steelcandy.common.io.Io;

import java.util.HashMap;

/**
    A minimal abstract base class for most (if not all) SymbolTable
    classes.

    @author James MacKay
    @version $Revision: 1.23 $
*/
public abstract class MinimalAbstractSymbolTable
    implements SymbolTable
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_SYMBOL_TABLE_ENTRY_KIND_MSG =
            "INVALID_SYMBOL_TABLE_ENTRY_KIND_MSG";
    private static final String
        INVALID_UNSAFELY_ADDED_ENTRY_MSG =
            "INVALID_UNSAFELY_ADDED_ENTRY_MSG";
    private static final String
        DUPLICATE_UNSAFELY_ADDED_ENTRY_MSG =
            "DUPLICATE_UNSAFELY_ADDED_ENTRY_MSG";


    /** An empty SymbolTableEntryIterator. */
    protected static final SymbolTableEntryIterator EMPTY_ITERATOR =
        SymbolTableEntryList.createEmptyList().iterator();

    /** The default initial capacity for a symbol table. */
    protected static final int DEFAULT_INITIAL_CAPACITY = 32;

    /**
        The single instance of the NoInvalidArgumentTypesPredicate.
    */
    private static final NoInvalidArgumentTypesPredicate
        NO_INVALID_ARG_TYPES_PREDICATE =
            new NoInvalidArgumentTypesPredicate();


    // Private fields

    /** Our parent symbol table, or null if we don't have one. */
    private SymbolTable _parent;


    // Constructors

    /**
        Constructs a MinimalAbstractSymbolTable with no parent.

        @see #setParent(SymbolTable)
    */
    public MinimalAbstractSymbolTable()
    {
        _parent = null;
    }

    /**
        Constructs a MinimalAbstractSymbolTable with the specified symbol
        table as its parent.

        @param parent the symbol table's parent
    */
    public MinimalAbstractSymbolTable(SymbolTable parent)
    {
        Assert.require(parent != null);

        setParent(parent);
    }


    // Public methods

    /**
        @see SymbolTable#findRecursively(String, int)
    */
    public SymbolTableEntryList findRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result = find(name, arity);
        if (result.isEmpty() && hasParent())
        {
            result = parent().findRecursively(name, arity);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#parentFindRecursively(String, int)
    */
    public SymbolTableEntryList
        parentFindRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result;
        if (hasParent())
        {
            result = parent().findRecursively(name, arity);
        }
        else
        {
            result = SymbolTableEntryList.createEmptyList();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#findRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList findRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        SymbolTableEntryList result = find(name, argumentTypes, matcher);
        if (result.isEmpty())
        {
            result = parentFindRecursively(name, argumentTypes, matcher);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#parentFindRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList parentFindRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        SymbolTableEntryList result;
        if (hasParent())
        {
            result = parent().findRecursively(name, argumentTypes, matcher);
        }
        else
        {
            result = SymbolTableEntryList.createEmptyList();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#hasParent
    */
    public boolean hasParent()
    {
        return (_parent != null);
    }


    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer(4000);

        buf.append("Symbol table of class ").
            append(getClass().getName()).
            append(" - entries").
            append(Io.NL);

        SymbolTableEntryIterator iter = iterator();
        while (iter.hasNext())
        {
            buf.append("    ").
                append(iter.next().toString()).
                append(Io.NL);
        }

        if (_parent != null)
        {
            buf.append("Parent symbol table ...").
                append(_parent.toString());
        }

        return buf.toString();
    }


    // Public static methods

    /**
        @param entry a symbol table entry
        @return true iff none of 'entry''s argument's types are invalid
        @see SymbolTableEntry#argumentTypes
        @see Type#isInvalidType
    */
    public static boolean hasNoInvalidArgumentTypes(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        return NO_INVALID_ARG_TYPES_PREDICATE.isSatisfied(entry);
    }

    /**
        @return a predicate that is only satisfied by those symbol table
        entries that have no arguments whose types are invalid

        @see SymbolTableEntryList#filter(UnarySymbolTableEntryPredicate)
        @see SymbolTableEntry#argumentTypes
        @see Type#isInvalidType
    */
    public static UnarySymbolTableEntryPredicate
                        hasNoInvalidArgumentTypesPredicate()
    {
        Assert.ensure(NO_INVALID_ARG_TYPES_PREDICATE != null);
        return NO_INVALID_ARG_TYPES_PREDICATE;
    }


    // Protected methods

    /**
        @param candidates a list of candidate symbol table entries
        @param argumentTypes the types that match the argument types
        of the entries to find
        @param matcher the argument type matcher that is used to determine
        whether a type from 'argumentTypes' matches the type of an entry's
        argument
        @return a those items in 'candidates' whose arguments match
        'argumentTypes' according to 'matcher'
    */
    protected SymbolTableEntryList
        findMatches(SymbolTableEntryList candidates, TypeList argumentTypes,
                    ArgumentTypeMatcher matcher)
    {
        Assert.require(candidates != null);
        // Assert.require("for each item 'c' in 'candidates' " +
        //                  "c.arity() == argumentTypes.size()");
        Assert.require(argumentTypes != null);
        Assert.require(matcher != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(candidates.size());

        int arity = argumentTypes.size();
        SymbolTableEntryIterator iter = candidates.iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry entry = iter.next();
            Assert.check(entry.arity() == arity);

            boolean isMatch = true;
            for (int i = 0; i < arity; i++)
            {
                if (matcher.isMatch(entry.argumentType(i),
                                    argumentTypes.get(i)) == false)
                {
                    isMatch = false;
                    break;
                }
            }

            if (isMatch)
            {
                result.add(entry);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= candidates.size());
        return result;
    }

    /**
        Returns all of the entries in the specified list with all of the
        duplicates removed, where entries are considered to be duplicates of
        each other iff <code>areDuplicates()</code> indicates that they are.
        <p>
        In a set of duplicate entries the one that will end up in the
        returned list is one that is not less accessible than all of the
        other entries in that set.

        @param list the list of entries to remove duplicates from
        @return the entries in 'list' with all of the duplicate ones removed
        @see #areDuplicates(SymbolTableEntry, SymbolTableEntry)
    */
    protected SymbolTableEntryList
        removeDuplicates(SymbolTableEntryList list)
    {
        Assert.require(list != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(list.size());

        SymbolTableEntryIterator iter = list.iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry entry = iter.next();

            boolean addEntry = true;
            int numResults = result.size();
            for (int i = 0; i < numResults; i++)
            {
                SymbolTableEntry resultEntry = result.get(i);
                if (areDuplicates(entry, resultEntry))
                {
                    // Make sure the duplicate we choose isn't less
                    // accessible than any of the other duplicates.
                    if (entry.isAtLeastAsAccessibleAs(resultEntry))
                    {
                        result.set(i, entry);
                    }
                    addEntry = false;
                    break;  // for
                }
            }

            if (addEntry)
            {
                result.add(entry);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= list.size());
        Assert.ensure(list.isEmpty() || result.isEmpty() == false);
            // (list.isEmpty() == false) implies (result.isEmpty() == false)
        return result;
    }

    /**
        This implementation considers entries to be duplicates iff the first
        entry's equals() method considers it to be the same as the second
        entry.

        @param entry1 an symbol table entry
        @param entry2 another symbol table entry
        @return true iff 'entry1' and 'entry2' should be considered to be
        duplicates
        @see #removeDuplicates(SymbolTableEntryList)
        @see SymbolTableEntry#equals(SymbolTableEntry)
    */
    protected boolean
        areDuplicates(SymbolTableEntry entry1, SymbolTableEntry entry2)
    {
        Assert.require(entry1 != null);
        Assert.require(entry2 != null);

        return entry1.equals(entry2);
    }

    /**
        Checks that the specified entry is one that can validly be added to
        this symbol table.
        <p>
        Note: this implementation considers an entry invalid iff
        isValidEntryKind() returns false when passed the entry's kind.

        @param entry the entry to check
        @exception InvalidSymbolTableEntryException thrown if 'entry' cannot
        validly be added to this symbol table
        @see #isValidEntryKind(int)
        @see SymbolTableEntry#kind
    */
    protected void checkValidEntry(SymbolTableEntry entry)
        throws InvalidSymbolTableEntryException
    {
        Assert.require(entry != null);

        if (isValidEntryKind(entry.kind()) == false)
        {
            String msg = _resources.
                getMessage(INVALID_SYMBOL_TABLE_ENTRY_KIND_MSG,
                           String.valueOf(entry.kind()),
                           getClass().getName(),
                           entry.getClass().getName());
            throw new InvalidSymbolTableEntryException(msg);
        }
    }


    // Protected utility methods

    /**
        @see Containers#createHashMap(int)
    */
    protected HashMap createHashMap(int initialCapacity)
    {
        Assert.require(initialCapacity >= 0);

        //Assert.ensure(result != null);
        return Containers.createHashMap(initialCapacity);
    }

    /**
        Removes constructs from all of our entries, though not the ones in
        our parent table.

        @see SymbolTableEntry#removeConstructs
    */
    protected void removeEntryConstructs()
    {
        SymbolTableEntryIterator iter = iterator();
        while (iter.hasNext())
        {
            iter.next().removeConstructs();
        }
    }

    /**
        Adds the specified entry to this table. It is assumed that
        the symbol will not be a duplicate of an existing symbol, and
        that this table will consider it a valid entry.
        <p>
        The <code>add()</code> method should be used instead of this
        one except in the implementations of methods where the usual
        checks for invalid and duplicate symbols should always succeed.

        @param entry the entry to add to this table
        @exception UnexpectedException thrown if the entry is either
        considered an invalid entry by this table, or if it is a
        duplicate of an existing entry in this table
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
        @see #add(SymbolTableEntry)
    */
    protected void unsafeAdd(SymbolTableEntry entry)
        throws UnexpectedException, ImmutableSymbolTableException
    {
        Assert.require(entry != null);

        try
        {
            add(entry);
        }
        catch (InvalidSymbolTableEntryException ex)
        {
            String msg = _resources.
                getMessage(INVALID_UNSAFELY_ADDED_ENTRY_MSG,
                           getClass().getName(), entry.name(),
                           String.valueOf(entry.arity()));
            throw new UnexpectedException(msg, ex);
        }
        catch (DuplicateSymbolTableEntryException ex)
        {
            String msg = _resources.
                getMessage(DUPLICATE_UNSAFELY_ADDED_ENTRY_MSG,
                           getClass().getName(), entry.name(),
                           String.valueOf(entry.arity()));
            throw new UnexpectedException(msg, ex);
        }
    }


    /**
        Reports a duplicate symbol table entry in this table if the specified
        entry has arity zero and this table itself - and not an ancestor
        table - already contains a zero-arity entry with the same name.
        <p>
        This method is usually used to implement checkForDuplicate().

        @param entry the symbol table entry to look for duplicates of in
        this table
        @exception DuplicateSymbolTableEntryException thrown iff 'entry' is a
        zero-arity symbol and this table already contains a zero-arity entry
        with the same name
        @see #checkForDuplicate(SymbolTableEntry)
    */
    protected void reportZeroArityDuplicates(SymbolTableEntry entry)
        throws DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        if (entry.arity() == 0)
        {
            SymbolTableEntryList duplicates = find(entry.name(), 0);
            if (duplicates.isEmpty() == false)
            {
                throw new DuplicateSymbolTableEntryException(duplicates);
            }
        }
    }

    /**
        Reports a duplicate symbol table entry in this table if the specified
        entry has arity zero and this table or one of its ancestors already
        contains a zero-arity entry with the same name.
        <p>
        This method is usually used to implement checkForDuplicate().

        @param entry the symbol table entry to look for duplicates of in
        this table
        @exception DuplicateSymbolTableEntryException thrown iff 'entry' is a
        zero-arity symbol and this table or one of its ancestors already
        contains a zero-arity entry with the same name
        @see #checkForDuplicate(SymbolTableEntry)
    */
    protected void reportShadowedZeroArityDuplicates(SymbolTableEntry entry)
        throws DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        if (entry.arity() == 0)
        {
            SymbolTableEntryList
                duplicates = findRecursively(entry.name(), 0);
            if (duplicates.isEmpty() == false)
            {
                throw new DuplicateSymbolTableEntryException(duplicates);
            }
        }
    }

    /**
        @return this table's parent table
    */
    protected SymbolTable parent()
    {
        Assert.require(hasParent());

        Assert.ensure(_parent != null);
        return _parent;
    }

    /**
        @see MinimalAbstractSymbolTableEntry#displayAllFull(SymbolTableEntryList, String)
    */
    public static String displayAllFull(SymbolTableEntryList c, String sep)
    {
        Assert.require(c != null);
        Assert.require(sep != null);

        String result =
            MinimalAbstractSymbolTableEntry.displayAllFull(c, sep);

        Assert.ensure(result != null);
        return result;
    }


    // Private methods

    /**
        Sets the specified symbol table to be this symbol table's parent,
        replacing any existing parent.

        @param parent our new parent symbol table
    */
    private void setParent(SymbolTable parent)
    {
        Assert.require(parent != null);

        _parent = parent;
    }


    // Abstract methods

    /**
        Indicates whether a SymbolTableEntry with the specified kind can
        validly be added to this symbol table.

        @param kind the entry kind to check
        @return true iff an entry can have kind 'kind' and still validly be
        added to this symbol table
    */
    protected abstract boolean isValidEntryKind(int kind);

    /**
        Checks whether the specified entry is a duplicate of one already in
        this table, throwing an exception if it is.

        @param entry the symbol table entry to check
        @exception DuplicateSymbolTableEntryException thrown iff this
        table already contains an entry that is a duplicate of 'entry'
    */
    protected abstract void checkForDuplicate(SymbolTableEntry entry)
        throws DuplicateSymbolTableEntryException;
        // Assert.require(entry != null);


    // Inner classes

    /**
        A predicate satisfied only by those symbol table entries that have
        no arguments with invalid types.
        <p>
        Note: null entries have no arguments, and so satisfy this predicate.

        @see SymbolTableEntry#argumentTypes
    */
    private static class NoInvalidArgumentTypesPredicate
        implements UnarySymbolTableEntryPredicate
    {
        /**
            @see UnarySymbolTableEntryPredicate#isSatisfied(SymbolTableEntry)
        */
        public boolean isSatisfied(SymbolTableEntry item)
        {
            boolean result = true;

            if (item != null)
            {
                result =
                    AbstractType.areAllValidTypes(item.argumentTypes());
            }

            return result;
        }
    }
}
