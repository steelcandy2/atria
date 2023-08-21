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

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.io.Io;
import com.steelcandy.common.program.Program;
import com.steelcandy.common.program.ProgramException;
import com.steelcandy.common.xml.XmlException;
import com.steelcandy.common.xml.XmlUtilities;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
    A program that converts XML documents to Atria documents.

    @author  James MacKay
*/
public class XmlToAtriaConverterProgram
    implements Program
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        XmlToAtriaConverterResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        ARGUMENTS_SUMMARY_MSG =
            "ARGUMENTS_SUMMARY_MSG";
    private static final String
        USAGE_DESCRIPTION_MSG =
            "USAGE_DESCRIPTION_MSG";


    // Constructors

    /**
        Constructs an XmlToAtriaConverterProgram.
    */
    public XmlToAtriaConverterProgram()
    {
        // empty
    }


    // Public methods

    /**
        @see Program#execute(String[])
    */
    public void execute(String[] args)
        throws ProgramException
    {
        Assert.require(args != null);

        int numArgs = args.length;
        InputStream in = System.in;
        if (numArgs == 1)
        {
            try
            {
                File f = new File(args[0]);
                in = new FileInputStream(f);
            }
            catch (IOException ex)
            {
                throw EXECUTOR.createFailureException(this,
                                            ex.getLocalizedMessage());
            }
        }
        else if (numArgs > 1)
        {
            throw EXECUTOR.createBadUsageException(this);
        }

        IndentWriter w = null;
        try
        {
            SAXBuilder builder = new SAXBuilder(false);
            builder.setExpandEntities(false);
            Document doc = XmlUtilities.createDocument(builder, in);

            XmlToAtriaConverter converter = new XmlToAtriaConverter();

            w = IndentWriter.createForStandardOutput();
            converter.convert(doc, w);
        }
        catch (XmlException ex)
        {
            throw EXECUTOR.createFailureException(this,
                                            ex.getLocalizedMessage());
        }
        catch (IOException ex)
        {
            throw EXECUTOR.createFailureException(this,
                                            ex.getLocalizedMessage());
        }
        finally
        {
            Io.tryToClose(w);
        }
    }

    /**
        @see Program#argumentsSummary
    */
    public String argumentsSummary()
    {
        String result = _resources.getMessage(ARGUMENTS_SUMMARY_MSG);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Program#usageDescription
    */
    public String usageDescription()
    {
        String result = _resources.getMessage(USAGE_DESCRIPTION_MSG);

        Assert.ensure(result != null);
        return result;
    }


    // Main method

    /**
        Main method.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        XmlToAtriaConverterProgram p = new XmlToAtriaConverterProgram();
        EXECUTOR.executeAndExit(p, args);
    }
}
