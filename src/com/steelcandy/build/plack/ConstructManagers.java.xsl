<?xml version="1.0"?>
<!--
    $Id: ConstructManagers.java.xsl,v 1.34 2016/01/14 14:49:30 jgm Exp $

    Transforms a language description document into the Java base class
    for a construct manager class for that language's constructs.

    Author: James MacKay
    Last Updated: $Date: 2016/01/14 14:49:30 $

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
    <xsl:import href="language-common.java.xsl"/>


    <!-- Configuration -->


    <!-- Global variables -->

    <xsl:variable name="class-implementation"
        select="$implementation/construct-manager-base-class"/>

    <xsl:variable name="construct-flags"
        select="$top/construct-flags/section/flag-definition"/>
    <xsl:variable name="construct-flag-sets"
        select="$top/construct-flag-sets/section/flag-set-definition"/>
    <xsl:variable name="map-flags-to-tokens">
        <xsl:value-of select="true()"/>
    </xsl:variable>

    <!-- A list of the names of all of the language's flags, regardless
         of what flag sets they're in. -->
    <xsl:variable name="all-construct-flag-names-list">
       <xsl:call-template name="make-list">
            <xsl:with-param name="elements"
                select="$construct-flags/@name"/>
        </xsl:call-template>
    </xsl:variable>

    <xsl:variable name="number-of-construct-flags"
        select="count($construct-flags)"/>
    <xsl:variable name="construct-flag-sets"
        select="$top/construct-flag-sets/section/flag-set-definition"/>

    <xsl:variable name="number-of-constructs"
        select="count($constructs)"/>

    <xsl:variable name="base-construct-superinterface-name"
        select="$class-implementation/base-construct-superinterface/@name"/>
    <xsl:variable name="base-construct-superclass-name"
        select="$class-implementation/base-construct-superclass/@name"/>
    <xsl:variable name="single-token-construct-superclass-name"
        select="$class-implementation/single-token-construct-superclass/@name"/>

    <xsl:variable name="write-indent-levels" select="3"/>


    <!-- Utility templates -->

    <!-- Outputs the name of the type of a construct attribute that can
         contain a value of type 'type'. -->
    <xsl:template name="attribute-type-name">
        <xsl:param name="type" select="@type"/>

        <!-- Since Java lists can always contain null, and (except for those
             of primitive types) values can always be null, we just ignore
             'Nullable' prefixes on types (and assume that the 'Nullable'
             prefix isn't ever applied to single-value primitive types - at
             least for now). -->
        <xsl:variable name="np" select="'Nullable'"/>
        <xsl:variable name="t">
            <xsl:choose>
                <xsl:when test="starts-with($type, $np)">
                    <xsl:value-of select="substring-after($type, $np)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$type"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:value-of select="concat($t, 'Attribute')"/>
    </xsl:template>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:text/>

        <!-- Generate the construct managers README file. -->
        <xsl:call-template name="readme-file"/>

        <!-- Generate the construct manager base class. -->
        <xsl:call-template name="construct-manager-base-class"/>

        <!-- Generate skeleton for the default construct manager class. -->
        <xsl:call-template name="default-construct-manager-class-skeleton"/>

        <!-- Generate the singleton construct manager class. -->
        <xsl:call-template name="construct-manager-class"/>
    </xsl:template>

    <xsl:template name="readme-file">
        <xsl:text>%%%% file README.construct-managers
</xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> Construct Managers README

Copyright (C) James MacKay

This README describes some of the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct manager files that
are generated, as well as any additional work that has to be done 'by hand'
after the files have first been generated.

In addition to the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct manager source files that are
generated, the following files are also generated:

    - ConstructManagers.all: contains all of the generated construct
      manager files, and is used to determine when construct manager
      source code needs to be regenerated (and so shouldn't normally be
      deleted)
    - README.construct-managers: this file
    - construct-managers.generated: lists the filenames of all of the
      generated construct manager files, one filename per line
    - construct-managers.discard: lists the filenames of all of the
      construct manager files that should no longer be generated, one
      filename per line

When </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct manager files are first generated the only change
that needs to be made 'by hand' is to create the default construct
manager class source file from the generated skeleton by copying the
skeleton. On Unix systems this would be done using the following command
(when the current directory is the </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> constructs directory):

    cp Default</xsl:text>
        <xsl:value-of select="concat($construct-manager-class-name, $src-ext)"/>
        <xsl:text>.skeleton Default</xsl:text>
        <xsl:value-of select="concat($construct-manager-class-name, $src-ext)"/>
        <xsl:text>

