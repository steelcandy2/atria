<?xml version="1.0"?>
<!--
    Transforms a language description document into the Java classes
    that represent the language's constructs' validity constraint
    checklists, as well as the factory classes that create the
    validity constraint checklist classes.

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


    <!-- Configuration -->


    <!-- Global variables -->

    <xsl:variable name="class-implementation"
        select="$implementation/validity-constraint-checklist-factory-base-class"/>
    <xsl:variable name="visitor-class-name"
        select="concat($language-name, 'ChecklistCompletionConstructVisitor')"/>
    <xsl:variable name="completed-visitor-class-name"
        select="concat($language-name, 'CompletedChecklistCompletionConstructVisitor')"/>
    <xsl:variable name="base-construct-interface"
        select="$implementation/construct-manager-base-class/base-construct-superinterface/@name"/>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:text/>

        <!-- Generate the checklist factory README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the checklist factory base class. -->
        <xsl:call-template name="checklist-factory-base-class"/>

        <!-- Generate skeleton for the default checklist factory class. -->
        <xsl:call-template name="default-checklist-factory-class-skeleton"/>

        <!-- Generate the singleton checklist factory class. -->
        <xsl:call-template name="checklist-factory-class"/>

        <!-- Generate the checklist completion construct visitor interface. -->
        <xsl:call-template name="checklist-completion-visitor-interface"/>

        <!-- Generate the completed checklist completion construct
             visitor class. -->
        <xsl:call-template
            name="completed-checklist-completion-visitor-class"/>
    </xsl:template>


    <xsl:template name="readme-file">
        <xsl:text>%%%% file README.validity-constraint-checklists
</xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> Construct Managers README

Copyright (C) James MacKay

write this !!!!

- validity-constraint-checklists.ignore
- validity-constraint-checklists.discard

- create Default</xsl:text>
        <xsl:value-of select="concat($validity-constraint-checklist-factory-class-name, $src-ext)"/>
        <xsl:text> from
  Default</xsl:text>
        <xsl:value-of select="concat($validity-constraint-checklist-factory-class-name, $src-ext)"/>
        <xsl:text>.skeleton
    - add any extra code (though it can be left unchanged)
- once a skeleton file has been used to create the 'real' file, the
  skeleton file's name is usually added to the discard file
  (validity-constraint-checklists.discard)

- etc.</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the singleton checklist factory
         class. -->
    <xsl:template name="checklist-factory-class">
        <xsl:variable name="class-name"
            select="$validity-constraint-checklist-factory-class-name"/>
        <xsl:variable name="superclass-name"
            select="concat('Default', $class-name)"/>
        <xsl:variable name="ctor-name"
            select="$validity-constraint-checklist-factory-constructor-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    The singleton </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> validity constraint checklist
    factory class.
</xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
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
        <xsl:value-of select="concat($class-name, ' ', $ctor-name)"/>
        <xsl:text>()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs the single instance of this class.
    */
    private </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }
}</xsl:text>
    </xsl:template>


    <!-- Outputs the skeleton of the source code for the default
         checklist factory class. -->
    <xsl:template name="default-checklist-factory-class-skeleton">
        <xsl:variable name="class"
            select="concat('Default', $validity-constraint-checklist-factory-class-name)"/>
        <xsl:variable name="superclass" select="$class-name"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default validity constraint checklist factory class.
</xsl:text>
    <xsl:call-template name="common-skeleton-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$class"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
{
    // empty
}</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the interface implemented by
         construct visitors that check for unchecked validity
         constraints. -->
    <xsl:template name="checklist-completion-visitor-interface">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$visitor-class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.constructs.*;

/**
    The interface implemented by construct visitors that check which
    constructs' validity constraints have not been marked as having
    been checked in their validity constraint checklists.
</xsl:text>
        <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="concat($class-name, '#createPreorderChecklistCompletionVisitor')"/>
        <xsl:text>
    @see </xsl:text>
        <xsl:value-of select="concat($class-name, '#createPostorderChecklistCompletionVisitor')"/>
        <xsl:text>
*/
public interface </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text>, ChecklistCompletionConstructVisitor
{
    // empty
}</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the class of construct visitor
         that 'checks' for unchecked validity constraints on constructs
         with pre-completed checklists. -->
    <xsl:template name="completed-checklist-completion-visitor-class">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name"
                select="$completed-visitor-class-name"/>
        </xsl:call-template>
        <xsl:text>
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.UncheckedValidityConstraint;

import com.steelcandy.common.containers.Containers;

