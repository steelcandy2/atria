<?xml version="1.0"?>
<!--
    $Id: Parsers.java.xsl,v 1.88 2016/01/14 14:49:30 jgm Exp $

    Transforms a language description document into a file containing the
    source code for all of the automatically-generated parts of the parsers
    used to parse the language. (The file will have to be split into
    separate source files by some other utility.)

    Author: James MacKay
    Last Updated: $Date: 2016/01/14 14:49:30 $

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

<!--
***************************************************************************
TODO: at least for now, do NOT allow a subconstruct to be optional if it is
      preceded only by other optional parts. (Put this in the validation.)

TODO: generate skeletons for the default parsers of those constructs for
      which one could not be automatically generated (?)
        - do the same for parser maps and parsing helper maps (?)
        - done for choice and line choice constructs, but not multiline
          constructs
***************************************************************************
-->

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="constructs.xsl"/>
    <xsl:import href="map-string.xsl"/>
    <xsl:import href="language-common.java.xsl"/>


    <!-- ################# -->
    <!-- # Configuration # -->
    <!-- ################# -->


    <!-- #################### -->
    <!-- # Global variables # -->
    <!-- #################### -->

    <xsl:variable name="class-implementation"
        select="$implementation/parser-classes"/>

    <!-- The name of the interface that all parser classes and interfaces
         implement. -->
    <xsl:variable name="base-parser-interface-name"
        select="concat($language-name, 'Parser')"/>

    <!-- The name of the abstract base class that parser minimal abstract
         base classes often/always extend. -->
    <xsl:variable name="base-minimal-abstract-parser-class-name"
        select="concat($language-name, 'MinimalAbstractParser')"/>

    <!-- The name of the base class for parsers used to parse
         constructs that start with the wrong token. -->
    <xsl:variable name="invalid-first-token-parser-class-name"
        select="concat($language-name,
                       'InvalidFirstTokenParser')"/>

    <!-- The name of the base class for parsers used to parse a
         multiline construct's indented subconstructs that start
         with the wrong token. -->
    <xsl:variable
        name="invalid-first-indented-subconstruct-token-parser-class-name"
        select="concat($language-name,
                       'InvalidFirstIndentedSubconstructTokenParser')"/>

    <!-- The name of the base class for parsers used to parse choice
         constructs that start with the wrong token. -->
    <xsl:variable name="invalid-first-choice-token-parser-class-name"
        select="concat($language-name, 'InvalidFirstChoiceTokenParser')"/>


    <!-- The name of the interface that all parsing helper classes and
         interfaces implemented/extend. -->
    <xsl:variable name="base-parsing-helper-interface-name"
        select="concat($language-name, 'ParsingHelper')"/>

    <!-- The name of the interface that all token ID to parser map
         classes and interfaces implement. -->
    <xsl:variable name="base-parser-map-interface-name"
        select="concat($base-parser-interface-name, 'Map')"/>

    <!-- The name of the base class that all token ID to subconstruct
         parsing helper maps extend. -->
    <xsl:variable name="base-parsing-helper-map-class-name"
        select="concat($language-name, 'ParsingHelperMap')"/>

    <!-- The name of the factory singleton used to create parsers. -->
    <xsl:variable name="parser-factory-class-name">
        <xsl:value-of select="concat($language-name, 'ParserFactory')"/>
    </xsl:variable>

    <!-- The name of the (automatically generated) base class for
         the parser factory singleton. -->
    <xsl:variable name="parser-factory-base-class-name">
        <xsl:value-of select="concat($language-name, 'ParserFactoryBase')"/>
    </xsl:variable>


    <!-- ################## -->
    <!-- # Main templates # -->
    <!-- ################## -->

    <xsl:template match="language">
        <xsl:text/>

        <!-- Generate the parsers README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the interface implemented by all parser classes
             and interfaces. -->
        <xsl:call-template name="base-parser-interface"/>

        <!-- Generate the abstract base class extended by most/all
             parser minimal abstract base classes. -->
        <xsl:call-template name="base-minimal-abstract-parser-class"/>


        <!-- Generate the base class for parsers used to parse constructs
             that start with the wrong token. -->
        <xsl:call-template name="invalid-first-token-parser-class"/>

        <!-- Generate the base class for parsers used to parse
             indented subconstructs that start with the wrong token. -->
        <xsl:call-template
            name="invalid-first-indented-subconstruct-token-parser-class"/>

        <!-- Generate the base class for parsers used to parse
             choice constructs that start with the wrong token. -->
        <xsl:call-template name="invalid-first-choice-token-parser-class"/>


        <!-- Generate the interface implemented by all parsing
             helpers. -->
        <xsl:call-template name="base-parsing-helper-interface"/>

        <!-- Generate the interface implemented by all token ID to
             parser maps. -->
        <xsl:call-template name="base-parser-map-interface"/>

        <!-- Generate the abstract base class extended by most/all
             token ID to subconstruct parsing helper maps. -->
        <xsl:call-template name="base-parsing-helper-map-class"/>

        <!-- Generate the source code for the parser class(es) for
             each type of construct. -->
        <xsl:apply-templates select="$constructs"/>

        <!-- Generate the parser factory base class. -->
        <xsl:call-template name="parser-factory-base"/>

        <!-- Generate the default parser factory skeleton. -->
        <xsl:call-template name="default-parser-factory-skeleton"/>

        <!-- Generate the singleton parser factory class. -->
        <xsl:call-template name="parser-factory"/>
    </xsl:template>

<!--
NOTE: parsers for different types of constructs have different requirements
      on the token list and tokenizer passed to their parse() methods
NOTE: this information is probably at least a little wrong now: all
      parsers will assume that they're given at least (part of) their
      first line as a TrackedTokenList. Construct parsers will have to
      ensure this for all of their subconstruct parsers (see nextLine())
- 'alias-construct': no direct requirements, but the token list and
  tokenizer must meet the requirements specified by the construct that
  this construct is an alias for
- 'choice-construct', 'line-choice-construct': no requirements, though the
  token list and tokenizer will be passed unchanged to the parse() methods
  of the parsers that this parser chooses among, and so must satisfy those
  parsers' requirements
- 'construct', 'repeated-construct', 'single-token-construct': the token
  list must be non-null (though it can be empty). Only tokens in the token
  list will be parsed by this kind of parser: the tokenizer will be left
  untouched
