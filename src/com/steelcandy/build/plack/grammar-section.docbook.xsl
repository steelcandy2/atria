<?xml version="1.0"?>
<!--
    $Id: grammar-section.docbook.xsl,v 1.4 2004/06/03 04:07:07 jgm Exp $

    Transforms a language description document into a part of a
    DocBook XML document that consists of a section element that
    describes the language's grammar.

    Author: James MacKay
    Last Updated: $Date: 2004/06/03 04:07:07 $

    Copyright (C) 2002-2014 by James MacKay.

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

    <xsl:import href="language-common.xsl"/>

    <xsl:output method="xml"/>

    <!-- Configuration -->

    <xsl:variable name="reserved-words-section-title"
        select="'Reserved Words'"/>
    <xsl:variable name="reserved-words-per-row" select="5"/>

    <xsl:variable name="author" select="'James MacKay'"/>
    <xsl:variable name="e-mail" select="'jmackay@steelcandy.com'"/>

    <xsl:variable name="spaces-per-indent-level" select="4"/>

    <xsl:variable name="def-op" select="'::='"/>
    <xsl:variable name="or" select="'|'"/>


    <!-- Global variables -->

    <xsl:variable name="section-titles">
        <xsl:call-template name="section-titles"/>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:variable name="lang" select="@name"/>
        <section>
            <sectioninfo>
                <abstract>
                    <para><xsl:value-of select="$lang"/> Grammar</para>
                </abstract>
            </sectioninfo>
        <title id="Top"><xsl:value-of select="$lang"/> Grammar</title>
        <!--
            Note: this file was automatically generated, and so should
                  not be edited directly.
        -->
        <para>
The following is a summary of the grammar of the <xsl:value-of
select="$lang"/> programming language. Terminal symbols look like
<xsl:call-template name="as-terminal">
    <xsl:with-param name="value" select="'this'"/>
</xsl:call-template> and nonterminals look like
<xsl:call-template name="as-nonterminal">
    <xsl:with-param name="value" select="'this'"/>
</xsl:call-template>. A nonterminal being defined appears to the left of a
nonterminal definition operator '<xsl:value-of select="$def-op"/>', and the
nonterminal's definition appears to the right of the definition operator.
</para><para id="DefPart">
<xsl:variable name="def-part">
    <xsl:call-template name="as-nonterminal">
        <xsl:with-param name="value" select="'DefPart'"/>
    </xsl:call-template>
</xsl:variable>
<xsl:variable name="eg-sep">
    <xsl:call-template name="as-terminal">
        <xsl:with-param name="value" select="'separator'"/>
    </xsl:call-template>
</xsl:variable>
<xsl:variable name="sep-def-part">
    <xsl:copy-of select="$def-part"/>:
    <xsl:copy-of select="$eg-sep"/>
</xsl:variable>
Several shorthands are employed in this grammar, most of which have a
slightly nonstandard representation. For a part of a definition
<xsl:copy-of select="$def-part"/> and a terminal <xsl:copy-of
select="$eg-sep"/>
<itemizedlist mark="bullet">
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$def-part"/>
            <xsl:with-param name="number" select="'zero-or-one'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/>
        is optional: it can appear or not appear (though not more than once)
        in an instance of the construct</para></listitem>
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$def-part"/>
            <xsl:with-param name="number" select="'zero-or-more'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
        appear zero or more times</para></listitem>
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$sep-def-part"/>
            <xsl:with-param name="number" select="'zero-or-more'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
        be repeated zero or more times, separated by <xsl:copy-of
        select="$eg-sep"/>s (so there will be a <xsl:copy-of
        select="$eg-sep"/> between any two consecutive <xsl:copy-of
        select="$def-part"/>s, but not before the first or after the last
        <xsl:copy-of select="$def-part"/>)</para></listitem>
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$def-part"/>
            <xsl:with-param name="number" select="'one-or-more'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
        be repeated one or more times (but must appear at least
        once)</para></listitem>
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$sep-def-part"/>
            <xsl:with-param name="number" select="'one-or-more'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
        be repeated one or more times (but must appear at least once),
        separated by <xsl:copy-of select="$eg-sep"/>s (so there will be a
        <xsl:copy-of select="$eg-sep"/> between any two consecutive
        <xsl:copy-of select="$def-part"/>s, but not before the first or after
        the last <xsl:copy-of select="$def-part"/>)</para></listitem>
    <listitem><para><xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$def-part"/>
            <xsl:with-param name="number" select="'one-or-more-per-line'"/>
        </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
        be repeated one or more times (but must appear at least once), with
        the second and subsequent <xsl:copy-of select="$def-part"/>s on
        consecutive lines (ignoring blank and comment lines) immediately after
        the line that the first <xsl:copy-of select="$def-part"/> is on, and
        indented exactly the same number of levels as the first
        <xsl:copy-of select="$def-part"/>'s line. (In this case a
        <xsl:copy-of select="$def-part"/> must be the only construct on a
        line)</para></listitem>
