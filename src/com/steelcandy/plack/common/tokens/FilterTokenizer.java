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

/**
    The interface implemented by all tokenizer classes that generate tokens
    by processing the tokens obtained from another tokenizer. This latter
    tokenizer is referred to as the tokenizer's <strong>source
    tokenizer</strong>.
    <p>
    An instance's <code>initialize(Tokenizer)</code> method must be called
    before any of its other methods are called.

    @author James MacKay
*/
public interface FilterTokenizer
    extends Tokenizer
{
    /**
        Initializes this tokenizer and sets the specified tokenizer as the
        tokenizer that it will gets its tokens from.

        @param sourceTokenizer the tokenizer that this tokenizer will get its
        tokens from
    */
    public void initialize(Tokenizer sourceTokenizer);
        // Assert.require(sourceTokenizer != null);
}
