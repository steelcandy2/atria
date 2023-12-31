/*
 Copyright (C) 2005 by James MacKay.

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

package com.steelcandy.plack.atria.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.tokens.*;

import com.steelcandy.common.*;

/**
    The default tokenizer that completely tokenizes Atria source code.

    @author James MacKay
*/
public class AtriaDefaultTokenizer
    extends ReflectiveShellSourceCodeTokenizer
{
    // Constants

    /**
        The Resources object containing the information about this tokenizer,
        including the names of its filter subtokenizers' classes.
    */
    private static final Resources _infoResources =
        AtriaDefaultTokenizerResourcesLocator.resources;


    // Constructors

    /**
        Constructs an AtriaDefaultTokenizer.

        @param handler the error handler that all of the subtokenizers are
        to use to handle errors
    */
    public AtriaDefaultTokenizer(ErrorHandler handler)
    {
        super(_infoResources, handler);
        Assert.require(handler != null);
    }

    /**
        @see AbstractTokenizer#AbstractTokenizer(Resources, String, ErrorHandler)
    */
    public AtriaDefaultTokenizer(Resources subtokenizerResources,
                            String resourceKeyPrefix, ErrorHandler handler)
    {
        super(subtokenizerResources, resourceKeyPrefix, handler);
    }
}
