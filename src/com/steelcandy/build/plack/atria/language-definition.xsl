<?xml version="1.0"?>
<!--
    Transforms the Atria language description document into a document that
    defines and explains the Atria language.

TODO: FINISH THIS TO DOCUMENT Atria !!!

TODO: for now the generated document will be in HTML, but change that !!!
- modify this document to generate less HTML-specific output
- write XSL documents that take this file as input and output XSL files
  that, when applied to the Atria language description document, outputs
  the language definition document in specific formats
    - e.g. HTML, LaTeX, plain text, man (?), info (?)

    Copyright (C) 2005-2023 by James MacKay.

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

    <xsl:import href="../document-common.html.xsl"/>
    <xsl:import href="../grammar-rules.html.xsl"/>

    <xsl:output method="html"/>


    <!-- Configuration -->

    <xsl:variable name="our-id" select="'LanguageDefinition'"/>
    <xsl:variable name="our-title"
        select="concat($language-name, ' Language Definition')"/>


    <!-- Global variables -->

    <xsl:variable name="content-filename">
        <xsl:call-template name="id-to-content-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="index-filename">
        <xsl:call-template name="id-to-index-filename">
            <xsl:with-param name="id" select="$our-id"/>
        </xsl:call-template>
    </xsl:variable>


    <!-- Main templates -->

    <xsl:template match="language">
        <xsl:call-template name="language-definition-content"/>
        <xsl:call-template name="language-definition-index"/>
    </xsl:template>

    <xsl:template name="language-definition-content">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$content-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="menu">
                <xsl:call-template name="language-document-menu">
                    <xsl:with-param name="id" select="$our-id"/>
                </xsl:call-template>
            </xsl:with-param>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="language-definition-body"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="language-definition-index">
        <xsl:call-template name="html-prologue">
            <xsl:with-param name="filename" select="$index-filename"/>
        </xsl:call-template>
        <xsl:call-template name="html-index-document">
            <xsl:with-param name="title" select="$our-title"/>
            <xsl:with-param name="doc-content">
                <xsl:call-template name="index-links"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>


    <!-- Index templates -->

    <xsl:template name="index-links">
        <xsl:call-template name="major-section-links"/>
        <hr />

        <!-- Table of Contents. -->
        <p><a name="ToC">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#ToC')"/>
            <xsl:with-param name="text"
                select="'Table&nbsp;of&nbsp;Contents'"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>

        <!-- Introduction. -->
        <p><a name="introduction">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#introduction')"/>
            <xsl:with-param name="text" select="'Introduction'"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>

        <!-- Basic Parts. -->
        <p><a name="basic-parts">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#basic-parts')"/>
            <xsl:with-param name="text" select="'Basic&nbsp;Parts'"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#names')"/>
            <xsl:with-param name="text" select="'Names'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#texts')"/>
            <xsl:with-param name="text" select="'Texts'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#comments')"/>
            <xsl:with-param name="text" select="'Comments'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#blank-lines')"/>
            <xsl:with-param name="text" select="'Blank&nbsp;Lines'"/>
        </xsl:call-template>

        <!-- Documents Deconstructed. -->
        <p><a name="documents-deconstructed">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#documents-deconstructed')"/>
            <xsl:with-param name="text"
                select="'Documents Deconstructed'"/>
                <!-- This is a little long, so we intentionally allow it to
                      get split across two lines if it needs to be. -->
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#first-line')"/>
            <xsl:with-param name="text" select="'The&nbsp;First&nbsp;Line'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#top-level-commands')"/>
            <xsl:with-param name="text" select="'Top-Level&nbsp;Commands'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#elements-and-attributes')"/>
            <xsl:with-param name="text" select="'Elements&nbsp;and&nbsp;Attributes'"/>
        </xsl:call-template>
        <xsl:call-template name="make-minor-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#expression-commands')"/>
            <xsl:with-param name="text" select="'Expression&nbsp;Commands'"/>
        </xsl:call-template>

        <!-- Further Reading. -->
        <p><a name="further-reading">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url"
                select="concat($content-filename, '#further-reading')"/>
            <xsl:with-param name="text" select="'Further&nbsp;Reading'"/>
            <xsl:with-param name="suffix-link-href" select="$top-link"/>
            <xsl:with-param name="suffix-link-text" select="'^'"/>
        </xsl:call-template></a></p>
    </xsl:template>

    <!-- Outputs links to the major sections of the semantics document. -->
    <xsl:template name="major-section-links">
        <xsl:call-template name="make-major-index-entry">
            <xsl:with-param name="url" select="$content-filename"/>
            <xsl:with-param name="text" select="$top-name"/>
        </xsl:call-template>
        <div class="plack-minor-index-entry"><a
            href="#introduction">Introduction</a></div>
        <div class="plack-minor-index-entry"><a
            href="#basic-parts">Basic Parts</a></div>
        <div class="plack-minor-index-entry"><a
            href="#documents-deconstructed">Documents Deconstructed</a></div>
        <div class="plack-minor-index-entry"><a
            href="#further-reading">Further Reading</a></div>
    </xsl:template>



    <!-- Content templates -->

    <xsl:template name="language-definition-body">
        <xsl:call-template name="table-of-contents"/>
        <xsl:call-template name="introduction"/>
        <xsl:call-template name="part.language"/>