import java.util.List;

/**
    The singleton class of </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> that
    'checks' for unchecked validity constraints on constructs that
    all have pre-completed validity constraint checklists.
</xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
*/
public class </xsl:text>
        <xsl:value-of select="$completed-visitor-class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$abstract-construct-visitor-class-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
{
    // Constants

    /** The sole instance of this class. */
    private static final </xsl:text>
        <xsl:value-of select="$completed-visitor-class-name"/>
        <xsl:text>
        _instance = new </xsl:text>
        <xsl:value-of select="$completed-visitor-class-name"/>
        <xsl:text>();


    // Constructors

    /**
        @return the sole instance of this class
    */
    public static </xsl:text>
        <xsl:value-of select="$completed-visitor-class-name"/>
        <xsl:text> instance()
    {
        Assert.ensure(_instance != null);
        return _instance;
    }

    /**
        Constructs the sole instance of this class.
    */
    private </xsl:text>
        <xsl:value-of select="$completed-visitor-class-name"/>
        <xsl:text>()
    {
        // empty
    }


    // Public methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#areUncheckedConstraints
    */
    public boolean areUncheckedConstraints()
    {
        return false;
    }

    /**
        @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#uncheckedConstraintsInformation
    */
    public List uncheckedConstraintsInformation()
    {
        return Containers.emptyList();
    }


    // Protected methods

    /**
        @see </xsl:text>
        <xsl:value-of select="$abstract-construct-visitor-class-name"/>
        <xsl:text>#defaultVisit(</xsl:text>
        <xsl:value-of select="$base-construct-interface"/>
        <xsl:text>, ErrorHandler)
    */
    protected void defaultVisit(</xsl:text>
        <xsl:value-of select="$base-construct-interface"/>
        <xsl:text> c, ErrorHandler handler)
    {
        // empty - do nothing for each construct
    }
}</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the checklist factory base class. -->
    <xsl:template name="checklist-factory-base-class">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:value-of select="$imports"/>
        <xsl:text>
import </xsl:text>
        <xsl:value-of select="concat($validation-module, '.', $language-name, 'ValidityConstraintNames;')"/>
        <xsl:text>

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
    An abstract base class for factories that create validity constraint
    checklists for the various </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> constructs.
    &lt;p&gt;
    Instances can also create construct visitors that can determine
    which of a tree of constructs' validity constraints are not marked
    as having been checked.
</xsl:text>
    <xsl:call-template name="common-class-comment-part"/>
        <xsl:text>
    @see Construct
*/
public abstract class </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>
    extends </xsl:text>
        <xsl:value-of select="$superclass-name"/>
        <xsl:text>
    implements </xsl:text>
        <xsl:value-of select="concat($language-name, 'ValidityConstraintNames')"/>
        <xsl:text>
{
    // Constructors

    /**
        Constructs </xsl:text>
        <xsl:value-of select="concat($language-name-article, ' ', $language-name)"/>
        <xsl:text> validity constraint checklist factory.
    */
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        super(</xsl:text>
        <xsl:choose>
            <!-- If we're debugging then we don't want to create checklists
                 that are always complete. -->
            <xsl:when test="$do-debug-validity-constraints = 'true'">
                <xsl:text>false</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>true</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>);
    }


    // Public methods

    /**
        @return true iff validity constraint checklists will be checked to
        see whether all constraints have been checked for each construct
    */
    public boolean areCheckingValidityConstraintChecklists()
    {
        return </xsl:text>
        <xsl:choose>
            <xsl:when test="$do-debug-validity-constraints = 'true'">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>false</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:text>;
    }


    // Factory methods
</xsl:text>
<xsl:apply-templates select="$constructs" mode="checklist-factory-methods"/>
<xsl:text>

    // Add constraints methods
