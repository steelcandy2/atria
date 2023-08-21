<?xml version="1.0"?>
<!--
    $Id: ConstructVisitors.java.xsl,v 1.15 2006/04/17 21:15:01 jgm Exp $

    Transforms a language description document into a file containing the
    source code for all of the automatically-generated parts of the parsers
    used to parse the language. (The file will have to be split into
    separate source files by some other utility.)

    Author: James MacKay
    Last Updated: $Date: 2006/04/17 21:15:01 $

    Copyright (C) 2002-2016 by James MacKay.

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
-->

<!DOCTYPE xsl:transform [
<!ENTITY copy "&#169;">
<!ENTITY nbsp "&#160;">
]>

<xsl:transform version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:import href="constructs.xsl"/>
    <xsl:import href="map-string.xsl"/>
    <xsl:import href="language-common.java.xsl"/>


    <!-- ################# -->
    <!-- # Configuration # -->
    <!-- ################# -->


    <!-- #################### -->
    <!-- # Global variables # -->
    <!-- #################### -->

    <!-- Note: we set 'class-implementation' to the abstract class'
         description. The interface's information is set below. -->
    <xsl:variable name="class-implementation"
        select="$implementation/abstract-construct-visitor-class"/>

    <!-- The name of the interface implemented by all of the construct
         visitor classes for the language. -->
    <xsl:variable name="interface-name"
        select="$implementation/construct-visitor-class/@name"/>

    <!-- The name of the interface's superinterface. -->
    <xsl:variable name="interface-superclass-name"
        select="$implementation/construct-visitor-class/@superclass"/>

    <xsl:variable name="default-construct-type"
        select="$class-implementation/@default-construct-type"/>


    <!-- ################## -->
    <!-- # Main templates # -->
    <!-- ################## -->

    <xsl:template match="language">
        <xsl:text/>

        <xsl:call-template name="readme-file"/>

        <xsl:call-template name="visitor-interface"/>

        <xsl:call-template name="abstract-visitor-class"/>

        <xsl:call-template name="preorder-visitor-class"/>
        <xsl:call-template name="default-preorder-visitor-class"/>

        <xsl:call-template name="postorder-visitor-class"/>
        <xsl:call-template name="default-postorder-visitor-class"/>

        <xsl:call-template name="compound-visitor-class"/>
    </xsl:template>

    <!-- ############################# -->
    <!-- # Visitors README templates # -->
    <!-- ############################# -->

    <xsl:template name="readme-file">
        <xsl:text>%%%% file README.construct-visitors
</xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> Construct Visitors README

Copyright (C) James MacKay

write this !!!!

- etc.
</xsl:text>
    </xsl:template>


    <!-- ############################### -->
    <!-- # Visitor interface templates # -->
    <!-- ############################### -->

    <!-- Generates the interface implemented by all of the language's
         construct visitor classes and interfaces. -->
    <xsl:template name="visitor-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($interface-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The interface implemented by </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct
    visitor classes.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$interface-superclass-name"/>
        <xsl:text>
{
    // Visitor methods</xsl:text>
        <xsl:apply-templates select="$constructs" mode="interface-visitor-methods"/>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="interface-visitor-methods"/>

    <!-- Don't generate visitor methods for non-concrete construct classes. -->
    <xsl:template match="choice-construct | line-choice-construct"
        mode="interface-visitor-methods"/>

    <xsl:template match="*" mode="interface-visitor-methods">
        <xsl:text>

    /**
        Visits the specified </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct.

        @param c the </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct to be visited
        @param handler the error handler to use to handle any errors that
        occur while visiting the construct
    */
    public void visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(</xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>.
        </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> c, ErrorHandler handler);</xsl:text>
    </xsl:template>


    <!-- #################################### -->
    <!-- # Abstract visitor class templates # -->
    <!-- #################################### -->

    <!-- Generates the abstract base class extended by many/most
         of the language's construct visitor classes. -->
    <xsl:template name="abstract-visitor-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;
</xsl:text>
<xsl:value-of select="$imports"/>
        <xsl:text>
import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> classes.
    &lt;p&gt;
    Unless they are overridden each visitor method calls the defaultVisit()
    method.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see #defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text>, ErrorHandler)
*/
public abstract class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Constants

    /** The single construct manager instance. */
    private static final </xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>
        CONSTRUCT_MANAGER = </xsl:text>
        <xsl:value-of select="concat($construct-manager-class-name, '.',
                                     $construct-manager-constructor-name)"/>
        <xsl:text>();


    // Visitor methods
</xsl:text>
        <xsl:apply-templates select="$constructs" mode="abstract-visitor-methods"/>
        <xsl:text>

    // Subconstruct visiting methods
