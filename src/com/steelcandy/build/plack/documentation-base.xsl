<?xml version="1.0"?>
<!--
    $Id: documentation-base.xsl,v 1.6 2006/06/15 06:02:43 jgm Exp $

    A base stylesheet for XSL stylesheets that transform the
    'documentation.xml' file that contains the documentation for a language
    into an XSL stylesheet that, when it is applied to the language's
    language description document, results in the language's documentation
    in a specific format (such as HTML, for example).

    Note: the final stylesheets that import this stylesheet will be applied
    in the following manner:

        1. the stylesheet that imports this stylesheet will be applied to a
           language documentation document (usually named documentation.xml)
           to produce another stylesheet (which we will refer to here as
           'final-style'), then
        2. the 'final-style' stylesheet is applied to the same language's
           description document (usually named description.xml or 'lang'.xml
           where 'lang' is the language's name) to produce the language's
           documentation in a particular format.

    In order to keep straight which templates are applied at which of the
    above two stages, the following convention is used in this stylesheet,
    and should be used in all stylesheets that directly or indirectly import
    this stylesheet:

        - XSL templates that are (at least intended to be) applied as part
          of stage 1 should have names that start with 'generate-', and
        - XML templates that are (at least intended to be) applied as part
          of stage 2 should have names that start with 'output-'.

    As a check, a template's full name should start with either 'xsl:generate-'
    or 'x:output-', but never 'x:generate-' or 'xsl:output-'.

    Author: James MacKay
    Last Updated: $Date: 2006/06/15 06:02:43 $

    Copyright (C) 2006 by James MacKay.

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
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:x="fake.xsl">  <!-- see 'namespace-alias' below -->

    <xsl:namespace-alias stylesheet-prefix="x" result-prefix="xsl"/>

    <xsl:output method="xml" indent="yes"/>  <!-- Since it's XSL -->


    <!-- Configuration -->



    <!-- Global variables -->

    <!-- All of the 'book' elements. -->
    <xsl:variable name="books" select="/documents/book"/>

    <!-- Maps each book's tag to the corresponding book element. -->
    <xsl:key name="book" use="@tag" match="/documents/book"/>


    <!-- Main templates -->

    <xsl:template match="/">
        <x:transform version="1.0">
            <xsl:call-template name="generate-top-level-elements"/>
            <xsl:text>

</xsl:text>
            <xsl:comment> Constants </xsl:comment>
            <xsl:text>
</xsl:text>
            <x:variable name="top" select="/language"/>
            <x:variable name="language-name" select="$top/@name"/>
            <x:variable name="language-name-article" select="$top/@article"/>
            <xsl:text>

</xsl:text>
            <xsl:comment> Main templates </xsl:comment>
            <xsl:text>
</xsl:text>
            <x:template match="/">
                <x:call-template name="output-all-documentation"/>
            </x:template>
            <xsl:text>

</xsl:text>
            <xsl:comment> Output the programming language name </xsl:comment>
            <xsl:text>
</xsl:text>
            <x:template name="output-language-name">
                <x:value-of select="$language-name"/>
            </x:template>
            <xsl:text>
</xsl:text>
            <xsl:for-each select="/documents">
                <xsl:call-template name="generate-all-documentation-template"/>
            </xsl:for-each>
            <xsl:text>
</xsl:text>
            <xsl:call-template name="additional-toc-templates"/>
        </x:transform>
    </xsl:template>

    <!-- Generates top-level elements that are to appear at the start of the
         'final-style' stylesheet. -->
    <xsl:template name="generate-top-level-elements"/>

    <!-- Generates the XSL that outputs the formatted version of the book of
         documentation represented by the current element (which is assumed
         to be a 'book' element from a language documentation document). -->
    <xsl:template name="generate-book">
        <xsl:comment> Output a book </xsl:comment>
        <xsl:call-template name="generate-book-title-page"/>
        <xsl:call-template name="generate-chapter-toc"/>
        <xsl:call-template name="generate-subsection-toc"/>
        <xsl:call-template name="generate-book-contents"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Generates the actual contents of the book represented by the current
         element (which is assumed to be a 'book' element in a language
         documentation document). -->
    <xsl:template name="generate-book-contents">
        <xsl:apply-templates mode="doc.generate"/>
    </xsl:template>

    <!-- A template that can be overridden in stylesheets that import this
         stylesheet in order to output 'x:template' elements that define
         templates relating to tables of contents. -->
    <xsl:template name="additional-toc-templates"/>


    <!-- Templates that match tags, etc. in the language documentation
         document. -->

    <xsl:template match="language-name" mode="doc.generate">
        <!-- Replace with the name of the language, which is in the language
             description document. -->
        <x:call-template name="output-language-name"/>
    </xsl:template>

    <xsl:template match="grammar-rule" mode="doc.generate">
        <x:call-template name="output-grammar-rule">
            <x:with-param name="name"><xsl:value-of
                select="@name"/></x:with-param>
        </x:call-template>
    </xsl:template>

    <xsl:template match="reserved-word" mode="doc.generate">
        <x:call-template name="output-reserved-word">
            <x:with-param name="name"><xsl:value-of
                select="@name"/></x:with-param>
        </x:call-template>
    </xsl:template>

    <xsl:template match="constant" mode="doc.generate">
        <x:call-template name="output-constant">
            <x:with-param name="name"><xsl:value-of
                select="@name"/></x:with-param>
        </x:call-template>
    </xsl:template>


    <xsl:template match="authors | copyright" mode="doc.generate"/>

    <xsl:template match="text()" mode="doc.generate">
        <xsl:value-of select="."/>
    </xsl:template>


    <!-- Utility templates -->

    <!-- Outputs the ID of the part referred to by the specified reference
         element, where the part is a part of the specified book. -->
    <xsl:template name="referenced-id">
        <xsl:param name="ref" select="."/>
        <xsl:param name="book" select="ancestor::book"/>

        <xsl:variable name="num-matches">
            <xsl:call-template name="referenced-parts-count">
                <xsl:with-param name="ref" select="$ref"/>
                <xsl:with-param name="book" select="$book"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$num-matches = 1">
                <xsl:call-template name="referenced-part-id">
                    <xsl:with-param name="ref" select="$ref"/>
                    <xsl:with-param name="book" select="$book"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="display-ref">
                    <xsl:call-template name="displayable-reference">
                        <xsl:with-param name="ref" select="$ref"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="display-loc">
                    <xsl:call-template name="displayable-location">
                        <xsl:with-param name="node" select="$ref"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:if test="$num-matches = 0">
                    <xsl:message terminate="yes">
The reference '<xsl:value-of select="$display-ref"/>'
<xsl:value-of select="display-loc"/>
doesn't match any part of the book '<xsl:value-of select="$book/@tag"/>'.
                    </xsl:message>
                </xsl:if>
                <xsl:if test="$num-matches &gt; 1">
                    <xsl:message terminate="yes">
The reference '<xsl:value-of select="$display-ref"/>'
<xsl:value-of select="display-loc"/>
matches <xsl:value-of select="$num-matches"/> parts of the book '<xsl:value-of select="$book/@tag"/>'.
                    </xsl:message>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the number of parts that match the specified reference
         element, where the parts are limited to those that are part of the
         specified book.

         NOTE: changes to the 'referenced-parts' template (below) must be
         reflected here, and vice versa !!! -->
    <xsl:template name="referenced-parts-count">
        <xsl:param name="ref" select="."/>
        <xsl:param name="book" select="ancestor::book"/>

        <xsl:for-each select="$book">
            <!-- Handle every possible valid permutation of 'chapter',
                 'section', 'subsection', 'spot' and 'part' attributes.

                 Note: a 'spot' can be a descendant (i.e. not necessarily
                 a child) of a book, chapter, section or subsection.

                 We can assume that previous checking has ensured that
                    - at least one of the attributes is present, and
                    - both the 'spot' and 'part' attributes are not present. -->
            <xsl:choose>
                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection and $ref/@spot">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection and $ref/@part">
                    <!-- '@part' must be the tag of a spot. -->
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part])"/>
                </xsl:when>

                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and $ref/@spot">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and $ref/@part">
                    <!-- '@part' is the tag of either a subsection or a
                         spot. -->
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]//spot[@tag = $ref/@part] |
                        chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@part])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection and $ref/@spot">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part])"/>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection and $ref/@spot">
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part])"/>
                </xsl:when>

                <xsl:when test="$ref/@chapter and $ref/@section">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@spot">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@part">
                    <!-- '@part' is the tag of a section, subsection or
                         spot. -->
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter]/section[@tag = $ref/@part] |
                        chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@part] |
                        chapter[@tag = $ref/@chapter]//spot[@tag = $ref/@part])"/>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection">
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection])"/>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@spot">
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@part">
                    <!-- '@part' is the tag of a subsection or spot. -->
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@part] |
                        chapter/section[@tag = $ref/@section]//spot[@tag = $ref/@part])"/>
                </xsl:when>
                <xsl:when test="$ref/@subsection and $ref/@spot">
                    <xsl:value-of select="count(chapter/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:value-of select="count(chapter/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part])"/>
                </xsl:when>

                <xsl:when test="$ref/@chapter">
                    <xsl:value-of select="count(chapter[@tag = $ref/@chapter])"/>
                </xsl:when>
                <xsl:when test="$ref/@section">
                    <xsl:value-of select="count(chapter/section[@tag = $ref/@section])"/>
                </xsl:when>
                <xsl:when test="$ref/@subsection">
                    <xsl:value-of select="count(chapter/section/subsection[@tag = $ref/@subsection])"/>
                </xsl:when>
                <xsl:when test="$ref/@spot">
                    <xsl:value-of select="count(//spot[@tag = $ref/@spot])"/>
                </xsl:when>
                <xsl:when test="$ref/@part">
                    <!-- '@part' is the tag of a chapter, section, subsection
                         or spot. -->
                    <xsl:value-of select="count(chapter[@tag = $ref/@part] |
                        chapter/section[@tag = $ref/@part] |
                        chapter/section/subsection[@tag = $ref/@part] |
                        //spot[@tag = $ref/@part])"/>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the ID of the part that matches the specified reference
         element, where the part is a part of the specified book.

         NOTE: changes to the 'referenced-parts-count' template (above) must
         be reflected here, and vice versa !!! -->
    <xsl:template name="referenced-part-id">
        <xsl:param name="ref" select="."/>
        <xsl:param name="book" select="ancestor::book"/>

        <xsl:for-each select="$book">
            <!-- Handle every possible valid permutation of 'chapter',
                 'section', 'subsection', 'spot' and 'part' attributes.

                 Note: a 'spot' can be a descendant (i.e. not necessarily
                 a child) of a book, chapter, section or subsection.

                 We can assume that previous checking has ensured that
                    - at least one of the attributes is present, and
                    - both the 'spot' and 'part' attributes are not present. -->
            <xsl:choose>
                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection and $ref/@part">
                    <!-- '@part' must be the tag of a spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>

                <xsl:when test="$ref/@chapter and $ref/@section and
                                $ref/@subsection">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@section and $ref/@part">
                    <!-- '@part' is the tag of either a subsection or a
                         spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]//spot[@tag = $ref/@part] |
                                chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]/subsection[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>

                <xsl:when test="$ref/@chapter and $ref/@section">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@section]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@subsection">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@subsection]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@chapter and $ref/@part">
                    <!-- '@part' is the tag of a section, subsection or
                         spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]/section[@tag = $ref/@part] |
                                chapter[@tag = $ref/@chapter]/section/subsection[@tag = $ref/@part] |
                                chapter[@tag = $ref/@chapter]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@subsection">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@subsection]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section and $ref/@part">
                    <!-- '@part' is the tag of a subsection or spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]/subsection[@tag = $ref/@part] |
                                chapter/section[@tag = $ref/@section]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@subsection and $ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@subsection and $ref/@part">
                    <!-- '@part' is the tag of a spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section/subsection[@tag = $ref/@subsection]//spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>

                <xsl:when test="$ref/@chapter">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@chapter]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@section">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section[@tag = $ref/@section]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@subsection">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter/section/subsection[@tag = $ref/@subsection]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@spot">
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="//spot[@tag = $ref/@spot]"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:when test="$ref/@part">
                    <!-- '@part' is the tag of a chapter, section, subsection
                         or spot. -->
                    <xsl:call-template name="part-id">
                        <xsl:with-param name="book" select="$book"/>
                        <xsl:with-param name="part"
                            select="chapter[@tag = $ref/@part] |
                                chapter/section[@tag = $ref/@part] |
                                chapter/section/subsection[@tag = $ref/@part] |
                                //spot[@tag = $ref/@part]"/>
                    </xsl:call-template>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs a displayable representation of the specified reference. -->
    <xsl:template name="displayable-reference">
        <xsl:param name="ref"/>

        <xsl:for-each select="$ref">
            <xsl:value-of select="concat(local-name(), ' ')"/>
            <xsl:if test="@chapter">
                <xsl:value-of
                    select="concat('chapter=&quot;', @chapter, '&quot; ')"/>
            </xsl:if>
            <xsl:if test="@section">
                <xsl:value-of
                    select="concat('section=&quot;', @section, '&quot; ')"/>
            </xsl:if>
            <xsl:if test="@subsection">
                <xsl:value-of select="concat('subsection=&quot;',
                                             @subsection, '&quot; ')"/>
            </xsl:if>
            <xsl:if test="@spot">
                <xsl:value-of select="concat('spot=&quot;', @spot, '&quot; ')"/>
            </xsl:if>
            <xsl:if test="@part">
                <xsl:value-of select="concat('part=&quot;', @part, '&quot; ')"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs a displayable representation of the location of the
         specified node. -->
    <xsl:template name="displayable-location">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:variable name="book" select="ancestor-or-self::book"/>

            <xsl:choose>
                <xsl:when test="count($book) = 0">
                    <xsl:text>outside of any and all books</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:if test="count(ancestor-or-self::subsection) &gt; 0">
                        <xsl:value-of select="concat('in subsection ',
                            ancestor-or-self::subsection/@tag, ' ')"/>
                    </xsl:if>
                    <xsl:if test="count(ancestor-or-self::section) &gt; 0">
                        <xsl:value-of select="concat('in section ',
                            ancestor-or-self::section/@tag, ' ')"/>
                    </xsl:if>
                    <xsl:if test="count(ancestor-or-self::chapter) &gt; 0">
                        <xsl:value-of select="concat('in chapter ',
                            ancestor-or-self::chapter/@tag, ' ')"/>
                    </xsl:if>
                    <xsl:value-of select="concat('in book ', $book/@tag)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>


    <!-- Outputs the ID of the specified documentation part. -->
    <xsl:template name="part-id">
        <xsl:param name="part"/>
        <xsl:param name="sep" select="'_'"/>

        <xsl:apply-templates select="$part" mode="doc.generate-part-id">
            <xsl:with-param name="sep" select="$sep"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- Handle non-documentation parts. -->
    <xsl:template match="*" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <xsl:message terminate="yes">
