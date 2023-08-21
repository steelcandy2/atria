<?xml version="1.0"?>
<!--
    Transforms the information about a typed stack into the source
    code for the Java interface that represents the stack.

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
        select="$root/stacks/stack[@item-name=$item-class-name]"/>


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
import com.steelcandy.common.containers.TypedStack;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a stack of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends TypedStack
{
    // Constructors

    /**
        Constructs an empty stack of </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items.
    */
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        Pushes the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> onto the top of this
        stack.

        @param item the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to push onto this stack
    */
    public void push(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
    {
        pushObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Pops the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> from the top of this stack and
        returns it.

        @return the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> popped from the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> pop()
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) popObject();
    }

    /**
        Returns the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> at the top of this stack, without
        removing it.

        @return the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> at the top of this stack
        @exception NoSuchItemException thrown if the stack is empty
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> top()
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) topObject();
    }

    /**
        Returns an iterator over all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in
        this stack, in order from the top of the stack to the bottom. The
        items are popped as they are removed from the iterator.

        @return an iterator over all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in
        this stack, in order
        @see </xsl:text>
        <xsl:value-of select="concat($class-name, '.', $item-name)"/>
        <xsl:text>StackIterator
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator iterator()
    {
        return new </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>StackIterator(this);
    }


    // Inner classes

    /**
        Iterates through the items in a(n) </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>.

        @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>
        @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    */
    protected static class </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>StackIterator
        extends TypedStackIterator
        implements </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator
    {
        // Constructors

        /**
            Constructs a(n) </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>StackIterator from the
            </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> whose items it is to iterate over.

            @param stack the </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> that the iterator
            is to iterate over the elements of
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>StackIterator(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> stack)
        {
            super(stack);
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#next
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> next()
        {
            return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) nextObject();
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator#peek
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> peek()
        {
            return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) peekObject();
        }
    }
}
</xsl:text>
    </xsl:template>
</xsl:transform>
