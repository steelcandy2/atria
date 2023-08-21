/*
 Copyright (C) 2001-2002 by James MacKay.

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
    The interface implemented by classes that process work passed
    to them in the form of Runnable objects.
    <p>
    A WorkProcessor can continue to be used even after it has
    finished processing all of its work: simply add more work
    to it.
    <p>
    The main purpose of this interface is to abstract how work is
    processed: one subclass could process its work sequentially,
    another could spawn a new thread to process each piece of work,
    and a third could use a pool of threads to process its work.
    Other types of work processors are possible as well.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public interface WorkProcessor
{
    // Public methods

    /**
        Adds the specified work to the work that this processor
        is to process.

        @param work the work that this processor is to process
    */
    public void add(Runnable work);
        // Assert.require(work != null);

    /**
        Indicates whether this processor has finished processing
        all of its work.

        @return true if this processor has finished processing all
        of its work, and false if it hasn't
    */
    public boolean isDone();

    /**
        Waits until this processor has finished all of its work,
        then returns.
    */
    public void waitUntilDone();
}
