package com.inzyme.exception;

/**
 * IChainedThrowable represents a node in a chain of root causes of exceptions.
 * 
 * @author Mike Schrag
 */
public interface IChainedThrowable {
	/**
	 * Returns the parent exception in this chain.  This is intentionally named
	 * differently than the JDK 1.4 method since we have to run on 1.1 AND 1.4, I
	 * don't want to deal with name collisions.
	 * 
	 * @return Throwable the parent exception in this chain
	 */
	public Throwable getParent();
}
