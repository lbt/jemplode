/* NumericEnum - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.util;

public class NumericEnum
{
    private int myValue;
    
    public NumericEnum(int _value) {
	myValue = _value;
    }
    
    public int getValue() {
	return myValue;
    }
    
    public boolean equals(Object _obj) {
	boolean equals;
	if (_obj == null || !(_obj instanceof NumericEnum))
	    equals = false;
	else {
	    NumericEnum numEnum = (NumericEnum) _obj;
	    equals = myValue == numEnum.myValue;
	}
	return equals;
    }
    
    public int hashCode() {
	return myValue;
    }
    
    public String toString() {
	return ("[" + this.getClass().getName() + ": value = " + getValue()
		+ "]");
    }
}
