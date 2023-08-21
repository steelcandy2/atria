<?xml version="1.0"?>
<!--
    $Id: language-common.java.xsl,v 1.21 2006/06/09 23:32:41 jgm Exp $

    Provides common functionality for stylesheets that transform a
    language description document into the source code for a Java class.
    It is intended to be xsl:imported into another stylesheet.

    This stylesheet assumes that the stylesheet that imports it defines
    the following:

        - class-implementation: a variable whose value is the child element
            of the implementation element that contains information about
            the class whose source code is to be the result of the
            transformation. The value of $implementation should be used in
            setting this variable's value

    Author: James MacKay
    Last Updated: $Date: 2006/06/09 23:32:41 $

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
<!ENTITY uc "ABCDEFGHIJKLMNOPQRSTUVWXYZ">
<!ENTITY lc "abcdefghijklmnopqrstuvwxyz">
]>

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="language-common.xsl"/>

    <xsl:output method="text"/>


    <!-- Configuration -->

    <xsl:variable name="spaces-per-indent-level" select="4"/>


    <!-- Global variables -->

    <!-- The extension on Java source files. -->
    <xsl:variable name="src-ext" select="'.java'"/>

    <xsl:variable name="implementation"
        select="$top/implementation[@language='Java']"/>

    <xsl:variable name="root-module"
        select="$implementation/root-module/@name"/>
    <xsl:variable name="base-module"
        select="$implementation/base-module/@name"/>
    <xsl:variable name="source-module"
        select="$implementation/source-module/@name"/>
    <xsl:variable name="tokens-module"
        select="$implementation/tokens-module/@name"/>
    <xsl:variable name="constructs-module"
        select="$implementation/constructs-module/@name"/>
    <xsl:variable name="constructs-testing-module"
        select="$implementation/constructs-testing-module/@name"/>
    <xsl:variable name="validation-module"
        select="$implementation/validation-module/@name"/>
    <xsl:variable name="instructions-module"
        select="$implementation/instructions-module/@name"/>
    <xsl:variable name="runtime-module"
        select="$implementation/runtime-module/@name"/>

    <!-- The token flag that a token has if it indicates the end of
         a line. -->
    <xsl:variable name="end-of-line-token-flag">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name"
                select="$top/token-flags/@end-of-line-flag-name"/>
        </xsl:call-template>
    </xsl:variable>

    <!-- The token ID of the type of token that represents indentation. -->
    <xsl:variable name="indent-token-id">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name"
                select="$top/tokens/@indent-name"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="location-factory-class-name"
        select="$implementation/location-factory-class/@name"/>
    <xsl:variable name="location-factory-constructor-name"
        select="$implementation/location-factory-class/@constructor-name"/>

    <xsl:variable name="token-manager-class-name"
        select="$implementation/token-manager-class/@name"/>
    <xsl:variable name="token-manager-constructor-name"
        select="$implementation/token-manager-class/@constructor-name"/>

    <!-- The name of the language's token creator source code tokenizer. -->
    <xsl:variable name="source-tokenizer-class-name"
        select="$implementation/tokenizer-class/@name"/>

    <xsl:variable name="construct-manager-base-class-name"
        select="$implementation/construct-manager-base-class/@name"/>
    <xsl:variable name="construct-manager-class-name"
        select="$implementation/construct-manager-class/@name"/>
    <xsl:variable name="construct-manager-constructor-name"
        select="$implementation/construct-manager-class/@constructor-name"/>

    <xsl:variable name="construct-visitor-class-name"
        select="$implementation/construct-visitor-class/@name"/>
    <xsl:variable name="abstract-construct-visitor-class-name"
        select="$implementation/abstract-construct-visitor-class/@name"/>

    <xsl:variable name="validity-constraint-checklist-factory-class-name"
        select="$implementation/validity-constraint-checklist-factory-class/@name"/>
    <xsl:variable name="validity-constraint-checklist-factory-constructor-name"
        select="$implementation/validity-constraint-checklist-factory-class/@constructor-name"/>

    <xsl:variable name="construct-test-data-creator-class-name"
        select="$implementation/construct-test-data-creator-class/@name"/>

    <xsl:variable name="instruction-manager-base-class-name"
        select="$implementation/instruction-manager-base-class/@name"/>
    <xsl:variable name="instruction-manager-class-name"
        select="$implementation/instruction-manager-class/@name"/>
    <xsl:variable name="instruction-manager-constructor-name"
        select="$implementation/instruction-manager-class/@constructor-name"/>

    <xsl:variable name="imports">
        <xsl:for-each select="$class-implementation/imports/import">
            <xsl:choose>
                <xsl:when test="@type='relative'">
