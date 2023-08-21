/*
 Copyright (C) 2001-2011 by James MacKay.

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

import com.steelcandy.common.text.TextUtilities;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
    A writer that indents each line it writes a specified number of levels.
    <p>
    An instance indents lines DEFAULT_INDENT_LEVEL levels unless and until
    its indent level is changed, and it indents DEFAULT_INDENT_SIZE spaces
    per indent level unless and until its indent size is changed.

    @author James MacKay
*/
public class IndentWriter
    extends PrefixWriter
{
    // Constants

    /** The default indent level. */
    private static final int DEFAULT_INDENT_LEVEL = 0;

    /** The default indent size. */
    private static final int DEFAULT_INDENT_SIZE = 4;

    /**
        The instance of this class that just discards everything written to
        it.

        @see #createDiscarding
    */
    private static final IndentWriter DISCARDING_INSTANCE =
        createNonclosing(DiscardWriter.instance());


    // Private fields

    /** The current indent level. */
    private int _indentLevel;

    /** The current size of a single indent level. */
    private int _indentSize;

    /** The prefix to prepend to the current line. */
    private String _currentPrefix;

    /** The prefix to prepend to lines after the current line. */
    private String _futurePrefix;


    // Constructors

    /**
        @return an IndentWriter that writes to standard out
    */
    public static IndentWriter createForStandardOutput()
    {
        IndentWriter result = createNonclosing(Io.out);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return an IndentWriter that writes to standard error
    */
    public static IndentWriter createForStandardError()
    {
        IndentWriter result = createNonclosing(Io.err);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return an IndentWriter that discards everything written to it
        @see DiscardWriter
    */
    public static IndentWriter createDiscarding()
    {
        Assert.ensure(DISCARDING_INSTANCE != null);
        return DISCARDING_INSTANCE;
    }

    /**
        @param w a writer
        @return an IndentWriter that wraps 'w', and that will close 'w' when
        it is closed
    */
    public static IndentWriter createClosing(Writer w)
    {
        Assert.require(w != null);

        IndentWriter result = new IndentWriter(w, true);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @param w a writer
        @return an IndentWriter that wraps 'w', and that will <em>not</em>
        automatically close 'w' when it is closed
    */
    public static IndentWriter createNonclosing(Writer w)
    {
        Assert.require(w != null);

        IndentWriter result = new IndentWriter(w, false);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Constructs an IndentWriter.

        @param w the writer to be wrapped
        @param closeWrappedWriter true if the writer's close() method should
        attempt to close its wrapped writer, and false if it shouldn't
        @see #setIndentLevel(int)
    */
    protected IndentWriter(Writer w, boolean closeWrappedWriter)
    {
        super(w, closeWrappedWriter);
        initialize();
    }


    // Public methods

    /**
        @return a new PostponedIndentWriter that can later be used to write
        out everything written to it to this IndentWriter
        @see PostponedIndentWriter
    */
    public PostponedIndentWriter createPostponed()
    {
        PostponedIndentWriter result =
            new PostponedIndentWriter(this);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Sets the size of each indent level (i.e. the number of spaces
        indented per indent level) to the specified size.

        @param size the new indent size
    */
    public void setIndentSize(int size)
    {
        Assert.require(size >= 0);

        if (size != _indentSize)
        {
            _indentSize = size;
            rebuildPrefix();
        }
    }

    /**
        Sets this writer's indent level to the specified level.

        @param level this writer's new indent level
    */
    public void setIndentLevel(int level)
    {
        Assert.require(level >= 0);

        if (level != _indentLevel)
        {
            _indentLevel = level;
            rebuildPrefix();
        }
    }

    /**
        Increments this writer's indent level by one level.
    */
    public void incrementIndentLevel()
    {
        setIndentLevel(_indentLevel + 1);
    }

    /**
        Decrements this writer's indent level by one level.
    */
    public void decrementIndentLevel()
    {
        Assert.require(indentLevel() > 0);

        setIndentLevel(_indentLevel - 1);
    }


    /**
        @return this writer's current indent level
    */
    public int indentLevel()
    {
        return _indentLevel;
    }

    /**
        @return this writer's current indent size
    */
    public int indentSize()
    {
        return _indentSize;
    }


    // Public static methods

    /**
        @param level a number of indent levels
        @param size the size of each indent level
        @return the String that is the prefix for a line idented 'level'
        levels, each of with is 'size' characters long
    */
    public static String prefix(int level, int size)
    {
        Assert.require(level >= 0);
        Assert.require(size >= 0);

        String result = TextUtilities.copies(" ", level * size);

        Assert.ensure(result != null);
        Assert.ensure(result.length() == level * size);
        return result;
    }


    // Protected methods

    /**
        @see LineFilterWriter#writeLine(String, String)
    */
    protected void writeLine(String line, String terminator)
        throws IOException
    {
        Assert.require(line != null);
        Assert.require(terminator != null);
        Assert.require(line.length() > 0 || terminator.length() > 0);

        super.writeLine(line, terminator);
        _currentPrefix = _futurePrefix;
    }

    /**
        @see PrefixWriter#getPrefix(String, String)
    */
    protected String getPrefix(String line, String terminator)
    {
        Assert.require(line != null);
        Assert.require(terminator != null);
        Assert.require(line.length() > 0 || terminator.length() > 0);

        return _currentPrefix;
    }


    // Private methods

    /**
        Initializes this writer to its default state.
    */
    private void initialize()
    {
        _indentSize = DEFAULT_INDENT_SIZE;
        _indentLevel = DEFAULT_INDENT_LEVEL;
        rebuildPrefix();
    }

    /**
        Rebuilds our prefix using our current indent level and indent size.
    */
    private void rebuildPrefix()
    {
        Assert.check(_indentSize >= 0);
        Assert.check(_indentLevel >= 0);

        _futurePrefix = prefix(_indentLevel, _indentSize);

        // We rebuild the current prefix iff the current line is empty
        // because usually (well, almost always) our indent level and/or
        // size is changed after the previous line has been written but
        // before the next has been.
        if (isCurrentLineEmpty())
        {
            _currentPrefix = _futurePrefix;
        }

        Assert.ensure(_currentPrefix != null);
        Assert.ensure(_futurePrefix != null);
    }


    // Main method

    /**
        A main method that tests an IndentWriter's basic functionality.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        int maxLevel = 4;
        String filename = "indent-writer-test.txt";
        if (args.length > 0)
        {
            filename = args[0];
        }

        IndentWriter w = null;
        try
        {
            w = IndentWriter.
                    createClosing(new FileWriter(filename, true));
            for (int i = 0; i < maxLevel; i++)
            {
                w.write("level ");
                w.write(String.valueOf(i));
                w.write(Io.NL);
                w.incrementIndentLevel();
            }
            for (int i = maxLevel; i >= 0; i--)
            {
                w.write("level ");
                w.write(String.valueOf(i));
                w.write(Io.NL);
                if (i > 0)
                {
                    w.decrementIndentLevel();
                }
            }
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
}
