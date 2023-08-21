<?xml version="1.0"?>
<!--
    $Id: language-definition.xsl,v 1.1 2005/10/12 15:29:24 jgm Exp $

    Transforms the Atria language description document into a document that
    defines and explains the Atria language.

TODO: FINISH THIS TO DOCUMENT Atria !!!

TODO: for now the generated document will be in HTML, but change that !!!
- modify this document to generate less HTML-specific output
- write XSL documents that take this file as input and output XSL files
  that, when applied to the Atria language description document, outputs
  the language definition document in specific formats
    - e.g. HTML, LaTeX, plain text, man (?), info (?)

    Author: James MacKay
    Last Updated: $Date: 2005/10/12 15:29:24 $

    Copyright (C) 2005-2012 by James MacKay.

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

    <xsl:import href="../document-common.html.xsl"/>
    <xsl:import href="../grammar-rules.html.xsl"/>

    <xsl:output method="html"/>


    <!-- Configuration -->

    <xsl:variable name="our-id" select="'LanguageDefinition'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Language Definition')"/>


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


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="language-definition-content"/>
        <xsl:call-template name="language-definition-index"/>
    </xsl:template>

    <xsl:template name="language-definition-content">
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
                <xsl:call-template name="language-definition-body"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="language-definition-index">
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


    <!-- Index templates -->

    <xsl:template name="index-links">
<!-- write this !!! -->
        <p>
Chapter/section links go here !!! [See table of contents]
</p>
    </xsl:template>



    <!-- Content templates -->

    <xsl:template name="language-definition-body">
        <xsl:call-template name="table-of-contents"/>
        <xsl:call-template name="introduction"/>
        <xsl:call-template name="part.language"/>
<!--
        <xsl:call-template name="part.appendices"/>
-->
    </xsl:template>

    <xsl:template name="table-of-contents">
<h2 id="ToC">Table of Contents</h2>
<ul>
    <li><a href="#introduction">Introduction</a></li>
    <li><a href="#part-language">Part 1: The Language</a></li>
    <li><a href="part-appendices">Part 2: Appendices</a></li>
</ul>
    </xsl:template>

    <xsl:template name="introduction">

        <h2>Introduction</h2>
<p>
Atria is …
</p>
    </xsl:template>

    <xsl:template name="part.language">
        <p>
Document contents go here !!!
</p>
    </xsl:template>

</xsl:transform>
