/*
 Copyright (C) 2002 by James MacKay.

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

package com.steelcandy.common.program;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.SteelCandyException;

/**
    The base class for all program exceptions: exceptions that are thrown
    by a standalone program's top-level (or near top-level) methods.
    (Usually methods that are called directly by main()).
    <p>
    The message associated with an exception of this class (including
    those of all subclasses) should be displayable to an end user: it
    should be properly formatted for output, and should only contain
    user-domain terms and information. Thus code like the following
    should output properly formatted and user-friendly information
    about why a program failed:
    <pre>
        public static void main(String[] args)
        {
            result = 0;
            try
            {
                SomeClass obj = new SomeClass(args);
                obj.execute();
            }
            catch (ProgramException ex)
            {
                System.err.println(ex.getLocalizedMessage());
                result = 1;
            }

            System.exit(result);
        }</pre>
    (Note that calling System.exit() at the end of main() is not always
    appropriate, since it will cause the JVM to exit, which is not good
    if other programs are running in the same JVM.)

    @author James MacKay
*/
public class ProgramException
    extends SteelCandyException
{
    // Constructors

    /**
        Constructs a ProgramException.
    */
    public ProgramException()
    {
        // empty
    }

    /**
        Constructs a ProgramException.

        @param msg a (properly formatted) message describing why the
        exception was thrown
    */
    public ProgramException(String msg)
    {
        super(msg);
    }

    /**
        Constructs a ProgramException.

        @param ex the exception from which to construct an exception
    */
    public ProgramException(Throwable ex)
    {
        super(ex);
    }

    /**
        Constructs a ProgramException from a (properly formatted) message
        describing why it occurred, and from another exception.

        @param msg the message describing why the exception was thrown
        @param ex the exception from which to construct the
        ProgramException
    */
    public ProgramException(String msg, Throwable ex)
    {
        super(msg, ex);
    }
}