- 'compound-construct': the token list must be null
- 'single-line-construct', 'multiline-construct': the token list must be null.
  (The parser itself will construct a token list containing the tokens
  for the construct's (first) line)
-->

    <xsl:template match="alias-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-alias-construct-parser"/>
        <xsl:call-template name="default-alias-construct-parser"/>
    </xsl:template>

    <xsl:template match="single-token-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-single-token-construct-parser"/>
    </xsl:template>

    <xsl:template match="construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-construct-parser"/>
    </xsl:template>

    <xsl:template match="repeated-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-repeated-construct-parser"/>
    </xsl:template>

    <xsl:template match="single-line-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-single-line-construct-parser"/>
    </xsl:template>

    <xsl:template match="multiline-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-multiline-construct-parser"/>
    </xsl:template>

    <xsl:template match="compound-construct">
        <xsl:call-template name="parser-interface"/>
        <xsl:call-template name="minimal-abstract-construct-parser"/>
        <xsl:call-template name="default-compound-construct-parser"/>
    </xsl:template>

    <xsl:template match="choice-construct">
        <xsl:call-template name="parser-interface">
            <xsl:with-param name="can-be-aliased" select="false()"/>
        </xsl:call-template>
        <xsl:call-template name="minimal-abstract-construct-parser">
            <xsl:with-param name="can-be-aliased" select="false()"/>
        </xsl:call-template>
        <xsl:call-template name="default-or-abstract-choice-construct-parser">
            <xsl:with-param name="map-string">
                <xsl:call-template name="build-choice-construct-map-string"/>
            </xsl:with-param>
            <xsl:with-param name="first-token-index" select="0"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="line-choice-construct">
        <xsl:variable name="map-string">
            <xsl:call-template
                name="build-line-choice-construct-map-string"/>
        </xsl:variable>

        <xsl:call-template name="parser-interface">
            <xsl:with-param name="can-be-aliased" select="false()"/>
        </xsl:call-template>
        <xsl:call-template name="minimal-abstract-construct-parser">
            <xsl:with-param name="can-be-aliased" select="false()"/>
        </xsl:call-template>
        <xsl:call-template name="default-or-abstract-choice-construct-parser">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="first-token-index" select="1"/>
        </xsl:call-template>
    </xsl:template>


    <!-- ########################### -->
    <!-- # Parsers README templates # -->
    <!-- ########################### -->

    <xsl:template name="readme-file">
        <xsl:text>%%%% file README.parsers
</xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> Parsers README

Copyright (C) James MacKay

write this !!!!

- parsers.ignore
- parsers.discard

- create Default</xsl:text>
        <xsl:value-of select="concat($parser-factory-class-name, $src-ext)"/>
        <xsl:text> from
  Default</xsl:text>
        <xsl:value-of select="concat($parser-factory-class-name, $src-ext)"/>
        <xsl:text>.skeleton
    - replace '???' parts of the parser prototypes
- once a skeleton file has been used to create the 'real' file, the
  skeleton file's name is usually added to the discard file (parsers.discard)

- etc.
</xsl:text>
    </xsl:template>


    <!-- ########################## -->
    <!-- # Parser class templates # -->
    <!-- ########################## -->

    <!-- Outputs the interface that all parser classes and interfaces
         implement/extend. -->
    <xsl:template name="base-parser-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($base-parser-interface-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($source-module, '.', $location-factory-class-name)"/>
        <xsl:text>;
import </xsl:text>
        <xsl:value-of select="concat($tokens-module, '.', $token-manager-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The interface implemented by all </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> parser classes and interfaces.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$base-parser-interface-name"/>
        <xsl:text>
    extends Parser
{
    // Constants

    /** The token manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$token-manager-class-name"/>
        <xsl:text>
        TOKEN_MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($token-manager-class-name, '.',
                           $token-manager-constructor-name, '();')"/>
        <xsl:text>

    /** The construct manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>
        MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($construct-manager-class-name, '.',
                           $construct-manager-constructor-name, '();')"/>
        <xsl:text>

    /** The parser factory singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>
        PARSER_FACTORY = </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>.instance();

    /** The location factory singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$location-factory-class-name"/>
        <xsl:text>
        LOCATION_FACTORY = </xsl:text>
        <xsl:value-of
            select="concat($location-factory-class-name, '.',
                           $location-factory-constructor-name, '();')"/>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the abstract base class for all minimal parser abstract base
         classes. -->
    <xsl:template name="base-minimal-abstract-parser-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($base-minimal-abstract-parser-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocationFactory;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.TrackedTokenList;
import com.steelcandy.plack.common.constructs.MinimalAbstractParser;

/**
    The abstract base class for all minimal parser abstract base classes.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$base-minimal-abstract-parser-class-name"/>
        <xsl:text>
    extends MinimalAbstractParser
    implements </xsl:text>
        <xsl:value-of select="$base-parser-interface-name"/>
        <xsl:text>
{
    // Protected methods

    /**
        @param list a list of tokens
        @return the first token in 'list' that is not an indent token, or
        null if there are no non-indent tokens in 'list'
    */
    protected Token firstNonIndentToken(TrackedTokenList list)
    {
        Assert.require(list != null);

        Token result = null;

        int len = list.size();
        for (int i = 0 ; i &lt; len; i ++)
        {
            Token tok = list.get(i);
            if (TOKEN_MANAGER.isIndentToken(tok) == false)
            {
                result = tok;
                break;  // for
            }
        }

        // 'result' may be null
        return result;
    }

    /**
        @see MinimalAbstractParser#isEndOfLine(Token)
    */
    protected boolean isEndOfLine(Token tok)
    {
        return TOKEN_MANAGER.endOfLinePredicate().isSatisfied(tok);
    }

    /**
        @see MinimalAbstractParser#locationFactory
    */
    protected SourceLocationFactory locationFactory()
    {
        return LOCATION_FACTORY;
    }

    /**
        @see MinimalAbstractParser#tokenDescription(Token)
    */
    protected String tokenDescription(Token tok)
    {
        Assert.require(tok != null);

        String result = TOKEN_MANAGER.idToDescription(tok.id());

        Assert.ensure(result != null);
        return result;
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the abstract base class for parsers used to parse
         constructs that start with the wrong token. -->
    <xsl:template name="invalid-first-token-parser-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($invalid-first-token-parser-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.errors.ErrorHandler;

import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;

import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.UnsupportedMethodException;

/**
    An abstract base class for parsers used to parse subconstructs that
    start with the wrong token.
    &lt;p&gt;
    Subclasses just have to implement constructId() to return the
    construct ID of the type of construct that the invalid subconstruct
    is a subconstruct of, and reportInvalidFirstToken() to report the
    construct whose first token is invalid.
</xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
    <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-minimal-abstract-parser-class-name"/>
        <xsl:text>
{
    // Public methods

    /**
        This implementation always returns true since any token can
        be the start of a construct that starts with the wrong token.

        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return true;
    }

    /**
        This implementation always returns null.

        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public Construct parseConstruct(TrackedTokenList line, Tokenizer t,
                                    SubconstructParsingData data,
                                    ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        Token invalidTok;
        try
        {
            // Use the token after the indent as the invalid token iff there
            // is one.
            invalidTok = line.get(1);
            Assert.check(TOKEN_MANAGER.isIndentToken(invalidTok) == false);
        }
        catch (NoSuchItemException ex)
        {
            // There is no token after the indent, so use the indent token.
            invalidTok = line.get(0);
            Assert.check(TOKEN_MANAGER.isIndentToken(invalidTok));
        }
        SourceLocation loc = invalidTok.location();
        removeInvalidTokens(line, t, handler);
        String description = MANAGER.idToDescription(constructId());

        reportInvalidFirstToken(invalidTok, description, loc, t, handler);
        data.setPreviousLocation(loc);
        data.setWasParsedSuccessfully(false);

        return null;
    }


    // Protected methods

    /**
        @exception UnsupportedMethodException is always thrown because the
        type of construct parsed by this parser is unknown
        @see MinimalAbstractParser#parseableConstructDescription
    */
    protected String parseableConstructDescription()
    {
        throw new UnsupportedMethodException(getClass(),
                                    "parseableConstructDescription()");

        // Assert.ensure(result != null);
        // return result;
    }

    /**
        Removes enough tokens from the specified token list and (possibly)
        tokenizer after encountering an invalid first token in order to allow
        us to at least try to start parsing successfully again.
        &lt;p&gt;
        This implementation just removes the invalid first token. It should
        be overridden when the construct whose first token was invalid was to
        be one that consists of one or more full lines.

        @param line the tokens that represent the current line, starting with
        the invalid first token
        @param t the tokenizer from which to obtain the tokens representing
        the source code after the current line
        @param handler the error handler to use to handle any errors
        @return the location of the tokens that this method removed
    */
    protected SourceLocation removeInvalidTokens(TrackedTokenList line,
                                        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        SourceLocation result = line.get(0).location();
        line.advance();  // over the invalid first token

        return result;
    }

    /**
        @see MinimalAbstractParser#handleTopLevelConstructStartInvalid(Token, Tokenizer, ErrorHandler)
    */
    protected void handleTopLevelConstructStartInvalid(Token tok,
                                        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // This method should never get called since canStartConstruct()
        // always returns true.
        Assert.unreachable();
    }

    /**
        @see MinimalAbstractParser#handleConstructMissing(TrackedTokenList, Tokenizer, ErrorHandler)
    */
    protected void handleConstructMissing(TrackedTokenList line,
        Tokenizer t, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(line.isEmpty());
        Assert.require(t != null);
        Assert.require(handler != null);

        // This method should never get called since this parser doesn't
        // actually parse anything: it just discards tokens.
        Assert.unreachable();
    }


    // Abstract methods

    /**
        @return the construct ID of the type of construct that the
        invalid subconstructs are subconstructs of
    */
    protected abstract int constructId();

    /**
        Reports that the construct with the specified description
        starts with an invalid token.

        @param tok the invalid token
        @param constructDescription a description of the type of the
        construct that starts with the invalid token 'tok'
        @param loc the location in the source code of the invalid token
        @param t the tokenizer that is the source of the tokens being parsed:
        note that 'tok' - and possibly some following tokens - have already
        been removed from it
        @param handler the error handler to use to handle the error
    */
    protected abstract void
        reportInvalidFirstToken(Token tok, String constructDescription,
            SourceLocation loc, Tokenizer t, ErrorHandler handler);
        // Assert.require(tok != null);
        // Assert.require(constructDescription != null);
        // Assert.require(loc != null);
        // Assert.require(t != null);
        // Assert.require(handler != null);
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the abstract base class for parsers used to parse
         multiline constructs' indented subconstructs that start with
         the wrong token. -->
    <xsl:template name="invalid-first-indented-subconstruct-token-parser-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($invalid-first-indented-subconstruct-token-parser-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;

/**
    An abstract base class for parsers used to parse indented
    subconstructs that start with the wrong token.
    &lt;p&gt;
    Subclasses just have to implement constructId() to return the
    construct ID of the type of construct that the invalid subconstruct
    is a subconstruct of.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$invalid-first-indented-subconstruct-token-parser-class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>
{
    // Protected methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>#removeInvalidTokens(TrackedTokenList, Tokenizer, ErrorHandler)
    */
    protected SourceLocation removeInvalidTokens(TrackedTokenList line,
                                Tokenizer t, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: we remove everything up to (but not including) the next
        // line that is indented the same number of levels as 'line'.
        // This could conceivably consume only part of a compound
        // construct (whose first subconstruct's first token was invalid),
        // possibly causing spurious errors to be reported for the
        // subsequent subconstructs.
        // TODO: is there a better way to handle this ???!!!!???
        Token tok = line.get(0);
        Assert.check(TOKEN_MANAGER.isIndentToken(tok));
        int indentLevel =
            TOKEN_MANAGER.firstLineIndentLevels(tok);
        SourceLocation result = location(location(line),
            location(TOKEN_MANAGER.tokensUpToIndent(t, indentLevel)));

        return result;
    }


    /**
        @see </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>#reportInvalidFirstToken(Token, String, SourceLocation, Tokenizer, ErrorHandler)
    */
    protected void
        reportInvalidFirstToken(Token tok, String constructDescription,
            SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(constructDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidFirstIndentedSubconstructToken(tok,
                            constructDescription, loc, t, handler);
    }
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the abstract base class for parsers used to parse
         multiline constructs' indented subconstructs that start with
         the wrong token. -->
    <xsl:template name="invalid-first-choice-token-parser-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($invalid-first-choice-token-parser-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.Tokenizer;

/**
    An abstract base class for parsers used to parse choice constructs
    that start with the wrong token.
    &lt;p&gt;
    Subclasses just have to implement constructId() to return the
    construct ID of the type of the choice construct.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$invalid-first-choice-token-parser-class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>
{
    // Protected methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>#reportInvalidFirstToken(Token, String, SourceLocation, Tokenizer, ErrorHandler)
    */
    protected void
        reportInvalidFirstToken(Token tok, String constructDescription,
            SourceLocation loc, Tokenizer t, ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(constructDescription != null);
        Assert.require(loc != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidFirstChoiceToken(tok, constructDescription,
                                      loc, t, handler);
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the abstract base class that all token ID to subconstruct
         parsing helper classes extend. -->
    <xsl:template name="base-parsing-helper-map-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($base-parsing-helper-map-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($tokens-module, '.', $token-manager-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.constructs.AbstractTokenIdToParsingHelperMap;

/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> token ID to subconstruct
    parsing helper map classes.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$base-parsing-helper-map-class-name"/>
        <xsl:text>
    extends AbstractTokenIdToParsingHelperMap
{
    // Constants

    /** The token manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$token-manager-class-name"/>
        <xsl:text>
        TOKEN_MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($token-manager-class-name, '.',
                           $token-manager-constructor-name, '();')"/>
        <xsl:text>

    /** The construct manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>
        MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($construct-manager-class-name, '.',
                           $construct-manager-constructor-name, '();')"/>
        <xsl:text>

    /** The parser factory singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>
        PARSER_FACTORY = </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>.instance();


    // Constructors

    /**
        @see AbstractTokenIdToParsingHelperMap#AbstractTokenIdToParsingHelperMap(int)
    */
    public </xsl:text>
        <xsl:value-of select="$base-parsing-helper-map-class-name"/>
        <xsl:text>(int numEntries)
    {
        super(numEntries);
    }


    // Protected methods

    /**
        Returns the description of the construct with the specified ID.

        @param constructId the ID of the construct whose description
        is to be returned
        @return the description of the construct with ID 'constructId'
    */
    protected static String description(int constructId)
    {
        return MANAGER.idToDescription(constructId);
    }
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the interface that all parsing helper classes and
         interfaces implement/extend. -->
    <xsl:template name="base-parsing-helper-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($base-parsing-helper-interface-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($tokens-module, '.', $token-manager-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.TokenIdToParserMap;

/**
    The interface implemented by all </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> parsing helpers.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$base-parsing-helper-interface-name"/>
        <xsl:text>
{
    // Constants

    /** The token manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$token-manager-class-name"/>
        <xsl:text>
        TOKEN_MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($token-manager-class-name, '.',
                           $token-manager-constructor-name, '();')"/>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the interface that all token ID to parser map classes and
         interfaces implement/extend. -->
    <xsl:template name="base-parser-map-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($base-parser-map-interface-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($tokens-module, '.', $token-manager-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.TokenIdToParserMap;

/**
    The interface implemented by all </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> token ID
    to parser map classes and interfaces.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$base-parser-map-interface-name"/>
        <xsl:text>
    extends TokenIdToParserMap
{
    // Constants

    /** The token manager singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$token-manager-class-name"/>
        <xsl:text>
        TOKEN_MANAGER = </xsl:text>
        <xsl:value-of
            select="concat($token-manager-class-name, '.',
                           $token-manager-constructor-name, '();')"/>
        <xsl:text>

    /** The parser factory singleton. */
    public static final </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>
        PARSER_FACTORY = </xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
        <xsl:text>.instance();
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the interface implemented by all parsers that parse
         the type of construct described by the current node. -->
    <xsl:template name="parser-interface">
        <xsl:param name="can-be-aliased" select="true()"/>

        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name" select="$parser-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    The interface implemented by parsers that parse </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
    constructs.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-parser-interface-name"/>
        <xsl:text>
{
    // Public methods

    /**
        Returns a clone of this parser that can be used to parse a
        new </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> construct.
        &lt;p&gt;
        Note: stateless parsers (and reentrant parsers in general)
        can just return themselves (that is, 'this').

        @return a clone of this parser
    */
    public </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> cloneParser();


    /**
        Returns the construct that results from parsing - as a top-level
        construct - the first tokens from the specified tokenizer.
        &lt;p&gt;
        &lt;strong&gt;Note&lt;/strong&gt;: a top-level construct is one that is not a
        subconstruct of another construct, and thus cannot have been checked
        to start with the proper type of token. For example, when parsing a
        construct that represents the entire contents of a source code file
        you would use this method instead of parse().

        @param t the tokenizer whose first tokens are to be parsed as a
        top-level construct
        @param handler the error handler to use to handle any errors
        that occur during parsing
        @return the construct that results from parsing the tokens
        @see #parse(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parseTopLevel(Tokenizer t, ErrorHandler handler);
        // Assert.require(t != null);
        // Assert.require(handler != null);

    /**
        Returns the construct that results from parsing the first tokens from
        the specified tokenizer.
        &lt;p&gt;
        This version of parse() is the one that is usually called by
        non-parsers to parse </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> constructs.
        (The other versions of parse() are usually only called by other
        parsers.)

        @param t the tokenizer whose first tokens are to be parsed
        @param handler the error handler to use to handle any errors
        that occur during parsing
        @return the construct that results from parsing the tokens
        @see #parseTopLevel(Tokenizer, ErrorHandler)
        @see Parser#parseConstruct(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(Tokenizer t, ErrorHandler handler);
        // Assert.require(t != null);
        // Assert.require(handler != null);

    /**
        Returns the </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct that results from
        parsing the first tokens from the specified token list
        and/or tokenizer as a subconstruct of another construct.
        &lt;p&gt;
        The tokens in the token list are the as yet unparsed tokens
        that represent (part of) the current line being parsed, and the
        ones in the tokenizer represent the following lines. Thus the
        tokens in the list logically precede the ones in the tokenizer,
        and so should always be processed first.
        &lt;p&gt;
        This version of parse() is usually only called by other parsers.

        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param t the tokenizer containing the tokens that represent
        the lines following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
        that occur during parsing
        @return the construct that results from parsing the tokens
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)</xsl:text>
        <xsl:if test="$can-be-aliased">
            <xsl:text>
        @see #parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
</xsl:text>
        </xsl:if>
        <xsl:text>
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(isEmptyLine(line) == false);
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(handler != null);
</xsl:text>
        <xsl:if test="$can-be-aliased">
            <xsl:text>
    /**
        Returns the </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text> construct that results from
        parsing the first tokens from the specified token list
        and/or tokenizer as a subconstruct of another construct.
        &lt;p&gt;
        The tokens in the token list are the as yet unparsed tokens
        that represent (part of) the current line being parsed, and the
        ones in the tokenizer represent the following lines. Thus the
        tokens in the list logically precede the ones in the tokenizer,
        and so should always be processed first.
        &lt;p&gt;
        This version of parse() is usually only called by other parsers.

        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param t the tokenizer containing the tokens that represent
        the lines following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param creator the construct creator to use to create the
        construct resulting from parsing the tokens
        @param handler the error handler to use to handle any errors
        that occur during parsing
        @return the construct that results from parsing the tokens
        @see #parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(isEmptyLine(line) == false);
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(creator != null);
        // Assert.require(handler != null);


    // Inner interfaces

    /**
        The interface implemented by classes that create instances
        of the type of construct parsed by this parser.
    */
    public static interface ConstructCreator
    {
        /**
            @return a new </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text> construct
            instance
        */
        public </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text> createConstruct();
            // Assert.ensure(result != null);
    }</xsl:text>
        </xsl:if>
        <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for a minimal abstract base class for
         parsers of the type of construct described by the current node. -->
    <xsl:template name="minimal-abstract-construct-parser">
        <xsl:param name="can-be-aliased" select="true()"/>

        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('MinimalAbstract', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    A minimal abstract base class for </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
    &lt;p&gt;
    This class implements the other versions of parseConstruct() and
    parse() in terms of the most general parse() method.
    &lt;p&gt;
    Note: this class' implementation of cloneParser() assumes that
    this parser is reentrant: if a subclass isn't reentrant then it
    should override cloneParser() (to return another instance of that
    class).
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:choose>
            <xsl:when test="$can-be-aliased">
                <xsl:text>
    @see </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>.ConstructCreator, ErrorHandler)</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
    @see </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
*/
public abstract class MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-minimal-abstract-parser-class-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{</xsl:text>
        <xsl:if test="$can-be-aliased">
            <xsl:text>
    // Constants

    /**
        The construct creator used to create </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        constructs.
    */
    private static final ConstructCreator
        DEFAULT_CREATOR = new DefaultConstructCreator();

</xsl:text>
        </xsl:if>
        <xsl:text>
    // Public methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#cloneParser
    */
    public </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> cloneParser()
    {
        return this;
    }

    /**
        @see Parser#cloneConstructParser
    */
    public Parser cloneConstructParser()
    {
        return cloneParser();
    }


    /**
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public Construct parseConstruct(TrackedTokenList line, Tokenizer t,
                                    SubconstructParsingData data,
                                    ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return parse(line, t, data, handler);
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parseTopLevel(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parseTopLevel(Tokenizer t, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result = null;

        TrackedTokenList line = nextLine(t);
        if (isEmptyLine(line))
        {
            handleConstructMissing(line, t, handler);
        }
        else
        {
            Token tok = firstNonIndentToken(line);
            if (tok == null || canStartConstruct(tok))
            {
                // We ignore whether parsing was successful (as indicated by
                // 'data') and just return the resulting construct (which may
                // be null).
</xsl:text>
        <xsl:choose>
            <xsl:when test="$can-be-aliased">
                <xsl:text>
                result = parse(line, t, createParsingData(),
                               DEFAULT_CREATOR, handler);</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
                result = parse(line, t, createParsingData(), handler);</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
            }
            else
            {
                handleTopLevelConstructStartInvalid(tok, t, handler);
            }
        }

        checkForTokensAfterTopLevelConstruct(t, handler);

        return result;  // may be null
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(Tokenizer t, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result = null;

        TrackedTokenList line = nextLine(t);
        if (isEmptyLine(line))
        {
            handleConstructMissing(line, t, handler);
        }
        else
        {
            // We ignore whether parsing was successful (as indicated by
            // 'data') and just return the resulting construct (which may
            // be null).
</xsl:text>
        <xsl:choose>
            <xsl:when test="$can-be-aliased">
                <xsl:text>
            result = parse(line, t, createParsingData(),
                           DEFAULT_CREATOR, handler);</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
            result = parse(line, t, createParsingData(), handler);</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
        }

        return result;  // may be null
    }
</xsl:text>
        <xsl:if test="$can-be-aliased">
            <xsl:text>
    /**
        @see </xsl:text>
            <xsl:value-of select="$parser-name"/>
            <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return parse(line, t, data, DEFAULT_CREATOR, handler);
    }
</xsl:text>
        </xsl:if>
        <xsl:text>

    // Protected methods

    /**
        @see MinimalAbstractParser#parseableConstructDescription
    */
    protected String parseableConstructDescription()
    {
        String result = MANAGER.
            idToDescription(MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>);

        Assert.ensure(result != null);
        return result;
    }</xsl:text>
        <xsl:if test="$can-be-aliased">
            <xsl:text>

    // Inner classes

    /**
        A ConstructCreator that creates </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>
        constructs.
    */
    private static class DefaultConstructCreator
        implements ConstructCreator
    {
        /**
            @see </xsl:text>
            <xsl:value-of select="concat($parser-name,
                               '.ConstructCreator#createConstruct')"/>
            <xsl:text>
        */
        public </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text> createConstruct()
        {
            </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text> result =
                MANAGER.create</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>();

            Assert.ensure(result != null);
            return result;
        }
    }</xsl:text>
        </xsl:if>
        <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the default parser for the type of
         compound construct described by the current node.

         Note: this template assumes that the compound construct's
         map string consists entirely of unique mappings. This should
         have been checked by the language description validator and
         so is not checked again here. Invalid code may be generated
         if this assumption does not hold. -->
    <xsl:template name="default-compound-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="map-string">
            <xsl:call-template name="build-subconstructs-map-string"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Default', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationList;

import java.util.HashSet;
import java.util.Set;

/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants
</xsl:text>
        <xsl:for-each select="subconstruct">
            <xsl:call-template name="subconstruct-start-token-id-set">
                <xsl:with-param name="map-string" select="$map-string"/>
            </xsl:call-template>
        </xsl:for-each>
        <xsl:text>

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        _instance = new Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return </xsl:text>
        <xsl:value-of select="concat('isStartOf', subconstruct[1]/@type, '(tok.id());')"/>
        <xsl:text>
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result =
            creator.createConstruct();

        boolean wasSubconstruct;
        Token indentTok = line.get(0);
        Assert.check(TOKEN_MANAGER.isIndentToken(indentTok));
        int indentLevels =
            TOKEN_MANAGER.firstLineIndentLevels(indentTok);
        data.setPreviousPart(indentTok);
</xsl:text>
        <!-- Note: since the first line of the first subconstruct has
             already been extracted out into 'line' we have to parse the
             first subconstruct a little differently. (This also allows
             us to eliminate a few unnecessary steps.)

             Note: the first subconstruct can't be optional: this is
             assumed to have been checked by the language description
             validator. -->
        <xsl:for-each select="subconstruct[1]">
            <xsl:variable name="subconstruct-name">
                <xsl:call-template name="subconstruct-name"/>
            </xsl:variable>
            <xsl:variable name="subconstruct-construct-name">
                <xsl:call-template name="construct-class-name"/>
            </xsl:variable>
            <xsl:variable name="subconstruct-parser-name">
                <xsl:call-template name="parser-class-name"/>
            </xsl:variable>

            <xsl:text>

        SourceLocationList subLocations =
            SourceLocationList.createArrayList();

        // Parse the first subconstruct.
        Assert.check(line.size() &gt;= 2);
            // since otherwise it represents a blank line
        Assert.check(isStartOf</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>(line.get(1).id()));
        </xsl:text>
            <xsl:value-of select="$subconstruct-parser-name"/>
            <xsl:text> firstParser =
            PARSER_FACTORY.create</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>Parser();
        </xsl:text>
            <xsl:value-of select="$subconstruct-construct-name"/>
            <xsl:text> firstSubconstruct =
            firstParser.parse(line, t, data, handler);
        if (firstSubconstruct != null)</xsl:text>
            <xsl:choose>
                <xsl:when test="@number = 'one-or-more'">
                    <xsl:text>
        {
            result.add</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(firstSubconstruct);
            subLocations.add(firstSubconstruct.location());
        }

        // Parse any more subconstructs of the same type as the first.
        while (t.hasTokenAfterNext() &amp;&amp;
               TOKEN_MANAGER.firstLineIndentLevels(t.peek()) ==
                    indentLevels &amp;&amp;
               isStartOf</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>(t.peekTokenAfterNext().id()))
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            TrackedTokenList nextLine = nextLine(t);
            firstSubconstruct = firstParser.cloneParser().
                                    parse(nextLine, t, data, handler);
            if (firstSubconstruct != null)
            {
                result.add</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(firstSubconstruct);
                subLocations.add(firstSubconstruct.location());
            }
        }
</xsl:text>
                </xsl:when>
                <xsl:when test="not(@number) or @number = 'one'">
                    <xsl:text>
        {
            result.set</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(firstSubconstruct);
            subLocations.add(firstSubconstruct.location());
        }
</xsl:text>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>

        <xsl:for-each select="subconstruct[position() &gt; 1]">
            <xsl:variable name="subconstruct-id">
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@type"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="subconstruct-name">
                <xsl:call-template name="subconstruct-name"/>
            </xsl:variable>
            <xsl:variable name="subconstruct-construct-name">
                <xsl:call-template name="construct-class-name"/>
            </xsl:variable>
            <xsl:variable name="subconstruct-parser-name">
                <xsl:call-template name="parser-class-name"/>
            </xsl:variable>

            <!-- Note each of the cases in this 'choose' element are
                 subtly different: don't try to combine them. -->
            <xsl:choose>
                <xsl:when test="@number = 'zero-or-more'">
                    <xsl:text>
        // Parse the optional </xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text> subconstructs.
        while (t.hasTokenAfterNext() &amp;&amp;
               TOKEN_MANAGER.firstLineIndentLevels(t.peek()) ==
                    indentLevels &amp;&amp;
               isStartOf</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>(t.peekTokenAfterNext().id()))
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            TrackedTokenList nextLine = nextLine(t);

            </xsl:text>
                    <xsl:value-of select="$subconstruct-parser-name"/>
                    <xsl:text> p =
                PARSER_FACTORY.create</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>Parser();
            </xsl:text>
            <xsl:value-of select="$subconstruct-construct-name"/>
            <xsl:text>
                subconstruct = p.parse(nextLine, t, data, handler);
            if (subconstruct != null)
            {
                result.add</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(subconstruct);
                subLocations.add(subconstruct.location());
            }
        }</xsl:text>
                </xsl:when>
                <xsl:when test="@number = 'one-or-more'">
                    <xsl:text>
        // Parse the </xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text> subconstructs.
        wasSubconstruct = false;
        while (t.hasTokenAfterNext() &amp;&amp;
               TOKEN_MANAGER.firstLineIndentLevels(t.peek()) ==
                    indentLevels &amp;&amp;
               isStartOf</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>(t.peekTokenAfterNext().id()))
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            wasSubconstruct = true;
            TrackedTokenList nextLine = nextLine(t);

            </xsl:text>
                    <xsl:value-of select="$subconstruct-parser-name"/>
                    <xsl:text> p =
                PARSER_FACTORY.create</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>Parser();
            </xsl:text>
                    <xsl:value-of select="$subconstruct-construct-name"/>
                    <xsl:text>
                subconstruct = p.parse(nextLine, t, data, handler);
            if (subconstruct != null)
            {
                result.add</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(subconstruct);
                subLocations.add(subconstruct.location());
            }
        }
        if (wasSubconstruct == false)
        {
            String subconstructDescription = MANAGER.
                idToDescription(MANAGER.</xsl:text>
                    <xsl:value-of select="$subconstruct-id"/>
                    <xsl:text>);
            handleMissing</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(result,
                t, data, subconstructDescription, handler);
        }</xsl:text>
                </xsl:when>
                <xsl:when test="@number = 'zero-or-one'">
                    <xsl:text>
        // Parse the optional </xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text> subconstruct.
        if (t.hasTokenAfterNext() &amp;&amp;
            TOKEN_MANAGER.firstLineIndentLevels(t.peek()) ==
                indentLevels &amp;&amp;
            isStartOf</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>(t.peekTokenAfterNext().id()))
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            TrackedTokenList nextLine = nextLine(t);

            </xsl:text>
                    <xsl:value-of select="$subconstruct-parser-name"/>
                    <xsl:text> p =
                PARSER_FACTORY.create</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>Parser();
            </xsl:text>
                    <xsl:value-of select="$subconstruct-construct-name"/>
                    <xsl:text>
                subconstruct = p.parse(nextLine, t, data, handler);
            if (subconstruct != null)
            {
                result.set</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(subconstruct);
                subLocations.add(subconstruct.location());
            }
        }</xsl:text>
                </xsl:when>
                <xsl:otherwise> <!-- 'one' (which is the default) -->
                    <xsl:text>
        // Parse the </xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text> subconstructs.
        wasSubconstruct = false;
        if (t.hasTokenAfterNext() &amp;&amp;
            TOKEN_MANAGER.firstLineIndentLevels(t.peek()) ==
                indentLevels &amp;&amp;
            isStartOf</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>(t.peekTokenAfterNext().id()))
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            wasSubconstruct = true;
            TrackedTokenList nextLine = nextLine(t);

            </xsl:text>
                    <xsl:value-of select="$subconstruct-parser-name"/>
                    <xsl:text> p =
                PARSER_FACTORY.create</xsl:text>
                    <xsl:value-of select="@type"/>
                    <xsl:text>Parser();
            </xsl:text>
                    <xsl:value-of select="$subconstruct-construct-name"/>
                    <xsl:text>
                subconstruct = p.parse(nextLine, t, data, handler);
            if (subconstruct != null)
            {
                result.set</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(subconstruct);
                subLocations.add(subconstruct.location());
            }
        }
        if (wasSubconstruct == false)
        {
            String subconstructDescription = MANAGER.
                idToDescription(MANAGER.</xsl:text>
                    <xsl:value-of select="$subconstruct-id"/>
                    <xsl:text>);
            handleMissing</xsl:text>
                    <xsl:value-of select="$subconstruct-name"/>
                    <xsl:text>(result,
                t, data, subconstructDescription, handler);
        }</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position() != last()">
                <xsl:text>
</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>

        finishConstruct(result, t, data,
                        locationFactory().create(subLocations), handler);

        return result;
    }


    // Protected methods
</xsl:text>
        <xsl:for-each select="subconstruct">
            <xsl:call-template name="is-subconstruct-start-token-id-method"/>
        </xsl:for-each>
        <xsl:text>

    // Error handling methods
</xsl:text>
        <xsl:for-each select="subconstruct[position() != 1]">
            <xsl:if test="not(@number) or @number = 'one' or
                      @number = 'one-or-more'">
                <xsl:variable name="subconstruct-name">
                    <xsl:call-template name="subconstruct-name"/>
                </xsl:variable>

                <xsl:text>
    /**
        Handles the </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct(s) being missing.

        @param c the construct whose subconstruct is missing
        @param t the tokenizer whose next tokens were supposed to be the
        missing subconstruct(s), but were not
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param subconstructDescription the description of the missing
        subconstruct
        @param handler the error handler to use to handle any errors
    */
    protected void handleMissing</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>(
        </xsl:text>
                <xsl:value-of select="$construct-name"/>
                <xsl:text> c,
        Tokenizer t, SubconstructParsingData data,
        String subconstructDescription, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(subconstructDescription != null);
        Assert.require(handler != null);

        reportMissingCompoundSubconstruct(subconstructDescription,
                                          data, t, handler);
    }
</xsl:text>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the definition of the set that contains the token IDs
         of all of the tokens that - according to the specified map
         string - can start the compound construct subconstruct described
         by the current node. -->
    <xsl:template name="subconstruct-start-token-id-set">
        <xsl:param name="map-string"/>

        <xsl:text>
    /**
        A set containing the token IDs (as Integers) of all of the tokens
        that can start </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> subconstructs.
    */
    private static final Set
        _startOf</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>TokenIds = new HashSet();
    static
    {</xsl:text>
        <xsl:call-template name="add-subconstruct-start-token-ids">
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <!-- Outputs the code that adds all of the token IDs that can
         start the subconstruct described by the current node. -->
    <xsl:template name="add-subconstruct-start-token-ids">
        <xsl:param name="map-string"/>

        <xsl:if test="string-length($map-string) &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="first-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="tail">
                <xsl:call-template name="tail-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="first-contains-type">
                <xsl:call-template name="contains-construct-type">
                    <xsl:with-param name="construct-types">
                        <xsl:call-template
                            name="construct-types-from-mapping">
                            <xsl:with-param name="mapping" select="$first"/>
                        </xsl:call-template>
                    </xsl:with-param>
                    <xsl:with-param name="type" select="@type"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:if test="$first-contains-type = 'true'">
                <xsl:variable name="token-name">
                    <xsl:call-template name="token-name-from-mapping">
                        <xsl:with-param name="mapping" select="$first"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="token-id">
                    <xsl:call-template name="to-constant-name">
                        <xsl:with-param name="name" select="$token-name"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:text>
        _startOf</xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text>TokenIds.
            add(new Integer(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
                <xsl:text>));</xsl:text>
            </xsl:if>

            <xsl:call-template name="add-subconstruct-start-token-ids">
                <xsl:with-param name="map-string" select="$tail"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the source code for the method that indicates
         whether a specified token ID can start the compound construct
         subconstruct described by the current node. -->
    <xsl:template name="is-subconstruct-start-token-id-method">
        <xsl:text>
    /**
        Indicates whether the specified token ID can be the first
        token of </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> subconstructs.
    */
    protected boolean isStartOf</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(int tokenId)
    {
        return _startOf</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>TokenIds.
                    contains(new Integer(tokenId));
    }
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the minimal abstract base class for
         parsers of the type of alias construct described by the current
         node. -->
    <xsl:template name="minimal-abstract-alias-construct-parser">
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('MinimalAbstract', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-minimal-abstract-parser-class-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Public methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#cloneParser
    */
    public </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> cloneParser()
    {
        return this;
    }

    /**
        @see Parser#cloneConstructParser
    */
    public Parser cloneConstructParser()
    {
        return cloneParser();
    }


    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parseTopLevel(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parseTopLevel(Tokenizer t, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result = null;

        TrackedTokenList line = nextLine(t);
        if (isEmptyLine(line))
        {
            handleConstructMissing(line, t, handler);
        }
        else
        {
            Token tok = firstNonIndentToken(line);
            if (tok == null || canStartConstruct(tok))
            {
                // We ignore whether parsing was successful (as indicated by
                // 'data') and just return the resulting construct (which may
                // be null).
                result = parse(line, t, createParsingData(), handler);
            }
            else
            {
                handleTopLevelConstructStartInvalid(tok, t, handler);
            }
        }

        checkForTokensAfterTopLevelConstruct(t, handler);

        return result;  // may be null
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(Tokenizer, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(Tokenizer t, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result = null;

        TrackedTokenList line = nextLine(t);
        if (isEmptyLine(line))
        {
            handleConstructMissing(line, t, handler);
        }
        else
        {
            // We ignore whether parsing was successful (as indicated by
            // 'data') and just return the resulting construct (which may
            // be null).
            result = parse(line, t, createParsingData(), handler);
        }

        return result;  // may be null
    }

    /**
        @see Parser#parseConstruct(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public Construct parseConstruct(TrackedTokenList line, Tokenizer t,
                                    SubconstructParsingData data,
                                    ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return parse(line, t, data, handler);
    }


    // Protected methods

    /**
        @see MinimalAbstractParser#parseableConstructDescription
    */
    protected String parseableConstructDescription()
    {
        String result = MANAGER.
            idToDescription(MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>);

        Assert.ensure(result != null);
        return result;
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the default parser for the type of
         alias construct described by the current node.

         Note: alias constructs can always be aliased, and thus we
         can always assume here that the parse() methods that take
         ConstructCreators are defined. -->
    <xsl:template name="default-alias-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="aliased-construct">
            <xsl:call-template name="construct-class-name">
                <xsl:with-param name="construct-type"
                    select="@aliased-construct"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="aliased-parser-name">
            <xsl:call-template name="parser-class-name">
                <xsl:with-param name="construct-type"
                    select="@aliased-construct"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="aliased-creator-type"
            select="concat($aliased-parser-name, '.ConstructCreator')"/>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Default', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants

    /**
        The construct creator used to create </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        constructs for this parser.
    */
    private static final ConstructCreator
        DEFAULT_CREATOR = new DefaultConstructCreator();

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        _instance = new Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);

        // The token can start a construct parsed by this parser iff it
        // can start a construct parsed by a parser of the type of
        // construct that this construct aliases.
        return PARSER_FACTORY.
            create</xsl:text>
        <xsl:value-of select="@aliased-construct"/>
        <xsl:text>Parser().
                canStartConstruct(tok);
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return parse(line, t, data, DEFAULT_CREATOR, handler);
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$aliased-parser-name"/>
        <xsl:text> parser =
            PARSER_FACTORY.create</xsl:text>
        <xsl:value-of select="@aliased-construct"/>
        <xsl:text>Parser();

        return (</xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>)
            parser.parse(line, t, data,
                         new AliasedConstructCreator(creator), handler);
    }


    // Inner classes

    /**
        A construct creator that can be passed to the aliased construct
        parser's parser() methods. It uses a ConstructCreator to create
        its constructs.
    */
    private static class AliasedConstructCreator
        implements </xsl:text>
        <xsl:value-of select="$aliased-creator-type"/>
        <xsl:text>
    {
        // Private fields

        /** The ConstructCreator that creates our constructs. */
        private ConstructCreator _creator;


        // Constructors

        /**
            Constructs an AliasedConstructCreator from the
            ConstructCreator that will create its constructs.
        */
        public AliasedConstructCreator(ConstructCreator creator)
        {
            _creator = creator;
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="concat($aliased-creator-type,
                                              '#createConstruct')"/>
        <xsl:text>
        */
        public </xsl:text>
        <xsl:value-of select="$aliased-construct"/>
        <xsl:text> createConstruct()
        {
            // Assert.ensure(result != null);
            return _creator.createConstruct();
        }
    }

    /**
        A ConstructCreator that creates </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        constructs.
    */
    private static class DefaultConstructCreator
        implements ConstructCreator
    {
        /**
            @see </xsl:text>
        <xsl:value-of select="concat($parser-name, '#createConstruct')"/>
        <xsl:text>
        */
        public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> createConstruct()
        {
            // Assert.ensure(result != null);
            return MANAGER.create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>();
        }
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for an abstract base class for
         parsers that parse constructs that consist of at most one
         line (that is, parsers that only need to access the tokens
         from the TrackedTokenList but none from the Tokenizer). This
         includes single-line-constructs, but NOT line-choice-constructs. -->
    <xsl:template name="abstract-construct-parser">
        <xsl:param name="can-be-aliased" select="true()"/>

        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Abstract', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
    &lt;p&gt;
    Note: this class' implementation of cloneParser() assumes that
    this parser is reentrant: if a subclass isn't reentrant then it
    should override cloneParser() (to return another instance of that
    class).
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class Abstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Public methods
</xsl:text>
        <xsl:choose>
            <xsl:when test="$can-be-aliased">
                <xsl:text>
    /**
        @see </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
                <xsl:value-of select="$construct-name"/>
                <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        return parse(line, data, creator, handler);
    }</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
    /**
        @see </xsl:text>
                <xsl:value-of select="$parser-name"/>
                <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public </xsl:text>
                <xsl:value-of select="$construct-name"/>
                <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return parse(line, data, handler);
    }</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>


    // Abstract methods
</xsl:text>
        <xsl:choose>
            <xsl:when test="$can-be-aliased">
                <xsl:text>
    /**
        Returns the </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> construct that results from
        parsing the tokens in the token list as a subconstruct of
        another construct.

        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param creator the construct creator to use to create the
        construct resulting from parsing the tokens
        @param handler the error handler to use to handle any errors
        that occur during parsing
        @return the construct that results from parsing the tokens
    */
    protected abstract </xsl:text>
                <xsl:value-of select="$construct-name"/>
                <xsl:text>
        parse(TrackedTokenList line, SubconstructParsingData data,
              ConstructCreator creator, ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(isEmptyLine(line) == false);
        // Assert.require(data != null);
        // Assert.require(creator != null);
        // Assert.require(handler != null);</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
    /**
        Returns the </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> construct that results from
        parsing the tokens in the token list as a subconstruct of
        another construct.

        @param line the as yet unparsed tokens that represent (part of) the
        current line being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors that
        occur during parsing
        @return the construct that resulted from parsing the tokens
    */
    protected abstract </xsl:text>
                <xsl:value-of select="$construct-name"/>
                <xsl:text>
        parse(TrackedTokenList line, SubconstructParsingData data,
              ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(isEmptyLine(line) == false);
        // Assert.require(data != null);
        // Assert.require(handler != null);</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the default parser for a
         'single-token-construct'. -->
    <xsl:template name="default-single-token-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Default', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.common.ints.DefaultUnaryIntPredicate;

/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants

    /**
        The token predicate that matches only those tokens that a
        </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct can represent.
    */
    private static final UnaryTokenPredicate
        _predicate = createTokenPredicate();

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        _instance = new Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);

        return _predicate.isSatisfied(tok);
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
            result = creator.createConstruct();
        try
        {
            Token tok = line.get(0);

            // Note: 'result' gets its location from 'tok' (even if it turns
            // out to be a missing or invalid construct), so its location
            // doesn't have to be set explicitly.
            result.setToken(tok);

            if (_predicate.isSatisfied(tok))
            {
                // Consume 'tok' iff it is valid for our purposes.
                line.advance();
            }
            else
            {
                // The token that our construct is to represent is
                // either missing or invalid.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>);
                handleInvalidToken(line, data, description, t, handler);
            }
        }
        catch (InvalidConstructPartException ex)
        {
            data.setWasParsedSuccessfully(false);
        }

        // Note: the location can be null because 'result' got its
        // location from its token.
        finishConstruct(result, t, data, null, handler);

        Assert.ensure(result != null);
        return result;
    }


    /**
        Handles the token that the construct represents being missing
        or invalid.

        @param line the list of tokens whose first token was supposed to be
        the token that the construct was to represent, but is something else
        instead
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the single token
        construct whose token is invalid or missing
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the token is
        invalid or missing and it may not be possible to parse any following
        construct parts
    */
    protected void handleInvalidToken(TrackedTokenList line,
        SubconstructParsingData data, String constructDescription,
        Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        Token tok = line.get(0);
        String tokDesc = TOKEN_MANAGER.idToDescription(tok.id());

        reportInvalidSingleTokenConstructToken(constructDescription,
                                tokDesc, tok.location(), t, handler);
        throw InvalidConstructPartException.instance();
    }


    // Private static methods

    /**
        @return the token predicate that matches exactly those
        tokens that can be represented by </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        constructs
    */
    private static UnaryTokenPredicate createTokenPredicate()
    {
        DefaultUnaryIntPredicate idPredicate =
            new DefaultUnaryIntPredicate(false);
</xsl:text>
        <xsl:apply-templates select="*"
            mode="set-non-default-predicate-token-id-value"/>
        <xsl:text>

        return new IdPredicateTokenPredicate(idPredicate);
    }
}</xsl:text>
    </xsl:template>

    <xsl:template match="terminal-choice"
        mode="set-non-default-predicate-token-id-value">
        <xsl:apply-templates select="terminal"
            mode="set-non-default-predicate-token-id-value"/>
    </xsl:template>

    <xsl:template match="terminal"
        mode="set-non-default-predicate-token-id-value">
        <xsl:variable name="token-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
        idPredicate.setNonDefaultValue(TOKEN_MANAGER.</xsl:text>
        <xsl:value-of select="concat($token-manager-constructor-name, '().',
                                     $token-id)"/>
        <xsl:text>);</xsl:text>
    </xsl:template>

    <!-- Ignore any other child elements of a single-token-construct. -->
    <xsl:template match="*" mode="set-non-default-predicate-token-id-value"/>


    <!-- Outputs the source code for a default parser class for
         parsing a 'construct' described by the current node. -->
    <xsl:template name="default-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Default', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        _instance = new Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return </xsl:text>
        <xsl:call-template name="can-start-construct-implementation">
            <xsl:with-param name="parts" select="*"/>
        </xsl:call-template>
        <xsl:text>;
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        int startIndex = line.toAbsoluteIndex(0);
        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
            result = creator.createConstruct();
        try
        {
            parseParts(line, t, data, result, handler);
        }
        catch (InvalidConstructPartException ex)
        {
            data.setWasParsedSuccessfully(false);
        }

        SourceLocation loc = null;
        int pastEndIndex = line.toAbsoluteIndex(0);
        Assert.check(startIndex &lt;= pastEndIndex);
        if (startIndex &lt; pastEndIndex)
        {
            loc = TOKEN_MANAGER.location(line.
                    absoluteSubList(startIndex, pastEndIndex));
        }
        finishConstruct(result, t, data, loc, handler);

        Assert.ensure(result != null);
        return result;
    }


    // Construct part parsing methods

    /**
        Parses all of the parts of this construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose parts we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the parts
        @exception InvalidConstructPartException thrown if a part is missing
        or invalid
    */
    protected void parseParts(TrackedTokenList line,
        Tokenizer t, SubconstructParsingData data,
        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> parent,
        ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);
</xsl:text>
        <xsl:apply-templates select="*" mode="call-parse-construct-part-method"/>
        <xsl:text>
    }
</xsl:text>
        <xsl:apply-templates select="*" mode="parse-construct-part-method">
            <xsl:with-param name="parent-construct-name" select="$construct-name"/>
        </xsl:apply-templates>
        <xsl:text>

    // Error handling methods
</xsl:text>
        <xsl:apply-templates select="*" mode="handle-error-methods"/>
        <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for a default or abstract base parser
         class for parsing the type of 'line-choice-construct' or
         'choice-construct' described by the current node. It also
         outputs the source code for the token ID to parser map
         (default or abstract base) class used in the parser's
         implementation.

         Note: the generated class will be abstract if two different
         choices can start with the same token.-->
    <xsl:template name="default-or-abstract-choice-construct-parser">
        <xsl:param name="map-string"/>
        <xsl:param name="first-token-index"/>

        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="is-concrete">
            <xsl:call-template name="has-concrete-parser"/>
        </xsl:variable>
        <xsl:variable name="abstract-mod">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'false'">abstract </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="class-name">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:value-of
                        select="concat('Default', $parser-name)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="concat('Abstract', $parser-name)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="map-class-name"
            select="concat($class-name, 'Map')"/>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
</xsl:text>
        <xsl:choose>
            <xsl:when test="$is-concrete = 'true'">
                <xsl:text>    The default </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> parser implementation.
    </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>    An abstract base class for </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> parsers.
    </xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public </xsl:text>
        <xsl:value-of select="$abstract-mod"/>
        <xsl:text>class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{</xsl:text>
        <xsl:if test="$is-concrete = 'true'">
            <xsl:text>
    // Constants

    /**
        The token ID to parser map used by default by instances of
        this class.
    */
    private static final TokenIdToParserMap DEFAULT_MAP =
        new </xsl:text>
            <xsl:value-of select="$map-class-name"/>
            <xsl:text>();
    static
    {
        DEFAULT_MAP.initialize();
    }

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>
        _instance = new </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
            <xsl:value-of select="$parser-name"/>
            <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>()
    {
        // empty
    }

</xsl:text>
        </xsl:if>
        <xsl:text>
    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return getTokenIdToParserMap().hasParserFor(tok.id());
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        return (</xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>)
            getTokenIdToParserMap().get(line.get(</xsl:text>
        <xsl:value-of select="$first-token-index"/>
        <xsl:text>).id()).
                cloneConstructParser().
                    parseConstruct(line, t, data, handler);
    }
</xsl:text>
        <xsl:choose>
            <xsl:when test="$is-concrete = 'true'">
                <xsl:text>

    // Protected methods

    /**
        @return the token ID to parser map that maps the first token
        of a construct to be parsed by this parser to the parser to
        clone to get the parser to parse that construct
    */
    protected TokenIdToParserMap getTokenIdToParserMap()
    {
        return DEFAULT_MAP;
    }</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>

    // Abstract methods

    /**
        @return the token ID to parser map that maps the first token
        of a construct to be parsed by this parser to the parser to
        clone to get the parer to parse that construct
        @see </xsl:text>
                <xsl:value-of select="$map-class-name"/>
                <xsl:text>
    */
    protected abstract TokenIdToParserMap getTokenIdToParserMap();</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
}</xsl:text>

        <xsl:if test="$is-concrete = 'false'">
            <xsl:call-template
                name="default-choice-construct-parser-skeleton"/>
        </xsl:if>
        <xsl:call-template name="parser-map">
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the skeleton of the default parser for the choice
         construct described by the current node. -->
    <xsl:template name="default-choice-construct-parser-skeleton">
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="class-name"
            select="concat('Custom', $parser-name)"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;

/**
    The custom </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends Abstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants

    /**
        The token ID to parser map used by default by instances of
        this class.
    */
    private static final TokenIdToParserMap DEFAULT_MAP =
        new </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>Map();
    static
    {
        DEFAULT_MAP.initialize();
    }

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        _instance = new </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Protected methods

    /**
        @see Abstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#getTokenIdToParserMap
    */
    protected TokenIdToParserMap getTokenIdToParserMap()
    {
        return DEFAULT_MAP;
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for a default parser class for parsing
         the type of 'single-line-construct' described by the current
         node. -->
    <xsl:template name="default-single-line-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:call-template
            name="default-single-or-multiline-construct-parser-start">
            <xsl:with-param name="is-concrete" select="'true'"/>
            <xsl:with-param name="class-name-prefix" select="'Default'"/>
            <xsl:with-param name="first-line-parts" select="*"/>
        </xsl:call-template>
        <xsl:text>

        // Report the subconstructs indented under what is to be a
        // single line construct.
        String description = MANAGER.
            idToDescription(MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>);
        TokenList indentedTokens =
            TOKEN_MANAGER.tokensUpToIndent(t, indentLevels);
        if (indentedTokens.isEmpty() == false)
        {
            data.setPreviousPart(indentedTokens.getLast());
        }

        reportSubconstructsUnderSingleLineConstruct(description,
                                    location(indentedTokens), t, handler);
    }


    // Error handling methods
</xsl:text>
        <xsl:apply-templates select="*"
            mode="handle-error-methods"/>
        <xsl:text>
}</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the default parser for the type of
         multiline construct described by the current node. -->
    <xsl:template name="default-multiline-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="construct-type" select="@type"/>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="map-string">
            <xsl:call-template name="build-subconstructs-map-string">
                <xsl:with-param name="subconstructs"
                    select="indented-subconstructs/*"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="is-concrete">
            <xsl:call-template name="has-concrete-parser"/>
        </xsl:variable>
        <xsl:variable name="class-name-prefix">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:value-of select="'Default'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'Abstract'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="helper-interface-name"
            select="concat($language-name, @type, 'ParsingHelper')"/>
        <xsl:variable name="helper-map-name">
            <xsl:call-template name="parsing-helper-map-name"/>
        </xsl:variable>

        <xsl:call-template
            name="default-single-or-multiline-construct-parser-start">
            <xsl:with-param name="is-concrete" select="$is-concrete"/>
            <xsl:with-param name="class-name-prefix"
                select="$class-name-prefix"/>
            <xsl:with-param name="first-line-parts" select="first-line/*"/>
        </xsl:call-template>
        <xsl:text>

        while (t.hasNext())
        {
            Token indentTok = t.peek();
            Assert.check(TOKEN_MANAGER.isIndentToken(indentTok));
            data.setPreviousPart(indentTok);
            int currentIndentLevels =
                TOKEN_MANAGER.firstLineIndentLevels(indentTok);
            if (currentIndentLevels == indentLevels)
            {
                TrackedTokenList nextLine = nextLine(t);
                Assert.check(nextLine.size() &gt; 1);
                    // since there should be no blank lines (and
                    // the first token is an indent token)
                getTokenIdToParsingHelperMap().get(nextLine.get(1).id()).
                    parseAndAdd(nextLine, t, data, result,
                                partLocations, this, handler);
            }
            else if (currentIndentLevels &lt; indentLevels)
            {
                // The next line from 't' isn't indented under our
                // construct's first line, so we're done.
                break;
            }
            else  // currentIndentLevels &gt; indentLevels
            {
                int numLevels = currentIndentLevels - indentLevels + 1;
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>);
                SourceLocation loc = location(TOKEN_MANAGER.
                            tokensUpToIndent(t, currentIndentLevels));

                reportSubconstructIndentedTooFar(description, numLevels,
                                                 loc, t, handler);
            }
        }
    }


    // Subconstruct parsing helper map constants and methods
