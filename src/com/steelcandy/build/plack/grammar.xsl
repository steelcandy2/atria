<?xml version="1.0"?>
<!--
    Transforms a language description document into an HTML document
    that describes the language's grammar.

    Copyright (C) 2002-2011 by James MacKay.

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
    <xsl:import href="operator-information.html.xsl"/>
    <xsl:import href="grammar-rules.html.xsl"/>

    <xsl:output method="html"/>


    <!-- Configuration -->

    <xsl:variable name="our-id" select="'Grammar'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Grammar')"/>

    <xsl:variable name="grammar.reserved-words-section-title"
        select="'Reserved Words'"/>
    <xsl:variable name="grammar.reserved-words-per-row" select="5"/>


    <!-- Global variables -->

    <xsl:variable name="content-filename">
        <xsl:call-template name="id-to-content-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="index-filename">
        <xsl:call-template name="id-to-index-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="grammar.section-titles">
        <xsl:call-template name="grammar.section-titles"/>
    </xsl:variable>
    <xsl:variable name="sections-toolbar">
        <xsl:call-template name="sections-toolbar">
            <xsl:with-param name="titles" select="$grammar.section-titles"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="grammar.content"/>
        <xsl:call-template name="grammar.index"/>
    </xsl:template>

    <xsl:template name="grammar.content">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$content-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="menu">
                <xsl:call-template name="language-document-menu">
                    <xsl:with-param name="id" select="$our-id"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="grammar.intro"/>
                <xsl:apply-templates select="construct-flag-sets"/>
                <xsl:apply-templates select="constructs"/>
                <xsl:apply-templates select="character-classes"/>
                <xsl:call-template name="grammar.reserved-words"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="grammar.index">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$index-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-index-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="grammar.index-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- Content templates -->

    <!-- Outputs the introduction section of the grammar document. -->
    <xsl:template name="grammar.intro">
        <xsl:variable name="def-part">
            <xsl:call-template name="grammar.as-nonterminal">
                <xsl:with-param name="value" select="'DefPart'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="eg-sep">
            <xsl:call-template name="grammar.as-terminal">
                <xsl:with-param name="value" select="'separator'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="sep-def-part">
            <xsl:copy-of select="$def-part"/>:
            <xsl:copy-of select="$eg-sep"/>
        </xsl:variable>

    <p>
    The following is a summary of the grammar of the <xsl:value-of
    select="$language-name"/> programming language. Terminal symbols look like
    <xsl:call-template name="grammar.as-terminal">
        <xsl:with-param name="value" select="'this'"/>
    </xsl:call-template> and nonterminals look like
    <xsl:call-template name="grammar.as-nonterminal">
        <xsl:with-param name="value" select="'this'"/>
    </xsl:call-template>. A nonterminal being defined appears to the left of a
    nonterminal definition operator '<xsl:value-of select="$grammar.def-op"/>',
    and the nonterminal's definition appears to the right of the definition
    operator.
    </p><p>
    <a name="DefPart">Several shorthands are employed in this grammar, most of
    which have a slightly nonstandard representation. For a part of a definition
    <xsl:copy-of select="$def-part"/> and a terminal <xsl:copy-of
    select="$eg-sep"/></a>
    <ul>
        <li><xsl:call-template name="grammar.apply-number">
                <xsl:with-param name="content" select="$def-part"/>
                <xsl:with-param name="number" select="'zero-or-one'"/>
            </xsl:call-template> means that <xsl:copy-of select="$def-part"/>
            is optional: it can appear or not appear (though not more than once)
            in an instance of the construct</li>
        <li><xsl:call-template name="grammar.apply-number">
                <xsl:with-param name="content" select="$def-part"/>
                <xsl:with-param name="number" select="'zero-or-more'"/>
            </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
            appear zero or more times</li>
        <li><xsl:call-template name="grammar.apply-number">
                <xsl:with-param name="content" select="$sep-def-part"/>
                <xsl:with-param name="number" select="'zero-or-more'"/>
            </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
            be repeated zero or more times, separated by <xsl:copy-of
            select="$eg-sep"/>s (so there will be a <xsl:copy-of
            select="$eg-sep"/> between any two consecutive <xsl:copy-of
            select="$def-part"/>s, but not before the first or after the last
            <xsl:copy-of select="$def-part"/>)</li>
        <li><xsl:call-template name="grammar.apply-number">
                <xsl:with-param name="content" select="$def-part"/>
                <xsl:with-param name="number" select="'one-or-more'"/>
            </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
            be repeated one or more times (but must appear at least once)</li>
        <li><xsl:call-template name="grammar.apply-number">
                <xsl:with-param name="content" select="$sep-def-part"/>
                <xsl:with-param name="number" select="'one-or-more'"/>
            </xsl:call-template> means that <xsl:copy-of select="$def-part"/> can
            be repeated one or more times (but must appear at least once),
            separated by <xsl:copy-of select="$eg-sep"/>s (so there will be a
            <xsl:copy-of select="$eg-sep"/> between any two consecutive
            <xsl:copy-of select="$def-part"/>s, but not before the first or after
            the last <xsl:copy-of select="$def-part"/>)</li>
        <li><xsl:call-template name="grammar.apply-number">
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
            line)</li>
    </ul>
    Any appearance of any part of these shorthands as terminals are not part of
    one of these shorthands, but are part of the language construct being
    described.
    </p><p>
    The grammar is divided into the following sections:
    <xsl:call-template name="table-of-contents">
        <xsl:with-param name="titles" select="$grammar.section-titles"/>
    </xsl:call-template>
    </p>
    </xsl:template>

    <!-- Returns a bar-separated (|) list of the titles of all of the
         sections of the grammar document. There will also be a bar
         after the last element in the list. -->
    <xsl:template name="grammar.section-titles">
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
            <xsl:value-of
                select="$grammar.reserved-words-section-title"/>|<xsl:text/>
        </xsl:variable>

        <xsl:value-of select="concat($titles0, $titles1, $titles2, $titles3)"/>
    </xsl:template>


    <!-- Sections -->

    <xsl:template match="character-classes">
        <xsl:call-template name="grammar.sections-in"/>
    </xsl:template>

    <xsl:template match="construct-flag-sets">
        <xsl:call-template name="grammar.sections-in"/>
    </xsl:template>

    <xsl:template match="constructs">
        <xsl:call-template name="grammar.sections-in"/>
    </xsl:template>

    <!-- Outputs the sections directly under the specified node. -->
    <xsl:template name="grammar.sections-in">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/section">
            <xsl:variable name="title" select="@title"/>
            <xsl:variable name="url" select="translate($title, ' ', '_')"/>
            <hr />
            <h2><a name="{$url}"><xsl:value-of select="$title"/></a></h2>
            <xsl:apply-templates mode="grammar.rule"/>
            <xsl:copy-of select="$sections-toolbar"/>
        </xsl:for-each>
    </xsl:template>


    <!-- Tokens -->

    <!-- Outputs the contents of the section that lists all of the
         language's reserved words (including operator reserved words). -->
    <xsl:template name="grammar.reserved-words">
        <xsl:variable name="title"
            select="$grammar.reserved-words-section-title"/>
        <xsl:variable name="url" select="translate($title, ' ', '_')"/>

        <hr />
        <h2><a name="{$url}"><xsl:value-of select="$title"/></a></h2>

        <xsl:variable name="word-nodes"
            select="//tokens/*/reserved-word | //tokens/*/reserved-word-operator"/>
        <xsl:variable name="num-words" select="count($word-nodes)"/>

        <xsl:choose>
            <xsl:when test="$num-words = 0">
