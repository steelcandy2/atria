/*
 Copyright (C) 2003-2005 by James MacKay.

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

package com.steelcandy.build.plack;

// import com.steelcandy.common.debug.Assert;

import org.jdom.*;

import java.io.PrintStream;
import java.util.*;

/**
    An abstract base class for classes that check that fragments of
    XML are correct, and report anything in them that is incorrect.
    <p>
    Note: this class currently only checks documents' elements and
    their attributes.

    @author James MacKay
    @version $Revision: 1.15 $
*/
public abstract class AbstractXmlChecker
{
    // Constants

    /** Warning error severity level. */
    protected static final int WARNING_LEVEL = 0;

    /** Non-fatal error severity level. */
    protected static final int ERROR_LEVEL = 1;

    /** Fatal error severity level. */
    protected static final int FATAL_ERROR_LEVEL = 2;


    // Private fields

    /**
        The output stream on which error information is output.
    */
    private PrintStream _out;

    /**
        The number of errors that have been found by this checker
        so far.
    */
    private int _numErrors;


    // Constructors

    /**
        Constructs an AbstractXmlChecker.

        @param out the output stream on which the checker is to report
        any errors it finds in the XML
    */
    public AbstractXmlChecker(PrintStream out)
    {
        // Assert.require(out != null);

        _out = out;
        _numErrors = 0;
    }

    /**
        Constructs an AbstractXmlChecker that isn't ready for use yet.
        <p>
        An instance constructed using this constructor must have its
        setOutputStream() method called before it can be used.
    */
    protected AbstractXmlChecker()
    {
        _out = null;
        _numErrors = 0;
    }


    // Public methods

    /**
        @return the number of errors discovered by this checker so far
    */
    public int numberOfErrors()
    {
        // Assert.ensure(_numErrors >= 0);
        return _numErrors;
    }


    // Protected methods

    /**
        Sets the output stream that this checker is to output error
        information to to the specified output stream.

        @param out the output stream
        @see AbstractXmlChecker#AbstractXmlChecker
    */
    protected void setOutputStream(PrintStream out)
    {
        // Assert.require(out != null);

        _out = out;
    }


    // Utility methods

    /**
        Indicates whether the specified element has an attribute
        with the specified name.

        @param e the element
        @param attributeName the attribute name
        @return true iff 'e' has an attribute named 'attributeName'
    */
    protected boolean hasAttribute(Element e, String attributeName)
    {
        return (e.getAttributeValue(attributeName) != null);
    }

    /**
        Returns the 'node path' of the specified element.

        @param elem the element whose 'node path' is to be returned
        @return elem's node path
    */
    protected String nodePath(Element elem)
    {
        // Assert.require(elem != null);

        StringBuffer result = new StringBuffer(elem.getName());


        Element parent = parentElement(elem);
        while (parent != null)
        {
            result.insert(0, parent.getName() + "/");
            parent = parentElement(parent);
        }

        // Assert.ensure(result.toString() != null);
        return result.toString();
    }

    /**
        Returns the 'node path' of a child element of the specified
        parent element that has the specified name.

        @param parent the parent element
        @param childName the name of the child element of 'parent'
        @return the node path of a child element of 'parent' named
        'childName'
    */
    public String nodePath(Element parent, String childName)
    {
        // Assert.require(parent != null);
        // Assert.require(childName != null);

        String result = nodePath(parent) + "/" + childName;

        // Assert.ensure(result != null);
        return result;
    }

    /**
        Returns the 'node path' of the specified attribute.

        @param attr the attribute whose 'node path' is to be returned
        @return attr's node path
    */
    protected String nodePath(Attribute attr)
    {
        // Assert.require(attr != null);

        String result = nodePath(attr.getParent()) + "@" + attr.getName();

        // Assert.ensure(result != null);
        return result;
    }

    /**
        Note: an Element's parent can be a Document or an Element.

        @param e an element
        @return the parent element of 'e', or null if it doesn't have a
        parent or if its parent is not an Element
        @see Content#getParent
    */
    protected Element parentElement(Element e)
    {
        // Assert.require(e != null);

        Element result = null;

        Parent p = e.getParent();
        if (p != null && p instanceof Element)
        {
            result = (Element) p;
        }

        // 'result' may be null
        return result;
    }


