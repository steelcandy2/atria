/*
 Copyright (C) 2001-2004 by James MacKay.

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

import com.steelcandy.common.*;
import com.steelcandy.common.creation.ObjectCreator;
import com.steelcandy.common.creation.ObjectCreatorException;

/**
    A singleton factory class that creates Tokenizers.

    @author James MacKay
    @see Tokenizer
*/
public class TokenizerFactory
{
    // Constants

    /** The single instance of this class. */
    private static final TokenizerFactory _instance = new TokenizerFactory();


    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        CREATE_TOKENIZER_FAILED_MSG =
            "CREATE_TOKENIZER_FAILED_MSG";


    /**
        The array of Class objects that represent the types of the arguments
        passed by default to the constructor use to create a Tokenizer.
    */
    protected static final Class[] DEFAULT_TOKENIZER_CTOR_ARG_TYPES =
        new Class[]
        {
            ErrorHandler.class
        };

    /**
        The array of Class objects that represent the types of the arguments
        passed by default to the constructor use to create a possibly
        reflective Tokenizer.
    */
    protected static final Class[] REFLECTIVE_TOKENIZER_CTOR_ARG_TYPES =
        new Class[]
        {
            Resources.class,
            String.class,
            ErrorHandler.class
        };

    // Constructors

    /**
        @return the single instance of this class
    */
    public static TokenizerFactory instance()
    {
        return _instance;
    }

    /**
        Constructs a TokenizerFactory. It should only be called by
        subclass' constructors and to construct the single instance
        of this class.
    */
    protected TokenizerFactory()
    {
        // empty
    }


    // Factory methods

