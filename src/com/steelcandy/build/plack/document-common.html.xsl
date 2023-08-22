<?xml version="1.0"?>
<!--
    Provides common functionality for stylesheets that transform a
    language description document into an HTML document. It is intended to
    be xsl:imported into another stylesheet.

    This stylesheet makes no assumptions about the stylesheet that
    imports it.

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
<!ENTITY uc "ABCDEFGHIJKLMNOPQRSTUVWXYZ">
<!ENTITY lc "abcdefghijklmnopqrstuvwxyz">
]>

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="language-common.xsl"/>


    <!-- Configuration -->

    <!-- Outputs the filename (without any directory parts) corresponding to
         the specified ID, or outputs the empty string if there is no
         filename corresponding to the specified ID. If a suffix is specified
         and there is a filename corresponding to the ID then the suffix is
         appended to the filename. -->
    <xsl:template name="map-id-to-filename">
        <xsl:param name="id"/>
        <xsl:param name="suffix" select="''"/>

        <xsl:variable name="root">
            <xsl:choose>
                <xsl:when test="$id = 'Grammar'">
                    <xsl:text>grammar</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'ValidityConstraints'">
                    <xsl:text>validity-constraints</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'OperatorMethodMap' or
                                $id = 'UnaryOperatorMethodMap' or
                                $id = 'BinaryOperatorMethodMap'">
                    <xsl:text>operator-method-map</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'Semantics'">
                    <xsl:text>semantics</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'LanguageDefinition'">
                    <xsl:text>language-definition</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'OtherDocumentation'">
                    <xsl:text>other</xsl:text>
                </xsl:when>
                <xsl:when test="$id = 'Main'">
                    <xsl:text>index</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text></xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:value-of select="$root"/>
        <xsl:if test="string-length($root) &gt; 0">
            <xsl:value-of select="concat($suffix, $htmlExt)"/>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the URL corresponding to the specified link ID, or outputs
         the empty string if there is no URL corresponding to the specified
         ID. If a suffix is specified and there is a URL corresponding to
         the ID then the suffix is appended to the filename in the URL. -->
    <xsl:template name="map-id-to-url">
        <xsl:param name="id"/>
        <xsl:param name="suffix" select="''"/>

        <xsl:variable name="filename">
            <xsl:call-template name="map-id-to-filename">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="suffix" select="$suffix"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="string-length($filename) &gt; 0">
            <!-- Append any required fragment selector part. -->
            <xsl:choose>
                <xsl:when test="$id = 'UnaryOperatorMethodMap'">
                    <xsl:value-of select="concat($filename,
                                                 '#UnaryOperators')"/>
                </xsl:when>
                <xsl:when test="$id = 'BinaryOperatorMethodMap'">
                    <xsl:value-of select="concat($filename,
                                                 '#BinaryOperators')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$filename"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>


    <!-- Global variables -->

    <xsl:variable name="html-stylesheet-base" select="'stylesheet'"/>
    <xsl:variable name="html-stylesheet-ext" select="'.css'"/>
    <xsl:variable name="html-stylesheet-all"
        select="concat($html-stylesheet-base, $html-stylesheet-ext)"/>
    <xsl:variable name="html-stylesheet-screen"
        select="concat($html-stylesheet-base, '-screen', $html-stylesheet-ext)"/>
    <xsl:variable name="html-stylesheet-print"
        select="concat($html-stylesheet-base, '-print', $html-stylesheet-ext)"/>

    <xsl:variable name="author" select="'James MacKay'"/>
    <xsl:variable name="e-mail" select="'jgm@steelcandy.org'"/>

    <xsl:variable name="spaces-per-indent-level" select="4"/>

    <xsl:variable name="htmlExt" select="'.html'"/>

    <xsl:variable name="topFrameName" select="'_top'"/>
    <xsl:variable name="indexFrameName" select="'index'"/>
    <xsl:variable name="contentFrameName" select="'content'"/>

    <!-- The name of the node at the 'top' of a document. -->
    <xsl:variable name="top-name" select="'Top'"/>
    <xsl:variable name="top-link" select="concat('#', $top-name)"/>


    <!-- Link templates -->

    <xsl:template match="link">
        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="@id"/>
            <xsl:with-param name="url" select="@url"/>
            <xsl:with-param name="text" select="@text"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Creates an entry in the main index document. Such entries will
         replace the entire frameset (usually with another frameset).

         Note: make-{major,minor}-index-entry can still be used to create
         entries in the main index document that only replace the content
         frame. -->
    <xsl:template name="make-main-index-entry">
        <xsl:param name="id" select="''"/>
        <xsl:param name="url" select="''"/>
        <xsl:param name="suffix" select="''"/>
        <xsl:param name="text"/>

        <div class="plack-main-index-entry">
        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="target" select="'_top'"/>
            <xsl:with-param name="suffix" select="'-frames'"/>
            <xsl:with-param name="text" select="$text"/>
        </xsl:call-template>
        </div>
    </xsl:template>

    <!-- Creates a major entry in an index document. -->
    <xsl:template name="make-major-index-entry">
        <xsl:param name="id" select="''"/>
        <xsl:param name="url" select="''"/>
        <xsl:param name="suffix" select="''"/>
        <xsl:param name="text"/>
        <xsl:param name="prefix-link-href" select="''"/>
        <xsl:param name="prefix-link-text" select="''"/>
        <xsl:param name="suffix-link-href" select="''"/>
        <xsl:param name="suffix-link-text" select="''"/>

        <div class="plack-major-index-entry">
        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="target" select="'content'"/>
            <xsl:with-param name="suffix" select="$suffix"/>
            <xsl:with-param name="text" select="$text"/>
            <xsl:with-param name="prefix-link-href" select="$prefix-link-href"/>
            <xsl:with-param name="prefix-link-text" select="$prefix-link-text"/>
            <xsl:with-param name="suffix-link-href" select="$suffix-link-href"/>
            <xsl:with-param name="suffix-link-text" select="$suffix-link-text"/>
        </xsl:call-template>
        </div>
    </xsl:template>

    <!-- Creates a minor entry in an index document. -->
    <xsl:template name="make-minor-index-entry">
        <xsl:param name="id" select="''"/>
        <xsl:param name="url" select="''"/>
        <xsl:param name="suffix" select="''"/>
        <xsl:param name="text"/>

        <div class="plack-minor-index-entry">
        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="target" select="'content'"/>
            <xsl:with-param name="suffix" select="$suffix"/>
            <xsl:with-param name="text" select="$text"/>
        </xsl:call-template>
        </div>
    </xsl:template>

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

    <!-- Creates a link with the specified text that links to
            - the URL corresponding to the specified ID, if one is
              specified and there is a URL corresponding to that ID, or
            - the specified URL, if one is specified, or
            - nothing: just the link text is output

         If a 'prefix-link-href' parameter is specified then a link to that
         URL is added BEFORE the index entry, and on the same line. The added
         link's text is given by the optional 'prefix-link-text' parameter,
         so it should be specified whenever 'prefix-link-href' is. Similarly,
         if a 'suffix-link-href' parameter is specified then a link to that
         URL is added AFTER the index entry, and on the same line. The added
         link's text is given by the optional 'suffix-link-text' parameter,
         so it should be specified whenever 'suffix-link-href' is. -->
    <xsl:template name="make-link">
        <xsl:param name="id" select="''"/>
        <xsl:param name="url" select="''"/>
        <xsl:param name="fragmentId" select="''"/>
        <xsl:param name="target" select="''"/>
        <xsl:param name="suffix" select="''"/>
        <xsl:param name="text"/>
        <xsl:param name="title" select="''"/>
        <xsl:param name="prefix-link-href" select="''"/>
        <xsl:param name="prefix-link-text" select="''"/>
        <xsl:param name="suffix-link-href" select="''"/>
        <xsl:param name="suffix-link-text" select="''"/>

        <!-- First try to build the URL from 'id'. -->
        <xsl:variable name="url-1">
            <xsl:call-template name="map-id-to-url">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="suffix" select="$suffix"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- If 'url-1' is the empty string then either no ID was specified
             or there was no URL corresponding to the ID. -->
        <xsl:variable name="url-2">
            <xsl:choose>
                <xsl:when test="string-length($url-1) &gt; 0">
                    <xsl:value-of select="$url-1"/>
                </xsl:when>
                <xsl:when test="string-length($url) &gt; 0">
                    <xsl:value-of select="$url"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="no">

