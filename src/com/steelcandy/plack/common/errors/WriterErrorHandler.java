/*
 Copyright (C) 2001-2008 by James MacKay.

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

package com.steelcandy.plack.common.errors;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.plack.common.tokens.Tokenizer;

import com.steelcandy.plack.common.constructs.Parser;

import com.steelcandy.common.Resources;
import com.steelcandy.common.io.Io;

import com.steelcandy.common.text.TextUtilities;

import java.io.*;

import java.util.HashSet;
import java.util.Set;

/**
    An error handler that reports all errors using a Writer.

    @author James MacKay
    @version $Revision: 1.23 $
*/
public class WriterErrorHandler
    extends AbstractCountingErrorHandler
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonErrorResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        ERROR_WRITING_ERROR_MSG_MSG =
            "ERROR_WRITING_ERROR_MSG_MSG";
    private static final String
        ERROR_WITH_POS_FORMAT =
            "ERROR_WITH_POS_FORMAT";
    private static final String
        DEFAULT_LEVEL_ERROR_WITH_POS_FORMAT =
            "DEFAULT_LEVEL_ERROR_WITH_POS_FORMAT";
    private static final String
        ERROR_WITHOUT_POS_FORMAT =
            "ERROR_WITHOUT_POS_FORMAT";
    private static final String
        DEFAULT_LEVEL_ERROR_WITHOUT_POS_FORMAT =
            "DEFAULT_LEVEL_ERROR_WITHOUT_POS_FORMAT";
    private static final String
        DISPLAYABLE_FATAL_ERROR_LEVEL_NAME_MSG =
            "DISPLAYABLE_FATAL_ERROR_LEVEL_NAME_MSG";


    // Private fields

    /** The Writer to use for all output. */
    private Writer _writer;

    /**
        All of the distinct keys from all of the errors that this handler
        has handled so far: each item is an ErrorKey.
    */
    private Set _errorKeys;


    // Constructors

    /**
        @return a WriterErrorHandler that writes to standard error
    */
    public static WriterErrorHandler createForStandardError()
    {
        WriterErrorHandler result = new WriterErrorHandler(Io.err);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Constructs a WriterErrorHandler.

        @param writer the Writer to use for all output
    */
    public WriterErrorHandler(Writer writer)
    {
        Assert.require(writer != null);

        setWriter(writer);
        _errorKeys = new HashSet();
    }


    // Public methods

    /**
        Sets the specified Writer to be this error handlers new Writer.

        @param newWriter this error handler's new writer
    */
    public void setWriter(Writer newWriter)
    {
        Assert.require(newWriter != null);

        _writer = newWriter;
    }


    // Error handling methods

    /**
        @see ErrorHandler#handle(PlackInternalError)
    */
    protected void handleError(PlackInternalError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(ReadError)
    */
    protected void handleError(ReadError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(ExternalDataError)
    */
    protected void handleError(ExternalDataError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(TokenizingError, Tokenizer)
    */
    protected void handleError(TokenizingError error, Tokenizer source)
    {
        Assert.require(error != null);
        Assert.require(source != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(ParsingError, Parser)
    */
    protected void handleError(ParsingError error, Parser parser)
    {
        Assert.require(error != null);
        Assert.require(parser != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(DocumentationError)
    */
    protected void handleError(DocumentationError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(ValidationError)
    */
    protected void handleError(ValidationError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(SemanticError)
    */
    protected void handleError(SemanticError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(CodeGenerationError)
    */
    protected void handleError(CodeGenerationError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(CompilerError)
    */
    protected void handleError(CompilerError error)
    {
        Assert.require(error != null);

        report(error);
    }

    /**
        @see ErrorHandler#handle(RuntimeError)
    */
    protected void handleError(RuntimeError error)
    {
        Assert.require(error != null);

        report(error);
    }


    // Protected methods

    /**
        @see AbstractErrorHandler#isDuplicate(PlackError)
    */
    protected boolean isDuplicate(PlackError error)
    {
        Assert.require(error != null);

        boolean result = false;

        ErrorKey key = error.key();
        if (key != null && _errorKeys.contains(key))
        {
            // We've already seen an error with the same key.
            result = true;
        }

        return result;
    }

    /**
        @see AbstractErrorHandler#beforeHandlingError(PlackError)
    */
    protected void beforeHandlingError(PlackError error)
    {
        Assert.require(error != null);

        super.beforeHandlingError(error);

        ErrorKey key = error.key();
        if (key != null)
        {
            _errorKeys.add(key);
        }
    }

    /**
        @see AbstractErrorHandler#afterHandlingError(PlackError)
    */
    protected void afterHandlingError(PlackError error)
    {
        Assert.require(error != null);

        super.afterHandlingError(error);
        handleFatalError(error);
    }

    /**
        Reports the specified error.
    */
    protected void report(PlackError error)
    {
        Assert.require(error != null);

        SourceLocation loc = error.location();
        if (loc != null)
        {
            report(error, loc);
        }
        else
        {
            reportWithoutPosition(error);
        }
    }

    /**
        Reports the specified error that occurred at the specified position
        in the source code.

        @param error the error to report
        @param loc the location in the source code where the error occurred
    */
    protected void report(PlackError error, SourceLocation loc)
    {
        Assert.require(error != null);
        Assert.require(loc != null);

        SourceCode code = error.sourceCode();
        String markedError;
        if (code != null)
        {
            try
            {
                markedError = markedErrorLine(code, loc, "    ");
            }
            catch (IOException ex)
            {
                markedError = "    [could not read source code]";
            }
        }
        else
        {
            markedError = "    [unknown source code]";
        }
        Assert.check(markedError != null);

        SourcePosition pos = loc.startPosition();
        Object[] args = new Object[]
        {
            displayableLevelName(error),
            SourceCode.fullName(code),
            String.valueOf(pos.lineNumber()),
            String.valueOf(pos.offset() + 1),
            error.description(),
            markedError
        };
        String format = (error.level() == defaultErrorLevel()) ?
            DEFAULT_LEVEL_ERROR_WITH_POS_FORMAT : ERROR_WITH_POS_FORMAT;
        String msg = _resources.getMessage(format, args);
        report(msg);
    }

    /**
        Reports the specified error as an error that did not occur at any
        specific known location in the source code.

        @param error the error to report
    */
    protected void reportWithoutPosition(PlackError error)
    {
        Assert.require(error != null);

        Object[] args = new Object[]
        {
            displayableLevelName(error),
            SourceCode.fullName(error.sourceCode()),
            error.description()
        };
        String format = (error.level() == defaultErrorLevel()) ?
            DEFAULT_LEVEL_ERROR_WITHOUT_POS_FORMAT :
            ERROR_WITHOUT_POS_FORMAT;
        String msg = _resources.getMessage(format, args);
        report(msg);
    }

    /**
        Reports the specified message.

        @param msg the message to report
    */
    protected synchronized void report(String msg)
    {
        try
        {
            Io.writeLine(_writer, msg);
            _writer.flush();
        }
        catch (IOException ex)
        {
            String errorMsg =
                _resources.getMessage(ERROR_WRITING_ERROR_MSG_MSG,
                                      getClass().getName());
            Io.writeLine(errorMsg);
            Io.writeLine(Io.err, msg);
        }
    }

    /**
        Returns a string consisting of two lines: the first is the line in
        the specified source code containing the specified position, and the
        second is blank except for character(s) indicating error's position
        in the line.

        @param code the source code containing the error
        @param loc the location of the error in the source code
        @param prefix the string to prepend to both lines
        @return the two lines that indicate where the error occurred
        @exception IOException thrown if an I/O error occurs while reading
        the source code of the line where the error occurred
    */
    protected String markedErrorLine(SourceCode code, SourceLocation loc,
                                     String prefix)
        throws IOException
    {
        Assert.require(code != null);
        Assert.require(loc != null);
        Assert.require(prefix != null);

        SourcePosition pos = loc.startPosition();
        String line =
            TextUtilities.trimNewlines(code.line(pos.lineNumber()));
        int len = line.length();
        StringBuffer lineBuffer = new StringBuffer(len);
        StringBuffer markBuffer = new StringBuffer(len);
        for (int i = 0; i < len; i++)
        {
            // convert tabs to 4 spaces:
            char ch = line.charAt(i);
            if (ch == '\t')
            {
                lineBuffer.append("    ");
                markBuffer.append((pos.offset() == i) ? "^^^^" : "    ");
            }
            else
            {
                lineBuffer.append(ch);
                markBuffer.append((pos.offset() == i) ? '^' : ' ');
            }
        }

        return prefix + lineBuffer.toString() + TextUtilities.NL +
               prefix + markBuffer.toString();
    }

    /**
        @param error an error
        @return the displayable version of 'error''s error severity level
        name
        @see PlackError#levelName
    */
    protected String displayableLevelName(PlackError error)
    {
        Assert.require(error != null);

        String result;

        if (error.isFatal())
        {
            result = _resources.
                getMessage(DISPLAYABLE_FATAL_ERROR_LEVEL_NAME_MSG);
        }
        else
        {
            result = error.levelName();
        }

        Assert.ensure(result != null);
        return result;
    }
}
