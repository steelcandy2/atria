<?xml version="1.0"?>
<!--
    Provides common functionality for all stylesheets. It is intended to
    be xsl:imported into another stylesheet.

    Copyright (C) 2002-2012 by James MacKay.

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

    <!-- Global parameters -->

    <!-- The (possibly relative) pathname of the file being transformed. -->
    <xsl:param name="input-pathname"/>

    <!-- The (possibly relative) pathname of the XSL file doing the
         transformation. -->
    <xsl:param name="transform-pathname"/>

    <!-- The (possibly relative) pathname of the file resulting from
         the transformation. -->
    <xsl:param name="output-pathname"/>

    <!-- The date/time at which this transformation is taking place. -->
    <xsl:param name="generation-date-time"/>


    <!-- Global variables -->

    <!-- The default list separator used in lists created by make-list
         and assumed by the various list-related templates defined in this
         stylesheet. -->
    <xsl:variable name="default-list-separator" select="'|'"/>

    <!-- The last year to appear in copyright notices. -->
    <xsl:variable name="last-copyright-year" select="'2012'"/>


    <!-- Utility templates -->

    <!-- Outputs the 'pathname' of the specified node, from the root
         element down. Each component of the pathname will be the local
         name of an ancestor element. -->
    <xsl:template name="local-node-pathname">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:if test="count(. | /) != 1">
                <!-- 'node' isn't the document root '/'. -->
                <xsl:call-template name="local-node-pathname">
                    <xsl:with-param name="node" select=".."/>
                </xsl:call-template>
                <xsl:value-of select="concat('/', name())"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the 'pathname' of the specified node, from the root
         element down. Each component of the pathname will be the full
         name (that is, including any namespace prefix) of an ancestor
         element. -->
    <xsl:template name="node-pathname">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:if test="count(. | /) != 1">
                <!-- 'node' isn't the document root '/'. -->
                <xsl:call-template name="node-pathname">
                    <xsl:with-param name="node" select=".."/>
                </xsl:call-template>
                <xsl:value-of select="concat('/', name())"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


    <!-- Outputs the specified string with its first letter capitalized. -->
    <xsl:template name="capitalize">
        <xsl:param name="str"/>

        <xsl:variable name="first" select="substring($str, 1, 1)"/>
        <xsl:variable name="rest" select="substring($str, 2)"/>
        <xsl:variable name="upper-first">
            <xsl:call-template name="to-uppercase">
                <xsl:with-param name="str" select="$first"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat($upper-first, $rest)"/>
    </xsl:template>

    <!-- Outputs the specified string converted to all lowercase. -->
    <xsl:template name="to-lowercase">
        <xsl:param name="str"/>

        <xsl:value-of select="translate($str, '&uc;', '&lc;')"/>
    </xsl:template>

    <!-- Outputs the specified string converted to all uppercase. -->
    <xsl:template name="to-uppercase">
        <xsl:param name="str"/>

        <xsl:value-of select="translate($str, '&lc;', '&uc;')"/>
    </xsl:template>


    <!-- Outputs a list of the specified elements separated by the specified
         separator (which by default is $default-list-separator). -->
    <xsl:template name="make-list">
        <xsl:param name="elements"/>
        <xsl:param name="separator" select="$default-list-separator"/>

        <xsl:if test="$elements">
            <xsl:variable name="first" select="$elements[1]"/>
            <xsl:variable name="rest" select="$elements[position()!=1]"/>

            <xsl:value-of select="$first"/>
            <xsl:if test="$rest">
                <xsl:value-of select="$separator"/>
                <xsl:call-template name="make-list">
                    <xsl:with-param name="elements" select="$rest"/>
                    <xsl:with-param name="separator" select="$separator"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Outputs the number of elements in the specified list, where
         the list elements are separated with the specified separator. -->
    <xsl:template name="list-size">
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>
        <xsl:param name="previous-count" select="0"/>

        <xsl:choose>
            <xsl:when test="contains($list, $separator)">
                <xsl:call-template name="list-size">
                    <xsl:with-param name="list"
                        select="substring-after($list, $separator)"/>
                    <xsl:with-param name="separator" select="$separator"/>
                    <xsl:with-param name="previous-count"
                        select="$previous-count + 1"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="string-length($list) &gt; 0">
                <!-- There's just one element left in the list. -->
                <xsl:value-of select="$previous-count + 1"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$previous-count"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the first item in the specified list, or the empty
         string if the list is empty. -->
    <xsl:template name="list-head">
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>

        <xsl:choose>
            <xsl:when test="contains($list, $separator)">
                <!-- The list contains 2+ items: return the first -->
                <xsl:value-of select="substring-before($list, $separator)"/>
            </xsl:when>
            <xsl:when test="string-length($list) &gt; 0">
                <!-- The list contains one item: return it -->
                <xsl:value-of select="$list"/>
            </xsl:when>
            <xsl:otherwise></xsl:otherwise> <!-- The list is empty -->
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the specified list with its first item removed, or the
         empty string if the list is empty. (Thus the empty string is
         output if the list has zero or one items in it.) -->
    <xsl:template name="list-tail">
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>

        <xsl:if test="contains($list, $separator)">
            <!-- The list contains 2+ items: return everything after
                 the first $separator. -->
                <xsl:value-of select="substring-after($list, $separator)"/>
        </xsl:if>
        <!-- Otherwise the list contains zero or one item, so output
             nothing. -->
    </xsl:template>

    <!-- Returns true if the specified list contains the specified
         element, and false if it doesn't. -->
    <xsl:template name="list-contains">
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>
        <xsl:param name="element"/>

        <xsl:choose>
            <xsl:when test="$list = $element">
                <!-- 'list''s sole element is 'element' -->
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:when test="starts-with($list, concat($element, $separator))">
                <!-- 'list' is a 2+ element list whose first element is
                     'element' -->
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:when
                test="contains($list, concat($separator, $element, $separator))">
                <!-- 'list' is a 3+ element list that contains 'element',
                     but not as its first or last element -->
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- Check whether 'list' is a 2+ element list whose last
                     element is 'element' -->

                <!-- Same as ends-with($list, concat($separator, $element)) -->
                <xsl:variable name="str" select="concat($separator, $element)"/>
                <xsl:value-of select="substring($list,
                    string-length($list) - string-length($str) + 1) = $str"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Prepends the specified element to the start of the specified list. -->
    <xsl:template name="prepend-to-list">
        <xsl:param name="element"/>
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>

        <!-- Warn if $element contains $separator. -->
        <xsl:if test="contains($element, $separator)">
            <xsl:message terminate="no">
                <xsl:text>
