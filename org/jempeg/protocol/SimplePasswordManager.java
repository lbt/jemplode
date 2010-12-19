package org.jempeg.protocol;

/**
 * Provides an in-memory implementation of the PasswordManager interface.  This will
 * ignore requests to save the password.
 * 
 * @author Mike Schrag
 */
public class SimplePasswordManager implements IPasswordManager {
	private String myPassword;
	
	/**
	 * Constructs a SimplePasswordManager.
	 * 
	 * @param _password the initial password
	 */
	public SimplePasswordManager(String _password) {
		myPassword = _password;
	}

	/**
	 * Constructs a SimplePasswordManager.
	 */
	public SimplePasswordManager() {
	}

	public void setPassword(String _password, boolean _persistent) {
		myPassword = _password;
	}

	public String getPassword() {
		return myPassword;
	}
}