*** WARNING *** There is no URL corresponding to the
link with ID &quot;<xsl:value-of select="@id"/>&quot; and text
&quot;<xsl:value-of select="@text"/>&quot;, and
no URL was explicitly specified.
                    </xsl:message>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- Append the specified fragment ID, if any, to the URL. -->
        <xsl:variable name="final-url">
            <xsl:choose>
                <xsl:when test="string-length($fragmentId) &gt; 0">
                    <xsl:value-of select="concat($url-2, '#', $fragmentId)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$url-2"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="is-prefix-link"
            select="string-length($prefix-link-href) &gt; 0"/>
        <xsl:if test="$is-prefix-link and string-length($prefix-link-text) = 0">
            <xsl:message terminate="yes">
In making the link with to the URL '<xsl:value-of select="$final-url"/>'
with text '<xsl:value-of select="$text"/>', a prefix link
to the URL '<xsl:value-of select="$prefix-link-href"/> was to be
added, but no text was specified for the prefix link. Please specify a
'prefix-link-text' parameter with a non-empty value for the prefix link's text.
            </xsl:message>
        </xsl:if>

        <xsl:variable name="is-suffix-link"
            select="string-length($suffix-link-href) &gt; 0"/>
        <xsl:if test="$is-suffix-link and string-length($suffix-link-text) = 0">
            <xsl:message terminate="yes">
