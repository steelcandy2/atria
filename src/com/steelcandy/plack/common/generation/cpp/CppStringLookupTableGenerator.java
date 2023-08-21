/*
 Copyright (C) 2011-2012 by James MacKay.

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

package com.steelcandy.plack.common.generation.cpp;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.generation.CommonCodeGenerationUtilities;

import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.ints.*;
import com.steelcandy.common.io.IndentWriter;
import com.steelcandy.common.text.*;

import java.util.*;

/**
    Generates C++ StringLookupTables from a Java map whose keys are
    Strings.

    @author  James MacKay
    @version $Revision: 1.6 $
*/
public class CppStringLookupTableGenerator
    implements CommonCppGeneratorBase
{
    /*
        Implementation Note:
        --------------------
        Though we don't currently guarantee (or even mention anywhere but
        here) that we do so, we keep the keys and values in the generated C++
        arrays in the same order as they're obtained from the specified Map
        (which order can be controlled if it's something like a
        LinkedHashMap). We can make this fact part of the description of this
        class if and when we have a need for it.
    */


    // Constants

    /** An instance of the code generation utilities class. */
    private static final CommonCppGenerationUtilities
        UTILITIES = new CommonCppGenerationUtilities();

    /** 0-based indices for the various types of C++ characters. */
    private static final int
        CPP_NARROW_CHAR_TYPE_INDEX = 0,
        CPP_WIDE_CHAR_TYPE_INDEX = CPP_NARROW_CHAR_TYPE_INDEX + 1,
        NUM_CHAR_TYPES = CPP_WIDE_CHAR_TYPE_INDEX + 1;

    /** The C++ character type names. */
    private static final String
        CPP_NARROW_CHAR_TYPE = "char",
        CPP_WIDE_CHAR_TYPE = "wchar_t";

    /** The C++ string literal prefixes. */
    private static final String
        CPP_NARROW_CHAR_STRING_LITERAL_PREFIX =
            UTILITIES.CPP_DEFAULT_STRING_LITERAL_PREFIX,
        CPP_WIDE_CHAR_STRING_LITERAL_PREFIX =
            UTILITIES.CPP_WIDE_STRING_LITERAL_PREFIX;

    /**
        Maps 0-based C++ character type indices to their C++ type names.
    */
    private static String[] CPP_CHAR_TYPE_NAME_MAP =
        buildCharacterTypeNameMap();

    /**
        Maps 0-based character type indices to the prefix to use on C++ string
        literals made of that type of character.
    */
    private static String[] CPP_STRING_LITERAL_PREFIX_MAP =
        buildStringLiteralPrefixMap();


    /**
        The suffixes of various C++ names in the code we generate.
    */
    private static final String
        KEYS_ARRAY_NAME_SUFFIX = "LookupTableKeys",
        VALUES_ARRAY_NAME_SUFFIX = "LookupTableValues",
        LOOKUP_TABLE_CLASS_NAME_SUFFIX = "LookupTable";

    /**
        Various C++ identifiers from the non-generated C++ lookup table code
        (in lookup.h).
    */
    private static final String
        LOOKUP_TABLE_BASE_CLASS_NAME = "StringLookupTable",
        LOOKUP_TABLE_KEY_TYPE_NAME = "Key",
        LOOKUP_TABLE_VALUE_TYPE_NAME = "Value",
        LOOKUP_TABLE_INDEX_TYPE_NAME = "int",
        LOOKUP_TABLE_CHAR_TYPE_NAME = "CharType",
        LOOKUP_TABLE_FIND_INDEX_METHOD_NAME = "findIndex",
        LOOKUP_TABLE_VALUE_AT_INDEX_METHOD_NAME = "valueAtIndex",
        LOOKUP_KEY_SIZE_METHOD_NAME = "size",
        LOOKUP_KEY_IS_EQUAL_TO_METHOD_NAME = "isEqualTo",
        LOOKUP_KEY_CHARACTER_AT_METHOD_NAME = "characterAt";

    /** Local C++ argument and variable names. */
    private static final String
        INDEX_VARIABLE_NAME = "index",
        RESULT_VARIABLE_NAME = "result",
        KEY_VARIABLE_NAME = "key";


    /**
        The instances of this class.
    */
    private static final CppStringLookupTableGenerator
        _narrowHidingInstance = new CppStringLookupTableGenerator(true,
                                                CPP_NARROW_CHAR_TYPE_INDEX),
        _narrowNonhidingInstance = new CppStringLookupTableGenerator(false,
                                                CPP_NARROW_CHAR_TYPE_INDEX),
        _wideHidingInstance = new CppStringLookupTableGenerator(true,
                                                CPP_WIDE_CHAR_TYPE_INDEX),
        _wideNonhidingInstance = new CppStringLookupTableGenerator(false,
                                                CPP_WIDE_CHAR_TYPE_INDEX);


    // Private fields

    /**
        Indicates whether the generated C++ code should hide some parts of
        the lookup table's implementation inside an anonymous C++ namespace.
    */
    private boolean _doHideParts;

    /**
        The 0-based index for the C++ character type to use in the generated
        code: its value should be that of one of our CPP_*_CHAR_TYPE_INDEX
        constants.
    */
    private int _charTypeIndex;


    // Constructors

    /**
        Creates and returns an instance that uses narrow characters in the
        C++ strings it generates, and that generates C++ code that hides
        some of the lookup table's implementation in an anonymous C++
        namespace.

        @see #CppStringLookupTableGenerator(boolean, int)
    */
    public static CppStringLookupTableGenerator createNarrow()
    {
        Assert.ensure(_narrowHidingInstance != null);
        return _narrowHidingInstance;
    }

    /**
        Creates and returns an instance that uses narrow characters in the
        C++ strings it generates, and that generates C++ code that doesn't
        hide any of the lookup table's implementation in an anonymous C++
        namespace.
        <p>
        This constructor is usually used because all of the generated C++
        code will already be in an anonymous C++ namespace, but it can also
        be used if more direct access is required to the information in the
        lookup table.

        @see #CppStringLookupTableGenerator(boolean, int)
    */
    public static CppStringLookupTableGenerator createNarrowUnhidden()
    {
        Assert.ensure(_narrowNonhidingInstance != null);
        return _narrowNonhidingInstance;
    }

    /**
        Creates and returns an instance that uses wide characters in the
        C++ strings it generates, and that generates C++ code that hides
        some of the lookup table's implementation in an anonymous C++
        namespace.

        @see #CppStringLookupTableGenerator(boolean, int)
    */
    public static CppStringLookupTableGenerator createWide()
    {
        Assert.ensure(_wideHidingInstance != null);
        return _wideHidingInstance;
    }

    /**
        Creates and returns an instance that uses wide characters in the
        C++ strings it generates, and that generates C++ code that doesn't
        hide any of the lookup table's implementation in an anonymous C++
        namespace.
        <p>
        This constructor is usually used because all of the generated C++
        code will already be in an anonymous C++ namespace, but it can also
        be used if more direct access is required to the information in the
        lookup table.

        @see #CppStringLookupTableGenerator(boolean, int)
    */
    public static CppStringLookupTableGenerator createWideUnhidden()
    {
        Assert.ensure(_wideNonhidingInstance != null);
        return _wideNonhidingInstance;
    }

    /**
        Constructs a CppStringLookupTableGenerator.

        @param doHideParts true iff the generated C++ code should hide some
        parts of the lookup table's implementation inside an anonymous C++
        namespace
        @param charTypeIndex the 0-based index of the C++ character type to
        use in generated code
    */
    private CppStringLookupTableGenerator(boolean doHideParts,
                                          int charTypeIndex)
    {
        Assert.require(charTypeIndex >= 0);
        Assert.require(charTypeIndex < NUM_CHAR_TYPES);

        _doHideParts = doHideParts;
        _charTypeIndex = charTypeIndex;
    }


    // Public methods

    /**
        Generates the C++ StringLookupTable (and associated code) that maps
        'm''s keys to its values. 'm''s keys and values are all assumed to be
        Strings, but the keys will be converted by this generator into valid
        C++ string literals, whereas the values are assumed to consist of the
        C++ source code that evaluates to the values that the keys are to map
        to.

        @param w the writer to use to write out the generated C++ source code
        @param m the map from each of the strings that the generated lookup
        table is map to a value, to the C++ code that will evaluate to that
        value in the generated code
        @param prefix the prefix that is to start the name of the generated
        lookup table class, as well as possibly other generated top-level
        names
        @param cppValueType a string representation of the C++ type of the
        values in the generated lookup table
    */
    public void generate(IndentWriter w, Map m, String prefix,
                         String cppValueType)
    {
        Assert.require(w != null);
        Assert.require(m != null);
        Assert.require(prefix != null);
        Assert.require(cppValueType != null);

        StringList keysList = buildKeysList(m);

        if (m.isEmpty() == false)
        {
            writeHiddenPartStart(w);
            try
            {
                writeKeysArray(w, keysList, prefix);
                writeValuesArray(w, keysList, m, prefix, cppValueType);
            }
            finally
            {
                writeHiddenPartEnd(w);
            }
        }
        writeLine(w, "");
        writeLookupTableClass(w, keysList, prefix, cppValueType);
    }

    /**
        Note: the result of calling this method with the same 'prefix'
        argument as was passed to a call of our generate() method will
        return the name of the lookup table class that that generate() call
        generated the source code for.

        @param prefix the prefix that the class name is to start with
        @return the name of the lookup table class name eith prefix 'prefix'
    */
    public String lookupTableClassName(String prefix)
    {
        Assert.require(prefix != null);

        //Assert.ensure(result != null);
        return prefix + LOOKUP_TABLE_CLASS_NAME_SUFFIX;
    }


    // Protected methods

    /**
        Builds and returns a list of all of the keys in 'm' in the order that
        they're returned by the map's keySet()'s iterator.

        @param m the maps whose keys are used to build the list
        @return a list of all of the keys in 'm'
    */
    protected StringList buildKeysList(Map m)
    {
        Assert.require(m != null);

        StringList result = StringList.createArrayList(m.size());

        Iterator iter = m.keySet().iterator();
        while (iter.hasNext())
        {
            result.add((String) iter.next());
        }

        Assert.ensure(result != null);
        Assert.ensure(result.size() == m.size());
        return result;
    }

    /**
        Writes out the C++ code that defines the C++ array that contains all
        of our tables' keys.

        @param w the writer to use
        @param keysList the table's keys, in order
        @param prefix the prefix that is to start the name of the C++ array
    */
    protected void writeKeysArray(IndentWriter w, StringList keysList,
                                  String prefix)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(prefix != null);

        write(w, CPP_CONSTANT_START);
        write(w, characterType());
        write(w, CPP_POINTER_SUFFIX);
        write(w, keysArrayName(prefix));
        writeLine(w, "[] = {");
        w.incrementIndentLevel();
        try
        {
            String litPrefix = stringLiteralPrefix();
            StringIterator iter = keysList.iterator();
            while (iter.hasNext())
            {
                UTILITIES.writeStringLiteral(w, iter.next(), litPrefix);
                if (iter.hasNext())
                {
                    write(w, ",");
                }
                writeLine(w, "");
            }
        }
        finally
        {
            w.decrementIndentLevel();
            writeLine(w, "};");
        }
    }

    /**
        Writes out the C++ code that defines the C++ array that contains all
        of our tables' values.

        @param w the writer to use
        @param keysList the table's keys, in order
        @param m the map from which to get the table's values
        @param prefix the prefix that is to start the name of the C++ array
        @param cppValueType a string representation of the C++ type of the
        values in the generated lookup table
    */
    protected void writeValuesArray(IndentWriter w, StringList keysList,
                                Map m, String prefix, String cppValueType)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(m != null);
        Assert.require(m.size() == keysList.size());
        Assert.require(prefix != null);
        Assert.require(cppValueType != null);

        write(w, CPP_CONSTANT_START);
        write(w, cppValueType);
        write(w, " ");
        write(w, valuesArrayName(prefix));
        writeLine(w, "[] = {");
        w.incrementIndentLevel();
        try
        {
            StringIterator iter = keysList.iterator();
            while (iter.hasNext())
            {
                String cppValue = (String) m.get(iter.next());
                write(w, cppValue);
                if (iter.hasNext())
                {
                    write(w, ",");
                }
                writeLine(w, "");
            }
        }
        finally
        {
            w.decrementIndentLevel();
            writeLine(w, "};");
        }
    }

    /**
        Writes out the definition of the C++ StringLookupTable subclass that
        maps 'm''s keys to its values.

        @param w the writer to use
        @param keysList the table's keys, in order
        @param prefix the prefix that is to start the name of the C++ class
        @param cppValueType a string representation of the C++ type of the
        values in the generated lookup table
    */
    protected void writeLookupTableClass(IndentWriter w, StringList keysList,
                                         String prefix, String cppValueType)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);  // but it may be empty
        Assert.require(prefix != null);
        Assert.require(cppValueType != null);

        final String className = lookupTableClassName(prefix);

        UTILITIES.writeCommentBlockStart(w);
        try
        {
            writeLine(w, "The lookup table class.");
        }
        finally
        {
            UTILITIES.writeCommentBlockEnd(w);
        }
        write(w, CPP_CLASS_KEYWORD);
        write(w, " ");
        write(w, className);
        write(w, ": public ");
        write(w, LOOKUP_TABLE_BASE_CLASS_NAME);
        write(w, "<");
        write(w, characterType());
        write(w, ", ");
        write(w, cppValueType);
        writeLine(w, ">");
        UTILITIES.writeClassBodyStart(w,  className);
        try
        {
            UTILITIES.writePublicClassSectionStart(w);
            try
            {
                writeFindIndexMethod(w, keysList, prefix);
                writeLine(w, "");
                writeValueAtIndexMethod(w, keysList, prefix);
            }
            finally
            {
                UTILITIES.writeClassSectionEnd(w);
            }
        }
        finally
        {
            UTILITIES.writeClassBodyEnd(w, className);
        }
    }


    /**
        @param prefix the prefix the array name is to start with
        @return the name of the keys array with prefix 'prefix'
    */
    protected String keysArrayName(String prefix)
    {
        Assert.require(prefix != null);

        //Assert.ensure(result != null);
        return prefix + KEYS_ARRAY_NAME_SUFFIX;
    }

    /**
        @param prefix the prefix the array name is to start with
        @return the name of the values array with prefix 'prefix'
    */
    protected String valuesArrayName(String prefix)
    {
        Assert.require(prefix != null);

        //Assert.ensure(result != null);
        return prefix + VALUES_ARRAY_NAME_SUFFIX;
    }


    /**
        @return the name of the character type to use in the generated C++
    */
    protected String characterType()
    {
        String result = CPP_CHAR_TYPE_NAME_MAP[_charTypeIndex];

        Assert.ensure(result != null);
        return result;
    }

    /**
        @return the prefix to use on string literals in the generated C++
    */
    protected String stringLiteralPrefix()
    {
        String result = CPP_STRING_LITERAL_PREFIX_MAP[_charTypeIndex];

        Assert.ensure(result != null);
        return result;
    }


    /**
        Writes out the start of a part of the C++ code that we generate that
        is to be hidden, iff we're hiding parts of the code we generate.

        @param w the writer to use
        @see #writeHiddenPartEnd(IndentWriter)
    */
    protected void writeHiddenPartStart(IndentWriter w)
    {
        Assert.require(w != null);

        if (_doHideParts)
        {
// TODO: should this write the start of an anonymous C++ namespace instead ????!!!!????
// - in which case the following method should call writeNamespaceEnd()
            UTILITIES.writeBlockStart(w);
        }
    }

    /**
        Writes out the end of a part of the C++ code that we generate that
        is to be hidden, iff we're hiding parts of the code we generate.

        @param w the writer to use
        @see #writeHiddenPartStart(IndentWriter)
    */
    protected void writeHiddenPartEnd(IndentWriter w)
    {
        Assert.require(w != null);

        if (_doHideParts)
        {
            UTILITIES.writeBlockEnd(w);
        }
    }


    /**
        Writes out the definition of the "valueAtIndex()" method in the
        lookup table subclass that we generate.

        @param w the writer to use
        @param keysList the table's keys, in order
        @param prefix the prefix used to in building our class and array
        names
    */
    protected void writeValueAtIndexMethod(IndentWriter w,
                                    StringList keysList, String prefix)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);  // but it may be empty
        Assert.require(prefix != null);

        final String indexArg = INDEX_VARIABLE_NAME;

        write(w, CPP_CONSTANT_START);
        write(w, LOOKUP_TABLE_VALUE_TYPE_NAME);
        write(w, " &");
        write(w, LOOKUP_TABLE_VALUE_AT_INDEX_METHOD_NAME);
        write(w, "(");
        write(w, LOOKUP_TABLE_INDEX_TYPE_NAME);
        write(w, " ");
        write(w, indexArg);
        write(w, ") ");
        writeLine(w, CPP_CONSTANT_KEYWORD);
        UTILITIES.writeThrowNothingClause(w);
        UTILITIES.writeBlockStart(w);
        try
        {
            write(w, CPP_ASSERT_MACRO_NAME);
            write(w, "(");
            write(w, indexArg);
            writeLine(w, " >= 0);");

            if (keysList.isEmpty())
            {
                write(w, CPP_LINE_COMMENT_START);
                write(w, "'");
                write(w, indexArg);
                writeLine(w, "' can never be valid/in range.");
                write(w, CPP_THROW_START);
                write(w, CPP_OUT_OF_RANGE_EXCEPTION_TYPE_NAME);
                write(w, EMPTY_ARGUMENTS);
                writeLine(w, ";");
            }
            else
            {
                write(w, CPP_RETURN_START);
                write(w, valuesArrayName(prefix));
                write(w, "[");
                write(w, indexArg);
                writeLine(w, "];");
            }
        }
        finally
        {
            UTILITIES.writeBlockEnd(w);
        }
    }

    /**
        Writes out the definition of the "findIndex()" method in the lookup
        table subclass that we generate.

        @param w the writer to use
        @param keysList the table's keys, in order
        @param prefix the prefix used to in building our class and array
        names
    */
    protected void writeFindIndexMethod(IndentWriter w, StringList keysList,
                                        String prefix)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);  // but it may be empty
        Assert.require(prefix != null);

        write(w, LOOKUP_TABLE_INDEX_TYPE_NAME);
        write(w, " ");
        write(w, LOOKUP_TABLE_FIND_INDEX_METHOD_NAME);
        write(w, "(");
        write(w,  CPP_CONSTANT_START);
        write(w, LOOKUP_TABLE_KEY_TYPE_NAME);
        write(w, " &");
        write(w, KEY_VARIABLE_NAME);
        write(w, ") ");
        writeLine(w, CPP_CONSTANT_KEYWORD);
        UTILITIES.writeThrowNothingClause(w);
        UTILITIES.writeBlockStart(w);
        try
        {
            if (keysList.isEmpty())
            {
                write(w, CPP_RETURN_START);
                writeLine(w, "-1;");
            }
            else
            {
                writeSetFindIndexResultCode(w, keysList,
                                            keysArrayName(prefix));

                String resultVar = RESULT_VARIABLE_NAME;
                writeLine(w, "");
                write(w, CPP_ASSERT_MACRO_NAME);
                write(w, "(");
                write(w, resultVar);
                writeLine(w, " >= -1);");
                write(w, CPP_RETURN_START);
                write(w, resultVar);
                writeLine(w, ";");
            }
        }
        finally
        {
            UTILITIES.writeBlockEnd(w);
        }
    }

    /**
        Writes out the part of the "findIndex()" method's body that sets
        its result by searching our (non-empty) keys array.

        @param w the writer to use
        @param keysList the list of the keys in the keys array, in order
        @param keysArrayName the name of the C++ keys array to search
    */
    protected void writeSetFindIndexResultCode(IndentWriter w,
                                StringList keysList, String keysArrayName)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(keysArrayName != null);

        final String keyArg = KEY_VARIABLE_NAME;
        final String resultVar = RESULT_VARIABLE_NAME;
        write(w, LOOKUP_TABLE_INDEX_TYPE_NAME);
        write(w, " ");
        write(w, resultVar);
        writeLine(w, " = -1;");
        writeLine(w, "");

        Map keyLengthsToIndices = buildKeyLengthsToIndicesMap(keysList);
        int sz = keyLengthsToIndices.size();
        Assert.check(sz > 0);
            // since 'keysList' isn't empty
        if (sz == 1)
        {
            Map.Entry entry = (Map.Entry)
                keyLengthsToIndices.entrySet().iterator().next();
            Integer keyLength = (Integer) entry.getKey();
            write(w, CPP_IF_START);
            write(w, "(");
            write(w, keyArg);
            write(w, ".");
            write(w, LOOKUP_KEY_SIZE_METHOD_NAME);
            write(w, EMPTY_ARGUMENTS);
            write(w, " == ");
            write(w, keyLength.toString());
            writeLine(w, ")");
            UTILITIES.writeBlockStart(w);
            try
            {
                writeSetFindIndexResultByLength(w, keysList,
                    keysArrayName, keyLength.intValue(),
                    (IntSet) entry.getValue());
            }
            finally
            {
                UTILITIES.writeBlockEnd(w);
            }
        }
        else  // sz > 1
        {
            write(w, CPP_SWITCH_KEYWORD);
            write(w, " (");
            write(w, keyArg);
            write(w, ".");
            write(w, LOOKUP_KEY_SIZE_METHOD_NAME);
            write(w, EMPTY_ARGUMENTS);
            writeLine(w, ")");
            UTILITIES.writeBlockStart(w);
            try
            {
                Iterator iter = keyLengthsToIndices.entrySet().iterator();
                while (iter.hasNext())
                {
                    Map.Entry entry = (Map.Entry) iter.next();
                    int keyLength = ((Integer) entry.getKey()).intValue();
                    UTILITIES.writeCaseLabel(w, keyLength);
                    writeSetFindIndexResultByLength(w, keysList,
                        keysArrayName, keyLength, (IntSet) entry.getValue());
                    write(w, CPP_BREAK_KEYWORD);
                    writeLine(w, ";");
                }
            }
            finally
            {
                UTILITIES.writeBlockEnd(w);
            }
        }
    }

    /**
        Builds and returns a map that maps the unique sizes/lengths of the
        keys in 'keysList' to the 0-based indices (into 'keysList') of all of
        the keys in 'keysList' that have that size/length.

        @param keysList a lookup table's keys, in order
    */
    protected Map buildKeyLengthsToIndicesMap(StringList keysList)
    {
        Assert.require(keysList != null);

        Map result = new HashMap();

        int index = 0;
        StringIterator iter = keysList.iterator();
        while (iter.hasNext())
        {
            Integer sz = new Integer(iter.next().length());

            IntSet val = (IntSet) result.get(sz);
            if (val == null)
            {
                val = new IntHashSet();
                result.put(sz, val);
            }
            Assert.check(val != null);
            val.add(index);

            index += 1;
        }

        Assert.ensure(result != null);
        Assert.ensure(result.isEmpty() == keysList.isEmpty());
        return result;
    }

    /**
        Writes out the part of the "findIndex()" method's body that sets
        its result by searching those items in our (non-empty) keys array
        whose 0-based indices are in 'keyIndices', which is assumed to
        contain the indices (into 'keysList') of all of the keys in
        'keysList' whose size/length is 'keyLength'.

        @param w the writer to use
        @param keysList all of the keys array's keys, in order
        @param keysArrayName the name of the C++ keys array to search
        @param keyLength the size/length of all of the keys in 'keysList'
        whose indices are in 'keyIndices'
        @param keyIndices the 0-based indices (into 'keysList') of all of the
        keys in 'keysList' whose size/length is 'keyLength'
    */
    protected void writeSetFindIndexResultByLength(IndentWriter w,
        StringList keysList, String keysArrayName, int keyLength,
        IntSet keyIndices)
    {
        Assert.require(w != null);
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(keysArrayName != null);
        Assert.require(keyLength >= 0);
        Assert.require(keyIndices != null);
        Assert.require(keyIndices.isEmpty() == false);

        final String resultVar = RESULT_VARIABLE_NAME;
        final String keyArg = KEY_VARIABLE_NAME;

        int sz = keyIndices.size();
        if (sz == 1)
        {
            int arrayIndex = keyIndices.iterator().next();
            writeCompareKeyWithArrayItem(w, keysArrayName,
                                         keyLength, arrayIndex);
        }
        else  // sz > 1
        {
            List charPartitionings = new ArrayList();
            CoveringPartitions bestCovering = null;
            for (int i = 0; i < keyLength; i++)
            {
                CharacterPartitioning part =
                    new CharacterPartitioning(keysList, i, keyIndices);
                if (part.areAllCandidatesInSeparatePartitions())
                {
                    // We can stop here since we can compare all of the
                    // remaining keys on 'part''s character to determine
                    // which one the key can match.
                    bestCovering = part.minimalCoveringFor(keyIndices);
                    charPartitionings.add(part);
                        // so it's not empty (though it shouldn't get used)
                    break;  // for
                }
                else if (part.partitionCount() > 1)
                {
                    charPartitionings.add(part);
                }
                // Otherwise all of the key strings with indices in
                // 'keyIndices' have the same ('i'+1)th character, so they're
                // useless.
            }

            if (bestCovering == null)
            {
                bestCovering =
                    chooseBestCovering(charPartitionings, keyIndices);
            }
            Assert.check(bestCovering != null);
            writeSetFindIndexResultByCharacters(w, bestCovering,
                charPartitionings, keysList, keysArrayName, keyLength,
                keyIndices);
        }
    }

    /**
        @param charPartitionings a list of CharacterPartitionings
        @param keyIndices a subset of the set of key indices that each and
        every item in 'charPartitionings' partitions
        @return the collection of minimal covering partitions that is
        built from 'keyIndices' and one of the items in 'charPartitionings',
        and that best covers all of those indices (in the sense that it leads
        to better lookup code (hopefully) being generated)
    */
    protected CoveringPartitions
        chooseBestCovering(List charPartitionings, IntSet keyIndices)
    {
        Assert.require(charPartitionings != null);
        Assert.require(charPartitionings.isEmpty() == false);
        Assert.require(keyIndices != null);
        Assert.require(keyIndices.size() > 1);

        // I have no idea whether this is optimal or anything, but we choose
        // the partitioning that requires the largest number of partitions to
        // "cover" all of the indices in 'keyIndices' (and in the case of a
        // tie we use the one earliest in 'charPartitionings').
        CoveringPartitions result = null;
        int numIndices = keyIndices.size();
        Iterator iter = charPartitionings.iterator();
        int maxCount = 0;
        while (iter.hasNext())
        {
            CharacterPartitioning part = (CharacterPartitioning) iter.next();
            CoveringPartitions cover = part.minimalCoveringFor(keyIndices);
            int count = cover.size();
            if (count > maxCount)
            {
                result = cover;
                maxCount = count;
                if (count >= numIndices)
                {
                    Assert.check(count == numIndices);
                    break;  // while
                        // since 'numIndices' indices can't be covered by
                        // more than 'numIndices' (non-empty) partitions
                }
            }
        }

        Assert.ensure(result != null);
            // since 'charPartitionings' isn't empty and each partitioning
            // requires at least one partition to cover the non-empty set
            // 'keyIndices'
        Assert.ensure(result.size() > 1);
            // since there are at least two keys with indices in 'keyIndices'
            // and all of the keys are distinct, and so differ from each
            // other by at least one character, there must be a covering
            // corresponding to that character for those two (or any two if
            // there are more than two) keys
        return result;
    }

    /**
        Writes out the part of the "findIndex()" method's body that sets its
        result by searching those keys in 'keysList' whose indices are in
        'keyIndices', starting by examining the value they have for the
        character at the character index associated with 'bestCovering'
        (though other characters may also be examined).

        @param w the writer to use
        @param covering the covering to use to search among the keys in
        'keysList' whose indices are in 'keyIndices': it is assumed to
        cover 'keyIndices'
        @param charPartitionings all of the useful per-character
        partitionings of all of the keys in 'keysList'
        @param keysList all of the keys in the keys array, in order
        @param keysArrayName the name of the C++ array that contains (the C++
        versions of) the keys in 'keysList', in the same order
        @param keyLength the length of every key in 'keysList' whose 0-based
        index (into 'keysList') is in 'keyIndices'
        @param keyIndices the indices of the keys in 'keysList' that we're
        to write code to search among: they're the 0-based indices of a
        subset of the keys in 'keysList'
    */
    protected void writeSetFindIndexResultByCharacters(IndentWriter w,
        CoveringPartitions covering, List charPartitionings,
        StringList keysList, String keysArrayName, int keyLength,
        IntSet keyIndices)
    {
        Assert.require(w != null);
        Assert.require(covering != null);
        Assert.require(covering.size() > 1);
        Assert.require(charPartitionings != null);
        Assert.require(charPartitionings.isEmpty() == false);
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(keysArrayName != null);
        Assert.require(keyLength >= 0);
        Assert.require(keyIndices != null);
        Assert.require(keyIndices.isEmpty() == false);
        Assert.require(keyIndices.size() <= keysList.size());

        final String keyArg = KEY_VARIABLE_NAME;

        write(w, CPP_SWITCH_KEYWORD);
        write(w, " (");
        write(w, keyArg);
        write(w, ".");
        write(w, LOOKUP_KEY_CHARACTER_AT_METHOD_NAME);
        write(w, "(");
        write(w, covering.characterIndex());
        writeLine(w, "))");
        UTILITIES.writeBlockStart(w);
        try
        {
            char[] partChars = covering.partitioningCharacters();
            int numPartChars = partChars.length;
            Assert.check(numPartChars > 1);
                // since covering.size() > 1
            for (int i = 0; i < numPartChars; i++)
            {
                char ch = partChars[i];
                UTILITIES.writeCaseLabel(w, ch);

                IntSet charKeyIndices =
                    covering.findKeyIndicesInPartitionFor(ch);
                Assert.check(charKeyIndices != null);
                int sz = charKeyIndices.size();
                if (sz == 1)
                {
                    writeCompareKeyWithArrayItem(w, keysArrayName, keyLength,
                                        charKeyIndices.iterator().next());
                }
                else
                {
                    Assert.check(sz > 1);
                    CoveringPartitions bestSubcovering =
                        chooseBestCovering(charPartitionings, charKeyIndices);
                    writeSetFindIndexResultByCharacters(w, bestSubcovering,
                        charPartitionings, keysList, keysArrayName,
                        keyLength, charKeyIndices);
                }
                write(w, CPP_BREAK_KEYWORD);
                writeLine(w, ";");
            }
        }
        finally
        {
            UTILITIES.writeBlockEnd(w);
        }
    }

    /**
        Writes out the C++ code that tests that the item at index
        'arrayIndex' in the keys array named 'keysArrayName' is indeed equal
        to the lookup key (if the test is even necessary), and if so sets the
        result variable of our generated class' "findIndex()" method to
        'arrayIndex'.

        @param w the writer to use
        @param keysArrayName the name of the C++ keys array to search
        @param keyLength the size/length of the two keys to (potentially) be
        compared
        @param arrayIndex the 0-based index of the item in the keys array
        that (may be) compared to the lookup key in the generated code
    */
    protected void writeCompareKeyWithArrayItem(IndentWriter w,
        String keysArrayName, int keyLength, int arrayIndex)
    {
        Assert.require(w != null);
        Assert.require(keysArrayName != null);
        Assert.require(keyLength >= 0);
        Assert.require(arrayIndex >= 0);

        final String resultVar = RESULT_VARIABLE_NAME;
        final String keyArg = KEY_VARIABLE_NAME;

        boolean doTest = (keyLength > 0);
            // otherwise there's nothing to compare since we know that the
            // array entry and the lookup key are both empty, and hence equal
        if (doTest)
        {
            write(w, CPP_IF_START);
            write(w, "(");
            write(w, keyArg);
            write(w, ".");
            write(w, LOOKUP_KEY_IS_EQUAL_TO_METHOD_NAME);
            write(w, "(");
            write(w, keysArrayName);
            write(w, "[");
            write(w, arrayIndex);
            write(w, "], ");
            write(w, keyLength);
            writeLine(w, "))");
            UTILITIES.writeBlockStart(w);
        }
        write(w, resultVar);
        write(w, " = ");
        write(w, arrayIndex);
        writeLine(w, ";");
        if (doTest)
        {
            UTILITIES.writeBlockEnd(w);
        }
    }


    // Protected static methods

    /**
        @see CommonCodeGenerationUtilities#write(IndentWriter, String)
    */
    protected static void write(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        UTILITIES.write(w, str);
    }

    /**
        @see CommonCodeGenerationUtilities#write(IndentWriter, int)
    */
    protected static void write(IndentWriter w, int val)
    {
        Assert.require(w != null);

        UTILITIES.write(w, val);
    }

    /**
        @see CommonCodeGenerationUtilities#writeLine(IndentWriter, String)
    */
    protected static void writeLine(IndentWriter w, String str)
    {
        Assert.require(w != null);
        Assert.require(str != null);

        UTILITIES.writeLine(w, str);
    }


    // Private static methods

    /**
        @return a map from the 0-based indices of the various C++ character
        types to their C++ type names
    */
    private static String[] buildCharacterTypeNameMap()
    {
        String[] result = new String[NUM_CHAR_TYPES];

        Assert.check(NUM_CHAR_TYPES == 2);
        result[CPP_NARROW_CHAR_TYPE_INDEX] = CPP_NARROW_CHAR_TYPE;
        result[CPP_WIDE_CHAR_TYPE_INDEX] = CPP_WIDE_CHAR_TYPE;

        Assert.ensure(result != null);
        Assert.ensure(result.length == NUM_CHAR_TYPES);
        return result;
    }

    /**
        @return a map from the 0-based indices of the various C++ character
        types to the prefix to use on C++ string literals made of those types
        of characters
    */
    private static String[] buildStringLiteralPrefixMap()
    {
        String[] result = new String[NUM_CHAR_TYPES];

        Assert.check(NUM_CHAR_TYPES == 2);
        result[CPP_NARROW_CHAR_TYPE_INDEX] =
            CPP_NARROW_CHAR_STRING_LITERAL_PREFIX;
        result[CPP_WIDE_CHAR_TYPE_INDEX] =
            CPP_WIDE_CHAR_STRING_LITERAL_PREFIX;

        Assert.ensure(result != null);
        Assert.ensure(result.length == NUM_CHAR_TYPES);
        return result;
    }
}


