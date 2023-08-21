<?xml version="1.0"?>
<!--
    $Id: documentation-in-html.xsl,v 1.8 2006/06/16 04:12:59 jgm Exp $

    A base stylesheet for XSL stylesheets that transform the
    'documentation.xml' file that contains the documentation for a language
    into an XSL stylesheet that, when it is applied to the language's
    language description document, results in the language's documentation
    in a specific format (such as HTML, for example).

    Author: James MacKay
    Last Updated: $Date: 2006/06/16 04:12:59 $

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
    xmlns:x="fake.xsl">  <!-- see imported 'namespace-alias' -->

    <xsl:import href="documentation-base.xsl"/>


    <!-- Configuration -->



    <!-- Global variables -->



    <!-- Main templates -->


    <!-- Generates top-level elements that are to appear at the start of the
         'final-style' stylesheet. -->
    <xsl:template name="generate-top-level-elements">
        <x:import href="grammar-rules.html.xsl"/>
        <x:import href="language-common.xsl"/>

        <x:output method="html" indent="yes"/>
    </xsl:template>

    <!-- Generates the template that outputs the formatted version of all of
         the documentation under the current element (which is assumed to be
         the root 'documents' element from a language documentation
         document). -->
    <xsl:template name="generate-all-documentation-template">
        <xsl:text>
</xsl:text>
        <x:template name="output-all-documentation">
        <html>
            <head>
                <title><x:call-template name="output-language-name"/> Documentation</title>
                <style type="text/css" media="all">
                    body
                    {
                        margin: 0.5em 2%
                    }


                    #books-in-set
                    {
                        margin-bottom: 5em;
                    }

                    #books-in-set h1
                    {
                        margin-top: 0;
                        font-size: 1.2em;
                    }

                    /* Drop-down menu in non-IE browsers. */
                    html>body #books-in-set .entries
                    {
                        position: absolute;
                        display: none;
                        border: thin solid;
                        padding: 1em;

                        /* background-image: url(semi-transparent.gif); */
                    }
                    html>body #books-in-set:hover .entries
                    {
                        display: block;
                    }

                    /* Regular list in IE. */
                    #books-in-set .entries
                    {
                        border: thin solid;
                        padding: 1em;
                        width: 40%;
                    }


                    .book
                    {
                        margin: 0 0 5em;
                        border-bottom: solid thin;
                    }

                    .title-page
                    {
                        margin-bottom: 8em;
                        text-align: center;
                    }

                    .title-page .authors
                    {
                        font-weight: bold;
                        margin-bottom: 1em;
                    }

                    .chapter-toc
                    {
                        display: none;
                    }

                    .subsection-toc
                    {
                        /* display: none; */
                    }

                    .chapter-toc .chapter
                    {
                        padding-left: 2em;
                    }

                    .subsection-toc .chapter
                    {
                        padding-left: 2em;
                    }

                    .subsection-toc .section
                    {
                        padding-left: 2em;
                    }

                    .subsection-toc .subsection
                    {
                        padding-left: 2em;
                    }


                    .grammar-rules
                    {
                        border: dotted thin;
                        padding: 0 0.5em;
                        margin: 0.25em 5% 0.5em;
                    }


                    .example
                    {
                        border: solid thin;
                        margin: 1em 5%;
                        padding: 0.5em;
                    }

                    .example .title
                    {
                        border-bottom: solid thin;
                        font-weight: bold;
                        padding-bottom: 0.25em;
                    }
                </style>
            </head>
            <body>
                <div class="book-set">
            <xsl:if test="count(book) &gt; 1">
                    <div id="books-in-set">
                        <h1>Books:</h1>
                        <div class="entries">
                <xsl:for-each select="book">
                    <xsl:call-template name="generate-book-in-set-entry">
                        <xsl:with-param name="book-number" select="position()"/>
                    </xsl:call-template>
                </xsl:for-each>
                        </div>
                    </div>
            </xsl:if>
            <xsl:for-each select="book">
                <xsl:call-template name="generate-book"/>
            </xsl:for-each>
                </div>
            </body>
        </html>
        </x:template>
        <xsl:text>
