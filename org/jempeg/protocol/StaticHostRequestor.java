package org.jempeg.protocol;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Provids an implementation of the HostRequestor interface that 
 * always returns the same value (for programmatically setting
 * the host address).
 * 
 * @author Mike Schrag
 */
public class StaticHostRequestor implements IHostRequestor {
	private InetAddress myHostAddress;
	
	/**
	 * Constructs a StaticHostRequestor.
	 * 
	 * @param _hostAddress the host address to return
	 */
	public StaticHostRequestor(InetAddress _hostAddress) {
		myHostAddress = _hostAddress;
	}
	
	public InetAddress requestHost(InetAddress _lastKnownAddress) throws IOException {
		return (myHostAddress == null) ? _lastKnownAddress : myHostAddress;
	}
}
