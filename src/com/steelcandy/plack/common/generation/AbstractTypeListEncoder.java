/*
 Copyright (C) 2004-2009 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.semantic.Type;  // javadocs
import com.steelcandy.plack.common.semantic.TypeIterator;
import com.steelcandy.plack.common.semantic.TypeList;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.text.StringIterator;
import com.steelcandy.common.text.StringList;

/**
    An abstract base class for TypeListEncoders.
    <p>
    Subclasses just have to implement
    <code>encode(StringIterator, int, ErrorHandler)</code>.

    @author  James MacKay
    @see #encode(StringIterator, int, ErrorHandler)
*/
public abstract class AbstractTypeListEncoder
    implements TypeListEncoder, ErrorSeverityLevels
{
    // Constructors

    /**
        Constructs an AbstractTypeListEncoder.
    */
    public AbstractTypeListEncoder()
    {
        // empty
    }


    // Public methods

    /**
        @see TypeListEncoder#encode(TypeList, ErrorHandler)
    */
    public String encode(TypeList types, ErrorHandler handler)
    {
        Assert.require(types != null);
        Assert.require(handler != null);

        StringIterator iter = new GlobalTypeNameIterator(types.iterator());

        String result = encode(iter, types.size(), handler);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TypeListEncoder#encode(StringList, ErrorHandler)
    */
    public String encode(StringList globalTypeNames, ErrorHandler handler)
    {
        Assert.require(globalTypeNames != null);
        Assert.require(handler != null);

        String result = encode(globalTypeNames.iterator(),
                               globalTypeNames.size(), handler);

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        @param globalTypeNamesIter an iterator over the global names of a list
        of Types
        @param numNames the number of names in the list of Types
        @param handler the error handler to use to handle any errors that
        occur in the encoding
        @return a String that uniquely identifies the list of types whose
        global names are returned by 'globalTypeNamesIter', including the
        order and values of the types, and can be used as part of an
        identifier in the target language
        @see Type#globalName
    */
    protected abstract String
        encode(StringIterator globalTypeNamesIter, int numNames,
               ErrorHandler handler);
        // Assert.require(globalTypeNamesIter != null);
        // Assert.require(numNames >= 0);
        // Assert.require(handler != null);
        // Assert.ensure(result != null);


    // Inner classes

    /**
        The class of StringIterator that iterates over the global names of
        all of the Types returned by a TypeIterator.
    */
    public class GlobalTypeNameIterator
        implements StringIterator
    {
        // Private fields

        /**
            The iterator over the Types whose global names this iterator is
            to iterate over.
        */
        private TypeIterator _typeIterator;


        // Constructors

        /**
            Constructs a GlobalTypeNameIterator.

            @param iter an iterator over the types whose global names the
            constructed iterator is to iterate over
        */
        public GlobalTypeNameIterator(TypeIterator iter)
        {
            Assert.require(iter != null);

            _typeIterator = iter;
        }


        // Public methods

        /**
            @see StringIterator#hasNext
        */
        public boolean hasNext()
        {
            return _typeIterator.hasNext();
        }

        /**
            @see StringIterator#next
        */
        public String next()
            throws NoSuchItemException
        {
            return _typeIterator.next().globalName();
        }

        /**
            @see StringIterator#peek
        */
        public String peek()
            throws NoSuchItemException
        {
            return _typeIterator.peek().globalName();
        }
    }
}
