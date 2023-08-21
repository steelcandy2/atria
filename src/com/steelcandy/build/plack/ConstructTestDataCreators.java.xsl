<?xml version="1.0"?>
<!--
    Transforms a language description document into an abstract Java base
    class for classes that create test data (source code and Constructs)
    for use in testing various language processing tools, such as parsers.

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

    <xsl:import href="language-common.java.xsl"/>


    <!-- ### Configuration ### -->


    <!-- ### Global variables ### -->

    <xsl:variable name="class-implementation"
        select="$implementation/construct-test-data-creator-base-class"/>

    <!-- Note: 'tokens' and 'constructs' are defined in
         language-common.xsl -->
    <xsl:variable name="char-classes"
        select="$top/character-classes/section/*"/>
    <xsl:variable name="construct-flag-sets"
        select="$top/construct-flag-sets/section/*"/>


    <!-- ### Main templates ### -->

    <xsl:template match="language">
        <!-- Generate the construct test data creator README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the test data creator base class. -->
        <xsl:call-template name="creator-base-class"/>

        <!-- Generate the skeleton for the concrete test data creator
             class. -->
        <xsl:call-template name="creator-class-skeleton"/>
    </xsl:template>

    <xsl:template name="readme-file"><xsl:text/>
%%%% file README.construct-test-data-creators
<xsl:value-of select="$language-name"/> Construct Test Data Creators README

Copyright (C) James MacKay

write this !!!!

- construct-test-data-creators.ignore
- construct-test-data-creators.discard

- create <xsl:value-of select="concat($construct-test-data-creator-class-name, $src-ext)"/> from
  <xsl:value-of select="concat($construct-test-data-creator-class-name, $src-ext)"/>.skeleton
    - add any extra code, including the construct to generate and the
      tokenizer to use
- once a skeleton file has been used to create the 'real' file, the
  skeleton file's name is usually added to the discard file
  (construct-test-data-creators.discard)

- etc.<xsl:text/>
    </xsl:template>

    <!-- Outputs the skeleton of the source code for the concrete
         construct test data creator class. -->
    <xsl:template name="creator-class-skeleton">
        <xsl:variable name="class"
            select="$construct-test-data-creator-class-name"/>
        <xsl:variable name="superclass" select="$class-name"/>
<xsl:call-template name="source-skeleton-prologue">
    <xsl:with-param name="class-name" select="$class"/>
    <xsl:with-param name="module" select="$constructs-testing-module"/>
</xsl:call-template>
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.testing.AbstractConstructTestDataCreator; // javadocs only

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    The construct test data creator class.
    <xsl:call-template name="common-skeleton-class-comment-part"/>
*/
public class <xsl:value-of select="$class"/>
    extends <xsl:value-of select="$superclass"/>
{
    // Protected methods

    /**
        @see AbstractConstructTestDataCreator#generateDefaultConstruct(IndentWriter)
    */
    protected Construct generateDefaultConstruct(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        return generate???Construct(w);
    }

    /**
        @see AbstractConstructTestDataCreator#createTokenSourceCodeTokenizer(SourceCode, ErrorHandler)
    */
    protected Tokenizer createTokenSourceCodeTokenizer(SourceCode source,
                                                       ErrorHandler handler)
    {
        Assert.require(source != null);
        Assert.require(handler != null);

        write this !!!!

        Assert.ensure(result != null);
        return result;
    }


    // Main method

    /**
        The main method, which generates the test data indicated by
        the specified command line arguments.

        @param args the command line arguments
        @see AbstractConstructTestDataCreator#generateTestData(String[])
    */
    public static void main(String[] args)
    {
        <xsl:value-of select="$class"/> creator =
            new <xsl:value-of select="$class"/>();
        EXECUTOR.executeAndExit(creator, args);
    }
}<xsl:text/>
    </xsl:template>

    <!-- Outputs the source code for the construct test data creator
         base class. -->
    <xsl:template name="creator-base-class">
