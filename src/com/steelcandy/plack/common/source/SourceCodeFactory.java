/*
 Copyright (C) 2005-2008 by James MacKay.

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

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ReadError;

import java.io.File;
import java.io.IOException;

/**
    Represents a factory class that creates SourceCode objects.
    <p>
    Usually a compiler or other language processor will create an instance
    of this class and use it to create all of the SourceCode objects involved
    in a compilation.

    @author  James MacKay
*/
public class SourceCodeFactory
{
    // Private fields

    /**
        The minimum size that a source file has to have for this factory to
        consider not storing it in a SourceCodeString.
    */
    private long _minSize;

    /**
        The maximum number of files allowed to be open at any given time on
        SourceCodeFile instances created by this factory.
    */
    private int _maxFiles;

    /**
        The number of Readers that are currently (at least potentially) open
        on SourceCodeFiles created by this factory.
    */
    private int _numberOpen;


    // Constructors

    /**
        Constructs a SourceCodeFactory.
        <p>
        Note: the maxSize limit exists because many platforms place a limit
        on the number of files that can be open at a given time.

        @param minSize the minimum size that a source file has to have for
        the factory to consider not storing it in a SourceCodeString
        @param maxFiles the maximum number of files allowed to be open at
        any given time on SourceCodeFile instances created by the factory
    */
    public SourceCodeFactory(long minSize, int maxFiles)
    {
        Assert.require(minSize >= 0);
        Assert.require(maxFiles >= 0);

        _minSize = minSize;
        _maxFiles = maxFiles;
        _numberOpen = 0;
    }


    // Public methods

    /**
        @param sourceFile the pathname of a source file
        @param handler the error handler to use to handle any errors in
        creating the SourceCode instance
        @return a SourceCode object that represents the source code in
        'sourceFile'
    */
    public SourceCode create(File sourceFile, ErrorHandler handler)
    {
        Assert.require(sourceFile != null);
        Assert.require(handler != null);

        SourceCode result;

        SourceCodeFile f = new SourceCodeFile(sourceFile);
        if (doCreateSourceCodeString(sourceFile))
        {
            try
            {
                result = f.toSourceCodeString();
            }
            catch (IOException ex)
            {
                ReadError error = new ReadError(ReadError.FATAL_ERROR_LEVEL,
                                                ex.getLocalizedMessage(), f);
                handler.handle(error);
                Assert.unreachable();  // since this is a fatal error
                result = null;  // not reached
            }
        }
        else
        {
            f.setCreator(this);
            result = f;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Called by SourceCodeFile objects constructed by this factory to
        notify it that a Reader has been opened on the file.
    */
    public void notifyFileReaderOpened()
    {
        _numberOpen += 1;
    }

    /**
        Called by SourceCodeFile objects constructed by this factory to
        notify it that a Reader opened on the file has been closed.
    */
    public void notifyFileReaderClosed()
    {
        Assert.check(_numberOpen > 0);
        _numberOpen -= 1;
    }


    // Protected methods

    /**
        @param f a file containing source code
        @return true iff the SourceCode object that is to represent the
        source code in 'f' should be a SourceCodeString, and false if it
        should be a SourceCodeFile
    */
    protected boolean doCreateSourceCodeString(File f)
    {
        Assert.require(f != null);

//        return (_numberOpen >= _maxFiles) || (f.length() < _minSize);
        return false;
    }
}
