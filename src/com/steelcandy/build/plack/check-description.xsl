<?xml version="1.0"?>
<!--
    A stylesheet that checks that a language description document is valid.
    It assumes that any document that it is applied to has been succesfully
    validated against the 'description.rng' schema.

    Copyright (C) 2006-2015 by James MacKay.

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

    <xsl:import href="language-common.xsl"/>


    <!-- Global variables -->

    <!-- The names of all of the common attributes whose values must be
         non-empty.

         The names in this list are separated by spaces, and the list also
         starts with and ends with a space. -->
    <xsl:variable name="non-empty-common-attribute-names">
        <xsl:text> name type description title text id var start-chars token-name subconstruct of quote superclass constructor-name prefix superclass-prefix </xsl:text>
    </xsl:variable>

    <!-- The names of all of the common attributes whose values, if
         non-empty, must not contain any spaces.

         The names in this list are separated by spaces, and the list also
         starts with and ends with a space. -->
    <xsl:variable name="no-spaces-common-attribute-names">
        <xsl:text> name type id token-name var subconstruct of superclass constructor-name prefix superclass-prefix </xsl:text>
    </xsl:variable>


    <!-- Templates -->

    <xsl:template match="/">
        <xsl:variable name="preliminary-result">
            <xsl:apply-templates select="$top" mode="check"/>
        </xsl:variable>
        <xsl:variable name="result"
            select="normalize-space($preliminary-result)"/>

<!--
        <xsl:message terminate="no">
+++++ Checking results = '<xsl:value-of select="$result"/>'
        </xsl:message>
-->

        <!-- Note: if the 'terminate="yes"' attribute to the following
             'xsl:message' use doesn't cause the XSL processor to return
             an exit code indicating failure then the non-empty output from
             applying this stylesheet can still be used to detect that one
             or more checks failed. -->
        <xsl:if test="string-length($result) &gt; 0">
            <!-- At least one error was reported (by 'report-error'). -->
            <xsl:message terminate="yes">
Errors were found in the language description document
'<xsl:value-of select="$input-pathname"/>'.
            </xsl:message>
        </xsl:if>
    </xsl:template>


    <!-- Checking Templates -->

    <!-- Anything that doesn't get checked is an error. -->
    <xsl:template match="*" mode="check">
        <xsl:call-template name="report-error">
            <xsl:with-param name="msg">
INTERNAL ERROR: this part of the language description document was not
checked.
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="language" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <!-- Note: this is used by default for all of the 'section' elements
         in a language description document. -->
    <xsl:template match="section" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="notes" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="note" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="operator-information" mode="check">
        <!-- empty - nothing to check -->
    </xsl:template>

    <!-- Note: this checks uses of both validity constraint definitions
         and semantics definitions. -->
    <xsl:template match="def-use" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:choose>
            <xsl:when test="count(ancestor::validity-constraints) &gt; 0">
                <!-- Check that our 'name' attribute names a validity
                     constraint definition. -->
                <xsl:if test="count(key('validity-constraint-definition',
                                        @name)) = 0">
                    <xsl:call-template name="report-error">
                        <xsl:with-param name="msg">
'<xsl:value-of select="@name"/>' is not the name of a validity
constraint definition, so it cannot be used here.
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:when test="count(ancestor::semantics) &gt; 0">
                <!-- Check that our 'name' attribute names a semantics
                     definition. -->
                <xsl:if test="count(key('semantics-definition',
                                        @name)) = 0">
                    <xsl:call-template name="report-error">
                        <xsl:with-param name="msg">
