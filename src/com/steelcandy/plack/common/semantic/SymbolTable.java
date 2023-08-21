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

import com.steelcandy.plack.common.constructs.SymbolTableAttribute;  // javadocs only

/**
    The interface implemented by all classes that represent a
    symbol table.

    @author James MacKay
    @version $Revision: 1.21 $
    @see SymbolTableAttribute
    @see SymbolTableEntry
*/
public interface SymbolTable
{
    // Public methods

    /**
        Adds the specified entry to this symbol table.

        @param entry the entry to add to this symbol table
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
        @exception InvalidSymbolTableEntryException may be thrown if
        'entry' is not a valid entry of this table
        @exception DuplicateSymbolTableEntryException thrown if this
        symbol table already contains an entry that is the same as 'entry'
        and cannot be combined with, overridden by, etc. by 'entry'
    */
    public void add(SymbolTableEntry entry)
        throws ImmutableSymbolTableException,
            InvalidSymbolTableEntryException,
            DuplicateSymbolTableEntryException;
        // Assert.require(entry != null);

    /**
        @return true iff there are no symbol table entries in this table
    */
    public boolean isEmpty();

    /**
        @return true iff this table has a parent table
    */
    public boolean hasParent();

    /**
        Note: entries in this table's ancestors are not returned by the
        iterator.

        @return an iterator over all of the entries in this table:
        the order in which they're returned is not guaranteed
    */
    public SymbolTableEntryIterator iterator();
        // Assert.ensure(result != null);

    /**
        Removes an arbitrary entry from this symbol table and returns it.

        @return the entry that was removed
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
    */
    public SymbolTableEntry remove()
        throws ImmutableSymbolTableException;
        // Assert.require(isEmpty() == false);
        // Assert.ensure(result != null);


    /**
        Returns a (possibly empty) list of the entries in this table with
        the specified name and arity. This table's parent table, if any,
        is not searched.

        @param name the name of the entries to find
        @param arity the arity of the entries to find
        @return all of the entries in this table with name 'name' and
        arity 'arity'
    */
    public SymbolTableEntryList find(String name, int arity);
        // Assert.require(name != null);
        // Assert.require(arity >= 0);
        // Assert.ensure(result != null);

    /**
        Returns a list of the entries in this table with the specified
        name and arity, or if there are no such entries in this table
        then returns the results of finding matching entries recursively
        in this table's parent table, if it has one.

        @param name the name of the entries to find
        @param arity the arity of the entries to find
        @return all of the entries in this table with name 'name' and
        arity 'arity', or if there are no such entries then the results
        of searching for matching entries recursively in this table's
        parent table (if it has one)
    */
    public SymbolTableEntryList findRecursively(String name, int arity);
        // Assert.require(name != null);
        // Assert.require(arity >= 0);
        // Assert.ensure(result != null);

    /**
        Returns a list of the entries with the specified name and arity that
        are found recursively in our parent symbol table, or an empty list if
        we don't have a parent table.

        @param name the name of the entries to find
        @param arity the arity of the entries to find
        @return an empty list if we have no parent symbol table; otherwise
        all of the entries in our parent table with name 'name' and arity
        'arity', or if there are no such entries then the results of
        searching for matching entries recursively in our parent table's
        parent table (if it has one)
        @see #findRecursively(String, int)
    */
    public SymbolTableEntryList
        parentFindRecursively(String name, int arity);
        // Assert.require(name != null);
        // Assert.require(arity >= 0);
        // Assert.ensure(result != null);

    /**
        Removes and returns a (possibly empty) list of the entries in
        this table with the specified name and arity.

        @param name the name of the entries to remove
        @param arity the arity of the entries to remove
        @return all of the entries in this table with name 'name' and
        arity 'arity'
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
    */
    public SymbolTableEntryList remove(String name, int arity)
        throws ImmutableSymbolTableException;
        // Assert.require(name != null);
        // Assert.require(arity >= 0);
        // Assert.ensure(result != null);


    /**
        Returns a (possibly empty) list of the entries in this table with
        the specified name that are such that
        <ul>
            <li>they have the same number of arguments <em>n</em> as
                there are types in 'argumentTypes', and
            <li>for 1 &lt;= <em>i</em> &lt;= <em>n</em>, the <em>i</em>th
                Type in 'argumentTypes' matches their <em>i</em>th
                argument's type
        </ul>

        @param name the name of the entries to find
        @param argumentTypes the types that match the argument types
        of the entries to find
        @param matcher the argument type matcher that is used to determine
        whether a type from 'argumentTypes' matches the type of an entry's
        argument
        @return all of the entries in this table with name 'name' whose
        arguments' types match those in 'argumentTypes'
    */
    public SymbolTableEntryList find(String name, TypeList argumentTypes,
                                     ArgumentTypeMatcher matcher);
        // Assert.require(name != null);
        // Assert.require(argumentTypes != null);
        // Assert.require(argumentTypes.size() >= 0);
        // Assert.require(matcher != null);
        // Assert.ensure(result != null);