</xsl:text>
        <xsl:comment> Outputs the grammar rule for the character class,
construct flag set or construct with the specified name. </xsl:comment>
        <x:template name="output-grammar-rule">
            <x:param name="name"/>

            <x:variable name="matches"
                select="key('construct', $name) |
                        key('character-class', $name) |
                        key('construct-flag-set', $name)"/>
            <x:variable name="num-matches" select="count($matches)"/>

            <x:choose>
                <x:when test="$num-matches = 1">
                    <x:apply-templates select="$matches" mode="grammar.rule"/>
                </x:when>
                <x:when test="$num-matches = 0">
                    <x:message terminate="yes">
Invalid 'grammar-rule' tag: there is no construct, construct
flag set or character class named '<x:value-of select="$name"/>'.
                    </x:message>
                </x:when>
                <x:otherwise>  <!-- $num-matches &gt; 0 -->
                    <x:message terminate="yes">
Invalid 'grammar-rule' tag: there is more than one construct,
construct flag set and/or character class named '<x:value-of select="$name"/>'.
                    </x:message>
                </x:otherwise>
            </x:choose>
        </x:template>
        <xsl:text>
</xsl:text>
        <xsl:comment> Outputs the reserved word with the specified name. </xsl:comment>
        <x:template name="output-reserved-word">
            <x:param name="name"/>

            <x:variable name="matches" select="key('reserved-word', $name)"/>
            <x:variable name="num-matches" select="count($matches)"/>

            <x:choose>
                <x:when test="$num-matches = 1">
                    <x:value-of select="$matches/@text"/>
                </x:when>
                <x:when test="$num-matches = 0">
                    <x:message terminate="yes">
Invalid 'reserved-word' tag: there is no reserved word or
reserved word operator named '<x:value-of select="$name"/>'.
                    </x:message>
                </x:when>
                <x:otherwise>  <!-- $num-matches &gt; 0 -->
                    <x:message terminate="yes">
Invalid 'reserved-word' tag: there is more than one reserved
word and/or reserved word operator named '<x:value-of select="$name"/>'.
                    </x:message>
                </x:otherwise>
            </x:choose>
        </x:template>
        <xsl:text>
</xsl:text>
        <xsl:comment> Outputs the value of the constant with the specified name. </xsl:comment>
        <x:template name="output-constant">
            <x:param name="name"/>

            <x:variable name="matches" select="key('constant', $name)"/>
            <x:variable name="num-matches" select="count($matches)"/>

            <x:choose>
                <x:when test="$num-matches = 1">
                    <x:value-of select="$matches/@value"/>
                </x:when>
                <x:when test="$num-matches = 0">
                    <x:message terminate="yes">
Invalid 'constant' tag: there is no constant named
'<x:value-of select="$name"/>'.
                    </x:message>
                </x:when>
                <x:otherwise>  <!-- $num-matches &gt; 0 -->
                    <x:message terminate="yes">
