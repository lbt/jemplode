/* NakReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

public class NakReplyPacket extends AbstractReplyPacket
{
    public NakReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException {
	/* empty */
    }
}
