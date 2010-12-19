package org.jempeg.protocol;

/**
 * Provides an abstraction for retrieving a password.  This could
 * interact with the user, or load from a file, etc.
 * 
 * @author Mike Schrag
 */
public interface IAuthenticator {
	/**
	 * Requests a password given a password prompt.
	 * 
	 * @param _prompt the password prompt to display
	 * @return the PasswordAuthentication that was entered (or null if it was skipped)
	 */
	public PasswordAuthentication requestPassword(String _prompt);
}