/**
    Represents a partitioning of a subset of the keys in a keys array on
    the character they have at a specified index: all of the keys in the
    subset with the same character at that index are in the same partition.
*/
class CharacterPartitioning
{
    // Private fields

    /**
        The 0-based index of the character we contain information about for
        each candidate key string.
    */
    private int _charIndex;

    /**
        A map from each distinct character that one or more of the candidate
        key strings have at index '_charIndex' to an IntSet that contains the
        index into the keys array for each candidate key string that has that
        character at that index.
        <p>
        The keys are Characters and the values are IntSets.
    */
    private Map _charToKeyIndices;

    /**
        A map that's the inverse of our '_charToKeyIndices' map: it maps the
        index into the keys array for each candidate key string to the
        character that that key string has at index '_charIndex'.
        <p>
        The keys are ints and the values are ints that are cast chars.
    */
    private IntMap _keyIndexToChar;


    // Constructors

    /**
        Constructs a CharacterPartitioning from the list of all key strings
        and the set of the indices of all of the keys to be partitioned on
        their ('charIndex'+1)th character.

        @param keysList the list of all key strings
        @param charIndex the 0-based index of the character in each key to
        partition the candidate key strings on: all of the candidate key
        strings with the same ('charIndex'+1)th character are in the same
        partition
        @param candidateKeyIndices a set of the 0-based indices into
        'keysList' of all of the key strings that are to be included in our
        partitions
    */
    public CharacterPartitioning(StringList keysList, int charIndex,
                                 IntSet candidateKeyIndices)
    {
        Assert.require(keysList != null);
        Assert.require(keysList.isEmpty() == false);
        Assert.require(charIndex >= 0);
        Assert.require(candidateKeyIndices != null);
        Assert.require(candidateKeyIndices.isEmpty() == false);
        Assert.require(candidateKeyIndices.size() <= keysList.size());
        //Assert.require("keysList.get(i).length() is the same for every 'i' in candidateKeyIndices");
        //Assert.require("charIndex < keysList(i).length() for every 'i' in candidateKeyIndices");

        _charIndex = charIndex;

        int sz = candidateKeyIndices.size();
        _charToKeyIndices = new HashMap(sz);  // (over)estimate
        _keyIndexToChar = new IntHashMap(sz);

        IntIterator iter = candidateKeyIndices.iterator();
        while (iter.hasNext())
        {
            int keyIndex = iter.next();
            char ch = keysList.get(keyIndex).charAt(charIndex);
            Character charKey = new Character(ch);
            IntSet part = (IntSet) _charToKeyIndices.get(charKey);
            if (part == null)
            {
                part = new IntHashSet();
                _charToKeyIndices.put(charKey, part);
            }
            Assert.check(part != null);
            part.add(keyIndex);

            _keyIndexToChar.set(keyIndex, (int) ch);
        }
    }


