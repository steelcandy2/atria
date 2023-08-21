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
    A tokenizer that tokenizes a piece of source code written in the Atria
    programming language.
    <p>
    The tokens created by this tokenizer are usually used as input to a
    sequence of other tokenizers that perform other operations on the tokens.

    @author James MacKay
*/
public class AtriaSourceCodeTokenizer
    extends AtriaSourceCodeTokenizerBase
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaTokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TAB_IN_LEADING_WHITESPACE_MSG =
            "TAB_IN_LEADING_WHITESPACE_MSG";
    private static final String
        TAB_IN_SOURCE_MSG =
            "TAB_IN_SOURCE_MSG";
    private static final String
        NO_MORE_TOKENS_CREATED_MSG =
            "NO_MORE_TOKENS_CREATED_MSG";
    private static final String
        UNTERMINATED_TEXT_LITERAL_MSG =
            "UNTERMINATED_TEXT_LITERAL_MSG";
    private static final String
        INVALID_TEXT_LITERAL_CHAR_MSG =
            "INVALID_TEXT_LITERAL_CHAR_MSG";


    // Constructors

    /**
        Constructs a tokenizer from the error handler that it should use.

        @param handler the error handler that the tokenizer should use
        to handle any errors
    */
    public AtriaSourceCodeTokenizer(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AtriaSourceCodeTokenizer(Resources subtokenizerResources,
        String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see AbstractSourceCodeTokenizer#charToReturnOnReadError
    */
    protected char charToReturnOnReadError()
    {
        // Is there a better character to return here ???!!!???
        return AtriaInfo.COMMENT_START_CHAR;
    }


    // TokenCreator creation methods

    /**
        @see AtriaSourceCodeTokenizerBase#createInvalidCharacterTokenCreator
    */
    protected TokenCreator createInvalidCharacterTokenCreator()
    {
        return new OtherInvalidCharacterTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createTabTokenCreator
    */
    protected TokenCreator createTabTokenCreator()
    {
        return new TabTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createNewlineTokenCreator
    */
    protected TokenCreator createNewlineTokenCreator()
    {
        return new NewlineTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createSpaceTokenCreator
    */
    protected TokenCreator createSpaceTokenCreator()
    {
        return new SpaceTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createTextTokenCreator
    */
    protected TokenCreator createTextTokenCreator()
    {
        return new TextTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createNameTokenCreator
    */
    protected TokenCreator createNameTokenCreator()
    {
        return new NameTokenCreator();
    }

    /**
        @see AtriaSourceCodeTokenizerBase#createCommentTokenCreator
    */
    protected TokenCreator createCommentTokenCreator()
    {
        return new CommentTokenCreator();
//        return new DiscardCommentTokenCreator();
    }


    // Inner TokenCreator classes

    /**
        Handles invalid characters in the source code other than those (like
        tabs) that are handled specially.
        <p>
        Note: rather than report an error immediately, this token creator
        creates an InvalidCharacterToken, which is then (presumably) used
        to report an error later, possibly after aggregating consecutive
        InvalidCharacterTokens.
        <p>
        The motivation for inserting InvalidCharacterTokens into the token
        stream is to help prevent spurious errors: for example, if the token
        before an invalid character was an implicit line continuer then just
        discarding the invalid character without inserting a token into the
        token stream will leave that implicit line continuer as the last
        token on the line, which will result in the next line being treated
        as a continuation of the token's line. And if (as is very possible)
        that next line isn't indented under the token's line then a number
        of spurious errors can result.
    */
    private class OtherInvalidCharacterTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            char invalidChar = iter.next();

            return createOneTokenResult(tokenManager().
                    createInvalidCharacterToken(startPos, 1, invalidChar));
        }
    }

    /**
        Handles tabs in the source code. These are invalid in Atria source
        code, but are handled specially for several reasons:
        <ul>
            <li>it is uncommon for tabs to be invalid in source code
            <li>tabs can be treated as non-fatal errors unless they show up
                in leading whitespace, in which case the indentation is
                probably hopelessly out of whack. Thus we treat the latter
                case as a fatal error
        </ul>
        <p>
        Note that we only handle leading whitespace that <em>starts</em>
        with a tab. The TokenCreator(s) that handle other leading
        whitespace will will have to look for and handle tabs too.

        @see SpaceTokenCreator
    */
    private class TabTokenCreator
        extends InvalidCharacterTokenCreator
    {
        // Protected methods

        /**
            @see AbstractInvalidCharacterTokenCreator#createError
        */
        protected TokenizingError
            createError(char invalidChar, SourcePosition charPosition)
        {
            boolean isFatal = false;
            String msg;
            SourceLocation loc = createLocation(charPosition, 1);
            if (charPosition.offset() == 0)
            {
                // The tab is at the start of some leading whitespace,
                // which is considered fatal since it messes up the
                // indentation.
                msg = _resources.getMessage(TAB_IN_LEADING_WHITESPACE_MSG);
                isFatal = true;
            }
            else
            {
                msg = _resources.getMessage(TAB_IN_SOURCE_MSG);
            }

            int level = isFatal ? FATAL_ERROR_LEVEL : NON_FATAL_ERROR_LEVEL;
            return new TokenizingError(level, msg,
                                       tokenizingSourceCode(), loc);
        }
    }

    /**
        Creates the tokens representing newline characters, which
        can be CR, LF or CRLF.
    */
    private class NewlineTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            char ch = iter.peek();
            Assert.require(AtriaInfo.isNewlineStart(ch));
            Assert.require(ch == '\n' || ch == '\r');

            // If the newline starts with a carriage return then a
            // line feed that follows it is part of the newline, so
            // consume it too.
            iter.discard();    // the first character of the newline
            int length = 1;
            if (ch == '\r' && iter.peek() == '\n')
            {
                // TODO: at least under UNIX (well, at least under Linux),
                // a CRLF seems to count as a single character, so we
                // don't increment 'length' here. This should be checked
                // on other platforms, though, and handled appropriately.
                // length++;
                iter.discard();
            }

            // Note: a newline token's positionAfter() method will return
            // the correct next token start position.
            return createOneTokenResult(tokenManager().
                                createNewlineToken(startPos, length));
        }
    }

    /**
        Creates tokens for indentation/leading whitespace only: spaces
        that are not part of leading whitespace are discarded.
    */
    private class SpaceTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            Assert.require(iter.peek() == ' ');

            TokenCreatorResult result;
            if (startPos.offset() > 0)
            {
                // The space is not the start of leading whitespace, so
                // we discard it and any following spaces. No token is
                // created.
                iter.discard();
                int nextOffset = startPos.offset() + 1;
                while (iter.hasNext() && iter.peek() == ' ')
                {
                    iter.discard();
                    nextOffset += 1;
                }
                result = createZeroTokenResult(startPos.lineNumber(),
                                               nextOffset);
            }
            else
            {
                result = createIndent(iter, startPos, t, handler);
            }
            return result;
        }


        // Protected methods

        /**
            Creates and returns a TokenCreatorResult containing the
            result of creating the token representing the indentation/
            leading whitespace that the iterator is on the first
            character of.
            <p>
            Any tabs in the indentation are handled appropriately.

            @see TokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        protected TokenCreatorResult
            createIndent(CharacterIterator iter, SourcePosition startPos,
                         Tokenizer t, ErrorHandler handler)
        {
            Assert.require(iter.peek() == ' ');

            StringBuffer buf = new StringBuffer();
            buf.append(iter.next());
            int length = 1;
            while (iter.hasNext())
            {
                char ch = iter.peek();
                if (ch == ' ')
                {
                    buf.append(iter.next());
                }
                else if (ch == AtriaInfo.TAB_CHAR)
                {
                    // The tab in indentation is reported as a fatal
                    // error (see reportTab() below) and the tab is
                    // not included as part of the indentation.
                    SourcePosition pos =
                        new SourcePosition(startPos.lineNumber(),
                                           startPos.offset() + length);
                    reportTab(handler, t, pos);
                    iter.discard();
                }
                else
                {
                    break;    // while
                }
                length++;
            }

            return createOneTokenResult(tokenManager().
                createIndentToken(startPos, length, buf.toString()));
        }

        /**
            Reports the presence of a tab in leading whitespace.

            @param handler the error handler to use to handle any
            errors
            @param t the tokenizer that the token's being created for
            @param tabPos the position of the tab character in the
            source code
        */
        protected void reportTab(ErrorHandler handler, Tokenizer t,
                                 SourcePosition tabPos)
        {
            // The tab is in the middle of some leading whitespace,
            // which is considered fatal since it messes up indentation.
            String msg = _resources.
                getMessage(TAB_IN_LEADING_WHITESPACE_MSG);
            TokenizingError error =
                new TokenizingError(FATAL_ERROR_LEVEL, msg,
                                    t.sourceCode(),
                                    createLocation(tabPos, 1));
            handler.handle(error, t);
        }
    }

    /**
        Creates tokens representing literal text.
        <p>
        Note: there are no such thing as escaped characters in literal text
        in Atria.
    */
    private class TextTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            Assert.require(iter.peek() == '"');

            iter.discard();    // handle the opening quote
            int length = 1;

            boolean foundClosingQuote = false;
            StringBuffer buf = new StringBuffer();
            while (foundClosingQuote == false && iter.hasNext())
            {
                char ch = iter.peek();
                if (AtriaInfo.isNewlineStart(ch))
                {
                    break;  // while
                }
                else if (ch == '"')
                {
                    // A second quote is the text's closing quote. We don't
                    // include it in the text's contents.
                    foundClosingQuote = true;
                    iter.discard();    // closing quote
                    length += 1;
                    break;  // while
                }
                else
                {
                    if (AtriaInfo.isTextCharacter(ch) == false)
                    {
                        reportInvalidChar(ch, handler, t, startPos, length);
                    }
                    buf.append(ch);
                    iter.discard();
                    length += 1;
                }
            }

            // If the text literal wasn't terminated by another double
            // quote then report it. We still return a token, though.
            if (foundClosingQuote == false)
            {
                reportUnterminatedLiteral(handler, t, startPos, length);
            }

            return createOneTokenResult(tokenManager().
                    createTextToken(startPos, length, buf.toString()));
        }


        // Protected methods

        /**
            Reports that this text literal is not terminated by a double
            quote on the same line the literal started on.

            @param handler the error handler to use to report the
            unterminated literal
            @param t the tokenizer the token's being created for
            @param startPos the position in the source code of the string
            literal's opening quote
            @param length the length of the (incomplete) text literal
        */
        protected void
            reportUnterminatedLiteral(ErrorHandler handler, Tokenizer t,
                                      SourcePosition startPos, int length)
        {
            String msg = _resources.
                getMessage(UNTERMINATED_TEXT_LITERAL_MSG);
            TokenizingError error =
                new TokenizingError(NON_FATAL_ERROR_LEVEL, msg,
                                    t.sourceCode(),
                                    createLocation(startPos, length));
            handler.handle(error, t);
        }

        /**
            Reports that this text literal contains an invalid character.

            @param invalidChar the invalid character
            @param handler the error handler to use to report the invalid
            character
            @param t the tokenizer the token's being created for
            @param startPos the position in the source code of the text
            literal's opening quote
            @param the charPos the position of the invalid character in the
            text literal, relative to startPos
        */
        protected void
            reportInvalidChar(char invalidChar, ErrorHandler handler,
                              Tokenizer t, SourcePosition startPos,
                              int charPos)
        {
            String msg = _resources.
                getMessage(INVALID_TEXT_LITERAL_CHAR_MSG,
                            String.valueOf(invalidChar),
                            Integer.toString((int) invalidChar));
            reportInvalidChar(msg, handler, t, startPos, charPos);
        }

        /**
            Reports that this text literal contains an invalid (escaped
            or unescaped) character.

            @param msg the message to use to report the invalid character
            @param handler the error handler to use to report the tab
            @param t the tokenizer the token's being created for
            @param startPos the position in the source code of the text
            literal's opening quote
            @param the charPos the position of the invalid character in the
            text literal, relative to startPos
        */
        protected void reportInvalidChar(String msg, ErrorHandler handler,
                                         Tokenizer t, SourcePosition startPos,
                                         int charPos)
        {
            int charStartOffset = startPos.offset() + charPos;
            int charEndOffset = charStartOffset + 1;
            SourceLocation loc = locationFactory().
                create(startPos.lineNumber(),
                       charStartOffset, charEndOffset);
            TokenizingError error =
                new TokenizingError(NON_FATAL_ERROR_LEVEL, msg,
                                    t.sourceCode(), loc);
            handler.handle(error, t);
        }
    }

    /**
        Creates tokens that represent Atria names.
        <p>
        Note: there are no reserved words in Atria.
    */
    private class NameTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            StringBuffer buf = new StringBuffer();
            buf.append(iter.next());
            while (iter.hasNext())
            {
                char ch = iter.peek();
                if (AtriaInfo.isNameCharacter(ch))
                {
                    buf.append(iter.next());
                }
                else
                {
                    // ch is not a valid name character, so we've reached the
                    // name's end.
                    break;
                }
            }
            String name = buf.toString();

            Token tok = tokenManager().
                            createNameToken(startPos, name.length(), name);
            return createOneTokenResult(tok);
        }
    }

    /**
        Creates tokens representing one-line comments.
    */
    private class CommentTokenCreator
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            Assert.require(iter.peek() == AtriaInfo.COMMENT_START_CHAR);

            // The token's string value consists of everything after
            // the COMMENT_START_CHAR and before the next newline
            // (or up to the end of the source code).
            iter.discard();    // the COMMENT_START_CHAR
            StringBuffer buf = new StringBuffer();
            while (iter.hasNext() &&
                   AtriaInfo.isNewlineStart(iter.peek()) == false)
            {
                buf.append(iter.next());
            }

            Token tok = tokenManager().
                    createCommentToken(startPos, buf.length() + 1,
                                       buf.toString());
            return createOneTokenResult(tok);
        }
    }

    /**
        A class of TokenCreator that tokenizes Atria comments by discarding
        them: the characters of a comment are consumed but no token is
        created for them.
    */
    private static class DiscardCommentTokenCreator
        extends AbstractTokenCreator
    {
        /**
            @see TokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult
            create(CharacterIterator iter, SourcePosition startPos,
                   Tokenizer tokenizer, ErrorHandler handler)
        {
            Assert.require(iter.peek() == AtriaInfo.COMMENT_START_CHAR);

            iter.discard();  // the COMMENT_START_CHAR
            int commentLength = 1;  // including the COMMENT_START_CHAR
            while (iter.hasNext() &&
                   AtriaInfo.isNewlineStart(iter.peek()) == false)
            {
                iter.discard();
                commentLength += 1;
            }

            return createZeroTokenResult(startPos.lineNumber(),
                                         startPos.offset() + commentLength);
        }
    }
}
