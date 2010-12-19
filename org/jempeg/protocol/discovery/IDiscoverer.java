package org.jempeg.protocol.discovery;

/**
 * Defines the interface for anything that is able to locate a device.
 */
public interface IDiscoverer {
	/**
	 * Adds a listener that will hear discovery events.
	 * 
	 * @param _listener the listener to add
	 */
	public void addDiscoveryListener(IDiscoveryListener _listener);

	/**
	 * Removes a listener from hearing discovery events.
	 * 
	 * @param _listener the listener to remove
	 */
	public void removeDiscoveryListener(IDiscoveryListener _listener);
	
	/**
	 * Starts device discovery.  This is a blocking call.
	 */
	public void startDiscovery();
	
	/**
	 * Stops device discovery.  This is not a guaranteed to stop
	 * immediately, but it will block until stop is complete.
	 */
	public void stopDiscovery();
	
	/**
	 * Returns whether or not this discoverer is currently running.
	 */
	public boolean isRunning();
}
