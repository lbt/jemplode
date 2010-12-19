/* QuickSort - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.sort;
import com.inzyme.text.CollationKeyCache;

public class QuickSort
{
    private CollationKeyCache myCache;
    
    public QuickSort(CollationKeyCache _cache) {
	myCache = _cache;
    }
    
    protected void quicksort(Object[] a, int lo0, int hi0) {
	int lo = lo0;
	int hi = hi0;
	if (hi0 > lo0) {
	    Object mid = a[(lo0 + hi0) / 2];
	    while (lo <= hi) {
		for (/**/; lo < hi0; lo++) {
		    if (!isLessThan(a[lo], mid))
			break;
		}
		for (/**/; hi > lo0 && isGreaterThan(a[hi], mid); hi--) {
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
    
    private void swap(Object[] a, int i, int j) {
	Object t = a[i];
	a[i] = a[j];
	a[j] = t;
    }
    
    protected boolean isLessThan(Object _a, Object _b) {
	Comparable a = getCollationKey(_a);
	Comparable b = getCollationKey(_b);
	if (a.compareTo(b) < 0)
	    return true;
	return false;
    }
    
    protected boolean isGreaterThan(Object _a, Object _b) {
	Comparable a = getCollationKey(_a);
	Comparable b = getCollationKey(_b);
	if (a.compareTo(b) > 0)
	    return true;
	return false;
    }
    
    protected Comparable getCollationKey(Object _a) {
	String sortValue;
	if (_a instanceof String)
	    sortValue = (String) _a;
	else
	    sortValue = _a.toString();
	return myCache.getCollationKey(sortValue);
    }
    
    public void sort(Object[] a) {
	quicksort(a, 0, a.length - 1);
    }
}
