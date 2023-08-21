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
    The abstract base class for filter tokenizers that are a shell around a
    sequence of tokenizers consisting of zero or more subtokenizers that are
    themselves FilterTokenizers.
    <p>
    The tokens output by an instance of this class are the tokens resulting
    from passing the tokens through each of its FilterTokenizer subtokenizers
    in order.
    <p>
    Subclasses just have to implement the createSubtokenizers() method.

    @author James MacKay
    @version $Revision: 1.9 $
    @see FilterTokenizer
*/
public abstract class ShellFilterTokenizer
    extends ErrorHandlerTokenizer
    implements FilterTokenizer
{
    // Private fields

    /** The sequence of (one or more) filter subtokenizers. */
    private FilterTokenizer[] _subtokenizers;

    /**
        The last subtokenizer: it will be the last element of the
        _subtokenizers array.
    */
    private FilterTokenizer _lastSubtokenizer;


    // Constructors

    /**
        Constructs a ShellFilterTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public ShellFilterTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public ShellFilterTokenizer(Resources subtokenizerResources,
                                String resourceKeyPrefix,
                                ErrorHandler handler)
    {
        // The subtokenizerResources and resourceKeyPrefix arguments
        // are intentionally ignored.
        this(handler);
    }


    // Public methods

    /**
        @see FilterTokenizer#initialize(Tokenizer)
    */
    public void initialize(Tokenizer sourceTokenizer)
    {
        createSubtokenizers();

        // Initialize the subtokenizers: the first is initialized to have
        // this tokenizer's source tokenizer as its source tokenizer, and
        // the others are initialized so that each has the preceding
        // subtokenizer as its source tokenizer.
        Tokenizer source = sourceTokenizer;
        for (int i = 0; i < _subtokenizers.length; i++)
        {
            _subtokenizers[i].initialize(source);
            source = _subtokenizers[i];
        }
    }


    // Implemented Tokenizer methods

    /**
        @see TokenIterator#hasNext
    */
    public boolean hasNext()
    {
        return _lastSubtokenizer.hasNext();
    }

    /**
        @see Tokenizer#hasTokenAfterNext
    */
    public boolean hasTokenAfterNext()
    {
        return _lastSubtokenizer.hasTokenAfterNext();
    }

    /**
        @see TokenIterator#peek
    */
    public Token peek()
    {
        return _lastSubtokenizer.peek();
    }

    /**
        @see Tokenizer#peekTokenAfterNext
    */
    public Token peekTokenAfterNext()
    {
        return _lastSubtokenizer.peekTokenAfterNext();
    }

    /**
        @see TokenIterator#next
    */
    public Token next()
    {
        return _lastSubtokenizer.next();
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
        return _lastSubtokenizer.nextTo(endPredicate);
    }

    /**
        @see TokenIterator#nextThrough
    */
    public TokenList nextThrough(UnaryTokenPredicate endPredicate)
    {
        return _lastSubtokenizer.nextThrough(endPredicate);
    }

    /**
        @see TokenIterator#discardNext
    */
    public void discardNext()
    {
        _lastSubtokenizer.discardNext();
    }

    /**
        @see TokenIterator#discardRemaining
    */
    public void discardRemaining()
    {
        _lastSubtokenizer.discardRemaining();
    }

    /**
        @see Tokenizer#sourceCode
    */
    public SourceCode sourceCode()
    {
        Assert.check(_subtokenizers != null);
        Assert.check(_subtokenizers.length > 0);
        SourceCode result = _subtokenizers[0].sourceCode();

        Assert.ensure(result != null);
        return result;
    }


    // Private methods

    /**
        Creates this tokenizer's subtokenizers.

        @see #createSubtokenizers(ErrorHandler)
    */
    private void createSubtokenizers()
    {
        ErrorHandler handler = errorHandler();
        _subtokenizers = createSubtokenizers(handler);
        if (_subtokenizers == null || _subtokenizers.length < 1)
        {
            // Use a NullFilterTokenizer as the sole subtokenizer.
            _subtokenizers = new FilterTokenizer[1];
            _subtokenizers[0] = new NullFilterTokenizer(handler);
        }
        _lastSubtokenizer = _subtokenizers[_subtokenizers.length - 1];

        Assert.ensure(_subtokenizers != null);
        Assert.ensure(_subtokenizers.length > 0);
    }


    // Abstract methods

    /**
        Creates/obtains and returns this tokenizer's (filter) subtokenizers.
        They should appear in the array in the order in which they're to be
        applied to the tokens. If the returned array is null or empty then
        this shell filter tokenizer will behave like a NullFilterTokenizer.
        <p>
        The FilterTokenizers' source tokenizers should not be set in this
        method: this class will do that.

        @param handler the error handler the subtokenizers are to use to
        handle any errors
        @return this tokenizer's subtokenizers
        @see NullFilterTokenizer
    */
    protected abstract FilterTokenizer[]
        createSubtokenizers(ErrorHandler handler);
}