</xsl:text>
        <xsl:if test="$is-concrete = 'true'">
            <xsl:text>
    /**
        The token ID to parsing helper map used by default by
        instances of this class.
    */
    private static final </xsl:text>
            <xsl:value-of select="$helper-map-name"/>
            <xsl:text>
        DEFAULT_PARSING_HELPER_MAP =
            new Default</xsl:text>
            <xsl:value-of select="$helper-map-name"/>
            <xsl:text>();
    static
    {
        DEFAULT_PARSING_HELPER_MAP.initialize();
    }
</xsl:text>
        </xsl:if>

        <xsl:choose>
            <xsl:when test="$is-concrete = 'true'">
                <xsl:text>
    /**
        @return the token ID to parsing helper map that maps the
        first token of an indented subconstruct of a construct to be
        parsed by this parser to the parsing helper to use to parse
        the indented subconstruct and add it to the construct
    */
    protected </xsl:text>
                <xsl:value-of select="$helper-map-name"/>
                <xsl:text>
        getTokenIdToParsingHelperMap()
    {
        return DEFAULT_PARSING_HELPER_MAP;
    }
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
    /**
        @return the token ID to parsing helper map that maps the
        first token of an indented subconstruct of a construct to be
        parsed by this parser to the parsing helper to use to parse
        the indented subconstruct and add it to the construct
        @see Abstract</xsl:text>
                <xsl:value-of select="$helper-map-name"/>
                <xsl:text>
    */
    protected abstract </xsl:text>
                <xsl:value-of select="$helper-map-name"/>
                <xsl:text>
        getTokenIdToParsingHelperMap();
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>

    // Error handling methods
