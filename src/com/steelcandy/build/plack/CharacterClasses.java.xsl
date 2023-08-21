<?xml version="1.0"?>
<!--
    Transforms a language description document into a Java class that
    contains information related to the character classes defined in that
    document.

    Copyright (C) 2004-2016 by James MacKay.

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

    <xsl:import href="language-common.java.xsl"/>


    <!-- ################# -->
    <!-- # Configuration # -->
    <!-- ################# -->


    <!-- #################### -->
    <!-- # Global variables # -->
    <!-- #################### -->

    <xsl:variable name="class-implementation"
        select="$implementation/character-classes-class"/>

    <xsl:variable name="class-name"
        select="concat($language-name, 'CharacterClasses')"/>
    <xsl:variable name="number-of-classes"
        select="count($character-classes)"/>


    <!-- ################## -->
    <!-- # Main templates # -->
    <!-- ################## -->

    <xsl:template match="language">
        <xsl:text/>

        <xsl:call-template name="character-classes-class"/>
    </xsl:template>

    <!-- Outputs the class that contains the character classes-related
         information specified in the language description document. -->
    <xsl:template name="character-classes-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$base-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

/**
    The class that contains character classes-related information for the
    </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> language.
    &lt;p&gt;
    Note: this class is final because it should not be subclassed: rather
    another class that defines common values for the language (usually
    called </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text>Info) should define their own constants, methods, etc.
    using the constants, methods, etc. in this class.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public final class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
{
    // Constants
</xsl:text>
    <xsl:call-template name="character-class-constants"/>
<xsl:text>}
</xsl:text>
    </xsl:template>

    <!-- Outputs the definitions of the Java constants whose values are
         all of the characters in the character class.

         Note: the constant definitions have to be ordered so that constants
         used in calculating the value of another constant must all have been
         defined earlier. Otherwise Java gets upset. -->
    <xsl:template name="character-class-constants">
        <xsl:variable name="ordered-names">
            <!-- At least currently we don't output a constant for character
                 classes that contain elements that can consist of two or more
                 characters (which is not the same as consisting of characters
                 from a choice of two or more other classes), including classes
                 whose definitions involve - directly or indirectly - such
                 classes. Such classes represent things like escaped characters
                 (e.g. in text literals). -->
            <xsl:call-template name="remove-multicharacter-element-class-names">
                <xsl:with-param name="names">
                    <xsl:call-template name="ordered-constant-names"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:variable>

<!--
<xsl:message>+++ ordered names = <xsl:value-of select="$ordered-names"/>
</xsl:message>
-->
        <xsl:call-template name="constant-definitions">
            <xsl:with-param name="names" select="$ordered-names"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs definitions of all of the character class constants whose
         names are in the specified list, in the order that the names appear
         in the list.

         The list is assumed to be in the same form as lists created using
         the 'make-list' template with the default separator. -->
    <xsl:template name="constant-definitions">
        <xsl:param name="names"/>

        <xsl:variable name="num-names">
            <xsl:call-template name="list-size">
                <xsl:with-param name="list" select="$names"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="$num-names &gt; 0">
            <xsl:variable name="first">
                <xsl:call-template name="list-head">
                    <xsl:with-param name="list" select="$names"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="rest">
                <xsl:call-template name="list-tail">
                    <xsl:with-param name="list" select="$names"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output a constant for the character class named $first. -->
            <xsl:apply-templates select="$character-classes[@name = $first]"
                mode="character-class-constant"/>

            <!-- Process $rest. -->
            <xsl:call-template name="constant-definitions">
                <xsl:with-param name="names" select="$rest"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Outputs a single constant definition. -->
    <xsl:template match="character-class" mode="character-class-constant">
        <xsl:text>
    /**
        All of the characters in the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> character class.
    */
    public static final String </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> =
        </xsl:text>
        <xsl:apply-templates select="*" mode="class-contents"/>
        <xsl:text>;