The default construct manager source file (the one above with the </xsl:text>
        <xsl:value-of select="$src-ext"/>
        <xsl:text>
extension) can be edited to add any desired extra code, though often it
can be left unchanged. In any case the default construct manager source
file should then be added and committed into CVS. The skeleton file (the
one above ending in '</xsl:text>
        <xsl:value-of select="$src-ext"/>
        <xsl:text>.skeleton') is then no longer needed, so its
name should be added to the discard file construct-managers.discard.</xsl:text>
    </xsl:template>


    <!-- Outputs the source code for the singleton construct manager
         class. -->
    <xsl:template name="construct-manager-class">
        <xsl:variable name="class-name"
            select="$construct-manager-class-name"/>
        <xsl:variable name="superclass-name"
            select="concat('Default', $class-name)"/>
        <xsl:variable name="ctor-name"
            select="$construct-manager-constructor-name"/>

        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:text>
/**
    The singleton </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct manager class.
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
         construct manager class. -->
    <xsl:template name="default-construct-manager-class-skeleton">
        <xsl:variable name="class"
            select="concat('Default', $construct-manager-class-name)"/>
        <xsl:variable name="superclass" select="$class-name"/>

        <xsl:call-template name="source-skeleton-prologue">
            <xsl:with-param name="class-name" select="$class"/>
        </xsl:call-template>
        <xsl:text>
/**
    The default construct manager class.
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

    <!-- Outputs the source code for the construct manager base class. -->
    <xsl:template name="construct-manager-base-class">
        <xsl:call-template name="source-prologue">
            <xsl:with-param name="class-name" select="$class-name"/>
        </xsl:call-template>
        <xsl:value-of select="$imports"/>
        <xsl:text>
import com.steelcandy.plack.common.source.SourceCode;
import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.constructs.*;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.containers.Containers;
import com.steelcandy.common.ints.*;

import java.io.IOException;
import java.util.*;

/**
    An abstract base class for classes that create and manage
    the types of constructs used to represent </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text>
    programming language constructs. It also contains construct
    class definitions.
    &lt;p&gt;
    Note: all construct inner classes should have default constructors
    as their only constructors. This restriction helps avoid problems
    with alias construct classes subclassing different types of
    construct classes.
    &lt;p&gt;
    Note: the various setXXX() methods on constructs that set a single
    subconstruct allow that subconstruct to be null. Allowing this
    avoids the need for parsers to check for null being returned by
    sub-parsers. The addXXX() methods that add one of a list of
    subconstructs and the setXXX() methods that set a list of subconstructs
    do &lt;em&gt;not&lt;/em&gt; allow this, however.
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
{
    // Construct flag constants

    /** The first flag value usable as a construct flag in this class. */
    private static final int FIRST_FLAG = 1 &lt;&lt; </xsl:text>
        <xsl:value-of select="$constant-superclass-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS;

    /** Construct flags. */
</xsl:text>
    <xsl:if test="$number-of-construct-flags &gt; 0">
        <xsl:text>    public static final int
</xsl:text>
    </xsl:if>
        <xsl:apply-templates select="$construct-flags" mode="flag-constants"/>
        <xsl:text>
    /**
        The number of construct flags, &lt;em&gt;not&lt;/em&gt; including the ones
        defined in any superclasses.
    */
    private static final int NUMBER_OF_FLAGS = </xsl:text>
        <xsl:value-of select="$number-of-construct-flags"/>
        <xsl:text>;

    /**
        The number of construct flags, including any defined in
        superclasses.
    */
    public static final int </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS =
        </xsl:text>
        <xsl:value-of select="$constant-superclass-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS + NUMBER_OF_FLAGS;

    /**
        The first construct flag available for use by subclasses.
    */
    public static final int </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_FIRST_AVAILABLE_FLAG =
        1 &lt;&lt; </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS;

    /**
        The largest flag defined by this class, and thus by the way
        that flags are defined, the largest flag defined by any of its
        superclasses as well.
    */
    private static final int LARGEST_FLAG =
        (</xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS &gt; 0) ? (1 &lt;&lt; (</xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_NUMBER_OF_USED_FLAGS - 1)) : 0;

</xsl:text>
        <xsl:if test="$map-flags-to-tokens">
            <xsl:text>    /** The token ID to construct flag map. */
    private static final IntMap
        _tokenIdMap = createTokenIdToConstructFlagMap();
</xsl:text>
        </xsl:if>
        <xsl:text>

    // Construct ID constants

    /** The first </xsl:text>
        <xsl:value-of select="$language-name"/>
        <xsl:text> construct ID. */
    private static final int FIRST_ID = </xsl:text>
        <xsl:value-of select="$constant-superclass-prefix"/>
        <xsl:text>_FIRST_AVAILABLE_ID;

    /** Construct IDs. */
</xsl:text>
    <xsl:if test="$number-of-constructs &gt; 0">
        <xsl:text>    public static final int
</xsl:text>
    </xsl:if>
        <xsl:apply-templates select="$constructs" mode="id-constants"/>
        <xsl:text>
    /**
        The number of construct IDs, &lt;em&gt;not&lt;/em&gt; including any defined
        in superclasses.
    */
    private static final int NUMBER_OF_IDS = </xsl:text>
        <xsl:value-of select="$number-of-constructs"/>
        <xsl:text>;

    /**
        The number of construct IDs, including any defined in superclasses.
    */
    public static final int </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_NUMBER_OF_IDS = FIRST_ID + NUMBER_OF_IDS;

    /**
        The first construct ID available for use by subclasses.
    */
    public static final int </xsl:text>
        <xsl:value-of select="$constant-class-prefix"/>
        <xsl:text>_FIRST_AVAILABLE_ID = FIRST_ID + NUMBER_OF_IDS;


    // Flag set membership predicate constants

</xsl:text>
        <xsl:if test="count($construct-flag-sets) &gt; 0">
            <xsl:text>    private static final UnaryIntPredicate
</xsl:text>
        </xsl:if>
        <xsl:apply-templates select="$construct-flag-sets"
            mode="flag-set-membership-predicate-constants"/>
        <xsl:text>

    // Singleton instance constants

    /**
        The instance of the validity constraint checklist factory.
    */
    private static final </xsl:text>
        <xsl:value-of select="$validity-constraint-checklist-factory-class-name"/>
        <xsl:text>
        CHECKLIST_FACTORY =
            </xsl:text>
        <xsl:value-of select="concat($validity-constraint-checklist-factory-class-name, '.', $validity-constraint-checklist-factory-constructor-name)"/>
        <xsl:text>();


    // Private fields

    /**
        Maps construct IDs to the description of the corresponding
        type of construct.
    */
    private String[] _idToDescriptionMap = createIdToDescriptionMap();

    /**
        Maps construct flags to their descriptions.
        (Keys are Integers, values are Strings.)
    */
    private Map _flagToDescriptionMap = createFlagToDescriptionMap();


    // Constructors

    /**
        Constructs </xsl:text>
        <xsl:value-of select="concat($language-name-article, ' ', $language-name)"/>
        <xsl:text> construct manager.
    */
    public </xsl:text>
        <xsl:value-of select="$class-name"/>
        <xsl:text>()
    {
        // empty
    }

</xsl:text>
        <xsl:if test="$map-flags-to-tokens">
            <xsl:text>
    // Token ID &lt;-&gt; construct flag methods

    /**
        Returns the construct flag corresponding to the token with the
        specified token ID.

        @param tokenId the token ID of the token whose corresponding
        construct flag is to be returned
        @return the construct flag corresponding to the token with ID
        'tokenId'
        @exception NoSuchItemException thrown if there is no construct
        flag corresponding to 'tokenId'
    */
    public static int tokenIdToConstructFlag(int tokenId)
        throws NoSuchItemException
    {
        return _tokenIdMap.get(tokenId);
    }
</xsl:text>
        </xsl:if>
        <xsl:text>

    // Construct flag/ID &lt;-&gt; description conversion methods

    /**
        @see EmptyConstructManagerBase#idToDescription(int)
    */
    public String idToDescription(int constructId)
    {
        String result;
        if (constructId &lt; FIRST_ID)
        {
            result = super.idToDescription(constructId);
        }
        else
        {
            result = _idToDescriptionMap[constructId - FIRST_ID];
        }

        Assert.ensure(result != null);
        return result;
    }

    /**
        @see EmptyConstructManagerBase#flagToDescription(int)
    */
    public String flagToDescription(int flag)
    {
        String result;
        if (flag &lt; FIRST_FLAG)
        {
            result = super.flagToDescription(flag);
        }
        else
        {
            result = (String) _flagToDescriptionMap.get(new Integer(flag));
        }

        Assert.ensure(result != null);
        return result;
    }


    // Flag set membership-checking methods
</xsl:text>
        <xsl:apply-templates select="$construct-flag-sets"
                mode="flag-set-membership-checking-methods"/>
        <xsl:text>

    // Construct flag/ID &lt;-&gt; description map creation methods

    /**
        Creates and returns a Map that maps construct flags to their
        descriptions.
        &lt;p&gt;
        Only the construct flags defined in this class are mapped:
        those defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return a map that maps construct flags to their descriptions
    */
    private Map createFlagToDescriptionMap()
    {
        // Note: since flags aren't sequential we don't use an array.
        Map result = Containers.createHashMap(NUMBER_OF_FLAGS);

</xsl:text>
        <xsl:apply-templates select="$construct-flags" mode="flag-to-description"/>
        <xsl:text>
        Assert.ensure(result != null);
        return result;
    }

    /**
        Creates and returns an array that maps construct IDs to the
        description of the corresponding type of construct.
        &lt;p&gt;
        Only the construct IDs defined in this class are mapped:
        those defined in superclasses are &lt;em&gt;not&lt;/em&gt; mapped.

        @return an array that maps construct IDs to the corresponding
        construct type's description
    */
    private String[] createIdToDescriptionMap()
    {
        String[] result = new String[NUMBER_OF_IDS];

</xsl:text>
        <xsl:apply-templates select="$constructs" mode="id-to-description"/>
        <xsl:text>
        Assert.ensure(result != null);
        return result;
    }


    // Flag-related creation methods
</xsl:text>
        <xsl:apply-templates select="$construct-flag-sets"
                     mode="flag-set-predicate-creation-methods"/>
        <xsl:text>
</xsl:text>
        <xsl:if test="$map-flags-to-tokens">
            <xsl:text>    /**
        Creates and returns a map from token IDs of tokens that indicate
        by their presence that a construct has a flag set to that
        construct flag.

        @return a map from token IDs to the corresponding construct flags
    */
    private static IntMap createTokenIdToConstructFlagMap()
    {
        IntMap result = new IntHashMap();

</xsl:text>
            <xsl:apply-templates select="$construct-flags"
                     mode="create-token-id-to-flag-map"/>
            <xsl:text>
        return result;
    }
</xsl:text>
        </xsl:if>
        <xsl:text>

    // Construct creation methods
</xsl:text>
        <xsl:apply-templates select="$constructs"
                     mode="construct-creation-methods"/>
        <xsl:text>

    // Construct inner classes
</xsl:text>
        <xsl:for-each select="$constructs">
            <xsl:choose>
                <xsl:when test="@custom = 'true'">
                    <xsl:call-template name="custom-construct-class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="." mode="construct-classes"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        <xsl:text>}</xsl:text>
    </xsl:template>


    <!-- Constant definition templates -->

    <xsl:template match="*" mode="flag-constants">
        <xsl:text>        </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text> = FIRST_FLAG &lt;&lt; </xsl:text>
        <xsl:value-of select="position() - 1"/>
        <xsl:choose>
            <xsl:when test="position() = last()">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>,
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*" mode="id-constants">
        <xsl:text>        </xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@type"/>
        </xsl:call-template>
        <xsl:text> = FIRST_ID + </xsl:text>
        <xsl:value-of select="position() - 1"/>
        <xsl:choose>
            <xsl:when test="position() = last()">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>,
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*" mode="flag-set-membership-predicate-constants">
        <xsl:text>        _is</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Predicate =
            createIs</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Predicate()</xsl:text>
        <xsl:choose>
            <xsl:when test="position() = last()">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>,
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- Method definition templates -->

    <xsl:template match="*" mode="flag-set-membership-checking-methods">
        <xsl:text>
    /**
        Indicates whether the token with the specified token ID
        represents a member of the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flag set.

        @param tokenId the token ID to test
        @return true if a token with ID 'tokenId' represents a member
        of the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flag set, and false if it doesn't
    */
    public boolean is</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(int tokenId)
    {
        return _is</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Predicate.isSatisfied(tokenId);
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="flag-to-description">
        <xsl:text>        result.put(new Integer(</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@name"/>
        </xsl:call-template>
        <xsl:text>), &quot;</xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text>&quot;);
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="id-to-description">
        <xsl:text>        result[</xsl:text>
        <xsl:call-template name="to-constant-name">
            <xsl:with-param name="name" select="@type"/>
        </xsl:call-template>
        <xsl:text> - FIRST_ID] = &quot;</xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text>&quot;;
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="flag-set-predicate-creation-methods">
        <xsl:text>
    /**
        @return a precicate that is satisfied only by token IDs of tokens
        that represent a member of the </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flag set
    */
    private static UnaryIntPredicate
        createIs</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>Predicate()
    {
        DefaultUnaryIntPredicate result =
            new DefaultUnaryIntPredicate(false);

</xsl:text>
        <xsl:apply-templates select="flag" mode="add-to-predicate"/>
        <xsl:text>
        // Assert.ensure(result != null);
        return result;
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="add-to-predicate">
        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>        result.setNonDefaultValue(</xsl:text>
        <xsl:value-of select="concat($token-manager-class-name, '.',
                                     $constant-name, ');')"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="create-token-id-to-flag-map">
        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>        result.set(</xsl:text>
        <xsl:value-of select="concat($token-manager-class-name, '.',
                                     $constant-name, ', ',
                                     $constant-name, ');')"/>
        <xsl:text>
</xsl:text>
    </xsl:template>


    <!-- Construct creation method templates -->

    <!-- There are no creation methods for choice constructs. -->
    <xsl:template match="choice-construct | line-choice-construct"
                  mode="construct-creation-methods"/>

    <!-- The creation methods for all other types of constructs have
         the same format. -->
    <xsl:template match="*" mode="construct-creation-methods">
        <xsl:text>
    /**
        @return a new </xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text> construct
    */
    public </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>()
    {
        return new </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(CHECKLIST_FACTORY.
            create</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>Checklist());
    }
</xsl:text>
    </xsl:template>


    <!-- Templates that output construct class constructors -->

    <!-- Outputs the common constructors for the construct class that
         represents the type of construct described by the current node. -->
    <xsl:template name="common-construct-constructors">
        <xsl:text>
        // Constructors
</xsl:text>
        <xsl:call-template name="from-checklist-construct-constructor"/>
        <xsl:call-template name="construct-copy-constructor"/>
    </xsl:template>

    <!-- Outputs the constructor that constructs - from a validity constraint
         checklist - an instance of the class that represents the type of
         construct described by the current node. -->
    <xsl:template name="from-checklist-construct-constructor">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            Constructor.

            @param checklist the construct's validity constraint checklist
        */
        public </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(ValidityConstraintChecklist checklist)
        {
            super(checklist);
        }
</xsl:text>
    </xsl:template>

    <!-- Outputs the copy constructor for the class that represents the
         type of construct described by the current node. -->
    <xsl:template name="construct-copy-constructor">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            Copy constructor.
            &lt;p&gt;
            Note: this constructor is usually only used in implementing this
            class' cloning methods.

            @param c the construct to copy
        */
        protected </xsl:text>
        <xsl:value-of select="concat($type, '(', $type, ' c)')"/>
        <xsl:text>
        {
            // Assert.require(c != null);
            super(c, CHECKLIST_FACTORY.
                createCloned</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>Checklist());
        }

        /**
            A copy constructor that copies everything except its validity
            constraint checklist from the specified construct.
            &lt;p&gt;
            Note: this constructor is usually only used in implementing
            subclasses' copy constructors.

            @param c the construct that the construct is to be a copy of
            @param checklist the construct's validity constraint checklist
        */
        protected </xsl:text>
        <xsl:value-of select="concat($type, '(', $type)"/>
        <xsl:text> c,
                                    ValidityConstraintChecklist checklist)
        {
            // Assert.require(c != null);
            // Assert.require(checklist != null);
            super(c, checklist);
        }
</xsl:text>
    </xsl:template>


    <!-- Construct classes templates -->

    <xsl:template match="choice-construct | line-choice-construct"
                  mode="construct-classes">
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause">
                <xsl:with-param name="inherit-type" select="'extends'"/>
                <xsl:with-param name="default-supertype"
                    select="$base-construct-superinterface-name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> choice construct interface.
    */
    public static interface </xsl:text>
        <xsl:value-of select="concat(@type, $supertypes)"/>
        <xsl:text>
    {
        // Public methods

</xsl:text>
        <xsl:apply-templates select="*" mode="choice-construct-part"/>
        <xsl:call-template name="output-construct-single-flag-methods">
            <xsl:with-param name="declarations-only" select="'true'"/>
        </xsl:call-template>
        <xsl:apply-templates select="." mode="cloning-methods"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <!-- Ignore choice constructs' choice child element. -->
    <xsl:template match="choice" mode="choice-construct-part"/>

    <xsl:template match="attribute" mode="choice-construct-part">
        <xsl:variable name="name">
            <xsl:call-template name="construct-attribute-name"/>
        </xsl:variable>
        <xsl:variable name="type-name">
            <xsl:call-template name="attribute-type-name"/>
        </xsl:variable>
        <xsl:variable name="method-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name" select="concat($name, 'Attribute')"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="desc">
            <xsl:call-template name="construct-attribute-description"/>
        </xsl:variable>

        <xsl:text>
        /**
            @return the attribute whose value is: </xsl:text>
        <xsl:value-of select="$desc"/>
        <xsl:text>
        */
        public </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $method-name, '();')"/>
        <xsl:text>
            // Assert.ensure(result != null);
</xsl:text>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="choice-construct-part">
        <xsl:call-template name="flag-from-set-fields-and-methods">
            <xsl:with-param name="declarations-only" select="'true'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="subconstruct" mode="choice-construct-part">
        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="method-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name" select="$name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not(@number) or @number='one' or
                            @number='zero-or-one'">
                <xsl:call-template
                    name="single-subconstruct-choice-construct-part">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="number" select="@number"/>
                    <xsl:with-param name="type-name" select="@type"/>
                    <xsl:with-param name="method-name" select="$method-name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template
                    name="multiple-subconstruct-choice-construct-part">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="number" select="@number"/>
                    <xsl:with-param name="type-name" select="@type"/>
                    <xsl:with-param name="method-name" select="$method-name"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs a choice construct's methods for the subconstruct with
         the specified name, type name and (potential) number of instances.
         The choice construct can have at most one instance of the
         subconstruct. -->
    <xsl:template name="single-subconstruct-choice-construct-part">
        <xsl:param name="name"/>
        <xsl:param name="number"/>
        <xsl:param name="type-name"/>
        <xsl:param name="method-name"/>

        <xsl:text>
        /**
            Sets our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct to the one
            specified.

            @param c our new </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
        */
        public void set</xsl:text>
        <xsl:value-of select="concat($name, '(', $type-name, ' c)')"/>
        <xsl:text>;

        /**
            Indicates whether our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
            is present.

            @return true iff our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
            is set
        */
        public boolean has</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>();

        /**
            @return our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
        */
        public </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $method-name)"/>
        <xsl:text>();
            // Assert.require(has</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>());
            // Assert.ensure(result != null);
</xsl:text>
    </xsl:template>

    <!-- Outputs a choice construct's methods for the subconstruct with
         the specified name, type name and (potential) number of instances.
         The construct can potentially have more than one instance of the
         subconstruct. -->
    <xsl:template name="multiple-subconstruct-choice-construct-part">
        <xsl:param name="name"/>
        <xsl:param name="number"/>
        <xsl:param name="type-name"/>
        <xsl:param name="method-name"/>

        <xsl:text>
        /**
            Sets our list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            to the specified list.

            @param list our new list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>
            subconstructs
        */
        public void set</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>List(ConstructList list);
            // Assert.require(list != null);

        /**
            Adds the specified </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct to
            the end of our list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            iff it is non-null: it is not added if it is null.

            @param c the </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> construct to add to our
            list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs iff it is
            non-null
        */
        public void add</xsl:text>
        <xsl:value-of select="concat($name, '(', $type-name, ' c)')"/>
        <xsl:text>;

        /**
            @return the number of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            we have
        */
        public int </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>Count();
            // Assert.ensure(result >= 0);

        /**
            @return a list of our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs:
            it will never be null
        */
        public ConstructList </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>List();
            // Assert.ensure(result != null);
</xsl:text>
    </xsl:template>


    <xsl:template match="alias-construct" mode="construct-classes">
        <xsl:variable name="id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause"/>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> alias construct class.
    */
    public static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        extends </xsl:text>
        <xsl:value-of select="concat(@aliased-construct, $supertypes)"/>
        <xsl:text>
    {</xsl:text>
        <xsl:call-template name="common-construct-constructors"/>
        <xsl:text>

        // Public methods

        /**
            @see Construct#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>;
        }

        /**
            @see </xsl:text>
        <xsl:value-of select="concat($class-name, '.', @aliased-construct)"/>
        <xsl:text>#accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text>, ErrorHandler)
        */
        public void accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor,
                           ErrorHandler handler)
        {
            Assert.require(visitor != null);
            Assert.require(handler != null);

            visitor.visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(this, handler);
        }
</xsl:text>
        <xsl:call-template name="construct-attribute-fields-and-methods"/>
        <xsl:apply-templates select="." mode="cloning-methods"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>


    <xsl:template name="custom-construct-class">
        <xsl:variable name="id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="superclass"
            select="concat($class-prefix, @type, 'Construct')"/>
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause"/>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> custom construct class.

        @see </xsl:text>
        <xsl:value-of select="$superclass"/>
        <xsl:text>
    */
    public static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        extends </xsl:text>
        <xsl:value-of select="concat($superclass, $supertypes)"/>
        <xsl:text>
    {</xsl:text>
        <xsl:call-template name="common-construct-constructors"/>
        <xsl:text>

        // Public methods

        /**
            @see Construct#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>;
        }

        /**
            Causes this construct to accept the specified visitor.

            @param visitor the visitor that this construct is to accept
            @param handler the error handler to use to handle any errors
            that occur while the visitor is visiting this construct
            @see Construct#accept(ConstructVisitor, ErrorHandler)
        */
        public void accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor,
                           ErrorHandler handler)
        {
            Assert.require(visitor != null);
            Assert.require(handler != null);

            visitor.visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(this, handler);
        }
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="construct | single-line-construct |
                         compound-construct | repeated-construct"
            mode="construct-classes">
        <xsl:variable name="id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause"/>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct class.
    */
    public static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        extends </xsl:text>
        <xsl:value-of select="concat($base-construct-superclass-name, $supertypes)"/>
        <xsl:text>
    {</xsl:text>
        <xsl:call-template name="common-construct-constructors"/>
        <xsl:text>

        // Public methods

        /**
            @see Construct#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>;
        }

        /**
            Causes this construct to accept the specified visitor.

            @param visitor the visitor that this construct is to accept
            @param handler the error handler to use to handle any errors
            that occur while the visitor is visiting this construct
            @see Construct#accept(ConstructVisitor, ErrorHandler)
        */
        public void accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor,
                           ErrorHandler handler)
        {
            Assert.require(visitor != null);
            Assert.require(handler != null);

            visitor.visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(this, handler);
        }

        /**
            @see Construct#write(ConstructWriter, String, SourceCode)
        */
        public void write(ConstructWriter w, String name, SourceCode source)
            throws IOException
        {
            Assert.require(w != null);
            // 'name' can be null
            // 'source' can be null

            w.writeConstructStart(this, name, source);

</xsl:text>
        <xsl:apply-templates select="*" mode="write-construct-part"/>
        <xsl:text>
            w.writeConstructEnd(this, name, source);
        }
</xsl:text>
        <xsl:call-template name="output-construct-part-fields-and-methods">
            <xsl:with-param name="node" select="."/>
        </xsl:call-template>
        <xsl:call-template name="construct-attribute-fields-and-methods"/>
        <xsl:apply-templates select="." mode="cloning-methods"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="single-token-construct" mode="construct-classes">
        <xsl:variable name="id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause"/>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> construct class.
    */
    public static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        extends </xsl:text>
        <xsl:value-of select="concat($single-token-construct-superclass-name, $supertypes)"/>
        <xsl:text>
    {</xsl:text>
        <xsl:call-template name="common-construct-constructors"/>
        <xsl:text>

        // Public methods

        /**
            @see Construct#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>;
        }

        /**
            Causes this construct to accept the specified visitor.

            @param visitor the visitor that this construct is to accept
            @param handler the error handler to use to handle any errors
            that occur while the visitor is visiting this construct
            @see Construct#accept(ConstructVisitor, ErrorHandler)
        */
        public void accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor,
                           ErrorHandler handler)
        {
            Assert.require(visitor != null);
            Assert.require(handler != null);

            visitor.visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(this, handler);
        }
</xsl:text>
        <xsl:call-template name="construct-attribute-fields-and-methods"/>
        <xsl:apply-templates select="." mode="cloning-methods"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>

    <xsl:template match="multiline-construct" mode="construct-classes">
        <xsl:variable name="id">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="supertypes">
            <xsl:call-template name="supertypes-clause"/>
        </xsl:variable>

        <xsl:text>
    /**
        The </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text> multiline construct class.
    */
    public static class </xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>
        extends </xsl:text>
        <xsl:value-of select="concat($base-construct-superclass-name, $supertypes)"/>
        <xsl:text>
    {</xsl:text>
        <xsl:call-template name="common-construct-constructors"/>
        <xsl:text>

        // Public methods

        /**
            @see Construct#id
        */
        public int id()
        {
            return </xsl:text>
        <xsl:value-of select="$id"/>
        <xsl:text>;
        }

        /**
            Causes this construct to accept the specified visitor.

            @param visitor the visitor that this construct is to accept
            @param handler the error handler to use to handle any errors
            that occur while the visitor is visiting this construct
            @see Construct#accept(ConstructVisitor, ErrorHandler)
        */
        public void accept(</xsl:text>
        <xsl:value-of select="$construct-visitor-class-name"/>
        <xsl:text> visitor,
                           ErrorHandler handler)
        {
            Assert.require(visitor != null);
            Assert.require(handler != null);

            visitor.visit</xsl:text>
        <xsl:value-of select="@type"/>
        <xsl:text>(this, handler);
        }

        /**
            @see Construct#write(ConstructWriter, String, SourceCode)
        */
        public void write(ConstructWriter w, String name, SourceCode source)
            throws IOException
        {
            Assert.require(w != null);
            // 'name' can be null
            // 'source' can be null

            w.writeConstructStart(this, name, source);

</xsl:text>
        <xsl:apply-templates select="first-line/*"
                mode="write-construct-part"/>
        <xsl:apply-templates select="indented-subconstructs/*"
                mode="write-construct-part"/>
        <xsl:text>
            w.writeConstructEnd(this, name, source);
        }
</xsl:text>
        <xsl:call-template name="output-construct-part-fields-and-methods">
            <xsl:with-param name="node" select="first-line"/>
        </xsl:call-template>
        <xsl:call-template name="output-construct-part-fields-and-methods">
            <xsl:with-param name="node" select="indented-subconstructs"/>
        </xsl:call-template>
        <xsl:call-template name="construct-attribute-fields-and-methods"/>
        <xsl:apply-templates select="." mode="cloning-methods"/>
        <xsl:text>
    }
</xsl:text>
    </xsl:template>


    <!-- Templates that generate a construct's cloning-related methods. -->

    <xsl:template match="choice-construct | line-choice-construct"
        mode="cloning-methods">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            @return a deep clone of this </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> construct
            @see Construct#deepClone
        */
        public </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> deepClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>();
            // Assert.ensure(result != null);

        /**
            @return a shallow clone of this </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> construct
            @see Construct#shallowClone
        */
        public </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> shallowClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>();
            // Assert.ensure(result != null);
</xsl:text>
    </xsl:template>

    <xsl:template match="*" mode="cloning-methods">
        <xsl:text>

        // Cloning methods
</xsl:text>
        <xsl:call-template name="deep-clone-method"/>
        <xsl:call-template name="shallow-clone-method"/>
        <xsl:call-template name="inherited-cloning-methods"/>
    </xsl:template>


    <!-- Outputs the definition of the type-specific deep clone method
         for the construct described by the current node. -->
    <xsl:template name="deep-clone-method">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            @return a deep clone of this </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> construct
            @see Construct#deepClone
        */
        public </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> deepClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>()
        {
            </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> result =
                new </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>(this);
</xsl:text>
        <xsl:apply-templates select="*" mode="deep-clone-method-part"/>
        <xsl:call-template name="deep-clone-inherited-attributes"/>
        <xsl:text>
            Assert.ensure(result != null);
            return result;
        }
</xsl:text>
    </xsl:template>

    <!-- Outputs the definition of the type-specific shallow clone method
         for the construct described by the current node. -->
    <xsl:template name="shallow-clone-method">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            @return a shallow clone of this </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> construct
            @see Construct#shallowClone
        */
        public </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> shallowClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>()
        {
            </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text> result =
                new </xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>(this);
</xsl:text>
        <xsl:apply-templates select="*" mode="shallow-clone-method-part"/>
        <xsl:call-template name="shallow-clone-inherited-attributes"/>
        <xsl:text>
            Assert.ensure(result != null);
            return result;
        }
</xsl:text>
    </xsl:template>

    <!-- Outputs the definitions of the cloning methods declared in the
         superinterfaces of the construct class representing the type of
         construct described by the current node, as well as the ones
         declared in the Construct interface.

         Note: this template doesn't properly handle the case where the
         construct class inherits the same superinterface more than once: it
         currently outputs such superinterfaces' clone methods once for each
         time the superinterface is inherited by the class. -->
    <xsl:template name="inherited-cloning-methods">
        <xsl:variable name="type" select="@type"/>

        <xsl:call-template name="inherited-cloning-methods-only"/>
        <xsl:text>
</xsl:text>
        <xsl:call-template name="base-cloning-methods"/>
    </xsl:template>

    <!-- Outputs the definitions of the cloning methods declared in the
         superinterfaces of the construct class representing the type of
         of construct described by the current node.

         Note: this template doesn't properly handle the case where the
         construct class inherits the same superinterface more than once: it
         currently outputs such superinterfaces' clone methods once for each
         time the superinterface is inherited by the class. -->
    <xsl:template name="inherited-cloning-methods-only">
        <xsl:variable name="type" select="@type"/>

        <xsl:for-each select="$inheritable-constructs[choice/subconstruct/@type = $type]">
            <xsl:variable name="supertype" select="@type"/>

            <xsl:text>
        /**
            @see </xsl:text>
            <xsl:value-of select="concat($class-name, '.', $supertype)"/>
            <xsl:text>#deepClone</xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text>
        */
        public </xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text> deepClone</xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text>()
        {
            </xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text> result =
                deepClone</xsl:text>
            <xsl:value-of select="$type"/>
            <xsl:text>();

            Assert.ensure(result != null);
            return result;
        }

        /**
            @see </xsl:text>
            <xsl:value-of select="concat($class-name, '.', $supertype)"/>
            <xsl:text>#shallowClone</xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text>
        */
        public </xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text> shallowClone</xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text>()
        {
            </xsl:text>
            <xsl:value-of select="$supertype"/>
            <xsl:text> result =
                shallowClone</xsl:text>
            <xsl:value-of select="$type"/>
            <xsl:text>();

            Assert.ensure(result != null);
            return result;
        }
</xsl:text>

            <!-- Output the cloning methods declared in superinterfaces
                 of the current superinterface. -->
            <xsl:call-template name="inherited-cloning-methods-only"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the definitions of the cloning methods declared in the
         Construct interface for the construct described by the current
         node. -->
    <xsl:template name="base-cloning-methods">
        <xsl:variable name="type" select="@type"/>

        <xsl:text>
        /**
            @see Construct#deepClone
        */
        public Construct deepClone()
        {
            Construct result = deepClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>();

            Assert.ensure(result != null);
            return result;
        }

        /**
            @see Construct#shallowClone
        */
        public Construct shallowClone()
        {
            Construct result = shallowClone</xsl:text>
        <xsl:value-of select="$type"/>
        <xsl:text>();

            Assert.ensure(result != null);
            return result;
        }
</xsl:text>
    </xsl:template>


    <!-- Templates that generate parts of a construct's cloning methods -->

    <!-- Outputs the parts of the deep clone method for the construct
         described by the current node that deep clone the construct's
         inherited attributes. -->
    <xsl:template name="deep-clone-inherited-attributes">
        <xsl:variable name="construct-type" select="@type"/>

        <xsl:for-each select="$inheritable-constructs[choice/subconstruct/@type = $construct-type]">
            <xsl:apply-templates select="attribute" mode="deep-clone-method-part"/>
            <xsl:call-template name="deep-clone-inherited-attributes"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the parts of the shallow clone method for the construct
         described by the current node that shallow clone the construct's
         inherited attributes. -->
    <xsl:template name="shallow-clone-inherited-attributes">
        <xsl:variable name="construct-type" select="@type"/>

        <xsl:for-each select="$inheritable-constructs[choice/subconstruct/@type = $construct-type]">
            <xsl:apply-templates select="attribute" mode="shallow-clone-method-part"/>
            <xsl:call-template name="shallow-clone-inherited-attributes"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Ignore 'space' and terminal elements. -->
    <xsl:template match="space" mode="deep-clone-method-part"/>
    <xsl:template match="space" mode="shallow-clone-method-part"/>
    <xsl:template match="terminal" mode="deep-clone-method-part"/>
    <xsl:template match="terminal" mode="shallow-clone-method-part"/>
    <xsl:template match="terminal-choice" mode="deep-clone-method-part"/>
    <xsl:template match="terminal-choice" mode="shallow-clone-method-part"/>

    <!-- Clone the parts of multiline constructs. -->
    <xsl:template match="first-line" mode="deep-clone-method-part">
        <xsl:apply-templates select="*" mode="deep-clone-method-part"/>
    </xsl:template>
    <xsl:template match="first-line" mode="shallow-clone-method-part">
        <xsl:apply-templates select="*" mode="shallow-clone-method-part"/>
    </xsl:template>
    <xsl:template match="indented-subconstructs" mode="deep-clone-method-part">
        <xsl:apply-templates select="*" mode="deep-clone-method-part"/>
    </xsl:template>
    <xsl:template match="indented-subconstructs" mode="shallow-clone-method-part">
        <xsl:apply-templates select="*" mode="shallow-clone-method-part"/>
    </xsl:template>

    <!-- Report any new/unexpected construct parts that aren't being cloned. -->
    <xsl:template match="*" mode="deep-clone-method-part">
        <xsl:message terminate="yes">

Don't know how to deep clone the construct part represented by
an element named '<xsl:value-of select="local-name()"/>' in the language
description document.
        </xsl:message>
    </xsl:template>
    <xsl:template match="*" mode="shallow-clone-method-part">
        <xsl:message terminate="yes">

Don't know how to shallow clone the construct part represented by
an element named '<xsl:value-of select="local-name()"/>' in the language
description document.
        </xsl:message>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="deep-clone-method-part">
        <!-- Only output anything for the first flag-from-set. -->
        <xsl:if test="not(preceding-sibling::*[name()='flag-from-set'])">
            <xsl:text>
            result.setFlags(_flags);</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="shallow-clone-method-part">
        <xsl:apply-templates select="." mode="deep-clone-method-part"/>
    </xsl:template>

    <xsl:template match="subconstruct" mode="deep-clone-method-part">
        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="type" select="@type"/>
        <xsl:variable name="field-name">
            <xsl:call-template name="subconstruct-field-name">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="number" select="@number"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Note: we assume that the 'number' attribute's value is valid. -->
        <xsl:choose>
            <xsl:when test="@number='zero-or-more' or @number='one-or-more'">
                <xsl:text>
            if (</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text> != null)
            {
                result.set</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>List(deepCloneList(
                    </xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text>));
            }
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
            if (</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text> != null)
            {
                result.set</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>(</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text>.
                    deepClone</xsl:text>
                <xsl:value-of select="$type"/>
                <xsl:text>());
            }
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct" mode="shallow-clone-method-part">
        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="field-name">
            <xsl:call-template name="subconstruct-field-name">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="number" select="@number"/>
            </xsl:call-template>
        </xsl:variable>

        <!-- Note: we assume that the 'number' attribute's value is valid. -->
        <xsl:choose>
            <xsl:when test="@number='zero-or-more' or @number='one-or-more'">
                <xsl:text>
            if (</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text> != null)
            {
                ConstructList list =
                    ConstructList.createArrayList(</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text>.size());
                list.addAll(</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text>);
                result.set</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>List(list);
            }
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
            if (</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text> != null)
            {
                result.set</xsl:text>
                <xsl:value-of select="$name"/>
                <xsl:text>(</xsl:text>
                <xsl:value-of select="$field-name"/>
                <xsl:text>);
            }
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="attribute" mode="deep-clone-method-part">
        <xsl:variable name="name">
            <xsl:call-template name="construct-attribute-name"/>
        </xsl:variable>
        <xsl:variable name="accessor">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute()')"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
            if (</xsl:text>
        <xsl:value-of select="$accessor"/>
        <xsl:text>.isSet())
            {
                result.</xsl:text>
        <xsl:value-of select="$accessor"/>
        <xsl:text>.
                    setValue(</xsl:text>
        <xsl:value-of select="$accessor"/>
        <xsl:text>.value());
            }
</xsl:text>
    </xsl:template>

    <xsl:template match="attribute" mode="shallow-clone-method-part">
        <xsl:variable name="name">
            <xsl:call-template name="construct-attribute-name"/>
        </xsl:variable>
        <xsl:variable name="accessor">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute()')"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
            result.set</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>Attribute(</xsl:text>
        <xsl:value-of select="$accessor"/>
        <xsl:text>);
</xsl:text>
    </xsl:template>


    <!-- Templates that generate parts of a construct's write() method -->

    <!-- Ignore 'space' elements. -->
    <xsl:template match="space" mode="write-construct-part"/>

    <xsl:template match="flag-from-set" mode="write-construct-part">
        <xsl:param name="indent-levels" select="$write-indent-levels"/>

        <!-- Only output anything for the first flag-from-set. -->
        <xsl:if test="not(preceding-sibling::*[name()='flag-from-set'])">
            <xsl:call-template name="indent">
                <xsl:with-param name="levels" select="$indent-levels"/>
            </xsl:call-template>
            <xsl:text>w.writeFlags(_flags);
</xsl:text>
        </xsl:if>
    </xsl:template>

    <xsl:template match="subconstruct" mode="write-construct-part">
        <xsl:param name="indent-levels" select="$write-indent-levels"/>

        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="field-name">
            <xsl:call-template name="subconstruct-field-name">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="number" select="@number"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="call">
            <xsl:text>w.</xsl:text>
            <xsl:choose>
                <xsl:when test="not(@number) or @number='one'">
                    <xsl:text>writeSubconstruct</xsl:text>
                </xsl:when>
                <xsl:when test="@number='zero-or-one'">
                    <xsl:text>writeOptionalSubconstruct</xsl:text>
                </xsl:when>
                <xsl:when test="@number='zero-or-more'">
                    <xsl:text>writeOptionalSubconstructList</xsl:text>
                </xsl:when>
                <xsl:when test="@number='one-or-more'">
                    <xsl:text>writeSubconstructList</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:message terminate="yes">
Found a subconstruct named "<xsl:value-of select="$name"/>" whose 'number' attribute has the unknown value "<xsl:value-of select="@number"/>"
                    </xsl:message>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:text>(</xsl:text>
        </xsl:variable>

        <xsl:call-template name="indent">
            <xsl:with-param name="levels" select="$indent-levels"/>
        </xsl:call-template>
        <xsl:value-of select="concat($call, '&quot;')"/>
        <xsl:value-of select="$name"/>
        <xsl:text>&quot;,
</xsl:text>
        <xsl:call-template name="indent">
            <xsl:with-param name="levels" select="$indent-levels"/>
        </xsl:call-template>
        <xsl:call-template name="copies">
            <xsl:with-param name="str" select="' '"/>
            <xsl:with-param name="number" select="string-length($call)"/>
        </xsl:call-template>
        <xsl:value-of select="$field-name"/>
        <xsl:text>, source);
</xsl:text>
    </xsl:template>


    <!-- Templates that generate fields and methods for a construct's
         attributes. -->

    <!-- Outputs the fields and methods for storing and accessing all
         of the construct attributes of the construct described by the
         specified node. -->
    <xsl:template name="construct-attribute-fields-and-methods">
        <xsl:param name="node" select="."/>

        <xsl:text>

        // Attribute fields and methods</xsl:text>
        <xsl:for-each select="$node/attribute">
            <xsl:call-template name="one-attribute-fields-and-methods"/>
        </xsl:for-each>
        <xsl:call-template name="inherited-attribute-parts"/>
    </xsl:template>

    <!-- Outputs the fields and methods for storing and accessing the
         construct attribute described by the specified node (which is
         expected to be an 'attribute' element).

         @see one-inherited-attribute-fields-and-methods -->
    <xsl:template name="one-attribute-fields-and-methods">
        <xsl:param name="node" select="."/>

        <xsl:variable name="name">
            <xsl:call-template name="construct-attribute-name">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="type-name">
            <xsl:call-template name="attribute-type-name">
                <xsl:with-param name="type" select="$node/@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="getter-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute')"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setter-name"
            select="concat('set', $name, 'Attribute')"/>
        <xsl:variable name="field-name">
            <xsl:call-template name="to-field-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute')"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="desc">
            <xsl:call-template name="construct-attribute-description"/>
        </xsl:variable>

        <xsl:text>

        /**
            The attribute whose value is: </xsl:text>
        <xsl:value-of select="$desc"/>
        <xsl:text>.
        */
        private </xsl:text>
        <xsl:value-of select="$type-name"/>
        <xsl:text>
            </xsl:text>
        <xsl:value-of
            select="concat($field-name, ' = new ', $type-name, '();')"/>
        <xsl:text>

        /**
            Sets our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> attribute to be the specified attribute.

            @param a our new </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> attribute
        */
        private void </xsl:text>
        <xsl:value-of select="concat($setter-name, '(', $type-name, ' a)')"/>
        <xsl:text>
        {
            Assert.require(a != null);

            </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> = a;
        }

        /**
            @return the attribute whose value is: </xsl:text>
        <xsl:value-of select="$desc"/>
        <xsl:text>
        */
        public </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $getter-name, '()')"/>
        <xsl:text>
        {
            </xsl:text>
            <xsl:value-of select="concat('Assert.ensure(',
                                         $field-name, ' != null);')"/>
        <xsl:text>
            return </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>;
        }</xsl:text>
    </xsl:template>

    <!-- Outputs the fields and methods for storing and accessing each of the
         attributes inherited by the construct described by the specified
         node (which is expected to be a construct definition element). -->
    <xsl:template name="inherited-attribute-parts">
        <xsl:param name="construct" select="."/>

        <xsl:variable name="construct-type" select="$construct/@type"/>

        <xsl:for-each select="$inheritable-constructs[choice/subconstruct/@type = $construct-type]">
            <xsl:variable name="inherited-construct" select="."/>

            <!-- Output the parts for each of the inherited construct's
                 attributes. -->
            <xsl:for-each select="attribute">
                <xsl:call-template name="one-inherited-attribute-fields-and-methods">
                    <xsl:with-param name="construct" select="$inherited-construct"/>
                </xsl:call-template>
            </xsl:for-each>

            <!-- Output the parts for the attributes that this inherited
                 construct inherits. -->
            <xsl:call-template name="inherited-attribute-parts"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the fields and methods for storing and accessing the
         construct attribute described by the specified node (which is
         expected to be an 'attribute' element) that is inherited from
         the specified construct (which is expected to be a construct
         definition element).

         @see one-attribute-fields-and-methods -->
    <xsl:template name="one-inherited-attribute-fields-and-methods">
        <xsl:param name="construct"/>
        <xsl:param name="node" select="."/>

        <xsl:variable name="construct-class-name"
            select="concat($construct-manager-base-class-name, '.',
                           $construct/@type)"/>
        <xsl:variable name="see-start"
            select="concat('@see ', $construct-class-name, '#')"/>

        <xsl:variable name="name">
            <xsl:call-template name="construct-attribute-name">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="type-name">
            <xsl:call-template name="attribute-type-name">
                <xsl:with-param name="type" select="$node/@type"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="getter-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute')"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="setter-name"
            select="concat('set', $name, 'Attribute')"/>
        <xsl:variable name="field-name">
            <xsl:call-template name="to-field-name">
                <xsl:with-param name="name"
                    select="concat($name, 'Attribute')"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="desc">
            <xsl:call-template name="construct-attribute-description"/>
        </xsl:variable>

        <xsl:text>

        /**
            The attribute whose value is: </xsl:text>
        <xsl:value-of select="$desc"/>
        <xsl:text>.
        */
        private </xsl:text>
        <xsl:value-of select="$type-name"/>
        <xsl:text>
            </xsl:text>
        <xsl:value-of
            select="concat($field-name, ' = new ', $type-name, '();')"/>
        <xsl:text>

        /**
            </xsl:text>
        <xsl:value-of select="concat($see-start, $setter-name, '(', $type-name, ')')"/>
        <xsl:text>
        */
        private void </xsl:text>
        <xsl:value-of select="concat($setter-name, '(', $type-name, ' a)')"/>
        <xsl:text>
        {
            Assert.require(a != null);

            </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> = a;
        }

        /**
            </xsl:text>
            <xsl:value-of select="concat($see-start, $getter-name)"/>
        <xsl:text>
        */
        public </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $getter-name, '()')"/>
        <xsl:text>
        {
            </xsl:text>
            <xsl:value-of select="concat('Assert.ensure(',
                                         $field-name, ' != null);')"/>
        <xsl:text>
            return </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>;
        }</xsl:text>
    </xsl:template>


    <!-- Templates that generate a construct class' fields and methods -->

    <!-- Outputs the construct fields and methods corresponding to all of
         the specified node's subelements. -->
    <xsl:template name="output-construct-part-fields-and-methods">
        <xsl:param name="node"/>

        <xsl:apply-templates select="$node/*"
            mode="construct-part-fields-and-methods"/>

        <xsl:call-template name="output-construct-single-flag-methods">
            <xsl:with-param name="node" select="$node"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs all of the single-flag methods for the construct described
         by the specified node.

         Note: flag sets may have overlapping memberships, so we must be sure
         that we only output single-flag methods once for each flag,
         regardless of how many flag sets the flag is in. -->
    <xsl:template name="output-construct-single-flag-methods">
        <xsl:param name="node" select="."/>
        <xsl:param name="declarations-only" select="'false'"/>

        <!-- A list of the names of all of the flag sets in
             'flag-set-construct-parts'. -->
        <xsl:variable name="flag-set-names">
            <xsl:call-template name="make-list">
                <xsl:with-param name="elements"
                    select="$node/flag-from-set/@name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="output-single-flag-methods">
            <xsl:with-param name="flag-names"
                select="$all-construct-flag-names-list"/>
            <xsl:with-param name="flag-set-names"
                select="$flag-set-names"/>
            <xsl:with-param name="declarations-only"
                select="$declarations-only"/>
        </xsl:call-template>
    </xsl:template>

    <!-- Outputs single-flag methods for those flags whose names are in the
         specified flag name list and that are members of at least one of
         the flag sets whose names are in the specified flag set name
         list. -->
    <xsl:template name="output-single-flag-methods">
        <xsl:param name="flag-names"/>
        <xsl:param name="flag-set-names"/>
        <xsl:param name="declarations-only" select="'false'"/>

        <xsl:variable name="num-flags">
            <xsl:call-template name="list-size">
                <xsl:with-param name="list" select="$flag-names"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="num-flag-sets">
            <xsl:call-template name="list-size">
                <xsl:with-param name="list" select="$flag-set-names"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="$num-flags &gt; 0 and $num-flag-sets &gt; 0">
            <xsl:variable name="first-flag">
                <xsl:call-template name="list-head">
                    <xsl:with-param name="list" select="$flag-names"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="rest-flags">
                <xsl:call-template name="list-tail">
                    <xsl:with-param name="list" select="$flag-names"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="first-set">
                <xsl:call-template name="list-head">
                    <xsl:with-param name="list" select="$flag-set-names"/>
                </xsl:call-template>
            </xsl:variable>
            <xsl:variable name="rest-sets">
                <xsl:call-template name="list-tail">
                    <xsl:with-param name="list" select="$flag-set-names"/>
                </xsl:call-template>
            </xsl:variable>

            <!-- Output 'first-flag' iff it is in one of the sets named in
                 'flag-set-names'. -->
            <xsl:variable name="set"
                select="$construct-flag-sets[@name=$first-set]"/>
            <xsl:choose>
                <xsl:when test="count($set/flag[@name=$first-flag]) &gt; 0">
                    <!-- 'first-flag' is in 'first-set', so output the
                         methods corresponding to it: do NOT check for
                         'first-flag' in any other sets. -->
                    <xsl:call-template name="single-flag-methods">
                        <xsl:with-param name="flag-name" select="$first-flag"/>
                        <xsl:with-param name="declarations-only"
                            select="$declarations-only"/>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <!-- 'first-flag' is not in 'first-set', so look for
                         it (and it only) in 'rest-sets'. -->
                    <xsl:call-template name="output-single-flag-methods">
                        <xsl:with-param name="flag-names"
                            select="$first-flag"/>
                        <xsl:with-param name="flag-set-names"
                            select="$rest-sets"/>
                        <xsl:with-param name="declarations-only"
                            select="$declarations-only"/>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>

            <!-- Output the methods for the flags in 'rest-flags' that are in
                 at least one of the flag sets named in 'flag-set-names'. -->
            <xsl:call-template name="output-single-flag-methods">
                <xsl:with-param name="flag-names" select="$rest-flags"/>
                <xsl:with-param name="flag-set-names"
                    select="$flag-set-names"/>
                <xsl:with-param name="declarations-only"
                    select="$declarations-only"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <!-- Ignore 'space' and 'attribute' elements. -->
    <xsl:template match="space" mode="construct-part-fields-and-methods"/>
    <xsl:template match="attribute" mode="construct-part-fields-and-methods"/>

    <xsl:template match="flag-from-set"
                  mode="construct-part-fields-and-methods">
        <xsl:call-template name="flag-from-set-fields-and-methods"/>
    </xsl:template>

    <xsl:template name="flag-from-set-fields-and-methods">
        <xsl:param name="declarations-only" select="'false'"/>

        <!-- Only output the flags field before the first flag-from-set. -->
        <xsl:if test="not(preceding-sibling::*[name()='flag-from-set'])">
            <xsl:if test="$declarations-only = 'false'">
                <xsl:text>
        /** This construct's flags. */
        private int _flags = 0;

        /**
            Sets our flags to the specified flags.

            @param newFlags our new flags
        */
        private void setFlags(int newFlags)
        {
            _flags = newFlags;
        }
