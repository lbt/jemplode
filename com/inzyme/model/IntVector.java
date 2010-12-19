/* IntVector - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;
import java.util.NoSuchElementException;

public class IntVector
{
    private int[] elementData;
    private int elementCount;
    private int capacityIncrement;
    
    public IntVector(int initialCapacity, int capacityIncrement) {
	if (initialCapacity < 0)
	    throw new IllegalArgumentException("Illegal Capacity: "
					       + initialCapacity);
	elementData = new int[initialCapacity];
	this.capacityIncrement = capacityIncrement;
    }
    
    public IntVector(int initialCapacity) {
	this(initialCapacity, 0);
    }
    
    public IntVector() {
	this(10);
    }
    
    public void copyInto(int[] anArray) {
	System.arraycopy(elementData, 0, anArray, 0, elementCount);
    }
    
    public void trimToSize() {
	int oldCapacity = elementData.length;
	if (elementCount < oldCapacity) {
	    int[] oldData = elementData;
	    elementData = new int[elementCount];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }
    
    public void ensureCapacity(int minCapacity) {
	ensureCapacityHelper(minCapacity);
    }
    
    private void ensureCapacityHelper(int minCapacity) {
	int oldCapacity = elementData.length;
	if (minCapacity > oldCapacity) {
	    int[] oldData = elementData;
	    int newCapacity
		= (capacityIncrement > 0 ? oldCapacity + capacityIncrement
		   : oldCapacity * 2);
	    if (newCapacity < minCapacity)
		newCapacity = minCapacity;
	    elementData = new int[newCapacity];
	    System.arraycopy(oldData, 0, elementData, 0, elementCount);
	}
    }
    
    public void setSize(int newSize) {
	if (newSize > elementCount)
	    ensureCapacityHelper(newSize);
	else {
	    for (int i = newSize; i < elementCount; i++)
		elementData[i] = 0;
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
    
    public int[] elements() {
	return toArray();
    }
    
    public boolean contains(int elem) {
	if (indexOf(elem, 0) >= 0)
	    return true;
	return false;
    }
    
    public int indexOf(int elem) {
	return indexOf(elem, 0);
    }
    
    public int indexOf(int elem, int index) {
	for (int i = index; i < elementCount; i++) {
	    if (elem == elementData[i])
		return i;
	}
	return -1;
    }
    
    public int lastIndexOf(int elem) {
	return lastIndexOf(elem, elementCount - 1);
    }
    
    public int lastIndexOf(int elem, int index) {
	if (index >= elementCount)
	    throw new IndexOutOfBoundsException(String.valueOf(index) + " >= "
						+ elementCount);
	for (int i = index; i >= 0; i--) {
	    if (elem == elementData[i])
		return i;
	}
	return -1;
    }
    
    public int elementAt(int index) {
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
    
    public int firstElement() {
	if (elementCount == 0)
	    throw new NoSuchElementException();
	return elementData[0];
    }
    
    public int lastElement() {
	if (elementCount == 0)
	    throw new NoSuchElementException();
	return elementData[elementCount - 1];
    }
    
    public void setElementAt(int obj, int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " >= " + elementCount);
	elementData[index] = obj;
    }
    
    public void removeElementAt(int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " >= " + elementCount);
	if (index < 0)
	    throw new ArrayIndexOutOfBoundsException(index);
	int j = elementCount - index - 1;
	if (j > 0)
	    System.arraycopy(elementData, index + 1, elementData, index, j);
	elementCount--;
	elementData[elementCount] = 0;
    }
    
    public void insertElementAt(int obj, int index) {
	if (index >= elementCount + 1)
	    throw new ArrayIndexOutOfBoundsException(String.valueOf(index)
						     + " > " + elementCount);
	ensureCapacityHelper(elementCount + 1);
	System.arraycopy(elementData, index, elementData, index + 1,
			 elementCount - index);
	elementData[index] = obj;
	elementCount++;
    }
    
    public void addElement(int obj) {
	ensureCapacityHelper(elementCount + 1);
	elementData[elementCount++] = obj;
    }
    
    public boolean removeElement(int obj) {
	int i = indexOf(obj);
	if (i >= 0) {
	    removeElementAt(i);
	    return true;
	}
	return false;
    }
    
    public void removeAllElements() {
	for (int i = 0; i < elementCount; i++)
	    elementData[i] = 0;
	elementCount = 0;
    }
    
    public Object clone() {
	try {
	    IntVector v = (IntVector) super.clone();
	    v.elementData = new int[elementCount];
	    System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
	    return v;
	} catch (CloneNotSupportedException e) {
	    throw new InternalError();
	}
    }
    
    public int[] toArray() {
	int[] result = new int[elementCount];
	System.arraycopy(elementData, 0, result, 0, elementCount);
	return result;
    }
    
    public int[] toArray(int[] a) {
	if (a.length < elementCount)
	    a = new int[elementCount];
	System.arraycopy(elementData, 0, a, 0, elementCount);
	if (a.length > elementCount)
	    a[elementCount] = 0;
	return a;
    }
    
    public int get(int index) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	return elementData[index];
    }
    
    public int set(int index, int element) {
	if (index >= elementCount)
	    throw new ArrayIndexOutOfBoundsException(index);
	int oldValue = elementData[index];
	elementData[index] = element;
	return oldValue;
    }
    
    public boolean add(int o) {
	ensureCapacityHelper(elementCount + 1);
	elementData[elementCount++] = o;
	return true;
    }
    
    public boolean remove(int o) {
	return removeElement(o);
    }
    
    public void add(int index, int element) {
	insertElementAt(element, index);
    }
    
    public void clear() {
	removeAllElements();
    }
    
    public boolean equals(Object o) {
	return super.equals(o);
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
	int numMoved = elementCount - toIndex;
	System.arraycopy(elementData, toIndex, elementData, fromIndex,
			 numMoved);
	int newElementCount = elementCount - (toIndex - fromIndex);
	while (elementCount != newElementCount)
	    elementData[--elementCount] = 0;
    }
    
    public void addAll(int[] _values) {
	for (int i = 0; i < _values.length; i++)
	    addElement(_values[i]);
    }
}
