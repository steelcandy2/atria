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

package com.steelcandy.plack.common.base;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.Io;
import com.steelcandy.common.text.TextUtilities;

import java.io.IOException;
import java.util.*;

/**
    An abstract base class for Storage classes.
    <p>
    Note: this class is <em>not</em> thread-safe.

    @author  James MacKay
    @version $Revision: 1.5 $
*/
public abstract class AbstractStorage
    implements Storage
{
    // Constants

    /** The resources used by this class. */
    private static final Resources
        _resources = CommonStorageResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        INVALID_USER_ATTRIBUTE_NAME_MSG =
            "INVALID_USER_ATTRIBUTE_NAME_MSG",
        NO_STORABLE_LOADER_REGISTERED_MSG =
            "NO_STORABLE_LOADER_REGISTERED_MSG",
        NOT_A_STORABLES_LIST_MSG =
            "NOT_A_STORABLES_LIST_MSG",
        NOT_A_STRING_TO_STRORABLE_MAP_MSG =
            "NOT_A_STRING_TO_STRORABLE_MAP_MSG",
        NOT_A_TEXT_MSG =
            "NOT_A_TEXT_MSG";


    /**
        The value of a boolean-valued attribute whose value is "true".
    */
    public static final String TRUE_ATTRIBUTE_VALUE = "true";

    /**
        The value of a boolean-valued attribute whose value is "false".
    */
    public static final String FALSE_ATTRIBUTE_VALUE = "false";


    /** The Atria quote/quotation mark character. */
    private static final String QUOTE = AtriaInfo.QUOTE;

    /**
        A string consisting of all of the characters that aren't allowed in
        (user-specified) attribute names, but that are in
        AtriaCharacterClasses.UNQUALIFIED_NAME_CHARACTER.
        <p>
        Note: this exists so that the storage infrastructure classes can add
        attributes to StoredItems without worrying about them conflicting
        with ones added by Storable subclasses when they save() themselves.

        @see #TYPE_ATTRIBUTE_NAME
        @see #NAME_ATTRIBUTE_NAME
        @see AtriaCharacterClasses#UNQUALIFIED_NAME_CHARACTER
    */
    private static final String
        INVALID_USER_ATTRIBUTE_NAME_CHARACTERS = "_.";

    /**
        The names of attributes and elements used by utility methods in this
        class that save and load various data (such as lists and maps).
    */
    private static final String
        TEXT_TYPE_NAME = "plain-text",
        STORABLES_LIST_TYPE_NAME = "storables-list",
        LIST_ITEM_NAME = "item",
        STRING_TO_STORABLE_MAP_TYPE_NAME = "string-to-storable-map",
        MAP_KEY_NAME = "key",
        MAP_VALUE_NAME = "value";


    /**
        The name of an Atria element that contains a Storable's information.
    */
    private static final String STORABLE_ELEMENT_NAME = "storable";

    /**
        The name of the attribute on a StoredItem whose value is the name of
        the type of the Storable that the StoredItem represents the contents
        of.
    */
    public static final String TYPE_ATTRIBUTE_NAME = "storable_type";

    /**
        The name of the attribute on a StoredItem whose value is the name of
        the Storable that the StoredItem represents the contents of (where
        the name is usually used to distinguish between Storables that are
        direct children of another parent Storable).
    */
    public static final String NAME_ATTRIBUTE_NAME = "storable_name";

    static
    {
        // Check that they're invalid user-specified attribute names (so that
        // they can't clash with user-specified ones).
        Assert.check(isInvalidUserAttributeName(TYPE_ATTRIBUTE_NAME));
        Assert.check(isInvalidUserAttributeName(NAME_ATTRIBUTE_NAME));
    }


    // Private fields

    /**
        A map from the name of each type of object that we can load to the
        loader to use to load that type of object. The keys are all Strings
        and the values are all StorableLoaders.
    */
    private Map _loaders;


    // Constructors

    /**
        Constructs an AbstractStorage object.
    */
    public AbstractStorage()
    {
        _loaders = new HashMap();
    }


    // Public methods

    /**
        @param item an StoredItem that contains the information about a
        Storable
        @return the Storable that 'item' contains the information for
        @exception IOException if an I/O error occurs while loading the
        object
        @exception StorageException if a StorableLoader hasn't (yet) been
        registered with us for the object to be loaded or one of its
        subobjects
    */
    public Storable load(StoredItem item)
        throws IOException, StorageException
    {
        Assert.require(item != null);

        Storable result = loader(item.type()).load(this, item);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Storage#registerLoaderFor(String, StorableLoader)
    */
    public void registerLoaderFor(String typeName, StorableLoader loader)
    {
        Assert.require(typeName != null);
        Assert.require(loader != null);

        if (_loaders.get(typeName) == null)
        {
            _loaders.put(typeName, loader);
        }
    }

    /**
        @see Storage#replaceLoaderFor(String, StorableLoader)
    */
    public void replaceLoaderFor(String typeName, StorableLoader loader)
    {
        Assert.require(typeName != null);
        Assert.require(loader != null);

        _loaders.put(typeName, loader);
    }


    // Utility methods for saving and loading Storables

    /**
        @see Storage#writeElementStart(IndentWriter, String, String)
    */
    public void writeElementStart(IndentWriter w,
                                  String typeName, String name)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(typeName != null);
        Assert.require(name != null);

        w.write(STORABLE_ELEMENT_NAME);
        writeUncheckedAttribute(w, TYPE_ATTRIBUTE_NAME, typeName);
        writeUncheckedAttribute(w, NAME_ATTRIBUTE_NAME, name);
    }

    /**
        @see Storage#writeAttribute(IndentWriter, String, String)
    */
    public void writeAttribute(IndentWriter w, String name, String value)
        throws IOException, IllegalArgumentException
    {
        Assert.require(w != null);
        Assert.require(name != null);
        Assert.require(value != null);

        if (isInvalidUserAttributeName(name))
        {
            String msg = _resources.
                getMessage(INVALID_USER_ATTRIBUTE_NAME_MSG, name);
            throw new IllegalArgumentException(msg);
        }
        writeUncheckedAttribute(w, name, value);
    }

    /**
        @see Storage#writeBooleanAttribute(IndentWriter, String, boolean)
    */
    public void
        writeBooleanAttribute(IndentWriter w, String name, boolean value)
        throws IOException, IllegalArgumentException
    {
        Assert.require(w != null);
        Assert.require(name != null);

        writeAttribute(w, name,
                       value ? TRUE_ATTRIBUTE_VALUE : FALSE_ATTRIBUTE_VALUE);
    }

    /**
        @see Storage#writeLine(IndentWriter, String)
    */
    public void writeLine(IndentWriter w, String txt)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(txt != null);

        w.write(txt);
        writeLine(w);
    }

    /**
        @see Storage#writeLine(IndentWriter)
    */
    public void writeLine(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        w.write(Io.NL);
    }


    /**
        @see Storage#saveText(IndentWriter, String, String)
    */
    public void saveText(IndentWriter w, String name, String txt)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(name != null);
        Assert.require(txt != null);

        writeElementStart(w, TEXT_TYPE_NAME, name);
        writeLine(w);
        if (txt.length() > 0);
        {
            w.incrementIndentLevel();
            try
            {
                AtriaUtilities.writeStringAsExpressionItems(txt, w);
            }
            finally
            {
                w.decrementIndentLevel();
            }
        }
    }

    /**
        @see Storage#loadText(StoredItem)
    */
    public String loadText(StoredItem item)
        throws IOException, StorageException
    {
        Assert.require(item != null);
        Assert.require(item.children().size() == 0);

        if (TEXT_TYPE_NAME.equals(item.type()) == false)
        {
            String msg = _resources.
                getMessage(NOT_A_TEXT_MSG, item.type(), TEXT_TYPE_NAME);
            throw new StorageException(msg);
        }

        String result = item.textContents();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Storage#saveStorablesList(IndentWriter, String, List)
    */
    public void saveStorablesList(IndentWriter w, String listName,
                                  List storables)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(listName != null);
        Assert.require(storables != null);

        writeElementStart(w, STORABLES_LIST_TYPE_NAME, listName);
        w.incrementIndentLevel();
        try
        {
            Iterator iter = storables.iterator();
            while (iter.hasNext())
            {
                Storable s = (Storable) iter.next();
                s.save(this, w, LIST_ITEM_NAME);
            }
        }
        finally
        {
            w.decrementIndentLevel();
        }
    }

    /**
        @see Storage#loadStorablesList(StoredItem)
    */
    public List loadStorablesList(StoredItem item)
        throws IOException, StorageException
    {
        Assert.require(item != null);

        if (STORABLES_LIST_TYPE_NAME.equals(item.type()) == false)
        {
            String msg = _resources.
                getMessage(NOT_A_STORABLES_LIST_MSG, item.type(),
                           STORABLES_LIST_TYPE_NAME);
            throw new StorageException(msg);
        }

        List result = new ArrayList();

        StoredItemIterator iter = item.children().iterator();
        while (iter.hasNext())
        {
            StoredItem child = iter.next();
            Assert.check(LIST_ITEM_NAME.equals(child.name()));
            result.add(load(child));
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Storage#saveStringToStorablesMap(IndentWriter, String, Map)
    */
    public void saveStringToStorablesMap(IndentWriter w, String mapName,
                                         Map m)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(mapName != null);
        Assert.require(m != null);

        writeElementStart(w, STRING_TO_STORABLE_MAP_TYPE_NAME, mapName);
        w.incrementIndentLevel();
        try
        {
            Iterator iter = m.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry en = (Map.Entry) iter.next();
                saveText(w, MAP_KEY_NAME, (String) en.getKey());
                Storable value = (Storable) en.getValue();
                value.save(this, w, MAP_VALUE_NAME);
            }
        }
        finally
        {
            w.decrementIndentLevel();
        }
    }

    /**
        @see Storage#loadStringToStorableMap(StoredItem)
    */
    public Map loadStringToStorableMap(StoredItem item)
        throws IOException, StorageException
    {
        Assert.require(item != null);

        if (STRING_TO_STORABLE_MAP_TYPE_NAME.equals(item.type()) == false)
        {
            String msg = _resources.
                getMessage(NOT_A_STRING_TO_STRORABLE_MAP_MSG, item.type(),
                           STRING_TO_STORABLE_MAP_TYPE_NAME);
            throw new StorageException(msg);
        }

        Map result = new HashMap();

        StoredItemIterator iter = item.children().iterator();
        while (iter.hasNext())
        {
            StoredItem child = iter.next();
            Assert.check(MAP_KEY_NAME.equals(child.name()));
            String key = loadText(child);

            Assert.check(iter.hasNext());
            child = iter.next();
            Assert.check(MAP_VALUE_NAME.equals(child.name()));
            Storable value = load(child);

            result.put(key, value);
        }

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @param typeName the name of a Storable's type
        @return the loader to use to load Storables whose type is named
        'typeName' from us
        @exception StorageException thrown if no StorableLoader has been
        registered with us for the type named 'typeName'
        @see #registerLoaderFor(String, StorableLoader)
    */
    protected StorableLoader loader(String typeName)
        throws StorageException
    {
        Assert.require(typeName != null);

        StorableLoader result = (StorableLoader) _loaders.get(typeName);

        if (result == null)
        {
            String msg = _resources.
                getMessage(NO_STORABLE_LOADER_REGISTERED_MSG,
                           getClass().getName(), typeName);
            throw new StorageException(msg);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Writes out the attribute with name 'name' and value 'value' without
        first checking whether either is valid.

        @param w the writer to write the attribute to
        @param name the attribute's name
        @param value the attribute's (string) value
        @exception IOException thrown if an I/O error occurs while writing
        out the attribute
    */
    protected void
        writeUncheckedAttribute(IndentWriter w, String name, String value)
        throws IOException
    {
        Assert.require(w != null);
        Assert.require(name != null);
        Assert.require(value != null);

        w.write(" ");
        w.write(name);
        w.write("=");
        w.write(QUOTE);
        w.write(value);
        w.write(QUOTE);
    }


    // Private static methods

    /**
        @param name a potential attribute name
        @return true iff 'name' is an invalid (user-specified) attribute
        name
    */
    private static boolean isInvalidUserAttributeName(String name)
    {
        Assert.require(name != null);

        boolean isValid = TextUtilities.onlyContainsCharactersIn(name,
                           AtriaCharacterClasses.UNQUALIFIED_NAME_CHARACTER);
        if (isValid)
        {
            isValid = (TextUtilities.contains(name,
                        INVALID_USER_ATTRIBUTE_NAME_CHARACTERS) == false);
        }

        boolean result = (isValid == false);

        return result;
    }
}
