/* GetVersionReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.ProtocolVersion;

public class GetVersionReplyPacket extends AbstractReplyPacket
{
    private ProtocolVersion myVersion = new ProtocolVersion();
    
    public GetVersionReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public ProtocolVersion getVersion() {
	return myVersion;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException {
	myVersion.read(_is);
    }
}
