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

package com.steelcandy.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.Io;

import java.io.Writer;

/**
    The default class of TestWriter. It outputs ERROR to standard
    error, discards DEBUG and TIMING output, and sends all other
    type of output to standard output.

    @author James MacKay
    @version $Revision: 1.5 $
*/
public class DefaultTestWriter extends TestWriter
{
    // Constructors

    /**
        Default constructor.
    */
    public DefaultTestWriter()
    {
        setWriter(TestWriter.ERROR, Io.err);
        setWriter(TestWriter.DEBUG, Io.none);
        setWriter(TestWriter.TIMING, Io.none);
    }
}
