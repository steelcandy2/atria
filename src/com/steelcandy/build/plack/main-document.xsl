<?xml version="1.0"?>
<!--
    Transforms a language description document into the language's
    main HTML document: a document that provides links to all of the
    other documents related to the language.

    Copyright (C) 2004-2005 by James MacKay.

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

    <xsl:variable name="our-id" select="'Main'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Document Index')"/>

    <xsl:variable name="link-data-url" select="'main-links.xml'"/>


    <!-- Global variables -->

    <xsl:variable name="content-filename">
        <xsl:call-template name="id-to-content-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="no-frames-content-filename">
        <xsl:call-template name="map-id-to-filename">
            <xsl:with-param name="id" select="$our-id"/>
            <xsl:with-param name="suffix" select="'-noframes'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="index-filename">
        <xsl:call-template name="id-to-index-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="link-data"
        select="document($link-data-url)"/>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="main-content"/>
        <xsl:call-template name="main-no-frames-content"/>
        <xsl:call-template name="main-index"/>
    </xsl:template>

    <xsl:template name="main-content">
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
                <xsl:call-template name="content-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="main-no-frames-content">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$no-frames-content-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="menu">
                <xsl:call-template name="language-document-menu">
                    <xsl:with-param name="id" select="$our-id"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="no-frames-content-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="main-index">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$index-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-index-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="index-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- Content templates -->

    <!-- Outputs the main links as they should appear in the content
         frame (or in the standalone page). -->
    <xsl:template name="content-links">
        <xsl:apply-templates select="$link-data/*" mode="content-links"/>
    </xsl:template>

    <xsl:template match="group" mode="content-links">
        <xsl:if test="string-length(@title) &gt; 0">
            <h2><xsl:value-of select="@title"/></h2>
        </xsl:if>
        <ul>
            <xsl:apply-templates select="*" mode="content-links"/>
        </ul>
    </xsl:template>

    <xsl:template match="link" mode="content-links">
        <li><xsl:call-template name="make-main-link"/></li>
    </xsl:template>


    <xsl:template name="no-frames-content-links">
        <xsl:apply-templates select="$link-data/*" mode="no-frames-content-links"/>
    </xsl:template>

    <xsl:template match="group" mode="no-frames-content-links">
        <xsl:if test="string-length(@title) &gt; 0">
            <h2><xsl:value-of select="@title"/></h2>
        </xsl:if>
        <ul>
            <xsl:apply-templates select="*" mode="no-frames-content-links"/>
        </ul>
    </xsl:template>

    <xsl:template match="link" mode="no-frames-content-links">
        <li><xsl:call-template name="make-main-link">
                <xsl:with-param name="use-frames" select="false()"/>
            </xsl:call-template></li>
    </xsl:template>


    <!-- Index templates -->

    <!-- Outputs the main links as they should appear in the index frame. -->
    <xsl:template name="index-links">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$content-filename"/>
            <xsl:with-param name="text" select="$top-name"/>
        </xsl:call-template>

        <xsl:apply-templates select="$link-data/*" mode="index-links"/>
    </xsl:template>

    <xsl:template match="group" mode="index-links">
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="not(@short-title)">
                    <xsl:value-of select="@title"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@short-title"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:if test="string-length($title) &gt; 0">
            <h4><xsl:value-of select="$title"/></h4>
        </xsl:if>
        <xsl:apply-templates select="*" mode="index-links"/>
    </xsl:template>

    <xsl:template match="link" mode="index-links">
        <div class="plack-minor-index-entry"><xsl:call-template name="make-main-link">
                <xsl:with-param name="use-short" select="true()"/>
            </xsl:call-template></div>
    </xsl:template>


    <!-- Utility templates -->

    <!-- Outputs the HTML code for the link described by the specified
         link data document element. -->
    <xsl:template name="make-main-link">
        <xsl:param name="data" select="."/>
        <xsl:param name="use-short" select="false()"/>
        <xsl:param name="use-frames" select="true()"/>

        <xsl:variable name="id" select="$data/@id"/>
        <xsl:variable name="url" select="$data/@url"/>
        <xsl:variable name="text">
            <xsl:choose>
                <xsl:when test="$use-short = true() and
                                string-length($data/@short-text) &gt; 0">
                    <xsl:value-of select="$data/@short-text"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$data/@text"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="target">
            <xsl:choose>
                <xsl:when test="$use-frames = false()">
                    <xsl:value-of select="$topFrameName"/>
                </xsl:when>
                <xsl:when test="$id">
                    <!-- pages identified by ID consist of framesets and
                         so replace the whole window. -->
                    <xsl:value-of select="$topFrameName"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$contentFrameName"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$url and $id">
                <xsl:message>
Both the 'id' and 'url' attributes appear on the following 'link'
element from the document <xsl:value-of select="$link-data-url"/>:

<xsl:copy-of select="$data"/>

Exactly one of the attributes can be specified.
                </xsl:message>
            </xsl:when>
            <xsl:when test="$url">
                <xsl:call-template name="make-link">
                    <xsl:with-param name="url" select="$url"/>
                    <xsl:with-param name="text" select="$text"/>
                    <xsl:with-param name="target" select="$target"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$id">
                <xsl:variable name="suffix">
                    <xsl:choose>
                        <xsl:when test="$use-frames = 'true'">-frames</xsl:when>
                        <xsl:otherwise></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:call-template name="make-link">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="text" select="$text"/>
                    <xsl:with-param name="target" select="$target"/>
                    <xsl:with-param name="suffix" select="$suffix"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message>
Both the 'id' and 'url' attributes are missing from the following
'link' element from the document <xsl:value-of select="$link-data-url"/>:

<xsl:copy-of select="$data"/>

Exactly one of the attributes must be specified.
                </xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:transform>
