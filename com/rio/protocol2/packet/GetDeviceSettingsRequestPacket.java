/* GetDeviceSettingsRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianOutputStream;

public class GetDeviceSettingsRequestPacket extends AbstractRequestPacket
{
    public GetDeviceSettingsRequestPacket() {
	super(new PacketHeader(7));
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	/* empty */
    }
}