</xsl:text>
<xsl:apply-templates select="$constructs" mode="add-constraint-methods"/>
<xsl:text>


    // Protected methods

    /**
        @return the instance of the construct manager
    */
    protected </xsl:text>
        <xsl:value-of select="$construct-manager-class-name"/>
        <xsl:text> manager()
    {
        return </xsl:text>
        <xsl:value-of select="concat($construct-manager-class-name, '.', $construct-manager-constructor-name, '();')"/>
        <xsl:text>
    }


    // Visitor creation methods

    /**
        @return a construct visitor that visits constructs and their direct
        and indirect subconstructs in preorder to determine the ones on
        which their validity constraints have not all been marked as having
        been checked
    */
    public </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
        createPreorderChecklistCompletionVisitor()
    {
        </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> result =
            createCompletedChecklistVisitor();

        if (result == null)
        {
            result = new PreorderChecklistCompletionVisitor();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a construct visitor that visits constructs and their direct
        and indirect subconstructs in postorder to determine the ones on
        which their validity constraints have not all been marked as having
        been checked
    */
    public </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
        createPostorderChecklistCompletionVisitor()
    {
        </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> result =
            createCompletedChecklistVisitor();

        if (result == null)
        {
            result = new PostorderChecklistCompletionVisitor();
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a visitor that 'checks' completed checklists efficiently
        if this factory creates completed checklists; returns null
        otherwise
    */
    protected </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
        createCompletedChecklistVisitor()
    {
        </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> result = null;

        if (createCompletedChecklist() != null)
        {
            result = </xsl:text>
            <xsl:value-of select="$completed-visitor-class-name"/>
            <xsl:text>.instance();
        }

        // 'result' may be null
        return result;
    }


    // Visitor inner classes

    /**
        The construct visitor that checks a construct and its direct
        and indirect subconstructs for unchecked validity constraints
        in preorder.
    */
    protected static class PreorderChecklistCompletionVisitor
        extends </xsl:text>
        <xsl:value-of select="concat($language-name, 'PreorderConstructVisitor')"/>
        <xsl:text>
        implements </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
    {
        // Private fields

        /** The visitor to use to check each construct. */
        private </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> _visitor;


        // Constructors

        /**
            Constructs a PreorderChecklistCompletionVisitor.
        */
        public PreorderChecklistCompletionVisitor()
        {
            _visitor = new ChecklistCompletionVisitor();
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#areUncheckedConstraints
        */
        public boolean areUncheckedConstraints()
        {
            return _visitor.areUncheckedConstraints();
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#uncheckedConstraintsInformation
        */
        public List uncheckedConstraintsInformation()
        {
            return _visitor.uncheckedConstraintsInformation();
        }


        // Protected methods

        /**
            @see </xsl:text>
            <xsl:value-of select="concat($language-name, 'PreorderConstructVisitor')"/>
            <xsl:text>#visitor
        */
        protected </xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor()
        {
            return _visitor;
        }
    }

    /**
        The construct visitor that checks a construct and its direct
        and indirect subconstructs for unchecked validity constraints
        in postorder.
    */
    protected static class PostorderChecklistCompletionVisitor
        extends </xsl:text>
        <xsl:value-of select="concat($language-name, 'PostorderConstructVisitor')"/>
        <xsl:text>
        implements </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
    {
        // Private fields

        /** The visitor to use to check each construct. */
        private </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> _visitor;


        // Constructors

        /**
            Constructs a PostorderChecklistCompletionVisitor.
        */
        public PostorderChecklistCompletionVisitor()
        {
            _visitor = new ChecklistCompletionVisitor();
        }


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#areUncheckedConstraints
        */
        public boolean areUncheckedConstraints()
        {
            return _visitor.areUncheckedConstraints();
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#uncheckedConstraintsInformation
        */
        public List uncheckedConstraintsInformation()
        {
            return _visitor.uncheckedConstraintsInformation();
        }


        // Protected methods

        /**
            @see </xsl:text>
        <xsl:value-of select="concat($language-name, 'PostorderConstructVisitor')"/>
        <xsl:text>#visitor
        */
        protected </xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor()
        {
            return _visitor;
        }
    }

    /**
        The class of </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text> that checks
        each construct's checklist for unchecked validity constraints.
    */
    protected static class ChecklistCompletionVisitor
        extends </xsl:text>
        <xsl:value-of select="$abstract-construct-visitor-class-name"/>
        <xsl:text>
        implements </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>
    {
        // Private fields

        /** The list of UncheckedValidityConstraints. */
        private List _uncheckedConstraints = new ArrayList();


        // Public methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#areUncheckedConstraints
        */
        public boolean areUncheckedConstraints()
        {
            return _uncheckedConstraints.isEmpty() == false;
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="$visitor-class-name"/>
        <xsl:text>#uncheckedConstraintsInformation
        */
        public List uncheckedConstraintsInformation()
        {
            return Collections.unmodifiableList(_uncheckedConstraints);
        }


        // Protected methods

        /**
            @see </xsl:text>
        <xsl:value-of select="$abstract-construct-visitor-class-name"/>
        <xsl:text>#defaultVisit(</xsl:text>
        <xsl:value-of select="$base-construct-interface"/>
        <xsl:text>, ErrorHandler)
            @see AbstractValidityConstraintChecklistFactory#addUncheckedConstraints(Construct, List)
        */
        protected void defaultVisit(</xsl:text>
        <xsl:value-of select="$base-construct-interface"/>
        <xsl:text> c, ErrorHandler handler)
        {
            addUncheckedConstraints(c, _uncheckedConstraints);
        }
    }
}</xsl:text>
    </xsl:template>


    <!-- Checklist factory and related method templates -->

    <xsl:template match="*" mode="checklist-factory-methods">
        <xsl:variable name="construct-type" select="@type"/>
        <xsl:variable name="prefix">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$construct-type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        @return a new validity constraint checklist for
        </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> constructs
    */
    public ValidityConstraintChecklist
        create</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Checklist()
    {
        ValidityConstraintChecklist result = createCompletedChecklist();

        if (result == null)
        {
            String desc = manager().
                idToDescription(manager().</xsl:text>
        <xsl:value-of select="$prefix"/>
        <xsl:text>);

            DefaultValidityConstraintChecklist checklist =
                new DefaultValidityConstraintChecklist(desc);
            add</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Constraints(checklist);

            result = checklist;
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return a new validity constraint checklist for a clone of an
        original </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> construct
    */
    public ValidityConstraintChecklist
        createCloned</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Checklist()
    {
        ValidityConstraintChecklist result = createCompletedChecklist();

        if (result == null)
        {
            String desc = manager().
                idToDescription(manager().</xsl:text>
        <xsl:value-of select="$prefix"/>
        <xsl:text>);

            DefaultValidityConstraintChecklist checklist =
                new DefaultValidityConstraintChecklist(desc);
            addCloned</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Constraints(checklist);

            result = checklist;
        }

        Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="add-constraint-methods">
        <xsl:variable name="construct-type" select="@type"/>
        <xsl:variable name="prefix">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$construct-type"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        Adds the constraints for </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> constructs
        to the specified checklist.
    */
    protected void
        add</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Constraints(DefaultValidityConstraintChecklist checklist)
    {
        Assert.require(checklist != null);

</xsl:text>
        <!-- Add each of this construct's constraints. -->
        <xsl:for-each select="$top/validity-constraints/section/construct-constraints[@type = $construct-type]/constraint">
            <xsl:variable name="per-clone">
                <xsl:call-template name="is-per-clone-constraint"/>
            </xsl:variable>

            <xsl:if test="$per-clone = 'false'">
                <xsl:text>        checklist.addConstraint(</xsl:text>
                <xsl:value-of select="$prefix"/>
                <xsl:text>_</xsl:text>
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@name"/>
                </xsl:call-template>
                <xsl:text>);
</xsl:text>
            </xsl:if>
        </xsl:for-each>

        <!-- Add constraints from supertypes. -->
        <xsl:for-each
            select="$inheritable-constructs[choice/subconstruct/@type = $construct-type]">
            <xsl:text>
        add</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>Constraints(checklist);
</xsl:text>
        </xsl:for-each>
    <xsl:text>    }

    /**
        Adds the constraints for cloned </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> constructs
        to the specified checklist.
    */
    protected void
        addCloned</xsl:text>
        <xsl:value-of select="$construct-type"/>
        <xsl:text>Constraints(DefaultValidityConstraintChecklist checklist)
    {
        Assert.require(checklist != null);

</xsl:text>
        <!-- Add each of this construct's constraints. -->
        <xsl:for-each select="$top/validity-constraints/section/construct-constraints[@type = $construct-type]/constraint">
            <xsl:variable name="per-clone">
                <xsl:call-template name="is-per-clone-constraint"/>
            </xsl:variable>

            <xsl:if test="$per-clone = 'true'">
                <xsl:text>        checklist.addConstraint(</xsl:text>
                <xsl:value-of select="$prefix"/>
                <xsl:text>_</xsl:text>
                <xsl:call-template name="to-constant-name">
                    <xsl:with-param name="name" select="@name"/>
                </xsl:call-template>
                <xsl:text>);
</xsl:text>
            </xsl:if>
        </xsl:for-each>

        <!-- Add constraints from supertypes. -->
        <xsl:for-each
            select="$inheritable-constructs[choice/subconstruct/@type = $construct-type]">
            <xsl:text>
        addCloned</xsl:text>
            <xsl:value-of select="@type"/>
            <xsl:text>Constraints(checklist);
</xsl:text>
        </xsl:for-each>
    <xsl:text>    }
</xsl:text>
    </xsl:template>
</xsl:transform>
