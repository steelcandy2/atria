<?xml version="1.0"?>
<!--
    $Id: map-string.xsl,v 1.6 2003/02/14 10:48:05 jgm Exp $

    Contains the variables and templates related to information about a
    language's operators.

    Usually only the operator-info-table template is the only one in this
    file that is called directly from outside of this file. It outputs a
    table giving information about all of the language's operators.

    Author: James MacKay
    Last Updated: $Date: 2003/02/14 10:48:05 $

    Copyright (C) 2004 by James MacKay.

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


    <!-- Operator information templates -->

    <!-- Outputs a table describing all of the operators. -->
    <xsl:template name="operator-info-table">
        <p>
        <table border="1" width="90%">
            <tr>
                <th>Operators</th>
                <th>Arity</th>
                <th>Associativity</th>
                <th>Precedence<sup>*</sup></th>
                <th>Description</th>
            </tr>
            <xsl:for-each select="$operator-precedence-levels">
                <xsl:sort select="@value" order="descending" data-type="number"/>
                <xsl:call-template name="one-precedence-level-operator-info"/>
            </xsl:for-each>
            <tr>
                <td colspan="5"><sup>*</sup> a larger precedence value indicates
                    a higher precedence</td>
            </tr>
        </table>
        </p>
    </xsl:template>

    <!-- Outputs the table rows for all of the operators whose precedence
         level is given by the specified precedence-level element.

         Note: we output one row for each arity-associativity combination
         that is possessed by at least one operator at this precedence
         level. -->
    <xsl:template name="one-precedence-level-operator-info">
        <xsl:param name="level" select="."/>

        <xsl:variable name="level-ops"
            select="$operator-tokens[@precedence = $level/@name]"/>
        <xsl:variable name="op-keys-list">
            <xsl:call-template name="build-operator-keys-list">
                <xsl:with-param name="operators" select="$level-ops"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="operator-info-rows">
            <xsl:with-param name="level" select="$level"/>
            <xsl:with-param name="op-keys-list" select="$op-keys-list"/>
            <xsl:with-param name="level-ops" select="$level-ops"/>
            <xsl:with-param name="is-first" select="'true'"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs the operator information table rows for each of the
         operators in 'level-ops'. -->
    <xsl:template name="operator-info-rows">
        <xsl:param name="level"/>
        <xsl:param name="op-keys-list"/>
        <xsl:param name="level-ops"/>
        <xsl:param name="is-first" select="'false'"/>

        <xsl:if test="string-length($op-keys-list) &gt; 0">
            <xsl:variable name="keys">
                <xsl:choose>
                    <xsl:when test="contains($op-keys-list, '|')">
                        <xsl:value-of
                            select="substring-before($op-keys-list, '|')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$op-keys-list"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="arity"
                select="substring-before($keys, ':')"/>
            <xsl:variable name="associativity"
                select="substring-after($keys, ':')"/>
            <xsl:variable name="rest-op-keys-list">
                <xsl:choose>
                    <xsl:when test="contains($op-keys-list, '|')">
                        <xsl:value-of
                            select="substring-after($op-keys-list, '|')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text></xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="ops">
                <xsl:call-template name="ops-with-keys">
                    <xsl:with-param name="ops" select="$level-ops"/>
                    <xsl:with-param name="arity" select="$arity"/>
                    <xsl:with-param name="associativity" select="$associativity"/>
                </xsl:call-template>
            </xsl:variable>

            <tr>
                <td><xsl:value-of select="$ops"/></td>
                <td align="center"><xsl:value-of select="$arity"/></td>
                <td align="center"><xsl:value-of select="$associativity"/></td>
                <td align="center"><xsl:value-of select="$level/@value"/></td>
                <xsl:if test="$is-first = 'true'">
                    <xsl:variable name="num-rows">
                        <xsl:call-template name="list-size">
                            <xsl:with-param name="list"
                                select="$op-keys-list"/>
                            <xsl:with-param name="separator" select="'|'"/>
                        </xsl:call-template>
                    </xsl:variable>

                    <td valign="center" rowspan="{$num-rows}">
                        <xsl:value-of select="$level/@description"/>
                    </td>
                </xsl:if>
            </tr>
            <xsl:call-template name="operator-info-rows">
                <xsl:with-param name="level" select="$level"/>
                <xsl:with-param name="op-keys-list" select="$rest-op-keys-list"/>
                <xsl:with-param name="level-ops" select="$level-ops"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Builds a '|'-separated list of the distinct keys among all of the
         specified operators. A key is of the form 'arity:associativity'
         (without the quotation marks). -->
    <xsl:template name="build-operator-keys-list">
        <xsl:param name="operators"/>

        <xsl:if test="$operators">
            <xsl:variable name="first" select="$operators[1]"/>
            <xsl:variable name="rest" select="$operators[position()!=1]"/>
            <xsl:variable name="first-key">
                <xsl:value-of
                    select="concat($first/@arity, ':', $first/@associativity)"/>
            </xsl:variable>

            <xsl:choose>
                <xsl:when test="$rest">
                    <xsl:variable name="rest-keys">
                        <xsl:call-template name="build-operator-keys-list">
                            <xsl:with-param name="operators" select="$rest"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="contains-key">
                        <xsl:call-template name="list-contains">
                            <xsl:with-param name="list" select="$rest-keys"/>
                            <xsl:with-param name="separator" select="'|'"/>
                            <xsl:with-param name="element" select="$first-key"/>
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:choose>
                        <xsl:when test="$contains-key = 'true'">
                            <!-- rest-keys already contains first-key -->
                            <xsl:value-of select="$rest-keys"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- rest-keys does not contain first-key -->
                            <xsl:value-of
                                select="concat($first-key, '|', $rest-keys)"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$first-key"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>

    <!-- Outputs a displayable comma-separated list of the textual
         representations of those operators in the set of operators 'ops'
         whose arity and associativity match the specified ones. -->
    <xsl:template name="ops-with-keys">
        <xsl:param name="ops"/>
        <xsl:param name="arity"/>
        <xsl:param name="associativity"/>

        <xsl:variable name="matching-ops"
            select="$ops[@arity=$arity and @associativity=$associativity]"/>
        <xsl:for-each select="$matching-ops">
            <xsl:if test="position() != 1">
                <xsl:text>, </xsl:text>
            </xsl:if>
            <xsl:value-of select="@text"/>
        </xsl:for-each>
    </xsl:template>
</xsl:transform>
