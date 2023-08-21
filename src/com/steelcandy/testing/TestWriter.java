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

package com.steelcandy.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import java.io.*;
import java.util.*;

/**
    A 'Writer' that Tests use to output various types of information.
    <p>
    This class implements the same methods as does Writer, except that
    most take an additional initial OutputType parameter. Thus this
    class is analogous to a standard Java Writer class, but cannot
    subclass Writer.
    <p>
    This class can either be used directly or it can be subclassed.
    Subclasses and/or clients should call setWriter() to set writers
    for all of the OutputTypes that it supports. A subclass often
    does this in its constructor. If a writer isn't set for a particular
    type of output then the writer for the DEFAULT OutputType is used.
    <p>
    A subclass can define a new type of output simply by defining
    a constant set equal to a new OutputTypeConstant, registering
    that constant in a static block using the registerOutputType()
    method, and (optionally) setting a writer for that type of output.
    <p>
    None of this class' write() methods throw IOExceptions. If an
    error occurs trying to write some information then that information
    is output to standard error, preceded by an error message. Other
    methods, such as close() and flush(), can still throw IOExceptions,
    though.
    <p>
    By default the relevant writer is flush()ed after every write. The
    setFlushAfterWrite() method can be used to change this default
    behaviour.

    @author James MacKay
    @see Test
    @see java.io.Writer
    @see #registerOutputType
*/
public class TestWriter implements Cloneable
{
    // Protected static fields

    /**
        The table of all OutputTypes: it maps OutputType names to
        the OutputTypes themselves. (Thus the keys are Strings and
        the values are OutputTypes.)
        <p>
        Whenever a new OutputType (such as an OutputTypeConstant)
        is defined it should also be added to this table by calling
        the registerOutputType() method on it in a static block.

        @see #registerOutputType
    */
    protected static Map _outputTypesTable = new HashMap();


    // Output type constants

    /**
        The OutputType used to represent the 'type' corresponding
        to the default Writer: the Writer used by default for an
        OutputType for which no Writer has been explicitly set.
    */
    public static final OutputType DEFAULT =
        new OutputTypeConstant("default");
    static
    {
        registerOutputType(DEFAULT);
    }

    /** The output describes an error that occurred. */
    public static final OutputType ERROR =
        new OutputTypeConstant("error");
    static
    {
        registerOutputType(ERROR);
    }

    /**
        The output is debugging information. This is usually only
        used while debugging a test.
    */
    public static final OutputType DEBUG =
        new OutputTypeConstant("debug");
    static
    {
        registerOutputType(DEBUG);
    }

    /** The output is timing information. */
    public static final OutputType TIMING =
        new OutputTypeConstant("timing");
    static
    {
        registerOutputType(TIMING);
    }

    /**
        The output is (part of) the full results of the test. For
        example, it could be used to output the results for each
        sub-test individually.

        @see #SUMMARY
    */
    public static final OutputType RESULTS =
        new OutputTypeConstant("results");
    static
    {
        registerOutputType(RESULTS);
    }

    /**
        The output is (part of) a summary of the test's results. For
        example the number of (sub-)tests run and the number that
        were successful.

        @see #RESULTS
    */
    public static final OutputType SUMMARY =
        new OutputTypeConstant("summary");
    static
    {
        registerOutputType(SUMMARY);
    }

    /** The output is output from the test as it is running. */
    public static final OutputType OUTPUT =
        new OutputTypeConstant("output");
    static
    {
        registerOutputType(OUTPUT);
    }

    /**
        The output is (part of) the information about the test (e.g.
        its name, description, etc.)
    */
    public static final OutputType INFORMATION =
        new OutputTypeConstant("information");
    static
    {
        registerOutputType(INFORMATION);
    }


    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TestingResourcesLocator.resources;

    /** Resource keys. */
    private static final String LINE_OUTPUT_FAILED_MSG =
        "LINE_OUTPUT_FAILED_MSG";
    private static final String STRING_OUTPUT_FAILED_MSG =
        "STRING_OUTPUT_FAILED_MSG";
    private static final String BUFFER_OUTPUT_FAILED_MSG =
        "BUFFER_OUTPUT_FAILED_MSG";
    private static final String CHARACTER_OUTPUT_FAILED_MSG =
        "CHARACTER_OUTPUT_FAILED_MSG";

    private static final String OUTPUT_TYPE_ALREADY_REGISTERED_MSG =
        "OUTPUT_TYPE_ALREADY_REGISTERED_MSG";
    private static final String OUTPUT_TYPE_NOT_FOUND_MSG =
        "OUTPUT_TYPE_NOT_FOUND_MSG";


    // Private fields