In making the link with to the URL '<xsl:value-of select="$final-url"/>'
with text '<xsl:value-of select="$text"/>', a suffix link
to the URL '<xsl:value-of select="$suffix-link-href"/> was to be
added, but no text was specified for the suffix link. Please specify a
'suffix-link-text' parameter with a non-empty value for the suffix link's text.
            </xsl:message>
        </xsl:if>

        <xsl:if test="$is-prefix-link"><a
            href="{$prefix-link-href}"><xsl:value-of
            select="$prefix-link-text"/></a>&nbsp;
        </xsl:if>
        <xsl:choose>
            <xsl:when test="string-length($final-url) &gt; 0">
                <a href="{$final-url}">
                <xsl:if test="string-length($target) &gt; 0">
                    <xsl:attribute name="target">
                        <xsl:value-of select="$target"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="string-length($title) &gt; 0">
                    <xsl:attribute name="title">
                        <xsl:value-of select="$title"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:value-of select="$text"/></a>
            </xsl:when>
            <xsl:otherwise>
                <!-- Output the text by itself. -->
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="$is-suffix-link">&nbsp;<a
            href="{$suffix-link-href}"><xsl:value-of
            select="$suffix-link-text"/></a>
        </xsl:if>
    </xsl:template>


    <!-- Presentation templates -->

    <!-- The template to use to output an HTML index document: a document
         that appears in the index frame of the frames version of the
         language documents. It embeds the specified content in the body of
         a standard index document. -->
    <xsl:template name="html-index-document">
        <xsl:param name="title"/>
        <xsl:param name="doc-content">
            <p>
            No index content was specified!</p>
        </xsl:param>

        <html>
        <xsl:call-template name="document-head">
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
        <body>
        <xsl:copy-of select="$doc-content"/>
        </body>
        </html>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- The template to use to output an HTML document: it embeds the
         specified content in the body of a standard document 'template'. -->
    <xsl:template name="html-document">
        <xsl:param name="title"/>
        <xsl:param name="first-copyright-year"
            select="$language-first-copyright-year"/>
        <xsl:param name="menu" select="''"/>
        <xsl:param name="doc-content">
            <p>
            No content was specified!</p>
        </xsl:param>

        <html>
        <xsl:call-template name="document-head">
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
        <body>
        <a name="{$top-name}"><span /></a>
        <xsl:copy-of select="$menu"/>
        <xsl:call-template name="document-start">
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
        <xsl:copy-of select="$doc-content"/>
        <xsl:call-template name="address-block">
            <xsl:with-param name="title" select="$title"/>
            <xsl:with-param name="first-copyright-year"
                select="$first-copyright-year"/>
        </xsl:call-template>
        </body>
        </html>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs the head to use for all HTML documents. -->
    <xsl:template name="document-head">
        <xsl:param name="title"/>

        <head>
            <title><xsl:value-of select="$title"/></title>
            <link type="text/css" rel="stylesheet" href="{$html-stylesheet-all}" media="all"/>
            <link type="text/css" rel="stylesheet" href="{$html-stylesheet-screen}" media="screen"/>
            <link type="text/css" rel="stylesheet" href="{$html-stylesheet-print}" media="print"/>
        </head>
    </xsl:template>

    <!-- Outputs the title and other information that should appear at
         the start of all HTML documents. -->
    <xsl:template name="document-start">
        <xsl:param name="title"/>

            <!--
                Note: this file was automatically generated, and so should
                      not be edited directly.
            -->
            <div class="plack-top" align="center">
            <h1><xsl:value-of select="$title"/></h1>
            <span class="plack-author"><xsl:value-of
                select="$author"/></span><br />
            <span class="plack-generation-time"><xsl:value-of
                select="$generation-date-time"/></span><br />
            </div>
            <hr />
    </xsl:template>


    <!-- The menu that is to appear at the top of most/all language
         documents. -->
    <xsl:template name="language-document-menu">
        <xsl:param name="id"/> <!-- The calling document's ID -->

        <!--
            [Frames | No Frames]
        -->

        <xsl:variable name="frames-suffix" select="'-frames'"/>
        <xsl:variable name="no-frames-suffix">
            <xsl:choose>
                <xsl:when test="$id = 'Main'">-noframes</xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <div class="plack-language-document-menu">
            [<xsl:call-template name="make-link">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="target" select="'_top'"/>
                <xsl:with-param name="suffix" select="$frames-suffix"/>
                <xsl:with-param name="text" select="'Frames'"/>
            </xsl:call-template> |
            <xsl:call-template name="make-link">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="target" select="'_top'"/>
                <xsl:with-param name="suffix" select="$no-frames-suffix"/>
                <xsl:with-param name="text" select="'No Frames'"/>
            </xsl:call-template>]
        </div>
    </xsl:template>

    <!-- Outputs the address block that should appear at the bottom of
         all documents. -->
    <xsl:template name="address-block">
        <xsl:param name="title"/>
        <xsl:param name="first-copyright-year"/>

        <address>
            <div class="document-title">Title: <xsl:value-of select="$title"/></div>
            <div class="author">Author: <xsl:value-of select="$author"/>
                (<xsl:call-template name="make-mailto-link">
                    <xsl:with-param name="address" select="$e-mail"/>
                </xsl:call-template>)</div>
            <div class="copyright">Copyright &copy; <xsl:value-of select="concat($first-copyright-year, '-', $last-copyright-year, ' ', $author)"/></div>
            <div class="last-updated">Last Updated: <xsl:value-of select="$generation-date-time"/></div>
            <div class="url">URL:<nobr /><script>
                document.write(document.URL);</script>
                <noscript>&nbsp;[unknown]</noscript></div>

            <!-- This is just about the same as $generation-date-time
            <div class="last-modified">Last Modified:<nobr /><script>
                document.write(document.lastModified);</script></div>
            -->
        </address>
    </xsl:template>


    <!-- Outputs the table of contents. -->
    <xsl:template name="table-of-contents">
        <xsl:param name="titles"/>
        <ul>
            <xsl:call-template name="toc-section-entries">
                <xsl:with-param name="titles" select="$titles"/>
            </xsl:call-template>
        </ul>
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
            <li><xsl:call-template name="section-link">
                    <xsl:with-param name="title" select="$first-title"/>
                </xsl:call-template></li>
            <xsl:call-template name="toc-section-entries">
                <xsl:with-param name="titles" select="$rest"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Outputs the toolbar that provides links to all of the sections
         with the specified titles. -->
    <xsl:template name="sections-toolbar">
        <xsl:param name="titles"/>
        <p>
        <div class="plack-toolbar" align="center">
            <xsl:call-template name="section-link">
                <xsl:with-param name="title" select="$top-name"/>
            </xsl:call-template>
            <xsl:text> | </xsl:text>
            <xsl:call-template name="sections-toolbar-entries">
                <xsl:with-param name="titles" select="$titles"/>
            </xsl:call-template>
        </div>
        </p>
    </xsl:template>

    <xsl:template name="sections-toolbar-entries">
        <xsl:param name="titles"/>

        <xsl:if test="$titles">
            <xsl:variable name="first-title"
                select="substring-before($titles, '|')"/>
            <xsl:variable name="rest"
                select="substring-after($titles, '|')"/>
            <xsl:call-template name="section-link">
                <xsl:with-param name="title" select="$first-title"/>
            </xsl:call-template>
            <xsl:if test="$rest">
                <xsl:text> | </xsl:text>
                <xsl:call-template name="sections-toolbar-entries">
                    <xsl:with-param name="titles" select="$rest"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
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

    <!-- Returns a link to the section of the document with the
         specified title. -->
    <xsl:template name="section-link">
        <xsl:param name="title"/>

        <xsl:variable name="url"
            select="concat('#', translate($title, ' ', '_'))"/>
        <xsl:call-template name="make-link">
            <xsl:with-param name="url" select="$url"/>
            <xsl:with-param name="text" select="$title"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="list">
        <ul>
        <xsl:for-each select="item">
            <li><xsl:apply-templates/></li>
        </xsl:for-each>
        </ul>
    </xsl:template>


    <!-- Replace <constant> elements with the constant's value. -->
    <xsl:template match="constant">
        <xsl:call-template name="constant-value">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Replace <constant-*-list-*> elements with the appropriate list of
         constant values. -->
    <xsl:template match="constant-or-list-across">
        <xsl:variable name="start">
            <xsl:choose>
                <xsl:when test="@start-quote">
                    <xsl:value-of select="@start-quote"/>
                </xsl:when>
                <xsl:when test="@quote">
                    <xsl:value-of select="@quote"/>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="end">
            <xsl:choose>
                <xsl:when test="@end-quote">
                    <xsl:value-of select="@end-quote"/>
                </xsl:when>
                <xsl:when test="@quote">
                    <xsl:value-of select="@quote"/>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="output-constant-list-across">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="', '"/>
            <xsl:with-param name="last-separator" select="' or '"/>
            <xsl:with-param name="item-prefix" select="$start"/>
            <xsl:with-param name="item-suffix" select="$end"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="constant-and-list-across">
        <xsl:variable name="start">
            <xsl:choose>
                <xsl:when test="@start-quote">
                    <xsl:value-of select="@start-quote"/>
                </xsl:when>
                <xsl:when test="@quote">
                    <xsl:value-of select="@quote"/>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="end">
            <xsl:choose>
                <xsl:when test="@end-quote">
                    <xsl:value-of select="@end-quote"/>
                </xsl:when>
                <xsl:when test="@quote">
                    <xsl:value-of select="@quote"/>
                </xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:call-template name="output-constant-list-across">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="', '"/>
            <xsl:with-param name="last-separator" select="' and '"/>
            <xsl:with-param name="item-prefix" select="$start"/>
            <xsl:with-param name="item-suffix" select="$end"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="constant-list-down">
        <xsl:variable name="sep">
                <xsl:text>
    </xsl:text>
        </xsl:variable>

        <xsl:call-template name="output-constant-list-down">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="''"/>
            <xsl:with-param name="last-separator" select="''"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="constant-or-list-down">
        <xsl:call-template name="output-constant-list-down">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="','"/>
            <xsl:with-param name="last-separator" select="', or'"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="constant-and-list-down">
        <xsl:call-template name="output-constant-list-down">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="separator" select="','"/>
            <xsl:with-param name="last-separator" select="', and'"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="notes">
        <xsl:call-template name="output-notes"/>
    </xsl:template>

    <!-- Outputs all of the notes in the specified notes nodes, merged
         together into one set of nodes. -->
    <xsl:template name="output-merged-notes">
        <xsl:param name="notes-nodes" select="."/>
        <xsl:param name="title" select="'Notes'"/>

        <!-- Don't output anything unless there's at least one note across
             all of the sets of notes that isn't hidden. -->
        <xsl:if test="count($notes-nodes/note[not(@hidden = 'true')]) &gt; 0">
            <tr><td colspan="2">
            <h3><xsl:value-of select="$title"/>:</h3>
            <ul>
            <xsl:for-each select="$notes-nodes">
                <xsl:call-template name="output-note-children">
                    <xsl:with-param name="parent" select="."/>
                </xsl:call-template>
            </xsl:for-each>
            </ul>
            </td></tr>
        </xsl:if>
    </xsl:template>

    <xsl:template name="output-notes">
        <xsl:param name="notes-node" select="."/>
        <xsl:param name="title" select="'Notes'"/>

        <!-- Don't output anything unless there's at least one note across
             all of the sets of notes that isn't hidden. -->
        <xsl:if test="count($notes-node/note[not(@hidden = 'true')]) &gt; 0">
            <tr><td colspan="2">
            <h3><xsl:value-of select="$title"/>:</h3>
            <ul>
            <xsl:call-template name="output-note-children">
                <xsl:with-param name="parent" select="$notes-node"/>
            </xsl:call-template>
            </ul>
            </td></tr>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the HTML corresponsing to each of the non-hidden 'note'
         children of the specified parent node. -->
    <xsl:template name="output-note-children">
        <xsl:param name="parent"/>

        <xsl:for-each select="$parent/note[not(@hidden = 'true')]">
            <li><xsl:apply-templates/></li>
        </xsl:for-each>
    </xsl:template>


    <!-- Elements that should be output unchanged. -->
    <xsl:template match="em | code | strong | pre">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- Report unknown/unexpected elements. -->
    <xsl:template match="*">
        <xsl:message terminate="yes">
Encountered an unexpected element named '<xsl:value-of select="local-name()"/>' in
the language description document when generating a document.
        </xsl:message>
    </xsl:template>


    <!-- Utility templates -->

    <!-- Outputs the filename of the content HTML document corresponding to
         the specified ID, or outputs the empty string if there is no
         filename corresponding to the specified ID. -->
    <xsl:template name="id-to-content-filename">
        <xsl:param name="id"/>

        <xsl:call-template name="map-id-to-filename">
            <xsl:with-param name="id" select="$id"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the filename of the index HTML document corresponding to
         the specified ID, or outputs the empty string if there is no
         filename corresponding to the specified ID. -->
    <xsl:template name="id-to-index-filename">
        <xsl:param name="id"/>

        <xsl:call-template name="map-id-to-filename">
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="suffix" select="'-index'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the first part of an HTML document when it is just one of
         multiple files. -->
    <xsl:template name="html-prologue">
        <xsl:param name="filename"/>

        <xsl:if test="string-length($filename) = 0">
            <xsl:message terminate="yes">
Attempted to output a frameset or index HTML document with a blank filename.
            </xsl:message>
        </xsl:if>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="$filename"/>
        <xsl:text>
</xsl:text>
    </xsl:template>
</xsl:transform>
