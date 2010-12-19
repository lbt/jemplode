/* GetAllFileInfoReplyPacket - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package com.rio.protocol2.packet;
import java.io.IOException;

import com.inzyme.io.PaddedInputStream;
import com.inzyme.typeconv.LittleEndianInputStream;
import com.rio.protocol2.FileInfoEnumeration;

import org.jempeg.protocol.ProtocolException;

public class GetAllFileInfoReplyPacket extends AbstractStatusReplyPacket
{
    private FileInfoEnumeration myFileInfoEnumeration;
    
    public GetAllFileInfoReplyPacket(PacketHeader _header) {
	super(_header);
    }
    
    public FileInfoEnumeration getFileInfoEnumeration() {
	return myFileInfoEnumeration;
    }
    
    protected void readPayload(LittleEndianInputStream _is)
	throws IOException, ProtocolException {
	super.readPayload(_is);
	checkStatus();
	PaddedInputStream paddedInputStream = new PaddedInputStream(_is, 4);
	myFileInfoEnumeration
	    = new FileInfoEnumeration(paddedInputStream, false);
    }
}
