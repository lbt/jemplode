/* PrepareReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

import org.jempeg.protocol.ProtocolException;

public class PrepareReplyPacket extends AbstractStatusReplyPacket
{
    private UINT32 myFID = new UINT32();
    
    public PrepareReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public UINT32 getFID() {
	return myFID;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	myFID.read(_is);
	super.readPayload(_is);
    }
}
