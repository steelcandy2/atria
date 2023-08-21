/*
 Copyright (C) 2014-2015 by James MacKay.

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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import java.io.StringWriter;
import java.util.*;

/**
    The base class for Java message catalogs whose source code is generated
    from an Atria message catalog.

    @author James MacKay
*/
public class MessageCatalog
{
    // Protected constants

    /** The double quotation mark character. */
    protected static final String
        QUOTE = "\"";

// TODO: doublecheck that this is what we want, and not a hardcoded '\n' !!!
    /** The (platform-dependent) newline character(s). */
    protected static final String
        NEWLINE = System.getProperty("line.separator");


    // Private fields

    /**
        A map from (message catalog) variable names to values. The keys and
        values are all Strings.
    */
    private Map _variables;


    // Constructors

    /**
        Constructs a MessageCatalog.
    */
    public MessageCatalog()
    {
        _variables = new HashMap();
    }


    // Protected methods

    /**
        Adds the variable named 'name' that was defined in the source Atria
        message catalog to have the value 'value' to our list of such
        variables.

        @param name the name of the variable being added
        @param value the value of the variable being added
    */
    protected void addVariable(String name, String value)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);
        Assert.require(value != null);

        Object old = _variables.put(name, value);
        Assert.check(old != null);
            // which the source Atria message catalog should guarantee
    }

    /**
        @param name the name of a variable defined in the source Atria
        message catalog
        @return the variable's value
        @exception NoSuchElementException if no variable named 'name' was
        defined in the source message catalog
    */
    protected String variableValue(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        String result = (String) _variables.get(name);

        if (result == null)
        {
            throw new NoSuchElementException("No variable named '" + name +
                "' was defined in the source Atria message catalog");
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param w the writer to which which a message is assumed to have been
        written
        @return a (non-null) String containing the message that was written
        to 'w'
    */
    protected String toStringMessage(StringWriter w)
    {
        Assert.require(w != null);

        String result = w.toString();

        Assert.ensure(result != null);
        return result;
    }
}
