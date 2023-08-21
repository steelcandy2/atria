/*
 Copyright (C) 2002-2006 by James MacKay.

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

package com.steelcandy.common.io;

import com.steelcandy.common.debug.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
    An IndentWriter that records all of the operations called on it.
    They can later be 'played back' onto another IndentWriter.
    <p>
    In order to allow an instance of this class to be used to record
    only part of what is to be written to another IndentWriter, this
    class allows its indent level to be decremented more times than
    it is incremented. However, to support this instances of this class
    never actually change their indent level, and so their indentLevel()
    method returns an incorrect value.

    @author James MacKay
*/
public class RecordingIndentWriter
    extends IndentWriter
{
    // Constants

    /**
        The indent level that every instance always has.
        <p>
        It must be greater than zero in order to allow calls to
        incrementIndentLevel() to always succeed.
    */
    private static final int RECORDING_INDENT_LEVEL = 42;


    // Private fields

    /**
        The Operations that have been performed on this writer,
        in order.
    */
    private List _operations = new LinkedList();


    // Constructors

    /**
        Constructs a RecordingIndentWriter.
    */
    public RecordingIndentWriter()
    {
        super(DiscardWriter.instance(), false);
        super.setIndentLevel(RECORDING_INDENT_LEVEL);
    }


    // Public methods

    /**
        Plays back all of the operations performed on this writer
        onto the specified IndentWriter, in order.

        @param w the IndentWriter to play back our operations onto
        @exception IOException throws the first IOException thrown by
        'w' in the course of playing back our operations onto it
    */
    public void playBack(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        Iterator iter = _operations.iterator();
        while (iter.hasNext())
        {
            Operation op = (Operation) iter.next();
            op.perform(w);
        }
    }

    /**
        Clears all of the operations stored by this instance.
    */
    public void clear()
    {
        _operations.clear();
    }


    /**
        @see IndentWriter#setIndentSize(int)
    */
    public void setIndentSize(int size)
    {
        Assert.require(size >= 0);

        super.setIndentSize(size);
        add(new SetIndentSizeOperation(size));
    }

    /**
        @see IndentWriter#setIndentLevel(int)
    */
    public void setIndentLevel(int level)
    {
        Assert.require(level >= 0);

        // Note: we intentionally don't call any superclass method here.
        add(new SetIndentLevelOperation(level));
    }

    /**
        @see IndentWriter#incrementIndentLevel
    */
    public void incrementIndentLevel()
    {
        // Note: we intentionally don't call any superclass method here.
        add(new IncrementIndentLevelOperation());
    }

    /**
        @see IndentWriter#decrementIndentLevel
    */
    public void decrementIndentLevel()
    {
        Assert.require(indentLevel() > 0);

        // Note: we intentionally don't call any superclass method here.
        add(new DecrementIndentLevelOperation());
    }

    /**
        @see WrappingWriter#write(int)
    */
    public void write(int ch)
    {
        add(new WriteCharOperation(ch));
    }

    /**
        @see WrappingWriter#write(String)
    */
    public void write(String str)
    {
        add(new WriteStringOperation(str));
    }

    /**
        @see WrappingWriter#write(String, int, int)
    */
    public void write(String str, int offset, int length)
    {
        add(new WriteSubstringOperation(str, offset, length));
    }

    /**
        @see WrappingWriter#write(char[])
    */
    public void write(char[] array)
    {
        add(new WriteCharArrayOperation(array));
    }

    /**
        @see WrappingWriter#write(char[], int, int)
    */
    public void write(char[] array, int offset, int length)
    {
        add(new WriteCharSubarrayOperation(array, offset, length));
    }


    // Protected methods

    /**
        Adds the specified operation to our list of operations.

        @param op the operation to add to our list of operations
    */
    protected void add(Operation op)
    {
        Assert.require(op != null);

        // Note: _operations will be null until our superclass'
        // constructor is finished. (Any writers that we play back
        // onto will have already had such operations performed on
        // them by their constructors.)
        if (_operations != null)
        {
            _operations.add(op);
        }
    }


    // Main method

    /**
        A main method that tests an IndentWriter's basic functionality.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        int maxLevel = 4;
        String filename = "recording-indent-writer-test.txt";
        if (args.length > 0)
        {
            filename = args[0];
        }

        IndentWriter w = null;
        try
        {
            RecordingIndentWriter recorder = new RecordingIndentWriter();
            for (int i = 0; i < maxLevel; i++)
            {
                recorder.write("level ");
                recorder.write(String.valueOf(i));
                recorder.write(Io.NL);
                recorder.incrementIndentLevel();
            }
            for (int i = maxLevel; i >= 0; i--)
            {
                recorder.write("level ");
                recorder.write(String.valueOf(i));
                recorder.write(Io.NL);
                if (i > 0)
                {
                    recorder.decrementIndentLevel();
                }
            }

            w = IndentWriter.createClosing(new FileWriter(filename, true));
            recorder.playBack(w);
        }
        catch (IOException ex)
        {
            String msg = "an I/O error of class " +
                ex.getClass().getName() + " caused writing using an " +
                "IndentWriter to fail: " + ex.getLocalizedMessage();
            System.err.println("\n" + msg + "\n\n");
        }
        finally
        {
            Io.tryToClose(w);
        }
    }


    // Inner operation classes

    /**
        The interface implemented by all of the operations that
        instances of this class can record.
    */
    protected static interface Operation
    {
        /**
            Performs this operation on the specified IndentWriter.

            @param w the IndentWriter to perform this operation on
            @exception IOException throws whatever IOException is
            thrown by 'w' (if any) when this operation is performed
            on it
        */
        public void perform(IndentWriter w)
            throws IOException;
    }

    /**
        Represents the setIndentSize() operation.
    */
    protected static class SetIndentSizeOperation
        implements Operation
    {
        // Private fields

        /** The new indent size. */
        private int _size;


        // Constructors

        /**
            Constructs a SetIndentSizeOperation from the indent size
            it is to set on IndentWriters.

            @param size the indent size the operation is to set on an
            IndentWriter
        */
        public SetIndentSizeOperation(int size)
        {
            _size = size;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
        {
            w.setIndentSize(_size);
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName() + ": " + _size;
        }
    }

    /**
        Represents the setIndentLevel() operation.
    */
    protected static class SetIndentLevelOperation
        implements Operation
    {
        // Private fields

        /** The new indent level. */
        private int _level;


        // Constructors

        /**
            Constructs a SetIndentLevelOperation from the indent level
            it is to set on IndentWriters.

            @param level the indent level the operation is to set on an
            IndentWriter
        */
        public SetIndentLevelOperation(int level)
        {
            _level = level;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
        {
            w.setIndentLevel(_level);
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName() + ": " + _level;
        }
    }

    /**
        Represents the incrementIndentLevel() operation.
    */
    protected static class IncrementIndentLevelOperation
        implements Operation
    {
        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
        {
            w.incrementIndentLevel();
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName();
        }
    }

    /**
        Represents the decrementIndentLevel() operation.
    */
    protected static class DecrementIndentLevelOperation
        implements Operation
    {
        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
        {
            w.decrementIndentLevel();
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName();
        }
    }

    /**
        Represents the write(int) operation.
    */
    protected static class WriteCharOperation
        implements Operation
    {
        // Private fields

        /** The character that this operation is to write. */
        private int _ch;


        // Constructors

        /**
            Constructs a WriteCharOperation from the character that
            it is to write on IndentWriters.

            @param ch the character that the operation is to write on
            IndentWriters
        */
        public WriteCharOperation(int ch)
        {
            _ch = ch;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
            throws IOException
        {
            w.write(_ch);
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName() + ": '" + ((char) _ch) + "'";
        }
    }

    /**
        Represents the write(String) operation.
    */
    protected static class WriteStringOperation
        implements Operation
    {
        // Private fields

        /** The string that this operation is to write. */
        private String _str;


        // Constructors

        /**
            Constructs a WriteStringOperation from the string that
            it is to write on IndentWriters.

            @param str the string that the operation is to write on
            IndentWriters
        */
        public WriteStringOperation(String str)
        {
            _str = str;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
            throws IOException
        {
            w.write(str());
        }


        // Protected methods

        /**
            @return the string to write
        */
        protected String str()
        {
            return _str;
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName() + ": \"" + _str + "\"";
        }
    }

    /**
        Represents the write(String, int, int) operation.
    */
    protected static class WriteSubstringOperation
        extends WriteStringOperation
    {
        // Private fields

        /** The offset in the string of the start of the part to write. */
        private int _offset;

        /** The length of the substring to write. */
        private int _length;


        // Constructors

        /**
            Constructs a WriteSubstringOperation from the substring
            that it is to write on IndentWriters.

            @param str the string that the operation is to write part
            of on IndentWriters
            @param offset the offset in 'str' of the start of the
            substring to write
            @param length the length of the substring of 'str' to write
        */
        public WriteSubstringOperation(String str, int offset, int length)
        {
            super(str);
            _offset = offset;
            _length = length;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
            throws IOException
        {
            w.write(str(), _offset, _length);
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            StringBuffer result = new StringBuffer(getClass().getName());

            result.append(": \"").append(str()).append("\", ").
                   append(_offset).append(", ").append(_length);

            return result.toString();
        }
    }

    /**
        Represents the write(char[]) operation.
    */
    protected static class WriteCharArrayOperation
        implements Operation
    {
        // Private fields

        /** The character array that this operation is to write. */
        private char[] _array;


        // Constructors

        /**
            Constructs a WriteCharArrayOperation from the character
            array that it is to write on IndentWriters.

            @param array the character array that the operation is to
            write on IndentWriters
        */
        public WriteCharArrayOperation(char[] array)
        {
            _array = array;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
            throws IOException
        {
            w.write(array());
        }


        // Protected methods

        /**
            @return the array to write on IndentWriters
        */
        protected char[] array()
        {
            return _array;
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            return getClass().getName() + ": \"" + _array + "\"";
        }
    }

    /**
        Represents the write(char[], int, int) operation.
    */
    protected static class WriteCharSubarrayOperation
        extends WriteCharArrayOperation
    {
        // Private fields

        /** The offset in the array of the start of the part to write. */
        private int _offset;

        /** The length of the part of the array to write. */
        private int _length;


        // Constructors

        /**
            Constructs a WriteCharSubarrayOperation from the character
            subarray that it is to write on IndentWriters.

            @param array the character array that the operation is to
            write part of on IndentWriters
            @param offset the offset in 'array' of the start of the
            part to write
            @param length the length of the part of 'array' to write
        */
        public WriteCharSubarrayOperation(char[] array,
                                          int offset, int length)
        {
            super(array);
            _offset = offset;
            _length = length;
        }


        // Public methods

        /**
            @see RecordingIndentWriter.Operation#perform(IndentWriter)
        */
        public void perform(IndentWriter w)
            throws IOException
        {
            w.write(array(), _offset, _length);
        }

        /**
            @see Object#toString
        */
        public String toString()
        {
            StringBuffer result = new StringBuffer(getClass().getName());

            result.append(": \"").append(array()).append("\", ").
                   append(_offset).append(", ").append(_length);

            return result.toString();
        }
    }
}
