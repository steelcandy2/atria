<?xml version="1.0"?>
<!--
    $Id: map-string.xsl,v 1.8 2006/06/09 23:32:23 jgm Exp $

    Contains the variables and templates related to map strings, which
    map tokens to the types of constructs that they could validly start.

    A string of the following format is built to describe the
    mapping between the first token of a construct and the type
    of construct(s) that it could validly start:

        map-string ::= map-string-entry*
        map-string-entry ::= '|'token-name':'(construct-type';')*

    'token-name' is the name of the first token of a construct,
    and 'construct-type' is the name of one of the types of
    constructs that the corresponding token can start.

    Author: James MacKay
    Last Updated: $Date: 2006/06/09 23:32:23 $

    Copyright (C) 2002-2010 by James MacKay.

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

    <!-- Note: strings that contain the constructs that have already
         been visited have the following syntax:

             visited-constructs ::= ('|'construct-type';')* -->


    <!-- ######################################################### -->
    <!-- # Templates to build line choice construct map strings. # -->
    <!-- ######################################################### -->

    <!-- Outputs the map string for all of the constructs described
         by the child 'subconstruct' elements of the specified line
         construct parent element's 'choice' child element. -->
    <xsl:template name="build-line-choice-construct-map-string">
        <xsl:param name="parent" select="."/>

        <xsl:call-template name="merge-mappings">
            <xsl:with-param name="map-string" select="''"/>
            <xsl:with-param name="visited-constructs" select="''"/>
            <xsl:with-param name="subconstructs"
                select="$parent/choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Merges mappings for all of the specified subconstructs into the
         specified map string and returns the result of the merge. The
         mappings are to the construct of the specified type unless that
         type is unspecified (or the empty string), in which case the
         mappings are to the individual subconstructs' types. -->
    <xsl:template name="merge-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type" select="''"/>
        <xsl:param name="visited-constructs"/>
        <xsl:param name="subconstructs"/>

