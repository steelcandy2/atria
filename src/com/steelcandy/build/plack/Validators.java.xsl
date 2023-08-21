<?xml version="1.0"?>
<!--
    Transforms a language description document into a file containing the
    source code for all of the automatically-generated parts of the
    validators and constraint checkers used to check whether constructs
    that represent parts of the language satisfy their validity
    constraints. (The file will have to be split into separate source files
    by some other utility.)

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

    <xsl:import href="language-common.java.xsl"/>


    <!-- ################# -->
    <!-- # Configuration # -->
    <!-- ################# -->


    <!-- #################### -->
    <!-- # Global variables # -->
    <!-- #################### -->

    <xsl:variable name="class-implementation"
        select="$implementation/validity-constraints"/>

    <xsl:variable name="construct-interface"
        select="$implementation/construct-manager-base-class/base-construct-superinterface/@name"/>
    <xsl:variable name="visitor-name"
        select="$implementation/construct-visitor-class/@name"/>
    <xsl:variable name="postorder-visitor-class-name"
        select="concat($language-name, 'PostorderConstructVisitor')"/>
    <xsl:variable name="abstract-visitor-class-name"
        select="concat($language-name, 'AbstractConstructVisitor')"/>

    <!-- All of the validity constraints elements (excluding definitions). -->
    <xsl:variable name="validity-constraints"
        select="$top/validity-constraints/section/*[local-name() != 'definition']"/>


    <!-- ############################################# -->
    <!-- # Class name global variables and templates # -->
    <!-- ############################################# -->

    <xsl:variable name="names-interface-name"
        select="concat($language-name, 'ValidityConstraintNames')"/>

    <xsl:variable name="constraint-checks-class-name"
        select="concat($language-name, 'ConstraintChecks')"/>
    <xsl:variable name="constraint-checks-base-class-name"
        select="concat($constraint-checks-class-name, 'Base')"/>


    <!-- ################## -->
    <!-- # Main templates # -->
    <!-- ################## -->

    <xsl:template match="language">
        <xsl:text/>

        <xsl:call-template name="readme-file"/>
        <xsl:call-template name="constraint-names-list"/>

        <xsl:call-template name="constraint-names-interface"/>

        <!-- Generate ConstraintChecks class source files/skeletons. -->
        <xsl:call-template name="constraint-checks-base-class"/>
        <xsl:call-template name="constraint-checks-class-skeleton"/>
    </xsl:template>


    <!-- ############################### -->
    <!-- # Validators README templates # -->
    <!-- ############################### -->

    <xsl:template name="readme-file">
        <xsl:variable name="lang" select="$language-name"/>

        <xsl:text>%%%% file README.validators
</xsl:text>
        <xsl:value-of select="$lang"/>
        <xsl:text> Validators README

Copyright (C) James MacKay

- REWRITE THIS DOCUMENT !!!!

- the file constraint-names.txt lists the names of all of the validity
  constraints, one per line
- ConstraintChecks
    * </xsl:text>
        <xsl:value-of select="$constraint-checks-base-class-name"/>
        <xsl:text>
        - an abstract base class for </xsl:text>
        <xsl:value-of select="$constraint-checks-class-name"/>
        <xsl:text>
          that check that various validity constraints hold, and
          handles the violation of various validity constraints
    * </xsl:text>
        <xsl:value-of select="$constraint-checks-class-name"/>
        <xsl:text>
        - a skeleton for the language's ConstraintChecks singleton class
</xsl:text>
    </xsl:template>


    <!-- ################################### -->
    <!-- # Constraint names list templates # -->
    <!-- ################################### -->

    <xsl:template name="constraint-names-list">
        <xsl:text>%%%% file constraint-names.txt