'<xsl:value-of select="@name"/>' is not the name of a semantics
definition, so it cannot be used here.
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
a 'def-use' element that represents a use of a definition named
'<xsl:value-of select="@name"/>' is invalid because it is not in
either the 'validity-constraints' or the 'semantics' part of the
language description document.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="direct | indirect" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the value of the 'subconstruct' attribute is the
             type of a defined construct, construct flag set or character
             class. -->
        <xsl:if test="count(key('construct', @subconstruct)) = 0 and
                      count(key('construct-flag-set', @subconstruct)) = 0 and
                      count(key('character-class', @subconstruct)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no character class, construct or construct flag set
named '<xsl:value-of select="@subconstruct"/>', so it cannot be
used as the value of the 'subconstruct' attribute on this element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="constant-or-list-across" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="constant-and-list-across" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="nonterm" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that we name the type of a defined construct or
             character class. -->
        <xsl:if test="count(key('construct', @name)) = 0 and
                      count(key('character-class', @name)) = 0 and
                      count(key('construct-flag-set', @name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no character class, construct or construct flag set
named '<xsl:value-of select="@name"/>', so it cannot
be used as the name of a non-terminal.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="term" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that we name a defined token. -->
        <xsl:if test="count(key('token', @name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no token named '<xsl:value-of select="@name"/>', so it
cannot be used as the name of a terminal.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="list" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="item" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="table" mode="check">
        <!-- Check that the value of any 'border' attribute is a
             nonnegative integer. -->
        <xsl:if test="@border">
            <xsl:call-template name="check-nonnegative-integer-attribute">
                <xsl:with-param name="attribute-name" select="'border'"/>
                <xsl:with-param name="attribute-value" select="@border"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="tr" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="th | td" mode="check">
        <!-- Check that the value of the 'colspan' attribute, if there is
             one, is a positive integer. -->
        <xsl:if test="@colspan">
            <xsl:call-template name="check-positive-integer-attribute">
                <xsl:with-param name="attribute-name" select="'colspan'"/>
                <xsl:with-param name="attribute-value" select="@colspan"/>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that the value of the 'rowspan' attribute, if there is
             one, is a positive integer. -->
        <xsl:if test="@rowspan">
            <xsl:call-template name="check-positive-integer-attribute">
                <xsl:with-param name="attribute-name" select="'rowspan'"/>
                <xsl:with-param name="attribute-value" select="@rowspan"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="space" mode="check">
        <!-- empty - nothing to check -->
    </xsl:template>

    <xsl:template match="var" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="expr" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="em | code | strong | pre" mode="check">
        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="link" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="text()" mode="check">
        <!-- empty - nothing to check -->
    </xsl:template>


    <xsl:template match="constants" mode="check">
        <xsl:apply-templates mode="check-constants"/>
    </xsl:template>

    <!-- Note: this checks constant definitions, not uses. -->
    <xsl:template match="constant" mode="check-constants">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that there isn't a previous constant definition that
             defines a constant with the same name as ours. -->
        <xsl:if test="preceding-sibling::constant[./@name = current()/@name]">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
an earlier 'constant' element already defined '<xsl:value-of select="@name"/>'.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Note: this checks constant-list definitions, not uses. -->
    <xsl:template match="constant-list" mode="check-constants">
        <xsl:call-template name="check-common-attributes"/>

        <!-- TODO: we should really check that there are no duplicate
             constants in the list by recursing into 'constant-list'
             children and their 'constant-list' children, etc !!!

             Note: checking this in such a way that the location of a
             duplicate 'nested' in arbitrarily deep lists can be described
             is probably a lot more difficult that it would be to just
             detect the presence of such duplicates. -->

        <!-- TODO: check that a constant-list A is not a (direct or
             indirect) member of a constant-list B when B is also a (direct
             or indirect) member of A !!! -->

        <!-- Check that there isn't a previous constant-list definition that
             defines a constant-list with the same name as ours. -->
        <xsl:if test="preceding-sibling::constant-list[./@name = current()/@name]">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
an earlier 'constant-list' element already defined '<xsl:value-of select="@name"/>'.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that no two of our 'constant' children have the same
             name. -->
        <xsl:for-each select="constant">
            <xsl:if test="preceding-sibling::constant[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the 'constant-list' named '<xsl:value-of select="../@name"/>' already
has a 'constant' member named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- Check that no two of our 'constant-list' children have the
             same name. -->
        <xsl:for-each select="constant-list">
            <xsl:if test="preceding-sibling::constant-list[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the 'constant-list' named '<xsl:value-of select="../@name"/>'
already has a 'constant-list' member named
'<xsl:value-of select="@name"/>.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Checks constant uses (anywhere in the document). -->
    <xsl:template match="constant" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that we name a defined constant. -->
        <xsl:if test="count(key('constant', @name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of a constant named
'<xsl:value-of select="@name"/>', so it cannot be used here.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Checks constant-list uses (anywhere in the document). -->
    <xsl:template match="constant-list" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:if test="count(key('constant-list', @name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of a constant list named
'<xsl:value-of select="@name"/>', so it cannot be used here.
                    </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <xsl:template match="character-classes" mode="check">
        <!-- Check that no two character-classes have the same name. -->
        <xsl:for-each select="$character-classes">
            <xsl:variable name="matches"
                select="key('character-class', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'character-class' element already defines the character
class named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-character-class"/>
    </xsl:template>

    <xsl:template match="notes" mode="check-character-class">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="character-class" mode="check-character-class">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-character-class"/>
    </xsl:template>

    <xsl:template match="choice" mode="check-character-class">
        <xsl:apply-templates mode="check-character-class"/>
    </xsl:template>

    <xsl:template match="single-character-choice" mode="check-character-class">
        <!-- Check that the 'list' attribute's value is non-empty. -->
        <xsl:if test="string-length(@list) = 0">
            <xsl:call-template name="report-empty-attribute">
                <xsl:with-param name="attribute-name" select="'list'"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="char-from-class" mode="check-character-class">
        <xsl:variable name="name" select="@name"/>

        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that '@name' is the name of a defined character class. -->
        <xsl:if test="count($character-classes[@name = $name]) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is no definition of a character class named
'<xsl:value-of select="$name"/>', so it cannot be used here.
                    </xsl:with-param>
                </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="chars" mode="check-character-class">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>


    <xsl:template match="token-flags" mode="check">
        <xsl:variable name="end-name" select="@end-of-line-flag-name"/>

        <!-- Check that the value of the 'end-of-line-flag-name' attribute
             is the name of a token flag defined under this element. -->
        <xsl:if test="count(key('token-flag', $end-name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'end-of-line-flag-name' attribute's value is
'<xsl:value-of select="$end-name"/>', which isn't the name of a
token flag defined in this document.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that no two flags have the same name. -->
        <xsl:for-each select="$token-flags">
            <xsl:variable name="matches"
                select="key('token-flag', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'flag-definition' element already defines the token flag
named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-token-flag"/>
    </xsl:template>

    <xsl:template match="flag-definition" mode="check-token-flag">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>


    <xsl:template match="token-flag-sets" mode="check">
        <!-- Check that no two flag sets have the same name. -->
        <xsl:for-each select="$token-flag-sets">
            <xsl:variable name="matches"
                select="key('token-flag-set', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'flag-set-definition' element already defines the token flag
set named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- There must be exactly ONE of each of the following elements
             under this element (NOT one per section):
                - reserved-word-flag-set
                - operator-reserved-word-flag-set
                - operator-flag-set
        -->
        <xsl:variable name="num1"
            select="count(section/reserved-word-flag-set)"/>
        <xsl:if test="$num1 != 1">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there are <xsl:value-of select="$num1"/> 'reserved-word-flag-set' elements
under this element, but there must be exactly one.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:variable name="num2"
            select="count(section/operator-reserved-word-flag-set)"/>
        <xsl:if test="$num2 != 1">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there are <xsl:value-of select="$num2"/> 'operator-reserved-word-flag-set'
elements under this element, but there must be exactly one.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:variable name="num3"
            select="count(section/operator-flag-set)"/>
        <xsl:if test="$num3 != 1">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there are <xsl:value-of select="$num3"/> 'operator-flag-set' elements
under this element, but there must be exactly one.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check-token-flag-set"/>
    </xsl:template>

    <xsl:template match="flag-set-definition" mode="check-token-flag-set">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:for-each select="flag">
            <xsl:call-template name="check-common-attributes"/>

            <!-- Check that it names a defined token flag. -->
            <xsl:if test="count(key('token-flag', @name)) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is no definition of a token flag named
'<xsl:value-of select="@name"/>', so it cannot be used here.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <!-- Check that each 'flag' child has a different name. -->
            <xsl:if test="preceding-sibling::flag[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="@name"/>' token flag has already
been added to this token flag set.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="reserved-word-flag-set" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="operator-reserved-word-flag-set" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="operator-flag-set" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>


    <xsl:template match="operator-precedence-levels" mode="check">
        <!-- Check that no two precedence levels have the same name. -->
        <xsl:for-each select="$operator-precedence-levels">
            <xsl:variable name="matches"
                select="key('operator-precedence-level', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'precedence-level' element already defines the operator
precedence level named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- Check that no two precedence levels have the same value. -->
        <xsl:for-each select="$operator-precedence-levels">
            <xsl:variable name="matches"
                select="key('operator-precedence-level-value', @value)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'precedence-level' element already defines an operator
precedence level whose 'value' attribute is '<xsl:value-of select="@value"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="precedence-level" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:call-template name="check-nonnegative-integer-attribute">
            <xsl:with-param name="attribute-name" select="'value'"/>
            <xsl:with-param name="attribute-value" select="@value"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template match="tokens" mode="check">
        <!-- Check that the 'indent-name' attribute names a defined token. -->
        <xsl:if test="count(key('token', @indent-name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'indent-name' attribute's value is '<xsl:value-of select="@indent-name"/>',
which isn't the name of a token defined in this document.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that no two tokens have the same name. -->
        <xsl:for-each select="$tokens">
            <xsl:variable name="matches"
                select="key('token', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another element already defines a token named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="reserved-word" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:call-template name="check-tokens-flags"/>
    </xsl:template>


    <xsl:template match="reserved-word-operator" mode="check">
        <xsl:call-template name="check-operator-token-attributes"/>

        <xsl:call-template name="check-tokens-flags"/>
    </xsl:template>

    <xsl:template match="operator" mode="check">
        <xsl:call-template name="check-operator-token-attributes"/>

        <xsl:call-template name="check-tokens-flags"/>
    </xsl:template>

    <!-- Checks the attributes on the 'reserved-word-operator' or
         'operator' token defined by the current element. -->
    <xsl:template name="check-operator-token-attributes">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:if test="string-length(@method) = 0">
            <xsl:call-template name="report-empty-attribute">
                <xsl:with-param name="attribute-name" select="'method'"/>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that the 'precedence' attribute names a defined
             operator precedence level. -->
        <xsl:if test="count(key('operator-precedence-level', @precedence)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of an operator precedence named
'<xsl:value-of select="@precedence"/>', so it cannot be used here as the value
of an operator token's 'precedence' attribute.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="simple-token" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:call-template name="check-tokens-flags"/>
    </xsl:template>

    <!-- Checks the 'flag' child elements of the current element (if any),
         where the current element is assumed to define a token. -->
    <xsl:template name="check-tokens-flags">
        <xsl:for-each select="flag">
            <!-- Check that we refer to a defined token flag. -->
            <xsl:if test="count(key('token-flag', @name)) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is no definition of a token flag named
'<xsl:value-of select="@name"/>', so it cannot be used here.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <!-- Check that each 'flag' child has a different name. -->
            <xsl:if test="preceding-sibling::flag[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="@name"/>' token flag has already
been added to the token.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="custom-token" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-custom-token"/>
    </xsl:template>

    <xsl:template match="space" mode="check-custom-token">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="chars | char-from-class" mode="check-custom-token">
        <xsl:apply-templates select="." mode="check-character-class"/>
    </xsl:template>

    <xsl:template match="choice" mode="check-custom-token">
        <xsl:apply-templates mode="check-custom-token"/>
    </xsl:template>


    <xsl:template match="token-creators" mode="check">
        <xsl:call-template name="check-nonnegative-integer-attribute">
            <xsl:with-param name="attribute-name" select="'map-size'"/>
            <xsl:with-param name="attribute-value" select="@map-size"/>
        </xsl:call-template>

        <!-- Check that there is exactly one 'default-token-creator'
             element under the current element (and NOT one per section). -->
        <xsl:variable name="num"
            select="count(section/default-token-creator)"/>
        <xsl:if test="$num != 1">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there are <xsl:value-of select="$num"/> 'default-token-creator' elements
under this element, but there must be exactly one.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that no two token creators have the same name. -->
        <xsl:for-each select="$token-creators">
            <xsl:variable name="matches"
                select="key('token-creator', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another element already defines a token creator
named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- TODO: check that no two token creators' 'start-chars' attribute
             values contain the same character!!! (This test is complicated
             by the presence of escaped characters (e.g. '\n') and possibly
             XML entities (e.g. '&lt;') -->

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="default-token-creator" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- TODO: is this supposed to name a token creator defined by one
             of our sibling elements???!!!??? Because right now it doesn't
             appear to be in any of the language description documents. -->
    </xsl:template>

    <xsl:template match="custom-token-creator" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="single-token-creator" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:call-template name="check-defined-token-name"/>
    </xsl:template>

    <xsl:template match="multiple-token-creator" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:call-template name="check-defined-token-name"/>

        <xsl:for-each select="next-char">
            <xsl:variable name="len" select="string-length(@value)"/>

            <!-- Check that the value of '@value' is a (possibly escaped)
                 single character. -->
            <xsl:if test="$len = 0 or $len > 2 or ($len = 2 and
                substring(@value, 1, 1) != '\')">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the value of the 'value' attribute is '<xsl:value-of select="@value"/>', which
is not a - possibly escaped (with a leading backslash (\)) - single
character.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <xsl:call-template name="check-defined-token-name"/>
        </xsl:for-each>
    </xsl:template>

    <!-- Checks that the 'token-name' attribute of the current element has
         the name of a defined token as its value. -->
    <xsl:template name="check-defined-token-name">
        <xsl:if test="count(key('token', @token-name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of a token named '<xsl:value-of select="@token-name"/>',
so it cannot be used here.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <xsl:template match="construct-flags" mode="check">
        <!-- Check that no two flags have the same name. -->
        <xsl:for-each select="$construct-flags">
            <xsl:variable name="matches"
                select="key('construct-flag', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'flag-definition' element already defines the construct flag
named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-construct-flag"/>
    </xsl:template>

    <xsl:template match="flag-definition" mode="check-construct-flag">
        <xsl:call-template name="check-common-attributes"/>

        <!-- The name of a construct flag must be the same as the name of a
             defined token (NOT token flag). -->
        <xsl:if test="count(key('token', @name)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the name of the construct flag named '<xsl:value-of select="@name"/>' is not
the same as the name of a defined token, and so is invalid.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="construct-flag-sets" mode="check">
        <!-- Check that no two flag sets have the same name. -->
        <xsl:for-each select="$construct-flag-sets">
            <xsl:variable name="matches"
                select="key('construct-flag-set', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'flag-set-definition' element already defines the construct flag
set named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-construct-flag-set"/>
    </xsl:template>

    <xsl:template match="flag-set-definition" mode="check-construct-flag-set">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:for-each select="flag">
            <xsl:call-template name="check-common-attributes"/>

            <!-- Check that it names a defined construct flag. -->
            <xsl:if test="count(key('construct-flag', @name)) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is no definition of a construct flag named
'<xsl:value-of select="@name"/>', so it cannot be used here.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <!-- Check that each 'flag' child has a different name. -->
            <xsl:if test="preceding-sibling::flag[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="@name"/>' construct flag has already
been added to this construct flag set.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="constructs" mode="check">
        <!-- Check that no two constructs have the same 'type'. -->
        <xsl:for-each select="$constructs">
            <xsl:variable name="matches"
                select="key('construct', @type)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another element already defines a '<xsl:value-of select="@type"/>' construct.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="subconstruct" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the 'type' attribute is the type of a defined
             construct. -->
        <xsl:if test="count(key('construct', @type)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of a '<xsl:value-of select="@type"/>' construct,
so it cannot be used as a subconstruct here.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Checks construct attributes. -->
    <xsl:template match="attribute" mode="check">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="terminal" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the 'name' attribute names a defined token. -->
        <xsl:variable name="matches" select="key('token', @name)"/>
        <xsl:if test="count($matches) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no definition of a token named '<xsl:value-of select="@name"/>',
so it cannot be used as the name of a terminal here.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:apply-templates select="$matches"
            mode="check-terminal-not-future-reserved-word"/>
    </xsl:template>

    <xsl:template match="*" mode="check-terminal-not-future-reserved-word"/>
    <xsl:template match="reserved-word"
        mode="check-terminal-not-future-reserved-word">
        <xsl:if test="@future = 'true'">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
a reserved word whose 'future' attribute is 'true' cannot be used as a
terminal in a construct.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <xsl:template match="terminal-choice" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that each 'terminal' child has a different name. -->
        <xsl:for-each select="terminal">
            <xsl:if test="preceding-sibling::terminal[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="@name"/>' terminal has already
been added to this terminal choice.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="flag-from-set" mode="check">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the 'number' attribute, if it is present, doesn't
             allow values greater than 1. -->
        <xsl:if test="@number">
            <xsl:if test="@number = 'zero-or-more' or
                          @number = 'one-or-more'">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the 'number' attribute of a 'flag-from-set' part must specify a number
of items that is at most one, but the '<xsl:value-of select="@number"/>' value
used here allows more than one item.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <xsl:template name="common-construct-checks">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that each 'attribute' child has a different name. -->
        <xsl:for-each select="attribute">
            <xsl:if test="preceding-sibling::attribute[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="@name"/>' attribute has already been
added to this construct.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Checks that all of the 'subconstruct' child elements of the current
         element have distinct types. -->
    <xsl:template name="check-subconstructs-have-distinct-types">
        <xsl:param name="construct" select="."/>

        <xsl:for-each select="subconstruct">
            <xsl:if test="preceding-sibling::subconstruct[./@type = current()/@type]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="$construct/@type"/>' construct's subconstructs
must have distinct types, and it already has a subconstruct of
type '<xsl:value-of select="@type"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Checks that all of the 'subconstruct' child elements of the current
         element have distinct names.

         The name of a subconstruct is given by its 'name' attribute if it
         has one, and by its 'type' attribute otherwise. -->
    <xsl:template name="check-subconstructs-have-distinct-names">
        <xsl:param name="construct" select="."/>

        <xsl:for-each select="subconstruct">
            <xsl:variable name="name">
                <xsl:call-template name="subconstruct-name"/>
            </xsl:variable>

            <xsl:if test="preceding-sibling::subconstruct[@name = $name or (not(@name) and @type = $name)]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="$construct/@type"/>' construct's subconstructs
must have distinct names, and it already has a subconstruct
named '<xsl:value-of select="$name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <!-- Checks that all of the 'subconstruct' child elements of the current
         element represent line constructs. -->
    <xsl:template name="check-subconstructs-all-line-constructs">
        <xsl:param name="construct" select="."/>
        <xsl:param name="index" select="1"/>

        <xsl:variable name="num" select="count(subconstruct)"/>
        <xsl:if test="$index &lt;= $num">
            <xsl:variable name="subconstruct" select="subconstruct[$index]"/>
            <xsl:variable name="is-first-line">
                <xsl:call-template name="is-subconstruct-line-construct">
                    <xsl:with-param name="subconstruct" select="$subconstruct"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:if test="$is-first-line = 'false'">
                <xsl:variable name="name">
                    <xsl:call-template name="subconstruct-name">
                        <xsl:with-param name="node" select="$subconstruct"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="$name"/>' subconstruct of '<xsl:value-of
select="$construct/@type"/>'
is not a line construct, even though all of the subconstructs
of that construct have to be line constructs.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <xsl:call-template
                name="check-subconstructs-all-line-constructs">
                <xsl:with-param name="construct" select="$construct"/>
                <xsl:with-param name="index" select="$index + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

    <!-- Checks that all of the 'subconstruct' child elements of the current
         element represent non-line constructs. -->
    <xsl:template name="check-subconstructs-all-non-line-constructs">
        <xsl:param name="construct" select="."/>
        <xsl:param name="index" select="1"/>

        <xsl:variable name="num" select="count(subconstruct)"/>
        <xsl:if test="$index &lt;= $num">
            <xsl:variable name="subconstruct" select="subconstruct[$index]"/>
            <xsl:variable name="is-first-line">
                <xsl:call-template name="is-subconstruct-line-construct">
                    <xsl:with-param name="subconstruct" select="$subconstruct"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:if test="$is-first-line = 'true'">
                <xsl:variable name="name">
                    <xsl:call-template name="subconstruct-name">
                        <xsl:with-param name="node" select="$subconstruct"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the '<xsl:value-of select="$name"/>' subconstruct of '<xsl:value-of
select="$construct/@type"/>'
is a line construct, even though all of the subconstructs
of that construct have to be non-line constructs.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>

            <xsl:call-template
                name="check-subconstructs-all-non-line-constructs">
                <xsl:with-param name="construct" select="$construct"/>
                <xsl:with-param name="index" select="$index + 1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


<!-- TODO: test all of the checking done for constructs, validity
     constraints and semantics !!!! -->

    <xsl:template match="alias-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <!-- Check that the 'aliased-construct' attribute's value is the
             name of a defined construct. -->
        <xsl:choose>
            <xsl:when test="count(key('construct', @aliased-construct)) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is no definition of a '<xsl:value-of select="@aliased-construct"/>' construct,
so an alias construct cannot alias it.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <!-- Check that the construct to be aliased is allowed to
                     be aliased. -->
                <xsl:variable name="can-be-aliased">
                    <xsl:call-template name="construct-can-be-aliased">
                        <xsl:with-param name="construct-type"
                            select="@aliased-construct"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:if test="$can-be-aliased = 'false'">
                    <xsl:call-template name="report-error">
                        <xsl:with-param name="msg">
a '<xsl:value-of select="@aliased-construct"/>' construct cannot be aliased
(possibly because it is a choice construct).
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:call-template name="check-subconstructs-have-distinct-names"/>
        <xsl:call-template name="check-subconstructs-all-non-line-constructs"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="repeated-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:if test="@start-terminal">
            <xsl:call-template name="check-terminal-attribute-is-token-name">
                <xsl:with-param name="attribute-name"
                    select="'start-terminal'"/>
                <xsl:with-param name="attribute-value"
                    select="@start-terminal"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@end-terminal">
            <xsl:call-template name="check-terminal-attribute-is-token-name">
                <xsl:with-param name="attribute-name"
                    select="'end-terminal'"/>
                <xsl:with-param name="attribute-value"
                    select="@end-terminal"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@separator-terminal">
            <xsl:call-template name="check-terminal-attribute-is-token-name">
                <xsl:with-param name="attribute-name"
                    select="'separator-terminal'"/>
                <xsl:with-param name="attribute-value"
                    select="@separator-terminal"/>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that the 'subconstruct' child element has a 'number'
             attribute that allows it to appear multiple times. -->
        <xsl:for-each select="subconstruct">
            <xsl:if test="@number != 'zero-or-more' and
                          @number != 'one-or-more'">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
the 'number' attribute of the subconstruct of the
'<xsl:value-of select="../@type"/>' repeated construct must be
'zero-or-more' or 'one-or-more', in order to allow the subconstruct
to be repeated.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- There is only one subconstruct, so we don't have to check for
             distinct subconstruct names. -->

        <!-- Check that the 'start-terminal' and 'end-terminal' attributes
             are either both present or both absent. -->
        <xsl:if test="@start-terminal and not(@end-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'start-terminal' attribute is present (with value
'<xsl:value-of select="@start-terminal"/>') but the 'end-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@end-terminal and not(@start-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'end-terminal' attribute is present (with value
'<xsl:value-of select="@end-terminal"/>') but the 'start-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that the 'start-terminal' and 'space-after-start-terminal'
             attributes are either both present or both absent. -->
        <xsl:if test="@start-terminal and not(@space-after-start-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'start-terminal' attribute is present (with value
'<xsl:value-of select="@start-terminal"/>') but the 'space-after-start-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@space-after-start-terminal and not(@start-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'space-after-start-terminal' attribute is present (with value
'<xsl:value-of select="@space-after-start-terminal"/>') but the 'start-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that the 'end-terminal' and 'space-before-end-terminal'
             attributes are either both present or both absent. -->
        <xsl:if test="@end-terminal and not(@space-before-end-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'end-terminal' attribute is present (with value
