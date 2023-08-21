/*
 Copyright (C) 2003-2004 by James MacKay.

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

import com.steelcandy.plack.common.base.PlackRuntimeException;

/**
    The class of exception thrown when an attempt is made to obtain
    a Construct associated with a SymbolTableEntry, but that construct
    has been removed from the entry.
    <p>
    Constructs are removed from entries once they have been completely
    processed in order to reduce the amount of memory used by an entry.
    They should not have been removed from an entry until the program
    part (class, module, etc.) that the entry represents a part of has
    been fully compiled.

    @author James MacKay
*/
public class EntryConstructRemovedException
    extends PlackRuntimeException
{
    // Constructors

    /**
        Constructs an EntryConstructRemovedException.
    */
    public EntryConstructRemovedException()
    {
        // empty
    }

    /**
        Constructs an EntryConstructRemovedException.

        @param msg a message describing why the exception was thrown
    */
    public EntryConstructRemovedException(String msg)
    {
        super(msg);
    }

    /**
        Constructs an EntryConstructRemovedException.

        @param ex the exception from which to construct the exception
    */
    public EntryConstructRemovedException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs an EntryConstructRemovedException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was
        thrown
        @param ex the exception from which to construct the
        exception
    */
    public EntryConstructRemovedException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
