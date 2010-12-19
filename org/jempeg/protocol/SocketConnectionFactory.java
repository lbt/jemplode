package org.jempeg.protocol;

import java.net.InetAddress;

import com.inzyme.util.ReflectionUtils;

/**
 * Creates SocketConnections.
 * 
 * @author Mike Schrag
 */
public class SocketConnectionFactory implements IConnectionFactory {
	private InetAddress myAddress;
	private int myPort;
	
	/**
	 * Creates a new SocketConnectionFactory.
	 * 
	 * @param _address the internet address of the endpoint
	 * @param _port of the port to connect to
	 */
	public SocketConnectionFactory(InetAddress _address, int _port) {
		myAddress = _address;
		myPort = _port;
	}
	
	public InetAddress getAddress() {
		return myAddress;
	}
	
	public int getPort() {
		return myPort;
	}
	
	public IConnection createConnection() {
		return new SocketConnection(myAddress, myPort);
	}
	
	public String getLocationName() {
		return myAddress.getHostAddress();
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
