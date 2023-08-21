<?xml version="1.0"?>
<!--
    $Id: SourceTokenizers.java.xsl,v 1.3 2002/08/02 15:08:05 jgm Exp $

    Transforms a language description document into the Java base class
    for a token creator source code tokenizer class that tokenizes that
    language.

    Author: James MacKay
    Last Updated: $Date: 2002/08/02 15:08:05 $

    Copyright (C) 2002-2016 by James MacKay.

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
-->

<!DOCTYPE xsl:transform [
<!ENTITY copy "&#169;">
<!ENTITY nbsp "&#160;">
]>

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="tokens.xsl"/>
    <xsl:import href="language-common.java.xsl"/>


    <!-- Configuration -->


    <!-- Global variables -->

    <xsl:variable name="class-implementation"
        select="$implementation/tokenizer-base-class"/>

    <xsl:variable name="default-token-creator-class-name"
        select="$top/token-creators/section/default-token-creator/@name"/>

    <xsl:variable name="creators" select="$top/token-creators/section/*"/>

    <xsl:variable name="creator-array-size">
        <xsl:choose>
            <xsl:when test="$top/token-creators/@map-size">
                <xsl:value-of select="$top/token-creators/@map-size"/>
            </xsl:when>
            <xsl:otherwise>127</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <!-- Generate the token creator source code tokenizer README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the token creator source code tokenizer base class. -->
        <xsl:call-template name="tokenizer-base-class"/>

        <!-- Generate skeleton for the token creator source code
             tokenizer class. -->
        <xsl:call-template name="tokenizer-class-skeleton"/>
    </xsl:template>


    <xsl:template name="readme-file"><xsl:text/>
%%%% file README.source-tokenizers
<xsl:value-of select="$language-name"/> Source Tokenizers README

Copyright (C) James MacKay

write this !!!!

- source-tokenizers.ignore
- source-tokenizers.discard

- create <xsl:value-of select="concat($source-tokenizer-class-name, $src-ext)"/> from
  <xsl:value-of select="concat($source-tokenizer-class-name, $src-ext)"/>.skeleton
    - the generated TokenCreator creation methods should not usually
      need to be changed, but the generated (incomplete) TokenCreator
      inner classes will always need to be modified, usually radically
      (in some cases including changing the superclass)
    - the default token creator class is often a common class rather
      than an inner class in the source tokenizer: in such cases the
      skeleton for its inner class should be removed
- once a skeleton file has been used to create the 'real' file, the
  skeleton file's name is usually added to the discard file
  (source-tokenizers.discard)

- etc.<xsl:text/>
    </xsl:template>

    <!-- Outputs the skeleton of the source code for the token creator
         source code tokenizer class. -->
    <xsl:template name="tokenizer-class-skeleton">
        <xsl:variable name="class"
            select="$source-tokenizer-class-name"/>
        <xsl:variable name="superclass" select="$class-name"/>
<xsl:call-template name="source-skeleton-prologue">
    <xsl:with-param name="class-name" select="$class"/>
    <xsl:with-param name="module" select="$tokens-module"/>
</xsl:call-template>
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;
import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    A tokenizer that tokenizes a piece of source code written in the
    <xsl:value-of select="$language-name"/> programming language.
    &lt;p&gt;
    The tokens created by this tokenizer are usually used as input to
    a sequence of other tokenizers that perform other operations on
    the tokens.
    <xsl:call-template name="common-skeleton-class-comment-part"/>
