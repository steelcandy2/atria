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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ReadError;

import com.steelcandy.plack.common.source.CharacterIterator;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceCodeCharacterIterator;

import com.steelcandy.common.*;

import java.io.*;

/**
    An abstract base class for tokenizers that tokenize a piece of source
    code represented by a SourceCode object.
    <p>
    Subclasses still have to implement the getNextToken() method inherited
    from AbstractTokenizer, but this class provides several utility methods
    to aid in this. (Most subclasses are language-specific.)

    @author James MacKay
*/
public abstract class AbstractSourceCodeTokenizer
    extends AbstractTokenizer
    implements SourceCodeTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        SOURCE_FILE_NOT_FOUND_MSG =
            "SOURCE_FILE_NOT_FOUND_MSG";
    private static final String
        SOURCE_READ_FAILED_MSG =
            "SOURCE_READ_FAILED_MSG";


    // Private fields

    /** The source code we're tokenizing. */
    private SourceCode _sourceCode;

    /**
        The iterator we're using to iterate over the characters in our source
        code.

        @see #_sourceCode
    */
    private CharacterIterator _charIterator;


    // Constructors

    /**
        Constructs an AbstractSourceCodeTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public AbstractSourceCodeTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AbstractSourceCodeTokenizer(Resources subtokenizerResources,
                                       String resourceKeyPrefix,
                                       ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Public methods

    /**
        @see SourceCodeTokenizer#initialize(SourceCode)
    */
    public void initialize(SourceCode sourceCode)
    {
        Assert.require(sourceCode != null);

        _sourceCode = sourceCode;
        _charIterator = null;
        try
        {
            _charIterator =
                new SourceCodeCharacterIterator(_sourceCode,
                        charToReturnOnReadError(), errorHandler());
        }
        catch (FileNotFoundException ex)
        {
            handleInitializationFailure(_sourceCode,
                                        SOURCE_FILE_NOT_FOUND_MSG);
        }
        catch (IOException ex)
        {
            handleInitializationFailure(_sourceCode,
                                        SOURCE_READ_FAILED_MSG);
        }
    }

    /**
        @see SourceCodeTokenizer#close
    */
    public void close()
    {
        // _charIterator can be null if it fails to get set in initialize()
        // or if handleInitializationFailure() already close()d it.
        if (_charIterator != null)
        {
            _charIterator.close();
        }
    }


    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        Assert.ensure(_sourceCode != null);

        return _sourceCode;
    }


    // Abstract methods

    /**
        @return the character to be returned when an error occurs in trying
        to read the character from the source code
    */
    protected abstract char charToReturnOnReadError();


    // Protected methods

    /**
        @return the iterator over the source code's characters
    */
    protected CharacterIterator charIterator()
    {
        Assert.ensure(_charIterator != null);
        return _charIterator;
    }


    // Private methods

    /**
        Handles a failure to initialize this tokenizer.

        @param code the source code that this tokenizer was to tokenize
        @param errorMsgKey the message key to use to build the error
        message explaining why initializing this tokenizer failed
    */
    private void
        handleInitializationFailure(SourceCode code, String errorMsgKey)
    {
        Assert.require(code != null);
        Assert.require(errorMsgKey != null);

        String msg = _resources.getMessage(errorMsgKey, code.fullName());
        ReadError error =
            new ReadError(ReadError.FATAL_ERROR_LEVEL, msg, code);
        errorHandler().handle(error);

        // Since this is a fatal error there shouldn't be any subsequent
        // attempts to use _charIterator. Setting it to null here will help
        // ensure that that is true (though we have to close it first).
        if (_charIterator != null)
        {
            _charIterator.close();
            _charIterator = null;
        }
    }
}
