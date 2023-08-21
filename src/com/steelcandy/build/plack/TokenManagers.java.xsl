<?xml version="1.0"?>
<!--
    Transforms a language description document into the Java base class
    for a token manager class for that language's tokens.

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
        select="$implementation/token-manager-base-class"/>

    <xsl:variable name="reserved-word-token-superclass-name"
        select="$class-implementation/reserved-word-token-superclass/@name"/>
    <xsl:variable name="reserved-word-operator-token-superclass-name"
        select="$class-implementation/reserved-word-operator-token-superclass/@name"/>
    <xsl:variable name="operator-token-superclass-name"
        select="$class-implementation/operator-token-superclass/@name"/>

    <xsl:variable name="simple-token-superclass-name"
        select="$class-implementation/simple-token-superclass/@name"/>


    <!-- Main templates -->

    <xsl:template match="language">
        <!-- Generate the token managers README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the token manager base class. -->
        <xsl:call-template name="token-manager-base-class"/>

        <!-- Generate skeleton for the default token manager class. -->
        <xsl:call-template name="default-token-manager-class-skeleton"/>

        <!-- Generate the singleton token manager class. -->
        <xsl:call-template name="token-manager-class"/>
    </xsl:template>


    <xsl:template name="readme-file">
        <xsl:text>%%%% file README.token-managers
</xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> Token Managers README

Copyright (C) James MacKay

write this !!!!

- token-managers.ignore
- token-managers.discard

- create Default</xsl:text>
        <xsl:value-of select="concat($token-manager-class-name, $src-ext)"/>
        <xsl:text> from
  Default</xsl:text>
        <xsl:value-of select="concat($token-manager-class-name, $src-ext)"/>
        <xsl:text>.skeleton
    - finish both versions of the create...Token() token creation methods
      for each of the custom-tokens
        - the default implementations may not be sufficient: in particular,
          extra arguments may have to be passed to the token class'
          constructor (or the call to the other version of the method)
    - finish the inner class for each custom-token
        - the default implementations may need to be modified and/or
          expanded considerably
        - may also want to change the superclass in some cases (the one
          used is the base class for simple tokens)
- once a skeleton file has been used to create the 'real' file, the
  skeleton file's name is usually added to the discard file
  (token-managers.discard)

- etc.</xsl:text>
    </xsl:template>

    <!-- Outputs the source code for the singleton token manager
         class. -->
    <xsl:template name="token-manager-class">
        <xsl:variable name="class-name"
            select="$token-manager-class-name"/>
        <xsl:variable name="superclass-name"
            select="concat('Default', $class-name)"/>
        <xsl:variable name="ctor-name"
            select="$token-manager-constructor-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
            <xsl:with-param name="module" select="$tokens-module"/>
        </xsl:call-template>
        <xsl:text>
/**
    The singleton </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> token manager class.
    </xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass-name"/>
        <xsl:text>
{
    // Constants

    /** The single instance of this class. */
    private static final </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        _instance = new </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static </xsl:text>
        <xsl:value-of select="concat($class-name, ' ', $ctor-name)"/>
        <xsl:text>()
    {
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }
}</xsl:text>
    </xsl:template>

    <!-- Outputs the skeleton of the source code for the default
         token manager class. -->
    <xsl:template name="default-token-manager-class-skeleton">
        <xsl:variable name="class"
            select="concat('Default', $token-manager-class-name)"/>
        <xsl:variable name="superclass" select="$class-name"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class"/>
            <xsl:with-param name="module" select="$tokens-module"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourcePosition;
import com.steelcandy.plack.common.tokens.Token;

/**
    The default token manager class.
</xsl:text>
        <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
{
    // Token creation methods
</xsl:text>
        <xsl:for-each select="$custom-tokens">
            <xsl:text>
    /**
        Creates and returns the </xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text> token
        representing the source fragment at the specified location
        in the source code.

        @param loc the location in the source code of the source
        fragment that the token is to represent
        @return the </xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text> token that represents
        the source fragment
    */
    public Token create</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(loc);
    }

    /**
        @see #create</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(SourceLocation)
    */
    public Token
        create</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length &gt;= 0);

        SourceLocation loc = createLocation(startPos, length);
        return create</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(loc);
    }
</xsl:text>
        </xsl:for-each>
        <xsl:text>

    // Inner token classes</xsl:text>
        <xsl:for-each select="$custom-tokens">
            <xsl:variable name="token-id">
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@name"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>

    /**
        The </xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text> token class.
    */
    protected static class </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token
        extends </xsl:text>
            <xsl:value-of select="$simple-token-superclass-name"/>
            <xsl:text>
    {
        // Constructors

        /**
            Constructs a </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token from the
            location in the source code of the source fragment that
            it is to represent.

            @param loc the location in the source code of the source
            fragment that the token is to represent
        */
        public </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(SourceLocation loc)
        {
            super(loc);
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return </xsl:text>
            <xsl:value-of select="$token-id"/>
            <xsl:text>;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Token(loc);
        }
    }</xsl:text>
        </xsl:for-each>
        <xsl:text>
}</xsl:text>
    </xsl:template>

    <!-- Outputs the source code for the token manager base class. -->
    <xsl:template name="token-manager-base-class">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
            <xsl:with-param name="module" select="$tokens-module"/>
        </xsl:call-template>
        <xsl:value-of select="$imports"/>
        <xsl:text>