    // Error reporting methods

    /**
        Reports the specified message. It is assumed not to describe an
        error (for which one of the reportError() methods should be used
        instead).

        @see #reportError(String)
        @see #reportError(int, String)
    */
    protected void reportMessage(String msg)
    {
        _out.println(msg);
    }

    /**
        Reports an error of the default severity level that is
        described by the specified message.

        @param msg the message describing an error
    */
    protected void reportError(String msg)
    {
        reportError(ERROR_LEVEL, msg);
    }

    /**
        Reports an internal error: that is, an error in how this
        checker has been configured.
        <p>
        Note: internal errors are fatal errors.

        @param msg the message describing an internal error
    */
    protected void reportInternalError(String msg)
    {
        reportError(FATAL_ERROR_LEVEL, "*** INTERNAL ERROR *** " + msg);
    }

    /**
        Reports an error of the specified severity level that is
        described by the specified message.

        @param msg the message describing an error
        @param level the error's severity level
        @exception FatalXmlCheckingErrorException thrown iff the error
        level is FATAL_ERROR_LEVEL
    */
    protected void reportError(int level, String msg)
    {
        String finalMsg = msg;

        if (level == WARNING_LEVEL)
        {
            finalMsg = "warning: " + finalMsg;
        }
        else if (level == FATAL_ERROR_LEVEL)
        {
            finalMsg = "FATAL ERROR: " + finalMsg;
        }

        // Assert.check(_out != null);
        reportMessage(finalMsg);
        _numErrors += 1;

        if (level == FATAL_ERROR_LEVEL)
        {
            throw FatalXmlCheckingErrorException.instance();
        }
    }


    // Checking methods

    /**
        Checks that the specified element has no attributes, reporting
        an error for each attribute that it does have.

        @param e the element to check for attributes
    */
    protected void checkNoAttributes(Element e)
    {
        // Assert.require(e != null);

        (new ElementAttributesChecker()).check(e);
    }

    /**
        Checks that the specified element has some text content other than
        just whitespace, reporting an error if it does.

        @param e the element to check for text content
    */
    protected void checkHasText(Element e)
    {
        // Assert.require(e != null);

        (new HasTextElementChecker()).check(e);
    }

    /**
        Checks that the specified element has no text content, reporting
        an error if it does.

        @param e the element to check for text content
    */
    protected void checkNoText(Element e)
    {
        // Assert.require(e != null);

        (new NoTextElementChecker()).check(e);
    }

    /**
        Checks that the specified element has at least one child (of
        any type), reporting an error if it doesn't.

        @param e the element to check for children
    */
    protected void checkHasChildren(Element e)
    {
        // Assert.require(e != null);

        if (e.getChildren().isEmpty())
        {
            reportError("an element with node path " + nodePath(e) +
                " has no child elements, but it is supposed to have " +
                "at least one child element");
        }
    }

    /**
        Checks that the specified element has exactly the specified
        number of child elements (of any type), reporting an error if
        it doesn't.

        @param e the element to check
        @param numChildren the exact number of child elements that 'e'
        is supposed to have
    */
    protected void checkNumberOfChildren(Element e, int numChildren)
    {
        // Assert.require(e != null);
        // Assert.require(numChildren >= 0);

        int realNumChildren = e.getChildren().size();
        if (realNumChildren != numChildren)
        {
            reportError("an element with node path " + nodePath(e) +
                " has " + realNumChildren + " child elements, but " +
                "it is supposed to have exactly " + numChildren +
                " child elements");
        }
    }

    /**
        Checks that the specified element has no child elements, reporting
        an error if it does.
    */
    protected void checkNoChildren(Element e)
    {
        // Assert.require(e != null);

        (new ElementChildrenChecker()).check(e);
    }


    // Checker creation methods

    /**
        @return a new ElementAttributesChecker
    */
    protected ElementAttributesChecker createAttributesChecker()
    {
        return new ElementAttributesChecker();
    }

    /**
        @return a new ElementChildrenChecker
    */
    protected ElementChildrenChecker createChildrenChecker()
    {
        return new ElementChildrenChecker();
    }

