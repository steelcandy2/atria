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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;

/**
    The class of SymbolTable that contains only those entries in a given
    symbol table that satisfy a given predicate.
    <p>
    Note: any and all entries added to or removed from an instance of this
    class will be added to or removed from the symbol table that the instance
    was created from, though the results of remove() will be filtered by the
    instance's predicate.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class FilteredSymbolTable
    implements SymbolTable
{
    // Private fields

    /** The original symbol table. */
    private SymbolTable _table;

    /** The predicate to use to filter '_table''s entries to get ours. */
    private UnarySymbolTableEntryPredicate _filter;


    // Constructors

    /**
        Constructs a FilteredSymbolTable.

        @param table the symbol table whose entries the FilteredSymbolTable
        will contain a filtered subset of
        @param filter the symbol table entry predicate to use to determine
        which of 'table''s entries the FilteredSymbolTable will contain
    */
    public FilteredSymbolTable(SymbolTable table,
                               UnarySymbolTableEntryPredicate filter)
    {
        Assert.require(table != null);
        Assert.require(filter != null);

        _table = table;
        _filter = filter;
    }


    // Public methods

    /**
        @see SymbolTable#add(SymbolTableEntry)
    */
    public void add(SymbolTableEntry entry)
        throws ImmutableSymbolTableException,
            InvalidSymbolTableEntryException,
            DuplicateSymbolTableEntryException
    {
        Assert.require(entry != null);

        _table.add(entry);
    }

    /**
        @see SymbolTable#isEmpty
    */
    public boolean isEmpty()
    {
        boolean result = true;

        SymbolTableEntryIterator iter = _table.iterator();
        while (iter.hasNext())
        {
            if (_filter.isSatisfied(iter.next()))
            {
                result = false;
                break;  // while
            }
        }

        return result;
    }

    /**
        @see SymbolTable#hasParent
    */
    public boolean hasParent()
    {
        return _table.hasParent();
    }

    /**
        @see SymbolTable#iterator
    */
    public SymbolTableEntryIterator iterator()
    {
        SymbolTableEntryIterator result =
            new FilteredIterator(_table.iterator(), _filter);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        SymbolTableEntry result = null;

        SymbolTableEntryList toAddBack =
            SymbolTableEntryList.createArrayList();
        while (true)
        {
            result = _table.remove();
            if (_filter.isSatisfied(result))
            {
                // Note: since isEmpty() is false (by our precondition)
                // there must be at least one entry in _table that satisfies
                // _filter.
                break;
            }
            else
            {
                toAddBack.add(result);
            }
        }

        SymbolTableEntryIterator iter = toAddBack.iterator();
        try
        {
            while (iter.hasNext())
            {
                _table.add(iter.next());
            }
        }
        catch (InvalidSymbolTableEntryException ex)
        {
            Assert.unreachable();
                // since the entries were already in _table
        }
        catch (DuplicateSymbolTableEntryException ex)
        {
            Assert.unreachable();
                // because the entries weren't duplicates when they were in
                // _table before.
        }
        catch (ImmutableSymbolTableException ex)
        {
            Assert.unreachable();
                // since we removed entries from it above
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

        SymbolTableEntryList result = filter(_table.find(name, arity));

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#findRecursively(String, int)
    */
    public SymbolTableEntryList findRecursively(String name, int arity)
    {
        Assert.require(name != null);
        Assert.require(arity >= 0);

        SymbolTableEntryList result =
            filter(_table.findRecursively(name, arity));

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

        SymbolTableEntryList result =
            filter(_table.parentFindRecursively(name, arity));

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

        SymbolTableEntryList result = filter(_table.remove(name, arity));

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
            filter(_table.find(name, argumentTypes, matcher));

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

        SymbolTableEntryList result =
            filter(_table.findRecursively(name, argumentTypes, matcher));

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

        SymbolTableEntryList result = filter(_table.
            parentFindRecursively(name, argumentTypes, matcher));

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
            filter(_table.remove(name, argumentTypes, matcher));

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#removeConstructs
    */
    public void removeConstructs()
    {
        _table.removeConstructs();
    }


    // Protected methods

    /**
        @param list a list of symbol table entries
        @return those entries of 'list' - in order - that satisfy our
        entry filter predicate
    */
    protected SymbolTableEntryList filter(SymbolTableEntryList list)
    {
        Assert.require(list != null);

        SymbolTableEntryList result =
            SymbolTableEntryList.createArrayList(list.size());

        SymbolTableEntryIterator iter = list.iterator();
        while (iter.hasNext())
        {
            SymbolTableEntry entry = iter.next();
            if (_filter.isSatisfied(entry))
            {
                result.add(entry);
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() <= list.size());
        return result;
    }


    // Inner classes

    /**
        The class of SymbolTableEntryIterator that only returns entries
        in another iterator that satisfy a given predicate.
    */
    private static class FilteredIterator
        implements SymbolTableEntryIterator
    {
        // Private fields

        /** The iterator whose results we filter to get our results. */
        private SymbolTableEntryIterator _iterator;

        /** The predicate we use to filter '_iterator''s items. */
        private UnarySymbolTableEntryPredicate _filter;


        // Constructors

        /**
            Constructs a FilteredIterator.

            @param iter the iterator whose results the FilteredIterator will
            filter to get its results
            @param filter the predicate that the FilteredIterator will  use
            to filter 'iter''s results
        */
        public FilteredIterator(SymbolTableEntryIterator iter,
                                UnarySymbolTableEntryPredicate filter)
        {
            Assert.require(iter != null);
            Assert.require(filter != null);

            _iterator = iter;
            _filter = filter;
        }


        // Public methods

        /**
            @see SymbolTableEntryIterator#hasNext
        */
        public boolean hasNext()
        {
            boolean result = false;

            while (_iterator.hasNext())
            {
                if (_filter.isSatisfied(_iterator.peek()))
                {
                    result = true;
                    break;
                }
                else
                {
                    // Discard the next item in _iterator, since it can't be
                    // returned by this iterator.
                    _iterator.next();
                }
            }

            return result;
        }

        /**
            @see SymbolTableEntryIterator#next
        */
        public SymbolTableEntry next()
            throws NoSuchItemException
        {
            SymbolTableEntry result;

            if (hasNext())
            {
                result = _iterator.next();
            }
            else
            {
                throw new NoSuchItemException();
            }

            // 'result' can be null
            Assert.ensure(_filter.isSatisfied(result));
            return result;
        }

        /**
            @see SymbolTableEntryIterator#peek
        */
        public SymbolTableEntry peek()
            throws NoSuchItemException
        {
            SymbolTableEntry result;

            if (hasNext())
            {
                result = _iterator.peek();
            }
            else
            {
                throw new NoSuchItemException();
            }

            // 'result' can be null
            Assert.ensure(_filter.isSatisfied(result));
            return result;
        }
    }
}