</xsl:text>
        <xsl:apply-templates select="first-line/*"
            mode="handle-error-methods"/>
        <xsl:text>
}</xsl:text>
        <xsl:call-template name="parsing-helper-interface">
            <xsl:with-param name="class-name" select="$helper-interface-name"/>
            <xsl:with-param name="construct-name" select="$construct-name"/>
        </xsl:call-template>
        <xsl:call-template name="parsing-helper-map-interface">
            <xsl:with-param name="class-name" select="$helper-map-name"/>
            <xsl:with-param name="helper-class-name"
                select="$helper-interface-name"/>
        </xsl:call-template>
        <xsl:call-template name="default-or-abstract-parsing-helper-map">
            <xsl:with-param name="interface-name"
                select="$helper-map-name"/>
            <xsl:with-param name="helper-class-name"
                select="$helper-interface-name"/>
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the source code for the interface implemented by token
         ID to subconstruct parsing helper maps used by parsers of the
         type of multiline construct described by the current node. -->
    <xsl:template name="parsing-helper-map-interface">
        <xsl:param name="class-name"/>
        <xsl:param name="helper-class-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
            <xsl:text>
/**
    The interface implemented by token ID to subconstruct parsing helper
    maps used in implementing </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
    &lt;p&gt;
    </xsl:text>
        <strong>Note</strong>
        <xsl:text>: an instance's initialize() method must be
    called after it is constructed but before it is used.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
{
    // Public methods

    /**
        Initializes the contents of this map. It must be called
        before this map can usefully be used.
    */
    public void initialize();

    /**
        Returns the parsing helper to which the specified token ID
        maps. It will always be mapped to a (non-null) helper.

        @param tokenId the token ID to map to a parsing helper
        @return the parsing helper to which the token ID maps
    */
    public </xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text> get(int tokenId);
        // Assert.ensure(result != null);
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the source code for the default or abstract base class
         of the type of token ID to subconstruct parsing helper used in
         the implementation of parsers of the type of multiline construct
         described by the current node.

         The class that is output will be an abstract class if the
         specified map string is not unique, and concrete if it is. -->
    <xsl:template name="default-or-abstract-parsing-helper-map">
        <xsl:param name="interface-name"/>
        <xsl:param name="helper-class-name"/>
        <xsl:param name="map-string"/>

        <xsl:variable name="this" select="."/>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="is-concrete">
            <xsl:call-template name="has-concrete-parser"/>
        </xsl:variable>
        <xsl:variable name="class-name">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:value-of
                        select="concat('Default', $interface-name)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="concat('Abstract', $interface-name)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="abstract-mod">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'"></xsl:when>
                <xsl:otherwise>abstract </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="num-mappings">
            <xsl:call-template name="number-of-mappings">
                <xsl:with-param name="map-string" select="$map-string"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;  // javadocs only
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;
import com.steelcandy.plack.common.constructs.Parser;
import com.steelcandy.plack.common.constructs.SubconstructParsingData;

/**
    The class of token ID to subconstruct parsing helper map used in
    implementing </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public </xsl:text>
        <xsl:value-of select="$abstract-mod"/>
        <xsl:text>class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-parsing-helper-map-class-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Constants

    /**
        Parsing helper singletons.
        &lt;p&gt;
        All of the parsing helpers defined in this class are stateless
        and so can be shared.
    */
</xsl:text>
        <xsl:for-each select="indented-subconstructs/*">
            <xsl:text>    protected static final </xsl:text>
            <xsl:value-of select="$helper-class-name"/>
            <xsl:text>
        </xsl:text>
            <xsl:call-template name="to-field-name">
                <xsl:with-param name="name"
                    select="concat(@type, 'ParsingHelper')"/>
            </xsl:call-template>
            <xsl:text> =
            new </xsl:text>
            <xsl:value-of select="concat(@type, 'ParsingHelper')"/>
            <xsl:text>();
</xsl:text>
        </xsl:for-each>
        <xsl:text>
    protected static final </xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text>
        _invalidSubconstructHelper = new InvalidSubconstructParsingHelper();


    // Constructors

    /**
        Constructs a map large enough to hold the number of mappings
        added to it by this class' implementation of initializeMap().

        @see AbstractTokenIdToObjectMap#initializeMap
    */
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        super(</xsl:text>
        <xsl:value-of select="$num-mappings"/>
        <xsl:text>);
    }

    /**
        Constructs a map large enough to hold the specified number of
        mappings.
        &lt;p&gt;
        This constructor exists so that it can be called by constructors
        of subclasses that override initializeMap() to add a different
        number of mappings.

        @param numEntries the number of entries/mappings that the map is
        to be able to hold
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>(int numEntries)
    {
        super(numEntries);
    }


    // Public methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>#get(int)
    */
    public </xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text> get(int tokenId)
    {
        return (</xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text>) getObject(tokenId);
    }


    // Protected methods

    /**
        @see AbstractTokenIdToObjectMap#createDefaultObject
    */
    protected Object createDefaultObject()
    {
        // Assert.ensure(result != null);
        return _invalidSubconstructHelper;
    }

    /**
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected void initializeMap()
    {</xsl:text>
        <xsl:call-template name="add-parsing-helper-mapping-code">
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
        <xsl:text>
    }

</xsl:text>
        <xsl:call-template name="create-parsing-helper-methods">
            <xsl:with-param name="helper-class-name"
                select="$helper-class-name"/>
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
        <xsl:text>

    // Inner classes
</xsl:text>
        <xsl:for-each select="indented-subconstructs/*">
            <xsl:call-template name="parsing-helper-class">
                <xsl:with-param name="construct-type"
                    select="$this/@type"/>
                <xsl:with-param name="construct-name"
                    select="$construct-name"/>
                <xsl:with-param name="following-subconstructs"
                    select="following-sibling::*"/>
            </xsl:call-template>
        </xsl:for-each>
        <xsl:text>

    /**
        The class of parser used by the InvalidSubconstructParsingHelper.
    */
    private static class InvalidSubconstructParser
        extends </xsl:text>
        <xsl:value-of select="$invalid-first-indented-subconstruct-token-parser-class-name"/>
        <xsl:text>
    {
        /**
            @see </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>#constructId
        */
        protected int constructId()
        {
            return MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>;
        }
    }

    /**
        The instance of InvalidSubconstructParser used by all instances
        of InvalidSubconstructParsingHelper.
    */
    private static final InvalidSubconstructParser
        INVALID_SUBCONSTRUCT_PARSER = new InvalidSubconstructParser();

    /**
        The class of subconstruct parsing helper used to parse
        subconstructs that start with a token that can't start a
        valid subconstruct.
    */
    private static class InvalidSubconstructParsingHelper
        implements </xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text>
    {
        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$helper-class-name"/>
        <xsl:text>#parseAndAdd(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>, SourceLocationList, Parser, ErrorHandler)
        */
        public void parseAndAdd(TrackedTokenList line, Tokenizer t,
                                SubconstructParsingData data,
                                </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                                SourceLocationList partLocations,
                                Parser p, ErrorHandler handler)
        {
            Assert.require(line != null);
            // Assert.require(p.isEmptyLine(line) == false);
            Assert.require(t != null);
            Assert.require(data != null);
            Assert.require(c != null);
            // 'partLocations' may be null
            Assert.require(p != null);
            Assert.require(handler != null);

            // Note: the parser we use always returns null.
            INVALID_SUBCONSTRUCT_PARSER.
                parseConstruct(line, t, data, handler);
        }
    }
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the source code that adds token ID to parsing helper
         mappings for each mapping in the specified map string. -->
    <xsl:template name="add-parsing-helper-mapping-code">
        <xsl:param name="map-string"/>

        <xsl:call-template name="add-token-id-mapping-code">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="to-object-name" select="'ParsingHelper'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the source code of the (possibly abstract) method
         that creates the parsing helper for each mapping in the
         specified map string. -->
    <xsl:template name="create-parsing-helper-methods">
        <xsl:param name="helper-class-name"/>
        <xsl:param name="map-string"/>

        <xsl:if test="string-length($map-string) &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="first-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="tail-map-string">
                <xsl:call-template name="tail-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output the method for the first mapping. -->
            <xsl:variable name="token-name">
                <xsl:call-template name="token-name-from-mapping">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="is-concrete">
                <xsl:call-template name="is-mapping-unique">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>
    /**
        @return the parsing helper to which to map the token ID
        of </xsl:text>
            <xsl:value-of select="$token-name"/>
            <xsl:text> tokens
    */</xsl:text>
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:variable name="construct-type">
                        <xsl:call-template name="first-construct-type">
                            <xsl:with-param name="construct-types">
                                <xsl:call-template
                                    name="construct-types-from-mapping">
                                    <xsl:with-param name="mapping"
                                        select="$first"/>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:text>
    protected </xsl:text>
                    <xsl:value-of select="$helper-class-name"/>
                    <xsl:text>
        create</xsl:text>
                    <xsl:value-of select="$token-name"/>
                    <xsl:text>ParsingHelper()
    {
        // Assert.ensure(result != null);
        return </xsl:text>
        <xsl:call-template name="to-field-name">
            <xsl:with-param name="name"
                select="concat($construct-type, 'ParsingHelper;')"/>
        </xsl:call-template>
        <xsl:text>
    }
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>
    protected abstract </xsl:text>
                    <xsl:value-of select="$helper-class-name"/>
                    <xsl:text>
        create</xsl:text>
                    <xsl:value-of select="$token-name"/>
                    <xsl:text>ParsingHelper();
        // Assert.ensure(result != null);</xsl:text>
                </xsl:otherwise>
            </xsl:choose>

            <!-- Output the methods for the second and subsequent
                 mappings. -->
            <xsl:call-template name="create-parsing-helper-methods">
                <xsl:with-param name="helper-class-name"
                    select="$helper-class-name"/>
                <xsl:with-param name="map-string" select="$tail-map-string"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the source code for the interface implemented by
         subconstruct parsing helper classes that parse a specific type
         of subconstruct of the type of multiline construct described by
         the current node and then add the subconstruct to the construct. -->
    <xsl:template name="parsing-helper-interface">
        <xsl:param name="construct-name"/>
        <xsl:param name="class-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.tokens.TrackedTokenList;
