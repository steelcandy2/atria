/*
 Copyright (C) 2004-2012 by James MacKay.

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

package com.steelcandy.plack.common.semantic;

import com.steelcandy.common.debug.Assert;

import com.steelcandy.plack.common.source.*;
import com.steelcandy.plack.common.errors.*;
import com.steelcandy.plack.common.constructs.Construct;

import com.steelcandy.common.Resources;

/**
    An abstract base class for classes that can check the constraints on a
    language's name/identifier constructs, as well as test name/identifier
    constructs to see if they're valid.
    <p>
    Subclasses only have to implement the abstract methods declared in this
    class, though if the subclass' language has different idea than does Java
    about the types of characters (e.g. whether a character is a lowercase
    letter) then it should override methods that test the type of a character
    (such as isLowercaseLetter()).
    <p>
    Note: add other checking and testing methods to this class as they are
    needed.

    @author James MacKay
    @version $Revision: 1.8 $
    @see #isLowercaseLetter(char)
*/
public abstract class AbstractNameChecker
    implements ErrorSeverityLevels
{
    // Constants

    /** The resources used by this class and its subclasses. */
    protected static final Resources _resources =
        CommonSemanticResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NAME_TOO_SHORT_MSG =
            "NAME_TOO_SHORT_MSG",
        WRONG_NAME_MSG =
            "WRONG_NAME_MSG",
        NAME_DOES_NOT_START_WITH_LETTER_MSG =
            "NAME_DOES_NOT_START_WITH_LETTER_MSG",
        NAME_NOT_ALL_LOWERCASE_LETTERS_MSG =
            "NAME_NOT_ALL_LOWERCASE_LETTERS_MSG",
        NAME_DOES_NOT_START_WITH_LOWERCASE_LETTER_MSG =
            "NAME_DOES_NOT_START_WITH_LOWERCASE_LETTER_MSG",
        SECOND_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG =
            "SECOND_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG",
        THIRD_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG =
            "THIRD_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG",
        NAME_DOES_NOT_START_WITH_UPPERCASE_LETTER_MSG =
            "NAME_DOES_NOT_START_WITH_UPPERCASE_LETTER_MSG",
        SECOND_NAME_CHARACTER_NOT_UPPERCASE_LETTER_MSG =
            "SECOND_NAME_CHARACTER_NOT_UPPERCASE_LETTER_MSG",
        NAME_DOES_NOT_START_WITH_UNDERSCORE_MSG =
            "NAME_DOES_NOT_START_WITH_UNDERSCORE_MSG",
        NAME_DOES_NOT_END_WITH_ALPHANUMERIC_MSG =
            "NAME_DOES_NOT_END_WITH_ALPHANUMERIC_MSG",
        NON_UPPERCASE_ALPHANUMERIC_IN_REST_MSG =
            "NON_UPPERCASE_ALPHANUMERIC_IN_REST_MSG",
        NAME_DOES_NOT_END_WITH_EXCLAMATION_MARK_MSG =
            "NAME_DOES_NOT_END_WITH_EXCLAMATION_MARK_MSG";


    // Testing methods

    /**
        Indicates whether the specified name is at least the specified number
        of characters long.

        @param name the name to test
        @param minimumLength the minimum number of characters that 'name'
        must have if it is to pass this test
        @return true iff 'name' is at least 'minimumLength' characters long
        @see #checkMinimumLength(String, Construct, int, String, int, ErrorHandler)
    */
    protected boolean hasMinimumLength(String name, int minimumLength)
    {
        Assert.require(name != null);
        Assert.require(minimumLength >= 0);

        return name.length() >= minimumLength;
    }

    /**
        Indicates whether the first letter of the specified name is a letter
        of any (or even no) case.

        @param name the name to test
        @return true iff 'name' starts with a letter
        @see #checkFirstLetter(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isFirstLetter(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char first = name.charAt(0);
        return isLetter(first);
    }

    /**
        Indicates whether all of the characters in the specified name that
        are at or after the specified index are alphanumeric.

        @param name the name to check
        @param startIndex the index in 'name' to start checking
        @return true iff all of the characters in 'name' at or after
        index 'startIndex' are alphanumeric
    */
    protected boolean isRestAlphanumeric(String name, int startIndex)
    {
        Assert.require(name != null);
        Assert.require(startIndex >= 0);

        boolean result = true;

        int length = name.length();
        for (int i = startIndex; i < length; i++)
        {
            char ch = name.charAt(i);
            if (isAlphanumeric(ch) == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
        Indicates whether all of the characters in the specified name are
        lowercase letters.

        @param name the name to check
        @return true iff all of the characters in 'name' are lowercase
        letters
        @see #checkAllLowercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isAllLowercase(String name)
    {
        Assert.require(name != null);

        boolean result = true;

        int length = name.length();
        for (int i = 0; i < length; i++)
        {
            char ch = name.charAt(i);
            if (isLowercaseLetter(ch) == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
        Indicates whether the first letter of the specified name is a
        lowercase letter.

        @param name the name to test
        @return true iff 'name' starts with a lowercase letter
        @see #checkFirstLowercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isFirstLowercase(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char first = name.charAt(0);
        return isLowercaseLetter(first);
    }

    /**
        Indicates whether the second letter of the specified name is a
        lowercase letter.

        @param name the name to test
        @return true iff the second character of 'name' is a lowercase
        letter
        @see #checkSecondLowercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isSecondLowercase(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() >= 2);

        char second = name.charAt(1);
        return isLowercaseLetter(second);
    }

    /**
        Indicates whether the third letter of the specified name is a
        lowercase letter.

        @param name the name to test
        @return true iff the third character of 'name' is a lowercase letter
        @see #checkThirdLowercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isThirdLowercase(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() >= 2);

        char third = name.charAt(2);
        return isLowercaseLetter(third);
    }

    /**
        Indicates whether the first letter of the specified name is an
        uppercase letter.

        @param name the name to test
        @return true iff 'name' starts with an uppercase letter
        @see #checkFirstUppercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isFirstUppercase(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char first = name.charAt(0);
        return isUppercaseLetter(first);
    }

    /**
        Indicates whether the second letter of the specified name is an
        uppercase letter.

        @param name the name to test
        @return true iff the second character of 'name' is an uppercase
        letter
        @see #checkSecondUppercase(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isSecondUppercase(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() >= 2);

        char second = name.charAt(1);
        return isUppercaseLetter(second);
    }

    /**
        Indicates whether the specified name starts with an underscore.

        @param name the name to test
        @return true iff 'name' starts with an underscore
        @see #checkFirstUnderscore(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isFirstUnderscore(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char first = name.charAt(0);
        return isUnderscore(first);
    }

    /**
        Indicates whether the specified name ends with an underscore.

        @param name the name to check
        @return true iff 'name' ends with an underscore
    */
    protected boolean isLastUnderscore(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char last = getLastChar(name);
        return isUnderscore(last);
    }

    /**
        Indicates whether the specified name ends with an alphanumeric
        character.

        @param name the name to check
        @return true iff 'name' ends with an alphanumeric character
        @see #checkLastAlphanumeric(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isLastAlphanumeric(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char last = getLastChar(name);
        return isAlphanumeric(last);
    }

    /**
        Indicates whether all of the characters in the specified name from
        the specified index to the end of the name are all uppercase
        alphanumeric characters.

        @param name the name to test
        @return true iff the rest of 'name' consists of uppercase
        alphanumeric characters
        @see #checkRestUppercaseAlphanumeric(String, Construct, int, String, int, ErrorHandler)
    */
    protected boolean
        isRestUppercaseAlphanumeric(String name, int startIndex)
    {
        Assert.require(name != null);
        Assert.require(startIndex >= 0);

        boolean result = true;

        int length = name.length();
        for (int i = startIndex; i < length; i++)
        {
            char ch = name.charAt(i);
            if (isUppercaseAlphanumeric(ch) == false)
            {
                result = false;
                break;
            }
        }

        return result;
    }

    /**
        Indicates whether the specified name ends with an exclamation mark.

        @param name the name to check
        @return true iff 'name' ends with an exclamation mark
        @see #checkLastExclamationMark(String, Construct, String, int, ErrorHandler)
    */
    protected boolean isLastExclamationMark(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        char last = getLastChar(name);
        return isExclamationMark(last);
    }


    // Checking methods

    /**
        Checks that the specified name is at least the specified number of
        characters long.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param minimumLength the minimum number of characters that 'name'
        can have if it is to satisfy the constraint that this check is being
        used to check (at least partially)
        @param constraintName the name of the constraint that requires that
        the name have the minimum length
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not being
        long enough
        @return true iff 'name' is at least 'minimumLength' characters long
        @see #hasMinimumLength(String, int)
    */
    protected boolean checkMinimumLength(String name, Construct c,
        int minimumLength, String constraintName, int errorKeyId,
        ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(minimumLength >= 0);
        Assert.require(handler != null);

        boolean result = true;

        if (name != null)
        {
            int length = name.length();
            if (length < minimumLength)
            {
                result = false;
                Object[] args =
                {
                    name,
                    description(c),
                    String.valueOf(length),
                    String.valueOf(minimumLength)
                };
                String msg = _resources.getMessage(NAME_TOO_SHORT_MSG, args);
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }

        return result;
    }

    /**
        Checks that the specified name is the same as the specified value.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param value the value that 'name' must be equal to if it is to
        satisfy the constraint that this check is being used to check (at
        least partially)
        @param constraintName the name of the constraint that requires that
        the name be equal to 'value'
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not being
        equal to 'value'
        @see #areNamesEqual(String, String)
    */
    protected void checkNameEquals(String name, Construct c, String value,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(c != null);
        Assert.require(value != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null && areNamesEqual(name, value) == false)
        {
            String msg = _resources.
                getMessage(WRONG_NAME_MSG, name, description(c), value);
            violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                               msg, c, errorKeyId, handler);
        }
    }

    /**
        Checks that the specified name starts with a letter of any (or even
        no) case.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name start with a letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with a letter
        @see #isFirstLetter(String)
    */
    protected void checkFirstLetter(String name, Construct c,
        String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char first = name.charAt(0);
            if (isLetter(first) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_START_WITH_LETTER_MSG,
                        name, description(c), String.valueOf(first));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that all of the characters in the specified name are lowercase
        letters.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name consist entirely of lowercase letters
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        consisting entirely of lowercase letters
        @see #isAllLowercase(String)
    */
    protected void checkAllLowercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            int length = name.length();
            for (int i = 0; i < length; i++)
            {
                char ch = name.charAt(i);
                if (isLowercaseLetter(ch) == false)
                {
                    String msg = _resources.
                        getMessage(NAME_NOT_ALL_LOWERCASE_LETTERS_MSG,
                            name, description(c));
                    violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                       msg, c, errorKeyId, handler);
                    break;  // for
                }
            }
        }
    }

    /**
        Checks that the specified name starts with a lowercase letter.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name start with a lowercase letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with a lowercase letter
        @see #isFirstLowercase(String)
    */
    protected void checkFirstLowercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char first = name.charAt(0);
            if (isLowercaseLetter(first) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_START_WITH_LOWERCASE_LETTER_MSG,
                        name, description(c), String.valueOf(first));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name's second character is a lowercase
        letter.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the second character of the name be a lowercase letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not having
        a lowercase letter as its second character
        @see #isSecondLowercase(String)
    */
    protected void checkSecondLowercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() >= 2);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char second = name.charAt(1);
            if (isLowercaseLetter(second) == false)
            {
                String msg = _resources.
                    getMessage(SECOND_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG,
                        name, description(c), String.valueOf(second));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name's third character is a lowercase
        letter.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the third character of the name be a lowercase letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not having
        a lowercase letter as its third character
        @see #isThirdLowercase(String)
    */
    protected void checkThirdLowercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() >= 3);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char third = name.charAt(2);
            if (isLowercaseLetter(third) == false)
            {
                String msg = _resources.
                    getMessage(THIRD_NAME_CHARACTER_NOT_LOWERCASE_LETTER_MSG,
                        name, description(c), String.valueOf(third));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name starts with a uppercase letter.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name start with an uppercase letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with an uppercase letter
        @see #isFirstUppercase(String)
    */
    protected void checkFirstUppercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char first = name.charAt(0);
            if (isUppercaseLetter(first) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_START_WITH_UPPERCASE_LETTER_MSG,
                        name, description(c), String.valueOf(first));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name's second character is an uppercase
        letter.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the second character of the name be an uppercase letter
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not having
        an uppercase letter as its second character
        @see #isSecondUppercase(String)
    */
    protected void checkSecondUppercase(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() >= 2);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char second = name.charAt(1);
            if (isUppercaseLetter(second) == false)
            {
                String msg = _resources.
                    getMessage(SECOND_NAME_CHARACTER_NOT_UPPERCASE_LETTER_MSG,
                        name, description(c), String.valueOf(second));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name starts with an underscore.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name start with an underscore
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with an underscore
        @see #isFirstUnderscore(String)
    */
    protected void checkFirstUnderscore(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char first = name.charAt(0);
            if (isUnderscore(first) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_START_WITH_UNDERSCORE_MSG,
                        name, description(c), String.valueOf(first));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that the specified name ends with an alphanumeric character.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name end with an alphanumeric
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with an alphanumeric
        @see #isLastAlphanumeric(String)
    */
    protected void checkLastAlphanumeric(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char last = getLastChar(name);
            if (isAlphanumeric(last) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_END_WITH_ALPHANUMERIC_MSG,
                        name, description(c), String.valueOf(last));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }

    /**
        Checks that all of the characters in the specified name from the
        specified index to the end of the name are all uppercase alphanumeric
        characters.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the rest of the name consist of uppercase alphanumeric characters
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report that one or more of
        the characters in the rest of name is not uppercase alphanumeric
        @see #isRestUppercaseAlphanumeric(String, int)
    */
    protected void checkRestUppercaseAlphanumeric(String name, Construct c,
        int startIndex, String constraintName, int errorKeyId,
        ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(c != null);
        Assert.require(startIndex >= 0);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            int length = name.length();
            for (int i = startIndex; i < length; i++)
            {
                char ch = name.charAt(i);
                if (isUppercaseAlphanumeric(ch) == false)
                {
                    Object[] args =
                    {
                        name,
                        description(c),
                        String.valueOf(ch),
                        String.valueOf(i + 1)  // position, not index
                    };
                    String msg = _resources.
                        getMessage(NON_UPPERCASE_ALPHANUMERIC_IN_REST_MSG,
                                   args);
                    violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                       msg, c, errorKeyId, handler);
                    break;  // for
                }
            }
        }
    }

    /**
        Checks that the specified name ends with an exclamation mark.
        <p>
        Note: 'name' will not be checked if it is null: it is assumed to be
        null as a result of a previous (and reported) error.

        @param name the name to check
        @param c the construct that represents the name
        @param constraintName the name of the constraint that requires that
        the name end with an exclamation mark
        @param errorKeyId the error key ID to use to construct the key for
        the error reported if the constraint is violated: no error key will
        be used if this argument is CommonErrorKeyIds.NON_ERROR_KEY_ID
        @param handler the error handler to use to report the name not
        starting with an exclamation mark
        @see #isLastExclamationMark(String)
    */
    protected void checkLastExclamationMark(String name, Construct c,
                String constraintName, int errorKeyId, ErrorHandler handler)
    {
        // 'name' may be null
        Assert.require(name == null || name.length() > 0);
        Assert.require(c != null);
        Assert.require(constraintName != null);
        Assert.require(handler != null);

        if (name != null)
        {
            char last = getLastChar(name);
            if (isExclamationMark(last) == false)
            {
                String msg = _resources.
                    getMessage(NAME_DOES_NOT_END_WITH_EXCLAMATION_MARK_MSG,
                        name, description(c), String.valueOf(last));
                violatedConstraint(NON_FATAL_ERROR_LEVEL, constraintName,
                                   msg, c, errorKeyId, handler);
            }
        }
    }


    // Protected methods

    /**
        @see CommonConstraintChecks#violatedConstraint(int, String, String, Construct, ErrorHandler)
        @see CommonConstraintChecks#violatedConstraint(int, String, String, Construct, ErrorKey, ErrorHandler)
    */
    protected void violatedConstraint(int level, String constraintName,
        String description, Construct c, int errorKeyId, ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        Assert.require(c != null);
        Assert.require(c.hasCorrectnessData());
        Assert.require(handler != null);

        ErrorKey key = errorKey(errorKeyId);
        if (key != null)
        {
            CommonConstraintChecks.violatedConstraint(level, constraintName,
                                            description, c, key, handler);

        }
        else
        {
            CommonConstraintChecks.violatedConstraint(level, constraintName,
                                                    description, c, handler);
        }
    }

    /**
        @see CommonConstraintChecks#violatedConstraint(int, String, String, SourceCode, SourceLocation, ErrorHandler)
        @see CommonConstraintChecks#violatedConstraint(int, String, String, SourceCode, SourceLocation, ErrorKey, ErrorHandler)
    */
    protected void violatedConstraint(int level, String constraintName,
        String description, SourceCode code, SourceLocation loc,
        int errorKeyId, ErrorHandler handler)
    {
        Assert.require(constraintName != null);
        Assert.require(description != null);
        // 'code' and/or 'loc' may be null
        Assert.require(handler != null);

        ErrorKey key = errorKey(errorKeyId);
        if (key != null)
        {
            CommonConstraintChecks.violatedConstraint(level, constraintName,
                                    description, code, loc, key, handler);
        }
        else
        {
            CommonConstraintChecks.violatedConstraint(level, constraintName,
                                            description, code, loc, handler);
        }
    }

    /**
        Creates and returns the error key corresponding to the specified
        error key ID, or returns null if no error key is to be created for
        the ID.

        @param errorKeyId the error key ID
        @return an error key with ID 'errorKeyId', or null if no error key
        is to be created (including if 'errorKeyId' is 'NON_ERROR_KEY_ID')
        @see CommonErrorKeyIds#NON_ERROR_KEY_ID
        @see #createErrorKey(int)
    */
    protected ErrorKey errorKey(int errorKeyId)
    {
        ErrorKey result = null;

        if (errorKeyId != CommonErrorKeyIds.NON_ERROR_KEY_ID)
        {
            result = createErrorKey(errorKeyId);
        }

        // 'result' may be null
        return result;
    }

    /**
        @param name the name to return the last character of
        @return the last character of the name
    */
    protected char getLastChar(String name)
    {
        Assert.require(name != null);
        Assert.require(name.length() > 0);

        return name.charAt(name.length() - 1);
    }


    // Character type testing methods

    /*
        Note: this class' implementations of all of these methods use Java's
        classifications of the types of characters. Subclasses for
        languages with different classifications should override some or all
        of these methods.
    */

    /**
        Indicates whether the language considers the specified names to be
        the same.
        <p>
        This implementation assumes that two names are the same if the first
        equals() the second.

        @param name1 the first name
        @param name2 the second name
        @return true iff the language considers 'name1' and 'name2' to be
        the same
        @see Object#equals(Object)
    */
    protected boolean areNamesEqual(String name1, String name2)
    {
        Assert.require(name1 != null);
        Assert.require(name2 != null);

        return name1.equals(name2);
    }

    /**
        Indicates whether the specified character is considered to be a
        letter (of any (or even no) case) according to the language.
        <p>
        This implementation assumes that a character is a letter iff Java
        thinks it is a letter.

        @param ch the character to test
        @return true iff the language considers 'ch' a letter
    */
    protected boolean isLetter(char ch)
    {
        return Character.isLetter(ch);
    }

    /**
        Indicates whether the specified character is considered to be a
        lowercase letter according to the language.
        <p>
        This implementation assumes that a character is a lowercase letter
        iff Java thinks it is a lowercase letter.

        @param ch the character to test
        @return true iff the language considers 'ch' a lowercase letter
    */
    protected boolean isLowercaseLetter(char ch)
    {
        return Character.isLowerCase(ch);
    }

    /**
        Indicates whether the specified character is considered to be an
        uppercase letter according to the language.
        <p>
        This implementation assumes that a character is an uppercase letter
        iff Java thinks it is an uppercase letter.

        @param ch the character to test
        @return true iff the language considers 'ch' an uppercase letter
    */
    protected boolean isUppercaseLetter(char ch)
    {
        return Character.isUpperCase(ch);
    }

    /**
        Indicates whether the specified character is considered to be an
        underscore according to the language.
        <p>
        This implementation assumes that a character is an underscore iff
        it is '_'.

        @param ch the character to test
        @return true iff the language considers 'ch' an underscore
    */
    protected boolean isUnderscore(char ch)
    {
        return (ch == '_');
    }

    /**
        Indicates whether the specified character is considered to be an
        alphanumeric character according to the language.
        <p>
        This implementation assumes that a character is an alphanumeric
        character iff Java thinks it is a letter (of any (or even no) case)
        or a digit.

        @param ch the character to test
        @return true iff the language considers 'ch' an alphanumeric
        character
    */
    protected boolean isAlphanumeric(char ch)
    {
        return Character.isLetterOrDigit(ch);
    }

    /**
        Indicates whether the specified character is considered to be an
        uppercase alphanumeric character according to the language.
        <p>
        This implementation assumes that a character is an uppercase
        alphanumeric character iff Java thinks it is an uppercase letter or a
        digit.
    */
    protected boolean isUppercaseAlphanumeric(char ch)
    {
        return Character.isUpperCase(ch) || Character.isDigit(ch);
    }

    /**
        Indicates whether the specified character is considered to be an
        exclamation mark according to the language.
        <p>
        This implementation assumes that a character is an exclamation mark
        iff it is '!'.

        @param ch the character to test
        @return true iff the language considers 'ch' an exclamation mark
    */
    protected boolean isExclamationMark(char ch)
    {
        return (ch == '!');
    }


    // Abstract methods

    /**
        Returns a description of the type of the specified construct.

        @param c the construct whose type's description is to be returned
        @return the construct's type's description
    */
    protected abstract String description(Construct c);
        // Assert.require(c != null);

    /**
        Creates and returns the error key corresponding to the specified
        error key ID, or returns null if no error key is to be created for
        the ID.

        @param errorKeyId the error key ID
        @return an error key with ID 'errorKeyId', or null if no error key
        is to be created
        @see #errorKey(int)
    */
    protected abstract ErrorKey createErrorKey(int errorKeyId);
        // Assert.require(errorKeyId != CommonErrorKeyIds.NON_ERROR_KEY_ID);
        // 'result' may be null
}