</xsl:text>
    </xsl:template>


    <xsl:template match="char-from-class" mode="class-contents">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="chars" mode="class-contents">
        <xsl:value-of select="concat('&quot;', @text, '&quot;')"/>
    </xsl:template>

    <xsl:template match="single-character-choice" mode="class-contents">
        <xsl:value-of select="concat('&quot;', @list, '&quot;')"/>
    </xsl:template>

    <xsl:template match="choice" mode="class-contents">
        <xsl:for-each select="*">
            <xsl:if test="position() &gt; 1">
                <xsl:text> + </xsl:text>
            </xsl:if>
            <xsl:apply-templates select="." mode="choice-element"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="char-from-class" mode="choice-element">
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>


    <!-- ########################################### -->
    <!-- # Character class name ordering templates # -->
    <!-- ########################################### -->

    <!-- Outputs a list of the names of all of the character classes
         constants, in the order that the constents are to be defined.

         Note: the list will be in the format that 'make-list' uses when
         the default separator is used. -->
    <xsl:template name="ordered-constant-names">
        <xsl:param name="ordered-names" select="''"/>
        <xsl:param name="start-pos" select="1"/>

        <xsl:choose>
            <xsl:when test="$start-pos &gt; $number-of-classes">
                <!-- There are no more classes to process. -->
                <xsl:value-of select="$ordered-names"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="new-ordered-names">
                    <xsl:apply-templates select="$character-classes[$start-pos]"
                        mode="ordered-constant-names">
                        <xsl:with-param name="names" select="$ordered-names"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <!-- Then process the rest of the classes recursively, using
                     the new list of ordered names. -->
                <xsl:call-template name="ordered-constant-names">
                    <xsl:with-param name="ordered-names"
                        select="$new-ordered-names"/>
                    <xsl:with-param name="start-pos" select="$start-pos + 1"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="character-class" mode="ordered-constant-names">
        <xsl:param name="names"/>

        <!-- Add the names of any dependencies first. -->
        <xsl:variable name="new-names">
            <xsl:call-template name="children-ordered-constant-names">
                <xsl:with-param name="names" select="$names"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Then append the character class' name. -->
        <xsl:call-template name="append-ordered-name">
            <xsl:with-param name="names" select="$new-names"/>
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="choice" mode="ordered-constant-names">
        <xsl:param name="names"/>

        <xsl:call-template name="children-ordered-constant-names">
            <xsl:with-param name="names" select="$names"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Processes the specified child nodes. -->
    <xsl:template name="children-ordered-constant-names">
        <xsl:param name="names"/>
        <xsl:param name="children" select="*"/>
        <xsl:param name="start-pos" select="1"/>

        <xsl:variable name="num-children" select="count($children)"/>
        <xsl:choose>
            <xsl:when test="$start-pos &gt; $num-children">
                <xsl:value-of select="$names"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="new-names">
                    <xsl:apply-templates select="$children[$start-pos]"
                        mode="ordered-constant-names">
                        <xsl:with-param name="names" select="$names"/>
                    </xsl:apply-templates>
                </xsl:variable>

                <xsl:call-template name="children-ordered-constant-names">
                    <xsl:with-param name="names" select="$new-names"/>
                    <xsl:with-param name="children" select="$children"/>
                    <xsl:with-param name="start-pos" select="$start-pos + 1"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="char-from-class" mode="ordered-constant-names">
        <xsl:param name="names"/>

        <xsl:variable name="name" select="@name"/>

        <!-- Add the names of any of the class' dependencies first. -->
        <xsl:variable name="new-names">
            <xsl:apply-templates select="$character-classes[@name = $name]"
                mode="ordered-constant-names">
                <xsl:with-param name="names" select="$names"/>
            </xsl:apply-templates>
        </xsl:variable>

        <!-- Then add the class' name (iff it isn't already in 'names'). -->
        <xsl:call-template name="append-ordered-name">
            <xsl:with-param name="names" select="$new-names"/>
            <xsl:with-param name="name" select="$name"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Character class components that don't introduce ordering
         dependencies. -->
    <xsl:template match="chars | single-character-choice"
        mode="ordered-constant-names">
        <xsl:param name="names"/>

        <xsl:value-of select="$names"/>
    </xsl:template>

    <!-- Append 'name' to 'names' iff it isn't already in 'names'. -->
    <xsl:template name="append-ordered-name">
        <xsl:param name="names"/>
        <xsl:param name="name"/>

        <xsl:variable name="already-contains-name">
            <xsl:call-template name="list-contains">
                <xsl:with-param name="list" select="$names"/>
                <xsl:with-param name="element" select="$name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$already-contains-name = 'true'">
                <xsl:value-of select="$names"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="append-to-list">
                    <xsl:with-param name="list" select="$names"/>
                    <xsl:with-param name="element" select="$name"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ########################################## -->
    <!-- # Character class name removal templates # -->
    <!-- ########################################## -->

    <!-- Removes from the specified list of names the names of those character
         classes that can contain elements that consist of two or more
         characters (e.g. escaped characters).

         Note: the list passed to this template is assumed to be in the format
         that 'make-list' uses when the default separator is used. -->
    <xsl:template name="remove-multicharacter-element-class-names">
        <xsl:param name="names"/>

        <xsl:variable name="num-names">
            <xsl:call-template name="list-size">
                <xsl:with-param name="list" select="$names"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="$num-names = 0">
                <xsl:text></xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first">
                    <xsl:call-template name="list-head">
                        <xsl:with-param name="list" select="$names"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="rest">
                    <xsl:call-template name="list-tail">
                        <xsl:with-param name="list" select="$names"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="remove-first">
                    <xsl:call-template name="contains-multicharacter-elements">
                        <xsl:with-param name="name" select="$first"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:choose>
                    <xsl:when test="$remove-first = 'true'">
