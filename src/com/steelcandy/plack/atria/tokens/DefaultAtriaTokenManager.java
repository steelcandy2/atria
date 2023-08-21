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

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.tokens.*;

/**
    The default token manager class.

    @author James MacKay
*/
public class DefaultAtriaTokenManager
    extends AtriaTokenManagerBase
{
    // Token creation methods

    /**
        Creates and returns the newline token representing the source
        fragment at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @return the newline token that represents the source fragment
    */
    public Token createNewlineToken(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new NewlineToken(loc);
    }

    /**
        @see #createNewlineToken(SourceLocation)
    */
    public Token
        createNewlineToken(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);

        SourceLocation loc = createLocation(startPos, length);
        return createNewlineToken(loc);
    }

    /**
        Creates and returns the indentation token representing the source
        fragment at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @param leadingWhitespace the whitespace at the beginning of a
        line that the token is to represent: it may be an empty string
        @return the indentation token that represents the source fragment
    */
    public Token createIndentToken(SourceLocation loc,
                                   String leadingWhitespace)
    {
        Assert.require(loc != null);
        Assert.require(leadingWhitespace != null);

        return new IndentToken(loc, leadingWhitespace);
    }

    /**
        @see #createIndentToken(SourceLocation, String)
    */
    public Token
        createIndentToken(SourcePosition startPos, int length,
                          String leadingWhitespace)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);
        Assert.require(leadingWhitespace != null);

        SourceLocation loc = createLocation(startPos, length);
        return createIndentToken(loc, leadingWhitespace);
    }

    /**
        Creates and returns the comment token representing the source
        fragment at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @param contents the comment's contents: everything after the
        comment start character
        @return the comment token that represents the source fragment
    */
    public Token createCommentToken(SourceLocation loc, String contents)
    {
        Assert.require(loc != null);
        Assert.require(contents != null);

        return new CommentToken(loc, contents);
    }

    /**
        @see #createCommentToken(SourceLocation, String)
    */
    public Token
        createCommentToken(SourcePosition startPos, int length,
                           String contents)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);
        Assert.require(contents != null);

        SourceLocation loc = createLocation(startPos, length);
        return createCommentToken(loc, contents);
    }

    /**
        Creates and returns the explicit line continuer ('\') token
        representing the source fragment at the specified location
        in the source code.

        @param loc the location in the source code of the source
        fragment that the token is to represent
        @return the explicit line continuer ('\') token that represents
        the source fragment
    */
    public Token createExplicitLineContinuerToken(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new ExplicitLineContinuerToken(loc);
    }

    /**
        @see #createExplicitLineContinuerToken(SourceLocation)
    */
    public Token
        createExplicitLineContinuerToken(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);

        SourceLocation loc = createLocation(startPos, length);
        return createExplicitLineContinuerToken(loc);
    }

    /**
        Creates and returns the name token representing the source fragment
        at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @param name the name that the token will represent
        @return the name token that represents the source fragment
    */
    public Token createNameToken(SourceLocation loc, String name)
    {
        Assert.require(loc != null);
        Assert.require(name != null);

        return new NameToken(loc, name);
    }

    /**
        @see #createNameToken(SourceLocation, String)
    */
    public Token
        createNameToken(SourcePosition startPos, int length, String name)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);
        Assert.require(name != null);

        SourceLocation loc = createLocation(startPos, length);
        return createNameToken(loc, name);
    }

    /**
        Creates and returns the literal text token representing the source
        fragment at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @param text the contents of the literal text: everything between the
        quotation marks
        @return the literal text token that represents the source fragment
    */
    public Token createTextToken(SourceLocation loc, String text)
    {
        Assert.require(loc != null);
        Assert.require(text != null);

        return new TextToken(loc, text);
    }

    /**
        @see #createTextToken(SourceLocation, String)
    */
    public Token
        createTextToken(SourcePosition startPos, int length, String text)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);
        Assert.require(text != null);

        SourceLocation loc = createLocation(startPos, length);
        return createTextToken(loc, text);
    }

    /**
        Creates and returns the invalid character token representing the
        source fragment at the specified location in the source code.

        @param loc the location in the source code of the source fragment
        that the token is to represent
        @param invalidChar the invalid character that the token is to
        represent
        @return the invalid character token that represents the source
        fragment
    */
    public Token createInvalidCharacterToken(SourceLocation loc,
                                             char invalidChar)
    {
        Assert.require(loc != null);

        return new InvalidCharacterToken(loc, invalidChar);
    }

    /**
        @see #createInvalidCharacterToken(SourceLocation, char)
    */
    public Token
        createInvalidCharacterToken(SourcePosition startPos, int length,
                                    char invalidChar)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);

        SourceLocation loc = createLocation(startPos, length);
        return createInvalidCharacterToken(loc, invalidChar);
    }


    // Inner token classes

    /**
        The newline token class.
    */
    protected static class NewlineToken
        extends AtriaAbstractToken
    {
        // Private fields

        /** This token's flags. */
        private int _flags = IS_END_OF_LINE;


        // Constructors

        /**
            Constructs a NewlineToken from the location in the source code of
            the source fragment that it is to represent.

            @param loc the location in the source code of the source fragment
            that the token is to represent
        */
        public NewlineToken(SourceLocation loc)
        {
            super(loc);
        }

        /**
            Constructs a NewlineToken from the location in the source code of
            the source fragment that it is to represent, as well as the
            token's (initial) flags.
            <p>
            This constructor is only intended to be used by our cloneToken()
            method.

            @param loc the location in the source code of the source fragment
            that the token is to represent
            @param flags the token's (initial) flags
        */
        protected NewlineToken(SourceLocation loc, int flags)
        {
            super(loc);

            _flags = flags;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return NEWLINE;
        }


        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new NewlineToken(loc, _flags);
        }

        /**
            @see Token#positionAfter
        */
        public SourcePosition positionAfter()
        {
            return new SourcePosition(location().endLineNumber() + 1, 0);
        }


        /**
            @see Token#isFlagSet
        */
        public boolean isFlagSet(int flag)
        {
            return (flag & _flags) != 0;
        }

        /**
            @see Token#canSetFlag
        */
        public boolean canSetFlag(int flag)
        {
            return (flag == IS_END_OF_LINE);
        }

        /**
            @see Token#setFlag
        */
        public void setFlag(int flag)
        {
            Assert.require(canSetFlag(flag));

            _flags = setFlags(flag, _flags);
        }

        /**
            @see Token#canClearFlag
        */
        public boolean canClearFlag(int flag)
        {
            return (flag == IS_END_OF_LINE);
        }

        /**
            @see Token#clearFlag
        */
        public void clearFlag(int flag)
        {
            Assert.require(canClearFlag(flag));

            _flags = clearFlags(flag, _flags);
        }
    }

    /**
        The class of token representing how much a line is indented: that is,
        it represents all of the whitespace (if any) at the start of a line.
        <p>
        A token of this type has the following values associated with
        it:
        <ul>
            <li><em>first int value</em>: the number of levels that the
                first physical line of the logical line that the token is a
                part of is indented, or -1 if that value hasn't been
                calculated for the token yet
            <li><em>second int value</em>: the number of levels that the
                last physical line of the logical line that the token is a
                part of is indented, or -1 if that value hasn't been
                calculated for the token yet
            <li><em>string value</em>: the string of whitespace used
                to indent the first physical line of the token's logical
                line (i.e. a string of spaces)
        </ul>
        The int values aren't set when the token is created since for some
        (but not all) lines indentation that isn't a multiple of the size of
        a level of indentation must be reported and rounded to the nearest
        number of levels of indentation.
    */
    protected static class IndentToken
        extends AtriaNonCodeToken
    {
        // Private fields

        /**
            The number of levels that the first physical line of this token's
            logical line is indented, or -1 if that hasn't been determined
            yet.
        */
        private int _firstLineIndentLevels;

        /**
            The number of levels that the last physical line of this token's
            logical line is indented, or -1 if that hasn't been determined
            yet.
        */
        private int _lastLineIndentLevels;

        /**
            The leading whitespace at the beginning of a line that this token
            represents.
        */
        private String _whitespace;


        // Constructors

        /**
            Constructs an IndentToken from the leading whitespace it is
            to represent and its location in the source code.

            @param loc the location in the source code of the leading
            whitespace that the token is to represent
            @param leadingWhitespace the whitespace that the token is
            to represent
        */
        public IndentToken(SourceLocation loc, String leadingWhitespace)
        {
            this(loc, leadingWhitespace, -1, -1);
        }

        /**
            Constructs an IndentToken from the leading whitespace it is
            to represent and its location in the source code, as well as
            the absolute and relative (to the indentation of the previous
            line) number of levels of indentation that the leading
            whitespace represents.
            <p>
            This constructor is only provided for use by subclasses (if
            any) and the cloneToken() method.

            @param loc the location in the source code of the leading
            whitespace that the token is to represent
            @param leadingWhitespace the whitespace that the token is
            to represent
            @param firstLineIndentLevels the absolute number of levels that
            the first physical line of the logical line containing the indent
            token is indented
            @param lastLineIndentLevels the absolute number of levels that
            the last physical line of the logical line containing the indent
            token is indented
            @see #cloneToken(SourceLocation)
        */
        protected IndentToken(SourceLocation loc, String leadingWhitespace,
                        int firstLineIndentLevels, int lastLineIndentLevels)
        {
            super(loc);
            Assert.require(leadingWhitespace != null);
            Assert.require(firstLineIndentLevels >= -1);
            Assert.require(lastLineIndentLevels >= -1);

            _whitespace = leadingWhitespace;
            _firstLineIndentLevels = firstLineIndentLevels;
            _lastLineIndentLevels = lastLineIndentLevels;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return INDENT;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new IndentToken(loc, _whitespace, _firstLineIndentLevels,
                                   _lastLineIndentLevels);
        }


        /**
            @see Token#hasIntValue
        */
        public boolean hasIntValue()
        {
            return true;
        }

        /**
           @see Token#intValue
        */
        public int intValue()
        {
            return _firstLineIndentLevels;
        }

        /**
            @see Token#canSetIntValue
        */
        public boolean canSetIntValue()
        {
            return true;
        }

        /**
            @see Token#setIntValue(int)
        */
        public void setIntValue(int newValue)
        {
            _firstLineIndentLevels = newValue;
        }


        /**
            @see Token#hasSecondIntValue
        */
        public boolean hasSecondIntValue()
        {
            return true;
        }

        /**
            @see Token#secondIntValue
        */
        public int secondIntValue()
        {
            return _lastLineIndentLevels;
        }

        /**
            @see Token#canSetSecondIntValue
        */
        public boolean canSetSecondIntValue()
        {
            return true;
        }

        /**
            @see Token#setSecondIntValue(int)
        */
        public void setSecondIntValue(int newValue)
        {
            _lastLineIndentLevels = newValue;
        }


        /**
            @see Token#hasStringValue
        */
        public boolean hasStringValue()
        {
            return true;
        }

        /**
            @see Token#stringValue
        */
        public String stringValue()
        {
            return _whitespace;
        }
    }

    /**
        The class of token representing comments. An instance's string value
        (which it always has) is the contents of the comment, not including
        the comment start character.
    */
    protected static class CommentToken
        extends AtriaNonCodeToken
    {
        // Private fields

        /**
            The contents of the comment, excluding the comment start
            character.
        */
        private String _contents;


        // Constructors

        /**
            Constructs a CommentToken from its contents (excluding the
            comment start character) and its location in the source code.

            @param loc the location in the source code of the source
            fragment that the token is to represent
            @param contents the contents of the comment
        */
        public CommentToken(SourceLocation loc, String contents)
        {
            super(loc);
            Assert.require(contents != null);

            _contents = contents;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return COMMENT;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new CommentToken(loc, _contents);
        }

        /**
            @see Token#hasStringValue
        */
        public boolean hasStringValue()
        {
            return true;
        }

        /**
            @see Token#stringValue
        */
        public String stringValue()
        {
            return _contents;
        }
    }

    /**
        The class of token representing a single explicit line continuer
        ('\').
    */
    protected static class ExplicitLineContinuerToken
        extends AtriaAbstractToken
    {
        // Private fields

        /** The flags that this token has set. */
        private int _flags = IS_LINE_CONTINUER;


        // Constructors

        /**
            Constructs a token from the location in the source code of
            the explicit line continuer ('\') that it is to represent.

            @param loc the location in the source code of the
            explicit line continuer ('\') that the token will represent
        */
        public ExplicitLineContinuerToken(SourceLocation loc)
        {
            super(loc);
        }

        /**
            Constructs a token from the location in the source code of
            the explicit line continuer ('\') that it is to represent, as
            well as the flags that the token is to have set initially.
            <p>
            This constructor is only meant to be used by cloneToken().

            @param loc the location in the source code of the
            explicit line continuer ('\') that the token will represent
            @param flags the token's (initial) flags
        */
        protected ExplicitLineContinuerToken(SourceLocation loc, int flags)
        {
            super(loc);
            _flags = flags;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return EXPLICIT_LINE_CONTINUER;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new ExplicitLineContinuerToken(loc, _flags);
        }

        /**
            @see Token#isFlagSet
        */
        public boolean isFlagSet(int flag)
        {
            return (flag & _flags) != 0;
        }

        /**
            The IS_VALIDATED flag can be set on EXPLICIT_LINE_CONTINUER
            tokens.

            @see AbstractToken#canSetFlag(int)
        */
        public boolean canSetFlag(int flag)
        {
            return (flag == IS_VALIDATED);
        }

        /**
            @see AbstractToken#setFlag(int flag)
        */
        public void setFlag(int flag)
        {
            Assert.require(canSetFlag(flag));

            _flags = setFlags(flag, _flags);
        }
    }

    /**
        The class of token representing names. An instance's string value
        (which it always has) is the name itself.
    */
    protected static class NameToken
        extends AtriaAbstractToken
    {
        // Private fields

        /** The name that this token represents. */
        private String _name;


        // Constructors

        /**
            Constructs a NameToken from the name that it represents and its
            location in the source code.

            @param loc the location in the source code of the source fragment
            that the token is to represent
            @param name the name that the token is to represent
        */
        public NameToken(SourceLocation loc, String name)
        {
            super(loc);
            Assert.require(name != null);

            // TODO: we intern() the name to avoid multiple copies of the
            // same name (with the goal of reducing overall memory use). Is
            // this going to accomplish that goal???!!!???
            _name = name.intern();
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return NAME;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new NameToken(loc, _name);
        }

        /**
            @see Token#hasStringValue
        */
        public boolean hasStringValue()
        {
            return true;
        }

        /**
            @see Token#stringValue
        */
        public String stringValue()
        {
            return _name;
        }
    }

    /**
        The literal text token class. An instance's string value (which it
        always has) is the text's contents (i.e. the string of characters
        between the quotation marks).
    */
    protected static class TextToken
        extends AtriaAbstractToken
    {
        // Private fields

        /** The literal text represented by this token. */
        private String _text;

        // Constructors

        /**
            Constructs a TextToken from its literal text and the location in
            the source code of the source fragment that it is to represent.

            @param loc the location in the source code of the source fragment
            that the token is to represent
            @param text the literal text
        */
        public TextToken(SourceLocation loc, String text)
        {
            super(loc);
            Assert.require(text != null);

            _text = text;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return TEXT;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new TextToken(loc, _text);
        }

        /**
            @see Token#hasStringValue
        */
        public boolean hasStringValue()
        {
            return true;
        }

        /**
            @see Token#stringValue
        */
        public String stringValue()
        {
            return _text;
        }
    }

    /**
        The class of token representing invalid characters. An instance's
        string value (which it always has) is the invalid character.
    */
    protected static class InvalidCharacterToken
        extends AtriaAbstractToken
    {
        // Private fields

        /** The invalid character represented by this token. */
        private char _invalidChar;


        // Constructors

        /**
            Constructs an InvalidCharacterToken from the invalid character
            and its location in the source code.

            @param loc the location in the source code of the source fragment
            that the token is to represent
            @param ch the invalid character
        */
        public InvalidCharacterToken(SourceLocation loc, char ch)
        {
            super(loc);

            _invalidChar = ch;
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return INVALID_CHARACTER;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new InvalidCharacterToken(loc, _invalidChar);
        }

        /**
            @see Token#hasStringValue
        */
        public boolean hasStringValue()
        {
            return true;
        }

        /**
            @see Token#stringValue
        */
        public String stringValue()
        {
            return String.valueOf(_invalidChar);
        }
    }
}
