/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.atria.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.source.AtriaSourceLocationFactory;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocationFactory;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    The abstract base class for most of the more complex Atria filter
    tokenizers.
    <p>
    In addition to the methods inherited from its superclass, this class
    manages the source tokenizer and provides several utility methods to
    help in tokenizing Atria source code.
    <p>
    Subclasses have to implement the generateTokens() method.

    @author James MacKay
    @see Token
    @see BufferedTokenizer#generateTokens
*/
public abstract class AtriaFilterTokenizer
    extends BufferedTokenizer
    implements FilterTokenizer
{
    // Private fields

    /** This tokenizer's source tokenizer. */
    private Tokenizer _sourceTokenizer;


    // Constructors

    /**
        Constructs an AtriaFilterTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public AtriaFilterTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AtriaFilterTokenizer(Resources subtokenizerResources,
                            String resourceKeyPrefix, ErrorHandler handler)
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
        Assert.ensure(_sourceTokenizer.sourceCode() != null);
        return _sourceTokenizer.sourceCode();
    }


    // Protected methods

    /**
        @return this tokenizer's source tokenizer
    */
    protected Tokenizer source()
    {
        return _sourceTokenizer;
    }

    /**
        @return the SourceLocationFactory that AtriaFilterTokenizers should
        use to create SourceLocations
    */
    protected SourceLocationFactory locationFactory()
    {
        return AtriaSourceLocationFactory.instance();
        // Assert.ensure(result != null);
    }


    // Protected utility methods

    /**
        Indicates whether the specified list of tokens represents an Atria
        blank line: a line containing only non-code tokens.
        <p>
        This method assumes that the list of tokens contains exactly one
        token with its IS_END_OF_LINE flag set, and that it is the last
        token in the list.

        @param lineTokens the tokens representing the line to check
        @return true iff the tokens represent a blank line
    */
    protected static boolean isBlankLine(TokenList lineTokens)
    {
        // A line is blank iff it doesn't contain any code tokens.
        UnaryTokenPredicate pred = CodeTokenInLinePredicate.instance();
        return (lineTokens.indexOf(pred) < 0);
    }

    /**
        @return the tokens representing the (rest of) the current
        line from the source tokenizer
    */
    protected TokenList nextSourceLine()
    {
        return source().nextThrough(AtriaTokenManager.instance().
                                                endOfLinePredicate());
    }
}