'<xsl:value-of select="@end-terminal"/>') but the 'space-before-end-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="@space-before-end-terminal and not(@end-terminal)">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the 'space-before-end-terminal' attribute is present (with value
'<xsl:value-of select="@space-before-end-terminal"/>') but the 'end-terminal'
attribute is absent from the '<xsl:value-of select="@type"/>' repeated
construct. Either both attributes must be present or both must be absent.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that if the 'end-terminal' is absent then the subconstruct
             must appear at least once. -->
        <xsl:if test="not(@end-terminal)">
            <xsl:for-each select="subconstruct">
                <xsl:if test="@number != 'one-or-more'">
                    <xsl:call-template name="report-error">
                        <xsl:with-param name="msg">
the subconstruct's 'number' attribute must be 'one-or-more' since its
parent '<xsl:value-of select="../@type"/>' repeated construct does not
have an end terminal (that is, its 'end-terminal' attribute is absent).
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:for-each>
        </xsl:if>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <!-- Checks that the attribute with the specified name and value on the
         current element names a defined token. -->
    <xsl:template name="check-terminal-attribute-is-token-name">
        <xsl:param name="attribute-name"/>
        <xsl:param name="attribute-value"/>

        <xsl:if test="count(key('token', $attribute-value)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the value of the '<xsl:value-of select="$attribute-name"/>' attribute is
