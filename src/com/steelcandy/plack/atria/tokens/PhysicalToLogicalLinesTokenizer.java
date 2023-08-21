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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A filter tokenizer that joins physical source code lines into logical
    lines by clearing the IS_END_OF_LINE flag from all tokens that have it
    set and that are not at the end of a logical line.
    <p>
    This filter assumes that all blank lines and comments have been removed.
    Thus it is often preceded by a RemoveBlankLinesTokenizer (as well as a
    tokenizer that removes all comments).
    <p>
    The output of instances of this filter is often processed by a
    RemoveExplicitLineContinuersTokenizer since this filter doesn't remove
    explicit (or implicit) line continuers. (They aren't removed by this
    tokenizer since some source code processors, such as pretty printers, may
    want the explicit line continuers left in.) Instances of this class will
    mark all explicit line continuers that it uses as having been validated
    (by setting their IS_VALIDATED flags). All other explicit line continuers
    are invalid.
    <p>
    The output of instances of this filter is also often processed by a
    RemovePhysicalNewlinesTokenizer, at least when the tokens are for use by
    a compiler or similar language processor. Some processors, such as pretty
    printers, may not want to have an instance's output processed by a
    RemovePhysicalNewlinesTokenizer, though, since it removes all of the
    newline tokens that are at the end of physical but not logical lines.
    <p>
    A <strong>physical line</strong> is the source fragment
    <ul>
        <li>from the first character in the source code up to and including
            the next newline or, if there are no newlines in the source code,
            the last character in the source code, or
        <li>following a newline and up to and including the next newline or,
            if there are no more newlines in the source code, up to and
            including the last character in the source code.
    </ul>
    <p>
    A logical line consists of one or more physical lines. A physical line is
    part of exactly one logical line: thus the first physical line of a
    logical line is either the first physical line in a piece of source code
    or is the next physical line after the last physical line in the previous
    logical line.
    <p>
    A logical line is continued on the next physical line if
    <ul>
        <li>the current physical line is a blank line (as they are defined by
            the Atria programming language), or
        <li>the last token of the current physical line that does not have
            its IS_NON_CODE or IS_END_OF_LINE flag set is an explicit or
            implicit line continuation token.
    </ul>
    The only explicit line continuation token is the
    EXPLICIT_LINE_CONTINUER token. A token is an implicit line continuation
    token iff it is not an explicit line continuation token and it has its
    IS_LINE_CONTINUER flag set. Both explicit and implicit line continuation
    tokens are part of the logical line. (At least currently there are no
    implicit line continuer tokens in Atria.)
    <p>
    Note that only the last token on a physical line (aside from tokens with
    their IS_NON_CODE or IS_END_OF_LINE flag set) is used to determine
    whether a logical line is continued on the next physical line. Thus
    things like unclosed parentheses or braces do not continue a logical line
    on the next physical line, unless they're the last token on a physical
    line.
    <p>
    The sequence of tokens representing a physical or logical line are often
    themselves referred to as physical or logical lines where this does not
    cause any confusion.

    @author James MacKay
    @version $Revision: 1.15 $
    @see RemoveExplicitLineContinuersTokenizer
*/
public class PhysicalToLogicalLinesTokenizer
    extends AtriaFilterTokenizer
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaTokenResourcesLocator.resources;

    /** Resource keys. */
    private static final String
        UNEXPECTED_END_OF_SOURCE_MSG =
            "UNEXPECTED_END_OF_SOURCE_MSG";


    // Constructors

    /**
        Constructs a PhysicalToLogicalLinesTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public PhysicalToLogicalLinesTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public PhysicalToLogicalLinesTokenizer(Resources subtokenizerResources,
                                           String resourceKeyPrefix,
                                           ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see BufferedTokenizer#generateTokens
    */
    protected void generateTokens()
    {
        TokenList logicalLine = TokenList.createArrayList();

        boolean atEndOfLogicalLine = false;
        while (atEndOfLogicalLine == false && source().hasNext())
        {
            TokenList physicalLine = nextSourceLine();

            // The current logical line is continued on the next physical
            // line iff physicalLine is a blank line or a continued line
            // (and all blank lines have been removed previously, so ...)
            if (isContinuedLine(physicalLine))
            {
                // physicalLine is not the last physical line in
                // its logical line. Clear the IS_END_OF_LINE flag
                // from its last token iff it is set.
                Assert.check(physicalLine.isEmpty() == false);
                Token lastToken = physicalLine.getLast();
                if (lastToken.isFlagSet(AtriaTokenManager.IS_END_OF_LINE))
                {
                    Assert.check(lastToken.
                        canClearFlag(AtriaTokenManager.IS_END_OF_LINE));
                    lastToken.clearFlag(AtriaTokenManager.IS_END_OF_LINE);
                }

                if (source().hasNext() == false)
                {
                    // The line ends with a line continuation token, but
                    // we've reached the end of the tokens.
                    String msg =
                        _resources.getMessage(UNEXPECTED_END_OF_SOURCE_MSG);
                    Assert.check(physicalLine.size() >= 2);
                        // since there are no blank lines
                    Token lineContinuer =
                        physicalLine.get(physicalLine.size() - 2);
                    handleError(NON_FATAL_ERROR_LEVEL, msg,
                                lineContinuer.location());
                }
            }
            else
            {
                // physicalLine is the last physical line in logicalLine.
                atEndOfLogicalLine = true;
            }

            // Add all of physicalLine's tokens to logicalLine.
            logicalLine.addAll(physicalLine);
        }

        // Output the logical line.
        output(logicalLine);
    }

    /**
        Indicates whether the specified (list of tokens representing a)
        physical line is a continued line: that is, whether it ends, ignoring
        comments, etc., with a line continuation token. It also marks any
        explicit line continuation tokens at the end of the line as having
        been validated.

        @param physicalLine the (tokens representing the) physical line to
        test
        @return true iff the physical line is a continued line
    */
    protected boolean isContinuedLine(TokenList physicalLine)
    {
        Assert.require(isBlankLine(physicalLine) == false);
            // though this should never happen in this filter

        boolean result = false;

        // Find the index of the token in physicalLine that indicates whether
        // or not 'physicalLine' is the last physical line in the current
        // logical line. (That token should always exist since 'physicalLine'
        // isn't a blank line.)
        int index = physicalLine.
                        lastIndexOf(CodeTokenInLinePredicate.instance());
        Assert.check(index >= 0);
        Token lastToken = physicalLine.get(index);
        if (lastToken.isFlagSet(AtriaTokenManager.IS_LINE_CONTINUER))
        {
            result = true;

            // If the lastToken is an explicit line continuation
            // token then mark it as valid.
            if (lastToken.id() == AtriaTokenManager.EXPLICIT_LINE_CONTINUER)
            {
                Assert.check(lastToken.
                                canSetFlag(AtriaTokenManager.IS_VALIDATED));
                lastToken.setFlag(AtriaTokenManager.IS_VALIDATED);
            }
        }

        return result;
    }
}
