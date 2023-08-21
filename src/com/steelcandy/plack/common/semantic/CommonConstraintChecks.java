/*
 Copyright (C) 2004-2014 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorKey;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;
import com.steelcandy.plack.common.errors.SemanticError;
import com.steelcandy.plack.common.errors.ValidationError;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.constructs.Construct;

import com.steelcandy.common.Resources;

import java.util.Map;

/**
    A base class for classes that contain the methods that actually perform
    most/all of a language's validity constraint checks.
    <p>
    This class provides a place to put language-independent checks or parts
    of checks. Subclasses provide a place to put constraint checks without
    worrying about where in the code processing process the checks should
    occur.

    @author James MacKay
*/
public class CommonConstraintChecks
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        DUPLICATE_NAME_MSG =
            "DUPLICATE_NAME_MSG";


    // Public static methods

    /**
        Handles the specified construct violating the validity constraint
        with the specified name on a construct.
        <p>
        Note that the construct that violates a constraint isn't necessarily
        the one that the validity constraint applies to: it could be a
        subconstruct, for example.
        <p>
        This method is public and static so that other validation-related
        classes can leverage its implementation.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param c the construct that did not satisfy the validity constraint
        @param handler the error handler to use to handle the validation
        error
    */
    public static void violatedConstraint(int level,
        String constraintName, String description, Construct c,
        ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(handler != null);

        internalViolatedConstraint(level, constraintName, description,
            c.sourceCode(), c.location(), null, handler);
    }

    /**
        Handles the specified construct violating the validity constraint
        with the specified name on a construct.
        <p>
        Note that the construct that violates a constraint isn't necessarily
        the one that the validity constraint applies to: it could be a
        subconstruct, for example.
        <p>
        This method is public and static so that other validation-related
        classes can leverage its implementation.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param c the construct that did not satisfy the validity constraint
        @param key the key to associate with the error used to report the
        violated constraint
        @param handler the error handler to use to handle the validation
        error
    */
    public static void violatedConstraint(int level,
        String constraintName, String description, Construct c,
        ErrorKey key, ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(key != null);
        Assert.require(handler != null);

        internalViolatedConstraint(level, constraintName, description,
            c.sourceCode(), c.location(), key, handler);
    }

    /**
        Handles the validation error described by the specified information
        using the specified error handler.
        <p>
        This method is public and static so that other validation-related
        classes can leverage its implementation.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param handler the error handler to use to handle the validation
        error
        @see ValidationError#ValidationError(int, String, String, SourceCode, SourceLocation)
    */
    public static void violatedConstraint(int level, String constraintName,
        String description, SourceCode code, SourceLocation loc,
        ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        // 'code' and/or 'loc' may be null
        Assert.require(handler != null);

        internalViolatedConstraint(level, constraintName, description,
                                   code, loc, null, handler);
    }

    /**
        Handles the validation error described by the specified information
        using the specified error handler.
        <p>
        This method is public and static so that other validation-related
        classes can leverage its implementation.

        @param level the error's severity level
        @param constraintName the name of the validity constraint that was
        not satisfied
        @param description the error's description
        @param code the piece of source code containing the error
        @param loc the location in the source code where the error occurred
        @param key the key to associate with the error used to report the
        violated constraint
        @param handler the error handler to use to handle the validation
        error
        @see ValidationError#ValidationError(int, String, String, SourceCode, SourceLocation, ErrorKey)
    */
    public static void violatedConstraint(int level, String constraintName,
        String description, SourceCode code, SourceLocation loc,
        ErrorKey key, ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        // 'code' and/or 'loc' may be null
        Assert.require(key != null);
        Assert.require(handler != null);

        internalViolatedConstraint(level, constraintName, description,
                                   code, loc, key, handler);
    }


    // Protected methods


    /**
        Reports the warning with message 'msg' about 'c' using 'handler'.

        @param c the construct we're to report a warning about
        @param msg the warning message to report
        @param handler the error handler to use to report the warning
    */
    protected void warn(Construct c, String msg, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(msg != null);
        Assert.require(handler != null);

        SourceCode code = null;
        if (c.hasCorrectnessData())
        {
            code = c.sourceCode();
        }

        // We intentionally don't use a ValidationError here since no
        // validity constraint has been violated.
        handler.handle(new SemanticError(WARNING_LEVEL, msg, code,
                                         c.location()));
    }

    /**
        Adds the specified zero-arity symbol table entry to the specified
        symbol table if the table doesn't already contain an entry with the
        same name. If the table itself  - that is, not counting any entries
        in any parent symbol tables - does already contain an entry with the
        same name then a violation of the constraint with the specified name
        is reported - iff the name is non-null - and the table is left
        unmodified.

        @param entry the arity zero symbol table entry to add to 'table'
        @param table the symbol table to (possibly) add 'entry' to
        @param constrainedConstruct the construct that violates the
        constraint named 'constraintName' if 'table' already contains an
        entry with the same name as 'entry'
        @param constraintName the name of the constraint that is violated if
        'table' already contains an entry with the same name as 'entry',
        or null if the duplicate entry is not to be reported (presumably
        because it has already been reported elsewhere)
        @param handler the error handler to use to report any constraint
        violations
        @return true iff there isn't already an entry in 'table' with the
        same name as 'entry'
        @exception InvalidSymbolTableEntryException thrown if 'entry' is not
        a valid entry for 'table'
    */
    protected boolean addZeroArityEntry(SymbolTableEntry entry,
        SymbolTable table, Construct constrainedConstruct,
        String constraintName, ErrorHandler handler)
        throws InvalidSymbolTableEntryException
    {
        Assert.require(entry != null);
        Assert.require(entry.arity() == 0);
        Assert.require(table != null);
        Assert.require(constrainedConstruct != null);
        // 'constraintName' may be null
        Assert.require(handler != null);

        return internalAddZeroArityEntry(entry, table, constrainedConstruct,
                                         constraintName, null, handler);
    }

    /**
        Adds the specified zero-arity symbol table entry to the specified
        symbol table if the table doesn't already contain an entry with the
        same name. If the table itself  - that is, not counting any entries
        in any parent symbol tables - does already contain an entry with the
        same name then a violation of the constraint with the specified name
        is reported - iff the name is non-null - and the table is left
        unmodified.

        @param entry the arity zero symbol table entry to add to 'table'
        @param table the symbol table to (possibly) add 'entry' to
        @param constrainedConstruct the construct that violates the
        constraint named 'constraintName' if 'table' already contains an
        entry with the same name as 'entry'
        @param constraintName the name of the constraint that is violated if
        'table' already contains an entry with the same name as 'entry',
        or null if the duplicate entry is not to be reported (presumably
        because it has already been reported elsewhere)
        @param key the key to associate with the error used to report the
        violated constraint
        @param handler the error handler to use to report any constraint
        violations
        @return true iff there isn't already an entry in 'table' with the
        same name as 'entry'
        @exception InvalidSymbolTableEntryException thrown if 'entry' is not
        a valid entry for 'table'
    */
    protected boolean addZeroArityEntry(SymbolTableEntry entry,
        SymbolTable table, Construct constrainedConstruct,
        String constraintName, ErrorKey key, ErrorHandler handler)
        throws InvalidSymbolTableEntryException
    {
        Assert.require(entry != null);
        Assert.require(entry.arity() == 0);
        Assert.require(table != null);
        Assert.require(constrainedConstruct != null);
        // 'constraintName' may be null
        Assert.require(key != null);
        Assert.require(handler != null);

        return internalAddZeroArityEntry(entry, table, constrainedConstruct,
                                         constraintName, key, handler);
    }

    /**
        @param desc a description of the type of symbol the duplicate symbol
        is
        @param symbol the duplicate symbol
        @param existingDesc a description of the type of the symbol that
        'symbol' is a duplicate of
        @param existingLoc the location in the source code where the symbol
        that 'symbol' is a duplicate of was defined
        @return a message that can be used to report a duplicate zero-arity
        symbol
    */
    protected String duplicateZeroAritySymbolMessage(String desc,
        String symbol, String existingDesc, SourceLocation existingLoc)
    {
        Object[] args = new Object[]
        {
            desc,
            symbol,
            existingDesc,
            lineNumberString(existingLoc)
        };

        String result = _resources.getMessage(DUPLICATE_NAME_MSG, args);

        Assert.ensure(result != null);
        return result;
    }


    /**
        Adds the specified construct with the specified name to the specified
        name-to-construct map if the map doesn't already contain an entry for
        that name. If the map does already contain an entry with that name
        then a violation of the constraint with the specified name is
        reported and the map is left unmodified.
        <p>
        Note: this method does <em>not</em> mark the constraint as having
        been checked.

        @param name the name associated with 'c'
        @param c the construct named 'name'
        @param m the name-to-construct map: its keys are Strings and its
        values are Constructs
        @param r the resources to look up 'messageResourceId' in
        @param messageResourceId the resource ID to use in constructing the
        message that used in reporting any violation of the constraint named
        'constraintName': it will be passed the name and the line number of
        the previous construct with the same name, respectively, as arguments
        @param constraintName the name of the constraint that is violated if
        'm' already contains an entry whose key is 'name'
        @param errorConstruct the construct whose location is to be used as
        the error location if there is already a construct named 'name'
        @param handler the error handler to use to report any constraint
        violations
        @return true iff there isn't already an entry in 'm' with 'name' as
        its key
    */
    protected boolean addNamedConstruct(String name, Construct c, Map m,
        Resources r, String messageResourceId, String constraintName,
        Construct errorConstruct, ErrorHandler handler)
    {
        Assert.require(name != null);
        Assert.require(c != null);
        Assert.require(m != null);
        Assert.require(r != null);
        Assert.require(messageResourceId != null);
        Assert.require(constraintName != null);
        Assert.require(errorConstruct != null);
        Assert.require(handler != null);

        boolean result;

        if (m.containsKey(name))
        {
            // There is already a construct named 'name' in 'm'.
            Construct old = (Construct) m.get(name);
            Assert.check(old != null);
            Assert.check(old.hasCorrectnessData());

            String msg = r.
                getMessage(messageResourceId, name,
                           lineNumberString(old.location()));
            violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                               msg, errorConstruct, handler);
            result = false;
        }
        else
        {
            // There wasn't already a construct named 'name' in 'm'.
            m.put(name, c);
            result = true;
        }

        return result;
    }

    /**
        Returns the line number of the line that the specified SourceLocation
        ends on, converted to a String.
        <p>
        Note: we use the end line number rather than the start line number
        since in languages that support prefixes several line-spanning
        constructs can start on the same line.

        @param loc the source location
        @return the line number that 'loc' ends on, as a String
        @see #startingLineNumberString(SourceLocation)
    */
    protected String lineNumberString(SourceLocation loc)
    {
        Assert.require(loc != null);

        return String.valueOf(loc.endLineNumber());
    }

    /**
        Returns the line number of the line that the specified SourceLocation
        starts on, converted to a String.

        @param loc the source location
        @return the line number that 'loc' starts on, as a String
        @see #lineNumberString(SourceLocation)
    */
    protected String startingLineNumberString(SourceLocation loc)
    {
        Assert.require(loc != null);

        return String.valueOf(loc.startLineNumber());
    }


    // Private static methods

    /**
        This method is used to implement the various versions of
        violatedConstraint(). It allows the error key to be null.

        @see #violatedConstraint(int, String, String, Construct, ErrorHandler)
        @see #violatedConstraint(int, String, String, Construct, ErrorKey, ErrorHandler)
        @see #violatedConstraint(int, String, String, SourceCode, SourceLocation, ErrorHandler)
        @see #violatedConstraint(int, String, String, SourceCode, SourceLocation, ErrorKey, ErrorHandler)
    */
    private static void internalViolatedConstraint(int level,
        String constraintName, String description, SourceCode code,
        SourceLocation loc, ErrorKey key, ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        // 'code' and/or 'loc' may be null
        // 'key' may be null
        Assert.require(handler != null);

        ValidationError error;
        if (key != null)
        {
            error = new ValidationError(level, constraintName, description,
                                        code, loc, key);
            key.setError(error);
        }
        else
        {
            error = new ValidationError(level, constraintName, description,
                                        code, loc);
        }

        handler.handle(error);
    }


    // Private methods

    /**
        This method is used to implement the various versions of
        addZeroArityEntry(). It allows the error key to be null.

        @see #addZeroArityEntry(SymbolTableEntry, SymbolTable, Construct, String, ErrorHandler)
        @see #addZeroArityEntry(SymbolTableEntry, SymbolTable, Construct, String, ErrorKey, ErrorHandler)
    */
    private boolean internalAddZeroArityEntry(SymbolTableEntry entry,
        SymbolTable table, Construct constrainedConstruct,
        String constraintName, ErrorKey key, ErrorHandler handler)
        throws InvalidSymbolTableEntryException
    {
        Assert.require(entry != null);
        Assert.require(entry.arity() == 0);
        Assert.require(table != null);
        Assert.require(constrainedConstruct != null);
        // 'constraintName' may be null
        // 'key' may be null
        Assert.require(handler != null);

        boolean result;

        try
        {
            table.add(entry);
            result = true;

            entry.addDirectlyShadowedEntries(table.
                                parentFindRecursively(entry.name(), 0));
        }
        catch (DuplicateSymbolTableEntryException ex)
        {
            // There is already a zero-arity entry with the same name as
            // 'entry'.
            if (constraintName != null)
            {
                SymbolTableEntryList
                    matchingEntries = table.findRecursively(entry.name(), 0);
                Assert.check(matchingEntries.isEmpty() == false);

                SymbolTableEntryIterator iter = matchingEntries.iterator();
                while (iter.hasNext())
                {
                    SymbolTableEntry matchingEntry = iter.next();
                    String msg = duplicateZeroAritySymbolMessage(entry.
                        kindDescription(), entry.name(), matchingEntry.
                        kindDescription(), matchingEntry.location());
                    if (key != null)
                    {
                        violatedConstraint(NON_FATAL_ERROR_LEVEL,
                            constraintName, msg, constrainedConstruct, key,
                            handler);
                    }
                    else
                    {
                        violatedConstraint(NON_FATAL_ERROR_LEVEL,
                            constraintName, msg, constrainedConstruct,
                            handler);
                    }
                }
            }  // if (constraintName != null)
            result = false;
        }  // catch

        return result;
    }
}
