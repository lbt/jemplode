package org.jempeg.protocol;

/**
 * A ConnectionFactory can create Connection objects based on various
 * configuration parameters.
 * 
 * @author Mike Schrag
 */
public interface IConnectionFactory {
	/**
	 * Creates a new connection based on the parameters that the factory was configured with.
	 * 
	 * @return a new Connection 
	 */
	public IConnection createConnection();
	
	/**
	 * Returns a displayablename for the location that this factory points to.
	 * 
	 * @return a display name for the target connection
	 */
	public String getLocationName();
}