Invalid 'constant' tag: there is more than one constant named
'<x:value-of select="$name"/>'.
                    </x:message>
                </x:otherwise>
            </x:choose>
        </x:template>
    </xsl:template>

    <!-- Generates the XSL that outputs the entry that describes a book in
         the list of books in a documentation set. The book being described
         is the one represented by the current element (which is assumed to
         be a 'book' element from a language documentation document). -->
    <xsl:template name="generate-book-in-set-entry">
        <xsl:param name="book-number"/>

        <xsl:variable name="url">
            <xsl:call-template name="part-url">
                <xsl:with-param name="prefix" select="'#'"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="entry">
            <span class="book-number"><xsl:value-of
                select="$book-number"/></span>.
            <xsl:call-template name="generate-title-span">
                <xsl:with-param name="url" select="$url"/>
            </xsl:call-template>
        </div>
    </xsl:template>

    <!-- Generates the XSL that outputs the formatted version of the book of
         documentation represented by the current element (which is assumed
         to be a 'book' element from a language documentation document). -->
    <xsl:template name="generate-book">
        <xsl:variable name="url">
            <xsl:call-template name="part-url"/>
        </xsl:variable>

        <xsl:comment> Output a book </xsl:comment>
        <div class="book" id="{$url}">
        <xsl:call-template name="generate-book-title-page"/>
        <xsl:call-template name="generate-chapter-toc"/>
        <xsl:call-template name="generate-subsection-toc"/>
        <xsl:call-template name="generate-book-contents"/>
        </div>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Generates the title page of the book represented by the current
         element (which is assumed to be a 'book' element in a language
         documentation document). -->
    <xsl:template name="generate-book-title-page">
        <div class="title-page">
            <xsl:apply-templates mode="doc.book-title-page"/>
        </div>
    </xsl:template>

    <xsl:template match="*" mode="doc.book-title-page"/>

    <xsl:template match="title" mode="doc.book-title-page">
        <h1>
            <xsl:call-template name="title-contents">
                <xsl:with-param name="node" select="."/>
            </xsl:call-template>
        </h1>
    </xsl:template>

    <xsl:template match="authors" mode="doc.book-title-page">
        <div class="authors">
            <xsl:apply-templates mode="doc.book-title-page"/>
        </div>
    </xsl:template>

    <xsl:template match="author" mode="doc.book-title-page">
        <div class="author">
            <xsl:value-of select="concat(first-name, ' ', last-name)"/>
        </div>
    </xsl:template>

    <xsl:template match="copyright" mode="doc.book-title-page">
        <div class="copyright">
            <xsl:text>Copyright &copy; </xsl:text>
            <span class="years">
                <xsl:value-of select="years/@from"/>
                <xsl:if test="years/@to">
                    <xsl:value-of select="concat('-', years/@to)"/>
                </xsl:if>
            </span>
            <xsl:text> by </xsl:text>
            <span class="holder"><xsl:value-of select="holder"/></span>
            <xsl:text>. </xsl:text>
            <span class="message"><xsl:value-of select="message"/></span>
            <xsl:text>.</xsl:text>
        </div>
    </xsl:template>


    <!-- Table of contents templates -->


    <!-- Generates the chapter-level table of contents for the book
         represented by the current element. -->
    <xsl:template name="generate-chapter-toc">
        <div class="chapter-toc">
            <h2>Contents at a Glance</h2>
            <xsl:apply-templates
                select="chapter" mode="doc.chapter-toc-item"/>
        </div>
    </xsl:template>

    <!-- Generates the subsection-level table of contents for the book
         represented by the current element. -->
    <xsl:template name="generate-subsection-toc">
        <div class="subsection-toc">
            <h2>Table of Contents</h2>
            <xsl:apply-templates
                select="chapter" mode="doc.subsection-toc-item"/>
        </div>
    </xsl:template>

    <xsl:template match="chapter" mode="doc.chapter-toc-item">
        <xsl:variable name="num">
            <xsl:call-template name="chapter-number"/>
        </xsl:variable>

        <xsl:variable name="url">
            <xsl:call-template name="part-url">
                <xsl:with-param name="prefix" select="'#'"/>
            </xsl:call-template>
        </xsl:variable>

        <div>
            <xsl:attribute name="class">
                <xsl:text>chapter</xsl:text>
                <xsl:if test="@appendix = 'true'">
                    <xsl:text> appendix</xsl:text>
                </xsl:if>
            </xsl:attribute>

            <xsl:if test="@appendix = 'true'">
                <xsl:text>Appendix </xsl:text>
            </xsl:if>
            <span class="number"><xsl:value-of select="$num"/></span>.
            <xsl:call-template name="generate-title-span">
                <xsl:with-param name="url" select="$url"/>
            </xsl:call-template>
        </div>
    </xsl:template>

    <xsl:template match="chapter" mode="doc.subsection-toc-item">
        <xsl:variable name="num">
            <xsl:call-template name="chapter-number"/>
        </xsl:variable>
        <xsl:variable name="url">
            <xsl:call-template name="part-url">
                <xsl:with-param name="prefix" select="'#'"/>
            </xsl:call-template>
        </xsl:variable>

        <div>
            <xsl:attribute name="class">
                <xsl:text>chapter</xsl:text>
                <xsl:if test="@appendix = 'true'">
                    <xsl:text> appendix</xsl:text>
                </xsl:if>
            </xsl:attribute>

            <xsl:if test="@appendix = 'true'">
                <xsl:text>Appendix </xsl:text>
            </xsl:if>
            <span class="number"><xsl:value-of select="$num"/></span>.
            <xsl:call-template name="generate-title-span">
                <xsl:with-param name="url" select="$url"/>
            </xsl:call-template>

            <xsl:apply-templates select="section"
                mode="doc.subsection-toc-item"/>
        </div>
    </xsl:template>

    <xsl:template match="section" mode="doc.subsection-toc-item">
        <xsl:variable name="num">
            <xsl:call-template name="full-section-number"/>
        </xsl:variable>

        <xsl:variable name="url">
            <xsl:call-template name="part-url">
                <xsl:with-param name="prefix" select="'#'"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="section">
            <span class="number"><xsl:value-of select="$num"/></span>.
            <xsl:call-template name="generate-title-span">
                <xsl:with-param name="url" select="$url"/>
            </xsl:call-template>

            <xsl:apply-templates select="subsection"
                mode="doc.subsection-toc-item"/>
        </div>
    </xsl:template>

    <xsl:template match="subsection" mode="doc.subsection-toc-item">
        <xsl:variable name="num">
            <xsl:call-template name="full-subsection-number"/>
        </xsl:variable>
        <xsl:variable name="url">
            <xsl:call-template name="part-url">
                <xsl:with-param name="prefix" select="'#'"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="section">
            <span class="number"><xsl:value-of select="$num"/></span>.
            <xsl:call-template name="generate-title-span">
                <xsl:with-param name="url" select="$url"/>
            </xsl:call-template>
        </div>
    </xsl:template>


    <!-- Templates that match tags, etc. in the language documentation
         document. -->

    <xsl:template match="chapter" mode="doc.generate">
        <xsl:variable name="url">
            <xsl:call-template name="part-url"/>
        </xsl:variable>
        <xsl:variable name="num">
            <xsl:call-template name="chapter-number"/>
        </xsl:variable>

        <div class="chapter" id="{$url}">
            <h2>
                <xsl:if test="@appendix = 'true'">
                    <xsl:text>Appendix </xsl:text>
                </xsl:if>
                <xsl:value-of select="concat($num, '. ')"/>
                <xsl:call-template name="title-contents"/>
            </h2>
            <xsl:apply-templates mode="doc.generate"/>
        </div>
    </xsl:template>

    <xsl:template match="section" mode="doc.generate">
        <xsl:variable name="url">
            <xsl:call-template name="part-url"/>
        </xsl:variable>
        <xsl:variable name="num">
            <xsl:call-template name="full-section-number"/>
        </xsl:variable>

        <div class="section" id="{$url}">
            <h3><xsl:value-of select="concat($num, '. ')"/>
                <xsl:call-template name="title-contents"/></h3>
            <xsl:apply-templates mode="doc.generate"/>
        </div>
    </xsl:template>

    <xsl:template match="subsection" mode="doc.generate">
        <xsl:variable name="url">
            <xsl:call-template name="part-url"/>
        </xsl:variable>

        <div class="subsection" id="{$url}">
            <xsl:variable name="num">
                <xsl:call-template name="full-subsection-number"/>
            </xsl:variable>

            <h4><xsl:value-of select="concat($num, '. ')"/>
                <xsl:call-template name="title-contents"/></h4>
            <xsl:apply-templates mode="doc.generate"/>
        </div>
    </xsl:template>


    <!-- Titles are always output explicitly. -->
    <xsl:template match="title" mode="doc.generate"/>

    <xsl:template match="p" mode="doc.generate">
        <p><xsl:apply-templates mode="doc.generate"/></p>
    </xsl:template>

    <xsl:template match="list" mode="doc.generate">
        <ul>
            <xsl:apply-templates mode="doc.generate"/>
        </ul>
    </xsl:template>

    <xsl:template match="item" mode="doc.generate">
        <li><xsl:apply-templates mode="doc.generate"/></li>
    </xsl:template>

    <xsl:template match="example" mode="doc.generate">
        <div class="example">
            <div class="title">
                <xsl:text>Example </xsl:text>
                <xsl:call-template name="example-number"/>
                <xsl:text>: </xsl:text>
                <xsl:call-template name="title-contents"/>
            </div>
            <xsl:apply-templates mode="doc.generate"/>
        </div>
    </xsl:template>

    <xsl:template match="code-block" mode="doc.generate">
        <pre><xsl:apply-templates mode="doc.generate"/></pre>
    </xsl:template>

    <xsl:template match="code" mode="doc.generate">
        <code><xsl:apply-templates mode="doc.generate"/></code>
    </xsl:template>

    <xsl:template match="var" mode="doc.generate">
        <span class="var"><xsl:value-of select="@name"/></span>
    </xsl:template>

    <xsl:template match="em" mode="doc.generate">
        <em><xsl:apply-templates mode="doc.generate"/></em>
    </xsl:template>


    <xsl:template match="spot" mode="doc.generate">
        <xsl:variable name="url">
            <xsl:call-template name="part-url"/>
        </xsl:variable>

        <span id="{$url}"><xsl:apply-templates mode="doc.generate"/></span>
    </xsl:template>

    <xsl:template match="ref" mode="doc.generate">
        <xsl:variable name="id">
            <xsl:call-template name="referenced-id"/>
        </xsl:variable>
        <xsl:variable name="url" select="concat('#', $id)"/>

        <a href="{$url}"><xsl:apply-templates mode="doc.generate"/></a>
    </xsl:template>

    <xsl:template match="external-ref" mode="doc.generate">
