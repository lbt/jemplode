package org.jempeg.protocol;

/**
 * AuthenticationFactory provides a factory for
 * retrieving an appropriate IAuthenticator implementation.
 * 
 * @author Mike Schrag
 */
public class AuthenticatorFactory {
	private static IAuthenticator myInstance;
	
	/**
	 * Sets the current authenticator implementation to use for this app.
	 * 
	 * @param _authenticator the authenticator to use 
	 */
	public static void setInstance(IAuthenticator _authenticator) {
		myInstance = _authenticator;
	}
	
	/**
	 * Returns the authenticator to authenticate with.
	 * 
	 * @return the authenticator to authenticate with
	 */
	public static synchronized IAuthenticator getInstance() {
		if (myInstance == null) {
			setInstance(new DefaultAuthenticator());
		}
		return myInstance;
	}
}
