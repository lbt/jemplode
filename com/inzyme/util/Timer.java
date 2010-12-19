/**
* Copyright (c) 2001, Mike Schrag & Daniel Zimmerman
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* Redistributions of source code must retain the above copyright notice,
* this list of conditions and the following disclaimer.
*
* Redistributions in binary form must reproduce the above copyright notice,
* this list of conditions and the following disclaimer in the documentation
* and/or other materials provided with the distribution.
*
* Neither the name of Mike Schrag, Daniel Zimmerman, nor the names of any
* other contributors may be used to endorse or promote products derived from
* this software without specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
* "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
* TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
* PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE
* LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
* CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
* SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
* CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
* POSSIBILITY OF SUCH DAMAGE.
*/
package com.inzyme.util;

import java.lang.reflect.Method;

/**
* Timer provides a common "alarm clock"
* for various operations.  A Timer can be started
* for a certain length of time.  If the timer is 
* interrupted, then it will go back to sleep again
* for the original amount of time.  If nothing
* interrupts the timer after the preset 
* length of time, then the method that is passed
* in the constructor will be called.
*
* @author Mike Schrag
* @version $Revision: 1.4 $
*/
public class Timer implements Runnable {
	private Thread myThread;
	private int mySleepFor;
	private Object myWakeupObj;
	private String myWakeupMethodName;
	private boolean myStop;
	private long myLastMark;

	/**
	* Constructs a new Timer.
	*
	* @param _sleepFor number of milliseconds to sleep for
	* @param _wakeupObj the object to active when the Timer wakes up
	* @param _wakeupMethodName the name of the method on _wakeupObj to call on wakeup (no params)
	*/
	public Timer(int _sleepFor, Object _wakeupObj, String _wakeupMethodName) {
		mySleepFor = _sleepFor;
		myWakeupObj = _wakeupObj;
		myWakeupMethodName = _wakeupMethodName;
	}

	/**
	 * Set the number of milliseconds that the Timer should
	 * sleep for.
	 * 
	 * @param _sleepTime number of milliseconds to sleep for
	 */
	public void setSleepTime(int _sleepTime) {
		mySleepFor = _sleepTime;
	}

	/**
	 * Returns whether or not this thread is sleeping.
	 * 
	 * @return whether or not this thread is sleeping
	 */
	public synchronized boolean isSleeping() {
		return (myThread != null && myThread.isAlive());
	}

	public synchronized void mark() {
		myStop = false;
		if (myThread == null || !myThread.isAlive()) {
			myThread = new Thread(this);
			myThread.setName("Timer (" + myWakeupObj + ": " + myWakeupMethodName + ")");
			myThread.start();
		}
		else {
			wakeUp();
		}
	}

	/**
	* Interrupts the Timer.
	*/
	public synchronized void wakeUp() {
		myStop = false;
		myLastMark = System.currentTimeMillis();
		//    if (myThread != null) {
		//      myThread.interrupt();
		//    }
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
		myLastMark = 0;
		boolean done = false;
		while (!myStop && !done) {
			try {
				Thread.sleep(mySleepFor);
				long now = System.currentTimeMillis();
				// add a 50ms fluff to be in case the thread woke up early
				if ((now - myLastMark) < (mySleepFor - 50)) {
					done = false;
				}
				else {
					done = true;
					try {
						Method wakeupMethod = myWakeupObj.getClass().getMethod(myWakeupMethodName, null);
						if (!myStop) {
							wakeupMethod.invoke(myWakeupObj, null);
						}
					}
					catch (Throwable t) {
						Debug.println(t);
					}
				}
			}
			catch (InterruptedException e) {
				System.out.println("Timer.run: interrupted!");
			}
		}
		myStop = true;
	}
}