    /**
        Returns a child element checker that checks that the specified
        parent element has exactly the specified number of child elements
        with the specified name, and that checks that type of child
        element using the specified element checker.

        @param numAllowed the exact number of children named 'childName'
        that 'parent' is allowed to have
        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker exactly(int numAllowed,
        Element parent, String childName, ElementChecker childChecker)
    {
        // Assert.require(numAllowed >= 0);
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return new ExactChildCountElementChecker(numAllowed, parent,
                                                childName, childChecker);
    }

    /**
        Returns a child element checker that allows the specified
        parent element to have any  number of child elements with the
        specified name, and that checks that type of child element using
        the specified element checker.

        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker any(Element parent,
            String childName, ElementChecker childChecker)
    {
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return atLeast(0, parent, childName, childChecker);
    }

    /**
        Returns a child element checker that checks that the specified
        parent element has exactly one child element with the specified
        name, and that checks that type of child element using the
        specified element checker.

        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker exactlyOne(Element parent,
                    String childName, ElementChecker childChecker)
    {
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return exactly(1, parent, childName, childChecker);
    }

    /**
        Returns a child element checker that checks that the specified
        parent element has at least the specified number of child elements
        with the specified name, and that checks that type of child
        element using the specified element checker.

        @param minimumCount the minimum number of children named
        'childName' that 'parent' is allowed to have
        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker atLeast(int minimumCount,
        Element parent, String childName, ElementChecker childChecker)
    {
        // Assert.require(minimumCount >= 0);
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return new MinimumChildCountElementChecker(minimumCount, parent,
                                                childName, childChecker);
    }

    /**
        Returns a child element checker that checks that the specified
        parent element has at most the specified number of child elements
        with the specified name, and that checks that type of child
        element using the specified element checker.

        @param maximumCount the maximum number of children named
        'childName' that 'parent' is allowed to have
        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker atMost(int maximumCount,
        Element parent, String childName, ElementChecker childChecker)
    {
        // Assert.require(maximumCount >= 0);
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return new MaximumChildCountElementChecker(maximumCount, parent,
                                                childName, childChecker);
    }

    /**
        Returns a child element checker that checks that the specified
        parent element has a number of child elements with the specified
        name that is between the specified minimum number and the specified
        maximum number (inclusive), and that checks that type of child
        element using the specified element checker.

        @param minimumCount the minimum number of children named
        'childName' that 'parent' is allowed to have
        @param maximumCount the maximum number of children named
        'childName' that 'parent' is allowed to have
        @param parent the parent element
        @param childName the name of the child elements of 'parent' that
        the returned checker is to check
        @param childChecker the element checker that is to be used to
        check the child elements of 'parent' named 'childName'
        themselves
    */
    protected ChildCountElementChecker between(int minimumCount,
        int maximumCount, Element parent, String childName,
        ElementChecker childChecker)
    {
        // Assert.require(minimumCount >= 0);
        // Assert.require(maximumCount >= 0);
        // Assert.require(minimumCount <= maximumCount);
        // Assert.require(parent != null);
        // Assert.require(childName != null);
        // Assert.require(childChecker != null);

        return new RangeChildCountElementChecker(minimumCount, maximumCount,
                                        parent, childName, childChecker);
    }


    // Common attribute checker inner classes

    /**
        The interface implemented by all classes that check that
        XML attributes are correct.
    */
    protected static interface AttributeChecker
    {
        // Public methods

        /**
            Checks that the specified attribute is correct, reporting
            anything that is incorrect.
        */
        public void check(Attribute attr);
            // Assert.require(attr != null);
    }

    /**
        An abstract base class for AttributeCheckers that just check
        that the attribute's value is valid.
        <p>
        Subclasses just have to implement checkValue().
        <p>
        Note: most AttributeCheckers subclass this class since there
        isn't usually anything else to check about an attribute.
    */
    protected abstract class AbstractAttributeValueChecker
        implements AttributeChecker
    {
        // Public methods

        /**
            @see AbstractXmlChecker.AttributeChecker#check(Attribute)
        */
        public void check(Attribute attr)
        {
            // Assert.require(attr != null);

            checkValue(attr, attr.getValue());
        }


        // Abstract methods

        /**
            Checks that the specified value of the specified attribute
            is correct.

            @param attr the attribute whose value is to be checked
            @param value 'attr''s value
        */
        protected abstract void checkValue(Attribute attr, String value);
            // Assert.require(attr != null);
            // Assert.require(value != null);
    }

