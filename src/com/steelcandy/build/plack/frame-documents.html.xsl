<?xml version="1.0"?>
<!--
    $Id: frame-documents.html.xsl,v 1.4 2005/11/10 04:02:14 jgm Exp $

    Transforms a language description document into the frame-specific
    HTML documents used in the frames-enabled version of the language
    documents. This includes the frameset HTML documents, but not the index
    documents, except for the overview index document.

    Author: James MacKay
    Last Updated: $Date: 2005/11/10 04:02:14 $

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

    <xsl:variable name="overview-index-filename"
        select="concat('overview-index', $htmlExt)"/>


    <!-- Global variables -->


    <!-- Main templates -->

    <xsl:template match="language">
        <!-- Index documents -->
        <xsl:call-template name="overview-index"/>

        <!-- Frameset documents -->
        <xsl:call-template name="main-frameset"/>
        <xsl:call-template name="grammar-frameset"/>
        <xsl:call-template name="validity-constraints-frameset"/>
        <xsl:call-template name="semantics-frameset"/>
        <xsl:call-template name="operator-method-map-frameset"/>
        <xsl:call-template name="language-definition-frameset"/>
        <xsl:call-template name="other-docs-frameset"/>
    </xsl:template>


    <!-- Index templates -->

    <xsl:template name="overview-index">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$overview-index-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-index-document">
            <xsl:with-param name="title"
                select="concat($language-name, ' Overview Index')"/>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="overview-index-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="overview-index-links">
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'Main'"/>
            <xsl:with-param name="text" select="'Main'"/>
        </xsl:call-template>
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'Grammar'"/>
            <xsl:with-param name="text" select="'Grammar'"/>
        </xsl:call-template>
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'ValidityConstraints'"/>
            <xsl:with-param name="text" select="'Validity Constraints'"/>
        </xsl:call-template>
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'Semantics'"/>
            <xsl:with-param name="text" select="'Semantics'"/>
        </xsl:call-template>
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'OperatorMethodMap'"/>
            <xsl:with-param name="text" select="'Operators'"/>
        </xsl:call-template>
        <xsl:call-template name="make-main-index-entry">
            <xsl:with-param name="id" select="'LanguageDefinition'"/>
            <xsl:with-param name="text" select="'Language Definition'"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Frameset templates -->

    <xsl:template name="main-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'Main'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Document Index')"/>
            <xsl:with-param name="no-frames-suffix" select="'-noframes'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="grammar-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'Grammar'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Grammar')"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="validity-constraints-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'ValidityConstraints'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Validity Constraints')"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="semantics-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'Semantics'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Semantics')"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="operator-method-map-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'OperatorMethodMap'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Operator-Method Mappings')"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="language-definition-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'LanguageDefinition'"/>
            <xsl:with-param name="title"
                select="concat($language-name, ' Language Definition')"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="other-docs-frameset">
        <xsl:call-template name="frameset-document">
            <xsl:with-param name="id" select="'OtherDocumentation'"/>
            <xsl:with-param name="title"
                select="concat('Other ', $language-name, ' Documaentation')"/>
        </xsl:call-template>
    </xsl:template>


    <!-- The template to use to output an HTML frameset document for use
         in displaying language-related documents in frames. -->
    <xsl:template name="frameset-document">
        <xsl:param name="title"/>
        <xsl:param name="id"/>
        <xsl:param name="no-frames-suffix" select="''"/>

        <xsl:variable name="filename">
            <xsl:call-template name="map-id-to-filename">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="suffix" select="'-frames'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="index-url">
            <xsl:call-template name="map-id-to-url">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="suffix" select="'-index'"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="content-url">
            <xsl:call-template name="map-id-to-url">
                <xsl:with-param name="id" select="$id"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="no-frames-url">
            <xsl:call-template name="map-id-to-url">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="suffix" select="$no-frames-suffix"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$filename"/>
        </xsl:call-template>
<html>
        <xsl:call-template name="document-head">
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
<frameset cols="18%,*">
    <frameset rows="23%,*">
        <frame src="{$overview-index-filename}" name="overview"/>
        <frame src="{$index-url}" name="{$indexFrameName}"/>
    </frameset>
    <frame src="{$content-url}" name="{$contentFrameName}"/>
    <noframes>
        This document requires that your browser supports frames. Please
        use the <a href="{$no-frames-url}" target="_top">non-frames
        version of this document</a> instead.
    </noframes>
</frameset>
</html>
        <xsl:text>
</xsl:text>
    </xsl:template>
</xsl:transform>
