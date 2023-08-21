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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.plack.common.base.InternalLanguageProcessingException;

import com.steelcandy.common.Resources;

/**
    The class of exception thrown when a construct is asked for
    correctness-related information once the construct's correctness
    has been established. It does <em>not</em> indicate an error in the
    source code being processed.
    <p>
    Instances of this class are usually constructed from the Construct whose
    correctness data is missing rather than an explicit message.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class MissingConstructCorrectnessDataException
    extends InternalLanguageProcessingException
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        MISSING_CORRECTNESS_DATA_MSG =
            "MISSING_CORRECTNESS_DATA_MSG";


    // Constructors

    /**
        Constructs a MissingConstructCorrectnessDataException.

        @param c the construct whose correctness data being missing is to be
        indicated by the exception
    */
    public MissingConstructCorrectnessDataException(Construct c)
    {
        this(createMessage(c));
    }

    /**
        Constructs a MissingConstructCorrectnessDataException from a
        construct and another exception.

        @param c the construct whose correctness data being missing is to be
        indicated by the exception
        @param ex the exception from which to construct the
        MissingConstructCorrectnessDataException
    */
    public MissingConstructCorrectnessDataException(Construct c,
                                                    Throwable ex)
    {
        this(createMessage(c), ex);
    }


    /**
        Constructs a MissingConstructCorrectnessDataException.

        @param msg a message describing why the exception was thrown
    */
    public MissingConstructCorrectnessDataException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a MissingConstructCorrectnessDataException.

        @param ex the exception from which to construct the exception
    */
    public MissingConstructCorrectnessDataException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs a MissingConstructCorrectnessDataException from a message
        describing why it occurred and another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the
        MissingConstructCorrectnessDataException
    */
    public MissingConstructCorrectnessDataException(String msg,
                                                    Throwable ex)
    {
        super(msg, ex);
    }


    // Protected static methods

    /**
        Returns the exception message used by default when an exception of
        this class is used to report that the specified construct is missing
        its correctness data.

        @param c the construct
        @return the exception message used by default in the exception that
        indicates that 'c' is missing its correctness data
        @see #MissingConstructCorrectnessDataException(Construct)
        @see #MissingConstructCorrectnessDataException(Construct, Throwable)
    */
    protected static String createMessage(Construct c)
    {
        // TODO: include the construct's location in the source code???!!!???
        return _resources.getMessage(MISSING_CORRECTNESS_DATA_MSG,
                                     c.getClass().getName());
    }
}
