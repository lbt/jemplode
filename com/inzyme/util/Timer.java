/* Timer - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.util;
import java.lang.reflect.Method;

public class Timer implements Runnable
{
    private Thread myThread;
    private int mySleepFor;
    private Object myWakeupObj;
    private String myWakeupMethodName;
    private boolean myStop;
    private long myLastMark;
    
    public Timer(int _sleepFor, Object _wakeupObj, String _wakeupMethodName) {
	mySleepFor = _sleepFor;
	myWakeupObj = _wakeupObj;
	myWakeupMethodName = _wakeupMethodName;
    }
    
    public void setSleepTime(int _sleepTime) {
	mySleepFor = _sleepTime;
    }
    
    public synchronized boolean isSleeping() {
	if (myThread != null && myThread.isAlive())
	    return true;
	return false;
    }
    
    public synchronized void mark() {
	myStop = false;
	if (myThread == null || !myThread.isAlive()) {
	    myThread = new Thread(this);
	    myThread.setName("Timer (" + myWakeupObj + ": "
			     + myWakeupMethodName + ")");
	    myThread.start();
	} else
	    wakeUp();
    }
    
    public synchronized void wakeUp() {
	myStop = false;
	myLastMark = System.currentTimeMillis();
    }
    
    public synchronized void stop() {
	if (!myStop && myThread != null) {
	    myStop = true;
	    myThread.interrupt();
	    myThread = null;
	}
    }
    
    public synchronized boolean isStopped() {
	return myStop;
    }
    
    public void run() {
	myLastMark = 0L;
	boolean done = false;
	while (!myStop && !done) {
	    try {
		Thread.sleep((long) mySleepFor);
		long now = System.currentTimeMillis();
		if (now - myLastMark < (long) (mySleepFor - 50))
		    done = false;
		else {
		    done = true;
		    try {
			Method wakeupMethod
			    = myWakeupObj.getClass()
				  .getMethod(myWakeupMethodName, null);
			if (!myStop)
			    wakeupMethod.invoke(myWakeupObj, null);
		    } catch (Throwable t) {
			Debug.println(t);
		    }
		}
	    } catch (InterruptedException e) {
		System.out.println("Timer.run: interrupted!");
	    }
	}
	myStop = true;
    }
}