There are no reserved words.
            </xsl:when>
            <xsl:otherwise>
There are <xsl:value-of select="$num-words"/> reserved words:
<p />
                <table class="reserved-words" border="0" width="90%" align="center">
                    <xsl:call-template name="grammar.reserved-words-table-rows">
                        <xsl:with-param name="word-nodes" select="$word-nodes"/>
                        <xsl:with-param name="words-per-row"
                            select="$grammar.reserved-words-per-row"/>
                    </xsl:call-template>
                </table>
                <xsl:if test="count($word-nodes[@future='true']) &gt; 0">
                <p>
                <span class="footnote-icon">*</span> reserved for future use
                </p>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:copy-of select="$sections-toolbar"/>
    </xsl:template>

    <!-- Outputs the rows of the reserved words table for the reserved
         words represented by the specified nodes. -->
    <xsl:template name="grammar.reserved-words-table-rows">
        <xsl:param name="word-nodes"/>
        <xsl:param name="words-per-row"/>

        <xsl:variable name="this-row-word-nodes"
            select="$word-nodes[position() &lt;= $words-per-row]"/>
        <xsl:variable name="remaining-word-nodes"
            select="$word-nodes[position() &gt; $words-per-row]"/>
        <xsl:if test="$this-row-word-nodes">
            <tr>
                <xsl:for-each select="$this-row-word-nodes">
                    <td>
                    <xsl:call-template name="grammar.as-terminal">
                        <xsl:with-param name="value" select="@text"/>
                    </xsl:call-template>
                    <xsl:if test="@future='true'">
                        <span class="footnote-icon">*</span>
                    </xsl:if>
                    </td>
                </xsl:for-each>
            </tr>
            <xsl:call-template name="grammar.reserved-words-table-rows">
                <xsl:with-param name="word-nodes"
                    select="$remaining-word-nodes"/>
                <xsl:with-param name="words-per-row"
                    select="$words-per-row"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Terminals and nonterminals -->

    <xsl:template match="term">
        <xsl:call-template name="grammar.terminal-text">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="nonterm">
        <xsl:call-template name="grammar.as-nonterminal">
            <xsl:with-param name="value" select="@name"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Notes templates -->

    <xsl:template match="notes" mode="grammar.rule">
        <xsl:if test="count(note[not(@hidden = 'true')]) &gt; 0">
            <div class="plack-notes">
            <h3>Notes:</h3>
            <ul>
            <xsl:for-each select="note">
                <xsl:if test="not(@hidden = 'true')">
                    <li><xsl:apply-templates/></li>
                </xsl:if>
            </xsl:for-each>
            </ul>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="operator-information">
        <xsl:call-template name="operator-info-table"/>
    </xsl:template>

    <!-- At least for now allow tables in notes. -->
    <xsl:template match="table">
        <xsl:copy-of select="."/>
    </xsl:template>


    <!-- Index templates -->

    <xsl:template name="grammar.index-links">
        <xsl:call-template name="grammar.major-section-links"/>
        <hr />
        <xsl:apply-templates select="construct-flag-sets"
            mode="grammar.index-links"/>
        <xsl:apply-templates select="constructs"
            mode="grammar.index-links"/>
        <xsl:apply-templates select="character-classes"
            mode="grammar.index-links"/>
        <xsl:call-template name="grammar.reserved-words-index"/>
    </xsl:template>

    <!-- Outputs links to the major sections of the grammar document. -->
    <xsl:template name="grammar.major-section-links">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$content-filename"/>
            <xsl:with-param name="text" select="$top-name"/>
        </xsl:call-template>

        <xsl:apply-templates select="construct-flag-sets"
            mode="grammar.major-section-links"/>
        <xsl:apply-templates select="constructs"
            mode="grammar.major-section-links"/>
        <xsl:apply-templates select="character-classes"
            mode="grammar.major-section-links"/>
        <xsl:call-template name="grammar.reserved-words-major-index"/>
    </xsl:template>

    <xsl:template name="grammar.reserved-words-major-index">
        <xsl:variable name="title"
            select="$grammar.reserved-words-section-title"/>
        <xsl:variable name="url"
            select="concat('#', translate($title, ' ', '_'))"/>

        <div class="plack-minor-index-entry"><a href="{$url}"><xsl:value-of
            select="translate($title, ' ', '&nbsp;')"/></a></div>
    </xsl:template>

    <xsl:template match="construct-flag-sets" mode="grammar.major-section-links">
        <xsl:call-template name="grammar.major-sections"/>
    </xsl:template>

    <xsl:template match="constructs" mode="grammar.major-section-links">
        <xsl:call-template name="grammar.major-sections"/>
    </xsl:template>

    <xsl:template match="character-classes" mode="grammar.major-section-links">
        <xsl:call-template name="grammar.major-sections"/>
    </xsl:template>

    <!-- Ignore everything else. -->
    <xsl:template match="*" mode="grammar.major-section-links"/>

    <xsl:template name="grammar.major-sections">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/section">
            <xsl:variable name="title" select="@title"/>
            <xsl:variable name="url"
                select="concat('#', translate($title, ' ', '_'))"/>

            <div class="plack-minor-index-entry"><a href="{$url}"><xsl:value-of
                select="translate($title, ' ', '&nbsp;')"/></a></div>
        </xsl:for-each>
    </xsl:template>


    <xsl:template name="grammar.reserved-words-index">
        <xsl:variable name="title"
            select="$grammar.reserved-words-section-title"/>
        <xsl:variable name="tr-title"
            select="translate($title, ' ', '_')"/>
        <xsl:variable name="url"
            select="concat($content-filename, '#', $tr-title)"/>

        <p><a name="{$tr-title}">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="text" select="$title"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
    </xsl:template>

    <xsl:template match="construct-flag-sets" mode="grammar.index-links">
        <xsl:call-template name="grammar.sections-links"/>
    </xsl:template>

    <xsl:template match="constructs" mode="grammar.index-links">
        <xsl:call-template name="grammar.sections-links"/>
    </xsl:template>

    <xsl:template match="character-classes" mode="grammar.index-links">
        <xsl:call-template name="grammar.sections-links"/>
    </xsl:template>

    <!-- Ignore everything else. -->
    <xsl:template match="*" mode="grammar.index-links"/>


    <!-- Outputs the links for the sections directly under the specified
         node. -->
    <xsl:template name="grammar.sections-links">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/section">
            <xsl:variable name="title" select="@title"/>
        <xsl:variable name="tr-title"
            select="translate($title, ' ', '_')"/>
            <xsl:variable name="url"
                select="concat($content-filename, '#', $tr-title)"/>

            <p><a name="{$tr-title}">
            <xsl:call-template name="make-major-index-entry">
                <xsl:with-param name="url" select="$url"/>
                <xsl:with-param name="text" select="$title"/>
                <xsl:with-param name="suffix-link-href" select="$top-link"/>
                <xsl:with-param name="suffix-link-text" select="'^'"/>
            </xsl:call-template></a></p>

            <xsl:apply-templates mode="grammar.item-links"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="character-class" mode="grammar.item-links">
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#', @name)"/>
            <xsl:with-param name="text" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="flag-set-definition" mode="grammar.item-links">
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#', @name)"/>
            <xsl:with-param name="text" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="alias-construct | construct | single-line-construct |
        multiline-construct | compound-construct | choice-construct |
        line-choice-construct | single-token-construct | repeated-construct"
        mode="grammar.item-links">
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#', @type)"/>
            <xsl:with-param name="text" select="@type"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Ignore everything else. -->
    <xsl:template match="*" mode="grammar.item-links"/>
</xsl:transform>
