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

import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.common.Resources;

/**
    The abstract base class for tokenizers that are a shell around a
    SourceCodeTokenizer and a FilterTokenizer.
    <p>
    The tokens output by an instance of this class are the tokens resulting
    from the source code being tokenized by the SourceCodeTokenizer, then
    processed by the FilterTokenizer.
    <p>
    Note that the fact that an instance of this class wraps exactly one
    FilterTokenizer isn't really a limitation: a NullFilterTokenizer can be
    used in the (presumably rare) case where no FilterTokenizer is needed,
    and a ShellFilterTokenizer can be used where more than one
    FilterTokenizer is needed.
    <p>
    Subclasses just have to implement the createSourceSubtokenizer() and
    createFilterSubtokenizer() methods.

    @author James MacKay
    @see SourceCodeTokenizer
    @see FilterTokenizer
*/
public abstract class ShellSourceCodeTokenizer
    extends ErrorHandlerTokenizer
    implements SourceCodeTokenizer
{
    // Private fields

    /** The source subtokenizer. */
    private SourceCodeTokenizer _sourceSubtokenizer;

    /** The filter subtokenizer. */
    private FilterTokenizer _filterSubtokenizer;


    // Constructors

    /**
        Constructs a ShellSourceCodeTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public ShellSourceCodeTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public ShellSourceCodeTokenizer(Resources subtokenizerResources,
                                    String resourceKeyPrefix,
                                    ErrorHandler handler)
    {
        // The subtokenizerResources and resourceKeyPrefix arguments are
        // intentionally ignored.
        this(handler);
    }


    // Public methods

    /**
        @see SourceCodeTokenizer#initialize(SourceCode)
    */
    public void initialize(SourceCode sourceCode)
    {
        Assert.require(sourceCode != null);

        createSubtokenizers();
        _sourceSubtokenizer.initialize(sourceCode);
        _filterSubtokenizer.initialize(_sourceSubtokenizer);
    }

    /**
        See SourceCodeTokenizer#close
    */
    public void close()
    {
        _sourceSubtokenizer.close();
    }


    /**
        @see TokenIterator#hasNext
    */
    public boolean hasNext()
    {
        return _filterSubtokenizer.hasNext();
    }

    /**
        @see Tokenizer#hasTokenAfterNext
    */
    public boolean hasTokenAfterNext()
    {
        return _filterSubtokenizer.hasTokenAfterNext();
    }

    /**
        @see TokenIterator#peek
    */
    public Token peek()
    {
        return _filterSubtokenizer.peek();
    }

    /**
        @see Tokenizer#peekTokenAfterNext
    */
    public Token peekTokenAfterNext()
    {
        return _filterSubtokenizer.peekTokenAfterNext();
    }

    /**
        @see TokenIterator#next
    */
    public Token next()
    {
        return _filterSubtokenizer.next();
    }

    /**
        @see TokenIterator#remaining
    */
    public TokenList remaining()
    {
        return nextThrough(UnsatisfiableUnaryTokenPredicate.instance());
    }

    /**
        @see TokenIterator#nextTo
    */
    public TokenList nextTo(UnaryTokenPredicate endPredicate)
    {
        return _filterSubtokenizer.nextTo(endPredicate);
    }

    /**
        @see TokenIterator#nextThrough
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate)
    {
        return _filterSubtokenizer.nextThrough(endPredicate);
    }

    /**
        @see TokenIterator#discardNext
    */
    public void discardNext()
    {
        _filterSubtokenizer.discardNext();
    }

    /**
        @see TokenIterator#discardRemaining
    */
    public void discardRemaining()
    {
        _filterSubtokenizer.discardRemaining();
    }

    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        Assert.check(_sourceSubtokenizer != null);
        SourceCode result = _sourceSubtokenizer.sourceCode();

        Assert.ensure(result != null);
        return result;
    }


    // Abstract methods

    /**
        Creates/obtains and returns this tokenizer's source code
        subtokenizer.

        @param handler the error handler the source code subtokenizer is to
        use to handle any errors
        @return this tokenizer's source code subtokenizer
    */
    protected abstract SourceCodeTokenizer
        createSourceSubtokenizer(ErrorHandler handler);
        // Assert.ensure(result != null);

    /**
        Creates/obtains and returns this tokenizer's filter subtokenizer.
        <p>
        The FilterTokenizer's source tokenizer should not be set in this
        method: it will be set by this class.

        @param handler the error handler the filter subtokenizer is to use
        to handle any errors
        @return this tokenizer's filter subtokenizers
    */
    protected abstract FilterTokenizer
        createFilterSubtokenizer(ErrorHandler handler);
        // Assert.ensure(result != null);


    // Private methods

    /**
        Creates this tokenizer's subtokenizers.

        @see #createSourceSubtokenizer(ErrorHandler)
        @see #createFilterSubtokenizer(ErrorHandler)
    */
    private void createSubtokenizers()
    {
        ErrorHandler handler = errorHandler();
        _sourceSubtokenizer = createSourceSubtokenizer(handler);
        _filterSubtokenizer = createFilterSubtokenizer(handler);

        Assert.ensure(_sourceSubtokenizer != null);
        Assert.ensure(_filterSubtokenizer != null);
    }
}
