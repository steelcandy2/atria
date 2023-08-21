/*
 Copyright (C) 2005-2006 by James MacKay.

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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A filter tokenizer that removes all explicit line continuation tokens
    (that is, all EXPLICIT_LINE_CONTINUER tokens), reporting those that are
    not marked as being valid (that is, those without their IS_VALIDATED flag
    set) as errors.
    <p>
    Since JoinLinesTokenizers are currently the only objects that set
    EXPLICIT_LINE_CONTINUER tokens' IS_VALIDATED flag, this filter is usually
    only used to process tokens that have already been processed by a
    JoinLinesTokenizer.

    @author James MacKay
    @see JoinLinesTokenizer
    @see AtriaTokenManagerBase#EXPLICIT_LINE_CONTINUER
*/
public class RemoveExplicitLineContinuersTokenizer
    extends RemoveNonValidatedTokensTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaTokenResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        MISPLACED_EXPLICIT_LINE_CONTINUER_MSG =
            "MISPLACED_EXPLICIT_LINE_CONTINUER_MSG";


    // Constructors

    /**
        Constructs a RemoveExplicitLineContinuersTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public RemoveExplicitLineContinuersTokenizer(ErrorHandler handler)
    {
        super(AtriaTokenManager.EXPLICIT_LINE_CONTINUER, handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public RemoveExplicitLineContinuersTokenizer(Resources subtokenizerResources,
                                                 String resourceKeyPrefix,
                                                 ErrorHandler handler)
    {
        super(AtriaTokenManager.EXPLICIT_LINE_CONTINUER,
              subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see RemoveNonValidatedTokensTokenizer#handleNonValidatedToken
    */
    protected void handleNonValidatedToken(Token tok)
    {
        // The only way an explicit line continuer can be invalid is if it
        // does not appear at the end of a physical line (ignoring any
        // comments or whitespace).
        String msg =
            _resources.getMessage(MISPLACED_EXPLICIT_LINE_CONTINUER_MSG);
        handleError(NON_FATAL_ERROR_LEVEL, msg, tok.location());
    }
}
