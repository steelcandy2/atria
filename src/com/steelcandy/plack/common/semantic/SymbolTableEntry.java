/*
 Copyright (C) 2003-2015 by James MacKay.

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

import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;

import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.constructs.ConstructList;

/**
    The interface implemented by all classes that represent an entry in a
    SymbolTable.
    <p>
    Notes:
    <ul>
        <li>the arity of an entry is the number of arguments taken by
            the variable, routine, etc. that the entry represents.
            (Though variables usually don't take any arguments: that is,
            their entries usually have arity zero)</li>
        <li>every entry is assumed to have at least one result type:
            that is, every symbol is assumed to evaluate to at least
            one result</li>
        <li>subclasses should override toString(): the representation of a
            symbol is language-specific (both in what information is
            included and how it is represented) and so toString() cannot
            usually be implemented in a common base class</li>
    </ul>

    @author James MacKay
    @version $Revision: 1.47 $
    @see SymbolTable
*/
public interface SymbolTableEntry
{
    // Public methods

    /**
        @return the name of this entry
    */
    public String name();
        // Assert.ensure(result != null);

    /**
        Returns a value that indicates what modifiers this entry has. What
        values are valid and what various values mean is language-dependent.

        @return a value that indicates what modifiers this entry has
    */
    public int modifiers();


    /**
        Note: examples of declarations are forward class declarations,
        abstract methods and function declarations. Variable declarations
        are considered to be definitions, even if they don't initialize
        their variable.

        @return true iff this entry just represents a declaration of
        its symbol, instead of a full definition
    */
    public boolean isDeclaration();

    /**
        @return the construct that defined (or possibly just declared)
        the symbol represented by this entry
        @exception EntryConstructRemovedException thrown iff the construct
        representing this entry's definition has been removed from it (to
        save memory once it has been fully compiled)
    */
    public Construct definition()
        throws EntryConstructRemovedException;
        // Assert.ensure(result != null);

    /**
        @return the source code that this entry's definition is contained
        in
        @exception EntryConstructRemovedException thrown iff the construct
        representing this entry's definition has been removed from it and
        it is the source of the source code
    */
    public SourceCode sourceCode()
        throws EntryConstructRemovedException;
        // Assert.ensure(result != null);

    /**
        @return the location of this entry's definition in its source
        code
        @exception EntryConstructRemovedException thrown iff the construct
        representing this entry's definition has been removed from it and
        it is the source of the location
        @see #sourceCode
    */
    public SourceLocation location()
        throws EntryConstructRemovedException;
        // Assert.ensure(result != null);

    /**
        @return true if this entry has an originating type, or false if it
        doesn't have one (which may be because it doesn't have one, it
        couldn't be obtained, it doesn't make sense for this type of symbol
        or the language it's in, etc.)
    */
    public boolean hasOriginatingType();

    /**
        @return the type in which this entry's symbol was defined
        @see #hasOriginatingType
    */
    public Type originatingType();
        // Assert.require(hasOriginatingType());
        // Assert.ensure(result != null);


    /**
        Returns a value that indicates what kind of symbol this entry
        represents. What values are valid and what various values mean
        is language-dependent.

        @return a value that indicates what kind of symbol this entry
        represents
    */
    public int kind();

    /**
        @return a description of the kind of this symbol
    */
    public String kindDescription();
        // Assert.ensure(result != null);


    /**
        Indicates whether the specified symbol table contains at least
        one entry that matches this entry.
        <p>
        What it means for an entry to match another entry is language-
        specific: for example, entries with the same name might match
        in one language, while in another only entries with the same
        name, arity and argument types match.
        <p>
        Note: this method compares symbols (e.g. ones that represent
        routines) with each other: it doesn't match routines against routine
        <em>calls</em>. So for example object-oriented languages that allow
        method overloading should only consider methods with the same name
        and arity to match if their argument types are elementwise
        <em>equal</em>, and not if one's just conforms to another's, since
        in the latter case both methods can (and do) coexist in the same
        class.
        <p>
        Note: most languages that use a symbol's argument and/or result types
        in matching symbols will <em>not</em> consider two symbols to match
        if one or both of the symbols have an argument or result with an
        invalid type.

        @param table the symbol table to look for matching entries in
        @return true iff 'table' contains at least one entry that
        matches this entry
        @exception UnsupportedOperationException thrown if this entry
        doesn't support this operation
    */
    public boolean containsMatching(SymbolTable table)
        throws UnsupportedOperationException;
        // Assert.require(table != null);

