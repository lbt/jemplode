package org.jempeg.protocol.discovery;

import org.jempeg.protocol.IConnectionFactory;

import com.inzyme.util.ReflectionUtils;

/**
 * The basic implementation of IDevice.
 * 
 * @author Mike Schrag
 */
public class BasicDevice implements IDevice {
	private String myName;
	private IConnectionFactory myConnectionFactory;
	
	/**
	 * Constructs a BasicDevice.
	 * 
	 * @param _name the name of the device
	 * @param _connectionFactory the connection factory for connecting to the device
	 */
	public BasicDevice(String _name, IConnectionFactory _connectionFactory) {
		myName = _name;
		myConnectionFactory = _connectionFactory;
	}
	
	public String getName() {
		return myName;
	}

	public IConnectionFactory getConnectionFactory() {
		return myConnectionFactory;
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