import com.steelcandy.plack.common.constructs.Parser;
import com.steelcandy.plack.common.constructs.SubconstructParsingData;

/**
    The interface implemented by classes that parse subconstructs of
    </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> constructs and then add them to the
    construct.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$base-parsing-helper-interface-name"/>
        <xsl:text>
{
    // Public methods

    /**
        Parses one </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> subconstruct of
        the specified construct and adds it to the construct.

        @param line the as yet unparsed tokens that represent the current
        line being parsed
        @param t the tokenizer containing the tokens that represent the lines
        following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param c the construct that the subconstruct is to be added to once
        the subconstruct has been parsed
        @param p the parser of 'c', on whose behalf the subconstruct is being
        parsed
        @param partLocations the locations of the parts of 'c'
        @param handler the error handler to use to handle any errors that
        occur during parsing
    */
    public void parseAndAdd(TrackedTokenList line, Tokenizer t,
                            SubconstructParsingData data,
                            </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                            SourceLocationList partLocations,
                            Parser p, ErrorHandler handler);
        // Assert.require(line != null);
        // Assert.require(line.isEmpty() == false);
        // Assert.require(t != null);
        // Assert.require(data != null);
        // Assert.require(c != null);
        // 'partLocations' may be null
        // Assert.require(p != null);
        // Assert.require(handler != null);
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the source code for the subconstruct parsing helper
         that parses an instance of the type of subconstruct described by
         the current node (which is a 'subconstruct' element) and adds it
         to the construct. -->
    <xsl:template name="parsing-helper-class">
        <xsl:param name="construct-type"/>
        <xsl:param name="construct-name"/>
        <xsl:param name="following-subconstructs"/>

        <xsl:variable name="interface-name"
            select="concat($language-name, $construct-type,
                           'ParsingHelper')"/>
        <xsl:variable name="subconstruct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="subconstruct-construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="subconstruct-parser-name">
            <xsl:call-template name="parser-class-name">
                <xsl:with-param name="construct-type" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        The class of subconstruct parsing helper that parses one
        </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> subconstruct of a given
        </xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text> construct and adds
        the subconstruct to the construct.
    */
    private static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>ParsingHelper
        implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
    {
        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>#parseAndAdd(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>, Parser, ErrorHandler)
        */
        public void parseAndAdd(TrackedTokenList line, Tokenizer t,
                                SubconstructParsingData data,
                                </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                                SourceLocationList partLocations,
                                Parser p, ErrorHandler handler)
        {
            Assert.require(line != null);
            Assert.require(line.isEmpty() == false);
            Assert.require(t != null);
            Assert.require(data != null);
            Assert.require(c != null);
            // 'partLocations' may be null
            Assert.require(p != null);
            Assert.require(handler != null);

            </xsl:text>
        <xsl:value-of select="$subconstruct-parser-name"/>
        <xsl:text> parser =
                PARSER_FACTORY.create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Parser();
            </xsl:text>
        <xsl:value-of select="$subconstruct-construct-name"/>
        <xsl:text>
                subconstruct = parser.parse(line, t, data, handler);
            if (subconstruct != null)
            {</xsl:text>
        <xsl:if test="count($following-subconstructs) &gt; 0">
            <xsl:text>
                checkSubconstructsOrder(c, subconstruct, p, t, handler);</xsl:text>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@number = 'zero-or-more' or
                             @number = 'one-or-more'">
                <xsl:text>
                c.add</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>(subconstruct);</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
                c.set</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>(subconstruct);</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
                if (partLocations != null)
                {
                    partLocations.add(subconstruct.location());
                }
            }
        }</xsl:text>
        <xsl:if test="count($following-subconstructs) &gt; 0">
            <xsl:text>


        // Protected methods

        /**
            Checks that a subconstruct parsed by this helper appears
            in the proper place in the sequence of subconstructs. If
            not then an error is reported.

            @param c the construct whose indented subconstructs' order
            is to be checked
            @param p the parser that this helper is helping
            @param t the tokenizer that is the source of the tokens being
            parsed
            @param handler the error handler to use to report an error
            if the subconstruct isn't in the proper place
        */
        protected void checkSubconstructsOrder(
            </xsl:text>
            <xsl:value-of select="$construct-name"/>
            <xsl:text> c,
            </xsl:text>
            <xsl:value-of select="$subconstruct-construct-name"/>
            <xsl:text> subconstruct,
            Parser p, Tokenizer t, ErrorHandler handler)
        {
            Assert.require(c != null);
            Assert.require(subconstruct != null);
            Assert.require(p != null);
            Assert.require(t != null);
            Assert.require(handler != null);

            </xsl:text>
            <xsl:call-template name="if-subconstructs-set">
                <xsl:with-param name="subconstructs"
                    select="$following-subconstructs"/>
            </xsl:call-template>
            <xsl:text>
            {
                String desc = MANAGER.idToDescription(subconstruct.id());
                reportMisplacedSubconstruct(desc,
                    followingSubconstructsDescription(),
                    subconstruct.location(), p, t, handler);
            }
        }


        // Private methods

        /**
            @return a description of all of the types of subconstructs
            that can validly follow the type of subconstruct parsed by
            this parsing helper
        */
        private String followingSubconstructsDescription()
        {
            return description(MANAGER.</xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name"
                    select="$following-subconstructs[1]/@type"/>
            </xsl:call-template>
            <xsl:text>)</xsl:text>

            <xsl:for-each select="$following-subconstructs[position() != 1]">
                <xsl:text> + &quot;</xsl:text>
                <xsl:choose>
                    <xsl:when test="position() = last()">
                        <xsl:text> or </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>, </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:text>&quot; +
                   </xsl:text>
                <xsl:text>description(MANAGER.</xsl:text>
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@type"/>
                </xsl:call-template>
                <xsl:text>)</xsl:text>
            </xsl:for-each>
            <xsl:text>;
        }</xsl:text>
        </xsl:if>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <!-- Output the start of the 'if' statement that checks whether
         a construct with the specified name has any of the specified
         subconstructs set. -->
    <xsl:template name="if-subconstructs-set">
        <xsl:param name="subconstructs"/>
        <xsl:param name="construct-var" select="'c'"/>

        <xsl:text>if (</xsl:text>
        <xsl:for-each select="$subconstructs">
            <xsl:variable name="subconstruct-name">
                <xsl:call-template name="subconstruct-name"/>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="@number = 'one-or-more' or
                                @number = 'zero-or-more'">
                    <xsl:variable name="as-method-name">
                        <xsl:call-template name="to-method-name">
                            <xsl:with-param name="name"
                                select="$subconstruct-name"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:value-of select="concat($construct-var, '.', $as-method-name, 'Count() &gt; 0')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($construct-var, '.has', $subconstruct-name, '()')"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="position() != last()"> ||
                <xsl:text/>
            </xsl:if>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
    </xsl:template>


    <!-- Outputs the start of the source code for the default or abstract
         parser for the type of single line or multiline construct
         described by the current node.

         This template outputs everything up to the body of the
         parseIndentedSubconstructs() method, including the parse() and
         parseFirstLine() methods. -->
    <xsl:template name="default-single-or-multiline-construct-parser-start">
        <xsl:param name="is-concrete"/>
        <xsl:param name="class-name-prefix"/>
        <xsl:param name="first-line-parts"/>

        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="class-name"
            select="concat($class-name-prefix, $parser-name)"/>

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceLocationList;

/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{</xsl:text>
        <xsl:if test="$is-concrete = 'true'">
            <xsl:text>
    // Constants

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>
        _instance = new </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
            <xsl:value-of select="$parser-name"/>
            <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected </xsl:text>
            <xsl:value-of select="$class-name"/>
            <xsl:text>()
    {
        // empty
    }

</xsl:text>
        </xsl:if>
        <xsl:text>
    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return </xsl:text>
        <xsl:call-template name="can-start-construct-implementation">
            <xsl:with-param name="parts" select="$first-line-parts"/>
        </xsl:call-template>
        <xsl:text>;
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);

        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
            result = creator.createConstruct();

        SourceLocationList partLocations =
            SourceLocationList.createArrayList();

        // Parse the first line.
        Token firstTok = line.get(0);
        Assert.check(TOKEN_MANAGER.isIndentToken(firstTok));
        data.setPreviousPart(firstTok);
        int firstIndentLevel =
                TOKEN_MANAGER.lastLineIndentLevels(firstTok);
        line.advance();  // over the indent token
        parseFirstLine(line, t, data, result, partLocations, handler);

        // Regardless of whether or not parsing the first line succeeded,
        // parse the subconstructs indented under the first line, if any.
        if (t.hasNext())
        {
            Assert.check(TOKEN_MANAGER.isIndentToken(t.peek()));
            if (firstIndentLevel &lt;
                        TOKEN_MANAGER.firstLineIndentLevels(t.peek()))
            {
                parseIndentedSubconstructs(t, data, firstIndentLevel + 1,
                                           result, partLocations, handler);
            }
        }

        finishConstruct(result, t, data,
                        locationFactory().create(partLocations), handler);

        return result;
    }


    // First line construct part parsing methods

    /**
        Parses the specified first line of the specified construct.

        @param line the token list containing the tokens that represent the
        line to be parsed
        @param t the tokenizer containing the tokens following the ones in
        'line': this method should never extract any tokens from this
        tokenizer (it is provided since it must sometimes be passed to
        sub-parsers (who also should not extract tokens from it ...))
        @param data contains information about the parsing of the construct
        as a subconstruct, including whether it was parsed successfully
        @param result represents the construct whose first line this method
        is to parse
        @param partLocations the locations of the parts of 'result'
        @param handler the error handler to use to handle any errors
    */
    protected void
        parseFirstLine(TrackedTokenList line, Tokenizer t,
                       SubconstructParsingData data,
                       </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result,
                       SourceLocationList partLocations,
                       ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(result != null);
        Assert.require(partLocations != null);
        Assert.require(handler != null);

        int startIndex = line.toAbsoluteIndex(0);
        try
        {
            parseFirstLineParts(line, t, data, result, handler);
            if (isEmptyLine(line) == false)
            {
                data.setPreviousPart(line.getLast());
                reportExtraAtEndOfLine(location(line), t, handler);
                line.advanceToEnd();
            }
        }
        catch (InvalidConstructPartException ex)
        {
            data.setWasParsedSuccessfully(false);
        }

        int pastEndIndex = line.toAbsoluteIndex(0);
        Assert.check(startIndex &lt;= pastEndIndex);
        if (startIndex &lt; pastEndIndex)
        {
            partLocations.add(location(line.
                absoluteSubList(startIndex, pastEndIndex)));
        }
    }

    /**
        Parses all of the parts of this construct's first line.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose first line's parts we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the parts
        @exception InvalidConstructPartException thrown if a part is missing
        or invalid
    */
    protected void parseFirstLineParts(TrackedTokenList line,
        Tokenizer t, SubconstructParsingData data,
        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> parent,
        ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);
</xsl:text>
        <xsl:apply-templates select="$first-line-parts"
            mode="call-parse-construct-part-method"/>
        <xsl:text>
    }
