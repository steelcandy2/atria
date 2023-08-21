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

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;
import java.util.*;

/**
    The interface implemented by classes that represent a place where zero or
    more Storable objects can be saved to and later loaded from.

    @author  James MacKay
    @see Storable
    @see StorableLoader
*/
public interface Storage
{
    // Public methods

    /**
        Saves 's' under the name 'name' in this storage, replacing any
        existing object that has been saved under that name.

        @param name the name to save 's' under in this storage
        @param s the object to store
        @return true iff 's' replaced another object that was already saved
        under the name 'name'
        @exception IOException if an I/O error occurred in saving 's'
        @see #load(String)
    */
    public boolean save(String name, Storable s)
        throws IOException;
        // Assert.require(name != null);
        // Assert.require(s != null);

    /**
        @param name the name under which an object may have been saved in
        this storage
        @return the object that was last saved under the name 'name' in this
        storage, or null if no object has been saved in us under that name
        @exception IOException if an I/O error occurs while loading the
        object
        @exception StorageException if a StorableLoader hasn't (yet) been
        registered with us for the object to be loaded or one of its
        subobjects
        @see #save(String, Storable)
        @see #registerLoaderFor(String, StorableLoader)
    */
    public Storable load(String name)
        throws IOException, StorageException;
        // Assert.require(name != null);
        // 'result' can be null

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
        throws IOException, StorageException;
        // Assert.require(item != null);
        // Assert.ensure(result != null);


    /**
        Registers 'loader' as the StorableLoader to use to load objects of
        the type named 'typeName', unless a loader has alreadt been
        registered for that type.
        <p>
        Note: 'typeName' doesn't have to correspond to the name of a Java
        type or class (or the name of a type or class in any other language),
        though it may.

        @param typeName the name of the type of object that 'loader' will be
        responsible for loading
        @param loader the loader that will load any and all objects of the
        type named 'typeName' from this storage
        @see #replaceLoaderFor(String, StorableLoader)
    */
    public void registerLoaderFor(String typeName, StorableLoader loader);
        // Assert.require(typeName != null);
        // Assert.require(loader != null);

    /**
        Registers 'loader' as the StorableLoader to use to load objects of
        the type named 'typeName', replacing any loader that may have already
        been registered for that type.

        @param typeName the name of the type of object that 'loader' will be
        responsible for loading
        @param loader the loader that will load any and all objects of the
        type named 'typeName' from this storage
        @see #registerLoaderFor(String, StorableLoader)
    */
    public void replaceLoaderFor(String typeName, StorableLoader loader);
        // Assert.require(typeName != null);
        // Assert.require(loader != null);


    // Utility methods for saving and loading Storables

    /**
        Writes out the start of the first line of the representation of an
        element that represents a Storable. It does <em>not</em> write out
        the newline at the end of the line.

        @param w the writer to write the start of an element to
        @param typeName the element's type name
        @param name the element's name
        @exception IOException thrown if an I/O error occurs while writing
        out the start of the element
    */
    public void writeElementStart(IndentWriter w,
                                  String typeName, String name)
        throws IOException;
        // Assert.require(w != null);
        // Assert.require(typeName != null);
        // Assert.require(name != null);

    /**
        Writes out the attribute with name 'name' and value 'value'.

        @param w the writer to write the attribute to
        @param name the attribute's name
        @param value the attribute's (string) value
        @exception IOException thrown if an I/O error occurs while writing
        out the attribute
        @exception IllegalArgumentException if 'name' isn't a valid user-
        specified StoredItem attribute name
    */
    public void writeAttribute(IndentWriter w, String name, String value)
        throws IOException, IllegalArgumentException;
        // Assert.require(w != null);
        // Assert.require(name != null);
        // Assert.require(value != null);

    /**
        Writes out the boolean-valued attribute with name 'name' and value
        'value'.

        @param w the writer to write the attribute to
        @param name the attribute's name
        @param value the attribute's (boolean) value
        @exception IOException thrown if an I/O error occurs while writing
        out the attribute
        @exception IllegalArgumentException if 'name' isn't a valid user-
        specified StoredItem attribute name
    */
    public void
        writeBooleanAttribute(IndentWriter w, String name, boolean value)
        throws IOException, IllegalArgumentException;
        // Assert.require(w != null);
        // Assert.require(name != null);


