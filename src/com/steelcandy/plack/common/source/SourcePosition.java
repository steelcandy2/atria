/*
 Copyright (C) 2001-2004 by James MacKay.

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

/**
    Represents the position of a single character in a piece of source code.

    @author James MacKay
    @version $Revision: 1.4 $
*/
public class SourcePosition
{
    // Private fields

    /**
        The line number of the line that the character is on. The first line
        in a piece of source code is line 1.
    */
    private int _lineNumber;

    /**
        The number of characters the character is away from the first
        character in its line. Thus the first character of a line is at
        offset 0.
    */
    private int _offset;


    // Constructors

    /**
        Constructs a character's position in a piece of source code from the
        line number of the line it is on and the number of characters it is
        away from being the first character in its line.

        @param lineNumber the line number of the line the character is on
        @param offset the number of character the character is away from
        being the first character in its line
    */
    public SourcePosition(int lineNumber, int offset)
    {
        Assert.require(lineNumber >= 1);
        Assert.require(offset >= 0);

        _lineNumber = lineNumber;
        _offset = offset;
    }


    // Public methods

    /**
        @return the line number part of this position
    */
    public int lineNumber()
    {
        Assert.ensure(_lineNumber >= 1);
        return _lineNumber;
    }

    /**
        @return the offset part of this position
    */
    public int offset()
    {
        Assert.ensure(_offset >= 0);
        return _offset;
    }


    /**
        Compares this position against the specified one, and returns -1, 0
        or 1 depending on whether this position is less than, equal to or
        greater than the specified position.
        <p>
        Two positions are equal if they have the same line numbers and
        offsets. If two unequal positions have the same line number then the
        one with the larger offset is the greater of the two: otherwise the
        one with the larger line number is the greater of the two.

        @param pos the position to compare to this one
        @return 1 if this position is greater than the other one, -1 if this
        position is less than the other one, and 0 if this position is equal
        to the other one
        @see #equals
    */
    public int compare(SourcePosition pos)
    {
        Assert.require(pos != null);

        int result = 0;

        int line = _lineNumber;
        int posLine = pos._lineNumber;
        if (line < posLine)
        {
            result = -1;
        }
        else if (line > posLine)
        {
            result = 1;
        }
        else
        {
            int off = _offset;
            int posOff = pos._offset;
            if (off < posOff)
            {
                result = -1;
            }
            else if (off > posOff)
            {
                result = 1;
            }
        }

        Assert.ensure(result != 0 || this.equals(pos));
            // i.e. (result == 0) => this.equals(pos)
        return result;
    }


    // Overridden Object methods

    /**
        @see Object#equals
    */
    public boolean equals(Object obj)
    {
        boolean result = false;
        if (obj != null && obj instanceof SourcePosition)
        {
            SourcePosition pos = (SourcePosition) obj;
            result = _lineNumber == pos._lineNumber &&
                        _offset == pos._offset;
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
        // TODO: Use a better algorithm here ???!!!???
        return _lineNumber + _offset;
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("line number = ");
        buf.append(_lineNumber).append(", offset = ").append(_offset);
        return buf.toString();
    }
}
