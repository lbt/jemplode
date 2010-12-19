package com.inzyme.model;

/**
 * Range represents a range of numbers.
 * 
 * @author Mike Schrag
 * @version $Revision: 1.1 $
 */
public class Range {
	private int myStart;
	private int myEnd;
	
	/**
	 * Constructs a Range.
	 */
	public Range() {
	}
	
	/**
	 * Constructs a range.
	 * 
	 * @param _values the values to create a range for
	 */
	public Range(int[] _values) {
		setValues(_values);
	}
	
	/**
	 * Constructor for Range.
	 * 
	 * @param _start the starting value of the range
	 * @param _end the ending value of the range
	 */
	public Range(int _start, int _end) {
		myStart = _start;
		myEnd = _end;
	}
	
	/**
	 * Takes a set of values and finds the top
	 * and bottom of values.
	 * 
	 * @param _values the values to use for this range
	 */
	public void setValues(int[] _values) {
		int startIndex = Integer.MAX_VALUE;
		int endIndex = -1;
		for (int i = 0; i < _values.length; i ++) {
			int value = _values[i];
			if (value > endIndex) {
				endIndex = value;
			}
			if (value < startIndex) {
				startIndex = value;
			}
		}
		myStart = startIndex;
		myEnd = endIndex;
	}
	
	/**
	 * Returns the end of this range.
	 * 
	 * @return the end of this range
	 */
	public int getEnd() {
		return myEnd;
	}

	/**
	 * Returns the start of this range.
	 * 
	 * @return the start of this range
	 */
	public int getStart() {
		return myStart;
	}

	/**
	 * Sets the end.
	 * 
	 * @param _end The end to set
	 */
	public void setEnd(int _end) {
		myEnd = _end;
	}

	/**
	 * Sets the start.
	 * 
	 * @param _start The start to set
	 */
	public void setStart(int _start) {
		myStart = _start;
	}
	
	public String toString() {
		return "[Range: start = " + myStart + "; end = " + myEnd + "]";
	}
}