<!--
<xsl:message>+++ removing char class <xsl:value-of select="$first"/></xsl:message>
-->
                        <xsl:call-template
                            name="remove-multicharacter-element-class-names">
                            <xsl:with-param name="names" select="$rest"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Prepend $first to the front of the result of
                             removing elements from $rest. -->
                        <xsl:call-template name="prepend-to-list">
                            <xsl:with-param name="element" select="$first"/>
                            <xsl:with-param name="list">
                                <xsl:call-template
                                    name="remove-multicharacter-element-class-names">
                                    <xsl:with-param name="names" select="$rest"/>
                                </xsl:call-template>
                            </xsl:with-param>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Indicates whether the character class with the specified name can
         contain multicharacter elements.

         Note: if a character class' definition involves another class that
         can contain multicharacter elements, then that character class can
         also contain multicharacter elements. -->
    <xsl:template name="contains-multicharacter-elements">
        <xsl:param name="name"/>

        <xsl:apply-templates select="$character-classes[@name = $name]"
            mode="contains-multichar-elements"/>
    </xsl:template>

    <xsl:template match="character-class" mode="contains-multichar-elements">
        <xsl:variable name="num-children" select="count(*)"/>

        <xsl:choose>
            <xsl:when test="$num-children &gt; 1">
                <!-- This class contains multichar elements directly. (The
                     2+ child elements represent the components of each
                     element.) -->
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:when test="$num-children = 0">
                <!-- I'm not even sure this is possible, but hey. -->
                <xsl:value-of select="false()"/>
            </xsl:when>
            <xsl:otherwise>  <!-- $num-children = 1 -->
                <xsl:apply-templates select="*"
                    mode="contains-multichar-elements"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="choice" mode="contains-multichar-elements">
        <xsl:call-template name="choice-contains-multicharacter-elements"/>
    </xsl:template>

    <!-- Indicates whether any of the choices in the current 'choice' element
         at or after the specified position can contain multicharacter
         elements. -->
    <xsl:template name="choice-contains-multicharacter-elements">
        <xsl:param name="start-pos" select="1"/>

        <xsl:variable name="num-choices" select="count(*)"/>

        <xsl:choose>
            <xsl:when test="$start-pos &gt; $num-choices">
                <xsl:value-of select="false()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="first-contains">
                    <xsl:apply-templates select="*[$start-pos]"
                        mode="contains-multichar-elements"/>
                </xsl:variable>

                <xsl:choose>
                    <xsl:when test="$first-contains = 'true'">
                        <xsl:value-of select="true()"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template
                            name="choice-contains-multicharacter-elements">
                            <xsl:with-param name="start-pos"
                                select="$start-pos + 1"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="char-from-class" mode="contains-multichar-elements">
        <xsl:variable name="name" select="@name"/>

        <xsl:apply-templates select="$character-classes[@name = $name]"
            mode="contains-multichar-elements"/>
    </xsl:template>

    <xsl:template match="chars | single-character-choice"
        mode="contains-multichar-elements">
        <xsl:value-of select="false()"/>
    </xsl:template>
</xsl:transform>
