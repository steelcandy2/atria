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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Parser;

/**
    An abstract base class for error handlers that maintains counts of the
    number of errors of each severity level that have been handled by a
    subclass instance.

    @author James MacKay
    @see ErrorHandler#fatalErrorCount
    etc.
*/
public abstract class AbstractCountingErrorHandler
    extends AbstractErrorHandler
    implements ErrorSeverityLevels
{
    // Private fields

    /** An array of error counts, indexed by error severity level. */
    private int[] _counts;


    // Constructors

    /**
        Constructs an AbstractErrorHandler.
    */
    public AbstractCountingErrorHandler()
    {
        // All of _count's elements are initialized to zero.
        _counts = new int[NUMBER_OF_ERROR_SEVERITY_LEVELS];
    }


    // Error count methods

    /**
        @see ErrorHandler#fatalErrorCount
    */
    public int fatalErrorCount()
    {
        // Assert.ensure(result >= 0);
        return _counts[FATAL_ERROR_LEVEL];
    }

    /**
        @see ErrorHandler#nonFatalErrorCount
    */
    public int nonFatalErrorCount()
    {
        // Assert.ensure(result >= 0);
        return _counts[NON_FATAL_ERROR_LEVEL];
    }

    /**
        @see ErrorHandler#warningErrorCount
    */
    public int warningErrorCount()
    {
        // Assert.ensure(result >= 0);
        return _counts[WARNING_LEVEL];
    }

    /**
        @see ErrorHandler#informationErrorCount
    */
    public int informationErrorCount()
    {
        // Assert.ensure(result >= 0);
        return _counts[INFO_LEVEL];
    }

    /**
        @see ErrorHandler#debugErrorCount
    */
    public int debugErrorCount()
    {
        // Assert.ensure(result >= 0);
        return _counts[DEBUG_LEVEL];
    }


    // Protected methods

    /**
        @see AbstractErrorHandler#beforeHandlingError(PlackError)
    */
    protected void beforeHandlingError(PlackError error)
    {
        Assert.require(error != null);

        super.beforeHandlingError(error);
        incrementCount(error);
    }

    /**
        Increments the proper error count to include the specified error.

        @param error the error to count
    */
    protected void incrementCount(PlackError error)
    {
        _counts[error.level()] += 1;
    }
}
