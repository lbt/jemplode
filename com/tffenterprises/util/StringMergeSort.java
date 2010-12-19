/* StringMergeSort - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.tffenterprises.util;

public class StringMergeSort extends AbstractMergeSort
{
    protected int compareElementsAt(int firstIndex, int secondIndex) {
	return ((String) arrayToSort[firstIndex])
		   .compareTo((String) arrayToSort[secondIndex]);
    }
}