    /**
        Maps OutputTypes to the Writer to be used to output that
        type of output. The keys are all OutputTypes and the values
        are all Writers.
    */
    private Map _typeToWriterMap = new HashMap();

    /**
        Indicates whether Writers should be flushed after every write.
    */
    private boolean _flushAfterWrite = true;


    // Constructors

    /**
        Default constructor.
    */
    public TestWriter()
    {
        // Set the DEFAULT Writer to output nothing. (It is
        // assumed that the DEFAULT Writer is always set.)
        setWriter(DEFAULT, Io.none);
    }


    // Public write methods

    /**
        Writes the specified string followed by the current platform's
        line separator as the specified type of output.

        @param type the type of the output
        @param msg the information to output as a line
    */
    public void writeLine(OutputType type, String msg)
    {
        try
        {
            Writer w = writer(type);
            Io.writeLine(w, msg);
            tryToFlush(w);
        }
        catch (IOException ex)
        {
            String errorMsg = _resources.getMessage(LINE_OUTPUT_FAILED_MSG, msg);
            Io.err.println(errorMsg);
        }
    }

    /**
        @see Writer#write(char[], int, int)
    */
    public void write(OutputType type, char[] cbuf, int offset, int length)
    {
        try
        {
            Writer w = writer(type);
            w.write(cbuf, offset, length);
            tryToFlush(w);
        }
        catch (IOException ex)
        {
            String output = new String(cbuf, offset, length);
            String msg = _resources.getMessage(BUFFER_OUTPUT_FAILED_MSG, output);
            Io.err.println(msg);
        }
    }

    /**
        @see Writer#write(int)
    */
    public void write(OutputType type, int ch)
    {
        try
        {
            Writer w = writer(type);
            w.write(ch);
            tryToFlush(w);
        }
        catch (IOException ex)
        {
            String msg = _resources.getMessage(CHARACTER_OUTPUT_FAILED_MSG,
                                               String.valueOf((char) ch));
            Io.err.println(msg);
        }
    }

    /**
        @see Writer#write(char[])
    */
    public void write(OutputType type, char[] cbuf)
    {
        write(type, cbuf, 0, cbuf.length);
    }

    /**
        @see Writer#write(String)
    */
    public void write(OutputType type, String str)
    {
        write(type, str, 0, str.length());
    }

    /**
        @see Writer#write(String, int, int)
    */
    public void write(OutputType type, String str, int offset, int length)
    {
        try
        {
            Writer w = writer(type);
            w.write(str, offset, length);
            tryToFlush(w);
        }
        catch (IOException ex)
        {
            String output = str.substring(offset, offset + length);
            String msg = _resources.getMessage(STRING_OUTPUT_FAILED_MSG, output);
            Io.err.println(msg);
        }
    }


    // Public methods

