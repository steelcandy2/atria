/*
 Copyright (C) 2003-2008 by James MacKay.

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

import com.steelcandy.plack.common.base.PlackException;

/**
    The class of exception thrown when an attempt is made to add an
    entry to a SymbolTable that already contains an entry that is
    the same as that entry and cannot be combined with, overridden
    by, or otherwise coexist with the existing entry.

    @author James MacKay
*/
public class DuplicateSymbolTableEntryException
    extends PlackException
{
    // Private fields

    /**
        The symbol table entries that the entry that was to be added
        was a duplicate of.
    */
    private SymbolTableEntryList _duplicates;


    // Constructors

    /**
        Constructs a DuplicateSymbolTableEntryException.

        @param duplicates the symbol table entries that matched the
        one to be found
    */
    public DuplicateSymbolTableEntryException(SymbolTableEntryList duplicates)
    {
        Assert.require(duplicates != null);
        Assert.require(duplicates.isEmpty() == false);

        _duplicates = duplicates;
    }

    /**
        Constructs a DuplicateSymbolTableEntryException.

        @param msg a message describing why the exception was thrown
        @param duplicates the symbol table entries that matched the
        one to be found
    */
    public DuplicateSymbolTableEntryException(String msg,
                                SymbolTableEntryList duplicates)
    {
        super(msg);
        Assert.require(duplicates != null);
        Assert.require(duplicates.isEmpty() == false);

        _duplicates = duplicates;
    }

    /**
        Constructs a DuplicateSymbolTableEntryException.

        @param ex the exception from which to construct the exception
        @param duplicates the symbol table entries that matched the
        one to be found
    */
    public DuplicateSymbolTableEntryException(Throwable ex,
                                SymbolTableEntryList duplicates)
    {
        super(ex);
        Assert.require(duplicates != null);
        Assert.require(duplicates.isEmpty() == false);

        _duplicates = duplicates;
    }

    /**
        Constructs a DuplicateSymbolTableEntryException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was
        thrown
        @param ex the exception from which to construct the
        exception
        @param duplicates the symbol table entries that matched the
        one to be found
    */
    public DuplicateSymbolTableEntryException(String msg, Throwable ex,
                                        SymbolTableEntryList duplicates)
    {
        super(msg, ex);
        Assert.require(duplicates != null);
        Assert.require(duplicates.isEmpty() == false);

        _duplicates = duplicates;
    }


    // Public methods

    /**
        @return the symbol table entries that matched the one that was
        to be added - that is, the ones that the entry to be added was
        a duplicate of
    */
    public SymbolTableEntryList duplicates()
    {
        Assert.ensure(_duplicates != null);
        Assert.ensure(_duplicates.isEmpty() == false);
        return _duplicates;
    }
}