The '<xsl:value-of select="local-name()"/>' element
<xsl:call-template name="displayable-location"/>
is not recognized as a documentation part, so its ID cannot
be determined.
        </xsl:message>
    </xsl:template>

    <xsl:template match="book" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <xsl:value-of select="@tag"/>
    </xsl:template>

    <xsl:template match="chapter" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <xsl:variable name="prefix">
            <xsl:apply-templates select="ancestor::book"
                mode="doc.generate-part-id">
                <xsl:with-param name="sep" select="$sep"/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:value-of select="concat($prefix, $sep, @tag)"/>
    </xsl:template>

    <xsl:template match="section" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <xsl:variable name="prefix">
            <xsl:apply-templates select="ancestor::chapter"
                mode="doc.generate-part-id">
                <xsl:with-param name="sep" select="$sep"/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:value-of select="concat($prefix, $sep, @tag)"/>
    </xsl:template>

    <xsl:template match="subsection" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <xsl:variable name="prefix">
            <xsl:apply-templates select="ancestor::section"
                mode="doc.generate-part-id">
                <xsl:with-param name="sep" select="$sep"/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:value-of select="concat($prefix, $sep, @tag)"/>
    </xsl:template>

    <xsl:template match="spot" mode="doc.generate-part-id">
        <xsl:param name="sep"/>

        <!-- We assume here that a spot must be a descendant of a book. -->
        <xsl:variable name="prefix">
            <xsl:choose>
                <xsl:when test="count(ancestor::subsection) &gt; 0">
                    <xsl:apply-templates select="ancestor::subsection"
                        mode="doc.generate-part-id">
                        <xsl:with-param name="sep" select="$sep"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="count(ancestor::section) &gt; 0">
                    <xsl:apply-templates select="ancestor::section"
                        mode="doc.generate-part-id">
                        <xsl:with-param name="sep" select="$sep"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="count(ancestor::chapter) &gt; 0">
                    <xsl:apply-templates select="ancestor::chapter"
                        mode="doc.generate-part-id">
                        <xsl:with-param name="sep" select="$sep"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="ancestor::book"
                        mode="doc.generate-part-id">
                        <xsl:with-param name="sep" select="$sep"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- '@tag' is preceded by two separators in order to distinguish
             spot IDs from the IDs of other types of parts (assuming that
             the separator can't appear in a tag). -->
        <xsl:value-of select="concat($prefix, $sep, $sep, @tag)"/>
    </xsl:template>


    <!-- Outputs the chapter number of the chapter (or appendix) represented
         by the specified 'chapter' element. -->
    <xsl:template name="chapter-number">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:variable name="tag" select="@tag"/>

            <xsl:choose>
                <xsl:when test="@appendix = 'true'">
                    <xsl:for-each select="../chapter[@appendix = 'true']">
                        <xsl:if test="@tag = $tag">
                            <xsl:number format="A" value="position()"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="../chapter[not(@appendix) or @appendix = 'false']">
                        <xsl:if test="@tag = $tag">
                            <xsl:value-of select="position()"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the full section number - which includes the chapter and
         section numbers - of the section represented by the specified
         'section' element. -->
    <xsl:template name="full-section-number">
        <xsl:param name="node" select="."/>
        <xsl:param name="sep" select="'.'"/>

        <xsl:for-each select="$node">
            <xsl:variable name="chapter-num">
                <xsl:call-template name="chapter-number">
                    <xsl:with-param name="node" select=".."/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="section-num">
                <xsl:call-template name="section-number"/>
            </xsl:variable>

            <xsl:value-of select="concat($chapter-num, $sep, $section-num)"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the section number of the section represented by the
         specified 'section' element. -->
    <xsl:template name="section-number">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:variable name="tag" select="@tag"/>

            <xsl:for-each select="../section">
                <xsl:if test="@tag = $tag">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the full subsection number - which includes the chapter,
         section and subsection numbers - of the subsection represented by
         the specified 'subsection' element. -->
    <xsl:template name="full-subsection-number">
        <xsl:param name="node" select="."/>
        <xsl:param name="sep" select="'.'"/>

        <xsl:for-each select="$node">
            <xsl:variable name="start">
                <xsl:call-template name="full-section-number">
                    <xsl:with-param name="node" select=".."/>
                    <xsl:with-param name="sep" select="$sep"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="subsection-num">
                <xsl:call-template name="subsection-number"/>
            </xsl:variable>

            <xsl:value-of select="concat($start, $sep, $subsection-num)"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the section number of the (titled) subsection represented
         by the specified 'subsection' element. -->
    <xsl:template name="subsection-number">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:variable name="tag" select="@tag"/>

            <xsl:for-each select="../subsection[title]">
                <xsl:if test="@tag = $tag">
                    <xsl:value-of select="position()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the example number of the specified 'example' element. -->
    <xsl:template name="example-number">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:variable name="chapter" select="ancestor::chapter"/>

            <xsl:for-each select="$chapter">
                <xsl:variable name="chapter-num">
                    <xsl:call-template name="chapter-number"/>
                </xsl:variable>
                <xsl:variable name="example-num">
                    <xsl:for-each select="descendant::example">
                        <xsl:if test="generate-id(.) = generate-id($node)">
                            <xsl:value-of select="position()"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>

                <xsl:value-of
                    select="concat($chapter-num, '-', $example-num)"/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>
</xsl:transform>
