/*
 Copyright (C) 2005-2006 by James MacKay.

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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.tokens.*;
import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.tokens.*;
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.semantic.*;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.io.*;

import java.io.*;

/**
    An abstract base class for Atria interpreters whose function is to
    write out information.

    @author  James MacKay
*/
public abstract class AtriaAbstractWritingInterpreter
    extends AtriaAbstractInterpreter
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaProgramResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_CURRENT_BUFFER_MSG =
            "NO_CURRENT_BUFFER_MSG",
        WRITING_FAILED_MSG =
            "WRITING_FAILED_MSG",
        INTERPRETER_BASE_WRITER_NOT_SET_MSG =
            "INTERPRETER_BASE_WRITER_NOT_SET_MSG",
        INTERPRETER_BASE_WRITER_ALREADY_SET_MSG =
            "INTERPRETER_BASE_WRITER_ALREADY_SET_MSG",
        CANNOT_START_BUFFER_BEFORE_BASE_WRITER_SET_MSG =
            "CANNOT_START_BUFFER_BEFORE_BASE_WRITER_SET_MSG",
        BASE_WRITER_CANNOT_WRITE_TO_FILE_MSG =
            "BASE_WRITER_CANNOT_WRITE_TO_FILE_MSG";


    /**
        The initial size of StringBuffers that are used to temporarily
        hold written out data.
    */
    private static final int INITIAL_BUFFER_SIZE = 512;


    // Private fields

    /**
        A stack of writers currently being used to write out information.
        The topmost writer on the stack is the one currently being used.
        <p>
        Note: all but the bottom-most item on this stack will be a
        StringIndentWriter. This stack should never be empty.
    */
    private IndentWriterStack _writers;


    // Constructors

    /**
        Constructs an AtriaAbstractWritingInterpreter.

        @param w the writer that the interpreter is to use to write out
        information
    */
    public AtriaAbstractWritingInterpreter(IndentWriter w)
    {
        this();
        Assert.require(w != null);

        setBaseWriter(w);
    }

    /**
        Constructs an AtriaAbstractWritingInterpreter.
        <p>
        Note: setBaseWriter() will have to be called before the interpreter
        tries to write out anything.

        @see #setBaseWriter(IndentWriter)
    */
    public AtriaAbstractWritingInterpreter()
    {
        _writers = new IndentWriterStack();
    }


    // Protected methods

    /**
        Starts a new buffer that the write...() methods will write into.
        <p>
        There must be a matching finishBuffer() call for each startBuffer()
        call: that call will return the buffer that was written to.

        @exception IllegalStateException is thrown iff our base writer hasn't
        been called before this method was called
        @see #finishBuffer
    */
    protected void startBuffer()
    {
        if (_writers.isEmpty())
        {
            String msg = _resources.
                getMessage(CANNOT_START_BUFFER_BEFORE_BASE_WRITER_SET_MSG,
                           getClass().getName());
            throw new IllegalStateException(msg);
        }

        StringWriter w = new StringWriter(INITIAL_BUFFER_SIZE);
        _writers.push(new StringIndentWriter(w));
    }

    /**
        Finishes writing to the current buffer.

        @return the buffer we just finished writing to
        @exception IllegalStateException if there is no current buffer
    */
    protected StringBuffer finishBuffer()
    {
        if (_writers.size() <= 1)
        {
            String msg = _resources.
                getMessage(NO_CURRENT_BUFFER_MSG);
            throw new IllegalStateException(msg);
        }

        StringIndentWriter w = (StringIndentWriter) _writers.pop();

        // Closing 'w' ensures that everything it has written will have been
        // flushed to the StringWriter that it wraps.
        try
        {
            w.close();
        }
        catch (IOException ex)
        {
            handleWriteFailure(ex, w, w.contents());
        }

        StringBuffer result = w.contentsBuffer();

        Assert.ensure(result != null);
        return result;
    }


    /**
        Writes out the specified text followed by a newline, all using our
        writer().

        @param msg the text to write
    */
    protected void writeLine(String msg)
    {
        Assert.require(msg != null);

        IndentWriter w = writer();
        try
        {
            Io.writeLine(w, msg);
        }
        catch (IOException ex)
        {
            handleWriteFailure(ex, w, msg);
        }
    }

    /**
        Writes out the specified text using our writer().

        @param msg the text to write
    */
    protected void write(String msg)
    {
        Assert.require(msg != null);

        Writer w = writer();
        try
        {
            w.write(msg);
        }
        catch (IOException ex)
        {
            handleWriteFailure(ex, writer(), msg);
        }
    }


    /**
        Modifies our writer() so that subsequent lines are output indented
        one more level than before.
    */
    protected void indent()
    {
        writer().incrementIndentLevel();
    }

    /**
        Modifies our writer() so that subsequent lines are output one less
        level than before.
    */
    protected void unindent()
    {
        writer().decrementIndentLevel();
    }


    /**

        Creates and returns a base writer for this interpreter that writes
        to the file with the specified pathname.

        @param f the pathname of the file that the base writer is to
        write to
        @return a base writer that writes to 'f'
        @exception FatalErrorException is thrown iff the base writer could
        not be created (for example, if the file could not be written to)
    */
    protected IndentWriter createBaseWriter(File f)
        throws FatalErrorException
    {
        Assert.require(f != null);

        FileWriter fw = null;
        try
        {
            fw = new FileWriter(f);
        }
        catch (IOException ex)
        {
            AtriaConstructManager.Document doc = document();
            ErrorHandler handler = errorHandler();
            Assert.check(doc != null);
            Assert.check(handler != null);

            String msg = _resources.
                getMessage(BASE_WRITER_CANNOT_WRITE_TO_FILE_MSG,
                           f.getPath(), ex.getLocalizedMessage());
            handleRuntimeError(FATAL_ERROR_LEVEL, msg, doc, handler);
            Assert.unreachable();
                // since this is a fatal error
        }

        IndentWriter result = IndentWriter.createClosing(fw);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param w our new base writer
        @exception IllegalStateException is thrown if our base writer has
        already been set
    */
    protected void setBaseWriter(IndentWriter w)
    {
        Assert.require(w != null);

        if (_writers.isEmpty() == false)
        {
            String msg = _resources.
                getMessage(INTERPRETER_BASE_WRITER_ALREADY_SET_MSG,
                           getClass().getName());
            throw new IllegalStateException(msg);
        }

        _writers.push(w);

        Assert.ensure(_writers.size() == 1);
    }

    /**
        Tries to close our base writer, if we have one set. It assumes that
        all started buffers have been finished.
    */
    protected void tryToCloseBaseWriter()
    {
        Assert.check(_writers.size() <= 1);

        if (_writers.isEmpty() == false)
        {
            Writer w = _writers.pop();
            Io.tryToClose(w);
        }
        // Otherwise there's no base writer (possibly because it could
        // not be created).

        Assert.ensure(_writers.isEmpty());
    }

    /**
        @return the writer to use to output information
        @exception IllegalStateException is thrown if our base writer hasn't
        been set
        @see #setBaseWriter(IndentWriter)
    */
    protected IndentWriter writer()
    {
        IndentWriter result;

        try
        {
            result = _writers.top();
        }
        catch (NoSuchItemException ex)
        {
            String msg = _resources.
                getMessage(INTERPRETER_BASE_WRITER_NOT_SET_MSG,
                           getClass().getName());
            IllegalStateException newEx = new IllegalStateException(msg);
            newEx.initCause(ex);
            throw newEx;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Handles the situation where writing out information fails.

        @param ex the exception that signalled that writing failed
        @param w the writer that failed to write the information
        @param msg the information that was supposed to be written
    */
    protected void handleWriteFailure(IOException ex, IndentWriter w,
                                      String msg)
    {
        Assert.require(ex != null);
        Assert.require(w != null);
        Assert.require(msg != null);

        AtriaConstructManager.Document doc = document();
        ErrorHandler handler = errorHandler();
        Assert.check(doc != null);
        Assert.check(handler != null);

        String errorMsg = _resources.
            getMessage(WRITING_FAILED_MSG, msg,
                       ex.getLocalizedMessage());
        handleRuntimeError(FATAL_ERROR_LEVEL, errorMsg, doc, handler);
    }


    // Abstract methods

    /**
        @return the document currently being interpreted, or null if one
        isn't currently being interpreted
    */
    protected abstract AtriaConstructManager.Document document();
        // 'result' may be null

    /**
        @return the error handler that is to be used to handle errors that
        occur during interpretation, or null if a document isn't currently
        being interpreted
    */
    protected abstract ErrorHandler errorHandler();
        // 'result' may be null
}
