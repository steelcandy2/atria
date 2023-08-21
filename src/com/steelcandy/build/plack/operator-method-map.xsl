<?xml version="1.0"?>
<!--
    $Id: operator-method-map.xsl,v 1.13 2005/03/30 16:45:19 jgm Exp $

    Transforms a language description document into an HTML document
    that describes the language's operator to method map.

    Author: James MacKay
    Last Updated: $Date: 2005/03/30 16:45:19 $

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

    <xsl:variable name="our-id" select="'OperatorMethodMap'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Operator Method Map')"/>

    <!-- Section titles -->
    <xsl:variable name="intro-section-title"
        select="'Introduction'"/>
    <xsl:variable name="toc-section-title"
        select="'Table of Contents'"/>
    <xsl:variable name="unary-operators-section-title"
        select="'Unary Operators'"/>
    <xsl:variable name="binary-operators-section-title"
        select="'Binary Operators'"/>
    <xsl:variable name="unary-operators-section-id"
        select="'UnaryOperators'"/>
    <xsl:variable name="binary-operators-section-id"
        select="'BinaryOperators'"/>


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

    <!-- Variables used in examples. -->
    <xsl:variable name="type1">
        <xsl:call-template name="format-type">
            <xsl:with-param name="type" select="'S'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="type2">
        <xsl:call-template name="format-type">
            <xsl:with-param name="type" select="'T'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="expr1">
        <xsl:call-template name="format-expression">
            <xsl:with-param name="expression" select="'expr1'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="expr2">
        <xsl:call-template name="format-expression">
            <xsl:with-param name="expression" select="'expr2'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="method1">
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="'m1'"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="method2">
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="'m2'"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="op-method-map-content"/>
        <xsl:call-template name="op-method-map-index"/>
    </xsl:template>

    <xsl:template name="op-method-map-content">
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
                <xsl:call-template name="op-method-map-intro"/>
                <xsl:call-template name="unary-operators-section"/>
                <xsl:call-template name="binary-operators-section"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="op-method-map-index">
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

    <!-- Outputs the introduction section of the operator method map
         document. -->
    <xsl:template name="op-method-map-intro">
        <h2><xsl:value-of select="$intro-section-title"/></h2>
        <p>
        This document lists the various <xsl:value-of select="$language-name"/>
        operators that can be defined to operate on user-defined types and the
        method to define on a type in order to be able to use an operator on
        an instance of that type. The
        <xsl:call-template name="make-link">
            <xsl:with-param name="id">LanguageDefinition</xsl:with-param>
            <xsl:with-param name="text">language definition</xsl:with-param>
        </xsl:call-template> describes the algorithm used to determine the
        method call to which a given use of an operator will be mapped.
        </p><p>
        In the following the expressions
        <xsl:value-of select="concat($expr1, ' and ', $expr2)"/> will be used.
        <xsl:value-of select="$expr1"/> is of type
        <xsl:value-of select="$type1"/> and <xsl:value-of select="$expr2"/> is
        of type <xsl:value-of select="$type2"/>.
        </p>
    </xsl:template>

    <!-- Output the section describing unary operators. -->
    <xsl:template name="unary-operators-section">
        <hr />
        <h2><a name="{$unary-operators-section-id}">
            <xsl:value-of select="$unary-operators-section-title"/>
        </a></h2>
        <xsl:apply-templates select="$tokens" mode="unary-maps"/>
    </xsl:template>

    <!-- Output the section describing binary operators. -->
    <xsl:template name="binary-operators-section">
        <hr />
        <h2><a name="{$binary-operators-section-id}">
            <xsl:value-of select="$binary-operators-section-title"/>
        </a></h2>
        <xsl:apply-templates select="$tokens" mode="binary-maps"/>
    </xsl:template>


    <!-- Output a map for each token that represents a unary operator, and that
         specifies the name of a method that it maps to (by having a 'method'
         attribute). -->
    <xsl:template match="operator | reserved-word-operator" mode="unary-maps">
        <xsl:if test="@method and @arity = 'unary'">
            <xsl:call-template name="unary-map">
                <xsl:with-param name="operator" select="@text"/>
                <xsl:with-param name="method-name" select="@method"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Ignore non-operator tokens. -->
    <xsl:template match="*" mode="unary-maps"/>


    <!-- Output a map for each token that represents a binary operator, and that
         specifies the name of a method that it maps to (by having a 'method'
         attribute). -->
    <xsl:template match="operator | reserved-word-operator" mode="binary-maps">
        <xsl:if test="@method and @arity = 'binary'">
            <xsl:call-template name="binary-map">
                <xsl:with-param name="operator" select="@text"/>
                <xsl:with-param name="method-name" select="@method"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Ignore non-operator tokens. -->
    <xsl:template match="*" mode="binary-maps"/>


    <!-- Operator map templates -->

    <!-- Given a unary operator and its corresponding method name, outputs the
         information about the mapping from that operator to its method. -->
    <xsl:template name="unary-map">
        <xsl:param name="operator"/>
        <xsl:param name="method-name"/>

        <xsl:variable name="link-label">
            <xsl:call-template name="unary-operator-label">
                <xsl:with-param name="method-name" select="$method-name"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="operator-method-information">
            <table border="0">
                <tr class="operator">
                    <td class="title"><a name="{$link-label}">Operator</a></td>
                    <td class="value">
        <xsl:call-template name="format-operator">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="example">
                    <td class="title">Example</td>
                    <td class="value">
        <xsl:call-template name="unary-operator-example">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-name">
                    <td class="title">Method Name</td>
                    <td class="value">
        <xsl:call-template name="unary-operator-method">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-signature">
                    <td class="title">Method Signature</td>
                    <td class="value">
        <xsl:call-template name="unary-operator-method-signature">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-call">
                    <td class="title">Method Call</td>
                    <td class="value">
        <xsl:call-template name="unary-operator-method-call">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>

    <!-- Given a binary operator and its corresponding method name, outputs the
         information about the mapping from that operator to its methods. -->
    <xsl:template name="binary-map">
        <xsl:param name="operator"/>
        <xsl:param name="method-name"/>

        <xsl:variable name="link-label">
            <xsl:call-template name="binary-operator-label">
                <xsl:with-param name="method-name" select="$method-name"/>
            </xsl:call-template>
        </xsl:variable>

        <div class="operator-method-information">
            <table border="0">
                <tr class="operator">
                    <td class="title"><a name="{$link-label}">Operator</a></td>
                    <td class="value">
        <xsl:call-template name="format-operator">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="example">
                    <td class="title">Example</td>
                    <td class="value">
        <xsl:call-template name="binary-operator-example">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-name">
                    <td class="title">Method Name</td>
                    <td class="value">
        <xsl:call-template name="binary-operator-method">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-signature">
                    <td class="title">Method Signature</td>
                    <td class="value">
        <xsl:call-template name="binary-operator-method-signature">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
                <tr class="method-call">
                    <td class="title">Method Call</td>
                    <td class="value">
        <xsl:call-template name="binary-operator-method-call">
            <xsl:with-param name="method-name" select="$method-name"/>
        </xsl:call-template>
                    </td>
                </tr>
            </table>
        </div>
    </xsl:template>


    <!-- Templates that output pieces of operator information -->

    <!-- Given a unary operator (e.g. '-'), outputs a formatted example of
         its use. -->
    <xsl:template name="unary-operator-example">
        <xsl:param name="operator"/>

        <xsl:call-template name="format-operator">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:copy-of select="$expr1"/>
    </xsl:template>

    <!-- Outputs a formatted version of a unary operator's method name. -->
    <xsl:template name="unary-operator-method">
        <xsl:param name="method-name"/>

        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Given a unary operator's method name, outputs a formatted
         version of its signature. -->
    <xsl:template name="unary-operator-method-signature">
        <xsl:param name="method-name"/>

        <xsl:copy-of select="$type1"/>
        <xsl:text>.</xsl:text>
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Given a unary operator's method name, outputs an example of the
         method call that a use of the operator translates to. -->
    <xsl:template name="unary-operator-method-call">
        <xsl:param name="method-name"/>

        <xsl:text>(</xsl:text>
        <xsl:copy-of select="$expr1"/>
        <xsl:text>).</xsl:text>
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
    </xsl:template>


    <!-- Given a binary operator (e.g. '*'), outputs a formatted example of
         its use. -->
    <xsl:template name="binary-operator-example">
        <xsl:param name="operator"/>

        <xsl:copy-of select="$expr1"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="format-operator">
            <xsl:with-param name="operator" select="$operator"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:copy-of select="$expr2"/>
    </xsl:template>

    <!-- Outputs a formatted version of a binary operator's method name. -->
    <xsl:template name="binary-operator-method">
        <xsl:param name="method-name"/>

        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Given a binary operator's method name, outputs a formatted
         version of its signature. -->
    <xsl:template name="binary-operator-method-signature">
        <xsl:param name="method-name"/>

        <xsl:copy-of select="$type1"/>
        <xsl:text>.</xsl:text>
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
        <xsl:text>(</xsl:text>
        <xsl:copy-of select="$type2"/>
        <xsl:text>)</xsl:text>
    </xsl:template>

    <!-- Given a binary operator's method name, outputs an example of the
         method call that a use of the operator translates to. -->
    <xsl:template name="binary-operator-method-call">
        <xsl:param name="method-name"/>

        <xsl:text>(</xsl:text>
        <xsl:copy-of select="$expr1"/>
        <xsl:text>).</xsl:text>
        <xsl:call-template name="format-method">
            <xsl:with-param name="method" select="$method-name"/>
        </xsl:call-template>
        <xsl:text>(</xsl:text>
        <xsl:copy-of select="$expr2"/>
        <xsl:text>)</xsl:text>
    </xsl:template>


    <!-- Formatting templates -->

    <!-- Formats an operator (e.g. '+'). -->
    <xsl:template name="format-operator">
        <xsl:param name="operator"/>

        <span class="op"><xsl:value-of select="$operator"/></span>
    </xsl:template>

    <!-- Formats a method name. -->
    <xsl:template name="format-method">
        <xsl:param name="method"/>

        <span class="method"><xsl:value-of select="$method"/></span>
    </xsl:template>

    <!-- Formats an expression. -->
    <xsl:template name="format-expression">
        <xsl:param name="expression"/>

        <span class="expr"><xsl:value-of select="$expression"/></span>
    </xsl:template>

    <!-- Formats a type. -->
    <xsl:template name="format-type">
        <xsl:param name="type"/>

        <span class="type"><xsl:value-of select="$type"/></span>
    </xsl:template>


    <!-- Miscellaneous templates -->

    <!-- Returns the link label for the unary operator corresponding to
         the specified operator method name. -->
    <xsl:template name="unary-operator-label">
        <xsl:param name="method-name"/>

        <xsl:value-of select="concat($method-name, 'UnaryOp')"/>
    </xsl:template>

    <!-- Returns the link label for the binary operator corresponding to
         the specified operator method name. -->
    <xsl:template name="binary-operator-label">
        <xsl:param name="method-name"/>

        <xsl:value-of select="concat($method-name, 'BinaryOp')"/>
    </xsl:template>


    <!-- Index templates -->

    <xsl:template name="index-links">
        <xsl:variable name="unary-url"
            select="concat('#', $unary-operators-section-id)"/>
        <xsl:variable name="binary-url"
            select="concat('#', $binary-operators-section-id)"/>

        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$content-filename"/>
            <xsl:with-param name="text" select="$top-name"/>
        </xsl:call-template>
        <div class="plack-minor-index-entry"><a href="{$unary-url}"><xsl:value-of
            select="$unary-operators-section-title"/></a></div>
        <div class="plack-minor-index-entry"><a href="{$binary-url}"><xsl:value-of
            select="$binary-operators-section-title"/></a></div>

        <hr />

        <p><a name="{$unary-operators-section-id}">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#',
                               $unary-operators-section-id)"/>
            <xsl:with-param name="text"
                select="$unary-operators-section-title"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
        <xsl:apply-templates select="$tokens" mode="unary-links"/>

        <p><a name="{$binary-operators-section-id}">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#',
                               $binary-operators-section-id)"/>
            <xsl:with-param name="text"
                select="$binary-operators-section-title"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
        <xsl:apply-templates select="$tokens" mode="binary-links"/>
    </xsl:template>

    <!-- Output a link for each token that represents a unary operator, and that
         specifies the name of a method that it maps to (by having a 'method'
         attribute). -->
    <xsl:template match="operator | reserved-word-operator" mode="unary-links">
        <xsl:if test="@method and @arity = 'unary'">
            <xsl:variable name="fragment-id">
                <xsl:call-template name="unary-operator-label">
                    <xsl:with-param name="method-name" select="@method"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:call-template name="make-minor-index-entry">
                <xsl:with-param name="url"
                    select="concat($content-filename, '#', $fragment-id)"/>
                <xsl:with-param name="text">
                    <xsl:value-of select="concat(@text, ' [', @method, ']')"/>
<!--
                    <xsl:value-of select="concat(@method, ' (', @text, ')')"/>
                    <xsl:value-of select="concat(@text, ' (', @description, ')')"/>
                    <xsl:value-of select="@text"/>
-->
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Ignore non-operator tokens. -->
    <xsl:template match="*" mode="unary-links"/>


    <!-- Output a link for each token that represents a binary operator, and that
         specifies the name of a method that it maps to (by having a 'method'
         attribute). -->
    <xsl:template match="operator | reserved-word-operator" mode="binary-links">
        <xsl:if test="@method and @arity = 'binary'">
            <xsl:variable name="fragment-id">
                <xsl:call-template name="binary-operator-label">
                    <xsl:with-param name="method-name" select="@method"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:call-template name="make-minor-index-entry">
                <xsl:with-param name="url"
                    select="concat($content-filename, '#', $fragment-id)"/>
                <xsl:with-param name="text">
                    <xsl:value-of select="concat(@text, ' [', @method, ']')"/>
<!--
                    <xsl:value-of select="concat(@text, ' (', @description, ')')"/>
                    <xsl:value-of select="@text"/>
-->
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Ignore non-operator tokens. -->
    <xsl:template match="*" mode="binary-links"/>
</xsl:transform>
