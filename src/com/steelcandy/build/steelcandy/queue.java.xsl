<?xml version="1.0"?>
<!--
    $Id: queue.java.xsl,v 1.6 2004/05/21 15:56:05 jgm Exp $

    Transforms the information about a typed queue into the source
    code for the Java interface that represents the queue.

    Author: James MacKay
    Last Updated: $Date: 2004/05/21 15:56:05 $

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
        select="$root/queues/queue[@item-name=$item-class-name]"/>


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
import com.steelcandy.common.containers.TypedQueue;
import com.steelcandy.common.NoSuchItemException;

/**
    Represents a queue of </xsl:text>
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
    extends TypedQueue
{
    // Constructors

    /**
        Constructs an empty queue of </xsl:text>
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
        Adds the specified </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to this queue at its tail.

        @param item the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> to enqueue
    */
    public void enqueue(</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> item)
    {
        enqueueObject(item);

        Assert.ensure(isEmpty() == false);
    }

    /**
        Removes the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> from the head of this queue and
        returns it.

        @return the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> removed from the head of this queue
        @exception NoSuchItemException thrown if the queue is empty
    */
    protected </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> dequeue()
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) dequeueObject();
    }

    /**
        Returns the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> at the head of this queue without
        removing it.

        @return the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> at the head of this queue
        @exception NoSuchItemException thrown if this queue is empty
    */
    protected </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> peek()
        throws NoSuchItemException
    {
        return (</xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>) peekObject();
    }

    /**
        Returns an iterator over all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in
        this queue, in order. The items are dequeued as they are removed
        from the iterator.

        @return an iterator over all of the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in this
        queue, in order
        @see </xsl:text>
        <xsl:value-of select="concat($class-name, '.', $item-name)"/>
        <xsl:text>QueueIterator
    */
    public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator iterator()
    {
        return new </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>QueueIterator(this);
    }


    // Inner classes

    /**
        Iterates through the </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items in a </xsl:text>
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
        <xsl:text>QueueIterator
        extends TypedQueueIterator
        implements </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>Iterator
    {
        // Constructors

        /**
            Constructs a(n) </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>QueueIterator from the queue
            whose </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text> items it is to iterate over.

            @param queue the </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> that the iterator is to
            iterate over the elements of
        */
        public </xsl:text>
        <xsl:value-of select="$item-name"/>
        <xsl:text>QueueIterator(</xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> queue)
        {
            super(queue);
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