</xsl:text>
        <xsl:apply-templates select="$first-line-parts"
            mode="parse-construct-part-method">
            <xsl:with-param name="parent-construct-name" select="$construct-name"/>
        </xsl:apply-templates>
        <xsl:text>

    // Indented subconstructs parsing methods

    /**
        Parses the subconstructs of the specified construct that are
        indented under the construct's first line.
        &lt;p&gt;
        Note: this method assumes that 'result' has its location set to
        that of the construct's first line, and so this method will combine
        it with the locations of all of the indented subconstructs to build
        the location of the entire construct, and then set the location of
        'result' to that combined location.

        @param t the tokenizer that returns the tokens representing the
        indented subconstructs (and possibly any following source code)
        @param data contains information about the parsing of the construct
        as a subconstruct, including whether it was parsed successfully
        @param indentLevels the indentation level that all of the
        indented subconstructs should have
        @param result represents the construct whose first line this method
        is to parse
        @param partLocations the locations of the parts of 'result'
        @param handler the error handler to use to handle any errors
    */
    public void parseIndentedSubconstructs(Tokenizer t,
        SubconstructParsingData data, int indentLevels,
        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> result,
        SourceLocationList partLocations, ErrorHandler handler)
    {
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(indentLevels >= 1);
        Assert.require(result != null);
        Assert.require(partLocations != null);
        Assert.require(handler != null);</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for a default parser class for parsing
         the type of 'repeated-construct' described by the current node. -->
    <xsl:template name="default-repeated-construct-parser">
        <xsl:variable name="construct-name">
            <xsl:call-template name="construct-class-name"/>
        </xsl:variable>
        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="start-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@start-terminal"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="separator-id">
            <xsl:choose>
                <xsl:when test="@separator-terminal">
                    <xsl:call-template name="to-constant-name">
                        <xsl:with-param name="name" select="@separator-terminal"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise><xsl:text></xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="end-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@end-terminal"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="num-subconstructs"
            select="subconstruct[1]/@number"/>
        <xsl:variable name="subconstruct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="subconstruct[1]/@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name">
                <xsl:with-param name="node" select="subconstruct[1]"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subconstruct-class-name">
            <xsl:call-template name="construct-class-name">
                <xsl:with-param name="construct-type"
                    select="subconstruct[1]/@type"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Assumptions (which are all checked when the language
             description document is validated):
                - there is exactly one 'subconstruct' child element of
                  this repeated-construct element
                - the 'subconstruct' child element's 'number' attribute
                  is present and is either 'zero-or-more' or 'one-or-more'
                - this repeated-construct either has both of its
                  'start-terminal' and 'end-terminal' attributes present,
                  or neither is present: there cannot be just one or the
                  other
                - if there is no 'start-terminal' and no 'end-terminal'
                  then the 'subconstruct' child element must have a
                  'number' attribute of 'one-or-more' (so that the
                  repeated-construct can't be missing and still be
                  valid)
        -->

        <xsl:call-template name="parser-source-prologue">
            <xsl:with-param name="class-name"
                select="concat('Default', $parser-name)"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parser implementation.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
    extends MinimalAbstract</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
{
    // Constants

    /**
        The instance of this class returned by create().

        @see #create
    */
    private static final Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        _instance = new Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>();


    // Constructors

    /**
        Returns an instance of this class that can be used to parse
        a new construct.
        &lt;p&gt;
        This method should only be called directly by the parser
        factory (so that a different class of parser can easily be
        used in place of this one).

        @return a parser that can be used to parse a new construct
    */
    public static </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text> create()
    {
        return _instance;
    }

    /**
        Constructs a Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.
        &lt;p&gt;
        This constructor should only be called by subclasses'
        constructors: instances of this class should only be created
        using the parser factory.
    */
    protected Default</xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see Parser#canStartConstruct(Token)
    */
    public boolean canStartConstruct(Token tok)
    {
        Assert.require(tok != null);
        return </xsl:text>
        <xsl:choose>
            <xsl:when test="@start-terminal">
                <!-- It can only start with the start-terminal. -->
                <xsl:variable name="token-id">
                    <xsl:call-template name="to-constant-name">
                        <xsl:with-param name="name" select="@start-terminal"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:text>tok.id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- There is no start-terminal, so it can only start
                     with a repeated subconstruct. (If there is no start
                     terminal then the subconstruct must be repeated at
                     leat once (as checked by the language description
                     validator).) -->
                <xsl:text>PARSER_FACTORY.create</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Parser().canStartConstruct(tok)</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>;
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>#parse(TrackedTokenList, Tokenizer, SubconstructParsingData, </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>.ConstructCreator, ErrorHandler)
    */
    public </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
        parse(TrackedTokenList line, Tokenizer t,
              SubconstructParsingData data, ConstructCreator creator,
              ErrorHandler handler)
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(creator != null);
        Assert.require(handler != null);
</xsl:text>

        <xsl:if test="@end-terminal">
            <xsl:text>
        boolean isAtEnd = false;</xsl:text>
        </xsl:if>
        <xsl:text>
        int startIndex = line.toAbsoluteIndex(0);
        </xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text>
            result = creator.createConstruct();
        try
        {</xsl:text>
        <xsl:choose>
            <!-- There's a start-terminal iff there's an end-terminal. -->
            <xsl:when test="@end-terminal">
                <xsl:text>
            // Parse the starting </xsl:text>
                <xsl:value-of select="@start-terminal"/>
                <xsl:text> terminal.
            Token tok = line.get(0);
            if (tok.id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$start-id"/>
                <xsl:text>)
            {
                data.setPreviousPart(tok);
                line.advance();
            }
            else
            {
                // The starting terminal is missing or invalid.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                String terminalDescription = TOKEN_MANAGER.
                    idToDescription(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$start-id"/>
                <xsl:text>);
                handleInvalidStartingTerminal(line, data, description,
                                        terminalDescription, t, handler);
            }

            // Parse the first subconstruct.
            //
            // Note: the case where isEmptyLine(line) will be handled
            // below when we attempt to parse the ending terminal.
            if (isEmptyLine(line) == false)
            {
                int tokenId = line.get(0).id();
                if (tokenId == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$end-id"/>
                <xsl:text>)
                {</xsl:text>
                <xsl:if test="$num-subconstructs = 'one-or-more'">
                    <xsl:text>
                    // Report the missing required repeated subconstructs.
                    String description = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                    <xsl:value-of select="$construct-id"/>
                    <xsl:text>);
                    String subconstructDescription = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                    <xsl:value-of select="$subconstruct-id"/>
                    <xsl:text>);
                    handleMissingFirstSubconstruct(line, data, description,
                                    subconstructDescription, t, handler);
</xsl:text>
                </xsl:if>
                <xsl:text>
                    // Leave the ending terminal to be parsed below.
                    isAtEnd = true;
                }
                else if (tokenId == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$separator-id"/>
                <xsl:text>)
                {
                    // Note: we assume that a subconstruct cannot start with
                    //       a separator.
                    String description = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                    String subconstructDescription = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                    handleMissingFirstSubconstruct(line, data,
                        description, subconstructDescription, t, handler);
                }
                else
                {
                    // Attempt to parse the first subconstruct.
                    addFirstSubconstruct(result, line, t, data, handler);
                }
            }

            // Parse the subsequent separators and subconstructs.
            //
            // Note: we don't check for the first element of 'line'
            // being an ending terminal and then report a missing last
            // element since it is possible that the ending terminal is
            // also a valid start of a subconstruct.
            while (isAtEnd == false &amp;&amp; isEmptyLine(line) == false &amp;&amp;
                   line.get(0).id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$separator-id"/>
                <xsl:text>)
            {
                data.setPreviousPart(line.get(0));
                line.advance();  // over the separator
                if (isEmptyLine(line))
                {
                    // The subconstruct is missing.
                    String description = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                    String subconstructDescription = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                    handleMissingSubsequentSubconstruct(line, data,
                        description, subconstructDescription, t, handler);
                }
                else
                {
                    addSubsequentSubconstruct(result, line,
                                              t, data, handler);
                }
            }

            // Parse the ending </xsl:text>
                <xsl:value-of select="@end-terminal"/>
                <xsl:text> terminal.
            if (isEmptyLine(line) == false)
            {
                Token firstTok = line.get(0);
                if (firstTok.id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$end-id"/>
                <xsl:text>)
                {
                    data.setPreviousPart(firstTok);
                    line.advance();
                }
                else
                {
                    // The ending terminal is missing or invalid.
                    String description = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                    String terminalDescription = TOKEN_MANAGER.
                        idToDescription(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$end-id"/>
                <xsl:text>);
                    handleInvalidEndingTerminal(line, data, description,
                                        terminalDescription, t, handler);
                }
            }
            else
            {
                // The ending terminal is missing.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                String terminalDescription = TOKEN_MANAGER.
                    idToDescription(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$end-id"/>
                <xsl:text>);
                handleMissingEndingTerminal(line, data, description,
                                    terminalDescription, t, handler);
            }</xsl:text>
            </xsl:when>
            <xsl:when test="$separator-id = ''">
                <xsl:text>
            // Parse the repeated subconstructs, which should be one after
            // another on the same line with no (non-whitespace) separators
            // between them.
            </xsl:text>
            <xsl:call-template name="parser-class-name">
                <xsl:with-param name="construct-type"
                    select="subconstruct[1]/@type"/>
            </xsl:call-template>
            <xsl:text> subParser = PARSER_FACTORY.
                create</xsl:text>
            <xsl:value-of select="subconstruct[1]/@type"/>
            <xsl:text>Parser();
            int numSubs = 0;
            while (isEmptyLine(line) == false &amp;&amp;
                    subParser.canStartConstruct(line.get(0)))
            {
                numSubs += 1;
                if (numSubs == 1)
                {
                    addFirstSubconstruct(result, line, t, data, handler);
                }
                else
                {
                    addSubsequentSubconstruct(result, line, t, data,
                                              handler);
                }
            }
            if (numSubs == 0)
            {
                // The first subconstruct is missing.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                String subconstructDescription = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                handleMissingFirstSubconstruct(line, data, description,
                                    subconstructDescription, t, handler);
            }</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
            // Parse the first subconstruct.
            int tokenId = line.get(0).id();
            if (tokenId == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$separator-id"/>
                <xsl:text>)
            {
                // The first subconstruct is missing. (We leave the
                // separator in 'line' to be parsed out below, if we
                // continue parsing this construct.)
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                String subconstructDescription = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                handleMissingFirstSubconstruct(line, data, description,
                                    subconstructDescription, t, handler);
            }
            else
            {
                // Attempt to parse the first subconstruct.
                addFirstSubconstruct(result, line, t, data, handler);
            }

            // Parse separator-subconstruct pairs until the next
            // token isn't a separator.
            while (isEmptyLine(line) == false &amp;&amp;
                   line.get(0).id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$separator-id"/>
                <xsl:text>)
            {
                data.setPreviousPart(line.get(0));
                line.advance();  // over the separator
                if (isEmptyLine(line))
                {
                    // The subconstruct is missing.
                    String description = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$construct-id"/>
                <xsl:text>);
                    String subconstructDescription = MANAGER.
                        idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                    handleMissingSubsequentSubconstruct(line, data,
                        description, subconstructDescription, t, handler);
                }
                else
                {
                    addSubsequentSubconstruct(result, line,
                                              t, data, handler);
                }
            }</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>
        }
        catch (InvalidConstructPartException ex)
        {
            data.setWasParsedSuccessfully(false);
        }

        SourceLocation loc = null;
        int pastEndIndex = line.toAbsoluteIndex(0);
        Assert.check(startIndex &lt;= pastEndIndex);
        if (startIndex &lt; pastEndIndex)
        {
            loc = location(line.absoluteSubList(startIndex,
                                                pastEndIndex));
        }
        finishConstruct(result, t, data, loc, handler);

        Assert.ensure(result != null);
        return result;
    }


    // Protected methods

    /**
        Adds the next construct to be parsed from the specified token list
        and/or tokenizer as the first subconstruct of the construct we're
        parsing.

        @param c the construct that the subconstruct will be added to
        after it is parsed
        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param t the tokenizer containing the tokens that represent
        the lines following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
        that occur during parsing
    */
    protected void
        addFirstSubconstruct(</xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                             TrackedTokenList line, Tokenizer t,
                             SubconstructParsingData data,
                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        addSubconstruct(c, line, t, data, handler);
    }

    /**
        Adds the next construct to be parsed from the specified token list
        and/or tokenizer as the second or subsequent subconstruct of the
        construct we're parsing.

        @param c the construct that the subconstruct will be added to
        after it is parsed
        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param t the tokenizer containing the tokens that represent
        the lines following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
        that occur during parsing
    */
    protected void
        addSubsequentSubconstruct(</xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                                  TrackedTokenList line, Tokenizer t,
                                  SubconstructParsingData data,
                                  ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        addSubconstruct(c, line, t, data, handler);
    }

    /**
        Adds the next construct to be parsed from the specified token list
        and/or tokenizer as the next subconstruct of the construct we're
        parsing.

        @param c the construct that the subconstruct will be added to
        after it is parsed
        @param line the as yet unparsed tokens that represent (part of)
        the current line being parsed
        @param t the tokenizer containing the tokens that represent
        the lines following the one currently being parsed
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param handler the error handler to use to handle any errors
        that occur during parsing
    */
    protected void
        addSubconstruct(</xsl:text>
        <xsl:value-of select="$construct-name"/>
        <xsl:text> c,
                        TrackedTokenList line, Tokenizer t,
                        SubconstructParsingData data,
                        ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(handler != null);

        c.add</xsl:text>
        <xsl:value-of select="$subconstruct-name"/>
        <xsl:text>(PARSER_FACTORY.
            create</xsl:text>
        <xsl:value-of select="$subconstruct-name"/>
        <xsl:text>Parser().
                parse(line, t, data, handler));
    }


    // Error handler methods