</xsl:text>
            </xsl:if>
        </xsl:if>

        <xsl:text>
        /**
            Sets the specified </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flag(s) on this construct.
            Non-</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> flags will &lt;em&gt;not&lt;/em&gt; be set.

            @param flags the (or'ed together) </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>
            flags to set on this construct
        */
        public void set</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>(int flags)</xsl:text>
        <xsl:choose>
            <xsl:when test="$declarations-only = 'true'">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
        {
</xsl:text>
        <xsl:call-template name="set-any-flags-code"/>
        <xsl:text>        }
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs code that sets any of the flags in the flag set with
         the specified name as part of a construct's set[set-name]()
         method. -->
    <xsl:template name="set-any-flags-code">
        <xsl:param name="set-name" select="@name"/>

        <xsl:for-each select="$top/construct-flag-sets/section/flag-set-definition[@name=$set-name]/flag">
            <xsl:text>            _flags |= (flags &amp; </xsl:text>
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="@name"/>
            </xsl:call-template>
            <xsl:text>);
</xsl:text>
        </xsl:for-each>
    </xsl:template>

    <!-- Outputs the methods to query and set the construct flag with the
         specified name on a construct. -->
    <xsl:template name="single-flag-methods">
        <xsl:param name="flag-name"/>
        <xsl:param name="declarations-only" select="'false'"/>

        <xsl:variable name="constant-name">
            <xsl:call-template name="to-constant-name">
                <xsl:with-param name="name" select="$flag-name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:text>
        /**
            Sets whether the construct's </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text> flag is set.

            @param setFlag true if this construct's </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text> flag is
            to be set, and false if it is to be unset/cleared
        */
        public void set</xsl:text>
        <xsl:value-of select="$flag-name"/>
        <xsl:text>(boolean setFlag)</xsl:text>
        <xsl:choose>
            <xsl:when test="$declarations-only = 'true'">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
        {
            if (setFlag)
            {
                _flags |= </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>;
            }
            else
            {
                _flags &amp;= ~</xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>;
            }
        }
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:text>
        /**
            @return true if this construct's </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text> flag is
            set, and false if it isn't
        */
        public boolean is</xsl:text>
        <xsl:value-of select="$flag-name"/>
        <xsl:text>()</xsl:text>
        <xsl:choose>
            <xsl:when test="$declarations-only = 'true'">
                <xsl:text>;
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>
        {
            return (_flags &amp; </xsl:text>
        <xsl:value-of select="$constant-name"/>
        <xsl:text>) != 0;
        }
