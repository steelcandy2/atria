<?xml version="1.0"?>
<!--
    $Id: Constants.java.xsl,v 1.4 2012/03/20 00:49:06 jgm Exp $

    Transforms a language description document into a Java class that
    contains the constants defined in that document.

    Author: James MacKay
    Last Updated: $Date: 2012/03/20 00:49:06 $

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
        select="$implementation/constants-class"/>

    <xsl:variable name="constants-class-name"
        select="concat($language-name, 'Constants')"/>


    <!-- ################## -->
    <!-- # Main templates # -->
    <!-- ################## -->

    <xsl:template match="language">
        <xsl:text/>

        <xsl:call-template name="constants-class"/>
    </xsl:template>

    <!-- Outputs the class that contains all of the constants defined in the
         language description document. -->
    <xsl:template name="constants-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($constants-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$base-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
    The class that defines constants for the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> language.
    &lt;p&gt;
    Note: this class is final because it should not be subclassed: rather
    another class that defines common values for the language (usually
    called </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text>Info) should define their own constants using the
    constants in this class, often with more appropriate types.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public final class </xsl:text>
        <xsl:value-of select="$constants-class-name"/>
        <xsl:text>
{
    // Constants
</xsl:text>
    <xsl:apply-templates select="$constants" mode="constant-def"/>
    <xsl:text>

    // Constant lists and sets
</xsl:text>
    <xsl:call-template name="constant-list-and-set-defs"/>
    <xsl:text>

    // Create constant lists and sets methods
</xsl:text>
    <xsl:call-template name="create-lists-and-sets-methods"/>
    <xsl:text>

    // Add items to constant lists and sets methods
</xsl:text>
    <xsl:call-template name="add-list-or-set-items-methods"/>
    <xsl:text>

    /**
        Adds the specified item to the specified collection.

        @param c the collection to add 'item' to
        @param item the item to add to 'c'
    */
    private static void addItem(Collection c, String item)
    {
        Assert.require(c != null);
        Assert.require(item != null);

        c.add(item.intern());
    }
}
</xsl:text>
    </xsl:template>


    <!-- Outputs the definition of the matched constant. -->
    <xsl:template match="constant" mode="constant-def">
        <xsl:text>
    /**
        </xsl:text>
        <xsl:call-template name="capitalize">
            <xsl:with-param name="str" select="@description"/>
        </xsl:call-template>
        <xsl:text>.
    */
    public static final String
        </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> = &quot;</xsl:text>
        <xsl:value-of select="@value"/>
        <xsl:text>&quot;;
</xsl:text>
    </xsl:template>

    <!-- Outputs the definitions of the List and Set constants that contain
         the items in each of the constant-lists.

         Note: this template assumes that the Java constants that represent
         the individual constants have been defined before this code is
         reached. -->
    <xsl:template name="constant-list-and-set-defs">
        <xsl:for-each select="$constant-lists">
            <xsl:call-template name="constant-list-defs">
                <xsl:with-param name="list-name" select="@name"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the definitions of List and Set constants that contain
         the items in the constant-list with the specified name. -->
    <xsl:template name="constant-list-defs">
        <xsl:param name="list-name"/>

        <!--
            NOTE: the implementations of the methods that create the Sets
            rely on the fact that the corresponding Lists are created
            before they are called. (So don't change the order that the
            Lists and Sets are defined here.)
        -->

        <xsl:text>
    /**
        The list that contains all of the items in the </xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>
        constant list.
    */
    public static final List
        </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name"
                select="concat($list-name, 'List')"/>
        </xsl:call-template>
        <xsl:text> =
            create</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>List();

    /**
        The set that contains all of the &lt;em&gt;unique&lt;/em&gt; items in the
</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text> constant list.
    */
    public static final Set
        </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name"
                select="concat($list-name, 'Set')"/>
        </xsl:call-template>
        <xsl:text> =
            create</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>Set();
</xsl:text>
    </xsl:template>

    <!-- Outputs the methods that create the Sets and Lists that contain
         the items in each constant-list. -->
    <xsl:template name="create-lists-and-sets-methods">
        <xsl:for-each select="$constant-lists">
            <xsl:call-template name="create-list-and-set-methods">
                <xsl:with-param name="list-name" select="@name"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the methods that create the Set and List that contains
         the items in the constant-list with the specified name. -->
    <xsl:template name="create-list-and-set-methods">
        <xsl:param name="list-name"/>

        <xsl:text>
    /**
        @return a new List containing all of the items in the constant list
        named </xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>
    */
    private static List create</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>List()
    {
        List result = new ArrayList();

        add</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>Items(result);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a new Set containing all of the &lt;em&gt;unique&lt;/em&gt; items
        in the constant list named </xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>
    */
    private static Set create</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>Set()
    {
        Set result = new HashSet(</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="concat($list-name, 'List')"/>
        </xsl:call-template>
        <xsl:text>);

        Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    </xsl:template>

    <!-- Outputs the methods that add all of the items in a constant-list to
         a Set or List. -->
    <xsl:template name="add-list-or-set-items-methods">
        <xsl:for-each select="$constant-lists">
            <xsl:call-template name="add-list-items-method">
                <xsl:with-param name="list-name" select="@name"/>
            </xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the method that adds all of the items in the constant-list
         with the specified name to a List or Set. -->
    <xsl:template name="add-list-items-method">
        <xsl:param name="list-name"/>

        <xsl:variable name="list"
            select="$constant-lists[@name=$list-name]"/>

        <xsl:text>
    /**
        Adds to the specified collection all of the items in the constant
        list named </xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>.

        @param c the collection to add the constant list items to
    */
    private static void add</xsl:text>
        <xsl:value-of select="$list-name"/>
        <xsl:text>Items(Collection c)
    {
        Assert.require(c != null);
</xsl:text>
        <xsl:apply-templates select="$constant-lists[@name=$list-name]/*"
            mode="add-list-items"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="constant" mode="add-list-items">
        <xsl:variable name="name" select="@name"/>

        <xsl:if test="count($constants[@name=$name]) = 0">
            <xsl:message terminate="yes">
There is no constant named <xsl:value-of select="$name"/>, so it cannot be added to a list of constants.
            </xsl:message>

            <!-- The above xsl:message call doesn't seem to terminate compilation,
                 so we output invalid Java code too. -->
            <xsl:text>
ERROR: THERE IS NO CONSTANT NAMED </xsl:text>
            <xsl:value-of select="$name"/>
            <xsl:text>!!!!
</xsl:text>
        </xsl:if>

        <!-- This just makes the constant's definition current: this should
             always iterate exactly once. -->
        <xsl:for-each select="$constants[@name=$name]">
            <xsl:text>
        addItem(c, &quot;</xsl:text>
            <xsl:value-of select="@value"/>
            <xsl:text>&quot;);</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="constant-list" mode="add-list-items">
        <xsl:variable name="name" select="@name"/>

        <xsl:text>
        add</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Items(c);</xsl:text>
    </xsl:template>
</xsl:transform>
