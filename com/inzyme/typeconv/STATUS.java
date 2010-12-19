/**
* This file is licensed under the GPL.
*
* See the LICENSE0 file included in this release, or
* http://www.opensource.org/licenses/gpl-license.html
* for the details of the license.
*/
package com.inzyme.typeconv;

/**
* Represents an Empeg "STATUS".
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class STATUS extends INT32 {
	public STATUS() {
		super();
	}

	public STATUS(int _value) {
		super(_value);
	}

	public boolean isSucceeded() {
		return getValue() >= 0;
	}

	public boolean isFailed() {
		return getValue() < 0;
	}

	public long getSeverity() {
		return (getValue() >> 30) & 3;
	}

	public long getFacility() {
		return (getValue() >> 16) & 0xFFF;
	}

	public long getComponent() {
		return (getValue() >> 12) & 0xF;
	}

	public long getResult() {
		return getValue() & 0xFFF;
	}

	public long getPrintable() {
		return getValue();
	}

	public long toInteger() {
		return getValue();
	}
	
	public boolean equals(int _facility, int _component, int _severity, int _result) {
		return getFacility() == _facility && getComponent() == _component && getSeverity() == _severity && getResult() == _result;
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
		return "[STATUS: succeeded=" + isSucceeded() + "; severity=" + getSeverity() + "; facility=" + getFacility() + "; component = " + getComponent() + "; result = " + getResult() + "; value=" + getValue() + "]";
	}
	
	public static void main(String[] _args) {
		try {
			String errorCode = _args[0];
			if (errorCode.startsWith("0x")) {
				errorCode = errorCode.substring(2);
			}
			long code = Long.parseLong(errorCode, 16);
			STATUS status = new STATUS((int)code);
			System.out.println(status.getResourceBundleKey());
		}
		catch (Throwable t) {
			com.inzyme.exception.ExceptionUtils.printChainedStackTrace(t);
		}
	}
	
}
