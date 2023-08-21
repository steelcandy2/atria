<?xml version="1.0"?>
<!--
    $Id: unary-predicate.java.xsl,v 1.4 2003/09/07 19:51:12 jgm Exp $

    Transforms the information about a typed unary predicate into the
    source code for the Java interface representing the predicate.

    Author: James MacKay
    Last Updated: $Date: 2003/09/07 19:51:12 $

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

    <xsl:import href="common-generic.java.xsl"/>

    <!-- Configuration -->


    <!-- Global variables -->

    <xsl:variable name="top"
        select="$root/unary-predicates/unary-predicate[@item-name=$item-class-name]"/>


    <!-- Main templates -->

    <xsl:template match="generic-classes">
        <xsl:text>// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$module-name"/>
        <xsl:text>;
</xsl:text>
        <xsl:value-of select="$item-import"/>
        <xsl:text>
/**
    The interface implemented by all unary predicates on
    </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
{
    /**
        Indicates whether the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> satisfies this
        predicate.

        @param item the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to test
        @return true iff the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> satisfies this predicate
    */
    public boolean isSatisfied(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item);
}
</xsl:text>
    </xsl:template>
</xsl:transform>
