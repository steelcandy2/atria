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
import com.steelcandy.common.program.Program;
import com.steelcandy.common.program.ProgramException;

import java.io.*;

/**
    A program that converts Atria document to XML.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class AtriaToXmlConverterProgram
    implements Program
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaToXmlConverterResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        ARGUMENTS_SUMMARY_MSG =
            "ARGUMENTS_SUMMARY_MSG",
        USAGE_DESCRIPTION_MSG =
            "USAGE_DESCRIPTION_MSG",
        CONVERSION_FAILED_MSG =
            "CONVERSION_FAILED_MSG";


    // Constructors

    /**
        Constructs an AtriaToXmlConverterProgram.
    */
    public AtriaToXmlConverterProgram()
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
        if (numArgs == 1)
        {
            File f = new File(args[0]);
            AtriaToXmlConverter converter = new AtriaToXmlConverter();
            try
            {
                converter.convert(f);
            }
            catch (AtriaConversionException ex)
            {
                String msg = _resources.
                    getMessage(CONVERSION_FAILED_MSG);
                throw EXECUTOR.createFailureException(this, msg);
            }
        }
        else
        {
            throw EXECUTOR.createBadUsageException(this);
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
        AtriaToXmlConverterProgram p = new AtriaToXmlConverterProgram();
        EXECUTOR.executeAndExit(p, args);
    }
}