</xsl:text>

        <xsl:for-each select="$validity-constraints">
            <xsl:apply-templates select="constraint" mode="constraint-names-list">
                <xsl:with-param name="construct-type" select="@type"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="constraint" mode="constraint-names-list">
        <xsl:param name="construct-type"/>

        <xsl:value-of select="concat($construct-type, '.', @name)"/>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <!-- ######################################## -->
    <!-- # Constraint names interface templates # -->
    <!-- ######################################## -->

    <!-- Generates the interface that contains constants representing
         the names of the language's validity constraints. -->
    <xsl:template name="constraint-names-interface">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($names-interface-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$validation-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

/**
    The interface that defines constants representing the names of
    the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> validity constraints.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$names-interface-name"/>
        <xsl:text>
{</xsl:text>
        <xsl:apply-templates select="$validity-constraints" mode="constraint-names"/>
        <xsl:text>
}
</xsl:text>
    </xsl:template>

    <xsl:template match="construct-constraints" mode="constraint-names">
        <xsl:if test="not(@always-valid = 'true')">
            <xsl:text>
    // </xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text> Constraint Names
</xsl:text>
            <xsl:if test="count(constraint) &gt; 0">
                <xsl:text>
    public static final String</xsl:text>
            </xsl:if>
            <xsl:apply-templates select="constraint" mode="constraint-names">
                <xsl:with-param name="construct-type" select="@type"/>
            </xsl:apply-templates>
            <xsl:text>

</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="constraint" mode="constraint-names">
        <xsl:param name="construct-type"/>

        <xsl:variable name="name">
            <xsl:call-template name="constraint-name">
                <xsl:with-param name="construct-type"
                    select="$construct-type"/>
                <xsl:with-param name="constraint-name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="value"
            select="concat($construct-type, '.', @name)"/>

        <xsl:text>
        </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> =
            &quot;</xsl:text>
        <xsl:value-of select="$value"/>
        <xsl:text>&quot;</xsl:text>
        <xsl:choose>
            <xsl:when test="position() = last()">
                <xsl:text>;</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>,</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- ##################################### -->
    <!-- # Constraint checks class templates # -->
    <!-- ##################################### -->

    <!-- Generates the abstract base class for the class that checks
         and handles the violations of  validity constraints. -->
    <xsl:template name="constraint-checks-base-class">
        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($constraint-checks-base-class-name, $src-ext)"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$validation-module"/>
        <xsl:text>;

import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($constructs-module, '.',
                                     $construct-manager-class-name, ';')"/>
        <xsl:text>

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.errors.ErrorKey;
import com.steelcandy.plack.common.errors.ErrorSeverityLevels;  // javadocs
import com.steelcandy.plack.common.constructs.Construct;
import com.steelcandy.plack.common.semantic.CommonConstraintChecks;
import com.steelcandy.plack.common.semantic.ValidityConstraintAlreadyCheckedException;

/**
    An abstract base class for classes that check </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> validity
    constraints and handles their violation.
    </xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public abstract class </xsl:text>
        <xsl:value-of select="$constraint-checks-base-class-name"/>
        <xsl:text>
    extends CommonConstraintChecks
    implements </xsl:text>
        <xsl:value-of select="$names-interface-name"/>
        <xsl:text>
{
    // Mark constraints checked methods

</xsl:text>
        <xsl:apply-templates select="$validity-constraints"
                             mode="mark-constraints-checked"/>
        <xsl:text>
    // Handle constraint violations methods

</xsl:text>
        <xsl:apply-templates select="$validity-constraints"
                             mode="handle-violated-constraints"/>
        <xsl:text>}
</xsl:text>
    </xsl:template>

    <xsl:template match="construct-constraints"
                                    mode="mark-constraints-checked">
        <xsl:variable name="construct-type" select="@type"/>

        <xsl:for-each select="constraint">
            <xsl:variable name="constraint-name">
                <xsl:call-template name="constraint-name">
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="constraint-name" select="@name"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>    /**
        Marks that the specified </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text> has had its
        </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> constraint checked.

        @param c the </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text> construct to mark that
        it had its </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> constraint checked
        @exception ValidityConstraintAlreadyCheckedException thrown
        iff the </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> has already
        been checked on 'c'
    */
    public void checked</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(
        </xsl:text>
            <xsl:value-of select="concat($construct-manager-class-name,
                                         '.', $construct-type)"/>
            <xsl:text> c)
        throws ValidityConstraintAlreadyCheckedException
    {
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());

        c.validityChecklist().
            markChecked(</xsl:text>
            <xsl:value-of select="$constraint-name"/>
            <xsl:text>);
    }

    /**
        Marks that the specified (presumably </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>)
        construct has had its </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> constraint checked.

        @param c the construct to mark that it has had its
        </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> constraint checked
        @exception ValidityConstraintAlreadyCheckedException thrown
        iff the </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> has already
        been checked on 'c'
        @exception ClassCastException thrown iff 'c' is not a(n)
        </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>
    */
    public void checkedUntyped</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(Construct c)
    {
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());

        // The use of toUnsafeConstruct() is OK here since we don't make any
        // assumptions about its state: we just mark one of its constraints
        // as having been checked.
        checked</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(
            (</xsl:text>
            <xsl:value-of select="concat($construct-manager-class-name,
                                         '.', $construct-type)"/>
            <xsl:text>) c.toUnsafeConstruct());
    }