<!--
<xsl:message>== in merge-mappings()</xsl:message>
-->
        <xsl:choose>
            <xsl:when test="count($subconstructs) = 0">
                <xsl:value-of select="$map-string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="type">
                    <xsl:choose>
                        <xsl:when test="string-length($construct-type) = 0">
                            <xsl:value-of select="$subconstructs[1]/@type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$construct-type"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:variable name="new-map-string">
                    <xsl:apply-templates select="$subconstructs[1]"
                        mode="merge-line-construct-part-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="construct-type"
                            select="$type"/>
                        <xsl:with-param name="visited-constructs"
                            select="$visited-constructs"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <xsl:call-template name="merge-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                    <xsl:with-param name="subconstructs"
                        select="$subconstructs[position() != 1]"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="compound-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings from the token(s) that can start the
             FIRST subconstruct to the type of this compound
             construct. (The parser for this construct will find
             and parse the subsequent subconstructs too.) -->
        <xsl:apply-templates select="subconstruct[1]"
            mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type"
                select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="multiline-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings from the token(s) that can start the
             first line of the construct. -->
        <xsl:apply-templates select="first-line"
            mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type" select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="construct | single-line-construct | first-line |
                         single-token-construct"
            mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings from the token(s) that can start the
             construct. -->
        <xsl:apply-templates select="*[1]"
            mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type"
                select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="line-choice-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings from the token(s) that can start EACH
             of the subconstructs in the choice to the type of that
             subconstruct. -->
        <xsl:call-template name="merge-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
            <xsl:with-param name="subconstructs"
                select="choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="choice-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings from the token(s) that can start EACH
             of the subconstructs in the choice to the specified
             construct type. -->
        <xsl:call-template name="merge-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type"
                select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
            <xsl:with-param name="subconstructs"
                select="choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="repeated-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge a mapping from the start terminal if it has one;
             otherwise merge mapping(s) from the token(s) that can
             start the subconstruct that is repeated. -->
        <xsl:choose>
            <xsl:when test="@start-terminal">
                <xsl:call-template name="merge-mapping">
                    <xsl:with-param name="map-string" select="$map-string"/>
                    <xsl:with-param name="token-name"
                        select="@start-terminal"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- There is assumed to be at least one (actually, exactly
                     one) 'subconstruct' child element. -->
                <xsl:apply-templates select="subconstruct[1]"
                        mode="merge-line-construct-part-mappings">
                    <xsl:with-param name="map-string" select="$map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="alias-construct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@type"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merges the mapping(s) for the aliased construct. -->
        <xsl:variable name="aliased-construct"
            select="@aliased-construct"/>
        <xsl:variable name="aliased-construct-element"
            select="key('construct', $aliased-construct)"/>

        <xsl:apply-templates select="$aliased-construct-element"
                mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type"
                select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="terminal-choice"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Merge mappings for each terminal in the choice. -->
        <xsl:call-template name="merge-terminals">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type" select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
            <xsl:with-param name="terminals" select="terminal"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Merges the mappings from each of the specified terminals/tokens
         to the specified construct type into the specified map string,
         and outputs the results of the merge. -->
    <xsl:template name="merge-terminals">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>
        <xsl:param name="terminals"/>

        <xsl:variable name="first" select="$terminals[1]"/>
        <xsl:choose>
            <xsl:when test="$first">
                <xsl:variable name="rest"
                    select="$terminals[position() != 1]"/>

                <xsl:variable name="new-map-string">
                    <xsl:apply-templates select="$first"
                        mode="merge-line-construct-part-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="construct-type"
                            select="$construct-type"/>
                        <xsl:with-param name="visited-constructs"
                            select="$visited-constructs"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <xsl:call-template name="merge-terminals">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                    <xsl:with-param name="terminals" select="$rest"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$map-string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="terminal" mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched <xsl:value-of select="@name"/> [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <xsl:variable name="new-map-string">
            <xsl:call-template name="merge-mapping">
                <xsl:with-param name="map-string" select="$map-string"/>
                <xsl:with-param name="token-name" select="@name"/>
                <xsl:with-param name="construct-type"
                    select="$construct-type"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- If the terminal is optional then also add mappings for
             the token(s) that start the next construct part (if any). -->
        <xsl:choose>
            <xsl:when test="@number = 'zero-or-one' or
                            @number = 'zero-or-more'">
                <xsl:apply-templates select="(following-sibling::*)[1]"
                    mode="merge-line-construct-part-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-map-string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="space | attribute"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <!-- Skip this part: use the next sibling instead. (There
             should always be a next sibling.) -->
        <xsl:apply-templates select="(following-sibling::*)[1]"
                mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type"
                select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>

    <!-- TODO: we currently properly handle the first subconstruct of a
         construct being optional, but we don't properly handle any
         subsequent subconstructs being optional too. So for now we don't
         allow the second and subsequent subconstructs of a construct to be
         optional, or at least the generated parsers won't handle them
         correctly. -->
    <xsl:template match="subconstruct"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched subconstruct [<xsl:value-of select="@type"/>]</xsl:message>
-->
        <!-- Merges the mapping(s) for the specific type of construct
             that this subconstruct is, provided it hasn't already been
             merged in as part of processing this construct (which can
             happen if the construct or one of its subconstructs has a
             recursive definition). -->
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="visited-type"
            select="concat('|', $type, ';')"/>
        <xsl:variable name="new-visited-constructs"
            select="concat($visited-constructs, $visited-type)"/>

        <xsl:choose>
            <xsl:when test="not(contains($visited-constructs, $visited-type))">
                <xsl:variable name="subconstruct-element"
                    select="key('construct', $type)"/>

                <xsl:variable name="new-map-string">
                    <xsl:apply-templates select="$subconstruct-element"
                            mode="merge-line-construct-part-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="construct-type"
                            select="$construct-type"/>
                        <xsl:with-param name="visited-constructs"
                            select="$new-visited-constructs"/>
                    </xsl:apply-templates>
                </xsl:variable>
