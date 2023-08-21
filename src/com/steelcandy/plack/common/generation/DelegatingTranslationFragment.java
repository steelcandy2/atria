/*
 Copyright (C) 2014 by James MacKay.

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

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    The class of TranslationFragment that delegates to another
    TranslationFragment that can be replaced.

    @author  James MacKay
*/
public class DelegatingTranslationFragment
    implements TranslationFragment
{
    // Private fields

    /** The TranslationFragment that we delegate to. */
    private TranslationFragment _delegate;


    // Constructors

    /**
        @return a DelegatingTranslationFragment that (at least initially)
        delegates to an EmptyTranslationFragment
    */
    public static DelegatingTranslationFragment makeEmpty()
    {
        DelegatingTranslationFragment result =
            new DelegatingTranslationFragment(EmptyTranslationFragment.
                                                                instance());

        Assert.ensure(result != null);
        return result;
    }

    /**
        Constructs a DelegatingTranslationFragment from the
        TranslationFragment that it initially delegates to.

        @param delegate the TranslationFragment to initially delegate to
    */
    public DelegatingTranslationFragment(TranslationFragment delegate)
    {
        Assert.require(delegate != null);

        _delegate = delegate;
    }


    // Public methods

    /**
        Replaces the TranslationFragment that we currently delegate to with
        'newDelegate'.

        @param newDelegate the TranslationFragment that we are to replace
        our current delegate with
        @return the TranslationFragment that we replaced with 'newDelegate'
    */
    public TranslationFragment replace(TranslationFragment newDelegate)
    {
        Assert.require(newDelegate != null);

        TranslationFragment result = _delegate;

        _delegate = newDelegate;

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see TranslationFragment#isEmpty
    */
    public boolean isEmpty()
    {
        return _delegate.isEmpty();
    }

    /**
        @see TranslationFragment#write(IndentWriter)
    */
    public void write(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        _delegate.write(w);
    }
}
