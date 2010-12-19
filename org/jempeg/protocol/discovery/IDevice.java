package org.jempeg.protocol.discovery;

import org.jempeg.protocol.IConnectionFactory;

/**
 * Represents a discovered device and the connection factory that
 * is used to open a connection to it.
 * 
 * @author Mike Schrag
 */
public interface IDevice {
	/**
	 * Returns the display name of this device.
	 */
	public String getName();
	
	/**
	 * Returns the connection factory used to connect to the device.
	 */
	public IConnectionFactory getConnectionFactory();
}
