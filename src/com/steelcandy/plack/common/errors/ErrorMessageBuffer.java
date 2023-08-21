/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

/**
    A buffer that can contain and concatenate zero or more error messages.
    It is usually used when multiple errors are reported as a single error.
    (For example, all of the documentation-related errors for a construct
    could be reported as a single compiler error.)
    <p>
    Note: this class is not designed to allow individual error messages to
    be extracted after they've been added.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class ErrorMessageBuffer
{
    // Constants

    /** The initial size of a buffer. */
    private static final int INITIAL_BUFFER_SIZE = 250;


    // Private fields

    /**
        The buffer used to hold the error messages. It will be null iff
        no error messages have been added to it yet.
    */
    private StringBuffer _buffer;

    /** The prefix to prepend to each error message. */
    private String _prefix;


    // Constructors

    /**
        Constructs an ErrorMessageBuffer.

        @param prefix the prefix to prepend to each error message before
        adding it to the buffer
    */
    public ErrorMessageBuffer(String prefix)
    {
        Assert.require(prefix != null);

        _buffer = null;
        _prefix = prefix;
    }


    // Public methods

    /**
        Adds the specified error message to this buffer.

        @param errorMessage the error message to add to this buffer
    */
    public void add(String errorMessage)
    {
        Assert.require(errorMessage != null);

        if (_buffer == null)
        {
            _buffer = new StringBuffer(INITIAL_BUFFER_SIZE);
        }
        _buffer.append(_prefix).append(errorMessage).append(Io.NL);
    }

    /**
        @return true iff this buffer contains one or more error messages
    */
    public boolean containsMessages()
    {
        return (_buffer != null);
    }

    /**
        @return the contents of this buffer concatenated together
    */
    public String contents()
    {
        String result;

        if (_buffer != null)
        {
            result = _buffer.toString();
        }
        else
        {
            result = "";
        }

        Assert.ensure(result != null);
        return result;
    }
}
