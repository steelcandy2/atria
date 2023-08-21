/*
 Copyright (C) 2001-2008 by James MacKay.

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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ReadError;
import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.Utilities;
import com.steelcandy.common.io.Io;

import java.io.*;

/**
    A class of iterator that iterates over a piece of text one character at a
    time. It also allows the next character to be peeked at without actually
    retrieving it.
    <p>
    Note: unlike regular Java iterators a CharacterIterator must be close()d
    once it is no longer being used.

    @author James MacKay
    @version $Revision: 1.10 $
    @see #close
*/
public class CharacterIterator
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        SourceCodeResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String NO_MORE_CHARS_MSG = "NO_MORE_CHARS_MSG";


    // Private fields

    /**
        The Reader from which we get the characters that we iterate over.
        We close it when we're done with it.
    */
    private Reader _reader;

    /** The error handler to use to handle errors. */
    private ErrorHandler _handler;

    /**
        The character to return when an error occurs in reading from our
        Reader.
    */
    private char _errorChar;


    /**
        The next character to be returned by this iterator, iff _useNextChar
        is true. Otherwise this variable does NOT hold the next character to
        be returned by this iterator.

        @see #_useNextChar
    */
    private char _nextChar;

    /**
        Indicates whether _nextChar is the next character to be returned by
        this iterator.
    */
    private boolean _useNextChar;


    // Constructors

    /**
        Constructs a CharacterIterator.

        @param r the Reader from which the iterator is to obtain the
        characters it is to iterate over
        @param errorChar the character to be returned from this iterator when
        an error occurs in reading the character from the source
        @param handler the error handler for the iterator to use in handling
        errors
    */
    public CharacterIterator(Reader r, char errorChar, ErrorHandler handler)
    {
        _errorChar = errorChar;
        _handler = handler;
        reset(r);
    }


    // Public methods

    /**
        Indicates whether this iterator has any more characters to return.

        @return true iff this iterator has more characters to return
    */
    public boolean hasNext()
    {
        boolean result = false;
        if (_useNextChar)
        {
            result = true;
        }
        else
        {
            try
            {
                int ch = _reader.read();
                if (ch != -1)
                {
                    _nextChar = (char) ch;
                    _useNextChar = true;
                    result = true;
                }
            }
            catch (IOException ex)
            {
                handleReadException(ex);
                _useNextChar = false;
                result = false;
            }
        }

        return result;
    }

    /**
        Returns the next character to be returned from this iterator without
        actually consuming it: the next call to this method or next() will
        return the same character.

        @return the next character to be returned from this iterator
        @exception NoSuchItemException thrown if this iterator has no more
        characters to return
        @see #next
        @see #hasNext
    */
    public char peek()
    {
        if (_useNextChar == false)
        {
            _nextChar = getCharFromSource();
            _useNextChar = true;
        }

        return _nextChar;
    }

    /**
        @return the next character from this iterator
        @exception NoSuchItemException thrown if this iterator has no more
        characters to return
        @see #hasNext
    */
    public char next()
    {
        char result;
        if (_useNextChar)
        {
            _useNextChar = false;
            result = _nextChar;
        }
        else
        {
            result = getCharFromSource();
        }

        return result;
    }

    /**
        Consumes the next character from this token without returning it.
        <p>
        The effect of this method could be obtained by calling the next()
        method and ignoring the returned character, but this method makes
        the callers intention to discard the token clear.

        @exception NoSuchItemException thrown if this iterator has no more
        characters to return
        @see #hasNext
    */
    public void discard()
    {
        next();
    }

    /**
        Resets this iterator to return characters read from (the start of)
        the specified Reader.

        @param newReader the new Reader from which this iterator is to obtain
        the characters it returns
    */
    public void reset(Reader newReader)
    {
        Io.tryToClose(_reader);
        _reader = newReader;
        _useNextChar = false;
    }

    /**
        Closes this iterator.
    */
    public void close()
    {
        Io.tryToClose(_reader);
    }


    // Protected methods

    /**
        @return the next character from our Reader
        @exception NoSuchItemException thrown if our Reader has no more
        characters to return
    */
    protected char getCharFromSource()
    {
        try
        {
            int ch = _reader.read();
            if (ch == -1)
            {
                String msg = _resources.getMessage(NO_MORE_CHARS_MSG);
                throw new NoSuchItemException(msg);
            }
            else
            {
                return (char) ch;
            }
        }
        catch (IOException ex)
        {
            handleReadException(ex);
            return _errorChar;
        }
    }

    /**
        @return the piece of source code whose characters this iterator
        iterates over, or null if that information is not available or
        applicable
    */
    protected SourceCode sourceCode()
    {
        return null;
    }


    // Private methods

    /**
        Handles the specified IOException that occurred whil trying to read
        from our Reader.

        @param ex the IOException that signalled the read error
    */
    private void handleReadException(IOException ex)
    {
        // Since this is a fatal error no more attempts should be made to get
        // tokens from this source, and hence no more character reads should
        // be attempted.
        ReadError error = new ReadError(ReadError.FATAL_ERROR_LEVEL,
                                    ex.getLocalizedMessage(), sourceCode());
        _handler.handle(error);
    }
}
