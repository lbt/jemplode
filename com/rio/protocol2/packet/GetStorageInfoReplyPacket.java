/* GetStorageInfoReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.ProtocolException;
import org.jempeg.protocol.StorageInfo;

public class GetStorageInfoReplyPacket extends AbstractStatusReplyPacket
{
    private StorageInfo myStorageInfo = new StorageInfo();
    
    public GetStorageInfoReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public StorageInfo getStorageInfo() {
	return myStorageInfo;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	super.readPayload(_is);
	myStorageInfo.read(_is);
    }
}