'<xsl:value-of select="$attribute-value"/>', which is not a valid terminal
name since there is no definition of a token with that name (i.e.
'<xsl:value-of select="$attribute-value"/>').
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>


    <xsl:template match="single-token-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <!-- Check that the 'terminal' or 'terminal-choice' child element
             doesn't have a 'number' attribute. -->
        <xsl:for-each select="terminal">
            <xsl:if test="@number">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
a 'terminal' child element of a 'repeated-construct' element is not
allowed to have a 'number' attribute, but the '<xsl:value-of select="../@type"/>'
repeated construct has a 'terminal' child element whose 'number'
attribute is '<xsl:value-of select="@number"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>
        <xsl:for-each select="terminal-choice">
            <xsl:if test="@number">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
a 'terminal-choice' child element of a 'repeated-construct' element
is not allowed to have a 'number' attribute, but the
'<xsl:value-of select="../@type"/>' repeated construct has a 'terminal'
child element whose 'number' attribute is '<xsl:value-of select="@number"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="choice-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:call-template name="check-subconstructs-have-distinct-names"/>
        <xsl:call-template name="check-subconstructs-all-non-line-constructs"/>

        <xsl:apply-templates mode="check-choice-construct"/>
    </xsl:template>

    <xsl:template match="*" mode="check-choice-construct">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="choice" mode="check-choice-construct">
        <xsl:call-template name="check-subconstructs-have-distinct-names">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>
        <xsl:call-template name="check-subconstructs-have-distinct-types">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:call-template name="check-subconstructs-all-non-line-constructs">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="line-choice-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:call-template name="check-subconstructs-have-distinct-names"/>

        <!-- Note: the 'subconstruct' elements that are direct child elements
             of a 'line-choice-construct' can specify either line or non-line
             constructs: it is just ones that are children of the 'choice'
             child element of a 'line-choice-construct' that must specify
             line constructs. -->

        <xsl:apply-templates mode="check-line-choice-construct"/>
    </xsl:template>

    <xsl:template match="*" mode="check-line-choice-construct">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="choice" mode="check-line-choice-construct">
        <xsl:call-template name="check-subconstructs-have-distinct-names">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>
        <xsl:call-template name="check-subconstructs-have-distinct-types">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:call-template name="check-subconstructs-all-line-constructs">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="single-line-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:call-template name="check-subconstructs-have-distinct-names"/>

        <xsl:call-template name="check-subconstructs-all-non-line-constructs"/>

        <xsl:if test="count(*) - count(space) - count(attribute) &lt;= 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there aren't any child elements that aren't 'space' or 'attribute'
