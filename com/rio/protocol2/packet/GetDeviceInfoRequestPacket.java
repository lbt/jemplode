/* GetDeviceInfoRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

public class GetDeviceInfoRequestPacket extends AbstractRequestPacket
{
    public GetDeviceInfoRequestPacket() {
	super(new PacketHeader(5));
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	/* empty */
    }
}