</itemizedlist>
Any appearance of any part of these shorthands as terminals are not part of
one of these shorthands, but are part of the language construct being
described.
</para><para>
The grammar is divided into the following sections:
<xsl:call-template name="table-of-contents"/>
</para>
<xsl:apply-templates select="construct-flag-sets"/>
<xsl:apply-templates select="constructs"/>
<xsl:apply-templates select="character-classes"/>
<xsl:call-template name="reserved-words"/>
</section>
    </xsl:template>

    <!-- Outputs the table of contents. -->
    <xsl:template name="table-of-contents">
        <itemizedlist mark="bullet">
            <xsl:call-template name="toc-section-entries">
                <xsl:with-param name="titles" select="$section-titles"/>
            </xsl:call-template>
        </itemizedlist>
    </xsl:template>

    <!-- Outputs table of content entries for the sections with the
         specified (bar-separated) titles. -->
    <xsl:template name="toc-section-entries">
        <xsl:param name="titles"/>

        <xsl:if test="$titles">
            <xsl:variable name="first-title"
                select="substring-before($titles, '|')"/>
            <xsl:variable name="rest"
                select="substring-after($titles, '|')"/>
            <listitem><para><xsl:call-template name="section-link">
                    <xsl:with-param name="title" select="$first-title"/>
                </xsl:call-template></para></listitem>
            <xsl:call-template name="toc-section-entries">
                <xsl:with-param name="titles" select="$rest"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Returns a bar-separated (|) list of the titles of all of the
         sections of the grammar document. There will also be a bar
         after the last element in the list. -->
    <xsl:template name="section-titles">
        <xsl:variable name="titles0">
            <xsl:call-template name="append-section-titles">
                <xsl:with-param name="sections"
                    select="//construct-flag-sets/section"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="titles1">
            <xsl:call-template name="append-section-titles">
                <xsl:with-param name="sections"
                    select="//constructs/section"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="titles2">
            <xsl:call-template name="append-section-titles">
                <xsl:with-param name="sections"
                    select="//character-classes/section"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="titles3">
            <xsl:value-of select="$reserved-words-section-title"/>|<xsl:text/>
        </xsl:variable>

        <xsl:value-of select="concat($titles0, $titles1, $titles2, $titles3)"/>
    </xsl:template>

    <!-- Appends the titles of the specified sections to the specified
         bar-separated list of section titles. -->
    <xsl:template name="append-section-titles">
        <xsl:param name="sections"/>
        <xsl:param name="titles" select="''"/>

        <xsl:choose>
            <xsl:when test="$sections">
                <xsl:variable name="first-title"
                    select="$sections[1]/@title"/>
                <xsl:variable name="rest" select="$sections[position()!=1]"/>
                <xsl:call-template name="append-section-titles">
                    <xsl:with-param name="sections" select="$rest"/>
                    <xsl:with-param name="titles"
                        select="concat($titles, $first-title, '|')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$titles"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns a link to the section of the grammar document with the
         specified title. -->
    <xsl:template name="section-link">
        <xsl:param name="title"/>

        <xsl:variable name="url"
            select="translate($title, ' ', '_')"/>
        <xsl:call-template name="make-link">
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="text" select="$title"/>
        </xsl:call-template>
   </xsl:template>


    <!-- Character classes -->

    <xsl:template match="character-classes">
        <xsl:call-template name="sections-in"/>
    </xsl:template>

    <xsl:template match="construct-flag-sets">
        <xsl:call-template name="sections-in"/>
    </xsl:template>

    <xsl:template match="constructs">
        <xsl:call-template name="sections-in"/>
    </xsl:template>

    <!-- Outputs the sections directly under the specified node. -->
    <xsl:template name="sections-in">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/section">
            <xsl:variable name="title" select="@title"/>
            <xsl:variable name="url" select="translate($title, ' ', '_')"/>
            <section id="{$url}">
                <title><xsl:value-of select="$title"/></title>
            <xsl:apply-templates/>
            </section>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="character-class">
        <anchor id="{@name}"/>
        <literallayout class="monospaced">
        <xsl:value-of select="@name"/>
        <xsl:value-of select="concat(' ', $def-op, ' ')"/>
        <xsl:apply-templates select="*" mode="character-class-part"/>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="choice" mode="character-class-part">
        <xsl:for-each select="*">
            <xsl:apply-templates select="." mode="character-class-part"/>
            <xsl:if test="position() != last()">
                <xsl:text> | </xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="single-character-choice" mode="character-class-part">
        <xsl:call-template name="single-character-choice-part">
            <xsl:with-param name="characters" select="@list"/>
        </xsl:call-template>
    </xsl:template>

    <!-- By default the parts of a character class definition are output
         the same way as they would be if they were part of a construct
         definition. -->
    <xsl:template match="*" mode="character-class-part">
        <xsl:apply-templates select="." mode="construct-part"/>
    </xsl:template>


    <!-- Matches construct flag set definitions, and is used to build
         construct modifiers definitions. -->
    <xsl:template match="flag-set-definition">
        <anchor id="{@name}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="@name"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>
            <xsl:call-template name="flags-choice">
                <xsl:with-param name="flag-nodes" select="flag"/>
            </xsl:call-template>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs a choice between the flags specified by the specified
         nodes. -->
    <xsl:template name="flags-choice">
        <xsl:param name="flag-nodes"/>

        <xsl:if test="$flag-nodes">
            <xsl:variable name="first" select="$flag-nodes[1]"/>
            <xsl:variable name="rest" select="$flag-nodes[position()!=1]"/>

            <!-- TODO: currently we assume that construct flags map to
                 tokens, which is not necessarily the case (unless we decide
                 to require it ...). -->
            <xsl:call-template name="as-terminal-by-name">
                <xsl:with-param name="terminal-name" select="$first/@name"/>
            </xsl:call-template>

            <xsl:if test="$rest">
                <xsl:text> | </xsl:text>
                <xsl:call-template name="flags-choice">
                    <xsl:with-param name="flag-nodes" select="$rest"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>


    <!-- Templates that output the definitions of the various types of
         constructs, and ancillary templates. -->

    <xsl:template match="alias-construct">
        <anchor id="{@type}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="@type"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>
            <link linkend="{@alias-construct}">
                <xsl:value-of select="@alias-construct"/>
            </link>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="construct">
        <xsl:call-template name="simple-definition">
            <xsl:with-param name="type-name" select="@type"/>
            <xsl:with-param name="parts" select="*"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="single-line-construct">
        <xsl:call-template name="simple-definition">
            <xsl:with-param name="type-name" select="@type"/>
            <xsl:with-param name="parts" select="*"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs a simple construct definition: one whose right-hans side
         consists of a single line. -->
    <xsl:template name="simple-definition">
        <xsl:param name="type-name"/>
        <xsl:param name="parts"/>

        <anchor id="{$type-name}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="$type-name"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>
            <xsl:apply-templates select="$parts" mode="construct-part"/>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <xsl:template match="multiline-construct">
        <xsl:call-template name="compound-definition">
            <xsl:with-param name="construct-name" select="@type"/>
            <xsl:with-param name="first-line-parts"
                select="first-line/*"/>
            <xsl:with-param name="subsequent-indent-adjustment"
                select="3"/>
            <xsl:with-param name="subsequent-subconstructs"
                select="indented-subconstructs/*"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="compound-construct">
        <xsl:call-template name="compound-definition">
            <xsl:with-param name="construct-name" select="@type"/>
            <xsl:with-param name="first-line-parts"
                select="subconstruct[1]"/>
            <xsl:with-param name="subsequent-indent-adjustment"
                select="0"/>
            <xsl:with-param name="subsequent-subconstructs"
                select="subconstruct[position() != 1]"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs a compound construct definition: one whose right-hand
         side consists of multiple lines. -->
    <xsl:template name="compound-definition">
        <xsl:param name="construct-name"/>
        <xsl:param name="first-line-parts"/>
        <xsl:param name="subsequent-indent-adjustment"/>
        <xsl:param name="subsequent-subconstructs"/>

        <!-- The amount to indent the second and subsequent lines. -->
        <xsl:variable name="indent">
            <xsl:call-template name="rhs-indent">
                <xsl:with-param name="construct-name"
                    select="$construct-name"/>
                <xsl:with-param name="adjustment"
                    select="$subsequent-indent-adjustment"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Output the first line of the definition. -->
        <anchor id="{$construct-name}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="$construct-name"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>
            <xsl:apply-templates select="$first-line-parts" mode="construct-part"/>
            <xsl:text>
