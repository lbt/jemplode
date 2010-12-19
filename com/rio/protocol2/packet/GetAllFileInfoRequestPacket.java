/* GetAllFileInfoRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

public class GetAllFileInfoRequestPacket extends AbstractRequestPacket
{
    public GetAllFileInfoRequestPacket() {
	super(new PacketHeader(13));
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	/* empty */
    }
}
