/*
 Copyright (C) 2004 by James MacKay.

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

package com.steelcandy.plack.common.generation.java;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.errors.ErrorHandler;
import com.steelcandy.plack.common.generation.AbstractTypeListEncoder;
import com.steelcandy.plack.common.generation.CommonCodeGenerationUtilities;

import com.steelcandy.common.Resources;
import com.steelcandy.common.text.StringIterator;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
    The default class of TypeListEncoder that encodes list of Types in such
    a way that they are valid parts of Java identifiers.

    @author  James MacKay
    @version $Revision: 1.1 $
*/
public class DefaultJavaTypeListEncoder
    extends AbstractTypeListEncoder
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        CommonJavaGenerationResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        COULD_NOT_ENCODE_GENERIC_ACTUALS_MSG =
            "COULD_NOT_ENCODE_GENERIC_ACTUALS_MSG";


    /** The bytes used to separate global type names. */
    private static final byte[]
        SEPARATOR_BYTES = " ".getBytes();

    /** The name of the algorithm by MessageDigests to encode type names. */
    private static final String
        ENCODING_DIGEST_ALGORITHM_NAME = "SHA-1";

    /** The radix to use in encoding the type names. */
    private static final int
        ENCODING_RADIX = 36;


    // Private fields

    /** The message digest to use to encode type lists. */
    private MessageDigest _encodingDigest;


    // Constructors

    /**
        Constructs a DefaultJavaTypeListEncoder.
    */
    public DefaultJavaTypeListEncoder()
    {
        _encodingDigest = null;  // see encodingMessageDigest()
    }


    // Protected methods

    /**
        @see AbstractTypeListEncoder#encode(StringIterator, int, ErrorHandler)
    */
    protected String
        encode(StringIterator globalTypeNamesIter, int numNames,
               ErrorHandler handler)
    {
        Assert.require(globalTypeNamesIter != null);
        Assert.require(numNames >= 0);
        Assert.require(handler != null);

//StringBuffer typesBuf = new StringBuffer();

        String result;

        if (numNames == 0)
        {
            // An empty list of types is encoded as an empty string.
            result = "";
        }
        else  // numNames > 0
        {
//typesBuf.append("(");
            Assert.check(globalTypeNamesIter.hasNext());

            // See http://java.sun.com/developer/qow/archive/28/
            MessageDigest digest = encodingMessageDigest(handler);

            // We separate the types' names in order to avoid run-together
            // names matching other run-together names (which is unlikely,
            // but possible).
            while (globalTypeNamesIter.hasNext())
            {
                String name = globalTypeNamesIter.next();
//typesBuf.append(name);
                digest.update(name.getBytes());
                if (globalTypeNamesIter.hasNext())
                {
//typesBuf.append(", ");
                    digest.update(SEPARATOR_BYTES);
                }
            }
//typesBuf.append(")");
            // We convert the digest to a somewhat compressed form that is
            // still valid in a Java class name by converting it to a
            // BigInteger, then getting its base-36 representation (which
            // consists of uppercase (?) letters and numbers, once we handle
            // its sign).
            BigInteger i = new BigInteger(digest.digest());
            result = i.abs().toString(ENCODING_RADIX);
            if (i.signum() >= 0)
            {
                result = "1" + result;
            }
            else
            {
                result = "0" + result;
            }
        }

//if (typesBuf.length() > 0)
//{
//System.err.println("type-encode:" + result + " -> " + typesBuf.toString());
//}
        Assert.ensure(result != null);
        return result;
    }


    // Private methods

    /**
        Note: this method calls reset() on the digest just before it returns
        it.

        @param handler the error handler to use to handle any errors that
        occur in trying to obtain the message digest
        @return the MessageDigest to use in encoding generic actuals so that
        they can appear in Java class names.
    */
    private MessageDigest encodingMessageDigest(ErrorHandler handler)
    {
        Assert.require(handler != null);

        if (_encodingDigest == null)
        {
            try
            {
                _encodingDigest = MessageDigest.
                    getInstance(ENCODING_DIGEST_ALGORITHM_NAME);
            }
            catch (NoSuchAlgorithmException ex)
            {
                String msg = _resources.
                    getMessage(COULD_NOT_ENCODE_GENERIC_ACTUALS_MSG,
                               ENCODING_DIGEST_ALGORITHM_NAME,
                               ex.getLocalizedMessage());
                CommonCodeGenerationUtilities.
                    handleError(FATAL_ERROR_LEVEL, msg, handler);
                Assert.unreachable();
                    // since this is a fatal error
            }
        }
        _encodingDigest.reset();

        Assert.ensure(_encodingDigest != null);
        return _encodingDigest;
    }
}
