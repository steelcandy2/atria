<?xml version="1.0"?>
<!--
    Transforms the information about a typed iterator into the source
    code for the Java interface that represents the iterator.

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
        select="$root/iterators/iterator[@item-name=$item-class-name]"/>


    <!-- Main templates -->

    <xsl:template match="generic-classes">
        <xsl:text>// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$module-name"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;
</xsl:text>
        <xsl:value-of select="$item-import"/>
        <xsl:text>
import com.steelcandy.common.NoSuchItemException;

/**
    The interface implemented by all iterators over </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
    items.
    &lt;p&gt;
    Unlike java.util.Iterators this class does not provide the ability to
    remove elements.
    &lt;p&gt;
    The &lt;code&gt;peek()&lt;/code&gt; and &lt;code&gt;next()&lt;/code&gt; methods throw a
    NoSuchItemException when and only when &lt;code&gt;hasNext()&lt;/code&gt; returns false.
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
    // Public methods

    /**
        @return true iff this iterator will return at least one more
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
    */
    public boolean hasNext();

    /**
        @return the next </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        @exception NoSuchItemException thrown iff there isn't a next
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        @see #peek
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> next()
        throws NoSuchItemException;

    /**
        Note: this method can be called before &lt;code&gt;next()&lt;/code&gt; has
        ever been called.

        @return the next </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> without consuming it
        (so the next call to peek() or next() will return the same
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>)
        @exception NoSuchItemException thrown iff there isn't a next
        </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        @see #next
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> peek()
        throws NoSuchItemException;
}
</xsl:text>
    </xsl:template>
</xsl:transform>