<!--
        <xsl:call-template name="part.appendices"/>
-->
    </xsl:template>

<!--
<h2 id="ToC">Table of Contents</h2>
<ul>
    <li><a href="#introduction">Introduction</a></li>
    <li><a href="#part-language">Part 1: The Language</a></li>
    <li><a href="part-appendices">Part 2: Appendices</a></li>
</ul>
-->
    <xsl:template name="table-of-contents">
<h2 id="ToC">Table of Contents</h2>
<ul>
    <li><a href="#introduction">Introduction</a></li>
    <li><a href="#basic-parts">Basic Parts</a>
    <ul>
        <li><a href="#names">Names</a></li>
        <li><a href="#texts">Texts</a></li>
        <li><a href="#comments">Comments</a></li>
        <li><a href="#blank-lines">Blank Lines</a></li>
    </ul>
    </li>
    <li><a href="#documents-deconstructed">Documents Deconstructed</a>
    <ul>
        <li><a href="#first-line">The First Line</a></li>
        <li><a href="#top-level-commands">Top-Level Commands</a>
        <ul>
            <li><a href="#namespace-command">The Namespace Command</a></li>
            <li><a href="#set-command">The Set Command</a></li>
            <li><a href="#top-command">The Top Command</a></li>
        </ul>
        </li>
        <li><a href="#elements-and-attributes">Elements and Attributes</a></li>
        <li><a href="#expression-commands">Expression Commands</a>
        <ul>
            <li><a href="#get-command">The Get Command</a></li>
            <li><a href="#newline-command">The Newline Command</a></li>
            <li><a href="#quote-command">The Quote Command</a></li>
            <li><a href="#join-command">The Join Command</a></li>
            <li><a href="#quoted-command">The Quoted Command</a></li>
        </ul>
        </li>
    </ul>
    </li>
    <li><a href="#further-reading">Further Reading</a></li>
</ul>
    </xsl:template>

    <xsl:template name="introduction">
<h2 id="introduction">Anatomy of an Atria Document</h2>
<p>
This is an informal introduction to the Atria data representation language.
First we describe the more <a href="#basic-parts">basic</a> components of the
language, then we describe the <a href="#documents-deconstructed">form</a> of
an Atria document starting from its beginning and working our way to its
end.</p>
    </xsl:template>

    <xsl:template name="part.language">
<h3 id="basic-parts">Basic Parts</h3>
<p>
First the basic parts of an Atria document.</p>

