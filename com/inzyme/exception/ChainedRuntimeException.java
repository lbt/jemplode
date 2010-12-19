/* ChainedRuntimeException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.exception;
import com.inzyme.text.ResourceBundleKey;

public class ChainedRuntimeException extends RuntimeException
    implements IChainedThrowable
{
    private Throwable myParent;
    
    public ChainedRuntimeException(ResourceBundleKey _resourceBundleKey) {
	this(_resourceBundleKey.getString());
    }
    
    public ChainedRuntimeException(ResourceBundleKey _resourceBundleKey,
				   Throwable _parent) {
	this(_resourceBundleKey.getString(), _parent);
    }
    
    public ChainedRuntimeException(String _message) {
	this(_message, null);
    }
    
    public ChainedRuntimeException(String _message, Throwable _parent) {
	super(_message);
	myParent = _parent;
    }
    
    public Throwable getParent() {
	return myParent;
    }
}
