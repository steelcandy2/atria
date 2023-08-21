/*
 Copyright (C) 2002-2008 by James MacKay.

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

import com.steelcandy.plack.common.tokens.Tokenizer;
import com.steelcandy.plack.common.constructs.Parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
    An ErrorHandler that handles errors by recording them, usually so that
    they can be transferred to another error handler later.

    @author James MacKay
*/
public class RecordingErrorHandler
    extends AbstractCountingErrorHandler
{
    // Private fields

    /**
        The list of the records of all of the errors received by this
        handler, in the order that they were received. Each item in the list
        is an ErrorRecord.
    */
    private List _errorRecords;


    // Constructors

    /**
        Constructs a RecordingErrorHandler that has no errors recorded yet.
    */
    public RecordingErrorHandler()
    {
        _errorRecords = new ArrayList();
    }


    // Public methods

    /**
        Clears all of the errors from this handler's record of errors.
    */
    public void clear()
    {
        _errorRecords.clear();
    }

    /**
        Transfers all of the errors recorded by this handler to the specified
        handler: the errors will be removed from this handler's record as
        they are added to the specified handler.

        @param handler the error handler to which to transfer all of this
        handler's recorded errors
    */
    public void transferErrorsTo(ErrorHandler handler)
    {
        Assert.require(handler != null);

        Iterator iter = _errorRecords.iterator();
        while (iter.hasNext())
        {
            ErrorRecord r = (ErrorRecord) iter.next();
            r.addErrorTo(handler);
            iter.remove();
        }
        Assert.check(_errorRecords.isEmpty());
    }


    // Error handling methods

    /**
        @see ErrorHandler#handle(PlackInternalError)
    */
    protected void handleError(PlackInternalError error)
    {
        Assert.require(error != null);

        addRecord(new InternalErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(ReadError)
    */
    protected void handleError(ReadError error)
    {
        Assert.require(error != null);

        addRecord(new ReadErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(ExternalDataError)
    */
    protected void handleError(ExternalDataError error)
    {
        Assert.require(error != null);

        addRecord(new ExternalDataErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(TokenizingError, Tokenizer)
    */
    protected void handleError(TokenizingError error, Tokenizer source)
    {
        Assert.require(error != null);
        Assert.require(source != null);

        addRecord(new TokenizingErrorRecord(error, source));
    }

    /**
        @see ErrorHandler#handle(ParsingError, Parser)
    */
    protected void handleError(ParsingError error, Parser parser)
    {
        Assert.require(error != null);
        Assert.require(parser != null);

        addRecord(new ParsingErrorRecord(error, parser));
    }

    /**
        @see ErrorHandler#handle(DocumentationError)
    */
    protected void handleError(DocumentationError error)
    {
        Assert.require(error != null);

        addRecord(new DocumentationErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(ValidationError)
    */
    protected void handleError(ValidationError error)
    {
        Assert.require(error != null);

        addRecord(new ValidationErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(SemanticError)
    */
    protected void handleError(SemanticError error)
    {
        Assert.require(error != null);

        addRecord(new SemanticErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(CodeGenerationError)
    */
    protected void handleError(CodeGenerationError error)
    {
        Assert.require(error != null);

        addRecord(new CodeGenerationErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(CompilerError)
    */
    protected void handleError(CompilerError error)
    {
        Assert.require(error != null);

        addRecord(new CompilerErrorRecord(error));
    }

    /**
        @see ErrorHandler#handle(RuntimeError)
    */
    protected void handleError(RuntimeError error)
    {
        Assert.require(error != null);

        addRecord(new RuntimeErrorRecord(error));
    }


    // Protected methods

    /**
        Adds the specified error record to the end of this error handler's
        list of error records.
    */
    protected void addRecord(ErrorRecord record)
    {
        Assert.require(record != null);

        _errorRecords.add(record);
    }


    // ErrorRecord inner classes

    /**
        The interface implemented by all classes that are a record of an
        error received by a RecordingErrorHandler.
    */
    private static interface ErrorRecord
    {
        /**
            Adds the error that this is a record of to the specified error
            handler.

            @param handler the error handler to add our error to
        */
        public void addErrorTo(ErrorHandler handler);
            // Assert.require(handler != null);
    }

    /**
        The class of ErrorRecord that records an internal error.
    */
    private static class InternalErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The internal error that this record records. */
        private PlackInternalError _error;


        // Constructor

        /**
            Constructs an InternalErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
        */
        public InternalErrorRecord(PlackInternalError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a read error.
    */
    private static class ReadErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The read error that this record records. */
        private ReadError _error;


        // Constructor

        /**
            Constructs a ReadErrorRecord from the error that it is to record.

            @param error the error that the error record is to record
        */
        public ReadErrorRecord(ReadError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records an external data error.
    */
    private static class ExternalDataErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The external data error that this record records. */
        private ExternalDataError _error;


        // Constructor

        /**
            Constructs an ExternalDataErrorRecord from the error that it is
            to record.

            @param error the error that the error record is to record
        */
        public ExternalDataErrorRecord(ExternalDataError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a tokenizing error.
    */
    private static class TokenizingErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The tokenizing error that this record records. */
        private TokenizingError _error;

        /** The tokenizer that reported the error. */
        private Tokenizer _tokenizer;


        // Constructor

        /**
            Constructs a TokenizingErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
            @param t the tokenizer that reported the error
        */
        public TokenizingErrorRecord(TokenizingError error,
                                     Tokenizer t)
        {
            Assert.require(error != null);
            Assert.require(t != null);

            _error = error;
            _tokenizer = t;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error, _tokenizer);
        }
    }

    /**
        The class of ErrorRecord that records a parsing error.
    */
    private static class ParsingErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The parsing error that this record records. */
        private ParsingError _error;

        /** The parser that reported the error. */
        private Parser _parser;


        // Constructor

        /**
            Constructs a ParsingErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
            @param p the parser that reported the error
        */
        public ParsingErrorRecord(ParsingError error, Parser p)
        {
            Assert.require(error != null);
            Assert.require(p != null);

            _error = error;
            _parser = p;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error, _parser);
        }
    }

    /**
        The class of ErrorRecord that records a documentation error.
    */
    private static class DocumentationErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The documentation error that this record records. */
        private DocumentationError _error;


        // Constructor

        /**
            Constructs a DocumentationErrorRecord from the error that it is
            to record.

            @param error the error that the error record is to record
        */
        public DocumentationErrorRecord(DocumentationError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a validation error.
    */
    private static class ValidationErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The validation error that this record records. */
        private ValidationError _error;


        // Constructor

        /**
            Constructs a ValidationErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
        */
        public ValidationErrorRecord(ValidationError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a semantic error.
    */
    private static class SemanticErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The semantic error that this record records. */
        private SemanticError _error;


        // Constructor

        /**
            Constructs a SemanticErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
        */
        public SemanticErrorRecord(SemanticError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a code generation error.
    */
    private static class CodeGenerationErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The code generation error that this record records. */
        private CodeGenerationError _error;


        // Constructor

        /**
            Constructs a CodeGenerationErrorRecord from the error that it is
            to record.

            @param error the error that the error record is to record
        */
        public CodeGenerationErrorRecord(CodeGenerationError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a compiler error.
    */
    private static class CompilerErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The compiler error that this record records. */
        private CompilerError _error;


        // Constructor

        /**
            Constructs a CompilerErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
        */
        public CompilerErrorRecord(CompilerError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }

    /**
        The class of ErrorRecord that records a runtime error.
    */
    private static class RuntimeErrorRecord
        implements ErrorRecord
    {
        // Private fields

        /** The runtime error that this record records. */
        private RuntimeError _error;


        // Constructor

        /**
            Constructs a RuntimeErrorRecord from the error that it is to
            record.

            @param error the error that the error record is to record
        */
        public RuntimeErrorRecord(RuntimeError error)
        {
            Assert.require(error != null);

            _error = error;
        }


        // Public methods

        /**
            @see RecordingErrorHandler.ErrorRecord#addErrorTo(ErrorHandler)
        */
        public void addErrorTo(ErrorHandler handler)
        {
            handler.handle(_error);
        }
    }
}
