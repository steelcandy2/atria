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

package com.steelcandy.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import java.io.*;
import java.util.*;

import java.util.Iterator;

/**
    A TestWriter that outputs various types of information to
    destinations specified in a Resources object.
    <p>
    This class will check for resources describing the output
    destinations for all registered OutputTypes.
    <p>
    Let K be the resource key prefix with which an instance of
    this class is constructed. Then the output destination for
    the OutputType with name T is given by the resource with key
    'K.output.T'.

    @author James MacKay
    @version $Revision: 1.6 $
*/
public class ResourceTestWriter extends TestWriter
{
    // Constants

    /**
        The end part of the prefix of the resource keys used to
        look up the output destinations for the output types.
    */
    private static final String PREFIX_SUFFIX = "output";


    // Private fields

    /**
        If it isn't null, the directory to which all file output
        destinations that specify relative pathnames are relative to.
    */
    private File _baseDir = null;


    // Constructors

    /**
        Constructs a ResourceTestWriter from the Resources object
        to get its output destinations from and the initial part of
        the resource keys to use to look up that information.

        @param resources the Resources object in which to look up
        the writer's output destinations
        @param resourceKeyPrefix the initial part of the resource
        keys to use to look up the output destinations in the
        Resources
        @exception TestWriterCreationException thrown if an error
        occurs in creating the ResourceTestWriter
    */
    public ResourceTestWriter(Resources resources, String resourceKeyPrefix)
        throws TestWriterCreationException
    {
        this(null, resources, resourceKeyPrefix);
    }

    /**
        Constructs a ResourceTestWriter from the Resources object
        to get its output destinations from and the initial part of
        the resource keys to use to look up that information.

        @param baseDir the directory to which all file output
        destinations that specify a relative pathname are relative
        to
        @param resources the Resources object in which to look up
        the writer's output destinations
        @param resourceKeyPrefix the initial part of the resource
        keys to use to look up the output destinations in the
        Resources
        @exception TestWriterCreationException thrown if an error
        occurs in creating the ResourceTestWriter
    */
    public ResourceTestWriter(File baseDir,
                              Resources resources, String resourceKeyPrefix)
        throws TestWriterCreationException
    {
        _baseDir = baseDir;
        setRegisteredOutputTypeWriters(resources, resourceKeyPrefix);
    }


    // Protected methods

    /**
        Sets the Writer for all of the registered OutputTypes
        for which an output destination is specified in the
        Resources object.

        @param r the Resources object in which to look up the
        writer's output destinations
        @param resourceKeyPrefix the initial part of the resource
        keys to use to look up the output destinations in the
        Resources
        @exception TestWriterCreationException thrown if an error
        occurs in creating the Writers
    */
    protected void setRegisteredOutputTypeWriters(Resources r,
                                                  String resourceKeyPrefix)
        throws TestWriterCreationException
    {
        Iterator iter = registeredOutputTypeNames();
        while (iter.hasNext())
        {
            String typeName = (String) iter.next();
            OutputType type = registeredOutputType(typeName);
            setWriter(r, resourceKeyPrefix, type);
        }
    }

    /**
        Iff an output destination is specified for the specified
        OutputType in the specified Resources object, sets the
        Writer for that OutputType to a Writer that outputs to that
        destination.

        @param r the resources in which to look up the OutputType's
        output destination
        @param resourceKeyPrefix the initial part of the resource
        key to use to look up the OutputType's output destination
        in the Resources
        @param type the OutputType whose Writer is to be set (if
        an output destination is specified for it in the Resources)
        @exception TestWriterCreationException thrown if an error
        occurs in creating the output destination's Writer
    */
    protected void setWriter(Resources r, String resourceKeyPrefix,
                             OutputType type)
        throws TestWriterCreationException
    {
        String key = Resources.keyConcat(resourceKeyPrefix, PREFIX_SUFFIX);
        key = Resources.keyConcat(key, type.name());
        try
        {
            String outputDestination = r.getRequiredString(key);
            Writer w = createWriter(outputDestination);
            setWriter(type, w);
        }
        catch (MissingResourceException ex)
        {
            // No output destination was specified in the Resources
            // for the OutputType, so don't set its Writer: the
            // default Writer will be used for that type.
        }
    }

    /**
        Creates and returns a Writer that outputs to the destination
        indicated by the specified string.

        @param dest the string specifying the output destination that
        the returned Writer should write to
        @return a Writer that writes to the specified destination
        @exception InvalidOutputDestinationException thrown if an
        invalid output destination was specified in the Resources
        @exception WriterCreationFailedException thrown if an error
        occurs in creating the Writer for the output destination
    */
    protected Writer createWriter(String dest)
        throws InvalidOutputDestinationException,
               WriterCreationFailedException
    {
        Assert.require(dest != null);

        try
        {
            return Io.createWriter(dest, _baseDir);
        }
        catch (IllegalArgumentException ex)
        {
            throw new InvalidOutputDestinationException(ex);
        }
        catch (IOException ex)
        {
            throw new WriterCreationFailedException(ex);
        }
    }
}
