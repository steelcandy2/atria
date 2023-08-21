/*
 Copyright (C) 2003-2006 by James MacKay.

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
import com.steelcandy.plack.common.source.AbstractSourceLocationFactory;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationIterator;
import com.steelcandy.plack.common.source.SourceLocationList;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.ints.IntArrayIterator;
import com.steelcandy.common.ints.IntIterator;
import com.steelcandy.common.text.TextUtilities;

import java.io.IOException;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
    Describes the location of a fragment of source code in a source file that
    consists of one or more subfragments.
    <p>
    Note: this class' equals() method isn't as good as it could be, due to
    the difficulty/complexity of determining equality. For example:
    <ul>
        <li>a CompoundSourceLocation with exactly one sublocation should be
            considered equal to that sublocation (even if that sublocation
            isn't a CompoundSourceLocation)</li>
        <li>two CompoundSourceLocations that represent the same location, but
            that have different sets of sublocations should be considered
            equal</li>
    </ul>
    The current implementation handles the first case properly, but not the
    second case.

    @author James MacKay
    @version $Revision: 1.6 $
    @see ContinuousSourceLocation
*/
public class CompoundSourceLocation
    extends AbstractSourceLocation
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        SharedSourceCodeResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        LINES_BETWEEN_FORMAT = "LINES_BETWEEN_FORMAT";


    // Private fields

    /**
        This location's sublocations. (The sublocations are not necessarily
        continuous).
    */
    SourceLocationList _sublocations;


    // Constructors

    /**
        Constructs a CompoundSourceLocation from its sublocations. The
        sublocations are not necessarily continuous locations. The following
        conditions must hold:
        <ul>
            <li>the sublocations are in order, and</li>
            <li>none of the sublocations overlap.</li>
        </ul>

        @see ContinuousSourceLocation
    */
    public CompoundSourceLocation(SourceLocationList sublocations)
    {
        Assert.require(sublocations != null);
        Assert.require(sublocations.isEmpty() == false);
        Assert.require(AbstractSourceLocationFactory.
                            areOrderedAndDisjoint(sublocations));

        _sublocations = sublocations;
    }


    // Public methods

    /**
        @see SourceLocation#startPosition
    */
    public SourcePosition startPosition()
    {
        Assert.check(_sublocations.isEmpty() == false);
        SourcePosition result = _sublocations.get(0).startPosition();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SourceLocation#pastEndPosition
    */
    public SourcePosition pastEndPosition()
    {
        Assert.check(_sublocations.isEmpty() == false);
        SourcePosition result = _sublocations.getLast().pastEndPosition();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see SourceLocation#startLineNumber
    */
    public int startLineNumber()
    {
        int result = startPosition().lineNumber();

        Assert.ensure(result > 0);
        return result;
    }

    /**
        @see SourceLocation#endLineNumber
    */
    public int endLineNumber()
    {
        int result = pastEndPosition().lineNumber();

        Assert.ensure(result > 0);
        return result;
    }

    /**
        @see SourceLocation#lineNumbers
    */
    public IntIterator lineNumbers()
    {
        SortedSet numbers = new TreeSet();
        SourceLocationIterator locIter = _sublocations.iterator();
        while (locIter.hasNext())
        {
            IntIterator iter = locIter.next().lineNumbers();
            while (iter.hasNext())
            {
                numbers.add(new Integer(iter.next()));
            }
        }

        // 'numbers' now contains, in ascending order, the distinct line
        // numbers of all of the lines that contain at least one character
        // of at least one of our sublocations. So now we just have to build
        // an IntIterator to iterate over them.
        int numNumbers = numbers.size();
        int[] result = new int[numNumbers];
        Iterator numIter = numbers.iterator();
        for (int i = 0; i < numNumbers; i++)
        {
            Assert.check(numIter.hasNext());
            result[i] = ((Integer) numIter.next()).intValue();
        }
        Assert.check(numIter.hasNext() == false);

        // Assert.ensure(result != null);
        return new IntArrayIterator(result);
    }

    /**
        @see SourceLocation#startOffset
    */
    public int startOffset()
    {
        int result = startPosition().offset();

        Assert.ensure(result >= 0);
        return result;
    }

    /**
        @see SourceLocation#pastEndOffset
    */
    public int pastEndOffset()
    {
        int result = pastEndPosition().offset();

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

        StringBuffer buf = new StringBuffer();
        SourceLocation prevLoc = null;
        SourceLocationIterator iter = _sublocations.iterator();
        while (iter.hasNext())
        {
            SourceLocation loc = iter.next();
            if (prevLoc != null)
            {
                int numSkippedLines =
                    loc.startLineNumber() - prevLoc.endLineNumber();
                if (numSkippedLines > 1)
                {
                    String msg = _resources.
                        getMessage(LINES_BETWEEN_FORMAT,
                                   Integer.toString(numSkippedLines));
                    buf.append(msg).append(TextUtilities.NL);
                }
                else if (numSkippedLines == 1)
                {
                    buf.append(TextUtilities.NL);
                }
                else  // numSkippedLines == 0
                {
                    // Retain spacing between sublocations on the same line.
                    int numSpaces =
                        loc.startOffset() - prevLoc.pastEndOffset();
                    buf.append(TextUtilities.copies(' ', numSpaces));
                }
            }
            buf.append(loc.fragmentOf(source));

            prevLoc = loc;
        }  // while (iter.hasNext())

        // Assert.ensure(result != null);
        return buf.toString();
    }

    /**
        @see SourceLocation#basicComponents
    */
    public SourceLocationList basicComponents()
    {
        SourceLocationList result =
            SourceLocationList.createArrayList(_sublocations.size());

        SourceLocationIterator iter = _sublocations.iterator();
        while (iter.hasNext())
        {
            result.addAll(iter.next().basicComponents());
        }

        Assert.ensure(result != null);
        return result;
    }


    // Overridden Object methods

    /**
        @see Object#equals
    */
    public boolean equals(Object obj)
    {
        boolean result = false;

        if (obj != null && obj instanceof CompoundSourceLocation)
        {
            CompoundSourceLocation loc = (CompoundSourceLocation) obj;
            if (_sublocations.size() == loc._sublocations.size())
            {
                result = true;
                SourceLocationIterator iter1 = _sublocations.iterator();
                SourceLocationIterator iter2 = loc._sublocations.iterator();
                while (iter1.hasNext())
                {
                    Assert.check(iter2.hasNext());
                    if (iter1.next().equals(iter2.next()) == false)
                    {
                        result = false;
                        break;
                    }
                }
            }
        }
        else if (obj != null && obj instanceof SourceLocation &&
                 _sublocations.size() == 1)
        {
            result = _sublocations.get(0).equals(obj);
        }

        return result;
    }

    /**
        @see Object#hashCode
    */
    public int hashCode()
    {
        int result = 0;

        // We use a bounded number of sublocation so that this method runs in
        // constant time.
        int numSublocs = Math.min(5, _sublocations.size());
        for (int i = 0; i < numSublocs; i++)
        {
            result ^= _sublocations.get(i).hashCode();
        }

        return result;
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer(classNameToString());
        buf.append(": (");
        SourceLocationIterator iter = _sublocations.iterator();
        while (iter.hasNext())
        {
            buf.append("[").append(iter.next().toString()).append("]");
            if (iter.hasNext())
            {
                buf.append(", ");
            }
        }
        buf.append(")");
        return buf.toString();
    }
}
