<?xml version="1.0"?>
<!--
    Defines templates that render language grammar rules in HTML.

    Copyright (C) 2004-2014 by James MacKay.

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

    <xsl:import href="document-common.html.xsl"/>

    <xsl:output method="html"/>


    <!-- Configuration -->

    <!-- The link ID to use as the base for links to constructs that
         appear in grammar rules. -->
    <xsl:variable name="grammar.construct-link-id"
        select="'Grammar'"/>

    <xsl:variable name="grammar.def-op" select="'::='"/>
    <xsl:variable name="grammar.or" select="' | '"/>


    <!-- Character class grammar rules -->

    <xsl:template match="character-class" mode="grammar.rule">
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
            <span class="plack-nonterminal"><xsl:value-of select="@name"/></span>
            <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
            <xsl:apply-templates select="*" mode="grammar.character-class-part"/>
        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="choice" mode="grammar.character-class-part">
        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content">
                <xsl:for-each select="*">
                    <xsl:apply-templates select="."
                        mode="grammar.character-class-part"/>
                    <xsl:if test="position() != last()">
                        <xsl:value-of select="$grammar.or"/>
                    </xsl:if>
                </xsl:for-each>
            </xsl:with-param>
            <xsl:with-param name="number">
                <xsl:call-template name="grammar.node-number">
                    <xsl:with-param name="node" select="."/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="single-character-choice"
        mode="grammar.character-class-part">
        <xsl:call-template name="grammar.single-character-choice-part">
            <xsl:with-param name="characters" select="@list"/>
        </xsl:call-template>
    </xsl:template>

    <!-- By default the parts of a character class definition are output
         the same way as they would be if they were part of a construct
         definition. -->
    <xsl:template match="*" mode="grammar.character-class-part">
        <xsl:apply-templates select="." mode="grammar.construct-part"/>
    </xsl:template>


    <!-- Construct flag grammar rules -->

    <!-- Matches construct flag set definitions, and is used to build
         construct modifiers definitions. -->
    <xsl:template match="flag-set-definition" mode="grammar.rule">
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="@name"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
        <xsl:call-template name="grammar.flags-choice">
            <xsl:with-param name="flag-nodes" select="flag"/>
        </xsl:call-template>
        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs a choice between the flags specified by the specified
         nodes. -->
    <xsl:template name="grammar.flags-choice">
        <xsl:param name="flag-nodes"/>

        <xsl:if test="$flag-nodes">
            <xsl:variable name="first" select="$flag-nodes[1]"/>
            <xsl:variable name="rest" select="$flag-nodes[position()!=1]"/>

            <!-- TODO: currently we assume that construct flags map to
                 tokens, which is not necessarily the case (unless we decide
                 to require it ...). -->
            <xsl:call-template name="grammar.as-terminal-by-name">
                <xsl:with-param name="terminal-name" select="$first/@name"/>
            </xsl:call-template>

            <xsl:if test="$rest">
                <xsl:value-of select="$grammar.or"/>
                <xsl:call-template name="grammar.flags-choice">
                    <xsl:with-param name="flag-nodes" select="$rest"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>


    <!-- Construct grammar rules -->

    <xsl:template match="alias-construct" mode="grammar.rule">
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="@type"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
        <xsl:call-template name="grammar.make-construct-link">
            <xsl:with-param name="type" select="@aliased-construct"/>
        </xsl:call-template>
        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="construct" mode="grammar.rule">
        <xsl:call-template name="grammar.simple-definition">
            <xsl:with-param name="type-name" select="@type"/>
            <xsl:with-param name="parts" select="*"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="single-line-construct" mode="grammar.rule">
        <xsl:call-template name="grammar.simple-definition">
            <xsl:with-param name="type-name" select="@type"/>
            <xsl:with-param name="parts" select="*"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs a simple construct definition: one whose right-hand side
         consists of a single line. -->
    <xsl:template name="grammar.simple-definition">
        <xsl:param name="type-name"/>
        <xsl:param name="parts"/>

        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="$type-name"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="$type-name"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
        <xsl:apply-templates select="$parts" mode="grammar.construct-part"/>
        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <xsl:template match="multiline-construct" mode="grammar.rule">
        <xsl:call-template name="grammar.compound-definition">
            <xsl:with-param name="construct-name" select="@type"/>
            <xsl:with-param name="first-line-parts"
                select="first-line/*"/>
            <xsl:with-param name="subsequent-indent-adjustment"
                select="3"/>
            <xsl:with-param name="subsequent-subconstructs"
                select="indented-subconstructs/*"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="compound-construct" mode="grammar.rule">
        <xsl:call-template name="grammar.compound-definition">
            <xsl:with-param name="construct-name" select="@type"/>
            <xsl:with-param name="first-line-parts"
                select="subconstruct[1]"/>
            <xsl:with-param name="subsequent-indent-adjustment"
                select="0"/>
            <xsl:with-param name="subsequent-subconstructs"
                select="subconstruct[position() != 1]"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs a compound construct definition: one whose right-hans
         consists of multiple lines. -->
    <xsl:template name="grammar.compound-definition">
        <xsl:param name="construct-name"/>
        <xsl:param name="first-line-parts"/>
        <xsl:param name="subsequent-indent-adjustment"/>
        <xsl:param name="subsequent-subconstructs"/>

        <!-- The amount to indent the second and subsequent lines. -->
        <xsl:variable name="indent">
            <xsl:call-template name="grammar.rhs-indent">
                <xsl:with-param name="construct-name"
                    select="$construct-name"/>
                <xsl:with-param name="adjustment"
                    select="$subsequent-indent-adjustment"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="$construct-name"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Output the first line of the definition. -->
        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of
            select="$construct-name"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
        <xsl:apply-templates select="$first-line-parts"
            mode="grammar.construct-part"/>
        <xsl:text>
