/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.plack.common.generation;

import com.steelcandy.plack.common.base.PlackRuntimeException;

import java.io.IOException;

/**
    The class of exception thrown when an IOException occurs while outputting
    target code during code generation.

    @author James MacKay
    @version $Revision: 1.2 $
*/
public class CodeGenerationIoException
    extends PlackRuntimeException
{
    // Constructors

    /**
        Constructs a CodeGenerationIoException.

        @param ex the IOException that occurred while trying to output the
        target code
    */
    public CodeGenerationIoException(IOException ex)
    {
        super(ex);
    }
}
