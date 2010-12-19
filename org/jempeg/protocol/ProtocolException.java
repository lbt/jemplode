package org.jempeg.protocol;

import com.inzyme.exception.ChainedException;
import com.inzyme.text.ResourceBundleKey;

/**
 * Thrown when the device protocol fails.
 * 
 * @author Mike Schrag
 */
public class ProtocolException extends ChainedException {
	// TODO: EMPEG: REMOVE THIS
	public ProtocolException(String _forEmpeg) {
		super(_forEmpeg);
	}
	
	// TODO: EMPEG: REMOVE THIS
	public ProtocolException(String _forEmpeg, Throwable _t) {
		super(_forEmpeg, _t);
	}
	
	public ProtocolException(ResourceBundleKey _resourceBundleKey) {
		super(_resourceBundleKey);
	}

	public ProtocolException(ResourceBundleKey _resourceBundleKey, Throwable _parent) {
		super(_resourceBundleKey, _parent);
	}
}