WARNING: prepending to a list an element that contains the list separator:
    element: </xsl:text>
                <xsl:value-of select="$element"/>
                <xsl:text>
    separator: </xsl:text>
                <xsl:value-of select="$separator"/>
                <xsl:text>
    list: </xsl:text>
                <xsl:value-of select="$list"/>
                <xsl:text>
</xsl:text>
            </xsl:message>
        </xsl:if>

        <!-- Follow $element with $separator iff $list isn't empty -->
        <xsl:value-of select="$element"/>
        <xsl:if test="string-length($list) &gt; 0">
            <xsl:value-of select="$separator"/>
        </xsl:if>
        <xsl:value-of select="$list"/>
    </xsl:template>

    <!-- Appends the specified element to the end of the specified list. -->
    <xsl:template name="append-to-list">
        <xsl:param name="list"/>
        <xsl:param name="separator" select="$default-list-separator"/>
        <xsl:param name="element"/>

        <!-- Warn if $element contains $separator. -->
        <xsl:if test="contains($element, $separator)">
            <xsl:message terminate="no">
                <xsl:text>
WARNING: appending to a list an element that contains the list separator:
    element: </xsl:text>
                <xsl:value-of select="$element"/>
                <xsl:text>
    separator: </xsl:text>
                <xsl:value-of select="$separator"/>
                <xsl:text>
    list: </xsl:text>
                <xsl:value-of select="$list"/>
                <xsl:text>
</xsl:text>
            </xsl:message>
        </xsl:if>

        <!-- Precede $element with $separator iff $list isn't empty -->
        <xsl:value-of select="$list"/>
        <xsl:if test="string-length($list) &gt; 0">
            <xsl:value-of select="$separator"/>
        </xsl:if>
        <xsl:value-of select="$element"/>
    </xsl:template>

    <!-- Outputs the specified number of copies of the specified string. -->
    <xsl:template name="copies">
        <xsl:param name="str"/>
        <xsl:param name="number"/>

        <xsl:if test="$number &gt; 0">
            <xsl:value-of select="$str"/>
            <xsl:call-template name="copies">
                <xsl:with-param name="str" select="$str"/>
                <xsl:with-param name="number" select="$number - 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Given a 1-based numerical position, outputs the name for that
         position: e.g. "first" for 1, "second" for 2, etc. -->
    <xsl:template name="position-name">
        <xsl:param name="pos"/>

        <xsl:variable name="posMod10" select="$pos mod 10"/>
        <xsl:variable name="posMod100" select="$pos mod 100"/>

        <xsl:choose>
            <!-- Output words for the first few indices. -->
            <xsl:when test="$pos = 1">
                <xsl:text>first</xsl:text>
            </xsl:when>
            <xsl:when test="$pos = 2">
                <xsl:text>second</xsl:text>
            </xsl:when>
            <xsl:when test="$pos = 3">
                <xsl:text>third</xsl:text>
            </xsl:when>
            <xsl:when test="$pos = 4">
                <xsl:text>fourth</xsl:text>
            </xsl:when>
            <xsl:when test="$pos = 5">
                <xsl:text>fifth</xsl:text>
            </xsl:when>

            <xsl:when test="$posMod100 &gt; 10 and $posMod100 &lt; 20">
                <!-- '11th, 12th, 213th' not '11st, 12nd, 213rd' -->
                <xsl:value-of select="concat($pos, 'th')"/>
            </xsl:when>

            <xsl:when test="$posMod10 = 1">
                <xsl:value-of select="concat($pos, 'st')"/>
            </xsl:when>
            <xsl:when test="$posMod10 = 2">
                <xsl:value-of select="concat($pos, 'nd')"/>
            </xsl:when>
            <xsl:when test="$posMod10 = 3">
                <xsl:value-of select="concat($pos, 'rd')"/>
            </xsl:when>

            <xsl:otherwise>
                <xsl:value-of select="concat($pos, 'th')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:transform>
