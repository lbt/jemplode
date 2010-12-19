package org.jempeg.nodestore;

import com.inzyme.exception.ChainedException;
import com.inzyme.text.ResourceBundleKey;

/**
 * SynchronizeException is thrown when a synchronize or download fails.
 * 
 * @author Mike Schrag
 */
public class SynchronizeException extends ChainedException {
	public SynchronizeException(ResourceBundleKey _resourceBundleKey) {
		super(_resourceBundleKey);
	}

	public SynchronizeException(ResourceBundleKey _resourceBundleKey, Throwable _parent) {
		super(_resourceBundleKey, _parent);
	}

}