    // Public methods

    /**
        @return the 0-based index of the character that we partition on
    */
    public int characterIndex()
    {
        Assert.ensure(_charIndex >= 0);
        return _charIndex;
    }

    /**
        @param keyIndex one of the key indices partitioned by this
        partitioning
        @return the character that defines the partition of ours that
        includes 'keyIndex'
        @exception NoSuchItemException if 'keyIndex' isn't one of the key
        indices partitioned by this partitioning
    */
    public char characterForKeyIndex(int keyIndex)
        throws NoSuchItemException
    {
        Assert.require(keyIndex >= 0);

        return (char) _keyIndexToChar.get(keyIndex);
            // this cast shouldn't lose any precision since it started out as
            // a char that we cast to an int (so that the map would hold it)
    }

    /**
        @param ch a character that defines one of our partitions
        @return all of the key indices in the partition defined by 'ch',
        or null if 'ch' isn't a character that defines one of our
        partitions
    */
    public IntSet findKeyIndicesInPartitionFor(char ch)
    {
        Character charKey = new Character(ch);

        IntSet result = (IntSet) _charToKeyIndices.get(charKey);

        // 'result' may be null
        return result;
    }

    /**
        @return the number of partitions we split the candidate key strings
        into
    */
    public int partitionCount()
    {
        int result = _charToKeyIndices.size();

        Assert.ensure(result > 0);
            // since there's at least one candidate key index (see ctor)
        return result;
    }

