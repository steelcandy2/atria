/*
 Copyright (C) 2001 by James MacKay.

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

package com.steelcandy.common.work;

import com.steelcandy.common.debug.Assert;

/**
    A thread-safe SequentialWorkProcessor.

    @author James MacKay
    @see SequentialWorkProcessor
*/
public class SyncSequentialWorkProcessor
    extends SequentialWorkProcessor
{
    // Constructors

    /**
        Constructs a SyncSequentialWorkProcessor.
    */
    public SyncSequentialWorkProcessor()
    {
        // empty
    }


    // Public methods

    /**
        @see WorkProcessor#add
    */
    public synchronized void add(Runnable work)
    {
        Assert.require(work != null);

        super.add(work);
    }

    /**
        @see WorkProcessor#isDone
    */
    public synchronized boolean isDone()
    {
        return super.isDone();
    }

    /**
        @see WorkProcessor#waitUntilDone
    */
    public synchronized void waitUntilDone()
    {
        super.waitUntilDone();
    }
}