elements: it must have at least one non-'space', non-'attribute'
child element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <xsl:call-template name="check-first-subconstruct-not-optional"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="multiline-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="first-line" mode="check">
        <xsl:call-template name="check-subconstructs-have-distinct-names">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:call-template name="check-subconstructs-all-non-line-constructs">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:if test="count(*) - count(space) &lt;= 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there aren't any child elements that aren't 'space' elements:
it must have at least one non-'space' child element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <xsl:call-template name="check-first-subconstruct-not-optional"/>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="indented-subconstructs" mode="check">
        <xsl:call-template name="check-subconstructs-have-distinct-names">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:call-template name="check-subconstructs-all-line-constructs">
            <xsl:with-param name="construct" select=".."/>
        </xsl:call-template>

        <xsl:apply-templates mode="check"/>
    </xsl:template>

    <xsl:template match="compound-construct" mode="check">
        <xsl:call-template name="common-construct-checks"/>

        <xsl:call-template name="check-subconstructs-have-distinct-names"/>

        <xsl:call-template name="check-subconstructs-all-line-constructs"/>

        <xsl:call-template name="check-first-subconstruct-not-optional"/>

<!--
TODO:
    - check that a given token can only be the first token of at most
      one subconstruct
        - the parsers (at least currently) require this
-->

        <xsl:apply-templates mode="check"/>
    </xsl:template>


    <!-- Checks that the first 'subconstruct' child element of the current
         element - if it has such a child element - is not optional (that
         is, doesn't have 'number' attribute with value 'zero-or-more' or
         'zero-or-one') if it is preceded only by child elements (terminals,
         flag-from-sets, etc.) that are optional. -->
    <xsl:template name="check-first-subconstruct-not-optional">
        <xsl:if test="count(subconstruct) &gt; 0 and
                      subconstruct[1]/@number">
            <xsl:variable name="number" select="subconstruct[1]/@number"/>

            <xsl:if test="$number = 'zero-or-one' or $number = 'zero-or-more'">
                <xsl:variable name="are-preceding-optional">
                    <xsl:call-template name="are-preceding-siblings-all-optional">
                        <xsl:with-param
                            name="subconstruct" select="subconstruct[1]"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:if test="$are-preceding-optional = 'true'">
                    <xsl:call-template name="report-error">
                        <xsl:with-param name="msg">
