/* ChangeDeviceSettingsRequestPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.util.Properties;

import com.inzyme.io.PaddedOutputStream;
import com.inzyme.typeconv.LittleEndianOutputStream;

public class ChangeDeviceSettingsRequestPacket extends AbstractRequestPacket
{
    private Properties mySettings;
    
    public ChangeDeviceSettingsRequestPacket(Properties _settings) {
	super(new PacketHeader(8));
	mySettings = _settings;
    }
    
    protected void writePayload(LittleEndianOutputStream _os)
	throws IOException {
	PaddedOutputStream pos = new PaddedOutputStream(_os, 4);
	pos.writeProperties(mySettings, null, true);
	pos.pad();
    }
}
