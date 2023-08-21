/*
 Copyright (C) 2002-2008 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

/**
    A class that specifies the configuration of a ConstructWriter.

    @author James MacKay
    @version $Revision: 1.3 $
    @see ConstructWriter
*/
public class ConstructWriterConfiguration
    implements ConstructWriterOptions
{
    // Show option constants

    /** Indicates that a part of a construct should never be shown. */
    public static final ShowOption HIDE = new DefaultShowOption();

    /**
        Indicates that a part of a construct should be shown if it is
        present.
    */
    public static final ShowOption SHOW = new DefaultShowOption();

    /**
        Indicates that a part of a construct should be shown, even if it
        isn't present.
    */
    public static final ShowOption ALWAYS_SHOW = new DefaultShowOption();


    // Private fields

    /** The way to show construct ID values. */
    private ShowOption _showIdValue = SHOW;

    /** The way to show construct ID names. */
    private ShowOption _showIdName = SHOW;

    /** The way to show construct names. */
    private ShowOption _showName = SHOW;

    /** The way to show construct descriptions. */
    private ShowOption _showDescription = SHOW;

    /** The way to show construct values. */
    private ShowOption _showValue = SHOW;

    /** The way to show construct locations. */
    private ShowOption _showLocation = SHOW;

    /** The way to show the source code a construct represents. */
    private ShowOption _showSourceCode = SHOW;

    /** The way to show construct flags. */
    private ShowOption _showFlags = SHOW;

    /** The way to show subconstructs. */
    private ShowOption _showSubconstructs = SHOW;


    // Constructors

    /**
        Constructs a ConstructWriterConfiguration that initially specifies
        that everything be shown.
    */
    public ConstructWriterConfiguration()
    {
        // empty
    }


    // Get/set options methods

    /**
        @return how the value of a construct's ID is to be shown
    */
    public ShowOption showIdValue()
    {
        return _showIdValue;
    }

    /**
        Sets how the value of a construct's ID is shown to the way indicated
        by the specified show option: it is shown unless the show option is
        HIDE.

        @param opt the show option that indicates how the value of
        constructs' IDs are to be shown
    */
    public void setShowIdValue(ShowOption opt)
    {
        Assert.require(opt != null);

        _showIdValue = opt;
    }

    /**
        @return how the name of a construct's ID is to be shown
    */
    public ShowOption showIdName()
    {
        return _showIdName;
    }

    /**
        Sets how the name of a construct's ID is shown to the way indicated
        by the specified show option:
        <ul>
            <li>the HIDE option causes the name of a construct's ID to never
                be shown
            <li>the SHOW option causes the name of a construct's ID to be
                shown iff it is available
            <li>the ALWAYS_SHOW option causes the name of a construct's ID to
                always be shown
        </ul>

        @param opt the show option that indicates how the name of constructs'
        IDs are to be shown
    */
    public void setShowIdName(ShowOption opt)
    {
        Assert.require(opt != null);

        _showIdName = opt;
    }

    /**
        @return how a construct's name is to be shown
    */
    public ShowOption showName()
    {
        return _showName;
    }

    /**
        Sets how a construct's name is shown to the way indicated by the
        specified show option:
        <ul>
            <li>the HIDE option causes a construct's name to never be shown
            <li>the SHOW option causes a construct's name to be shown iff it
                is available
            <li>the ALWAYS_SHOW option causes a construct's name to always be
                shown
        </ul>

        @param opt the show option that indicates how constructs' names are
        to be shown
    */
    public void setShowName(ShowOption opt)
    {
        Assert.require(opt != null);

        _showName = opt;
    }

    /**
        @return how a construct's description is to be shown
    */
    public ShowOption showDescription()
    {
        return _showDescription;
    }

    /**
        Sets how a construct's description is shown to the way indicated by
        the specified show option:
        <ul>
            <li>the HIDE option causes a construct's description to never be
                shown
            <li>the SHOW option causes a construct's description to be shown
                iff it is available
            <li>the ALWAYS_SHOW option causes a construct's description to
                always be shown
        </ul>

        @param opt the show option that indicates how constructs'
        descriptions are to be shown
    */
    public void setShowDescription(ShowOption opt)
    {
        Assert.require(opt != null);

        _showDescription = opt;
    }

    /**
        @return how a construct's value is to be shown
    */
    public ShowOption showValue()
    {
        return _showValue;
    }

    /**
        Sets how a construct's value is shown to the way indicated by the
        specified show option:
        <ul>
            <li>the HIDE option causes a construct's value to never be shown
            <li>the SHOW option causes a construct's value to be shown iff it
                is available
            <li>the ALWAYS_SHOW option causes a construct's value to always
                be shown
        </ul>

        @param opt the show option that indicates how constructs' values are
        to be shown
    */
    public void setShowValue(ShowOption opt)
    {
        Assert.require(opt != null);

        _showValue = opt;
    }

    /**
        @return how a construct's location is to be shown
    */
    public ShowOption showLocation()
    {
        return _showLocation;
    }

    /**
        Sets how a construct's location is shown to the way indicated by the
        specified show option:
        <ul>
            <li>the HIDE option causes a construct's location to never be
                shown
            <li>the SHOW option causes a construct's location tp be shown
                iff it is available
            <li>the ALWAYS_SHOW option causes a construct's location to
                always be shown
        </ul>

        @param opt the show option that indicates how constructs' locations
        are to be shown
    */
    public void setShowLocation(ShowOption opt)
    {
        Assert.require(opt != null);

        _showLocation = opt;
    }

    /**
        @return how the source code fragment that a construct represents is
        to be shown
    */
    public ShowOption showSourceCode()
    {
        return _showSourceCode;
    }

    /**
        Sets how the source code fragment that a construct represents is
        shown to the way indicated by the specified show option:
        <ul>
            <li>the HIDE option causes a construct's source code to never be
                shown
            <li>the SHOW option causes a construct's source code to be shown
                iff it is available
            <li>the ALWAYS_SHOW option causes a construct's source code to
                always be shown
        </ul>

        @param opt the show option that indicates how constructs' source code
        is to be shown
    */
    public void setShowSourceCode(ShowOption opt)
    {
        Assert.require(opt != null);

        _showSourceCode = opt;
    }

    /**
        @return how a construct's flags are to be shown
    */
    public ShowOption showFlags()
    {
        return _showFlags;
    }

    /**
        Sets how constructs' flags are shown to the way indicated by the
        specified show option:
        <ul>
            <li>the HIDE option causes a construct's flags to never be shown
            <li>the SHOW option causes a construct's flags to be shown only
                if the construct has at least one flag set
            <li>the ALWAYS_SHOW option causes a construct's flags to always
                be shown if it can have any, regardless of whether it has any
                set
        </ul>
        Note that a construct that cannot have any flags set will never have
        its flags shown (even if the ALWAYS_SHOW option is specified).

        @param opt the show option that indicates how constructs' flags are
        to be shown
    */
    public void setShowFlags(ShowOption opt)
    {
        Assert.require(opt != null);

        _showFlags = opt;
    }

    /**
        @return how a construct's subconstructs are to be shown
    */
    public ShowOption showSubconstructs()
    {
        return _showSubconstructs;
    }

    /**
        Sets how constructs' subconstructs are shown to the way indicated by
        the specified show option:
        <ul>
            <li>the HIDE option causes a construct's subconstructs to never
                be shown
            <li>the SHOW option causes a construct's optional subconstructs
                to be shown only if they're pressent
            <li>the ALWAYS_SHOW option causes all of a construct's
                subconstructs to be shown, even those that aren't present
        </ul>
        Note that unless the HIDE option is specified a construct's missing
        required subconstructs will be shown.

        @param opt the show option that indicates how constructs'
        subconstructs are to be shown
    */
    public void setShowSubconstructs(ShowOption opt)
    {
        Assert.require(opt != null);

        _showSubconstructs = opt;
    }


    // Inner classes

    /**
        The concrete type of all ShowOptions.
    */
    private static class DefaultShowOption
        implements ConstructWriterOptions.ShowOption
    {
        // empty
    }
}