    /**
        Creates and returns a SourceCodeTokenizer that has the same effect as
        would applying the specified SourceCodeTokenizer to a piece of source
        code, then applying the specified FilterTokenizers in order to the
        resulting token stream.
        <p>
        The specified ErrorHandler will be used in constructing all of the
        Tokenizers created by this method, but the specified subtokenizers
        are not changed to use it (and currently they can't be). Thus the
        subtokenizers should usually have been constructed using the same
        handler.

        @param sourceSubtokenizer the SourceCodeTokenizer subtokenizer that
        converts source code into the token stream that the FilterTokenizer
        subtokenizers will process
        @param filterSubtokenizers the SourceCodeTokenizer's filter
        subtokenizers
        @param handler the handler that all Tokenizers created by this method
        are to use to handle all errors
        @return a SourceCodeTokenizer that has the same effect as applying
        the SourceCodeTokenizer subtokenizer and then the FilterTokenizer
        subtokenizers in order
    */
    public SourceCodeTokenizer
        createSourceCodeTokenizer(SourceCodeTokenizer sourceSubtokenizer,
                                  FilterTokenizer[] filterSubtokenizers,
                                  ErrorHandler handler)
    {
        Assert.require(sourceSubtokenizer != null);

        SourceCodeTokenizer result = null;
        if (filterSubtokenizers == null || filterSubtokenizers.length == 0)
        {
            // There are no FilterTokenizer subtokenizers, so we can just
            // return the SourceCodeTokenizer subtokenizer.
            result = sourceSubtokenizer;
        }
        else
        {
            FilterTokenizer filter =
                new FixedShellFilterTokenizer(filterSubtokenizers, handler);
            result = new FixedShellSourceCodeTokenizer(sourceSubtokenizer,
                                                       filter, handler);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a FilterTokenizer that has the same effect as
        would applying the specified subtokenizers in the order that they
        appear in the array. (If the array is null or empty then the
        FilterTokenizer will pass tokens from its source through unchanged.)
        <p>
        The specified ErrorHandler will be used in constructing all of the
        Tokenizers created by this method, but the specified subtokenizers
        are not changed to use it (and currently they can't be). Thus the
        subtokenizers should usually have been constructed using the same
        handler.

        @param subtokenizers the FilterTokenizer's subtokenizers
        @param handler the handler that all Tokenizers created by this method
        are to use to handle all errors
        @return a FilterTokenizer that has the same effect as applying the
        subtokenizers in order
    */
    public FilterTokenizer
        createFilterTokenizer(FilterTokenizer[] subtokenizers,
                                ErrorHandler handler)
    {
        FilterTokenizer result = null;
        if (subtokenizers == null || subtokenizers.length == 0)
        {
            result = new NullFilterTokenizer(handler);
        }
        else if (subtokenizers.length > 1)
        {
            result = new FixedShellFilterTokenizer(subtokenizers, handler);
        }
        else
        {
            Assert.check(subtokenizers.length == 1);
            result = subtokenizers[0];
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns a tokenizer that is an instance of the specified
        (Tokenizer-derived) class. The Tokenizer is constructed using the
        specified ErrorHandler as the sole argument passed to its
        constructor.

        @param className the fully-qualified class name of the tokenizer to
        be created
        @param handler the ErrorHandler from which to construct the tokenizer
        @exception UnexpectedException thrown if the tokenizer instance could
        not be created
        @exception ClassCastException thrown if the tokenizer instance could
        be created but isn't a Tokenizer
    */
    public Tokenizer
        createTokenizer(String className, ErrorHandler handler)
    {
        return createTokenizer(className,
                               DEFAULT_TOKENIZER_CTOR_ARG_TYPES,
                               new Object[] { handler });
    }

    /**
        Creates and returns a tokenizer that is an instance of the specified
        (Tokenizer-derived) class. The Tokenizer is constructed using the
        specified arguments (other than the class name) as the arguments
        passed to its constructor.

        @param className the fully-qualified class name of the tokenizer to
        be created
        @param subtokenizerResources the subtokenizer Resources from which to
        construct the tokenizer
        @param resourceKeyPrefix the resource key prefix from which to
        construct the tokenizer
        @param handler the ErrorHandler from which to construct the tokenizer
        @exception UnexpectedException thrown if the tokenizer instance could
        not be created
        @exception ClassCastException thrown if the tokenizer instance could
        be created but isn't a Tokenizer
    */
    public Tokenizer
        createTokenizer(String className, Resources subtokenizerResources,
                        String resourceKeyPrefix, ErrorHandler handler)
    {
        Object[] args = new Object[]
        {
            subtokenizerResources,
            resourceKeyPrefix,
            handler
        };
        return createTokenizer(className,
                               REFLECTIVE_TOKENIZER_CTOR_ARG_TYPES, args);
    }

    /**
        Creates and returns a tokenizer that is an instance of the specified
        (Tokenizer-derived) class.

        @param className the fully-qualified class name of the tokenizer to
        be created
        @param ctorArgTypes the Class objects representing the types of the
        constructor arguments (as specified in the ctorArgs array)
        @param ctorArgs the arguments with which to construct the tokenizer
        @return the tokenizer that was created
        @exception UnexpectedException thrown if the tokenizer instance could
        not be created
        @exception ClassCastException thrown if the tokenizer instance could
        be created but isn't a Tokenizer
    */
    public Tokenizer createTokenizer(String className,
                                     Class[] ctorArgTypes, Object[] ctorArgs)
    {
        Assert.require(ctorArgs != null);
        Assert.require(ctorArgTypes.length == ctorArgs.length);

        Tokenizer result = null;
        try
        {
            Object obj = ObjectCreator.instance().
                create(className, ctorArgTypes, ctorArgs);
            result = (Tokenizer) obj;
        }
        catch (ObjectCreatorException ex)
        {
            Object[] args = new Object[]
            {
                className,
                ex.getLocalizedMessage()
            };
            String msg =
                _resources.getMessage(CREATE_TOKENIZER_FAILED_MSG, args);
            throw new UnexpectedException(msg, ex);
        }
        return result;
    }


    // Inner classes

    /**
        A ShellSourceCodeTokenizer that is constructed from its
        subtokenizers.
    */
    private static class FixedShellSourceCodeTokenizer
        extends ShellSourceCodeTokenizer
    {
        // Private fields

        /** The source code subtokenizer. */
        private SourceCodeTokenizer _sourceSubtokenizer;

        /** The filter subtokenizer. */
        private FilterTokenizer _filterSubtokenizer;


        // Constructors

        /**
            Constructs a FixedShellSourceCodeTokenizer.

            @param sourceSubtokenizer the tokenizer's source code
            subtokenizer
            @param filterSubtokenizer the tokenizer's filter subtokenizer
            @param handler the error handler that the tokenizer will use
            to handle any errors
        */
        public FixedShellSourceCodeTokenizer(
                    SourceCodeTokenizer sourceSubtokenizer,
                    FilterTokenizer filterSubtokenizer,
                    ErrorHandler handler)
        {
            super(handler);

            _sourceSubtokenizer = sourceSubtokenizer;
            _filterSubtokenizer = filterSubtokenizer;
        }


        // Protected methods

        /**
            @see ShellSourceCodeTokenizer#createSourceSubtokenizer
        */
        protected SourceCodeTokenizer
            createSourceSubtokenizer(ErrorHandler handler)
        {
            return _sourceSubtokenizer;
        }

        /**
            @see ShellSourceCodeTokenizer#createFilterSubtokenizer
        */
        protected FilterTokenizer
            createFilterSubtokenizer(ErrorHandler handler)
        {
            return _filterSubtokenizer;
        }
    }

    /**
        A ShellFilterTokenizer that is constructed from its
        subtokenizers.
    */
    private static class FixedShellFilterTokenizer
        extends ShellFilterTokenizer
    {
        // Private fields

        /** Our subtokenizers. */
        private FilterTokenizer[] _subtokenizers;


        // Constructors

        /**
            Constructs a FixedShellFilterTokenizer.

            @param subtokenizers the tokenizer's subtokenizers
            @param handler the error handler that the tokenizer will use
            to handle any errors
        */
        public FixedShellFilterTokenizer(FilterTokenizer[] subtokenizers,
                                            ErrorHandler handler)
        {
            super(handler);
            _subtokenizers = subtokenizers;
        }


        // Protected methods

        /**
            @see ShellFilterTokenizer#createSubtokenizers
        */
        protected FilterTokenizer[]
            createSubtokenizers(ErrorHandler handler)
        {
            return _subtokenizers;
        }
    }
}