*/
public class <xsl:value-of select="$class"/>
    extends <xsl:value-of select="$superclass"/>
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        ???;

    /** Resource identifiers. */
    private static final String
        ???;


    // Constructors

    /**
        Constructs a tokenizer from the error handler that it should use.

        @param handler the error handler that the tokenizer should use
        to handle any errors
    */
    public <xsl:value-of select="$class"/>(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public <xsl:value-of select="$class"/>(Resources subtokenizerResources,
        String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @see AbstractTokenizer#charToReturnOnReadError
    */
    protected char charToReturnOnReadError()
    {
        return ???;
    }


    // TokenCreator creation methods
    <xsl:apply-templates select="$creators" mode="skeleton-creation-method">
        <xsl:with-param name="superclass" select="$superclass"/>
    </xsl:apply-templates>

    // Inner TokenCreator classes
    <xsl:apply-templates select="$creators" mode="skeleton-creator-class"/>}<xsl:text/>
    </xsl:template>

    <xsl:template match="custom-token-creator|default-token-creator"
        mode="skeleton-creation-method">
        <xsl:param name="superclass"/>

        <xsl:variable name="creator-name"
            select="concat(@name, 'TokenCreator')"/>
    /**
        @see <xsl:value-of select="$superclass"/>#create<xsl:value-of select="$creator-name"/>
    */
    protected TokenCreator create<xsl:value-of select="$creator-name"/>()
    {
        return new <xsl:value-of select="$creator-name"/>();
    }<xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="skeleton-creation-method"/>

    <xsl:template match="custom-token-creator|default-token-creator"
        mode="skeleton-creator-class">

        <xsl:variable name="class-name"
            select="concat(@name, 'TokenCreator')"/>
    /**
        The <xsl:value-of select="@name"/> token creator class.
    */
    private class <xsl:value-of select="$class-name"/>
        extends AbstractTokenCreator
    {
        // Public methods

        /**
            @see AbstractTokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult create(CharacterIterator iter,
            SourcePosition startPos, Tokenizer t, ErrorHandler handler)
        {
            ???
        }
    }<xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="skeleton-creator-class"/>


    <!-- Outputs the source code for the token creator source code
         tokenizer base class. -->
    <xsl:template name="tokenizer-base-class">
<xsl:call-template name="source-prologue">
    <xsl:with-param name="class-name" select="$class-name"/>
    <xsl:with-param name="module" select="$tokens-module"/>
</xsl:call-template>
<xsl:value-of select="$imports"/>
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.TokenizingError;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.chars.UnaryCharacterPredicate;
import com.steelcandy.common.chars.DefaultUnaryCharacterPredicate;

/**
    The base class for a tokenizer that tokenizes a piece of source code
    written in the <xsl:value-of select="$language-name"/> programming language.
    &lt;p&gt;
    The tokens created by subclasses of this class are usually used as
    input to a sequence of other tokenizers that perform other operations
    on the tokens.
    &lt;p&gt;
    In addition to the abstract methods declared in this class, subclasses
    have to implement the following methods:
    &lt;ul&gt;
        &lt;li&gt;AbstractSourceCodeTokenizer.charToReturnOnReadError()
    &lt;/ul&gt;
    <xsl:call-template name="common-class-comment-part"/>
*/
public abstract class <xsl:value-of select="$class-name"/>
    extends TokenCreatorSourceCodeTokenizer
{
    // Constants

    /** The token manager singleton. */
    public static final <xsl:value-of select="$token-manager-class-name"/>
        TOKEN_MANAGER = <xsl:value-of
            select="concat($token-manager-class-name, '.',
                           $token-manager-constructor-name, '();')"/>

    /** The size of the TokenCreators array. */
    private static final int CREATORS_ARRAY_SIZE = <xsl:value-of select="$creator-array-size"/>;


    // Constructors

    /**
        Constructs a <xsl:value-of select="$class-name"/>.

        @param handler the error handler the tokenizer is
        to use to handle any errors
    */
    public <xsl:value-of select="$class-name"/>(ErrorHandler handler)
    {
        super(handler);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public <xsl:value-of select="$class-name"/>(
        Resources subtokenizerResources,
        String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }


    // Protected methods

    /**
        @return an instance of the class that creates and manages
        the language's tokens
    */
    protected <xsl:value-of select="$token-manager-class-name"/> tokenManager()
    {
        return TOKEN_MANAGER;
    }

    /**
        @see TokenCreatorSourceCodeTokenizer#locationFactory
    */
    protected SourceLocationFactory locationFactory()
    {
        return <xsl:value-of select="concat($location-factory-class-name, '.', $location-factory-constructor-name, '()')"/>;
    }


    /**
        @see TokenCreatorSourceCodeTokenizer#creatorArraySize
    */
    protected int creatorArraySize()
    {
        return CREATORS_ARRAY_SIZE;
    }

    /**
        @see TokenCreatorSourceCodeTokenizer#createDefaultTokenCreator
    */
    protected TokenCreator createDefaultTokenCreator()
    {
        return create<xsl:value-of select="$default-token-creator-class-name"/>TokenCreator();
    }

    /**
        @see TokenCreatorSourceCodeTokenizer#setTokenCreators(TokenCreator[])
    */
    protected void setTokenCreators(TokenCreator[] creators)
    {
        TokenCreator creator;
        String startChars;
        int numStartChars;

<xsl:apply-templates select="$creators" mode="set-creators"/>
    }


    // TokenCreator creation methods
<xsl:apply-templates select="$creators" mode="creation-methods"/>

    // Inner TokenCreator classes
<xsl:apply-templates select="$creators" mode="creator-class"/>}<xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Set token creators templates -->

    <!-- Don't put the default token creator in the array. -->
    <xsl:template match="default-token-creator" mode="set-creators"/>

    <xsl:template match="*" mode="set-creators">
        <xsl:text>        creator = create</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>TokenCreator();
        startChars = &quot;</xsl:text>
        <xsl:value-of select="@start-chars"/>
        <xsl:text>&quot;;
        numStartChars = startChars.length();
        for (int i = 0; i &lt; numStartChars; i++)
        {
            char ch = startChars.charAt(i);
            creators[(int) ch] = creator;
        }</xsl:text>
        <xsl:if test="position()!=last()">
            <xsl:text>

</xsl:text>
        </xsl:if>
    </xsl:template>


    <!-- Creator creation method templates -->

    <xsl:template match="custom-token-creator|default-token-creator"
                                            mode="creation-methods">
        <xsl:variable name="creator-name"
            select="concat(@name, 'TokenCreator()')"/>
    /**
        @return a new <xsl:value-of select="$creator-name"/>
    */
    protected abstract TokenCreator create<xsl:value-of select="$creator-name"/>;<xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="creation-methods">
        <xsl:variable name="creator-name"
            select="concat(@name, 'TokenCreator()')"/>
    /**
        @return a new <xsl:value-of select="$creator-name"/>
    */
    protected TokenCreator create<xsl:value-of select="$creator-name"/>
    {
        return new <xsl:value-of select="$creator-name"/>;
    }<xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Token creator class definition templates -->

    <!-- Don't output a class definition for custom and default
         creators. -->
    <xsl:template match="*" mode="creator-class"/>

    <xsl:template match="single-token-creator" mode="creator-class">
    /**
        A token creator that creates <xsl:value-of select="@name"/> tokens.
    */
    private class <xsl:value-of select="@name"/>TokenCreator
        extends AbstractTokenCreator
    {
        /**
            @see TokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult
            create(CharacterIterator iter, SourcePosition startPos,
                   Tokenizer tokenizer, ErrorHandler handler)
        {
            iter.discard();
            return createOneTokenResult(tokenManager().
                create<xsl:value-of select="@name"/>Token(startPos, 1));
        }
    }<xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="multiple-token-creator" mode="creator-class">
    /**
        A token creator that can create <xsl:value-of select="@name"/> tokens,
        as well as other types of tokens.
    */
    private class <xsl:value-of select="@name"/>TokenCreator
        extends AbstractTokenCreator
    {
        /**
            @see TokenCreator#create(CharacterIterator, SourcePosition, Tokenizer, ErrorHandler)
        */
        public TokenCreatorResult
            create(CharacterIterator iter, SourcePosition startPos,
                   Tokenizer tokenizer, ErrorHandler handler)
        {
            Token tok;
            iter.discard();    // the starting character
            if (iter.hasNext())
            {
                char ch = iter.peek();
<xsl:call-template name="multiple-creator-choices">
    <xsl:with-param name="choices" select="next-char"/>
</xsl:call-template>                else
                {
                    tok = tokenManager().
                        create<xsl:value-of select="@name"/>Token(startPos, 1);
                }
            }
            else
            {
                // There's no character after the first, so we have the
                // default type of token.
                tok = tokenManager().
                    create<xsl:value-of select="@name"/>Token(startPos, 1);
            }

            return createOneTokenResult(tok);
        }
    }<xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs the 'if' and 'else if' clauses that are part of the
         implementation of multiple token creators. -->
    <xsl:template name="multiple-creator-choices">
        <xsl:param name="choices"/>

        <xsl:for-each select="$choices">
            <xsl:text>                </xsl:text>
            <xsl:if test="position()!=1">
                <xsl:text>else </xsl:text>
            </xsl:if>
            <xsl:text>if (ch == '</xsl:text>
            <xsl:value-of select="@value"/>
            <xsl:text>')
                {
                    iter.discard();
                    tok = tokenManager().
                        create</xsl:text>
            <xsl:value-of select="@token-name"/>
            <xsl:text>Token(startPos, 2);
                }
</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:transform>