the first 'subconstruct' child element of this element
cannot be optional (since any and all preceding terminals
and flags are optional): its 'number' attribute's value
cannot be '<xsl:value-of select="$number"/>'.
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <!-- Outputs 'true' iff all of the relevant siblings preceding the
         specified 'subconstruct' element are optional. -->
    <xsl:template name="are-preceding-siblings-all-optional">
        <xsl:param name="subconstruct"/>

        <xsl:variable name="results">
            <xsl:for-each select="$subconstruct">
                <xsl:apply-templates select="preceding-sibling::*"
                    mode="is-optional-construct-part"/>
            </xsl:for-each>
        </xsl:variable>
<xsl:message terminate="no">++++ results = <xsl:value-of select="$results"/></xsl:message>
        <xsl:value-of select="not(contains($results, 'false'))"/>
    </xsl:template>

    <xsl:template match="terminal | flag-from-set"
            mode="is-optional-construct-part">
        <xsl:value-of select="@number = 'zero-or-one' or @number = 'zero-or-more'"/>
    </xsl:template>

    <!-- Other construct parts aren't significant. -->
    <xsl:template match="*" mode="is-optional-construct-part"/>


    <xsl:template match="optimized-forms" mode="check">
        <!-- empty - nothing to check -->
    </xsl:template>


    <xsl:template match="validity-constraints" mode="check">
        <xsl:variable name="context" select="."/>

        <!-- Iff the 'must-exist' attribute is 'true' then we must have
             at least one 'section' child element. -->
        <xsl:if test="@must-exist = 'true' and count(section) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
since its 'must-exist' attribute is 'true' the 'validity-constraints'
element must have at least one 'section' child element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that for each construct in the language there is
             exactly one 'construct-constraints' grandchild element of
             this element that applies to that construct. -->
        <xsl:for-each select="$constructs">
            <xsl:variable name="type" select="@type"/>
            <xsl:variable name="matches"
                select="$context/section/construct-constraints[@type = $type]"/>
            <xsl:variable name="num-matches" select="count($matches)"/>

            <xsl:if test="$num-matches = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="node" select="$context"/>
                    <xsl:with-param name="msg">
no validity constraints have been specified for the
'<xsl:value-of select="$type"/>' construct: there is no
'construct-constraints' element under this element whose 'type'
attribute is '<xsl:value-of select="$type"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$num-matches &gt; 1">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="node" select="$context"/>
                    <xsl:with-param name="msg">
too many sets of validity constraints have been specified for
the '<xsl:value-of select="$type"/>' construct: there are
<xsl:value-of select="$num-matches"/> 'construct-constraints' elements under this
element each of whose 'type' attribute is '<xsl:value-of select="$type"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- Check that no two 'definition' grandchild elements have the
             same name. -->
        <xsl:for-each select="$validity-constraint-definitions">
            <xsl:variable name="matches"
                select="key('validity-constraint-definition', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'definition' element already defines a validity constraint
definition named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

<!--
    TODO: check that every 'definition' grandchild is used at least once !!!
    - do we really care that much whether they're used?
-->

        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="*" mode="check-constraints">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="text()" mode="check-constraints"/>

    <xsl:template match="section" mode="check-constraints">
        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="construct-constraints" mode="check-constraints">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the value of the 'type' attribute is the type
             of a defined construct. -->
        <xsl:if test="count(key('construct', @type)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no '<xsl:value-of select="@type"/>' construct, so it cannot
be used as the value of the 'type' attribute on this element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- If the 'always-valid' attribute is 'true' then the 'var'
             and 'iff' attributes must be absent and there cannot be any
             'let' or 'constraint' child elements. -->
        <xsl:if test="@always-valid = 'true'">
            <xsl:if test="@iff or @var">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
since the 'always-valid' attribute is 'true' for the constraints on
the '<xsl:value-of select="@type"/>' construct both the 'iff'
and 'var' attributes must be absent.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="count(let) &gt; 0 or
                          count(constraint) &gt; 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
since the 'always-valid' attribute is 'true' for the constraints on
the '<xsl:value-of select="@type"/>' construct there cannot be
any 'let' or 'constraint' child elements of this element.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <!-- If the 'always-valid' attribute is absent or false then the
             'var' and 'iff' attributes must be present and there must
             be at least one 'constraint' child element. -->
        <xsl:if test="not(@always-valid) or @always-valid = 'false'">
            <xsl:if test="not(@iff) or not(@var)">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
since the '<xsl:value-of select="@type"/>' construct is not always
valid both the 'iff' and 'var' attributes must be present on this
element.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="count(constraint) = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
since the '<xsl:value-of select="@type"/>' construct is not always
valid there must be at least one 'constraint' child element of
this element (since it must have at least one constraint).
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>

        <!-- Check that no two 'let' children declare variables with the
             same name. -->
        <xsl:for-each select="let">
            <xsl:if test="preceding-sibling::let[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is already a 'let' element declaring a variable named
