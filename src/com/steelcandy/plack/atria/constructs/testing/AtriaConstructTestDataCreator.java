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

package com.steelcandy.plack.atria.constructs.testing;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.tokens.AtriaSourceCodeTokenizer;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.SourceCodeTokenizer;
import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.testing.AbstractConstructTestDataCreator; // javadocs only

import com.steelcandy.common.io.IndentWriter;

import java.io.IOException;

/**
    The construct test data creator class.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class AtriaConstructTestDataCreator
    extends AtriaConstructTestDataCreatorBase
{
    // Protected methods

    /**
        @see AbstractConstructTestDataCreator#generateDefaultConstruct(IndentWriter)
    */
    protected Construct generateDefaultConstruct(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        return generateDocumentConstruct(w);
    }

    /**
        @see AbstractConstructTestDataCreator#createTokenSourceCodeTokenizer(ErrorHandler)
    */
    protected SourceCodeTokenizer
        createTokenSourceCodeTokenizer(ErrorHandler handler)
    {
        Assert.require(handler != null);

        AtriaSourceCodeTokenizer result =
            new AtriaSourceCodeTokenizer(handler);

        Assert.ensure(result != null);
        return result;
    }


    // Main method

    /**
        The main method, which generates the test data indicated by
        the specified command line arguments.

        @param args the command line arguments
    */
    public static void main(String[] args)
    {
        AtriaConstructTestDataCreator creator =
            new AtriaConstructTestDataCreator();
        EXECUTOR.executeAndExit(creator, args);
    }
}
