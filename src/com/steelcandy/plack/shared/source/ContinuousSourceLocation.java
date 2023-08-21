/*
 Copyright (C) 2003-2005 by James MacKay.

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

import com.steelcandy.plack.common.source.AbstractSourceLocation;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.ints.ConsecutiveIntIterator;
import com.steelcandy.common.ints.IntIterator;

/**
    The abstract base class for location classes that represent the location
    of a continuous fragment of source code in a source file. The fragment
    may be empty (i.e. length zero).
    <p>
    In addition to implementing the unimplemented abstract methods,
    subclasses should also override <code>toString()</code>.

    @author James MacKay
*/
public abstract class ContinuousSourceLocation
    extends AbstractSourceLocation
{
    // Private fields

    /** The position of the first character in the fragment. */
    private SourcePosition _startPos;


    // Constructors

    /**
        Constructs a ContinuousSourceLocation from the line number of the
        line that the fragment starts on and the offset of the fragment's
        first character from the start of that line.

        @param lineNumber the line number of the line the fragment starts on
        @param offset the offset of the start of the fragment, relative to
        the beginning of the line the fragment starts on
    */
    public ContinuousSourceLocation(int lineNumber, int offset)
    {
        this(new SourcePosition(lineNumber, offset));
    }

    /**
        Constructs a ContinuousSourceLocation from the position of the
        fragment's first character.

        @param startPos the position of the first character in the fragment
    */
    public ContinuousSourceLocation(SourcePosition startPos)
    {
        Assert.require(startPos != null);

        _startPos = startPos;
    }


    // Public methods

    /**
        @see SourceLocation#startPosition
    */
    public SourcePosition startPosition()
    {
        Assert.ensure(_startPos != null);
        return _startPos;
    }

    /**
        @see SourceLocation#startLineNumber
    */
    public int startLineNumber()
    {
        int result = _startPos.lineNumber();

        Assert.ensure(result > 0);
        return result;
    }

    /**
        @see SourceLocation#lineNumbers
    */
    public IntIterator lineNumbers()
    {
        IntIterator result =
            new ConsecutiveIntIterator(startLineNumber(), endLineNumber());

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SourceLocation#startOffset
    */
    public int startOffset()
    {
        int result = _startPos.offset();

        Assert.ensure(result >= 0);
        return result;
    }


    // Overridden Object methods

    /**
        @see Object#equals
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof ContinuousSourceLocation)
        {
            ContinuousSourceLocation loc = (ContinuousSourceLocation) obj;
            result = startPosition().equals(loc.startPosition()) &&
                pastEndPosition().equals(loc.pastEndPosition());
        }

        return result;
    }

    /**
        We have to override this method so that objects that are equal
        according to equals() have the same hash code.

        @see Object#hashCode
        @see Object#equals
    */
    public int hashCode()
    {
        return startPosition().hashCode() ^ pastEndPosition().hashCode();
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        return classNameToString() + ": " + startPosition().toString();
    }
}
