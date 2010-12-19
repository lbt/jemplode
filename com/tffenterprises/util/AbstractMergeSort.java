/* AbstractMergeSort - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.util;

public abstract class AbstractMergeSort
{
    protected Object[] arrayToSort;
    protected Object[] scratch;
    
    public synchronized void sort(Object[] arrayToSort) {
	if (arrayToSort != null && arrayToSort.length > 1) {
	    int arrayLength = arrayToSort.length;
	    scratch = new Object[arrayLength];
	    this.arrayToSort = arrayToSort;
	    mergeSort(0, arrayLength - 1);
	    scratch = null;
	    this.arrayToSort = null;
	}
    }
    
    public void mergeSort(int beginIndex, int endIndex) {
	if (beginIndex != endIndex) {
	    int middleIndex = (beginIndex + endIndex) / 2;
	    mergeSort(beginIndex, middleIndex);
	    mergeSort(middleIndex + 1, endIndex);
	    merge(beginIndex, middleIndex, endIndex);
	}
    }
    
    protected void merge(int beginIndex, int middleIndex, int endIndex) {
	int firstHalf = beginIndex;
	int secondHalf = middleIndex + 1;
	int count = beginIndex;
	while (firstHalf <= middleIndex && secondHalf <= endIndex) {
	    if (compareElementsAt(secondHalf, firstHalf) < 0)
		scratch[count++] = arrayToSort[secondHalf++];
	    else
		scratch[count++] = arrayToSort[firstHalf++];
	}
	if (firstHalf <= middleIndex) {
	    while (firstHalf <= middleIndex)
		scratch[count++] = arrayToSort[firstHalf++];
	} else {
	    while (secondHalf <= endIndex)
		scratch[count++] = arrayToSort[secondHalf++];
	}
	for (count = beginIndex; count <= endIndex; count++)
	    arrayToSort[count] = scratch[count];
    }
    
    protected abstract int compareElementsAt(int i, int i_0_);
}