    /**
        The class of attribute checker that is satisfied by any and all
        attributes.
    */
    protected class AlwaysValidAttributeChecker
        implements AttributeChecker
    {
        /**
            @see AbstractXmlChecker.AttributeChecker#check(Attribute)
        */
        public void check(Attribute attr)
        {
            // Assert.require(attr != null);

            // empty
        }
    }

    /**
        The class of attribute checker that checks that a module name
        attribute's value is correct.
    */
    protected class NonEmptyAttributeChecker
        extends AbstractAttributeValueChecker
    {
        // Private fields

        /**
            A description of what the attribute's value represents.
        */
        private String _description;


        // Constructors

        /**
            Constructs a NonEmptyAttributeChecker from a description
            of what the attribute's value represents.

            @param description a description of what the attribute's
            value represents
        */
        public NonEmptyAttributeChecker(String description)
        {
            // Assert.require(description != null);

            _description = description;
        }


        // Protected methods

        /**
            @see AbstractXmlChecker.AbstractAttributeValueChecker#checkValue(Attribute, String)
        */
        protected void checkValue(Attribute attr, String value)
        {
            // Assert.require(attr != null);
            // Assert.require(value != null);

            if (value.length() == 0)
            {
                reportError("an element with node path " +
                    nodePath(attr.getParent()) +
                    " has an attribute named '" + attr.getName() +
                    "' with an empty value, which is an invalid " +
                    _description);
            }
        }
    }

    /**
        The class of attribute checker that checks that an attribute's
        value is a boolean value (i.e. 'true' or 'false').
    */
    protected class BooleanAttributeChecker
        extends AbstractAttributeValueChecker
    {
        // Protected methods

        /**
            @see AbstractXmlChecker.AbstractAttributeValueChecker#checkValue(Attribute, String)
        */
        protected void checkValue(Attribute attr, String value)
        {
            // Assert.require(attr != null);
            // Assert.require(value != null);

            if (value.equals("true") == false &&
                value.equals("false") == false)
            {
                reportError("an element with node path " +
                    nodePath(attr.getParent()) +
                    " has an attribute named '" + attr.getName() +
                    "' that is supposed to have a boolean value, " +
                    "but doesn't. (Its value is '" + value + "' and the " +
                    "valid boolean values are 'true' and 'false')");
            }
        }
    }

    /**
        The class of attribute checker that checks that an attribute's
        value is an int value.
        <p>
        Subclasses can override checkIntValue() to perform more checks
        on the attribute's value once it has been determined to be an
        int. (For example, to check that its value is positive.)
    */
    protected class IntAttributeChecker
        extends AbstractAttributeValueChecker
    {
        // Protected methods

        /**
            @see AbstractXmlChecker.AbstractAttributeValueChecker#checkValue(Attribute, String)
        */
        protected void checkValue(Attribute attr, String value)
        {
            // Assert.require(attr != null);
            // Assert.require(value != null);

            try
            {
                int intValue = Integer.parseInt(value);
                checkIntValue(attr, intValue);
            }
            catch (NumberFormatException ex)
            {
                reportError("an element with node path " +
                    nodePath(attr.getParent()) +
                    " has an attribute named '" + attr.getName() +
                    " that is supposed to have an integer value, but " +
                    "doesn't. (Its value is '" + value + "')");
            }
        }

        /**
            Checks that the specified attribute's int value is
            correct, reporting an error if it isn't.
            <p>
            This implementation does nothing.

            @param attr the attribute
            @param value its value
        */
        protected void checkIntValue(Attribute attr, int value)
        {
            // Assert.require(attr != null);

            // empty
        }
    }

    /**
        The class of attribute checker that checks that an attribute's
        value is a positive int value.
    */
    protected class PositiveIntAttributeChecker
        extends IntAttributeChecker
    {
        // Protected methods

        /**
            @see AbstractXmlChecker.IntAttributeChecker#checkIntValue(Attribute, int)
        */
        protected void checkIntValue(Attribute attr, int value)
        {
            // Assert.require(attr != null);

            if (value <= 0)
            {
                reportError("an element with node path " +
                    nodePath(attr.getParent()) + " has an attribute " +
                    "named '" + attr.getName() + "' that is supposed " +
                    "to have a positive integer value, but doesn't. " +
                    "(Its value is '" + value + "')");
            }
        }
    }

