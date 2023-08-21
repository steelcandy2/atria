/*
 Copyright (C) 2004-2005 by James MacKay.

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
    An abstract base class for symbol tables that delegate some or all of
    their implementation to another instance, but that transform each of the
    entries from that instance in some way before returning them.
    <p>
    Note: by default entries add()ed to an instance of this class are
    <em>not</em> transform()ed first.
    <p>
    Subclasses just have to implement transform(SymbolTableEntry) and
    delegate(), though they may want to override other methods if they don't
    want them delegated and/or their results transformed.

    @author James MacKay
    @version $Revision: 1.2 $
    @see SymbolTable#add(SymbolTableEntry)
*/
public abstract class AbstractTransformingSymbolTable
    extends AbstractDelegatingSymbolTable
{
    // Public methods

    /**
        @see SymbolTable#iterator
    */
    public SymbolTableEntryIterator iterator()
    {
        SymbolTableEntryIterator result =
            new EntryIterator(super.iterator());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SymbolTable#remove
    */
    public SymbolTableEntry remove()
    {
        Assert.require(isEmpty() == false);

        SymbolTableEntry result = transform(super.remove());

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

        SymbolTableEntryList result = transform(super.find(name, arity));

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
            transform(super.findRecursively(name, arity));

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
            transform(super.parentFindRecursively(name, arity));

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

        SymbolTableEntryList result =
            transform(super.remove(name, arity));

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
            transform(super.find(name, argumentTypes, matcher));

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
            transform(super.findRecursively(name, argumentTypes, matcher));

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

        SymbolTableEntryList result = transform(super.
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
            transform(super.remove(name, argumentTypes, matcher));

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @param entries a list of entries from this table
        @return a list of the results of transforming the entries in
        'entries'
        @see #transform(SymbolTableEntry)
    */
    protected SymbolTableEntryList transform(SymbolTableEntryList entries)
    {
        Assert.require(entries != null);

        SymbolTableEntryList result;

        int numEntries = entries.size();
        if (numEntries == 0)
        {
            result = entries;
        }
        else
        {
            result = SymbolTableEntryList.createArrayList(numEntries);
            SymbolTableEntryIterator iter = entries.iterator();
            while (iter.hasNext())
            {
                result.add(transform(iter.next()));
            }
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == entries.size());
        return result;
    }


    // Abstract methods

    /**
        @param entry one of our entries
        @return the entry to transform it into before returning it
    */
    protected abstract SymbolTableEntry transform(SymbolTableEntry entry);
        // Assert.require(entry != null);
        // Assert.ensure(result != null);


    // Inner classes

    /**
        The class of SymbolTableEntryIterator returned by our iterator()
        method.
    */
    private class EntryIterator
        implements SymbolTableEntryIterator
    {
        // Private fields

        /** The iterator whose items we return transformed versions of. */
        private SymbolTableEntryIterator _iterator;


        // Constructors

        /**
            Constructs an EntryIterator.

            @param iter the iterator whose items the iterator is to return
            transformed versions of
        */
        public EntryIterator(SymbolTableEntryIterator iter)
        {
            Assert.require(iter != null);

            _iterator = iter;
        }


        // Public methods

        /**
            @see SymbolTableEntryIterator#hasNext
        */
        public boolean hasNext()
        {
            return _iterator.hasNext();
        }

        /**
            @see SymbolTableEntryIterator#next
        */
        public SymbolTableEntry next()
            throws NoSuchItemException
        {
            return transform(_iterator.next());
        }

        /**
            @see SymbolTableEntryIterator#peek
        */
        public SymbolTableEntry peek()
            throws NoSuchItemException
        {
            return transform(_iterator.peek());
        }
    }
}
