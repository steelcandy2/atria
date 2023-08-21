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
    A WorkProcessor that processes its work one at a time as
    it gets it. All work processing is done on the calling
    thread.
    <p>
    This class doesn't really do anything useful in and of
    itself: the work objects' <code>run()</code> methods could
    just be called directly. Thus this class is only used in
    situations where a different WorkProcessor could be used
    instead (i.e. where the means by which work is processed
    is strategized).
    <p>
    This class is <em>not</em> thread-safe, in the sense that
    calling a method to test or wait for all work to be
    completed may not be correct if that method call is done
    on a thread different from the one on which work is add()ed
    to this processor, or if work is being add()ed on more than
    one thread. The SyncSequentialWorkProcessor class is a
    thread-safe version of this class. (However, this class
    should be sufficient for most uses.)

    @author James MacKay
*/
public class SequentialWorkProcessor
    implements WorkProcessor
{
    // Constructors

    /**
        Constructs a SequentialWorkProcessor.
    */
    public SequentialWorkProcessor()
    {
        // empty
    }


    // Public methods

    /**
        @see WorkProcessor#add
    */
    public void add(Runnable work)
    {
        Assert.require(work != null);

        work.run();
    }

    /**
        @see WorkProcessor#isDone
    */
    public boolean isDone()
    {
        return true;
    }

    /**
        @see WorkProcessor#waitUntilDone
    */
    public void waitUntilDone()
    {
        // empty
    }
}
