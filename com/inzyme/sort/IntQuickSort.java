/* IntQuickSort - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.sort;

public class IntQuickSort
{
    protected void quicksort(int[] a, int lo0, int hi0) {
	int lo = lo0;
	int hi = hi0;
	if (hi0 > lo0) {
	    int mid = a[(lo0 + hi0) / 2];
	    while (lo <= hi) {
		for (/**/; lo < hi0; lo++) {
		    if (a[lo] >= mid)
			break;
		}
		for (/**/; hi > lo0 && a[hi] > mid; hi--) {
		    /* empty */
		}
		if (lo <= hi) {
		    swap(a, lo, hi);
		    lo++;
		    hi--;
		}
	    }
	    if (lo0 < hi)
		quicksort(a, lo0, hi);
	    if (lo < hi0)
		quicksort(a, lo, hi0);
	}
    }
    
    private void swap(int[] a, int i, int j) {
	int t = a[i];
	a[i] = a[j];
	a[j] = t;
    }
    
    public void sort(int[] a) {
	quicksort(a, 0, a.length - 1);
    }
}
