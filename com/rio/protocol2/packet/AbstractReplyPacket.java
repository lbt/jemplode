/* AbstractReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.ProtocolException;

public abstract class AbstractReplyPacket extends AbstractPacket
{
    public AbstractReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public void read(LittleEndianInputStream _is) throws PacketException {
	try {
	    readPayload(_is);
	} catch (Throwable e) {
	    throw new PacketException((new ResourceBundleKey
				       ("errors",
					"protocol.packet.replyPayloadFailed")),
				      e);
	}
    }
    
    protected abstract void readPayload
	(LittleEndianInputStream littleendianinputstream)
	throws IOException, ProtocolException;
}
