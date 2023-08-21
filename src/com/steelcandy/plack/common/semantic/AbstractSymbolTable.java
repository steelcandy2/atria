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

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;

import java.util.Iterator;
import java.util.Map;

/**
    An abstract base class for SymbolTable classes.

    @author James MacKay
*/
public abstract class AbstractSymbolTable
    extends MinimalAbstractSymbolTable
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_NEXT_SYMBOL_TABLE_ENTRY_MSG =
            "NO_NEXT_SYMBOL_TABLE_ENTRY_MSG";
    private static final String
        NO_CURRENT_SYMBOL_TABLE_ENTRY_MSG =
            "NO_CURRENT_SYMBOL_TABLE_ENTRY_MSG";


    // Private fields

    /**
        A map whose values are lists of symbol table entries that
        all share the same name and arity, and whose keys consist
        of that name and arity. The keys are all SymbolTableEntryKey
        objects and the values are all SymbolTableEntryList objects
        (whose elements are all SymbolTableEntry objects).
    */
    private Map _entries;


    // Constructors

    /**
        Constructs an empty AbstractSymbolTable with no parent.
    */
    public AbstractSymbolTable()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
        Constructs an empty AbstractSymbolTable with the specified symbol
        table as its parent symbol table.

        @param parent the symbol table's parent
    */
    public AbstractSymbolTable(SymbolTable parent)
    {
        // Assert.require(parent != null);
        this(parent, DEFAULT_INITIAL_CAPACITY);
    }

    /**
        Constructs an empty AbstractSymbolTable with no parent and the
        specified initial capacity.

        @param initialCapacity the symbol table's initial capacity
    */
    public AbstractSymbolTable(int initialCapacity)
    {
        super();
        Assert.require(initialCapacity >= 0);

        _entries = createHashMap(initialCapacity);
    }

    /**
        Constructs an empty AbstractSymbolTable with the specified initial
        capacity, and that has the specified symbol table as its parent
        symbol table.

        @param parent the symbol table's parent
        @param initialCapacity the symbol table's initial capacity
    */
    public AbstractSymbolTable(SymbolTable parent, int initialCapacity)
    {
        super(parent);
        // Assert.require(parent != null);
        Assert.require(initialCapacity >= 0);

        _entries = createHashMap(initialCapacity);
    }


    // Public methods

    /**
        This implementation throws a DuplicateSymbolTableEntryException
        iff an attempt is made to add a zero-arity entry whose name is
        the same as a zero-arity entry that is already in this table.

        @see SymbolTable#add(SymbolTableEntry)
        @see #checkForDuplicate(SymbolTableEntry)
    */
    public void add(SymbolTableEntry entry)
        throws ImmutableSymbolTableException,
            InvalidSymbolTableEntryException,
            DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        checkValidEntry(entry);
        checkForDuplicate(entry);

        SymbolTableEntryKey key = createKey(entry);
        SymbolTableEntryList values = getEntryList(key);
        if (values == null)
        {
            values = createEntryList();
            try
            {
                _entries.put(key, values);
            }
            catch (UnsupportedOperationException ex)
            {
                throw new ImmutableSymbolTableException(ex);
            }
        }

        Assert.check(values != null);
        values.add(entry);
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
        return new EntryIterator();
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        Object key = _entries.keySet().iterator().next();
        SymbolTableEntryList list =
            (SymbolTableEntryList) _entries.get(key);
        Assert.check(list.isEmpty() == false);

        SymbolTableEntry result = list.getLast();
        list.removeLast();

        if (list.isEmpty())
        {
            _entries.remove(key);
        }

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

        SymbolTableEntryKey key =
            new SymbolTableEntryKey(name, arity);
        SymbolTableEntryList result = getEntryList(key);
        if (result == null)
        {
            // Return an empty list.
            result = createEntryList();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove(String, int)
    */
    public SymbolTableEntryList remove(String name, int arity)
        throws ImmutableSymbolTableException
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryKey key =
            new SymbolTableEntryKey(name, arity);
        SymbolTableEntryList result = removeEntryList(key);
        if (result == null)
        {
            // Return an empty list.
            result = createEntryList();
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

        SymbolTableEntryList result =
            findMatches(find(name, argumentTypes.size()),
                        argumentTypes, matcher);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList remove(String name, TypeList argumentTypes,
                                       ArgumentTypeMatcher matcher)
        throws ImmutableSymbolTableException
    {
        Assert.require(name != null);
        Assert.require(argumentTypes != null);
        Assert.require(argumentTypes.size() >= 0);
        Assert.require(matcher != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList();

        // Initially remove all of the symbols with matching name and
        // arity. We'll put back those whose arguments' types don't match.
        int arity = argumentTypes.size();
        SymbolTableEntryIterator iter = remove(name, arity).iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry entry = iter.next();
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
            else
            {
                unsafeAdd(entry);  // put 'entry' back in this table
            }
        }  // while

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        Creates and returns a SymbolTableEntryKey that corresponds to
        the specified SymbolTableEntry.

        @param entry the SymbolTableEntry
        @return a SymbolTableEntryKey that corresponds to 'entry'
    */
    protected SymbolTableEntryKey createKey(SymbolTableEntry entry)
    {
        Assert.require(entry != null);

        // Assert.ensure(result != null);
        return new SymbolTableEntryKey(entry);
    }

    /**
        @return an empty SymbolTableEntryList
    */
    protected SymbolTableEntryList createEntryList()
    {
        return SymbolTableEntryList.createLinkedList();
    }

    /**
        Returns the list of entries in this table that correspond to
        the specified key, or null if there are no such entries in
        this table.

        @param key the key
        @return a list of the entries in this table that correspond
        to 'key', or null if there are no such entries
    */
    protected SymbolTableEntryList getEntryList(SymbolTableEntryKey key)
    {
        Assert.require(key != null);

        SymbolTableEntryList result =
            (SymbolTableEntryList) _entries.get(key);

        // Note: 'result' may be null.
        return result;
    }

    /**
        Removes and returns the list of entries in this table that
        correspond to the specified key, or null if there are no such
        entries in this table.

        @param key the key
        @return a list of the entries in this table that correspond
        to 'key', or null if there are no such entries
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
    */
    protected SymbolTableEntryList removeEntryList(SymbolTableEntryKey key)
        throws ImmutableSymbolTableException
    {
        Assert.require(key != null);

        SymbolTableEntryList result;

        try
        {
            result = (SymbolTableEntryList) _entries.remove(key);
        }
        catch (UnsupportedOperationException ex)
        {
            throw new ImmutableSymbolTableException(ex);
        }

        // Note: 'result' may be null.
        return result;
    }


    // Inner classes

    /**
        The class of SymbolTableEntryIterator that iterates over the
        all of the entries in an AbstractSymbolTable.
    */
    protected class EntryIterator
        implements SymbolTableEntryIterator
    {
        // Private fields

        /**
            An iterator over our AbstractSymbolTable's _entries Map's
            values. Each item returned by the iterator is a
            SymbolTableEntryList.
            <p>
            This field should never be null once an instance's
            constructor has finished.
        */
        private Iterator _mapValuesIter;

        /**
            An iterator over the SymbolTableEntryList returned from
            _mapValuesIter that contains this iterator's current item.
            <p>
            This field should never be null once an instance's
            constructor has finished.
        */
        private SymbolTableEntryIterator _listIter;

        /**
            Indicates whether this iterator has a current value.
            (It is necessary since the current value could be null.)

            @see #_current
        */
        private boolean _hasCurrent;

        /**
            This iterator's current value: the value returned by
            <code>peek()</code> or <code>next()</code>.
            <p>
            This field may be null.
        */
        private SymbolTableEntry _current;


        // Constructors

        /**
            Constructs an EntryIterator that iterates over all of the
            SymbolTableEntries in its outer AbstractSymbolTable instance.
        */
        public EntryIterator()
        {
            _mapValuesIter = _entries.values().iterator();
            _listIter = EMPTY_ITERATOR;

            getNewCurrent();
        }


        // Public methods

        /**
            @see SymbolTableEntryIterator#hasNext
        */
        public boolean hasNext()
        {
            return _hasCurrent;
        }

        /**
            @see SymbolTableEntryIterator#next
        */
        public SymbolTableEntry next()
            throws NoSuchItemException
        {
            SymbolTableEntry result;

            if (_hasCurrent)
            {
                result = _current;
                getNewCurrent();
            }
            else
            {
                String msg = _resources.
                    getMessage(NO_NEXT_SYMBOL_TABLE_ENTRY_MSG,
                               getClass().getName());
                throw new NoSuchItemException(msg);
            }

            // 'result' may be null
            return result;
        }

        /**
            @see SymbolTableEntryIterator#peek
        */
        public SymbolTableEntry peek()
            throws NoSuchItemException
        {
            SymbolTableEntry result = _current;

            if (_hasCurrent == false)
            {
                String msg = _resources.
                    getMessage(NO_CURRENT_SYMBOL_TABLE_ENTRY_MSG,
                               getClass().getName());
                throw new NoSuchItemException(msg);
            }

            // 'result' may be null
            return result;
        }


        // Private methods

        /**
            Gets a new value for our _current field, setting _hasCurrent
            appropriately as well depending on whether there is a next
            SymbolTableEntry for this iterator to return.
        */
        private void getNewCurrent()
        {
            _hasCurrent = false;

            while (_listIter.hasNext() == false)
            {
                if (_mapValuesIter.hasNext() == false)
                {
                    break;  // while
                }

                SymbolTableEntryList entryList =
                    (SymbolTableEntryList) _mapValuesIter.next();
                _listIter = entryList.iterator();
            }

            if (_listIter.hasNext())
            {
                _current = _listIter.next();
                _hasCurrent = true;
            }
        }
    }
}