<h4 id="names">Names</h4>
<p>
In Atria a <strong id="simple-name-def">simple name</strong> consists of a
sequence of one or more characters that are all uppercase or lowercase ASCII
letters (<code>A-Z</code> or <code>a-z</code>), decimal digits
(<code>0-9</code>), periods (<code>.</code>), hyphens (<code>-</code>) or
underscores (<code>_</code>). A <strong id="name-def">name</strong> is either
a simple name or two simple names separated by a colon (<code>:</code>),
where there's no spaces or other whitespace characters on either side of the
colon. So all of the following are valid names</p>
<pre>
name
id2
2by4
23
_-_
.
xsl:template
a:b
</pre>
<p>
where the first six are also valid simple names (though the last three would
be unusual choices for names), but none of the following are valid names:</p>
<pre>
a:b:c
any::thing
:first
last:
</pre>

<h4 id="texts">Texts</h4>
<p>
A <strong id="text-def">text</strong> is a sequence of zero or more
characters that both starts and ends with a double quote character
(<code>&quot;</code>), where both of those double quotes are on the same
line. The characters between the double quotes can be any Unicode character
except a double quote or a newline. So all of the following are valid
texts</p>
<pre>
&quot;This is a sentence. But this &quot;
&quot;Hi&quot;
&quot;&quot;
</pre>
<p>
but none of the following are</p>
<pre>
&quot;&quot;&quot;
&quot;
&quot;some&quot;thing&quot;
&quot;This is the first line
   and another line
followed by a third.&quot;
</pre>