    /**
        The class of attribute checker that checks that an attribute's
        value is a non-negative int value.
    */
    protected class NonNegativeIntAttributeChecker
        extends IntAttributeChecker
    {
        // Protected methods

        /**
            @see AbstractXmlChecker.IntAttributeChecker#checkIntValue(Attribute, int)
        */
        protected void checkIntValue(Attribute attr, int value)
        {
            // Assert.require(attr != null);

            if (value < 0)
            {
                reportError("an element with node path " +
                    nodePath(attr.getParent()) + " has an attribute " +
                    "named '" + attr.getName() + "' that is supposed " +
                    "to have a non-negative integer value, but doesn't. " +
                    "(Its value is '" + value + "')");
            }
        }
    }


    // Common element checker inner classes

    /**
        The interface implemented by all classes that check that
        XML elements (including their attributes and descendent
        elements) are correct.
    */
    protected static interface ElementChecker
    {
        // Public methods

        /**
            Checks that the specified element (including all of its
            attributes and descendent elements) is correct, reporting
            anything that is incorrect.
        */
        public void check(Element e);
            // Assert.require(e != null);
    }


    /**
        The class of element checker that checks that an element has
        the correct attributes and ONLY the correct attributes. It
        does <em>not</em> check the values of any of the attributes.
        <p>
        Note: by definition an element cannot have more than one
        attribute with a given name. This class depends on this fact.
    */
    protected class ElementAttributesChecker
        implements ElementChecker
    {
        // Private fields

        /**
            A map from the allowed optional attribute names to the
            checker to use to check attributes with that name. The
            keys are Strings and the values are AttributeCheckers.
        */
        private Map _optionalMap = new HashMap();

        /**
            A map from the allowed required attribute names to the
            checker to use to check attributes with that name. The
            keys are Strings and the values are AttributeCheckers.
        */
        private Map _requiredMap = new HashMap();


        // Public methods

        /**
            Adds the specified attribute name to this checker's list of
            the names of attributes that may - but do not have to -
            appear on any element it checks in order for it to consider
            the element to be correct.

            @param name the name of the optional attribute
            @param checker the checker to use to check any attributes
            named 'name'
            @return this
        */
        public ElementAttributesChecker
            addOptional(String name, AttributeChecker checker)
        {
            _optionalMap.put(name, checker);
            return this;
        }

        /**
            Adds the specified attribute name to this checker's list of
            the names of attributes that must appear on any element it
            checks in order for it to consider the element to be correct.

            @param name the name of the required attribute
            @param checker the checker to use to check any attributes
            named 'name'
            @return this
        */
        public ElementAttributesChecker
            addRequired(String name, AttributeChecker checker)
        {
            _requiredMap.put(name, checker);
            return this;
        }

        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // Assert.require(e != null);

            Iterator iter = e.getAttributes().iterator();
            while (iter.hasNext())
            {
                Attribute attr = (Attribute) iter.next();
                String name = attr.getName();

                if (_requiredMap.containsKey(name))
                {
                    AttributeChecker checker =
                        (AttributeChecker) _requiredMap.get(name);
                    checker.check(attr);
                    _requiredMap.remove(name);
                }
                else if (_optionalMap.containsKey(name))
                {
                    AttributeChecker checker =
                        (AttributeChecker) _optionalMap.get(name);
                    checker.check(attr);
                }
                else
                {
                    // Found an attribute that isn't allowed.
                    reportError("an element with node path " +
                        nodePath(e) + " has an attribute named '" +
                        name + "' that it is not allowed to have.");
                }
            }

            // Report all missing required attributes.
            iter = _requiredMap.keySet().iterator();
            while (iter.hasNext())
            {
                String missingName = (String) iter.next();
                reportError("an element with node path " + nodePath(e) +
                    " is missing the required attribute named '"+
                    missingName + "'");
            }
        }
    }

    /**
        The class of element checker that checks that an element has some
        text content, other than just whitespace.
    */
    protected class HasTextElementChecker
        implements ElementChecker
    {
        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // Assert.require(e != null);

            String text = e.getTextTrim();
            if (text.length() == 0)
            {
                reportError("an element with node path " + nodePath(e) +
                    " was expected to contain some text other than " +
                    "just whitespace, but it doesn't");
            }
        }
    }

    /**
        The class of element checker that checks that an element has
        no (non-whitespace) text content: that is, that it only contains
        child elements.
    */
    protected class NoTextElementChecker
        implements ElementChecker
    {
        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // Assert.require(e != null);

            String text = e.getTextTrim();
            if (text.length() != 0)
            {
                reportError("an element with node path " + nodePath(e) +
                    " was expected to not contain anything except " +
                    "(possibly) child elements, but it contains the " +
                    "text '" + text);
            }
        }
    }

    /**
        The interface implemented by checkers that check that an element
        has the correct number of child elements of a given type by
        keeping track of how many elements it has checked. (Thus it is
        assumed that the same instance of this class visits all and only
        the child elements of that given type.)
        <p>
        In order for an instance to be able to check that a minimum number
        of child elements of a given type exist, the instance must know
        when all of the child elements of that type have been checked.
        The instance's checkCount() method should be called when it has
        checked all of the child elements of the given type.
    */
    protected interface ChildCountElementChecker
        extends ElementChecker
    {
        // Public methods

        /**
            @return the name of the child elements that this checker
            counts instances of
        */
        public String childName();
            // Assert.ensure(result != null);

        /**
            Checks that the number of child elements checked by this
            checker is an allowed number.
        */
        public void checkCount();
    }

    /**
        An abstract base class for ChildCountElementCheckers.
        <p>
        Subclasses just have to implement checkCount().
    */
    protected abstract class AbstractChildCountElementChecker
        implements ChildCountElementChecker
    {
        // Private fields

        /**
            The parent element of all of the child elements that this
            checker is to check.
        */
        private Element _parent;

        /**
            The name of the child elements that this checker is to
            check.
        */
        private String _childName;

        /**
            The element checker that this checker is to use to check
            the child elements themselves.
        */
        private ElementChecker _childChecker;

        /**
            The number of child elements that this checker has
            checked so far.
        */
        private int _numChecked;


        // Constructors

        /**
            Constructs an AbstractChildCountElementChecker.

            @param parent the parent element of the child elements
            that this checker will be checking
            @param childName the name of all of the child elements
            that this checker will be checking
            @param childChecker the element checker to use to check
            the child elements themselves (rather than just the total
            number of them)
        */
        public AbstractChildCountElementChecker(Element parent,
                    String childName, ElementChecker childChecker)
        {
            // Assert.require(parent != null);
            // Assert.require(childName != null);
            // Assert.require(childChecker != null);

            _parent = parent;
            _childName = childName;
            _childChecker = childChecker;
            _numChecked = 0;
        }


        // Public methods

        /**
            @return the name the child elements that this checker checks
        */
        public String childName()
        {
            // Assert.ensure(_childName != null);
            return _childName;
        }

        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // Assert.require(e != null);

            if (_childName.equals(e.getName()) == false)
            {
                reportInternalError("attempted to apply a " +
                    "ChildCountElementChecker that checks child elements " +
                    "named '" + _childName + "' to a child element named '" +
                    e.getName() + "' with node path " + nodePath(e));
            }

            _numChecked += 1;
            _childChecker.check(e);
        }


        // Protected methods

        /**
            @return the number of child elements that this checker has
            checked
        */
        protected int numberChecked()
        {
            // Assert.ensure(_numChecked >= 0);
            return _numChecked;
        }

        /**
            Reports that our parent element has more than the specified
            maximum number of children of our type.

            @param maxAllowed the maximum number of children of our type
            that our parent is allowed to have
        */
        protected void reportMoreThan(int maxAllowed)
        {
            // Assert.require(maxAllowed >= 0);

            // Assert.check(numberChecked() > maxAllowed);
            reportError("an element with node path " + nodePath(_parent) +
                " has " + numberChecked() + " child elements named '" +
                _childName + "', but is only allowed to have at most " +
                maxAllowed + " child elements with that name");
        }

        /**
            Reports that our parent element has fewer than the specified
            minimum number of children of our type.

            @param minAllowed the minimum number of children of our type
            that our parent is allowed to have
        */
        protected void reportFewerThan(int minAllowed)
        {
            // Assert.require(minAllowed >= 0);

            // Assert.check(numberChecked() < minAllowed);
            reportError("an element with node path " + nodePath(_parent) +
                " has " + numberChecked() + " child elements named '" +
                _childName + "', but is required to have at least " +
                minAllowed + " child element(s) with that name");
        }

        /**
            Reports that our parent element does not have exactly the
            specified number of children of our type.

            @param numAllowed the exact number of children of our type
            that our parent is allowed to have
        */
        protected void reportNotExactly(int numAllowed)
        {
            // Assert.require(numAllowed >= 0);

            // Assert.check(numberChecked() != numAllowed);
            reportError("an element with node path " + nodePath(_parent) +
                " has " + numberChecked() + " child elements named '" +
                _childName + "', but is required to have exactly " +
                numAllowed + " child element(s) with that name");
        }
    }

    /**
        The class of ChildCountElementChecker that checks that there are
        a specific number of child elements of a given type.
    */
    protected class ExactChildCountElementChecker
        extends AbstractChildCountElementChecker
    {
        // Private fields

        /**
            The number of child elements of our type that the parent
            element is allowed to have.
        */
        private int _numAllowed;


        // Constructors

        /**
            Constructs an ExactChildCountElementChecker.

            @param numAllowed the exact number of child elements with
            the specified name that the specified parent element is
            allowed to have
            @see AbstractXmlChecker.AbstractChildCountElementChecker#AbstractXmlChecker.AbstractChildCountElementChecker(Element, String, AbstractXmlChecker.ElementChecker)
        */
        public ExactChildCountElementChecker(int numAllowed,
            Element parent, String childName, ElementChecker childChecker)
        {
            super(parent, childName, childChecker);
            // Assert.require(numAllowed >= 0);

            _numAllowed = numAllowed;
        }


        // Public methods

        /**
            @see AbstractXmlChecker.ChildCountElementChecker#checkCount
        */
        public void checkCount()
        {
            if (numberChecked() != _numAllowed)
            {
                reportNotExactly(_numAllowed);
            }
        }
    }

    /**
        The class of ChildCountElementChecker that checks that there are
        a minimum number of child elements of a given type.
    */
    protected class MinimumChildCountElementChecker
        extends AbstractChildCountElementChecker
    {
        // Private fields

        /**
            The minimum number of child elements of our type that the
            parent element is allowed to have.
        */
        private int _minimumCount;


        // Constructors

        /**
            Constructs a MinimumChildCountElementChecker.

            @param minimumCount the minimum number of child elements with
            the specified name that the specified parent element is
            allowed to have
            @see AbstractXmlChecker.AbstractChildCountElementChecker#AbstractXmlChecker.AbstractChildCountElementChecker(Element, String, AbstractXmlChecker.ElementChecker)
        */
        public MinimumChildCountElementChecker(int minimumCount,
            Element parent, String childName, ElementChecker childChecker)
        {
            super(parent, childName, childChecker);
            // Assert.require(minimumCount >= 0);

            _minimumCount = minimumCount;
        }


        // Public methods

        /**
            @see AbstractXmlChecker.ChildCountElementChecker#checkCount
        */
        public void checkCount()
        {
            if (numberChecked() < _minimumCount)
            {
                reportFewerThan(_minimumCount);
            }
        }
    }

    /**
        The class of ChildCountElementChecker that checks that there are
        no more than a maximum number of child elements of a given type.
    */
    protected class MaximumChildCountElementChecker
        extends AbstractChildCountElementChecker
    {
        // Private fields

        /**
            The maximum number of child elements of our type that the
            parent element is allowed to have.
        */
        private int _maximumCount;


        // Constructors

        /**
            Constructs a MaximumChildCountElementChecker.

            @param maximumCount the maximum number of child elements with
            the specified name that the specified parent element is
            allowed to have
            @see AbstractXmlChecker.AbstractChildCountElementChecker#AbstractXmlChecker.AbstractChildCountElementChecker(Element, String, AbstractXmlChecker.ElementChecker)
        */
        public MaximumChildCountElementChecker(int maximumCount,
            Element parent, String childName, ElementChecker childChecker)
        {
            super(parent, childName, childChecker);
            // Assert.require(maximumCount >= 0);

            _maximumCount = maximumCount;
        }


        // Public methods

        /**
            @see AbstractXmlChecker.ChildCountElementChecker#checkCount
        */
        public void checkCount()
        {
            if (numberChecked() > _maximumCount)
            {
                reportMoreThan(_maximumCount);
            }
        }
    }

    /**
        The class of ChildCountElementChecker that checks that there are
        at least a minimum number and at most a maximum number of child
        elements of a given type.
    */
    protected class RangeChildCountElementChecker
        extends AbstractChildCountElementChecker
    {
        // Private fields

        /**
            The minimum number of child elements of our type that the
            parent element is allowed to have.
        */
        private int _minimumCount;

        /**
            The maximum number of child elements of our type that the
            parent element is allowed to have.
        */
        private int _maximumCount;


        // Constructors

        /**
            Constructs a RangeChildCountElementChecker.

            @param minimumCount the minimum number of child elements with
            the specified name that the specified parent element is
            allowed to have
            @param maximumCount the maximum number of child elements with
            the specified name that the specified parent element is
            allowed to have
            @see AbstractXmlChecker.AbstractChildCountElementChecker#AbstractXmlChecker.AbstractChildCountElementChecker(Element, String, AbstractXmlChecker.ElementChecker)
        */
        public RangeChildCountElementChecker(int minimumCount,
            int maximumCount, Element parent, String childName,
            ElementChecker childChecker)
        {
            super(parent, childName, childChecker);
            // Assert.require(minimumCount >= 0);
            // Assert.require(maximumCount >= 0);
            // Assert.require(minimumCount <= maximumCount);

            _minimumCount = minimumCount;
            _maximumCount = maximumCount;
        }


        // Public methods

        /**
            @see AbstractXmlChecker.ChildCountElementChecker#checkCount
        */
        public void checkCount()
        {
            int numChecked = numberChecked();

            if (numChecked < _minimumCount)
            {
                reportFewerThan(_minimumCount);
            }
            else if (numChecked > _maximumCount)
            {
                reportMoreThan(_maximumCount);
            }
        }
    }


    /**
        The class of element checker that checks all of an element's
        child elements.
    */
    protected class ElementChildrenChecker
        implements ElementChecker
    {
        // Private fields

        /**
            A map from child element names to the checker to use to
            check child elements with that name. The keys are Strings
            and the values are ChildCountElementCheckers.
        */
        private Map _checkerMap = new HashMap();


        // Public methods

        /**
            Adds the specified child count checker as the checker
            that this checker uses to check child elements with the
            name specified by the child count checker's childName()
            method.

            @see AbstractXmlChecker.ChildCountElementChecker#childName
        */
        public ElementChildrenChecker
            add(ChildCountElementChecker childChecker)
        {
            String name = childChecker.childName();
            if (_checkerMap.containsKey(name))
            {
                // TODO: is there a way to more exactly identify this
                // checker ???!!!???
                reportInternalError("an attempt was made to add a " +
                    "ChildCountElementChecker for child elements " +
                    "named '" + name + "' to an ElementChildrenChecker " +
                    "that already has a ChildCountElementChecker for " +
                    "child elements with that name.");
            }
            else
            {
                _checkerMap.put(name, childChecker);
            }

            return this;
        }

        /**
            @see AbstractXmlChecker.ElementChecker#check(Element)
        */
        public void check(Element e)
        {
            // Assert.check(e != null);

            Iterator iter = e.getChildren().iterator();
            while (iter.hasNext())
            {
                Element child = (Element) iter.next();
                String name = child.getName();

                ElementChecker childChecker =
                    (ElementChecker) _checkerMap.get(name);
                if (childChecker != null)
                {
                    childChecker.check(child);
                }
                else
                {
                    reportError("an element with node path " + nodePath(e) +
                        " has a child element named '" + name +
                        "': it is not supposed to have any child elements " +
                        "with that name");
                }
            }

            // Check the counts for each type of child element.
            iter = _checkerMap.values().iterator();
            while (iter.hasNext())
            {
                ChildCountElementChecker childChecker =
                    (ChildCountElementChecker) iter.next();
                childChecker.checkCount();
            }
        }
    }
}
