/*
 Copyright (C) 2008 by James MacKay.

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

package com.steelcandy.plack.common.compiler;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ExternalDataError;

import com.steelcandy.common.Resources;

import com.steelcandy.common.debug.Assert;

import java.io.*;

/**
    A class that contains miscellaneous methods useful in compilation, but
    that don't fit into any one class.

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class CommonCompilerUtilities
{
    // Private static constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonCompilerResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        IO_ERROR_SAVING_OBJECT_MSG =
            "IO_ERROR_SAVING_OBJECT_MSG",
        IO_ERROR_RESTORING_OBJECT_MSG =
            "IO_ERROR_RESTORING_OBJECT_MSG",
        FAILED_TO_CLOSE_SAVE_FILE_MSG =
            "FAILED_TO_CLOSE_SAVE_FILE_MSG",
        FAILED_TO_CLOSE_RESTORE_FILE_MSG =
            "FAILED_TO_CLOSE_RESTORE_FILE_MSG",
        RESTORE_OBJECT_OF_UNKNOWN_CLASS_MSG =
            "RESTORE_OBJECT_OF_UNKNOWN_CLASS_MSG";


    // Public static methods

    /**
        Saves the serializable object 'obj' in the file with pathname 'f'.

        @param obj the object to save
        @param f the pathname of the file to save 'obj' in
        @param handler the error handler to use to handle any and all
        errors that occur in saving 'obj'
        @see #restore(File, ErrorHandler)
    */
    public static void save(Serializable obj, File f, ErrorHandler handler)
    {
        Assert.require(obj != null);
        Assert.require(f != null);
        Assert.require(handler != null);

        ObjectOutputStream out = null;
        try
        {
            out = new ObjectOutputStream(new FileOutputStream(f));
            out.writeObject(obj);
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_SAVING_OBJECT_MSG,
                           obj.getClass().getName(), f.getPath(),
                           ex.getLocalizedMessage());
            reportExternalDataError(msg, handler);
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException ex)
                {
                    String msg = _resources.
                        getMessage(FAILED_TO_CLOSE_SAVE_FILE_MSG,
                                   obj.getClass().getName(), f.getPath(),
                                   ex.getLocalizedMessage());
                    reportExternalDataError(msg, handler);
                }
            }
        }
    }

    /**
        Restores and returns the object that was save()d in the file with
        pathname 'f'.

        @param f the pathname of the file that contains an object's data
        @param handler the error handler to use to handle any and all
        errors that occur in restoring the object from 'f'
        @return the object that was restored from the data in 'f'
    */
    public static Object restore(File f, ErrorHandler handler)
    {
        Assert.require(f != null);
        Assert.require(handler != null);

        Object result = null;
        ObjectInputStream in = null;
        try
        {
            in = new ObjectInputStream(new FileInputStream(f));
            result = in.readObject();
        }
        catch (IOException ex)
        {
            String msg = _resources.
                getMessage(IO_ERROR_RESTORING_OBJECT_MSG, f.getPath(),
                           ex.getLocalizedMessage());
            reportExternalDataError(msg, handler);
        }
        catch (ClassNotFoundException ex)
        {
            String msg = _resources.
                getMessage(RESTORE_OBJECT_OF_UNKNOWN_CLASS_MSG, f.getPath(),
                           ex.getLocalizedMessage());
            reportExternalDataError(msg, handler);
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException ex)
                {
                    String msg = _resources.
                        getMessage(FAILED_TO_CLOSE_RESTORE_FILE_MSG,
                                   f.getPath(), ex.getLocalizedMessage());
                    reportExternalDataError(msg, handler);
                }
            }
        }

        Assert.ensure(result != null);
        return result;
    }


    // Private static methods

    /**
        Reports an error in reading/writing data from/to an external
        source/sink (such as a file).

        @param desc a description of what caused the error
        @param handler the error handler to use to report the error
    */
    private static void
        reportExternalDataError(String desc, ErrorHandler handler)
    {
        Assert.require(desc != null);
        Assert.require(handler != null);

        handler.handle(new ExternalDataError(desc));
    }
}