import <xsl:value-of select="concat($root-module, '.', @name)"/>;
                </xsl:when>
                <xsl:otherwise>
import <xsl:value-of select="@name"/>;
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:variable>

    <xsl:variable name="class-name"
        select="$class-implementation/@name"/>
    <xsl:variable name="superclass-name"
        select="$class-implementation/@superclass"/>

    <xsl:variable name="class-prefix"
        select="$class-implementation/@prefix"/>
    <xsl:variable name="constant-class-prefix">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="$class-prefix"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="superclass-prefix"
        select="$class-implementation/@superclass-prefix"/>
    <xsl:variable name="constant-superclass-prefix">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="$superclass-prefix"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- Utility templates -->

    <!-- Indicates whether the specified type name is the name of a
         concrete construct class. -->
    <xsl:template name="is-concrete-construct-class">
        <xsl:param name="type" select="@type"/>

        <xsl:variable name="matching-constructs"
            select="key('construct', $type)"/>
        <xsl:choose>
            <xsl:when test="count($matching-constructs) &gt; 0">
                <xsl:apply-templates select="$matching-constructs"
                    mode="is-concrete-construct-class"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct"
        mode="is-concrete-construct-class">
        <xsl:value-of select="false()"/>
    </xsl:template>

    <xsl:template match="*" mode="is-concrete-construct-class">
        <xsl:value-of select="true()"/>
    </xsl:template>


    <!-- Outputs the start of the source file that defines the class with
         the specified name, including the file separator that separates
         files in the composite file. -->
    <xsl:template name="source-prologue">
        <xsl:param name="class-name"/>
        <xsl:param name="module" select="$constructs-module"/>
%%%% file <xsl:value-of select="concat($class-name, $src-ext)"/>
// Copyright (C) James MacKay

package <xsl:value-of select="$module"/>;

import com.steelcandy.common.debug.Assert;<xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs the start of the skeleton of the source file that
         defines the class with the specified name, including the
         file separator that separates files in the composite file. -->
    <xsl:template name="source-skeleton-prologue">
        <xsl:param name="class-name"/>
        <xsl:param name="module" select="$constructs-module"/>
%%%% file <xsl:value-of select="concat($class-name, $src-ext, '.skeleton')"/>
// Copyright (C) James MacKay

package <xsl:value-of select="$module"/>;

