/*
 Copyright (C) 2001-2012 by James MacKay.

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

package com.steelcandy.common;

import com.steelcandy.common.debug.Assert;

import java.text.MessageFormat;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import java.util.Enumeration;
import java.util.Locale;

/**
    A class analogous to ResourceBundle, except that instead of throwing a
    MissingResourceException when a resource can't be found, the key itself
    is returned instead. However, the getRequiredXXX() methods can be used to
    try to get resources that must be present (and MissingResourceExceptions
    are thrown if they aren't found).
    <p>
    This new class is used instead of subclassing ResourceBundle since the
    latter has several final methods.

    @author James MacKay
    @version $Revision: 1.4 $
    @see ResourceBundle
*/
public class Resources
{
    // Constants

    /** The String that separates components of a resource key. */
    public static final String KEY_COMPONENT_SEPARATOR = ".";


    // Private fields

    /**
        The ResourceBundle to which this class delegates its resource
        lookups.
    */
    private ResourceBundle _bundle;


    // Constructors

    /**
        Returns the Resources object corresponding to the specified base name
        and for the specified locale, or null if such a Resources object
        can't be constructed.

        @param baseName the resources' base name
        @param locale the resources' locale
    */
    public static Resources getResources(String baseName, Locale locale)
    {
        Resources result;
        try
        {
            ResourceBundle bundle =
                ResourceBundle.getBundle(baseName, locale);
            result = new Resources(bundle);
        }
        catch (MissingResourceException ex)
        {
            result = null;
        }
        return result;
    }

    /**
        Returns the Resources object corresponding to the specified base
        name and for the default locale, or null if such a Resources object
        can't be constructed.

        @param baseName the resources' base name
    */
    public static Resources getResources(String baseName)
    {
        return getResources(baseName, Locale.getDefault());
    }

    /**
        Constructs a Resources object from the ResourceBundle to which it is
        to delegate its lookups.

        @param bundle the ResourceBundle to which the Resources object is to
        delegate its lookups
    */
    protected Resources(ResourceBundle bundle)
    {
        Assert.require(bundle != null);

        _bundle = bundle;
    }


    // Public methods

    /**
        Get the string resource corresponding to the specified key from
        these Resources. If the resource isn't found then the key itself is
        returned.

        @param key the key identifying the resource to return
        @return the string resource
    */
    public String getString(String key)
    {
        String result;
        try
        {
            result = _bundle.getString(key);
        }
        catch (MissingResourceException ex)
        {
            result = key;
        }
        return result;
    }

    /**
        Get the string resource corresponding to the specified key from these
        Resources. If the resource isn't found then a
        MissingResourceException is thrown.

        @param key the key identifying the resource to return
        @return the string resource
        @exception MissingResourceException thrown if a resource with the
        specified key cannot be found
    */
    public String getRequiredString(String key)
        throws MissingResourceException
    {
        return _bundle.getString(key);
    }

    /**
        Get the string array resource corresponding to the specified key from
        these Resources. If the resource isn't found then a single-element
        array containing the key itself is returned.

        @param key the key identifying the resource to return
        @return the string array resource
    */
    public String[] getStringArray(String key)
    {
        String[] result;
        try
        {
            result = _bundle.getStringArray(key);
        }
        catch (MissingResourceException ex)
        {
            result = new String[] { key };
        }
        return result;
    }

    /**
        Get the string array resource corresponding to the specified key from
        these Resources. If the resource isn't found then a
        MissingResourceException is thrown.

        @param key the key identifying the resource to return
        @return the string array resource
        @exception MissingResourceException thrown if a resource with the
        specified key cannot be found
    */
    public String[] getRequiredStringArray(String key)
        throws MissingResourceException
    {
        return _bundle.getStringArray(key);
    }

    /**
        Get the Object resource corresponding to the specified key from
        these Resources. If the resource isn't found then the key itself is
        returned (which could cause a ClassCastException if an Object of a
        non-String type is expected/assumed).

        @param key the key identifying the resource to return
        @return the object resource
    */
    public Object getObject(String key)
    {
        Object result;
        try
        {
            result = _bundle.getObject(key);
        }
        catch (MissingResourceException ex)
        {
            result = key;
        }
        return result;
    }

    /**
        Get the object resource corresponding to the specified key from these
        Resources. If the resource isn't found then a
        MissingResourceException is thrown.

        @param key the key identifying the resource to return
        @return the object resource
        @exception MissingResourceException thrown if a resource with the
        specified key cannot be found
    */
    public Object getRequiredObject(String key)
        throws MissingResourceException
    {
        return _bundle.getObject(key);
    }