'<xsl:value-of select="@name"/>' for use in the validity
constraints on '<xsl:value-of select="../@type"/>' constructs.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- Check that no two 'constraint' children have the same name. -->
        <xsl:for-each select="constraint">
            <xsl:if test="preceding-sibling::
                            constraint[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is already a constraint named '<xsl:value-of select="@name"/>'
on '<xsl:value-of select="../@type"/>' constructs.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="let" mode="check-constraints">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the variable we define doesn't have the same
             name as the variable used to represent the construct that
             the constraints are applied to. -->
        <xsl:if test="@name = ../@var">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the variable named '<xsl:value-of select="@name"/>' declared by this
element has the same name as is specified by its parent
'construct-constraints' element's 'var' attribute, which
is invalid.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="constraint" mode="check-constraints">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="definition" mode="check-constraints">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>

    <xsl:template match="example" mode="check-constraints">
        <xsl:apply-templates mode="check-constraints"/>
    </xsl:template>


    <xsl:template match="semantics" mode="check">
        <xsl:variable name="context" select="."/>

        <!-- Iff the 'must-exist' attribute is 'true' then we must have
             at least one 'section' child element. -->
        <xsl:if test="@must-exist = 'true' and count(section) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
since its 'must-exist' attribute is 'true' the 'semantics' element
must have at least one 'section' child element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that for each construct in the language there is
             exactly one 'construct-semantics' grandchild element of
             this element that applies to that construct. -->
        <xsl:for-each select="$constructs">
            <xsl:variable name="type" select="@type"/>
            <xsl:variable name="matches"
                select="$context/section/construct-semantics[@type = $type]"/>
            <xsl:variable name="num-matches" select="count($matches)"/>

            <xsl:if test="$num-matches = 0">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="node" select="$context"/>
                    <xsl:with-param name="msg">
no semantics have been specified for the '<xsl:value-of select="$type"/>'
construct: there is no 'construct-semantics' element under this
element whose 'type' attribute is '<xsl:value-of select="$type"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
            <xsl:if test="$num-matches &gt; 1">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="node" select="$context"/>
                    <xsl:with-param name="msg">
too many sets of semantics have been specified for the
'<xsl:value-of select="$type"/>' construct: there are <xsl:value-of select="$num-matches"/>
'construct-semantics' elements under this element each of whose
'type' attribute is '<xsl:value-of select="$type"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <!-- Check that no two 'definition' grandchild elements have the
             same name. -->
        <xsl:for-each select="$semantics-definitions">
            <xsl:variable name="matches"
                select="key('semantics-definition', @name)"/>

            <xsl:if test="count($matches) &gt; 1 and
                            generate-id(.) != generate-id($matches[1])">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
another 'definition' element already defines a semantics
definition named '<xsl:value-of select="@name"/>'.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

<!--
    TODO: check that every 'definition' grandchild is used at least once !!!
    - do we really care that much whether they're used?
-->

        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="*" mode="check-semantics">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="text()" mode="check-semantics"/>

    <xsl:template match="section" mode="check-semantics">
        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="general-notes" mode="check-semantics">
        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="construct-semantics" mode="check-semantics">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the value of the 'type' attribute is the type
             of a defined construct. -->
        <xsl:if test="count(key('construct', @type)) = 0">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
there is no '<xsl:value-of select="@type"/>' construct, so it cannot
be used as the value of the 'type' attribute on this element.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <!-- Check that no two 'let' children declare variables with the
             same name. -->
        <xsl:for-each select="let">
            <xsl:if test="preceding-sibling::let[./@name = current()/@name]">
                <xsl:call-template name="report-error">
                    <xsl:with-param name="msg">
there is already a 'let' element declaring a variable named
'<xsl:value-of select="@name"/>' for use in the semantics of
'<xsl:value-of select="../@type"/>' constructs.
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:if>
        </xsl:for-each>

        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="let" mode="check-semantics">
        <xsl:call-template name="check-common-attributes"/>

        <!-- Check that the variable we define doesn't have the same
             name as the variable used to represent the construct whose
             semantics are being described. -->
        <xsl:if test="@name = ../@var">
            <xsl:call-template name="report-error">
                <xsl:with-param name="msg">
the variable named '<xsl:value-of select="@name"/>' declared by this
element has the same name as is specified by its parent
'construct-semantics' element's 'var' attribute, which
is invalid.
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="description" mode="check-semantics">
        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>

    <xsl:template match="definition" mode="check-semantics">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-semantics"/>
    </xsl:template>


    <xsl:template match="implementation" mode="check">
        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <xsl:template match="*" mode="check-implementation">
        <xsl:apply-templates select="." mode="check"/>
    </xsl:template>

    <xsl:template match="text()" mode="check-implementation"/>

    <xsl:template match="imports" mode="check-implementation">
        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <xsl:template match="import" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <!-- Check module elements. -->
    <xsl:template match="root-module | base-module | source-module |
                         tokens-module | tokenizers-module |
                         constructs-module | parsers-module |
                         constructs-testing-module | validation-module |
                         runtime-module" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="constants-class" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="character-classes-class" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <!-- Check constructor class. -->
    <xsl:template match="location-factory-class | token-manager-class |
                         construct-manager-class |
                         validity-constraint-checklist-factory-class"
        mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <!-- Check class. -->
    <xsl:template match="tokenizer-class | construct-test-data-creator-class |
                         tokenizer-base-class" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <!-- Check subclass. -->
    <xsl:template match="construct-visitor-class |
                         validity-constraint-checklist-factory-base-class |
                         construct-test-data-creator-base-class"
        mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <xsl:template match="token-manager-base-class" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <!-- Child elements of 'token-manager-base-class'. -->
    <xsl:template match="simple-token-superclass |
                         reserved-word-token-superclass |
                         reserved-word-operator-token-superclass |
                         operator-token-superclass"
        mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="construct-manager-base-class" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <!-- Child elements of 'construct-manager-base-class'. -->
    <xsl:template match="base-construct-superinterface |
                         base-construct-superclass |
                         single-token-construct-superclass"
        mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="abstract-construct-visitor-class"
        mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>

        <xsl:if test="string-length(@interface) = 0">
            <xsl:call-template name="report-empty-attribute">
                <xsl:with-param name="attribute-name" select="'interface'"/>
            </xsl:call-template>
        </xsl:if>
        <xsl:if test="string-length(@default-construct-type) = 0">
            <xsl:call-template name="report-empty-attribute">
                <xsl:with-param name="attribute-name"
                    select="'default-construct-type'"/>
            </xsl:call-template>
        </xsl:if>

        <xsl:apply-templates mode="check-implementation"/>
    </xsl:template>

    <xsl:template match="parser-classes" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="validity-constraints" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>

    <xsl:template match="runtime-classes" mode="check-implementation">
        <xsl:call-template name="check-common-attributes"/>
    </xsl:template>


    <!-- Common checking templates -->

    <!-- Checks that the attribute with the specified name and value on the
         current element has a positive integer as its value. -->
    <xsl:template name="check-positive-integer-attribute">
        <xsl:param name="attribute-name"/>
        <xsl:param name="attribute-value"/>

        <xsl:variable name="desc" select="'positive'"/>

        <xsl:choose>
            <xsl:when test="$attribute-value = 0">
                <xsl:call-template
                    name="report-incorrect-integer-attribute">
                    <xsl:with-param name="attribute-name"
                        select="$attribute-name"/>
                    <xsl:with-param name="attribute-value"
                        select="$attribute-value"/>
                    <xsl:with-param name="desc" select="$desc"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="check-nonnegative-integer-attribute">
                    <xsl:with-param name="attribute-name"
                        select="$attribute-name"/>
                    <xsl:with-param name="attribute-value"
                        select="$attribute-value"/>
                    <xsl:with-param name="desc" select="$desc"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Checks that the attribute with the specified name and value on the
         current element has a non-negative integer as its value. -->
    <xsl:template name="check-nonnegative-integer-attribute">
        <xsl:param name="attribute-name"/>
        <xsl:param name="attribute-value"/>
        <xsl:param name="desc" select="'nonnegative'"/>

        <xsl:choose>
            <xsl:when test="string-length($attribute-value) = 0">
                <xsl:call-template name="report-empty-attribute">
                    <xsl:with-param name="attribute-name"
                        select="$attribute-name"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="invalid-chars"
                    select="translate($attribute-value, '0123456789', '')"/>
                <xsl:if test="string-length($invalid-chars) &gt; 0">
                    <xsl:call-template
                        name="report-incorrect-integer-attribute">
                        <xsl:with-param name="attribute-name"
                            select="$attribute-name"/>
                        <xsl:with-param name="attribute-value"
                            select="$attribute-value"/>
                        <xsl:with-param name="desc" select="$desc"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- Reports that the attribute with the specified name and value on
         the current element is not of the correct type (for example, that
         it isn't a positive integer). -->
    <xsl:template name="report-incorrect-integer-attribute">
        <xsl:param name="attribute-name"/>
        <xsl:param name="attribute-value"/>
        <xsl:param name="desc"/>

        <xsl:call-template name="report-error">
            <xsl:with-param name="msg">
the value of the '<xsl:value-of select="$attribute-name"/>' attribute is '<xsl:value-of select="$attribute-value"/>', which
is not a <xsl:value-of select="$desc"/> integer.
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- Checks the attributes of the specified element, if any, that have
         the names of common attributes. -->
    <xsl:template name="check-common-attributes">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node/@*">
            <xsl:variable name="name" select="local-name()"/>
            <xsl:variable name="padded-name"
                select="concat(' ', $name, ' ')"/>

            <xsl:if test="contains($non-empty-common-attribute-names,
                                   $padded-name) and
                          string-length(.) = 0">
                <xsl:call-template name="report-empty-attribute">
                    <xsl:with-param name="node" select="$node"/>
                    <xsl:with-param name="attribute-name" select="$name"/>
                </xsl:call-template>
            </xsl:if>

            <xsl:if test="contains($no-spaces-common-attribute-names,
                                   $padded-name)">
                <xsl:variable name="removed"
                    select="translate(., ' ', '')"/>
                <xsl:if test="string-length(.) != string-length($removed)">
                    <xsl:call-template name="report-spaces-in-attribute">
                        <xsl:with-param name="node" select="$node"/>
                        <xsl:with-param name="attribute-name" select="$name"/>
                        <xsl:with-param name="attribute-value" select="."/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="report-empty-attribute">
        <xsl:param name="node" select="."/>
        <xsl:param name="attribute-name"/>

        <xsl:call-template name="report-error">
            <xsl:with-param name="node" select="$node"/>
            <xsl:with-param name="msg">
