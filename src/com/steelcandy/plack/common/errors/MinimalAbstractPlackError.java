/*
 Copyright (C) 2004-2008 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.common.Resources;

/**
    A minimal abstract base class for PlackErrors.

    @author James MacKay
    @version $Revision: 1.1 $
    @see AbstractKeyedPlackError
*/
public abstract class MinimalAbstractPlackError
    implements PlackError
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonErrorResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DEBUG_LEVEL_NAME =
            "DEBUG_LEVEL_NAME";
    private static final String
        INFO_LEVEL_NAME =
            "INFO_LEVEL_NAME";
    private static final String
        NOTICE_LEVEL_NAME =
            "NOTICE_LEVEL_NAME";
    private static final String
        WARNING_LEVEL_NAME =
            "WARNING_LEVEL_NAME";
    private static final String
        NON_FATAL_ERROR_LEVEL_NAME =
            "NON_FATAL_ERROR_LEVEL_NAME";
    private static final String
        FATAL_ERROR_LEVEL_NAME =
            "FATAL_ERROR_LEVEL_NAME";


    /**
        The array containing the names of the various error severity levels.
    */
    private static final String[] _levelNames = createLevelNamesArray();


    // Constructors

    /**
        Constructs a MinimalAbstractPlackError.
    */
    public MinimalAbstractPlackError()
    {
        // empty
    }


    // Public static methods

    /**
        @param level an error severity level
        @return the name of the specified error severity level
    */
    public static String levelName(int level)
    {
        Assert.check(level >= 0);
        Assert.check(level < _levelNames.length);

        return _levelNames[level];
    }


    // Public methods

    /**
        @see PlackError#key
    */
    public ErrorKey key()
    {
        // 'result' may be null
        return null;
    }

    /**
        @see PlackError#equalKeys(PlackError)
    */
    public boolean equalKeys(PlackError err)
    {
        Assert.require(err != null);

        ErrorKey otherKey = err.key();
        boolean result = (key() != null) && (otherKey != null) &&
            key().equals(otherKey);

        Assert.ensure(key() != null || result == false);
        Assert.ensure(err.key() != null || result == false);
        return result;
    }

    /**
        @see PlackError#levelName
    */
    public String levelName()
    {
        String result = levelName(level());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see PlackError#isFatal
    */
    public boolean isFatal()
    {
        return (level() == FATAL_ERROR_LEVEL);
    }


    // Private static methods

    /**
        Creates and returns an array of error severity level names, indexed
        by error severity level.

        @return an array of the severity level names, indexed by the severity
        levels
    */
    private static String[] createLevelNamesArray()
    {
        String[] result = new String[MAXIMUM_ERROR_SEVERITY_LEVEL + 1];

        result[DEBUG_LEVEL] =
            _resources.getString(DEBUG_LEVEL_NAME);
        result[INFO_LEVEL] =
            _resources.getString(INFO_LEVEL_NAME);
        result[NOTICE_LEVEL] =
            _resources.getString(NOTICE_LEVEL_NAME);
        result[WARNING_LEVEL] =
            _resources.getString(WARNING_LEVEL_NAME);
        result[NON_FATAL_ERROR_LEVEL] =
            _resources.getString(NON_FATAL_ERROR_LEVEL_NAME);
        result[FATAL_ERROR_LEVEL] =
            _resources.getString(FATAL_ERROR_LEVEL_NAME);

        return result;
    }
}
