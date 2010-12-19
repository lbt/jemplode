package org.jempeg.protocol;

import com.inzyme.util.ReflectionUtils;

/**
 * Stores the user's response to an authentication request.
 * 
 * @author Mike Schrag
 */
public class PasswordAuthentication {
	private String myPassword;
	private boolean mySavePasswordRequested;
	
	/**
	 * Constructs a PasswordAuthentication.
	 * 
	 * @param _password the password that was entered
	 * @param _savePasswordRequested whether or not the user requested a persistent password
	 */
	public PasswordAuthentication(String _password, boolean _savePasswordRequested) {
		myPassword = _password;
		mySavePasswordRequested = _savePasswordRequested;
	}
	
	/**
	 * Returns the entered password.
	 * 
	 * @return the entered password
	 */	
	public String getPassword() {
		return myPassword;
	}
	
	/**
	 * Returns whether or not the user wants his/her password to be saved.
	 * 
	 * @return whether or not the user wants his/her password to be saved
	 */
	public boolean isSavePasswordRequested() {
		return mySavePasswordRequested;
	}
	
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