</xsl:text>

        <!-- Output the second and subsequent lines of the definition. -->
        <xsl:for-each select="$subsequent-subconstructs">
            <xsl:value-of select="$indent"/>
            <xsl:apply-templates select="." mode="grammar.construct-part"/>
            <xsl:if test="position() != last()">
                <xsl:text>
</xsl:text>
            </xsl:if>
        </xsl:for-each>

        </pre>
        </a>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct"
        mode="grammar.rule">
        <!-- The amount to indent the second and subsequent lines
             (if there are any). -->
        <xsl:variable name="indent">
            <xsl:call-template name="grammar.rhs-indent">
                <xsl:with-param name="construct-name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="@type"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>
        <xsl:for-each select="choice/*">
            <xsl:apply-templates select="." mode="grammar.construct-part"/>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$grammar.or"/>
                <xsl:if test="position() mod 3 = 0">
                    <xsl:text>
</xsl:text>
                    <xsl:value-of select="$indent"/>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
        </pre>
        </a>
        <xsl:text>
</xsl:text>

        <!-- Add a list of the subconstructs that all choices must have,
             if there are any such subconstructs. -->
        <xsl:if test="count(subconstruct) &gt; 0">
        <p>
        <strong>Note</strong>: every <xsl:value-of select="@type"/> construct
        has the following:
        <ul>
        <xsl:for-each select="flag-from-set">
            <li><xsl:call-template
                    name="grammar.choice-flag-from-set-list-item"/></li>
        </xsl:for-each>
        <xsl:for-each select="subconstruct">
            <li><xsl:call-template
                    name="grammar.choice-subconstruct-list-item"/></li>
        </xsl:for-each>
        </ul>
        </p>
        </xsl:if>
    </xsl:template>

    <!-- Outputs information about a set of flags that all choices in a
         choice construct can/must have. -->
    <xsl:template name="grammar.choice-flag-from-set-list-item">
        <xsl:param name="node" select="."/>

        <xsl:variable name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="set-name" select="$node/@name"/>

        <xsl:choose>
            <xsl:when test="$number = 'one'">
                <xsl:text>a </xsl:text>
                <xsl:value-of select="$set-name"/>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-one'">
                <xsl:text>an optional </xsl:text>
                <xsl:value-of select="$set-name"/>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-more'">
                <xsl:text>zero or more </xsl:text>
                <xsl:value-of select="$set-name"/>
                <xsl:text> flags</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'one-or-more'">
                <xsl:text>one or more </xsl:text>
                <xsl:value-of select="$set-name"/>
                <xsl:text> flags</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Invalid 'number of items' constant in a choice construct's