<!--
<xsl:message>    new-map-string = [<xsl:value-of select="$new-map-string"/>]</xsl:message>
-->

                <!-- If the subconstruct is optional then also add mappings
                     for the token(s) that start the next construct part (if
                     any). -->
                <xsl:choose>
                    <xsl:when test="count(preceding-sibling::subconstruct) = 0 and
                        (@number = 'zero-or-one' or @number = 'zero-or-more')">
                        <xsl:apply-templates select="(following-sibling::*)[1]"
                            mode="merge-line-construct-part-mappings">
                            <xsl:with-param name="map-string"
                                select="$new-map-string"/>
                            <xsl:with-param name="construct-type"
                                select="$construct-type"/>
                            <xsl:with-param name="visited-constructs"
                                select="$visited-constructs"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$new-map-string"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- We've already visited this type of subconstruct, so just
                     return the map string unchanged unless the subconstruct
                     is optional, in which case also add mappings for the
                     token(s) that start the next construct part (if any). -->
                <xsl:choose>
                    <xsl:when test="count(preceding-sibling::subconstruct) = 0 and
                        (@number = 'zero-or-one' or @number = 'zero-or-more')">
                        <xsl:apply-templates select="(following-sibling::*)[1]"
                            mode="merge-line-construct-part-mappings">
                            <xsl:with-param name="map-string"
                                select="$map-string"/>
                            <xsl:with-param name="construct-type"
                                select="$construct-type"/>
                            <xsl:with-param name="visited-constructs"
                                select="$visited-constructs"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$map-string"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="flag-from-set"
        mode="merge-line-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

