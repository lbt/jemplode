/* Range - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;

public class Range
{
    private int myStart;
    private int myEnd;
    
    public Range() {
	/* empty */
    }
    
    public Range(int[] _values) {
	setValues(_values);
    }
    
    public Range(int _start, int _end) {
	myStart = _start;
	myEnd = _end;
    }
    
    public void setValues(int[] _values) {
	int startIndex = 2147483647;
	int endIndex = -1;
	for (int i = 0; i < _values.length; i++) {
	    int value = _values[i];
	    if (value > endIndex)
		endIndex = value;
	    if (value < startIndex)
		startIndex = value;
	}
	myStart = startIndex;
	myEnd = endIndex;
    }
    
    public int getEnd() {
	return myEnd;
    }
    
    public int getStart() {
	return myStart;
    }
    
    public void setEnd(int _end) {
	myEnd = _end;
    }
    
    public void setStart(int _start) {
	myStart = _start;
    }
    
    public String toString() {
	return "[Range: start = " + myStart + "; end = " + myEnd + "]";
    }
}
