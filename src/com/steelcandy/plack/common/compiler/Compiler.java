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

package com.steelcandy.plack.common.compiler;

import com.steelcandy.common.debug.Assert;

/**
    The interface implemented by all classes that can compile a language's
    source code.

    @author James MacKay
*/
public interface Compiler
{
    // Public methods

    /**
        Compiles the source code indicated (directly or indirectly) by the
        specified arguments in the way described by the arguments.

        @param args the arguments passed to the compiler
        @exception InvalidCompilerArgumentsException thrown if 'args' is not
        a valid set of compiler arguments
    */
    public void compile(String[] args)
        throws InvalidCompilerArgumentsException;
}
