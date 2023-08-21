/*
 Copyright (C) 2003-2004 by James MacKay.

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

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;  // javadocs only
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.common.NoSuchItemException;

import java.io.IOException;

/**
    Describes the location of a continuous fragment of source code in a
    source file where the source fragment doesn't span more than one line.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class SingleLineContinuousSourceLocation
    extends ContinuousSourceLocation
{
    // Private fields

    /** The length of (i.e. number of characters in) the fragment. */
    private int _length;


    // Constructors

    /**
        Constructs a SingleLineContinuousSourceLocation from the line number
        of the line that the fragment is on, the offset of the fragment's
        first character from the start of that line, and the length of the
        fragment in characters.

        @param lineNumber the line number of the line the fragment is on
        @param offset the offset of the start of the fragment, relative to
        the beginning of the line the fragment is on
        @param length the length of the fragment, in characters
    */
    public SingleLineContinuousSourceLocation(int lineNumber,
                                              int offset, int length)
    {
        super(lineNumber, offset);
        Assert.require(length >= 0);

        _length = length;
    }

    /**
        Constructs a SingleLineContinuousSourceLocation from the position of
        the fragment's first character and the length of the fragment in
        characters.

        @param startPos the position of the first character in the fragment
        @param length the length of the fragment, in characters
    */
    public SingleLineContinuousSourceLocation(SourcePosition startPos,
                                              int length)
    {
        super(startPos);
        Assert.require(length >= 0);

        _length = length;
    }


    // Public methods

    /**
        @see SourceLocation#pastEndPosition
    */
    public SourcePosition pastEndPosition()
    {
        // Assert.ensure(result != null);
        return new SourcePosition(endLineNumber(), pastEndOffset());
    }

    /**
        @see SourceLocation#endLineNumber
    */
    public int endLineNumber()
    {
        // Since the location begins and ends on the same line ...
        int result = startLineNumber();

        Assert.ensure(result > 0);
        return result;
    }

    /**
        @see SourceLocation#pastEndOffset
    */
    public int pastEndOffset()
    {
        int result = startOffset() + _length;

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @see SourceLocation#fragmentOf(SourceCode)
    */
    public String fragmentOf(SourceCode source)
        throws IOException, IndexOutOfBoundsException
    {
        Assert.require(source != null);

        String result =
            source.lineFragment(startLineNumber(),
                                startOffset(), pastEndOffset());

        Assert.ensure(result != null);
        return result;
    }


    // Overridden Object methods

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer(super.toString());
        buf.append(", length = ").append(_length);
        return buf.toString();
    }
}