    /**
        Returns all of the entries in the specified symbol table that
        match this entry.
        <p>
        What it means for an entry to match another entry is language-
        specific: for example, entries with the same name might match
        in one language, while in another only entries with the same
        name, arity and argument types match.
        <p>
        Note: this method compares symbols (e.g. ones that represent
        routines) with each other: it doesn't match routines against routine
        <em>calls</em>. So for example object-oriented languages that allow
        method overloading should only consider methods with the same name
        and arity to match if their argument types are elementwise
        <em>equal</em>, and not if one's just conforms to another's, since
        in the latter case both methods can (and do) coexist in the same
        class.
        <p>
        Note: most languages that use a symbol's argument and/or result types
        in matching symbols will <em>not</em> consider two symbols to match
        if one or both of the symbols have an argument or result with an
        invalid type.

        @param table the symbol table to look for matching entries in
        @return all of the entries in 'table' that match this entry
        @exception UnsupportedOperationException thrown if this entry
        doesn't support this operation
    */
    public SymbolTableEntryList findMatching(SymbolTable table)
        throws UnsupportedOperationException;
        // Assert.require(table != null);
        // Assert.ensure(result != null);

    /**
        Removes and returns all of the entries in the specified symbol table
        that match this entry.
        <p>
        What it means for an entry to match another entry is language-
        specific: for example, entries with the same name might match in one
        language, while in another only entries with the same name, arity
        and argument types match.
        <p>
        Note: this method compares symbols (e.g. ones that represent
        routines) with each other: it doesn't match routines against routine
        <em>calls</em>. So for example object-oriented languages that allow
        method overloading should only consider methods with the same name
        and arity to match if their argument types are elementwise
        <em>equal</em>, and not if one's just conforms to another's, since
        in the latter case both methods can (and do) coexist in the same
        class.
        <p>
        Note: most languages that use a symbol's argument and/or result types
        in matching symbols will <em>not</em> consider two symbols to match
        if one or both of the symbols have an argument or result with an
        invalid type.

        @param table the symbol table to look for and remove matching
        entries in/from
        @return all of the entries in 'table' that match this entry
        @exception UnsupportedOperationException thrown if this entry
        doesn't support this operation
    */
    public SymbolTableEntryList removeMatching(SymbolTable table)
        throws UnsupportedOperationException;
        // Assert.require(table != null);
        // Assert.ensure(result != null);

    /**
        Returns a value that indicates how accessible the symbol this
        entry represents is. What values are valid and what various
        values mean is language-dependent.

        @return a value that indicates how accessible the symbol this
        entry represents is
    */
    public int accessibility();

    /**
        @return a description of the accessibility of this symbol
    */
    public String accessibilityDescription();
        // Assert.ensure(result != null);

    /**
        Indicates whether the symbol represented by this symbol table
        entry is at least as accessible as the symbol represented by
        the specified symbol table entry.
        <p>
        Note: for a given language it is possible that all accessibility
        values are not comparable: that is, it is possible that a given
        accessibility is not necessarily more, less or as accessible as
        another accessibility. Therefore
        <pre>
            entry1.isAtLeastAsAccessibleAs(entry2) == false
        </pre>
        doesn't necessarily imply that
        <pre>
            entry2.isAtLeastAsAccessibleAs(entry1) == true
        </pre>

        @param entry the other symbol table entry
        @return true iff the symbol represented by this entry is at
        least as accessible as the symbol represented by 'entry'
        @see #accessibility
    */
    public boolean isAtLeastAsAccessibleAs(SymbolTableEntry entry);
        // Assert.require(entry != null);


