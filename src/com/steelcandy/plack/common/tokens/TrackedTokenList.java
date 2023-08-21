/*
 Copyright (C) 2002-2008 by James MacKay.

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

import com.steelcandy.common.containers.TypedList;  // javadocs only
import com.steelcandy.common.NoSuchItemException;
import com.steelcandy.common.Resources;

import java.util.ArrayList;
import java.util.List;

/**
    Represents a list of tokens that tokens can - at least apparently - have
    tokens removed from the front of it, but that also allows those tokens to
    be accessed after they've been 'removed'.
    <p>
    The 'regular' TokenList methods of an instance of this class behave as
    though the tokens 'removed' from the front of an instance (using one of
    the 'advance' methods) are actually gone; the 'absolute' methods behave
    as though those tokens were not removed from the front of the instance.

    @author James MacKay
    @version $Revision: 1.11 $
    @see Token
*/
public class TrackedTokenList
    extends TokenList
{
    // Constants

    /** The resources used by this class. */
    private static final Resources _resources =
        TokenResourcesLocator.resources;

    /** Resource identifiers. */
    private static final String
        NO_TOKENS_TO_ADVANCE_OVER_MSG =
            "NO_TOKENS_TO_ADVANCE_OVER_MSG";


    // Private fields

    /**
        The index of the first item in this list that hasn't been removed
        using one of the 'advance' methods.
    */
    private int _currentStartIndex = 0;


    // Constructors

    /**
        Constructs a TrackedTokenList with the default initial capacity.
    */
    public TrackedTokenList()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
        Constructs a TrackedTokenList with the specified initial capacity.

        @param initialCapacity the list's initial capacity
    */
    public TrackedTokenList(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
        Constructs a TrackedTokenList with the specified backing list.

        @param backingList the list's backing list
    */
    protected TrackedTokenList(List backingList)
    {
        super(backingList);
    }


    // 'Advance' methods

    /**
        Advances the start of this list one token, effectively removing the
        first token from this list.

        @exception IllegalStateException thrown if there are no tokens left
        to advance over
    */
    public void advance()
        throws IllegalStateException
    {
        if (_currentStartIndex < absoluteSize())
        {
            _currentStartIndex += 1;
        }
        else
        {
            String msg = _resources.
                getMessage(NO_TOKENS_TO_ADVANCE_OVER_MSG);
            throw new IllegalStateException(msg);
        }
    }

    /**
        Advances the start of this list the specified number of tokens,
        effectively removing the first tokens from this list.

        @param numToAdvance the number of tokens to advance over
        @exception IllegalStateException thrown if there are not at least
        'numToAdvance' tokens left to advance over
    */
    public void advance(int numToAdvance)
        throws IllegalStateException
    {
        Assert.require(numToAdvance >= 0);

        for (int i = 0; i < numToAdvance; i++)
        {
            advance();
        }
    }

    /**
        Advances the start of this list so that it is effectively empty.
    */
    public void advanceToEnd()
    {
        _currentStartIndex = absoluteSize();
    }


    // Public methods

    /**
        @return the absolute index of the specified 'relative' or regular
        index into this list
    */
    public int toAbsoluteIndex(int index)
    {
        return _currentStartIndex + index;
    }

    /**
        @return the last token in this list that was advanced over, or null
        if no tokens in this list have been advanced over yet
    */
    public Token lastAdvancedOver()
    {
        Token result = null;

        if (_currentStartIndex > 0)
        {
            result = absoluteGet(_currentStartIndex - 1);
        }

        return result;
    }


    // TokenList methods (with 'absolute' equivalents)

    // Note: the superclass' version of iterator() works fine for this
    // subclass too.

    /**
        @return an iterator over all of the tokens in this list, including
        all of the ones that were advanced over
    */
    public TokenIterator absoluteIterator()
    {
        return absoluteSubList(0).iterator();
    }

    /**
        @see TokenList#get(int)
    */
    public Token get(int index)
        throws NoSuchItemException
    {
        return super.get(toAbsoluteIndex(index));
    }

    /**
        Returns the token in this list that is at the specified 'absolute'
        index.

        @param index the absolute index in the list of the token to return
        @return the (index + 1)th token in this list
        @exception NoSuchItemException thrown if this list doesn't have an
        item at the specified absolute index
    */
    public Token absoluteGet(int index)
        throws NoSuchItemException
    {
        return super.get(index);
    }

    /**
        @see TokenList#getLast
    */
    public Token getLast()
        throws NoSuchItemException
    {
        if (isEmpty())
        {
            throw new NoSuchItemException("TrackedTokenList is empty");
        }
        return super.getLast();
    }

    /**
        @return the last element in the list, even if it has been 'removed'
        by being advanced over
        @exception NoSuchItemException thrown if this list doesn't have a
        last item - that is, if it is ('absolutely') empty
    */
    public Token absoluteGetLast()
        throws NoSuchItemException
    {
        return super.getLast();
    }


    /**
        Returns the index of the first token in this list (including those
        that have been 'removed' by being advanced over) that satisfies the
        specified predicate, or -1 if there is no such token in this list.

        @param predicate the predicate that an item in this list must
        satisfy in order for its index to be returned
        @return the index of the first token in this list that satisfies the
        predicate
    */
    public int absoluteIndexOf(UnaryTokenPredicate predicate)
    {
        return absoluteIndexOf(predicate, 0);
    }

    /**
        @see TokenList#indexOf(UnaryTokenPredicate, int)
    */
    public int indexOf(UnaryTokenPredicate predicate,
                       int startIndex)
    {
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < size());

        return super.indexOf(predicate, toAbsoluteIndex(startIndex));
    }

    /**
        Returns the index of the first token in this list (including those
        that have been 'removed' by being advanced over) at or after the
        specified index that satisfies the specified predicate, or -1 if
        there is no such token in this list.

        @param predicate the predicate that an item in this list must
        satisfy in order for its index to be returned
        @param startIndex the index at which an item must be at or after in
        this list in order for its index to be returned
        @return the index of the first item in this list that is at or after
        the startIndex and that satisfies the predicate
    */
    public int absoluteIndexOf(UnaryTokenPredicate predicate,
                               int startIndex)
    {
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < absoluteSize());

        return super.indexOf(predicate, startIndex);
    }

    /**
        Returns the index of the last Token in this list (including those
        that have been 'removed' by being advanced over) that satisfies the
        specified predicate, or -1 if there is no such token in this list.

        @param predicate the predicate that an item in this list must
        satisfy in order for its index to be returned
        @return the index of the last item in this list that satisfies the
        predicate
    */
    public int absoluteLastIndexOf(UnaryTokenPredicate predicate)
    {
        return super.lastIndexOf(predicate, absoluteSize() - 1);
    }

    /**
        @see TokenList#lastIndexOf(UnaryTokenPredicate, int)
    */
    public int lastIndexOf(UnaryTokenPredicate predicate,
                           int startIndex)
    {
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < size());
        int result;

        int absoluteIndex = absoluteLastIndexOf(predicate,
                                toAbsoluteIndex(startIndex));
        if (absoluteIndex >= _currentStartIndex)
        {
            // A match was found that hasn't been advanced over.
            result = absoluteIndex - _currentStartIndex;
        }
        else
        {
            // Either no matching token was found, or the one that was found
            // has already been advanced over.
            result = -1;
        }

        return result;
    }

    /**
        Returns the index of the first token in this list (including those
        that have been 'removed' by being advanced over) at or before the
        specified index that satisfies the specified predicate, or -1 if
        there is no such Token in this list.

        @param predicate the predicate that an item in this list must
        satisfy in order for its index to be returned
        @param startIndex the index at which an item must be at or before in
        this list in order for its index to be returned
        @return the index of the last item in this list that is at or before
        the startIndex and that satisfies the predicate
    */
    public int absoluteLastIndexOf(UnaryTokenPredicate predicate,
                                   int startIndex)
    {
        Assert.require(startIndex >= 0);
        Assert.require(startIndex < absoluteSize());

        return super.lastIndexOf(predicate, startIndex);
    }


    /**
        @see TokenList#insert(Token, int)
    */
    public void insert(Token newItem, int index)
    {
        super.insert(newItem, toAbsoluteIndex(index));
    }

    /**
        Inserts the specified token into this list at the specified absolute
        index.

        @param newItem the token to insert into this list
        @param index the absolute index at which to insert the token
    */
    public void absoluteInsert(Token newItem, int index)
    {
        super.insert(newItem, index);
    }

    /**
        @see TokenList#insertAll(TokenList, int)
    */
    public void insertAll(TokenList list, int index)
    {
        super.insertAll(list, toAbsoluteIndex(index));
    }

    /**
        Inserts all of the tokens in the specified list into this list in
        order, at the specified absolute index.

        @param list the list of tokens to insert into this list
        @param index the absolute index at which to insert the tokens
    */
    public void absoluteInsertAll(TokenList list, int index)
    {
        super.insertAll(list, index);
    }

    /**
        @see TokenList#set(int, Token)
    */
    public void set(int index, Token newItem)
    {
        Assert.require(index >= 0);
        Assert.require(index <= size());  // can have index == size()

        super.set(toAbsoluteIndex(index), newItem);
    }

    /**
        Sets the (index + 1)th element of this list - including those
        elements that have been 'removed' by being advanced over - to be the
        specified token.

        @param index the absolute index of the list element to set
        @param newItem the value to set the list element to
    */
    public void absoluteSet(int index, Token newItem)
    {
        Assert.require(index >= 0);
        Assert.require(index <= absoluteSize());
            // can have index == absoluteSize()

        super.set(index, newItem);
    }

    /**
        @see TokenList#toArray
    */
    public Token[] toArray()
    {
        Token[] result;

        if (_currentStartIndex > 0)
        {
            // Note: 'subList(0)' results in a new TrackedTokenList whose
            // backing List contains only the elements in this list that
            // haven't been advanced over. The call of 'toArray()' on it will
            // cause this method to be called recursively, but this time with
            // _currentStartIndex == 0.
            result = subList(0).toArray();
        }
        else
        {
            result = super.toArray();
        }

        return result;
    }

    /**
        @return an array containing all of the elements of this list in
        order, including those that have been 'removed' by being advanced
        over
    */
    public Token[] absoluteToArray()
    {
        return super.toArray();
    }


    /**
        @see TypedList#size
    */
    public int size()
    {
        return super.size() - _currentStartIndex;
    }

    /**
        @return the number of elements in this list, including those that
        have been 'removed' by being advanced over
    */
    public int absoluteSize()
    {
        return super.size();
    }

    /**
        @see TypedList#isEmpty
    */
    public boolean isEmpty()
    {
        return (size() == 0);
    }

    /**
        @return true iff there are no elements in this list, including
        those that have been 'removed' by being advanced over
    */
    public boolean isAbsoluteEmpty()
    {
        return super.isEmpty();
    }

    /**
        @see TokenList#cloneList
    */
    public TokenList cloneList()
    {
        TrackedTokenList result = new TrackedTokenList(absoluteSize());

        result.addAll(absoluteSubList(0));
        result._currentStartIndex = _currentStartIndex;

        Assert.ensure(result != null);
        Assert.ensure(result.size() == size());
        return result;
    }

    /**
        @see TokenList#subList(int)
    */
    public TokenList subList(int startIndex)
    {
        int absoluteStart = toAbsoluteIndex(startIndex);
        return new TrackedTokenList(subBackingList(absoluteStart));
    }

    /**
        Returns a list containing the items in this list from the one at the
        specified absolute index through the last one in this list,
        inclusive.
        <p>
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the absolute index of the first item in this list
        to be in the returned sublist
        @return the sublist of this list
    */
    public TrackedTokenList absoluteSubList(int startIndex)
    {
        return new TrackedTokenList(subBackingList(startIndex));
    }

    /**
        @see TokenList#subList(int, int)
    */
    public TokenList subList(int startIndex, int pastEndIndex)
    {
        int absoluteStart = toAbsoluteIndex(startIndex);
        int absolutePastEnd = toAbsoluteIndex(pastEndIndex);
        return new TrackedTokenList(subBackingList(absoluteStart,
                                                   absolutePastEnd));
    }

    /**
        Returns a list containing the items in this list from the one at the
        specified absolute start index through the one at the index, if any,
        before the specified absolute past-end index.
        <p>
        The returned list is backed by this list, so changes in one will be
        reflected in the other.

        @param startIndex the absolute index of the first item in this list
        to be in the returned sublist
        @param pastEndIndex the index one past the absolute index of the
        last item in this list to be in the returned sublist
        @return the sublist of this list
    */
    public TrackedTokenList
        absoluteSubList(int startIndex, int pastEndIndex)
    {
        return new TrackedTokenList(subBackingList(startIndex,
                                                   pastEndIndex));
    }


    // Protected methods

    /**
        @see TypedList#createBackingList(int)
    */
    protected List createBackingList(int initialCapacity)
    {
        return new ArrayList(initialCapacity);
    }
}