flag-from-set: "<xsl:value-of select="$number"/>"
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs information about a subconstruct that all choices in a
         choice construct can/must have. -->
    <xsl:template name="grammar.choice-subconstruct-list-item">
        <xsl:param name="node" select="."/>

        <xsl:variable name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="type" select="$node/@type"/>

        <xsl:choose>
            <xsl:when test="$number = 'one'">
                <xsl:text>a direct </xsl:text>
                <xsl:value-of select="$type"/>
                <xsl:text> subconstruct</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-one'">
                <xsl:text>an optional direct </xsl:text>
                <xsl:value-of select="$type"/>
                <xsl:text> subconstruct</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'zero-or-more'">
                <xsl:text>zero or more direct </xsl:text>
                <xsl:value-of select="$type"/>
                <xsl:text> subconstructs</xsl:text>
            </xsl:when>
            <xsl:when test="$number = 'one-or-more'">
                <xsl:text>one or more direct </xsl:text>
                <xsl:value-of select="$type"/>
                <xsl:text> subconstructs</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Invalid 'number of items' constant in a choice construct's
subconstruct: "<xsl:value-of select="$number"/>"
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template match="single-token-construct" mode="grammar.rule">
        <!-- The amount to indent the second and subsequent lines
             (if there are any). -->
        <xsl:variable name="indent">
            <xsl:call-template name="grammar.rhs-indent">
                <xsl:with-param name="construct-name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="@type"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>

        <!-- There should only be one child: a 'terminal' or a
             'terminal-choice'. -->
        <xsl:apply-templates select="*" mode="grammar.construct-part">
            <xsl:with-param name="indent" select="$indent"/>
        </xsl:apply-templates>
        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="repeated-construct" mode="grammar.rule">
        <xsl:variable name="link-name">
            <xsl:call-template name="grammar.make-link-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <a name="{$link-name}">
        <pre>
        <span class="plack-nonterminal"><xsl:value-of select="@type"/></span>
        <xsl:value-of select="concat(' ', $grammar.def-op, ' ')"/>

        <xsl:if test="@start-terminal">
            <xsl:call-template name="grammar.as-terminal-by-name">
                <xsl:with-param name="terminal-name"
                    select="@start-terminal"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content">
                <xsl:call-template name="grammar.subconstruct-part">
                    <xsl:with-param name="node" select="subconstruct"/>
                    <xsl:with-param name="number" select="'one'"/>
                </xsl:call-template>
                <xsl:if test="@separator-terminal">
                    <xsl:text>: </xsl:text>
                    <xsl:call-template name="grammar.as-terminal-by-name">
                        <xsl:with-param name="terminal-name"
                            select="@separator-terminal"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:with-param>
            <xsl:with-param name="number" select="subconstruct/@number"/>
        </xsl:call-template>

        <xsl:if test="@end-terminal">
            <xsl:call-template name="grammar.as-terminal-by-name">
                <xsl:with-param name="terminal-name"
                    select="@end-terminal"/>
            </xsl:call-template>
        </xsl:if>

        </pre>
        </a>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Templates that output construct parts. -->

    <!-- Ignore construct attributes. -->
    <xsl:template match="attribute" mode="grammar.construct-part"/>

    <xsl:template match="subconstruct" mode="grammar.construct-part">
        <xsl:variable name="parent-name" select="local-name(..)"/>
        <xsl:variable name="is-line-subconstruct"
            select="$parent-name = 'compound-construct' or $parent-name = 'indented-subconstructs'"/>
        <xsl:variable name="can-be-multiple"
            select="@number and (@number = 'zero-or-more' or @number = 'one-or-more')"/>

        <xsl:variable name="plain-num">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="."/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="num">
            <xsl:choose>
                <xsl:when test="$can-be-multiple and $is-line-subconstruct">
                    <xsl:value-of select="concat($plain-num, '-per-line')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$plain-num"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="grammar.subconstruct-part">
            <xsl:with-param name="node" select="."/>
            <xsl:with-param name="number" select="$num"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="grammar.construct-part">
        <xsl:call-template name="grammar.flag-from-set-part"/>
    </xsl:template>

    <xsl:template match="terminal-choice" mode="grammar.construct-part">
        <xsl:param name="indent"/>

        <!-- Note: each child should be a 'terminal' element. -->
        <xsl:for-each select="*">
            <xsl:apply-templates select="." mode="grammar.construct-part"/>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$grammar.or"/>
                <xsl:if test="position() mod 12 = 0">
                    <xsl:text>
