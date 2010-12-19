/* UnlockRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

public class UnlockRequestPacket extends AbstractRequestPacket
{
    public UnlockRequestPacket() {
	super(new PacketHeader(10));
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	/* empty */
    }
}
