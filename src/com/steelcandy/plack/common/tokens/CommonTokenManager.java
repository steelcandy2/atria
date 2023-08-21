/*
 Copyright (C) 2001-2010 by James MacKay.

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

package com.steelcandy.plack.common.tokens;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.SourceLocation;
import com.steelcandy.plack.common.source.SourceLocationFactory;
import com.steelcandy.plack.common.source.SourcePosition;

import com.steelcandy.common.Resources;

/**
    The abstract base class for all token manager classes that create and
    manage tokens for a given language.

    @author James MacKay
*/
public abstract class CommonTokenManager
    implements OperatorConstants
{
    // Constants

    /** The number of flags used by this class. */
    public static final int COMMON_NUMBER_OF_USED_FLAGS = 0;

    /** The first token flag available for use by subclasses. */
    public static final int COMMON_FIRST_AVAILABLE_FLAG =
        1 << COMMON_NUMBER_OF_USED_FLAGS;

    /** The first token ID available for use by subclasses. */
    public static final int COMMON_FIRST_AVAILABLE_ID = 0;


    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        UNEXPECTED_TOKEN_ID_MSG =
            "UNEXPECTED_TOKEN_ID_MSG",
        UNEXPECTED_FLAG_MSG =
            "UNEXPECTED_FLAG_MSG",
        INVALID_RESERVED_WORD_MSG =
            "INVALID_RESERVED_WORD_MSG";


    // Public methods

    /**
        Indicates whether the specified string is a reserved word.
        <p>
        Subclasses that override this method should call their superclass'
        version if they don't recognize a string as a reserved word.

        @param str the string to test
        @return true iff the specified string is a reserved word
    */
    public boolean isReservedWord(String str)
    {
        return false;
    }

    /**
        Returns the location of the fragment of source code represented by
        the specified list of tokens. The tokens are assumed to represent (a
        part of) a single language construct.

        @param list the list of tokens
        @return the location represented by all of the tokens in the list
    */
    public SourceLocation location(TokenList list)
    {
        Assert.require(list != null);

        return locationFactory().create(AbstractTokenizer.locations(list));
    }


    // Token creation methods

    /**
        Creates a token representing the reserved word corresponding to the
        specified token ID.
        <p>
        Subclasses that override this method should call their superclass'
        version when the token ID is less than all of the token IDs that it
        defines.

        @param tokenId the token ID corresponding to the reserved word for
        which a token is to be created
    */
    public Token createReservedWordToken(SourcePosition startPos,
                                         int length, int tokenId)
    {
        handleUnexpectedTokenId(tokenId);

        return null;  // this should never be reached
    }

    /**
        Creates a token representing the specified reserved word.
        <p>
        Subclasses that override this method should call their superclass'
        version when they don't recognize the reserved word.

        @param reservedWord the reserved word that the token is to represent
    */
    public Token createReservedWordToken(SourcePosition startPos,
                                         int length, String reservedWord)
    {
        Assert.require(isReservedWord(reservedWord));

        String msg = _resources.getMessage(INVALID_RESERVED_WORD_MSG,
                                           reservedWord);
        throw new IllegalArgumentException(msg);
    }


    // Token ID-related methods

    /**
        Returns the description of the type of token with the specified token
        ID.
        <p>
        Subclasses that override this method should call their superclass'
        version for those IDs less than all of the token IDs that they
        define.

        @param tokenId the token ID for the type of token whose description
        is to be returned
        @return the description of the type of token with the specified token
        ID
    */
    public String idToDescription(int tokenId)
    {
        handleUnexpectedTokenId(tokenId);

        return null;  // this should never be reached
    }

    /**
        Returns the name of the token ID constant whose value is the
        specified token ID.
        <p>
        Subclasses that override this method should call their superclass'
        version for those IDs less than all of the token IDs that they
        define.

        @param tokenId the token ID for which the corresponding constant
        name is to be returned
        @return the name of the token ID constant whose value is 'tokenId'
    */
    public String idToConstantName(int tokenId)
    {
        handleUnexpectedTokenId(tokenId);

        return null;  // this should never be reached
    }

    /**
        Returns the name of the method corresponding to operator tokens
        with the specified token ID, or null if the specified token ID is
        not that of an operator token with a corresponding method.

        @param tokenId a token ID
        @return the name of the method corresponding to operator tokens
        with token ID 'tokenId', or null if 'tokenId' is either not an
        operator token ID or is an operator token ID for operator tokens
        that do not have methods corresponding to them
    */
    public String operatorIdToMethodName(int tokenId)
    {
        // 'result' may be null
        return null;
    }

    /**
        @param name a name (usually a method or routine name, but that isn't
        mandatory)
        @return true iff 'name' is the the name of a method corresponding to
        one or more operator tokens
    */
    public boolean isOperatorMethodName(String name)
    {
        Assert.require(name != null);

        return false;
    }

    /**
        Handles the specified unexpected token ID.
        <p>
        A token ID is usually reported as unexpected because it was passed to
        one of this class' method implementations when a subclass'
        implementation should have handled it.

        @param tokenId the unexpected token ID
        @exception IllegalArgumentException thrown as part of handling the
        unexpected token ID
    */
    private void handleUnexpectedTokenId(int tokenId)
    {
        String msg = _resources.getMessage(UNEXPECTED_TOKEN_ID_MSG,
                                           Integer.toString(tokenId));
        throw new IllegalArgumentException(msg);
    }


    // Token flag-related methods

    /**
        Returns the name of the token flag constant whose value is the
        specified token flag.
        <p>
        Subclasses that override this method should call their superclass'
        version when the flag is less than any flag that they define.

        @param flag the token flag for which the name of the corresponding
        token flag constant is to be returned
        @return the name of the token flag constant whose value is 'flag'
    */
    public String flagToConstantName(int flag)
    {
        handleUnexpectedFlag(flag);

        return null;  // this should never be reached
    }


    /**
        Returns a comma-separated list of the token flag constant names
        corresponding to the flags that the specified token has set. (So
        an empty string will be returned iff the token has no flags set.)

        @param tok the token whose flags' corresponding constant names are
        to be in the returned string
        @return a comma-separated list of the token flag constant names for
        each of the flags that 'tok' has set
    */
    public String flagsToConstantNames(Token tok)
    {
        StringBuffer result = new StringBuffer();
        int largestFlag = largestFlag();
        for (int i = 0, flag = 1;
             flag <= largestFlag; i += 1, flag = 1 << i)
        {
            if (tok.isFlagSet(flag))
            {
                if (result.length() > 0)
                {
                    result.append(", ");
                }

                result.append(flagToConstantName(flag));
            }
        }

        Assert.ensure(result != null);
        return result.toString();
    }

    /**
        Handles the specified unexpected token flag.
        <p>
        A token flag is usually reported as unexpected because it was passed
        to one of this class' method implementations when a subclass'
        implementation should have handled it.

        @param flag the unexpected flag
        @exception IllegalArgumentException thrown as part of handling
        the unexpected flag
    */
    private void handleUnexpectedFlag(int flag)
    {
        String msg = _resources.getMessage(UNEXPECTED_FLAG_MSG,
                                           Integer.toString(flag));
        throw new IllegalArgumentException(msg);
    }


    // Protected methods

    /**
        Returns the flag with the largest value that is defined in this class
        or any superclass.

        @return the flag that has the largest value and was defined in this
        class or a superclass
    */
    protected int largestFlag()
    {
        int result = 0;
        if (COMMON_NUMBER_OF_USED_FLAGS > 0)
        {
            result = 1 << (COMMON_NUMBER_OF_USED_FLAGS - 1);
        }
        return result;
    }


    // Utility methods

    /**
        Creates a token representing the source fragment with the specified
        starting position and length, and that is otherwise a clone of the
        specified prototype token.

        @param startPos the position in the source code of the first
        character in the source fragment that the token is to represent
        @param length the length, in characters, of the source fragment that
        the token is to represent
        @param prototype the token to use as a prototype for the one that
        this method creates
        @return a token that is a clone of the prototype token and that
        represents the source fragment at the specified location
    */
    protected Token createToken(SourcePosition startPos,
                                int length, Token prototype)
    {
        Assert.require(prototype != null);

        return prototype.cloneToken(createLocation(startPos, length));
    }

    /**
        Creates and returns a SourceLocation given its starting position and
        length.

        @param startPos the starting position of the location
        @param length the length of the location
        @return the location with the specified starting position and length
    */
    protected SourceLocation
        createLocation(SourcePosition startPos, int length)
    {
        return locationFactory().create(startPos, length);
    }


    // Abstract methods

    /**
        @return the SourceLocationFactory that methods in this class should
        use to create SourceLocations
    */
    public abstract SourceLocationFactory locationFactory();
        // Assert.ensure(result != null);
}
