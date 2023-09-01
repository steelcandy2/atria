/*
 Copyright (C) 2005 by James MacKay.

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
*/

package com.steelcandy.plack.atria.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.plack.atria.base.AtriaInfo;

/**
    The default construct manager class.

    @author  James MacKay
*/
public class DefaultAtriaConstructManager
    extends AtriaConstructManagerBase
{
    // Public methods

    /**
        @param c an Atria construct
        @return true iff 'c' is an Element construct
    */
    public boolean isElement(Construct c)
    {
        Assert.require(c != null);

        return (c.id() == AtriaConstructManager.ELEMENT);
    }

    /**
        @param c an Atria construct
        @return true iff 'c' is a Command construct that represents a 'join'
        command that's been given no arguments
    */
    public boolean isZeroArgumentJoinCommand(Construct c)
    {
        Assert.require(c != null);

        boolean result = false;

        if (c.id() == AtriaConstructManager.COMMAND)
        {
            AtriaConstructManager.Command cmd =
                (AtriaConstructManager.Command) c;
            result = (cmd.argumentCount() == 0) &&
                (cmd.name().equals(AtriaInfo.JOIN_COMMAND_NAME));
        }

        return result;
    }

    /**
        @param c an Element construct
        @return true iff 'c' represents the root element of a document
    */
    public boolean isRootElement(AtriaConstructManager.Element c)
    {
        Assert.require(c.depthAttribute().isSet());

        return (c.depthAttribute().value() == 0);
    }

    /**
        @param c an Element construct
        @return the name of the element, or null if it is missing
    */
    public String elementName(Element c)
    {
        Assert.require(c != null);

        String result = null;

        if (c.hasName())
        {
            result = toText(c.name());
        }

        // 'result' may be null
        return result;
    }


    /**
        @param c an Attribute construct
        @return the name of the attribute, or null if it is missing
    */
    public String attributeName(Attribute c)
    {
        Assert.require(c != null);

        String result = null;

        if (c.hasName())
        {
            result = toText(c.name());
        }

        // 'result' may be null
        return result;
    }

    /**
        @param c a command construct
        @return the name of 'c''s command, or null if it is missing
    */
    public String commandName(Command c)
    {
        Assert.require(c != null);

        String result = null;

        if (c.hasName())
        {
            result = toText(c.name());
        }

        // 'result' may be null
        return result;
    }

    /**
        @param name a Name construct
        @return a textual representation of 'name'
    */
    public String toText(Name name)
    {
        Assert.require(name != null);

        String result = null;

        if (name.hasValue())
        {
            result = name.value();
        }

        // 'result' may be null
        return result;
    }

    /**
        @param t a literal text construct
        @return the contents of 'c': the characters between the starting and
        ending double quotation marks, exclusive
    */
    public String contents(Text t)
    {
        Assert.require(t != null);

        String result = null;

        if (t.hasValue())
        {
            result = t.value();
        }

        // 'result' may be null
        return result;
    }
}