</xsl:text>
                    <xsl:value-of select="$indent"/>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="terminal" mode="grammar.construct-part">
        <xsl:call-template name="grammar.terminal-part"/>
    </xsl:template>

    <xsl:template match="char-from-class" mode="grammar.construct-part">
        <xsl:call-template name="grammar.char-from-class-part"/>
    </xsl:template>

    <xsl:template match="choice" mode="grammar.construct-part">
        <xsl:apply-templates select="." mode="grammar.character-class-part"/>
    </xsl:template>

    <xsl:template match="chars" mode="grammar.construct-part">
        <xsl:call-template name="grammar.chars-part"/>
    </xsl:template>

    <xsl:template match="space" mode="grammar.construct-part">
        <!-- Output all spaces unless they're disallowed. -->
        <xsl:if test="not(@type) or @type != 'disallowed'">
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>


    <!-- Construct and character class part output templates -->

    <xsl:template name="grammar.single-character-choice-part">
        <xsl:param name="characters"/>

        <!-- Set 'chars' to 'characters' with any escape character
             preceding the first character removed. -->
        <xsl:variable name="chars">
            <xsl:call-template name="unescape-first-char">
                <xsl:with-param name="str" select="$characters"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="$chars">
            <xsl:variable name="rest" select="substring($chars, 2)"/>

            <span class="plack-single-character"><xsl:value-of
                select="substring($chars, 1, 1)"/></span>
            <xsl:if test="$rest">
                <xsl:value-of select="$grammar.or"/>
                <xsl:call-template name="grammar.single-character-choice-part">
                    <xsl:with-param name="characters" select="$rest"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the character (from a character class) described by the
         specified node as part of a construct. -->
    <xsl:template name="grammar.char-from-class-part">
        <xsl:param name="node" select="."/>
        <xsl:param name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:param>

        <xsl:variable name="nonterm">
            <xsl:call-template name="grammar.as-nonterminal">
                <xsl:with-param name="value" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the subconstruct described by the specified node as
         part of a construct. -->
    <xsl:template name="grammar.subconstruct-part">
        <xsl:param name="node" select="."/>
        <xsl:param name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:param>

        <xsl:variable name="nonterm">
            <xsl:call-template name="grammar.as-nonterminal">
                <xsl:with-param name="value" select="$node/@type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the terminal described by the specified node as part
         of a construct. -->
    <xsl:template name="grammar.terminal-part">
        <xsl:param name="node" select="."/>

        <xsl:variable name="term">
            <xsl:call-template name="grammar.as-terminal-by-name">
                <xsl:with-param name="terminal-name" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content" select="$term"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the 'flag from set' described by the specified node
         as part of a construct. -->
    <xsl:template name="grammar.flag-from-set-part">
        <xsl:param name="node" select="."/>

        <xsl:variable name="number">
            <xsl:call-template name="grammar.node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="nonterm">
            <xsl:call-template name="grammar.as-nonterminal">
                <xsl:with-param name="value" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the character(s) described by the specified node
         as part of a construct. -->
    <xsl:template name="grammar.chars-part">
        <xsl:param name="node" select="."/>

        <!-- Get the value of the text attribute with all of the
             escapes removed. -->
        <xsl:variable name="chars">
            <xsl:call-template name="remove-all-escapes">
                <xsl:with-param name="str" select="$node/@text"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="grammar.apply-number">
            <xsl:with-param name="content">
                <span class="plack-single-character"><xsl:value-of
                    select="$chars"/></span>
            </xsl:with-param>
            <xsl:with-param name="number">
                <xsl:call-template name="grammar.node-number">
                    <xsl:with-param name="node" select="."/>
                </xsl:call-template>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- Miscellaneous templates -->

    <!-- Returns the value of the specified node's 'number' attribute, or
         the specified default value iff it has no 'number' attribute. -->
    <xsl:template name="grammar.node-number">
        <xsl:param name="node"/>
        <xsl:param name="default-number" select="'one'"/>

        <!-- There should only be one node: we use for-each here
             to make it the current node. -->
        <xsl:for-each select="$node">
            <xsl:choose>
                <xsl:when test="@number">
                    <xsl:value-of select="@number"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$default-number"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Applies the formatting to the specified content appropriate to
         the specified number (which is one of the number of items
         constants: 'one', 'zero-or-one', etc.).

         Iff 'explicit-one' is true the fact that the number is 'one'
         will be indicated explicitly by the formatting. -->
    <xsl:template name="grammar.apply-number">
        <xsl:param name="content"/>
        <xsl:param name="number"/>
        <xsl:param name="explicit-one" select="false()"/>

        <xsl:choose>
            <xsl:when test="$number='one'">
                <xsl:choose>
                    <xsl:when test="$explicit-one">
                        <xsl:text/>{<xsl:copy-of select="$content"/>}<xsl:text/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:copy-of select="$content"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$number='zero-or-one'">
                <xsl:text/>[<xsl:copy-of select="$content"/>]<xsl:text/>
            </xsl:when>
            <xsl:when test="$number='zero-or-more'">
                <xsl:text/>{<xsl:copy-of select="$content"/>}*<xsl:text/>
            </xsl:when>
            <xsl:when test="$number='one-or-more'">
                <xsl:text/>{<xsl:copy-of select="$content"/>}+<xsl:text/>
            </xsl:when>
            <xsl:when test="$number='one-or-more-per-line'">
                <xsl:text/>{<xsl:copy-of select="$content"/>}@<xsl:text/>
            </xsl:when>
            <xsl:when test="$number='zero-or-more-per-line'">
                <xsl:text/>[{<xsl:copy-of select="$content"/>}@]<xsl:text/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">
