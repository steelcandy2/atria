/*
 Copyright (C) 2001-2005 by James MacKay.

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

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.common.creation.ObjectCreator;
import com.steelcandy.common.creation.ObjectCreatorException;

import java.lang.reflect.Method;

/**
    Represents an internal error: an error that should not occur if the
    source code processor is working properly.

    @author James MacKay
    @version $Revision: 1.9 $
*/
public class PlackInternalError
    extends AbstractPlackError
{
    // Private fields

    /** The class in which the error occurred. */
    private Class _sourceClass;

    /** The object in which the error occurred. */
    private Object _sourceObject;


    // Constructors

    /**
        Constructs a PlackInternalError.

        @param level the error's severity level
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param c the class in which the error occurred
        @param obj the object in which the error occurred
    */
    public PlackInternalError(int level, String description,
                              SourceCode code, SourceLocation loc,
                              Class c, Object obj)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'c' and/or 'obj' can be null
        super(level, description, code, loc);

        _sourceClass = c;
        _sourceObject = obj;
    }

    /**
        Constructs a PlackInternalError.

        @param level the error's severity level
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param className the fully-qualified class name of the class in which
        the error occurred
        @param obj the object in which the error occurred
    */
    public PlackInternalError(int level, String description,
                              SourceCode code, SourceLocation loc,
                              String className, Object obj)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'className' and/or 'obj' can be null
        super(level, description, code, loc);

        _sourceClass = classFromName(className);
        _sourceObject = obj;
    }

    /**
        Constructs a PlackInternalError.

        @param level the error's severity level
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param stackTrace the stack trace from an exception that signalled an
        internal error
    */
    public PlackInternalError(int level, String description,
                              SourceCode code, SourceLocation loc,
                              StackTraceElement[] stackTrace)
    {
        // Assert.require(description != null);
        // 'code' and/or 'loc' can be null
        // 'stackTrace' can be null
        super(level, description, code, loc);

        _sourceObject = null;
        if (stackTrace != null && stackTrace.length > 0)
        {
            _sourceClass = classFromName(stackTrace[0].getClassName());
        }
        else
        {
            _sourceClass = null;
        }
    }


    // Public methods

    /**
        @return the class in which this error occurred
    */
    public Class sourceClass()
    {
        Class result = _sourceClass;

        // 'result' may be null
        return result;
    }

    /**
        @return the object in which this error occurred
    */
    public Object sourceObject()
    {
        Object result = _sourceObject;

        // 'result' may be null
        return result;
    }


    // Protected methods

    /**
        Returns the Class object representing the class with the specified
        fully-qualified class name, or null if there is no such class.

        @param name the fully-qualified class name
        @return the Class object representing the class named 'name'
    */
    protected Class classFromName(String name)
    {
        // 'name' can be null

        Class result;

        if (name != null)
        {
            try
            {
                result = ObjectCreator.instance().forName(name);
            }
            catch (ObjectCreatorException ex)
            {
                result = null;
            }
        }
        else
        {
            result = null;
        }

        // 'result' may be null
        return result;
    }
}
