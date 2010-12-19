/* Timeout - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.empeg.protocol;

public class Timeout
{
    private int myPeriod;
    private boolean myFrozen;
    private long myTimeoutTime;
    
    public Timeout(int _ms) {
	myPeriod = _ms;
	myTimeoutTime = 0L;
    }
    
    public void reset() {
	myTimeoutTime = System.currentTimeMillis();
	myTimeoutTime += (long) myPeriod;
	myFrozen = false;
    }
    
    public final boolean isTimedOut() {
	if (!myFrozen && System.currentTimeMillis() > myTimeoutTime)
	    return true;
	return false;
    }
    
    public final boolean isNotTimedOut() {
	if (!myFrozen && System.currentTimeMillis() > myTimeoutTime)
	    return false;
	return true;
    }
    
    public boolean isFrozen() {
	return myFrozen;
    }
    
    public void expire() {
	myTimeoutTime = 0L;
    }
    
    public void freeze() {
	myFrozen = true;
    }
    
    public void unFreeze() {
	myFrozen = false;
    }
    
    public void setPeriod(int _ms) {
	myPeriod = _ms;
    }
    
    public final int getPeriod() {
	return myPeriod;
    }
    
    public final long getRemainingMilliseconds() {
	return myTimeoutTime - System.currentTimeMillis();
    }
}