    /**
        Returns a list of the entries in this table with the specified
        name that are such that
        <ul>
            <li>they have the same number of arguments <em>n</em> as
                there are types in 'argumentTypes', and
            <li>for 1 &lt;= <em>i</em> &lt;= <em>n</em>, the <em>i</em>th
                Type in 'argumentTypes' matches their <em>i</em>th
                argument's type
        </ul>
        or if there are no such entries in this table then returns
        the results of finding matching entries recursively in this
        table's parent table, if it has one.

        @param name the name of the entries to find
        @param argumentTypes the types that match the argument types
        of the entries to find
        @param matcher the argument type matcher that is used to determine
        whether a type from 'argumentTypes' matches the type of an entry's
        argument
        @return all of the entries in this table with name 'name' whose
        arguments' types match those in 'argumentTypes', or if there are
        no such entries then the results of searching for matching
        entries recursively in this table's parent table (if it has one)
    */
    public SymbolTableEntryList findRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher);
        // Assert.require(name != null);
        // Assert.require(argumentTypes != null);
        // Assert.require(argumentTypes.size() >= 0);
        // Assert.require(matcher != null);
        // Assert.ensure(result != null);

    /**
        Returns an empty list if we have no parent symbol table; otherwise
        returns a list of the entries in our parent symbol table with the
        specified name that are such that
        <ul>
            <li>they have the same number of arguments <em>n</em> as there
                are types in 'argumentTypes', and
            <li>for 1 &lt;= <em>i</em> &lt;= <em>n</em>, the <em>i</em>th
                Type in 'argumentTypes' matches their <em>i</em>th
                argument's type
        </ul>
        or if there are no such entries in our parent table then returns the
        results of finding matching entries recursively in our parent table's
        parent table, if it has one.

        @param name the name of the entries to find
        @param argumentTypes the types that match the argument types
        of the entries to find
        @param matcher the argument type matcher that is used to determine
        whether a type from 'argumentTypes' matches the type of an entry's
        argument
        @return an empty list if we don't have a parent symbol table;
        otherwise all of the entries in our parent table with name 'name'
        whose arguments' types match those in 'argumentTypes', or if there are
        no such entries then the results of searching for matching entries
        recursively in our parent table's parent table (if it has one)
        @see #findRecursively(String, TypeList, ArgumentTypeMatcher)
    */
    public SymbolTableEntryList parentFindRecursively(String name,
                        TypeList argumentTypes, ArgumentTypeMatcher matcher);
        // Assert.require(name != null);
        // Assert.require(argumentTypes != null);
        // Assert.require(argumentTypes.size() >= 0);
        // Assert.require(matcher != null);
        // Assert.ensure(result != null);

    /**
        Removes and returns a (possibly empty) list of the entries in
        this table with the specified name that are such that
        <ul>
            <li>they have the same number of arguments <em>n</em> as
                there are types in 'argumentTypes', and
            <li>for 1 &lt;= <em>i</em> &lt;= <em>n</em>, their
                <em>i</em>'th argument's type is equal to the
                <em>i</em>'th Type in 'argumentTypes'
        </ul>

        @param name the name of the entries to remove
        @param argumentTypes the types that match the argument types
        of the entries to remove
        @param matcher the argument type matcher that is used to determine
        whether a type from 'argumentTypes' matches the type of an entry's
        argument
        @return all of the entries in this table with name 'name' whose
        arguments' types match those in 'argumentTypes'
        @exception ImmutableSymbolTableException thrown if this method is
        called on an immutable symbol table
    */
    public SymbolTableEntryList remove(String name, TypeList argumentTypes,
                                       ArgumentTypeMatcher matcher)
        throws ImmutableSymbolTableException;
        // Assert.require(name != null);
        // Assert.require(argumentTypes != null);
        // Assert.require(argumentTypes.size() >= 0);
        // Assert.require(matcher != null);
        // Assert.ensure(result != null);


    /**
        Remove any and all constructs from this table and all of its entries
        (to save memory once it has been fully compiled), if they haven't
        been already. (So instances have to be able to handle this method
        being called multiple times.)

        @see SymbolTableEntry#removeConstructs
    */
    public void removeConstructs();
}
