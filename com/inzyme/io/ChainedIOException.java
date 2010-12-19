package com.inzyme.io;

import java.io.IOException;

import com.inzyme.exception.IChainedThrowable;

/**
 * Thrown from method that have to throw IOException but you really want
 * chaining.
 * 
 * @author Mike Schrag
 */
public class ChainedIOException extends IOException implements IChainedThrowable {
	private Throwable myParent;
	
	public ChainedIOException(String s) {
		this(s, null);
	}
	
	public ChainedIOException(String s, Throwable _parent) {
		super(s);
		myParent = _parent;
	}

	public Throwable getParent() {
		return myParent;
	}

}
