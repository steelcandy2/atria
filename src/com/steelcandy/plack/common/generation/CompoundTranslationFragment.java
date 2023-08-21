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

package com.steelcandy.plack.common.generation;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.common.io.*;

import java.io.IOException;
import java.util.*;

/**
    The class of TranslationFragment that represents zero or more other
    consecutive TranslationFragments. It also manages the indenting of
    lines of target code.

    @author  James MacKay
    @version $Revision: 1.2 $
*/
public class CompoundTranslationFragment
    implements TranslationFragment
{
    // Constants

    /**
        The default number of spaces that make up one level of indentation.
    */
    public static final int DEFAULT_INDENT_SIZE = 4;


    // Private fields

    /** Our consecutive TranslationFragments. */
    private List _components;

    /**
        The expandable translation fragment we're currently adding target
        code to.
    */
    private ExpandableFixedTranslationFragment _expandableFragment;

    /** Our current indentation level. */
    private int _indentLevel;

    /** The spaces that make up one level of indentation. */
    private String _oneIndent;

    /**
        Indicates whether the next code added to us is at the start of a
        line of target code.
    */
    private boolean _isAtStartOfLine;


    // Constructors

    /**
        Constructs a CompoundTranslationFragment whose initial indentation
        level is 'initialIndentLevel', where each level of indentation
        consists of 'indentSize' spaces.

        @param initialIndentLevel the number of levels of indentation that
        each
        @param indentSize the number of spaces in each level of indentation
    */
    public CompoundTranslationFragment(int initialIndentLevel,
                                       int indentSize)
    {
        Assert.require(initialIndentLevel >= 0);
        Assert.require(indentSize > 0);

        _components = new ArrayList();
        _expandableFragment = new ExpandableFixedTranslationFragment();
        _indentLevel = initialIndentLevel;
        _oneIndent = buildOneIndent(indentSize);
        _isAtStartOfLine = true;

        Assert.ensure(indentLevel() == initialIndentLevel);
    }

    /**
        Constructs a CompoundTranslationFragment whose initial indentation
        level is 'initialIndentLevel', where each level of indentation
        consists of 'DEFAULT_INDENT_SIZE' spaces.

        @param initialIndentLevel the number of levels of indentation that
        each
    */
    public CompoundTranslationFragment(int initialIndentLevel)
    {
        // Assert.require(initialIndentLevel >= 0);
        this(initialIndentLevel, DEFAULT_INDENT_SIZE);
    }

    /**
        Constructs a CompoundTranslationFragment whose initial indentation
        level is 0, where each level of indentation consists of
        'DEFAULT_INDENT_SIZE' spaces.
    */
    public CompoundTranslationFragment()
    {
        this(0, DEFAULT_INDENT_SIZE);
    }


    // Public methods

    /**
        Adds 'code' to the end of the target code that we represent.
        <p>
        Note: if 'code' contains any newlines then the lines after any
        newlines in it cannot be proeprly indented by us.

        @param code a piece of target code that is assumed not to contain
        any newlines (even at its end)
        @return this
    */
    public CompoundTranslationFragment add(String code)
    {
        Assert.require(code != null);  // though it may be empty

        if (code.length() > 0)
        {
            addIndentIfAtStartOfLine();
            _expandableFragment.add(code);
            Assert.check(_isAtStartOfLine == false);
        }

        // Assert.ensure(result != null);
        return this;
    }

    /**
        Adds the target code representation of 'val' to the end of the target
        code that we represent.

        @param val an int value
        @return this
    */
    public CompoundTranslationFragment add(int val)
    {
        return add(String.valueOf(val));
    }

    /**
        Adds the target code in 'frag' to the end of the target code that
        we represent, where 'frag' is assumed to consist of zero or more
        full lines of target code.

        @param frag represents the full lines of target code to add
        @return this
    */
    public CompoundTranslationFragment add(TranslationFragment frag)
    {
        Assert.require(frag != null);

        addIndentIfAtStartOfLine();
        addFragment(frag, true);

        // Assert.ensure(result != null);
        return this;
    }

    /**
        Adds the target code in 'frag' to the end of the target code that
        we represent, where the last line of 'frag' is assumed to only be a
        partial line of target code.

        @param frag the target code to add, where its last line is assumed to
        only be a partial line of target code
        @return this
    */
    public CompoundTranslationFragment
        addPartialLine(TranslationFragment frag)
    {
        Assert.require(frag != null);

        addIndentIfAtStartOfLine();
        addFragment(frag, false);

        // Assert.ensure(result != null);
        return this;
    }

    /**
        Adds a newline to the end of the target code that we represent.

        @return this
    */
    public CompoundTranslationFragment addNewline()
    {
        // Note: we NEVER indent right before a newline if it's the first
        // thing on a line: we just write out an empty line instead.
        _expandableFragment.add(Io.NL);
        _isAtStartOfLine = true;

        // Assert.ensure(result != null);
        return this;
    }


    /**
        Increases by one the number of levels we're currently indenting lines
        of target code.

        @return this
    */
    public CompoundTranslationFragment indentMore()
    {
        _indentLevel += 1;

        // Assert.ensure(result != null);
        return this;
    }

    /**
        Decreases by one the number of levels we're currently indenting lines
        of target code.

        @return this
    */
    public CompoundTranslationFragment indentLess()
    {
        Assert.require(indentLevel() > 0);

        _indentLevel -= 1;

        // Assert.ensure(result != null);
        return this;
    }

    /**
        @return the number of levels we're currently indenting lines of
        target code
    */
    public int indentLevel()
    {
        int result = _indentLevel;

        Assert.ensure(result >= 0);
        return result;
    }


    /**
        @see TranslationFragment#isEmpty
    */
    public boolean isEmpty()
    {
        boolean result = _expandableFragment.isEmpty();

        if (result)
        {
            Iterator iter = _components.iterator();
            while (result && iter.hasNext())
            {
                TranslationFragment tr = (TranslationFragment) iter.next();
                result = tr.isEmpty();
            }
        }

        return result;
    }

    /**
        @see TranslationFragment#write(IndentWriter)
    */
    public void write(IndentWriter w)
        throws IOException
    {
        Assert.require(w != null);

        Iterator iter = _components.iterator();
        while (iter.hasNext())
        {
            TranslationFragment tr = (TranslationFragment) iter.next();
            tr.write(w);
        }
        _expandableFragment.write(w);
    }


    // Protected methods

    /**
        @param sz the number of spaces that are to make up one indent level
        @return the 'sz' spaces that make up one level of indentation
    */
    protected String buildOneIndent(int sz)
    {
        Assert.require(sz > 0);

        StringBuffer res = new StringBuffer(sz);
        for (int i = 0; i < sz; i++)
        {
            res.append(" ");
        }

        String result = res.toString();

        Assert.ensure(result != null);
        Assert.ensure(result.length() == sz);
        return result;
    }

    /**
        Adds to us the number of spaces required to indent us our current
        number of indent levels iff we're at the start of a line of target
        code.
    */
    protected void addIndentIfAtStartOfLine()
    {
        if (_isAtStartOfLine)
        {
            addIndentLevels();
            _isAtStartOfLine = false;
        }

        Assert.ensure(_isAtStartOfLine == false);
    }

    /**
        Adds to us the spaces required to indent a line our current number of
        indentation levels.
    */
    protected void addIndentLevels()
    {
        int sz = _indentLevel;
        for (int i = 0; i < sz; i++)
        {
            _expandableFragment.add(_oneIndent);
        }
    }


    // Private methods

    /**
        Adds the target code in 'frag' to the end of the target code that
        we represent, where 'frag' is assumed to consist of zero or more
        full lines of target code if 'isFullLines' is true, and whose last
        line is assumed to only be a partial line otherwise.

        @param frag the target code to add to us
        @param isFullLines true iff 'frag' consists of zero or more full
        lines of target code
    */
    private void addFragment(TranslationFragment frag, boolean isFullLines)
    {
        if (_expandableFragment.isEmpty() == false)
        {
            _components.add(_expandableFragment);
            _expandableFragment = new ExpandableFixedTranslationFragment();
        }
        _components.add(frag);
        _isAtStartOfLine = isFullLines;

        Assert.ensure(_isAtStartOfLine == isFullLines);
        Assert.ensure(_expandableFragment.isEmpty());
    }
}
