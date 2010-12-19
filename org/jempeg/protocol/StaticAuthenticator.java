package org.jempeg.protocol;

/**
 * Provids an implementation of the Authenticator interface that 
 * always returns the same value (for programmatically setting
 * the password).
 * 
 * @author Mike Schrag
 */
public class StaticAuthenticator implements IAuthenticator {
	private String myPassword;
	
	/**
	 * Constructs a StaticAuthenticator.
	 * 
	 * @param _password the password to return
	 */
	public StaticAuthenticator(String _password) {
		myPassword = _password;
	}

	public PasswordAuthentication requestPassword(String _prompt) {
		return new PasswordAuthentication(myPassword, true);
	}
}