</xsl:text>
        <xsl:apply-templates select="$constructs"
            mode="subconstruct-visiting-methods"/>
        <xsl:text>

    // Protected static methods

    /**
        @return the single construct manager instance
    */
    protected static </xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text> manager()
    {
        Assert.ensure(CONSTRUCT_MANAGER != null);
        return CONSTRUCT_MANAGER;
    }


    // Protected methods

    /**
        The default action performed when visiting a construct.
        &lt;p&gt;
        This implementation calls handleUnexpectedVisit().

        @param c the construct being visited
        @param handler the error handler to use to handle any errors that
        occur while visiting the construct
        @see MinimalAbstractConstructVisitor#handleUnexpectedVisit(Construct, ErrorHandler)
    */
    protected void defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text> c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        handleUnexpectedVisit(c, handler);
    }
}
</xsl:text>
    </xsl:template>

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="abstract-visitor-methods"/>

    <!-- Don't generate visitor methods for non-concrete construct classes. -->
    <xsl:template match="choice-construct | line-choice-construct"
        mode="abstract-visitor-methods"/>

    <xsl:template match="*" mode="abstract-visitor-methods">
        <xsl:call-template name="visitor-method-start"/>
        <xsl:text>
        defaultVisit(c, handler);
    }
</xsl:text>
    </xsl:template>


    <!-- #################################### -->
    <!-- # Preorder visitor class templates # -->
    <!-- #################################### -->

    <!-- Generates an abstract base class for visitor classes that cause
         another visitor to visit constructs and their direct and indirect
         subconstructs in preorder: that is, a construct is visited first
         and then its subconstructs are visited. -->
    <xsl:template name="preorder-visitor-class">
        <xsl:variable name="class"
            select="concat($language-name, 'PreorderConstructVisitor')"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
    classes that visit constructs and their direct and indirect subconstructs
    in preorder: a construct is visited and then all of its subconstructs are
    visited, recursively.
    &lt;p&gt;
    Subclasses just have to implement the visitor() method, though they may
    also want to override beforeVisit() and/or afterVisit().
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see #visitor
    @see #beforeVisit(Construct, ErrorHandler)
    @see #afterVisit(Construct, ErrorHandler)
*/
public abstract class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$class-name"/>  <!-- the abstract visitor class -->
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Visitor methods
</xsl:text>
        <xsl:apply-templates select="$constructs" mode="preorder-visitor-methods"/>
        <xsl:text>

    // Protected methods

    /**
        The method called for each construct visited by this visitor before
        either the construct or any of its subconstructs are visited.

        @param c the construct about to be visited
        @param handler the error handler passed to the visitor method that
        visited 'c'
    */
    protected void beforeVisit(Construct c, ErrorHandler handler)
    {
        // Assert.require(c != null);
        // Assert.require(handler != null);

        // empty
    }

    /**
        The method called for each construct visited by this visitor after
        the construct ans any of its subconstructs are visited.

        @param c the construct that has been visited
        @param handler the error handler passed to the visitor method that
        visited 'c'
    */
    protected void afterVisit(Construct c, ErrorHandler handler)
    {
        // Assert.require(c != null);
        // Assert.require(handler != null);

        // empty
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text>, ErrorHandler)
    */
    protected void defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text> c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since all visitor methods should have been overridden above
    }


    // Abstract methods

    /**
        @return the visitor that this visitor is to cause to visit
        constructs and their subconstructs
    */
    protected abstract </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> visitor();
        // Assert.ensure(result != null);
}
</xsl:text>
    </xsl:template>

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="preorder-visitor-methods"/>

    <!-- Don't generate visitor methods for non-concrete construct classes. -->
    <xsl:template match="choice-construct | line-choice-construct"
        mode="preorder-visitor-methods"/>

    <xsl:template match="*" mode="preorder-visitor-methods">
        <xsl:call-template name="visitor-method-start"/>
        <xsl:text>
        beforeVisit(c, handler);

        // Visit the construct itself.
        c.accept(visitor(), handler);

        // Visit all of its subconstructs.
        visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Subconstructs(c, handler);

        afterVisit(c, handler);
    }
</xsl:text>
    </xsl:template>

    <!-- Generates the default preorder construct visitor class: it is
         constructed from the visitor that the instance is to cause to
         visit constructs and their subconstructs. -->
    <xsl:template name="default-preorder-visitor-class">
        <xsl:variable name="class"
            select="concat($language-name, 'DefaultPreorderConstructVisitor')"/>
        <xsl:variable name="superclass"
            select="concat($language-name, 'PreorderConstructVisitor')"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