    /**
        @return true iff each key index in the set of candidate key indices
        that we were constructed from is in a different one of our partitions
    */
    public boolean areAllCandidatesInSeparatePartitions()
    {
        return (_charToKeyIndices.size() == _keyIndexToChar.size());
    }

    /**
        @param keyIndices a subset of the sey of candidate key indices that
        we were constructed from
        @return a minimal covering consisting of all of our partitions that
        include one or more of the indices in 'keyIndices'
    */
    public CoveringPartitions minimalCoveringFor(IntSet keyIndices)
    {
        Assert.require(keyIndices != null);

        //Assert.ensure(result != null);
        return new CoveringPartitions(this, keyIndices);
    }
}

/**
    Represents the partitions in a given CharacterPartitioning that "cover"
    a given subset of the set of keys used to construct the
    CharacterPartitioning.

    @see CharacterPartitioning
*/
class CoveringPartitions
{
    // Private fields

    /** The CharacterPartitioning whose partitions we're a collection of. */
    private CharacterPartitioning _partitioning;

    /** The set of key indices that we (minimally) cover. */
    private IntSet _keyIndices;

    /**
        An array containing all of the distinct characters that define the
        partitions in this cover, in sorted order.
    */
    private char[] _partitionChars;


    // Constructors

    /**
        Constructs CoveringPartitions from the CharacterPartitioning that it
        gets its partitions from and the subset of key indices that the
        collection of partitions is to cover.
        <p>
        The resulting collection of partitions will be minimal in that it
        won't contain any partitions that don't include at least one index
        from 'keyIndices'.

        @param part the character partitioning from which come the partitions
        that make up this collection of partitions
        @param keyIndices the set of key indices that the CoveringPartitions
        we're constructing is to cover
        @exception NoSuchItemException if 'keyIndices' isn't a subset of the
        set of key indices that 'part' partitions
    */
    public CoveringPartitions(CharacterPartitioning part, IntSet keyIndices)
        throws NoSuchItemException
    {
        Assert.require(part != null);
        Assert.require(keyIndices != null);

        _partitioning = part;
        _keyIndices = keyIndices;

        IntSet charsSet = new IntHashSet();
            // contains chars cast to ints
        IntIterator iter = keyIndices.iterator();
        while (iter.hasNext())
        {
            int ch = (int) part.characterForKeyIndex(iter.next());
            charsSet.add(ch);
        }

        int sz = charsSet.size();
        _partitionChars = new char[sz];
        iter = charsSet.iterator();
        for (int i = 0; i < sz; i++)
        {
            Assert.check(iter.hasNext());
            _partitionChars[i] = ((char) iter.next());
        }
        Arrays.sort(_partitionChars);
    }