<!--
<xsl:message>  === matched [<xsl:value-of select="name()"/>]</xsl:message>
-->
        <xsl:variable name="flag-set-name" select="@name"/>
        <xsl:variable name="flag-set-def"
            select="$top/construct-flag-sets/section/flag-set-definition[@name = $flag-set-name]"/>

        <!-- Merge mapping(s) from the token(s) corresponding to ALL
             of the flags in the flag set. -->
        <xsl:variable name="new-map-string">
            <xsl:call-template name="merge-flag-mappings">
                <xsl:with-param name="map-string" select="$map-string"/>
                <xsl:with-param name="flag-defs"
                    select="$flag-set-def/flag"/>
                <xsl:with-param name="construct-type"
                    select="$construct-type"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- If the flag is optional then also add mappings for
             the token(s) that start the next construct part (if any). -->
        <xsl:choose>
            <xsl:when test="@number = 'zero-or-one' or
                            @number = 'zero-or-more'">
                <xsl:apply-templates select="(following-sibling::*)[1]"
                    mode="merge-line-construct-part-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$new-map-string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns the result of merging the specified map string with a
         mapping  for each flag in the specified set of flag definitions.
         The mapping for a flag is from the token corresponding to the
         flag to the construct of the specified type.

         Each of the elements in the 'flag-defs' parameter is one of the
         'flag' child elements of a flag-set-definition. -->
    <xsl:template name="merge-flag-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="flag-defs"/>
        <xsl:param name="construct-type"/>

        <xsl:choose>
            <xsl:when test="count($flag-defs) = 0">
                <xsl:value-of select="$map-string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first" select="$flag-defs[1]"/>
                <xsl:variable name="rest"
                    select="$flag-defs[position() != 1]"/>
                <xsl:variable name="new-map-string">
                    <xsl:call-template name="merge-mapping">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="token-name"
                            select="$first/@name"/>
                        <xsl:with-param name="construct-type"
                            select="$construct-type"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:call-template name="merge-flag-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="flag-defs" select="$rest"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- #################################################### -->
    <!-- # Templates to build choice construct map strings. # -->
    <!-- #################################################### -->

    <!-- Outputs the map string for all of the constructs described
         by the child 'subconstruct' elements of the specified choice
         construct parent element's 'choice' child element. -->
    <xsl:template name="build-choice-construct-map-string">
        <xsl:param name="parent" select="."/>

        <xsl:call-template name="merge-choice-mappings">
            <xsl:with-param name="map-string" select="''"/>
            <xsl:with-param name="visited-constructs" select="''"/>
            <xsl:with-param name="subconstructs"
                select="$parent/choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Merges mappings for all of the specified choice-construct
         subconstructs into the specified map string and returns the
         result of the merge. The mappings are to the construct of the
         specified type unless that type is unspecified (or the empty
         string), in which case the mappings are to the individual
         subconstructs' types. -->
    <xsl:template name="merge-choice-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type" select="''"/>
        <xsl:param name="visited-constructs"/>
        <xsl:param name="subconstructs"/>

        <xsl:choose>
            <xsl:when test="count($subconstructs) = 0">
                <xsl:value-of select="$map-string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="type">
                    <xsl:choose>
                        <xsl:when test="string-length($construct-type) = 0">
                            <xsl:value-of select="$subconstructs[1]/@type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$construct-type"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="new-map-string">
                    <xsl:apply-templates select="$subconstructs[1]"
                        mode="merge-choice-construct-part-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="construct-type"
                            select="$type"/>
                        <xsl:with-param name="visited-constructs"
                            select="$visited-constructs"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <xsl:call-template name="merge-choice-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                    <xsl:with-param name="subconstructs"
                        select="$subconstructs[position() != 1]"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct"
        mode="merge-choice-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <!-- Merges the mapping(s) for the specific type of construct
             that this subconstruct is, provided it hasn't already been
             merged in as part of processing this construct (which can
             happen if the construct or one of its subconstructs has a
             recursive definition). -->
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="visited-type"
            select="concat('|', $type, ';')"/>
        <xsl:variable name="new-visited-constructs"
            select="concat($visited-constructs, $visited-type)"/>

        <xsl:choose>
            <xsl:when test="not(contains($visited-constructs, $visited-type))">
                <xsl:variable name="subconstruct-element"
                    select="key('construct', $type)"/>

                <xsl:apply-templates select="$subconstruct-element"
                        mode="merge-choice-construct-part-mappings">
                    <xsl:with-param name="map-string"
                        select="$map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$new-visited-constructs"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <!-- We've already visited this type of subconstruct,
                     so just return the map string unchanged. -->
                <xsl:value-of select="$map-string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Note: matches only subconstructs that are choice-constructs and
         that are direct subconstructs of a choice-construct. -->
    <xsl:template match="choice-construct"
        mode="merge-choice-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <!-- Merge mappings to each construct in the choice (i.e.
             NOT this construct - ignore construct-type). -->
        <xsl:call-template name="merge-choice-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
            <xsl:with-param name="subconstructs"
                select="choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Note: matches all other choice-construct parts, including
         subconstructs that are choice-constructs but that are not
         direct subconstructs of a choice-construct. -->
    <xsl:template match="*" mode="merge-choice-construct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <!-- Merge mappings to 'construct-type' (which should always
             be non-empty here): this is done using the line-construct
             templates for building map-strings. -->
        <xsl:apply-templates select="."
            mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type" select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>


    <!-- ################################################ -->
    <!-- # Templates to build subconstruct map strings. # -->
    <!-- ################################################ -->

    <!-- Outputs the map string for all of the constructs described by
         the specified 'subconstruct' elements (which by default is all
         of the 'subconstruct' child elements of the current node). -->
    <xsl:template name="build-subconstructs-map-string">
        <xsl:param name="subconstructs" select="subconstruct"/>

        <xsl:call-template name="merge-subconstructs-mappings">
            <xsl:with-param name="map-string" select="''"/>
            <xsl:with-param name="visited-constructs" select="''"/>
            <xsl:with-param name="subconstructs" select="$subconstructs"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Merges mappings for all of the specified subconstructs into the
         specified map string and returns the result of the merge. The
         mappings are to the construct of the specified type unless that
         type is unspecified (or the empty string), in which case the
         mappings are to the individual subconstructs' types. -->
    <xsl:template name="merge-subconstructs-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type" select="''"/>
        <xsl:param name="visited-constructs"/>
        <xsl:param name="subconstructs"/>

        <xsl:choose>
            <xsl:when test="count($subconstructs) = 0">
                <xsl:value-of select="$map-string"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="type">
                    <xsl:choose>
                        <xsl:when test="string-length($construct-type) = 0">
                            <xsl:value-of select="$subconstructs[1]/@type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$construct-type"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="new-map-string">
                    <xsl:apply-templates select="$subconstructs[1]"
                        mode="merge-subconstruct-part-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                        <xsl:with-param name="construct-type"
                            select="$type"/>
                        <xsl:with-param name="visited-constructs"
                            select="$visited-constructs"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <xsl:call-template
                    name="merge-subconstructs-mappings">
                    <xsl:with-param name="map-string"
                        select="$new-map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$visited-constructs"/>
                    <xsl:with-param name="subconstructs"
                        select="$subconstructs[position() != 1]"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct"
        mode="merge-subconstruct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <!-- Merges the mapping(s) for the specific type of construct
             that this subconstruct is, provided it hasn't already been
             merged in as part of processing this construct (which can
             happen if the construct or one of its subconstructs has a
             recursive definition). -->
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="visited-type"
            select="concat('|', $type, ';')"/>
        <xsl:variable name="new-visited-constructs"
            select="concat($visited-constructs, $visited-type)"/>

        <xsl:choose>
            <xsl:when test="not(contains($visited-constructs, $visited-type))">
                <xsl:variable name="subconstruct-element"
                    select="key('construct', $type)"/>
                <xsl:apply-templates select="$subconstruct-element"
                        mode="merge-subconstruct-part-mappings">
                    <xsl:with-param name="map-string"
                        select="$map-string"/>
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="visited-constructs"
                        select="$new-visited-constructs"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <!-- We've already visited this type of subconstruct,
                     so just return the map string unchanged. -->
                <xsl:value-of select="$map-string"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="choice-construct | line-choice-construct"
        mode="merge-subconstruct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <!-- Merge mappings from the token(s) that can start EACH
             of the subconstructs in the choice to the SPECIFIED type of
             construct. -->
        <xsl:call-template name="merge-subconstructs-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type" select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
            <xsl:with-param name="subconstructs"
                select="choice/subconstruct"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Merge in all other subconstruct parts in the same way
         as they would be for line choice constructs. -->
    <xsl:template match="*" mode="merge-subconstruct-part-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="construct-type"/>
        <xsl:param name="visited-constructs"/>

        <xsl:apply-templates select="."
            mode="merge-line-construct-part-mappings">
            <xsl:with-param name="map-string" select="$map-string"/>
            <xsl:with-param name="construct-type" select="$construct-type"/>
            <xsl:with-param name="visited-constructs"
                select="$visited-constructs"/>
        </xsl:apply-templates>
    </xsl:template>



    <!-- ###################### -->
    <!-- # Utility templates. # -->
    <!-- ###################### -->

    <!-- Returns the first mapping in the specified map string, or
         an empty string if there are no mappings in the map string.
         The mapping will NOT start with a vertical bar ('|'). -->
    <xsl:template name="first-mapping">
        <xsl:param name="map-string"/>

        <xsl:choose>
            <xsl:when test="starts-with($map-string, '|')">
                <!-- There is at least one mapping in map-string -->
                <xsl:choose>
                    <xsl:when test="contains(substring($map-string, 2), '|')">
                        <!-- There is another mapping after the first -->
                        <xsl:value-of select="substring-before(substring($map-string, 2), '|')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- There is only one mapping in map-string -->
                        <xsl:value-of select="substring($map-string, 2)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- There are no mappings in map-string -->
                <xsl:value-of select="''"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns a map-string consisting of all of the mappings after
         the first in the specified map string, or an empty string if
         there are less than two mappings in the map string. -->
    <xsl:template name="tail-mappings">
        <xsl:param name="map-string"/>

        <xsl:choose>
            <xsl:when test="starts-with($map-string, '|') and
                            contains(substring($map-string, 2), '|')">
                <!-- There are two or more mappings in map-string -->
                <xsl:value-of select="concat('|', substring-after(substring($map-string, 2), '|'))"/>
            </xsl:when>
            <xsl:otherwise>
                <!-- There are zero or one mappings in map-string, and
                     hence nothing in its tail. -->
                <xsl:value-of select="''"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns the token name contained in the specified token
         name to construct types mapping. -->
    <xsl:template name="token-name-from-mapping">
        <xsl:param name="mapping"/>

        <xsl:value-of select="substring-before($mapping, ':')"/>
    </xsl:template>

    <!-- Returns the construct type(s) contained in the specified
         token name to construct types mapping. Each of the construct
         types is followed by a semicolon (';'), even if there is
         only one construct type. -->
    <xsl:template name="construct-types-from-mapping">
        <xsl:param name="mapping"/>

        <xsl:value-of select="substring-after($mapping, ':')"/>
    </xsl:template>

    <!-- Indicates whether the specified list of construct types
         contains the specified construct type. -->
    <xsl:template name="contains-construct-type">
        <xsl:param name="construct-types"/>
        <xsl:param name="type"/>

        <xsl:choose>
            <xsl:when test="string-length($construct-types) = 0">
                <xsl:value-of select="false()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first">
                    <xsl:call-template name="first-construct-type">
                        <xsl:with-param name="construct-types"
                            select="$construct-types"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="tail">
                    <xsl:call-template name="tail-construct-types">
                        <xsl:with-param name="construct-types"
                            select="$construct-types"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:choose>
                    <xsl:when test="$first = $type">
                        <xsl:value-of select="true()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="contains-construct-type">
                            <xsl:with-param name="construct-types"
                                select="$tail"/>
                            <xsl:with-param name="type" select="$type"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Returns the first construct type from the specified list
         of construct types, as found in a token name to construct
         types mapping (from a map string). The construct type will
         NOT be followed by a semicolon. -->
    <xsl:template name="first-construct-type">
        <xsl:param name="construct-types"/>

        <xsl:value-of select="substring-before($construct-types, ';')"/>
    </xsl:template>

    <!-- Returns a list of construct types consisting of all of the
         construct types except the first in the specified list of
         construct types. Both the specified list of construct types
         and the one that is returned is of the format found in a
         token name to construct mapping (in a map string). -->
    <xsl:template name="tail-construct-types">
        <xsl:param name="construct-types"/>

        <xsl:value-of select="substring-after($construct-types, ';')"/>
    </xsl:template>


    <!-- Returns the number of mappings in the specified map string.

         Note: the previous-count parameter is usually omitted in calls
         to this template, except in the implementation of this template
         itself. -->
    <xsl:template name="number-of-mappings">
        <xsl:param name="map-string"/>
        <xsl:param name="previous-count" select="0"/>

        <xsl:choose>
            <xsl:when test="string-length($map-string) &gt; 0">
                <xsl:variable name="tail">
                    <xsl:call-template name="tail-mappings">
                        <xsl:with-param name="map-string"
                            select="$map-string"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:call-template name="number-of-mappings">
                    <xsl:with-param name="map-string" select="$tail"/>
                    <xsl:with-param name="previous-count"
                        select="$previous-count + 1"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- There are no mappings in map-string. -->
                <xsl:value-of select="$previous-count"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Indicates whether the specified mapping (from a map string)
         maps its token ID to exactly one construct type: returns
         true if it does and false if it doesn't. -->
    <xsl:template name="is-mapping-unique">
        <xsl:param name="mapping"/>

        <xsl:variable name="tail-types">
            <xsl:call-template name="tail-construct-types">
                <xsl:with-param name="construct-types">
                    <xsl:call-template name="construct-types-from-mapping">
                        <xsl:with-param name="mapping" select="$mapping"/>
                    </xsl:call-template>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="string-length($tail-types) = 0"/>
    </xsl:template>

    <!-- Indicates whether the specified map string maps each token ID to
         a unique construct type: returns true if does and false if it
         doesn't.

         TODO: convert this to use is-mapping-unique and other map string
               utility templates (?) -->
    <xsl:template name="is-map-unique">
        <xsl:param name="map-string"/>

        <xsl:choose>
            <xsl:when test="contains($map-string, '|')">
                <xsl:variable name="after"
                    select="substring-after($map-string, '|')"/>
                <xsl:variable name="first"
                    select="substring-before(concat($after, '|'), '|')"/>
                <xsl:variable name="rest"
                    select="substring-after($after, '|')"/>

                <!-- A token maps to multiple constructs if the first
                     entry contains more than one semicolon, or one of
                     the other entries maps a token to multiple ones. -->
                <xsl:choose>
                    <xsl:when test="contains(substring-after($first, ';'), ';')">
                        <xsl:value-of select="false()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="is-map-unique">
                            <xsl:with-param name="map-string" select="$rest"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- There are no more map entries and no mapping
                     from a token to multiple constructs was found,
                     so return true. -->
                <xsl:value-of select="true()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Merges the mapping from the specified token-name to the specified
         construct-type into the specified map-string.

         The mapping is merged into the map-string as follows:
            - look for a map-string-entry in the map-string that
              starts with '|token-name:'
                - if no such map-string-entry is found then append a new
                  map-string-entry to the map-string that is of the form
                  '|token-name:construct-type;'
                - otherwise a matching map-string-entry was found: search
                  for ';construct-type;' in the list of construct types in
                  the ';'map-string-entry (i.e. between the ':' and the end
                  of the string or the next '|', exclusive)
                    - if the construct-type is found then leave the map
                      string unmodified
                    - otherwise append 'construct-type;' to the end of
                      the map-string-entry (that is, insert it into the
                      map-string at the end of the entry) -->
    <xsl:template name="merge-mapping">
        <xsl:param name="map-string"/>
        <xsl:param name="token-name"/>
        <xsl:param name="construct-type"/>

        <xsl:variable name="entry-start"
            select="concat('|', $token-name, ':')"/>
        <xsl:choose>
            <xsl:when test="contains($map-string, $entry-start)">
                <!-- There exists a map-string-entry in map-string for
                     token-name. -->
                <xsl:variable name="before"
                    select="substring-before($map-string, $entry-start)"/>
                <xsl:variable name="rest"
                    select="substring-after($map-string, $entry-start)"/>
                <xsl:variable name="construct-types">
                    <xsl:choose>
                        <xsl:when test="contains($rest, '|')">
                            <xsl:value-of
                                select="substring-before($rest, '|')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$rest"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="after">
                    <xsl:choose>
                        <xsl:when test="contains($rest, '|')">
                            <xsl:value-of select="concat('|', substring-after($rest, '|'))"/>
                        </xsl:when>
                        <xsl:otherwise></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:choose>
                    <xsl:when
                        test="contains(concat(';', $construct-types),
                                       concat(';', $construct-type, ';'))">
                        <!-- The mapping is already in the map-string, so
                             output the map-string unchanged. -->
                        <xsl:value-of select="$map-string"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Add the construct-type to the token-name's
                             map-string-entry. -->
                        <xsl:value-of select="concat($before, $entry-start, $construct-types, $construct-type, ';', $after)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- There is no map-string-entry in map-string for
                     token-name, so add one to the end of the map-string
                     that we output. -->
                <xsl:value-of select="concat($map-string, '|', $token-name, ':', $construct-type, ';')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:transform>
