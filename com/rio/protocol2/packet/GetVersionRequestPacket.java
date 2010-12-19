/* GetVersionRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

import org.jempeg.protocol.ProtocolVersion;

public class GetVersionRequestPacket extends AbstractRequestPacket
{
    private ProtocolVersion myVersion;
    
    public GetVersionRequestPacket(ProtocolVersion _version) {
	super(new PacketHeader(0));
	myVersion = _version;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	myVersion.write(_os);
    }
}
