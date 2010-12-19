/* LongLinkedList - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;
import java.util.Iterator;

import com.inzyme.exception.MethodNotImplementedException;

public class LongLinkedList
{
    private ListItem myFirst;
    private ListItem myLast;
    private int mySize;
    
    public static class ListItem implements Comparable
    {
	public long myValue;
	public ListItem myNext;
	public ListItem myPrev;
	
	public ListItem(long _value) {
	    myValue = _value;
	}
	
	public long getValue() {
	    return myValue;
	}
	
	public ListItem getNext() {
	    return myNext;
	}
	
	public int compareTo(Object _obj) {
	    long ol = ((ListItem) _obj).myValue;
	    if (ol == myValue)
		return 0;
	    if (myValue > ol)
		return 1;
	    return -1;
	}
    }
    
    private final class LongLinkedListIterator implements Iterator
    {
	private ListItem myItem = myFirst;
	
	public boolean hasNext() {
	    if (myItem != null)
		return true;
	    return false;
	}
	
	public Object next() {
	    Object temp = myItem;
	    myItem = myItem.myNext;
	    return temp;
	}
	
	public void remove() {
	    throw new MethodNotImplementedException();
	}
    }
    
    public void add(long _value) {
	if (myFirst == null) {
	    myFirst = new ListItem(_value);
	    myLast = myFirst;
	} else {
	    ListItem next = new ListItem(_value);
	    myLast.myNext = next;
	    next.myPrev = myLast;
	    myLast = next;
	}
	mySize++;
    }
    
    public int size() {
	return mySize;
    }
    
    public ListItem getFirst() {
	return myFirst;
    }
    
    public ListItem removeFirst() {
	ListItem temp = myFirst;
	myFirst = myFirst.myNext;
	if (myFirst != null)
	    myFirst.myPrev = null;
	if (myLast == temp)
	    myLast = null;
	mySize--;
	return temp;
    }
    
    public long getLast() {
	long last;
	if (myLast != null)
	    last = myLast.myValue;
	else
	    last = -1L;
	return last;
    }
    
    public boolean contains(long _value) {
	for (ListItem item = myFirst; item != null; item = item.myNext) {
	    if (item.myValue == _value)
		return true;
	}
	return false;
    }
    
    public boolean remove(long _value) {
	for (ListItem item = myFirst; item != null; item = item.myNext) {
	    if (item.myValue == _value) {
		if (item == myFirst)
		    removeFirst();
		else {
		    item.myPrev.myNext = item.myNext;
		    if (item == myLast)
			myLast = item.myPrev;
		    else
			item.myNext.myPrev = item.myPrev;
		    mySize--;
		}
		return true;
	    }
	}
	return false;
    }
    
    public void clear() {
	myFirst = null;
	myLast = null;
	mySize = 0;
    }
    
    public long[] toArray() {
	long[] data = new long[mySize];
	ListItem next = myFirst;
	int i = 0;
	for (/**/; next != null; next = next.myNext)
	    data[i++] = next.myValue;
	return data;
    }
    
    public Iterator iterator() {
	return new LongLinkedListIterator();
    }
    
    public String toString() {
	StringBuffer buf = new StringBuffer();
	for (ListItem item = myFirst; item != null; item = item.myNext)
	    buf.append(item.myValue).append(' ');
	return buf.toString();
    }
}
