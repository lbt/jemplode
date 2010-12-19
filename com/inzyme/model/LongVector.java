/* LongVector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Enumeration;
import java.util.NoSuchElementException;

public class LongVector implements Cloneable, Serializable
{
    private int modCount = 0;
    protected long[] elementData;
    protected int elementCount;
    protected int capacityIncrement;
    private static final long serialVersionUID = -2767605614048989439L;
    
    public LongVector(int initialCapacity, int capacityIncrement) {
	if (initialCapacity < 0)
	    throw new IllegalArgumentException("Illegal Capacity: "
					       + initialCapacity);
	elementData = new long[initialCapacity];
	this.capacityIncrement = capacityIncrement;
    }
    
    public LongVector(int initialCapacity) {
	this(initialCapacity, 0);
    }
    
    public LongVector() {
	this(10);
    }
    
    public void copyInto(long[] anArray) {
	System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }
    
    public void trimToSize() {
	modCount++;
	int oldCapacity = elementData.length;
	if (elementCount < oldCapacity) {
	    long[] oldData = elementData;
	    elementData = new long[elementCount];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }
    
    public void ensureCapacity(int minCapacity) {
	modCount++;
	ensureCapacityHelper(minCapacity);
    }
    
    private void ensureCapacityHelper(int minCapacity) {
	int oldCapacity = elementData.length;
	if (minCapacity > oldCapacity) {
	    long[] oldData = elementData;
	    int newCapacity
		= (capacityIncrement > 0 ? oldCapacity + capacityIncrement
		   : oldCapacity * 2);
	    if (newCapacity < minCapacity)
		newCapacity = minCapacity;
	    elementData = new long[newCapacity];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }
    
    public void setSize(int newSize) {
	modCount++;
	if (newSize > elementCount)
	    ensureCapacityHelper(newSize);
	else {
	    for (int i = newSize; i < elementCount; i++)
		elementData[i] = -1L;
	}
	elementCount = newSize;
    }
    
    public int capacity() {
	return elementData.length;
    }
    
    public int size() {
	return elementCount;
    }
    
    public boolean isEmpty() {
	if (elementCount == 0)
	    return true;
	return false;
    }
    
    public Enumeration elements() {
	return new Enumeration() {
	    int count = 0;
	    
	    public boolean hasMoreElements() {
		if (count < elementCount)
		    return true;
		return false;
	    }
	    
	    public Object nextElement() {
		if (count < elementCount)
		    return new Long(elementData[count++]);
		throw new NoSuchElementException("Vector Enumeration");
	    }
	};
    }
    
    public boolean contains(long elem) {
	if (indexOf(elem, 0) >= 0)
	    return true;
	return false;
    }
    
    public int indexOf(long elem) {
	return indexOf(elem, 0);
    }
    
    public int indexOf(long elem, int index) {
	for (int i = index; i < elementCount; i++) {
	    if (elementData[i] == elem)
		return i;
	}
	return -1;
    }
    
    public int lastIndexOf(long elem) {
	return lastIndexOf(elem, elementCount - 1);
    }
    
    public int lastIndexOf(long elem, int index) {
	if (index >= elementCount)
	    throw new IndexOutOfBoundsException(String.valueOf(index) + " >= "
						+ elementCount);
	for (int i = index; i >= 0; i--) {
	    if (elementData[i] == elem)
		return i;
	}
	return -1;
    }
    
    public long elementAt(int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " >= " + elementCount);
	try {
	    return elementData[index];
	} catch (ArrayIndexOutOfBoundsException e) {
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " < 0");
	}
    }
    
    public long firstElement() {
	if (elementCount == 0)
	    throw new NoSuchElementException();
	return elementData[0];
    }
    
    public long lastElement() {
	if (elementCount == 0)
	    throw new NoSuchElementException();
	return elementData[elementCount - 1];
    }
    
    public void setElementAt(long obj, int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " >= " + elementCount);
	elementData[index] = obj;
    }
    
    public void removeElementAt(int index) {
	modCount++;
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " >= " + elementCount);
	if (index < 0)
	    throw new ArrayIndexOutOfBoundsException(index);
	int j = elementCount - index - 1;
	if (j > 0)
	    System.arraycopy(elementData, index + 1, elementData, index, j);
	elementCount--;
	elementData[elementCount] = -1L;
    }
    
    public void insertElementAt(long obj, int index) {
	modCount++;
	if (index >= elementCount + 1)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " > " + elementCount);
	ensureCapacityHelper(elementCount + 1);
	System.arraycopy(elementData, index, elementData, index + 1,
			 elementCount - index);
	elementData[index] = obj;
	elementCount++;
    }
    
    public void addElement(long obj) {
	modCount++;
	ensureCapacityHelper(elementCount + 1);
	elementData[elementCount++] = obj;
    }
    
    public boolean removeElement(long obj) {
	modCount++;
	int i = indexOf(obj);
	if (i >= 0) {
	    removeElementAt(i);
	    return true;
	}
	return false;
    }
    
    public void removeAllElements() {
	modCount++;
	for (int i = 0; i < elementCount; i++)
	    elementData[i] = -1L;
	elementCount = 0;
    }
    
    public Object clone() {
	try {
	    LongVector v = (LongVector) super.clone();
	    v.elementData = new long[elementCount];
	    System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
	    v.modCount = 0;
	    return v;
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }
    
    public long[] toArray() {
	long[] result = new long[elementCount];
	System.arraycopy(elementData, 0, result, 0, elementCount);
	return result;
    }
    
    public long[] toArray(long[] a) {
	if (a.length < elementCount)
	    a = (long[]) Array.newInstance(a.getClass().getComponentType(),
					   elementCount);
	System.arraycopy(elementData, 0, a, 0, elementCount);
	if (a.length > elementCount)
	    a[elementCount] = -1L;
	return a;
    }
    
    public long get(int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	return elementData[index];
    }
    
    public long set(int index, long element) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	long oldValue = elementData[index];
	elementData[index] = element;
	return oldValue;
    }
    
    public boolean add(long o) {
	modCount++;
	ensureCapacityHelper(elementCount + 1);
	elementData[elementCount++] = o;
	return true;
    }
    
    public boolean remove(long o) {
	return removeElement(o);
    }
    
    public void add(int index, long element) {
	insertElementAt(element, index);
    }
    
    public long remove(int index) {
	modCount++;
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	long oldValue = elementData[index];
	int numMoved = elementCount - index - 1;
	if (numMoved > 0)
	    System.arraycopy(elementData, index + 1, elementData, index,
			     numMoved);
	elementData[--elementCount] = -1L;
	return oldValue;
    }
    
    public void clear() {
	removeAllElements();
    }
    
    public boolean equals(Object o) {
	return super.equals(o);
    }
    
    public int hashCode() {
	return super.hashCode();
    }
    
    public String toString() {
	return "[LongVector: elementCount = " + elementCount + "]";
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
	modCount++;
	int numMoved = elementCount - toIndex;
	System.arraycopy(elementData, toIndex, elementData, fromIndex,
			 numMoved);
	int newElementCount = elementCount - (toIndex - fromIndex);
	while (elementCount != newElementCount)
	    elementData[--elementCount] = -1L;
    }
}
