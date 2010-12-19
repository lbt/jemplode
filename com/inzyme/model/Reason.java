package com.inzyme.model;

import java.util.List;

/**
* FIDChangeReason encapsulated a changed node and the
* explanation for why the change occurred.
*
* @author Mike Schrag
*/
public class Reason {
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

  /**
  * Returns the filename that was affected.
  *
  * @returns the filename that was affected
  */
  public String getFileName() {
    return myFileName;
  }

  /**
  * Returns the reason this change occurred.
  *
  * @returns the reason this change occurred
  */
  public String getReason() {
    String reason = null;
    if (myReason == null) {
      if (myException != null) {
        reason = myException.getMessage();
        if (reason == null) {
          reason = myException.getClass().getName();
        }
      }
    } else {
      reason = myReason;
    }
    if (reason == null) {
      reason = "No reason";
    }
    return reason;
  }

  /**
  * Returns the exception that is associated with this change.
  *
  * @returns the exception that is associated with this change
  */
  public Throwable getException() {
    return myException;
  }
  
  public String toString() {
  	return getReason();
  }
  
  /**
   * Returns the set of reasons from this Vector.  This is just
   * a convenience method because it happens kind of often.
   * 
   * @param _reasonsVec the Vector of reasons
   * @return an array of Reasons
   */
  public static Reason[] toArray(List _reasonsVec) {
  	Reason[] reasons = new Reason[_reasonsVec.size()];
  	_reasonsVec.toArray(reasons);
  	return reasons;
  }
  
  /**
   * Adds the array of reasons to the given Vector.  This is just
   * a convenience method because it happens kind of often.
   * 
   * @param _reasons the Reasons to add to the vector
   * @param _reasonsVec the Vector to add to
   */
  public static void fromArray(Reason[] _reasons, List _reasonsVec) {
  	for (int i = 0; i < _reasons.length; i ++) {
  		_reasonsVec.add(_reasons[i]);
  	}
  }
  
  public static void print(Reason[] _reasons) {
		for (int i = 0; i < _reasons.length; i ++) {
			System.out.println("  " + _reasons[i]);
		}
  }
}