<xsl:call-template name="source-prologue">
    <xsl:with-param name="class-name" select="$class-name"/>
    <xsl:with-param name="module" select="$constructs-testing-module"/>
</xsl:call-template>
<xsl:value-of select="$imports"/>
import <xsl:value-of select="$constructs-module"/>.*;
import <xsl:value-of select="concat($tokens-module, '.', $token-manager-class-name)"/>;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.Token;
import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.constructs.testing.*;

import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.Io;

import java.io.IOException;

/**
    An abstract base class for classes that generates data (source code and
    Constructs) for use in testing various <xsl:value-of select="$language-name"/> language processing
    tools, such as parsers.
    <xsl:call-template name="common-class-comment-part"/>
*/
public abstract class <xsl:value-of select="$class-name"/>
    extends <xsl:value-of select="$superclass-name"/>
{
    // Constants

    /** The construct manager singleton. */
    protected static final <xsl:value-of select="$construct-manager-class-name"/>
        MANAGER = <xsl:value-of select="concat($construct-manager-class-name, '.', $construct-manager-constructor-name, '();')"/>

    /** The token manager singleton. */
    protected static final <xsl:value-of select="$token-manager-class-name"/>
        TOKEN_MANAGER = <xsl:value-of select="concat($token-manager-class-name, '.', $token-manager-constructor-name, '();')"/>


    // Test data generation methods
<xsl:apply-templates select="$constructs" mode="gen-methods"/>
<xsl:apply-templates select="$construct-flag-sets" mode="gen-methods"/>
<xsl:for-each select="$tokens">
    <xsl:call-template name="gen-terminal-methods"/>
</xsl:for-each>
<xsl:apply-templates select="$char-classes" mode="gen-methods"/>}<xsl:text>
</xsl:text>
    </xsl:template>


    <!-- ### Construct data generation method templates ### -->

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="gen-methods"/>

    <xsl:template match="alias-construct" mode="gen-methods">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1-start"/>
        <xsl:text>
    {
        </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> result =
            </xsl:text>
        <xsl:value-of select="concat('MANAGER.create', @type, '();')"/>
        <xsl:text>

        incrementPartLevel();
        generate</xsl:text>
        <xsl:value-of select="@aliased-construct"/>
        <xsl:text>Construct(result, w);
        decrementPartLevel();

        return result;
    }</xsl:text>

        <xsl:call-template name="construct-gen-method-2-start"/>
        <xsl:text>
    {
        generate</xsl:text>
        <xsl:value-of select="@aliased-construct"/>
        <xsl:text>Construct(c, w);
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct"
        mode="gen-methods">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1-start"/>
        <xsl:text>
    {
        </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> result;

        incrementPartLevel();
        int num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(choice/subconstruct) - 1"/>
        <xsl:text>);
        switch (num)
        {
</xsl:text>
        <xsl:for-each select="choice/subconstruct">
            <xsl:text>        case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
            result = generate</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>Construct(w);
            break;
</xsl:text>
        </xsl:for-each>
        <xsl:text>        default:
            Assert.unreachable();
            result = null;  // not reached
        }
        decrementPartLevel();

        return result;
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="single-token-construct" mode="gen-methods">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1"/>
        <xsl:call-template name="construct-gen-method-2-start"/>
    {
        Token tok;
        int num;
<xsl:apply-templates select="*" mode="single-token-construct">
    <xsl:with-param name="indent-level" select="2"/>
</xsl:apply-templates><xsl:text>
        c.setToken(tok);
    }
</xsl:text>
    </xsl:template>

    <!-- Ignore construct attributes. -->
    <xsl:template match="attribute" mode="single-token-construct"/>

    <xsl:template match="terminal-choice" mode="single-token-construct">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(terminal) - 1"/>
        <xsl:text>);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>switch (num)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:for-each select="terminal">
            <xsl:value-of select="$indent"/>
            <xsl:text>case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
</xsl:text>
            <xsl:value-of select="$indent"/>
            <xsl:text>    tok = generate</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Terminal(w);
</xsl:text>
            <xsl:value-of select="$indent"/>
            <xsl:text>    break;
