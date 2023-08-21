<?xml version="1.0"?>
<!--
    Contains the variables and templates related to tokens and
    that are independent of any target language (e.g. Java).

    Copyright (C) 2002-2005 by James MacKay.

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

    <xsl:import href="language-common.xsl"/>


    <!-- ##################### -->
    <!-- # Global variables. * -->
    <!-- ##################### -->

    <xsl:variable name="token-flags"
        select="$top/token-flags/section/flag-definition"/>
    <xsl:variable name="number-of-token-flags"
        select="count($token-flags)"/>
    <xsl:variable name="token-flag-sets"
        select="$top/token-flag-sets/section/flag-set-definition"/>

    <xsl:variable name="reserved-word-tokens"
        select="$top/tokens/section/reserved-word"/>
    <xsl:variable name="reserved-word-operator-tokens"
        select="$top/tokens/section/reserved-word-operator"/>
    <xsl:variable name="operator-tokens"
        select="$top/tokens/section/operator"/>
    <xsl:variable name="simple-tokens"
        select="$top/tokens/section/simple-token"/>
    <xsl:variable name="custom-tokens"
        select="$top/tokens/section/custom-token"/>

    <xsl:variable name="all-reserved-word-tokens"
        select="$reserved-word-tokens | $reserved-word-operator-tokens"/>
    <xsl:variable name="number-of-reserved-words"
        select="count($all-reserved-word-tokens)"/>

    <xsl:variable name="all-operator-tokens"
        select="$operator-tokens | $reserved-word-operator-tokens"/>
    <xsl:variable name="number-of-operators"
        select="count($all-operator-tokens)"/>
    <xsl:variable name="number-of-operators-with-methods"
        select="count($all-operator-tokens[@method])"/>

    <xsl:variable name="tokens"
        select="$reserved-word-tokens | $reserved-word-operator-tokens |
                $operator-tokens | $simple-tokens | $custom-tokens"/>
        <!-- Note: the reserved word tokens are required to be first -->
    <xsl:variable name="number-of-tokens" select="count($tokens)"/>


    <!-- ###################### -->
    <!-- # Utility templates. * -->
    <!-- ###################### -->

</xsl:transform>
