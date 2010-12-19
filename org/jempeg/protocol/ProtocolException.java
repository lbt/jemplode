/* ProtocolException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.protocol;
import com.inzyme.exception.ChainedException;
import com.inzyme.text.ResourceBundleKey;

public class ProtocolException extends ChainedException
{
    public ProtocolException(String _forEmpeg) {
	super(_forEmpeg);
    }
    
    public ProtocolException(String _forEmpeg, Throwable _t) {
	super(_forEmpeg, _t);
    }
    
    public ProtocolException(ResourceBundleKey _resourceBundleKey) {
	super(_resourceBundleKey);
    }
    
    public ProtocolException(ResourceBundleKey _resourceBundleKey,
			     Throwable _parent) {
	super(_resourceBundleKey, _parent);
    }
}
