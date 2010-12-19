package org.jempeg.protocol;

/**
 * Before a ConnectionFactory is created, this stub class can be used
 * to avoid NullPointerExceptions.
 * 
 * @author Mike Schrag
 */
public class NoConnectionFactory implements IConnectionFactory {
	public IConnection createConnection() {
		return null;
	}

	public String getLocationName() {
		return "";
	}
}
