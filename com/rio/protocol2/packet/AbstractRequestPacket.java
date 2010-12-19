/* AbstractRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.text.ResourceBundleKey;
import com.inzyme.typeconv.LittleEndianOutputStream;

import org.jempeg.protocol.ProtocolException;

public abstract class AbstractRequestPacket extends AbstractPacket
{
    public AbstractRequestPacket(PacketHeader _header) {
	super(_header);
    }
    
    public void write(LittleEndianOutputStream _os) throws PacketException {
	try {
	    getHeader().write(_os);
	    writePayload(_os);
	} catch (Throwable e) {
	    throw new PacketException
		      ((new ResourceBundleKey
			("errors", "protocol.packet.requestPayloadFailed")),
		       e);
	}
    }
    
    protected abstract void writePayload
	(LittleEndianOutputStream littleendianoutputstream)
	throws IOException, ProtocolException;
}