<h4 id="comments">Comments</h4>
<p>
For any line that contains a single quote character (<code>'</code>) that
isn't between the double quotes of a <a href="#text-def">text</a>, the part
of that line starting from (and including) the single quote to the end of the
line is a <strong id="comment-def">comment</strong>. Comments are generally
ignored by software programs that process Atria documents since they're
intended to be used to contain information meant solely for human
readers.</p>

<h4 id="blank-lines">Blank Lines</h4>
<p>
A line is considered to be a <strong id="blank-line-def">blank line</strong>
if it contains nothing but whitespace characters and optionally a
<a href="#comment-def">comment</a>.</p>


<h3 id="documents-deconstructed">Documents Deconstructed</h3>
<p>
Here we describe the form of an Atria document, starting at its beginning and
proceeding through to its end. It's important to note that in these documents
the whitespace at the start of a line is significant unless that line is a
<a href="#blank-line-def">blank line</a>, and that a single level of
indentation consists of exactly 4 consecutive spaces: tabs are not
allowed.</p>

<h4 id="first-line">The First Line</h4>
<p>
The first non-<a href="#blank-line-def">blank</a> line of a document must
be</p>
<pre>
language atria
</pre>
<p>
and it must be unindented (that is, there mustn't be any spaces before the
word <code>language</code>).</p>

<h4 id="top-level-commands">Top-Level Commands</h4>
<p>
That line can be followed by zero or more
<strong id="top-level-command-def">top-level commands</strong>, which are
commands that start at the beginning of a line (and thus are unindented). A
<strong id="command-def">command</strong> is enclosed in square brackets and
consists of a <a href="#simple-name-def">simple name</a> followed by zero or
more arguments, where an <strong id="argument-def">argument</strong> is
either a <a href="#name-def">name</a>, a <a href="#text-def">text</a>, an
<a href="#expression-command-def">expression command</a> or an
<a href="#attribute-def">attribute</a>. (Both expression commands and
attributes will be described below.) The following are examples of valid
commands (when they appear at the right places in a document)</p>
<pre>
[get out]
[quote]
[set name &quot;Atria&quot;]
</pre>
<p>
but only the last one is a valid top-level command because the commands that
can be used as top-level commands are restricted to the ones described
next.</p>

<h5 id="namespace-command">The Namespace Command</h5>
<p>
A namespace command starts with the <a href="#simple-name-def">simple
name</a> <code>namespace</code> followed by two <a
href="#argument-def">arguments</a>, the first of which must be a simple name
that's different from the first argument of any other namespace command in
the same document, and the second of which must be a <a
href="#text-def">text</a> or an <a href="#expression-command-def">expression
command</a>. These are all valid namespace commands:</p>

<pre>
[namespace xsl &quot;http://www.w3.org/1999/XSL/Transform&quot;]
[namespace my [get my-ns-url]]
[namespace g [join https &quot;example.com/&quot; year &quot;/Graphics&quot;]]
</pre>

<h5 id="set-command">The Set Command</h5>
<p>
A set command starts with the <a href="#simple-name-def">simple name</a>
<code>set</code> followed by two <a href="#argument-def">arguments</a>, the
first of which is a simple name and the second of which is a
<a href="#text-def">text</a> or an
<a href="#expression-command-def">expression command</a>. The following are
all valid set commands:</p>

<pre>
[set my-ns-url &quot;https://steelcandy.org/ns/2023/fake&quot;]
[set lang &quot;atria&quot;]
</pre>
<p>
A set command sets (or resets) the value of the variable whose name is given
by the command's first argument. (So the examples above set the value of
variables named <code>my-ns-url</code> and <code>lang</code>, respectively.)</p>

<h5 id="top-command">The Top Command</h5>
<p>
A top command starts with the <a href="#simple-name-def">simple name</a>
<code>top</code> followed by one or more <a
href="#argument-def">arguments</a>, the first of which is a
<a href="#name-def">name</a>. Any subsequent arguments must all be
<a href="#attribute-def">attributes</a>. There can be at most one top command
in a given document, and if a document has one then it defines the
<a href="#root-element-def">root element</a> (see below) under which all of
the document's other elements are effectively indented. These are all valid
top commands (assuming that each is in a different document):</p>

<pre>
[top root]
[top xsl:transform version=&quot;1.0&quot;]
[top g:image width=&quot;300&quot; height=&quot;200&quot; background=&quot;black&quot;]
</pre>

<h4 id="elements-and-attributes">Elements and Attributes</h4>
<p>
An <strong id="element-def">element</strong> has a first line that consists
of a <a href="#name-def">name</a> followed by zero or more attributes, and
can have zero or more content items indented exactly one level under it, each
of which starts on its own line. An <strong
id="attribute-def">attribute</strong> consists of a name and either a
<a href="#text-def">text</a> or an
<a href="#expression-command-def">expression command</a>, separated by an
equals sign (<code>=</code>); a <strong id="content-item-def">content
item</strong> is either a text, an expression command or an element.</p>
<p>
The rest of a valid Atria document consists of one or more top-level
elements, where a <strong id="top-level-element-def">top-level
element</strong> is an element whose first line isn't indented. All of the
elements in a document must be (effectively) indented under a single element
called the document's <strong id="root-element-def">root element</strong>, so
for a given document to be valid it must be true that either</p>
<ul>
    <li>the document has a top-level <a href="#top-command">top command</a>
        that defines the document's root element, and under which all of the
        document's top-level elements are considered to be indented exactly
        one level, or</li>
    <li>the document has exactly one top-level element, which is also the
        document's root element.</li>
</ul>
<p>
An Atria document that starts with</p>

<pre>
albums list=&quot;partial&quot;
    album id=&quot;loudbomb2002&quot; year=&quot;2002&quot; format=&quot;cd&quot;
        artist
            &quot;Loudbomb&quot;
        title
            &quot;Long Playing Grooves&quot;
    album id=&quot;lovetractor1986&quot; year=&quot;1986&quot; format=&quot;lp&quot;
        artist
            &quot;Love Tractor&quot;
        title
            &quot;This Ain't No Outer Space Ship&quot;
    album id=&quot;lovettlyle1986&quot; year=&quot;1986&quot; format=&quot;cd&quot;
        artist
            &quot;Lyle Lovett&quot;
        title
            &quot;Lyle Lovett&quot;
</pre>
<p>
could be rewritten using a top command as</p>

<pre>
[top albums list=&quot;partial&quot;]

album id=&quot;loudbomb2002&quot; year=&quot;2002&quot; format=&quot;cd&quot;
    artist
        &quot;Loudbomb&quot;
    title
        &quot;Long Playing Grooves&quot;
album id=&quot;lovetractor1986&quot; year=&quot;1986&quot; format=&quot;lp&quot;
    artist
        &quot;Love Tractor&quot;
    title
        &quot;This Ain't No Outer Space Ship&quot;
album id=&quot;lovettlyle1986&quot; year=&quot;1986&quot; format=&quot;cd&quot;
    artist
        &quot;Lyle Lovett&quot;
    title
        &quot;Lyle Lovett&quot;
</pre>
<p>
without affecting what information it represents: they both define a document
whose root element is named <code>albums</code>, for example. The latter form
just reduces the number of levels that elements have to be indented.</p>

<h4 id="expression-commands">Expression Commands</h4>
<p>
An <strong id="expression-command-def">expression command</strong> is a
command that isn't a <a href="#top-level-commands">top-level command</a>: its
opening bracket isn't at the beginning of an unindented line. Below we
describe the only commands that can be used as expression commands. (Note
that, at least currently, none of the commands that can be used as top-level
commands can be used as expression commands, and vice versa.)</p>

<h5 id="get-command">The Get Command</h5>
<p>
A get command starts with the <a href="#simple-name-def">simple name</a>
<code>get</code> followed by a single simple name argument, where that simple
name should be the same as that of the first argument of a
<a href="#set-command">set command</a> that precedes the get command in the
document. Such a get command represents the value represented by the second
argument of the closest such set command. The following are all valid get
commands:</p>

<pre>
[get name]
[get id]
</pre>

<h5 id="newline-command">The Newline Command</h5>
<p>
A newline command consists of the <a href="#simple-name-def">simple name</a>
<code>newline</code> without any arguments after it, and represents a single
newline character. A valid newline command is</p>

<pre>
[newline]
</pre>

<h5 id="quote-command">The Quote Command</h5>
<p>
A quote command consists of the <a href="#simple-name-def">simple name</a>
<code>quote</code> without any arguments after it, and represents a single
double quote character (<code>&quot;</code>). A valid quote command is</p>

<pre>
[quote]
</pre>

<h5 id="join-command">The Join Command</h5>
<p>
A join command starts with the <a href="#simple-name-def">simple name</a>
<code>join</code> followed by one or more arguments, each of which must be a
<a href="#text-def">text</a> or an
<a href="#expression-command-def">expression command</a>. It represents the
concatenation of the values that each of its arguments represent, in order.
Thus all of the join commands in the following represent the same value:</p>

<pre>
[set where &quot;there&quot;]
[join &quot;Hi&quot; &quot; &quot; &quot;there!&quot;]
[join &quot;Hi &quot; &quot;there!&quot;]
[join &quot;Hi &quot; [get where] &quot;!&quot;]
</pre>

<h5 id="quoted-command">The Quoted Command</h5>
<p>
A quoted command — not to be confused with a <a href="#quote-command">quote
command</a> — starts with the <a href="#simple-name-def">simple name</a>
<code>quoted</code> followed by a single argument that's either a
<a href="#text-def">text</a> or an
<a href="#expression-command-def">expression command</a>, and it represents
the same value that its argument represents preceded and followed by a double
quote character (<code>&quot;</code>). For example,</p>

<pre>
&quot;Oops!&quot;           ' Oops!
[quoted &quot;Oops!&quot;]  ' &quot;Oops!&quot;
</pre>
<p>
For any expression command or text <code>expr</code> the command
<code>[quoted expr]</code> represents the same value that
<code>[join [quote] expr [quote]]</code> does.</p>

<h3 id="further-reading">Further Reading</h3>
<p>
For a more complete and formal description of the Atria data representation
language see the language's <a href="grammar.html">grammar</a>,
<a href="validity-constraints.html">validity constraints</a> and
<a href="semantics.html">semantics</a>.</p>
    </xsl:template>
</xsl:transform>
