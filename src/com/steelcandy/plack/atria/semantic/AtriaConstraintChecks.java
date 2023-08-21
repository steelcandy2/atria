/*
 Copyright (C) 2005-2006 by James MacKay.

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

package com.steelcandy.plack.atria.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;
import com.steelcandy.plack.atria.constructs.*;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.common.Resources;

import java.util.*;

/**
    A singleton class that contains methods that perform various
    Atria validity constraint checks.
    <p>
    This class provides a place to put these checks without worrying
    about which constraint checker class they should appear in, etc.

    @author James MacKay
*/
public class AtriaConstraintChecks
    extends AtriaConstraintChecksBase
{
    // Constants

    /** The single instance of this class. */
    private static final AtriaConstraintChecks
        _instance = new AtriaConstraintChecks();

    /** The Atria construct manager. */
    private static final AtriaConstructManager
        MANAGER = AtriaConstructManager.instance();


    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        TOO_MANY_TOP_ELEMENTS_MSG =
            "TOO_MANY_TOP_ELEMENTS_MSG",
        NO_TOP_ELEMENT_MSG =
            "NO_TOP_ELEMENT_MSG",
        APPLICATION_CONSTRAINT_VIOLATED_MSG =
            "APPLICATION_CONSTRAINT_VIOLATED_MSG",
        TOO_MANY_TOP_COMMAND_PROLOGUE_ITEMS_MSG =
            "TOO_MANY_TOP_COMMAND_PROLOGUE_ITEMS_MSG",

        INVALID_FIRST_LANGUAGE_SPECIFIER_NAME_MSG =
            "INVALID_FIRST_LANGUAGE_SPECIFIER_NAME_MSG",
        INVALID_LANGUAGE_NAME_MSG =
            "INVALID_LANGUAGE_NAME_MSG",

        DUPLICATE_ATTRIBUTE_NAME_MSG =
            "DUPLICATE_ATTRIBUTE_NAME_MSG",

        COMMAND_NOT_PROLOGUE_ITEM_MSG =
            "COMMAND_NOT_PROLOGUE_ITEM_MSG",
        COMMAND_IS_PROLOGUE_ITEM_MSG =
            "COMMAND_IS_PROLOGUE_ITEM_MSG",
        NOT_PREDEFINED_COMMAND_NAME_MSG =
            "NOT_PREDEFINED_COMMAND_NAME_MSG",

        MORE_THAN_ZERO_ARGUMENTS_MSG =
            "MORE_THAN_ZERO_ARGUMENTS_MSG",
        NOT_JUST_ONE_ARGUMENT_MSG =
            "NOT_JUST_ONE_ARGUMENT_MSG",
        NOT_JUST_TWO_ARGUMENTS_MSG =
            "NOT_JUST_TWO_ARGUMENTS_MSG",
        SINGLE_ARGUMENT_NOT_NAME_MSG =
            "SINGLE_ARGUMENT_NOT_NAME_MSG",
        SINGLE_ARGUMENT_NOT_EXPRESSION_MSG =
            "SINGLE_ARGUMENT_NOT_EXPRESSION_MSG",
        NAMESPACE_PREFIX_ALREADY_DEFINED_MSG =
            "NAMESPACE_PREFIX_ALREADY_DEFINED_MSG",
        ZERO_ARGUMENTS_MSG =
            "ZERO_ARGUMENTS_MSG",
        JOIN_ARGUMENT_NOT_EXPRESSION_MSG =
            "JOIN_ARGUMENT_NOT_EXPRESSION_MSG",
        FIRST_ARGUMENT_NOT_NAME_MSG =
            "FIRST_ARGUMENT_NOT_NAME_MSG",
        SECOND_ARGUMENT_NOT_EXPRESSION_MSG =
            "SECOND_ARGUMENT_NOT_EXPRESSION_MSG",
        NAMESPACE_PREFIX_IN_NAMESPACE_PREFIX_MSG =
            "NAMESPACE_PREFIX_IN_NAMESPACE_PREFIX_MSG",
        CANNOT_SET_PREFIXED_VARIABLES_MSG =
            "CANNOT_SET_PREFIXED_VARIABLES_MSG",
        TOP_ARGUMENT_NOT_ATTRIBUTE_MSG =
            "TOP_ARGUMENT_NOT_ATTRIBUTE_MSG",

        NAME_STARTS_WITH_NAMESPACE_SEPARATOR_MSG =
            "NAME_STARTS_WITH_NAMESPACE_SEPARATOR_MSG",
        NAME_ENDS_WITH_NAMESPACE_SEPARATOR_MSG =
            "NAME_ENDS_WITH_NAMESPACE_SEPARATOR_MSG",
        NAME_HAS_MULTIPLE_NAMESPACE_SEPARATORS_MSG =
            "NAME_HAS_MULTIPLE_NAMESPACE_SEPARATORS_MSG",
        NAME_NAMESPACE_PREFIX_NOT_DEFINED_MSG =
            "NAME_NAMESPACE_PREFIX_NOT_DEFINED_MSG";


    // Constructors

    /**
        @return the single instance of this class
    */
    public static AtriaConstraintChecks instance()
    {
        return _instance;
    }

    /**
        This constructor should only be used to construct
        the single instance of this class.

        @see #instance
    */
    private AtriaConstraintChecks()
    {
        // empty
    }


    // Document checking methods

    /**
        Reports that a document has more than one top element (and so also
        cannot have had a top element specified by a 'top' command).

        @param c the document with more than one top element
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportMoreThanOneTopElement(AtriaConstructManager.
                                            Document c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.elementCount() > 1);
        Assert.require(handler != null);

        int topElementCount = c.elementCount();
        String msg = _resources.
            getMessage(TOO_MANY_TOP_ELEMENTS_MSG,
                       new Integer(topElementCount),
                       AtriaInfo.TOP_COMMAND_NAME);
        violatedDocumentIsTopElement(msg, c.elementList().get(1), handler);
    }

    /**
        Reports that a document has no top element (and so also cannot have
        had a top element specified by a 'top' command).

        @param c the document with no top element
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportNoTopElement(AtriaConstructManager.Document c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.elementCount() == 0);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NO_TOP_ELEMENT_MSG, AtriaInfo.TOP_COMMAND_NAME);
        violatedDocumentIsTopElement(msg, c, handler);
    }

    /**
        Reports that an application-specific constraint was violated.

        @param description a description of which constraint was violated
        and/or how it was violated
        @param errorConstruct the construct that is the location of the
        violation of the application-specific constraint
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportApplicationConstraintViolated(String description,
                            Construct errorConstruct, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(errorConstruct != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(APPLICATION_CONSTRAINT_VIOLATED_MSG, description);
        violatedDocumentApplication(msg, errorConstruct, handler);
    }


    // Prologue checking methods

    /**
        Reports that the specified Prologue has more than one direct
        PrologueItem subconstruct whose direct Command subconstruct is a
        'top' command.

        @param c the prologue with too many 'top' command prologue items
        @param topCommands the constructs that represent all of the 'top'
        commands that are prologue items, in the order that they appear in
        the prologue: each item is an AtriaConstructManager.Command
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportTooManyTopCommandPrologueItems(AtriaConstructManager.
        Prologue c, ConstructList topCommands, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(topCommands != null);
        Assert.require(topCommands.size() > 1);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(TOO_MANY_TOP_COMMAND_PROLOGUE_ITEMS_MSG,
                       new Integer(topCommands.size()),
                       AtriaInfo.TOP_COMMAND_NAME);
        violatedPrologueAtMostOneTopItem(msg, topCommands.get(1), handler);
    }


    // LanguageSpecifier checking methods

    /**
        Checks that the first Name subconstruct - if there is one - is
        'language'.

        @param c the language specifier to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkFirstIsLanguage(AtriaConstructManager.
                                LanguageSpecifier c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = null;
        if (c.hasName())
        {
            name = MANAGER.toText(c.name());
        }

        String requiredName = AtriaInfo.FIRST_LANGUAGE_SPECIFIER_NAME;
        if (name != null && name.equals(requiredName) == false)
        {
            String msg = _resources.
                getMessage(INVALID_FIRST_LANGUAGE_SPECIFIER_NAME_MSG,
                           name, requiredName);
            violatedLanguageSpecifierFirstIsLanguage(msg, c.name(), handler);
        }
    }

    /**
        Checks that the language name specified by the specified
        LanguageSpecifier is 'atria'.

        @param c the language specifier to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkLanguageName(AtriaConstructManager.
                                   LanguageSpecifier c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String name = null;
        if (c.hasLanguageName())
        {
            name = MANAGER.toText(c.languageName());
        }

        String requiredName = AtriaInfo.LANGUAGE_NAME;
        if (name != null && name.equals(requiredName) == false)
        {
            String msg = _resources.
                getMessage(INVALID_LANGUAGE_NAME_MSG, name, requiredName);
            violatedLanguageSpecifierName(msg, c.languageName(), handler);
        }
    }


    // Element checking methods

    /**
        Checks that the names of all of the direct Attribute subconstructs
        of the specified Element have unique names.

        @param c the element to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkAttributeNamesUnique(AtriaConstructManager.
        Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        ConstructList attributes = c.attributeList();
        if (attributes.size() > 1)
        {
            Set names = new HashSet();
            ConstructIterator iter = attributes.iterator();
            while (iter.hasNext())
            {
                AtriaConstructManager.Attribute attr =
                    (AtriaConstructManager.Attribute) iter.next();
                String name = null;
                if (attr.hasName())
                {
                    name = MANAGER.toText(attr.name());
                }

                if (name != null && names.add(name) == false)
                {
                    // 'name' is already in 'name'.
                    String msg = _resources.
                        getMessage(DUPLICATE_ATTRIBUTE_NAME_MSG, name);
                    violatedElementUniqueAttributeNames(msg, attr.name(),
                                                        handler);
                }
            }
        }
    }


    // Command checking methods

    /**
        Checks that the specified command - which is assumed to be a
        PrologueItem - is not a command that is required <em>not</em> to be a
        PrologueItem.

        @param c the command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkCommandNonPrologueItem(AtriaConstructManager.Command c,
                                            ErrorHandler handler)
    {
        Assert.require(c != null);
        // Assert.require("'c' is a direct PrologueItem subconstruct");
        Assert.require(handler != null);

        String name = MANAGER.commandName(c);
        if (name != null && AtriaInfo.
                NON_PROLOGUE_ITEM_COMMAND_NAMES_SET.contains(name))
        {
            String msg = _resources.
                getMessage(COMMAND_IS_PROLOGUE_ITEM_MSG, name);
            violatedCommandNonPrologueItem(msg, c.name(), handler);
        }
    }

    /**
        Checks that the specified command - which is assumed <em>not</em> to
        be a PrologueItem - is not a command that is required to be a
        PrologueItem.

        @param c the command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkCommandPrologueItemOnly(AtriaConstructManager.Command c,
                                             ErrorHandler handler)
    {
        Assert.require(c != null);
        // Assert.require("'c' is not a direct PrologueItem subconstruct");
        Assert.require(handler != null);

        String name = MANAGER.commandName(c);
        if (name != null && AtriaInfo.
                PROLOGUE_ITEM_ONLY_COMMAND_NAMES_SET.contains(name))
        {
            String msg = _resources.
                getMessage(COMMAND_NOT_PROLOGUE_ITEM_MSG, name);
            violatedCommandPrologueItemOnly(msg, c.name(), handler);
        }
    }

    /**
        Reports that the specified command's name does not contain a
        namespace prefix and is not the name of a predefined command.

        @param c the command with the unkown name
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportNotPredefinedCommandName(AtriaConstructManager.
                                        Command c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NOT_PREDEFINED_COMMAND_NAME_MSG,
                       MANAGER.commandName(c));
        violatedCommandPredefinedName(msg, c.name(), handler);
    }


    /**
        Checks that the specified command has no arguments.

        @param c the command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkZeroArguments(AtriaConstructManager.Command c,
                                   ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        if (c.argumentCount() > 0)
        {
            reportMoreThanZeroArguments(c, handler);
        }
    }

    /**
        Reports that the specified command, which is supposed to have no
        arguments, has one or more arguments.

        @param c the command with one or more arguments
        @param handler the error handler to use to report the constraint
        violation
    */
    protected void
        reportMoreThanZeroArguments(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() > 0);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(MORE_THAN_ZERO_ARGUMENTS_MSG,
                       MANAGER.commandName(c),
                       new Integer(c.argumentCount()));
        violatedCommandNoArguments(msg, c.argumentList().get(0), handler);
    }

    /**
        Checks that the specified command has exactly one argument, and that
        the argument is a Name. If no validity constraint violation is
        reported then the command's NameArgument attribute is also set to the
        name argument.

        @param c the command whose arguments are to be checked
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkOneNameArgument(AtriaConstructManager.Command c,
                                     ErrorHandler handler)
    {
        Assert.require(c.nameArgumentAttribute().isSet() == false);

        if (c.argumentCount() != 1)
        {
            reportNotJustOneArgument(c, handler);
        }
        else
        {
            Construct arg = c.argumentList().get(0);
            if (isName(arg) == false)
            {
                reportSingleArgumentNotName(c, handler);
            }
            else
            {
                String name = MANAGER.
                    toText((AtriaConstructManager.Name) arg);
                c.nameArgumentAttribute().setValue(name);
            }
        }
    }

    /**
        Checks that the specified command has exactly one argument, and that
        the argument is an Expression. The command's NameArgument attribute
        is not set.

        @param c the command whose arguments are to be checked
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkOneExpressionArgument(AtriaConstructManager.Command c,
                                           ErrorHandler handler)
    {
        if (c.argumentCount() != 1)
        {
            reportNotJustOneArgument(c, handler);
        }
        else if (isExpression(c.argumentList().get(0)) == false)
        {
            reportSingleArgumentNotExpression(c, handler);
        }
    }

    /**
        Reports that the specified command, which is supposed to have exactly
        one argument, has more or fewer arguments.

        @param c the command with the wrong number of arguments
        @param handler the error handler to use to report the constraint
        violation
    */
    protected void reportNotJustOneArgument(AtriaConstructManager.Command c,
                                            ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() != 1);
        Assert.require(handler != null);

        int numArgs = c.argumentCount();

        Construct errorConstruct = c;
        if (numArgs > 1)
        {
            errorConstruct = c.argumentList().get(1);
                        // = the second argument
        }

        String msg = _resources.
            getMessage(NOT_JUST_ONE_ARGUMENT_MSG,
                       MANAGER.commandName(c),
                       new Integer(numArgs));
        violatedCommandOneArgument(msg, errorConstruct, handler);
    }

    /**
        Reports that the specified command's single argument is not a Name.

        @param c the command whose sole argument is not a Name
        @param handler the error handler to use to report the constraint
        violation
    */
    protected void
        reportSingleArgumentNotName(AtriaConstructManager.Command c,
                                    ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() == 1);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(SINGLE_ARGUMENT_NOT_NAME_MSG,
                       MANAGER.commandName(c));
        violatedCommandOneNameArgument(msg, c.argumentList().get(0),
                                       handler);
    }

    /**
        Reports that the specified command's single argument is not an
        Expression.

        @param c the command whose sole argument is not an Expression
        @param handler the error handler to use to report the constraint
        violation
    */
    protected void
        reportSingleArgumentNotExpression(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() == 1);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(SINGLE_ARGUMENT_NOT_EXPRESSION_MSG,
                       MANAGER.commandName(c));
        violatedCommandOneExpressionArgument(msg, c.argumentList().get(0),
                                             handler);
    }

    /**
        Checks that the specified 'join' command's arguments are valid.

        @param c the 'join' command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkJoinCommandArguments(AtriaConstructManager.Command c,
                                          ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        if (c.argumentCount() == 0)
        {
            String msg = _resources.
                getMessage(ZERO_ARGUMENTS_MSG,
                           MANAGER.commandName(c));
            violatedCommandJoinArguments(msg, c, handler);
        }
        else
        {
            ConstructIterator iter = c.argumentList().iterator();
            while (iter.hasNext())
            {
                Construct arg = iter.next();
                if (isExpression(arg) == false)
                {
                    String msg = _resources.
                        getMessage(JOIN_ARGUMENT_NOT_EXPRESSION_MSG);
                    violatedCommandJoinArguments(msg, arg, handler);
                }
            }
        }
    }

    /**
        Checks that the specified 'top' command's arguments are valid. If no
        validity constraint violation is reported then the command's
        NameArgument attribute is also set to the first argument.

        @param c the 'top' command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkTopCommandArguments(AtriaConstructManager.Command c,
                                         ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.nameArgumentAttribute().isSet() == false);
        Assert.require(handler != null);

        AtriaConstructManager.Name elementName = null;
        String name = MANAGER.commandName(c);
        if (c.argumentCount() == 0)
        {
            String msg = _resources.
                getMessage(ZERO_ARGUMENTS_MSG, name);
            violatedCommandTopArguments(msg, c, handler);
        }
        else
        {
            ConstructIterator iter = c.argumentList().iterator();

            Construct arg1 = iter.next();
            if (isName(arg1) == false)
            {
                String msg = _resources.
                    getMessage(FIRST_ARGUMENT_NOT_NAME_MSG, name);
                violatedCommandTopArguments(msg, arg1, handler);
            }
            else
            {
                elementName = (AtriaConstructManager.Name) arg1;
            }

            while (iter.hasNext())
            {
                Construct arg = iter.next();
                if (isAttribute(arg) == false)
                {
                    elementName = null;
                    String msg = _resources.
                        getMessage(TOP_ARGUMENT_NOT_ATTRIBUTE_MSG, name);
                    violatedCommandTopArguments(msg, arg, handler);
                }
            }
        }

        if (elementName != null)
        {
            c.nameArgumentAttribute().setValue(MANAGER.toText(elementName));
        }
    }

    /**
        Checks that the specified 'set' command's arguments are valid. If no
        validity constraint violation is reported then the command's
        NameArgument attribute is also set to the first argument.

        @param c the 'set' command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkSetCommandArguments(AtriaConstructManager.
                                         Command c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.nameArgumentAttribute().isSet() == false);
        Assert.require(handler != null);

        AtriaConstructManager.Name varName = null;
        String name = MANAGER.commandName(c);
        int numArgs = c.argumentCount();

        if (numArgs != 2)
        {
            reportNotJustTwoArguments(c, handler);
        }
        else
        {
            Construct arg1 = c.argumentList().get(0);
            Construct arg2 = c.argumentList().get(1);

            if (isName(arg1))
            {
                varName = (AtriaConstructManager.Name) arg1;
                String prefix = namespacePrefix(varName);
                if (prefix.length() > 0)
                {
                    String msg = _resources.
                        getMessage(CANNOT_SET_PREFIXED_VARIABLES_MSG,
                                   name, prefix);
                    violatedCommandSetArguments(msg, arg1, handler);
                }
            }
            else
            {
                String msg = _resources.
                    getMessage(FIRST_ARGUMENT_NOT_NAME_MSG, name);
                violatedCommandSetArguments(msg, arg1, handler);
            }

            if (isExpression(arg2) == false)
            {
                varName = null;
                String msg = _resources.
                    getMessage(SECOND_ARGUMENT_NOT_EXPRESSION_MSG, name);
                violatedCommandSetArguments(msg, arg2, handler);
            }
        }

        if (varName != null)
        {
            c.nameArgumentAttribute().setValue(MANAGER.toText(varName));
        }
    }

    /**
        Checks that the specified 'namespace' command's arguments are valid.
        If no validity constraint violation is reported then the command's
        NameArgument attribute is also set to the first argument.

        @param c the 'namespace' command to check
        @param handler the error handler to use to report any constraint
        violations
    */
    public void checkNamespaceCommandArguments(AtriaConstructManager.
                                            Command c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.nameArgumentAttribute().isSet() == false);
        Assert.require(handler != null);

        AtriaConstructManager.Name prefix = null;
        String name = MANAGER.commandName(c);
        int numArgs = c.argumentCount();

        if (numArgs != 2)
        {
            reportNotJustTwoArguments(c, handler);
        }
        else
        {
            Construct arg1 = c.argumentList().get(0);
            Construct arg2 = c.argumentList().get(1);

            if (isName(arg1))
            {
                prefix = (AtriaConstructManager.Name) arg1;
                String prefixPrefix = namespacePrefix(prefix);
                if (prefixPrefix.length() > 0)
                {
                    String msg = _resources.
                        getMessage(NAMESPACE_PREFIX_IN_NAMESPACE_PREFIX_MSG,
                                   name, prefixPrefix);
                    violatedCommandNamespaceArguments(msg, arg1, handler);
                }
            }
            else
            {
                String msg = _resources.
                    getMessage(FIRST_ARGUMENT_NOT_NAME_MSG, name);
                violatedCommandNamespaceArguments(msg, arg1, handler);
            }

            if (isExpression(arg2) == false)
            {
                prefix = null;
                String msg = _resources.
                    getMessage(SECOND_ARGUMENT_NOT_EXPRESSION_MSG, name);
                violatedCommandNamespaceArguments(msg, arg2, handler);
            }
        }

        if (prefix != null)
        {
            c.nameArgumentAttribute().setValue(MANAGER.toText(prefix));
        }
    }

    /**
        Reports that the specified 'namespace' command defines a namespace
        prefix that has already been defined by a preceding 'namespace'
        command.

        @param c the 'namespace' command
        @param prefix the namespace defined by 'c'
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportNamespacePrefixAlreadyDefined(AtriaConstructManager.
                            Command c, String prefix, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() > 0);
            // it must have a first argument since it defines a prefix
        Assert.require(prefix != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NAMESPACE_PREFIX_ALREADY_DEFINED_MSG, prefix,
                       AtriaInfo.NAMESPACE_COMMAND_NAME);
        violatedCommandNamespaceArguments(msg, c.argumentList().get(0),
                                          handler);
    }

    /**
        Reports that the specified command, which is supposed to have exactly
        two arguments, has more or fewer arguments.

        @param c the command with the wrong number of arguments
        @param handler the error handler to use to report the constraint
        violation
    */
    protected void reportNotJustTwoArguments(AtriaConstructManager.Command c,
                                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(c.argumentCount() != 2);
        Assert.require(handler != null);

        int numArgs = c.argumentCount();

        Construct errorConstruct = c;
        if (numArgs > 2)
        {
            errorConstruct = c.argumentList().get(2);
                        // = the third argument
        }

        String msg = _resources.
            getMessage(NOT_JUST_TWO_ARGUMENTS_MSG,
                       MANAGER.commandName(c),
                       new Integer(numArgs));
        violatedCommandTwoArguments(msg, errorConstruct, handler);
    }


    // Name checking methods

    /**
        Reports that the specified name's first character is a namespace
        separator.

        @param c the name that starts with a namespace separator
        @param handler the error handler to use to report the constraint
        violation
    */
    public void
        reportNameStartsWithNamespaceSeparator(AtriaConstructManager.Name c,
                                               ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NAME_STARTS_WITH_NAMESPACE_SEPARATOR_MSG,
                       AtriaInfo.NAMESPACE_SEPARATOR);
        violatedNameNamespaceSeparatorNotAtEnds(msg, c, handler);
    }

    /**
        Reports that the specified name's last character is a namespace
        separator.

        @param c the name that ends with a namespace separator
        @param handler the error handler to use to report the constraint
        violation
    */
    public void
        reportNameEndsWithNamespaceSeparator(AtriaConstructManager.Name c,
                                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NAME_ENDS_WITH_NAMESPACE_SEPARATOR_MSG,
                       AtriaInfo.NAMESPACE_SEPARATOR);
        violatedNameNamespaceSeparatorNotAtEnds(msg, c, handler);
    }

    /**
        Reports that the specified name contains more than one namespace
        separator.

        @param c the name that contains more than one namespace separator
        @param secondSeparatorIndex the zero-based index of the second
        namespace separator in 'c'
        @param handler the error handler to use to report the constraint
        violation
    */
    public void
        reportNameHasMultipleNamespaceSeparators(AtriaConstructManager.
                    Name c, int secondSeparatorIndex, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(secondSeparatorIndex > 0);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NAME_HAS_MULTIPLE_NAMESPACE_SEPARATORS_MSG,
                       AtriaInfo.NAMESPACE_SEPARATOR);
        violatedNameOneNamespaceSeparator(msg, c, handler);
    }

    /**
        Reports that the specified name contains a namespace prefix that was
        not defined by a preceding 'namespace' command.

        @param c the name
        @param prefix 'c''s namespace prefix
        @param handler the error handler to use to report the constraint
        violation
    */
    public void reportNameNamespaceNotDefined(AtriaConstructManager.Name c,
                                        String prefix, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(prefix != null);
        Assert.require(prefix.length() > 0);
        Assert.require(handler != null);

        String msg = _resources.
            getMessage(NAME_NAMESPACE_PREFIX_NOT_DEFINED_MSG,
                       prefix, AtriaInfo.NAMESPACE_COMMAND_NAME);
        violatedNameDefinedNamespacePrefix(msg, c, handler);
    }


    // Protected methods

    /**
        @param c a construct
        @return true iff 'c' is an Attribute
    */
    protected boolean isAttribute(Construct c)
    {
        Assert.require(c != null);

        return (c instanceof AtriaConstructManager.Attribute);
    }

    /**
        @param c a construct
        @return true iff 'c' is an Expression
    */
    protected boolean isExpression(Construct c)
    {
        Assert.require(c != null);

        return (c instanceof AtriaConstructManager.Expression);
    }

    /**
        @param c a construct
        @return true iff 'c' is a Name
    */
    protected boolean isName(Construct c)
    {
        Assert.require(c != null);

        return (c instanceof AtriaConstructManager.Name);
    }

    /**
        @param c a name
        @return the namespace prefix part of 'c'
    */
    protected String namespacePrefix(AtriaConstructManager.Name c)
    {
        Assert.require(c != null);
        Assert.require(c.namespacePrefixAttribute().isSet());

        String result = c.namespacePrefixAttribute().value();

        Assert.ensure(result != null);
        return result;
    }
}
