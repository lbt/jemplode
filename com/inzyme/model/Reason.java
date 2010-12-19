/* Reason - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.model;
import java.util.List;

public class Reason
{
    public static final Reason[] NO_REASONS = new Reason[0];
    private String myFileName;
    private String myReason;
    private Throwable myException;
    
    public Reason(Throwable _exception) {
	this(null, _exception);
    }
    
    public Reason(String _fileName, Throwable _exception) {
	myFileName = _fileName;
	myException = _exception;
    }
    
    public Reason(String _reason) {
	this(null, _reason);
    }
    
    public Reason(String _fileName, String _reason) {
	myFileName = _fileName;
	myReason = _reason;
    }
    
    public String getFileName() {
	return myFileName;
    }
    
    public String getReason() {
	String reason = null;
	if (myReason == null) {
	    if (myException != null) {
		reason = myException.getMessage();
		if (reason == null)
		    reason = myException.getClass().getName();
	    }
	} else
	    reason = myReason;
	if (reason == null)
	    reason = "No reason";
	return reason;
    }
    
    public Throwable getException() {
	return myException;
    }
    
    public String toString() {
	return getReason();
    }
    
    public static Reason[] toArray(List _reasonsVec) {
	Reason[] reasons = new Reason[_reasonsVec.size()];
	_reasonsVec.toArray(reasons);
	return reasons;
    }
    
    public static void fromArray(Reason[] _reasons, List _reasonsVec) {
	for (int i = 0; i < _reasons.length; i++)
	    _reasonsVec.add(_reasons[i]);
    }
    
    public static void print(Reason[] _reasons) {
	for (int i = 0; i < _reasons.length; i++)
	    System.out.println("  " + _reasons[i]);
    }
}
