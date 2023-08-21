/*
 Copyright (C) 2003-2009 by James MacKay.

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
    An abstract base class for SymbolTables that only contain zero-arity
    symbols, and that can contain at most one symbol with a given name.
    <p>
    Note that any parent or ancestor tables that an instance may have are not
    restricted to zero-arity symbols.

    @author James MacKay
    @version $Revision: 1.8 $
*/
public abstract class AbstractZeroAritySymbolTable
    extends MinimalAbstractSymbolTable
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_NONZERO_ARITY_ENTRY_MSG =
            "INVALID_NONZERO_ARITY_ENTRY_MSG";


    // Private fields

    /**
        A map from (zero arity) symbol name to the entry with that name.
        The keys are Strings and the values are SymbolTableEntry objects.
    */
    private Map _entries;


    // Constructors

    /**
        Constructs a AbstractZeroAritySymbolTable with no parent.
    */
    public AbstractZeroAritySymbolTable()
    {
        _entries = new HashMap();
    }

    /**
        Constructs an empty AbstractZeroAritySymbolTable with the specified
        symbol table as its parent symbol table.

        @param parent the symbol table's parent
    */
    public AbstractZeroAritySymbolTable(SymbolTable parent)
    {
        // Assert.require(parent != null);
        this(parent, DEFAULT_INITIAL_CAPACITY);
    }

    /**
        Constructs an empty AbstractZeroAritySymbolTable with no parent and
        the specified initial capacity.

        @param initialCapacity the symbol table's initial capacity
    */
    public AbstractZeroAritySymbolTable(int initialCapacity)
    {
        super();
        Assert.require(initialCapacity >= 0);

        _entries = createHashMap(initialCapacity);
    }

    /**
        Constructs an empty AbstractZeroAritySymbolTable with the specified
        initial capacity, and that has the specified symbol table as its
        parent symbol table.

        @param parent the symbol table's parent
        @param initialCapacity the symbol table's initial capacity
    */
    public AbstractZeroAritySymbolTable(SymbolTable parent,
                                        int initialCapacity)
    {
        super(parent);
        // Assert.require(parent != null);
        Assert.require(initialCapacity >= 0);

        _entries = createHashMap(initialCapacity);
    }


    // Public methods

    /**
        This implementation throws
        <ul>
            <li>an InvalidSymbolTableEntryException iff an attempt is
                made to add an entry whose arity is nonzero</li>
            <li>a DuplicateSymbolTableEntryException iff an attempt is
                made to add a zero-arity entry whose name is the same
                as that of an entry already in this table</li>
        </ul>

        @see SymbolTable#add(SymbolTableEntry)
        @see #checkValidEntry(SymbolTableEntry)
        @see MinimalAbstractSymbolTable#checkForDuplicate(SymbolTableEntry)
    */
    public void add(SymbolTableEntry entry)
        throws InvalidSymbolTableEntryException,
               DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        checkValidEntry(entry);
        checkForDuplicate(entry);

        Object old = _entries.put(entry.name(), entry);
        Assert.check(old == null);
            // i.e. that there wasn't already an entry with the same name
    }

    /**
        @see SymbolTable#isEmpty
    */
    public boolean isEmpty()
    {
        return _entries.isEmpty();
    }

    /**
        @see SymbolTable#iterator
    */
    public SymbolTableEntryIterator iterator()
    {
        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(_entries.size());

        Iterator iter = _entries.values().iterator();
        while (iter.hasNext())
        {
            result.add((SymbolTableEntry) iter.next());
        }

        return result.iterator();
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        Object key = _entries.keySet().iterator().next();

        SymbolTableEntry result =
            (SymbolTableEntry) _entries.remove(key);

        Assert.ensure(result != null);
        return result;
    }


    /**
        @see SymbolTable#find(String, int)
    */
    public SymbolTableEntryList find(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result = emptyList();

        if (arity == 0)
        {
            SymbolTableEntry entry =
                (SymbolTableEntry) _entries.get(name);
            if (entry != null)
            {
                result = createList(entry);
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove(String, int)
    */
    public SymbolTableEntryList remove(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result = emptyList();

        if (arity == 0)
        {
            SymbolTableEntry entry =
                (SymbolTableEntry) _entries.remove(name);
            if (entry != null)
            {
                result = createList(entry);
            }
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#find(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList find(String name, TypeList argumentTypes,
                                     ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Assert.ensure(result != null);
        return find(name, argumentTypes.size());
    }

    /**
        @see SymbolTable#remove(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList remove(String name, TypeList argumentTypes,
                                       ArgumentTypeMatcher matcher)
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        // Assert.ensure(result != null);
        return remove(name, argumentTypes.size());
    }


    // Protected methods

    /**
        This implementation considers an entry to be valid iff it is a zero-
        arity entry and is considered valid by our superclass' implementation
        of this method.

        @see MinimalAbstractSymbolTable#checkValidEntry(SymbolTableEntry)
    */
    protected void checkValidEntry(SymbolTableEntry entry)
        throws InvalidSymbolTableEntryException
    {
        Assert.require(entry != null);

        super.checkValidEntry(entry);
        if (entry.arity() != 0)
        {
            String msg = _resources.
                getMessage(INVALID_NONZERO_ARITY_ENTRY_MSG,
                           String.valueOf(entry.arity()),
                           getClass().getName(),
                           entry.getClass().getName());
            throw new InvalidSymbolTableEntryException(msg);
        }
    }


    // Protected utility methods

    /**
        @return an unmodifiable empty SymbolTableEntryList
    */
    protected SymbolTableEntryList emptyList()
    {
        return SymbolTableEntryList.createEmptyList();
    }

    /**
        Creates and returns an unmodifiable list that contains the
        specified entry as its sole element.

        @param entry the entry that the list is to have as its sole
        element
        @return an unmodifiable list with 'entry' as its sole element
    */
    protected SymbolTableEntryList createList(SymbolTableEntry entry)
    {
        // 'entry' can be null

        return SymbolTableEntryList.createSingleItemList(entry);
    }

    /**
        @see MinimalAbstractSymbolTable#removeEntryConstructs
    */
    protected void removeEntryConstructs()
    {
        Iterator iter = _entries.values().iterator();
        while (iter.hasNext())
        {
            ((SymbolTableEntry) iter.next()).removeConstructs();
        }
    }
}