</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="construct-constraints"
                                    mode="handle-violated-constraints">
        <xsl:variable name="construct-type" select="@type"/>

        <xsl:for-each select="constraint">
            <xsl:variable name="constraint-name">
                <xsl:call-template name="constraint-name">
                    <xsl:with-param name="construct-type"
                        select="$construct-type"/>
                    <xsl:with-param name="constraint-name" select="@name"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:text>    /**
        Handles the violation of the </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> validity
        constraint on a given </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>.

        @param level the severity level of the error resulting from
        the constraint being violated
        @param description a description of exactly how the constraint
        was violated
        @param c the construct that violated the </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>'s
        validity constraint: it is not necessarily the
        </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text> construct itself
        @param handler the error handler to use to handle the
        constraint violation
    */
    public void violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(int level,
        String description, Construct c, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(handler != null);

        violatedConstraint(level, </xsl:text>
            <xsl:value-of select="$constraint-name"/>
            <xsl:text>,
                           description, c, handler);
    }

    /**
        Handles the violation of the </xsl:text>
            <xsl:value-of select="@name"/>
            <xsl:text> validity
        constraint on a given </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>.

        @param level the severity level of the error resulting from
        the constraint being violated
        @param description a description of exactly how the constraint
        was violated
        @param c the construct that violated the </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text>'s
        validity constraint: it is not necessarily the
        </xsl:text>
            <xsl:value-of select="$construct-type"/>
            <xsl:text> construct itself
        @param key the key to associate with the error used to report the
        violated constraint
        @param handler the error handler to use to handle the
        constraint violation
    */
    public void violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(int level,
        String description, Construct c, ErrorKey key, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(key != null);
        Assert.require(handler != null);

        violatedConstraint(level, </xsl:text>
            <xsl:value-of select="$constraint-name"/>
            <xsl:text>,
                           description, c, key, handler);
    }

    /**
        This method is the same as the other method in this class with
        the same name, except that the error severity level defaults to
        NON_FATAL_ERROR_LEVEL.

        @see #violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(int, String, Construct, ErrorHandler)
        @see ErrorSeverityLevels#NON_FATAL_ERROR_LEVEL
    */
    public void violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(
        String description, Construct c, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(handler != null);

        violatedConstraint(NON_FATAL_ERROR_LEVEL,
                    </xsl:text>
            <xsl:value-of select="$constraint-name"/>
            <xsl:text>,
                           description, c, handler);
    }

    /**
        This method is the same as the other method in this class with
        the same name, except that the error severity level defaults to
        NON_FATAL_ERROR_LEVEL.

        @see #violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(int, String, Construct, ErrorKey, ErrorHandler)
        @see ErrorSeverityLevels#NON_FATAL_ERROR_LEVEL
    */
    public void violated</xsl:text>
            <xsl:value-of select="concat($construct-type, @name)"/>
            <xsl:text>(
        String description, Construct c, ErrorKey key, ErrorHandler handler)
    {
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(key != null);
        Assert.require(handler != null);

        violatedConstraint(NON_FATAL_ERROR_LEVEL,
                    </xsl:text>
            <xsl:value-of select="$constraint-name"/>
            <xsl:text>,
                           description, c, key, handler);
    }

</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="constraint-checks-class-skeleton">
        <xsl:variable name="class-name"
            select="$constraint-checks-class-name"/>
        <xsl:variable name="superclass-name"
            select="$constraint-checks-base-class-name"/>

        <xsl:text>%%%% file </xsl:text>
        <xsl:value-of select="concat($class-name, $src-ext, '.skeleton')"/>
        <xsl:text>
// Copyright (C) James MacKay

package </xsl:text>
        <xsl:value-of select="$validation-module"/>;
        <xsl:text>
import com.steelcandy.common.debug.Assert;

import </xsl:text>
        <xsl:value-of select="concat($constructs-module, '.',
                                     $construct-manager-class-name)"/>
        <xsl:text>;

import com.steelcandy.plack.common.errors.ErrorHandler;

/**
    A singleton class that contains methods that perform various
    </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> validity constraint checks.
    &lt;p&gt;
    This class provides a place to put these checks without worrying
    about which constraint checker class they should appear in, etc.
    </xsl:text>
        <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass-name"/>
        <xsl:text>
{
    // Constants

    /** The single instance of this class. */
    private static final </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
        _instance = new </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>();


    // Constructors

    /**
        @return the single instance of this class
    */
    public static </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text> instance()
    {
        return _instance;
    }

    /**
        This constructor should only be used to construct
        the single instance of this class.

        @see #instance
    */
    private </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }
}
</xsl:text>
    </xsl:template>


    <!-- ########################### -->
    <!-- # Miscellaneous templates # -->
    <!-- ########################### -->

    <!-- Outputs the name of the constant that represents the name of
         the validity constraint with the specified name on the construct
         of the specified type. -->
    <xsl:template name="constraint-name">
        <xsl:param name="construct-type"/>
        <xsl:param name="constraint-name"/>

        <xsl:variable name="part1">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$construct-type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="part2">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$constraint-name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:value-of select="concat($part1, '_', $part2)"/>
    </xsl:template>
</xsl:transform>
