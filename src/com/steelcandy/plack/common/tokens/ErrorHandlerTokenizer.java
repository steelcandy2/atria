/*
 Copyright (C) 2001-2004 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    An abstract base class for tokenizers that manage an error handler.

    @author James MacKay
    @version $Revision: 1.5 $
*/
public abstract class ErrorHandlerTokenizer
    implements Tokenizer
{
    // Private fields

    /** This tokenizer's error handler. */
    private ErrorHandler _handler;


    // Constructors

    /**
        Constructs an ErrorHandlerTokenizer from the error handler it is to
        use to handle all errors.

        @param handler the error handler the tokenizer is to use to handle
        all errors
    */
    public ErrorHandlerTokenizer(ErrorHandler handler)
    {
        Assert.require(handler != null);

        setErrorHandler(handler);
    }

    /**
        Constructs an ErrorHandlerTokenizer that doesn't have an error
        handler set (yet).
        <p>
        This constructor should not be used unless absolutely necessary: all
        of a tokenizer's constructors should require they be passed an error
        handler (so that the tokenizer always has an error handler).
    */
    protected ErrorHandlerTokenizer()
    {
        _handler = null;
    }


    // Public methods

    /**
        @see Tokenizer#setErrorHandler
    */
    public void setErrorHandler(ErrorHandler handler)
    {
        Assert.require(handler != null);

        _handler = handler;
    }

    /**
        @see Tokenizer#errorHandler
    */
    public ErrorHandler errorHandler()
    {
        return _handler;
    }
}
