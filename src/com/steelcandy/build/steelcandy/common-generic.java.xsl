<?xml version="1.0"?>
<!--
    $Id: common-generic.java.xsl,v 1.4 2004/05/12 19:27:34 jgm Exp $

    Provides common functionality for stylesheets that generate the
    source code for generic classes with one generic 'parameter'.
    It is intended to be xsl:imported into another stylesheet.

        - top: a variable whose value is the element that describes
               the generic class. The value of $root should be used in
               setting this variable's value

    Author: James MacKay
    Last Updated: $Date: 2004/05/12 19:27:34 $

    Copyright (C) 2002-2004 by James MacKay.

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
    <xsl:param name="item-class-name"/>

    <xsl:output method="text"/>

    <!-- Configuration -->


    <!-- Global variables -->

    <!-- The (possibly relative) pathname of the file being transformed. -->
    <xsl:param name="input-pathname"/>

    <!-- The (possibly relative) pathname of the XSL file doing the
         transformation. -->
    <xsl:param name="transform-pathname"/>

    <!-- The (possibly relative) pathname of the file resulting from
         the transformation. -->
    <xsl:param name="output-pathname"/>


    <xsl:variable name="root" select="//generic-classes"/>

    <xsl:variable name="item-name" select="$top/@item-name"/>
    <xsl:variable name="module-name" select="$top/@module-name"/>
    <xsl:variable name="class-name" select="$top/@class-name"/>
    <xsl:variable name="item-module-name">
        <!-- Unless it is explicitly specified the item's module name is
             assumed to be the same as the predicate's module name. -->
        <xsl:choose>
            <xsl:when test="$top/@item-module-name">
                <xsl:value-of select="$top/@item-module-name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$module-name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <!-- We only need to import the item class if it is in a different
         module than the predicate, and isn't in java.lang. -->
    <xsl:variable name="item-import">
        <xsl:choose>
            <xsl:when test="$item-module-name!=$module-name and
                            $item-module-name!='java.lang'">
                <xsl:text>import </xsl:text>
                <xsl:value-of
                    select="concat($item-module-name, '.', $item-name)"/>
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <!-- Common templates -->

    <!-- Outputs the common part of a generated class' class comment.

         Note: the comment does NOT include a @version tag since using
         $Revision: 1.4 $ for its value expands to this template's revision,
         causing all generated files to appear to have been changed when
         they haven't otherwise changed. -->
    <xsl:template name="common-class-comment-part">
        <xsl:text>    &lt;p&gt;
    &lt;strong&gt;Note&lt;/strong&gt;: this file was automatically generated, and so
    should not be edited directly.
    &lt;ul&gt;
        &lt;li&gt;Input: </xsl:text>
        <xsl:value-of select="$input-pathname"/>
        <xsl:text>
        &lt;li&gt;Transform: </xsl:text>
        <xsl:value-of select="$transform-pathname"/>
        <xsl:text>
        &lt;li&gt;Output: </xsl:text>
        <xsl:value-of select="$output-pathname"/>
        <xsl:text>
    &lt;/ul&gt;

    @author James MacKay</xsl:text>
    </xsl:template>
</xsl:transform>