    /**
        Gets the message whose format is obtained from these resources using
        the specified resource key, and using the specified arguments.
        <p>
        If the message format resource can't be found then the resource key
        itself is used as the message format.

        @param formatKey the resource key of the String resource that is used
        to find the message's format in these resources
        @param msgArgs the arguments to substitute into the message format
        @return the message constructed by inserting the message arguments
        into the message format
    */
    public String getMessage(String formatKey, Object[] msgArgs)
    {
        Assert.require(msgArgs != null);

        String fmt = getString(formatKey);
        return MessageFormat.format(fmt, msgArgs);
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the five specified arguments.

        @param formatKey the resource key of the String resource that is
        used to find the message's format in these resources
        @param msgArg1 the first message argument
        @param msgArg2 the second message argument
        @param msgArg3 the third message argument
        @param msgArg4 the fourth message argument
        @param msgArg5 the fifth message argument
        @return the message constructed by inserting the message arguments
        into the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey, Object msgArg1,
            Object msgArg2, Object msgArg3, Object msgArg4, Object msgArg5)
    {
        return getMessage(formatKey,
            new Object[] { msgArg1, msgArg2, msgArg3, msgArg4, msgArg5 });
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the four specified arguments.

        @param formatKey the resource key of the String resource that is
        used to find the message's format in these resources
        @param msgArg1 the first message argument
        @param msgArg2 the second message argument
        @param msgArg3 the third message argument
        @param msgArg4 the fourth message argument
        @return the message constructed by inserting the message arguments
        into the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey, Object msgArg1,
                             Object msgArg2, Object msgArg3, Object msgArg4)
    {
        return getMessage(formatKey,
                new Object[] { msgArg1, msgArg2, msgArg3, msgArg4 });
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the three specified arguments.

        @param formatKey the resource key of the String resource that is
        used to find the message's format in these resources
        @param msgArg1 the first message argument
        @param msgArg2 the second message argument
        @param msgArg3 the third message argument
        @return the message constructed by inserting the message arguments
        into the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey, Object msgArg1,
                             Object msgArg2, Object msgArg3)
    {
        return getMessage(formatKey,
                          new Object[] { msgArg1, msgArg2, msgArg3 });
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the two specified arguments.

        @param formatKey the resource key of the String resource that is
        used to find the message's format in these resources
        @param msgArg1 the first message argument
        @param msgArg2 the second message argument
        @return the message constructed by inserting the message arguments
        into the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey,
                             Object msgArg1, Object msgArg2)
    {
        return getMessage(formatKey, new Object[] { msgArg1, msgArg2 });
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the specified argument.

        @param formatKey the resource key of the String resource that is used
        to find the message's format in these resources
        @param msgArg the sole message argument
        @return the message constructed by inserting the message arguments
        into the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey, Object msgArg)
    {
        return getMessage(formatKey, new Object[] { msgArg });
    }

    /**
        A convenience version of getMessage() that constructs the message
        using the two no arguments. Thus the message is the message format
        itself.
        <p>
        While not strictly necessary, this version of getMessage() is
        provided so that a method of the same name can be called regardless
        of how many arguments are used to construct the message.

        @param formatKey the resource key of the String resource that is used
        to find the message's format in these resources
        @return the message format
        @see #getMessage(String, Object[])
    */
    public String getMessage(String formatKey)
    {
        return getString(formatKey);
    }


    /**
        @return an enumeration of the resource keys
        @see ResourceBundle#getKeys
    */
    public Enumeration keys()
    {
        return _bundle.getKeys();
    }


    // Public static methods

    /**
        Concatenates the two parts of a resource key together. Each is
        assumed to consist of zero or more complete components, and not to
        begin or end with the resource key component separator.
    */
    public static String keyConcat(String part1, String part2)
    {
        Assert.require(part1 != null);
        Assert.require(part2 != null);
        Assert.require(part1.startsWith(KEY_COMPONENT_SEPARATOR) == false);
        Assert.require(part1.endsWith(KEY_COMPONENT_SEPARATOR) == false);
        Assert.require(part2.startsWith(KEY_COMPONENT_SEPARATOR) == false);
        Assert.require(part2.endsWith(KEY_COMPONENT_SEPARATOR) == false);

        String result = part1;

        int part2Length = part2.length();
        if (part1.length() > 0 && part2Length > 0)
        {
            result = part1 + KEY_COMPONENT_SEPARATOR + part2;
        }
        else if (part2Length > 0)
        {
            result = part2;
        }

        return result;
    }
}