</xsl:text>
        <xsl:if test="@start-terminal">
            <xsl:text>
    /**
        Handles the starting terminal being missing or invalid.

        @param line the list of tokens whose first token was supposed to be
        the starting terminal, but is something else instead
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the repeated construct
        whose starting terminal is invalid or missing
        @param terminalDescription a description of the starting terminal
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the terminal is
        invalid or missing and it may not be possible to parse any following
        construct parts
    */
    protected void handleInvalidStartingTerminal(TrackedTokenList line,
        SubconstructParsingData data, String constructDescription,
        String terminalDescription, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidRepeatedConstructStartTerminal(constructDescription,
            terminalDescription, line.get(0).location(), t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        </xsl:if>
        <xsl:if test="@end-terminal">
            <xsl:text>
    /**
        Handles the ending terminal being missing or invalid.

        @param line the list of tokens whose first token was supposed to be
        the ending terminal, but is something else instead
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the repeated construct
        whose ending terminal is invalid or missing
        @param terminalDescription a description of the ending terminal
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the terminal is
        invalid or missing and it may not be possible to parse any following
        construct parts
    */
    protected void handleInvalidEndingTerminal(TrackedTokenList line,
        SubconstructParsingData data, String constructDescription,
        String terminalDescription, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidRepeatedConstructEndingTerminal(constructDescription,
            terminalDescription, line.get(0).location(), t, handler);
        throw InvalidConstructPartException.instance();
    }

    /**
        Reports a repeated construct's ending terminal being missing.

        @param line the list of tokens whose first token was supposed to be
        the ending terminal (but is empty instead)
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the repeated construct
        whose ending terminal is missing
        @param terminalDescription a description of the ending terminal
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the terminal is
        missing and it may not be possible to parse any following construct
        parts
    */
    protected void handleMissingEndingTerminal(TrackedTokenList line,
        SubconstructParsingData data, String constructDescription,
        String terminalDescription, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(terminalDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: we don't just always use 'data.previousLocation()' since
        // the previous token could give us a better location in some
        // cases (e.g. the indent token before the terminal at the start
        // of a line (where data.previousLocation() would give us the
        // location of the preceding line at best)).
        SourceLocation loc = null;
        Token previousToken = line.lastAdvancedOver();
        if (previousToken != null)
        {
            loc = previousToken.location();
        }
        if (loc == null)
        {
            loc = data.previousLocation();
        }
        Assert.check(loc != null);

        reportMissingRepeatedConstructEndingTerminal(constructDescription,
            terminalDescription, loc, t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        </xsl:if>
        <xsl:text>
    /**
        Handles a missing first subconstruct of a repeated construct.

        @param line the list of tokens whose first token was supposed to be
        the start of the first repeated subconstruct, or whose next token was
        supposed to be the start of the first repeated subconstruct if this
        list is empty
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the repeated
        construct whose first subconstruct is missing
        @param subconstructDescription a description of the missing
        subconstruct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the subconstruct
        is missing and it may not be possible to parse any following
        construct parts
    */
    protected void handleMissingFirstSubconstruct(TrackedTokenList line,
        SubconstructParsingData data, String constructDescription,
        String subconstructDescription, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        // 'line' may or may not be empty
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(subconstructDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Use the location of the next token if there is one; otherwise
        // use the location of the current token, if there is one. (Note
        // that we intentionally use line.isEmpty() instead of
        // line.isEmptyLine() since an end-of-line token is a better
        // location that none at all.)
        //
        // Note: we don't just always use 'data.previousLocation()' since
        // the previous token could give us a better location in some
        // cases (e.g. the indent token before the terminal at the start
        // of a line (where data.previousLocation() would give us the
        // location of the preceding line at best)).
        SourceLocation loc = null;
        if (line.isEmpty() == false)
        {
            loc = line.get(0).location();
        }
        else
        {
            Token previousToken = line.lastAdvancedOver();
            if (previousToken != null)
            {
                loc = previousToken.location();
            }
        }
        if (loc == null)
        {
            loc = data.previousLocation();
        }
        Assert.check(loc != null);

        reportMissingRepeatedConstructFirstSubconstruct(
            constructDescription, subconstructDescription,
            loc, t, handler);
        throw InvalidConstructPartException.instance();
    }

    /**
        Handles a missing second or subsequent subconstruct of a
        repeated construct.

        @param line the list of tokens whose first token was supposed to be
        the start of a subconstruct (but which is empty)
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param constructDescription a description of the repeated
        construct whose subconstruct is missing
        @param subconstructDescription a description of the missing
        subconstruct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the subconstruct
        is missing and it may not be possible to parse any following
        construct parts
    */
    protected void
        handleMissingSubsequentSubconstruct(TrackedTokenList line,
            SubconstructParsingData data, String constructDescription,
            String subconstructDescription, Tokenizer t,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(data != null);
        Assert.require(constructDescription != null);
        Assert.require(subconstructDescription != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        Assert.check(line.lastAdvancedOver() != null);
            // since there was a separator before this
        reportMissingRepeatedConstructSubsequentSubconstruct(
            constructDescription, subconstructDescription,
            line.lastAdvancedOver().location(), t, handler);
        throw InvalidConstructPartException.instance();
    }
}
</xsl:text>
    </xsl:template>


    <!-- ######################################################### -->
    <!-- # Templates that output token ID to parser map classes. # -->
    <!-- ######################################################### -->

    <!-- Outputs the source code for the token ID to parser map
         described by the specified map string, and for use by the
         parser of the construct described by the current element. -->
    <xsl:template name="parser-map">
        <xsl:param name="map-string"/>

        <xsl:variable name="construct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="is-concrete">
            <xsl:call-template name="has-concrete-parser"/>
        </xsl:variable>
        <xsl:variable name="abstract-mod">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'false'">abstract </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="map-class-name">
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:value-of
                        select="concat('Default', $parser-name, 'Map')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="concat('Abstract', $parser-name, 'Map')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="num-mappings">
            <xsl:call-template name="number-of-mappings">
                <xsl:with-param name="map-string" select="$map-string"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$map-class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.tokens.AbstractTokenIdToObjectMap;  // javadocs only
import com.steelcandy.plack.common.constructs.AbstractTokenIdToParserMap;
import com.steelcandy.plack.common.constructs.Parser;

/**</xsl:text>
        <xsl:choose>
            <xsl:when test="$is-concrete = 'true'">
                <xsl:text>
    The default token ID to parser map used in implementing
    </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> parsers.
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
    An abstract base class for token ID to parser maps used in
    implementing </xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text> parsers.
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public </xsl:text>
        <xsl:value-of select="$abstract-mod"/>
        <xsl:text>class </xsl:text>
        <xsl:value-of select="$map-class-name"/>
        <xsl:text>
    extends AbstractTokenIdToParserMap
    implements </xsl:text>
        <xsl:value-of select="$base-parser-map-interface-name"/>
        <xsl:text>
{
    // Constants

    /**
        The parser returned by default for those token IDs for which
        there is no explicit mapping to a parser.
    */
    private static final Parser DEFAULT_PARSER = new DefaultParser();


    // Constructors

    /**
        Constructs a map large enough to hold the number of mappings
        added to it by this class' implementation of initializeMap().

        @see AbstractTokenIdToObjectMap#initializeMap
    */
    public </xsl:text>
        <xsl:value-of select="$map-class-name"/>
        <xsl:text>()
    {
        super(</xsl:text>
        <xsl:value-of select="$num-mappings"/>
        <xsl:text>);
    }

    /**
        Constructs a map large enough to hold the specified number of
        mappings.
        &lt;p&gt;
        This constructor exists so that it can be called by constructors
        of subclasses that override initializeMap() to add a different
        number of mappings.

        @param numEntries the number of entries/mappings that the map is
        to be able to hold
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected </xsl:text>
        <xsl:value-of select="$map-class-name"/>
        <xsl:text>(int numEntries)
    {
        super(numEntries);
    }


    // Protected methods

    /**
        @see AbstractTokenIdToObjectMap#createDefaultObject
    */
    protected Object createDefaultObject()
    {
        return DEFAULT_PARSER;
    }

    /**
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected void initializeMap()
    {</xsl:text>
        <xsl:call-template name="add-parser-mapping-code">
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
        <xsl:text>
    }
</xsl:text>
        <xsl:call-template name="create-parser-methods">
            <xsl:with-param name="map-string" select="$map-string"/>
        </xsl:call-template>
        <xsl:text>

    // Inner classes

    /**
        The class of this map's default parser.
    */
    private static class DefaultParser
        extends </xsl:text>
        <xsl:value-of select="$invalid-first-choice-token-parser-class-name"/>
        <xsl:text>
    {
        /**
            @see </xsl:text>
        <xsl:value-of select="$invalid-first-token-parser-class-name"/>
        <xsl:text>#constructId
        */
        protected int constructId()
        {
            return MANAGER.</xsl:text>
        <xsl:value-of select="$construct-id"/>
        <xsl:text>;
        }
    }
}</xsl:text>
        <xsl:if test="$is-concrete = 'false'">
            <xsl:call-template name="default-parser-map-skeleton">
                <xsl:with-param name="map-string" select="$map-string"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the source code that adds token ID to parser mappings
         for each mapping in the specified map string. -->
    <xsl:template name="add-parser-mapping-code">
        <xsl:param name="map-string"/>

        <xsl:call-template name="add-token-id-mapping-code">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="to-object-name" select="'Parser'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the source code that adds token ID to object mappings
         for each mapping in the specified map string. -->
    <xsl:template name="add-token-id-mapping-code">
        <xsl:param name="map-string"/>
        <xsl:param name="to-object-name"/>

        <xsl:if test="string-length($map-string) &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="first-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="tail-map-string">
                <xsl:call-template name="tail-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output the code for the first mapping. -->
            <xsl:variable name="token-name">
                <xsl:call-template name="token-name-from-mapping">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="token-id">
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="$token-name"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>
        addMapping(TOKEN_MANAGER.</xsl:text>
            <xsl:value-of select="$token-id"/>
            <xsl:text>,
                   create</xsl:text>
            <xsl:value-of select="concat($token-name, $to-object-name, '()')"/>
            <xsl:text>);</xsl:text>

            <!-- Output the code for the second and subsequent mappings. -->
            <xsl:call-template name="add-token-id-mapping-code">
                <xsl:with-param name="map-string" select="$tail-map-string"/>
                <xsl:with-param name="to-object-name"
                    select="$to-object-name"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the source code of the (possibly abstract) method
         that creates the parser for each mapping in the specified
         map string. -->
    <xsl:template name="create-parser-methods">
        <xsl:param name="map-string"/>

        <xsl:if test="string-length($map-string) &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="first-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="tail-map-string">
                <xsl:call-template name="tail-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output the method for the first mapping. -->
            <xsl:variable name="token-name">
                <xsl:call-template name="token-name-from-mapping">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="is-concrete">
                <xsl:call-template name="is-mapping-unique">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>
    /**
        @return the (prototype) parser to which to map the token ID
        of </xsl:text>
            <xsl:value-of select="$token-name"/>
            <xsl:text> tokens
    */</xsl:text>
            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:variable name="construct-type">
                        <xsl:call-template name="first-construct-type">
                            <xsl:with-param name="construct-types">
                                <xsl:call-template
                                    name="construct-types-from-mapping">
                                    <xsl:with-param name="mapping"
                                        select="$first"/>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:variable>
                        <xsl:text>
    protected Parser create</xsl:text>
                        <xsl:value-of select="$token-name"/>
                        <xsl:text>Parser()
    {
        // Assert.ensure(result != null);
        return PARSER_FACTORY.
            create</xsl:text>
                        <xsl:value-of select="$construct-type"/>
                        <xsl:text>Parser();
    }
</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>
    protected abstract Parser create</xsl:text>
                    <xsl:value-of select="$token-name"/>
                    <xsl:text>Parser();
        // Assert.ensure(result != null);
</xsl:text>
                </xsl:otherwise>
            </xsl:choose>

            <!-- Output the methods for the second and subsequent
                 mappings. -->
            <xsl:call-template name="create-parser-methods">
                <xsl:with-param name="map-string" select="$tail-map-string"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the skeleton for the default parser map class for
         use in parsing the type of construct described by the current
         node. -->
    <xsl:template name="default-parser-map-skeleton">
        <xsl:param name="map-string"/>

        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>
        <xsl:variable name="class-name"
            select="concat('Custom', $parser-name, 'Map')"/>
        <xsl:variable name="superclass-name"
            select="concat('Abstract', $parser-name, 'Map')"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.constructs.*;

/**
    The token ID to parser map used in implementing custom
    </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> parsers.
</xsl:text>
        <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass-name"/>
        <xsl:text>
{
    // Constructors

    /**
        Constructs a map large enough to hold the number of mappings
        added to it by the inherited version of initializeMap().

        @see AbstractTokenIdToObjectMap#initializeMap
    */
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        super();
    }

    /**
        Constructs a map large enough to hold the specified number
        of mappings.
        &lt;p&gt;
        This constructor exists so that it can be called by constructors
        of subclasses that override initializeMap() to add a different
        number of mappings.

        @param numEntries the number of entries/mappings that the map
        is to be able to hold
        @see AbstractTokenIdToObjectMap#initializeMap
    */
    protected </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>(int numEntries)
    {
        super(numEntries);
    }


    // Protected methods</xsl:text>
    <xsl:call-template name="create-skeleton-parser-methods">
        <xsl:with-param name="map-string" select="$map-string"/>
        <xsl:with-param name="superclass-name" select="$superclass-name"/>
    </xsl:call-template>
    <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the skeleton of the source code of the method that
         creates the parser for each non-unique mapping in the specified
         map string. -->
    <xsl:template name="create-skeleton-parser-methods">
        <xsl:param name="map-string"/>
        <xsl:param name="superclass-name"/>

        <xsl:if test="string-length($map-string) &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="first-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="tail-map-string">
                <xsl:call-template name="tail-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output the method for the first mapping. -->
            <xsl:variable name="token-name">
                <xsl:call-template name="token-name-from-mapping">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="is-concrete">
                <xsl:call-template name="is-mapping-unique">
                    <xsl:with-param name="mapping" select="$first"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:if test="$is-concrete = 'false'">
                <xsl:text>

    /**
        @see </xsl:text>
                <xsl:value-of select="$superclass-name"/>
                <xsl:text>#create</xsl:text>
                <xsl:value-of select="$token-name"/>
                <xsl:text>Parser
    */
    protected Parser create</xsl:text>
                <xsl:value-of select="$token-name"/>
                <xsl:text>Parser()
    {
        // Assert.ensure(result != null);
        return ???;
    }</xsl:text>
            </xsl:if>

        <!-- Output the methods for the second and subsequent
             mappings. -->
            <xsl:call-template name="create-skeleton-parser-methods">
                <xsl:with-param name="map-string" select="$tail-map-string"/>
                <xsl:with-param name="superclass-name"
                    select="$superclass-name"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- ######################################################## -->
    <!-- # Templates that output parts of canStartConstruct()   # -->
    <!-- # method implementations.                              # -->
    <!-- ######################################################## -->

    <xsl:template name="can-start-construct-implementation">
        <xsl:param name="parts"/>

        <xsl:choose>
            <xsl:when test="count($parts) &gt; 0">
                <xsl:variable name="first" select="$parts[1]"/>
                <xsl:variable name="rest"
                    select="$parts[position() != 1]"/>

                <xsl:apply-templates select="$first"
                    mode="can-start-construct">
                    <xsl:with-param name="following-parts" select="$rest"/>
                    <xsl:with-param name="position" select="1"/>
                    <xsl:with-param name="code" select="''"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>false</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Ignore spaces and construct attributes, skipping to the next
         construct part. -->
    <xsl:template match="space | attribute" mode="can-start-construct">
        <xsl:param name="following-parts"/>
        <xsl:param name="position"/>
        <xsl:param name="code"/>

        <xsl:choose>
            <xsl:when test="count($following-parts) &gt; 0">
                <xsl:variable name="first" select="$following-parts[1]"/>
                <xsl:variable name="rest"
                    select="$following-parts[position() != 1]"/>

                <!-- Note that this element doesn't count in
                     calculating 'position'. -->
                <xsl:apply-templates select="$first"
                    mode="can-start-construct">
                    <xsl:with-param name="following-parts" select="$rest"/>
                    <xsl:with-param name="position" select="$position"/>
                    <xsl:with-param name="code" select="$code"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$code"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="terminal" mode="can-start-construct">
        <xsl:param name="following-parts"/>
        <xsl:param name="position"/>
        <xsl:param name="code"/>

        <xsl:variable name="token-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="add-code">
            <xsl:if test="$position != 1">
                <xsl:text> ||
            </xsl:text>
            </xsl:if>
            <xsl:text>tok.id() == TOKEN_MANAGER.</xsl:text>
            <xsl:value-of select="$token-id"/>
        </xsl:variable>
        <xsl:variable name="new-code" select="concat($code, $add-code)"/>
        <xsl:variable name="optional">
            <xsl:call-template name="is-optional"/>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="count($following-parts) &gt; 0 and
                            $optional = 'true'">
                <xsl:variable name="first" select="$following-parts[1]"/>
                <xsl:variable name="rest"
                    select="$following-parts[position() != 1]"/>

                <xsl:apply-templates select="$first"
                    mode="can-start-construct">
                    <xsl:with-param name="following-parts" select="$rest"/>
                    <xsl:with-param name="position"
                        select="$position + 1"/>
                    <xsl:with-param name="code" select="$new-code"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-code"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="can-start-construct">
        <xsl:param name="following-parts"/>
        <xsl:param name="position"/>
        <xsl:param name="code"/>

        <xsl:variable name="add-code">
            <xsl:if test="$position != 1">
                <xsl:text> ||
            </xsl:text>
            </xsl:if>
            <xsl:text>MANAGER.is</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>(tok.id())</xsl:text>
        </xsl:variable>
        <xsl:variable name="new-code" select="concat($code, $add-code)"/>
        <xsl:variable name="optional">
            <xsl:call-template name="is-optional"/>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="count($following-parts) &gt; 0 and
                            $optional = 'true'">
                <xsl:variable name="first" select="$following-parts[1]"/>
                <xsl:variable name="rest"
                    select="$following-parts[position() != 1]"/>

                <xsl:apply-templates select="$first"
                    mode="can-start-construct">
                    <xsl:with-param name="following-parts" select="$rest"/>
                    <xsl:with-param name="position"
                        select="$position + 1"/>
                    <xsl:with-param name="code" select="$new-code"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-code"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct" mode="can-start-construct">
        <xsl:param name="following-parts"/>
        <xsl:param name="position"/>
        <xsl:param name="code"/>

        <xsl:variable name="add-code">
            <xsl:if test="$position != 1">
                <xsl:text> ||
            </xsl:text>
            </xsl:if>
            <xsl:text>PARSER_FACTORY.create</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>Parser().canStartConstruct(tok)</xsl:text>
        </xsl:variable>
        <xsl:variable name="new-code" select="concat($code, $add-code)"/>
        <xsl:variable name="optional">
            <xsl:call-template name="is-optional"/>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="count($following-parts) &gt; 0 and
                            $optional = 'true'">
                <xsl:variable name="first" select="$following-parts[1]"/>
                <xsl:variable name="rest"
                    select="$following-parts[position() != 1]"/>

                <xsl:apply-templates select="$first"
                    mode="can-start-construct">
                    <xsl:with-param name="following-parts" select="$rest"/>
                    <xsl:with-param name="position"
                        select="$position + 1"/>
                    <xsl:with-param name="code" select="$new-code"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-code"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- ######################################################## -->
    <!-- # Templates to generate the definitions of methods to  # -->
    <!-- # parse construct parts, and templates to generate     # -->
    <!-- # calls to those methods.                              # -->
    <!-- ######################################################## -->

    <!-- Ignore spaces as construct parts. -->
    <xsl:template match="space" mode="call-parse-construct-part-method"/>
    <xsl:template match="space" mode="parse-construct-part-method"/>

    <!-- Ignore construct attributes as construct parts. -->
    <xsl:template match="attribute" mode="call-parse-construct-part-method"/>
    <xsl:template match="attribute" mode="parse-construct-part-method"/>

    <xsl:template match="terminal" mode="call-parse-construct-part-method">
        <xsl:text>
        parse</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(line, t, data, parent, handler);</xsl:text>
    </xsl:template>

    <xsl:template match="terminal" mode="parse-construct-part-method">
        <xsl:param name="parent-construct-name"/>

        <xsl:variable name="token-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Note: @number cannot be invalid, 'zero-or-more' or
             'one-or-more' - they are checked for when the language
             description is validated. -->
        <xsl:choose>
            <xsl:when test="not(@number) or @number = 'one'">
                <xsl:text>
    /**
        Parses the required </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose terminal part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the terminal
        @exception InvalidConstructPartException thrown if the terminal part
        is missing or invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Terminal(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false)
        {
            Token tok = line.get(0);
            if (tok.id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
                <xsl:text>)
            {
                data.setPreviousPart(tok);
                line.advance();
            }
            else
            {
                // The </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal is missing or invalid.
                String description = TOKEN_MANAGER.
                    idToDescription(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
                <xsl:text>);
                handleInvalid</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Terminal(line, data, description, t, handler);
            }
        }
        else
        {
            // The </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal is missing.
            String description = TOKEN_MANAGER.
                idToDescription(TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
                <xsl:text>);
            handleMissing</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Terminal(line, data, description, t, handler);
        }
    }
</xsl:text>
            </xsl:when>

            <!-- Note: we only provide handler methods for optional terminals
                 (and not optional constructs, for example) because there is
                 no easy way to tell later whether a terminal was present or
                 not. These handler methods can be used to set construct
                 attributes (or whatever) when necessary. -->
            <xsl:when test="@number = 'zero-or-one'">
                <xsl:text>
    /**
        Parses the optional </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose terminal part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the terminal
        @exception InvalidConstructPartException thrown if the terminal part
        is invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Terminal(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false)
        {
            Token tok = line.get(0);
            if (tok.id() == TOKEN_MANAGER.</xsl:text>
                <xsl:value-of select="$token-id"/>
                <xsl:text>)
            {
                handleOptional</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>TerminalPresent(tok, parent, handler);
                data.setPreviousPart(tok);
                line.advance();
            }
            else
            {
                handleOptional</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>TerminalAbsent(parent, handler);
            }
        }
    }

    /**
        Handles the case where our optional </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal is present.
        &lt;p&gt;
        This implementation does nothing.

        @param tok the token that represents the optional terminal that is
        present
        @param parent the construct that the terminal was a part of
        @param handler the error handler to use to handle any errors that
        occur in parsing the terminal
    */
    protected void handleOptional</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>TerminalPresent(Token tok,
        </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
        ErrorHandler handler)
    {
        Assert.require(tok != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        // empty
    }

    /**
        Handles the case where our optional </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> terminal is absent.
        &lt;p&gt;
        This implementation does nothing.

        @param parent the construct that the terminal, if present, would
        have been a part of
        @param handler the error handler to use to handle any errors that
        occur in parsing the terminal
    */
    protected void handleOptional</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>TerminalAbsent(
        </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
        ErrorHandler handler)
    {
        Assert.require(parent != null);
        Assert.require(handler != null);

        // empty
    }
</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="flag-from-set" mode="call-parse-construct-part-method">
        <xsl:text>
        parse</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Flag(line, t, data, parent, handler);</xsl:text>
    </xsl:template>

    <!-- Note: the code output to handle the flag being missing will never
         get called if the flag is at the start of a construct (but doesn't
         do any harm). -->
    <xsl:template match="flag-from-set" mode="parse-construct-part-method">
        <xsl:param name="parent-construct-name"/>

        <xsl:variable name="predicate">
            <xsl:value-of select="concat('is', @name)"/>
        </xsl:variable>
        <xsl:variable name="set-desc">
            <xsl:call-template name="construct-flag-set-description">
                <xsl:with-param name="set-name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not(@number) or @number = 'one'">
                <xsl:text>
    /**
        Parses the required </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> flag,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose flag part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the flag
        @exception InvalidConstructPartException thrown if the flag part is
        missing or invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Flag(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false)
        {
            Token tok = line.get(0);
            int tokenId = tok.id();
            if (MANAGER.</xsl:text>
                <xsl:value-of select="$predicate"/>
                <xsl:text>(tokenId))
            {
                parent.set</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>(
                    MANAGER.tokenIdToConstructFlag(tokenId));
                data.setPreviousPart(tok);
                line.advance();
            }
            else
            {
                // The </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> flag is missing or invalid.
                String description = &quot;</xsl:text>
                <xsl:value-of select="$set-desc"/>
                <xsl:text>&quot;;
                handleInvalid</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Flag(line, data, description, t, handler);
            }
        }
        else
        {
            // The </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> flag is missing.
            String description = &quot;</xsl:text>
                <xsl:value-of select="$set-desc"/>
                <xsl:text>&quot;;
            handleMissing</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Flag(line, data, description, t, handler);
        }
    }
</xsl:text>
            </xsl:when>
            <xsl:when test="@number = 'zero-or-one'">
                <xsl:text>
    /**
        Parses the optional </xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text> flag,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose flag part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the flag
        @exception InvalidConstructPartException thrown if the flag part is
        invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>Flag(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false)
        {
            Token tok = line.get(0);
            int tokenId = tok.id();
            if (MANAGER.</xsl:text>
                <xsl:value-of select="$predicate"/>
                <xsl:text>(tokenId))
            {
                parent.set</xsl:text>
                <xsl:value-of select="@name"/>
                <xsl:text>(
                    MANAGER.tokenIdToConstructFlag(tokenId));
                data.setPreviousPart(tok);
                line.advance();
            }
        }
    }
</xsl:text>
            </xsl:when>
            <!-- Note: the language description validator flags all other
                 values of '@number' as invalid, and so are assumed not
                 to be able to appear here. -->
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct" mode="call-parse-construct-part-method">
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>

        <xsl:text>
        parse</xsl:text>
        <xsl:value-of select="$subconstruct-name"/>
        <xsl:text>Subconstruct(line, t, data, parent, handler);</xsl:text>
    </xsl:template>

    <!-- Note: the code output to handle the subconstruct being missing
         will never get called if the subconstruct is at the start of a
         construct (but doesn't do any harm). -->
    <xsl:template match="subconstruct" mode="parse-construct-part-method">
        <xsl:param name="number" select="@number"/>
        <xsl:param name="parent-construct-name"/>

        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="subconstruct-id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not($number) or $number = 'one'">
                <xsl:text>
    /**
        Parses the required </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose subconstruct part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the subconstruct
        @exception InvalidConstructPartException thrown if the subconstruct
        part is missing or invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false)
        {
            boolean oldSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(true);
            </xsl:text>
                <xsl:value-of select="concat($construct-manager-class-name, '.',
                                             @type, ' subconstruct =')"/>
                <xsl:text>
                PARSER_FACTORY.create</xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text>Parser().
                    parse(line, t, data, handler);
            parent.set</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>(subconstruct);

            boolean newSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(oldSuccess &amp;&amp; newSuccess);
            if (newSuccess == false)
            {
                // The </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct is
                // missing or invalid.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                handleInvalid</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(
                    line, data, subconstruct, description, t, handler);
            }
        }
        else
        {
            // The </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct is missing.
            String description = MANAGER.
                idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
            handleMissing</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(line, data, description, t, handler);
        }
    }
</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-one'">
                <xsl:text>
    /**
        Parses the optional </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct,
        adding any resulting information to the specified parent construct.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose subconstruct part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the subconstruct
        @exception InvalidConstructPartException thrown if the subconstruct
        part is invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        if (isEmptyLine(line) == false &amp;&amp;
            PARSER_FACTORY.create</xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text>Parser().
                canStartConstruct(line.get(0)))
        {
            boolean oldSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(true);
            </xsl:text>
                <xsl:value-of select="concat($construct-manager-class-name, '.',
                                             @type, ' subconstruct =')"/>
                <xsl:text>
                PARSER_FACTORY.create</xsl:text>
                <xsl:value-of select="@type"/>
                <xsl:text>Parser().
                    parse(line, t, data, handler);
            parent.set</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>(subconstruct);

            boolean newSuccess = data.wasParsedSuccessfully();
            data.setWasParsedSuccessfully(oldSuccess &amp;&amp; newSuccess);
            if (newSuccess == false)
            {
                // The </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct is
                // missing or invalid.
                // Note: 'subconstruct' may be null.
                String description = MANAGER.
                    idToDescription(MANAGER.</xsl:text>
                <xsl:value-of select="$subconstruct-id"/>
                <xsl:text>);
                handleInvalid</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(
                    line, data, subconstruct, description, t, handler);
            }
        }
    }
</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-more' or
                            $number = 'one-or-more'">
                <xsl:message terminate="no">
A valid parser cannot currently be generated for constructs that
have a subconstruct with a 'number' attribute with the value
'<xsl:value-of select="$number"/>'. There is a subconstruct named
'<xsl:value-of select="$subconstruct-name"/>' that is such a subconstruct,
and so a custom parser will have to be written for the type of
construct of which it is a subconstruct. (The generated parser
for the subconstruct's construct will NOT be valid.)
                </xsl:message>

                <xsl:text>
    /**
        Parses the </xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text> subconstruct,
        adding any resulting information to the specified parent construct.
        &lt;p&gt;
        NOTE: this method is INVALID, and so should NOT be used. As indicated
        when this class' source code was generated, a custom parser must be
        implemented: this default one cannot be used.

        @param line the list of tokens representing the first line of 'parent'
        @param t the tokenizer that returns the tokens after the ones in 'line'
        @param data the data used in parsing 'parent' and its parts
        @param parent the construct whose subconstruct part we're parsing
        @param handler the error handler to use to handle any errors that
        occur in parsing the subconstruct
        @exception InvalidConstructPartException thrown if the subconstruct
        part is invalid
    */
    protected void
        parse</xsl:text>
                <xsl:value-of select="$subconstruct-name"/>
                <xsl:text>Subconstruct(TrackedTokenList line,
            Tokenizer t, SubconstructParsingData data,
            </xsl:text>
                <xsl:value-of select="$parent-construct-name"/>
                <xsl:text> parent,
            ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(t != null);
        Assert.require(data != null);
        Assert.require(parent != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since this default parser is invalid, and so a custom parser
            // class should be written and used instead
    }
</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>


    <!-- ############################################################### -->
    <!-- # Templates that output construct part error handler methods. # -->
    <!-- ############################################################### -->

    <!-- Spaces don't need error handlers. -->
    <xsl:template match="space" mode="handle-error-methods"/>

    <!-- Construct attributes don't need error handlers. -->
    <xsl:template match="attribute" mode="handle-error-methods"/>

    <xsl:template match="terminal" mode="handle-error-methods">
        <!-- Note: assumes @number is missing, 'one' or 'zero'. -->
        <xsl:text>
    /**
        Handles the invalid terminal that was supposed to be the first
        element of the specified token list. (It may be invalid because
        it is missing.)

        @param line the list of tokens whose first token was supposed
        to represent the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> terminal
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the invalid terminal
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the terminal is
        invalid and it may not be possible to correctly parse any following
        construct parts
    */
    protected void handleInvalid</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(
        TrackedTokenList line, SubconstructParsingData data,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(data != null);
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidTerminal(description,
                              line.get(0).location(), t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        <!-- Only required terminals can be missing. -->
        <xsl:if test="not(@number) or @number = 'one'">
            <xsl:text>
    /**
        Handles the terminal that is missing because there are no more
        tokens in the specified list of tokens.

        @param line the list of tokens that was supposed to be followed by
        the token representing the terminal
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the missing terminal
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the terminal is
        invalid and it may not be possible to correctly parse any following
        construct parts
    */
    protected void handleMissing</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(
        TrackedTokenList line, SubconstructParsingData data,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(data != null);
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: we don't just always use 'data.previousLocation()' since
        // the previous token could give us a better location in some
        // cases (e.g. the indent token before the terminal at the start
        // of a line (where data.previousLocation() would give us the
        // location of the preceding line at best)).
        SourceLocation loc = null;
        Token previousToken = line.lastAdvancedOver();
        if (previousToken != null)
        {
            loc = previousToken.location();
        }
        if (loc == null)
        {
            loc = data.previousLocation();
        }
        Assert.check(loc != null);

        reportMissingTerminal(description, loc, t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="handle-error-methods">
        <!-- Note: assumes @number is missing, 'one' or 'zero'. -->
        <xsl:text>
    /**
        Handles the invalid flag that was supposed to be the first
        element of the specified token list. (It may be invalid because
        it is missing.)

        @param line the list of tokens whose first token was supposed
        to represent the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flag
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the type of invalid flag
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the flag is
        invalid and it may not be possible to correctly parse any following
        construct parts
    */
    protected void handleInvalid</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Flag(
        TrackedTokenList line, SubconstructParsingData data,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line) == false);
        Assert.require(data != null);
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        reportInvalidConstructFlag(description,
                                   line.get(0).location(), t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        <!-- Only required flags can be missing. -->
        <xsl:if test="not(@number) or @number = 'one'">
            <xsl:text>
    /**
        Handles the flag that is missing because there are no more
        tokens in the specified list of tokens.

        @param line the list of tokens that was supposed to be followed
        by a token that represented the </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> flag
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the type of missing flag
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the flag is
        invalid and it may not be possible to correctly parse any following
        construct parts
    */
    protected void handleMissing</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Flag(
        TrackedTokenList line, SubconstructParsingData data,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(data != null);
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: we don't just always use 'data.previousLocation()' since
        // the previous token could give us a better location in some
        // cases (e.g. the indent token before the terminal at the start
        // of a line (where data.previousLocation() would give us the
        // location of the preceding line at best)).
        SourceLocation loc = null;
        Token previousToken = line.lastAdvancedOver();
        if (previousToken != null)
        {
            loc = previousToken.location();
        }
        if (loc == null)
        {
            loc = data.previousLocation();
        }
        Assert.check(loc != null);

        reportMissingConstructFlag(description, loc, t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="subconstruct" mode="handle-error-methods">
        <!-- Note: assumes @number is missing, 'one' or 'zero'. -->
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>

        <xsl:text>
    /**
        Handles the invalid subconstruct that was supposed to be
        represented by the first token(s) of the specified token list.
        (It may be invalid because it is missing.)

        @param line the list of tokens whose first token(s) were supposed
        to represent the </xsl:text>
        <xsl:value-of select="$subconstruct-name"/>
        <xsl:text> subconstruct
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the invalid subconstruct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the subconstruct
        is invalid and it may not be possible to correctly parse any
        following construct parts
    */
    protected void handleInvalid</xsl:text>
        <xsl:value-of select="$subconstruct-name"/>
        <xsl:text>Subconstruct(
        TrackedTokenList line, SubconstructParsingData data,
        </xsl:text>
        <xsl:value-of select="concat($construct-manager-class-name, '.',
                                     @type)"/>
        <xsl:text> subconstruct,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(data != null);
        // 'subconstruct' may be null
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: 'isEmptyLine(line)' may be true since the subconstruct
        // parser may have consumed the rest of 'line'.

        // The subconstruct parser has already reported the error.
        Assert.check(data.wasParsedSuccessfully() == false);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        <!-- Only required subconstructs can be missing. -->
        <xsl:if test="not(@number) or @number = 'one'">
            <xsl:text>
    /**
        Handles the subconstruct that is missing because there are no
        more tokens in the specified list of tokens.

        @param line the list of tokens that was supposed to be followed by
        the subconstruct's tokens
        @param data contains information about the parsing of this construct
        as a subconstruct, including whether it was parsed successfully
        @param description a description of the missing subconstruct
        @param t the tokenizer that is the source of the tokens being parsed
        @param handler the error handler to use to handle the error
        @exception InvalidConstructPartException thrown if the subconstruct
        is invalid and it may not be possible to correctly parse any
        following construct parts
    */
    protected void handleMissing</xsl:text>
            <xsl:value-of select="$subconstruct-name"/>
            <xsl:text>Subconstruct(
        TrackedTokenList line, SubconstructParsingData data,
        String description, Tokenizer t, ErrorHandler handler)
        throws InvalidConstructPartException
    {
        Assert.require(line != null);
        Assert.require(isEmptyLine(line));
        Assert.require(data != null);
        Assert.require(description != null);
        Assert.require(t != null);
        Assert.require(handler != null);

        // Note: we don't just always use 'data.previousLocation()' since
        // the previous token could give us a better location in some
        // cases (e.g. the indent token before the terminal at the start
        // of a line (where data.previousLocation() would give us the
        // location of the preceding line at best)).
        SourceLocation loc = null;
        Token previousToken = line.lastAdvancedOver();
        if (previousToken != null)
        {
            loc = previousToken.location();
        }
        if (loc == null)
        {
            loc = data.previousLocation();
        }
        Assert.check(loc != null);

        reportMissingSubconstruct(description, loc, t, handler);
        throw InvalidConstructPartException.instance();
    }
</xsl:text>
        </xsl:if>
    </xsl:template>


    <!-- ############################# -->
    <!-- # Parser factory templates. # -->
    <!-- ############################# -->

    <!-- Outputs the singleton parser factory class. -->
    <xsl:template name="parser-factory">
        <xsl:variable name="class-name"
            select="$parser-factory-class-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    The </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> parser factory singleton class.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends Default</xsl:text>
        <xsl:value-of select="$parser-factory-class-name"/>
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
        <xsl:value-of select="$class-name"/>
        <xsl:text> instance()
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
}
</xsl:text>
    </xsl:template>

    <!-- Outputs the default parser factory class skeleton. -->
    <xsl:template name="default-parser-factory-skeleton">
        <xsl:variable name="class-name"
            select="concat('Default', $parser-factory-class-name)"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> parser factory class.
    &lt;p&gt;
    </xsl:text>
    <strong>Implementation Note</strong>
        <xsl:text>: we use the create() static
    methods on the individual parser classes to create parser instances
    rather than cloning prototype instances created by this factory because:
    &lt;ul&gt;
        &lt;li&gt;if we assign the parser instances to constants in this
            class then a circularity is introduced when constructing this
            class: when a parser is constructed as part of constructing this
            class, that parser's PARSER_FACTORY constant field must be
            created, which causes this class to be constructed .... (Many
            parsers need the PARSER_FACTORY to construct parsers to parse
            their subconstructs.)
        &lt;li&gt;if we assign the parser instances to constants in this
            class lazily, then - because Java's memory model doesn't properly
            support the double-checked locking pattern - every attempt
            to create a parser will have to be synchronized (or we could
            just live with the possibility of creating a few instances
            of a given type of parser, though that seems a little ugly
            and error-prone)
    &lt;/ul&gt;
</xsl:text>
        <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$parser-factory-base-class-name"/>
        <xsl:text>
{
    // Factory methods
</xsl:text>
    <xsl:for-each select="$constructs">
        <xsl:variable name="is-concrete">
            <xsl:call-template name="has-concrete-parser"/>
        </xsl:variable>

        <xsl:if test="$is-concrete = 'false'">
            <xsl:call-template name="concrete-parser-factory-method"/>
        </xsl:if>
    </xsl:for-each>
    <xsl:text>
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the parser factory base class. -->
    <xsl:template name="parser-factory-base">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$parser-factory-base-class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    An abstract base class for the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> parser factory.
    &lt;p&gt;
    </xsl:text>
    <strong>Implementation Note</strong>
        <xsl:text>: we use the create() static
    methods on the individual parser classes to create parser instances
    rather than cloning prototype instances created by this factory because:
    &lt;ul&gt;
        &lt;li&gt;if we assign the parser instances to constants in this
            class then a circularity is introduced when constructing this
            class: when a parser is constructed as part of constructing this
            class, that parser's PARSER_FACTORY constant field must be
            created, which causes this class to be constructed .... (Many
            parsers need the PARSER_FACTORY to construct parsers to parse
            their subconstructs.)
        &lt;li&gt;if we assign the parser instances to constants in this
            class lazily, then - because Java's memory model doesn't properly
            support the double-checked locking pattern - every attempt
            to create a parser will have to be synchronized (or we could
            just live with the possibility of creating a few instances
            of a given type of parser, though that seems a little ugly
            and error-prone)
    &lt;/ul&gt;
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$parser-factory-base-class-name"/>
        <xsl:text>
{
    // Factory methods
</xsl:text>
        <xsl:for-each select="$constructs">
            <xsl:variable name="is-concrete">
                <xsl:call-template name="has-concrete-parser"/>
            </xsl:variable>

            <xsl:choose>
                <xsl:when test="$is-concrete = 'true'">
                    <xsl:call-template name="concrete-parser-factory-method"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="abstract-parser-factory-method"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Outputs a concrete parser factory method for the parser of
         the type of construct described by the current node. -->
    <xsl:template name="concrete-parser-factory-method">
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:text>
    /**
        @return a parser of </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> constructs
    */
    public </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Parser()
    {
        return Default</xsl:text>
        <xsl:value-of select="concat($parser-name, '.create();')"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <!-- Outputs an abstract parser factory method for the parser of
         the type of construct described by the current node. -->
    <xsl:template name="abstract-parser-factory-method">
        <xsl:variable name="parser-name">
            <xsl:call-template name="parser-class-name"/>
        </xsl:variable>

        <xsl:text>
    /**
        @return a parser of </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> constructs
    */
    public abstract </xsl:text>
        <xsl:value-of select="$parser-name"/>
        <xsl:text>
        create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Parser();
</xsl:text>
    </xsl:template>


    <!-- ##################### -->
    <!-- # Utility templates # -->
    <!-- ##################### -->

    <!-- Outputs the name of the subconstruct parsing helper map class
         for the (multiline) construct of the specified type. -->
    <xsl:template name="parsing-helper-map-name">
        <xsl:param name="construct-type" select="@type"/>

        <xsl:value-of select="concat($language-name, $construct-type,
                                     'TokenIdToParsingHelperMap')"/>
    </xsl:template>


    <!-- Outputs the start of the source file that defines the parser
         class with the specified name. It includes the file separator
         that separates files in the composite file. -->
    <xsl:template name="parser-source-prologue">
        <xsl:param name="class-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:value-of select="$imports"/>
        <xsl:text>
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;
</xsl:text>
    </xsl:template>


    <!-- Outputs the name of the parser class that parses constructs
         of the specified type. -->
    <xsl:template name="parser-class-name">
        <xsl:param name="construct-type" select="@type"/>

        <xsl:value-of select="concat($language-name, $construct-type, 'Parser')"/>
    </xsl:template>

    <!-- Outputs the name of the construct class of the specified type. -->
    <xsl:template name="construct-class-name">
        <xsl:param name="construct-type" select="@type"/>

        <xsl:value-of select="concat($construct-manager-class-name,
                                     '.', $construct-type)"/>
    </xsl:template>
</xsl:transform>
