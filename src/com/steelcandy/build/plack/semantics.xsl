<?xml version="1.0"?>
<!--
    $Id: semantics.xsl,v 1.7 2006/06/10 13:23:58 jgm Exp $

    Transforms a language description document into an HTML document
    detailing the language's semantics.

    Author: James MacKay
    Last Updated: $Date: 2006/06/10 13:23:58 $

    Copyright (C) 2005-2008 by James MacKay.

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

    <xsl:variable name="our-id" select="'Semantics'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Semantics')"/>

    <!-- Links in grammar rules link to the construct's semantics. (This
         overrides the definition in grammar-rules.html.xsl. -->
    <xsl:variable name="grammar.construct-link-id"
        select="'Semantics'"/>


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

    <xsl:variable name="semantics" select="$top/semantics"/>

    <xsl:variable name="semantics-definitions"
        select="$semantics/section/definition"/>

    <xsl:variable name="section-titles">
        <xsl:call-template name="section-titles"/>
    </xsl:variable>
    <xsl:variable name="sections-toolbar">
        <xsl:call-template name="sections-toolbar">
            <xsl:with-param name="titles" select="$section-titles"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="semantics-content"/>
        <xsl:call-template name="semantics-index"/>
    </xsl:template>

    <xsl:template name="semantics-content">
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
                <xsl:call-template name="semantics-intro"/>
                <xsl:apply-templates select="semantics"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="semantics-index">
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

    <!-- Outputs the introduction section of the semantics document. -->
    <xsl:template name="semantics-intro">
        <xsl:copy-of select="$sections-toolbar"/>
        <p>
        This document specifies the semantics of the <xsl:value-of
        select="$language-name"/> programming language.
        </p><p>
        A construct's semantics are as they are described below iff the
        construct satisfies all of its validity constraints: a construct's
        semantics are undefined if it does not satisfy all of its validity
        constraints (and so usually executable representations of such
        constructs should not be generated).
        </p><p>
        <strong>Note</strong>: at least as far as construct semantics are
        concerned, a choice construct is treated as though exactly one of
        its subconstructs - that is, the constructs among which it represents
        a choice - is present.
        </p><p>
        In this document the terms 'subconstruct', 'direct subconstruct' and
        'indirect subconstruct' have very specific meanings: let
        SomeConstruct and OtherConstruct be types of constructs in our
        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="'Grammar'"/>
            <xsl:with-param name="text" select="'grammar'"/>
        </xsl:call-template>,
        and let <span class="plack-var">c</span> be an instance of SomeConstruct.
        Then
        <ul>
            <li>an <strong>OtherConstruct subconstruct</strong> of <span
                class="plack-var">c</span> is an OtherConstruct construct that
                is either a direct subconstruct of <span
                class="plack-var">c</span> or an indirect subconstruct of <span
                class="plack-var">c</span></li>
            <li>a <strong>direct OtherConstruct subconstruct</strong> of <span
                class="plack-var">c</span> is an OtherConstruct that appears on
                the right-hand side of the definition of SomeConstruct in the
                grammar</li>
            <li>an <strong>indirect OtherConstruct subconstruct</strong> of <span
                class="plack-var">c</span> is an OtherConstruct subconstruct of
                a direct subconstruct of <span class="plack-var">c</span></li>
        </ul>
        </p><p>
        <strong>Note</strong>: the definition numbers in this document can - and
        often will - change between even minor revisions of this document, and
        so should not be used to identify definitions anywhere outside of this
        document.
        </p><p>
        This document is divided into the following sections:
        <xsl:call-template name="table-of-contents">
            <xsl:with-param name="titles" select="$section-titles"/>
        </xsl:call-template>
        </p>
        <xsl:for-each select="$semantics/general-notes">
        <p>
            <xsl:call-template name="output-notes">
                <xsl:with-param name="title" select="'General Notes'"/>
            </xsl:call-template>
        </p>
        </xsl:for-each>
    </xsl:template>


    <!-- Sections -->

    <!-- Returns a bar-separated (|) list of the titles of all of the
         sections of this document. There will also be a bar after the
         last element in the list. -->
    <xsl:template name="section-titles">
        <xsl:call-template name="append-section-titles">
            <xsl:with-param name="sections" select="$semantics/section"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="semantics">
        <xsl:call-template name="sections-in"/>
    </xsl:template>

    <!-- Outputs the sections directly under the specified node. -->
    <xsl:template name="sections-in">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/section">
            <xsl:variable name="title" select="@title"/>
            <xsl:variable name="url" select="translate($title, ' ', '_')"/>
            <hr />
            <h2><a name="{$url}"><xsl:value-of select="$title"/></a></h2>

            <xsl:apply-templates mode="section-part"/>
            <xsl:copy-of select="$sections-toolbar"/>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="definition" mode="section-part">
        <xsl:variable name="def-number">
            <xsl:call-template name="definition-number">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <p>
        <a name="def.{@name}">
            <span class="plack-semantics-definition-number">
                <xsl:value-of select="concat('[', $def-number, '] ')"/>
            </span>
            <span class="plack-semantics-definition-name">
                <xsl:value-of select="concat(@name, '. ')"/>
            </span>
        <xsl:apply-templates mode="definition-part"/></a>
        </p>
    </xsl:template>

    <xsl:template match="example" mode="definition-part">
        <p>
        <div class="plack-semantics-definition-example">
            <xsl:text>For example, </xsl:text>
            <xsl:apply-templates mode="definition-part"/>
        </div></p>
    </xsl:template>

    <xsl:template match="*" mode="definition-part">
        <xsl:apply-templates select="."/>
    </xsl:template>

    <!-- Returns the number/1-based index of a definition given its name. -->
    <xsl:template name="definition-number">
        <xsl:param name="name"/>

        <xsl:for-each select="$semantics-definitions">
            <xsl:if test="@name = $name">
                <xsl:value-of select="position()"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="construct-semantics" mode="section-part">
        <a name="{@type}">
            <div class="plack-construct-semantics">
                <xsl:call-template
                    name="output-one-construct-semantics-header"/>
                <div class="grammar-def">
                <xsl:call-template name="output-grammar-def">
                    <xsl:with-param name="type" select="@type"/>
                </xsl:call-template>
                </div>
                <div class="definition">
                <xsl:call-template name="output-one-construct-semantics"/>
                </div>
            </div>
        </a>
    </xsl:template>

    <!-- Outputs the header (i.e. top) line for one construct's semantics. -->
    <xsl:template name="output-one-construct-semantics-header">
        <xsl:param name="type" select="@type"/>

        <xsl:variable name="prefix">
            <span class="plack-one-construct-semantics-name"><xsl:value-of
                select="$type"/>Semantics</span>
        </xsl:variable>

        <table border="0" width="100%" class="header">
        <tr>
            <td class="title"><xsl:copy-of select="$prefix"/></td>
            <td class="menu">
                <xsl:call-template name="construct-semantics-menu"/>
            </td>
        </tr>
        </table>
    </xsl:template>

    <!-- Outputs the contents of the menu that is part of a construct's
         semantics information. -->
    <xsl:template name="construct-semantics-menu">
        <xsl:choose>
            <xsl:when test="preceding::construct-semantics">
                <xsl:for-each select="preceding::construct-semantics[1]">
                    <xsl:variable name="ref" select="concat('#', @type)"/>
        <a href="{$ref}">Previous</a>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>Previous</xsl:otherwise>
        </xsl:choose>
        <xsl:text>&nbsp;</xsl:text>
        <xsl:choose>
            <xsl:when test="following::construct-semantics">
                <xsl:for-each select="following::construct-semantics[1]">
                    <xsl:variable name="ref" select="concat('#', @type)"/>
        <a href="{$ref}">Next</a>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>Next</xsl:otherwise>
        </xsl:choose>
        <xsl:text>&nbsp;</xsl:text>
        <a href="{$top-link}"><xsl:value-of select="$top-name"/></a>
    </xsl:template>

    <!-- Outputs the information about the semantics of a single construct
         represented by the current node. -->
    <xsl:template name="output-one-construct-semantics">
        <xsl:choose>
            <xsl:when test="count(let) = 0">
                <xsl:call-template name="simple-semantics"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="full-semantics"/>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:apply-templates select="notes"/>
    </xsl:template>

    <!-- Outputs the information about the single construct's semantics
         represented by the current node.

         Any 'let' children that the node has are ignored. The node must
         have a 'var' attribute. -->
    <xsl:template name="simple-semantics">
        <xsl:text>A given </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@var"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:call-template name="output-construct-semantics-description"/>
    </xsl:template>

    <!-- Outputs the information about the single construct's semantics
         represented by the current node.

         The node is assumed to have at least one 'let' child, as well as a
         'var' attribute. -->
    <xsl:template name="full-semantics">
        <xsl:text>A given </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@var"/>
        </xsl:call-template>
        <xsl:text>, where </xsl:text>
        <xsl:call-template name="output-let-statements">
            <xsl:with-param name="lets" select="let"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:call-template name="output-construct-semantics-description"/>
    </xsl:template>


    <!-- Construct Semantics Parts -->

    <!-- Output the definition - from the grammar - of the specified type of
         construct. -->
    <xsl:template name="output-grammar-def">
        <xsl:param name="type"/>

        <xsl:variable name="construct" select="key('construct', $type)"/>
        <xsl:apply-templates select="$construct" mode="grammar.rule"/>
    </xsl:template>


    <!-- Outputs the specified name of a variable used in semantics. -->
    <xsl:template name="output-variable">
        <xsl:param name="name"/>

        <span class="plack-var"><xsl:value-of select="$name"/></span>
    </xsl:template>

    <xsl:template match="var">
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the specified expression used in semantics. -->
    <xsl:template name="output-expression">
        <xsl:param name="text"/>

        <span class="plack-expr"><xsl:value-of select="$text"/></span>
    </xsl:template>

    <xsl:template match="expr">
        <xsl:call-template name="output-expression">
            <xsl:with-param name="text" select="@text"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="operator-information">
        <xsl:call-template name="operator-info-table"/>
    </xsl:template>


    <!-- Outputs the specified set of 'let' statements that are part of
         a construct's semantics. -->
    <xsl:template name="output-let-statements">
        <xsl:param name="lets"/>

        <ul>
            <xsl:for-each select="$lets">
                <xsl:variable name="suffix">
                    <xsl:choose>
                        <xsl:when test="position() = last() - 1">
                            <xsl:text>, and</xsl:text>
                        </xsl:when>
                        <xsl:otherwise></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:apply-templates select=".">
                    <xsl:with-param name="suffix" select="$suffix"/>
                </xsl:apply-templates>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="let">
        <xsl:param name="suffix" select="''"/>

        <li>
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> is </xsl:text>
        <xsl:apply-templates/>
        <xsl:value-of select="$suffix"/>
        </li>
    </xsl:template>

    <xsl:template match="direct">
        <xsl:variable name="subs">
            <xsl:choose>
                <xsl:when test="@multiple = 'true'">subconstructs</xsl:when>
                <xsl:otherwise>subconstruct</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:text>direct </xsl:text>
        <xsl:value-of select="concat(@subconstruct, ' ', $subs, ' ')"/>
        <xsl:if test="@name">
            <xsl:call-template name="output-variable">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:text>of </xsl:text>
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@of"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="indirect">
        <xsl:variable name="subs">
            <xsl:choose>
                <xsl:when test="@multiple = 'true'">subconstructs</xsl:when>
                <xsl:otherwise>subconstruct</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:text>indirect </xsl:text>
        <xsl:value-of select="concat(@subconstruct, ' ', $subs, ' ')"/>
        <xsl:if test="@name">
            <xsl:call-template name="output-variable">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:text>of </xsl:text>
        <xsl:call-template name="output-variable">
            <xsl:with-param name="name" select="@of"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Outputs the part of the specification of a construct's semantics
         that s contained in the 'description' child element. -->
    <xsl:template name="output-construct-semantics-description">
        <xsl:param name="construct-semantics" select="."/>

        <xsl:for-each select="$construct-semantics/description">
            <xsl:apply-templates/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="def-use">
        <xsl:variable name="def-number">
            <xsl:call-template name="definition-number">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:apply-templates/>
        <xsl:text> </xsl:text>
        <span class="plack-semantics-definition-link">
        <xsl:call-template name="make-def-link">
            <xsl:with-param name="name" select="@name"/>
            <xsl:with-param name="text"
                select="concat('[', $def-number, ']')"/>
        </xsl:call-template>
        </span>
    </xsl:template>

    <!-- Outputs a link to the definition with the specified name. -->
    <xsl:template name="make-def-link">
        <xsl:param name="name"/>
        <xsl:param name="text"/>

        <xsl:call-template name="make-link">
            <xsl:with-param name="id" select="$our-id"/>
            <xsl:with-param name="fragmentId" select="concat('def.', $name)"/>
            <xsl:with-param name="text" select="$text"/>
            <xsl:with-param name="title" select="$name"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Index templates -->

    <xsl:template name="index-links">
        <xsl:call-template name="major-section-links"/>
        <hr />
        <xsl:for-each select="$semantics/section">
            <xsl:variable name="tr-title" select="translate(@title, ' ', '_')"/>
            <xsl:variable name="url"
                select="concat($content-filename, '#', $tr-title)"/>

            <p><a name="{$tr-title}">
            <xsl:call-template name="make-major-index-entry">
                <xsl:with-param name="url" select="$url"/>
                <xsl:with-param name="text"
                    select="translate(@title, ' ', '&nbsp;')"/>
                <xsl:with-param name="suffix-link-href" select="$top-link"/>
                <xsl:with-param name="suffix-link-text" select="'^'"/>
            </xsl:call-template></a></p>

            <xsl:apply-templates mode="item-links"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs links to the major sections of the semantics document. -->
    <xsl:template name="major-section-links">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$content-filename"/>
            <xsl:with-param name="text" select="$top-name"/>
        </xsl:call-template>

        <xsl:for-each select="$semantics/section">
            <xsl:variable name="section-url"
                select="concat('#', translate(@title, ' ', '_'))"/>
            <xsl:variable name="section-title"
                select="translate(@title, ' ', '&nbsp;')"/>

            <div class="plack-minor-index-entry"><a
                href="{$section-url}"><xsl:value-of
                select="$section-title"/></a></div>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="construct-semantics" mode="item-links">
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#', @type)"/>
            <xsl:with-param name="text">
                <xsl:value-of select="@type"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- Ignore everything else. -->
    <xsl:template match="*" mode="item-links"/>
</xsl:transform>
