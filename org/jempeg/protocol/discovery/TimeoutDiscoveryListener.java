package org.jempeg.protocol.discovery;

import com.inzyme.util.Timer;

/**
 * TimeoutDiscoveryListener is a listener that stops discovery after
 * a given amount of time with no devices being discovered.
 */
public class TimeoutDiscoveryListener implements IDiscoveryListener {
	private IDiscoverer myDiscoverer;
	private Timer myTimer;
	
	/**
	 * Constructs a TimeoutDiscoveryListener.
	 * 
	 * @param _discoverer the discoverer to stop
	 * @param _timeout the number of milliseconds of inactivity before stopping 
	 */
	public TimeoutDiscoveryListener(IDiscoverer _discoverer, int _timeout) {
		myDiscoverer = _discoverer;
		myTimer = new Timer(_timeout, this, "wakeup");
		myTimer.mark();
	}
	
	/**
	 * Callback method for the Timer.
	 */
	public void wakeup() {
		myDiscoverer.stopDiscovery();
	}
	
	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device) {
		myTimer.mark();
	}
}
