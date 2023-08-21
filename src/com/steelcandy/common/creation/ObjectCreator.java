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

package com.steelcandy.common.creation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

import java.lang.reflect.*;

/**
    A singleton class that creates objects using reflection.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class ObjectCreator
{
    // Constants

    /**  The single instance of this class. */
    private static final ObjectCreator _instance = new ObjectCreator();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static ObjectCreator instance()
    {
        return _instance;
    }

    /**
        Default constructor.
    */
    private ObjectCreator()
    {
        // empty
    }


    // Public methods

    /**
        Creates and returns an instance of the specified class using
        the default constructor.

        @param c the class to create an instance of
        @return an instance of the specified class, constructed using
        the default constructor
        @exception ObjectCreatorException thrown if the instance could
        not be created
    */
    public Object create(Class c)
        throws ObjectCreatorException
    {
        return create(c, new Class[0], new Object[0]);
    }

    /**
        Creates and returns an instance of the class with the
        specified fully-qualified name using the default constructor.

        @param className the fully-qualified class name of the class
        to create an instance of
        @return an instance of the specified class, constructed using
        the default constructor
        @exception ObjectCreatorException thrown if the instance could
        not be created
        @see #create(Class)
    */
    public Object create(String className)
        throws ObjectCreatorException
    {
        return create(forName(className));
    }

    /**
        Creates and returns an instance of the specified class using
        the constructor that takes the specified types of arguments,
        passing it the specified arguments.

        @return an instance of the specified class
        @exception ObjectCreatorException thrown if the instance could
        not be created
    */
    public Object create(Class c, Class[] ctorArgTypes, Object[] ctorArgs)
        throws ObjectCreatorException
    {
        Constructor ctor = null;
        try
        {
            ctor = c.getConstructor(ctorArgTypes);
        }
        catch (SecurityException ex)
        {
            throw ObjectCreatorException.
                    createForSecurityRestriction(c, ctorArgTypes);
        }
        catch (NoSuchMethodException ex)
        {
            throw ObjectCreatorException.
                    createForNoSuchConstructor(c, ctorArgTypes);
        }

        try
        {
            return ctor.newInstance(ctorArgs);
        }
        catch (InstantiationException ex)
        {
            throw ObjectCreatorException.createForAbstractClass(c);
        }
        catch (IllegalAccessException ex)
        {
            throw ObjectCreatorException.createForNoAccess(ctor);
        }
        catch (IllegalArgumentException ex)
        {
            throw ObjectCreatorException.
                createForBadConstructorArguments(ctor, ctorArgs);
        }
        catch (InvocationTargetException ex)
        {
            Throwable targetEx = ex.getTargetException();
            throw ObjectCreatorException.
                createForConstructorException(ctor, targetEx);
        }
    }

    /**
        @exception ObjectCreatorException thrown if the instance could
        not be created
        @see #create(Class, Class[], Object[])
    */
    public Object create(String className,
                            Class[] ctorArgTypes, Object[] ctorArgs)
        throws ObjectCreatorException
    {
        return create(forName(className), ctorArgTypes, ctorArgs);
    }

    /**
        Returns the Class object representing the class with the specified
        fully-qualified class name.

        @param className the fully-qualified class name
        @return the Class object for the class with the specified
        fully-qualified name
        @exception ObjectCreatorException thrown if the Class object
        could not be obtained
    */
    public Class forName(String className)
        throws ObjectCreatorException
    {
        try
        {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex)
        {
            throw ObjectCreatorException.createForClassNotFound(className);
        }
    }


    // Main/testing methods

    /**
        Main method, which is used for testing the ObjectCreator class.

        @param args the command-line arguments
    */
    public static void main(String[] args)
    {
        ObjectCreator creator = ObjectCreator.instance();
        if (args.length != 1)
        {
            Io.err.println("usage: java " + creator.getClass().getName() +
                               " class-name");
            Io.err.println("");
            Io.err.println("where class-name is the fully-qualified class " +
                               "name of the class of which an instance is to " +
                               "be constructed using the class' default " +
                               "constructor.");
            System.exit(1);
        }

        try
        {
            Object obj = creator.create(args[0]);
            Io.out.println("Successfully created object of class " + args[0]);
            Io.out.println("  object.toString() = " + obj.toString());
        }
        catch (ObjectCreatorException ex)
        {
            Io.err.println("Object creation failed with reason code " +
                               ex.reason() + ": " + ex.getLocalizedMessage());
            System.exit(2);
        }
        System.exit(0);
    }
}