    /**
        Exactly what it means for one symbol table entry to shadow/override
        another entry is to some degree language-specific, though it is
        often necessary for both entries to have the same name and argument
        types in order for one to potentially shadow/override the other.
        <p>
        A symbol table entry 'a' directly shadows/overrides another entry
        'b' iff 'b' would not be shadowed/overridden if 'a' were not present.
        A symbol table entry 'a' indirectly shadows/overrides another entry
        'b' iff 'a' shadows/overrides 'b', but does not <em>directly</em>
        shadow/override 'b'.

        @return a list of all of the symbol table entries that this entry
        directly shadows or overrides: it does not include entries that
        it only shadows/overrides indirectly
    */
    public SymbolTableEntryList directlyShadows();
        // Assert.ensure(result != null);

    /**
        Adds the specified entry to the list of symbol table entries that
        this entry directly shadows/overrides.

        @param entry the symbol table entry to add to the list of entries
        directly shadowed/overridden by this entry
        @exception UnsupportedOperationException thrown if this entry
        doesn't support this operation
    */
    public void addDirectlyShadowedEntry(SymbolTableEntry entry)
        throws UnsupportedOperationException;
        // Assert.require(entry != null);

    /**
        Adds the specified entries to the list of symbol table entries that
        this entry directly shadows/overrides.

        @param entries the symbol table entries to add to the list of entries
        directly shadowed/overridden by this entry
        @exception UnsupportedOperationException thrown if this entry
        doesn't support this operation
    */
    public void addDirectlyShadowedEntries(SymbolTableEntryList entries)
        throws UnsupportedOperationException;
        // Assert.require(entries != null);


    /**
        @return the arity of this entry
    */
    public int arity();
        // Assert.ensure(result >= 0);

    /**
        Returns the construct that represents the ('index'+1)th
        argument of the symbol represented by this entry.

        @param index the zero-based argument index
        @return the construct that represents the ('index'+1)th
        argument of the symbol represented by this entry
        @exception EntryConstructRemovedException thrown iff the construct
        representing this argument has been removed from this entry (to
        save memory once the entry has been fully compiled)
    */
    public Construct argumentConstruct(int index)
        throws EntryConstructRemovedException;
        // Assert.require(index >= 0);
        // Assert.require(index < arity());
        // Assert.ensure(result != null);

    /**
        Returns a value that indicates what modifiers the ('index'+1)th
        argument of the symbol represented by this entry has. What values
        are valid and what various values mean is language-dependent.

        @param index the zero-based argument index
        @return a value that indicates what modifiers the ('index'+1)th
        argument of the symbol represented by this entry has
    */
    public int argumentModifiers(int index);
        // Assert.require(index >= 0);
        // Assert.require(index < arity());

    /**
        Returns the type of the ('index'+1)th argument of the symbol
        represented by this entry.

        @param index the zero-based argument index
        @return the type of the ('index'+1)th argument of the symbol
        represented by this entry
        @exception MissingTypeException thrown if no type has been
        specified for the ('index'+1)th argument
    */
    public Type argumentType(int index)
        throws MissingTypeException;
        // Assert.require(index >= 0);
        // Assert.require(index < arity());
        // Assert.ensure(result != null);

