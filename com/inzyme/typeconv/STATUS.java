/* STATUS - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.typeconv;
import com.inzyme.exception.ExceptionUtils;

public class STATUS extends INT32
{
    public STATUS() {
	/* empty */
    }
    
    public STATUS(int _value) {
	super(_value);
    }
    
    public boolean isSucceeded() {
	if (getValue() >= 0L)
	    return true;
	return false;
    }
    
    public boolean isFailed() {
	if (getValue() < 0L)
	    return true;
	return false;
    }
    
    public long getSeverity() {
	return getValue() >> 30 & 0x3L;
    }
    
    public long getFacility() {
	return getValue() >> 16 & 0xfffL;
    }
    
    public long getComponent() {
	return getValue() >> 12 & 0xfL;
    }
    
    public long getResult() {
	return getValue() & 0xfffL;
    }
    
    public long getPrintable() {
	return getValue();
    }
    
    public long toInteger() {
	return getValue();
    }
    
    public boolean equals(int _facility, int _component, int _severity,
			  int _result) {
	if (getFacility() == (long) _facility
	    && getComponent() == (long) _component
	    && getSeverity() == (long) _severity
	    && getResult() == (long) _result)
	    return true;
	return false;
    }
    
    public String getResourceBundleKey() {
	StringBuffer sb = new StringBuffer();
	sb.append(getFacility());
	sb.append(".");
	sb.append(getComponent());
	sb.append(".");
	sb.append(getSeverity());
	sb.append(".");
	sb.append(getResult());
	return sb.toString();
    }
    
    public String toString() {
	return ("[STATUS: succeeded=" + isSucceeded() + "; severity="
		+ getSeverity() + "; facility=" + getFacility()
		+ "; component = " + getComponent() + "; result = "
		+ getResult() + "; value=" + getValue() + "]");
    }
    
    public static void main(String[] _args) {
	try {
	    String errorCode = _args[0];
	    if (errorCode.startsWith("0x"))
		errorCode = errorCode.substring(2);
	    long code = Long.parseLong(errorCode, 16);
	    STATUS status = new STATUS((int) code);
	    System.out.println(status.getResourceBundleKey());
	} catch (Throwable t) {
	    ExceptionUtils.printChainedStackTrace(t);
	}
    }
}
