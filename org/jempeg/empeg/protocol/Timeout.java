package org.jempeg.empeg.protocol;

/**
* Handles automatic timeouts.
*
* @author Mike Schrag
* @version $Revision: 1.1 $
*/
public class Timeout {
  private int myPeriod; // In milliseconds
  private boolean myFrozen;
  private long myTimeoutTime;
  
  public Timeout(int _ms) {
    myPeriod = _ms;
    myTimeoutTime = 0; //??
	  // this is much cheaper than a gettimeofday() in Time::Now()
  }
  
  public void reset() {
    myTimeoutTime = System.currentTimeMillis();
//    myTimeoutTime += (myPeriod * 1000);
    myTimeoutTime += myPeriod;
    myFrozen = false;
  }

  public final boolean isTimedOut() {
    return (!myFrozen && (System.currentTimeMillis() > myTimeoutTime));
  }

  public final boolean isNotTimedOut() {
    return (myFrozen || (System.currentTimeMillis() <= myTimeoutTime));
  }

  public boolean isFrozen() {
    return myFrozen;
  }

  public void expire() {
    myTimeoutTime = 0;
  }

  public void freeze() {
    myFrozen = true;
  }

  public void unFreeze() {
    myFrozen = false;
  }

  /// This won't affect a currently active timeout.
  public void setPeriod(int _ms) {
    myPeriod = _ms;
  }

  public final int getPeriod() {
    return myPeriod;
  }

  public final long getRemainingMilliseconds() {
    return (myTimeoutTime - System.currentTimeMillis());
  }
}