    /**
        Returns the Writer to be used to output information of
        the specified type.

        @param type the output type for which to get the Writer
        @return the Writer to use to output that type of output
    */
    public Writer writer(OutputType type)
    {
        Writer result = (Writer) _typeToWriterMap.get(type);
        if (result == null)
        {
            // There is no writer for the specific type of output,
            // so use the default one. (The default Writer should
            // always be set, i.e. non-null.)
            result = writer(DEFAULT);
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets the specified writer to be the one used to output
        information of the specified type.

        @param type the type of output for which to set the writer
        @param newWriter the new writer to use to output information
        of that type
    */
    public void setWriter(OutputType type, Writer newWriter)
    {
        _typeToWriterMap.put(type, newWriter);
    }

    /**
        Sets the writer that this TestWriter uses to output information
        of the specified type to be the writer that the specified
        TestWriter uses to output information of type 'sourceWriterType'.

        @param type the type of output for which to set the writer
        @param sourceWriter the TestWriter from which to get the writer
        @param sourceWriterType the type of output that the writer to
        be obtained from the sourceWriterType is used to output
    */
    public void setWriter(OutputType type, TestWriter sourceWriter,
                          OutputType sourceWriterType)
    {
        _typeToWriterMap.put(type, sourceWriter.writer(sourceWriterType));
    }

    /**
        Sets whether this writer is flushed after every write.
        (A write will still succeed even if the following flush()
        fails.)

        @param flushAfterWrite true if we're to flush() after every
        write, and false if we're not to
    */
    public void setFlushAfterWrite(boolean flushAfterWrite)
    {
        _flushAfterWrite = flushAfterWrite;
    }

    /**
        @see Writer#flush
    */
    public void flush(OutputType type)
        throws IOException
    {
        writer(type).flush();
    }

    /**
        Flushes the Writers for all types of output.

        @exception IOException thrown if an I/O error occurs while
        flushing one of the Writers
    */
    public void flushAll()
        throws IOException
    {
        Iterator iter = allWriters();
        while (iter.hasNext())
        {
            Writer w = (Writer) iter.next();
            w.flush();
        }
    }

    /**
        @see Writer#close
    */
    public void close()
        throws IOException
    {
        Iterator iter = allWriters();
        while (iter.hasNext())
        {
            Writer w = (Writer) iter.next();
            w.close();
        }
    }


    // Overridden Object methods

    /**
        @see Object#clone
    */
    public Object clone()
    {
        return cloneWriter();
    }

    /**
        @return a clone of this TestWriter
    */
    public TestWriter cloneWriter()
    {
        try
        {
            TestWriter result = (TestWriter) super.clone();
            result._typeToWriterMap = (Map) ((HashMap) _typeToWriterMap).clone();
            return result;
        }
        catch (CloneNotSupportedException ex)
        {
            // This should never happen since we're Cloneable.
            throw new InternalError("TestWriter.cloneWriter() got a " +
                                    "CloneNotSupprtedException");
        }
    }


    // Protected methods

    /**
        Attempts to flush() the specified writer iff we're to flush
        writers after every write. If the flush() fails then it fails
        silently: this method just attempts to flush the writer.

        @param w the writer to attempt to flush
        @see Writer#flush
    */
    protected void tryToFlush(Writer w)
    {
        if (_flushAfterWrite)
        {
            Io.tryToFlush(w);
        }
    }

    /**
        @return an iterator over the Writers for all of the different
        OutputTypes that have a Writer specifically set. Each item
        returned by the iterator will be a Writer
    */
    protected Iterator allWriters()
    {
        return _typeToWriterMap.values().iterator();
    }


    // Protected static methods

    /**
        Registers the specified OutputType.

        @param type the new OutputType to register
        @exception IllegalArgumentException thrown if an OutputType
        with the same name as the specified OutputType has already
        been registered
    */
    protected static void registerOutputType(OutputType type)
    {
        String typeName = type.name();
        if (_outputTypesTable.get(typeName) == null)
        {
            _outputTypesTable.put(type.name(), type);
        }
        else
        {
            // There is already an OutputType registered under the
            // same name that 'type' has.
            String msg = _resources.
                getMessage(OUTPUT_TYPE_ALREADY_REGISTERED_MSG, typeName);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
        @return an iterator over the names of all of the
        registered OutputTypes. Each item returned by the
        iterator will be a String
    */
    protected static Iterator registeredOutputTypeNames()
    {
        return _outputTypesTable.keySet().iterator();
    }

    /**
        Returns the registered OutputType with the specified name.

        @param typeName the name of the registered OutputType to
        be returned
        @return the registered OutputType with the specified name
        @exception NoSuchItemException thrown if there is no
        registered OutputType with the specified name
    */
    protected static OutputType registeredOutputType(String typeName)
    {
        OutputType result = (OutputType) _outputTypesTable.get(typeName);
        if (result == null)
        {
            String msg =
                _resources.getMessage(OUTPUT_TYPE_NOT_FOUND_MSG, typeName);
            throw new NoSuchItemException(msg);
        }
        return result;
    }


    // Inner classes

    /**
        The abstract base class for the class of the constants used
        to represent the various types of test output.
        <p>
        This class is public so that other classes can store and pass
        as arguments the various output type constants, but it is
        abstract so that they cannot create their own instances.

        @see TestWriter.OutputTypeConstant
    */
    public abstract static class OutputType
    {
        /** This OutputType's name. */
        private String _name;

        /**
            Making this class' sole constructor private prevents
            any class outside of other inner classes of our outer
            class from subclassing this class. However, this is
            not implemented yet (at least as of JDK 1.1.7), so
            this constructor is protected for now.

            @param the OutputType's name. It should be unique
            across all OutputTypes
        */
        protected OutputType(String name)
        {
            Assert.require(name != null);

            _name = name;
        }

        /**
            @return this OutputType's name
        */
        public String name()
        {
            Assert.ensure(_name != null);
            return _name;
        }
    }

    /**
        The class of the output type constants defined in this class
        and in subclasses.
        <p>
        This class is protected so that only this class and subclasses
        can create instances of OutputTypeConstant, and thus are the
        only ones that can create instances of OutputType.
        <p>
        Subclasses should only construct instances of this class to set
        the values of output type constants, as is done in this class'
        outer class.
    */
    protected static class OutputTypeConstant extends OutputType
    {
        /**
            Constructs an OutputTypeConstant from its name.

            @param name the output type's name: it should be unique
            across all OutputTypes
        */
        public OutputTypeConstant(String name)
        {
            super(name);
        }
    }
}
