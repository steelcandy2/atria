<?xml version="1.0"?>
<!--
    $Id: language-common.xsl,v 1.29 2006/06/14 04:33:36 jgm Exp $

    Provides common functionality for stylesheets that transform a
    language description document. It is intended to be xsl:imported
    into another stylesheet.

    This stylesheet makes no assumptions about the stylesheet that
    imports it.

    Author: James MacKay
    Last Updated: $Date: 2006/06/14 04:33:36 $

    Copyright (C) 2002-2012 by James MacKay.

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

    <xsl:import href="common.xsl"/>


    <!-- Global variables and keys -->

    <xsl:variable name="top" select="/language"/>

    <!-- The name of the language that the generated class will help
         process and the indefinite article that should be used before
         the name of the language. -->
    <xsl:variable name="language-name" select="$top/@name"/>
    <xsl:variable name="language-name-article">
        <xsl:choose>
            <xsl:when test="$top/@article">
                <xsl:value-of select="$top/@article"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- 'a' is the default article. -->
                <xsl:text>a</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="language-first-copyright-year"
        select="$top/@first-copyright-year"/>


    <!-- All of the constant and constant-list elements. -->
    <xsl:variable name="constants" select="$top/constants/constant"/>
    <xsl:variable name="constant-lists" select="$top/constants/constant-list"/>

    <xsl:key name="constant" use="@name"
        match="/language/constants/constant"/>
    <xsl:key name="constant-list" use="@name"
        match="/language/constants/constant-list"/>


    <!-- All of the character class elements (excluding notes). -->
    <xsl:variable name="character-classes"
        select="$top/character-classes/section/character-class"/>

    <xsl:key name="character-class" use="@name"
        match="/language/character-classes/section/character-class"/>


    <!-- All of the operator precedence level elements (excluding notes). -->
    <xsl:variable name="operator-precedence-levels"
        select="$top/operator-precedence-levels/section/precedence-level"/>
    <xsl:variable name="number-of-operator-precedence-levels"
        select="count($operator-precedence-levels)"/>


    <!-- All of the token flag elements. -->
    <xsl:variable name="token-flags"
        select="$top/token-flags/section/flag-definition"/>

    <xsl:key name="token-flag" use="@name"
        match="/language/token-flags/section/flag-definition"/>


    <!-- All of the token flag set elements. -->
    <xsl:variable name="token-flag-sets"
        select="$top/token-flag-sets/section/flag-set-definition"/>

    <xsl:key name="token-flag-set" use="@name"
        match="/language/token-flag-sets/section/flag-set-definition"/>


    <!-- All of the operator precedence levels. -->
    <xsl:variable name="operator-precedence-levels"
        select="$top/operator-precedence-levels/section/precedence-level"/>

    <xsl:key name="operator-precedence-level" use="@name"
        match="/language/operator-precedence-levels/section/precedence-level"/>
    <xsl:key name="operator-precedence-level-value" use="@value"
        match="/language/operator-precedence-levels/section/precedence-level"/>


    <!-- All of the token elements (excluding notes). -->
    <xsl:variable name="tokens"
        select="$top/tokens/section/*[local-name() != 'notes']"/>

    <xsl:key name="token" use="@name"
        match="/language/tokens/section/*[local-name() != 'notes']"/>


    <!-- All of the operator token elements. -->
    <xsl:variable name="operator-tokens"
        select="$top/tokens/section/operator |
                $top/tokens/section/reserved-word-operator"/>

    <!-- All of the reserved word token elements (including the reserved
         word operators). -->
    <xsl:variable name="reserved-word-tokens"
        select="$top/tokens/section/reserved-word |
                $top/tokens/section/reserved-word-operator"/>

    <xsl:key name="reserved-word" use="@name"
        match="/language/tokens/section/reserved-word |
               /language/tokens/section/reserved-word-operator"/>


    <!-- All of the token creator elements (excluding notes and the
         default token creator (which doesn't define a new creator)). -->
    <xsl:variable name="token-creators"
        select="$top/token-creators/section/*[local-name() != 'notes' and local-name() != 'default-token-creator']"/>

    <xsl:key name="token-creator" use="@name"
        match="/language/token-creators/section/*[local-name() != 'notes' and local-name() != 'default-token-creator']"/>


    <!-- All of the construct flag elements. -->
    <xsl:variable name="construct-flags"
        select="$top/construct-flags/section/flag-definition"/>

    <xsl:key name="construct-flag" use="@name"
        match="/language/construct-flags/section/flag-definition"/>


    <!-- All of the construct flag set elements. -->
    <xsl:variable name="construct-flag-sets"
        select="$top/construct-flag-sets/section/flag-set-definition"/>

    <xsl:key name="construct-flag-set" use="@name"
        match="/language/construct-flag-sets/section/flag-set-definition"/>


    <!-- All of the construct elements (excluding notes). -->
    <xsl:variable name="constructs"
        select="$top/constructs/section/*[local-name() != 'notes']"/>

    <!-- A key for finding constructs by name (i.e. 'type' attribute value). -->
    <xsl:key name="construct" use="@type"
             match="/language/constructs/section/*[local-name() != 'notes']"/>

    <!-- The construct elements that describe constructs that can be
         'inherited' by other constructs. -->
    <xsl:variable name="inheritable-constructs"
        select="$top/constructs/section/choice-construct |
                $top/constructs/section/line-choice-construct"/>


    <!-- All of the validity constraint definitions. -->
    <xsl:variable name="validity-constraint-definitions"
        select="$top/validity-constraints/section/definition"/>

    <!-- A key for finding validity constraint definitions by name. -->
    <xsl:key name="validity-constraint-definition" use="@name"
        match="/language/validity-constraints/section/definition"/>


    <!-- All of the semantics definitions. -->
    <xsl:variable name="semantics-definitions"
        select="$top/semantics/section/definition"/>

    <!-- A key for finding semantics definitions by name. -->
    <xsl:key name="semantics-definition" use="@name"
        match="/language/semantics/section/definition"/>


    <xsl:variable name="do-debug-validity-constraints"
        select="$top/validity-constraints/@debug"/>


    <!-- Construct templates -->

    <!-- Outputs the description of the construct flag set with the
         specified name. -->
    <xsl:template name="construct-flag-set-description">
        <xsl:param name="set-name"/>

        <xsl:value-of select="$top/construct-flag-sets/section/flag-set-definition[@name=$set-name]/@description"/>
    </xsl:template>

    <!-- Outputs the name of the subconstruct described by the specified
         node (which defaults to the current node).

         Note: the name of a subconstruct is the one specified by its
         'name' attribute if it has one, and the one specified by its
         'type' attribute otherwise. -->
    <xsl:template name="subconstruct-name">
        <xsl:param name="node" select="."/>

         <xsl:choose>
            <xsl:when test="not($node/@name)">
                <xsl:value-of select="$node/@type"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$node/@name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- Validity constraint templates. -->

    <!-- Outputs a boolean value that indicates whether the validity
         constraint represented by the specified element is a per-clone
         constraint. -->
    <xsl:template name="is-per-clone-constraint">
        <xsl:param name="constraint" select="."/>

        <xsl:choose>
            <xsl:when test="$constraint/@per-clone">
                <xsl:value-of select="$constraint/@per-clone"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- Presentation templates -->

    <!-- Returns the specified string with all escape characters removed.
         A fatal error is reported if the string ends with an escape
         character. -->
    <xsl:template name="remove-all-escapes">
        <xsl:param name="str"/>

        <xsl:variable name="chars">
            <xsl:call-template name="unescape-first-char">
                <xsl:with-param name="str" select="$str"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:if test="$chars">
            <xsl:variable name="first" select="substring($chars, 1, 1)"/>
            <xsl:variable name="rest">
                <xsl:if test="string-length($chars) &gt; 1">
                    <xsl:call-template name="remove-all-escapes">
                        <xsl:with-param name="str"
                            select="substring($chars, 2)"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:variable>
            <xsl:value-of select="$first"/>
            <xsl:value-of select="$rest"/>
        </xsl:if>
    </xsl:template>

    <!-- Returns the specified string with any escape character preceding
         the first character removed. A fatal error is reported if the
         string ends with an escape character. -->
    <xsl:template name="unescape-first-char">
        <xsl:param name="str"/>

        <xsl:if test="$str">
            <xsl:variable name="first" select="substring($str, 1, 1)"/>
            <xsl:choose>
                <xsl:when test="$first = '\'">
                    <xsl:if test="string-length($str) = 1">
                        <xsl:message terminate="yes">
Found a string that contains an escape character ('\') with nothing after it.
                        </xsl:message>
                    </xsl:if>
                    <xsl:value-of select="substring($str, 2)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$str"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>


    <!-- Constant-related templates -->

    <!-- Outputs the value of the constant with the specified name. -->
    <xsl:template name="constant-value">
        <xsl:param name="name"/>

        <xsl:value-of select="$constants[@name=$name]/@value"/>
    </xsl:template>

    <!-- Outputs the values of the constants in the constant-list with the
         specified name. -->
    <xsl:template name="output-constant-list-across">
        <xsl:param name="name"/>
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:call-template name="output-constant-list-items">
            <xsl:with-param name="name" select="$name"/>
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="output-constant-list-down">
        <xsl:param name="name"/>
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

<!-- TODO: fix the values of these !!!
    - can't have an opening tag without a closing one and vice versa -->
        <xsl:variable name="prefix">
            <xsl:text>
        </xsl:text><xsl:value-of select="concat('&lt;li&gt;', $item-prefix)"/>
        </xsl:variable>
        <xsl:variable name="suffix" select="concat($item-suffix, '&lt;/li&gt;')"/>

        <ul>
            <xsl:call-template name="output-constant-list-items">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="separator" select="$separator"/>
                <xsl:with-param name="last-separator" select="$last-separator"/>
                <xsl:with-param name="item-prefix" select="$prefix"/>
                <xsl:with-param name="item-suffix" select="$suffix"/>
            </xsl:call-template>
        </ul>
    </xsl:template>

    <!-- Outputs the items in a constant-list, including separators. -->
    <xsl:template name="output-constant-list-items">
        <xsl:param name="name"/>
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="list"
            select="$constant-lists[@name=$name]"/>
        <xsl:variable name="num-items" select="count($list/*)"/>

        <!-- Note: the applied templates output the appropriate separator
             BEFORE each item, not after. -->
        <xsl:choose>
            <xsl:when test="$num-items = 0"/> <!-- output nothing -->
            <xsl:when test="$num-items = 1">
                <xsl:apply-templates select="$list/*[1]"
                    mode="output-single-constant-list-item">
                    <xsl:with-param name="separator" select="$separator"/>
                    <xsl:with-param name="last-separator" select="$last-separator"/>
                    <xsl:with-param name="item-prefix" select="$item-prefix"/>
                    <xsl:with-param name="item-suffix" select="$item-suffix"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <!-- There are at least 2 list items. -->
                <xsl:apply-templates select="$list/*[1]"
                    mode="output-first-constant-list-item">
                    <xsl:with-param name="separator" select="$separator"/>
                    <xsl:with-param name="last-separator" select="$last-separator"/>
                    <xsl:with-param name="item-prefix" select="$item-prefix"/>
                    <xsl:with-param name="item-suffix" select="$item-suffix"/>
                </xsl:apply-templates>
                <xsl:apply-templates
                    select="$list/*[position() &gt; 1 and position() &lt; last()]"
                    mode="output-middle-constant-list-item">
                    <xsl:with-param name="separator" select="$separator"/>
                    <xsl:with-param name="last-separator" select="$last-separator"/>
                    <xsl:with-param name="item-prefix" select="$item-prefix"/>
                    <xsl:with-param name="item-suffix" select="$item-suffix"/>
                </xsl:apply-templates>
                <xsl:apply-templates select="$list/*[position() = last()]"
                    mode="output-last-constant-list-item">
                    <xsl:with-param name="separator" select="$separator"/>
                    <xsl:with-param name="last-separator" select="$last-separator"/>
                    <xsl:with-param name="item-prefix" select="$item-prefix"/>
                    <xsl:with-param name="item-suffix" select="$item-suffix"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="constant" mode="output-single-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constants[@name=$name]"/>

        <xsl:value-of select="concat($item-prefix, $def/@value, $item-suffix)"/>
    </xsl:template>

    <xsl:template match="constant-list" mode="output-single-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:call-template name="output-constant-list-items">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="constant" mode="output-first-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constants[@name=$name]"/>

        <!-- Output the constant's value without any preceding separator. -->
        <xsl:value-of select="concat($item-prefix, $def/@value, $item-suffix)"/>
    </xsl:template>

    <xsl:template match="constant-list" mode="output-first-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constant-lists[@name=$name]"/>

        <!-- Output the list's first item as the first item of the parent
             list, then output the remaining items (if any) as middle
             items in the parent list.

             Note: constant-lists must have at least one child. -->
        <xsl:apply-templates select="$def/*[1]"
            mode="output-first-constant-list-item">
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="$def/*[position() != 1]"
            mode="output-middle-constant-list-item">
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="constant" mode="output-middle-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constants[@name=$name]"/>

        <!-- Output the constant's value preceded by the separator. -->
        <xsl:value-of select="concat($separator, $item-prefix,
                                     $def/@value, $item-suffix)"/>
    </xsl:template>

    <xsl:template match="constant-list" mode="output-middle-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constant-lists[@name=$name]"/>

        <!-- Output each item in the list as a middle item in the parent
             list. -->
        <xsl:apply-templates select="$def/*"
            mode="output-middle-constant-list-item">
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="constant" mode="output-last-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constants[@name=$name]"/>

        <!-- Output the constant's value preceded by the last-separator. -->
        <xsl:value-of select="concat($last-separator, $item-prefix,
                                     $def/@value, $item-suffix)"/>
    </xsl:template>

    <xsl:template match="constant-list" mode="output-last-constant-list-item">
        <xsl:param name="separator"/>
        <xsl:param name="last-separator"/>
        <xsl:param name="item-prefix"/>
        <xsl:param name="item-suffix"/>

        <xsl:variable name="name" select="@name"/>
        <xsl:variable name="def"
            select="$constant-lists[@name=$name]"/>

        <!-- Output all but the last item in the list as a middle item in
             the parent list, and output the last item in the list as the
             last item in the parent list.

             Note: constant-lists must have at least one child. -->
        <xsl:apply-templates select="$def/*[position() != last()]"
            mode="output-middle-constant-list-item">
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:apply-templates>
        <xsl:apply-templates select="$def/*[last()]"
            mode="output-last-constant-list-item">
            <xsl:with-param name="separator" select="$separator"/>
            <xsl:with-param name="last-separator" select="$last-separator"/>
            <xsl:with-param name="item-prefix" select="$item-prefix"/>
            <xsl:with-param name="item-suffix" select="$item-suffix"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- Report errors for unexpected constant-list children -->
    <xsl:template match="*" mode="output-single-constant-list-item">
        <xsl:call-template name="report-invalid-constant-list-item"/>
    </xsl:template>
    <xsl:template match="*" mode="output-first-constant-list-item">
        <xsl:call-template name="report-invalid-constant-list-item"/>
    </xsl:template>
    <xsl:template match="*" mode="output-constant-list-item">
        <xsl:call-template name="report-invalid-constant-list-item"/>
    </xsl:template>
    <xsl:template match="*" mode="output-last-constant-list-item">
        <xsl:call-template name="report-invalid-constant-list-item"/>
    </xsl:template>
    <xsl:template name="report-invalid-constant-list-item">
        <xsl:message terminate="yes">
Unexpected constant-list child element named <xsl:value-of select="local-name()"/> found</xsl:message>
    </xsl:template>


    <!-- Utility templates -->

    <!-- Indicates whether the specified type of construct can be
         aliased. Returns true if it can be, false if it can't be,
         and terminates with an error if the specified type is not
         recognized as a defined construct type. -->
    <xsl:template name="construct-can-be-aliased">
        <xsl:param name="construct-type"/>

        <xsl:variable name="construct"
            select="key('construct', $construct-type)"/>
        <xsl:choose>
            <xsl:when test="$construct">
                <xsl:apply-templates select="$construct"
                    mode="construct-can-be-aliased"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
The construct type '<xsl:value-of select="$construct-type"/>' was passed to the 'construct-can-be-aliased' template, but it is not recognized as a defined construct type.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct"
        mode="construct-can-be-aliased">
        <xsl:value-of select="false()"/>
    </xsl:template>

    <xsl:template match="*" mode="construct-can-be-aliased">
        <xsl:value-of select="true()"/>
    </xsl:template>


    <!-- Indicates whether the specified 'subconstruct' element
         represents a line construct or a non-line construct. -->
    <xsl:template name="is-subconstruct-line-construct">
        <xsl:param name="subconstruct" select="."/>

        <xsl:variable name="construct"
            select="key('construct', $subconstruct/@type)"/>

        <xsl:call-template name="is-line-construct">
            <xsl:with-param name="construct" select="$construct"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Indicates whether the specified construct node represents the
         type of construct that consists of one or more whole lines.
         Returns true if it does, false if it doesn't, and terminates with
         an error if the node isn't recognized as a construct node. -->
    <xsl:template name="is-line-construct">
        <xsl:param name="construct" select="."/>

        <xsl:apply-templates select="$construct" mode="is-line-construct"/>
    </xsl:template>

    <xsl:template match="construct | choice-construct |
                         single-token-construct | repeated-construct"
        mode="is-line-construct">
        <xsl:value-of select="false()"/>
    </xsl:template>

    <xsl:template match="single-line-construct | multiline-construct |
                         compound-construct | line-choice-construct |
                         first-line | indented-subconstructs"
        mode="is-line-construct">
        <xsl:value-of select="true()"/>
    </xsl:template>

    <xsl:template match="alias-construct" mode="is-line-construct">
        <xsl:variable name="type" select="@aliased-construct"/>
        <xsl:apply-templates select="key('construct', $type)"
            mode="is-line-construct"/>
    </xsl:template>

    <xsl:template match="*" mode="is-line-construct">
        <xsl:message terminate="yes">
A node with local name <xsl:value-of select="local-name()"/> was passed to the template
named 'is-line-construct', but that type of node does not describe a construct.
        </xsl:message>
    </xsl:template>
</xsl:transform>
