package com.inzyme.exception;

/**
 * Thrown when a method is not yet implemented.
 * 
 * @author Mike Schrag
 */
public class MethodNotImplementedException extends ChainedRuntimeException {
	public MethodNotImplementedException() {
		super("This method is not yet implemented.");
	}

	public MethodNotImplementedException(String _message) {
		super(_message);
	}

	public MethodNotImplementedException(String _message, Throwable _parent) {
		super(_message, _parent);
	}

}
