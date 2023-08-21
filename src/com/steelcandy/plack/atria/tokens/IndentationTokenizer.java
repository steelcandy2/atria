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

import com.steelcandy.plack.atria.base.AtriaInfo;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A filter tokenizer that
    <ul>
        <li>sets both int values of the INDENT token at the start of each
            logical line to the number of levels its first physical line
            is indented,
        <li>reports an error for each of the second and subsequent indent
            tokens in a logical line that do not represent <em>more</em>
            spaces of indentation than does the logical line's first indent
            token, and
        <li>removes the second and subsequent indent tokens from each
            logical line (which were at the start of the logical line's
            second and subsequent physical lines).
    </ul>
    <p>
    This tokenizer assumes that
    <ul>
        <li>all blank lines and comments have been removed,
        <li>every <em>physical</em> line (and thus every logical line) starts
            with an indent token (and hence there is at least one token in
            each physical line), and
        <li>physical lines have been converted into logical lines in such a
            way that any NEWLINE token that is not at the end of a logical
            line has been removed, but that the indent token that follows it
            has <em>not</em> been removed.
    </ul>
    (This filter will remove the indent tokens that followed NEWLINE tokens
    that weren't at the end of logical lines, once it has checked that the
    indent tokens are valid.) Because of these assumptions, this filter is
    usually only used to process tokens that have already been processed by
    the following tokenizers (in this order, though not necessarily
    consecutively):
    <ul>
        <li>IndentAllLinesTokenizer (to cause every physical line to start
            with an INDENT token),
        <li>PhysicalToLogicalLinesTokenizer (to transform physical lines into
            logical lines), and
        <li>RemovePhysicalNewlinesTokenizer (to remove the NEWLINE tokens
            that aren't at the end of logical lines).
    </ul>

    @author James MacKay
    @version $Revision: 1.1 $
*/
public class IndentationTokenizer
    extends AtriaFilterTokenizer
{
    // Constants

    /** The number of spaces in each indentation level. */
    protected static final int SPACES_PER_LEVEL =
        AtriaInfo.SPACES_PER_INDENT_LEVEL;

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaTokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        BAD_INDENT_MSG =
            "BAD_INDENT_MSG";
    private static final String
        NOT_INDENTED_ENOUGH_MSG =
            "NOT_INDENTED_ENOUGH_MSG";


    // Constructors

    /**
        Constructs an IndentationTokenizer.

        @param handler the error handler the tokenizer is to use to handle
        any errors
    */
    public IndentationTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public IndentationTokenizer(Resources subtokenizerResources,
                                String resourceKeyPrefix,
                                ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Overridden/implemented methods

    /**
        @see BufferedTokenizer#generateTokens
    */
    protected void generateTokens()
    {
        // Get an iterator over the next logical line's tokens.
        TokenList line = nextSourceLine();
        TokenIterator iter = line.iterator();

        if (iter.hasNext())
        {
            // Set the first physical line's INDENT token's int values to the
            // number of levels that its logical line's first and last
            // physical lines, respectively, are indented, then output that
            // INDENT token.
            Token firstIndent = iter.next();
            int numIndentLevels = numberOfIndentLevels(firstIndent);
            firstIndent.setIntValue(numIndentLevels);
            firstIndent.setSecondIntValue(numIndentLevels);
            output(firstIndent);

            // Output the rest of the logical line unchanged except for the
            // remaining indent tokens (which are the first tokens of the
            // second and subsequent physical lines): check that those
            // indent tokens specify more spaces of indentation than did the
            // first line's - reporting those that don't - then don't output
            // them, effectively removing them from the token stream.
            int firstIndentLength = firstIndent.stringValue().length();
            while (iter.hasNext())
            {
                Token tok = iter.next();
                if (isIndentToken(tok) == false)
                {
                    output(tok);
                }
                else  // tok is an indent token
                {
                    checkSubsequentIndents(tok, firstIndentLength);
                    // Note: we don't output internal indent tokens.
                }
            }
        }   // if (iter.hasNext())
    }


    // Protected methods

    /**
        Given an indent token and the tokens that represent the physical line
        that the token is a part of, returns the number of indentation levels
        that the token - or more specifically, its string value - represents.
        <p>
        Note: an error is reported if the token doesn't represent an integral
        number of levels of indentation.

        @param indent the indent token
        @return the number of indentation levels that 'indent''s string value
        represents
    */
    protected int numberOfIndentLevels(Token indent)
    {
        Assert.require(indent != null);
        Assert.require(isIndentToken(indent));
        Assert.require(indent.hasStringValue());

        int result;

        int indentLength = indent.stringValue().length();
        result = indentLength / SPACES_PER_LEVEL;
        if (indentLength % SPACES_PER_LEVEL != 0)
        {
            // The line is not indented an integral number of levels.
            //
            // Note: this tokenizer assumes that all blank lines have been
            // removed, so we don't have to check for them to determine
            // whether to report an error here.
            String msg = _resources.getMessage(BAD_INDENT_MSG,
                new Integer(SPACES_PER_LEVEL), new Integer(indentLength));
            handleError(NON_FATAL_ERROR_LEVEL, msg, indent.location());

            // Set the number of levels to represent the nearest
            // multiple of SPACES_PER_LEVEL spaces.
            double rawLevels = (double) indentLength / SPACES_PER_LEVEL;
            result = ((int) (rawLevels + 0.5));
        }

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        Checks that the specified INDENT token (which shouldn't be the first
        in a logical line) represents more spaces of indentation than the
        specified number of spaces that the logical line's first INDENT token
        represents. If it doesn't then an error is reported.

        @param indent the INDENT token whose amount of indentation is to be
        checked
        @param firstIndentLength the number of spaces of indentation that the
        logical line's first INDENT token represents
    */
    protected void checkSubsequentIndents(Token indent, int firstIndentLength)
    {
        Assert.require(isIndentToken(indent));
        Assert.require(indent.hasStringValue());

        int indentLength = indent.stringValue().length();
        if (indentLength <= firstIndentLength)
        {
            String msg = _resources.getMessage(NOT_INDENTED_ENOUGH_MSG,
                new Integer(firstIndentLength), new Integer(indentLength));
            handleError(NON_FATAL_ERROR_LEVEL, msg, indent.location());
        }
    }

    /**
        @param tok a token
        @return true iff 'tok' is an indent token
    */
    protected boolean isIndentToken(Token tok)
    {
        Assert.require(tok != null);

        return (tok.id() == AtriaTokenManager.INDENT);
    }
}
