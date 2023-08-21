/*
 Copyright (C) 2001-2005 by James MacKay.

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
import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.common.*;

import java.util.*;

/**
    A base class for shell source code tokenizers whose subtokenizers'
    fully-qualified class names are obtained from a Resources object.
    <p>
    Subclasses don't have to (and rarely do) override anything, though
    usually they provide the Resources object.
    <p>
    If a subtokenizer instance can't be created or isn't of the proper type
    then an UnexpectedException is thrown. (They are considered unexpected
    since it is assumed that a tokenizer of this class is properly
    configured.)

    @author James MacKay
*/
public class ReflectiveShellSourceCodeTokenizer
    extends ShellSourceCodeTokenizer
{
    // Constants

    /**
        The default initial part of the resource keys used to look up the
        fully-qualified class names of the subtokenizers of a
        ReflectiveShellSourceCodeTokenizer.
    */
    public static final String DEFAULT_RESOURCE_KEY_PREFIX = "tokenizer";

    /**
        The end part of the resource key used to look up the class name of an
        instance's source code subtokenizer: the resource key used will be
        _prefix + SOURCE_TOKENIZER_KEY_SUFFIX.
    */
    public static final String SOURCE_TOKENIZER_KEY_SUFFIX = "source";


    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_SOURCE_TOKENIZER_RESOURCE_MSG =
            "NO_SOURCE_TOKENIZER_RESOURCE_MSG";
    private static final String
        NOT_A_SOURCE_CODE_TOKENIZER_CLASS_MSG =
            "NOT_A_SOURCE_CODE_TOKENIZER_CLASS_MSG";


    // Private fields

    /** The Resources from which to get our subtokenizers' class names. */
    private Resources _subtokenizerResources;

    /**
        The initial part the resource keys used to look up the
        fully-qualified class names of our source code and filter
        subtokenizers.
    */
    private String _prefix;


    // Constructors

    /**
        Constructs a ReflectiveShellSourceCodeTokenizer. The defaults will
        be used for the resource keys used to look up the class names of the
        subtokenizers.

        @param subtokenizerResources the Resources object from which the
        fully-qualified names of the shell tokenizer's subtokenizers will be
        obtained
        @param handler the error handler the tokenizer is to use to handle
        any errors
        @see #DEFAULT_RESOURCE_KEY_PREFIX
        @see #setPrefix
    */
    public ReflectiveShellSourceCodeTokenizer(Resources subtokenizerResources,
                                              ErrorHandler handler)
    {
        this(subtokenizerResources, DEFAULT_RESOURCE_KEY_PREFIX, handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public ReflectiveShellSourceCodeTokenizer(Resources subtokenizerResources,
                                              String resourceKeyPrefix,
                                              ErrorHandler handler)
    {
        super(handler);
        Assert.require(subtokenizerResources != null);
        Assert.require(resourceKeyPrefix != null);

        _subtokenizerResources = subtokenizerResources;
        setPrefix(resourceKeyPrefix);
    }


    // Public methods

    /**
        Sets this tokenizer's resource key prefix to the specified prefix.
        <p>
        If this method is going to be called then it should be called before
        this class' initialize() methods is called.

        @param newPrefix this tokenizer's new resource key prefix
        @see SourceCodeTokenizer#initialize(SourceCode)
    */
    public void setPrefix(String newPrefix)
    {
        Assert.require(newPrefix != null);

        _prefix = newPrefix;
    }


    // Overridden/implemented methods

    /**
        @see ShellSourceCodeTokenizer#createSourceSubtokenizer
    */
    protected SourceCodeTokenizer
        createSourceSubtokenizer(ErrorHandler handler)
    {
        SourceCodeTokenizer result;
        String className = null;
        String key = sourceCodeSubtokenizerKey();
        try
        {
            className = _subtokenizerResources.getRequiredString(key);
        }
        catch (MissingResourceException ex)
        {
            // No resource with the key was found.
            String msg =
                _resources.getMessage(NO_SOURCE_TOKENIZER_RESOURCE_MSG, key);
            throw new UnexpectedException(msg, ex);
        }

        try
        {
            result = (SourceCodeTokenizer) TokenizerFactory.instance().
                                        createTokenizer(className, handler);
        }
        catch (ClassCastException ex)
        {
            // Subtokenizer class is not SourceCodeTokenizer-derived.
            String msg = _resources.
                getMessage(NOT_A_SOURCE_CODE_TOKENIZER_CLASS_MSG,
                    className, SourceCodeTokenizer.class.getName());
            throw new UnexpectedException(msg, ex);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @exception UnexpectedException thrown if a filter tokenizer couldn't
        be created or isn't a FilterTokenizer
        @see ShellSourceCodeTokenizer#createFilterSubtokenizer
    */
    protected FilterTokenizer
        createFilterSubtokenizer(ErrorHandler handler)
    {
        FilterTokenizer result =
            new ReflectiveShellFilterTokenizer(_subtokenizerResources,
                                               _prefix, handler);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @return the resource key to use to look up the fully-qualified class
        name of this tokenizer's source code subtokenizer
    */
    protected String sourceCodeSubtokenizerKey()
    {
        return Resources.keyConcat(_prefix, SOURCE_TOKENIZER_KEY_SUFFIX);
    }
}