import com.steelcandy.common.debug.Assert;<xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Outputs the common part of a generated class' class comment. -->
    <xsl:template name="common-class-comment-part">&lt;p&gt;
    &lt;strong&gt;Note&lt;/strong&gt;: this file was automatically generated,
    and so should not be edited directly.
    &lt;ul&gt;
        &lt;li&gt;Input: <xsl:value-of select="$input-pathname"/>&lt;/li&gt;
        &lt;li&gt;Transform: <xsl:value-of select="$transform-pathname"/>&lt;/li&gt;
        &lt;li&gt;Output: <xsl:value-of select="$output-pathname"/>&lt;/li&gt;
    &lt;/ul&gt;

    @author James MacKay
    @version $Revision: 1.21 $<xsl:text/>
    </xsl:template>

    <!-- Outputs the common part of a generated skeleton class' class
         comment. -->
    <xsl:template name="common-skeleton-class-comment-part">
    @author James MacKay
    @version $Revision: 1.21 $<xsl:text/>
    </xsl:template>


    <!-- Outputs Java source code that combines all of the flags described
         by the specified flag elements.

         This template handles an empty set of flags properly. -->
    <xsl:template name="combine-flags">
        <xsl:param name="flags"/>

        <xsl:choose>
            <xsl:when test="$flags">
                <xsl:for-each select="$flags">
                    <xsl:call-template name="to-constant-name">
                        <xsl:with-param name="name" select="@name"/>
                    </xsl:call-template>
                    <xsl:if test="position()!=last()">
                        <xsl:text> | </xsl:text>
                    </xsl:if>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs enough spaces to provide the specified number of levels
         of indentation. -->
    <xsl:template name="indent">
        <xsl:param name="levels"/>
        <xsl:param name="spaces-per-level"
            select="$spaces-per-indent-level"/>

        <xsl:if test="$levels &gt; 0">
            <xsl:call-template name="copies">
                <xsl:with-param name="str" select="' '"/>
                <xsl:with-param name="number" select="$spaces-per-level"/>
            </xsl:call-template>
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$levels - 1"/>
                <xsl:with-param name="spaces-per-level"
                    select="$spaces-per-level"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Name conversion templates -->

    <xsl:template name="to-method-name">
        <xsl:param name="name"/>

        <xsl:variable name="first" select="substring($name, 1, 1)"/>
        <xsl:variable name="rest" select="substring($name, 2)"/>
        <xsl:value-of
            select="concat(translate($first, '&uc;', '&lc;'), $rest)"/>
    </xsl:template>

    <xsl:template name="to-field-name">
        <xsl:param name="name"/>

        <xsl:variable name="as-method-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name" select="$name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:value-of select="concat('_', $as-method-name)"/>
    </xsl:template>

    <!-- Converts the specified choice number to the name of the
         field that refers to that choice. -->
    <xsl:template name="to-choice-field-name">
        <xsl:param name="choice-number"/>

        <xsl:value-of select="concat('_choice', $choice-number)"/>
    </xsl:template>

    <xsl:template name="to-class-name">
        <xsl:param name="name"/>

        <xsl:variable name="first" select="substring($name, 1, 1)"/>
        <xsl:variable name="rest" select="substring($name, 2)"/>
        <xsl:value-of
            select="concat(translate($first, '&lc;', '&uc;'), $rest)"/>
    </xsl:template>

    <xsl:template name="to-constant-name">
        <xsl:param name="name"/>

        <xsl:variable name="first" select="substring($name, 1, 1)"/>
        <xsl:variable name="rest" select="substring($name, 2)"/>

        <xsl:if test="$name">
            <xsl:variable name="result-first"
                select="translate($first, '&lc;', '&uc;')"/>
            <xsl:variable name="result-rest">
                <xsl:call-template name="to-constant-name-tail">
                    <xsl:with-param name="name-tail" select="$rest"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:value-of select="concat($result-first, $result-rest)"/>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the specified 'tail' (i.e. second and subsequent
         characters) of a name converted to the tail of a constant
         name. -->
    <xsl:template name="to-constant-name-tail">
        <xsl:param name="name-tail"/>

        <xsl:if test="$name-tail">
            <xsl:variable name="first"
                select="substring($name-tail, 1, 1)"/>
            <xsl:variable name="rest"
                select="substring($name-tail, 2)"/>

            <xsl:variable name="result-first">
                <!-- Precede uppercase letters with an underscore, and
                     convert all other characters to uppercase. -->
                <xsl:choose>
                    <xsl:when test="contains('&uc;', $first)">
                        <xsl:value-of select="concat('_', $first)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of
                            select="translate($first, '&lc;', '&uc;')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="result-rest">
                <xsl:call-template name="to-constant-name-tail">
                    <xsl:with-param name="name-tail" select="$rest"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:value-of select="concat($result-first, $result-rest)"/>
        </xsl:if>
    </xsl:template>
</xsl:transform>
