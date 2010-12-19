package org.jempeg.protocol;

/**
 * PasswordManager provides an abstraction on storing and 
 * retrieving the a user's password.
 * 
 * @author Mike Schrag
 */
public interface IPasswordManager {
	/**
	 * Saves the most recently entered password.
	 * 
	 * @param _password the last password
	 * @param _persistent whether or not to persist the password across sessions 
	 */
	public void setPassword(String _password, boolean _persistent);

	/**
	 * Returns the last password that was entered (or null if there isn't one)
	 * 
	 * @return the last password
	 */
	public String getPassword();
}
