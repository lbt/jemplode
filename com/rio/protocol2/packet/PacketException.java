/* PacketException - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import com.inzyme.text.ResourceBundleKey;

import org.jempeg.protocol.ProtocolException;

public class PacketException extends ProtocolException
{
    public PacketException(ResourceBundleKey _resourceBundleKey) {
	super(_resourceBundleKey);
    }
    
    public PacketException(ResourceBundleKey _resourceBundleKey,
			   Throwable _parent) {
	super(_resourceBundleKey, _parent);
    }
}
