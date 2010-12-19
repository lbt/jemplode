package org.jempeg.protocol.discovery;

/**
 * Provides an interface for receiving device discovery events.
 */
public interface IDiscoveryListener {
	/**
	 * Called when a device is discovered.
	 * 
	 * @param _discoverer the discoverer that found the device
	 * @param _device the device that was found
	 */
	public void deviceDiscovered(IDiscoverer _discoverer, IDevice _device);
}