</xsl:text>
        </xsl:for-each>
        <xsl:value-of select="$indent"/>
        <xsl:text>default:
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    Assert.unreachable();
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    tok = null;  // not reached
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="terminal" mode="single-token-construct">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>tok = generate</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(w);
</xsl:text>
    </xsl:template>

    <xsl:template match="construct | compound-construct" mode="gen-methods">
        <xsl:call-template name="simple-construct-gen-methods"/>
    </xsl:template>

    <xsl:template match="single-line-construct" mode="gen-methods">
        <xsl:call-template name="simple-construct-gen-methods">
            <xsl:with-param name="is-line-construct" select="true()"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the methods that generate test data for the type of
         construct described by the current node. -->
    <xsl:template name="simple-construct-gen-methods">
        <xsl:param name="is-line-construct" select="false()"/>

        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1"/>
        <xsl:call-template name="construct-gen-method-2-start"/>
    {
        int num;
        boolean isFirstPart = true;
        int spaceType = NO_SPACE;

<xsl:apply-templates select="*" mode="gen-regular-methods">
    <xsl:with-param name="indent-level" select="2"/>
</xsl:apply-templates>
<xsl:if test="$is-line-construct">
    <xsl:text>        w.write(Io.NL);
</xsl:text>
</xsl:if>
<xsl:text>    }
</xsl:text>
    </xsl:template>

    <xsl:template match="multiline-construct" mode="gen-methods">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1"/>
        <xsl:call-template name="construct-gen-method-2-start"/>
        <xsl:text>
    {
        int num;
        boolean isFirstPart = true;
        int spaceType = NO_SPACE;

        // Generate the first line of the construct.
</xsl:text>
<xsl:apply-templates select="first-line/*" mode="gen-regular-methods">
    <xsl:with-param name="indent-level" select="2"/>
</xsl:apply-templates>
        <xsl:text>        w.write(Io.NL);

        // Generate the indented subconstructs.
        spaceType = NO_SPACE;
        w.incrementIndentLevel();
</xsl:text>
<xsl:apply-templates select="indented-subconstructs/*"
    mode="gen-regular-methods">
    <xsl:with-param name="indent-level" select="2"/>
</xsl:apply-templates>
        <xsl:text>        w.decrementIndentLevel();
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="repeated-construct" mode="gen-methods">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>
        <xsl:variable name="subconstruct-type"
            select="subconstruct/@type"/>
        <xsl:variable name="full-subconstruct-type"
            select="concat($construct-manager-class-name, '.',
                           $subconstruct-type)"/>
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name">
                <xsl:with-param name="node" select="subconstruct"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="num-subconstructs" select="subconstruct/@number"/>

        <xsl:call-template name="construct-gen-method-1"/>
        <xsl:call-template name="construct-gen-method-2-start"/>
        <xsl:text>
    {
        int num;
        boolean isFirstPart = true;
        int spaceType = NO_SPACE;
</xsl:text>
        <xsl:if test="@start-terminal"><xsl:text>
        // Generate the start terminal
        num = generateOnePartCount();
        for (int i = 0; i &lt; num; i++)
        {
            generate</xsl:text>
            <xsl:value-of select="@start-terminal"/>
            <xsl:text>Terminal(w);
            isFirstPart = false;
            doWriteSpace(w, </xsl:text>
            <xsl:call-template name="space-type-constant">
                <xsl:with-param name="space-type"
                    select="@space-after-start-terminal"/>
            </xsl:call-template>
            <xsl:text>, isFirstPart);
        }
</xsl:text>
        </xsl:if>
<xsl:text>
        // Generate the repeated subconstructs and separators.
        num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="$num-subconstructs"/>
        </xsl:call-template>
        <xsl:text>;
        if (num &gt; 0)
        {
            // Generate the first subconstruct.
            incrementPartLevel();
            </xsl:text>
            <xsl:value-of select="$full-subconstruct-type"/>
            <xsl:text> sub =
                generate</xsl:text>
            <xsl:value-of select="$subconstruct-type"/>
            <xsl:text>Construct(w);
            c.</xsl:text>
            <xsl:call-template name="add-subconstruct-method">
                <xsl:with-param name="subconstruct-name"
                    select="$subconstruct-name"/>
                <xsl:with-param name="number"
                    select="$num-subconstructs"/>
            </xsl:call-template>
            <xsl:text>(sub);
            isFirstPart = false;
            decrementPartLevel();
        }

        // Generate the remaining subconstructs preceded by separators.
        for (int i = 1; i &lt; num; i++)
        {
            // Generate the separator and any preceding/followng spaces, or
            // just zero or more spaces if there's no separator.</xsl:text>
            <xsl:choose>
                <xsl:when test="@separator-terminal">
                    <xsl:text>
            doWriteSpace(w, </xsl:text>
                    <xsl:call-template name="space-type-constant">
                        <xsl:with-param name="space-type"
                            select="@space-before-separators"/>
                    </xsl:call-template>
                    <xsl:text>, isFirstPart);
            generate</xsl:text>
                    <xsl:value-of select="@separator-terminal"/>
                    <xsl:text>Terminal(w);
            doWriteSpace(w, </xsl:text>
                    <xsl:call-template name="space-type-constant">
                        <xsl:with-param name="space-type"
                            select="@space-after-separators"/>
                    </xsl:call-template>
                    <xsl:text>, isFirstPart);</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>
            doWriteSpace(w, ENCOURAGED_SPACE, isFirstPart);</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>

            // Generate the subconstruct.
            incrementPartLevel();
            </xsl:text>
            <xsl:value-of select="$full-subconstruct-type"/>
            <xsl:text> sub =
                generate</xsl:text>
            <xsl:value-of select="$subconstruct-type"/>
            <xsl:text>Construct(w);
            c.</xsl:text>
            <xsl:call-template name="add-subconstruct-method">
                <xsl:with-param name="subconstruct-name"
                    select="$subconstruct-name"/>
                <xsl:with-param name="number"
                    select="$num-subconstructs"/>
            </xsl:call-template>
            <xsl:text>(sub);
            isFirstPart = false;
            decrementPartLevel();
        }
</xsl:text>

        <xsl:if test="@end-terminal"><xsl:text>
        // Generate the end terminal
        num = generateOnePartCount();
        for (int i = 0; i &lt; num; i++)
        {
            doWriteSpace(w, </xsl:text>
            <xsl:call-template name="space-type-constant">
                <xsl:with-param name="space-type"
                    select="@space-before-end-terminal"/>
            </xsl:call-template>
            <xsl:text>, isFirstPart);
            generate</xsl:text>
            <xsl:value-of select="@end-terminal"/>
            <xsl:text>Terminal(w);
            isFirstPart = false;
        }
</xsl:text>
        </xsl:if>
        <xsl:text>    }
