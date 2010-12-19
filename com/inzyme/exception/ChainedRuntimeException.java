package com.inzyme.exception;

import com.inzyme.text.ResourceBundleKey;

/**
 * ChainedRuntimeException is a chainable RuntimeException.
 * 
 * @author Mike Schrag
 */
public class ChainedRuntimeException extends RuntimeException implements IChainedThrowable {
	private Throwable myParent;
	
	/**
	 * Constructor for ChainedRuntimeException.
	 * 
	 * @param _resourceBundleKey the resource bundle key for the message of this exception
	 */
	public ChainedRuntimeException(ResourceBundleKey _resourceBundleKey) {
		this(_resourceBundleKey.getString());
	}
		
	/**
	 * Constructor for ChainedRuntimeException.
	 * 
	 * @param _resourceBundleKey the resource bundle key for the message of this exception
	 * @param _parent the parent exception in the chain
	 */
	public ChainedRuntimeException(ResourceBundleKey _resourceBundleKey, Throwable _parent) {
		this(_resourceBundleKey.getString(), _parent);
	}
		
	/**
	 * Constructor for ChainedRuntimeException.
	 * 
	 * @param _message the description of this exception
	 */
	public ChainedRuntimeException(String _message) {
		this(_message, null);
	}

	/**
	 * Constructor for ChainedRuntimeException.
	 * 
	 * @param _message the description of this exception
	 * @param _parent the parent exception in the chain
	 */
	public ChainedRuntimeException(String _message, Throwable _parent) {
		super(_message);
		myParent = _parent;
	}
	
	public Throwable getParent() {
		return myParent;
	}
}
