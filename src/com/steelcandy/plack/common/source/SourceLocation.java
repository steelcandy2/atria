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

import com.steelcandy.plack.shared.source.ContinuousSourceLocation;  // javadocs only

import com.steelcandy.common.ints.IntIterator;

import java.io.IOException;

/**
    The interface implemented by all classes that describe the location of a
    fragment of source code in a source file. The fragment need not be
    continuous (though the location of a single continuous fragment can be
    described by a <code>ContinuousSourceLocation</code>). A location's
    length may be zero.
    <p>
    Note: instances should override equals() and hashCode() since they may
    be compared with each other (e.g. in determining whether errors are
    duplicates).

    @author James MacKay
    @see ContinuousSourceLocation
*/
public interface SourceLocation
{
    // Public methods

    /**
        @return the position of the first character in the source code
        fragment
    */
    public SourcePosition startPosition();
        // Assert.ensure(result != null);

    /**
        Returns the position after - but on the same line as - the last
        character in the source code fragment.
        <p>
        Note: there may not be a character at the returned position, and it
        may not be a valid position in the source code.

        @return the position of the character after the last character of the
        fragment
    */
    public SourcePosition pastEndPosition();
        // Assert.ensure(result != null);

    /**
        @return the line number of the line containing the first character of
        the fragment
    */
    public int startLineNumber();
        // Assert.ensure(result > 0);

    /**
        @return the line number of the line containing the last character of
        the fragment
    */
    public int endLineNumber();
        // Assert.ensure(result > 0);

    /**
        Returns an iterator over the line numbers of all of the lines that
        the source code fragment has at least one character on. The line
        numbers that the iterator returns will not necessarily be consecutive,
        but will be distinct and returned in ascending order.

        @return an iterator over the line numbers of all of the lines that
        the source code fragment has at least one character on
    */
    public IntIterator lineNumbers();
        // Assert.ensure(result != null);


    /**
        @return the offset, relative to the start of the line, of the first
        character of the source code fragment
    */
    public int startOffset();
        // Assert.ensure(result >= 0);

    /**
        Returns the offset, relative to the start of the line, one past that
        of - but on the same line as - the last character in the fragment.
        <p>
        Note: there may not be a character at the returned offset, and it may
        not be a valid offset into the line of source code.
        <p>
        Note that it is possible that the last character in the fragment is
        on a different line than the first character of the fragment. The
        offset returned by this method is relative to the start of the line
        that the last character of the fragment is on. (Thus endOffset() can
        return a value that is less than offset(), but only if the fragment
        extends over two or more lines.)

        @return the offset of the last character of the fragment
    */
    public int pastEndOffset();


    /**
        Returns the fragment of the specified source code that this
        SourceLocation represents the location of.
        <p>
        Any line termination characters at the end of the fragment will
        <em>not</em> be removed.

        @param source the source code a fragment of which is to be returned
        @return the fragment of the source code whose location is represented
        by this location
        @exception IOException thrown if an I/O exception occurs while trying
        to get the source code fragment
        @exception IndexOutOfBoundsException thrown if the source code is not
        long enough to contain the specified location
    */
    public String fragmentOf(SourceCode source)
        throws IOException, IndexOutOfBoundsException;
        // Assert.require(source != null);
        // Assert.ensure(result != null);


    /**
        Note: the basic components of a location that is made up of
        sublocations consists of the set of all of the sublocations'
        basic components; the basic components of other types of locations
        consists of just the location itself.

        @return the most basic components of this location
    */
    public SourceLocationList basicComponents();
        // Assert.ensure(result != null);
}
