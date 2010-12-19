/* GetFileInfoReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;
import java.util.Properties;

import com.inzyme.io.PaddedInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;

import org.jempeg.protocol.ProtocolException;

public class GetFileInfoReplyPacket extends AbstractStatusReplyPacket
{
    private Properties myFileInfo;
    
    public GetFileInfoReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public Properties getFileInfo() {
	return myFileInfo;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	super.readPayload(_is);
	checkStatus();
	PaddedInputStream pis = new PaddedInputStream(_is, 4);
	pis.addStartsWithToEncodingMap("playlist=", "ISO-8859-1");
	myFileInfo = pis.readProperties();
	pis.pad();
    }
}
