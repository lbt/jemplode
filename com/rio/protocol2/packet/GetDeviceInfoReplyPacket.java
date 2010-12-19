/* GetDeviceInfoReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.DeviceInfo;

public class GetDeviceInfoReplyPacket extends AbstractReplyPacket
{
    private DeviceInfo myDeviceInfo = new DeviceInfo();
    
    public GetDeviceInfoReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public DeviceInfo getDeviceInfo() {
	return myDeviceInfo;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException {
	myDeviceInfo.read(_is);
    }
}
