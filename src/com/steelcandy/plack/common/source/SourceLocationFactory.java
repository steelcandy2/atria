/*
 Copyright (C) 2001-2005 by James MacKay.

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

package com.steelcandy.plack.common.source;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.shared.source.DefaultSourceLocationFactory;  // javadocs only

/**
    The interface implemented by all factory classes that create
    SourceLocations.

    @author James MacKay
    @see SourceLocation
    @see DefaultSourceLocationFactory
*/
public interface SourceLocationFactory
{
    // Factory methods

    /**
        Creates and returns a SourceLocation that represents the location of
        a continuous source code fragment
        <ul>
            <li>whose first character is at the specified offset in the line
                with the specified line number, and
            <li>that consists of <code>length</code> characters.
        </ul>
        The fragment cannot span more than one line.

        @param lineNumber the line number of the line the fragment starts on
        @param offset the offset of the start of the fragment, relative to
        the beginning of the line the fragment starts on
        @param length the length of the fragment, in characters
        @return the location of the continuous source code fragment
    */
    public SourceLocation create(int lineNumber, int offset, int length);
        // Assert.require(lineNumber >= 1);
        // Assert.require(offset >= 0);
        // Assert.require(length >= 0);
        // Assert.ensure(result != null);

    /**
        Creates and returns a SourceLocation that represents the location of
        a continuous source code fragment
        <ul>
            <li>whose first character is at the specified position, and
            <li>that consists of <code>length</code> characters.
        </ul>
        The fragment cannot span more than one line.

        @param startPos the position of the first character in the fragment
        @param length the length of the fragment, in characters
        @return the location of the continuous source code fragment
    */
    public SourceLocation create(SourcePosition startPos, int length);
        // Assert.require(startPos != null);
        // Assert.require(length >= 0);
        // Assert.ensure(result != null);

    /**
        Creates and returns a SourceLocation that represents the location of
        a continuous source fragment whose first character is at the
        specified starting position, and whose last character is just before
        the specified past end position. The first and last characters do not
        have to be on the same line.

        @param startPos the position of the first character in the fragment
        @param pastEndPos the position just after the last character in the
        fragment
        @return the location of the continuous source fragment
    */
    public SourceLocation create(SourcePosition startPos,
                                 SourcePosition pastEndPos);
        // Assert.require(startPos != null);
        // Assert.require(pastEndPos != null);
        // Assert.ensure(result != null);

    /**
        Creates and returns a SourceLocation that represents the location of
        a continuous source fragment whose first character is the first
        character of the specified starting location, and whose last
        character is the last character of the specified ending location. The
        first and last characters do not have to be on the same line.

        @param startLoc the location whose first character is the first
        character in the fragment
        @param endLoc the location whose last character is the last character
        in the fragment
        @return the location extending from the start of 'startLoc' to the
        end of 'endLoc'
    */
    public SourceLocation create(SourceLocation startLoc,
                                 SourceLocation endLoc);
        // Assert.require(startLoc != null);
        // Assert.require(endLoc != null);
        // Assert.ensure(result != null);

    /**
        Creates and returns a source location that essentially represents all
        of the specified source locations. The sublocations are assumed to be
        the locations of all of the parts of a single language construct, in
        the order that the constructs appear in the source code.
        <p>
        Elements of the list may be null. This method will return null iff
        the list is empty or all of its elements are null.

        @param sublocations the list of locations that the
    */
    public SourceLocation create(SourceLocationList sublocations);
        // Assert.require(sublocations != null);
        // 'result' may be null
}