/**
    The default </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>: it is constructed
    from the visitor that the instance is to cause to visit each construct
    and its subconstructs.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
{
    // Private fields

    /**
        The visitor that this visitor is to cause to visit constructs and
        their subconstructs.
    */
    private </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> _visitor;


    // Constructors

    /**
        Constructs a </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>.

        @param v the visitor that the visitor being constructed is to cause
        to visit constructs and their subconstructs
    */
    public </xsl:text>
        <xsl:value-of select="concat($class, '(', $interface-name, ' v)')"/>
        <xsl:text>
    {
        Assert.require(v != null);

        _visitor = v;
    }


    // Protected methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>#visitor
    */
    protected </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> visitor()
    {
        Assert.ensure(_visitor != null);
        return _visitor;
    }
}
</xsl:text>
    </xsl:template>


    <!-- ##################################### -->
    <!-- # Postorder visitor class templates # -->
    <!-- ##################################### -->

    <!-- Generates an abstract base class for visitor classes that cause
         another visitor to visit constructs and their direct and indirect
         subconstructs in postorder: that is, a construct's subconstructs
         are visited first and then the construct itself is visited. -->
    <xsl:template name="postorder-visitor-class">
        <xsl:variable name="class"
            select="concat($language-name, 'PostorderConstructVisitor')"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    An abstract base class for </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
    classes that visit constructs and their direct and indirect subconstructs
    in postorder: a construct's subconstructs are visited and then the
    construct itself is visited, recursively.
    &lt;p&gt;
    Subclasses just have to implement the visitor() method.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see #visitor
*/
public abstract class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$class-name"/>  <!-- the abstract visitor class -->
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Visitor methods
</xsl:text>
    <xsl:apply-templates select="$constructs" mode="postorder-visitor-methods"/>
    <xsl:text>

    // Protected methods

    /**
        The method called for each construct visited by this visitor before
        either the construct or any of its subconstructs are visited.

        @param c the construct about to be visited
        @param handler the error handler passed to the visitor method that
        visited 'c'
    */
    protected void beforeVisit(Construct c, ErrorHandler handler)
    {
        // Assert.require(c != null);
        // Assert.require(handler != null);

        // empty
    }

    /**
        The method called for each construct visited by this visitor after
        the construct ans any of its subconstructs are visited.

        @param c the construct that has been visited
        @param handler the error handler passed to the visitor method that
        visited 'c'
    */
    protected void afterVisit(Construct c, ErrorHandler handler)
    {
        // Assert.require(c != null);
        // Assert.require(handler != null);

        // empty
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>#defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text>, ErrorHandler)
    */
    protected void defaultVisit(</xsl:text>
        <xsl:value-of select="$default-construct-type"/>
        <xsl:text> c,
                                ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);

        Assert.unreachable();
            // since all visitor methods should have been overridden above
    }


    // Abstract methods

    /**
        @return the visitor that this visitor is to cause to visit
        constructs and their constructs
    */
    protected abstract </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> visitor();
        // Assert.ensure(result != null);
}
</xsl:text>
    </xsl:template>

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="postorder-visitor-methods"/>

    <!-- Don't generate visitor methods for non-concrete construct classes. -->
    <xsl:template match="choice-construct | line-choice-construct"
        mode="postorder-visitor-methods"/>

    <xsl:template match="*" mode="postorder-visitor-methods">
        <xsl:call-template name="visitor-method-start"/>
        <xsl:text>
        beforeVisit(c, handler);

        // Visit all of its subconstructs.
        visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Subconstructs(c, handler);

        // Visit the construct itself.
        c.accept(visitor(), handler);

        afterVisit(c, handler);
    }
</xsl:text>
    </xsl:template>

    <!-- Generates the default postorder construct visitor class: it is
         constructed from the visitor that the instance is to cause to
         visit constructs and their subconstructs. -->
    <xsl:template name="default-postorder-visitor-class">
        <xsl:variable name="class"
            select="concat($language-name, 'DefaultPostorderConstructVisitor')"/>
        <xsl:variable name="superclass"
            select="concat($language-name, 'PostorderConstructVisitor')"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

/**
    The default </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>: it is constructed
    from the visitor that the instance is to cause to visit each construct
    and its subconstructs.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
{
    // Private fields

    /**
        The visitor that this visitor is to cause to visit constructs and
        their subconstructs.
    */
    private </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> _visitor;


    // Constructors

    /**
        Constructs a </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>.

        @param v the visitor that the visitor being constructed is to cause
        to visit constructs and their subconstructs
    */
    public </xsl:text>
        <xsl:value-of select="concat($class, '(', $interface-name, ' v)')"/>
        <xsl:text>
    {
        Assert.require(v != null);

        _visitor = v;
    }


    // Protected methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>#visitor
    */
    protected </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> visitor()
    {
        Assert.ensure(_visitor != null);
        return _visitor;
    }
}
</xsl:text>
    </xsl:template>


    <!-- #################################### -->
    <!-- # Compound visitor class templates # -->
    <!-- #################################### -->

    <!-- Generates the visitor class that causes two other visitors
         to visit each of the constructs that it visits. -->
    <xsl:template name="compound-visitor-class">
        <xsl:variable name="class"
            select="concat($language-name, 'CompoundConstructVisitor')"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$constructs-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.constructs.*;
