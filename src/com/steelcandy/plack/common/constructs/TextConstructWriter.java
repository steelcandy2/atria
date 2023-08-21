/*
 Copyright (C) 2002-2004 by James MacKay.

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

import com.steelcandy.common.Resources;

import java.io.IOException;
import java.text.MessageFormat;

/**
    A ConstructWriter that outputs constructs in a text format.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class TextConstructWriter
    extends AbstractConstructWriter
{
    // Resource constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        ID_NAME_FORMAT_MSG = "ID_NAME_FORMAT_MSG";
    private static final String
        ID_VALUE_FORMAT_MSG = "ID_VALUE_FORMAT_MSG";
    private static final String
        ID_NAME_AND_VALUE_FORMAT_MSG = "ID_NAME_AND_VALUE_FORMAT_MSG";
    private static final String
        DESCRIPTION_FORMAT_MSG = "DESCRIPTION_FORMAT_MSG";
    private static final String
        VALUE_FORMAT_MSG = "VALUE_FORMAT_MSG";
    private static final String
        LOCATION_FORMAT_MSG = "LOCATION_FORMAT_MSG";
    private static final String
        SOURCE_CODE_FORMAT_MSG = "SOURCE_CODE_FORMAT_MSG";
    private static final String
        SUBCONSTRUCT_NAME_FORMAT_MSG = "SUBCONSTRUCT_NAME_FORMAT_MSG";


    // Part formatter constants

    /** Formats a construct's ID name. */
    private static final MessageFormat _idNameFormatter =
        createFormat(ID_NAME_FORMAT_MSG);

    /** Formats a construct's ID value. */
    private static final MessageFormat _idValueFormatter =
        createFormat(ID_VALUE_FORMAT_MSG);

    /** Formats a construct's ID name and value. */
    private static final MessageFormat _idNameAndValueFormatter =
        createFormat(ID_NAME_AND_VALUE_FORMAT_MSG);

    /** Formats a construct's description. */
    private static final MessageFormat _descriptionFormatter =
        createFormat(DESCRIPTION_FORMAT_MSG);

    /** Formats a construct's value. */
    private static final MessageFormat _valueFormatter =
        createFormat(VALUE_FORMAT_MSG);

    /** Formats a construct's location. */
    private static final MessageFormat _locationFormatter =
        createFormat(LOCATION_FORMAT_MSG);

    /** Formats a construct's source code. */
    private static final MessageFormat _sourceCodeFormatter =
        createFormat(SOURCE_CODE_FORMAT_MSG);

    /** Formats a subconstruct's (or subconstruct list's) name. */
    private static final MessageFormat _subconstructNameFormatter =
        createFormat(SUBCONSTRUCT_NAME_FORMAT_MSG);


    // Constructors

    /**
        Constructs a TextConstructWriter from its configuration. Its writer
        must be set using setWriter() or everything it writes will be
        discarded.

        @param configuration the writer's configuration
    */
    public TextConstructWriter(ConstructWriterConfiguration configuration)
    {
        super(configuration);
    }

    /**
        Constructs a TextConstructWriter with the default configuration.
    */
    public TextConstructWriter()
    {
        // empty
    }


    // Output methods

    /**
        @see ConstructWriter#writeConstructStart(Construct, String, SourceCode)
    */
    public void writeConstructStart(Construct c, String name,
                                    SourceCode source)
        throws IOException
    {
        Assert.require(c != null);
        // 'name' can be null
        // 'source' can be null

        writeConstructId(c);
        writeConstructDescription(c);
        writeConstructValue(c);
        writeConstructLocation(c);
        writeConstructSourceCode(c, source);
    }

    /**
        @see ConstructWriter#writeConstructEnd(Construct, String, SourceCode)
    */
    public void writeConstructEnd(Construct c, String name, SourceCode source)
        throws IOException
    {
        Assert.require(c != null);
        // 'name' can be null
        // 'source' can be null

        // empty - we have nothing to write here
    }

    /**
        @see AbstractConstructWriter#writeExistingSubconstruct(String, Construct, SourceCode)
    */
    protected void writeExistingSubconstruct(String name, Construct c,
                                             SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        Assert.require(c != null);
        // 'source' can be null

        writeConstructName(name);

        incrementIndentLevel();
        c.write(this, source);
        decrementIndentLevel();
    }

    /**
        @see AbstractConstructWriter#writeNonEmptyConstructList(String, ConstructList, SourceCode)
    */
    protected void
        writeNonEmptyConstructList(String name, ConstructList list,
                                   SourceCode source)
        throws IOException
    {
        Assert.require(name != null);
        Assert.require(list != null);
        Assert.require(list.isEmpty() == false);
        // 'source' can be null

        writeConstructName(name);

        incrementIndentLevel();
        ConstructIterator iter = list.iterator();
        while (iter.hasNext())
        {
            iter.next().write(this, source);
        }
        decrementIndentLevel();
    }


    /**
        @see AbstractConstructWriter#reportMissingRequiredSubconstruct(String)
    */
    protected void reportMissingRequiredSubconstruct(String name)
        throws IOException
    {
        writeLine(getMissingRequiredSubconstructMessage(name));
    }

    /**
        @see AbstractConstructWriter#reportMissingOptionalSubconstruct(String)
    */
    protected void reportMissingOptionalSubconstruct(String name)
        throws IOException
    {
        writeLine(getMissingOptionalSubconstructMessage(name));
    }

    /**
        @see AbstractConstructWriter#reportMissingRequiredSubconstructList(String)
    */
    protected void reportMissingRequiredSubconstructList(String name)
        throws IOException
    {
        writeLine(getMissingRequiredSubconstructListMessage(name));
    }

    /**
        @see AbstractConstructWriter#reportMissingOptionalSubconstructList(String)
    */
    protected void reportMissingOptionalSubconstructList(String name)
        throws IOException
    {
        writeLine(getMissingOptionalSubconstructListMessage(name));
    }


    /**
        @see AbstractConstructWriter#outputFlags(String)
    */
    protected void outputFlags(String flagsText)
        throws IOException
    {
        writeLine(flagsText);
    }

    /**
        @see AbstractConstructWriter#outputConstructIdNameAndValue(String, int)
    */
    protected void outputConstructIdNameAndValue(String name, int value)
        throws IOException
    {
        writeLine(_idNameAndValueFormatter.
            format(new Object[] { name, String.valueOf(value) }));
    }

    /**
        @see AbstractConstructWriter#outputConstructIdName(String)
    */
    protected void outputConstructIdName(String name)
        throws IOException
    {
        writeLine(_idNameFormatter.format(new Object[] { name }));
    }

    /**
        @see AbstractConstructWriter#outputConstructIdValue(int)
    */
    protected void outputConstructIdValue(int value)
        throws IOException
    {
        writeLine(_idValueFormatter.
            format(new Object[] { String.valueOf(value) }));
    }

    /**
        @see AbstractConstructWriter#outputConstructName(String)
    */
    protected void outputConstructName(String name)
        throws IOException
    {
        writeLine(_subconstructNameFormatter.format(new Object[] { name }));
    }

    /**
        @see AbstractConstructWriter#outputConstructDescription(String)
    */
    protected void outputConstructDescription(String desc)
        throws IOException
    {
        writeLine(_descriptionFormatter.format(new Object[] { desc }));
    }

    /**
        @see AbstractConstructWriter#outputConstructValue(String)
    */
    protected void outputConstructValue(String value)
        throws IOException
    {
        writeLine(_valueFormatter.format(new Object[] { value }));
    }

    /**
        @see AbstractConstructWriter#outputConstructLocation(String)
    */
    protected void outputConstructLocation(String loc)
        throws IOException
    {
        writeLine(_locationFormatter.format(new Object[] { loc }));
    }

    /**
        @see AbstractConstructWriter#outputConstructSourceCode(String)
    */
    protected void outputConstructSourceCode(String source)
        throws IOException
    {
        writeLine(_sourceCodeFormatter.format(new Object[] { source }));
    }


    // Private static methods

    /**
        Creates and returns a message formatter whose format is the value of
        the resource with the specified identifier.

        @param resourceId the resource identifier of the resource whose value
        is the formatter's format
        @return a new message formatter whose format is the resource's value
    */
    private static MessageFormat createFormat(String resourceId)
    {
        return new MessageFormat(_resources.getMessage(resourceId));
    }
}