</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="subconstruct"
                  mode="construct-part-fields-and-methods">
        <xsl:variable name="name">
            <xsl:call-template name="subconstruct-name"/>
        </xsl:variable>
        <xsl:variable name="field-name">
            <xsl:call-template name="subconstruct-field-name">
                <xsl:with-param name="name" select="$name"/>
                <xsl:with-param name="number" select="@number"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="method-name">
            <xsl:call-template name="to-method-name">
                <xsl:with-param name="name" select="$name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not(@number) or @number='one' or
                            @number='zero-or-one'">
                <xsl:call-template
                    name="single-subconstruct-fields-and-methods">
                    <xsl:with-param name="subconstruct" select="."/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="field-name" select="$field-name"/>
                    <xsl:with-param name="method-name" select="$method-name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template
                    name="multiple-subconstruct-fields-and-methods">
                    <xsl:with-param name="subconstruct" select="."/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="field-name" select="$field-name"/>
                    <xsl:with-param name="method-name" select="$method-name"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Outputs a construct's fields and methods for the subconstruct
         represented by the specified subconstruct element. The construct
         can have at most one instance of the subconstruct. -->
    <xsl:template name="single-subconstruct-fields-and-methods">
        <xsl:param name="subconstruct"/>
        <xsl:param name="name"/>
        <xsl:param name="field-name"/>
        <xsl:param name="method-name"/>

        <xsl:variable name="number" select="$subconstruct/@number"/>
        <xsl:variable name="type-name" select="$subconstruct/@type"/>

        <xsl:text>
        /**
            Our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct, or null if we
            don't have one (yet).
        */
        private </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $field-name)"/>
        <xsl:text> = null;

        /**
            Sets our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct to the one
            specified.

            @param c our new </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
        */
        public void set</xsl:text>
        <xsl:value-of select="concat($name, '(', $type-name, ' c)')"/>
        <xsl:text>
        {
            // 'c' is allowed to be null
            </xsl:text>
            <xsl:value-of select="$field-name"/>
            <xsl:text> = c;
        }

        /**
            Indicates whether our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
            is present.

            @return true iff our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
            is set
        */
        public boolean has</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>()
        {
            return (</xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> != null);
        }

        /**
            @return our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct
        */
        public </xsl:text>
        <xsl:value-of select="concat($type-name, ' ', $method-name)"/>
        <xsl:text>()
        {
            Assert.require(has</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>());

            Assert.ensure(</xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> != null);
            return </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>;
        }
