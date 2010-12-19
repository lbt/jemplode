/* ChainedException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.inzyme.exception;
import com.inzyme.text.ResourceBundleKey;

public class ChainedException extends Exception implements IChainedThrowable
{
    private Throwable myParent;
    
    public ChainedException(ResourceBundleKey _resourceBundleKey) {
	this(_resourceBundleKey.getString());
    }
    
    public ChainedException(ResourceBundleKey _resourceBundleKey,
			    Throwable _parent) {
	this(_resourceBundleKey.getString(), _parent);
    }
    
    public ChainedException(String _message) {
	this(_message, null);
    }
    
    public ChainedException(String _message, Throwable _parent) {
	super(_message);
	myParent = _parent;
    }
    
    public Throwable getParent() {
	return myParent;
    }
}
