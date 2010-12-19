/* SynchronizeException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jempeg.nodestore;
import com.inzyme.exception.ChainedException;
import com.inzyme.text.ResourceBundleKey;

public class SynchronizeException extends ChainedException
{
    public SynchronizeException(ResourceBundleKey _resourceBundleKey) {
	super(_resourceBundleKey);
    }
    
    public SynchronizeException(ResourceBundleKey _resourceBundleKey,
				Throwable _parent) {
	super(_resourceBundleKey, _parent);
    }
}