<!-- TODO: add check that an external-ref's '@book' attribute's value
     matches the '@tag' attribute of exactly one book in the set !!! -->
        <xsl:variable name="id">
            <xsl:call-template name="referenced-id">
                <xsl:with-param name="book" select="key('book', @book)"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="url" select="concat('#', $id)"/>

        <a href="{$url}"><xsl:apply-templates mode="doc.generate"/></a>
    </xsl:template>


    <xsl:template match="grammar-rules" mode="doc.generate">
        <div class="grammar-rules">
            <xsl:apply-templates mode="doc.generate"/>
        </div>
    </xsl:template>


    <!-- Title Templates -->

    <!-- Generates the 'span' element for a document part title. -->
    <xsl:template name="generate-title-span">
        <xsl:param name="url"/>
        <xsl:param name="node" select="title"/>

        <span class="title"><a href="{$url}">
            <xsl:call-template name="title-contents">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </a></span>
    </xsl:template>

    <!-- Outputs the contents of the specified 'title' element. -->
    <xsl:template name="title-contents">
        <xsl:param name="node" select="title"/>

        <xsl:for-each select="$node">
            <xsl:apply-templates mode="doc.generate"/>
        </xsl:for-each>
    </xsl:template>


    <!-- URL Templates -->

    <!-- Outputs the URL for the documentation part represented by the
         specified node, prefixed with the specified preifx. -->
    <xsl:template name="part-url">
        <xsl:param name="part" select="."/>
        <xsl:param name="prefix" select="''"/>

        <xsl:variable name="id">
            <xsl:call-template name="part-id">
                <xsl:with-param name="part" select="$part"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat($prefix, $id)"/>
    </xsl:template>
</xsl:transform>
