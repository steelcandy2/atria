/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.common.containers;

import com.steelcandy.common.debug.Assert;

import java.util.BitSet;

/**
    The class of BitSet that keeps track of how many bits it has set.

    @author  James MacKay
*/
public class CountedBitSet
    extends BitSet
{
    // Private fields

    /** The number of bits in this set that are set. */
    private int _numberSet;


    // Constructors

    /**
        @see BitSet#BitSet
    */
    public CountedBitSet()
    {
        super();

        _numberSet = 0;
    }

    /**
        @see BitSet#BitSet(int)
    */
    public CountedBitSet(int nbits)
    {
        super(nbits);

        _numberSet = 0;
    }


    // Public methods

    /**
        @return the number of bits we have set
    */
    public int numberSet()
    {
        if (_numberSet < 0)
        {
            updateNumberOfSetBits();
            Assert.check(_numberSet >= 0);
        }

        return _numberSet;
    }

    /**
        @return the number of bits we not have set
    */
    public int numberClear()
    {
        return length() - numberSet();
    }


    /**
        @see BitSet#set(int)
    */
    public void set(int bitIndex)
    {
        if (get(bitIndex) == false)
        {
            super.set(bitIndex);
            if (_numberSet >= 0)
            {
                _numberSet += 1;
            }
        }
    }

    /**
        @see BitSet#clear(int)
    */
    public void clear(int bitIndex)
    {
        if (get(bitIndex))
        {
            super.clear(bitIndex);
            _numberSet -= 1;  // still OK if we had _numberSet < 0
        }
    }

    /**
        @see BitSet#andNot(BitSet)
    */
    public void andNot(BitSet s)
    {
        super.andNot(s);
        _numberSet = -1;
    }

    /**
        @see BitSet#and(BitSet)
    */
    public void and(BitSet s)
    {
        super.and(s);
        _numberSet = -1;
    }

    /**
        @see BitSet#or(BitSet)
    */
    public void or(BitSet s)
    {
        super.or(s);
        _numberSet = -1;
    }

    /**
        @see BitSet#xor(BitSet)
    */
    public void xor(BitSet s)
    {
        super.xor(s);
        _numberSet = -1;
    }


    // Protected methods

    /**
        Recounts the number of bits we have set.
    */
    protected void updateNumberOfSetBits()
    {
        int newNumberSet = 0;
        int len = length();
        for (int i = 0; i < len; i++)
        {
            if (get(i))
            {
                newNumberSet += 1;
            }
        }

        _numberSet = newNumberSet;
    }
}
