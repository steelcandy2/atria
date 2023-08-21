/*
 Copyright (C) 2001-2015 by James MacKay.

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

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.common.*;

/**
    An abstract base class for tokenizers that tokenize a piece of source
    code using an array of TokenCreator objects. The TokenCreators are
    indexed by the first character of the source code fragments they're to
    create tokens for.

    @author James MacKay
*/
public abstract class TokenCreatorSourceCodeTokenizer
    extends AbstractSourceCodeTokenizer
{
    // Private fields

    /**
        The TokenCreator that is used to create tokens for source fragments
        starting with characters that are not valid indices into our
        _creators array.
    */
    private TokenCreator _defaultCreator = createDefaultTokenCreator();

    /**
        Our array of TokenCreators, indexed by the first character of the
        source code fragments for which they're to create tokens.
    */
    private TokenCreator[] _creators = createTokenCreators();

    /**
        The tokens remaining from the last time we got a TokenCreator to
        create its tokens.
        <p>
        New tokens are added to the end of the list, and they're removed
        from the front of the list.
    */
    private TokenList _currentTokens = TokenList.createLinkedList();

    /**
        The position of the first character in the source fragment that the
        next token will represent.
        <p>
        It is initialized to the first character in the source code (which is
        at offset 0 in the first line of the source code).
    */
    private SourcePosition _nextTokenStart = new SourcePosition(1, 0);


    // Constructors

    /**
        Constructs a TokenCreatorSourceCodeTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public TokenCreatorSourceCodeTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public TokenCreatorSourceCodeTokenizer(Resources subtokenizerResources,
                                           String resourceKeyPrefix,
                                           ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Abstract methods

    /**
        Returns the size of our TokenCreator array: it is only called once,
        when creating the array.

        @return the size of our TokenCreator array
    */
    protected abstract int creatorArraySize();
        // Assert.ensure(result >= 0);

    /**
        Sets the elements of the specified array to the TokenCreators that
        are to be used to create the tokens representing the source fragments
        starting with the character used as an index into the array.
        <p>
        Any element not set by this method will be set to the default
        TokenCreator.

        @param creators the array to set the elements of
        @see #createDefaultTokenCreator
    */
    protected abstract void setTokenCreators(TokenCreator[] creators);

    /**
        Creates and returns the default TokenCreator: the one that creates
        tokens for source fragments starting with characters that are either
        not valid indices into our array of TokenCreators, or for which no
        other TokenCreator has been specified.

        @return the default TokenCreator
        @see #createTokenCreators
        @see #setTokenCreators(TokenCreator[])
    */
    protected abstract TokenCreator createDefaultTokenCreator();
        // Assert.ensure(result != null);

    /**
        @return the SourceLocationFactory to be used to create all tokens'
        locations
    */
    protected abstract SourceLocationFactory locationFactory();


    // Protected methods

    /**
        Creates and returns the array of TokenCreators to use in tokenizing
        the source code.

        @return the array of TokenCreators, indexed by the first character of
        the source code fragments they're to create tokens for
        @see #setTokenCreators(TokenCreator[])
    */
    protected TokenCreator[] createTokenCreators()
    {
        int size = creatorArraySize();
        TokenCreator[] result = new TokenCreator[size];

        // Set each element to the default creator initially.
        Assert.check(_defaultCreator != null);
        for (int i = 0; i < size; i++)
        {
            result[i] = _defaultCreator;
        }

        // Set those elements that should use a creator other than the
        // default one.
        setTokenCreators(result);

        Assert.ensure(result != null);
        // Assert.ensure("no elements of 'result' are null");
        return result;
    }

    /**
        @param tok a token
        @return true iff the token that we returned right before 'tok' is
        the last token that we should return
    */
    protected boolean doStopBefore(Token tok)
    {
        Assert.require(tok != null);

        return false;
    }

    /**
        @see AbstractTokenizer#canGetNextToken
    */
    protected boolean canGetNextToken()
    {
        if (_currentTokens.isEmpty())
        {
            createMoreTokens();
        }

        return (_currentTokens.isEmpty() == false) &&
                (doStopBefore(_currentTokens.get(0)) == false);
    }

    /**
        @see AbstractTokenizer#getNextToken
    */
    protected Token getNextToken()
    {
        Assert.require(canGetNextToken());

        if (_currentTokens.isEmpty())
        {
            createMoreTokens();
        }

        Assert.check(_currentTokens.isEmpty() == false);
        Token result = _currentTokens.get(0);
        _currentTokens.remove(0);
        return result;
    }

    /**
        Creates more tokens, if possible, and adds them to _currentTokens.
    */
    protected void createMoreTokens()
    {
        CharacterIterator iter = charIterator();
        while (iter.hasNext())
        {
            TokenCreatorResult r = creatorFor(iter.peek()).
                create(iter, _nextTokenStart, this, errorHandler());
            _nextTokenStart = r.nextTokenStartPosition();
            if (r.firstToken() != null)
            {
                _currentTokens.add(r.firstToken());
                TokenIterator extraIter = r.extraTokens();
                while (extraIter != null && extraIter.hasNext())
                {
                    _currentTokens.add(extraIter.next());
                }
                break;  // while
            }
        }
    }

    /**
        Returns the TokenCreator to use to create the token(s) representing a
        piece of source code that starts with the specified character.

        @param ch the character at the start of the piece of source code that
        the returned TokenCreator is to create the tokens to represent
        @return the TokenCreator to use to create the token(s) representing
        a piece of source code that starts with the specified character
    */
    protected TokenCreator creatorFor(char ch)
    {
        TokenCreator result;

        if (ch < _creators.length)
        {
            result = _creators[(int) ch];
        }
        else
        {
            result = _defaultCreator;
        }

        Assert.ensure(result != null);
        return result;
    }


    /**
        Creates and returns the location of a source fragment starting at the
        specified position and that is the specified length. (The fragment is
        assumed not to span lines.)

        @param startPos the position of the first character in the fragment
        @param length the length of the fragment
        @return the location of the fragment
    */
    protected SourceLocation createLocation(SourcePosition startPos,
                                            int length)
    {
        int endOffset = startPos.offset() + length;
        return locationFactory().create(startPos, endOffset);
    }


    // Inner classes

    /**
        Handles characters that are not valid anywhere in the source code
        when used directly (though they may be valid in string literals or
        comments, for example). Subclasses can override the createError()
        method to handle characters that are only invalid in some contexts,
        or to create a more appropriate or specific error.
    */
    protected class InvalidCharacterTokenCreator
        extends AbstractInvalidCharacterTokenCreator
    {
        // Constructors

        /**
            Constructs an InvalidCharacterTokenCreator.
            <p>
            This constructor is defined because apparently the default one
            is protected.
        */
        public InvalidCharacterTokenCreator()
        {
            // empty
        }


        // Protected methods

        /**
            @see AbstractInvalidCharacterTokenCreator#createSingleCharacterLocation(SourcePosition)
        */
        protected SourceLocation createSingleCharacterLocation(SourcePosition pos)
        {
            Assert.require(pos != null);

            int offset = pos.offset();
            return locationFactory().create(pos.lineNumber(),
                                            offset, offset + 1);
            // Assert.ensure(result != null);
        }

        /**
            @see AbstractInvalidCharacterTokenCreator#tokenizingSourceCode
        */
        protected SourceCode tokenizingSourceCode()
        {
            return sourceCode();
        }
    }
}
