package org.jempeg.protocol;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Provides an abstraction for retrieving a host address.  This could
 * interact with the user, or load from properties, etc.
 * 
 * @author Mike Schrag
 */
public interface IHostRequestor {
	/**
	 * Requests a host address.
	 * 
	 * @param _lastKnownHost the last known host
	 * @return the InetAddress that was entered (or null if it was skipped)
	 * @throws IOException if the host address cannot be created 
	 */
	public InetAddress requestHost(InetAddress _lastKnownHost) throws IOException;
}
