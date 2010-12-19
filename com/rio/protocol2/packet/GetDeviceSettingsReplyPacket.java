/* GetDeviceSettingsReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.util.Properties;

import com.inzyme.io.PaddedInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.ProtocolException;

public class GetDeviceSettingsReplyPacket extends AbstractStatusReplyPacket
{
    private Properties mySettings;
    
    public GetDeviceSettingsReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public Properties getSettings() {
	return mySettings;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	super.readPayload(_is);
	if (getStatus().isSucceeded()) {
	    PaddedInputStream pis = new PaddedInputStream(_is, 4);
	    mySettings = pis.readProperties();
	    pis.pad();
	}
    }
}
