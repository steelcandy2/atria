/*
 Copyright (C) 2002-2011 by James MacKay.

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

package com.steelcandy.plack.common.constructs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;

import com.steelcandy.common.io.Io;
import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.Resources;

import java.io.IOException;
import java.io.Writer;

/**
    An abstract base class for ConstructWriters.

    @author James MacKay
    @version $Revision: 1.7 $
*/
public abstract class AbstractConstructWriter
    implements ConstructWriter
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_FLAGS_TEXT_MSG = "NO_FLAGS_TEXT_MSG";
    private static final String
        NO_ID_NAME_MSG = "NO_ID_NAME_MSG";
    private static final String
        NO_NAME_MSG = "NO_NAME_MSG";
    private static final String
        NO_DESCRIPTION_MSG = "NO_DESCRIPTION_MSG";
    private static final String
        NO_VALUE_MSG = "NO_VALUE_MSG";
    private static final String
        NO_LOCATION_MSG = "NO_LOCATION_MSG";
    private static final String
        NO_SOURCE_CODE_MSG = "NO_SOURCE_CODE_MSG";

    private static final String
        MISSING_REQUIRED_SUBCONSTRUCT_MSG =
            "MISSING_REQUIRED_SUBCONSTRUCT_MSG";
    private static final String
        MISSING_OPTIONAL_SUBCONSTRUCT_MSG =
            "MISSING_OPTIONAL_SUBCONSTRUCT_MSG";
    private static final String
        MISSING_REQUIRED_SUBCONSTRUCT_LIST_MSG =
            "MISSING_REQUIRED_SUBCONSTRUCT_LIST_MSG";
    private static final String
        MISSING_OPTIONAL_SUBCONSTRUCT_LIST_MSG =
            "MISSING_OPTIONAL_SUBCONSTRUCT_LIST_MSG";


    // Missing part text constants

    /**
        The text output to represent the construct flags of a construct that
        has no flags set.
    */
    protected static final String
        NO_FLAGS_TEXT = _resources.getMessage(NO_FLAGS_TEXT_MSG);

    /**
        The text to output to represent the name of a construct ID when it
        cannot be obtained.
    */
    protected static final String
        NO_ID_NAME = _resources.getMessage(NO_ID_NAME_MSG);

    /**
        The text to output to represent the name of a construct when its
        name cannot be obtained.
    */
    protected static final String
        NO_NAME = _resources.getMessage(NO_NAME_MSG);

    /**
        The text to output to represent the description of a construct when
        it cannot be obtained.
    */
    protected static final String
        NO_DESCRIPTION = _resources.getMessage(NO_DESCRIPTION_MSG);

    /**
        The text to output to represent the value of a construct when it
        cannot be obtained (usually because the construct doesn't have a
        value).
    */
    protected static final String
        NO_VALUE = _resources.getMessage(NO_VALUE_MSG);

    /**
        The text to output to represent the location of a construct when it
        cannot be obtained.
    */
    protected static final String
        NO_LOCATION = _resources.getMessage(NO_LOCATION_MSG);

    /**
        The text to output to represent the source code that a construct
        represents when it cannot be obtained.
    */
    protected static final String
        NO_SOURCE_CODE = _resources.getMessage(NO_SOURCE_CODE_MSG);


    // Private fields

    /** The writer that is used to write the construct and its parts. */
    private IndentWriter _writer;

    /** Indicates whether we should close _writer when we're close()d. */
    private boolean _doCloseWriter;

    /** Our configuration. */
    private ConstructWriterConfiguration _configuration;


    // Constructors

    /**
        Constructs an AbstractConstructWriter from its configuration. Its
        writer must be set using setWriter() or everything it writes will be
        discarded. By default its writer will be close()d when it is closed.

        @param configuration the writer's configuration
        @see #close
        @see #setWriter(Writer)
        @see #doCloseWriter(boolean)
    */
    public AbstractConstructWriter(ConstructWriterConfiguration configuration)
    {
        Assert.require(configuration != null);

        _configuration = configuration;
        _doCloseWriter = true;
        setWriter(Io.none);
    }

    /**
        Constructs an AbstractConstructWriter with the default configuration.

        @see ConstructWriterConfiguration#ConstructWriterConfiguration
    */
    public AbstractConstructWriter()
    {
        this(new ConstructWriterConfiguration());
    }


    // Public methods

    /**
        @see ConstructWriter#setWriter(Writer)
    */
    public void setWriter(Writer w)
    {
        Assert.require(w != null);

        _writer = IndentWriter.createNonclosing(w);
    }

    /**
        @see ConstructWriter#doCloseWriter(boolean)
    */
    public void doCloseWriter(boolean doClose)
    {
        _doCloseWriter = doClose;
    }

    /**
        @see ConstructWriter#configuration
    */
    public ConstructWriterConfiguration configuration()
    {
        return _configuration;
    }

    /**
        @see ConstructWriter#setIndentSize(int)
    */
    public void setIndentSize(int size)
    {
        Assert.check(size >= 0);

        _writer.setIndentSize(size);
    }

    /**
        @see ConstructWriter#indentLevel
    */
    public int indentLevel()
    {
        return _writer.indentLevel();
    }

    /**
        @see ConstructWriter#indentSize
    */
    public int indentSize()
    {
        return _writer.indentSize();
    }

    /**
        @see ConstructWriter#close
    */
    public void close()
        throws IOException
    {
        if (_doCloseWriter)
        {
            _writer.close();
        }
    }

    /**
        Attempts to close the specified construct writer (if it isn't null).

        @param w the construct writer to try to close
        @see Io#tryToClose(Writer)
    */
    public static void tryToClose(ConstructWriter w)
    {
        if (w != null)
        {
            try
            {
                w.close();
            }
            catch (IOException ex)
            {
                // ignore failure to close
            }
        }
    }


    // Output methods

    /**
        @see ConstructWriter#writeFlags(int)
    */
    public void writeFlags(int flags)
        throws IOException
    {
        Assert.require(indentLevel() >= 0);

        ShowOption showFlags = config().showFlags();
        if (showFlags != HIDE)
        {
            if (flags == 0 && showFlags == ALWAYS_SHOW)
            {
                outputFlags(NO_FLAGS_TEXT);
            }
            else if (flags != 0)
            {
                outputFlags(flagsToText(flags));
            }
        }
    }

    /**
        @see ConstructWriter#writeSubconstruct(String, Construct, SourceCode)
    */
    public void writeSubconstruct(String name, Construct sc,
                                  SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        // 'sc' and/or 'source' can be null

        if (config().showSubconstructs() != HIDE)
        {
            // SHOW and ALWAYS_SHOW have the same effect for required
            // subconstructs.
            if (sc != null)
            {
                writeExistingSubconstruct(name, sc, source);
            }
            else
            {
                reportMissingRequiredSubconstruct(name);
            }
        }
    }

    /**
        @see ConstructWriter#writeOptionalSubconstruct(String, Construct, SourceCode)
    */
    public void writeOptionalSubconstruct(String name, Construct sc,
                                          SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        // 'sc' and/or 'source' can be null

        ShowOption show = config().showSubconstructs();
        if (show != HIDE)
        {
            if (sc == null && show == ALWAYS_SHOW)
            {
                reportMissingOptionalSubconstruct(name);
            }
            else if (sc != null)
            {
                writeExistingSubconstruct(name, sc, source);
            }
        }
    }

    /**
        @see ConstructWriter#writeSubconstructList(String, ConstructList, SourceCode)
    */
    public void writeSubconstructList(String name, ConstructList list,
                                      SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        // 'list' and/or 'source' can be null

        if (config().showSubconstructs() != HIDE)
        {
            // SHOW and ALWAYS_SHOW have the same effect for required
            // subconstructs.
            if (list != null && list.isEmpty() == false)
            {
                writeNonEmptyConstructList(name, list, source);
            }
            else
            {
                reportMissingRequiredSubconstructList(name);
            }
        }
    }

    /**
        @see ConstructWriter#writeOptionalSubconstructList(String, ConstructList, SourceCode)
    */
    public void writeOptionalSubconstructList(String name, ConstructList list,
                                              SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        // 'list' and/or 'source' can be null

        ShowOption showSubconstructs = config().showSubconstructs();
        if (showSubconstructs != HIDE)
        {
            boolean isAbsent = (list == null) || list.isEmpty();
            if (isAbsent && showSubconstructs == ALWAYS_SHOW)
            {
                reportMissingOptionalSubconstructList(name);
            }
            else if (isAbsent == false)
            {
                writeNonEmptyConstructList(name, list, source);
            }
        }
    }

    /**
        Writes the name and value of the specified construct's ID.

        @param c the construct whose ID's name and value is to be written
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructId(Construct c)
        throws IOException
    {
        Assert.require(c != null);

        boolean showValue = true;
        if (config().showIdValue() == HIDE)
        {
            showValue = false;
        }

        boolean showName = false;
        String name = null;
        ShowOption showIdName = config().showIdName();
        if (showIdName != HIDE)
        {
            name = constructIdName(c);
            if (name == null && showIdName == ALWAYS_SHOW)
            {
                name = NO_ID_NAME;
                showName = true;
            }
            else if (name != null)
            {
                showName = true;
            }
        }
        Assert.check((showName == false) || (name != null));
            // showName implies (name != null)

        if (showName || showValue)
        {
            Object[] args;

            if (showName && showValue)
            {
                outputConstructIdNameAndValue(name, c.id());
            }
            else if (showValue)
            {
                outputConstructIdValue(c.id());
            }
            else  // showName
            {
                outputConstructIdName(name);
            }
        }
    }

    /**
        Writes the specified construct name.

        @param name the construct name, or null if the construct doesn't have
        a name
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructName(String name)
        throws IOException
    {
        // 'name' can be null

        ShowOption show = config().showName();
        if (show != HIDE)
        {
            boolean showName = false;
            if (name == null && show == ALWAYS_SHOW)
            {
                name = NO_NAME;
                showName = true;
            }
            else if (name != null)
            {
                showName = true;
            }

            if (showName)
            {
                outputConstructName(name);
            }
        }
    }

    /**
        Writes the specified construct's description.

        @param c the construct whose description is to be written
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructDescription(Construct c)
        throws IOException
    {
        Assert.require(c != null);

        ShowOption show = config().showDescription();
        if (show != HIDE)
        {
            boolean showDescription = false;
            String desc = constructDescription(c);
            if (desc == null && show == ALWAYS_SHOW)
            {
                desc = NO_DESCRIPTION;
                showDescription = true;
            }
            else if (desc != null)
            {
                showDescription = true;
            }

            if (showDescription)
            {
                outputConstructDescription(desc);
            }
        }
    }

    /**
        Writes the specified construct's value.

        @param c the construct whose value is to be written
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructValue(Construct c)
        throws IOException
    {
        Assert.require(c != null);

        ShowOption show = config().showValue();
        if (show != HIDE)
        {
            boolean showValue = false;
            boolean isValue = c.hasValue();
            String value = null;
            if (isValue == false && show == ALWAYS_SHOW)
            {
                value = NO_VALUE;
                showValue = true;
            }
            else if (isValue)
            {
                value = c.value();
                showValue = true;
            }

            if (showValue)
            {
                outputConstructValue(value);
            }
        }
    }

    /**
        Writes the specified construct's location.

        @param c the construct whose location is to be written
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructLocation(Construct c)
        throws IOException
    {
        Assert.require(c != null);

        ShowOption show = config().showLocation();
        if (show != HIDE)
        {
            boolean showLocation = false;
            String loc = null;
            if (c.location() != null)
            {
                loc = c.location().toString();
            }

            if (loc == null && show == ALWAYS_SHOW)
            {
                loc = NO_LOCATION;
                showLocation = true;
            }
            else if (loc != null)
            {
                showLocation = true;
            }

            if (showLocation)
            {
                outputConstructLocation(loc);
            }
        }
    }

    /**
        Writes the source code fragment that the specified construct
        represents.

        @param c the construct whose source code is to be written
        @param source the source code that 'c' represents a fragment of, or
        null if it wasn't available
        @exception IOException thrown if an error occurs writing the
        construct information
    */
    protected void writeConstructSourceCode(Construct c,
                                            SourceCode source)
        throws IOException
    {
        Assert.require(c != null);
        // 'source' can be null

        ShowOption show = config().showSourceCode();
        if (show != HIDE)
        {
            boolean showSourceCode = false;
            String sourceCode = null;
            if (source != null && c.location() != null)
            {
                sourceCode = source.fragmentAt(c.location());
            }

            if (sourceCode == null && show == ALWAYS_SHOW)
            {
                sourceCode = NO_SOURCE_CODE;
                showSourceCode = true;
            }
            else if (sourceCode != null)
            {
                showSourceCode = true;
            }

            if (showSourceCode)
            {
                outputConstructSourceCode(sourceCode);
            }
        }
    }


    // Protected methods

    /**
        @return the writer to use to output the construct and its parts
    */
    protected IndentWriter writer()
    {
        Assert.ensure(_writer != null);
        return _writer;
    }

    /**
        @return this writer's configuration
    */
    protected ConstructWriterConfiguration config()
    {
        return _configuration;
    }

    /**
        Increments the current indent level by one level.
    */
    protected void incrementIndentLevel()
    {
        _writer.incrementIndentLevel();
    }

    /**
        Decrements the current indent level by one level.
    */
    protected void decrementIndentLevel()
    {
        Assert.require(indentLevel() > 0);

        _writer.decrementIndentLevel();
    }

    /**
        Writes the specified string using our writer.

        @param str the string to write
        @exception IOException thrown if an error occurs while writing
    */
    protected void write(String str)
        throws IOException
    {
        writer().write(str);
    }

    /**
        Writes the specified string followed by the current platform's line
        separator using our writer.

        @param str the string to write followed by a line separator
        @exception IOException thrown if an error occurs while writing
    */
    protected void writeLine(String str)
        throws IOException
    {
        Io.writeLine(writer(), str);
    }

    /**
        Converts the specified set of construct flags to the text to output
        to represent them.
        <p>
        This implementation just returns the flags' numeric value as a
        String. Subclasses may want to override this method to provide a
        better result.
    */
    protected String flagsToText(int flags)
    {
        Assert.require(flags != 0);

        return String.valueOf(flags);
    }


    /**
        Returns a message indicating that a required subconstruct with the
        specified name is missing.

        @param subconstructName the name of the missing subconstruct
        @return a message indicating that the subconstruct is missing
    */
    protected String
        getMissingRequiredSubconstructMessage(String subconstructName)
    {
        return _resources.
            getMessage(MISSING_REQUIRED_SUBCONSTRUCT_MSG,
                       subconstructName);
    }

    /**
        Returns a message indicating that an optional subconstruct with the
        specified name is missing.

        @param subconstructName the name of the missing subconstruct
        @return a message indicating that the subconstruct is missing
    */
    protected String
        getMissingOptionalSubconstructMessage(String subconstructName)
    {
        return _resources.
            getMessage(MISSING_OPTIONAL_SUBCONSTRUCT_MSG,
                       subconstructName);
    }

    /**
        Returns a message indicating that a required subconstruct list with
        the specified name is missing.

        @param subconstructListName the name of the missing subconstruct list
        @return a message indicating that the subconstruct list is missing
    */
    protected String
        getMissingRequiredSubconstructListMessage(String subconstructListName)
    {
        return _resources.
            getMessage(MISSING_REQUIRED_SUBCONSTRUCT_LIST_MSG,
                       subconstructListName);
    }

    /**
        Returns a message indicating that an optional subconstruct list with
        the specified name is missing.

        @param subconstructListName the name of the missing subconstruct list
        @return a message indicating that the subconstruct list is missing
    */
    protected String
        getMissingOptionalSubconstructListMessage(String subconstructListName)
    {
        return _resources.
            getMessage(MISSING_OPTIONAL_SUBCONSTRUCT_LIST_MSG,
                       subconstructListName);
    }


    /**
        Returns the name of the specified construct's ID, or null if its name
        is not available.
        <p>
        This implementation always returns null: subclasses may want to
        override this method to provide a better result.

        @param c the construct whose ID's name is to be returned
        @return the name of the construct;s ID, or null if one isn't
        available
    */
    protected String constructIdName(Construct c)
    {
        Assert.require(c != null);

        return null;
    }

    /**
        Returns a description of the specified construct, or null if a
        description of the construct is not available.
        <p>
        This implementation always returns null: subclasses may want to
        override this method to provide a better result.

        @param c the construct to describe
        @return a description of the construct, or null if one isn't
        available
    */
    protected String constructDescription(Construct c)
    {
        Assert.require(c != null);

        return null;
    }


    // Abstract methods

    /**
        Writes the specified non-null subconstruct with the specified name
        using our writer.

        @param name the name of the subconstruct
        @param c the subconstruct to write
        @param source the source code that the subconstruct represents a
        fragment of, or null if it isn't available
        @exception IOException thrown if an error occurs writing the
        subconstruct
    */
    protected abstract void
        writeExistingSubconstruct(String name, Construct c,
                                  SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // Assert.require(c != null);
        // 'source' can be null

    /**
        Writes the specified non-empty subconstruct list with the specified
        name using our writer.

        @param name the name of the subconstruct list
        @param list the subconstruct list
        @param source the source code that each of the subconstructs in
        'list' represents a fragment of, or null if it isn't available
        @exception IOException thrown if an error occurs writing the
        subconstruct list
    */
    protected abstract void
        writeNonEmptyConstructList(String name, ConstructList list,
                                   SourceCode source)
        throws IOException;
        // Assert.require(name != null);
        // Assert.require(list != null);
        // Assert.require(list.isEmpty() == false);
        // 'source' can be null


    /**
        Reports that the required subconstruct with the specified name is
        missing.

        @param name the name of the missing subconstruct
        @exception IOException thrown if an error occurs in reporting the
        missing subconstruct
        @see #getMissingRequiredSubconstructMessage(String)
    */
    protected abstract void reportMissingRequiredSubconstruct(String name)
        throws IOException;

    /**
        Reports that the optional subconstruct with the specified name is
        missing.

        @param name the name of the missing subconstruct
        @exception IOException thrown if an error occurs in reporting the
        missing subconstruct
        @see #getMissingOptionalSubconstructMessage(String)
    */
    protected abstract void reportMissingOptionalSubconstruct(String name)
        throws IOException;

    /**
        Reports that the required subconstruct list with the specified name
        is missing.

        @param name the name of the missing subconstruct list
        @exception IOException thrown if an error occurs in reporting the
        missing subconstruct list
        @see #getMissingRequiredSubconstructListMessage(String)
    */
    protected abstract void
        reportMissingRequiredSubconstructList(String name)
        throws IOException;

    /**
        Reports that the optional subconstruct list with the specified name
        is missing.

        @param name the name of the missing subconstruct list
        @exception IOException thrown if an error occurs in reporting the
        missing subconstruct list
        @see #getMissingOptionalSubconstructListMessage(String)
    */
    protected abstract void
        reportMissingOptionalSubconstructList(String name)
        throws IOException;


    /**
        Outputs the specified construct flags (without checking whether they
        are to be shown or not).

        @param flagsText the text that represents a construct's flags
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputFlags(String flagsText)
        throws IOException;

    /**
        Outputs the specified construct ID name and value (without checking
        whether they are to be shown or not).

        @param name the construct ID's name
        @param value the construct ID's value
        @exception IOException thrown if an error occurs in outputting the
        information
        @see String#valueOf(int)
    */
    protected abstract void
        outputConstructIdNameAndValue(String name, int value)
        throws IOException;

    /**
        Outputs the specified construct ID name (without checking whether it
        is to be shown or not).
        <p>
        Note: this method is only called if just the ID's name is to be
        output: if both its name and value are to be output then
        outputConstructIdNameAndValue() will be called instead of this
        method.

        @param name the construct ID's name
        @exception IOException thrown if an error occurs in outputting the
        information
        @see #outputConstructIdNameAndValue(String, int)
    */
    protected abstract void outputConstructIdName(String name)
        throws IOException;

    /**
        Outputs the specified construct ID value (without checking whether it
        is to be shown or not).
        <p>
        Note: this method is only called if just the ID's value is to be
        output: if both its name and value are to be output then
        outputConstructIdNameAndValue() will be called instead of this
        method.

        @param value the construct ID's value
        @exception IOException thrown if an error occurs in outputting the
        information
        @see #outputConstructIdNameAndValue(String, int)
        @see String#valueOf(int)
    */
    protected abstract void outputConstructIdValue(int value)
        throws IOException;

    /**
        Outputs the specified construct name (without checking whether it is
        to be shown or not).

        @param name the construct name
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputConstructName(String name)
        throws IOException;

    /**
        Outputs the specified construct description (without checking
        whether it is to be shown or not).

        @param desc the construct description
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputConstructDescription(String desc)
        throws IOException;

    /**
        Outputs the specified construct value (without checking whether it is
        to be shown or not).

        @param value the construct value
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputConstructValue(String value)
        throws IOException;

    /**
        Outputs the specified construct location (without checking whether it
        is to be shown or not).

        @param loc a string representation of a construct's location
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputConstructLocation(String loc)
        throws IOException;

    /**
        Outputs the specified source code that a construct represents
        (without checking whether it is to be shown or not).

        @param source the source code that a construct represents
        @exception IOException thrown if an error occurs in outputting the
        information
    */
    protected abstract void outputConstructSourceCode(String source)
        throws IOException;
}
