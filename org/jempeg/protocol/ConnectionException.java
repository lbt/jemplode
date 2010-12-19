package org.jempeg.protocol;

import com.inzyme.exception.ChainedException;

/**
 * Thrown when a Connection method fails.
 * 
 * @author Mike Schrag
 */
public class ConnectionException extends ChainedException {
	public ConnectionException(String _message) {
		super(_message);
	}

	public ConnectionException(String _message, Throwable _parent) {
		super(_message, _parent);
	}

}
