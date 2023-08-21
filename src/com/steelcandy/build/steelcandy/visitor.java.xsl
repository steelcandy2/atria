<?xml version="1.0"?>
<!--
    $Id: visitor.java.xsl,v 1.3 2004/05/21 15:56:05 jgm Exp $

    Transforms the information about a visitor class into the source
    code for a visitor interface, and for an abstract visitor base
    class that by default has all visitor methods call an abstract
    defaultVisit() method.

    Author: James MacKay
    Last Updated: $Date: 2004/05/21 15:56:05 $

    Copyright (C) 2003-2004 by James MacKay.

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
    <xsl:param name="base-kind"/>

    <xsl:output method="text"/>


    <!-- Global variables -->

    <!-- The (possibly relative) pathname of the file being transformed. -->
    <xsl:param name="input-pathname"/>

    <!-- The (possibly relative) pathname of the XSL file doing the
         transformation. -->
    <xsl:param name="transform-pathname"/>

    <!-- The (possibly relative) pathname of the file resulting from
         the transformation. -->
    <xsl:param name="output-pathname"/>


    <xsl:variable name="root" select="//visitors"/>
    <xsl:variable name="top" select="$root/visitor[@base-kind=$base-kind]"/>

    <xsl:variable name="interface-name"
        select="$top/@interface-name"/>
    <xsl:variable name="abstract-class-name"
        select="$top/@abstract-class-name"/>
    <xsl:variable name="module-name" select="$top/@module-name"/>
    <xsl:variable name="var-name" select="$top/@item-argument-name"/>


    <!-- Templates -->

    <xsl:template match="visitors">
        <xsl:apply-templates select="visitor"/>
    </xsl:template>

    <xsl:template match="visitor">
        <xsl:call-template name="visitor-interface"/>
        <xsl:call-template name="abstract-visitor-class"/>
    </xsl:template>

    <xsl:template name="visitor-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($interface-name, '.java')"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$module-name"/>;
        <xsl:text>
import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by visitor classes that visit various
    kinds of </xsl:text>
        <xsl:value-of select="$base-kind"/>
        <xsl:text> objects.
    </xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$base-kind"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Visitor methods
</xsl:text>
        <xsl:apply-templates select="kinds/*"
            mode="visitor-method-declarations"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="kind" mode="visitor-method-declarations">
        <xsl:text>
    /**
        Visits the specified </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> object.

        @param </xsl:text>
        <xsl:value-of select="$var-name"/>
        <xsl:text> the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> object being visited
    */
    public void visit</xsl:text>
        <xsl:value-of select="concat(@name, '(', @name, ' ',
                                     $var-name, ');')"/>
        <xsl:text>
        // Assert.require(</xsl:text>
        <xsl:value-of select="$var-name"/>
        <xsl:text> != null);
</xsl:text>
    </xsl:template>


    <xsl:template name="abstract-visitor-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($abstract-class-name, '.java')"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$module-name"/>;
        <xsl:text>
import com.steelcandy.common.debug.Assert;

/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> classes.
    &lt;p&gt;
    All visitor methods are implemented to call &lt;code&gt;defaultVisit()&lt;/code&gt;.
    Subclasses just have to implement &lt;code&gt;defaultVisit()&lt;/code&gt; and
    override whichever visitor methods should not be handled by
    &lt;code&gt;defaultVisit()&lt;/code&gt;.
    </xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$base-kind"/>
        <xsl:text>
    @see #defaultVisit(</xsl:text>
        <xsl:value-of select="$base-kind"/>
        <xsl:text>)
*/
public abstract class </xsl:text>
        <xsl:value-of select="$abstract-class-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Visitor methods
</xsl:text>
        <xsl:apply-templates select="kinds/*"
            mode="visitor-method-definitions"/>
        <xsl:text>

    // Abstract methods

    /**
        The default visitor method.

        @param </xsl:text>
        <xsl:value-of select="$var-name"/>
        <xsl:text> the </xsl:text>
        <xsl:value-of select="$base-kind"/>
        <xsl:text> object being visited
    */
    protected abstract void defaultVisit(</xsl:text>
        <xsl:value-of select="concat($base-kind, ' ', $var-name)"/>
        <xsl:text>);
}
</xsl:text>
    </xsl:template>

    <xsl:template match="kind" mode="visitor-method-definitions">
        <xsl:text>
    /**
        @see </xsl:text>
        <xsl:value-of select="concat($interface-name, '#visit', @name, '(',
                                     @name, ')')"/>
        <xsl:text>
    */
    public void visit</xsl:text>
        <xsl:value-of select="concat(@name, '(', @name, ' ', $var-name, ')')"/>
        <xsl:text>
    {
        Assert.require(</xsl:text>
        <xsl:value-of select="$var-name"/>
        <xsl:text> != null);

        defaultVisit(</xsl:text>
        <xsl:value-of select="$var-name"/>
        <xsl:text>);
    }
</xsl:text>
    </xsl:template>


    <!-- Common templates -->

    <!-- Outputs the common part of a generated class' class comment.

         Note: the comment does NOT include a @version tag since using
         $Revision: 1.3 $ for its value expands to this template's revision,
         causing all generated files to appear to have been changed when
         they haven't otherwise changed. -->
    <xsl:template name="common-class-comment-part">&lt;p&gt;
    &lt;strong&gt;Note&lt;/strong&gt;: this file was automatically generated,
    and so should not be edited directly.
    &lt;ul&gt;
        &lt;li&gt;Input: <xsl:value-of select="$input-pathname"/>
        &lt;li&gt;Transform: <xsl:value-of select="$transform-pathname"/>
        &lt;li&gt;Output: <xsl:value-of select="$output-pathname"/>
    &lt;/ul&gt;

    @author James MacKay<xsl:text/>
    </xsl:template>
</xsl:transform>
