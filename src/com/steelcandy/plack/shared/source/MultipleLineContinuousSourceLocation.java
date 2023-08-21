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
    source file where the source fragment can span more than one line.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class MultipleLineContinuousSourceLocation
    extends ContinuousSourceLocation
{
    // Private fields

    /** The position after the last character in the fragment. */
    private SourcePosition _pastEndPos;


    // Constructors

    /**
        Constructs a MultipleLineContinuousSourceLocation from the position
        of the fragment's first character and the position after the
        fragment's last character.

        @param startPos the position of the fragment's first character
        @param pastEndPos the position after that of the fragment's last
        character
    */
    public MultipleLineContinuousSourceLocation(SourcePosition startPos,
                                                SourcePosition pastEndPos)
    {
        super(startPos);
        Assert.require(pastEndPos != null);

        _pastEndPos = pastEndPos;
    }


    // Public methods

    /**
        @see SourceLocation#pastEndPosition
    */
    public SourcePosition pastEndPosition()
    {
        Assert.ensure(_pastEndPos != null);
        return _pastEndPos;
    }

    /**
        @see SourceLocation#endLineNumber
    */
    public int endLineNumber()
    {
        int result = _pastEndPos.lineNumber();

        Assert.ensure(result > 0);
        return result;
    }

    /**
        @see SourceLocation#pastEndOffset
    */
    public int pastEndOffset()
    {
        int result = _pastEndPos.offset();

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
            source.lines(startLineNumber(), startOffset(),
                         endLineNumber(), pastEndOffset());

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
        buf.append(", ").append(_pastEndPos.toString());
        return buf.toString();
    }
}
