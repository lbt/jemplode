/* Login2ReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;
import com.inzyme.typeconv.UINT32;

import org.jempeg.protocol.ProtocolException;

public class Login2ReplyPacket extends AbstractStatusReplyPacket
{
    private UINT32 myAccess = new UINT32();
    
    public Login2ReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public UINT32 getAccess() {
	return myAccess;
    }
    
    public boolean isReadAccessGranted() {
	if (myAccess.getValue() != 0L && !isWriteAccessGranted())
	    return false;
	return true;
    }
    
    public boolean isWriteAccessGranted() {
	if (myAccess.getValue() == 1L)
	    return true;
	return false;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	super.readPayload(_is);
	myAccess.read(_is);
    }
}