Invalid 'number of items' constant: "<xsl:value-of select="$number"/>"
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="grammar.as-terminal-by-name">
        <xsl:param name="terminal-name"/>

        <xsl:call-template name="grammar.terminal-text">
            <xsl:with-param name="name" select="$terminal-name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="grammar.as-terminal">
        <xsl:param name="value"/>
        <span class="plack-terminal"><xsl:value-of select="$value"/></span>
    </xsl:template>

    <xsl:template name="grammar.as-nonterminal">
        <xsl:param name="value"/>

        <xsl:call-template name="grammar.make-construct-link">
            <xsl:with-param name="type" select="$value"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="grammar.terminal-text">
        <xsl:param name="name"/>

        <xsl:variable name="terminal-node"
            select="//tokens/section/*[@name=$name]"/>

        <!-- There should only be one terminal-node: we use for-each
             here to make it the current node. -->
        <xsl:for-each select="$terminal-node">
            <xsl:choose>
                <xsl:when test="@text">
                    <xsl:call-template name="grammar.as-terminal">
                        <xsl:with-param name="value" select="@text"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <!-- If the terminal doesn't have a 'text' attribute
                         then it must have a 'text' child element. -->
                    <xsl:apply-templates select="text/*"
                        mode="grammar.construct-part"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>


    <!-- Makes a link to the grammar definition of the specified type of
         construct (construct flag, character class, etc.). -->
    <xsl:template name="grammar.make-construct-link">
        <xsl:param name="type"/>
        <xsl:param name="text" select="$type"/>

        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="$grammar.construct-link-id"/>
            <xsl:with-param name="fragmentId" select="$type"/>
            <xsl:with-param name="text">
                <span class="plack-nonterminal"><xsl:value-of select="$type"/></span>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- Makes the name of a link to the grammar definition of the specified
         type of construct (construct flag, character class, etc.).

         The name itself is used if the grammar.construct-link-id is the
         ID of the grammar document: otherwise it is the name with a suffix
         appended to it (so that it doesn't conflict with the names that
         might be in use in the non-grammar document whose Id is given by
         grammar.construct-link-id). -->
    <xsl:template name="grammar.make-link-name">
        <xsl:param name="name"/>

        <xsl:choose>
            <xsl:when test="$grammar.construct-link-id = 'Grammar'">
                <xsl:value-of select="$name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($name, 'GrammarRule')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the spaces to use to indent each line in the right-hand
         side of the definition of the construct with the specified name,
         adjusted by the specified amount. -->
    <xsl:template name="grammar.rhs-indent">
        <xsl:param name="construct-name"/>
        <xsl:param name="adjustment" select="0"/>

        <xsl:call-template name="copies">
            <xsl:with-param name="str" select="' '"/>
            <xsl:with-param name="number"
                select="string-length($construct-name) +
                        string-length($grammar.def-op) + 2 + $adjustment"/>
        </xsl:call-template>
    </xsl:template>
</xsl:transform>
