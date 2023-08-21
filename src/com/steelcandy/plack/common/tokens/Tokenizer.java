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
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;

import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by all classes that are a source of tokens.
    <p>
    Note that while there are methods to check for and peek at the token
    after the next token, there are no methods (like next()) for extracting
    it. This is intentional: the tokens must be extracted in order.
    <p>
    Tokenizers usually have an <code>initialize()</code> method that must be
    called before any other methods are called on it. (No
    <code>initialize()</code> method is declared in this interface since
    different kinds of tokenizers' <code>initialize()</code> methods will
    take different parameters.)
    <p>
    Most Tokenizer classes should define a constructor that takes an
    ErrorHandler argument. All Tokenizer classes should also define a
    constructor that takes a Resources object, a String resource key prefix,
    and an ErrorHandler: this will allow Tokenizers of that class to be
    created via reflection. For most Tokenizer classes this three-parameter
    constructor can ignore the Resources and resource key prefix parameter
    and just be implemented as <code>this(handler)</code>, where
    <code>handler</code> is the constructor's ErrorHandler parameter.

    @author James MacKay
    @version $Revision: 1.13 $
    @see Token
    @see ReflectiveShellFilterTokenizer
    @see ReflectiveShellSourceCodeTokenizer
*/
public interface Tokenizer
    extends TokenIterator, ErrorSeverityLevels
{
    // Public methods

    /**
        @return the source code that the tokens output by this tokenizer
        originated from
    */
    public SourceCode sourceCode();
        // Assert.ensure(result != null);

    /**
        Sets the error handler that this tokenizer is to use to handle all
        errors to the specified handler.

        @param handler this tokenizer's new error handler
    */
    public void setErrorHandler(ErrorHandler handler);
        // Assert.require(handler != null);

    /**
        @return this tokenizer's error handler
    */
    public ErrorHandler errorHandler();


    /**
        Indicates whether this tokenizer has at least one more token after
        its next one: that is, whether it will return at least two more
        tokens (via calls to next()).

        @return true iff this iterator will return at least two more tokens
    */
    public boolean hasTokenAfterNext();
        // Assert.ensure("hasNext() == false implies result == false");

    /**
        @return the token after the next token, without consuming any tokens
        from this tokenizer
        @exception NoSuchItemException thrown iff there isn't another token
        after the next token
        @see #hasTokenAfterNext
        @see TokenIterator#peek
    */
    public Token peekTokenAfterNext();
}
