package com.inzyme.exception;

import com.inzyme.text.ResourceBundleKey;

/**
 * ChainedException is a chainable Exception.
 * 
 * @author Mike Schrag
 */
public class ChainedException extends Exception implements IChainedThrowable {
	private Throwable myParent;
	
	/**
	 * Constructor for ChainedException.
	 * 
	 * @param _resourceBundleKey the resource bundle key for the message of this exception
	 */
	public ChainedException(ResourceBundleKey _resourceBundleKey) {
		this(_resourceBundleKey.getString());
	}
		
	/**
	 * Constructor for ChainedException.
	 * 
	 * @param _resourceBundleKey the resource bundle key for the message of this exception
	 * @param _parent the parent exception in the chain
	 */
	public ChainedException(ResourceBundleKey _resourceBundleKey, Throwable _parent) {
		this(_resourceBundleKey.getString(), _parent);
	}
		
	/**
	 * Constructor for ChainedException.
	 * 
	 * @param _message the description of this exception
	 */
	public ChainedException(String _message) {
		this(_message, null);
	}

	/**
	 * Constructor for ChainedException.
	 * 
	 * @param _message the description of this exception
	 * @param _parent the parent exception in the chain
	 */
	public ChainedException(String _message, Throwable _parent) {
		super(_message);
		myParent = _parent;
	}
	
	public Throwable getParent() {
		return myParent;
	}
}