    /**
        @return a list of the types of the arguments of the symbol
        represented by this entry
        @exception MissingTypeException thrown if no type has been
        specified for one or more of our symbol's arguments
    */
    public TypeList argumentTypes()
        throws MissingTypeException;
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == arity());


    /**
        @return the number of results that are 'returned' when the
        symbol represented by this entry is evaluated
    */
    public int numberOfResults();
        // Assert.ensure(result >= 0);

    /**
        Returns the construct that represents the ('index'+1)th
        result returned when the symbol represented by this entry is
        evaluated.

        @param index the zero-based argument index
        @return the construct that represents the ('index'+1)th
        result returned when the symbol represented by this entry is
        evaluated
        @exception EntryConstructRemovedException thrown iff the construct
        representing this result has been removed from this entry (to
        save memory once the entry has been fully compiled)
    */
    public Construct resultConstruct(int index)
        throws EntryConstructRemovedException;
        // Assert.require(index >= 0);
        // Assert.require(index < numberOfResults());
        // Assert.ensure(result != null);

    /**
        Returns a value that indicates what modifiers the ('index'+1)th
        result of the symbol represented by this entry has. What values
        are valid and what various values mean is language-dependent.

        @param index the zero-based argument index
        @return a value that indicates what modifiers the ('index'+1)th
        result of the symbol represented by this entry has
    */
    public int resultModifiers(int index);
        // Assert.require(index >= 0);
        // Assert.require(index < numberOfResults());

    /**
        Returns the type of the ('index'+1)th result returned when
        the symbol represented by this entry is evaluated.

        @param index the zero-based result index
        @return the type of the ('index'+1)th result returned when
        the symbol represented by this entry is evaluated
        @exception MissingTypeException thrown if no type has been
        specified for the ('index'+1)th result
        @see #firstResultType
    */
    public Type resultType(int index)
        throws MissingTypeException;
        // Assert.require(index >= 0);
        // Assert.require(index < numberOfResults());
        // Assert.ensure(result != null);

    /**
        @return a list of the types of the results returned when
        the symbol represented by this entry is evaluated
        @exception MissingTypeException thrown if no type has been
        specified for one or more of our symbol's results
    */
    public TypeList resultTypes()
        throws MissingTypeException;
        // Assert.ensure(result != null);
        // Assert.ensure(result.size() == numberOfResults());

    /**
        Returns the type of the first result returned when the symbol
        represented by this entry is evaluated.
        <p>
        Note: this is a convenience method for use in the (common) case
        where every symbol in a language can only return a single
        result.

        @return the type of the first result returned when the symbol
        represented by this entry is evaluated
        @exception MissingTypeException thrown if no type has been
        specified for the first result
    */
    public Type firstResultType()
        throws MissingTypeException;
        // Assert.require(numberOfResults() > 0);
        // Assert.ensure(result != null);

    /**
        @return true iff the type returned by our firstResultType() method
        can be changed
        @see #setFirstResultType(Type)
    */
    public boolean canSetFirstResultType();

    /**
        Sets the type returned by our firstResultType() method to 't'.

        @param t our new first result type
    */
    public void setFirstResultType(Type t);
        // Assert.require(canSetFirstResultType());
        // Assert.require(t != null);

    /**
        Remove any and all constructs from this entry (to save memory once
        it has been fully compiled), if they haven't been already. (So
        instances have to be able to handle this method being called
        multiple times.)

        @see MinimalAbstractSymbolTableEntry#handleRemovedConstruct
    */
    public void removeConstructs();

    /**
        Note: exactly what constitutes the signature of an entry is
        language-specific.

        @return a displayable representation of the signature of this entry,
        for example for use in error messages
    */
    public String displaySignature();
        // Assert.ensure(result != null);

    /**
        @return a displayable representation of this entry, including its
        arguments and results
    */
    public String displayFull();
        // Assert.ensure(result != null);

    /**
        Indicates whether this entry is equal to the specified one.
        <p>
        Note: subclasses should override equals(Object) to use this method
        when the other object is a non-null SymbolTableEntry. They should
        also override hashCode() to be consistent with this method's
        implementation.

        @param entry another symbol table entry
        @return true iff this entry is equal to 'entry'
    */
    public boolean equals(SymbolTableEntry entry);
        // Assert.require(entry != null);

    /**
        Note: exactly what it means for one symbol to be a specialization or
        generalization of another symbol is language-specific. One possible
        use in languages with generic types is that a symbol table entry that
        represents a method on a given instance of a generic type is a
        specialization of the corresponding method on the generic type.

        @return the symbol table entry that represents the symbol that our
        symbol is  a specialization of (and thus is the symbol that is a
        generalization of ours), or null if our symbol isn't the
        specialization of any symbol
    */
    public SymbolTableEntry findGeneralization();
        // 'result' may be null
}
