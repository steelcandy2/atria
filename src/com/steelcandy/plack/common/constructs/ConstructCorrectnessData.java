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

/**
    A class that represents all of the data related to a construct's
    correctness.

    @author James MacKay
*/
public class ConstructCorrectnessData
{
    // Private fields

    /** The construct's validity constraint checklist. */
    private ValidityConstraintChecklist _validityChecklist;

    /** The piece of source code that contains this construct. */
    private SourceCode _sourceCode;


    // Constructors

    /**
        Constructs a ConstructCorrectnessData object from the construct's
        validity constraint checklist.

        @param checklist the construct's validity constraint checklist
    */
    public ConstructCorrectnessData(ValidityConstraintChecklist checklist)
    {
        Assert.require(checklist != null);

        _validityChecklist = checklist;
        _sourceCode = null;
    }

    /**
        Constructs a ConstructCorrectnessData object from the construct's
        validity constraint checklist and the ConstructCorrectnessData object
        from which it is to copy all of its other information.

        @param data the ConstructCorrectnessData object from which the
        data object being constructed is to copy all of its information
        except its validity constraint checklist
        @param checklist the construct's validity constraint checklist
    */
    public ConstructCorrectnessData(ConstructCorrectnessData data,
                                    ValidityConstraintChecklist checklist)
    {
        Assert.require(data != null);
        Assert.require(checklist != null);

        _sourceCode = data.sourceCode();
        _validityChecklist = checklist;
    }


    // Public methods

    /**
        Sets the piece of source code that contains our construct to the
        specified piece of source code.

        @param code the piece of source code that contains our construct
    */
    public void setSourceCode(SourceCode code)
    {
        Assert.require(code != null);

        _sourceCode = code;
    }

    /**
        @return the piece of source code that contains our construct
    */
    public SourceCode sourceCode()
    {
        return _sourceCode;
    }

    /**
        @return our construct's validity constraint checklist
    */
    public ValidityConstraintChecklist validityChecklist()
    {
        Assert.ensure(_validityChecklist != null);
        return _validityChecklist;
    }
}