</xsl:text>
    </xsl:template>

    <!-- Outputs a construct's fields and methods for the subconstruct
         represented by the specified subconstruct element. The construct
         can potentially have more than one instance of the subconstruct. -->
    <xsl:template name="multiple-subconstruct-fields-and-methods">
        <xsl:param name="subconstruct"/>
        <xsl:param name="name"/>
        <xsl:param name="field-name"/>
        <xsl:param name="method-name"/>

        <xsl:variable name="number" select="$subconstruct/@number"/>
        <xsl:variable name="type-name" select="$subconstruct/@type"/>

        <xsl:text>
        /**
            Our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs, or null if we
            don't have any (yet).
        */
        private ConstructList </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> = null;

        /**
            Sets our list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            to the specified list.

            @param list our new list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>
            subconstructs
        */
        public void set</xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text>List(ConstructList list)
        {
            Assert.require(list != null);

            </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> = list;
        }

        /**
            Adds the specified </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstruct to
            the end of our list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            iff it is non-null: it is not added if it is null.

            @param c the </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> construct to add to our
            list of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs iff it is
            non-null
        */
        public void add</xsl:text>
        <xsl:value-of select="concat($name, '(', $type-name, ' c)')"/>
        <xsl:text>
        {
            if (c != null)
            {
                if (</xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> == null)
                {
                    </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> =
                        ConstructList.createArrayList();
                }
                </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>.add(c);
            }
        }

        /**
            @return the number of </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs
            we have
        */
        public int </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>Count()
        {
            int result = 0;

            if (</xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text> != null)
            {
                result = </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>.size();
            }

            Assert.ensure(result >= 0);
            return result;
        }

        /**
            @return a list of our </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs:
            it will never be null
        */
        public ConstructList </xsl:text>
        <xsl:value-of select="$method-name"/>
        <xsl:text>List()
        {
            ConstructList result = </xsl:text>
        <xsl:value-of select="$field-name"/>
        <xsl:text>;
            if (result == null)
            {
                // Return an empty list if there are no
                // </xsl:text>
        <xsl:value-of select="$name"/>
        <xsl:text> subconstructs.
                result = ConstructList.createEmptyList();
            }

            Assert.ensure(result != null);
            return result;
        }