    // Public methods

    /**
        @return the number of partitions we have
    */
    public int size()
    {
        //Assert.ensure(result >= 0);
        return _partitionChars.length;
    }

    /**
        @return the 0-based index of the character that our partitioning
        partitions on
        @see CharacterPartitioning#characterIndex
    */
    public int characterIndex()
    {
        int result = _partitioning.characterIndex();

        Assert.ensure(result >= 0);
        return result;
    }


    /**
        @return an array containing all of the distinct characters that
        define the partitions in this cover, in sorted order
    */
    public char[] partitioningCharacters()
    {
        char[] result = _partitionChars;

        Assert.ensure(result != null);
        Assert.ensure(result.length == size());
        return result;
    }

    /**
        @param ch one of our partitioning characters
        @return a set of all of the key indices in the set of indices that
        we cover that are in the partition of ours corresponding to 'ch', or
        null if we don't have a partition corresponding to 'ch'
        @see #partitioningCharacters
    */
    public IntSet findKeyIndicesInPartitionFor(char ch)
    {
        IntSet result = null;

        IntSet all = _partitioning.findKeyIndicesInPartitionFor(ch);
        if (all != null)
        {
            result = new IntHashSet();
            IntIterator iter = all.iterator();
            while (iter.hasNext())
            {
                int keyIndex = iter.next();
                if (_keyIndices.has(keyIndex))
                {
                    result.add(keyIndex);
                }
            }

            if (result.isEmpty())
            {
                // 'ch' is a partitioning character for '_partitioning' but
                // not for us.
                result = null;
            }
        }

        // 'result' may be null
        Assert.ensure(result == null || result.isEmpty() == false);
        return result;
    }
}
