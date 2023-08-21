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

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.common.*;

/**
    An abstract base class for filter tokenizers. This class maintains the
    source tokenizer: that is, the tokenizer from which the tokenizer gets
    its tokens. The source tokenizer can be obtained by calling the source()
    method.
    <p>
    Subclasses only have to implement the canGetNextToken() and
    getNextToken() methods, though they may want to override the initialize()
    method (in which case they should call the superclass' version from the
    new version).

    @author James MacKay
    @version $Revision: 1.6 $
    @see Token
*/
public abstract class AbstractFilterTokenizer
    extends AbstractTokenizer
    implements FilterTokenizer
{
    // Private fields

    /** The tokenizer that is the source of this tokenizer's tokens. */
    private Tokenizer _sourceTokenizer;


    // Constructors

    /**
        Constructs an AbstractFilterTokenizer.

        @param handler the error handler that the tokenizer should use to
        handle errors
    */
    public AbstractFilterTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AbstractFilterTokenizer(Resources subtokenizerResources,
                                   String resourceKeyPrefix,
                                   ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Public methods

    /**
        @see FilterTokenizer#initialize(Tokenizer)
    */
    public void initialize(Tokenizer sourceTokenizer)
    {
        Assert.require(sourceTokenizer != null);

        _sourceTokenizer = sourceTokenizer;
    }


    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        SourceCode result = _sourceTokenizer.sourceCode();

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @return the Tokenizer that is this tokenizer's source of tokens
    */
    protected Tokenizer source()
    {
        return _sourceTokenizer;
    }
}
