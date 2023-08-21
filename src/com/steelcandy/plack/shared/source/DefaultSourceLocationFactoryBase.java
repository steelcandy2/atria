/*
 Copyright (C) 2005-2008 by James MacKay.

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


package com.steelcandy.plack.shared.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.AbstractSourceLocationFactory;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;  // javadocs only
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.source.SourcePosition;

/**
    A base class for the singleton default source location factory.

    @author James MacKay
    @see SourceLocation
*/
public class DefaultSourceLocationFactoryBase
    extends AbstractSourceLocationFactory
{
    // Factory methods

    /**
        @see SourceLocationFactory#create(SourcePosition, int)
    */
    public SourceLocation create(SourcePosition startPos, int length)
    {
        Assert.require(startPos != null);
        Assert.require(length >= 0);

        // Assert.ensure(result != null);
        return new SingleLineContinuousSourceLocation(startPos, length);
    }

    /**
        @see SourceLocationFactory#create(SourcePosition, SourcePosition)
    */
    public SourceLocation create(SourcePosition startPos,
                                 SourcePosition pastEndPos)
    {
        Assert.require(startPos != null);
        Assert.require(pastEndPos != null);

        SourceLocation result;
        if (startPos.lineNumber() == pastEndPos.lineNumber())
        {
            int length = pastEndPos.offset() - startPos.offset();
            Assert.check(length >= 0);
            result = new SingleLineContinuousSourceLocation(startPos, length);
        }
        else
        {
            result = new MultipleLineContinuousSourceLocation(startPos,
                                                              pastEndPos);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        This default implementation of this method assumes that all of the
        parts of a language construct are contiguous: thus this
        implementation returns the continuous location spanning from the
        start of the first sublocation to the end of the last location,
        inclusive.

        @see SourceLocationFactory#create(SourceLocationList)
    */
    public SourceLocation create(SourceLocationList sublocations)
    {
        Assert.require(sublocations != null);

        SourceLocation result = null;

        SourceLocationList nonNull = nonNullLocations(sublocations);
        int sz = nonNull.size();
        if (sz > 1)
        {
            result = create(nonNull.get(0), nonNull.getLast());
        }
        else if (sz == 1)
        {
            result = nonNull.get(0);
        }

        // 'result' may be null
        return result;
    }
}