</xsl:text>
    </xsl:template>


    <!-- Miscellaneous templates -->

    <!-- Outputs the supertypes clause for the construct described by
         the default node.

         The supertypes of a construct are the interfaces representing
         the choice-constructs (including line-choice-constructs) that
         the construct is a choice in, and possibly the scope-defining
         construct interface. -->
    <xsl:template name="supertypes-clause">
        <xsl:param name="type" select="@type"/>
        <xsl:param name="inherit-type" select="'implements'"/>
            <!-- Either 'implements' or 'extends' -->
        <xsl:param name="default-supertype" select="''"/>

        <xsl:variable name="is-inherited"
            select="count($inheritable-constructs[choice/subconstruct/@type = $type]) &gt; 0"/>
        <xsl:variable name="is-default"
            select="string-length($default-supertype) &gt; 0"/>

        <!-- Output 'inherit-type' iff there are any supertypes. -->
        <xsl:if test="$is-inherited = 'true' or $is-default = 'true'">
            <xsl:text>
        </xsl:text>
            <xsl:value-of select="concat($inherit-type, ' ')"/>
        </xsl:if>

        <!-- Output the supertypes, or the default supertype (if there is
             one) if there are aren't any other (non-scope defining construct)
             supertypes. -->
        <xsl:choose>
            <xsl:when test="$is-inherited">
                <xsl:for-each
                    select="$inheritable-constructs[choice/subconstruct/@type = $type]">
                    <xsl:if test="position() != 1">
                        <xsl:text>, </xsl:text>
                    </xsl:if>
                    <xsl:value-of select="@type"/>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="string-length($default-supertype) &gt; 0">
                <xsl:value-of select="$default-supertype"/>
            </xsl:when>
            <!-- Otherwise there are no supertypes of any kind, default or
                 otherwise (other than possibly the scope-defining construct
                 supertype), so output nothing. -->
        </xsl:choose>
    </xsl:template>

    <!-- Outputs the name of the field containing the subconstruct
         of a construct that has the specified name and (potential)
         number of instances. -->
    <xsl:template name="subconstruct-field-name">
        <xsl:param name="name"/>
        <xsl:param name="number" select="'one'"/>

        <xsl:variable name="field-name">
            <xsl:call-template name="to-field-name">
                <xsl:with-param name="name" select="$name"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:choose>
            <xsl:when test="not($number) or $number='one' or
                            $number='zero-or-one'">
                <xsl:value-of select="$field-name"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat($field-name, 'List')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:transform>