import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    The </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> class that causes two other visitors
    to visit each of the constructs that it visits.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends MinimalAbstractConstructVisitor
    implements </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text>
{
    // Private fields

    /** The visitor that visits the constructs first. */
    private </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> _first;

    /** The visitor that visits the constructs second. */
    private </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> _second;


    // Constructors

    /**
        Constructs a visitor from the two visitors that it will cause
        to visit each construct that it visits.

        @param first the visitor that will visit each construct first
        (i.e. before 'second' does)
        @param second the visitor that will visit each construct
        second (i.e. after 'first' does)
    */
    public </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>(
        </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> first,
        </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> second)
    {
        Assert.require(first != null);
        Assert.require(second != null);

        _first = first;
        _second = second;
    }


    // Visitor methods
</xsl:text>
        <xsl:apply-templates select="$constructs" mode="compound-visitor-methods"/>
        <xsl:text>

    // Protected methods

    /**
        @return the visitor that is to visit each construct first
    */
    protected </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> first()
    {
        return _first;
    }

    /**
        @return the visitor that is to visit each construct second
    */
    protected </xsl:text>
        <xsl:value-of select="$interface-name"/>
        <xsl:text> second()
    {
        return _second;
    }
}
</xsl:text>
    </xsl:template>

    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="compound-visitor-methods"/>

    <!-- Don't generate visitor methods for non-concrete construct classes. -->
    <xsl:template match="choice-construct | line-choice-construct"
        mode="compound-visitor-methods"/>

    <xsl:template match="*" mode="compound-visitor-methods">
        <xsl:call-template name="visitor-method-start"/>
        <xsl:text>
        c.accept(first(), handler);
        c.accept(second(), handler);
    }
</xsl:text>
    </xsl:template>


    <!-- ################################### -->
    <!-- # Subconstruct visiting templates # -->
    <!-- ################################### -->


    <!-- Ignore the notes. -->
    <xsl:template match="notes" mode="subconstruct-visiting-methods"/>

    <xsl:template match="*" mode="subconstruct-visiting-methods">
        <xsl:text>
    /**
        Visits all of the subconstructs of the specified </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>.

        @param c the construct whose subconstructs are to be visited
        @param handler the error handler to use to handle any errors that
        occur while visiting the subconstructs of 'c'
    */
    protected void visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Subconstructs(</xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>.
        </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);
</xsl:text>
        <xsl:apply-templates select="*" mode="visit-subconstructs-code"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="first-line | indented-subconstructs"
        mode="visit-subconstructs-code">
        <xsl:apply-templates select="*" mode="visit-subconstructs-code"/>
    </xsl:template>

    <xsl:template match="subconstruct" mode="visit-subconstructs-code">
        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="method-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name" select="$name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="@number='zero-or-more' or
                            @number='one-or-more'">
                <xsl:text>
        if (c.</xsl:text>
                <xsl:value-of select="$method-name"/>
                <xsl:text>Count() &gt; 0)
        {
            visitAll(c.</xsl:text>
                <xsl:value-of select="$method-name"/>
                <xsl:text>List(), handler);
        }</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
        if (c.has</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>())
        {
            c.</xsl:text>
                <xsl:value-of select="$method-name"/>
                <xsl:text>().accept(this, handler);
        }</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Ignore all other construct parts. -->
    <xsl:template match="*" mode="visit-subconstructs-code"/>


    <!-- ########################### -->
    <!-- # Miscellaneous templates # -->
    <!-- ########################### -->

    <!-- Outputs the start of a construct visitor method definition
         for the construct described by the specified node. -->
    <xsl:template name="visitor-method-start">
        <xsl:param name="construct-node" select="."/>

        <xsl:variable name="full-type"
            select="concat($construct-manager-class-name, '.', @type)"/>

        <xsl:text>
    /**
        @see </xsl:text>
        <xsl:value-of select="concat($interface-name, '#visit', @type, '(', $construct-manager-base-class-name, '.', @type, ', ErrorHandler)')"/>
        <xsl:text>
    */
    public void visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(</xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text>.
        </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> c, ErrorHandler handler)
    {
        Assert.require(c != null);
        Assert.require(handler != null);
</xsl:text>
    </xsl:template>
</xsl:transform>
