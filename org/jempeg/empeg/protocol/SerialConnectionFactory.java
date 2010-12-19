package org.jempeg.empeg.protocol;

import javax.comm.CommPortIdentifier;

import org.jempeg.protocol.IConnection;
import org.jempeg.protocol.IConnectionFactory;

import com.inzyme.util.ReflectionUtils;

/**
 * Creates SerialConnections.
 * 
 * @author Mike Schrag
 */
public class SerialConnectionFactory implements IConnectionFactory {
	private CommPortIdentifier myPortIdentifier;
	private int myBaudRate;
	
	/**
	 * Constructs a SerialConnectionFactory.
	 * 
	 * @param _portIdentifier the port identifier of the comm port to communicate with
	 * @param _baudRate the baud rate to communicate with
	 */
	public SerialConnectionFactory(CommPortIdentifier _portIdentifier, int _baudRate) {
		myPortIdentifier = _portIdentifier;
		myBaudRate = _baudRate;
	}
	
	public CommPortIdentifier getPortIdentifier() {
		return myPortIdentifier;
	}
	
	public int getBaudRate() {
		return myBaudRate;
	}

	public IConnection createConnection() {
		return new SerialConnection(myPortIdentifier, myBaudRate);
	}
	
	public String getLocationName() {
		return myPortIdentifier.getName();
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
