/* ByeRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

public class ByeRequestPacket extends AbstractRequestPacket
{
    public ByeRequestPacket() {
	super(new PacketHeader(19));
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	/* empty */
    }
}
