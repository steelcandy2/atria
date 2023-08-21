/*
 Copyright (C) 2002-2005 by James MacKay.

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

import com.steelcandy.plack.common.base.PlackException;
import com.steelcandy.plack.common.semantic.ValidityConstraintAlreadyCheckedException;

import com.steelcandy.common.Resources;
import com.steelcandy.common.text.TextUtilities;

import java.io.PrintStream;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
    The default ValidityConstraintChecklist implementation.
    <p>
    An instance's addConstraint() method must be called to add validity
    constraint names to an instance.

    @author James MacKay
*/
public class DefaultValidityConstraintChecklist
    implements ValidityConstraintChecklist
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonParserAndConstructResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        VALIDITY_CONSTRAINT_ALREADY_CHECKED_MSG =
            "VALIDITY_CONSTRAINT_ALREADY_CHECKED_MSG";
    private static final String
        NOT_IN_VALIDITY_CONSTRAINT_CHECKLIST_MSG =
            "NOT_IN_VALIDITY_CONSTRAINT_CHECKLIST_MSG";
    private static final String
        CANNOT_ADD_DUPLICATE_CONSTRAINT_MSG =
            "CANNOT_ADD_DUPLICATE_CONSTRAINT_MSG";


    // Private fields

    /**
        A description of the type of construct that this checklist is
        associated with.
    */
    private String _constructDescription;

    /**
        The set of the names of all of the validity constraints in this
        checklist that have not been checked yet. Each item in this set is a
        String.
    */
    private Set _uncheckedConstraintNames;

    /**
        The set of the names of all of the validity constraints in this
        checklist that have been checked, mapped to a Throwable that
        describes the context in which the constraint was first checked.
        The keys are Strings and the values are Throwables.
    */
    private Map _checkedConstraintNames;


    // Constructors

    /**
        Constructs a DefaultValidityConstraintChecklist that has no validity
        constraints in it initially.

        @param constructDescription a description of the type of construct
        that this checklist is associated with
        @see #addConstraint(String)
    */
    public DefaultValidityConstraintChecklist(String constructDescription)
    {
        _constructDescription = constructDescription;
        _uncheckedConstraintNames = new HashSet();
        _checkedConstraintNames = new HashMap();
    }

    /**
        Creates and returns a checklist that is a copy of the specified
        checklist, except that it initially contains no constraints.

        @param checklist the checklist to clone
        @return a checklist that is a clone of 'checklist', except that it
        initially contains no constraints
    */
    public static DefaultValidityConstraintChecklist
        createEmptyClone(DefaultValidityConstraintChecklist checklist)
    {
        Assert.require(checklist != null);

        String desc = checklist.getConstructDescription();

        DefaultValidityConstraintChecklist result =
            new DefaultValidityConstraintChecklist(desc);

        Assert.ensure(result != null);
        return result;
    }


    // Public methods

    /**
        @see ValidityConstraintChecklist#markChecked(String)
    */
    public void markChecked(String constraintName)
        throws ValidityConstraintAlreadyCheckedException,
               IllegalArgumentException
    {
        Assert.check(constraintName != null);

        if (_uncheckedConstraintNames.remove(constraintName))
        {
            _checkedConstraintNames.put(constraintName, new Throwable());
        }
        else if (_checkedConstraintNames.containsKey(constraintName))
        {
            Throwable prev =
                (Throwable) _checkedConstraintNames.get(constraintName);
            String msg = _resources.
                getMessage(VALIDITY_CONSTRAINT_ALREADY_CHECKED_MSG,
                           constraintName, getConstructDescription(),
                           PlackException.stackTrace(prev),
                           PlackException.stackTrace(new Throwable()));
// dumpConstraintNames(System.err);
            throw new ValidityConstraintAlreadyCheckedException(msg);
        }
        else
        {
            String msg = _resources.
                getMessage(NOT_IN_VALIDITY_CONSTRAINT_CHECKLIST_MSG,
                           constraintName, getConstructDescription());
// dumpConstraintNames(System.err);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
        @see ValidityConstraintChecklist#isComplete
    */
    public boolean isComplete()
    {
        return _uncheckedConstraintNames.isEmpty();
    }

    /**
        @see ValidityConstraintChecklist#uncheckedConstraintNames
    */
    public Set uncheckedConstraintNames()
    {
        Set result = Collections.
                        unmodifiableSet(_uncheckedConstraintNames);

        Assert.ensure(result != null);
        return result;
    }

    /**
        Adds the validity constraint with the specified name to this
        checklist.

        @param constraintName the name of the validity constraint to be added
        to this checklist
        @exception IllegalArgumentException thrown if this checklist already
        contains a validity constraint with the specified name
    */
    public void addConstraint(String constraintName)
    {
        Assert.require(constraintName != null);

        if (_uncheckedConstraintNames.contains(constraintName) ||
            _checkedConstraintNames.containsKey(constraintName))
        {
            String msg = _resources.
                getMessage(CANNOT_ADD_DUPLICATE_CONSTRAINT_MSG,
                           constraintName, getConstructDescription());
            throw new IllegalArgumentException(msg);
        }
        else
        {
            _uncheckedConstraintNames.add(constraintName);
        }
    }

    /**
        @see Object#toString
    */
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append(_constructDescription).
            append(" validity constraint checklist: unchecked = {").
            append(TextUtilities.
                separate(_uncheckedConstraintNames.iterator(), ", ")).
            append("}, checked = {").
            append(TextUtilities.
                separate(_checkedConstraintNames.keySet().iterator(), ", ")).
            append("}");

        return buf.toString();
    }


    // Protected methods

    /**
        @return a description of the type of construct that this checklist is
        associated with
    */
    protected String getConstructDescription()
    {
        return _constructDescription;
    }


    // Private methods

    /**
        Dumps out the names of our checked and unchecked constraints to
        the specified output stream.
        <p>
        This method is intended to be used only for debugging purposes.

        @param out the output stream to dump our constraint names to
    */
    private void dumpConstraintNames(PrintStream out)
    {
        Assert.require(out != null);

        out.println(getClass().getName() + " constraint names:");
        out.println("    unchecked constraint names:");
        Iterator iter = _uncheckedConstraintNames.iterator();
        while (iter.hasNext())
        {
            out.println("        " + (String) iter.next());
        }

        out.println("    checked constraint names:");
        iter = _checkedConstraintNames.keySet().iterator();
        while (iter.hasNext())
        {
            out.println("        " + (String) iter.next());
        }
    }
}