</xsl:text>

            <!-- Output the second and subsequent lines of the definition. -->
            <xsl:for-each select="$subsequent-subconstructs">
                <xsl:value-of select="$indent"/>
                <xsl:apply-templates select="." mode="construct-part"/>
                <xsl:if test="position() != last()">
                    <xsl:text>
</xsl:text>
                </xsl:if>
            </xsl:for-each>
        </literallayout>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct">
        <!-- The amount to indent the second and subsequent lines
             (if there are any). -->
        <xsl:variable name="indent">
            <xsl:call-template name="rhs-indent">
                <xsl:with-param name="construct-name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>

        <anchor id="{@type}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="@type"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>
            <xsl:for-each select="choice/*">
                <xsl:apply-templates select="." mode="construct-part"/>
                <xsl:if test="position() != last()">
                    <xsl:text> | </xsl:text>
                    <!-- The second part of the condition is an ugly little
                         hack to prevent choices of terminals from being
                         split into lines that are too short. -->
                    <xsl:if test="position() mod 4 = 0 and
                                  local-name() != 'terminal'">
                        <xsl:text>
</xsl:text>
                        <xsl:value-of select="$indent"/>
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="repeated-construct">
        <anchor id="{@type}"/>
        <literallayout class="monospaced">
            <xsl:value-of select="@type"/>
            <xsl:value-of select="concat(' ', $def-op, ' ')"/>

            <xsl:if test="@start-terminal">
                <xsl:call-template name="as-terminal-by-name">
                    <xsl:with-param name="terminal-name"
                        select="@start-terminal"/>
                </xsl:call-template>
            </xsl:if>

            <xsl:call-template name="apply-number">
                <xsl:with-param name="content">
                    <xsl:call-template name="subconstruct-part">
                        <xsl:with-param name="node" select="subconstruct"/>
                        <xsl:with-param name="number" select="'one'"/>
                    </xsl:call-template>
                    <if test="@separator-terminal">
                        <xsl:text>: </xsl:text>
                        <xsl:call-template name="as-terminal-by-name">
                            <xsl:with-param name="terminal-name"
                                select="@separator-terminal"/>
                        </xsl:call-template>
                    </xsl:if>
                </xsl:with-param>
                <xsl:with-param name="number" select="subconstruct/@number"/>
            </xsl:call-template>

            <xsl:if test="@end-terminal">
                <xsl:call-template name="as-terminal-by-name">
                    <xsl:with-param name="terminal-name"
                        select="@end-terminal"/>
                </xsl:call-template>
            </xsl:if>
        </literallayout>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Outputs the spaces to use to indent each line in the right-hand
         side of the definition of the construct with the specified name,
         adjusted by the specified amount. -->
    <xsl:template name="rhs-indent">
        <xsl:param name="construct-name"/>
        <xsl:param name="adjustment" select="0"/>

        <xsl:call-template name="copies">
            <xsl:with-param name="str" select="' '"/>
            <xsl:with-param name="number"
                select="string-length($construct-name) +
                        string-length($def-op) + 2 + $adjustment"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Templates that output construct parts. -->

    <!-- Ignore construct attributes. -->
    <xsl:template match="attribute" mode="construct-part"/>

    <xsl:template match="subconstruct" mode="construct-part">
        <xsl:call-template name="subconstruct-part"/>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="construct-part">
        <xsl:call-template name="flag-from-set-part"/>
    </xsl:template>

    <xsl:template match="terminal" mode="construct-part">
        <xsl:call-template name="terminal-part"/>
    </xsl:template>

    <xsl:template match="char-from-class" mode="construct-part">
        <xsl:call-template name="char-from-class-part"/>
    </xsl:template>

    <xsl:template match="chars" mode="construct-part">
        <xsl:call-template name="chars-part"/>
    </xsl:template>

    <xsl:template match="space" mode="construct-part">
        <!-- Output all spaces unless they're disallowed. -->
        <xsl:if test="not(@type) or @type != 'disallowed'">
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>


    <!-- Templates for outputting various construct and character
         class parts. -->

    <xsl:template name="single-character-choice-part">
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

            <xsl:value-of select="substring($chars, 1, 1)"/>
            <xsl:if test="$rest">
                <xsl:text> | </xsl:text>
                <xsl:call-template name="single-character-choice-part">
                    <xsl:with-param name="characters" select="$rest"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the character (from a character class) described by the
         specified node as part of a construct. -->
    <xsl:template name="char-from-class-part">
        <xsl:param name="node" select="."/>
        <xsl:param name="number">
            <xsl:call-template name="node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:param>

        <xsl:variable name="nonterm">
            <xsl:call-template name="as-nonterminal">
                <xsl:with-param name="value" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the subconstruct described by the specified node as
         part of a construct. -->
    <xsl:template name="subconstruct-part">
        <xsl:param name="node" select="."/>
        <xsl:param name="number">
            <xsl:call-template name="node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:param>

        <xsl:variable name="nonterm">
            <xsl:call-template name="as-nonterminal">
                <xsl:with-param name="value" select="$node/@type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the terminal described by the specified node as part
         of a construct. -->
    <xsl:template name="terminal-part">
        <xsl:param name="node" select="."/>

        <xsl:variable name="term">
            <xsl:call-template name="as-terminal-by-name">
                <xsl:with-param name="terminal-name" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="number">
            <xsl:call-template name="node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$term"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the 'flag from set' described by the specified node
         as part of a construct. -->
    <xsl:template name="flag-from-set-part">
        <xsl:param name="node" select="."/>

        <xsl:variable name="number">
            <xsl:call-template name="node-number">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="nonterm">
            <xsl:call-template name="as-nonterminal">
                <xsl:with-param name="value" select="$node/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="apply-number">
            <xsl:with-param name="content" select="$nonterm"/>
            <xsl:with-param name="number" select="$number"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the character(s) described by the specified node
         as part of a construct. -->
    <xsl:template name="chars-part">
        <xsl:param name="node" select="."/>

        <!-- Get the value of the text attribute with all of the
             escapes removed. -->
        <xsl:variable name="chars">
            <xsl:call-template name="remove-all-escapes">
                <xsl:with-param name="str" select="$node/@text"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="$chars"/>
    </xsl:template>


    <!-- Tokens -->

    <!-- Outputs the contents of the section that lists all of the
         language's reserved words (including operator reserved words). -->
    <xsl:template name="reserved-words">
        <xsl:variable name="title" select="$reserved-words-section-title"/>
        <xsl:variable name="url" select="translate($title, ' ', '_')"/>
        <xsl:variable name="word-nodes"
                select="//tokens/*/reserved-word | //tokens/*/reserved-word-operator"/>

        <section id="{$url}">
            <title>
                <xsl:value-of select="$title"/>
            </title>

            <informaltable frame="none" colsep="0" rowsep="0" pgwide="1">
                <tgroup cols="{$reserved-words-per-row}">
                    <tbody>
                        <xsl:call-template name="reserved-words-table-rows">
                            <xsl:with-param name="word-nodes"
                                select="$word-nodes"/>
                            <xsl:with-param name="words-per-row"
                                select="$reserved-words-per-row"/>
                        </xsl:call-template>
                    </tbody>
                </tgroup>
            </informaltable>
        </section>
    </xsl:template>

    <!-- Outputs the rows of the reserved words table for the reserved
         words represented by the specified nodes. -->
    <xsl:template name="reserved-words-table-rows">
        <xsl:param name="word-nodes"/>
        <xsl:param name="words-per-row"/>

        <xsl:variable name="this-row-word-nodes"
            select="$word-nodes[position() &lt;= $words-per-row]"/>
        <xsl:variable name="remaining-word-nodes"
            select="$word-nodes[position() &gt; $words-per-row]"/>
        <xsl:if test="$this-row-word-nodes">
            <row>
                <xsl:for-each select="$this-row-word-nodes">
                    <entry><xsl:call-template name="as-terminal">
                            <xsl:with-param name="value" select="@text"/>
                        </xsl:call-template></entry>
                </xsl:for-each>
            </row>
            <xsl:call-template name="reserved-words-table-rows">
                <xsl:with-param name="word-nodes"
                    select="$remaining-word-nodes"/>
                <xsl:with-param name="words-per-row"
                    select="$words-per-row"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Terminals and nonterminals -->

    <xsl:template match="term">
        <xsl:variable name="text">
            <xsl:call-template name="terminal-text">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="as-terminal">
            <xsl:with-param name="value" select="$text"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="nonterm">
        <xsl:call-template name="as-nonterminal">
            <xsl:with-param name="value" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="as-terminal-by-name">
        <xsl:param name="terminal-name"/>

        <xsl:variable name="text">
            <xsl:call-template name="terminal-text">
                <xsl:with-param name="name" select="$terminal-name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="as-terminal">
            <xsl:with-param name="value" select="$text"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="as-terminal">
        <xsl:param name="value"/>

        <xsl:value-of select="$value"/>
    </xsl:template>

    <xsl:template name="as-nonterminal">
        <xsl:param name="value"/>

        <link linkend="{$value}">
            <xsl:value-of select="$value"/>
        </link>
    </xsl:template>

    <xsl:template name="terminal-text">
        <xsl:param name="name"/>

        <xsl:variable name="terminal-node"
            select="//tokens/section/*[@name=$name]"/>

        <!-- There should only be one terminal-node: we use for-each
             here to make it the current node. -->
        <xsl:for-each select="$terminal-node">
            <xsl:choose>
                <xsl:when test="@text">
                    <xsl:value-of select="@text"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- Use the terminal's name if the terminal
                         doesn't have a text attribute. -->
                    <xsl:value-of select="@name"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Returns the value of the specified node's 'number' attribute, or
         the specified default value iff it has no 'number' attribute. -->
    <xsl:template name="node-number">
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
    <xsl:template name="apply-number">
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


    <!-- Notes, lists, etc. -->

    <xsl:template match="notes">
        <section>
            <title>Notes</title>
            <itemizedlist mark="bullet">
                <xsl:for-each select="note">
                    <xsl:if test="not(@hidden) or @hidden = 'false'">
                        <listitem><para><xsl:apply-templates/></para></listitem>
                    </xsl:if>
                </xsl:for-each>
            </itemizedlist>
        </section>
    </xsl:template>

    <xsl:template match="list">
        <itemizedlist mark="bullet">
            <xsl:for-each select="item">
                <listitem><para><xsl:apply-templates/></para></listitem>
            </xsl:for-each>
        </itemizedlist>
    </xsl:template>


    <!-- General templates -->

    <!-- Creates a link with the specified text that e-mails to the
         specified e-mail address. -->
    <xsl:template name="make-mailto-link">
        <xsl:param name="address"/>
        <xsl:param name="text" select="$address"/>

        <xsl:variable name="url">
            <xsl:text>mailto:</xsl:text><xsl:value-of select="$address"/>
        </xsl:variable>
        <xsl:call-template name="make-link">
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="text" select="$text"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Creates a link with the specified text (which may be a node
         set) that links to the specified URL. -->
    <xsl:template name="make-link">
        <xsl:param name="url"/>
        <xsl:param name="text"/>

        <link linkend="{$url}">
            <xsl:copy-of select="$text"/>
        </link>
    </xsl:template>

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
</xsl:transform>
