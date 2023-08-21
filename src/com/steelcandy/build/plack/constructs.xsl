<?xml version="1.0"?>
<!--
    Contains the variables and templates related to constructs and
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
    <xsl:import href="map-string.xsl"/>


    <!-- ##################### -->
    <!-- # Global variables. # -->
    <!-- ##################### -->

    <!-- A string of the form

            (construct-type:[AC]|)*

         where construct-type is the name of a type of construct and
         the 'A' or 'C' that follows it indicates whether an abstract
         or a concrete parser can be automatically generated for the
         type of construct, respectively. There should be exactly one
         such section in the string for each type of construct in the
         language. -->
    <xsl:variable name="has-concrete-parser-string">
        <xsl:call-template name="build-has-concrete-parser-string">
            <xsl:with-param name="construct-defs" select="$constructs"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- ######################################################## -->
    <!-- # Construct attribute-related templates.               # -->
    <!-- ######################################################## -->

    <!-- Returns the name of the construct attribute described by the
         specified element.

         Note: if the element doesn't explicitly specify a name by having a
         'name' attribute then the value of its (required) 'type' attribute
         is used as the attribute's name. -->
    <xsl:template name="construct-attribute-name">
        <xsl:param name="node" select="."/>

        <xsl:choose>
            <xsl:when test="$node/@name">
                <xsl:value-of select="$node/@name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$node/@type"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns the description of the construct attribute described by
         the specified element.

         Note: if the element doesn't explicitly specify a description by
         having a 'description' attribute then its name is used as the
         attribute's description. -->
    <xsl:template name="construct-attribute-description">
        <xsl:param name="node" select="."/>

        <xsl:choose>
            <xsl:when test="@description">
                <xsl:value-of select="@description"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="construct-attribute-name"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Indicates whether the construct described by the specified node
         has an attribute with the specified name. -->
    <xsl:template name="has-attribute-named">
        <xsl:param name="construct" select="."/>
        <xsl:param name="attribute-name"/>

        <!-- Build a string of 'T's and 'F's to indicate whether each of
             the construct's attributes has the specified name. -->
        <xsl:variable name="has-attribute-string">
            <xsl:apply-templates select="$construct"
                mode="build-has-attribute-named-string">
                <xsl:with-param name="attribute-name" select="$attribute-name"/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:value-of select="contains($has-attribute-string, 'T')"/>
    </xsl:template>

    <xsl:template match="alias-construct" mode="build-has-attribute-named-string">
        <xsl:param name="attribute-name"/>

        <!-- Build the 'has-attribute-string' for the attributes that this
             construct gets from the construct it aliases. -->
        <xsl:variable name="aliased-construct"
            select="$constructs[@type = @aliased-construct]"/>
        <xsl:variable name="aliased-construct-has-attribute-string">
            <xsl:apply-templates select="$aliased-construct"
                mode="build-has-attribute-named-string">
                <xsl:with-param name="attribute-name"
                    select="$attribute-name"/>
            </xsl:apply-templates>
        </xsl:variable>

        <!-- Build the 'has-attribute-string' for the attributes inherited
             by this construct. -->
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="inherited-construct-has-attribute-string">
            <xsl:apply-templates select="$inheritable-constructs[choice/subconstruct/@type = $type]"
                mode="build-has-attribute-named-string">
                <xsl:with-param name="attribute-name"
                    select="$attribute-name"/>
            </xsl:apply-templates>
        </xsl:variable>

        <!-- Build the 'has-attribute-string' that is the concatenation of:
             - the attributes defined on this construct,
             - the attributes this construct gets from the construct it
               aliases, and
             - the attributes inherited by this construct -->
        <xsl:call-template name="build-has-attribute-named-string">
            <xsl:with-param name="attributes" select="attribute"/>
            <xsl:with-param name="attribute-name" select="$attribute-name"/>
            <xsl:with-param name="string"
                select="concat($aliased-construct-has-attribute-string,
                               $inherited-construct-has-attribute-string)"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="*" mode="build-has-attribute-named-string">
        <xsl:param name="attribute-name"/>

        <!-- Build the 'has-attribute-string' for the attributes defined
             directly on this construct. -->
        <xsl:variable name="we-have-attribute-named-string">
            <xsl:call-template name="build-has-attribute-named-string">
                <xsl:with-param name="attributes" select="attribute"/>
                <xsl:with-param name="attribute-name" select="$attribute-name"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Build the 'has-attribute-string' for the attributes inherited
             by this construct. -->
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="inherited-construct-has-attribute-string">
            <xsl:apply-templates select="$inheritable-constructs[choice/subconstruct/@type = $type]"
                mode="build-has-attribute-named-string">
                <xsl:with-param name="attribute-name"
                    select="$attribute-name"/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:value-of select="concat($we-have-attribute-named-string,
                                     $inherited-construct-has-attribute-string)"/>
    </xsl:template>

    <!-- Builds a strings of 'T's and 'F's  - one for each of the specified
         attributes - each of which indicates whether the corresponding
         attribute has the specified name or not. -->
    <xsl:template name="build-has-attribute-named-string">
        <xsl:param name="attributes"/>
        <xsl:param name="attribute-name"/>
        <xsl:param name="string" select="''"/>

        <xsl:choose>
            <xsl:when test="count($attributes) = 0">
                <xsl:value-of select="$string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first" select="$attributes[1]"/>
                <xsl:variable name="rest"
                    select="$attributes[position() != 1]"/>
                <xsl:variable name="name">
                    <xsl:call-template name="construct-attribute-name">
                        <xsl:with-param name="node" select="$first"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="t-or-f">
                    <xsl:choose>
                        <xsl:when test="$name = $attribute-name">
                            <xsl:text>T</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>F</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:call-template name="build-has-attribute-named-string">
                    <xsl:with-param name="attributes" select="$rest"/>
                    <xsl:with-param name="attribute-name"
                        select="$attribute-name"/>
                    <xsl:with-param name="string"
                        select="concat($string, $t-or-f)"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- ######################################################## -->
    <!-- # Templates to test whether a construct has a concrete # -->
    <!-- # parser for it or not.                                # -->
    <!-- ######################################################## -->

    <!-- Returns true if a concrete parser can be automatically
         generated for the specified type of construct, and false
         if only an abstract parser can be automatically generated
         for it. -->
    <xsl:template name="has-concrete-parser">
        <xsl:param name="construct-type" select="@type"/>

        <xsl:choose>
            <xsl:when test="contains($has-concrete-parser-string,
                                     concat('|', $construct-type, ':C|'))">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- ######################################################## -->
    <!-- # Templates to build the 'has concrete parser' string. # -->
    <!-- ######################################################## -->

    <!-- Builds and outputs the 'has concrete parser' string for
         all of the type of constructs defined by the nodes in the
         specified set of nodes. -->
    <xsl:template name="build-has-concrete-parser-string">
        <xsl:param name="construct-defs"/>
        <xsl:param name="string" select="'|'"/>

        <xsl:choose>
            <xsl:when test="count($construct-defs) = 0">
                <xsl:value-of select="$string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first" select="$construct-defs[1]"/>
                <xsl:variable name="rest"
                    select="$construct-defs[position() != 1]"/>
                <xsl:variable name="first-entry">
                    <xsl:apply-templates select="$first"
                        mode="has-concrete-parser-string-entry"/>
                </xsl:variable>

                <xsl:call-template name="build-has-concrete-parser-string">
                    <xsl:with-param name="construct-defs" select="$rest"/>
                    <xsl:with-param name="string"
                        select="concat($string, $first-entry)"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs 'C' if the specified map string is unique and
         'A' if it isn't. -->
    <xsl:template name="map-string-to-letter">
        <xsl:param name="map-string"/>

        <xsl:variable name="is-concrete">
            <xsl:call-template name="is-map-unique">
                <xsl:with-param name="map-string" select="$map-string"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$is-concrete = 'true'">C</xsl:when>
            <xsl:otherwise>A</xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="choice-construct"
        mode="has-concrete-parser-string-entry">
        <xsl:variable name="letter">
            <xsl:call-template name="map-string-to-letter">
                <xsl:with-param name="map-string">
                    <xsl:call-template
                        name="build-choice-construct-map-string"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat(@type, ':', $letter, '|')"/>
    </xsl:template>

    <xsl:template match="line-choice-construct"
        mode="has-concrete-parser-string-entry">
        <xsl:variable name="letter">
            <xsl:call-template name="map-string-to-letter">
                <xsl:with-param name="map-string">
                    <xsl:call-template
                        name="build-line-choice-construct-map-string"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat(@type, ':', $letter, '|')"/>
    </xsl:template>

    <xsl:template match="multiline-construct"
        mode="has-concrete-parser-string-entry">
        <xsl:variable name="letter">
            <xsl:call-template name="map-string-to-letter">
                <xsl:with-param name="map-string">
                    <xsl:call-template
                        name="build-subconstructs-map-string">
                        <xsl:with-param name="subconstructs"
                            select="indented-subconstructs/*"/>
                    </xsl:call-template>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat(@type, ':', $letter, '|')"/>
    </xsl:template>

    <!-- Concrete parsers can always be automatically generated for
         all other types of constructs. -->
    <xsl:template match="*" mode="has-concrete-parser-string-entry">
        <xsl:value-of select="concat(@type, ':C|')"/>
    </xsl:template>


    <!-- ##################### -->
    <!-- # Utility templates # -->
    <!-- ##################### -->

    <!-- Outputs 'true' if the specified node has a 'number' attribute
         whose value indicates that it is optional, and false otherwise. -->
    <xsl:template name="is-optional">
        <xsl:param name="node" select="."/>

        <xsl:choose>
            <xsl:when test="@number = 'zero-or-one' or
                            @number = 'zero-or-more'">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:transform>