</xsl:text>
    </xsl:template>


    <!-- Generates the 1-argument generate...Construct() method for
         the type of construct described by the current node. (The
         generated method may not be appropriate for all types of
         constructs - alias constructs for example.) -->
    <xsl:template name="construct-gen-method-1">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:call-template name="construct-gen-method-1-start"/>
        <xsl:text>
    {
        </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> result =
            </xsl:text>
            <xsl:value-of select="concat('MANAGER.create', @type, '();')"/>
        <xsl:text>

        incrementPartLevel();
        generate</xsl:text>
        <xsl:value-of select="concat(@type, 'Construct(result, w);')"/>
        <xsl:text>
        decrementPartLevel();

        return result;
    }</xsl:text>
    </xsl:template>

    <!-- Generates the start of the 1-argument generate...Construct()
         method for the type of construct described by the current node. -->
    <xsl:template name="construct-gen-method-1-start">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:text>
    /**
        Generates test data for the </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct:
        a construct is generated and returned after the corresponding
        source code is written using the specified writer.

        @param w the writer to use to write out the source code
        corresponding to the returned construct
        @return a newly-generated construct instance
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    public </xsl:text>
    <xsl:value-of select="$type"/><xsl:text>
        generate</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Construct(IndentWriter w)
        throws IOException</xsl:text>
    </xsl:template>

    <!-- Generates the start of the 2-argument generate...Construct()
         method for the type of construct described by the current node. -->
    <xsl:template name="construct-gen-method-2-start">
        <xsl:variable name="type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:text>

    /**
        Generates test data for the </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct:
        the parts of the specified construct are generated and added to it,
        and the corresponding source code is written using the specified
        writer.

        @param c the construct whose parts are to be generated and then
        added to it
        @param w the writer to use to write out the source code
        corresponding to the returned construct
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    protected void generate</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Construct(
        </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> c, IndentWriter w)
        throws IOException</xsl:text>
    </xsl:template>


    <!-- ### Regular construct part templates ### -->

    <!-- Ignore construct attributes. -->
    <xsl:template match="attribute" mode="gen-regular-methods"/>

    <xsl:template match="flag-from-set" mode="gen-regular-methods">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
        <xsl:call-template name="num-loop-start">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
        <xsl:value-of select="$indent"/>
        <xsl:text>    int flag = writeFlagFrom</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Set(w);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    c.set</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(flag);
</xsl:text>
        <xsl:call-template name="num-loop-end">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="space" mode="gen-regular-methods">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>spaceType = </xsl:text>
        <xsl:call-template name="space-type-constant">
            <xsl:with-param name="space-type" select="@type"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
    </xsl:template>

    <xsl:template match="subconstruct" mode="gen-regular-methods">
        <xsl:param name="indent-level"/>
        <xsl:param name="type" select="@type"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="subconstruct-name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
        <xsl:call-template name="num-loop-start">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
        <xsl:value-of select="$indent"/>
        <xsl:text>    </xsl:text>
        <xsl:value-of
            select="concat($construct-manager-class-name, '.', $type)"/>
        <xsl:text>
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>        sub = generate</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>Construct(w);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    c.</xsl:text>
        <xsl:call-template name="add-subconstruct-method">
            <xsl:with-param name="subconstruct-name"
                select="$subconstruct-name"/>
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>(sub);
</xsl:text>
        <xsl:call-template name="num-loop-end">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="terminal-choice" mode="gen-regular-methods">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(terminal) - 1"/>
        <xsl:text>);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>switch (num)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:for-each select="terminal">
            <xsl:value-of select="$indent"/>
            <xsl:text>case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
</xsl:text>
            <xsl:value-of select="$indent"/>
            <xsl:text>    generate</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Terminal(w);
</xsl:text>
            <xsl:value-of select="$indent"/>
            <xsl:text>    break;
</xsl:text>
        </xsl:for-each>
        <xsl:value-of select="$indent"/>
        <xsl:text>default:
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    Assert.unreachable();
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="terminal" mode="gen-regular-methods">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
        <xsl:call-template name="num-loop-start">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
        <xsl:value-of select="$indent"/>
        <xsl:text>    generate</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(w);
</xsl:text>
        <xsl:call-template name="num-loop-end">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:call-template>
    </xsl:template>


    <!-- ### Construct flag set data generation method templates ### -->

    <xsl:template match="flag-set-definition" mode="gen-methods">
        <xsl:text>
    /**
        Writes out the source code corresponding to one of the flags in
        the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> construct flag set using the specified writer, and
        returns the flag that was written.

        @param w the writer to use to write out the flag's source code
        @return the flag that was written
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    public int writeFlagFrom</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Set(IndentWriter w)
        throws IOException
    {
        int result;

        int num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(flag) - 1"/>
        <xsl:text>);
        switch (num)
        {
</xsl:text>
        <!-- NOTE: we assume here that flags map to tokens (i.e.
             terminals) with the same name as the flag. -->
        <xsl:for-each select="flag">
            <xsl:variable name="construct-flag">
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@name"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:text>        case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
            generate</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Terminal(w);
            result = MANAGER.</xsl:text>
            <xsl:value-of select="$construct-flag"/>
            <xsl:text>;
            break;
</xsl:text>
        </xsl:for-each>
        <xsl:text>        default:
            Assert.unreachable();
            result = -1;  // not reached
        }

        return result;
    }
</xsl:text>
    </xsl:template>


    <!-- ### Token/terminal data generation method templates ### -->

    <xsl:template name="gen-terminal-methods">
        <xsl:text>
    /**
        Generates test data for the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> terminal/token:
        a token is generated and returned after the corresponding source
        code is written using the specified writer.

        @param w the writer to use to write out the source code
        corresponding to the returned terminal/token
        @return a newly-generated token, or null if the token couldn't
        be generated (in which case an error should have been reported)
        @exception IOException thrown if an error occurs in writing the
        source code
    */
    public Token generate</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Terminal(IndentWriter w)
        throws IOException
    {
        Token result;</xsl:text>
<xsl:choose>
    <xsl:when test="@text">
        <xsl:text>

        String text = &quot;</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>&quot;;
        result = TOKEN_MANAGER.create</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Token(
                    DEFAULT_SOURCE_POSITION, text.length());
        w.write(text);

        return result;</xsl:text>
    </xsl:when>
    <xsl:otherwise>
        <xsl:text>
        int num;
        int spaceType = NO_SPACE;
        boolean isFirstPart = true;
        StringBuffer buf = new StringBuffer();

</xsl:text>
        <xsl:apply-templates select="text/*" mode="terminal-text">
            <xsl:with-param name="indent-level" select="2"/>
        </xsl:apply-templates>
        <xsl:text>
        String text = buf.toString();
        if (isLegal</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>TerminalText(text))
        {
            result = createToken(text);
            w.write(text);
        }
        else
        {
            // Try to generate another, legal instance of the terminal.
            result = generate</xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text>Terminal(w);
        }

        return result;
    }

    /**
        Indicates whether the specified text is legal </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> terminal text.
        &lt;p&gt;
        According to this implementation all text is legal terminal
        text. Subclasses can override this for various reasons, such
        as to disallow reserved words as legal identifier text.

        @param text the text to check
        @return true iff 'text' is legal </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> terminal text
    */
    protected boolean isLegal</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>TerminalText(String text)
    {
        return true;</xsl:text>
    </xsl:otherwise>
</xsl:choose><xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="char-from-class" mode="terminal-text">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>if (num == 0)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    spaceType = NO_SPACE;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>for (int i = 0; i &lt; num; i++)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    doAppendSpace(buf, spaceType, isFirstPart);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    append</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(buf);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    isFirstPart = false;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="choice" mode="terminal-text">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(*) - 1"/>
        <xsl:text>);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>if (num == 0)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    spaceType = NO_SPACE;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>switch (num)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:for-each select="*">
            <xsl:value-of select="$indent"/>
            <xsl:text>case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
