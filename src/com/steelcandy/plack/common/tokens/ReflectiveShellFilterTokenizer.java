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

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
    A base class for shell filter tokenizers whose subtokenizers'
    fully-qualified class names are obtained from a Resources object.
    <p>
    The (i + 1)'th subtokenizer's fully-qualified class name (where i >= 0)
    is given by the String resource in th Resources object with the key
    constructed from the specified prefix with the (string representation of
    the) value 'i - 1' appended to it. Note that this means that the resource
    keys are zero-based, so for example the first subtokenizer's class name
    resource key ends in '0'.
    <p>
    There are assumed to be 'n' subtokenizers, where 'n' is the smallest
    nonnegative integer such that there is no resource with the key
    constructed (as described above) from 'n' and the specified prefix.
    <p>
    Subclasses don't have to (and rarely do) override anything, though
    usually they provide the Resources object.
    <p>
    If a subtokenizer instance can't be created or isn't of the proper type
    then an UnexpectedException is thrown. (They are considered unexpected
    since it is assumed that a tokenizer of this class is properly
    configured.)

    @author James MacKay
    @version $Revision: 1.8 $
*/
public class ReflectiveShellFilterTokenizer
    extends ShellFilterTokenizer
{
    // Constants

    /**
        The end part of the prefix of the resource keys used to look up the
        class name of an instance's filter subtokenizers: the resource key
        used for the n'th filter subtokenizer will be
        _prefix + FILTER_TOKENIZER_KEY_PREFIX_SUFFIX + (n - 1).
    */
    public static final String FILTER_TOKENIZER_KEY_PREFIX_SUFFIX = "filter";


    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NOT_A_FILTER_TOKENIZER_CLASS_MSG =
            "NOT_A_FILTER_TOKENIZER_CLASS_MSG";


    // Private fields

    /** The Resources from which to get our subtokenizers' class names. */
    private Resources _subtokenizerResources;

    /**
        The prefix of the resource keys to use to look up the fully-qualified
        class names of the subtokenizers.
    */
    private String _prefix;


    // Constructors

    /**
        Constructs a ReflectiveShellFilterTokenizer.

        @param subtokenizerResources the Resources object from which the
        fully-qualified names of the shell tokenizer's subtokenizers are to
        be obtained
        @param resourceKeyPrefix the prefix of the resource keys to use to
        look up the fully-qualified class names of the subtokenizers
        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public ReflectiveShellFilterTokenizer(Resources subtokenizerResources,
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
        this class' <code>initialize()</code> method has been called.

        @param newPrefix this tokenizer's new resource key prefix
        @see FilterTokenizer#initialize(Tokenizer)
    */
    public void setPrefix(String newPrefix)
    {
        Assert.require(newPrefix != null);

        _prefix = newPrefix;
    }


    // Protected methods

    /**
        @exception UnexpectedException thrown if a filter tokenizer couldn't
        be created or isn't a FilterTokenizer
        @see ShellFilterTokenizer#createSubtokenizers
    */
    protected FilterTokenizer[] createSubtokenizers(ErrorHandler handler)
    {
        List subtokenizers = new ArrayList();
        for (int index = 0; ; index++)
        {
            String key = subtokenizerKey(index);
            String className;
            try
            {
                className = _subtokenizerResources.getRequiredString(key);
            }
            catch (MissingResourceException ex)
            {
                // If the key isn't found then we assume that there are no
                // more filter tokenizers.
                break;  // while
            }

            // Construct the next filter subtokenizer and append it to our
            // list of subtokenizers.
            try
            {
                FilterTokenizer ft = (FilterTokenizer)
                    TokenizerFactory.instance().
                        createTokenizer(className, _subtokenizerResources,
                                        _prefix, handler);
                subtokenizers.add(ft);
            }
            catch (ClassCastException ex)
            {
                String msg = _resources.
                    getMessage(NOT_A_FILTER_TOKENIZER_CLASS_MSG,
                        className, FilterTokenizer.class.getName());
                throw new UnexpectedException(msg, ex);
            }
        }

        // Convert the subtokenizers list to an array.
        FilterTokenizer[] result =
            new FilterTokenizer[subtokenizers.size()];
        result = (FilterTokenizer[]) subtokenizers.toArray(result);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the resource key to use to look up the fully-qualified class
        name of this tokenizer's subtokenizer with the specified index.

        @param index the (zero-based) index of the subtokenizer whose
        resource key is to be returned
        @return the resource key to use to look up the fully-qualified class
        name of this tokenizer's subtokenizer with the specified index
    */
    protected String subtokenizerKey(int index)
    {
        String part1 = Resources.
            keyConcat(_prefix, FILTER_TOKENIZER_KEY_PREFIX_SUFFIX);
        return Resources.keyConcat(part1, "" + index);
    }
}
