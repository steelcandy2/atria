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
import com.steelcandy.common.text.TextUtilities;

import java.io.IOException;

/**
    A ConstructWriter that outputs constructs in an XML format.

    @author James MacKay
    @version $Revision: 1.3 $
*/
public class XmlConstructWriter
    extends AbstractConstructWriter
{
    // Constructors

    /**
        Constructs an XmlConstructWriter from its configuration. Its writer
        must be set using setWriter() or everything it writes will be
        discarded.

        @param configuration the writer's configuration
    */
    public XmlConstructWriter(ConstructWriterConfiguration configuration)
    {
        super(configuration);
    }

    /**
        Constructs an XmlConstructWriter with the default configuration.
    */
    public XmlConstructWriter()
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

        write("<construct");
        writeConstructName(name);
        writeConstructId(c);
        writeConstructDescription(c);
        writeConstructValue(c);
        writeConstructLocation(c);
        writeConstructSourceCode(c, source);
        writeLine(">");
        incrementIndentLevel();
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

        decrementIndentLevel();

        String endTag = "</construct>";
        if (name != null && config().showName() != HIDE)
        {
            write(endTag);
            write(" <!-- ");
            write(name);
            writeLine(" -->");
        }
        else
        {
            writeLine(endTag);
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
                writeLine(getMissingRequiredSubconstructMessage(name));
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
                writeLine(getMissingOptionalSubconstructMessage(name));
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
                writeLine(getMissingRequiredSubconstructListMessage(name));
            }
        }
    }

    /**
        @see ConstructWriter#writeOptionalSubconstructList(String, ConstructList, SourceCode)
    */
    public void
        writeOptionalSubconstructList(String name, ConstructList list,
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
                writeLine(getMissingOptionalSubconstructListMessage(name));
            }
            else if (isAbsent == false)
            {
                writeNonEmptyConstructList(name, list, source);
            }
        }
    }


    // Protected methods

    /**
        Writes an attribute with the specified name and value using our
        writer.
        <p>
        The attribute will be preceded with a space, and the attribute's
        value will be modified so that special XML characters (e.g. less than
        signs) are escaped.

        @param name the attribute's name
        @param value the attribute's value
        @exception IOException thrown if an error occurs writing out the
        attribute
    */
    protected void writeAttribute(String name, String value)
        throws IOException
    {
        write(" ");
        write(name);
        write("=\"");
        writeEscaped(value);
        write("\"");
    }

    /**
        Writes the specified string after escaping all special XML
        characters.

        @param str the string to write
        @exception IOException thrown if an error occurs in writing the
        escaped string
    */
    protected void writeEscaped(String str)
        throws IOException
    {
        write(TextUtilities.escapeXml(str));
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

        c.write(this, name, source);
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

        writeLine("<construct-list>");
        incrementIndentLevel();
        ConstructIterator iter = list.iterator();
        while (iter.hasNext())
        {
            iter.next().write(this, name, source);
        }
        decrementIndentLevel();
        writeLine("</construct-list>");
    }


    /**
        @see AbstractConstructWriter#reportMissingRequiredSubconstruct(String)
    */
    protected void reportMissingRequiredSubconstruct(String name)
        throws IOException
    {
        write("<error");
        writeAttribute("type", "missing-required-subconstruct");
        write(">");
        writeEscaped(getMissingRequiredSubconstructMessage(name));
        writeLine("</error>");
    }

    /**
        @see AbstractConstructWriter#reportMissingOptionalSubconstruct(String)
    */
    protected void reportMissingOptionalSubconstruct(String name)
        throws IOException
    {
        write("<error");
        writeAttribute("type", "missing-optional-subconstruct");
        write(">");
        writeEscaped(getMissingOptionalSubconstructMessage(name));
        writeLine("</error>");
    }

    /**
        @see AbstractConstructWriter#reportMissingRequiredSubconstructList(String)
    */
    protected void reportMissingRequiredSubconstructList(String name)
        throws IOException
    {
        write("<error");
        writeAttribute("type", "missing-required-subconstruct-list");
        write(">");
        writeEscaped(getMissingRequiredSubconstructListMessage(name));
        writeLine("</error>");
    }

    /**
        @see AbstractConstructWriter#reportMissingOptionalSubconstructList(String)
    */
    protected void reportMissingOptionalSubconstructList(String name)
        throws IOException
    {
        write("<error");
        writeAttribute("type", "missing-optional-subconstruct-list");
        write(">");
        writeEscaped(getMissingOptionalSubconstructListMessage(name));
        writeLine("</error>");
    }


    /**
        @see AbstractConstructWriter#outputFlags(String)
    */
    protected void outputFlags(String flagsText)
        throws IOException
    {
        writeAttribute("flags", flagsText);
    }

    /**
        @see AbstractConstructWriter#outputConstructIdNameAndValue(String, int)
    */
    protected void outputConstructIdNameAndValue(String name, int value)
        throws IOException
    {
        outputConstructIdName(name);
        outputConstructIdValue(value);
    }

    /**
        @see AbstractConstructWriter#outputConstructIdName(String)
    */
    protected void outputConstructIdName(String name)
        throws IOException
    {
        writeAttribute("id-name", name);
    }

    /**
        @see AbstractConstructWriter#outputConstructIdValue(int)
    */
    protected void outputConstructIdValue(int value)
        throws IOException
    {
        writeAttribute("id-value", String.valueOf(value));
    }

    /**
        @see AbstractConstructWriter#outputConstructName(String)
    */
    protected void outputConstructName(String name)
        throws IOException
    {
        writeAttribute("name", name);
    }

    /**
        @see AbstractConstructWriter#outputConstructDescription(String)
    */
    protected void outputConstructDescription(String desc)
        throws IOException
    {
        writeAttribute("description", desc);
    }

    /**
        @see AbstractConstructWriter#outputConstructValue(String)
    */
    protected void outputConstructValue(String value)
        throws IOException
    {
        writeAttribute("value", value);
    }

    /**
        @see AbstractConstructWriter#outputConstructLocation(String)
    */
    protected void outputConstructLocation(String loc)
        throws IOException
    {
        writeAttribute("location", loc);
    }

    /**
        @see AbstractConstructWriter#outputConstructSourceCode(String)
    */
    protected void outputConstructSourceCode(String source)
        throws IOException
    {
        writeAttribute("source-code", source);
    }
}