</xsl:text>
            <xsl:apply-templates select="." mode="terminal-text">
                <xsl:with-param name="indent-level"
                    select="$indent-level + 1"/>
            </xsl:apply-templates>
            <xsl:value-of select="$indent"/>
            <xsl:text>    break;
</xsl:text>
        </xsl:for-each>
        <xsl:value-of select="$indent"/>
        <xsl:text>default:
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    Assert.unreachable();
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="chars" mode="terminal-text">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>num = </xsl:text>
        <xsl:call-template name="generate-number-method">
            <xsl:with-param name="number" select="@number"/>
        </xsl:call-template>
        <xsl:text>;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>if (num == 0)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    spaceType = NO_SPACE;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>for (int i = 0; i &lt; num; i++)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    buf.append(&quot;</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>&quot;);
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    isFirstPart = false;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="space" mode="terminal-text">
        <xsl:param name="indent-level"/>

        <!-- Sets spaceType the same way as for regular constructs. -->
        <xsl:apply-templates select="." mode="gen-regular-methods">
            <xsl:with-param name="indent-level" select="$indent-level"/>
        </xsl:apply-templates>
    </xsl:template>


    <!-- ### Character class data generation method templates ### -->

    <!-- TODO: do we want to supprt 'number' attributes on character
               class templates? -->

    <!-- Ignore notes -->
    <xsl:template match="notes" mode="char-class-constants"/>

    <xsl:template match="character-class" mode="gen-methods">
    /**
        Appends a single <xsl:value-of select="@name"/> to the specified buffer.

        @param buf the buffer to append the <xsl:value-of select="@name"/> to
    */
    public void append<xsl:value-of select="@name"/>(StringBuffer buf)
    {
<xsl:text/>
        <xsl:if test="count(choice) &gt; 0">
            <xsl:text>        int num;

</xsl:text>
        </xsl:if>
<xsl:apply-templates select="*" mode="gen-char-methods"/>    }<xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="single-character-choice" mode="gen-char-methods">
        <xsl:text>        doAppendOneOf(buf, &quot;</xsl:text>
        <xsl:value-of select="@list"/>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>

    <xsl:template match="char-from-class" mode="gen-char-methods">
        <xsl:text>        append</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(buf);
</xsl:text>
    </xsl:template>

    <xsl:template match="chars" mode="gen-char-methods">
        <xsl:text>        doAppendOneOf(buf, &quot;</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>

    <!-- We assume that a 'choice' element contains at least one
         choice (i.e. child element). -->
    <xsl:template match="choice" mode="gen-char-methods">
        <xsl:text>        num = generateUniformRandomNumber(0, </xsl:text>
        <xsl:value-of select="count(*) - 1"/>
        <xsl:text>);
        switch (num)
        {
</xsl:text>
        <xsl:for-each select="*">
            <xsl:text>        case </xsl:text>
            <xsl:value-of select="position() - 1"/>
            <xsl:text>:
            </xsl:text>
            <xsl:apply-templates select="." mode="char-choice"/>
            <xsl:text>            break;
</xsl:text>
        </xsl:for-each>
        <xsl:text>        default:
            Assert.unreachable();
        }
</xsl:text>
    </xsl:template>

    <xsl:template match="char-from-class" mode="char-choice">
        <xsl:text>append</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(buf);
</xsl:text>
    </xsl:template>

    <xsl:template match="single-character-choice" mode="char-choice">
        <xsl:text>doAppendOneOf(buf, &quot;</xsl:text>
        <xsl:value-of select="@list"/>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>

    <xsl:template match="chars" mode="char-choice">
        <xsl:text>doAppendOneOf(buf, &quot;</xsl:text>
        <xsl:value-of select="@text"/>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>


    <!-- Miscellaneous templates -->

    <!-- Outputs the first part of a loop over 'num' construct parts. -->
    <xsl:template name="num-loop-start">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>if (num == 0)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    spaceType = NO_SPACE;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>for (int i = 0; i &lt; num; i++)
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>{
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>    doWriteSpace(w, spaceType, isFirstPart);
</xsl:text>
    </xsl:template>

    <!-- Outputs the last part of a loop over 'num' construct parts. -->
    <xsl:template name="num-loop-end">
        <xsl:param name="indent-level"/>

        <xsl:variable name="indent">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-level"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$indent"/>
        <xsl:text>    isFirstPart = false;
</xsl:text>
        <xsl:value-of select="$indent"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <!-- Appends a space of the specified type to the buffer 'buf'. -->
    <xsl:template name="append-space">
        <xsl:param name="indent-level"/>
        <xsl:param name="space-type"/>

        <xsl:call-template name="output-space">
            <xsl:with-param name="indent-level" select="$indent-level"/>
            <xsl:with-param name="space-type" select="$space-type"/>
            <xsl:with-param name="prefix" select="'Append'"/>
        </xsl:call-template>
        <xsl:text>(buf);
</xsl:text>
    </xsl:template>

    <!-- Writes out a space of the specified type using the writer 'w'. -->
    <xsl:template name="write-space">
        <xsl:param name="indent-level"/>
        <xsl:param name="space-type"/>

        <xsl:call-template name="output-space">
            <xsl:with-param name="indent-level" select="$indent-level"/>
            <xsl:with-param name="space-type" select="$space-type"/>
            <xsl:with-param name="prefix" select="'Write'"/>
        </xsl:call-template>
        <xsl:text>(w);
</xsl:text>
    </xsl:template>

    <!-- Returns the name of the space type constant that corresponds
         to the specified space type ('required', 'encouraged', etc.) -->
    <xsl:template name="space-type-constant">
        <xsl:param name="space-type"/>

        <xsl:choose>
            <xsl:when test="not($space-type) or $space-type='required'">
                <xsl:text>REQUIRED_SPACE</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='encouraged'">
                <xsl:text>ENCOURAGED_SPACE</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='discouraged'">
                <xsl:text>DISCOURAGED_SPACE</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='disallowed'">
                <xsl:text>DISALLOWED_SPACE</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Found an unknown 'space type' value &quot;<xsl:value-of select="$space-type"/>&quot;
that was passed to the &quot;space-type-constant&quot; template.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the code to output a space of the specified type
         ('required', 'encouraged', etc.)

         TODO: for now we just output a space wherever one is required
         or encouraged. We should refine this template to not put out
         a space when the following construct part is missing or if all
         of the preceding parts of the construct are missing !!! -->
    <xsl:template name="output-space">
        <xsl:param name="indent-level"/>
        <xsl:param name="space-type"/>
        <xsl:param name="prefix"/>

        <xsl:call-template name="indent">
            <xsl:with-param name="levels" select="$indent-level"/>
        </xsl:call-template>
        <xsl:text>do</xsl:text>
        <xsl:value-of select="$prefix"/>
        <xsl:choose>
            <xsl:when test="not($space-type) or $space-type='required'">
                <xsl:text>RequiredSpace</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='encouraged'">
                <xsl:text>EncouragedSpace</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='discouraged'">
                <xsl:text>DiscouragedSpace</xsl:text>
            </xsl:when>
            <xsl:when test="$space-type='disallowed'">
                <xsl:text>DisallowedSpace</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Found an unknown 'space type' value &quot;<xsl:value-of select="$space-type"/>&quot;
that was passed to the &quot;write-space&quot; template.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns the name and (empty) arguments of the method to use
         to get the number of items to output based on the specified
         'number of items' value (e.g. 'zero-or-one'). -->
    <xsl:template name="generate-number-method">
        <xsl:param name="number"/>

        <xsl:choose>
            <xsl:when test="not($number) or $number='one'">
                <xsl:text>generateOnePartCount</xsl:text>
            </xsl:when>
            <xsl:when test="$number='zero-or-one'">
                <xsl:text>generateZeroOrOnePartCount</xsl:text>
            </xsl:when>
            <xsl:when test="$number='zero-or-more'">
                <xsl:text>generateZeroOrMorePartCount</xsl:text>
            </xsl:when>
            <xsl:when test="$number='one-or-more'">
                <xsl:text>generateOneOrMorePartCount</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Found an unknown 'number of items' attribute value &quot;<xsl:value-of select="$number"/>&quot;
that was passed to the &quot;generate-number-method&quot; template.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>()</xsl:text>
    </xsl:template>

    <!-- Returns the name of the method to use to add a subconstruct
         with the specified name to a construct that can have a number of
         instances of the subconstruct indicated by the specified 'number
         of items' value (e.g. 'zero-or-more'). -->
    <xsl:template name="add-subconstruct-method">
        <xsl:param name="subconstruct-name"/>
        <xsl:param name="number"/>

        <xsl:choose>
            <xsl:when test="not($number) or $number='one' or
                            $number='zero-or-one'">
                <xsl:text>set</xsl:text>
            </xsl:when>
            <xsl:when test="$number='zero-or-more' or
                            $number='one-or-more'">
                <xsl:text>add</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Found an unknown 'number of items' attribute value &quot;<xsl:value-of select="$number"/>&quot;
that was passed to the &quot;add-subconstruct-method&quot; template.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="$subconstruct-name"/>
    </xsl:template>
</xsl:transform>
