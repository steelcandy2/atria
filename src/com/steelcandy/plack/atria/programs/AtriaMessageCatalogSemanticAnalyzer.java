/*
 Copyright (C) 2014 by James MacKay.

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

package com.steelcandy.plack.atria.programs;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.atria.base.AtriaInfo;
import com.steelcandy.plack.atria.constructs.*;
import com.steelcandy.plack.atria.semantic.*;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.common.Resources;
import com.steelcandy.common.containers.Containers;

import java.util.*;

/**
    The class of semantic analyzer that analyzes Atria constructs used in
    the Atria documents used to generate message catalog classes.
    <p>
    Note: we don't guarantee that the names used in message catalogs are
    directly suitable for use in generated source code: see the class
    AtriaMessageCatalogNameChecker for the definitions of valid names in
    message catalogs, and adapt them as necessary in the subclass of
    AtriaAbstractMessageCatalogBuilder that generates the source code.

    @author  James MacKay
*/
public class AtriaMessageCatalogSemanticAnalyzer
    extends AtriaSemanticAnalyzer
    implements AtriaMessageCatalogConstants
{
    // Constants

    /**
        The checker that determines whether various types of names are valid.
    */
    private static final AtriaMessageCatalogNameChecker
        NAME_CHECKER = AtriaMessageCatalogNameChecker.instance();


    /** The resources used by this class. */
    private static final Resources _resources =
        AtriaMessageCatalogBuilderResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        ELEMENT_TOO_DEEP_MSG =
            "ELEMENT_TOO_DEEP_MSG",

        INVALID_ROOT_ELEMENT_NAME_MSG =
            "INVALID_ROOT_ELEMENT_NAME_MSG",
        INVALID_ROOT_ELEMENT_ATTRIBUTE_NAME_MSG =
            "INVALID_ROOT_ELEMENT_ATTRIBUTE_NAME_MSG",
        ATTRIBUTE_VALUE_NOT_TEXT_MSG =
            "ATTRIBUTE_VALUE_NOT_TEXT_MSG",
        ATTRIBUTE_VALUE_NOT_BOOLEAN_MSG =
            "ATTRIBUTE_VALUE_NOT_BOOLEAN_MSG",
        CANNOT_SPECIFY_BOTH_STYLE_AND_LOCALE_MSG =
            "CANNOT_SPECIFY_BOTH_STYLE_AND_LOCALE_MSG",
        NO_STYLE_OR_LOCALE_ATTRIBUTE_MSG =
            "NO_STYLE_OR_LOCALE_ATTRIBUTE_MSG",
        ROOT_ELEMENT_MISSING_REQUIRED_ATTRIBUTE_MSG =
            "ROOT_ELEMENT_MISSING_REQUIRED_ATTRIBUTE_MSG",
        MODULE_ATTRIBUTE_VALUE_NOT_MODULE_NAME_MSG =
            "MODULE_ATTRIBUTE_VALUE_NOT_MODULE_NAME_MSG",
        NAME_ATTRIBUTE_VALUE_NOT_CLASS_NAME_MSG =
            "NAME_ATTRIBUTE_VALUE_NOT_CLASS_NAME_MSG",

        MESSAGE_ELEMENT_NAME_NOT_UNIQUE_MSG =
            "MESSAGE_ELEMENT_NAME_NOT_UNIQUE_MSG",
        INVALID_MESSAGE_ELEMENT_NAME_MSG =
            "INVALID_MESSAGE_ELEMENT_NAME_MSG",
        TOO_MANY_MESSAGE_ELEMENT_ATTRIBUTES_MSG =
            "TOO_MANY_MESSAGE_ELEMENT_ATTRIBUTES_MSG",
        INVALID_MESSAGE_ELEMENT_ATTRIBUTE_NAME_MSG =
            "INVALID_MESSAGE_ELEMENT_ATTRIBUTE_NAME_MSG",
        INVALID_ARGS_ATTRIBUTE_VALUE_MSG =
            "INVALID_ARGS_ATTRIBUTE_VALUE_MSG",
        NO_MESSAGE_ELEMENT_ATTRIBUTES_MSG =
            "NO_MESSAGE_ELEMENT_ATTRIBUTES_MSG",

        UNKNOWN_MESSAGE_CHILD_ELEMENT_NAME_MSG =
            "UNKNOWN_MESSAGE_CHILD_ELEMENT_NAME_MSG",
        TOO_MANY_ARG_ELEMENT_ATTRIBUTES_MSG =
            "TOO_MANY_ARG_ELEMENT_ATTRIBUTES_MSG",
        INVALID_ARG_ELEMENT_ATTRIBUTE_NAME_MSG =
            "INVALID_ARG_ELEMENT_ATTRIBUTE_NAME_MSG",
        NO_ARG_ELEMENT_ATTRIBUTES_MSG =
            "NO_ARG_ELEMENT_ATTRIBUTES_MSG",
        INVALID_ARG_INDEX_ATTRIBUTE_VALUE_MSG =
            "INVALID_ARG_INDEX_ATTRIBUTE_VALUE_MSG",
        ARG_INDEX_TOO_LARGE_MSG =
            "ARG_INDEX_TOO_LARGE_MSG",

        NAMESPACE_COMMAND_USED_MSG =
            "NAMESPACE_COMMAND_USED_MSG";


    /**
        A map from each valid root element attribute name to the prototype
        AttributeChecker to clone and then use to check the attribute with
        that name. The keys are all Strings and the values are all
        AttributeCheckers.
    */
    private static final Map
        ROOT_ELEMENT_CHECKERS = createRootElementCheckersMap();


    // Private fields

    /**
        The number of arguments specified by the 'args' attribute of the
        root child element currently being analyzed, or -1 if that attribute
        is missing or invalid.
    */
    private int _numberOfArguments;

    /**
        The message catalog data object to which we're to add information
        that we obtain in the course of our analyses.
    */
    private AtriaMessageCatalogData _data;

    /**
        The names of all of the message elements that have been analyzed so
        far. Each item in the set is a String.
    */
    private Set _messageElementNames;


    // Constructors

    /**
        Constructs an AtriaAbstractMessageCatalogSemanticAnalyzer.

        @param data the data object to which the analyzer is to add
        information that it obtains in the course of its analyses
    */
    public AtriaMessageCatalogSemanticAnalyzer(AtriaMessageCatalogData data)
    {
        Assert.require(data != null);

        _data = data;
        _numberOfArguments = -1;
        _messageElementNames = new HashSet();
    }


    // Visitor methods

    /**
        @see AtriaConstructVisitor#visitElement(AtriaConstructManagerBase.Element, ErrorHandler)
    */
    public void visitElement(AtriaConstructManager.
        Element c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int depth = elementDepth();
        Assert.check(depth >= 0);

        if (depth == 1)
        {
            setNumberOfArguments(c);
                // so that 'arg' and 'quoted-arg' elements can check that
                // their indices are less than it
        }
        super.visitElement(c, handler);

        String name = MANAGER.elementName(c);

        if (depth == 0)
        {
            if (name != null)
            {
                checkRootElement(name, c.name(), c.attributeList(), handler);
            }
        }
        else if (depth == 1)
        {
            checkMessageElement(c, name, handler);
        }
        else if (depth == 2)
        {
            if (name != null)
            {
                if (_data.isArgumentElementName(name))
                {
                    checkArgElementAttributes(c, handler);
                }
                else
                {
                    String msg = _resources.
                        getMessage(UNKNOWN_MESSAGE_CHILD_ELEMENT_NAME_MSG,
                                   name);
                    reportApplicationConstraintViolated(msg, c.name(),
                                                        handler);
                }
            }
        }
        else  // depth > 2
        {
            String msg = _resources.
                getMessage(ELEMENT_TOO_DEEP_MSG);
            reportApplicationConstraintViolated(msg, c, handler);
        }
    }


    // Command visitor methods

    /**
        @see AtriaCommandConstructVisitor#visitTopCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitTopCommand(AtriaConstructManager.Command c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        super.visitTopCommand(c, handler);

        if (c.argumentCount() > 0)
        {
            String elementName = c.nameArgumentAttribute().value();
            ConstructList args = c.argumentList();
            checkRootElement(elementName, args.get(0), args.subList(1),
                             handler);
        }
    }

    /**
        @see AtriaCommandConstructVisitor#visitNamespaceCommand(AtriaConstructManagerBase.Command, ErrorHandler)
    */
    public void visitNamespaceCommand(AtriaConstructManager.Command c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        // At least for now 'namespace' commands are not allowed in message
        // catalogs.
        Assert.check(c.hasName());
            // since otherwise how could 'c' be a 'namespace' command?
        String msg = _resources.
            getMessage(NAMESPACE_COMMAND_USED_MSG,
                       AtriaInfo.NAMESPACE_COMMAND_NAME);
        reportApplicationConstraintViolated(msg, c.name(), handler);

        // We don't call our superclass' version of this method since there's
        // no point in reporting other problems with a command that can't be
        // used at all.
        // super.visitNamespaceCommand(c, handler);
    }


    // Protected methods

    /**
        Sets the number of arguments taken by the message described by the
        message element we're currently analyzing.

        @param c the message element we're currently analyzing
    */
    protected void setNumberOfArguments(AtriaConstructManager.Element c)
    {
        Assert.require(c != null);

        _numberOfArguments = -1;
        ConstructIterator iter = c.attributeList().iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) iter.next();
            if (attr.hasName())
            {
                String name = MANAGER.attributeName(attr);

                if (name != null &&
                    _data.isNumberOfArgumentsAttributeName(name))
                {
                    _numberOfArguments = AtriaMessageCatalogUtilities.
                        parseNonnegativeIntegerAttributeValue(attr);
                    break;  // while
                }
            }
        }
    }

    /**
        Checks the additional constraints on the root element of a message
        catalog.

        @param elementName the name of the root element
        @param nameConstruct the construct that specifies the root element's
        name
        @param attributes the constructs that represent the root element's
        attributes
    */
    protected void checkRootElement(String elementName,
        Construct nameConstruct, ConstructList attributes,
        ErrorHandler handler)
    {
        Assert.require(elementName != null);
        Assert.require(nameConstruct != null);
        Assert.require(attributes != null);
        Assert.require(handler != null);

        if (ROOT_ELEMENT_NAME.equals(elementName) == false)
        {
            String msg = _resources.
                getMessage(INVALID_ROOT_ELEMENT_NAME_MSG,
                           elementName, ROOT_ELEMENT_NAME);
            reportApplicationConstraintViolated(msg, nameConstruct, handler);
        }

        Set checkedNames = new HashSet(attributes.size());
        ConstructIterator iter = attributes.iterator();
        while (iter.hasNext())
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) iter.next();
            String name = MANAGER.attributeName(attr);
            if (name != null)
            {
                AttributeChecker checker = (AttributeChecker)
                    ROOT_ELEMENT_CHECKERS.get(name);
                if (checker != null)
                {
                    checker.cloneChecker().
                        check(attr, _data, checkedNames, handler);
                }
                else
                {
                    String msg = _resources.getMessage(
                        INVALID_ROOT_ELEMENT_ATTRIBUTE_NAME_MSG, name);
                    Assert.check(attr.hasName());
                        // since 'name' is non-null
                    reportApplicationConstraintViolated(msg, attr.name(),
                                                        handler);
                }
                checkedNames.add(name);
            }
        }

        // Check that all of the required attributes were found.
        if (checkedNames.
                contains(LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME) == false &&
            checkedNames.
                contains(STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME) == false)
        {
            String msg = _resources.
                getMessage(NO_STYLE_OR_LOCALE_ATTRIBUTE_MSG,
                           LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                           STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME);
            reportApplicationConstraintViolated(msg, nameConstruct, handler);
        }
        if (checkedNames.
                contains(MODULE_ROOT_ELEMENT_ATTRIBUTE_NAME) == false)
        {
            String msg = _resources.
                getMessage(ROOT_ELEMENT_MISSING_REQUIRED_ATTRIBUTE_MSG,
                           MODULE_ROOT_ELEMENT_ATTRIBUTE_NAME);
            reportApplicationConstraintViolated(msg, nameConstruct, handler);
        }
        if (checkedNames.
                contains(NAME_ROOT_ELEMENT_ATTRIBUTE_NAME) == false)
        {
            String msg = _resources.
                getMessage(ROOT_ELEMENT_MISSING_REQUIRED_ATTRIBUTE_MSG,
                           NAME_ROOT_ELEMENT_ATTRIBUTE_NAME);
            reportApplicationConstraintViolated(msg, nameConstruct, handler);
        }
        if (checkedNames.
                contains(LOCALIZABLE_ROOT_ELEMENT_ATTRIBUTE_NAME) == false)
        {
            String msg = _resources.
                getMessage(ROOT_ELEMENT_MISSING_REQUIRED_ATTRIBUTE_MSG,
                           LOCALIZABLE_ROOT_ELEMENT_ATTRIBUTE_NAME);
            reportApplicationConstraintViolated(msg, nameConstruct, handler);
        }
    }


    /**
        Checks the specified message element and its subconstructs.

        @param c the message element
        @param name the name of the message element, or null if it could not
        be obtained
        @param handler the error handler to use to report any constraint
        violations
    */
    protected void checkMessageElement(AtriaConstructManager.Element c,
                                       String name, ErrorHandler handler)
    {
        Assert.require(c != null);
        // 'name' may be null
        Assert.require(handler != null);

        if (name != null)
        {
            if (_messageElementNames.add(name) == false)
            {
                // 'name' was already in the set.
                String msg = _resources.
                    getMessage(MESSAGE_ELEMENT_NAME_NOT_UNIQUE_MSG,
                               name);
                reportApplicationConstraintViolated(msg, c.name(), handler);
            }

            String why = NAME_CHECKER.checkValidMessageElementName(name);
            if (why != null)
            {
                String msg = _resources.
                    getMessage(INVALID_MESSAGE_ELEMENT_NAME_MSG, name, why);
                reportApplicationConstraintViolated(msg, c.name(), handler);
            }
        }
        checkMessageElementAttributes(c, handler);
    }

    /**
        Checks constraints on the attributes of a message element.

        @param c the message element to check
        @param handler the error handler to use to report any constraint
        violations
    */
    protected void
        checkMessageElementAttributes(AtriaConstructManager.Element c,
                                      ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int numAttributes = c.attributeCount();
        if (numAttributes == 1)
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) c.attributeList().get(0);
            String attrName = MANAGER.attributeName(attr);
            if (attrName != null &&
                _data.isNumberOfArgumentsAttributeName(attrName) == false)
            {
                String msg = _resources.
                    getMessage(INVALID_MESSAGE_ELEMENT_ATTRIBUTE_NAME_MSG,
                        attrName, _data.numberOfArgumentsAttributeName());
                reportApplicationConstraintViolated(msg, attr.name(),
                                                    handler);
            }

            if (attr.hasExpression())
            {
                int value = AtriaMessageCatalogUtilities.
                                parseNonnegativeIntegerAttributeValue(attr);
                if (value < 0)
                {
                    String msg = _resources.
                        getMessage(INVALID_ARGS_ATTRIBUTE_VALUE_MSG,
                                   attrName);
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
            }
        }
        else if (numAttributes > 1)
        {
            String msg = _resources.
                getMessage(TOO_MANY_MESSAGE_ELEMENT_ATTRIBUTES_MSG,
                           _data.numberOfArgumentsAttributeName());
            reportApplicationConstraintViolated(msg,
                                        c.attributeList().get(1), handler);
        }
        else
        {
            Assert.check(numAttributes == 0);
            String msg = _resources.
                getMessage(NO_MESSAGE_ELEMENT_ATTRIBUTES_MSG,
                           _data.numberOfArgumentsAttributeName());
            reportApplicationConstraintViolated(msg, c, handler);
        }
    }

    /**
        Checks constraints on the attributes of an 'arg' or 'quoted-arg'
        element.

        @param c the 'arg' or 'quoted-arg' element to check
        @param handler the error handler to use to report any constraint
        violations
    */
    protected void checkArgElementAttributes(AtriaConstructManager.Element c,
                                             ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        int numAttributes = c.attributeCount();
        if (numAttributes == 1)
        {
            AtriaConstructManager.Attribute attr =
                (AtriaConstructManager.Attribute) c.attributeList().get(0);
            String attrName = MANAGER.attributeName(attr);
            if (attrName != null &&
                _data.isArgumentIndexAttributeName(attrName) == false)
            {
                String msg = _resources.
                    getMessage(INVALID_ARG_ELEMENT_ATTRIBUTE_NAME_MSG,
                               MANAGER.elementName(c), attrName,
                               _data.argumentIndexAttributeName());
                reportApplicationConstraintViolated(msg, attr.name(),
                                                    handler);
            }

            if (attr.hasExpression())
            {
                int value = AtriaMessageCatalogUtilities.
                                parseNonnegativeIntegerAttributeValue(attr);
                if (value < 0)
                {
                    String msg = _resources.
                        getMessage(INVALID_ARG_INDEX_ATTRIBUTE_VALUE_MSG,
                                   attrName);
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
                else if (_numberOfArguments >= 0 &&
                         value >= _numberOfArguments)
                {
                    Object[] args = new Object[]
                    {
                        MANAGER.elementName(c),
                        attrName,
                        new Integer(value),
                        new Integer(_numberOfArguments),
                        _data.numberOfArgumentsAttributeName()
                    };
                    String msg = _resources.
                        getMessage(ARG_INDEX_TOO_LARGE_MSG, args);
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
            }
        }
        else if (numAttributes > 1)
        {
            String msg = _resources.
                getMessage(TOO_MANY_ARG_ELEMENT_ATTRIBUTES_MSG,
                           MANAGER.elementName(c),
                           _data.argumentIndexAttributeName());
            reportApplicationConstraintViolated(msg,
                                        c.attributeList().get(1), handler);
        }
        else
        {
            Assert.check(numAttributes == 0);
            String msg = _resources.
                getMessage(NO_ARG_ELEMENT_ATTRIBUTES_MSG,
                           MANAGER.elementName(c),
                           _data.argumentIndexAttributeName());
            reportApplicationConstraintViolated(msg, c, handler);
        }
    }


    // Private static methods

    /**
        @return a map from valid root element attribute names to the
        AttributeChecker prototypes to clone and then use to check attributes
        with that name on the root element
    */
    private static Map createRootElementCheckersMap()
    {
        Map result = Containers.createHashMap(6);

        result.put(LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new LocaleRootElementAttributeChecker());
        result.put(STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new StyleRootElementAttributeChecker());
        result.put(MODULE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new ModuleRootElementAttributeChecker());
        result.put(NAME_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new NameRootElementAttributeChecker());
        result.put(LOCALIZABLE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new LocalizableRootElementAttributeChecker());
        result.put(COMPACT_ROOT_ELEMENT_ATTRIBUTE_NAME,
                   new CompactRootElementAttributeChecker());

        Assert.ensure(result != null);
        return result;
    }


    // Inner classes

    /**
        The interface implemented by classes that check the validity of
        Attributes.
    */
    private static interface AttributeChecker
    {
        // Public methods

        /**
            Checks the validity of the specified attribute.

            @param attr the construct that represents the attribute to
            check
            @param data the message catalog data object to which to add
            information obtained while checking the attribute
            @param checkedNames the names of all of the other attributes
            on the same element as 'attr' that have already been checked:
            each item in the set is a String
            @param handler the error handler to use to report any validity
            constraint violations
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler);
            // Assert.require(attr != null);
            // Assert.require(MANAGER.attributeName(attr) != null);
            // Assert.require(data != null);
            // Assert.require(checkedNames != null);
            // Assert.require(handler != null);

        /**
            @return a clone of this checker, or possibly this checker
            itself if it is stateless
        */
        public AttributeChecker cloneChecker();
            // Assert.ensure(result != null);
    }

    /**
        An abstract base class for AttributeCheckers.
    */
    private static abstract class AbstractAttributeChecker
        implements AttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogUtilities.AttributeChecker#cloneChecker
        */
        public AttributeChecker cloneChecker()
        {
            // Assert.ensure(result != null);
            return this;
        }


        // Protected methods

        /**
            Checks that the value of the specified attribute is a text
            literal.

            @param attr the attribute whose value is to be checked
            @param handler the error handler to use to report any validity
            constraint violations
            @return the text that the value of 'attr' represents if it is
            a text literal, and null if it isn't
        */
        protected String
            checkIsTextAttribute(AtriaConstructManager.Attribute attr,
                                 ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(handler != null);

            String result =
                AtriaMessageCatalogUtilities.parseTextAttributeValue(attr);

            if (result == null)
            {
                AtriaConstruct c = attr;
                if (attr.hasExpression())
                {
                    c = attr.expression();
                }
                String msg = _resources.
                    getMessage(ATTRIBUTE_VALUE_NOT_TEXT_MSG,
                               MANAGER.attributeName(attr));
                reportApplicationConstraintViolated(msg, c, handler);
            }

            // 'result' may be null
            return result;
        }

        /**
            Checks that the value of the specified attribute is (the text
            representation of) a boolean literal.

            @param attr the attribute whose value is to be checked
            @param handler the error handler to use to report any validity
            constraint violations
            @return the boolean that the value of 'attr' represents if it is
            a boolean literal, and null if it isn't
            @see Boolean#booleanValue
        */
        protected Boolean
            checkIsBooleanAttribute(AtriaConstructManager.Attribute attr,
                                    ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(handler != null);

            Boolean result = null;

            String text = checkIsTextAttribute(attr, handler);

            if (text != null)
            {
                if (TRUE_VALUE_TEXT.equals(text))
                {
                    result = Boolean.TRUE;
                }
                else if (FALSE_VALUE_TEXT.equals(text))
                {
                    result = Boolean.FALSE;
                }
                else
                {
                    String msg = _resources.
                        getMessage(ATTRIBUTE_VALUE_NOT_BOOLEAN_MSG,
                                   MANAGER.attributeName(attr),
                                   TRUE_VALUE_TEXT, FALSE_VALUE_TEXT);
                    Assert.check(attr.hasExpression());
                        // since 'text' is non-null
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
            }

            // 'result' may be null
            return result;
        }
    }


    /**
        The class of AttributeChecker to use to check 'localizable'
        attributes on root elements.
    */
    private static class LocalizableRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            Boolean value = checkIsBooleanAttribute(attr, handler);
            if (value != null)
            {
                data.setIsLocalizable(value.booleanValue());
            }
        }
    }

    /**
        The class of AttributeChecker to use to check 'compact' attributes
        on root elements.
    */
    private static class CompactRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            Boolean value = checkIsBooleanAttribute(attr, handler);
            if (value != null)
            {
                data.setIsCompact(value.booleanValue());
            }
        }
    }

    /**
        The class of AttributeChecker to use to check 'locale' attributes
        on root elements.
    */
    private static class LocaleRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            String value = checkIsTextAttribute(attr, handler);
            if (value != null)
            {
// TODO: add check that 'value' is a valid locale name (or at least has the
// general format of one) !!!

                data.setLocaleName(value);
                if (checkedNames.contains(STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME))
                {
                    // There is both a 'locale' and a 'style' attribute.
                    String msg = _resources.
                        getMessage(CANNOT_SPECIFY_BOTH_STYLE_AND_LOCALE_MSG,
                                   LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                                   STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME);
                    Assert.check(attr.hasName());
                    reportApplicationConstraintViolated(msg, attr.name(),
                                                        handler);
                }
            }
        }
    }

    /**
        The class of AttributeChecker to use to check 'style' attributes
        on root elements.
    */
    private static class StyleRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            String value = checkIsTextAttribute(attr, handler);
            if (value != null)
            {
// TODO: add check that 'value' is a valid style name (or at least has the
// general format of one) !!!

                data.setStyleName(value);
                if (checkedNames.
                        contains(LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME))
                {
                    // There is both a 'locale' and a 'style' attribute.
                    String msg = _resources.
                        getMessage(CANNOT_SPECIFY_BOTH_STYLE_AND_LOCALE_MSG,
                                   STYLE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                                   LOCALE_ROOT_ELEMENT_ATTRIBUTE_NAME);
                    Assert.check(attr.hasName());
                    reportApplicationConstraintViolated(msg, attr.name(),
                                                        handler);
                }
            }
        }
    }

    /**
        The class of AttributeChecker to use to check 'module' attributes
        on root elements.
    */
    private static class ModuleRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            String value = checkIsTextAttribute(attr, handler);
            if (value != null)
            {
                String why = NAME_CHECKER.checkValidModuleName(value);
                if (why == null)
                {
                    data.setModuleName(value);
                }
                else
                {
                    String msg = _resources.
                        getMessage(MODULE_ATTRIBUTE_VALUE_NOT_MODULE_NAME_MSG,
                                   MODULE_ROOT_ELEMENT_ATTRIBUTE_NAME,
                                   value, why);
                    Assert.check(attr.hasExpression());
                        // since 'value' is non-null
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
            }
        }
    }

    /**
        The class of AttributeChecker to use to check 'name' attributes
        on root elements.
    */
    private static class NameRootElementAttributeChecker
        extends AbstractAttributeChecker
    {
        // Public methods

        /**
            @see AtriaMessageCatalogSemanticAnalyzer.AttributeChecker#check(AtriaConstructManagerBase.Attribute, AtriaMessageCatalogData, Set, ErrorHandler)
        */
        public void check(AtriaConstructManager.Attribute attr,
                          AtriaMessageCatalogData data, Set checkedNames,
                          ErrorHandler handler)
        {
            Assert.require(attr != null);
            Assert.require(MANAGER.attributeName(attr) != null);
            Assert.require(data != null);
            Assert.require(checkedNames != null);
            Assert.require(handler != null);

            String value = checkIsTextAttribute(attr, handler);
            if (value != null)
            {
                String why = NAME_CHECKER.checkValidClassName(value);
                if (why == null)
                {
                    data.setClassName(value);
                }
                else
                {
                    String msg = _resources.
                        getMessage(NAME_ATTRIBUTE_VALUE_NOT_CLASS_NAME_MSG,
                            NAME_ROOT_ELEMENT_ATTRIBUTE_NAME, value, why);
                    Assert.check(attr.hasExpression());
                        // since 'value' is non-null
                    reportApplicationConstraintViolated(msg,
                                                attr.expression(), handler);
                }
            }
        }
    }
}