import </xsl:text>
        <xsl:value-of select="concat($source-module, '.', $location-factory-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.containers.Containers;

import java.util.*;

/**
    An abstract base class for classes that create and manage the types
    of tokens used to represent fragments of </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> source code.
    &lt;p&gt;
    One of the purposes of this class is to hide the actual token classes:
    all instances of such classes should never need to be accessed as
    anything but Token instances. It also provides a convenient place to
    define both the token classes  - without polluting the global
    namespace - and token-related utility methods.
    &lt;p&gt;
    For every create...Token() method that take a SourceLocation argument
    there is a version of the same method that takes a SourcePosition and
    a length in its place. The latter version is more frequently used since
    the caller will usually have the SourcePosition and length as separate
    pieces of information, and so being able to pass them to
    create...Token() separately saves their having to create a
    SourceLocation object from them first.
    &lt;p&gt;
    Note that all of the factory methods require that the length be
    specified explicitly, even for those types of tokens for which the
    length would seem to be obvious: the reason for this requirement is
    that this manager shouldn't know anything about the source fragment it
    is creating tokens for.
    </xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
    <xsl:text>
    @see Token
*/
public abstract class </xsl:text>
    <xsl:value-of select="$class-name"/>
    <xsl:text>
    extends </xsl:text>
    <xsl:value-of select="$superclass-name"/>
    <xsl:text>
{
    // Token flag constants

    /** The first flag value usable as a token flag in this class. */
    private static final int FIRST_FLAG = 1 &lt;&lt; </xsl:text>
    <xsl:value-of select="$constant-superclass-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS;

    /** Token flags. */
</xsl:text>
<xsl:call-template name="token-flag-constants"/>
    <xsl:text>
    /**
        The number of token flags, &lt;em&gt;not&lt;/em&gt; including the ones
        defined in any superclasses.
    */
    private static final int NUMBER_OF_FLAGS = </xsl:text>
    <xsl:value-of select="$number-of-token-flags"/>
    <xsl:text>;

    /**
        The number of token flags, including any defined in superclasses.
    */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS =
        </xsl:text>
    <xsl:value-of select="$constant-superclass-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS + NUMBER_OF_FLAGS;

    /**
        The first token flag available for use by subclasses.
    */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_AVAILABLE_FLAG =
        1 &lt;&lt; </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS;

    /**
        The largest flag defined by this class, and thus, by the way
        that flags are defined, the largest flag defined by any of its
        superclasses as well.
    */
    private static final int LARGEST_FLAG =
        (</xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS &gt; 0) ? (1 &lt;&lt; (</xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_USED_FLAGS - 1)) : 0;


    // Token flag sets

</xsl:text>
    <xsl:call-template name="token-flag-set-constants"/>
    <xsl:text>

    // Token ID constants

    /** The first </xsl:text>
    <xsl:value-of select="$language-name"/>
    <xsl:text> token ID. */
    private static final int FIRST_ID = </xsl:text>
    <xsl:value-of select="$constant-superclass-prefix"/>
    <xsl:text>_FIRST_AVAILABLE_ID;

    /** The first </xsl:text>
    <xsl:value-of select="$language-name"/>
    <xsl:text> reserved word token ID. */
    public static final int
        </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID = FIRST_ID;

    /** Token IDs. */
</xsl:text>
    <xsl:call-template name="token-id-constants"/>
    <xsl:text>
    /**
        The number of token IDs, &lt;em&gt;not&lt;/em&gt; including any defined
        in superclasses.
    */
    private static final int NUMBER_OF_IDS = </xsl:text>
    <xsl:value-of select="$number-of-tokens"/>
    <xsl:text>;

    /**
        The number of token IDs, including any defined in superclasses.
    */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_IDS = FIRST_ID + NUMBER_OF_IDS;

    /**
        The first token ID available for use by subclasses.
    */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_AVAILABLE_ID = FIRST_ID + NUMBER_OF_IDS;


    // Reserved word-related constants

    /** The total number of reserved words defined in this class. */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS = </xsl:text>
    <xsl:value-of select="$number-of-reserved-words"/>
    <xsl:text>;

    /**
        The last reserved word token ID. All of the reserved word
        tokens are assumed to have consecutive token IDs.
    */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_LAST_RESERVED_WORD_ID =
        </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID + </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS - 1;


    // Operator precedence level constants

</xsl:text>
    <xsl:call-template name="operator-precedence-level-constants"/>
    <xsl:text>
    /** The number of precedence levels defined in this class. */
    public static final int </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_PRECEDENCE_LEVELS = </xsl:text>
    <xsl:value-of select="$number-of-operator-precedence-levels"/>
    <xsl:text>;

</xsl:text>

    <xsl:if test="count($all-operator-tokens[@method]) &gt; 0">
        <xsl:text>
    // Operator method name constants

    public static final String
</xsl:text>
        <xsl:for-each select="$all-operator-tokens[@method]">
            <xsl:if test="position() &gt; 1">
                <xsl:text>,
</xsl:text>
            </xsl:if>
            <xsl:text>        </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>_OPERATOR_METHOD_NAME = &quot;</xsl:text>
            <xsl:value-of select="@method"/>
            <xsl:text>&quot;</xsl:text>
        </xsl:for-each>
        <xsl:text>;

</xsl:text>
    </xsl:if>

    <xsl:text>
    // Other constants

    /**
        A token predicate that is satisfied by tokens that represent
        the end of a line. Its isSatisfied() method is reentrant, and
        so this instance can be shared by multiple threads.
    */
    private static final UnaryTokenPredicate _isEndOfLinePredicate =
        new HasFlagSetTokenPredicate(</xsl:text>
    <xsl:value-of select="$end-of-line-token-flag"/>
    <xsl:text>);


    // Private fields

    /**
        The reserved words: the reserved word corresponding to token ID
        tokenId is at index (tokenId - </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID).
    */
    private String[] _reservedWords =
        new String[</xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS];

    /**
        The prototype reserved word tokens. The prototype for the
        reserved word corresponding to token ID tokenId is at index
        (tokenId - </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID).
    */
    private Token[] _reservedWordPrototypeTokens =
        new Token[</xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS];

    /**
        Maps reserved words (i.e. Strings) to the prototype Token
        used to create tokens representing instances of that
        reserved word.
    */
    private Map _reservedWordToPrototypeTokenMap = Containers.
        createHashMap(</xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS);


    /**
        Maps token IDs to the description of the corresponding
        type of token.
    */
    private String[] _idToDescriptionMap = createIdToDescriptionMap();

    /** Maps token IDs to the names of the corresponding ID constants. */
    private String[] _idToConstantNameMap = createIdToConstantNameMap();
</xsl:text>
    <xsl:if test="$number-of-operators-with-methods &gt; 0">
        <xsl:text>
    /**
        Maps operator token IDs to the names of the corresponding methods,
        for those operators with corresponding methods. The keys are
        Integers and the values are Strings.
    */
    private Map _operatorIdToMethodNameMap =
                        createOperatorIdToMethodNameMap();

    /**
        The set of all of the method names that correspond to one or
        more operators. The values are Strings.
    */
    private Set _operatorMethodNames = createOperatorMethodNameSet();
</xsl:text>
    </xsl:if>
    <xsl:text>
    /**
        Maps token flags to the names of the corresponding token flag
        constants. The keys are Integers and the values are Strings.
    */
    private Map _flagToConstantNameMap = createFlagToConstantNameMap();


    // Constructors

    /**
        Constructs </xsl:text>
    <xsl:value-of select="concat($language-name-article, ' ', $class-name)"/>
    <xsl:text>.
    */
    protected </xsl:text>
    <xsl:value-of select="$class-name"/>
    <xsl:text>()
    {
        initializeReservedWordCollections(_reservedWords,
            _reservedWordPrototypeTokens,
            _reservedWordToPrototypeTokenMap);
    }


    // Public methods

    /**
        @see CommonTokenManager#isReservedWord(String)
    */
    public boolean isReservedWord(String str)
    {
        boolean result =
            _reservedWordToPrototypeTokenMap.containsKey(str);

        if (result == false)
        {
            result = super.isReservedWord(str);
        }

        return result;
    }


    /**
        @return a unary token predicate that is only satisfied by
        a token that indicates the end of a line
    */
    public UnaryTokenPredicate endOfLinePredicate()
    {
        return _isEndOfLinePredicate;
    }

    /**
        Indicates whether the specified token represents a line's
        indentation.

        @param tok the token to test
        @return true iff the token is an indent token
    */
    public boolean isIndentToken(Token tok)
    {
        Assert.require(tok != null);

        return (tok.id() == </xsl:text>
    <xsl:value-of select="$indent-token-id"/>
    <xsl:text>);
    }

    /**
        Returns the number of levels of indentation of a logical line's first
        physical line that the specified indent token represents.

        @param indentToken the indent token
        @return the number of levels of indentation of a logical line's first
        physical line that 'indentToken' represents
    */
    public int firstLineIndentLevels(Token indentToken)
    {
        Assert.require(isIndentToken(indentToken));

        // Note: it is assumed that the (first) int value of an indent token
        // specifies the number of levels of indentation of the first
        // physical line of the logical line that the indent token is part of.
        return indentToken.intValue();
    }

    /**
        Returns the number of levels of indentation of a logical line's last
        physical line that the specified indent token represents.

        @param indentToken the indent token
        @return the number of levels of indentation of a logical line's last
        physical line that 'indentToken' represents
    */
    public int lastLineIndentLevels(Token indentToken)
    {
        Assert.require(isIndentToken(indentToken));

        // Note: it is assumed that the second int value of an indent token
        // specifies the number of levels of indentation of the last physical
        // line of the logical line that the indent token is part of.
        return indentToken.secondIntValue();
    }

    /**
        Returns a list of the first tokens from the specified iterator,
        up to but not including the first indent token that represents
        fewer than the specified number of levels of indentation. If
        there is no such indent token in the iterator then all of the
        remaining tokens in the iterator will be returned.
        &lt;p&gt;
        Note: the number of levels of indentation of the first physical line
        in the indent token's logical line is used by this method.

        @param iter the iterator to get the tokens from
        @param indentLevels the number of indentation levels that an
        indent token from 'iter' must represent fewer levels of indentation
        that in order for it and all of the tokens after it to be left
        in 'iter'
        @return a list of the first tokens in 'iter' up to but not
        including the first indent token that represents fewer than
        'indentLevels' levels of indentation, or all of 'iter's tokens
        if it has no such indent token
        @see #tokensUpThroughIndent(TokenIterator, int)
    */
    public TokenList tokensUpToIndent(TokenIterator iter, int indentLevels)
    {
        Assert.require(iter != null);
        Assert.require(indentLevels &gt;= 0);

        return iter.nextTo(new UpToIndentPredicate(indentLevels));
    }

    /**
        Returns a list of the first tokens from the specified iterator, up to
        and including the first indent token that represents fewer than the
        specified number of levels of indentation. If there is no such indent
        token in the iterator then all of the remaining tokens in the
        iterator will be returned.
        &lt;p&gt;
        Note: the number of levels of indentation of the first physical line
        in the indent token's logical line is used by this method.

        @param iter the iterator to get the tokens from
        @param indentLevels the number of indentation levels that an
        indent token from 'iter' must represent fewer levels of indentation
        that in order for it and all of the tokens after it to be left
        in 'iter'
        @return a list of the first tokens in 'iter' up to but not
        including the first indent token that represents fewer than
        'indentLevels' levels of indentation, or all of 'iter's tokens
        if it has no such indent token
        @see #tokensUpToIndent(TokenIterator, int)
    */
    public TokenList
        tokensUpThroughIndent(TokenIterator iter, int indentLevels)
    {
        Assert.require(iter != null);
        Assert.require(indentLevels &gt;= 0);

        return iter.nextThrough(new UpToIndentPredicate(indentLevels));
    }


    // Token ID-related methods

    /**
        @param tokenId a token ID
        @return true iff 'tokenId' is the ID of a token that represents a
        reserved word
    */
    public boolean isReservedWordId(int tokenId)
    {
        int firstId = </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID;
        int numReservedWords = </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_NUMBER_OF_RESERVED_WORDS;

        boolean result = (tokenId &gt;= firstId) &amp;&amp;
                            (tokenId &lt; firstId + numReservedWords);

        return result;
    }

    /**
        @param tokenId the token ID of a reserved word token
        @return the reserved word that a token with Id 'tokenId' represents
    */
    public String idToReservedWord(int tokenId)
    {
        Assert.require(isReservedWordId(tokenId));

        int index = tokenId - </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID;

        String result = _reservedWords[index];

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see CommonTokenManager#idToDescription(int)
    */
    public String idToDescription(int tokenId)
    {
        String result;

        if (tokenId &lt; FIRST_ID)
        {
            result = super.idToDescription(tokenId);
        }
        else
        {
            result = _idToDescriptionMap[tokenId - FIRST_ID];
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see CommonTokenManager#idToConstantName(int)
    */
    public String idToConstantName(int tokenId)
    {
        String result;

        if (tokenId &lt; FIRST_ID)
        {
            result = super.idToConstantName(tokenId);
        }
        else
        {
            result = _idToConstantNameMap[tokenId - FIRST_ID];
        }

        Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    <xsl:if test="$number-of-operators-with-methods &gt; 0">
        <xsl:text>
    /**
        @see CommonTokenManager#operatorIdToMethodName(int)
    */
    public String operatorIdToMethodName(int tokenId)
    {
        String result;

        if (tokenId &lt; FIRST_ID)
        {
            result = super.operatorIdToMethodName(tokenId);
        }
        else
        {
            result = (String)
                _operatorIdToMethodNameMap.get(new Integer(tokenId));
        }

        // 'result' may be null
        return result;
    }

    /**
        @see CommonTokenManager#isOperatorMethodName(String)
    */
    public boolean isOperatorMethodName(String name)
    {
        Assert.require(name != null);

        return _operatorMethodNames.contains(name);
    }
</xsl:text>
    </xsl:if>
    <xsl:text>

    // Token flag-related methods

    /**
        @see CommonTokenManager#flagToConstantName(int)
    */
    public String flagToConstantName(int flag)
    {
        String result;
        if (flag &lt; FIRST_FLAG)
        {
            result = super.flagToConstantName(flag);
        }
        else
        {
            result = (String)
                _flagToConstantNameMap.get(new Integer(flag));
        }

        Assert.ensure(result != null);
        return result;
    }


    // Token creation methods

    /**
        @see CommonTokenManager#createReservedWordToken(SourcePosition, int, int)
    */
    public Token createReservedWordToken(SourcePosition startPos,
                                            int length, int tokenId)
    {
        Token result;
        if (tokenId &lt; FIRST_ID)
        {
            result = super.createReservedWordToken(startPos, length, tokenId);
        }
        else
        {
            Assert.check(tokenId &gt;= </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID);
            Assert.check(tokenId &lt;= </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_LAST_RESERVED_WORD_ID);

            int index = tokenId - </xsl:text>
    <xsl:value-of select="$constant-class-prefix"/>
    <xsl:text>_FIRST_RESERVED_WORD_ID;
            result = createToken(startPos, length,
                                 _reservedWordPrototypeTokens[index]);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see CommonTokenManager#createReservedWordToken(SourcePosition, int, String)
    */
    public Token createReservedWordToken(SourcePosition startPos,
                                         int length, String reservedWord)
    {
        Assert.require(isReservedWord(reservedWord));

        Token result;
        Object obj = _reservedWordToPrototypeTokenMap.get(reservedWord);
        if (obj != null)
        {
            result = createToken(startPos, length, (Token) obj);
        }
        else
        {
            result = super.createReservedWordToken(startPos, length,
                                                   reservedWord);
        }

        Assert.ensure(result != null);
        return result;
    }

</xsl:text>
    <xsl:apply-templates select="$tokens" mode="creation-methods"/>
    <xsl:text>

    // Token ID map creation methods

    /**
        Creates and returns an array that maps each token ID to a
        description of the type of token with that token ID.
        &lt;p&gt;
        Only the token IDs defined in this class are mapped: specifically,
        token IDs defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return an array that maps token IDs to the corresponding
        token type's description
    */
    private String[] createIdToDescriptionMap()
    {
        String[] result = new String[NUMBER_OF_IDS];

</xsl:text>
    <xsl:apply-templates select="$tokens" mode="id-to-description"/>
    <xsl:text>
        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns an array that maps each tokens ID to the
        name of the token ID constant whose value is that token ID.
        &lt;p&gt;
        Only the token IDs defined in this class are mapped: specifically,
        token IDs defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return an array that maps token IDs to the names of the
        corresponding token ID constants
    */
    private String[] createIdToConstantNameMap()
    {
        String[] result = new String[NUMBER_OF_IDS];

</xsl:text>
    <xsl:apply-templates select="$tokens" mode="id-to-constant-name"/>
    <xsl:text>
        Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    <xsl:if test="$number-of-operators-with-methods &gt; 0">
        <xsl:text>
    /**
        Creates and returns a map from the token IDs of tokens that
        represent operators with methods corresponding to them to the
        names of the corresponding methods.
        &lt;p&gt;
        Only the token IDs defined in this class are mapped: specifically,
        token IDs defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return a map from operator token IDs for operators with
        corresponding method to the names of those methods: the keys are
        Integers and the values are Strings
    */
    private Map createOperatorIdToMethodNameMap()
    {
        Map result = Containers.createHashMap(</xsl:text>
        <xsl:value-of select="$number-of-operators-with-methods"/>
        <xsl:text>);

</xsl:text>
        <xsl:for-each select="$all-operator-tokens[@method]">
            <xsl:text>        result.put(new Integer(</xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>), </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>_OPERATOR_METHOD_NAME);
</xsl:text>
        </xsl:for-each>
<xsl:text>
        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns a set of all of the method names that correspond
        to one or more operators. The values are Strings.

        @return a set of operator method names: the values are Strings
    */
    private Set createOperatorMethodNameSet()
    {
        Set result = Containers.createHashSet(</xsl:text>
        <xsl:value-of select="$number-of-operators-with-methods"/>
        <xsl:text>);

</xsl:text>
        <xsl:for-each select="$all-operator-tokens[@method]">
            <xsl:text>        result.add(</xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>_OPERATOR_METHOD_NAME);
</xsl:text>
        </xsl:for-each>
<xsl:text>
        Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    </xsl:if>
    <xsl:text>

    // Token flag map creation methods

    /**
        Creates and returns an array that maps each tokens flag to the
        name of the token flag constant whose value is that token flag.
        &lt;p&gt;
        Only the token flags defined in this class are mapped: specifically,
        token flags defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return an array that maps token flags to the names of the
        corresponding token flag constants
    */
    private Map createFlagToConstantNameMap()
    {
        // Since flags are not sequential ints we don't use an array.
        Map result = Containers.createHashMap(NUMBER_OF_FLAGS);

</xsl:text>
    <xsl:apply-templates select="$token-flags" mode="flag-to-constant-name"/>
    <xsl:text>
        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        @see CommonTokenManager#largestFlag
    */
    protected int largestFlag()
    {
        return LARGEST_FLAG;
    }

    /**
        @see CommonTokenManager#locationFactory
    */
    public SourceLocationFactory locationFactory()
    {
        return </xsl:text>
    <xsl:value-of select="concat($location-factory-class-name, '.', $location-factory-constructor-name, '()')"/>
    <xsl:text>;
    }


    // Private methods

    /**
        Initializes the specified reserved word-related collections.

        @param textMap the token ID to reserved word text map
        @param tokMap the token ID to prototype reserved word token map to
        initialize
        @param wordMap the reserved word to prototype reserved word token
        map to initialize: its keys are Strings and its values are Tokens
    */
    private void initializeReservedWordCollections(String textMap[],
                                                Token[] tokMap, Map wordMap)
    {
        // We need a fake location with which to construct the
        // prototype tokens: it will always be replaced in the clones
        // with the one passed to the tokens' cloneToken() methods.
        SourceLocation loc =
            locationFactory().create(1, 0, 0);

        int index;
        String text;
        Token tok;

</xsl:text>
    <xsl:apply-templates select="$all-reserved-word-tokens"
                         mode="reserved-word-collection-init"/>
    <xsl:text>
    }


    // Predicate inner classes

    /**
        Represents a token predicate that is only satisfied by indent tokens
        that represent fewer than the specified number of levels of
        indentation.
        &lt;p&gt;
        Note: the number of levels of indentation of the first physical line
        in the indent token's logical line is used by this predicate.
    */
    private class UpToIndentPredicate
        implements UnaryTokenPredicate
    {
        // Private fields

        /**
            The number of levels of indentation that an indent token must
            represent fewer levels of indentation than in order to be
            satisfied by this predicate.
        */
        private int _indentLevels;


        // Constructors

        /**
            Constructs an UpToIndentPredicate.

            @param indentLevels the number of levels of indentation that an
            indent token must represent fewer levels of
            indentation than in order to be satisfied by the predicate
        */
        public UpToIndentPredicate(int indentLevels)
        {
            Assert.require(indentLevels &gt;= 0);

            _indentLevels = indentLevels;
        }


        // Public methods

        /**
            @see UnaryTokenPredicate#isSatisfied(Token)
        */
        public boolean isSatisfied(Token tok)
        {
            return (tok != null) &amp;&amp; isIndentToken(tok) &amp;&amp;
                   (firstLineIndentLevels(tok) &lt; _indentLevels);
        }
    }


    // Inner token classes

</xsl:text>
    <xsl:apply-templates select="$tokens" mode="token-class"/>
    <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Constant definition templates -->

    <!-- Outputs the definitions of all of the token flag constants. -->
    <xsl:template name="token-flag-constants">
        <xsl:param name="flags" select="$token-flags"/>

        <xsl:if test="count($flags) &gt; 0">
            <xsl:text>    public static final int
</xsl:text>
        </xsl:if>
        <xsl:for-each select="$flags">
            <xsl:text>        </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text> = FIRST_FLAG &lt;&lt; </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:choose>
                <xsl:when test="position() = last()">
                    <xsl:text>;
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>,
</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the definitions of all of the token flag set
         constants. -->
    <xsl:template name="token-flag-set-constants">
        <xsl:param name="flag-sets" select="$token-flag-sets"/>

        <xsl:if test="count($flag-sets) &gt; 0">
            <xsl:text>    public static final int
</xsl:text>
        </xsl:if>
        <xsl:for-each select="$flag-sets">
            <xsl:text>        </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name"
                    select="concat($class-prefix, @name)"/>
            </xsl:call-template>
            <xsl:text> =
            </xsl:text>
            <xsl:call-template name="combine-flags">
                <xsl:with-param name="flags" select="flag"/>
            </xsl:call-template>
            <xsl:choose>
                <xsl:when test="position() = last()">
                    <xsl:text>;
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>,
</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the definitions of all of the token ID constants. -->
    <xsl:template name="token-id-constants">
        <xsl:param name="tokens" select="$tokens"/>

        <xsl:if test="count($tokens) &gt; 0">
            <xsl:text>    public static final int
</xsl:text>
        </xsl:if>
        <xsl:for-each select="$tokens">
            <xsl:text>        </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text> = FIRST_ID + </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:choose>
                <xsl:when test="position() = last()">
                    <xsl:text>;
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>,
</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the definitions of all of the operator precedence
         level constants. -->
    <xsl:template name="operator-precedence-level-constants">
        <xsl:param name="levels" select="$operator-precedence-levels"/>

        <xsl:if test="count($levels) &gt; 0">
            <xsl:text>    public static final int
</xsl:text>
        </xsl:if>
        <xsl:for-each select="$levels">
            <xsl:text>        </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text> = </xsl:text>
            <xsl:value-of select="@value"/>
            <xsl:choose>
                <xsl:when test="position() = last()">
                    <xsl:text>;
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>,
</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>


    <!-- Token creation method templates -->

    <!-- Note: there are no creation methods for custom tokens. -->
    <xsl:template match="*" mode="creation-methods">
        <xsl:text></xsl:text>
    </xsl:template>

    <xsl:template match="simple-token" mode="creation-methods">
        <xsl:variable name="method-name"
            select="concat('create', @name, 'Token')"/>

        <xsl:text>
    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> token at the specified location.

        @param loc the location in the source code of the fragment
        of source code that the token is to represent
        @return a token representing the source code fragment
    */
    public Token </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
    }

    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> token at the location determined by
        the specified starting position and length.

        @param startPos the starting position in the source code of
        the fragment of source code that the token is to represent
        @param length the length (in characters) of the source code
        fragment that the token is to represent
        @return a token representing the source code fragment
    */
    public Token
        </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length &gt;= 0);

        return </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(createLocation(startPos, length));
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="reserved-word" mode="creation-methods">
        <xsl:variable name="method-name"
            select="concat('create', @name, 'Token')"/>

        <xsl:text>
    /**
        Creates and returns a token representing the reserved word
        '</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>' at the specified location.

        @param loc the location in the source code of the reserved word
        that the token is to represent
        @return a token representing the reserved word
    */
    public Token </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
    }

    /**
        Creates and returns a token representing the reserved word
        </xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text> at the location determined by the specified starting
        position and length.

        @param startPos the starting position in the source code of the
        reserved word that the token is to represent
        @param length the length (in characters) of the reserved word
        that the token is to represent
        @return a token representing the reserved word
    */
    public Token
        </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length &gt;= 0);

        return </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(createLocation(startPos, length));
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="reserved-word-operator" mode="creation-methods">
        <xsl:variable name="method-name"
            select="concat('create', @name, 'Token')"/>

        <xsl:text>
    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> at the specified location.

        @param loc the location in the source code of the reserved word
        operator that the token is to represent
        @return a token representing the reserved word operator
    */
    public Token </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
    }

    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> at the location determined by the
        specified starting position and length.

        @param startPos the starting position in the source code of the
        reserved word operator that the token is to represent
        @param length the length (in characters) of the reserved word
        operator that the token is to represent
        @return a token representing the reserved word operator
    */
    public Token
        </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length &gt;= 0);

        return </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(createLocation(startPos, length));
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="operator" mode="creation-methods">
        <xsl:variable name="method-name"
            select="concat('create', @name, 'Token')"/>

        <xsl:text>
    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> at the specified location.

        @param loc the location in the source code of the operator
        that the token is to represent
        @return a token representing the operator
    */
    public Token </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourceLocation loc)
    {
        Assert.require(loc != null);

        return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
    }

    /**
        Creates and returns a token representing the
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> at the location determined by the
        specified starting position and length.

        @param startPos the starting position in the source code of the
        operator that the token is to represent
        @param length the length (in characters) of the operator that
        the token is to represent
        @return a token representing the operator
    */
    public Token
        </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length &gt;= 0);

        return </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>(createLocation(startPos, length));
    }
</xsl:text>
    </xsl:template>


    <!-- Templates that build maps involving token IDs. -->

    <xsl:template match="reserved-word" mode="id-to-description">
        <xsl:text>        result[</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> - FIRST_ID] = &quot;</xsl:text>
        <xsl:call-template name="reserved-word-description"/>
        <xsl:text>&quot;;
</xsl:text>
    </xsl:template>

    <!-- Outputs the description of the (non-operator) reserved word
         described by the specified token node. -->
    <xsl:template name="reserved-word-description">
        <xsl:param name="token" select="."/>

        <xsl:text>reserved word '</xsl:text>
        <xsl:value-of select="$token/@text"/>
        <xsl:text>'</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="id-to-description">
        <xsl:text>        result[</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> - FIRST_ID] = &quot;</xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text>&quot;;
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="id-to-constant-name">
        <xsl:text>        result[</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> - FIRST_ID] = &quot;</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text>&quot;;
</xsl:text>
    </xsl:template>


    <!-- Templates that build maps involving token flags. -->

    <xsl:template match="*" mode="flag-to-constant-name">
        <xsl:text>        result.put(new Integer(</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text>), &quot;</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>


    <!-- Reserved word collection initialization templates -->

    <xsl:template match="*" mode="reserved-word-collection-init">
        <xsl:text>        index = </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> - </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_FIRST_RESERVED_WORD_ID;
        text = &quot;</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>&quot;;
        tok = create</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
        textMap[index] = text;
        tokMap[index] = tok;
        wordMap.put(text, tok);

</xsl:text>
    </xsl:template>


    <!-- Token class templates -->

    <!-- Note: no token classes are output for custom tokens. -->
    <xsl:template match="custom-token" mode="token-class"/>

    <xsl:template match="simple-token" mode="token-class">
        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="flags">
            <xsl:call-template name="combine-flags">
                <xsl:with-param name="flags" select="flag"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        The class of token representing a single
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text>.
    */
    protected static class </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token
        extends </xsl:text>
        <xsl:value-of select="$simple-token-superclass-name"/>
        <xsl:text>
    {
        // Constants

        /** The flags that tokens of this class always have set. */
        private static final int _flags =
            </xsl:text>
        <xsl:value-of select="$flags"/>
        <xsl:text>;


        // Constructors

        /**
            Constructs a token from the location in the source code of
            the </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> that it is to represent.

            @param loc the location in the source code of the
            </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> that the token will represent
        */
        public </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(SourceLocation loc)
        {
            super(loc);
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>;
        }

        /**
            @see Token#isFlagSet
        */
        public boolean isFlagSet(int flag)
        {
            return (flag &amp; _flags) != 0;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
        }
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="reserved-word" mode="token-class">
        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="flags">
            <xsl:choose>
                <xsl:when test="count(flag) = 0">
                    <!-- If no flags are specified explicitly then use
                         the default reserved word flags. -->
                    <xsl:value-of select="$constant-class-prefix"/>
                    <xsl:text>_RESERVED_WORD_FLAGS</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <!-- Flags have been specified explicitly for this
                         reserved word: use ONLY those flags. -->
                    <xsl:call-template name="combine-flags">
                        <xsl:with-param name="flags" select="flag"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:text>
    /**
        The class of token representing an instance of the
        '</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>' reserved word.
    */
    protected static class </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token
        extends </xsl:text>
        <xsl:value-of select="$reserved-word-token-superclass-name"/>
        <xsl:text>
    {
        // Constants

        /** The flags that tokens of this class always have set. */
        private static final int _flags =
            </xsl:text>
        <xsl:value-of select="$flags"/>
        <xsl:text>;


        // Constructors

        /**
            Constructs a token from the location in the source code of
            the '</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>' reserved word that it is to represent.

            @param loc the location in the source code of the
            '</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>' reserved word that the token will represent
        */
        public </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(SourceLocation loc)
        {
            super(loc);
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>;
        }

        /**
            @see Token#isFlagSet
        */
        public boolean isFlagSet(int flag)
        {
            return (flag &amp; _flags) != 0;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
        }
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="operator" mode="token-class">
        <xsl:call-template name="operator-token-class">
            <xsl:with-param name="desc" select="'operator'"/>
            <xsl:with-param name="superclass"
                select="$operator-token-superclass-name"/>
            <xsl:with-param name="flag-set-suffix"
                select="'_OPERATOR_FLAGS'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="reserved-word-operator" mode="token-class">
        <xsl:call-template name="operator-token-class">
            <xsl:with-param name="desc" select="'reserved word operator'"/>
            <xsl:with-param name="superclass"
                select="$reserved-word-operator-token-superclass-name"/>
            <xsl:with-param name="flag-set-suffix"
                select="'_OPERATOR_RESERVED_WORD_FLAGS'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the source code for the class of token that represents
         an operator token (including reserved word operators). -->
    <xsl:template name="operator-token-class">
        <xsl:param name="node" select="."/>
        <xsl:param name="desc"/>
        <xsl:param name="superclass"/>
        <xsl:param name="flag-set-suffix"/>

        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        The class of token representing an instance of the
        '</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>' </xsl:text>
        <xsl:value-of select="$desc"/>
        <xsl:text>.
    */
    protected static class </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token
        extends </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
    {
        // Constants

        /** The operator data object shared by all instances. */
        private static final OperatorData _data = new OperatorData();
        static
        {
            _data.setArity(</xsl:text>
        <xsl:call-template name="arity-constant"/>
        <xsl:text>);
            _data.setAssociativity(</xsl:text>
        <xsl:call-template name="associativity-constant"/>
        <xsl:text>);
            _data.setFixity(</xsl:text>
        <xsl:call-template name="fixity-constant"/>
        <xsl:text>);
            _data.setPrecedence(</xsl:text>
        <xsl:call-template name="precedence-constant"/>
        <xsl:text>);

            _data.checkValid();
        }


        // Constructors

        /**
            Constructs a token from the location in the source code of
            the </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> that it is to represent.

            @param loc the location in the source code of the
            </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> that the token will represent
        */
        public </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(SourceLocation loc)
        {
            super(loc);
        }


        // Public methods

        /**
            @see Token#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>;
        }

        /**
            @see Token#objectValue
        */
        public Object objectValue()
        {
            return _data;
        }

        /**
            @see Token#isFlagSet
        */
        public boolean isFlagSet(int flag)
        {
            return (flag &amp; </xsl:text>
        <xsl:value-of select="concat($constant-class-prefix, $flag-set-suffix)"/>
        <xsl:text>) != 0;
        }

        /**
            @see Token#cloneToken(SourceLocation)
        */
        public Token cloneToken(SourceLocation loc)
        {
            return new </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(loc);
        }
    }
</xsl:text>
    </xsl:template>


    <!-- Outputs the name of the constant that represents the arity of
         the operator described by the specified node. -->
    <xsl:template name="arity-constant">
        <xsl:param name="node" select="."/>

        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="$node/@arity"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the name of the constant that represents the associativity
         of the operator described by the specified node. -->
    <xsl:template name="associativity-constant">
        <xsl:param name="node" select="."/>

        <xsl:variable name="part1">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$node/@associativity"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat($part1, '_ASSOCIATIVE')"/>
    </xsl:template>

    <!-- Outputs the name of the constant that represents the fixity of
         the operator described by the specified node. -->
    <xsl:template name="fixity-constant">
        <xsl:param name="node" select="."/>

        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="$node/@fixity"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the name of the constant that represents the precedence
         level of the operator described by the specified node. -->
    <xsl:template name="precedence-constant">
        <xsl:param name="node" select="."/>

        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="$node/@precedence"/>
        </xsl:call-template>
    </xsl:template>
</xsl:transform>
