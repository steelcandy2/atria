/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common.io;

import com.steelcandy.common.debug.Assert;

import java.io.Writer;

/**
    A writer that prepends a fixed prefix to each line that it outputs.
    <p>
    An instance's prefix is the empty string unless and until another
    prefix is specified.

    @author James MacKay
*/
public class FixedPrefixWriter
    extends PrefixWriter
{
    // Constants

    /** The default prefix. */
    private static final String DEFAULT_PREFIX = "";


    // Private fields

    /** The prefix to prepend to each line. */
    private String _prefix;


    // Constructors

    /**
        Constructs a FixedPrefixWriter.

        @param w the writer to be wrapped
        @param prefix the prefix the writer is to prepend to each
        line it writes
        @param closeWrappedWriter true if the writer's close() method
        should attempt to close its wrapped writer, and false if it
        shouldn't
    */
    public FixedPrefixWriter(Writer w, String prefix,
                             boolean closeWrappedWriter)
    {
        super(w, closeWrappedWriter);
        Assert.require(prefix != null);

        setPrefix(prefix);
    }

    /**
        Constructs a FixedPrefixWriter whose prefix is initially the
        empty string.

        @param w the writer to be wrapped
        @param closeWrappedWriter true if the writer's close() method
        should attempt to close its wrapped writer, and false if it
        shouldn't
        @see #setPrefix(String)
    */
    public FixedPrefixWriter(Writer w, boolean closeWrappedWriter)
    {
        this(w, DEFAULT_PREFIX, closeWrappedWriter);
    }

    /**
        Constructs a FixedPrefixWriter that does <em>not</em> attempt
        to close its wrapped writer when it is closed. Its prefix is
        initially the empty string.

        @param w the writer to be wrapped
        @param prefix the prefix the writer is to prepend to each
        line it writes
    */
    public FixedPrefixWriter(Writer w, String prefix)
    {
        super(w);
        Assert.require(prefix != null);

        setPrefix(prefix);
    }

    /**
        Constructs a FixedPrefixWriter that does <em>not</em> attempt
        to close its wrapped writer when it is closed. Its prefix is
        initially the empty string.

        @param w the writer to be wrapped
        @see #setPrefix(String)
    */
    public FixedPrefixWriter(Writer w)
    {
        this(w, DEFAULT_PREFIX);
    }


    // Public methods

    /**
        Sets the prefix that this wrtier is to prepend to each line to the
        specified prefix.

        @param prefix the prefix that this writer is to prepend to each line
    */
    public void setPrefix(String prefix)
    {
        Assert.require(prefix != null);

        _prefix = prefix;
    }


    // Protected methods

    /**
        @see PrefixWriter#getPrefix(String, String)
    */
    protected String getPrefix(String line, String terminator)
    {
        Assert.require(line != null);
        Assert.require(terminator != null);
        Assert.require(line.length() > 0 || terminator.length() > 0);

        return _prefix;
    }
}
