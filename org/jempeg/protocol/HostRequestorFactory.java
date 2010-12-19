package org.jempeg.protocol;


/**
 * HostRequestorFactory provides a factory for
 * retrieving the host address of the device.
 * 
 * @author Mike Schrag
 */
public class HostRequestorFactory {
	private static IHostRequestor myInstance;
	
	/**
	 * Sets the current host requestor implementation to use for this app.
	 * 
	 * @param _hostRequestor the host requestor to use 
	 */
	public static void setInstance(IHostRequestor _hostRequestor) {
		myInstance = _hostRequestor;
	}
	
	/**
	 * Returns the host requestor to use.
	 * 
	 * @return the host requestor to use
	 */
	public static synchronized IHostRequestor getInstance() {
		if (myInstance == null) {
			setInstance(new DefaultHostRequestor());
		}
		return myInstance;
	}
}