the value of the '<xsl:value-of select="$attribute-name"/>' attribute must not be empty.
                    </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="report-spaces-in-attribute">
        <xsl:param name="node" select="."/>
        <xsl:param name="attribute-name"/>
        <xsl:param name="attribute-value"/>

        <xsl:call-template name="report-error">
            <xsl:with-param name="node" select="$node"/>
            <xsl:with-param name="msg">
the value of the '<xsl:value-of select="$attribute-name"/>' attribute is not allowed
to contain spaces, but this attribute's value -
'<xsl:value-of select="$attribute-value"/>' - does.
                    </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- Reports an error found in the language description document that
         is described by 'msg' and occurs at node 'node'. -->
    <xsl:template name="report-error">
        <xsl:param name="msg"/>
        <xsl:param name="node" select="."/>

        <xsl:variable name="pathname">
            <xsl:call-template name="local-node-pathname">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="location">
            <xsl:call-template name="local-node-location">
                <xsl:with-param name="node" select="$node"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:message terminate="no">
            <xsl:text>
</xsl:text>
Error at: <xsl:value-of select="$location"/>
<xsl:value-of select="$msg"/>
        </xsl:message>

        <!-- Note: exactly what is output here isn't terribly important,
             though it should consist of at least one full line (including
             newline). Then non-empty output can be used to detect that at
             least one check failed. -->
        <xsl:text>Error occurred at </xsl:text>
        <xsl:value-of select="$location"/>
        <xsl:text>
</xsl:text>
    </xsl:template>

    <!-- Outputs the 'location' of the specified node, from the root
         element down. Each component of the location will be the local
         name of an ancestor element annotated with whatever name, type,
         ID, etc. can be extracted from each element. -->
    <xsl:template name="local-node-location">
        <xsl:param name="node" select="."/>

        <xsl:for-each select="$node">
            <xsl:if test="count(. | /) != 1">
                <!-- 'node' isn't the document root '/'. -->
                <xsl:call-template name="local-node-location">
                    <xsl:with-param name="node" select=".."/>
                </xsl:call-template>
                <xsl:value-of select="concat('/', name())"/>
                <xsl:if test="@id or @name or @type or @title">
                    <xsl:text>[</xsl:text>
                    <xsl:choose>
                        <xsl:when test="@id">
                            <xsl:value-of select="@id"/>
                        </xsl:when>
                        <xsl:when test="@name">
                            <xsl:value-of select="@name"/>
                        </xsl:when>
                        <xsl:when test="@type">
                            <xsl:value-of select="@type"/>
                        </xsl:when>
                        <xsl:when test="@title">
                            <xsl:value-of select="@title"/>
                        </xsl:when>
                    </xsl:choose>
                    <xsl:text>]</xsl:text>
                </xsl:if>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:transform>
