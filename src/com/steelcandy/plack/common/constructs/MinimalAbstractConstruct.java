/*
 Copyright (C) 2002-2015 by James MacKay.

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
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.PlackInternalError;

import com.steelcandy.common.io.Io;

import java.io.IOException;
import java.io.StringWriter;

/**
    A minimal abstract base class that all classes that represent a language
    construct should subclass.

    @author James MacKay
*/
public abstract class MinimalAbstractConstruct
    implements Construct
{
    // Private fields

    /**
        This construct's correctness-related data. It will be null once this
        construct's correctness has been established.
    */
    private ConstructCorrectnessData _correctnessData;


    // Constructors

    /**
        Constructs a MinimalAbstractConstruct.

        @param checklist the construct's validity constraint checklist
        @see Construct#setLocation
    */
    public MinimalAbstractConstruct(ValidityConstraintChecklist checklist)
    {
        Assert.require(checklist != null);

        _correctnessData = new ConstructCorrectnessData(checklist);
    }

    /**
        Copy constructor (more or less).

        @param c the construct that the construct is to be a copy of
        @param checklist the construct's validity constraint checklist
    */
    public MinimalAbstractConstruct(MinimalAbstractConstruct c,
                                    ValidityConstraintChecklist checklist)
    {
        Assert.require(c != null);
        Assert.require(checklist != null);

        _correctnessData =
            new ConstructCorrectnessData(c.correctnessData(), checklist);
    }


    // Public static methods

    /**
        Handles the internal error that occurred in the specified construct.

        @param level the severity level of the internal error
        @param description a description of the internal error
        @param c the construct that the internal error occurred in
        @param obj the language processor object that the error occurred in
        (e.g. a parser object)
        @param handler the error handler to use to handle the internal error
    */
    public static void handleInternalError(int level, String description,
                            Construct c, Object obj, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(obj != null);
        Assert.require(handler != null);

        SourceCode code = null;
        SourceLocation loc = null;
        if (c.hasCorrectnessData())
        {
            code = c.sourceCode();
            loc = c.location();
        }

        handler.handle(new PlackInternalError(level, description,
                                code, loc, obj.getClass(), obj));
    }

    /**
        Writes information about the specified construct to standard error.
        This method is intended for use in debugging only.

        @param c the construct
        @see Construct#write(ConstructWriter, SourceCode)
    */
    public static void debugWrite(Construct c)
    {
        Assert.require(c != null);

        TextConstructWriter w = null;
        try
        {
            w = new TextConstructWriter();
            w.setWriter(Io.err);
            w.doCloseWriter(false);  // otherwise stderr get closed

            SourceCode code = null;
            if (c.hasCorrectnessData())
            {
                code = c.sourceCode();
            }
            c.write(w, code);
        }
        catch (IOException ex)
        {
            System.err.println("I/O error occurred writing construct " +
                               "debugging information: " +
                               ex.getLocalizedMessage());
        }
        finally
        {
            AbstractConstructWriter.tryToClose(w);
        }
    }


    // Public methods

    /**
        @see Construct#write(ConstructWriter, SourceCode)
    */
    public void write(ConstructWriter w, SourceCode source)
        throws IOException
    {
        Assert.require(w != null);
        // 'source' can be null

        write(w, null, source);
    }

    /**
        @see Construct#safe
    */
    public SafeConstruct safe()
    {
        SafeConstruct result = new SafeConstruct(this);

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#toUnsafeConstruct
    */
    public Construct toUnsafeConstruct()
    {
        // Assert.ensure(result != null);
        return this;
    }


    /**
        @see Object#toString
    */
    public String toString()
    {
        String result;

        StringWriter sw = null;
        TextConstructWriter w = null;
        try
        {
            sw = new StringWriter();
            w = new TextConstructWriter();
            w.setWriter(sw);

            SourceCode source = null;
            if (_correctnessData != null)
            {
                source = sourceCode();
            }

            write(w, source);
            result = sw.toString();
        }
        catch (IOException ex)
        {
            result = null;
            if (sw != null)
            {
                result = sw.toString();
            }
        }
        finally
        {
            AbstractConstructWriter.tryToClose(w);
                // which close()s 'sw' too
        }

        Assert.ensure(result != null);
        return result;
    }


    // Correctness-related methods

    /**
        @see Construct#hasCorrectnessData
    */
    public boolean hasCorrectnessData()
    {
        return (_correctnessData != null);
    }

    /**
        @see Construct#removeCorrectnessData
    */
    public void removeCorrectnessData()
    {
        _correctnessData = null;
    }

    /**
        @see Construct#validityChecklist
    */
    public ValidityConstraintChecklist validityChecklist()
        throws MissingConstructCorrectnessDataException
    {
        handleMissingCorrectnessData();

        ValidityConstraintChecklist result =
            _correctnessData.validityChecklist();

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see Construct#setSourceCode(SourceCode)
    */
    public void setSourceCode(SourceCode code)
        throws MissingConstructCorrectnessDataException
    {
        Assert.require(code != null);

        handleMissingCorrectnessData();
        _correctnessData.setSourceCode(code);
    }

    /**
        @see Construct#sourceCode
    */
    public SourceCode sourceCode()
    {
        handleMissingCorrectnessData();

        SourceCode result = _correctnessData.sourceCode();

        Assert.ensure(result != null);
        return result;
    }

    /**
        Handles the situation where an attempt is made to access this
        construct's correctness data when it doesn't contain its correctness
        data.

        @exception MissingConstructCorrectnessDataException thrown iff this
        construct does not contain its correctness data
    */
    protected void handleMissingCorrectnessData()
        throws MissingConstructCorrectnessDataException
    {
        if (_correctnessData == null)
        {
            throw new MissingConstructCorrectnessDataException(this);
        }
    }


    // Private methods

    /**
        @return our correctness data
    */
    private ConstructCorrectnessData correctnessData()
    {
        return _correctnessData;
    }
}
