/*
 Copyright (C) 2001-2015 by James MacKay.

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

import com.steelcandy.plack.common.source.HasSourceLocation;
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.errors.ErrorHandler;

import java.io.IOException;

/**
    The interface implemented by all classes that represent a language
    construct (e.g. an expression, a statement, a class, etc.)
    <p>
    Constructs that represents parts of another construct are referred to as
    the latter's child constructs or children, and the construct itself is
    referred to as its children's parent construct or parent. A construct's
    child constructs are ordered and can be accessed by index.

    @author James MacKay
    @version $Revision: 1.23 $
*/
public interface Construct
    extends HasSourceLocation
{
    // Public methods

    /**
        @return the ID identifying which type of construct this construct
        object represents
    */
    public int id();

    /**
        Causes this construct to accept the specified visitor (i.e. by
        calling the appropriate <code>visit...()</code> method on it,
        passing itself as the parameter).
        <p>
        Since ConstructVisitor defines no <code>visit...()</code> methods,
        the visitor will almost always have to be cast to a (usually
        language-specific) subclass before it can be accepted by a Construct.

        @param visitor the visitor to accept
        @param handler the error handler to be used to handle any errors that
        occur while visiting
        @see ConstructVisitor
    */
    public void accept(ConstructVisitor visitor,
                       ErrorHandler handler);
        // Assert.require(visitor != null);
        // Assert.require(handler != null);


    /**
        Indicates whether this construct's location can be set.

        @return true if this construct's location can be set, and false if it
        can't
    */
    public boolean canSetLocation();

    /**
        Sets the location in the source code of the source fragment that this
        construct represents to the specified location.

        @param loc the new location of the source fragment that this
        construct represents
    */
    public void setLocation(SourceLocation loc);
        // Assert.require(canSetLocation());
        // Assert.require(loc != null);

    /**
        Indicates whether this construct has a value associated with it.
        Usually only 'leaf' constructs - such as literals and identifiers -
        have a value associated with them.

        @return true iff this construct has a value associated with it
    */
    public boolean hasValue();

    /**
        Returns the value associated with this construct (if any). Usually
        only leaf constructs have a value associated with them.
        <p>
        (As examples, a literal's value would be a string representation
        of its value, and an identifier's value would be the identifier
        itself.)

        @return the value associated with this construct
        @see #hasValue
    */
    public String value();
        // Assert.require(hasValue());


    /**
        Writes information about this construct using the specified
        construct writer. It will have no name associated with it.

        @param w the writer to use to output this construct's information
        @param source the source code that the construct represents a
        fragment of, of null if it isn't available
        @exception IOException thrown if an error occurs in outputting the
        information
        @see ConstructWriter
        @see #write(ConstructWriter, String, SourceCode)
    */
    public void write(ConstructWriter w, SourceCode source)
        throws IOException;
        // Assert.require(w != null);
        // 'source' can be null

    /**
        Writes information about this construct using the specified construct
        writer.

        @param w the writer to use to output this construct's information
        @param name this construct's name, or null if it doesn't have one
        (a construct usually only has a name when it is a subconstruct)
        @param source the source code that the construct represents a
        fragment of, of null if it isn't available
        @exception IOException thrown if an error occurs in outputting the
        information
        @see ConstructWriter
        @see #write(ConstructWriter, SourceCode)
    */
    public void write(ConstructWriter w, String name, SourceCode source)
        throws IOException;
        // Assert.require(w != null);
        // 'name' can be null
        // 'source' can be null


    /**
        Note: casting a Construct to a subtype can cause problems since it
        may not currently be as processed as required, and so for example
        may not have all of its attributes properly set.

        @return a representation of this construct that can't be cast to a
        (useful) subtype
        @see #toUnsafeConstruct
        @see SafeConstruct
    */
    public SafeConstruct safe();
        // Assert.ensure(result != null);

    /**
        Note: you shouldn't call this unless you know why casting this
        construct to a subtype won't cause any problems: such calls should
        usually be accompanied by a comment explaining <em>why</em> it won't
        cause any problems.

        @return a representation of this construct that <em>can</em> be cast
        to a (useful) subtype
        @see #safe
    */
    public Construct toUnsafeConstruct();
        // Assert.ensure(result != null);


    // Cloning methods

    /**
        Note: the attributes, subconstructs and validity constraint
        checklists of a deep clone of a construct are deep clones of the
        attributes, subconstructs and validity constraint checklists of
        the original construct. The original construct and its deep clones
        do share their source locations and source code objects, however.

        @return a construct that is a deep clone of this construct
    */
    public Construct deepClone();
        // Assert.ensure(result != null);

    /**
        Note: the attributes, subconstructs and validity constraint
        checklists of a shallow clone of a construct are the same objects
        as the attributes, subconstructs and validity constraint checklists
        of the original construct. The original construct and its shallow
        clones do share their source locations and source code objects,
        however.

        @return a construct that is a shallow clone of this construct
    */
    public Construct shallowClone();
        // Assert.ensure(result != null);


    // Correctness-related methods

    /**
        Indicates whether this construct still contains information used
        mainly - though not necessarily exclusively - in checking the
        construct's correctness.

        @return true if this construct still contains its correctness data,
        and false if it doesn't
    */
    public boolean hasCorrectnessData();

    /**
        Removes the correctness data from this construct.
    */
    public void removeCorrectnessData();

    /**
        @return this construct's validity constraint checklist
        @exception MissingConstructCorrectnessDataException thrown if this
        construct does not contain its correctness data
    */
    public ValidityConstraintChecklist validityChecklist()
        throws MissingConstructCorrectnessDataException;
        // Assert.ensure(result != null);

    /**
        Sets the piece of source code that contains this construct to the
        specified piece of source code.

        @param code the piece of source code that contains our construct
        @exception MissingConstructCorrectnessDataException thrown if this
        construct does not contain its correctness data
    */
    public void setSourceCode(SourceCode code)
        throws MissingConstructCorrectnessDataException;
        // Assert.require(code != null);

    /**
        @return the piece of source code that contains this construct
        @exception MissingConstructCorrectnessDataException thrown if this
        construct does not contain its correctness data
    */
    public SourceCode sourceCode()
        throws MissingConstructCorrectnessDataException;
}