    /**
        Writes out 'txt' followed by a newline.

        @param w the writer to write to
        @param txt the text to write out immediately before the newline
        @exception IOException thrown if an I/O error occurs while writing
        out 'txt' or the newline
    */
    public void writeLine(IndentWriter w, String txt)
        throws IOException;
        // Assert.require(w != null);
        // Assert.require(txt != null);

    /**
        Writes out a newline.

        @param w the writer to write to
        @exception IOException thrown if an I/O error occurs while writing
        out the newline
    */
    public void writeLine(IndentWriter w)
        throws IOException;
        // Assert.require(w != null);


    /**
        Saves the string of 'txt' to this storage by writing it out to 'w'.

        @param w the writer to write to
        @param name the name to save the text under
        @exception IOException thrown if an I/O error occurs while writing
        to 'w'
        @see #loadText(StoredItem)
    */
    public void saveText(IndentWriter w, String name, String txt)
        throws IOException;
        // Assert.require(w != null);
        // Assert.require(name != null);
        // Assert.require(txt != null);

    /**
        @param item a StoredItem that contains information about a single
        string of text
        @return that string of text
        @exception IOException if an I/O error occurs while loading the text
        @exception StorageException if 'item' doesn't represent a single
        string of text
        @see #saveText(IndentWriter, String, String)
    */
    public String loadText(StoredItem item)
        throws IOException, StorageException;
        // Assert.require(item != null);
        // Assert.require(item.children().size() == 0);
        // Assert.ensure(result != null);

    /**
        Saves 'storables' to this storage by writing it out to 'w'. Each item
        in 'storables' is assumed to be a Storable.

        @param w the writer to write to
        @param listName the name to save the list under
        @param storables the list of Storables to save
        @exception IOException thrown if an I/O error occurs while writing
        to 'w'
        @see #loadStorablesList(StoredItem)
    */
    public void saveStorablesList(IndentWriter w, String listName,
                                  List storables)
        throws IOException;
        // Assert.require(w != null);
        // Assert.require(listName != null);
        // Assert.require(storables != null);

    /**
        @param item a StoredItem that contains the information about a list
        of Storables
        @return the list of Storables that 'item' contains the information
        for: each item in the list will be a Storable
        @exception IOException if an I/O error occurs while loading the list
        @exception StorageException if 'item' doesn't represent a list of
        Storables, or if a StorableLoader hasn't (yet) been registered with
        us for the objects in the list to be loaded, or for one of their
        subobjects
        @see #saveStorablesList(IndentWriter, String, List)
    */
    public List loadStorablesList(StoredItem item)
        throws IOException, StorageException;
        // Assert.require(item != null);
        // Assert.ensure(result != null);

    /**
        Saves 'm' to this storage by writing it out to 'w'. Each key in 'm'
        is assumed to be a String, and each value in 'm' is assumed to be a
        Storable.

        @param w the writer to write to
        @param mapName the name to save the map under
        @param m the map to save
        @exception IOException thrown if an I/O error occurs while writing
        to 'w'
        @see #loadStringToStorableMap(StoredItem)
    */
    public void saveStringToStorablesMap(IndentWriter w, String mapName,
                                         Map m)
        throws IOException;
        // Assert.require(w != null);
        // Assert.require(mapName != null);
        // Assert.require(m != null);

    /**
        @param item a StoredItem that contains the information about a map
        from Strings to Storables
        @return the map from Strings to Storables that 'item' contains the
        information for: each key in the map will be a String, and each value
        in the map will be a Storable
        @exception IOException if an I/O error occurs while loading the map
        @exception StorageException if 'item' doesn't represent a String to
        Storable map, or if a StorableLoader hasn't (yet) been registered
        with us for the values in the map to be loaded, or for one of their
        subobjects
        @see #saveStringToStorablesMap(IndentWriter, String, Map)
    */
    public Map loadStringToStorableMap(StoredItem item)
        throws IOException, StorageException;
        // Assert.require(item != null);
        // Assert.ensure(result != null);
}
